package QueryValidator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class QueryValidator {

    // checks if the 'create database <dbname>' query is valid
    public static boolean validateCreateDatabaseQuery (List<String> queryChunks) {
        if (queryChunks.size() != 3) {
            System.out.println("Too many or few arguments provided in query.");
            return false;
        } else if (checkIfDBExists(queryChunks.get(2))) {
            System.out.println("Database already exists.");
            return false;
        }

        return true;
    }

    // checks if the database directory exists
    public static boolean checkIfDBExists (String dbName) {
        return Files.exists(Path.of(dbName));
    }

    // checks if the database directory has a meta.txt file
    public static boolean checkIfDbHasMeta (String dbName) {
        return Files.exists(Path.of(dbName, "meta.txt"));
    }

    // checks if the 'use <dbname>' query is valid
    public static boolean validateUseDatabaseQuery (List<String> queryChunks) {
        if (queryChunks.size() != 2) {
            System.out.println("Too many or few arguments provided in query.");
            return false;
        } else if (!checkIfDBExists(queryChunks.get(1))) {
            System.out.println("No such database exists.");
            return false;
        }

        return true;
    }

    // checks if a table file exists
    public static boolean validateTableFileExists (String dbName, String tableName) {
        return Files.exists(Path.of(dbName, tableName));
    }

    // validate if data is good to insert in a table
    public static boolean validateDataAsPerColumnMeta (
            HashMap<String, String> columnMeta,
            String data,
            List<HashMap<String, String>> rows
    ) {
        boolean valid = true;

        // if the column is pk, check if the same value exists elsewhere
        if (rows != null && columnMeta.containsKey("pk") && columnMeta.get("pk").equals("true")) {
            valid = !rows.stream().anyMatch(row -> row.get(columnMeta.get("name")).equals(data));
            if (!valid) {
                System.out.println("Invalid data. Duplicate primary key not allowed.");
            }
        }

        // check if data valid according to column data type
        if (valid && columnMeta.get("type").equals("varchar")) {
            String size = columnMeta.get("size");
            if (data.length() > Integer.parseInt(size)) {
                valid = false;
                System.out.println("Invalid data. Varchar of length " + size + " expected but received " + data);
            }
        } else if (valid && columnMeta.get("type").equals("int")) {
            if (!data.matches("\\d+")) {
                valid = false;
                System.out.println("Invalid data. Integer expected but received " + data);
            }
        }

        return valid;
    }

    // validate strings like '( value1, value2 )' and return a list of [value1, value2]
    public static List<String> validateAndCreateListFromRoundBracketValues (String raw) {
        List<String> columns = new ArrayList<>(List.of(raw.split(" ")));

        // remove all null parts
        columns.removeAll(Arrays.asList("", null));

        if (columns.size() > 1) {
            if (!columns.remove(0).equals("(") || !columns.remove(columns.size() - 1).equals(")")) {
                System.out.println("Invalid query.");
                return new ArrayList<>();
            }

            columns = new ArrayList<>(List.of(String.join("", columns).split(",")));

            return columns;
        } else {
            return new ArrayList<>();
        }
    }
}
