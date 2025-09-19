package org.example.RepositoryMemory;

import org.example.Domain.Account;
import org.example.Domain.User;
import org.example.Repository.AccountRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryAccountRepository implements AccountRepository {

    List<Account> savedAccounts = new ArrayList<>();
    Account account = new Account();

    @Override
    public void save(Account account){
       savedAccounts.add(account);
    }

    @Override
    public List<Account> findMyAccounts(UUID ownerId){
       return  savedAccounts.stream()
                           .filter(account -> account.getUserId().equals(ownerId))
                           .toList();
    }

    @Override
    public List<Account> getAllAccounts(UUID userId){
        return savedAccounts.stream().filter(account -> !account.getUserId().equals(userId))
                            .toList();
    }

    @Override
    public boolean withdraw(String accountId , BigDecimal withdrawAmount) {

        Optional<Account> accOpt = savedAccounts.stream()
                .filter(acc -> acc.getAccountId().equals(accountId))
                .findFirst();

        if (accOpt.isEmpty()) return false;

        Account account = accOpt.get();

        if(!account.getStatus()) return false;
        if(account.getBalance().compareTo(withdrawAmount) < 0) return false;

        account.withdraw(withdrawAmount);
        return true;
    }

    @Override
    public boolean deposit(String accountId , BigDecimal depositAmount) {
        Optional<Account> accOpt = savedAccounts.stream()
                .filter(acc -> acc.getAccountId().equals(accountId))
                .findFirst();

        if (accOpt.isEmpty()) return false;

        Account account = accOpt.get();

        if(!account.getStatus()) return false;

        account.deposit(depositAmount);
        return true;
    }

    @Override
    public boolean closeAccount(String accountID){
         savedAccounts.stream()
                 .filter(account -> account.getAccountId().equals(accountID))
                 .findFirst()
                 .ifPresent(acc -> acc.setIsActive());
         return true;
    }

    //

//    public boolean closeAccount(){
//
//    }
}
