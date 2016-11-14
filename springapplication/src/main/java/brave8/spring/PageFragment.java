package brave8.spring;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    //data variables
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    DataPoint[] values;
    double[] power;
    double num =0.0;

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


        if (mPage == 1) {
            //home activity
            view = inflater.inflate(R.layout.activity_home, container, false);
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
            view = inflater.inflate(R.layout.activity_data, container, false);



            //fake data
            Random rn = new Random();
            power = new double[4000];
            for (int i=0;i<power.length;i++)
            {
                power[i]= 1.0 + (20.0 - 1.0) * rn.nextDouble();
            }

            Spinner spinner = (Spinner) view.findViewById(R.id.spinner1);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.planets_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            Spinner dropdown2 = (Spinner)view.findViewById(R.id.spinner2);
            String[] items2 = new String[]{"1 Day", "7 Days", "30 Days"};
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items2);
            dropdown2.setAdapter(adapter2);

            graph = (GraphView) view.findViewById(R.id.graph);

            //----------
            GridLabelRenderer gridLabelRenderer = graph.getGridLabelRenderer();
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
        }
        else {
            view = inflater.inflate(R.layout.activity_settings, container, false);
        }


        return view;
    }


}