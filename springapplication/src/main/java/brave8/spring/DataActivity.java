/**
 * Brave 8
 */

package brave8.spring;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class DataActivity extends AppCompatActivity {

    GraphView graph;
    LineGraphSeries<DataPoint> series;
    DataPoint[] values;
    double[] power;
    double num =0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);


        //fake data
        Random rn = new Random();
        power = new double[4000];
        for (int i=0;i<power.length;i++)
        {
            power[i]= 1.0 + (20.0 - 1.0) * rn.nextDouble();
        }

        Spinner dropdown1 =(Spinner)findViewById(R.id.spinner1);
        String[] items = new String[]{"Power", "Humidity", "Temperature", "Barometric", "Light"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DataActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown1.setAdapter(adapter);

        Spinner dropdown2 = (Spinner)findViewById(R.id.spinner2);
        String[] items2 = new String[]{"1 Day", "7 Days", "30 Days"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(DataActivity.this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);
        graph = (GraphView) findViewById(R.id.graph);

        //----------
        GridLabelRenderer gridLabelRenderer = graph.getGridLabelRenderer();
        if (Build.VERSION.SDK_INT >= 23) {
            gridLabelRenderer.setGridColor(ContextCompat.getColor(this, R.color.black));
            gridLabelRenderer.setHorizontalLabelsColor(ContextCompat.getColor(this, R.color.black));
            gridLabelRenderer.setVerticalLabelsColor(ContextCompat.getColor(this, R.color.black));
        } else {
            gridLabelRenderer.setGridColor(getResources().getColor(R.color.black));
            gridLabelRenderer.setHorizontalLabelsColor(getResources().getColor(R.color.black));
            gridLabelRenderer.setVerticalLabelsColor(getResources().getColor(R.color.black));
        }
        graph.getGridLabelRenderer().reloadStyles();
        //-----------

        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(pos==0){
                    graph.removeAllSeries(); //clear last graph
                    values = new DataPoint[24]; //24 hours
                    for (int i =0,k=power.length-96;i<values.length;i++,k+=4) //24 entries, k=end of the array - 24*4
                    {
                        for(int x=0;x<4;x++) //combine four 15 min entries for 1 hour
                        {
                            num+=power[k+x]; //add four entries into one variable
                        }
                        values[i] = new DataPoint(i,num/4); //average of the four numbers
                        num=0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                }
                else if(pos==1){
                    graph.removeAllSeries();
                    values = new DataPoint[7];
                    for (int i =0,k=power.length-672;i<values.length;i++,k+=96)
                    {
                        for(int x=0;x<96;x++)
                        {
                            num+=power[k+x];
                        }
                        values[i] = new DataPoint(i,num/96);
                        num=0.0;
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                }
                else if(pos==2){
                    graph.removeAllSeries();
                    values = new DataPoint[4];
                    for (int i =0,k=power.length-2688;i<values.length;i++,k+=672)
                    {
                        for(int x=0;x<672;x++)
                        {
                            num+=power[k+x];
                        }
                        values[i] = new DataPoint(i,num/672);
                        num=0.0;
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {}
        });

        LinearLayout homeLayout = (LinearLayout) findViewById (R.id.home_layout);
        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout settingsLayout = (LinearLayout) findViewById (R.id.settings_layout);
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataActivity.this, SettingsActivity.class);
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
