package net.xtrafrancyz.skinservice.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author xtrafrancyz
 */
public class Log {
    private static final Logger LOGGER = Logger.getLogger("SkinService");
    private static final String NEW_LINE = System.getProperty("line.separator");
    
    static {
        LOGGER.setUseParentHandlers(false);
        Handler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        LOGGER.addHandler(handler);
    }
    
    public static void info(String msg) {
        LOGGER.log(Level.INFO, msg);
    }
    
    public static void warning(String msg) {
        LOGGER.log(Level.WARNING, msg);
    }
    
    public static void severe(String msg) {
        LOGGER.log(Level.SEVERE, msg);
    }
    
    public static void warning(Throwable thr) {
        LOGGER.log(Level.WARNING, null, thr);
    }
    
    public static void warning(Throwable thr, String message) {
        LOGGER.log(Level.WARNING, message, thr);
    }
    
    public static void error(Throwable thr) {
        LOGGER.log(Level.SEVERE, null, thr);
    }
    
    public static void error(Throwable thr, String message) {
        LOGGER.log(Level.SEVERE, message, thr);
    }
    
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private static class LogFormatter extends Formatter {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        @Override
        public String format(LogRecord lr) {
            String message = dateFormat.format(new Date(lr.getMillis())) + "." + numToString((int) (lr.getMillis() % 1000), 3) + " [" + lr.getLevel().getName() + "] ";
            if (lr.getMessage() != null)
                message += lr.getMessage();
            if (lr.getThrown() != null) {
                StringWriter sw = new StringWriter();
                sw.write(NEW_LINE);
                lr.getThrown().printStackTrace(new PrintWriter(sw));
                message += sw.toString();
            }
            message += NEW_LINE;
            return message;
        }
    }
    
    private static String numToString(int num, int length) {
        String str = num + "";
        while (str.length() < length)
            str = "0" + str;
        if (str.length() > length)
            str = str.substring(0, length);
        return str;
    }
}
