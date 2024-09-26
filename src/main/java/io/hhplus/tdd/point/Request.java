package io.hhplus.tdd.point;

public class Request {

    long id;
    long amount;
    TransactionType transactionType;

    public Request(long id, long amount, TransactionType transactionType) {
        this.id = id;
        this.amount = amount;
        this.transactionType = transactionType;
    }
}
