package ERD;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ERD {

    public void main() throws FileNotFoundException {
        int columnCount=0;
        int numberOfColumns=0;
        String table=null;
        System.out.println("Enter database name to generate ERD:");
        Scanner sc = new Scanner(System.in);
        String currentDatabase=sc.nextLine();
        String path = Path.of(currentDatabase, "meta.txt").toString();
        PrintWriter pw = new PrintWriter("ERD.txt");
        pw.close();
        try {
            FileReader fileReader=new FileReader(path);
            BufferedReader bufferedReader=new BufferedReader(fileReader);
            String data=bufferedReader.readLine();

            while(data!=null) {

                String line[]=data.split("[|]");
                table=line[0];

                if(Objects.equals(line[0], table)) {
                    columnCount += 1;
                    System.out.println(table+" "+columnCount);
                    data = bufferedReader.readLine();

                    if (data != null) {
                        String nextline[] = data.split("[|]");
                        if(!Objects.equals(nextline[0], table)){
                            numberOfColumns=columnCount;
                            System.out.println("table:"+table+"columns:"+numberOfColumns);
                            display(currentDatabase,table,numberOfColumns);
                            System.out.println("display called\n");
                            columnCount=0;
                        }
                        line[0] = nextline[0];
                    }

                }
            }
            numberOfColumns=columnCount;
            System.out.println("table:"+table+"columns:"+numberOfColumns);
            display(currentDatabase,table,numberOfColumns);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void display(String currentDatabase,String table, int numberOfColumns) {
        String path = Path.of(currentDatabase, "meta.txt").toString();
        try {
            FileReader fileReader=new FileReader(path);
            BufferedReader bufferedReader=new BufferedReader(fileReader);
            String data=bufferedReader.readLine();

            File file=new File("ERD.txt");
            FileWriter fileWriter=new FileWriter(file,true);

            fileWriter.write("\nTABLE:"+table);
            fileWriter.write("\nCOLUMNS:");
            List<String> columns=new ArrayList<>();
            while(data!=null) {

                String line[]=data.split("[|]");
                if(Objects.equals(line[0], table)) {
                    columns.add(line[1]);
                }
                data=bufferedReader.readLine();

            }
            for(String column:columns){
                fileWriter.write(column+",");
            }

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

