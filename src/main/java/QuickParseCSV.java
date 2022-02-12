import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class QuickParseCSV implements ParseCSV{

    private String  csvFilePath;

    private File csvFileObject;

    public QuickParseCSV()
    {

    }

    public QuickParseCSV(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        this.csvFileObject = new File(csvFilePath);
    }

    public QuickParseCSV(File csvFileObject) {
        this.csvFileObject = csvFileObject;
        this.csvFilePath = csvFileObject.toString();
    }

    public void profileCSV()  {
        String csvNameNoExtension = csvFilePath.substring(csvFilePath.lastIndexOf("/") + 1);
        csvNameNoExtension = csvNameNoExtension.substring(0, csvNameNoExtension.lastIndexOf("."));
        String csvClassName = csvNameNoExtension.substring(0,1).toUpperCase() + csvNameNoExtension.substring(1).toLowerCase();

        try
            {
                Class.forName(csvClassName);
                //end profileCSV if csv POJO class exists
                return;
            }catch(ClassNotFoundException ex)
            {
                System.out.println("Generating Java Class....");
            }

            ArrayList<ColumnCSV> columns = buildColumns();

            try {
                buildPOJO(columns);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Attempt to Build Class Failed. ");
            }
        }
        public <csvClass> ArrayList<csvClass> readCSV(Class csvClass)
        {
            ArrayList<csvClass> result = new ArrayList<csvClass>();
            ArrayList<ColumnCSV> columns = buildColumns();

            //iterate through all ColumnCSV objects for the number of data cells that they hold.
            for(int i = 0; i < columns.get(0).getColumnStringArray().size(); i++){
                //Holds Parameters to pass to csvClass constructor
                Object[] parameterization = new Object[columns.size()];
                //Holds Classes of datatypes to identify correct constructor to instantiate ColumnCSV
                Class[] datatypes = new Class[columns.size()];

                //Counter for parameterization and datatype index for row
                int p = 0;
                //Iterate through columnCSV objects and create parameterization/datatypes for csvClass "row"
                for(ColumnCSV col: columns)
                {
                    if(col.getColumnDataType().equals("Integer"))
                    {
                        parameterization[p] = Integer.parseInt(col.getColumnStringArray().get(i));
                        datatypes[p] = Integer.class;
                    }
                    else if(col.getColumnDataType().equals("Double"))
                    {
                        parameterization[p] = Double.parseDouble(col.getColumnStringArray().get(i));
                        datatypes[p] = Double.class;
                    }
                    else if(col.getColumnDataType().equals("LocalDateTime"))
                    {
                        parameterization[p] = LocalDateTime.parse(col.getColumnStringArray().get(i));
                        datatypes[p] = LocalDateTime.class;
                    }
                    else if(col.getColumnDataType().equals("LocalDate"))
                    {
                        try
                        {
                            parameterization[p] = LocalDate.parse(col.getColumnStringArray().get(i));
                            datatypes[p] = LocalDate.class;
                        }catch(DateTimeParseException exDt)
                        {
                            try
                            {
                                parameterization[p] = LocalDate.parse(col.getColumnStringArray().get(i), DateTimeFormatter.ofPattern("M/d/yyyy"));
                                datatypes[p] = LocalDate.class;
                            }catch(DateTimeParseException exDt2)
                            {
                                try
                                {
                                    parameterization[p] = LocalDate.parse(col.getColumnStringArray().get(i), DateTimeFormatter.ofPattern("M/d/yy"));
                                    datatypes[p] = LocalDate.class;
                                }catch(DateTimeParseException exDt3)
                                {
                                    try
                                    {
                                        parameterization[p] = LocalDate.parse(col.getColumnStringArray().get(i), DateTimeFormatter.ofPattern("M-d-yyyy"));
                                        datatypes[p] = LocalDate.class;
                                    }catch(DateTimeParseException exDt4)
                                    {
                                        try
                                        {
                                            parameterization[p] = LocalDate.parse(col.getColumnStringArray().get(i), DateTimeFormatter.ofPattern("M-d-yy"));
                                            datatypes[p] = LocalDate.class;
                                        }catch(DateTimeParseException exDt5)
                                        {
                                            try
                                            {
                                                parameterization[p] = LocalDate.parse(col.getColumnStringArray().get(i), DateTimeFormatter.ofPattern("yyyy/M/d"));
                                                datatypes[p] = LocalDate.class;
                                            }catch(DateTimeParseException exDt6)
                                            {
                                                //should never be reached due to column building steps

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(col.getColumnDataType().equals("Boolean"))
                    {
                        if(col.getColumnStringArray().get(i).equalsIgnoreCase("True"))
                        {
                            parameterization[p] = true;
                            datatypes[p] = Boolean.class;
                        }
                        else if(col.getColumnStringArray().get(i).equalsIgnoreCase("Yes"))
                        {
                            parameterization[p] = true;
                            datatypes[p] = Boolean.class;
                        }
                        else
                        {
                            try
                            {
                                if(Integer.parseInt(col.getColumnStringArray().get(i)) == 1)
                                {
                                    parameterization[p] = true;
                                    datatypes[p] = Boolean.class;
                                }
                                else
                                {
                                    parameterization[p] = false;
                                    datatypes[p] = Boolean.class;
                                }
                            }
                            catch(NumberFormatException exInt)
                            {
                                parameterization[p] = false;
                                datatypes[p] = Boolean.class;
                            }
                        }
                    }
                    else
                    {
                        parameterization[p] = col.getColumnStringArray().get(i);
                        datatypes[p] = String.class;
                    }
                    //increment index of parameterization and datatype
                    p++;
                }

                Constructor csvConst = null;
                System.out.println(datatypes.toString());
                //retrieve appropriate constructor of csvClass
                try {
                    csvConst = csvClass.getConstructor(datatypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                //add new "row" to arrayList
                try {
                    result.add((csvClass) csvConst.newInstance(parameterization));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

            }

            return result;
        }

    private ArrayList<ColumnCSV> buildColumns()
    {
        Scanner fileScnr = null;
        try {
            fileScnr = new Scanner(new File(csvFilePath));
        } catch (FileNotFoundException e2x) {
            System.out.println("Error: File Not Found");
        }

        String headerRow = null;
        if (fileScnr.hasNextLine()) {
            headerRow = fileScnr.nextLine();
        } else {
            System.out.println("ERROR: CSV Not Found. ");
        }

        //store String[] representation of rows in file
        ArrayList<String[]> rawRowArrays = new ArrayList<>();

        //get String[] representation of ALL rows in file
        while (fileScnr.hasNextLine()) {
            rawRowArrays.add(fileScnr.nextLine().split(","));
        }

        //Scanner for HEADERROW String (Not file)
        Scanner headerScnr = new Scanner(headerRow);
        headerScnr.useDelimiter(",");

        //Store ColumnCSV objects
        ArrayList<ColumnCSV> columns = new ArrayList<>();

        //int representation of column in CSV
        int colIndex = 0;

        //Instantiate all ColumnCSV objects and store in Columns
        while (headerScnr.hasNext()) {
            String colName = headerScnr.next();

            ArrayList<String> currentColumnValues = new ArrayList<>();
            for (int i = 0; i < rawRowArrays.size(); i++) {
                currentColumnValues.add(rawRowArrays.get(i)[colIndex]);
            }
            currentColumnValues = cleanCells(currentColumnValues);

            columns.add(new ColumnCSV(colName, colIndex, currentColumnValues, intuitDatatype(currentColumnValues)));

            colIndex++;
        }

        return columns;
    }

    //Logic to determine datatype. Everything is a Double, Integer, or String
    private Object intuitDatatype(ArrayList<String> columnData)
    {
        Class cellBestClass = String.class;
        ArrayList<Class> potentialColumnClass = new ArrayList<>();

        for(String cell: columnData)
        {
            cellBestClass = String.class;

            try
            {
                Double.parseDouble(cell);
                cellBestClass = Double.class;
            }catch(NumberFormatException ex)
            {
                if(cell.equalsIgnoreCase("Yes") || cell.equalsIgnoreCase("No"))
                {
                    cellBestClass = Boolean.class;
                }
                else if(cell.equalsIgnoreCase("True")  || cell.equalsIgnoreCase("False"))
                {
                    cellBestClass = Boolean.class;
                }
                else
                {
                    try
                    {
                        LocalDateTime.parse(cell);
                        cellBestClass = LocalDateTime.class;
                    }catch(DateTimeParseException exDt)
                    {
                        try
                        {
                            LocalDate.parse(cell);
                            cellBestClass = LocalDate.class;
                        }catch(DateTimeParseException exD)
                        {
                            try
                            {
                                LocalDate.parse(cell, DateTimeFormatter.ofPattern("M/d/yyyy"));
                                cellBestClass = LocalDate.class;
                            }catch(DateTimeParseException exD2)
                            {
                                try
                                {
                                    LocalDate.parse(cell, DateTimeFormatter.ofPattern("M/d/yy"));
                                    cellBestClass = LocalDate.class;
                                }catch(DateTimeParseException exD3)
                                {
                                    try
                                    {
                                        LocalDate.parse(cell, DateTimeFormatter.ofPattern("M-d-yyyy"));
                                        cellBestClass = LocalDate.class;
                                    }catch(DateTimeParseException exD4)
                                    {
                                        try
                                        {
                                            LocalDate.parse(cell, DateTimeFormatter.ofPattern("M-dd-yy"));
                                            cellBestClass = LocalDate.class;
                                        }catch(DateTimeException exD5)
                                        {
                                            try
                                            {
                                                LocalDate.parse(cell, DateTimeFormatter.ofPattern("yyyy/M/d"));
                                                cellBestClass = LocalDate.class;
                                            }catch(DateTimeException exD6)
                                            {
                                                cellBestClass = String.class;
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

            if(cellBestClass.equals(Double.class))
            {
                try
                {
                    Integer.parseInt(cell);
                    cellBestClass = Integer.class;
                }catch (NumberFormatException exInt)
                {

                }
            }

            if(cellBestClass.equals(Integer.class))
            {
                if(Integer.parseInt(cell) == 0 || Integer.parseInt(cell)  == 1)
                {
                    cellBestClass = Boolean.class;
                }
            }

            potentialColumnClass.add(cellBestClass);

        }
        //System.out.println(potentialColumnClass);
        if(potentialColumnClass.contains(String.class))
        {
            return String.class;
        }
        else if(potentialColumnClass.contains(LocalDateTime.class) && !(potentialColumnClass.contains(LocalDate.class)) && !(potentialColumnClass.contains(Integer.class)) && !(potentialColumnClass.contains(Double.class) && !(potentialColumnClass.contains(Boolean.class))))
        {
            return LocalDateTime.class;
        }
        else if(!(potentialColumnClass.contains(LocalDateTime.class)) && (potentialColumnClass.contains(LocalDate.class)) && !(potentialColumnClass.contains(Integer.class)) && !(potentialColumnClass.contains(Double.class) && !(potentialColumnClass.contains(Boolean.class))))
        {
            return LocalDate.class;
        }
        else if((!(potentialColumnClass.contains(LocalDateTime.class)) &&  !potentialColumnClass.contains(LocalDate.class) && !(potentialColumnClass.contains(Integer.class)) && !(potentialColumnClass.contains(Double.class)) && (potentialColumnClass.contains(Boolean.class))))
        {
            return Boolean.class;
        }
        else if(!(potentialColumnClass.contains(LocalDateTime.class)) && !potentialColumnClass.contains(Boolean.class) && !(potentialColumnClass.contains(LocalDate.class)) && ((potentialColumnClass.contains(Integer.class) || potentialColumnClass.contains(Double.class))))
        {
            if(potentialColumnClass.contains(Double.class))
            {
                return Double.class;
            }
            else
            {
                return Integer.class;
            }
        }
        else
        {
            return String.class; //safety
        }

    }

    private void buildPOJO(ArrayList<ColumnCSV> columns) throws IOException {
        String csvNameNoExtension = csvFilePath.substring(csvFilePath.lastIndexOf("/") + 1);
        csvNameNoExtension = csvNameNoExtension.substring(0, csvNameNoExtension.lastIndexOf("."));
        String csvClassName = csvNameNoExtension.substring(0,1).toUpperCase() + csvNameNoExtension.substring(1).toLowerCase();

        //ensure no duplicate named columns
        ArrayList<String> colNames = new ArrayList<>();
        for(ColumnCSV col: columns)
        {
            if(colNames.contains(col.getColumnName()))
            {
                col.setColumnName(col.getColumnName() + "I");
            }
            colNames.add(col.getColumnName());
        }

        //Create file for POJO Structure
        File csvPojo = new File(String.format("src/main/java/%s%s", csvClassName, ".java"));

        //Create FileWriter
        Writer buildCSVClass = null;
        try {
            buildCSVClass = new FileWriter(csvPojo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Boolean localDateTimeImport = false;
        for(ColumnCSV col: columns)
        {
            if(col.getColumnDataType().equals("LocalDateTime"))
            {
                localDateTimeImport = true;
            }
        }
        if(localDateTimeImport == true)
        {
            buildCSVClass.write("import java.time.LocalDateTime;\n\n");
        }

        //check localdate import
        Boolean localDateImport = false;
        for(ColumnCSV col: columns)
        {
            if(col.getColumnDataType().equals("LocalDate"))
            {
                localDateImport = true;
            }
        }

        if(localDateImport == true)
        {
            buildCSVClass.write("import java.time.LocalDate;\n\n");
        }

        //Write Class Signature to File
        try {
            buildCSVClass.write(String.format("public class %s {\n", csvClassName));
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Write Fields to File
        for (ColumnCSV col : columns) {
            try {
                buildCSVClass.write("private " + col.getColumnDataType()
                        + String.format(" %s;\n\n", col.getColumnName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Write default constructor to file
        buildCSVClass.write(String.format("public %s() {\n\n}\n\n", csvClassName));

        //Start building parameterized constructor
        String constructorParameterization = String.format("public %s(", csvClassName);

        //Finish Parameterized Constructor
        int i = 0;
        for (ColumnCSV col : columns) {
            if (i == 0) {
                constructorParameterization = constructorParameterization + " " + col.getColumnDataType() + " " + col.getColumnName();
            } else {
                constructorParameterization = constructorParameterization + ", " + col.getColumnDataType() + " " + col.getColumnName();
            }
            i++;
        }
        constructorParameterization = constructorParameterization + ") {\n";

        //Write paramaterized Constructor Signature to File
        buildCSVClass.write(constructorParameterization);

        //Write paramaterized constructor assignments to file
        for (ColumnCSV col : columns) {
            buildCSVClass.write(String.format("this.%s= %s;\n", col.getColumnName(), col.getColumnName()));
        }
        buildCSVClass.write("}\n\n");

        //write getters and setters to file
        for (ColumnCSV col : columns) {
            buildCSVClass.write(String.format("public %s get%s() {\n return %s;\n}\n\n", col.getColumnDataType(), col.getColumnName(), col.getColumnName()));
            buildCSVClass.write(String.format("public void set%s(%s %s) {\nthis.%s = %s;\n}\n\n", col.getColumnName(), col.getColumnDataType(), col.getColumnName(), col.getColumnName(), col.getColumnName()));
        }

        //write toString signature to File
        buildCSVClass.write("@Override()\npublic String toString() {\nreturn ");

        //Create toString String
        int j = 0;
        for (ColumnCSV col : columns) {
            if (j < columns.size() - 1) {
                buildCSVClass.write(String.format("\"%s:  \" +  %s\n + ", col.getColumnName(), col.getColumnName()));
            } else {
                buildCSVClass.write(String.format("\"%s:  \" +  %s;\n}\n\n}", col.getColumnName(), col.getColumnName()));
            }
            j++;

        }

        //Close FileWriter
        buildCSVClass.close();

    }

    //Logic to clean up confusing/illegal characters from cells (Needs to be improved)
    public ArrayList<String> cleanCells(ArrayList<String> currentColumnValues)
    {
        ArrayList<String> cleanValues = new ArrayList<>();

        //remove quotes and empties-->NaN
        for(String cell: currentColumnValues)
        {
            String correctedCell = cell.replaceAll("\"", "").trim();
            if(correctedCell.equals(""))
            {
                correctedCell = "NaN";

            }
            cleanValues.add(correctedCell);
        }


        return cleanValues;
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }

    public File getCsvFileObject() {
        return csvFileObject;
    }
}


