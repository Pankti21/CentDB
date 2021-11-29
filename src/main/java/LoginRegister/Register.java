package LoginRegister;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.xml.bind.DatatypeConverter;

public class Register {
    String userID;
    String password;
    final String SQ1= "What is your favourite animal?";
    String SA1;
    final String SQ2= "What is your middle name?";
    String SA2;

    public Register(){
    }

    String getUserIDFromUser() throws NoSuchAlgorithmException {
        System.out.println("Enter a User ID:");
        Scanner sc = new Scanner(System.in);
        userID=sc.nextLine();
        return userID;
    }

    private String generateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();
        String hash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        return hash;
    }

    void getSecurityAnswers(){
        System.out.println(SQ1);
        Scanner sc = new Scanner(System.in);
        SA1=sc.nextLine();
        System.out.println(SQ2);
        SA2=sc.nextLine();
    }

    String getPasswordFromUser() throws NoSuchAlgorithmException {
        System.out.println("Enter a password:");
        Scanner sc = new Scanner(System.in);
        password=sc.nextLine();
        return password;
    }

    boolean checkValidPassword(String password) {
        if(password.length()<8){
            System.out.println("Password should be atleast 8 charaters long");
            return false;
        }
        return true;
    }

    boolean checkUserIdExists() throws IOException, NoSuchAlgorithmException {
        BufferedReader reader = new BufferedReader(new FileReader(Path.of("UserProfile.txt").toString()));
        String line = reader.readLine();
        while (line != null) {
            String[] credentials=line.split("[|]");
            System.out.println(generateHash(credentials[0]));
            if(generateHash(credentials[0]).equals(userID)){
                System.out.println("User already exists.");
                return true;
            }
            line=reader.readLine();
        }
        return false;
    }

    void writeToFile(String content) throws IOException {
        try {
            File UserProfileLog = new File("UserProfile.txt");
            FileWriter fileWriter=new FileWriter(UserProfileLog,true);
            PrintWriter printWriter=new PrintWriter(fileWriter);
            printWriter.println(content);
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void register() throws IOException, NoSuchAlgorithmException {
        System.out.println("In register");
        userID = getUserIDFromUser();
        while (checkUserIdExists()){
            System.out.println("1. Continue to login");
            System.out.println("1. Try a different username");
            System.out.println("Enter a valid choice: ");
            Scanner sc=new Scanner(System.in);
            int choice=sc.nextInt();
            switch (choice){
                case 1:
                    Login login=new Login();
                    login.login();
                    break;
                case 2:
                    getUserIDFromUser();
                default:
                    System.out.println("Invalid choice.");
            }
        }
        password=getPasswordFromUser();
        while (!checkValidPassword(password)){
            getPasswordFromUser();
        }
        getSecurityAnswers();
        String hashedUserId=generateHash(userID);
        String hashedPassword=generateHash(password);
        //Add hashed userID, hashed password and security answers to file
        writeToFile(hashedUserId+"|"+hashedPassword+"|"+SA1+"|"+SA2);
    }

}

