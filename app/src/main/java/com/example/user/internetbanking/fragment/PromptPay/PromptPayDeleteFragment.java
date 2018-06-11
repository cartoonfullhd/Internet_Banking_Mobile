package com.example.user.internetbanking.fragment.PromptPay;

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

import com.example.user.internetbanking.R;
import com.example.user.internetbanking.model.PromptPayAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PromptPayDeleteFragment extends Fragment
{
    Spinner promptPayType;
    Button nextBtn;
    EditText phoneEdt, idCardEdt;
    private Bundle bundle = new Bundle();
    String value;
    String customer_id, local_ip;

    public PromptPayDeleteFragment()
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
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prompt_pay_delete, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Bind your views.
        promptPayType = view.findViewById(R.id.typeSpinner);
        nextBtn = view.findViewById(R.id.nextBtn);
        idCardEdt = view.findViewById(R.id.idCardEdt);
        phoneEdt = view.findViewById(R.id.phoneEdt);

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
                new checkAnyId().execute();
            }
        });
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
            try
            {
            super.onPostExecute(result);
                if(status == 200)
                {
                    JSONObject accountObj = new JSONObject(result);
                    PromptPayAccount promptPayAccount = new PromptPayAccount(accountObj.getString("AIPID"), accountObj.getString("IDValue"), accountObj.getString("IDType"), accountObj.getString("BankCode"), accountObj.getString("Status"), accountObj.getString("AccountID"), accountObj.getString("AccountName"), accountObj.getString("RegisterDTM"));

                    bundle.putString("AIPID", promptPayAccount.getAIPID());
                    bundle.putString("IDValue", promptPayAccount.getIDValue());
                    bundle.putString("IDType", promptPayAccount.getIDType());
                    bundle.putString("BankCode", promptPayAccount.getBankCode());
                    bundle.putString("Status", promptPayAccount.getStatus());
                    bundle.putString("CUSTOMER_ID", customer_id);
                    bundle.putString("AccountID", promptPayAccount.getAccountID());
                    bundle.putString("AccountName", promptPayAccount.getAccountName());
                    bundle.putString("RegisterDTM", promptPayAccount.getRegisterDTM());
                    bundle.putString("local_ip", local_ip);

                    PromptPayDeleteCheckFragment promptPayDeleteCheckFragment = new PromptPayDeleteCheckFragment();
                    promptPayDeleteCheckFragment.setArguments(bundle);
                    FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                    fragTransaction.replace(R.id.PromptPayDeleteFragment,promptPayDeleteCheckFragment );
                    fragTransaction.addToBackStack(null);
                    fragTransaction.commit();
                }
                else{
                    Toast.makeText(getActivity(), "Data not found! \n Try again!",
                            Toast.LENGTH_LONG).show();
                }
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
