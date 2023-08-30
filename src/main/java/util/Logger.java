package util;

public class Logger {
    public static void log(String msg) {
        if (Context.DEBUG) {
            System.out.println(msg);
        }
    }
}
