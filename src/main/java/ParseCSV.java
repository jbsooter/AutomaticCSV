import java.util.ArrayList;

public interface ParseCSV {

    ArrayList<Object> profileCSV();

    <structureType> ArrayList<structureType> readCSV(Class CSVClass);

}
