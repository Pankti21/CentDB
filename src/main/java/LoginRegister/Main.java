package LoginRegister;

import ERD.ERD;

import QueryProcessor.QueryProcessor;

import java.io.*;
import java.util.Scanner;

import Analytics.BasicAnalytics;

public class Main {
    public static void main(String args[]) throws IOException {
        int choice;
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Write Queries");
        System.out.println("4. Generate ERD");
        System.out.println("5. Analytics");
        String activeUserName = "";
        
        Scanner sc=new Scanner(System.in);
        while(true) {
        System.out.println("\nEnter a valid choice:");
        choice=sc.nextInt();
            
        switch (choice){
            case 1:
                Register register=new Register();
                register.register();
            case 2:
                Login login=new Login();
                activeUserName = login.login();
                break;
            case 3:
                QueryProcessor queryProcessor=new QueryProcessor();
                queryProcessor.handleQuery();
                break;
            case 4:
                 ERD erd=new ERD();
                 erd.main();
            case 5:
            	BasicAnalytics.main(new String[] {activeUserName});
            	break;
            default:
                System.out.println("Please enter a valid choice");
        }
        if(choice==0) {
        	System.out.println("Exiting....");
        	break;
        }
        }
    }
}
