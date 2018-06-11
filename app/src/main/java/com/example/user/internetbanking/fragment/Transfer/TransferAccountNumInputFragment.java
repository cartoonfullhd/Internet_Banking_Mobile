package com.example.user.internetbanking.fragment.Transfer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.internetbanking.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransferAccountNumInputFragment extends Fragment
{
    private EditText myAccountEdt, desAccountEdt, AmountEdt;
    private Button nextBtn;
    private Bundle bundle = new Bundle();
    String customer_id, local_ip;

    public TransferAccountNumInputFragment()
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
        return inflater.inflate(R.layout.fragment_transfer_account_num_input, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Bind your views.
        myAccountEdt = view.findViewById(R.id.myAccountEdt);
        desAccountEdt = view.findViewById(R.id.desAccountEdt);
        AmountEdt = view.findViewById(R.id.AmountEdt);
        nextBtn = view.findViewById(R.id.nextBtn);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
                    else if(myAccountEdt.getText().toString().equals(desAccountEdt.getText().toString()))
                    {
                        myAccountEdt.setError("Account number error");
                        desAccountEdt.setError("Account number error");
                    }
                    else if(myAccountEdt.getText().toString().isEmpty())
                    {
                        myAccountEdt.setError("Input account number");
                    }
                    else if(myAccountEdt.getText().toString().length() < 10)
                    {
                        myAccountEdt.setError("Input account 10 digits");
                    }
                    else{
                        new checkDesAccount().execute();
                        bundle.putString("myAccountId", myAccountEdt.getText().toString());
                        bundle.putString("desAccountId", desAccountEdt.getText().toString());
                        bundle.putString("amount", AmountEdt.getText().toString());
                        bundle.putString("local_ip", local_ip);
                    }
                }
            });
    }

    private class checkDesAccount extends AsyncTask<Void, Void, String>
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
            String urlParameters  = "?account_id=" + desAccountEdt.getText().toString();
            try
            {
                url = new URL(local_ip + getResources().getString(R.string.bank_local)+ "checkaccount" + urlParameters);
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
                bundle.putString("CUSTOMER_ID", customer_id);
                bundle.putString("local_ip", local_ip);
                TransferAccountNumberCheckFragment transferAccountNumCheckFragment = new TransferAccountNumberCheckFragment();
                transferAccountNumCheckFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.TransferAccountNumInputFrameLayout,transferAccountNumCheckFragment );
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
            else{
                Toast.makeText(getActivity(), "Destination Account invalid! \n Try again!",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }
}
