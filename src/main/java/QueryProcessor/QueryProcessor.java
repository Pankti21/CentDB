package QueryProcessor;

import QueryValidator.QueryValidator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class QueryProcessor {
    // path of global data dictionary
    public static int varcharMaxLength = 8000;

    /**
     * {
     *   tableName: [
     *     { name: "", type: "", size: "", pk: "" }
     *   ]
     * }
     */
    public static HashMap<String, List<HashMap<String, String>>> tablesMetaData = new HashMap<>();

    /**
     * {
     *   tableName: [
     *     { columnName: value }
     *   ]
     * }
     */
    public static HashMap<String, List<HashMap<String, String>>> tableRows = new HashMap<>();

    public static HashMap<String, String> tableColumnsOrder = new HashMap<>();

    public String currentDatabase = "";

    public void resetDatabaseState () {
        currentDatabase = "";
        tablesMetaData = new HashMap<>();
        tableRows = new HashMap<>();
        tableColumnsOrder = new HashMap<>();
    }

    // create database directory
    public void createDatabase (String dbName) throws IOException {
        Files.createDirectory(Path.of(dbName));
        if (QueryValidator.checkIfDBExists(dbName)) {
            Files.createFile(Path.of(dbName, "meta.txt"));
            if (QueryValidator.checkIfDbHasMeta(dbName)) {
                System.out.printf("Database %s created successfully.\n", dbName);
            } else {
                System.out.printf("Database %s created successfully. Meta file creation failed.\n", dbName);
            }
        } else {
            System.out.println("Database creation failed.");
        }
    }

    // get data from db's meta file
    public void parseMetaDataOfTable () {
        tablesMetaData = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Path.of(currentDatabase, "meta.txt").toString()));
            String line = reader.readLine();
            while (line != null) {
                String[] splitLine = line.split("\\|");
                List<String> lineData = new ArrayList<>(Arrays.asList(splitLine));

                HashMap<String, String> lineDataMap = new HashMap<>();

                // get the table name
                String table = lineData.get(0);
                lineData.remove(0);

                // get the column name and save it in a map
                String column = lineData.get(0);
                lineDataMap.put("name", column);
                lineData.remove(0);


                // get the column data type and save it in a map
                String type = lineData.get(0);
                lineDataMap.put("type", type);
                lineData.remove(0);


                // if the type is varchar, get and save its length in a map
                String length;
                if (type.equals("varchar")) {
                    length = lineData.get(0);
                    lineDataMap.put("size", length);
                    lineData.remove(0);
                }

                String fkTableName = "";
                String fkFieldName = "";

                // check if the column is a primary key
                if (lineData.size() > 0 && lineData.get(0).equals("pk")) {
                    lineDataMap.put("pk", "true");
                    lineData.remove(0);
                }

                if (lineData.size() >= 3 && lineData.get(0).equals("fk")) {
                    fkTableName = lineData.get(1);
                    fkFieldName = lineData.get(2);

                    if (fkTableName.length() > 0 && fkFieldName.length() > 0) {
                        lineDataMap.put("fk", "true");
                        lineDataMap.put("fkTableName", fkTableName);
                        lineDataMap.put("fkFieldName", fkFieldName);
                        lineData.remove(0);
                        lineData.remove(0);
                        lineData.remove(0);
                    }
                }

                if (tablesMetaData.get(table) != null && tablesMetaData.get(table).size() > 0) {
                    // if a list of meta already exists for this table
                    List<HashMap<String, String>> data = new ArrayList<>(tablesMetaData.get(table));
                    data.add(lineDataMap);
                    tablesMetaData.put(table, data);
                } else {
                    // create and insert new list as it doesn't exist already
                    tablesMetaData.put(table, List.of(lineDataMap));
                }

                // read next line
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // save a table's info to its meta.txt
    public void saveTableDataToMetaFile () {
        try {
            // write to file
            String path = Path.of(currentDatabase, "meta.txt").toString();
            FileWriter fw = new FileWriter(path);
            PrintWriter writer = new PrintWriter(fw);

            for (Map.Entry<String, List<HashMap<String, String>>> table: tablesMetaData.entrySet()) {
                String tableName = table.getKey();
                List<HashMap<String, String>> tableColumns = table.getValue();
                String output = tableName;

                for (HashMap<String, String> column : tableColumns) {
                    String colName = column.get("name");
                    String colType = column.get("type");
                    String colSize = column.get("size");
                    String colPk = column.get("pk");
                    String colFk = column.get("fk");
                    String colFkTable = column.get("fkTableName");
                    String colFkField = column.get("fkFieldName");

                    output = output.concat("|").concat(colName);
                    output = output.concat("|").concat(colType);
                    if (colType.equals("varchar")) output = output.concat("|").concat(colSize);
                    if (colPk != null && colPk.equals("true")) output = output.concat("|pk");

                    if (colFk != null && colFk.equals("true")) {
                        output = output.concat("|fk|").concat(colFkTable).concat("|").concat(colFkField);
                    }

                    writer.println(output);
                    output = tableName;
                }
            }

            writer.close();

            System.out.println("Table created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // iterate through create table query to validate and create table
    // sample query: create table tName ( id int primary key, name varchar(255) );
    public void parseCreateTableQuery (List<String> queryChunks) throws IOException {
        // check if a database is selected
        if (currentDatabase.length() == 0) {
            System.out.println("No database selected.");
            return;
        }

        List<String> queryList = new ArrayList<>(queryChunks);
        // removes the 'create' token
        queryList.remove(0);
        // removes the 'table' token
        queryList.remove(0);

        // stores and removes the 'table name' token
        String tableName = queryList.get(0);
        queryList.remove(0);

        // verifies if the table already exists
        if (tablesMetaData.containsKey(tableName)) {
            System.out.println("Invalid query. Table already exists.");
            return;
        }

        if (queryList.get(0).equals("(") && queryList.get(queryList.size() - 1).equals(")")) {
            // remove '(' from query
            queryList.remove(0);
            // remove last ')' from  query
            queryList.remove(queryList.size() - 1);

            List<String> columns = List.of(String.join(" ", queryList).split(","));

            List<HashMap<String, String>> parsedColumnsMap = new ArrayList<>();
            List<String> columnNameList = new ArrayList<>();

            for (String column : columns) {
                List<String> columnData = new ArrayList<>(List.of(column.trim().split(" ")));
                if (columnData.size() < 2) {
                    System.out.println("Invalid query.");
                    return;
                }

                HashMap<String, String> columnMap = new HashMap<>();

                String columnName = columnData.get(0);
                columnMap.put("name", columnName);
                columnNameList.add(columnName);
                columnData.remove(0);

                String columnType = columnData.get(0);
                columnMap.put("type", columnType);
                columnData.remove(0);

                int columnLength;
                String foreignKeyReferenceTable;
                String foreignKeyReferenceField;

                // get varchar field max size if the type is varchar
                if (columnType.equals("varchar")) {
                    if (columnData.size() >= 3 && columnData.get(0).equals("(") && columnData.get(1).matches("\\d+") && columnData.get(2).equals(")")) {
                        columnLength = Integer.parseInt(columnData.get(1));
                    } else {
                        columnLength = varcharMaxLength;
                    }

                    columnMap.put("size", Integer.toString(columnLength));
                } else if (!columnType.equals("int")) {
                    System.out.println("Invalid query.");
                }

                if (columnData.size() == 0) {
                    // no more column data other than field name and type.
                    parsedColumnsMap.add(columnMap);
                    continue;
                }

                for (int i = 0; i < columnData.size(); i++) {
                    String currentToken = columnData.get(i).toLowerCase();
                    String nextToken = null;

                    if (i + 1 < columnData.size()) nextToken = columnData.get(i + 1).toLowerCase();

                    if (currentToken.equals("primary") && nextToken != null && nextToken.equals("key")) {
                        columnMap.put("pk", "true");
                        i++;
                        continue;
                    }

                    if (currentToken.equals("foreign") && nextToken != null && nextToken.equals("key")) {
                        i++;
                        // <table name> ( <column name> )
                        if (i + 4 < columnData.size()) {
                            String tableNameTemp = columnData.get(i+1);
                            String tableFieldTemp = columnData.get(i+3);

                            if (QueryValidator.validateForeignKeyReference(currentDatabase, columnType, tableNameTemp, tableFieldTemp, tablesMetaData)) {
                                columnMap.put("fk", "true");
                                columnMap.put("fkTableName", tableNameTemp);
                                columnMap.put("fkFieldName", tableFieldTemp);
                                continue;
                            } else {
                                return;
                            }
                        } else {
                            System.out.println("Invalid query. No reference given for foreign key.");
                            return;
                        }
                    }
                }

                parsedColumnsMap.add(columnMap);
            }

            tablesMetaData.put(tableName, parsedColumnsMap);
            saveTableDataToMetaFile();
            Files.createFile(Path.of(currentDatabase, tableName+ ".txt"));

            String columnNamesAsString = String.join("|", columnNameList);
            Files.write(Path.of(currentDatabase, tableName+ ".txt"), columnNamesAsString.getBytes());
            tableColumnsOrder.put(tableName, columnNamesAsString);
        } else {
            System.out.println("Invalid query.");
        }
    }

    // read and store all tables data
    public void readAllTablesData () {
        for (Map.Entry<String, List<HashMap<String, String>>> entry: tablesMetaData.entrySet()) {
            String tableName = entry.getKey();
            List<HashMap<String, String>> columnsInMetaData = entry.getValue();

            List<String> columnNamesInMetaData = columnsInMetaData.stream().map(v -> v.get("name")).collect(Collectors.toList());

            Path tableFilePath = Path.of(currentDatabase, tableName + ".txt");

            if (!Files.exists(tableFilePath)) {
                System.out.println("Table " + tableName + " does not exist on disk. Meta file must be corrupt.");
                return;
            }

            try {
                BufferedReader reader = new BufferedReader(new FileReader(tableFilePath.toString()));
                String line = reader.readLine();

                if (line.length() == 0) {
                    System.out.println("Table " + tableName + " does not contain header row.");
                    return;
                }

                // check if columns in header match metadata
                List<String> columns = List.of(line.split("\\|"));
                for (String column: columns) {
                    if (!columnNamesInMetaData.contains(column)) {
                        System.out.println("Table " + tableName + " has a column not matching in metadata.");
                        resetDatabaseState();
                        return;
                    }
                }

                // save the column order for this table
                tableColumnsOrder.put(tableName, line);

                // read next line
                line = reader.readLine();

                // list of all rows
                List<HashMap<String, String>> rows = new ArrayList<>();

                // map column name to column meta data for ease of access
                HashMap<String, HashMap<String, String>> columnMeta = new HashMap<>();
                columnsInMetaData.forEach(column -> columnMeta.put(column.get("name"), column));

                // loop till the end of file
                while (line != null) {
                    List<String> rowData = Arrays.asList(line.split("\\|", -1));

                    if (rowData.size() != columns.size()) {
                        System.out.println("Table " + tableName + " has a corrupt row.");
                        resetDatabaseState();
                        return;
                    }

                    // data of current row
                    HashMap<String, String> rowMap = new HashMap<>();

                    for (int index  = 0; index < columns.size(); index++) {
                        String data = rowData.get(index);
                        String column = columns.get(index);

                        HashMap<String, String> currentColumn = columnMeta.get(column);
                        boolean valid = true;

                        // verify data type
                        if (data.length() != 0 && currentColumn.get("type").equals("varchar")) {
                            String size = currentColumn.get("size");
                            if (data.length() > Integer.parseInt(size)) {
                                valid = false;
                            }
                        } else if (data.length() != 0 && currentColumn.get("type").equals("int")) {
                            if (!data.matches("\\d+")) {
                                valid = false;
                            }
                        }

                        // verify uniqueness if it is primary key
                        if (valid && currentColumn.get("pk") != null && currentColumn.get("pk").equals("true")) {
                            valid = rows.stream().noneMatch(row -> row.get(column).equals(data));
                        }

                        if (!valid) {
                            System.out.println("Table " + tableName + " has a corrupt row.");
                            resetDatabaseState();
                            return;
                        } else {
                            // store in list of maps
                            rowMap.put(column, data);
                        }
                    }

                    rows.add(rowMap);

                    // read next line
                    line = reader.readLine();
                }

                tableRows.put(tableName, rows);
            } catch (IOException e) {
                e.printStackTrace();
                resetDatabaseState();
            }
        }
    }

    // returns a column meta info from a table
    public HashMap<String, String> getColumnMetaInfo (String tableName, String columnName) {
        for(HashMap<String, String> column: tablesMetaData.get(tableName)) {
            if (column.get("name").equals(columnName)) {
                return column;
            }
        }

        System.out.println("No column named " + columnName + " found in table " + tableName);
        return new HashMap<>();
    }

    // sample query: insert into tName (id, name) values (1, myname)
    public void parseInsertValueInTableQuery (List<String> queryChunks) {
        // check if no database is selected
        if (currentDatabase.length() == 0) {
            System.out.println("No database selected.");
            return;
        }

        List<String> queryList = new ArrayList<>(queryChunks);

        // remove the 'insert' token
        queryList.remove(0);
        // remove the 'into' token
        queryList.remove(0);
        // save the table name and remove token
        String tableName = queryList.remove(0);

        String queryWithoutPrecedingTokens = String.join(" ", queryList);
        String[] columnsAndValuesFromQuery = queryWithoutPrecedingTokens.split("[Vv][Aa][Ll][Uu][Ee][Ss]");

        if (columnsAndValuesFromQuery.length != 2) {
            System.out.println("Invalid query. Length");
            return;
        }

        List<String> columns = QueryValidator.validateAndCreateListFromRoundBracketValues(columnsAndValuesFromQuery[0]);
        List<String> values = QueryValidator.validateAndCreateListFromRoundBracketValues(columnsAndValuesFromQuery[1]);

        if (values.size() == 0) {
            return;
        }

        List<String> tableColumnsInOrder =  new ArrayList<>(List.of(tableColumnsOrder.get(tableName).split("\\|")));

        // if no columns are given in the query, use all columns
        boolean useAllColumns = columns.size() == 0;

        // store the valid data to this hashmap to insert into table rows
        HashMap<String, String> newRowData = new HashMap<>();

        // index to keep track of values while looping through columns
        int idx = 0;
        for (String column: tableColumnsInOrder) {
            if (!useAllColumns && !columns.contains(column)) {
                newRowData.put(column, "");
                continue;
            }

            String data = useAllColumns ? values.get(idx) : values.get(columns.indexOf(column));
            HashMap<String, String> columnMetaData = getColumnMetaInfo(tableName, column);
            if (QueryValidator.validateDataAsPerColumnMeta(tableName, columnMetaData, data, tableRows)) {
                newRowData.put(column, data);
                idx++;
            } else {
                return;
            }
        }

        // add the validated data to the list of rows for this table
        List<HashMap<String, String>> newTableData = tableRows.get(tableName) != null ? new ArrayList<>(tableRows.get(tableName)) : new ArrayList<>();
        newTableData.add(newRowData);
        tableRows.put(tableName, newTableData);

        persistTableDataToDisk(tableName);
        System.out.println("Record added successfully.");
    }

    // write the contents of tableRows to table files
    public void persistTableDataToDisk (String tableName) {
        String currentTableColumnsOrder = tableColumnsOrder.get(tableName);
        List<String> tableColumnsInOrder =  new ArrayList<>(List.of(currentTableColumnsOrder.split("\\|")));

        try {
            // write to file
            String path = Path.of(currentDatabase, tableName + ".txt").toString();
            FileWriter fw = new FileWriter(path);
            PrintWriter writer = new PrintWriter(fw);
            List<HashMap<String, String>> currentTableRows = tableRows.get(tableName);

            // output the header line
            writer.println(currentTableColumnsOrder);

            for (HashMap<String, String> row: currentTableRows) {
                String output = "";
                int index = 0;
                for(String column: tableColumnsInOrder) {
                    if (row.containsKey(column)) {
                        output = output.concat(row.get(column));
                    }

                    // if it is not the last column, append a '|' delimeter
                    if (index != tableColumnsInOrder.size() - 1) {
                        output = output.concat("|");
                    }

                    index++;
                }

                writer.println(output);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleQuery () throws IOException {
        Scanner sc = new Scanner(System.in);
        String input = "";

        // infinite input loop
        while (true) {
            String currentInput;
            if (input.length() == 0) {
                System.out.printf("%s> ", currentDatabase.length() > 0 ? currentDatabase : "sql");
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
            } else if (currentInput.equals("print data")) {
                System.out.println(tableRows);
                continue;
            } else if (currentInput.equals("print meta")) {
                System.out.println(tablesMetaData);
                continue;
            }

            String lastCharacter = currentInput.substring(currentInput.length() - 1);
            //For multiline query input
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

            if (inputChunks.size() == 1) {
                System.out.println("Too few arguments. Invalid query.");
            }

            String queryType = inputChunks.get(0).toLowerCase();

            switch (queryType) {
                case "create":
                    String createQueryType = inputChunks.get(1).toLowerCase();
                    if (createQueryType.equals("database") && QueryValidator.validateCreateDatabaseQuery(inputChunks)) {
                        // query is for creating database and is validated
                        createDatabase(inputChunks.get(2));
                    } else if (createQueryType.equals("table")) {
                        parseCreateTableQuery(inputChunks);
                    }
                    break;

                case "use":
                    if (QueryValidator.validateUseDatabaseQuery(inputChunks)) {
                        currentDatabase = inputChunks.get(1);
                        parseMetaDataOfTable();
                        readAllTablesData();
                    }
                    break;

                case "insert":
                    String secondToken = inputChunks.get(1).toLowerCase();
                    if (secondToken.equals("into")) {
                        parseInsertValueInTableQuery(inputChunks);
                    }
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
