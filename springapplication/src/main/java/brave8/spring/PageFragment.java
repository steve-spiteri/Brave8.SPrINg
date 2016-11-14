package brave8.spring;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;

import java.util.List;
import java.util.Random;
//test
public class PageFragment extends Fragment implements OnItemSelectedListener {
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
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    //data variables
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    DataPoint[] values;
    double num =0.0;
    List<Solar> solarList;
    Spinner spinner1;
    Spinner spinner2;
    int spinner2Pos = 0;
    GridLabelRenderer gridLabelRenderer;

    double[] power;
    double[] temperature;
    double[] light;
    double[] barometric;
    double[] humidity;
    String[] time;
    String[] date;

    public static PageFragment create(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if(mPage == 1) {
            view = inflater.inflate(R.layout.activity_home, container, false);
        }
        else if(mPage == 2) {
            view = inflater.inflate(R.layout.activity_data, container, false);
        }
        else {
            view = inflater.inflate(R.layout.activity_settings, container, false);
        }
        setData(view);
        return view;
    }

    public void setData(final View view) {

        loading = ProgressDialog.show(getContext(),"Please wait...","Fetching...",false,false);

        String url = DATA_URL_FETCH_BY_LOGIN;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                displayData(view, response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void displayData(View view, String response) {


        /////////////////

        power = new double[2];
        SolarDataSource solarDataSource = new SolarDataSource(getContext());
        try {
            //String jsonData = solarDataSource.getData();

            //Initialize array sizes
            solarList = solarDataSource.createSolarList(response);
            power = new double[solarList.size()];
            temperature = new double[solarList.size()];
            light = new double[solarList.size()];
            barometric = new double[solarList.size()];
            humidity = new double[solarList.size()];
            time = new String[solarList.size()];
            date = new String[solarList.size()];

            for (int i = 0; i < solarList.size(); i++)
            {
                power[i] = solarList.get(i).getPower();
                temperature[i] = solarList.get(i).getTemperature();
                light[i] = solarList.get(i).getLight();
                barometric[i] = solarList.get(i).getBarometric();
                humidity[i] = solarList.get(i).getHumidity();
                time[i] = solarList.get(i).getTime();
                date[i] = solarList.get(i).getDate();

                String string = Double.toString(barometric[i]);
                Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /////////////////

        if (mPage == 1) {
            //home activity

            TextView world_data = (TextView) view.findViewById(R.id.world_icon_data);
            //insert code to retrieve from data base
            world_data.setText("17.3" + " kWh");

            TextView barchart_data = (TextView) view.findViewById(R.id.barchart_icon_data);
            //insert code to retrieve from data base
            barchart_data.setText("3.44" + " kWh");

            TextView bolt_data = (TextView) view.findViewById(R.id.bolt_icon_data);
            //insert code to retrieve from data base
            bolt_data.setText("2.17" + " kWh");

            TextView sun_data = (TextView) view.findViewById(R.id.sun_icon_data);
            //insert code to retrieve from data base
            sun_data.setText("17" + " C");

            TextView raindrop_data = (TextView) view.findViewById(R.id.raindrop_icon_data);
            //insert code to retrieve from data base
            raindrop_data.setText("45" + "%");

            TextView updown_data = (TextView) view.findViewById(R.id.updown_icon_data);
            //insert code to retrieve from data base
            updown_data.setText("101 355" + " kPa");
        }
        else if (mPage == 2) {


            //fake data
            //Random rn = new Random();
            //power = new double[4000];
            /*for (int i=0;i<power.length;i++)
            {
                power[i]= 1.0 + (20.0 - 1.0) * rn.nextDouble();
            }*/

            spinner1 = (Spinner) view.findViewById(R.id.spinner1);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.source_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner1.setAdapter(adapter);

            spinner2 = (Spinner) view.findViewById(R.id.spinner2);
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.time_array, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapter2);

            graph = (GraphView) view.findViewById(R.id.graph);

            //----------
            gridLabelRenderer = graph.getGridLabelRenderer();
            if (Build.VERSION.SDK_INT >= 23) {
                gridLabelRenderer.setGridColor(ContextCompat.getColor(getContext(), R.color.black));
                gridLabelRenderer.setHorizontalLabelsColor(ContextCompat.getColor(getContext(), R.color.black));
                gridLabelRenderer.setVerticalLabelsColor(ContextCompat.getColor(getContext(), R.color.black));
                //gridLabelRenderer.setHorizontalAxisTitle("test");
            } else {
                gridLabelRenderer.setGridColor(getResources().getColor(R.color.black));
                gridLabelRenderer.setHorizontalLabelsColor(getResources().getColor(R.color.black));
                gridLabelRenderer.setVerticalLabelsColor(getResources().getColor(R.color.black));
            }
            graph.getGridLabelRenderer().reloadStyles();
            //-----------

            spinner1.setOnItemSelectedListener(this);
            spinner2.setOnItemSelectedListener(this);
            //spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(spinner1.getSelectedItemPosition()==0){
            graph.removeAllSeries(); //clear last graph

            ///////////

            values = new DataPoint[3];
            values[0] = new DataPoint(0, power[0]);
            values[1] = new DataPoint(1, power[1]);
            values[2] = new DataPoint(2, power[2]);

            ///////////
            if(spinner2.getSelectedItemPosition() == 0) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(24); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Voltage"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Hour"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(12); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(7); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Voltage"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Day"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(7); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(30); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Voltage"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Day"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(10); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }

                        /*values = new DataPoint[24]; //24 hours
                        for (int i =0,k=power.length-96;i<values.length;i++,k+=4) //24 entries, k=end of the array - 24*4
                        {
                            for(int x=0;x<4;x++) //combine four 15 min entries for 1 hour
                            {
                                num+=power[k+x]; //add four entries into one variable
                            }
                            values[i] = new DataPoint(i,num/4); //average of the four numbers
                            num=0.0; //reset num so it can be used again
                        }*/

        }
        else if(spinner1.getSelectedItemPosition()==1){
            graph.removeAllSeries(); //clear last graph

            ///////////

            values = new DataPoint[3];
            values[0] = new DataPoint(0, humidity[0]);
            values[1] = new DataPoint(1, humidity[1]);
            values[2] = new DataPoint(2, humidity[2]);

            ///////////

                        /*values = new DataPoint[24]; //24 hours
                        for (int i =0,k=power.length-96;i<values.length;i++,k+=4) //24 entries, k=end of the array - 24*4
                        {
                            for(int x=0;x<4;x++) //combine four 15 min entries for 1 hour
                            {
                                num+=power[k+x]; //add four entries into one variable
                            }
                            values[i] = new DataPoint(i,num/4); //average of the four numbers
                            num=0.0; //reset num so it can be used again
                        }*/
            if(spinner2.getSelectedItemPosition() == 0) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(24); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Humidity"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Hour"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(12); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(7); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Humidity"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Day"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(7); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(30); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Humidity"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Day"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(10); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
                       /* graph.removeAllSeries(); //clear last graph
                        values = new DataPoint[7]; //7 days
                        for (int i =0,k=power.length-672;i<values.length;i++,k+=96) //7 entries, k=end of array - (24*4)*7
                        {
                            for(int x=0;x<96;x++) //combine 96 15 min entries for 1 day
                            {
                                num+=power[k+x]; //add 96 entries into one variable
                            }
                            values[i] = new DataPoint(i,num/96); //average the 96 numbers
                            num=0.0; //reset num so it can be used again
                        }
                        series = new LineGraphSeries<>(values);
                        graph.addSeries(series);
                        graph.getViewport().setXAxisBoundsManual(true);
                        graph.getViewport().setMaxX(7); //so you can see last label
                        gridLabelRenderer.setVerticalAxisTitle("Voltage");
                        gridLabelRenderer.setHorizontalAxisTitle("Day");
                        gridLabelRenderer.setNumHorizontalLabels(7);
                        graph.getGridLabelRenderer().reloadStyles();*/
        }
        else if(spinner1.getSelectedItemPosition()==2){
            graph.removeAllSeries(); //clear last graph

            ///////////

            values = new DataPoint[3];
            values[0] = new DataPoint(0, temperature[0]);
            values[1] = new DataPoint(1, temperature[1]);
            values[2] = new DataPoint(2, temperature[2]);

            ///////////

                        /*values = new DataPoint[24]; //24 hours
                        for (int i =0,k=power.length-96;i<values.length;i++,k+=4) //24 entries, k=end of the array - 24*4
                        {
                            for(int x=0;x<4;x++) //combine four 15 min entries for 1 hour
                            {
                                num+=power[k+x]; //add four entries into one variable
                            }
                            values[i] = new DataPoint(i,num/4); //average of the four numbers
                            num=0.0; //reset num so it can be used again
                        }*/
            if(spinner2.getSelectedItemPosition() == 0) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(24); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Temperature"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Hour"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(12); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(7); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Temperature"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Day"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(7); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(30); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Temperature"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Day"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(10); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
                        /*graph.removeAllSeries(); //clear last graph
                        values = new DataPoint[4]; //4 weeks
                        for (int i =0,k=power.length-2688;i<values.length;i++,k+=672) //4 entries, k=end of array = ((24*4)*7)*4
                        {
                            for(int x=0;x<672;x++) //combine 672 15 min entries int one vatiable
                            {
                                num+=power[k+x]; //add 672 entries into one variable
                            }
                            values[i] = new DataPoint(i,num/672); //average the 672 numbers
                            num=0.0; //reset num so it can be used again
                        }
                        series = new LineGraphSeries<>(values);
                        graph.addSeries(series);
                        graph.getViewport().setXAxisBoundsManual(true);
                        graph.getViewport().setMaxX(4); //so you can see last label
                        gridLabelRenderer.setVerticalAxisTitle("Voltage");
                        gridLabelRenderer.setHorizontalAxisTitle("Week");
                        gridLabelRenderer.setNumHorizontalLabels(4);
                        graph.getGridLabelRenderer().reloadStyles();*/
        }
        else if(spinner1.getSelectedItemPosition() == 3) {
            graph.removeAllSeries(); //clear last graph

            ///////////

            values = new DataPoint[3];
            values[0] = new DataPoint(0, barometric[0]);
            values[1] = new DataPoint(1, barometric[1]);
            values[2] = new DataPoint(2, barometric[2]);

            ///////////

                        /*values = new DataPoint[24]; //24 hours
                        for (int i =0,k=power.length-96;i<values.length;i++,k+=4) //24 entries, k=end of the array - 24*4
                        {
                            for(int x=0;x<4;x++) //combine four 15 min entries for 1 hour
                            {
                                num+=power[k+x]; //add four entries into one variable
                            }
                            values[i] = new DataPoint(i,num/4); //average of the four numbers
                            num=0.0; //reset num so it can be used again
                        }*/
            if(spinner2.getSelectedItemPosition() == 0) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(24); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Barometric"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Hour"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(12); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(7); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Barometric"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Day"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(7); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(30); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Barometric"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Day"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(10); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
        }
        else if(spinner1.getSelectedItemPosition() == 4) {
            graph.removeAllSeries(); //clear last graph

            ///////////

            values = new DataPoint[3];
            values[0] = new DataPoint(0, light[0]);
            values[1] = new DataPoint(1, light[1]);
            values[2] = new DataPoint(2, light[2]);

            ///////////

                        /*values = new DataPoint[24]; //24 hours
                        for (int i =0,k=power.length-96;i<values.length;i++,k+=4) //24 entries, k=end of the array - 24*4
                        {
                            for(int x=0;x<4;x++) //combine four 15 min entries for 1 hour
                            {
                                num+=power[k+x]; //add four entries into one variable
                            }
                            values[i] = new DataPoint(i,num/4); //average of the four numbers
                            num=0.0; //reset num so it can be used again
                        }*/
            if(spinner2.getSelectedItemPosition() == 0) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(24); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Light"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Hour"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(12); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(7); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Light"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Day"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(7); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(30); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle("Light"); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle("Day"); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(10); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
        }
    }

    public void onNothingSelected(AdapterView<?> parent)
    {}

    public void rangeSpinner(){
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(pos == 0){
                    spinner2Pos = 0;
                }
                else if(pos == 1) {
                    spinner2Pos = 1;
                }
                else if(pos == 2) {
                    spinner2Pos = 2;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
