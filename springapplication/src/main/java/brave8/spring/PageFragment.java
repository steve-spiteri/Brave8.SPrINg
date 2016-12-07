package brave8.spring;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
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

public class PageFragment extends Fragment implements OnItemSelectedListener {

    public static final String DATA_URL_FETCH_BY_LOGIN = "http://springdb.eu5.org//spring/fetch_data_by_login.php?id=2";
    private ProgressDialog loading;

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
    GridLabelRenderer gridLabelRenderer;

    double[] power;
    double[] temperature;
    double[] light;
    double[] barometric;
    double[] humidity;
    String[] time;
    String[] date;

    //constants
    private int ONE_DAY = 96;
    private int ONE_WEEK = 672;
    private int ONE_MONTH = 2688;
    private int SEVEN_DAYS = 7;
    private int FOUR_WEEKS = 4;


    //fake data variables
    double[] fakepower;
    double[] faketemp;
    double[] fakelight;
    double[] fakebarometric;
    double[] fakehumidity;

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
                        Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void displayData(View view, String response) {
        SolarDataSource solarDataSource = new SolarDataSource(getContext());
        try {
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
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mPage == 1) {
            //home activity

            //set date
            TextView date_text = (TextView) view.findViewById(R.id.date_text);
            date_text.setText(date[date.length-1]);

            //set time
            TextView time_text = (TextView) view.findViewById(R.id.time_text);
            time_text.setText(time[time.length-1]);

            TextView power_data = (TextView) view.findViewById(R.id.power_data);
            power_data.setText(getResources().getString(R.string.power_data,power[power.length-1]));

            TextView humidity_text = (TextView) view.findViewById(R.id.humidity_data);
            humidity_text.setText(getResources().getString(R.string.humidity_data,humidity[humidity.length-1]));

            TextView temperature_text = (TextView) view.findViewById(R.id.temperature_data);
            temperature_text.setText(getResources().getString(R.string.temperature_data,temperature[temperature.length-1],"C"));

            TextView barometric_text = (TextView) view.findViewById(R.id.barometric_data);
            barometric_text.setText(getResources().getString(R.string.barometric_date,barometric[barometric.length-1]));

            TextView light_text = (TextView) view.findViewById(R.id.light_data);
            light_text.setText("Sunny"); //-------------------------------------------temp hard coded string

        }
        else{

            //fake data-----------------------------------------------------------------------------
            Random rn = new Random();
            fakepower = new double[4000];
            for (int i=0;i<fakepower.length;i++)
            {
                fakepower[i]= 1.0 + (20.0 - 1.0) * rn.nextDouble();
            }
            faketemp = new double[4000];
            for (int i=0;i<faketemp.length;i++)
            {
                faketemp[i]= 1.0 + (20.0 - 1.0) * rn.nextDouble();
            }
            fakelight = new double[4000];
            for (int i=0;i<fakelight.length;i++)
            {
                fakelight[i]= 0.0 + (100000.0 - 0.0) * rn.nextDouble();
            }
            fakebarometric = new double[4000];
            for (int i=0;i<fakebarometric.length;i++)
            {
                fakebarometric[i]= 90.0 + (105.0 - 90.0) * rn.nextDouble();
            }
            fakehumidity = new double[4000];
            for (int i=0;i<fakehumidity.length;i++)
            {
                fakehumidity[i]= 30.0 + (70.0 - 30.0) * rn.nextDouble();
            }
            //--------------------------------------------------------------------------------------

            spinner1 = (Spinner) view.findViewById(R.id.spinner1);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.source_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner1.setAdapter(adapter);

            spinner2 = (Spinner) view.findViewById(R.id.spinner2);
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.time_array, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapter2);

            graph = (GraphView) view.findViewById(R.id.graph);

            gridLabelRenderer = graph.getGridLabelRenderer();
            if (Build.VERSION.SDK_INT >= 23) {
                gridLabelRenderer.setGridColor(ContextCompat.getColor(getContext(), R.color.black));
                gridLabelRenderer.setHorizontalLabelsColor(ContextCompat.getColor(getContext(), R.color.black));
                gridLabelRenderer.setVerticalLabelsColor(ContextCompat.getColor(getContext(), R.color.black));
            } else {
                gridLabelRenderer.setGridColor(getResources().getColor(R.color.black));
                gridLabelRenderer.setHorizontalLabelsColor(getResources().getColor(R.color.black));
                gridLabelRenderer.setVerticalLabelsColor(getResources().getColor(R.color.black));
            }
            graph.getGridLabelRenderer().reloadStyles();

            spinner1.setOnItemSelectedListener(this);
            spinner2.setOnItemSelectedListener(this);
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(spinner1.getSelectedItemPosition()==0){ //equals power
            graph.removeAllSeries(); //clear last graph
            //-------------------------------------------------------------------------------------------------------
            //POWER
            if(spinner2.getSelectedItemPosition() == 0) {
                //real data
                values = new DataPoint[24]; //Temporary (24 entries per day for now, will be 96)
                for(int i = 0; i < values.length; i++) {
                    values[i] = new DataPoint(i, power[i]);
                }
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(24); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.voltage)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.hour)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(12); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                //fake data
                values = new DataPoint[SEVEN_DAYS]; //7 days
                for (int i =0,k=fakepower.length-ONE_WEEK;i<values.length;i++,k+=ONE_DAY) //7 entries, k=end of array - (24*4)*7
                {
                    for(int x=0;x<ONE_DAY;x++) //combine 96 15 min entries for 1 day
                    {
                        num+=fakepower[k+x]; //add 96 entries into one variable
                    }
                    values[i] = new DataPoint(i,num/ONE_DAY); //average the 96 numbers
                    num=0.0; //reset num so it can be used again
                }
                series = new LineGraphSeries<>(values);
                graph.addSeries(series);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(7); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.voltage)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.day)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(7);
                graph.getGridLabelRenderer().reloadStyles();
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                //fake data
                values = new DataPoint[FOUR_WEEKS]; //4 weeks
                for (int i =0,k=fakepower.length-ONE_MONTH;i<values.length;i++,k+=ONE_WEEK) //4 entries, k=end of array = ((24*4)*7)*4
                {
                    for(int x=0;x<ONE_WEEK;x++) //combine 672 15 min entries int one vatiable
                    {
                        num+=fakepower[k+x]; //add 672 entries into one variable
                    }
                    values[i] = new DataPoint(i,num/ONE_WEEK); //average the 672 numbers
                    num=0.0; //reset num so it can be used again
                }
                series = new LineGraphSeries<>(values);
                graph.addSeries(series);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(4); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.voltage)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.week)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(4);
                graph.getGridLabelRenderer().reloadStyles();
            }
        }
        //-------------------------------------------------------------------------------------------------------
        //HUMIDITY
        else if(spinner1.getSelectedItemPosition()==1){
            graph.removeAllSeries(); //clear last graph

            if(spinner2.getSelectedItemPosition() == 0) {
                //REAL DATA
                values = new DataPoint[24]; //Temporary (24 entries per day for nwo, will be 96)
                for(int i = 0; i < values.length; i++) {
                    values[i] = new DataPoint(i, humidity[i]);
                }
                graph.removeAllSeries(); //clear last graph
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(24); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.humidity)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.hour)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(12); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                values = new DataPoint[SEVEN_DAYS]; //7 days
                for (int i =0,k=fakehumidity.length-ONE_WEEK;i<values.length;i++,k+=ONE_DAY) //7 entries, k=end of array - (24*4)*7
                {
                    for(int x=0;x<ONE_DAY;x++) //combine 96 15 min entries for 1 day
                    {
                        num+=fakehumidity[k+x]; //add 96 entries into one variable
                    }
                    values[i] = new DataPoint(i,num/ONE_DAY); //average the 96 numbers
                    num=0.0; //reset num so it can be used again
                }
                series = new LineGraphSeries<>(values);
                graph.addSeries(series);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(7); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.humidity)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.day)); //set horizontal axis title;
                gridLabelRenderer.setNumHorizontalLabels(7);
                graph.getGridLabelRenderer().reloadStyles();
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                values = new DataPoint[FOUR_WEEKS]; //4 weeks
                for (int i =0,k=fakehumidity.length-ONE_MONTH;i<values.length;i++,k+=ONE_WEEK) //4 entries, k=end of array = ((24*4)*7)*4
                {
                    for(int x=0;x<ONE_WEEK;x++) //combine 672 15 min entries int one vatiable
                    {
                        num+=fakehumidity[k+x]; //add 672 entries into one variable
                    }
                    values[i] = new DataPoint(i,num/ONE_WEEK); //average the 672 numbers
                    num=0.0; //reset num so it can be used again
                }
                series = new LineGraphSeries<>(values);
                graph.addSeries(series);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(4); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.humidity)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.week)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(4);
                graph.getGridLabelRenderer().reloadStyles();
            }
        }
        //-------------------------------------------------------------------------------------------------------
        //TEMPERATURE
        else if(spinner1.getSelectedItemPosition()==2){
            graph.removeAllSeries(); //clear last graph
            if(spinner2.getSelectedItemPosition() == 0) {
                //REAL DATA
                values = new DataPoint[24]; //Temporary (24 entries per day for nwo, will be 96)
                for(int i = 0; i < values.length; i++) {
                    values[i] = new DataPoint(i, temperature[i]);
                }
                graph.removeAllSeries(); //clear last graph
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(24); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.temperature_c)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.hour)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(12); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                values = new DataPoint[SEVEN_DAYS]; //7 days
                for (int i =0,k=faketemp.length-ONE_WEEK;i<values.length;i++,k+=ONE_DAY) //7 entries, k=end of array - (24*4)*7
                {
                    for(int x=0;x<ONE_DAY;x++) //combine 96 15 min entries for 1 day
                    {
                        num+=faketemp[k+x]; //add 96 entries into one variable
                    }
                    values[i] = new DataPoint(i,num/ONE_DAY); //average the 96 numbers
                    num=0.0; //reset num so it can be used again
                }
                series = new LineGraphSeries<>(values);
                graph.addSeries(series);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(7); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.temperature_c)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.day)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(7);
                graph.getGridLabelRenderer().reloadStyles();
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                //fake data
                values = new DataPoint[FOUR_WEEKS]; //4 weeks
                for (int i =0,k=faketemp.length-ONE_MONTH;i<values.length;i++,k+=ONE_WEEK) //4 entries, k=end of array = ((24*4)*7)*4
                {
                    for(int x=0;x<ONE_WEEK;x++) //combine 672 15 min entries int one vatiable
                    {
                        num+=faketemp[k+x]; //add 672 entries into one variable
                    }
                    values[i] = new DataPoint(i,num/ONE_WEEK); //average the 672 numbers
                    num=0.0; //reset num so it can be used again
                }
                series = new LineGraphSeries<>(values);
                graph.addSeries(series);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(4); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.temperature_c)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.week)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(4);
                graph.getGridLabelRenderer().reloadStyles();
            }
        }
        //-------------------------------------------------------------------------------------------------------
        //BAROMETRIC
        else if(spinner1.getSelectedItemPosition() == 3) {
            graph.removeAllSeries(); //clear last graph

            if(spinner2.getSelectedItemPosition() == 0) {
                //REAL DATA
                values = new DataPoint[24]; //Temporary (24 entries per day for nwo, will be 96)
                for(int i = 0; i < values.length; i++) {
                    values[i] = new DataPoint(i, barometric[i]);
                }
                graph.removeAllSeries(); //clear last graph
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(24); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.barometric)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.hour)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(12); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                values = new DataPoint[SEVEN_DAYS]; //7 days
                for (int i =0,k=fakebarometric.length-ONE_WEEK;i<values.length;i++,k+=ONE_DAY) //7 entries, k=end of array - (24*4)*7
                {
                    for(int x=0;x<ONE_DAY;x++) //combine 96 15 min entries for 1 day
                    {
                        num+=fakebarometric[k+x]; //add 96 entries into one variable
                    }
                    values[i] = new DataPoint(i,num/ONE_DAY); //average the 96 numbers
                    num=0.0; //reset num so it can be used again
                }
                series = new LineGraphSeries<>(values);
                graph.addSeries(series);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(7); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.barometric)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.day)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(7);
                graph.getGridLabelRenderer().reloadStyles();
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                //fake data
                values = new DataPoint[FOUR_WEEKS]; //4 weeks
                for (int i =0,k=fakebarometric.length-ONE_MONTH;i<values.length;i++,k+=ONE_WEEK) //4 entries, k=end of array = ((24*4)*7)*4
                {
                    for(int x=0;x<ONE_WEEK;x++) //combine 672 15 min entries int one vatiable
                    {
                        num+=fakebarometric[k+x]; //add 672 entries into one variable
                    }
                    values[i] = new DataPoint(i,num/ONE_WEEK); //average the 672 numbers
                    num=0.0; //reset num so it can be used again
                }
                series = new LineGraphSeries<>(values);
                graph.addSeries(series);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(4); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.barometric)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.week)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(4);
                graph.getGridLabelRenderer().reloadStyles();
            }
        }
        //-------------------------------------------------------------------------------------------------------
        //LIGHT LEVEL
        else if(spinner1.getSelectedItemPosition() == 4) {
            graph.removeAllSeries(); //clear last graph

            if(spinner2.getSelectedItemPosition() == 0) {
                //REAL DATA
                values = new DataPoint[24]; //Temporary (24 entries per day for nwo, will be 96)
                for(int i = 0; i < values.length; i++) {
                    values[i] = new DataPoint(i, light[i]);
                }
                graph.removeAllSeries(); //clear last graph
                series = new LineGraphSeries<>(values); //add values to series
                graph.addSeries(series); //add series to graph
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(24); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.light)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.hour)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(12); //set number of horizontal labels
                graph.getGridLabelRenderer().reloadStyles(); //reload style
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                values = new DataPoint[SEVEN_DAYS]; //7 days
                for (int i =0,k=fakelight.length-ONE_WEEK;i<values.length;i++,k+=ONE_DAY) //7 entries, k=end of array - (24*4)*7
                {
                    for(int x=0;x<ONE_DAY;x++) //combine 96 15 min entries for 1 day
                    {
                        num+=fakelight[k+x]; //add 96 entries into one variable
                    }
                    values[i] = new DataPoint(i,num/ONE_DAY); //average the 96 numbers
                    num=0.0; //reset num so it can be used again
                }
                series = new LineGraphSeries<>(values);
                graph.addSeries(series);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(7); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.light)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.day)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(7);
                graph.getGridLabelRenderer().reloadStyles();
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                //fake data
                values = new DataPoint[FOUR_WEEKS]; //4 weeks
                for (int i =0,k=fakelight.length-ONE_MONTH;i<values.length;i++,k+=ONE_WEEK) //4 entries, k=end of array = ((24*4)*7)*4
                {
                    for(int x=0;x<ONE_MONTH;x++) //combine 672 15 min entries int one vatiable
                    {
                        num+=fakelight[k+x]; //add 672 entries into one variable
                    }
                    values[i] = new DataPoint(i,num/ONE_MONTH); //average the 672 numbers
                    num=0.0; //reset num so it can be used again
                }
                series = new LineGraphSeries<>(values);
                graph.addSeries(series);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(4); //so you can see last label
                gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.light)); //set vertical axis title
                gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.week)); //set horizontal axis title
                gridLabelRenderer.setNumHorizontalLabels(4);
                graph.getGridLabelRenderer().reloadStyles();
            }
            //-------------------------------------------------------------------------------------------------------
        }
    }

    public void onNothingSelected(AdapterView<?> parent)
    {}
}
