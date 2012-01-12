package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;
import robot.media.network.weather.Weather;
import robot.media.network.weather.WeatherFormatter;
import robot.media.network.weather.YahooParser;
import robot.media.network.weather.YahooRetriever;
import utils.RobotConsts;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 23.08.11
 * Time: 11:54
 * To change this template use File | Settings | File Templates.
 */
public class GetWeatherForecast extends RobotCommand {
    @Override
    public void execute(MyRobot robot) {
        String result = "Sorry, no internet connection";
        if (robot.getMemory().getCurrent(RobotConsts.MEMORY_INTERNET_STATUS) != null)
        {
            result = sayWeatherForecastCity(RobotConsts.WEATHER_BORYSPOL);
            result += sayWeatherForecastCity(RobotConsts.WEATHER_KIEV);
        }
        this.resultTarget.outResult(result);
    }

    protected String sayWeatherForecastCity(String cityCode) {
        try {
            InputStream dataIn = new YahooRetriever().retrieve(cityCode);
            Weather weather = new YahooParser().parse(dataIn);
            return  new WeatherFormatter().formatForecast(weather);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
