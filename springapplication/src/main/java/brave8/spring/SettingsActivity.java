package brave8.spring;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch theme_switch = (Switch)findViewById(R.id.controls_application_theme);
        final TextView theme_text = (TextView)findViewById(R.id.text_theme_name);
        theme_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //ThemeUtils.changeToTheme(getActivity(), ThemeUtils.DARK);
                    theme_text.setText(R.string.dark_theme);
                } else {
                    //ThemeUtils.changeToTheme(getActivity(), ThemeUtils.LIGHT);
                    theme_text.setText(R.string.light_theme);
                }
            }
        });

        Switch temperature_switch = (Switch)findViewById(R.id.controls_change_temperature);
        final TextView temperature_text = (TextView)findViewById(R.id.text_temperature);
        temperature_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    temperature_text.setText(R.string.fahrenheit);

                } else {
                    temperature_text.setText(R.string.celsius);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, LoginActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;

            case R.id.action_settings:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
