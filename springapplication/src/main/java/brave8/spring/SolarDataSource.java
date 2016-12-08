package brave8.spring;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SolarDataSource {

    public static final String KEY_ID_DATA = "id_data";
    public static final String KEY_ID_LOGIN = "id_login";
    public static final String KEY_POWER = "power";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_LIGHT = "light";
    public static final String KEY_BAROMETRIC = "bar_pressure";
    public static final String KEY_HUMIDITY = "humidity";
    public static final String KEY_TIME = "time";
    public static final String KEY_DATE = "date";
    public static final String JSON_ARRAY = "result";

    Context context;

    public SolarDataSource(Context context) {
        this.context = context;
    }

    public List<Solar> createSolarList(String jsonData) throws JSONException {
        List<Solar> solarList = new ArrayList<Solar>();

        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);
        for(int i = 0; i < result.length(); i++) {
            solarList.add(jsonToSolar(result.getJSONObject(i)));
        }
        return solarList;
    }

    private Solar jsonToSolar(JSONObject jsonObject) throws JSONException {
        Solar solar = new Solar();
        solar.setIdData(jsonObject.getInt(KEY_ID_DATA));
        solar.setIdLogin(jsonObject.getInt(KEY_ID_LOGIN));
        solar.setPower(jsonObject.getDouble(KEY_POWER));
        solar.setTemperature(jsonObject.getDouble(KEY_TEMPERATURE));
        solar.setLight(jsonObject.getDouble(KEY_LIGHT));
        solar.setBarometric(jsonObject.getDouble(KEY_BAROMETRIC));
        solar.setHumidity(jsonObject.getDouble(KEY_HUMIDITY));
        solar.setTime(jsonObject.getString(KEY_TIME));
        solar.setDate(jsonObject.getString(KEY_DATE));
        return solar;
    }
}