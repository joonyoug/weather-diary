package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DataWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiaryService {
    @Value("${openweathermap.key}")
    private String apiKey;

    private final DataWeatherRepository dataWeatherRepository;
    private final DiaryRepository diaryRepository;

    private static  final Logger logger= LoggerFactory.getLogger(WeatherApplication.class);
    public DiaryService(DataWeatherRepository dataWeatherRepository, DiaryRepository diaryRepository) {
        this.dataWeatherRepository = dataWeatherRepository;
        this.diaryRepository = diaryRepository;
    }
    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate(){
        dataWeatherRepository.save(getWeatherFromApi());
    }

    private DateWeather getWeatherFromApi(){

        String weatherData=getWeatherString();
        Map<String,Object> parseWeather = parseWeather(weatherData);
        DateWeather dateWeather=new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parseWeather.get("main").toString());
        dateWeather.setIcon(parseWeather.get("icon").toString());
        dateWeather.setTemperature((double) parseWeather.get("temperature"));
        return dateWeather;

    }
    private DateWeather getDateWeather(LocalDate date){
        logger.info("매일 날씨 가져오기");
        List<DateWeather> dateWeatherListFromDB=dataWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.size()==0){
            return getWeatherFromApi();
        }
        else{
            return dateWeatherListFromDB.get(0);
        }
    }
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date,String text){
        logger.info("start to create diary");
        DateWeather dateWeather=getDateWeather(date);

        Diary diary=new Diary();
        diary.setDateWeather(dateWeather);

        diary.setText(text);

        diaryRepository.save(diary);

        logger.info("end to create diary");

    }
    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date){
//        logger.debug("read diary");
//
//        if(date.isAfter(LocalDate.ofYearDay(3050,1))){
//            throw new InvalidDate();
//        }
        return diaryRepository.findAllByDate(date);

    }
    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate start,LocalDate end){

        return  diaryRepository.findAllByDateBetween(start,end)
       ;
    }
    public void updateDiary(LocalDate date,String text){
        Diary nowDiary=diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }
    public void deleteDiary(LocalDate date){
        diaryRepository.deleteAllByDate(date);
    }

    private Map<String, Object> parseWeather(String jsonString){
        JSONParser jsonParser =new JSONParser();
        JSONObject jsonObject;

        try{
            jsonObject=(JSONObject)jsonParser.parse(jsonString);

        }catch (ParseException e){
            throw new RuntimeException(e);
        }

        Map<String,Object> resultMap=new HashMap<>();
        JSONObject mainData=(JSONObject) jsonObject.get("main");
        resultMap.put("temp",mainData.get("temp"));
        JSONArray weatherArray=(JSONArray) jsonObject.get("weather");
        JSONObject weatherData=(JSONObject)weatherArray.get(0);
        resultMap.put("main",weatherData.get("main"));
        resultMap.put("icon",weatherData.get("icon"));

        return resultMap;


    }

    private String getWeatherString()  {

       String apiUrl= "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid="+apiKey;
        try {
            URL url=new URL(apiUrl);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode=connection.getResponseCode();
            BufferedReader br;
            if(responseCode==200){
                br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            else {
                br=new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response =new StringBuilder();
            while((inputLine=br.readLine())!=null){
                response.append(inputLine);
            }
            br.close();


            return response.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
