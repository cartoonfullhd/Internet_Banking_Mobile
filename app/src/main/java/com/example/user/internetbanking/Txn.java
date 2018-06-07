package com.example.user.internetbanking;

public class Txn
{
    private String TxnRefID;
    private String TxnDTM;
    private String TxnType;
    private String Amount;
    private String Result;
    private String SendAccountID;
    private String ReceiveAccountID;

    public Txn(String txnRefID, String txnDTM, String txnType, String amount, String result, String sendAccountID, String receiveAccountID)
    {
        TxnRefID = txnRefID;
        TxnDTM = txnDTM;
        TxnType = txnType;
        Amount = amount;
        Result = result;
        SendAccountID = sendAccountID;
        ReceiveAccountID = receiveAccountID;
    }

    public String getTxnRefID() {
        return TxnRefID;
    }

    public void setTxnRefID(String txnRefID) {
        TxnRefID = txnRefID;
    }

    public String getTxnDTM() {
        return TxnDTM;
    }

    public void setTxnDTM(String txnDTM) {
        TxnDTM = txnDTM;
    }

    public String getTxnType() {
        return TxnType;
    }

    public void setTxnType(String txnType) {
        TxnType = txnType;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getSendAccountID() {
        return SendAccountID;
    }

    public void setSendAccountID(String sendAccountID) {
        SendAccountID = sendAccountID;
    }

    public String getReceiveAccountID() {
        return ReceiveAccountID;
    }

    public void setReceiveAccountID(String receiveAccountID) {
        ReceiveAccountID = receiveAccountID;
    }
}
