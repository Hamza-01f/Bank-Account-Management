package org.example;

import org.example.Domain.Account;
import org.example.Domain.Transaction;
import org.example.Domain.User;
import org.example.RepositoryMemory.InMemoryAccountRepository;
import org.example.RepositoryMemory.InMemoryTransactionRepository;
import org.example.RepositoryMemory.InMemoryUserRepository;
import org.example.Service.AccountService;
import org.example.Service.AuthService;
import org.example.Service.TransactionService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Stream;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AuthService authService = new AuthService(new InMemoryUserRepository());
        AccountService accountService = new AccountService(new InMemoryAccountRepository());
        TransactionService transactionService = new TransactionService(new InMemoryTransactionRepository());
        Boolean accountSituation = true;
        User authenticatedUser = null;
        int choice;
        do {
            System.out.println("/---------------------------------------------------------------------------------/");
            System.out.println("/=================================== Welcome =====================================/");
            System.out.println("/---------------------------------------------------------------------------------/");
            System.out.println("1) Register");
            System.out.println("2) Login");
            System.out.println("3) Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Full name: ");
                    String fullName = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Password: ");
                    String pwd = sc.nextLine();

                    if (authService.register(fullName, email,  pwd))
                        System.out.println("Registration successful!");
                    else
                        System.out.println("Registration failed!");
                    break;

                case 2:
                    System.out.print("Email: ");
                    String loginEmail = sc.nextLine();
                    System.out.print("Password: ");
                    String loginPwd = sc.nextLine();

                    if (authService.login(loginEmail, loginPwd)) {
                        authenticatedUser = authService.getCurrentUser();
                        System.out.println("Logged in as !");
                        System.out.println("//-----  " + authenticatedUser.getFullName() + "  -----/");
                        do{
                            System.out.println("//-----------------------------------------------------//");
                            System.out.println("1 Create account : ");
                            System.out.println("2 List My Accounts : ");
                            System.out.println("3 Withdraw : ");
                            System.out.println("4 Deposit : ");
                            System.out.println("5 Transfer : ");
                            System.out.println("6 Transaction History: ");
                            System.out.println("7 Update Profile : ");
                            System.out.println("8 Change Password : ");
                            System.out.println("9 Close Account : ");
                            System.out.println("10 Logout : ");
                            System.out.println("11 Exit: ");
                            System.out.println("//---------------------------------------------------------//");
                            System.out.print("Enter Your Choice : ");

                            choice = sc.nextInt();
                            sc.nextLine();

                            mainLoop:
                            switch (choice){
                                
                                case 1 :

                                    UUID userId = authenticatedUser.getId();
                                    if(accountService.createAccount(userId)){
                                        System.out.println("//--------------------------------------------------------");
                                        System.out.println("Congratulation " + authenticatedUser.getFullName() + " you have created your account! : ");
                                        System.out.println("//---------------------------------------------------------");
                                    }else{
                                        System.out.println("something went wrong ! : ");
                                    }
                                    break;
                                case 2 :

                                    UUID ownerId = authenticatedUser.getId();
                                    List<Account> accounts = accountService.getMyAccounts(ownerId);
                                    System.out.println("//------ here is all of your Accounts : ---------//");
                                    accounts.stream().forEach(System.out::println);
                                    break;
                                case 3 :

                                    List<Account> withdrawAccountsList = accountService.getMyAccounts(authenticatedUser.getId());
                                    System.out.println("Here is a list of your accounts ! ");
                                    withdrawAccountsList.forEach(System.out::println);

                                    System.out.print("Enter your account ID : ");
                                    String accountId1 = sc.nextLine();

                                    System.out.print("Enter the amount you wanna withdraw : ");
                                    BigDecimal withdrawAmount = sc.nextBigDecimal();

                                    if(accountService.withdraw(accountId1 , withdrawAmount)){
                                        UUID owner = authenticatedUser.getId();
                                        transactionService.recordWithdraw(owner ,accountId1 , withdrawAmount);
                                        System.out.println("the amount is withdrawed with success ! ");
                                    }else{
                                        System.out.println("Your account amount does not allow for withdrawing ! !");
                                    }
                                    break;
                                case 4 :

                                    List<Account> accountsList = accountService.getMyAccounts(authenticatedUser.getId());
                                    System.out.println("Here is a list of your accounts ! ");
                                    accountsList.forEach(System.out::println);

                                    System.out.print("Enter your account ID : ");
                                    String accountId = sc.nextLine();

                                    System.out.print("Enter the amount you wanna deposit : ");
                                    BigDecimal depositAmount = sc.nextBigDecimal();

                                    if(accountService.deposit(accountId , depositAmount)){
                                        UUID owner = authenticatedUser.getId();
                                        transactionService.recordDeposit(owner , accountId , depositAmount);
                                        System.out.println("the amount is deposited with success ! ");
                                    }else{
                                        System.out.println("this account is not active!");
                                    }
                                    break;
                                case 5 :
                                    System.out.println("//----------- choose the type of the transfer ---------// ");
                                    System.out.println("1 Internal Transfer :");
                                    System.out.println("2 External Transfer :");
                                    System.out.println("3 Exit :");
                                    System.out.print("Enter your choice : ");
                                    choice = sc.nextInt();
                                    if(choice == 1 ){

                                        List<Account> accountsList1 = accountService.getMyAccounts(authenticatedUser.getId());
                                        if(accountsList1.size() < 2){
                                            System.out.println("You need at least to have two accounts ");
                                            return;
                                        }
                                        System.out.println("Here is a list of your accounts ! ");
                                        accountsList1.forEach(System.out::println);

                                        System.out.print("choose the account you wanna transfer from : ");
                                        String transferFrom = sc.nextLine();
                                        sc.nextLine();
                                        System.out.print("choose the account you wanna transfer to : ");
                                        String transferTo = sc.nextLine();

                                        System.out.print("Enter the amount you wanna send to : ");
                                        BigDecimal amount = sc.nextBigDecimal();

                                        if(accountService.withdraw(transferFrom,amount) && accountService.deposit(transferTo,amount)){
                                            transactionService.recordTransfer(authenticatedUser.getId() , transferFrom , transferTo , amount);
                                            System.out.println(" the transfer went well ");
                                        }else{
                                            System.out.println("something went wrong the transaction did no went as expected!");
                                        }

                                    } else if (choice == 2) {
                                        List<Account> allAccounts = accountService.getAllAccounts(authenticatedUser.getId());
                                        System.out.println("Here is a list of All accounts ! ");
                                        allAccounts.forEach(System.out::println);

                                        System.out.print("choose the account you wish transfer to : ");
                                        String transferTo = sc.nextLine();
                                        sc.nextLine();
                                        List<Account> accountsList1 = accountService.getMyAccounts(authenticatedUser.getId());
                                        System.out.println("//------------ here is a list of all of your accounts : ----//");
                                        System.out.print("choose the account you wanna transfer from : ");
                                        String transferFrom = sc.nextLine();

                                        System.out.print("Enter the amount you wanna send to : ");
                                        BigDecimal amount = sc.nextBigDecimal();

                                        if(accountService.withdraw(transferFrom,amount) && accountService.deposit(transferTo,amount)){
                                            transactionService.recordTransfer(authenticatedUser.getId() , transferFrom , transferTo , amount);
                                            System.out.println(" the transfer went well ");
                                        }else{
                                            System.out.println("something went wrong the transaction did no went as expected!");
                                            
                                        }

                                    }else{
                                        return;
                                    }

                                        System.out.println("no we are in the second process ! ");


                                     break;
                                case 6 :

                                     System.out.println("//------- Your Transactions History --------------//");
                                     List<Transaction> transactions = transactionService.getTransactions(authenticatedUser.getId());
                                     transactions.forEach(System.out::println);
                                     break;
                                case 7:

                                    System.out.print("Enter Your new username : ");
                                    String username = sc.nextLine();
                                    sc.nextLine();

                                    System.out.print("Enter Your new Email :");
                                    String newemail = sc.nextLine();
                                    sc.nextLine();

                                    if(authService.updateProfile(username , newemail)){
                                        System.out.println(" Your Profile is updated with success : ");
                                    }else{
                                        System.out.println("something went wrong therefore Your account is not updated : ");
                                    }
                                    break;
                                case 8 :

                                    System.out.print("Enter Your new password : ");
                                    String NewPassword = sc.nextLine();
                                    if(accountService.changePassword(NewPassword)){
                                        System.out.println("You have changed your password with success : ");
                                    }else{
                                        System.out.println("Your password did not change : ");
                                    }
                                    break;
                                case 9 :

                                    List<Account> accountsList1 = accountService.getMyAccounts(authenticatedUser.getId());
                                    System.out.println("Here is a list of your accounts ! ");
                                    accountsList1.forEach(System.out::println);

                                    System.out.print("Choose the account that you want to close : ");
                                    String closedAccount = sc.nextLine();
                                    if(accountService.closeAccount(closedAccount)){
                                        System.out.println("your account is closed with success : ");
                                    }else{
                                        System.out.println("Your account is already closed : ");
                                    }
                                    break;
                                case 10 :

                                    accountSituation = false;
                                    authenticatedUser = null;
                                    System.out.println("you are logged out right now : ");

                                    break mainLoop;
                                default:
                                    System.out.println("The entered number is incorrect : ");
                                    break;
                            }
                        }while(choice != 11 && accountSituation);
                    }else
                        System.out.println(" Invalid credentials!");
                    break;

                case 3:
                    System.out.println("Have a nice day");
                    break;

                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        } while (choice != 3);
    }
}