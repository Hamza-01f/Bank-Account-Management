package org.example.Repository;

import org.example.Domain.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountRepository {
    void save(Account account);
    List<Account> findMyAccounts(UUID ownerId);
    List<Account> getAllAccounts(UUID userId);
    boolean withdraw(String accountId , BigDecimal withdrawAmount);
    boolean deposit(String accountId , BigDecimal depositAmount);
    boolean closeAccount(String accountID);

}
