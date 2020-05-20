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

    @Override
    public String toString() {
        return new StringBuilder(this.name).append(" ").append(this.ip).toString();
    }
}
