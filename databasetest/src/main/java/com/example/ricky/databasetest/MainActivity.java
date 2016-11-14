package com.example.ricky.databasetest;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextId;
    private Button buttonGet;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextId = (EditText) findViewById(R.id.editTextId);
        buttonGet = (Button) findViewById(R.id.buttonGet);
        textViewResult = (TextView) findViewById(R.id.textViewResult);

        buttonGet.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        SolarDataSource solarDataSource = new SolarDataSource(this, editTextId, buttonGet, textViewResult);
        solarDataSource.getData();

        //List<Solar> solarList = new ArrayList<Solar>();
       // solarList = solarDataSource.getList();
        //textViewResult.setText("Power:\t"+solarList.get(1).getPower()+"\nBar:\t" +solarList.get(1).getBarometric()+ "\nDate:\t"+ solarList.get(1).getDate());
    }
}