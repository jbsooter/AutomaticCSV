import java.io.File;
import java.util.ArrayList;

public interface ParseCSV {
    public void profileCSV();

    public <structureType> ArrayList<structureType> readCSV(Class structureClass);
    //public ArrayList<Object> readCSV(File fileName);

    //public void writeCSV(String fileName);

    //public void writeCSV(File fileName);


}
