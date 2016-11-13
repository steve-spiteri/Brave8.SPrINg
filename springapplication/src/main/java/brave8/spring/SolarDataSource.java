package brave8.spring;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricky on 2016-11-11.
 */

public class SolarDataSource extends AsyncTask<String, Void, String>{

    private Context context;

    public SolarDataSource(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... arg0) {
       /* String fullName = arg0[0];
        String userName = arg0[1];
        String passWord = arg0[2];
        String phoneNumber = arg0[3];
        String emailAddress = arg0[4];*/

        String id_login = "1";
        String power = "7.0";
        String temperature = "22";
        String light = "19000";
        String bar_pressure = "55";
        String humidity = "50.3";
        String date = "25/09/2014 6:28:21";


        String link;
        String data;
        BufferedReader bufferedReader;
        String result;

        try {
            data = "?id_login=" + URLEncoder.encode(id_login, "UTF-8");
            data += "&power=" + URLEncoder.encode(power, "UTF-8");
            data += "&temperature=" + URLEncoder.encode(temperature, "UTF-8");
            data += "&light=" + URLEncoder.encode(light, "UTF-8");
            data += "&bar_pressure=" + URLEncoder.encode(bar_pressure, "UTF-8");
            data += "&humidity=" + URLEncoder.encode(humidity, "UTF-8");
            data += "&date=" + URLEncoder.encode(date, "UTF-8");

            link = "http://691840d3.ngrok.io/spring/insert_test2.php" + data;
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            result = bufferedReader.readLine();
            return result;
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {

                JSONObject jsonObj = new JSONObject();
                String query_result = jsonObj.getString("query_result");
                if (query_result.equals("SUCCESS")) {
                    Toast.makeText(context, "Data inserted successfully. Signup successfull.", Toast.LENGTH_SHORT).show();
                } else if (query_result.equals("FAILURE")) {
                    Toast.makeText(context, "Data could not be inserted. Signup failed.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Couldn't connect to remote database.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Couldn't get any JSON data.", Toast.LENGTH_SHORT).show();
        }
    }


    /*String str;

    public SolarDataSource() throws IOException, ClassNotFoundException, SQLException {
       URL url = new URL("http://localhost/spring/test.php");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        InputStream readDB = conn.getInputStream();
        InputStreamReader readDBReadewr = new InputStreamReader(readDB);
         str = "Hey there";
        insertToDatabase("test", "test");

    }
    public String getStr()
    {
        return str;
    }

    private void insertToDatabase(String name, String add) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id_login", "1"));
                nameValuePairs.add(new BasicNameValuePair("power", "8.0"));
                nameValuePairs.add(new BasicNameValuePair("temperature", "22"));
                nameValuePairs.add(new BasicNameValuePair("light", "19800"));
                nameValuePairs.add(new BasicNameValuePair("bar_pressure", "102.3"));
                nameValuePairs.add(new BasicNameValuePair("humidity", "77"));
                nameValuePairs.add(new BasicNameValuePair("date", "25/09/2014 6:28:21"));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://localhost/spring/insert_test.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }

                return "success";
            }

            @Override
            protected void onPostExecute(String result){
                if(result.equalsIgnoreCase("Exception Caught")){

                }else{

                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(name, add);
    }
*/

}
