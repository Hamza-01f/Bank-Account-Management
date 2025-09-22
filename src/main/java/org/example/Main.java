package org.example;

import org.example.Domain.Account;
import org.example.Domain.Transaction;
import org.example.Domain.User;
import org.example.RepositoryMemory.InMemoryAccountRepository;
import org.example.RepositoryMemory.InMemoryTransactionRepository;
import org.example.RepositoryMemory.InMemoryUserRepository;
import org.example.Service.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Stream;


public class Main {
    public static void main(String[] args) {

          AuthService authService = new AuthService(new InMemoryUserRepository());
          AccountService accountService = new AccountService(new InMemoryAccountRepository());
          TransactionService transactionService = new TransactionService(new InMemoryTransactionRepository());
          ConsoleService consoleService = new ConsoleService(accountService  , authService , transactionService);

          BankApplication bankApplication = new BankApplication(authService , accountService , transactionService , consoleService );
          bankApplication.run();
    }
}

class BankApplication{

    ValidationService validationService = new ValidationService();
    private final AccountService accountService;
    private final AuthService authService;
    private final TransactionService transactionService;
    private final ConsoleService consoleService;
    private User currentUser;

    public  BankApplication(AuthService authService ,
                            AccountService accountService ,
                            TransactionService transactionService,
                            ConsoleService consoleService){
        this.accountService = accountService;
        this.authService = authService;
        this.transactionService = transactionService;
        this.consoleService = consoleService;

    }

    public void run(){

        boolean running = true ;
        while(running){
            if(currentUser == null){
                showUnauthinticatedMenu();
            }else {
                showAuthonticatedMenu();
            }

            consoleService.displayInfo("thank you for using our bank application : ");
        }
    }

    public boolean showUnauthinticatedMenu(){
           consoleService.displayWelcom();
           int choice = consoleService.readInt("");
           switch (choice) {
               case 1 -> handleRegistration();
               case 2 -> handleLogIn();
               case 3 -> {return false;}
               default -> consoleService.displayError("invalid Choice!");
           }

           return true;
    }

    public boolean showAuthonticatedMenu(){
           consoleService.displayMainMenu(currentUser);
           int choice = consoleService.readInt("");

           try{
               switch(choice){
                   case 1 -> handleCreateAccount();
                   case 2 -> handleListAccounts();
                   case 3 -> handleDeposit();
                   case 4 -> handleWithdraw();
                   case 5 -> handleTransfer();
                   case 6 -> handleTransactionHistory();
                   case 7 -> handleUpdateProfile();
                   case 8 -> handleChangePassword();
                   case 9 -> handleCloseAccount();
                   case 10 -> { currentUser = null; return true; }
                   case 11 -> { return false; }
                   default -> consoleService.displayError("Invalid choice!");
               }
           }catch(Exception e){
               consoleService.displayError(e.getMessage());
           }

           return true;
    }

    public void handleRegistration(){
       String fullName = consoleService.readString("Full Name : ");
       String email =  consoleService.readString("Email : ");
       String password = consoleService.readString("Password : ");

       if(authService.register(fullName , email , password)){
           consoleService.displaySuccess(" Registration successful!");
       }else{
           consoleService.displayError("Registration failed. Email may already exist.");
       }
    }

    public void handleLogIn(){
       String email = consoleService.readString("Email : ");
       String password = consoleService.readString("Password : ");

       if(authService.login(email , password)){
           currentUser = authService.getCurrentUser();
           consoleService.displaySuccess("Logged in Successefully Welcom! " + currentUser.getFullName());
       }else {
            consoleService.displayError("Invalid Credentials: ");
       }
    }

    public void handleCreateAccount(){
        var account = accountService.createAccount(currentUser.getId());
        consoleService.displaySuccess("Account created successfully: ");
    }

    public void handleListAccounts(){
       var accounts = accountService.getUserAccounts(currentUser.getId());
       consoleService.displayAccounts(accounts);
    }

    void handleDeposit(){

       var accounts = accountService.getUserAccounts(currentUser.getId());
       consoleService.displayAccounts(accounts);
       String accountId = consoleService.readString("Account Id : ");
       BigDecimal amount = consoleService.readBigDecimal("Amount :  ");
       accountService.deposit(accountId , amount , currentUser.getId());
       transactionService.recordTransaction(currentUser.getId() , accountId , Transaction.Type.DEPOSIT , amount , null);

    }

    void handleWithdraw(){

        var accounts = accountService.getUserAccounts(currentUser.getId());
        consoleService.displayAccounts(accounts);
        String accountId = consoleService.readString("Account Id : ");
        BigDecimal amount = consoleService.readBigDecimal("Amount :  ");
        accountService.withdraw(accountId , amount , currentUser.getId());
        transactionService.recordTransaction(currentUser.getId() , accountId , Transaction.Type.WITHDRAW , amount , null);

    }

    boolean handleTransfer(){

           consoleService.displayTransfer();
           int choice = consoleService.readInt("");
           switch(choice){
               case 1 -> handleInternalTransfer();
               case 2 -> handleExternalTransfer();
               case 3 -> {return  false;}
               default ->  consoleService.displayError("Invalid choice : ");
           }

           return  true;
    }

    void handleTransactionHistory(){
        var transactions = transactionService.getUserTransactions(currentUser.getId());
        consoleService.displayTransactions(transactions);
    }

    void handleUpdateProfile(){
        String name = consoleService.readString("Full Name : ");
        String email =  consoleService.readString("Email : ");
        authService.updateProfile(name , email);
    }

    void handleChangePassword(){
        String password = consoleService.readString("New Password : ");
        authService.changePassword(password , currentUser.getId());

    }

    void handleCloseAccount(){

        var accounts = accountService.getUserAccounts(currentUser.getId());
        consoleService.displayAccounts(accounts);
        String accoundToClose = consoleService.readString("Account You want to close : ");
        accountService.closeAccount(accoundToClose , currentUser.getId());

    }

    void handleExternalTransfer() {
        try {
            var accounts = accountService.getUserAccounts(currentUser.getId());
            consoleService.displayAccounts(accounts);

            String fromAccountId = consoleService.readString("Account to transfer from: ");
            String toAccountId = consoleService.readString("External account ID to transfer to: ");
            BigDecimal amount = consoleService.readBigDecimal("Amount: ");

            accountService.getAccount(fromAccountId, currentUser.getId());

            accountService.withdraw(fromAccountId, amount, currentUser.getId());

            transactionService.recordTransaction(currentUser.getId(), fromAccountId,
                    Transaction.Type.TRANSFEROUT, amount, toAccountId);

            consoleService.displaySuccess("External transfer completed successfully!");

        } catch (Exception e) {
            consoleService.displayError(e.getMessage());
        }
    }

    void handleInternalTransfer(){

        try {
            var accounts = accountService.getUserAccounts(currentUser.getId());
            consoleService.displayAccounts(accounts);

            String fromAccountId = consoleService.readString("Account to transfer from: ");
            String toAccountId = consoleService.readString("Account to transfer to: ");
            BigDecimal amount = consoleService.readBigDecimal("Amount: ");

            accountService.getAccount(fromAccountId, currentUser.getId());
            accountService.getAccount(toAccountId, currentUser.getId());

            accountService.withdraw(fromAccountId, amount, currentUser.getId());
            accountService.deposit(toAccountId, amount, currentUser.getId());

            transactionService.recordTransaction(currentUser.getId(), fromAccountId,
                    Transaction.Type.TRANSFEROUT, amount, toAccountId);
            transactionService.recordTransaction(currentUser.getId(), toAccountId,
                    Transaction.Type.TRANSFERIN, amount, fromAccountId);

            consoleService.displaySuccess("Transfer completed successfully!");

        } catch (Exception e) {
            consoleService.displayError(e.getMessage());
        }
    }


}

//        Scanner sc = new Scanner(System.in);
//        AuthService authService = new AuthService(new InMemoryUserRepository());
//        AccountService accountService = new AccountService(new InMemoryAccountRepository());
//        TransactionService transactionService = new TransactionService(new InMemoryTransactionRepository());
//        Boolean accountSituation = true;
//        User authenticatedUser = null;
//
//        int choice;
//        do {
//            System.out.println("/---------------------------------------------------------------------------------/");
//            System.out.println("/=================================== Welcome =====================================/");
//            System.out.println("/---------------------------------------------------------------------------------/");
//            System.out.println("1) Register");
//            System.out.println("2) Login");
//            System.out.println("3) Exit");
//            System.out.print("Enter choice: ");
//
//            choice = sc.nextInt();
//            sc.nextLine();
//
//            switch (choice) {
//                case 1:
//                    System.out.print("Full name: ");
//                    String fullName = sc.nextLine();
//                    System.out.print("Email: ");
//                    String email = sc.nextLine();
//                    System.out.print("Password: ");
//                    String pwd = sc.nextLine();
//
//                    if (authService.register(fullName, email,  pwd))
//                        System.out.println("Registration successful!");
//                    else
//                        System.out.println("Registration failed!");
//                    break;
//
//                case 2:
//                    System.out.print("Email: ");
//                    String loginEmail = sc.nextLine();
//                    System.out.print("Password: ");
//                    String loginPwd = sc.nextLine();
//
//                    if (authService.login(loginEmail, loginPwd)) {
//                        authenticatedUser = authService.getCurrentUser();
//                        System.out.println("Logged in as !");
//                        System.out.println("//-----  " + authenticatedUser.getFullName() + "  -----/");
//                        do{
//                            System.out.println("//-----------------------------------------------------//");
//                            System.out.println("1 Create account : ");
//                            System.out.println("2 List My Accounts : ");
//                            System.out.println("3 Withdraw : ");
//                            System.out.println("4 Deposit : ");
//                            System.out.println("5 Transfer : ");
//                            System.out.println("6 Transaction History: ");
//                            System.out.println("7 Update Profile : ");
//                            System.out.println("8 Change Password : ");
//                            System.out.println("9 Close Account : ");
//                            System.out.println("10 Logout : ");
//                            System.out.println("11 Exit: ");
//                            System.out.println("//---------------------------------------------------------//");
//                            System.out.print("Enter Your Choice : ");
//
//                            choice = sc.nextInt();
//                            sc.nextLine();
//
//                            mainLoop:
//                            switch (choice){
//
//                                case 1 :
//
//                                    UUID userId = authenticatedUser.getId();
//                                    if(accountService.createAccount(userId)){
//                                        System.out.println("//--------------------------------------------------------");
//                                        System.out.println("Congratulation " + authenticatedUser.getFullName() + " you have created your account! : ");
//                                        System.out.println("//---------------------------------------------------------");
//                                    }else{
//                                        System.out.println("something went wrong ! : ");
//                                    }
//                                    break;
//                                case 2 :
//
//                                    UUID ownerId = authenticatedUser.getId();
//                                    List<Account> accounts = accountService.getMyAccounts(ownerId);
//                                    System.out.println("//------ here is all of your Accounts : ---------//");
//                                    accounts.stream().forEach(System.out::println);
//                                    break;
//                                case 3 :
//
//                                    List<Account> withdrawAccountsList = accountService.getMyAccounts(authenticatedUser.getId());
//                                    System.out.println("Here is a list of your accounts ! ");
//                                    withdrawAccountsList.forEach(System.out::println);
//
//                                    System.out.print("Enter your account ID : ");
//                                    String accountId1 = sc.nextLine();
//
//                                    System.out.print("Enter the amount you wanna withdraw : ");
//                                    BigDecimal withdrawAmount = sc.nextBigDecimal();
//
//                                    if(accountService.withdraw(accountId1 , withdrawAmount)){
//                                        UUID owner = authenticatedUser.getId();
//                                        transactionService.recordWithdraw(owner ,accountId1 , withdrawAmount);
//                                        System.out.println("the amount is withdrawed with success ! ");
//                                    }else{
//                                        System.out.println("Your account amount does not allow for withdrawing ! !");
//                                    }
//                                    break;
//                                case 4 :
//
//                                    List<Account> accountsList = accountService.getMyAccounts(authenticatedUser.getId());
//                                    System.out.println("Here is a list of your accounts ! ");
//                                    accountsList.forEach(System.out::println);
//
//                                    System.out.print("Enter your account ID : ");
//                                    String accountId = sc.nextLine();
//
//                                    System.out.print("Enter the amount you wanna deposit : ");
//                                    BigDecimal depositAmount = sc.nextBigDecimal();
//
//                                    if(accountService.deposit(accountId , depositAmount)){
//                                        UUID owner = authenticatedUser.getId();
//                                        transactionService.recordDeposit(owner , accountId , depositAmount);
//                                        System.out.println("the amount is deposited with success ! ");
//                                    }else{
//                                        System.out.println("this account is not active!");
//                                    }
//                                    break;
//                                case 5 :
//                                    System.out.println("//----------- choose the type of the transfer ---------// ");
//                                    System.out.println("1 Internal Transfer :");
//                                    System.out.println("2 External Transfer :");
//                                    System.out.println("3 Exit :");
//                                    System.out.print("Enter your choice : ");
//                                    choice = sc.nextInt();
//                                    sc.nextLine();
//                                    if(choice == 1 ){
//
//
//                                        List<Account> accountsList1 = accountService.getMyAccounts(authenticatedUser.getId());
//                                        if(accountsList1.size() < 2){
//                                            System.out.println("You need at least to have two accounts ");
//                                            return;
//                                        }
//
//                                        System.out.println("Here is a list of your accounts ! ");
//                                        accountsList1.forEach(System.out::println);
//
//                                        System.out.print("choose the account you wanna transfer from : ");
//                                        String transferFrom = sc.nextLine().trim();
//
//                                        System.out.print("choose the account you wanna transfer to : ");
//                                        String transferTo = sc.nextLine().trim();
//
//                                        System.out.print("Enter the amount you wanna send to : ");
//                                        BigDecimal amount = sc.nextBigDecimal();
//                                        sc.nextLine();
//
//                                        accountService.withdraw(transferFrom,amount);
//                                        accountService.deposit(transferTo,amount);
//
//                                        transactionService.recordTransfer(authenticatedUser.getId() , transferFrom , transferTo , amount , true);
//                                        System.out.println(" the transfer is done : ");
//
//                                    } else if (choice == 2) {
//                                        List<Account> allAccounts = accountService.getAllAccounts(authenticatedUser.getId());
//                                        System.out.println("Here is a list of All accounts ! ");
//                                        allAccounts.forEach(System.out::println);
//
//                                        System.out.print("choose the account you wish to transfer to : ");
//                                        String transferTo = sc.nextLine();
//
//                                        List<Account> accountsList1 = accountService.getMyAccounts(authenticatedUser.getId());
//                                        System.out.println("//------------ here is a list of all of your accounts : ----//");
//                                        System.out.print("choose the account you wanna transfer from : ");
//                                        String transferFrom = sc.nextLine();
//
//                                        System.out.print("Enter the amount you wanna send  : ");
//                                        BigDecimal amount = sc.nextBigDecimal();
//                                        sc.nextLine();
//
//                                        accountService.withdraw(transferFrom,amount);
//                                        accountService.deposit(transferTo,amount);
//
//                                        transactionService.recordTransfer(authenticatedUser.getId() , transferFrom , transferTo , amount , false);
//                                        System.out.println(" the transfer went well ");
//
//                                    }else{
//                                        return;
//                                    }
//
//                                        System.out.println("no we are in the second process ! ");
//
//
//                                     break;
//                                case 6 :
//
//                                     System.out.println("//------- Your Transactions History --------------//");
//                                     List<Transaction> transactions = transactionService.getTransactions(authenticatedUser.getId());
//                                     transactions.forEach(System.out::println);
//                                     break;
//                                case 7:
//
//                                    System.out.print("Enter Your new username : ");
//                                    String username = sc.nextLine();
//                                    sc.nextLine();
//
//                                    System.out.print("Enter Your new Email :");
//                                    String newemail = sc.nextLine();
//                                    sc.nextLine();
//
//                                    if(authService.updateProfile(username , newemail)){
//                                        System.out.println(" Your Profile is updated with success : ");
//                                    }else{
//                                        System.out.println("something went wrong therefore Your account is not updated : ");
//                                    }
//                                    break;
//                                case 8 :
//
//                                    System.out.print("Enter Your new password : ");
//                                    String NewPassword = sc.nextLine();
//                                    if(authService.changePassword(NewPassword , authenticatedUser.getId())){
//                                        System.out.println("You have changed your password with success : ");
//                                    }else{
//                                        System.out.println("Your password did not change : ");
//                                    }
//                                    break;
//                                case 9 :
//
//                                    List<Account> accountsList1 = accountService.getMyAccounts(authenticatedUser.getId());
//                                    System.out.println("Here is a list of your accounts ! ");
//                                    accountsList1.forEach(System.out::println);
//
//                                    System.out.print("Choose the account that you want to close : ");
//                                    String closedAccount = sc.nextLine();
//                                    if(accountService.closeAccount(closedAccount)){
//                                        System.out.println("your account is closed with success : ");
//                                    }else{
//                                        System.out.println("Your account is already closed : ");
//                                    }
//                                    break;
//                                case 10 :
//
//                                    accountSituation = false;
//                                    authenticatedUser = null;
//                                    System.out.println("you are logged out right now : ");
//
//                                    break mainLoop;
//                                default:
//                                    System.out.println("The entered number is incorrect : ");
//                                    break;
//                            }
//                        }while(choice != 11 && accountSituation);
//                    }else
//                        System.out.println(" Invalid credentials!");
//                    break;
//
//                case 3:
//                    System.out.println("Have a nice day");
//                    break;
//
//                default:
//                    System.out.println("Invalid choice!");
//                    break;
//            }
//        } while (choice != 3);
