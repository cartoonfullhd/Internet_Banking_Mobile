package com.example.user.internetbanking;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransferPromptPayInputFragment extends Fragment
{
    String customer_id, myCustomerName, local_ip;
    private EditText myAccountEdt, AmountEdt, phoneEdt, idCardEdt;
    private Button nextBtn;
    private Bundle bundle = new Bundle();
    Spinner promptPayType;
    String value;
    PromptPayAccount promptPayAccount;

    public TransferPromptPayInputFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        customer_id = getArguments().getString("CUSTOMER_ID");
        local_ip = getArguments().getString("local_ip");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer_prompt_pay_input, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Bind your views.
        myAccountEdt = view.findViewById(R.id.myAccountEdt);
        idCardEdt = view.findViewById(R.id.idCardEdt);
        phoneEdt = view.findViewById(R.id.phoneEdt);
        promptPayType = view.findViewById(R.id.typeSpinner);
        AmountEdt = view.findViewById(R.id.AmountEdt);
        nextBtn = view.findViewById(R.id.nextBtn);

        // Create your layout manager.

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.PromptPaySpinnerType, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        promptPayType.setAdapter(adapter);

        promptPayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position == 0)
                {
                    phoneEdt.setEnabled(false);
                    idCardEdt.setEnabled(true);
                }
                else
                {
                    phoneEdt.setEnabled(true);
                    idCardEdt.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        nextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(AmountEdt.getText().toString().equals("0") || AmountEdt.getText().toString().matches(""))
                {
                    AmountEdt.setError("Input amount");
                }
                else
                {
                    new checkAccount().execute();
                }
            }
        });
    }

    private class checkAccount extends AsyncTask<Void, Void, String>
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
            String urlParameters  = "?account_id=" + myAccountEdt.getText().toString();
            try
            {
                url = new URL( local_ip + getResources().getString(R.string.bank_local)+ "checkaccount" + urlParameters);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                //InputStream decode urlConnection to Byte format
                InputStream Byte = urlConnection.getInputStream();
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
                new checkAnyId().execute();
            }
            else{
                Toast.makeText(getActivity(), "Account number invalid! \n Try again!",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }

    private class checkAnyId extends AsyncTask<Void, Void, String>
    {
        int status;
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
            if(promptPayType.getSelectedItem().toString().equals("idcard"))
            {
                value = idCardEdt.getText().toString();
            }
            else
            {
                value = phoneEdt.getText().toString();
            }
            String urlParameters  = "checkanyid?type=" + promptPayType.getSelectedItem().toString() + "&value=" + value;
            try
            {
                url = new URL(local_ip + getResources().getString(R.string.bank_local)+ urlParameters);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                //InputStream decode urlConnection to Byte format
                InputStream Byte = urlConnection.getInputStream();
                //InputStreamReader decode Byte format to Char format
                InputStreamReader Char = new InputStreamReader(Byte);
                BufferedReader reader = new BufferedReader(Char);
                StringBuilder builder = new StringBuilder();
                status = urlConnection.getResponseCode();

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
            if(status == 200)
            {
                try
                {
                    JSONObject APIDObj = new JSONObject(result);
                    promptPayAccount = new PromptPayAccount(APIDObj.getString("AIPID"), APIDObj.getString("IDValue"), APIDObj.getString("IDType"), APIDObj.getString("BankCode"), APIDObj.getString("Status"), APIDObj.getString("AccountID"), APIDObj.getString("AccountName"), APIDObj.getString("RegisterDTM"));
                    new getCustomerDetail().execute();
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getActivity(), "Any ID not found \n Try again!",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }

    private class getCustomerDetail extends AsyncTask<Void, Void, String>
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
            String urlParameter = "customerdetail?customer_id=";
            try
            {
                url = new URL(local_ip + getResources().getString(R.string.bank_local)+ urlParameter + customer_id);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                //InputStream decode urlConnection to Byte format
                InputStream Byte = urlConnection.getInputStream();
                //InputStreamReader decode Byte format to Char format
                InputStreamReader Char = new InputStreamReader(Byte);
                BufferedReader reader = new BufferedReader(Char);
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
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
            try
            {
                JSONObject customerObj = new JSONObject(result);
                myCustomerName = customerObj.getString("name");
                new getAccountDetail().execute();
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }

    private class getAccountDetail extends AsyncTask<Void, Void, String>
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
            String urlParameter = "account?accountId=";
            try
            {
                url = new URL(local_ip + getResources().getString(R.string.bank_local)+ urlParameter + myAccountEdt.getText().toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                //InputStream decode urlConnection to Byte format
                InputStream Byte = urlConnection.getInputStream();
                //InputStreamReader decode Byte format to Char format
                InputStreamReader Char = new InputStreamReader(Byte);
                BufferedReader reader = new BufferedReader(Char);
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
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
            try
            {
                JSONObject accountObj = new JSONObject(result);
                Account account = new Account(accountObj.getString("accountId"), accountObj.getString("customerId"), accountObj.getString("bankCode"), accountObj.getString("accountType"), Double.parseDouble(accountObj.getString("balanceAmount")));

                bundle.putString("myCustomerName", myCustomerName);
                bundle.putString("desAccountName", promptPayAccount.getAccountName());
                bundle.putString("desAccountId", promptPayAccount.getAccountID());
                bundle.putString("desAPID", promptPayAccount.getAIPID());
                bundle.putString("desBankCode", promptPayAccount.getBankCode());
                bundle.putString("myAccountId", account.getAccountId());
                bundle.putString("myBankCode", account.getBankCode());
                bundle.putString("IDType", promptPayAccount.getIDType());
                bundle.putString("IDValue", promptPayAccount.getIDValue());
                bundle.putString("amount", AmountEdt.getText().toString());
                bundle.putString("CUSTOMER_ID", customer_id);
                bundle.putString("local_ip", local_ip);

                TransferPromptPayCheckFragment transferPromptPayCheckFragment = new TransferPromptPayCheckFragment();
                transferPromptPayCheckFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.TransferPromptPayInputFragment,transferPromptPayCheckFragment );
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }
}
