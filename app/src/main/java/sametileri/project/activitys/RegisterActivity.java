package sametileri.project.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sametileri.project.R;

public class RegisterActivity extends AppCompatActivity {

    public static List<Integer> fieldIds;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    EditText name;
    EditText surname;
    EditText username;
    EditText password;
    RadioButton radioFarmer;
    RadioButton radioZiraat;
    int statu;
    static boolean invokeShared = false;


    protected void onCreate(Bundle savedInstanceState) {

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        name = (EditText) findViewById(R.id.etName);
        surname = (EditText) findViewById(R.id.etSurname);
        username = (EditText) findViewById(R.id.etUserName);
        password = (EditText) findViewById(R.id.etPassword);
        radioFarmer = (RadioButton) findViewById(R.id.radioFarmer);
        radioZiraat = (RadioButton) findViewById(R.id.radioZiraat);

        if (invokeShared){
            retrieveDataFromShared();
            invokeShared = false;
        }
        else
            fieldIds = new ArrayList<>();

    }

    private void retrieveDataFromShared() {

        name.setText(sharedPreferences.getString("Name",""));
        surname.setText(sharedPreferences.getString("Surname",""));
        username.setText(sharedPreferences.getString("User_name",""));
        password.setText(sharedPreferences.getString("Password",""));
        statu = sharedPreferences.getInt("Status",-1);

        if (statu == 0)
            radioFarmer.setChecked(true);
        else
            radioZiraat.setChecked(true);

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radioFarmer:
                if (checked)
                    break;
            case R.id.radioZiraat:
                if (checked)
                    break;
        }
    }
    public void fieldChoice(View v) {

        if (v.getId() == R.id.btnFieldChoice) {

            putDataToSharedPref();
            invokeShared = true;

            Intent passMapsActv = new Intent(this, MapsActivity.class);
            startActivity(passMapsActv);
            finish();

        }
        else if (v.getId() == R.id.btnRegister){ // login ekranına git.

            if (name.getText() != null && surname.getText() != null &&
                username.getText() != null && password.getText() != null && fieldIds.size() != 0 &&
                    (radioFarmer.isChecked()|| radioZiraat.isChecked())){


                String serverUrl = "http://193.140.150.25:3542/AccountServices/AccountServices.asmx/Register";

                JSONObject user = new JSONObject();

                try {
                    user.put("Name",name.getText().toString());
                    user.put("Surname",surname.getText().toString());
                    user.put("User_name",username.getText().toString());
                    user.put("Password",password.getText().toString());
                    user.put("Status",statu);

                    JSONArray Fields = new JSONArray();
                    for (int i = 0; i < fieldIds.size(); i++){
                        JSONObject index = new JSONObject();
                        index.put("Field_id",fieldIds.get(i));
                        Fields.put(index);
                    }
                    user.put("Fields",Fields);

                    Log.d("user",user.toString());

                    String response = MainActivity.postJson("request",user.toString(), serverUrl);

                    if ("-1".equals(response))
                        Toast.makeText(this, "Servise bağlanılamadı!", Toast.LENGTH_LONG).show();
                    else {
                        Intent passMapsActv = new Intent(this, LoginActivity.class);
                        passMapsActv.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(passMapsActv);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else
                Toast.makeText(getApplicationContext(), "Gerekli yerleri doldurunuz!", Toast.LENGTH_SHORT).show();
        }

    }

    private void putDataToSharedPref() {

        if (radioFarmer.isChecked()) statu = 0;
        else statu = 1;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name",name.getText().toString());
        editor.putString("Surname",surname.getText().toString());
        editor.putString("User_name",username.getText().toString());
        editor.putString("Password",password.getText().toString());
        editor.putInt("Status",statu);
        editor.commit();

    }

}







