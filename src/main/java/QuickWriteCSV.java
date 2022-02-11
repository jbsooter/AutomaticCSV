import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class QuickWriteCSV implements WriteCSV{
    private String csvFilePath;

    private File csvFileObject;


    public QuickWriteCSV() {
    }

    public QuickWriteCSV(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        this.csvFileObject = new File(csvFilePath);
    }

    public QuickWriteCSV(File csvFileObject) {
        this.csvFileObject = csvFileObject;
        this.csvFilePath = csvFileObject.toString();
    }

    public  <typeparam> void writeCSV(ArrayList<typeparam> objectsToWrite) throws IOException, IllegalAccessException {
        FileWriter writeToCSV = new FileWriter(csvFileObject);

        Field[] fieldsToWrite = objectsToWrite.get(0).getClass().getDeclaredFields();

        System.out.println(fieldsToWrite[0].toString());

        for(int i = 0; i < fieldsToWrite.length; i++)
        {
            fieldsToWrite[i].setAccessible(true);
            if(i < fieldsToWrite.length - 1)
            {
                writeToCSV.write(fieldsToWrite[i].getName() + ",");
            }
            else
            {
                writeToCSV.write(fieldsToWrite[i].getName() + "\n");
            }
        }

        for(Object o: objectsToWrite)
        {
            for(int i = 0; i < fieldsToWrite.length; i++)
            {
                if(i < fieldsToWrite.length - 1)
                {
                    writeToCSV.write(fieldsToWrite[i].get(o).toString() + ",");
                }
                else
                {
                    writeToCSV.write(fieldsToWrite[i].get(o).toString() + "\n");
                }
            }
        }

        writeToCSV.close();

    }
}
