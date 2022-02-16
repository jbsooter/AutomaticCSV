import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuickParseCSV implements ParseCSV{

    private String  csvFilePath;

    private File csvFileObject;

    private String csvClassName;

    public QuickParseCSV()
    {

    }

    public QuickParseCSV(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        this.csvFileObject = new File(csvFilePath);
        this.csvClassName = createClassName(csvFilePath);
    }

    public QuickParseCSV(File csvFileObject) {
        this.csvFileObject = csvFileObject;
        this.csvFilePath = csvFileObject.toString();
        this.csvClassName = createClassName(csvFilePath);
    }

    public <T> ArrayList<T> profileCSV()  {

        try
            {
                //if class already exists>>>
                Class CSVClass = Class.forName(csvClassName);
                FileReader CSVClassReader = new FileReader(String.format("src/main/java/%s.java", csvClassName));
                System.out.println("CLASS EXISTS");
                return readCSV(CSVClass);
            }catch(ClassNotFoundException | FileNotFoundException ex)
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

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, null, null);
        try {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays
                    .asList(new File("build/classes/java/main/")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean success = compiler.getTask(null, fileManager, null, null, null,
                        fileManager.getJavaFileObjectsFromFiles(Arrays.asList(new File(String.format("src/main/java/%s.java", csvClassName)))))
                .call();
        try {
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            Class.forName(csvClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return readCSVEmbedded(columns);
        }
    public <csvClass> ArrayList<csvClass> readCSVEmbedded(ArrayList<ColumnCSV> columns)
    {
        ArrayList<csvClass> result = new ArrayList<csvClass>();
        //ArrayList<ColumnCSV> columns = buildColumns();

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

            //retrieve appropriate constructor of csvClass
            try {
                csvConst = Class.forName(csvClassName).getConstructor(datatypes);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
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
        public <csvClass> ArrayList<csvClass> readCSV(Class CSVClass)
        {
            ArrayList<csvClass> results = new ArrayList<>();

            Field[] fields = CSVClass.getDeclaredFields();

            Scanner fileScnr = null;
            try {
                fileScnr = new Scanner(new File(csvFilePath));
            } catch (FileNotFoundException e2x) {
                System.out.println("ERROR: CSV File Not Found. Check your File Path. ");
            }

            String headerRow = null;
            if (fileScnr.hasNextLine()) {
                headerRow = fileScnr.nextLine();
            } else {
                System.out.println("ERROR: CSV File Not Found. Check your File Path. ");
            }

            //Scanner for HEADERROW String (Not file)
            Scanner headerScnr = new Scanner(headerRow);
            headerScnr.useDelimiter(",");

            //START: code to put the datatypes from @QuickCSVField in the correct order relative to the CSV. (Protect against user modification or reordering)
            //NOTE: The QuickCSVConstructor is not affected by reordering the fields, so if it is clean the instantiation of objects is not affected once datatypes are ordered.
            //put all headers from csv into arraylist
            ArrayList<String> headers = new ArrayList<>();
            while(headerScnr.hasNext())
            {
                headers.add(headerScnr.next());
            }

            //create list of java qualified field names from csv header
            ArrayList<String> jcHeaders = new ArrayList<>();
            for(String h: headers)
            {
                String hjc = javaQualifiedName(h);
                boolean headercheck = false;

                while(headercheck == false)
                if(jcHeaders.contains(hjc))
                {
                    hjc += "I";
                }
                else
                {
                    jcHeaders.add(hjc);
                    headercheck = true;
                }
            }

            //get numver of QuickCSV annotated fields
            int numCSVFields = 0;
            for(Field f: fields)
            {
                if(f.isAnnotationPresent(QuickCSVField.class))
                {
                    numCSVFields += 1;
                }
            }

            //order classes of data to match csv
            Class[] csvFields = new Class[numCSVFields];
            for(Field f: fields)
            {
                int col_index = 0;
                if(f.isAnnotationPresent(QuickCSVField.class))
                {
                    for(String h: jcHeaders)
                    {
                        //System.out.println(h);
                        if(h.equals(f.getName()))
                        {
                            csvFields[col_index] = f.getType();

                        }
                        else
                        {
                            col_index += 1;
                        }

                    }

                }
            }

//get all constructors of class CSVClass
            Constructor[] con = CSVClass.getConstructors();
            Constructor quickCSVConstructor = null;

            //identify the constructor for the CSV data by annotation
            for(Constructor c: con) {
                if (c.isAnnotationPresent(QuickCSVConstructor.class)) {
                    quickCSVConstructor = c;
                }
            }
            //for(String[] row: rawRowArrays)
            while(fileScnr.hasNextLine())
            {
                String[] row = fileScnr.nextLine().split(",");
                //store parsed cell values to create csvClass object
                Object[] parsedRow = new Object[csvFields.length];

                for(int i = 0; i < csvFields.length; i++)

                {

                    String cleanCell = cleanCell(row[i]);
                    if(csvFields[i].equals(Double.class))
                    {
                        parsedRow[i] = Double.parseDouble(cleanCell);
                    }
                    else if(csvFields[i].equals(String.class))
                    {
                        parsedRow[i] = cleanCell;
                    }
                    else if(csvFields[i].equals(Integer.class))
                    {
                        parsedRow[i] = Integer.parseInt(cleanCell);
                    }
                    else if(csvFields[i].equals(LocalDateTime.class))
                    {
                        parsedRow[i] = LocalDateTime.parse(cleanCell);
                    }
                    else if(csvFields[i].equals(Boolean.class))
                    {
                        if(cleanCell.equalsIgnoreCase("yes") || cleanCell.equalsIgnoreCase("true") || checkIntTrue(cleanCell)) //checkint true to take care of integer bool
                        {
                            parsedRow[i] = true;
                        }
                        else
                        {
                            parsedRow[i] = false;
                        }
                    }
                    else
                    {
                        try
                        {
                            parsedRow[i] = LocalDate.parse(cleanCell);

                        }catch(DateTimeParseException exDt) {
                            try {
                                parsedRow[i] = LocalDate.parse(cleanCell, DateTimeFormatter.ofPattern("M/d/yyyy"));
                            } catch (DateTimeParseException exDt2) {
                                try {
                                    parsedRow[i] = LocalDate.parse(cleanCell, DateTimeFormatter.ofPattern("M/d/yy"));

                                } catch (DateTimeParseException exDt3) {
                                    try {
                                        parsedRow[i] = LocalDate.parse(cleanCell, DateTimeFormatter.ofPattern("M-d-yyyy"));
                                    } catch (DateTimeParseException exDt4) {
                                        try {
                                            parsedRow[i] = LocalDate.parse(cleanCell, DateTimeFormatter.ofPattern("M-d-yy"));
                                        } catch (DateTimeParseException exDt5) {
                                            try {
                                                parsedRow[i] = LocalDate.parse(cleanCell, DateTimeFormatter.ofPattern("yyyy/M/d"));
                                            } catch (DateTimeParseException exDt6) {
                                                //should never be reached due to column building steps
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                    //i += 1;
                }


                try{
                    results.add((csvClass) quickCSVConstructor.newInstance(parsedRow));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            //return arrayList of csvClass objects
            return results;
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

            columns.add(new ColumnCSV(javaQualifiedName(colName), colIndex, currentColumnValues));

            colIndex++;
        }

        //parallelize datatype intuition
        columns.parallelStream().forEach(columnCSV -> {columnCSV.setColumnDataType(intuitDatatype((ArrayList<String>) columnCSV.getColumnStringArray()));});

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
                buildCSVClass.write("@QuickCSVField\n");
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
        //TODO: Annotate
        constructorParameterization = "@QuickCSVConstructor\n" + constructorParameterization + ") {\n";

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
                buildCSVClass.write(String.format("\"%s: \" +  %s + \"  \"\n + ", col.getColumnName(), col.getColumnName()));
            } else {
                buildCSVClass.write(String.format("\"%s: \" +  %s + \"  \";\n}\n\n}", col.getColumnName(), col.getColumnName()));
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

    private String cleanCell(String cell)
    {
        String correctedCell = cell.replaceAll("\"", "").trim();
        if(correctedCell.equals(""))
        {
            correctedCell = "NaN";

        }

        return correctedCell;
    }

    private String createClassName(String csvFilePath)
    {
        String csvNameNoExtension = csvFilePath.substring(csvFilePath.lastIndexOf("/") + 1);
        csvNameNoExtension = csvNameNoExtension.substring(0, csvNameNoExtension.lastIndexOf("."));
        String csvClassName = csvNameNoExtension.substring(0,1).toUpperCase() + csvNameNoExtension.substring(1).toLowerCase();


        //remove unqualifiedchar
        csvClassName = javaQualifiedName(csvClassName);
        return csvClassName;
    }

    private String javaQualifiedName(String columnName) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(columnName);
        while(m.find())
        {
            columnName = columnName.replace(m.group(), EnglishNumberToWords.convert(Long.parseLong( m.group())));
        }
        columnName = columnName.trim().replaceAll("\\s", "").replaceAll("%", "pct")
                .replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "");
        columnName = columnName.replaceAll("[^a-zA-Z]", ""); //replace any char that is not a-zA-Z

        if((new JavaKeywords().getJAVA_KEYWORDS()).contains(columnName))
        {
            columnName = columnName + "_JavaKeyword";
        }

        if(columnName.equals(""))
        {
            columnName = columnName + "NoNameColumn";
        }
        return columnName;
    }

    public boolean checkIntTrue(String cell)
    {
        Integer intCell;
        try
        {
            intCell = Integer.parseInt(cell);
        }catch(NumberFormatException ex)
        {
            intCell = 0;
        }

        if(intCell == 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public String getCsvFilePath() {
        return csvFilePath;
    }

    public File getCsvFileObject() {
        return csvFileObject;
    }
}


