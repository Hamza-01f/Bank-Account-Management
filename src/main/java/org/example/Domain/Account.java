package org.example.Domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Account {
    private String accountId;
    private UUID userId;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private Boolean isActive;


    public Account(String accountId , UUID userId ){
           this.accountId = accountId;
           this.userId = userId;
           this.balance = BigDecimal.valueOf(0);
           this.isActive = true;
           this.createdAt = LocalDateTime.now();
    }

    public void deposit(BigDecimal amount){
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount){
        this.balance = this.balance.subtract(amount);
    }

//    public Account(BigDecimal withdrawAmount) {
//        this.balance = this.balance.subtract(withdrawAmount);
//    }
//
//    public Account(){
//
//    }

    public void setBalance(BigDecimal depositAmount){
         this.balance = this.balance.add(depositAmount);
    }

    public UUID getUserId(){
        return this.userId;
    }

    public boolean getStatus(){
        return this.isActive;
    }

    public BigDecimal getBalance(){
        return this.balance;
    }

    public String getAccountId(){
        return this.accountId;
    }

    @Override
    public String toString(){
        return "{ accountId = " + accountId +
                ", userId = " + userId +
                ", balance = " + balance +
                ", createdAt = " + createdAt +
                ", status : " + (isActive ? "ACTIVE" : "NOT ACTIVE") +
                "}";
    }

}
