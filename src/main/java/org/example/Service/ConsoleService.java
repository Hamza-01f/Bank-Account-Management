package org.example.Service;

import org.example.Domain.Account;
import org.example.Domain.Transaction;
import org.example.Domain.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner;
    private final AuthService authService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private  ValidationService validationService = new ValidationService();

    public ConsoleService(AccountService accountService ,
                          AuthService authService , TransactionService transactionService ){
           this.scanner = new Scanner(System.in);
           this.accountService = accountService;
           this.authService = authService;
           this.transactionService = transactionService;
           this.validationService = validationService;
    }

    public void displayWelcom(){
        System.out.println("/=================================== Welcome =====================================/");
        System.out.println("1) Register");
        System.out.println("2) Login");
        System.out.println("3) Exit");
        System.out.print("Enter choice: ");
    }

    public  void displayTransfer(){
        System.out.println("/=================================== Choose your way to transfer =====================================/");
        System.out.println("1) Entern Transfer");
        System.out.println("2) Outer Transfer");
        System.out.println("3) Exit");
        System.out.print("Enter choice: ");
    }

    public void displayMainMenu(User user){
        System.out.println("/=================================== Main Menu =====================================/");
        System.out.println("Logged in as: " + user.getFullName());
        System.out.println("1. Create Account");
        System.out.println("2. List My Accounts");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5. Transfer");
        System.out.println("6. Transaction History");
        System.out.println("7. Update Profile");
        System.out.println("8. Change Password");
        System.out.println("9. Close Account");
        System.out.println("10. Logout");
        System.out.println("11. Exit");
        System.out.print("Enter Your Choice: ");
    }

    public String readString(String prompt){
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public BigDecimal readBigDecimal(String prompt){

        while(true){
            try{
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                BigDecimal amount = new BigDecimal(input);

                if(validationService.isValidAmount(amount)){
                    return amount.setScale(2);
                }else{
                    System.out.println("Invalid amount. Must be positive with max 2 decimal places.");
                }
            }catch(Exception e){
                System.out.println("Please enter a valid number.");
            }
        }

    }

    public int readInt(String prompt){
        while(true){
            try{
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            }catch (NumberFormatException e){
                System.out.println("please enter a valid number : ");
            }
        }
    }

    public void displayAccounts(List<Account> accounts){
        if(accounts.isEmpty()){
            System.out.println("there is no accounts to display: ");
            return;
        }

        System.out.println("\n Your Accounts : ");
        System.out.println("----------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-10s %-10s%n", "Account ID", "Created", "Balance", "Status");
        System.out.println("----------------------------------------------------------------");
        for(Account account : accounts){
              System.out.printf("%-15s %-20s %-10s %-10s%n",
                account.getAccountId(),
                account.getCreatedAt().toLocalDate().toString(),
                account.getBalance(),
                account.getStatus() ? "ACTIVE" : "NOT ACTIVE");
        }

        System.out.println("----------------------------------------------------------------");
    }

    public void displayTransactions(List<Transaction> transactions){
         if(transactions.isEmpty()){
             System.out.println("No transactions found :");
             return;
         }

        System.out.println("\nTransaction History:");
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.printf("%-20s %-12s %-10s %-20s %-15s%n", "Date", "Type", "Amount", "Account", "Counterparty");
        System.out.println("----------------------------------------------------------------------------------------");

        for(Transaction transaction : transactions){
            System.out.printf("%-20s %-12s %-10s %-20s %-15s%n" ,
                              transaction.getTimestamp().toString(),
                              transaction.getType(),
                              transaction.getAmount(),
                              transaction.getAccountId(),
                              transaction.getCounterpartyAccountId() != null ?
                                      transaction.getCounterpartyAccountId() : "N/A");
        }

        System.out.println("----------------------------------------------------------------------------------------");
    }

    public void displayError(String message){
        System.out.println("Error :" + message);
    }

    public void displaySuccess(String message) {
        System.out.println("✅ " + message);
    }

    public void displayInfo(String message) {
        System.out.println("ℹ️ " + message);
    }
}
