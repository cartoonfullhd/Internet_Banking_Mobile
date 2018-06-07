package com.example.user.internetbanking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder>
{
    private List<Account> accountList;
    private Account account;
    private String local_ip;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView typeList, accountIdList, amountList;

        public MyViewHolder(View view)
        {
            super(view);
            typeList = view.findViewById(R.id.typeList);
            accountIdList = view.findViewById(R.id.accountIdList);
            amountList = view.findViewById(R.id.amountList);
        }
    }

    public AccountAdapter(List<Account> accountList, String local_ip)
    {
        this.accountList = accountList;
        this.local_ip = local_ip;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        account = accountList.get(position);
        holder.typeList.setText(String.valueOf(account.getAccountType()));
        holder.accountIdList.setText(account.getAccountId());
        holder.amountList.setText(String.valueOf(account.getBalanceAmount()));

        final Bundle bundle = new Bundle();
        bundle.putString("AccountId", account.getAccountId());
        bundle.putString("AccountType", account.getAccountType());
        bundle.putString("AccountAmount", account.getBalanceAmount().toString());
        bundle.putString("AccountBankCode", account.getBankCode());
        bundle.putString("local_ip", local_ip);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AccountDetailsFragment accountDetails = new AccountDetailsFragment();
                accountDetails.setArguments(bundle);
                ((AppCompatActivity) v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.accountFrameLayout, accountDetails).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return accountList.size();
    }
}
