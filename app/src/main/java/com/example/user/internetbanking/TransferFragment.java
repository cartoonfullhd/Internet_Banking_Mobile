package com.example.user.internetbanking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TransferFragment extends Fragment
{
    private Button accountNumBtn, promptPayBtn;
    String customer_id, local_ip;
    private Bundle bundle = new Bundle();

    public TransferFragment() {
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
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Bind your views.
        accountNumBtn = view.findViewById(R.id.AccountNumberbutton);
        promptPayBtn = view.findViewById(R.id.PromptPaybutton);

        // Create your layout manager.
        accountNumBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bundle.putString("CUSTOMER_ID", customer_id);
                bundle.putString("local_ip", local_ip);
                TransferAccountNumInputFragment transferAccountNumInputFragment = new TransferAccountNumInputFragment();
                transferAccountNumInputFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.TransferFrameLayout,transferAccountNumInputFragment );
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });

        promptPayBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bundle.putString("CUSTOMER_ID", customer_id);
                bundle.putString("local_ip", local_ip);
                TransferPromptPayInputFragment transferPromptPayInputFragment = new TransferPromptPayInputFragment();
                transferPromptPayInputFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.TransferFrameLayout,transferPromptPayInputFragment );
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });
    }
}
