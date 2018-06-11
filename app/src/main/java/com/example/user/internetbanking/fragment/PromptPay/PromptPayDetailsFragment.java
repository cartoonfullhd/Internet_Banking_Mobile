package com.example.user.internetbanking.fragment.PromptPay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.user.internetbanking.R;

public class PromptPayDetailsFragment extends Fragment
{
    String customer_id, local_ip;
    Button PromptPayRegisterbtn, PromptPayRemovebtn;
    private Bundle bundle = new Bundle();

    public PromptPayDetailsFragment()
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
        return inflater.inflate(R.layout.fragment_prompt_pay_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Bind your views.
        PromptPayRegisterbtn = view.findViewById(R.id.PromptPayRegisterbutton);
        PromptPayRemovebtn = view.findViewById(R.id.PromptPayRemovebutton);

        // Create your layout manager.
        PromptPayRegisterbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bundle.putString("CUSTOMER_ID", customer_id);
                bundle.putString("local_ip", local_ip);
                PromptPayRegisterFragment promptPayRegisterFragment = new PromptPayRegisterFragment();
                promptPayRegisterFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.PromptPayDetailsFragment,promptPayRegisterFragment );
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });

        PromptPayRemovebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bundle.putString("CUSTOMER_ID", customer_id);
                bundle.putString("local_ip", local_ip);
                PromptPayDeleteFragment promptPayDeleteFragment = new PromptPayDeleteFragment();
                promptPayDeleteFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.PromptPayDetailsFragment,promptPayDeleteFragment );
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });

    }
}
