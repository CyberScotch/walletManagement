package com.walletmanagement.crudoperations.model;

import java.util.List;

public class TransactionPageResponse {
    private List<Transaction> transactionsList;
    private Integer pageNo;

    public List<Transaction> getTransactionsList() {
        return transactionsList;
    }

    public void setTransactionsList(List<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public TransactionPageResponse(List<Transaction> transactionsList, Integer pageNo) {
        this.transactionsList = transactionsList;
        this.pageNo = pageNo;
    }
}
