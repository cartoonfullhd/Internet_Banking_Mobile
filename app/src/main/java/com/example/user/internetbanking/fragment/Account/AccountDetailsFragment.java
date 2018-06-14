package com.example.user.internetbanking.fragment.Account;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.internetbanking.R;
import com.example.user.internetbanking.adapter.TxnAdapter;
import com.example.user.internetbanking.model.Txn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AccountDetailsFragment extends Fragment
{
    private String accountId, accountType, accountAmount, bankCode;
    private TextView accountIdTxt, accountTypeTxt, accountAmountTxt, accountTypeHeader;
    private RecyclerView recyclerView;
    private List<Txn> txnList = new ArrayList<>();
    private TxnAdapter txnAdapter;
    private String local_ip;
    private Button downloadBtn;
    private ProgressDialog mProgressDialog;
    private Context context;
    private NotificationManager manager;
    private NotificationCompat.Builder mBuilder;
    private String urlDownloadParameter = "txnreport";


    public AccountDetailsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        accountId = getArguments().getString("AccountId");
        accountType = getArguments().getString("AccountType");
        accountAmount = getArguments().getString("AccountAmount");
        bankCode = getArguments().getString("AccountBankCode");
        local_ip = getArguments().getString("local_ip");
        new getTxn().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Bind your views.
        accountIdTxt = view.findViewById(R.id.accountNum);
        accountTypeTxt = view.findViewById(R.id.accountType);
        accountAmountTxt = view.findViewById(R.id.accountAmount);
        accountTypeHeader = view.findViewById(R.id.accountTypeHeader);
        recyclerView = view.findViewById(R.id.recycler_view);
        downloadBtn = view.findViewById(R.id.download_button);

        // Create your layout manager.
        accountIdTxt.setText(accountId);
        accountTypeTxt.setText(accountType);
        accountAmountTxt.setText(accountAmount);
        accountTypeHeader.setText(accountType + " Account");

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        downloadBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL();
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                }
                else {
                    // Permission is granted
                    mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            downloadFileFromURL.cancel(true);
                        }
                    });

                    downloadFileFromURL.execute(local_ip + getResources().getString(R.string.bank_local) + urlDownloadParameter + "?account_id=" + accountId);
                    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            downloadFileFromURL.cancel(true);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    private void showProgressNotification(Context context, int notificationId, String title, String message)
    {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("")
                .setProgress(0, 0, true);

        manager.notify(notificationId, mBuilder.build());
    }

    private void hideProgressNotification(final NotificationManager manager, final Context context, final int id)
    {
        manager.cancel(id);
    }

    class DownloadFileFromURL extends AsyncTask<String, Integer, String>
    {
        private PowerManager.WakeLock mWakeLock;

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
            showProgressNotification(getActivity(), 0, "Downloading", "Downloading...");
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url)
        {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try
            {
                URL url = new URL(f_url[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();

                output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile() + "/TxnReport_" + accountId + ".pdf");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1)
                {
                    // allow canceling with back button
                    if (isCancelled())
                    {
                        input.close();
                        hideProgressNotification(manager, getActivity(), 0);
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                    {
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    output.write(data, 0, count);
                }
            }
            catch (Exception e)
            {
                return e.toString();
            }
            finally
            {
                try
                {
                    if (output != null)
                    {
                        output.close();
                    }
                    if (input != null)
                    {
                        input.close();
                    }
                }
                catch (IOException ignored)
                {

                }
                if (connection != null)
                {
                    connection.disconnect();
                }
            }
        return null;
        }

        @Override
        protected void onCancelled()
        {
            hideProgressNotification(manager, getActivity(), 0);
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(Integer... progress)
        {
            super.onProgressUpdate(progress);
            // setting progress percentage
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
            mBuilder.setProgress(100, progress[0], false);
            manager.notify(0, mBuilder.build());
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String result)
        {
            mWakeLock.release();
            mProgressDialog.dismiss();
            hideProgressNotification(manager, getActivity(), 0);
            if (result != null)
            {
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile() + "/TxnReport_" + accountId + ".pdf");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        }
    }
    private class getTxn extends AsyncTask<Void, Void, String>
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
            String urlParameter = "txn?account_id=" + accountId;
            try
            {
                url = new URL(local_ip + getResources().getString(R.string.bank_local) + urlParameter);
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
                JSONArray txnArray = new JSONArray(result);

                Txn[] txn = new Txn[txnArray.length()];
                for(int i=0; i< txnArray.length(); i++)
                {
                    JSONObject txnObj = txnArray.getJSONObject(i);
                    txn[i] = new Txn(txnObj.getString("TxnRefID"), txnObj.getString("TxnDTM"), txnObj.getString("TxnType"), txnObj.getString("Amount"), txnObj.getString("Result"), txnObj.getString("SendAccountID"), txnObj.getString("ReceiveAccountID"));
                    txnList.add(txn[i]);
                }
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            txnAdapter = new TxnAdapter(txnList, accountId);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(txnAdapter);
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }
}