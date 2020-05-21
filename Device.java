class Device {
    String name;
    String ip;
    DeviceCommand deviceCommand;
    static String AVTRANSPORT_SERVICE = "AVTransport:1";
    static String AVTRANSPORT_SERVICE_URL = "/MediaRenderer/AVTransport/Control";
    static int PORT = 1400;

    public Device(String name, String ip) {
        this.name = name;
        this.ip = ip;
        deviceCommand = new DeviceCommand();
    }

    String getName() {
        return name;
    }

    String getIp() {
        return ip;
    }

    void play() {
        deviceCommand.startBuild();
        deviceCommand.newAction("Play", AVTRANSPORT_SERVICE).appendTag("InstanceID", 0).appendTag("Speed", "1")
                .endAction();
        deviceCommand.endBuild();
        deviceCommand.excute("http://" + this.ip + ":" + PORT + AVTRANSPORT_SERVICE_URL, "Play");
    }

    void pause() {
        deviceCommand.startBuild();
        deviceCommand.newAction("Pause", AVTRANSPORT_SERVICE).appendTag("InstanceID", 0).endAction();
        deviceCommand.endBuild();
        deviceCommand.excute("http://" + this.ip + ":" + PORT + AVTRANSPORT_SERVICE_URL, "Pause");
    }

    void stop() {
        deviceCommand.startBuild();
        deviceCommand.newAction("Stop", AVTRANSPORT_SERVICE).appendTag("InstanceID", 0).endAction();
        deviceCommand.endBuild();
        deviceCommand.excute("http://" + this.ip + ":" + PORT + AVTRANSPORT_SERVICE_URL, "Stop");
    }

    void next() {
        deviceCommand.startBuild();
        deviceCommand.newAction("Next", AVTRANSPORT_SERVICE).appendTag("InstanceID", 0).endAction();
        deviceCommand.endBuild();
        deviceCommand.excute("http://" + this.ip + ":" + PORT + AVTRANSPORT_SERVICE_URL, "Next");
    }

    void playFromUri(String uri) {

        // String metaData = "<DIDL-Lite xmlns:dc=\"http://purl.org/dc/elements/1.1/\"
        // xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\"
        // xmlns:r=\"urn:schemas-rinconnetworks-com:metadata-1-0/\"
        // xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\"><item
        // id=\"00030020trackid_22757082\" parentID=\"0004002calbumid_22757081\"
        // restricted=\"true\"><dc:title>Chord
        // Left</dc:title><upnp:class>object.item.audioItem.musicTrack</upnp:class><desc
        // id=\"cdudn\"
        // nameSpace=\"urn:schemas-rinconnetworks-com:metadata-1-0/\">SA_RINCON5127_42????35</desc></item></DIDL-Lite>";

        deviceCommand.startBuild();
        deviceCommand.newAction("SetAVTransportURI", AVTRANSPORT_SERVICE).appendTag("InstanceID", 0)
                .appendTag("CurrentURI", Helper.encodeXML(uri)).appendTag("CurrentURIMetaData", "").endAction();
        deviceCommand.endBuild();
        if (deviceCommand.excute("http://" + this.ip + ":" + PORT + AVTRANSPORT_SERVICE_URL, "SetAVTransportURI"))
            play();
    }

    @Override
    public String toString() {
        return new StringBuilder(this.name).append(" ").append(this.ip).toString();
    }
}
