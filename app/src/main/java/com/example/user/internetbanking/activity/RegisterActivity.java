package com.example.user.internetbanking.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.internetbanking.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity
{
    EditText usernameEdt, passwordEdt, nameEdt, addressEdt, phoneEdt, emailEdt;
    Button registerBtn;
    String local_ip;
    private static final String TAG = "Test";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_register);
        usernameEdt = findViewById(R.id.usernameEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        nameEdt = findViewById(R.id.nameEdt);
        addressEdt = findViewById(R.id.addressEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        emailEdt = findViewById(R.id.emailEdt);
        registerBtn = findViewById(R.id.registerBtn);
        local_ip = getIntent().getStringExtra("local_ip");

        registerBtn.setOnClickListener(new View.OnClickListener()
        {
            public  void onClick(View v)
            {
                if(usernameEdt.getText().toString().isEmpty() || passwordEdt.getText().toString().isEmpty() ||
                        nameEdt.getText().toString().isEmpty() || addressEdt.getText().toString().isEmpty() ||
                        phoneEdt.getText().toString().isEmpty() || emailEdt.getText().toString().isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please input again", Toast.LENGTH_LONG).show();
                }
                else
                {
                    new checkUserName().execute();
                }
            }
        });
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(TAG, "onResume?");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private class checkUserName extends AsyncTask<Void, Void, Integer>
    {
        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            Integer status = 0;
            URL url;
            HttpURLConnection urlConnection = null;
            String urlParameter = "checkusername?username=" + usernameEdt.getText().toString();

            try
            {
                url = new URL(local_ip + getResources().getString(R.string.bank_local) + urlParameter);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                status = urlConnection.getResponseCode();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(urlConnection != null)
                {
                    urlConnection.disconnect();
                }
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            super.onPostExecute(result);
            if(result == 200)
            {
                Toast.makeText(RegisterActivity.this, "Username duplicated", Toast.LENGTH_LONG).show();
            }
            else{
                new registerCustomer().execute();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }

    private class registerCustomer extends AsyncTask<Void, Void, Integer>
    {
        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            Integer status = 0;
            URL url;
            HttpURLConnection urlConnection = null;
            JSONObject jsonParam = new JSONObject();

            try
            {
                jsonParam.put("Login", usernameEdt.getText().toString());
                jsonParam.put("Password", passwordEdt.getText().toString());
                jsonParam.put("Name", nameEdt.getText().toString());
                jsonParam.put("Address", addressEdt.getText().toString());
                jsonParam.put("PhoneNum", phoneEdt.getText().toString());
                jsonParam.put("Email", emailEdt.getText().toString());
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try
            {
                url = new URL(local_ip + getResources().getString(R.string.bank_local) + "addaccount");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream ());
                printout.writeBytes(jsonParam.toString());
                printout.flush();
                printout.close();
                status = urlConnection.getResponseCode();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(urlConnection != null)
                {
                    urlConnection.disconnect();
                }
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            super.onPostExecute(result);
            if(result == 200)
            {
                Toast.makeText(RegisterActivity.this, "Register completed",
                        Toast.LENGTH_LONG).show();

                Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
            }
            else{
                Toast.makeText(RegisterActivity.this, "Register incomplete! \n Try again!",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }
}
