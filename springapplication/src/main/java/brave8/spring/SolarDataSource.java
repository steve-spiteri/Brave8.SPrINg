package brave8.spring;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.telecom.Call;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SolarDataSource {

    //public static final String DATA_URL = " http://a77a1a4f.ngrok.io/spring/test2.php?id=";
    public static final String DATA_URL = " http://a77a1a4f.ngrok.io/spring/test2.php";
    public static final String DATA_URL_FETCH_BY_LOGIN = " http://c1f13104.ngrok.io/spring/fetch_data_by_login.php?id=2";
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
    private EditText editTextId;
    private Button buttonGet;
    private TextView textViewResult;

    private ProgressDialog loading;

    private String jsonReturn = "";

    /*public List<Solar> getList() {
        return list;
    }*/

    // private List<Solar> list;


    public SolarDataSource(Context context) {
        this.context = context;
        /*VolleyResponseListener listener = new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String response) {
                jsonReturn = response;
            }
        };
        setData(listener);*/
        /*this.editTextId = editTextId;
        this.buttonGet = buttonGet;
        this.textViewResult = textViewResult;*/
    }

    /*
    public String getData() {
        return jsonReturn;
    }

    public void setData(final VolleyResponseListener listener) {
        /*String id = editTextId.getText().toString().trim();
        if (id.equals("")) {
            Toast.makeText(context, "Please enter an id", Toast.LENGTH_LONG).show();
            return;
        }
        loading = ProgressDialog.show(context,"Please wait...","Fetching...",false,false);

        String url = DATA_URL_FETCH_BY_LOGIN;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                listener.onResponse(response);
                //showJSON();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());
                        Toast.makeText(context,error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void showJSON(){
        String name="";
        String address="";
        String vc = "";
        try {
            // Solar solar = new Solar();

            //JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObject = new JSONObject(jsonReturn);
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(1);

            // name = collegeData.getString(Config.KEY_ID_LOGIN);
            //address = collegeData.getString(Config.KEY_POWER);

            //Solar solar = jsonToSolar(collegeData);
           // List<Solar> solarList = createSolarList();
            //list = new ArrayList<Solar>();
            //list = createSolarList();
            //textViewResult.setText("Power:\t"+solarList.get(1).getPower()+"\nBar:\t" +solarList.get(1).getBarometric()+ "\nDate:\t"+ solarList.get(1).getDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

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

    /*
    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String response);
    }*/
}