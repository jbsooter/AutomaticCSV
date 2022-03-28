import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.text.Annotation;
import java.util.ArrayList;

/**
 * Class to write an ArrayList of objects to a CSV file.
 * NOTE: Does not support CSV "cells" containing commas at this time.
 */
public class AutoWriteCSV implements WriteCSV{
    private String csvFilePath;

    private File csvFileObject;


    public AutoWriteCSV() {
    }

    /**
     * Create instance with String representation of the filepath to which you want to write the CSV file.
     * @param csvFilePath filepath of location to write CSV to.
     */
    public AutoWriteCSV(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        this.csvFileObject = new File(csvFilePath);
    }

    /**
     * Create instance with File representation of the filepath to which you want to write the CSV file.
     * @param csvFileObject filepath of location to write CSV to.
     */
    public AutoWriteCSV(File csvFileObject) {
        this.csvFileObject = csvFileObject;
        this.csvFilePath = csvFileObject.toString();
    }

    /**
     * Writes the provided ArrayList of objects to a CSV file specified by the csvFileObject and csvFilePath fields.
     * NOTE: Does not support CSV "cells" containing commas at this time.
     * @param objectsToWrite Objects to write to CSV file
     * @param <typeparam> Type of the objects to write to CSV file.
     * @throws IOException
     * @throws IllegalAccessException
     */
    public  <typeparam> void writeCSV(ArrayList<typeparam> objectsToWrite) throws IOException, IllegalAccessException {
        FileWriter writeToCSV = new FileWriter(csvFileObject);

        Field[] fieldsToWrite = objectsToWrite.get(0).getClass().getDeclaredFields();

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
                if(fieldsToWrite[i].getDeclaredAnnotations().length > 0)
                {
                    if(!fieldsToWrite[i].getDeclaredAnnotations()[0].annotationType().isAnnotationPresent(CSVField.class))
                    {
                        continue;
                        //if field is not annotated with @CSVField(), do not write it to file. Protects against unsupported datatypes.
                    }

                }
                else
                {
                    continue;
                    //if field is not annotated at all, do not write it to file. Protects against unsupported datatypes.
                }

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
