package robot.media.network.weather;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 23.08.11
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
public class WeatherFormatter {

  public String format( Weather weather ) {
    String result = null;
    result = String.format("Weather in <sayas class=\"city\">%s</sayas>. Temperature is <sayas class=\"number\">%s</sayas> grad. %s. ", weather.getCity(), weather.getTemp(), weather.getCondition());
    return result;
  }
  public String formatForecast( Weather weather ) {
    String result = null;
    result = String.format("Weather forecast for tomorrow in <sayas class=\"city\">%s</sayas>. Temperature in the night will be <sayas class=\"number\">%s</sayas> grad. Daily temperature is up to<sayas class=\"number\">%s</sayas> grad. %s. ", weather.getCity(), weather.getForecastTempLow(), weather.getForecastTempHigh(), weather.getCondition());
    return result;
  }

}

