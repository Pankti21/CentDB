package LoginRegister;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class Login {
    String userID;
    String password;
    String hashedUserId;
    String hashedPassword;
    Boolean isAuthenticated=false;

    void login() throws NoSuchAlgorithmException, IOException {
        System.out.println("======= LOGIN ========");
        Register register=new Register();
        userID=register.getUserIDFromUser();
        password=register.getPasswordFromUser();
        hashedUserId=register.generateHash(userID);
        hashedPassword=register.generateHash(password);
        if(checkIfUserExists()){
            System.out.println("\nWelcome to CentDB!");
            isAuthenticated=true;
        }
        else{
            System.out.println("You need to register first.");
            register.register();
        }
    }

    private boolean checkIfUserExists() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(Path.of("UserProfile.txt").toString()));
        String line = reader.readLine();
        while (line != null) {
            String[] credentials=line.split("[|]");
            if(credentials[0].equals(hashedUserId) && credentials[1].equals(hashedPassword)){
                return true;
            }
            line=reader.readLine();
        }
        return false;
    }

}
