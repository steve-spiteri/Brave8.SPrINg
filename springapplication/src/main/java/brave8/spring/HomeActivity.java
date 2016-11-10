/**
 * Brave 8
 */

package brave8.spring;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

//RICKy is best
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //when inserting from database only change the first string in the setText()
        //assuming we store a raw number and not a formatted string

        TextView world_data = (TextView) findViewById(R.id.world_icon_data);
        //insert code to retrieve from data base
        world_data.setText("17.3" + " kWh");

        TextView barchart_data = (TextView) findViewById(R.id.barchart_icon_data);
        //insert code to retrieve from data base
        barchart_data.setText("3.44" + " kWh");

        TextView bolt_data = (TextView) findViewById(R.id.bolt_icon_data);
        //insert code to retrieve from data base
        bolt_data.setText("2.17" + " kWh");

        TextView sun_data = (TextView) findViewById(R.id.sun_icon_data);
        //insert code to retrieve from data base
        sun_data.setText("17" + " C");

        TextView raindrop_data = (TextView) findViewById(R.id.raindrop_icon_data);
        //insert code to retrieve from data base
        raindrop_data.setText("45" + "%");

        TextView updown_data = (TextView) findViewById(R.id.updown_icon_data);
        //insert code to retrieve from data base
        updown_data.setText("101 355" + " kPa");

        LinearLayout dataLayout = (LinearLayout) findViewById (R.id.data_layout);
        dataLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DataActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout settingsLayout = (LinearLayout) findViewById (R.id.settings_layout);
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.back_pressed_title))
                .setMessage(getString(R.string.back_pressed_message))
                .setPositiveButton((getString(R.string.back_pressed_positive)), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton((getString(R.string.back_pressed_negative)), null)
                .show();
    }
}
