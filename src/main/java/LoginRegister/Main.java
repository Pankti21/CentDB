package LoginRegister;

import QueryProcessor.QueryProcessor;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) throws IOException, NoSuchAlgorithmException {
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
                if(login.isAuthenticated){
                    accessCentDb();
                }
                break;
            case 3:
                QueryProcessor queryProcessor=new QueryProcessor();
                queryProcessor.handleQuery();
                break;
            default:
                System.out.println("Please enter a valid choice");
        }
    }

    private static void accessCentDb() throws IOException {
        int choice;
        Scanner sc=new Scanner(System.in);
        System.out.println("1. Write Queries");
        System.out.println("Enter a valid choice:");
        choice=sc.nextInt();
        switch (choice){
            case 1:
                QueryProcessor queryProcessor=new QueryProcessor();
                queryProcessor.handleQuery();
                break;
            default:
                System.out.println("Please enter a valid choice");
        }
    }
}
