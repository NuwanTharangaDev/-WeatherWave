package weatherdashboard.service;

import weatherdashboard.config.ApiConfig;
import weatherdashboard.model.WeatherData;
import weatherdashboard.model.HourlyForecast;
import weatherdashboard.model.DailyForecast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service handling network API communications with OpenWeatherMap REST endpoints.
 * Parses raw JSON responses into strongly-typed domain model POJOs.
 */
public class WeatherApiService {

    public CompletableFuture<WeatherData> fetchCurrentWeather(String city) {
        return fetchCurrentWeather(city, "metric");
    }

    public CompletableFuture<WeatherData> fetchCurrentWeather(String city, String unit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlStr = ApiConfig.BASE_URL + "?q=" + URLEncoder.encode(city, "UTF-8")
                        + "&appid=" + ApiConfig.API_KEY + "&units=" + unit;
                String jsonResponse = executeHttpGet(urlStr);
                return parseCurrentWeatherJson(jsonResponse);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<List<HourlyForecast>> fetchHourlyForecast(String city) {
        return fetchHourlyForecast(city, "metric");
    }

    public CompletableFuture<List<HourlyForecast>> fetchHourlyForecast(String city, String unit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlStr = ApiConfig.FORECAST_URL + "?q=" + URLEncoder.encode(city, "UTF-8")
                        + "&appid=" + ApiConfig.API_KEY + "&units=" + unit;
                String jsonResponse = executeHttpGet(urlStr);
                return parseHourlyForecastJson(jsonResponse);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<List<DailyForecast>> fetch5DayForecast(String city) {
        return fetch5DayForecast(city, "metric");
    }

    public CompletableFuture<List<DailyForecast>> fetch5DayForecast(String city, String unit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlStr = ApiConfig.FORECAST_URL + "?q=" + URLEncoder.encode(city, "UTF-8")
                        + "&appid=" + ApiConfig.API_KEY + "&units=" + unit;
                String jsonResponse = executeHttpGet(urlStr);
                return parse5DayForecastJson(jsonResponse);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<Double> fetchUVIndex(double lat, double lon) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlStr = ApiConfig.UV_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + ApiConfig.API_KEY;
                String jsonResponse = executeHttpGet(urlStr);
                JSONObject json = new JSONObject(jsonResponse);
                return json.getDouble("value");
            } catch (Exception e) {
                return 0.0;
            }
        });
    }

    private String executeHttpGet(String urlString) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = URI.create(urlString).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", ApiConfig.USER_AGENT);
            conn.setConnectTimeout(ApiConfig.CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(ApiConfig.READ_TIMEOUT_MS);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                if (responseCode == 401) throw new Exception("Invalid API Key");
                if (responseCode == 404) throw new Exception("City not found");
                throw new Exception("API Error: " + responseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private WeatherData parseCurrentWeatherJson(String jsonData) throws Exception {
        JSONObject json = new JSONObject(jsonData);
        JSONObject coord = json.getJSONObject("coord");
        double lat = coord.getDouble("lat");
        double lon = coord.getDouble("lon");

        String cityName = json.getString("name");
        JSONObject sys = json.getJSONObject("sys");
        String country = sys.getString("country");

        JSONObject main = json.getJSONObject("main");
        double temp = main.getDouble("temp");
        double feelsLike = main.getDouble("feels_like");
        int humidity = main.getInt("humidity");
        double pressure = main.getDouble("pressure");

        JSONArray weatherArray = json.getJSONArray("weather");
        JSONObject weather = weatherArray.getJSONObject(0);
        String description = weather.getString("description");
        String iconCode = weather.getString("icon");

        JSONObject wind = json.getJSONObject("wind");
        double windSpeed = wind.getDouble("speed");
        int visibility = json.optInt("visibility", 0);

        long sunrise = sys.getLong("sunrise");
        long sunset = sys.getLong("sunset");

        return new WeatherData(cityName, country, temp, feelsLike, humidity, pressure,
                visibility, windSpeed, description, iconCode, lat, lon, sunrise, sunset);
    }

    private List<HourlyForecast> parseHourlyForecastJson(String jsonData) throws Exception {
        List<HourlyForecast> list = new ArrayList<>();
        JSONObject json = new JSONObject(jsonData);
        JSONArray array = json.getJSONArray("list");

        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat displayFormat = new SimpleDateFormat("h a");

        for (int i = 0; i < Math.min(6, array.length()); i++) {
            JSONObject item = array.getJSONObject(i);
            String dateTime = item.getString("dt_txt");
            JSONObject main = item.getJSONObject("main");
            double temp = main.getDouble("temp");

            JSONArray weatherArray = item.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String iconCode = weather.getString("icon");

            String timeLabel;
            try {
                Date dateObj = parseFormat.parse(dateTime);
                timeLabel = displayFormat.format(dateObj);
            } catch (Exception ex) {
                timeLabel = dateTime.split(" ")[1].substring(0, 5);
            }

            list.add(new HourlyForecast(timeLabel, temp, iconCode));
        }

        return list;
    }

    private List<DailyForecast> parse5DayForecastJson(String jsonData) throws Exception {
        List<DailyForecast> result = new ArrayList<>();
        JSONObject json = new JSONObject(jsonData);
        JSONArray list = json.getJSONArray("list");

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
        Map<String, List<JSONObject>> dailyMap = new LinkedHashMap<>();

        for (int i = 0; i < list.length(); i++) {
            JSONObject forecast = list.getJSONObject(i);
            String dateTime = forecast.getString("dt_txt");
            String dateKey = dateTime.split(" ")[0];

            dailyMap.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(forecast);
        }

        SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayKey = todayFormat.format(new Date());
        if (dailyMap.containsKey(todayKey) && dailyMap.size() > 5) {
            dailyMap.remove(todayKey);
        }

        int count = 0;
        for (Map.Entry<String, List<JSONObject>> entry : dailyMap.entrySet()) {
            if (count >= 5) break;

            List<JSONObject> dayItems = entry.getValue();
            double tempMax = -100.0;
            double tempMin = 100.0;
            JSONObject selectedEntry = dayItems.get(0);
            int minDiff = Integer.MAX_VALUE;

            for (JSONObject item : dayItems) {
                JSONObject main = item.getJSONObject("main");
                double max = main.getDouble("temp_max");
                double min = main.getDouble("temp_min");
                if (max > tempMax) tempMax = max;
                if (min < tempMin) tempMin = min;

                String dateTimeStr = item.getString("dt_txt");
                String timeStr = dateTimeStr.split(" ")[1];
                int hour = Integer.parseInt(timeStr.split(":")[0]);
                int diff = Math.abs(hour - 12);
                if (diff < minDiff) {
                    minDiff = diff;
                    selectedEntry = item;
                }
            }

            JSONArray weatherArray = selectedEntry.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String description = weather.getString("description");
            String icon = weather.getString("icon");

            long timestamp = selectedEntry.getLong("dt") * 1000;
            String dayName = dayFormat.format(new Date(timestamp));

            result.add(new DailyForecast(dayName, tempMax, tempMin, description, icon));
            count++;
        }

        return result;
    }
}
