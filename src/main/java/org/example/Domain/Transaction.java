package org.example.Domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transaction {

    public enum  Type {
        WITHDRAW , DEPOSIT , TRANSFERIN , TRANSFEROUT
    }

    private  UUID id;
    private UUID ownerId;
    private  String accountId;
    private  Instant timestamp;
    private  Type type;
    private  BigDecimal amount;
    private  String counterpartyAccountId;



    public Transaction(UUID ownerId , String accountId , Type type , BigDecimal amount , String counterpartyAccountId){
            this.ownerId = ownerId;
            this.id = UUID.randomUUID();
            this.accountId = accountId;
            this.timestamp = Instant.now();
            this.type = type;
            this.amount = amount.setScale(2);
            this.counterpartyAccountId = counterpartyAccountId;
    }


    public UUID getId(){
        return this.id;
    }

    public UUID getOwnerId(){
        return  this.ownerId;
    }

    public String getAccountId(){
        return  this.accountId;
    }

    public Type getType(){
        return this.type;
    }

    public BigDecimal getAmount(){
        return  this.amount;
    }

    public Instant getTimestamp(){
        return this.timestamp;
    }
    public String getCounterpartyAccountId(){
        return  this.counterpartyAccountId;
    }

    @Override
    public String toString(){
        return "this done in -> ["+ timestamp+ "] and its type is -> " +
                "["+ type + "] and the amount is ->[" + amount + (counterpartyAccountId != null ? " -> " +
                "" + counterpartyAccountId : "]") ;
    }
}
