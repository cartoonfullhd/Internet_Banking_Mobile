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

import org.json.JSONObject;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PromptPayRegisterCheckFragment extends Fragment
{
    Button confirmBtn;
    TextView accountNumberValue, phoneValue, idCardValue, accountNameValue, typeValue;
    String customer_id, idType, customerName, accountId, bankCode, idValue, local_ip;

    public PromptPayRegisterCheckFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        customer_id = getArguments().getString("CUSTOMER_ID");
        customerName = getArguments().getString("customerName");
        accountId = getArguments().getString("accountId");
        bankCode = getArguments().getString("bankCode");
        idType = getArguments().getString("IDType");
        idValue = getArguments().getString("IDValue");
        local_ip = getArguments().getString("local_ip");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prompt_pay_register_check, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Bind your views.
        typeValue = view.findViewById(R.id.typeValue);
        confirmBtn = view.findViewById(R.id.confirmBtn);
        phoneValue = view.findViewById(R.id.phoneValue);
        idCardValue = view.findViewById(R.id.idCardValue);
        accountNumberValue = view.findViewById(R.id.accountNumberValue);
        accountNameValue = view.findViewById(R.id.accountNameValue);

        // Create your layout manager.

        if(idType.equals("idcard"))
        {
            idCardValue.setText(idValue);
            phoneValue.setEnabled(false);
        }
        else
        {
            phoneValue.setText(idValue);
            idCardValue.setEnabled(false);
        }
        typeValue.setText(idType);
        accountNumberValue.setText(accountId);
        accountNameValue.setText(customerName);
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
                new registerAnyId().execute();
            }
        });
    }

    private class registerAnyId extends AsyncTask<Void, Void, String>
    {
        String status;
        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected String doInBackground(Void ... params)
        {
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("IDType", idType);
                jsonParam.put("IDValue", idValue);
                jsonParam.put("BankCode", bankCode);
                jsonParam.put("AccountID", accountId);
                jsonParam.put("AccountName", customerName);

                url = new URL(local_ip + getResources().getString(R.string.bank_local) + "addanyid");
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
                status = String.valueOf(urlConnection.getResponseCode());

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
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if(result.equals("200"))
            {
                Toast.makeText(getActivity(), "Register completed",
                        Toast.LENGTH_LONG).show();
                Intent home = new Intent(getActivity(), HomeActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.putExtra("CUSTOMER_ID", customer_id);
                home.putExtra("local_ip", local_ip);
                startActivity(home);
            }
            else{
                Toast.makeText(getActivity(), "Register incomplete! \n Try again!",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }
}
