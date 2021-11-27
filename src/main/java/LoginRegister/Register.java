package LoginRegister;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Register {
    String userID;
    String password;
    final String SQ1= "What is your favourite animal?";
    String SA1;
    final String SQ2= "What is your middle name?";
    String SA2;

    public Register(){
    }

    String getUserIDFromUser(){
        System.out.println("Enter a User ID:");
        Scanner sc = new Scanner(System.in);
        userID=sc.nextLine();
        return userID;
    }

    void getSecurityAnswers(){
        System.out.println(SQ1);
        Scanner sc = new Scanner(System.in);
        SA1=sc.nextLine();
        System.out.println(SQ2);
        SA2=sc.nextLine();
    }

    String getPasswordFromUser(){
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

    boolean checkUserIdExists(){
        //if it exists in file
        //System.out.println("User ID already exists!");
        return false;
    }

    void writeToFile(String content) throws IOException {
        try {
            File UserProfileLog = new File("UserProfile.txt");
            BufferedWriter writer=new BufferedWriter(new FileWriter(UserProfileLog));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void register() throws IOException {
        System.out.println("In register");
        userID = getUserIDFromUser();
        while (checkUserIdExists()){
            getUserIDFromUser();
        }
        password=getPasswordFromUser();
        while (!checkValidPassword(password)){
            getPasswordFromUser();
        }
        getSecurityAnswers();
        //Add userID password and security answers to file
        writeToFile(userID+"//"+password+"//"+SA1+"//"+SA2);
    }

}

