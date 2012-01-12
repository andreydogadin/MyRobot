package robot.media.network.weather;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 23.08.11
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class YahooRetriever {

  public InputStream retrieve(String cityCode) throws Exception {
    String url = "http://xml.weather.yahoo.com/forecastrss?p=" + cityCode + "&u=c";
    URLConnection conn = new URL(url).openConnection();
    return conn.getInputStream();
  }
}