package sametileri.project.activitys;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sametileri.project.adapters.CustomAdapter;
import sametileri.project.R;
import sametileri.project.objects.Saha;
import sametileri.project.objects.Sensor;
import sametileri.project.objects.Tarla;

public class MainActivity extends ListActivity {

    private static final String tag = "MainActivity";
    private List<Saha> sahaList = new ArrayList<>();
    private List<String> xData = new ArrayList<>();
    private List<Float> yData = new ArrayList<>();
    private int currentSensorId;
    private String currentTarlaName;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pieChart = (PieChart) findViewById(R.id.pieChart);
        TextView aselsanTv = (TextView) findViewById(R.id.aselsanTv);
        TextView tarbilTv = (TextView) findViewById(R.id.tarbilTv);

        Intent myIntent = getIntent();
        String userID = myIntent.getStringExtra("userID");

        String SERVER_URL = "http://193.140.150.25:3542/OperationServices/OperationServices.asmx/GetFieldsByUserId";

        //String jsonString = readFileFromRaw(R.raw.getfieldbyuserid);
        String jsonString = postJson("user_id", userID, SERVER_URL);
        Log.d("getFieldByUserId",jsonString);

        CustomAdapter mAdapter = new CustomAdapter(MainActivity.this);

        try {

            JSONObject mainJson = new JSONObject(jsonString);
            JSONArray userFields = mainJson.getJSONArray("User_fields");

            for (int i = 0; i < userFields.length(); i++){

                Saha saha = new Saha();

                JSONObject userField = userFields.getJSONObject(i);

                String areaName = userField.getString("Area_name");
                saha.setName(areaName);
                mAdapter.addSectionHeaderItem(areaName);
                JSONArray fields = userField.getJSONArray("Field");

                List<Tarla> tarlaList = new ArrayList<>();

                for (int j = 0; j < fields.length(); j++){
                    JSONObject field = fields.getJSONObject(j);

                    Tarla tarla = new Tarla();

                    int fieldId = field.getInt("Field_id");
                    String fieldName = field.getString("Field_name");

                    tarla.setTarlaID(fieldId);
                    tarla.setName(fieldName);
                    tarlaList.add(tarla);

                    mAdapter.addItem(fieldName);

                }
                saha.setTarlaList(tarlaList);
                sahaList.add(saha);
            }
            if (userFields.length() == 0)
                Toast.makeText(getApplicationContext(), "There is no field related to this user.",Toast.LENGTH_LONG).show();
            else
                getSensorsFromService(true,"");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setListAdapter(mAdapter);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                for (int i = 0; i < sahaList.size(); i++){
                    for (int j = 0; j < sahaList.get(i).getTarlaList().size(); j++){
                        Tarla tarla = sahaList.get(i).getTarlaList().get(j);
                        if (currentTarlaName.equals(tarla.getName())){
                            for (int k = 0; k < tarla.getSensorList().size(); k++){
                                Sensor sensor = tarla.getSensorList().get(k);
                                if (xData.get((int)h.getX()).equals(sensor.getName())){
                                    currentSensorId = sensor.getId();
                                }
                            }
                        }
                    }
                }

                Toast.makeText(getApplicationContext(), currentSensorId + " ",Toast.LENGTH_SHORT).show();
                drawGraphs(xData.get((int)h.getX()));
            }
            @Override
            public void onNothingSelected() {

            }
        });

    }

    public void linkClick(View v){
        if (v.getId() == R.id.aselsanTv){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.aselsan.com.tr/tr-tr/Sayfalar/default.aspx"));
            startActivity(browserIntent);
        }else{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tarbil.org/"));
            startActivity(browserIntent);
        }
    }

    private void drawLineGraphs() {

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.graphs_sensor, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(view);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.cartsLayout);

        LineChart aksamChart = (LineChart) ll.findViewById(R.id.aksamChart);
        ArrayList<Entry> entries = new ArrayList<>();

        Random rnd = new Random();

        for (int i = 0; i < 40; i++)
            entries.add(new Entry(i,rnd.nextInt(100)));

        LineDataSet set = new LineDataSet(entries,"# of calls");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.setDrawFilled(true);
        LineData lineData = new LineData(set);
        aksamChart.setData(lineData);
        aksamChart.setVisibleXRangeMaximum(10); // allow 20 values to be displayed at once on the x-axis, not more
        aksamChart.moveViewToX(10); // set the left edge of the chart to x-index 10

        alertDialog.show();

    }

    public void getSensorsFromService(boolean initial, String tarlaName){

        xData = new ArrayList<>();
        yData = new ArrayList<>();

        // son tıklanan tarlanın ismini al onun sensorune ulas onun nameine ulas ve bitir moruk!

        String SERVER_URL = "http://193.140.150.25:3542/OperationServices/OperationServices.asmx/GetSensorsWithFieldId";

        String tarlaID = "";

        Tarla tarla = null;

        if (initial) {
            tarla = sahaList.get(0).getTarlaList().get(0);
            tarlaID = String.valueOf(tarla.getTarlaID());
        }
        else{
            for (int i = 0; i < sahaList.size(); i++){
                for (int j = 0; j < sahaList.get(i).getTarlaList().size(); j++){
                    if (tarlaName.equals(sahaList.get(i).getTarlaList().get(j).getName())){
                        tarla = sahaList.get(i).getTarlaList().get(j);
                        tarlaID = String.valueOf(tarla.getTarlaID());
                    }
                }
            }
        }
        currentTarlaName = tarla.getName();

        String jsonString = postJson("field_id", tarlaID, SERVER_URL);
        Log.d("GetSensorWithFieldId",jsonString);

        //String jsonString = readFileFromRaw(R.raw.sensorsfieldtid1);
        //String jsonString = readFileFromRaw(R.raw.sensorsfieldtid2);

        // şimdi web service e parametre olarak field id atıp, o id e göre jsonlar geliyor ya
        // ben rawdaki jsonda bir modelini kullandığım için mekanik olarak değişmiyor!

        try {
            JSONObject mainJson = new JSONObject(jsonString);
            JSONArray fieldSensors = mainJson.getJSONArray("Field_sensors");

            List<Sensor> sensorList = new ArrayList<>();

            for (int i = 0; i < fieldSensors.length(); i++){
                JSONObject fieldSensor = fieldSensors.getJSONObject(i);

                Sensor sensor = new Sensor();

                String sensorName = fieldSensor.getString("SensorName1");
                int value = fieldSensor.getInt("SensorLastValue1");
                int id = fieldSensor.getInt("SensorId1");

                sensor.setName(sensorName);
                sensor.setValue(value);
                sensor.setId(id);

                xData.add(sensorName);
                yData.add((float) value);

                sensorList.add(sensor);
            }
            if (fieldSensors.length() == 0)
                Log.d(tag,"sensor not found !");
            else
                tarla.setSensorList(sensorList);
        setupPieChart(pieChart);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void drawGraphs(String sensor) {

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.graphs_sensor, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(view);

        Button geriDon = (Button) view.findViewById(R.id.geriDon);
        geriDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.cartsLayout);
        BarChart sabahChart = (BarChart) ll.findViewById(R.id.sabahChart);
        BarChart aksamChart = (BarChart) ll.findViewById(R.id.aksamChart);
        TextView sabahTv = (TextView) ll.findViewById(R.id.sabahTv);
        TextView aksamTv = (TextView) ll.findViewById(R.id.aksamTv);

        String sabahTvText = "";
        String aksamTvText = "";

        try {

            String serverUrl = "http://193.140.150.25:3542/OperationServices/OperationServices.asmx/GetSensorsDailyValues";
            String jsonString = postJson("sensor_id", String.valueOf(currentSensorId) ,serverUrl);

            //String jsonString = readFileFromRaw(R.raw.sensordaily);

            JSONObject mainJson = new JSONObject(jsonString);
            JSONObject dailyValues = mainJson.getJSONObject("Daily_values");
            JSONArray amGroup = dailyValues.getJSONArray("Am_group");
            JSONArray pmGroup = dailyValues.getJSONArray("Pm_group");

            //-- json stringi ver gerisine karışma!
            List<BarEntry> entriesSabah = new ArrayList<>();
            List<BarEntry> entriesAksam = new ArrayList<>();

            for ( int i = 0 ; i < amGroup.length(); i++){
                JSONObject index = amGroup.getJSONObject(i);

                if (i == 0 || i == amGroup.length()-1){
                    String[] split = index.getString("Value_date").split("T");
                    sabahTvText += split[0] + " " + split[1] + " ";
                }

                entriesSabah.add(new BarEntry(i,index.getInt("Value")));
            }
            sabahTvText += " arasında " + sensor + " için 20 dakka da bir veriler gösterilmektedir.";
            sabahTv.setText(sabahTvText);

            for ( int i = 0 ; i < pmGroup.length(); i++){
                JSONObject index = pmGroup.getJSONObject(i);

                if (i == 0 || i == pmGroup.length()-1){
                    String[] split = index.getString("Value_date").split("T");
                    aksamTvText += split[0] + " " + split[1] + " ";
                }
                entriesAksam.add(new BarEntry(i,index.getInt("Value")));
            }
            aksamTvText += " arasında " + sensor + " için 20 dakka da bir veriler gösterilmektedir.";
            aksamTv.setText(aksamTvText);

            BarDataSet setSabah = new BarDataSet(entriesSabah, sensor);
            BarDataSet setAksam = new BarDataSet(entriesAksam, sensor);

            setSabah.setColors(ColorTemplate.COLORFUL_COLORS);
            setAksam.setColors(ColorTemplate.COLORFUL_COLORS);

            setSabah.setValueTextSize(14);
            setAksam.setValueTextSize(14);

            BarData dataSabah = new BarData(setSabah);
            BarData dataAksam = new BarData(setAksam);

            dataSabah.setValueFormatter(new MyValueFormatter());
            dataAksam.setValueFormatter(new MyValueFormatter());

            dataSabah.setBarWidth(0.9f); // set custom bar width
            dataAksam.setBarWidth(0.9f); // set custom bar width

            sabahChart.setData(dataSabah);
            aksamChart.setData(dataAksam);

            sabahChart.setVisibleXRangeMaximum(10); // allow 20 values to be displayed at once on the x-axis, not more
            sabahChart.moveViewToX(10); // set the left edge of the chart to x-index 10
            sabahChart.setFitBars(true); // make the x-axis fit exactly all bars
            sabahChart.invalidate(); // refresh

            aksamChart.setVisibleXRangeMaximum(10); // allow 20 values to be displayed at once on the x-axis, not more
            aksamChart.moveViewToX(10); // set the left edge of the chart to x-index 10
            aksamChart.setFitBars(true); // make the x-axis fit exactly all bars
            aksamChart.invalidate(); // refresh

        } catch (JSONException e) {
            e.printStackTrace();
        }

        alertDialog.show();
    }

    private String readFileFromRaw(int rawid) {
        InputStream inputStream = getResources().openRawResource(rawid);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    private void setupPieChart(PieChart chart){

        List<PieEntry> pieEntries = new ArrayList<>();

        for (int i = 0; i < yData.size(); i++){
            pieEntries.add(new PieEntry(yData.get(i),xData.get(i)));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Saha değerleri");
        dataSet.setSliceSpace(2);
        dataSet.setValueTextSize(16);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        //set the chart
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new MyValueFormatter());
        chart.setUsePercentValues(false);
        chart.setData(data);
        chart.invalidate();

    }

    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); // use one decimal if needed
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if (value <= 1)
                return "";

            return mFormat.format(value) + ""; // e.g. append a dollar-sign
        }
    }

    public static String postJson(String key, String jsonValue,String serverUrl){

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add(key, jsonValue)
                .build();
        Request request = new Request.Builder()
                .url(serverUrl)
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();

            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }



}
