package LoginRegister;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) throws IOException {
        int choice;
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("Enter a valid choice:");
        Scanner sc=new Scanner(System.in);
        choice=sc.nextInt();
        switch (choice){
            case 1:
                Register register=new Register();
                register.register();
            case 2:
                Login login=new Login();
                login.login();
                break;
            default:
                System.out.println("Please enter a valid choice");
        }
    }
}
