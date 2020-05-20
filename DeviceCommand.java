import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Stack;

class DeviceCommand {
    static int NONE = 0;
    static int START_BUILD = 1;
    static int END_BUILD = 2;

    private StringBuilder data;
    private int currentState;
    private String serviceName;
    private Stack<String> lastTag = new Stack<String>();

    public DeviceCommand() {
        data = new StringBuilder();
        currentState = NONE;
    }

    public DeviceCommand startBuild() {
        clear();
        currentState = START_BUILD;
        data.append("<?xml version=\"1.0\"?>" + "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" + "<s:Body>");
        return this;
    }

    void checkState() {
        if (currentState == START_BUILD) {
            Debug.LOG("START BUILD STATE");
        } else {
            throw new CommandDeviceException("Command is not in START BUILD state, currentState: " + currentState);

        }
    }

    public DeviceCommand newBlockTag(String tagName, String addtionInfo) {
        data.append("<" + tagName);
        if (addtionInfo != null) {
            data.append(" " + addtionInfo);
        }
        data.append(">");
        this.lastTag.push(tagName);
        return this;
    }

    public DeviceCommand newAction(String actionName, String serviceName) {
        String interfaceStr = "xmlns:u=\"urn:schemas-upnp-org:service:";
        this.serviceName = serviceName;
        return this.newBlockTag("u:" + actionName, interfaceStr + serviceName + "\"");
    }

    public DeviceCommand endAction() {
        return endLastBlock();
    }

    public DeviceCommand endLastBlock() {
        if (this.lastTag.empty()) {
            Debug.LOG("lastTag is empty");
        } else {
            data.append("</" + this.lastTag.pop() + ">");
        }
        return this;
    }

    public DeviceCommand endBuild() {
        data.append("</s:Body>" + "</s:Envelope>");
        return this;
    }

    public DeviceCommand excute(String url, String actionName) {
        Debug.LOG("URL: " + url);
        Debug.LOG("serviceName: " + this.serviceName + " action: " + actionName);
        System.out.println(data.toString());
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).version(Version.HTTP_1_1)
                .header("content-type", "text/xml")
                .header("SOAPACTION", "urn:schemas-upnp-org:service:" + this.serviceName + "#" + actionName)
                .POST(BodyPublishers.ofString(data.toString())).build();
        HttpClient client = HttpClient.newHttpClient();
        System.out.println(request);
        // client.sendAsync(request,
        // BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(System.out::print);
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

        return this;
    }

    public DeviceCommand appendTag(String tagName, Object value) {
        data.append("<" + tagName + ">");
        if (value != null)
            data.append(value.toString());
        data.append("</" + tagName + ">");
        return this;
    }

    public DeviceCommand clear() {
        if (data.length() > 0) {
            currentState = NONE;
            data = new StringBuilder();
        }
        return this;
    }

    @Override
    public String toString() {
        return data.toString();
    }

}
