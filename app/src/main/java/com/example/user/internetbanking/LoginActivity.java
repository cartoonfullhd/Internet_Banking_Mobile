package com.example.user.internetbanking;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class LoginActivity extends AppCompatActivity
{

    EditText editUserName, editPassword, ipEdt;
    String ip_local;
    Button loginBtn, registerBtn;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ipEdt = findViewById(R.id.api_edt);
        editUserName = findViewById(R.id.logIn_edt);
        editPassword = findViewById(R.id.password_edt);
        loginBtn = findViewById(R.id.login_button);
        registerBtn = findViewById(R.id.register_button);

        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                username = editUserName.getText().toString();
                password = editPassword.getText().toString();
                if(TextUtils.isEmpty(username))
                {
                    editUserName.setError(getResources().getString(R.string.input_username_error));
                }
                if(TextUtils.isEmpty(password))
                {
                    editPassword.setError(getResources().getString(R.string.input_password_error));
                }
                else{
                    String[] userLogin = {username, password};
                    ip_local = ipEdt.getText().toString();
                    new login().execute(userLogin);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                register.putExtra("local_ip", ipEdt.getText().toString());
                startActivity(register);
            }
        });
    }

    private class login extends AsyncTask<String[], Void, String>
    {
        String customer_id;
        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected String doInBackground(String[]... params)
        {
            String JSONResult = "";
            String line;
            URL url;
            HttpURLConnection urlConnection = null;
            String[] parameter = params[0];
            String urlParameters  = "j_username=" + parameter[0] + "&j_password=" + parameter[1];
            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));

            try
            {
                url = new URL(ip_local + getResources().getString(R.string.bank_local) + "login");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                //InputStream decode urlConnection to Byte format
                InputStream Byte = urlConnection.getInputStream();
                customer_id = urlConnection.getHeaderField("customer_id");
                //InputStreamReader decode Byte format to Char format
                InputStreamReader Char = new InputStreamReader(Byte);
                BufferedReader reader = new BufferedReader(Char);
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                reader.close();
                JSONResult = builder.toString();
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
            return JSONResult;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if(result.equals("Success"))
            {
                Intent home = new Intent(LoginActivity.this, HomeActivity.class);
                home.putExtra("CUSTOMER_ID", customer_id);
                home.putExtra("local_ip", ip_local);
                startActivity(home);
            }
            else{
                Toast.makeText(LoginActivity.this, "Username or Password incorrect! \n Try again!",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }
}
