package LoginRegister;

import ERD.ERD;
import QueryProcessor.QueryProcessor;
import SqlDump.SqlDump;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) throws IOException, NoSuchAlgorithmException {
        int choice;
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Forgot Password");
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
                Recovery recovery=new Recovery();
                recovery.forgotPassword();
            default:
                System.out.println("Please enter a valid choice");
        }
    }

    public static void accessCentDb() throws IOException {
        int choice;
        Scanner sc=new Scanner(System.in);
        System.out.println("1. Write Queries");
        System.out.println("4. Generate ERD");
        System.out.println("5. Generate SQL Dump");
        System.out.println("Enter a valid choice:");
        choice=sc.nextInt();
        switch (choice){
            case 1:
                QueryProcessor queryProcessor=new QueryProcessor();
                queryProcessor.handleQuery();
                break;
            case 4:
                 ERD erd=new ERD();
                 erd.main();
                 break;
            case 5:
                SqlDump sqlDump = new SqlDump();
                sqlDump.mainGenerator();
                break;

            default:
                System.out.println("Please enter a valid choice");
        }
    }
}
