class Main {
    public static void main(String args[]) {
        Core core = new Core();
        core.discovery();
        if (args.length > 0) {
            if (args[0].equals("play")) {
                Device device = new Device("Bedroom", "192.168.0.105");
                device.play();
            } else if (args[0].equals("pause")) {
                Device device = new Device("Bedroom", "192.168.0.105");
                device.pause();
            } else if (args[0].equals("stop")) {
                Device device = new Device("Bedroom", "192.168.0.105");
                device.stop();
            } else if (args[0].equals("next")) {
                Device device = new Device("Bedroom", "192.168.0.105");
                device.next();
            } else if (args[0].equals("play_uri")) {
                Device device = new Device("Bedroom", "192.168.0.105");
                if (args.length > 1) {
                    System.out.println(args[1]);
                    device.playFromUri(args[1]);
                }
            }
        }
    }
}
