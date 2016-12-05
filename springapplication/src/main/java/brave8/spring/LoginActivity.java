/**
 * Brave 8
 */

package brave8.spring;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button signIn;
    EditText dbUser;
    EditText dbPass;
    public static final String MY_PREFS_NAME = "AppUsers";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //if device is xlarge(tablet) make the orientation always landscape
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if(screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("username", "user");
        editor.putString("password", "password");
        editor.apply();

        signIn = (Button) findViewById(R.id.sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dbUser = (EditText) findViewById(R.id.username);
                dbPass = (EditText) findViewById(R.id.password);
                //hello class
                //----------------------------
                //dbUser.setText("id");
                //dbPass.setText("spring");
                //----------------------------

                String dbUserStr = dbUser.getText().toString();
                String dbPassStr = dbPass.getText().toString();

                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String restoredUser = prefs.getString("username", null);
                String restoredPass = prefs.getString("password", null);

                boolean loginError = false;
                if(dbUserStr.equals("")){
                    dbUser.setError(getString(R.string.user_empty_error));
                    loginError = true;
                }
                else if(!restoredUser.equals(dbUserStr)){
                    dbUser.setError(getString(R.string.user_error));
                    loginError = true;
                }

                if(dbPassStr.equals("")){
                    dbPass.setError(getString(R.string.pass_empty_error));
                    loginError = true;
                }

                if(restoredUser.equals(dbUserStr)){
                    if(dbPassStr.equals("")){
                        dbPass.setError(getString(R.string.pass_empty_error));
                        loginError = true;
                    }
                    else if(!restoredPass.equals(dbPassStr)){
                        dbPass.setError(getString(R.string.pass_error));
                        loginError = true;
                    }
                }
                if(!loginError){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;

            //changing this in furture so it doesn't appear on the login actvity
            case R.id.action_settings:
                Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
