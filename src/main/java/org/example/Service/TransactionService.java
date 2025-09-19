package org.example.Service;

import org.example.Domain.Transaction;
import org.example.Repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void recordDeposit(UUID owner , String accountId , BigDecimal amount){
         transactionRepository.save(new Transaction(owner , accountId , Transaction.Type.DEPOSIT , amount , null));
    }

    public void recordWithdraw(UUID ownerId , String accountId , BigDecimal amount){
        transactionRepository.save(new Transaction(ownerId , accountId , Transaction.Type.WITHDRAW , amount , null));
    }



    public void recordTransfer(UUID owner , String fromAccountId , String toAccountId , BigDecimal amount , boolean transferType){
         if(transferType){
             transactionRepository.save(new Transaction(owner , fromAccountId , Transaction.Type.TRANSFERIN , amount , toAccountId));
         }else{
             transactionRepository.save(new Transaction(owner , toAccountId , Transaction.Type.TRANSFEROUT , amount , fromAccountId));
         }
    }

    public List<Transaction>  getTransactions(UUID ownerId){
        return transactionRepository.findByAccountId(ownerId);
    }
}
