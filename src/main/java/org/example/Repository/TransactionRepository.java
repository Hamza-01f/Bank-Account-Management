package org.example.Repository;

import org.example.Domain.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository {

    void save(Transaction transaction);
    List<Transaction> findByUserId(UUID userId);
//    List<Transaction> findByAccountId(UUID userId, String accountId);
//    List<Transaction> findByType(UUID userId, Transaction.Type type);
}
