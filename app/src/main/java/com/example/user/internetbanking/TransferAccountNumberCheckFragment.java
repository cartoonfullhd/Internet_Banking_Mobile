package com.example.user.internetbanking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class TransferAccountNumberCheckFragment extends Fragment
{
    TextView myAccountTxt, desAccountTxt, amountTxt;
    String myAccountReceive, desAccountReceive, amountReceive;
    Button confirmBtn;
    String customer_id, local_ip;

    public TransferAccountNumberCheckFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        myAccountReceive = getArguments().getString("myAccountId");
        desAccountReceive = getArguments().getString("desAccountId");
        amountReceive = getArguments().getString("amount");
        customer_id = getArguments().getString("CUSTOMER_ID");
        local_ip = getArguments().getString("local_ip");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer_account_number_check, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Bind your views.
        myAccountTxt = view.findViewById(R.id.myAccountValue);
        desAccountTxt = view.findViewById(R.id.desAccountValue);
        amountTxt = view.findViewById(R.id.amountValue);
        confirmBtn = view.findViewById(R.id.confirmBtn);

        myAccountTxt.setText(myAccountReceive);
        desAccountTxt.setText(desAccountReceive);
        amountTxt.setText(amountReceive);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        confirmBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new transfer().execute();
            }
        });
    }

    private class transfer extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected String doInBackground(Void ... params)
        {
            URL url;
            HttpURLConnection urlConnection = null;
            String line;
            String JSONResult = "";
            String urlParameters  = "my_account=" + myAccountReceive + "&des_account=" + desAccountReceive + "&amount=" + amountReceive;
            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
            try
            {
                url = new URL(local_ip + getResources().getString(R.string.bank_local)+ "transfer");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("customer_id", customer_id);
                urlConnection.getOutputStream().write(postData);
                //InputStream decode urlConnection to Byte format
                InputStream Byte = urlConnection.getInputStream();
                //InputStreamReader decode Byte format to Char format
                InputStreamReader Char = new InputStreamReader(Byte);
                BufferedReader reader = new BufferedReader(Char);
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null)
                {
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
            if(result.equals("Completed transfer"))
            {
                Toast.makeText(getActivity(), "Completed transfer",
                        Toast.LENGTH_LONG).show();

                //viewPager.setCurrentItem(0);
                Intent home = new Intent(getActivity(), HomeActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.putExtra("CUSTOMER_ID", customer_id);
                home.putExtra("local_ip", local_ip);
                startActivity(home);

            }
            else{
                Toast.makeText(getActivity(), "Incompleted transfer \n Try again!",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }
}
