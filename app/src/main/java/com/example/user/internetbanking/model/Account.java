package com.example.user.internetbanking.model;

public class Account
{
    private String AccountId;
    private String CustomerId;
    private String BankCode;
    private String AccountType;
    private Double BalanceAmount;

    public Account(String accountId, String customerId, String bankCode, String accountType, Double balanceAmount) {
        super();
        this.AccountId = accountId;
        this.CustomerId = customerId;
        this.BankCode = bankCode;
        this.AccountType = accountType;
        this.BalanceAmount = balanceAmount;
    }

    public String getAccountId() {
        return AccountId;
    }

    public void setAccountId(String accountId) {
        AccountId = accountId;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }

    public String getBankCode() {
        return BankCode;
    }

    public void setBankCode(String bankCode) {
        BankCode = bankCode;
    }

    public String getAccountType() {
        return AccountType;
    }

    public void setAccountType(String accountType) {
        AccountType = accountType;
    }

    public Double getBalanceAmount() {
        return BalanceAmount;
    }

    public void setBalanceAmount(Double balanceAmount) {
        BalanceAmount = balanceAmount;
    }
}
