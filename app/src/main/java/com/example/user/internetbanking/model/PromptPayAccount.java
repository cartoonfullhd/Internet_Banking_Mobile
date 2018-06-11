package com.example.user.internetbanking.model;

public class PromptPayAccount
{
    private String AIPID;
    private String IDValue;
    private String IDType;
    private String BankCode;
    private String Status;
    private String AccountID;
    private String AccountName;
    private String RegisterDTM;

    public PromptPayAccount(String AIPID, String IDValue, String IDType, String bankCode, String status, String accountID, String accountName, String registerDTM)
    {
        this.AIPID = AIPID;
        this.IDValue = IDValue;
        this.IDType = IDType;
        BankCode = bankCode;
        Status = status;
        AccountID = accountID;
        AccountName = accountName;
        RegisterDTM = registerDTM;
    }

    public String getAIPID() {
        return AIPID;
    }

    public void setAIPID(String AIPID) {
        this.AIPID = AIPID;
    }

    public String getIDValue() {
        return IDValue;
    }

    public void setIDValue(String IDValue) {
        this.IDValue = IDValue;
    }

    public String getIDType() {
        return IDType;
    }

    public void setIDType(String IDType) {
        this.IDType = IDType;
    }

    public String getBankCode() {
        return BankCode;
    }

    public void setBankCode(String bankCode) {
        BankCode = bankCode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getAccountID() {
        return AccountID;
    }

    public void setAccountID(String accountID) {
        AccountID = accountID;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getRegisterDTM() {
        return RegisterDTM;
    }

    public void setRegisterDTM(String registerDTM) {
        RegisterDTM = registerDTM;
    }
}
