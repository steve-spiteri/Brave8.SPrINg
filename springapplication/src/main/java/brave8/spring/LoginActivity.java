/**
 * Brave 8
 */

package brave8.spring;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    Button signIn;
    EditText dbUser, dbPass;
    LinearLayout status_layout, login_layout;
    TextView status, incorrect;
    String[] userInfo;
    CheckBox rememberMe;

    public static final String MY_PREFS_NAME = "AppUsers";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new CheckStatusTask().execute();

        //if device is xlarge(tablet) make the orientation always landscape
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if(screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        signIn = (Button) findViewById(R.id.sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dbUser = (EditText) findViewById(R.id.username);
                dbPass = (EditText) findViewById(R.id.password);

                String dbUserStr = dbUser.getText().toString();
                String dbPassStr = dbPass.getText().toString();

                boolean loginError = false;
                if(dbUserStr.equals("")){
                    dbUser.setError(getString(R.string.user_empty_error));
                    loginError = true;
                }

                if(dbPassStr.equals("")){
                    dbPass.setError(getString(R.string.pass_empty_error));
                    loginError = true;
                }

                if(!loginError){
                    userInfo = new String[] {dbUser.getText().toString(),dbPass.getText().toString()};
                    new CheckLoginTask().execute(userInfo);
                }
            }
        });
    }

    private class CheckStatusTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... v) {
            boolean internetConnection = false;
            boolean databaseConnection = false;
            try {
                URL url = new URL("https://www.google.ca/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.connect();
                internetConnection = connection.getResponseCode() == 200;
            }
            catch (IOException e)
            {
            }

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall("http://springdb.eu5.org/spring/check_connection.php");
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray result = jsonObj.getJSONArray("result");
                    String db_conn = result.getJSONObject(0).getString("db_conn");
                    if(db_conn.equals("success"))
                    {
                        databaseConnection = true;
                    }
                }
                catch (JSONException e)
                {
                }
            }

            if (internetConnection&&databaseConnection)
            {
                return 1;
            }
            else if (!internetConnection)
            {
                return -1;
            }
            else if (internetConnection&&!databaseConnection)
            {
                return -2;
            }
            return -3;
        }

        protected void onPostExecute(Integer result) {
            status_layout = (LinearLayout) findViewById(R.id.status_layout);
            login_layout = (LinearLayout) findViewById(R.id.login_layout);
            status = (TextView) findViewById(R.id.status);
            if (result == 1)
            {
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String restoredUser = prefs.getString("username", null);
                String restoredPass = prefs.getString("password", null);

                if(restoredUser!=null&restoredPass!=null)
                {
                    userInfo = new String[]{restoredUser, restoredPass};
                    new CheckLoginTask().execute(userInfo);
                }
                else
                {
                    status_layout.setVisibility(View.GONE);
                    login_layout.setVisibility(View.VISIBLE);
                }
            }
            else if (result == -1)
            {
                status.setText(getString(R.string.internetError));
            }
            else if (result == -2)
            {
                status.setText(getString(R.string.databaseError));
            }

        }
    }

    private class CheckLoginTask extends AsyncTask<String[], Void, Integer> {

        @Override
        protected Integer doInBackground(String[]... string) {
            HttpHandler sh = new HttpHandler();
            String login = "http://springdb.eu5.org/spring/login.php?user="+string[0][0]+"&pass="+string[0][1];
            String jsonStr = sh.makeServiceCall(login);
            if (jsonStr != null) {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray result = jsonObj.getJSONArray("result");
                    String db_conn = result.getJSONObject(0).getString("id_login");
                    if(db_conn.equals("-1"))
                    {
                        return -1;
                    }
                    else{
                        return Integer.parseInt(db_conn);
                    }
                } catch (JSONException e)
                {
                }
            }
            return -2;
        }

        protected void onPostExecute(Integer result) {
            incorrect = (TextView) findViewById(R.id.incorrect);
            rememberMe = (CheckBox) findViewById(R.id.rememberMe_checkBox);

            if (result==-1)
            {
                incorrect.setText(R.string.userPassError);
                dbUser.setText("");
                dbPass.setText("");
            }
            else
            {
                if (rememberMe.isChecked())
                {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("username", dbUser.getText().toString());
                    editor.putString("password", dbPass.getText().toString());
                    editor.apply();
                }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("loginID", result);
                    startActivity(intent);
                    finish();
            }
        }
    }
}