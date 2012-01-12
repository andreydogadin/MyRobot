package robot.media.network.weather;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 23.08.11
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
public class Weather {
  private String city;
  private String region;
  private String country;
  private String condition;
  private String temp;
  private String chill;
  private String humidity;

    public String getForecastDay() {
        return forecastDay;
    }

    public void setForecastDay(String forecastDay) {
        this.forecastDay = forecastDay;
    }

    public String getForecastTempHigh() {
        return forecastTempHigh;
    }

    public void setForecastTempHigh(String forecastTempHigh) {
        this.forecastTempHigh = forecastTempHigh;
    }

    public String getForecastTempLow() {
        return forecastTempLow;
    }

    public void setForecastTempLow(String forecastTempLow) {
        this.forecastTempLow = forecastTempLow;
    }

    public String getForecastCondition() {
        return forecastCondition;
    }

    public void setForecastCondition(String forecastCondition) {
        this.forecastCondition = forecastCondition;
    }

    private String forecastDay;
  private String forecastTempHigh;
  private String forecastTempLow;
  private String forecastCondition;


  public Weather() {}

  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }

  public String getRegion() { return region; }
  public void setRegion(String region) { this.region = region; }

  public String getCountry() { return country; }
  public void setCountry(String country) { this.country = country; }

  public String getCondition() { return condition; }
  public void setCondition(String condition) { this.condition = condition; }

  public String getTemp() { return temp; }
  public void setTemp(String temp) { this.temp = temp; }

  public String getChill() { return chill; }
  public void setChill(String chill) { this.chill = chill; }

  public String getHumidity() { return humidity; }
  public void setHumidity(String humidity) { this.humidity = humidity; }

}