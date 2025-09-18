package org.example.RepositoryMemory;

import org.example.Domain.Transaction;
import org.example.Repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryTransactionRepository implements TransactionRepository {

  private final List<Transaction> transactionList = new ArrayList<>();

  @Override
  public void save(Transaction transaction){
      transactionList.add(transaction);
  }

  @Override
  public List<Transaction> findByAccountId(UUID ownerId){
    return transactionList.stream()
                          .filter(transactions -> transactions.getOwnerId().equals(ownerId))
                          .sorted((t1 , t2 ) -> t1.getTimestamp().compareTo(t2.getTimestamp()))
                          .collect(Collectors.toList());
  }

}
