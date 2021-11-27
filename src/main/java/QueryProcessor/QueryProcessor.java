package QueryProcessor;

import QueryValidator.QueryValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class QueryProcessor {
    public void handleQuery () throws IOException {
        Scanner sc = new Scanner(System.in);
        String input = "";

        // infinite input loop
        while (true) {
            String currentInput;
            if (input.length() == 0) {
                System.out.printf("%s> ", "sql");
            }

            // take the input from user
            // trim removes extra spaces leading or following the input
            currentInput = sc.nextLine().trim();

            // if user enters exit, leave the loop
            if (input.length() == 0 && currentInput.equals("exit")) {
                System.out.println("Query processor closed.");
                break;
            } else if (currentInput.equals("")) {
                // if the user entered empty string, continue again
                continue;
            }

            String lastCharacter = currentInput.substring(currentInput.length() - 1);
            if (!lastCharacter.equals(";")) {
                input = input.concat(" " + currentInput);
                continue;
            } else if (input.length() > 0) {
                input = input.concat(currentInput.substring(0, currentInput.length() - 1));
            } else {
                input = currentInput.substring(0, currentInput.length() - 1);
            }

            // create a list from input string list split by ' ' (space)
            input = input.replaceAll("[(]", " ( ");
            input = input.replaceAll("[)]", " ) ");

            List<String> inputChunks = new ArrayList<>(Arrays.asList(input.trim().split(" ")));

            // remove extra elements created due to multiple spaces
            inputChunks.removeAll(Arrays.asList("", null));

            // store the cleaned input string back
            input = String.join(" ", inputChunks);

            if (inputChunks.size() == 1) {
                System.out.println("Too few arguments. Invalid query.");
            }

            String queryType = inputChunks.get(0).toLowerCase();

            switch (queryType) {
                case "create":
                    System.out.println("In create");
                    break;

                default:
                    System.out.println("Not a valid query.");
                    break;
            }

            // reset the input
            input = "";
        }
    }
}
