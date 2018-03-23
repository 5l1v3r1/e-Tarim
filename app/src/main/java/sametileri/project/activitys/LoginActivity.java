package sametileri.project.activitys;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sametileri.project.R;

public class LoginActivity extends AppCompatActivity {

    EditText etLoginName;
    EditText etPassword;

    String serverUrl = "http://193.140.150.25:3542/AccountServices/AccountServices.asmx/Login";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        /*if (getSupportActionBar() != null)
            getSupportActionBar().hide();*/

        etLoginName = (EditText) findViewById(R.id.etLoginName);
        etLoginName.setText("sultan");
        etPassword = (EditText) findViewById(R.id.etLoginPw);
        etPassword.setText("abcd");
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.setSelection(etPassword.getText().length());

    }

    public void loginClick(View btn) {

        if (btn.getId() == R.id.btnLogin) {
            if (!etLoginName.getText().toString().equals("") && !etPassword.getText().toString().equals("")) {

                JSONObject requestObj = new JSONObject();
                JSONObject user = new JSONObject();
                try {
                    user.put("user_name", etLoginName.getText().toString());
                    user.put("password", etPassword.getText().toString());
                    requestObj.put("request", user);

                    //String response = "1";
                    String response = MainActivity.postJson("request",requestObj.getString("request"), serverUrl);
                    if ("-1".equals(response))
                        Toast.makeText(this, "No record is found", Toast.LENGTH_LONG).show();
                    else {
                        Intent myIntent = new Intent(this, MainActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        myIntent.putExtra("userID", response);
                        startActivity(myIntent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (btn.getId() == R.id.btnSignUp)
            activityToAnother(RegisterActivity.class);
    }

    public void activityToAnother(Class<?> cls) {
        Intent passMapsActv = new Intent(this, cls);
        //passMapsActv.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(passMapsActv);
        finish();
    }

}
