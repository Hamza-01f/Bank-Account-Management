package org.example.Repository;

import org.example.Domain.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(String accountId);
    List<Account> findByUserId(UUID userId);
    boolean withdraw(String accountId, BigDecimal amount);
    boolean deposit(String accountId, BigDecimal amount);
    boolean closeAccount(String accountId);
    List<Account> findAllAccounts(UUID ownerId);


}
