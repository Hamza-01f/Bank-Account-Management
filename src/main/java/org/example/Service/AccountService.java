package org.example.Service;

import org.example.Domain.Account;
import org.example.Domain.User;
import org.example.Repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class AccountService {

    private  AccountRepository accountRepository;
    private final Random random = new Random();


    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public Account createAccount(UUID ownerId){
        String accountId = generateAccountId();
        Account account = new Account(accountId , ownerId);
        accountRepository.save(account);
        return account;
    }

    private String generateAccountId(){
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder part1 = new StringBuilder();
        for(int i = 0 ; i < 4 ; i++){
            part1.append(letters.charAt(random.nextInt(letters.length())));
        }

        int digits = 1000 + random.nextInt(9000);

        return "BK-" + digits + "-" + part1;
    }

//    public List<Account> getMyAccounts(UUID ownerId){
//        return accountRepository.findMyAccounts(ownerId);
//    }
//
//    public List<Account> getAllAccounts(UUID userId){
//        return  accountRepository.getAllAccounts(userId);
//    }
    public boolean withdraw(String accountId , BigDecimal withdrawAmount , UUID userId){
        Optional<Account> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }

        Account account = accountOpt.get();

        if (!account.getUserId().equals(userId)) {
            throw new SecurityException("Access denied: Not account owner");
        }

        if (!account.getIsActive()) {
            throw new IllegalStateException("Account is closed");
        }

        if(account.getBalance().compareTo(withdrawAmount) < 0){
            throw new IllegalArgumentException("Insufficient funds");
        }

        return accountRepository.withdraw(accountId, withdrawAmount);

    }

    public boolean deposit(String accountId , BigDecimal depositAmount , UUID userId){
        Optional<Account> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }

        Account account = accountOpt.get();

        if (!account.getUserId().equals(userId)) {
            throw new SecurityException("Access denied: Not account owner");
        }

        if (!account.getIsActive()) {
            throw new IllegalStateException("Account is closed");
        }

        return accountRepository.deposit(accountId, depositAmount);
    }

    public boolean closeAccount(String accountId, UUID userId){
        Optional<Account> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }

        Account account = accountOpt.get();

        if (!account.getUserId().equals(userId)) {
            throw new SecurityException("Access denied: Not account owner");
        }

        if (!account.getIsActive()) {
            throw new IllegalStateException("Account already closed");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot close account with balance");
        }

        return accountRepository.closeAccount(accountId);
    }

    public List<Account> getUserAccounts(UUID userId) {
        return accountRepository.findByUserId(userId);
    }

    public List<Account> getAllAccounts(UUID userId){
        return  accountRepository.findAllAccounts(userId);
    }

    public Optional<Account> getAccount(String accountId, UUID userId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isPresent() && !accountOpt.get().getUserId().equals(userId)) {
            throw new SecurityException("Access denied: Not account owner");
        }

        return accountOpt;
    }


}
