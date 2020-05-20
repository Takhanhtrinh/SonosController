import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class Core {
    DatagramSocket client;
    InetSocketAddress address;
    static final String hostIp = "239.255.255.250";
    static final int port = 1900;
    static final String DISCOVERY_MESSAGE = "M-SEARCH * HTTP/1.1\n" + "HOST: 239.255.255.250:1900\n"
            + "MAN: \"ssdp:discover\"\n" + "ST: ssdp:all\n";
    static final int RECIEVE_TIMEOUT = 500;

    public Core() {
        address = new InetSocketAddress(hostIp, port);
        this.createSocket();
    }

    void createSocket() {
        try {
            client = new DatagramSocket();
            client.setSoTimeout(RECIEVE_TIMEOUT);

        } catch (IOException e) {
            Debug.LOG("unable create socket " + e.toString());
        }
    }

    void discovery() {
        Debug.LOG("DISCOVERING DEVICES");
        try {
            DatagramPacket packet = new DatagramPacket(DISCOVERY_MESSAGE.getBytes(),
                    DISCOVERY_MESSAGE.getBytes().length, address);
            client.send(packet);
        } catch (IOException e) {
            Debug.LOG(e.toString());
        }
        StringBuilder temp = new StringBuilder();
        while (true) {

            byte[] recieveBytes = new byte[1024];
            DatagramPacket recievePacket = new DatagramPacket(recieveBytes, 1024);
            try {
                client.receive(recievePacket);
            } catch (SocketTimeoutException e) {
                Debug.LOG("DISCOVERYING DEVICES TIME OUT");
                break;
            } catch (IOException e) {
            }
            String data = new String(recieveBytes);
            temp.append(data);
            // System.out.println(data);
            // Helper.parseDeviceData(data);
        }
        client.close();
        ArrayList<Device> devices = getDeviceList(temp.toString());
        for (int i = 0; i < devices.size(); i++) {
            Debug.LOG(devices.get(i));
        }

    }

    private ArrayList<Device> getDeviceList(String data) {
        // System.out.println(data);
        Set<String> temp = new HashSet<>();
        ArrayList<Device> devices = new ArrayList<Device>();
        // https://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java
        String regex = "^LOCATION: ((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
        String arr[] = data.split("\n");
        for (String str : arr) {
            Pattern p = Pattern.compile(regex);
            Matcher matcher = p.matcher(str);
            if (matcher.find()) {
                temp.add(matcher.group(1));
            }
        }
        for (String url : temp) {
            try {
                String xmlContent = Helper.getXmlContent(url);
                Device device = getDevice(xmlContent);
                devices.add(device);

            } catch (IOException e) {
                Debug.LOG("unexpected IOException " + e.toString());
            } catch (InterruptedException e) {
                Debug.LOG("unexpected interruptedException " + e.toString());
            }
        }
        return devices;
    }

    private Device getDevice(String content) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes("UTF-8"));
            Document doc = builder.parse(input);
            doc.getDocumentElement().normalize();
            NodeList deviceTag = doc.getElementsByTagName("device");
            // System.out.println(deviceTag);
            NodeList childTag = deviceTag.item(0).getChildNodes();
            StringBuilder deviceName = new StringBuilder();
            StringBuilder deviceIp = new StringBuilder();
            for (int i = 0; i < childTag.getLength(); i++) {
                if (deviceName.length() > 0 && deviceIp.length() > 0)
                    break;
                if (childTag.item(i).getNodeName().equals("friendlyName")) {
                    String deviceFamilyName = childTag.item(i).getTextContent();
                    for (int j = 0; j < deviceFamilyName.length(); j++) {
                        if (deviceFamilyName.charAt(j) == ' ')
                            break;
                        else {
                            deviceIp.append(deviceFamilyName.charAt(j));
                        }
                    }
                }
                if (childTag.item(i).getNodeName().equals("roomName"))
                    deviceName.append(childTag.item(i).getTextContent());
            }
            return new Device(deviceName.toString(), deviceIp.toString());
        } catch (IOException e) {
            Debug.LOG(e.toString());
        } catch (SAXException e) {
            Debug.LOG(e.toString());
        } catch (ParserConfigurationException e) {
            Debug.LOG(e.toString());
        }
        return null;
    }

}
