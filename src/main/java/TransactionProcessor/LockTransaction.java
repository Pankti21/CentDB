package TransactionProcessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

    public class LockTransaction {

    private LockTransaction() throws IOException {}

    public static Date currenttime = new Date(System.currentTimeMillis());

    public static File file = new File("TransactionLocklog.txt");
    public static BufferedWriter output;

    static {
        try {
            output = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final Map<String, LockBase> getlocks =
            new HashMap<>();

    public enum  LockBase{
        MULTIPLE,
        SINGLE,
    }

    public static void SingleTransactionLock( String dbName, String table) throws IOException {
        if (table == null) {
            String dbdirectory = dbName + "." + "null";
            LockTransaction.LockBase locktechnique = LockTransaction.getlocks.get(dbdirectory);
            if (locktechnique == null) {
                LockTransaction.getlocks.put(dbdirectory, LockTransaction.LockBase.SINGLE);
            } else {
                System.out.println("Database of Name: " + dbName + "is locked!, Time: "+currenttime+"\n");
                output.write("Database of Name: " + dbName + "is locked!, Time: "+currenttime+"\n");
                output.close();
            }

        } else {
            String directory = dbName + "." + table;
            LockTransaction.LockBase locktechnique = LockTransaction.getlocks.get(directory);
            if (locktechnique == null) {
                LockTransaction.getlocks.put(directory, LockTransaction.LockBase.SINGLE);
            } else {
                System.out.println("Table: " + table + "is locked!");
                output.write("Table: " + table + "is locked!");
                output.close();
            }
        }
    }

    public static void SingleTransactionrelease(String dbName, String table) throws IOException {
        if (table == null) {
            String dbdirectory = dbName + "." + "null";
            LockTransaction.getlocks.remove(dbdirectory);
            System.out.println("Database: " + dbName + "is succesfully created! "+currenttime+"\n");
            output.write("Database: " + dbName + "is succesfully created! "+currenttime+"\n");
            output.close();
        } else {
            String tbdirectory = dbName + "." + table;
            LockTransaction.getlocks.remove(tbdirectory);
            System.out.println("Table: " + table + "is succesfully created! "+currenttime+"\n");
            output.write("Table: " + table + "is succesfully created! "+currenttime+"\n");
            output.close();
        }
    }

    public static int TransactionCount = 0;

    public static void MultipleTransactionsLock(String dbName, String table) throws IOException {

        String dbdirectory = dbName + "." + "null";
        LockTransaction.LockBase multiple = LockTransaction.getlocks.get(dbdirectory);
        if (multiple != null) {
            System.out.println("Database of Name: " + dbName + "is locked!, Time: "+currenttime+"\n");
            output.write("Database of Name: " + dbName + "is locked!, Time: "+currenttime+"\n");
            output.close();
        }
        String tbdirectory = dbName + "." + table;
        LockTransaction.LockBase single = LockTransaction.getlocks.get(tbdirectory);
        if (single == LockTransaction.LockBase.SINGLE) {
            System.out.println("Table: " + table + "not available for transaction., Time: "+currenttime+"\n");
            output.write("Table: " + table + "not available for transaction., Time: "+currenttime+"\n");
            output.close();
        }
        if (single == null) {
            LockTransaction.getlocks.put(tbdirectory, LockTransaction.LockBase.MULTIPLE);
            LockTransaction.TransactionCount++;
        }
        if (single == LockTransaction.LockBase.MULTIPLE) {
            LockTransaction.TransactionCount++;
        }
    }

    public static void MultipleTransactionsrelease(String dbName, String table) throws IOException {
        String tbdirectory = dbName + "." + table;
        LockTransaction.TransactionCount--;
        if (LockTransaction.TransactionCount == 0) {
            LockTransaction.getlocks.remove(tbdirectory);
            System.out.println("Database: " + dbName + "and table: "+table+"is succesfully created! "+currenttime+"\n");
            output.write("Database: " + dbName + "and table: "+table+"is succesfully created! "+currenttime+"\n");
            output.close();
        }
    }
}
