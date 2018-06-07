package com.example.user.internetbanking;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TxnAdapter extends RecyclerView.Adapter<TxnAdapter.MyViewHolder>
{
    private List<Txn> txnList;
    private Txn txn;
    private String accountID;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView dateList, typeList, amountList;

        public MyViewHolder(View view)
        {
            super(view);
            dateList = view.findViewById(R.id.dateList);
            typeList = view.findViewById(R.id.txnTypeList);
            amountList = view.findViewById(R.id.amountList);
        }
    }

    public TxnAdapter(List<Txn> txnList, String accountId)
    {
        this.txnList = txnList;
        this.accountID = accountId;
    }

    @Override
    public TxnAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.txn_list, parent, false);
        return new TxnAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TxnAdapter.MyViewHolder holder, int position)
    {
        txn = txnList.get(position);

        if(txn.getSendAccountID().equals(accountID))
        {
            holder.amountList.setText("- " + txn.getAmount());
        }
        else
        {
            holder.amountList.setText("+ " + txn.getAmount());
        }

        holder.dateList.setText(txn.getTxnDTM());
        holder.typeList.setText(txn.getTxnType());
        //holder.amountList.setText(txn.getAmount());

        /*final Bundle bundle = new Bundle();
        bundle.putString("AccountId", account.getAccountId());
        bundle.putString("AccountType", account.getAccountType());
        bundle.putString("AccountAmount", account.getBalanceAmount().toString());
        bundle.putString("AccountBankCode", account.getBankCode());

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AccountDetailsFragment accountDetails = new AccountDetailsFragment();
                accountDetails.setArguments(bundle);
                ((AppCompatActivity) v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.accountFrameLayout, accountDetails).addToBackStack(null).commit();
            }
        });*/
    }

    @Override
    public int getItemCount()
    {
        return txnList.size();
    }

}
