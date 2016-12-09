package brave8.spring;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

public class PageFragment extends Fragment implements OnItemSelectedListener {

    public static final String DATA_URL_FETCH_BY_LOGIN = "http://springdb.eu5.org//spring/fetch_data_by_login.php?id=";
    public static final String DATA_URL_FETCH_LAST = "http://springdb.eu5.org//spring/get_last.php?id=";
    public static final String DATA_URL_FETCH_COUNT = "http://springdb.eu5.org//spring/get_count.php?id=";

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    //data variables
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    DataPoint[] values;
    double num = 0.0;
    List<Solar> solarList = null;
    Spinner spinner1, spinner2;
    GridLabelRenderer gridLabelRenderer;
    TextView insufficient, noData;
    SharedPreferences settings;
    LinearLayout status_layout;
    LinearLayout main_layout;
    int idLogin;

    int[] idData;
    double[] power, temperature, light, barometric, humidity;
    String[] time, date;
    int rowCount;

    //constants
    private int ONE_DAY = 96;
    private int ONE_WEEK = 672;
    private int ONE_MONTH = 2688;
    private int TWENTYFOUR_HOURS = 24;
    private int SEVEN_DAYS = 7;
    private int FOUR_WEEKS = 4;
    private int X_AXIS_1 = 12;

    boolean isViewShown = false;


    //fake data variables
    double[] fakepower;
    double[] faketemp;
    double[] fakelight;
    double[] fakebarometric;
    double[] fakehumidity;

    View view;

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
        if(!isViewShown) {
            settings = getActivity().getSharedPreferences(LoginActivity.MY_PREFS_NAME, Context.MODE_PRIVATE);
            if (mPage == 1) {
                view = inflater.inflate(R.layout.activity_home, container, false);
            } else if (mPage == 2) {
                view = inflater.inflate(R.layout.activity_data, container, false);
            } else {
                view = inflater.inflate(R.layout.activity_settings, container, false);
            }

            insufficient = (TextView) view.findViewById(R.id.insufficient);
            idLogin = getActivity().getIntent().getIntExtra("loginID", 0);
            new DownloadDataTask().execute(String.valueOf(idLogin));
        }
        return view;
    }

    private class DownloadDataTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... userId) {
            return getData(userId[0]);
        }

        protected String getData(String userId) {
            String response;
            response = new HttpHandler().makeServiceCall(DATA_URL_FETCH_COUNT + userId);
            try {
                JSONObject all = new JSONObject(response);
                JSONArray result = all.getJSONArray("result");
                rowCount = result.getJSONObject(0).getInt("count");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            if(solarList == null) {

                return new HttpHandler().makeServiceCall(DATA_URL_FETCH_BY_LOGIN + userId);
            }
            if(rowCount > 0) {
                response = new HttpHandler().makeServiceCall(DATA_URL_FETCH_LAST + userId);
                try {
                    JSONObject all = new JSONObject(response);
                    JSONArray result = all.getJSONArray("result");
                    JSONObject last = result.getJSONObject(0);

                    int lastEntryDB = last.getInt("id_data");
                    int lastEntry = idData[idData.length - 1];
                    if (lastEntry != lastEntryDB) {
                        return new HttpHandler().makeServiceCall(DATA_URL_FETCH_BY_LOGIN + userId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return String.valueOf(-1);
        }

        protected void onPostExecute(String result) {
            //this is for the case where we flip the phone too fast between portrait and landscape
            //this would cause a crash because the async task is still doing stuff while we flip and it crashes
            try {
                if (result.equals(String.valueOf(-1))) {
                    displayData(result, false);
                } else {
                    displayData(result, true);
                }
            }
            catch(Exception e)
            {
                cancel(true);
                if(isCancelled()){
                    return;
                }
            }
        }
    }

    public void displayData(String response, boolean newData) {
        SolarDataSource solarDataSource = new SolarDataSource(getContext());
        if(newData) {
            try {
                solarList = solarDataSource.createSolarList(response);
                idData = new int[solarList.size()];
                power = new double[solarList.size()];
                temperature = new double[solarList.size()];
                light = new double[solarList.size()];
                barometric = new double[solarList.size()];
                humidity = new double[solarList.size()];
                time = new String[solarList.size()];
                date = new String[solarList.size()];

                for (int i = 0; i < solarList.size(); i++) {
                    idData[i] = solarList.get(i).getIdData();
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
        }

        if (mPage == 1) {
            //home activity

            status_layout = (LinearLayout) view.findViewById(R.id.status_layout);
            main_layout = (LinearLayout) view.findViewById(R.id.main_layout);

            if(rowCount==0)
            {
                noData = (TextView) view.findViewById(R.id.noData);
                noData.setText(R.string.insufficient);
            }
            else {
                status_layout.setVisibility(View.GONE);
                main_layout.setVisibility(View.VISIBLE);
                //set date
                TextView date_text = (TextView) view.findViewById(R.id.date_text);
                date_text.setText(date[date.length - 1]);

                //set time
                TextView time_text = (TextView) view.findViewById(R.id.time_text);
                time_text.setText(time[time.length - 1]);

                TextView power_data = (TextView) view.findViewById(R.id.power_data);
                power_data.setText(getResources().getString(R.string.power_data, power[power.length - 1]));

                TextView humidity_text = (TextView) view.findViewById(R.id.humidity_data);
                humidity_text.setText(getResources().getString(R.string.humidity_data, humidity[humidity.length - 1]));

                if (settings.getString("temperature", "").equals("fahrenheit")) {
                    TextView temperature_text = (TextView) view.findViewById(R.id.temperature_data);
                    temperature_text.setText(getResources().getString(R.string.temperature_data, (temperature[temperature.length - 1] * 1.8) + 32, "F"));
                } else {
                    TextView temperature_text = (TextView) view.findViewById(R.id.temperature_data);
                    temperature_text.setText(getResources().getString(R.string.temperature_data, temperature[temperature.length - 1], "C"));
                }

                TextView barometric_text = (TextView) view.findViewById(R.id.barometric_data);
                barometric_text.setText(getResources().getString(R.string.barometric_date, barometric[barometric.length - 1]));

                TextView light_text = (TextView) view.findViewById(R.id.light_data);
                light_text.setText("Sunny"); //-------------------------------------------temp hard coded string
            }
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

            //settings = getActivity().getSharedPreferences(LoginActivity.MY_PREFS_NAME, Context.MODE_PRIVATE);

            graph = (GraphView) view.findViewById(R.id.graph);



            gridLabelRenderer = graph.getGridLabelRenderer();
            if (Build.VERSION.SDK_INT >= 23) {
                gridLabelRenderer.setGridColor(ContextCompat.getColor(getContext(), android.R.color.black));
                gridLabelRenderer.setHorizontalLabelsColor(ContextCompat.getColor(getContext(), android.R.color.black));
                gridLabelRenderer.setVerticalLabelsColor(ContextCompat.getColor(getContext(), android.R.color.black));
            } else {
                gridLabelRenderer.setGridColor(getResources().getColor(android.R.color.black));
                gridLabelRenderer.setHorizontalLabelsColor(getResources().getColor(android.R.color.black));
                gridLabelRenderer.setVerticalLabelsColor(getResources().getColor(android.R.color.black));
            }
            graph.getGridLabelRenderer().reloadStyles();

            spinner1.setOnItemSelectedListener(this);
            spinner2.setOnItemSelectedListener(this);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        insufficient.setText("");
        if(spinner1.getSelectedItemPosition()==0){ //equals power
            graph.removeAllSeries(); //clear last graph
            //-------------------------------------------------------------------------------------------------------
            //POWER
            if(spinner2.getSelectedItemPosition() == 0) {
                //real data
                if(rowCount>=TWENTYFOUR_HOURS) {
                    values = new DataPoint[TWENTYFOUR_HOURS]; //Temporary (24 entries per day for now, will be 96)
                    for (int i = 0; i < values.length; i++) {
                        values[i] = new DataPoint(i, power[i]);
                    }
                    series = new LineGraphSeries<>(values); //add values to series
                    graph.addSeries(series); //add series to graph
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(TWENTYFOUR_HOURS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(power, TWENTYFOUR_HOURS)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.voltage)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.hour)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(TWENTYFOUR_HOURS / 2); //set number of horizontal labels
                    graph.getGridLabelRenderer().reloadStyles(); //reload style
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                //fake data
                if(rowCount>=ONE_WEEK) {
                    values = new DataPoint[SEVEN_DAYS]; //7 days
                    for (int i = 0, k = power.length - ONE_WEEK; i < values.length; i++, k += ONE_DAY) //7 entries, k=end of array - (24*4)*7
                    {
                        for (int x = 0; x < ONE_DAY; x++) //combine 96 15 min entries for 1 day
                        {
                            num += power[k + x]; //add 96 entries into one variable
                        }
                        values[i] = new DataPoint(i, num / ONE_DAY); //average the 96 numbers
                        num = 0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(SEVEN_DAYS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(power, ONE_WEEK)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.voltage)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.day)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(SEVEN_DAYS);
                    graph.getGridLabelRenderer().reloadStyles();
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                //fake data
                if(rowCount>=ONE_MONTH) {
                    values = new DataPoint[FOUR_WEEKS]; //4 weeks
                    for (int i = 0, k = power.length - ONE_MONTH; i < values.length; i++, k += ONE_WEEK) //4 entries, k=end of array = ((24*4)*7)*4
                    {
                        for (int x = 0; x < ONE_WEEK; x++) //combine 672 15 min entries int one vatiable
                        {
                            num += power[k + x]; //add 672 entries into one variable
                        }
                        values[i] = new DataPoint(i, num / ONE_WEEK); //average the 672 numbers
                        num = 0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(FOUR_WEEKS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(power, ONE_MONTH)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.voltage)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.week)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(FOUR_WEEKS);
                    graph.getGridLabelRenderer().reloadStyles();
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
        }
        //-------------------------------------------------------------------------------------------------------
        //HUMIDITY
        else if(spinner1.getSelectedItemPosition()==1){
            graph.removeAllSeries(); //clear last graph

            if(spinner2.getSelectedItemPosition() == 0) {
                //REAL DATA
                if(rowCount >= TWENTYFOUR_HOURS) {
                    values = new DataPoint[TWENTYFOUR_HOURS]; //Temporary (24 entries per day for nwo, will be 96)
                    for (int i = 0; i < values.length; i++) {
                        values[i] = new DataPoint(i, humidity[i]);
                    }
                    graph.removeAllSeries(); //clear last graph
                    series = new LineGraphSeries<>(values); //add values to series
                    graph.addSeries(series); //add series to graph
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(TWENTYFOUR_HOURS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(humidity, TWENTYFOUR_HOURS)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.humidity)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.hour)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(X_AXIS_1); //set number of horizontal labels
                    graph.getGridLabelRenderer().reloadStyles(); //reload style
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                values = new DataPoint[SEVEN_DAYS]; //7 days
                if(rowCount >= ONE_WEEK) {
                    for (int i = 0, k = humidity.length - ONE_WEEK; i < values.length; i++, k += ONE_DAY) //7 entries, k=end of array - (24*4)*7
                    {
                        for (int x = 0; x < ONE_DAY; x++) //combine 96 15 min entries for 1 day
                        {
                            num += humidity[k + x]; //add 96 entries into one variable
                        }
                        values[i] = new DataPoint(i, num / ONE_DAY); //average the 96 numbers
                        num = 0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(SEVEN_DAYS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(humidity, ONE_WEEK)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.humidity)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.day)); //set horizontal axis title;
                    gridLabelRenderer.setNumHorizontalLabels(SEVEN_DAYS);
                    graph.getGridLabelRenderer().reloadStyles();
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                if(rowCount >= ONE_MONTH) {
                    values = new DataPoint[FOUR_WEEKS]; //4 weeks
                    for (int i = 0, k = humidity.length - ONE_MONTH; i < values.length; i++, k += ONE_WEEK) //4 entries, k=end of array = ((24*4)*7)*4
                    {
                        for (int x = 0; x < ONE_WEEK; x++) //combine 672 15 min entries int one vatiable
                        {
                            num += humidity[k + x]; //add 672 entries into one variable
                        }
                        values[i] = new DataPoint(i, num / ONE_WEEK); //average the 672 numbers
                        num = 0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(FOUR_WEEKS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(humidity, ONE_MONTH)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.humidity)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.week)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(FOUR_WEEKS);
                    graph.getGridLabelRenderer().reloadStyles();
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
        }
        //-------------------------------------------------------------------------------------------------------
        //TEMPERATURE
        else if(spinner1.getSelectedItemPosition()==2){
            graph.removeAllSeries(); //clear last graph
            if(spinner2.getSelectedItemPosition() == 0) {
                if(rowCount >= TWENTYFOUR_HOURS) {
                    //REAL DATA
                    values = new DataPoint[TWENTYFOUR_HOURS]; //Temporary (24 entries per day for nwo, will be 96)
                    for (int i = 0; i < values.length; i++) {
                        values[i] = new DataPoint(i, temperature[i]);
                    }
                    graph.removeAllSeries(); //clear last graph
                    series = new LineGraphSeries<>(values); //add values to series
                    graph.addSeries(series); //add series to graph
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(TWENTYFOUR_HOURS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(temperature, TWENTYFOUR_HOURS)));
                    if(settings.getString("temperature", "").equals("fahrenheit")) {
                        gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.temperature_f)); //set vertical axis title
                    }
                    else {
                        gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.temperature_c)); //set vertical axis title
                    }
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.hour)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(X_AXIS_1); //set number of horizontal labels
                    graph.getGridLabelRenderer().reloadStyles(); //reload style
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                if(rowCount >= ONE_WEEK) {
                    values = new DataPoint[SEVEN_DAYS]; //7 days
                    for (int i = 0, k = temperature.length - ONE_WEEK; i < values.length; i++, k += ONE_DAY) //7 entries, k=end of array - (24*4)*7
                    {
                        for (int x = 0; x < ONE_DAY; x++) //combine 96 15 min entries for 1 day
                        {
                            num += temperature[k + x]; //add 96 entries into one variable
                        }
                        values[i] = new DataPoint(i, num / ONE_DAY); //average the 96 numbers
                        num = 0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(SEVEN_DAYS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(temperature, ONE_WEEK)));
                    if(settings.getString("temperature", "").equals("fahrenheit")) {
                        gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.temperature_f)); //set vertical axis title
                    }
                    else {
                        gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.temperature_c)); //set vertical axis title
                    }
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.day)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(SEVEN_DAYS);
                    graph.getGridLabelRenderer().reloadStyles();
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                //fake data
                if(rowCount >= ONE_MONTH) {
                    values = new DataPoint[FOUR_WEEKS]; //4 weeks
                    for (int i = 0, k = temperature.length - ONE_MONTH; i < values.length; i++, k += ONE_WEEK) //4 entries, k=end of array = ((24*4)*7)*4
                    {
                        for (int x = 0; x < ONE_WEEK; x++) //combine 672 15 min entries int one vatiable
                        {
                            num += temperature[k + x]; //add 672 entries into one variable
                        }
                        values[i] = new DataPoint(i, num / ONE_WEEK); //average the 672 numbers
                        num = 0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(FOUR_WEEKS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(temperature, ONE_MONTH)));
                    if(settings.getString("temperature", "").equals("fahrenheit")) {
                        gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.temperature_f)); //set vertical axis title
                    }
                    else {
                        gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.temperature_c)); //set vertical axis title
                    }
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.week)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(FOUR_WEEKS);
                    graph.getGridLabelRenderer().reloadStyles();
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
        }
        //-------------------------------------------------------------------------------------------------------
        //BAROMETRIC
        else if(spinner1.getSelectedItemPosition() == 3) {
            graph.removeAllSeries(); //clear last graph

            if(spinner2.getSelectedItemPosition() == 0) {
                if(rowCount >= TWENTYFOUR_HOURS) {
                    //REAL DATA
                    values = new DataPoint[TWENTYFOUR_HOURS]; //Temporary (24 entries per day for nwo, will be 96)
                    for (int i = 0; i < values.length; i++) {
                        values[i] = new DataPoint(i, barometric[i]);
                    }
                    graph.removeAllSeries(); //clear last graph
                    series = new LineGraphSeries<>(values); //add values to series
                    graph.addSeries(series); //add series to graph
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(TWENTYFOUR_HOURS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(barometric, TWENTYFOUR_HOURS)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.barometric)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.hour)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(X_AXIS_1); //set number of horizontal labels
                    graph.getGridLabelRenderer().reloadStyles(); //reload style
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                if(rowCount >= ONE_WEEK) {
                    values = new DataPoint[SEVEN_DAYS]; //7 days
                    for (int i = 0, k = barometric.length - ONE_WEEK; i < values.length; i++, k += ONE_DAY) //7 entries, k=end of array - (24*4)*7
                    {
                        for (int x = 0; x < ONE_DAY; x++) //combine 96 15 min entries for 1 day
                        {
                            num += barometric[k + x]; //add 96 entries into one variable
                        }
                        values[i] = new DataPoint(i, num / ONE_DAY); //average the 96 numbers
                        num = 0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(SEVEN_DAYS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(barometric, ONE_WEEK)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.barometric)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.day)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(SEVEN_DAYS);
                    graph.getGridLabelRenderer().reloadStyles();
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                if(rowCount >= ONE_MONTH) {
                    //fake data
                    values = new DataPoint[FOUR_WEEKS]; //4 weeks
                    for (int i = 0, k = barometric.length - ONE_MONTH; i < values.length; i++, k += ONE_WEEK) //4 entries, k=end of array = ((24*4)*7)*4
                    {
                        for (int x = 0; x < ONE_WEEK; x++) //combine 672 15 min entries int one vatiable
                        {
                            num += barometric[k + x]; //add 672 entries into one variable
                        }
                        values[i] = new DataPoint(i, num / ONE_WEEK); //average the 672 numbers
                        num = 0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(FOUR_WEEKS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(barometric, ONE_MONTH)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.barometric)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.week)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(FOUR_WEEKS);
                    graph.getGridLabelRenderer().reloadStyles();
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
        }
        //-------------------------------------------------------------------------------------------------------
        //LIGHT LEVEL
        else if(spinner1.getSelectedItemPosition() == 4) {
            graph.removeAllSeries(); //clear last graph

            if(spinner2.getSelectedItemPosition() == 0) {
                if(rowCount >= TWENTYFOUR_HOURS) {
                    //REAL DATA
                    values = new DataPoint[TWENTYFOUR_HOURS]; //Temporary (24 entries per day for nwo, will be 96)
                    for (int i = 0; i < values.length; i++) {
                        values[i] = new DataPoint(i, light[i]);
                    }
                    graph.removeAllSeries(); //clear last graph
                    series = new LineGraphSeries<>(values); //add values to series
                    graph.addSeries(series); //add series to graph
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(TWENTYFOUR_HOURS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(light, TWENTYFOUR_HOURS)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.light)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.hour)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(X_AXIS_1); //set number of horizontal labels
                    graph.getGridLabelRenderer().reloadStyles(); //reload style
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            else if(spinner2.getSelectedItemPosition()  == 1) {
                if(rowCount >= ONE_WEEK) {
                    values = new DataPoint[SEVEN_DAYS]; //7 days
                    for (int i = 0, k = light.length - ONE_WEEK; i < values.length; i++, k += ONE_DAY) //7 entries, k=end of array - (24*4)*7
                    {
                        for (int x = 0; x < ONE_DAY; x++) //combine 96 15 min entries for 1 day
                        {
                            num += light[k + x]; //add 96 entries into one variable
                        }
                        values[i] = new DataPoint(i, num / ONE_DAY); //average the 96 numbers
                        num = 0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(SEVEN_DAYS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(light, ONE_WEEK)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.light)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.day)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(SEVEN_DAYS);
                    graph.getGridLabelRenderer().reloadStyles();
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            else if(spinner2.getSelectedItemPosition()  == 2) {
                if(rowCount >= ONE_MONTH) {
                    //fake data
                    values = new DataPoint[FOUR_WEEKS]; //4 weeks
                    for (int i = 0, k = light.length - ONE_MONTH; i < values.length; i++, k += ONE_WEEK) //4 entries, k=end of array = ((24*4)*7)*4
                    {
                        for (int x = 0; x < ONE_MONTH; x++) //combine 672 15 min entries int one vatiable
                        {
                            num += light[k + x]; //add 672 entries into one variable
                        }
                        values[i] = new DataPoint(i, num / ONE_MONTH); //average the 672 numbers
                        num = 0.0; //reset num so it can be used again
                    }
                    series = new LineGraphSeries<>(values);
                    graph.addSeries(series);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(FOUR_WEEKS); //so you can see last label
                    graph.getViewport().setMaxY(Math.ceil(getMax(light, ONE_MONTH)));
                    gridLabelRenderer.setVerticalAxisTitle(getResources().getString(R.string.light)); //set vertical axis title
                    gridLabelRenderer.setHorizontalAxisTitle(getResources().getString(R.string.week)); //set horizontal axis title
                    gridLabelRenderer.setNumHorizontalLabels(FOUR_WEEKS);
                    graph.getGridLabelRenderer().reloadStyles();
                }
                else{
                    insufficient.setText(R.string.insufficient);
                    insufficient.setVisibility(View.VISIBLE);
                }
            }
            //-------------------------------------------------------------------------------------------------------
        }

    }

    private double getMax(double[] array,int values) {
        double max = 0.0;
        for (int x=(array.length-values);x<array.length;x++)
        {
            if (array[x]>max)
            {
                max = array[x];
            }
        }
        return max;
    }

    public void onNothingSelected(AdapterView<?> parent)
    {}

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //Will check for new data whenever a tab is clicked
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            new DownloadDataTask().execute(String.valueOf(idLogin));
        } else {
            isViewShown = false;
        }
    }
}
