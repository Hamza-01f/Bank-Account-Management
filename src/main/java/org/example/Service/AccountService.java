package org.example.Service;

import org.example.Domain.Account;
import org.example.Repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AccountService {

    private  AccountRepository accountRepository;
    private final Random random = new Random();

    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public boolean createAccount(UUID ownerId){
        String accountId = generateAccountId();
        Account account = new Account(accountId , ownerId);
        accountRepository.save(account);
        return true;
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

    public List<Account> getMyAccounts(UUID ownerId){
        return accountRepository.findMyAccounts(ownerId);
    }

    public List<Account> getAllAccounts(){
        return  accountRepository.getAllAccounts();
    }
    public boolean withdraw(String accountId , BigDecimal withdrawAmount){
        return accountRepository.withdraw(accountId , withdrawAmount);
    }

    public boolean deposit(String accountId , BigDecimal depositAmount){
        return  accountRepository.deposit(accountId , depositAmount);
    }

//    public boolean transfer(){
//
//    }
}
