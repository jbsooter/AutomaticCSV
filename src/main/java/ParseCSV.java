import java.util.ArrayList;

public interface ParseCSV {

    <csvClass> ArrayList<csvClass> profileCSV();

    <structureType> ArrayList<structureType> readCSV(Class CSVClass);

}
