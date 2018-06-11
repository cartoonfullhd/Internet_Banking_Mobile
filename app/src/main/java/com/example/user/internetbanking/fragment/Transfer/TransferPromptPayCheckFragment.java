package com.example.user.internetbanking.fragment.Transfer;

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

import com.example.user.internetbanking.R;
import com.example.user.internetbanking.activity.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransferPromptPayCheckFragment extends Fragment
{

    TextView myAccountTxt, desAccountTxt, amountTxt, desAccountNameTxt;
    String myAccount, desAccount, amount, desAccountName, desAPID, desBankCode, myBankCode, IDType, IDValue;
    Button confirmBtn;
    String customer_id, local_ip;
    String myCustomerName;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        myCustomerName = getArguments().getString("myCustomerName");
        desAccountName = getArguments().getString("desAccountName");
        desAccount = getArguments().getString("desAccountId");
        customer_id = getArguments().getString("CUSTOMER_ID");
        desAPID = getArguments().getString("desAPID");
        desBankCode = getArguments().getString("desBankCode");
        myAccount = getArguments().getString("myAccountId");
        myBankCode = getArguments().getString("myBankCode");
        IDType = getArguments().getString("IDType");
        IDValue = getArguments().getString("IDValue");
        amount = getArguments().getString("amount");
        local_ip = getArguments().getString("local_ip");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer_prompt_pay_check, container, false);
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
        desAccountNameTxt = view.findViewById(R.id.desAccountNameValue);

        myAccountTxt.setText(myAccount);
        desAccountTxt.setText(desAccount);
        amountTxt.setText(amount);
        desAccountNameTxt.setText(desAccountName);
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
                new updateAmountMyAccount().execute();
                //new updateAmountMyAccount().execute();
            }
        });
    }

    private class updateAmountMyAccount extends AsyncTask<Void, Void, String>
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
            String urlParameters  = "?my_account=" + myAccount + "&des_account=" + desAccount + "&amount=" + amount + "&apid=" + desAPID + "&des_code=" + desBankCode;
            try
            {
                url = new URL(local_ip + getResources().getString(R.string.bank_local)+ "transferapid" + urlParameters);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("customer_id", customer_id);
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
                new transfer().execute();
            }
            else{
                Toast.makeText(getActivity(), "My account server error \n Try again!",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }

    private class transfer extends AsyncTask<Void, Void, String>
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
            JSONObject jsonParam = new JSONObject();

            try
            {
                jsonParam.put("AIPID", desAPID);
                jsonParam.put("SendBankCode", desBankCode);
                jsonParam.put("SendAccountID", myAccount);
                jsonParam.put("Amount", amount);
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try
            {
                url = new URL(local_ip + getResources().getString(R.string.bank_local) + "transferanyid");
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
                Toast.makeText(getActivity(), "Transfer completed",
                        Toast.LENGTH_LONG).show();

                Intent home = new Intent(getActivity(), HomeActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.putExtra("CUSTOMER_ID", customer_id);
                home.putExtra("local_ip", local_ip);
                startActivity(home);
            }
            else{
                Toast.makeText(getActivity(), "Transfer incomplete! \n Try again!",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }
}
