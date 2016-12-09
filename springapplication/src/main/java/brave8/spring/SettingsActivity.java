package brave8.spring;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import static brave8.spring.LoginActivity.MY_PREFS_NAME;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch temperature_switch = (Switch)findViewById(R.id.controls_change_temperature);
        final TextView temperature_text = (TextView)findViewById(R.id.text_temperature);

        SharedPreferences settings = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if(settings.getString("temperature", "").equals("fahrenheit"))
        {
            temperature_switch.setChecked(true);
            temperature_text.setText(R.string.fahrenheit);
        }

        temperature_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("temperature", "fahrenheit");
                    editor.apply();
                    temperature_text.setText(R.string.fahrenheit);

                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("temperature", "celsius");
                    editor.apply();
                    temperature_text.setText(R.string.celsius);
                }
            }
        });

        Button logoutBtn = (Button)findViewById(R.id.button_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.remove("user");
                editor.remove("password");
                editor.apply();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
