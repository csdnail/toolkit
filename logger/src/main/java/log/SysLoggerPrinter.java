package log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


final public class SysLoggerPrinter implements Printer {

    static final String DEFAULT_TAG = "SYSLOGGER";

    static final int DEBUG = 3;
    static final int ERROR = 6;
    static final int ASSERT = 7;
    static final int INFO = 4;
    static final int VERBOSE = 2;
    static final int WARN = 5;


  /**
   * It is used for json pretty print
   */
    static final int JSON_INDENT = 2;

    String tag;

  /**
   * Localize single tag and method count for each thread
   */
    final ThreadLocal<String> localTag = new ThreadLocal<>();
    final ThreadLocal<Integer> localMethodCount = new ThreadLocal<>();

  /**
   * It is used to determine log settings such as method count, thread info visibility
   */
    final Settings settings = new Settings();

  public SysLoggerPrinter() {
    init(DEFAULT_TAG);
  }

  /**
   * It is used to change the tag
   *
   * @param tag is the given string which will be used in Log
   */
  @Override public Settings init(String tag) {
    if (tag == null) {
      throw new NullPointerException("tag may not be null");
    }
//    if (tag.trim().length() == 0) {
//      throw new IllegalStateException("tag may not be empty");
//    }
    this.tag = tag;
    return settings;
  }

  @Override public Settings getSettings() {
    return settings;
  }

  @Override public Printer t(String tag, int methodCount) {
    if (tag != null) {
      localTag.set(tag);
    }
    localMethodCount.set(methodCount);
    return this;
  }

  @Override public void d(String tag, String message, Object... args) {
    android.util.Log.d(tag, message);
  }

  @Override public void d(String tag,Object object) {
    String message;
    if (object.getClass().isArray()) {
      message = Arrays.deepToString((Object[]) object);
    } else {
      message = object.toString();
    }
    android.util.Log.d(tag, message);
  }

  @Override public void e(String tag, String message, Object... args) {
    android.util.Log.e(tag, message);
  }

  @Override public void e(String tag, Throwable throwable, String message, Object... args) {
    android.util.Log.e(tag, message);
  }

  @Override public void w(String tag, String message, Object... args) {
    android.util.Log.w(tag, message);
  }

  @Override public void i(String tag, String message, Object... args) {
    android.util.Log.i(tag, message);
  }

  @Override public void v(String tag, String message, Object... args) {
    android.util.Log.i(tag, message);
  }

  @Override public void wtf(String tag, String message, Object... args) {
    android.util.Log.e(tag, message);
  }

  /**
   * Formats the json content and print it
   *
   * @param json the json content
   */
  @Override public void json(String tag, String json) {
    if (Helper.isEmpty(json)) {
      d(tag, "Empty/Null json content");
      return;
    }
    try {
      json = json.trim();
      if (json.startsWith("{")) {
        JSONObject jsonObject = new JSONObject(json);
        String message = jsonObject.toString(JSON_INDENT);
        d(tag, message);
        return;
      }
      if (json.startsWith("[")) {
        JSONArray jsonArray = new JSONArray(json);
        String message = jsonArray.toString(JSON_INDENT);
        d(tag, message);
        return;
      }
      e(tag, "Invalid Json");
    } catch (JSONException e) {
      e(tag, "Invalid Json");
    }
  }

  /**
   * Formats the json content and print it
   *
   * @param xml the xml content
   */
  @Override public void xml(String tag, String xml) {
    if (Helper.isEmpty(xml)) {
      d(tag, "Empty/Null xml content");
      return;
    }
    try {
      Source xmlInput = new StreamSource(new StringReader(xml));
      StreamResult xmlOutput = new StreamResult(new StringWriter());
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.transform(xmlInput, xmlOutput);
      d(tag, xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
    } catch (TransformerException e) {
      e(tag, "Invalid xml");
    }
  }

  @Override public synchronized void log(int priority, String tag, String message, Throwable throwable) {
    if (settings.getLogLevel() == LogLevel.NONE) {
      return;
    }
    android.util.Log.d(tag, message);
  }

  @Override public void resetSettings() {
    settings.reset();
  }

  /**
   * This method is synchronized in order to avoid messy of logs' order.
   */
    synchronized void log(int priority, String tag, Throwable throwable, String msg, Object... args) {
    if (settings.getLogLevel() == LogLevel.NONE) {
      return;
    }
//    String tag = getTag();
    String message = createMessage(msg, args);
    log(priority, tag, message, throwable);
  }



    String createMessage(String message, Object... args) {
    return args == null || args.length == 0 ? message : String.format(message, args);
  }

}
