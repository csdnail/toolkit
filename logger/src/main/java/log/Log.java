package log;


public final class Log{

    public Log() {
        Log.init();
    }

    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;
    public static final int INFO = 4;
    public static final int VERBOSE = 2;
    public static final int WARN = 5;

    public static final boolean isDebug = false;
    static int CURRENT_LEVEL = VERBOSE;
    static String DEFAULT_TAG = "logger";

    static Printer printer = new SysLoggerPrinter();

    public static void setTag(String tag){
        DEFAULT_TAG = tag;
    }

    public static boolean isSysLogger(){
        return (printer instanceof SysLoggerPrinter);
    }

    public static void setPrinter(Printer p){
        if(p != null) {
            printer = p;
        }
    }

    @Deprecated
    public static boolean isDebug() {
        return isDebug;
    }

    public static void setIsDebug(boolean Debug) {

    }

    public static void setCurrentLevel(int currentLevel) {
        CURRENT_LEVEL = currentLevel;
    }

    /**
     * It is used to get the settings object in order to change settings
     *
     * @return the settings object
     */
    public static Settings init() {
        return init(DEFAULT_TAG);
    }

    /**
     * It is used to change the tag
     *
     * @param tag is the given string which will be used in Log as TAG
     */
    public static Settings init(String tag) {
        //printer = new LoggerPrinter();
        return printer.init(tag);
    }

    public static void resetSettings() {
        printer.resetSettings();
    }

    public static Printer t(String tag) {
        return printer.t(tag, printer.getSettings().getMethodCount());
    }

    public static Printer t(int methodCount) {
        return printer.t(null, methodCount);
    }

    public static Printer t(String tag, int methodCount) {
        return printer.t(tag, methodCount);
    }

    public static void log(int priority, String tag, String message, Throwable throwable) {
        printer.log(priority, tag, message, throwable);
    }

    public static void d(String TAG, String message, Object... args) {
        if (CURRENT_LEVEL <= DEBUG) {
            printer.d(TAG, message, args);
        }
    }

    public static void d(String TAG, Object object) {
        if (CURRENT_LEVEL <= DEBUG) {
            printer.d(TAG, object);
        }
    }


    public static void e(String TAG, String message, Object... args) {
        if (CURRENT_LEVEL <= ERROR) {
            printer.e(TAG, message, args);
        }
    }

    public static void e(String TAG, Throwable throwable, String message, Object... args) {
        if (CURRENT_LEVEL <= ERROR) {
            printer.e(TAG, throwable, message, args);
        }
    }

    public static void i(String TAG, String message, Object... args) {
        if (CURRENT_LEVEL <= INFO) {
            printer.i(TAG, message, args);
        }
    }

    public static void v(String TAG, String message, Object... args) {
        if (CURRENT_LEVEL <= VERBOSE) {
            printer.v(TAG, message, args);
        }
    }

    public static void w(String TAG, String message, Object... args) {
        if (CURRENT_LEVEL <= WARN) {
            printer.w(TAG, message, args);
        }
    }

    public static void wtf(String TAG, String message, Object... args) {
        if (CURRENT_LEVEL <= ASSERT) {
            printer.wtf(TAG, message, args);
        }
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String TAG, String json) {
        printer.json(TAG, json);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public static void xml(String TAG, String xml) {
        printer.xml(TAG, xml);
    }
}
