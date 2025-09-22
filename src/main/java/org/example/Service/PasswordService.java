package org.example.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordService {

    public static String hashPassword(String password){
            try{
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashedPassword = md.digest(password.getBytes());
                return Base64.getEncoder().encodeToString(hashedPassword);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException( "fialed to hash password" ,e);
            }
    }

    public static boolean verifyPassword(String password , String hashedPassword){
      return  hashPassword(password).equals(hashedPassword);
    }

}
