package util;

public class Logger {
    public static <T> void log(T msg) {
        if (Context.DEBUG) {
            System.out.println(msg);
        }
    }
}
