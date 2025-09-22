package org.example.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern ACCOUNT_ID_PATTERN = Pattern.compile("^BK-\\d{4}-[A-Z]{4}$");


    public static boolean isValidEmail(String email){
         return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password){
        return password != null && password.length() >= 6;
    }

    public static boolean isValidAmount(BigDecimal amount){
        return amount != null &&
                       amount.compareTo(BigDecimal.ZERO) > 0 &&
                       amount.scale() <= 2;
    }

    public static boolean isValidAccountId(String accountId){
          return accountId != null && ACCOUNT_ID_PATTERN.matcher(accountId).matches();
    }

}
