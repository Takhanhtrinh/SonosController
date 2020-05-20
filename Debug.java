class Debug {
    static boolean isDebug = true;

    public static void LOG(Object str) {
        if (Debug.isDebug) {
            String fileInfo = Thread.currentThread().getStackTrace()[2].getFileName() + " "
                    + Thread.currentThread().getStackTrace()[2].getMethodName() + " "
                    + +Thread.currentThread().getStackTrace()[2].getLineNumber();
            System.out.println(fileInfo + ": " + str.toString());
        }
    }

}
