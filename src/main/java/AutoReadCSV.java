import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows user to read a CSV file (headers assumed) and parse into an ArrayList of Java objects, with no existing class infrastructure.
 *
 * If the CSV file is being read in for the first time, and there is no existing POJO class, the file is read in as String,
 * and datatypes are determined programmatically for each column.
 *
 * The generated class is then compiled and loaded dynamically, and the CSV is parsed line by line and objects of the appropriate class type are instantiated.
 *
 * If the CSV file has been read in previously, AutomaticCSV skips to the line by line parsing step. This saves a substantial amount of time and preserves user added
 * class methods and constructors.
 *
 * The @CSVField and @CSVConstructor annotations intentionally facilitate robust protections against destructive user modifications to the class
 * representing the CSV. So long as the @CSVConstructor annotated constructor is not modified, and all @CSVField annotated fields exist
 * somewhere in the Class, in any order, the CSV can still be parsed into objects.
 *
 * User modification of the CSV-representative class is encouraged and is behind the philosophy of creating an automated method of reading in CSVs as POJOs rather
 * than some sort of Tablesaw-esque dataframe. The goal of this library is to achieve the one-line convenience of .readCSV() methods in data science libraries and
 * scripting languages while maintaining the benefits of custom Java objects and the strictly-typed philosophy.
 */
public class AutoReadCSV implements ReadCSV {

    /**
     * Path of the CSV file.
     */
    private String  csvFilePath;

    /**
     * Path of the CSV file.
     */
    private File csvFileObject;

    /**
     * Name of the Class representing the CSV file. Based off of the name of the CSV file,
     * but may be slightly different as AutomaticCSV programmatically ensures that the CSV class
     * is named with a Java qualified class name.
     */
    private String csvClassName;

    /**
     * Delimeter used in the csv file. Defaults to ",".
     */
    private String delimeter;

    /**
     * Path to the java source directory of your project. Default is the standard Gradle file structure,
     * "src/main/java/"
     * Can be modified using the appropriate setter.
     */
    private String srcDirPath;

    /**
     * Path to the java source directory of your project. Default is the standard Gradle file structure,
     * "src/main/java/"
     * Can be modified using the appropriate setter.
     */
    private File srcDirFileObject;

    /**
     * Path to the java build directory of your project. Default is the standard Gradle file structure.
     * "build/classes/java/main/"
     * Can be modified using the appropriate setter.
     */
    private String buildDirPath;

    /**
     * Path to the java build directory of your project. Default is the standard Gradle file structure.
     * "build/classes/java/main/"
     * Can be modified using the appropriate setter.
     */
    private File buildDirFileObject;

    /**
     * URL object pointing to the online location of a CSV file.
     */
    private URL csvFileURL;


    /**
     * Default Constructor
     */
    public AutoReadCSV()
    {

    }

    /**
     * Creates an AutoReadCSV instance using a String filepath. All other fields are initialized to default
     * values to maximize ease of use but can be overridden with the relevant setter.
     * @param csvFilePath representation of the path to the CSV file the user wants to read from.
     */
    public AutoReadCSV(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        this.csvFileObject = new File(csvFilePath);
        this.csvClassName = createClassName(csvFilePath);
        this.delimeter = ",";
        this.buildDirPath = "build/classes/java/main/";
        this.buildDirFileObject = new File("build/classes/java/main/");
        this.srcDirPath = "src/main/java/";
        this.srcDirFileObject = new File("src/main/java/");
    }

    /**
     * Creates an AutoReadCSV instance using a File filepath. All other fields are initialized to default
     * values to maximize ease of use but can be overridden with the relevant setter.
     * @param csvFileObject File object representation of the path to the CSV file the user wants to read from.
     */
    public AutoReadCSV(File csvFileObject) {
        this.csvFileObject = csvFileObject;
        this.csvFilePath = csvFileObject.toString();
        this.csvClassName = createClassName(csvFilePath);
        this.delimeter = ",";
        this.buildDirPath = "build/classes/java/main/";
        this.buildDirFileObject = new File("build/classes/java/main/");
        this.srcDirPath = "src/main/java/";
        this.srcDirFileObject = new File("src/main/java");
    }

    /**
     * Creates an AutoReadCSV instance using a URL object pointing to a CSV file at an online location. A preferredFileName must be specified ex: name.csv.
     * All other fields are initialized to default values but can be easily overridden with the relevant setter.
     * @param csvFileURL URL object for the online location of a CSV file. (Ex: Github)
     * @param preferredFileName Name that you would prefer the CSV/Class to be called.
     */
    public AutoReadCSV(URL csvFileURL, String preferredFileName)
    {
        this.delimeter = ",";
        this.buildDirPath = "build/classes/java/main/";
        this.buildDirFileObject = new File("build/classes/java/main/");
        this.srcDirPath = "src/main/java/";
        this.srcDirFileObject = new File("src/main/java");
        this.csvFileURL = csvFileURL;
        this.csvClassName = createClassName(preferredFileName);
    }

    /**
     * Read in data from the CSV at the filepath specified when the AutoReadCSV object was instantiated.
     * @param <T> Type of Objects in ArrayList storing data from the csv. On first run, this should be Object. Once the class representing
     * the CSV has been generated, this can be changed to that type to enable full functionality when interacting with the ArrayList.
     * @return ArrayList of Objects storing the CSV data.
     */
    public <T> ArrayList<T> readCSV()  {

        try
            {
                //Check to see if class already exists
                Class CSVClass = Class.forName(csvClassName);
                //make sure class does not only exist as a lingering build file.
                FileReader CSVClassReader = new FileReader(String.format("%s%s.java", srcDirPath, csvClassName));
                System.out.println(String.format("CLASS %s EXISTS", csvClassName));
                return readCSVfromClass(CSVClass);
            }catch(ClassNotFoundException | FileNotFoundException ex)
            {
                System.out.println(String.format("No CSV Class  \"%s\" detected. ", csvClassName));
                System.out.println(String.format("Generating CSV Class %s....", csvClassName));
            }

            ArrayList<ColumnCSV> columns = buildColumns();

            try {
                buildPOJO(columns);
            } catch (IOException e) {
                System.out.println(String.format("Attempt to Build Class  \"%s\" Failed. ", csvClassName));
                return null;
            }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, null, null);
        try {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays
                    .asList(new File(buildDirPath)));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(String.format("Build Directory %s not found. Set your build directory with setBuildDirPath()", buildDirPath));
        }

        boolean success = compiler.getTask(null, fileManager, null, null, null,
                        fileManager.getJavaFileObjectsFromFiles(Arrays.asList(new File(String.format("%s%s.java", srcDirPath, csvClassName)))))
                .call();
        try {
            fileManager.close();
        } catch (IOException e) {
            System.out.println("ERROR: Generated Class Failed to Compile. Verify the sourceDir and buildDir of your project. ");
        }

        Class c = null;
        try{
            c = Class.forName(csvClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return readCSVfromClass(c);
        }
    
        private <csvClass> ArrayList<csvClass> readCSVfromClass(Class CSVClass)
        {
            ArrayList<csvClass> results = new ArrayList<>();

            Field[] fields = CSVClass.getDeclaredFields();

            Scanner fileScnr = null;
            try {
                fileScnr = new Scanner(new File(csvFilePath));
            } catch ( NullPointerException ex) {
                try {
                    fileScnr = new Scanner(csvFileURL.openConnection().getInputStream());
                } catch (IOException e) {
                    System.out.println("ERROR: CSV file not found at the given URL. Check your URL. ");
                }
            }
            catch(FileNotFoundException ex2)
            {
                System.out.println("ERROR: CSV File Not Found. Check your File Path. ");
            }

            String headerRow = null;
            if (fileScnr.hasNextLine()) {
                headerRow = fileScnr.nextLine();
            } else {
                System.out.println("ERROR: CSV File Not Found. Check your File Path. ");
            }

            //Scanner for HEADER ROW String (Not file)
            Scanner headerScnr = new Scanner(headerRow);
            headerScnr.useDelimiter(delimeter);

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
                if(f.isAnnotationPresent(CSVField.class))
                {
                    numCSVFields += 1;
                }
            }

            //order classes of data to match csv
            Class[] csvFields = new Class[numCSVFields];
            for(Field f: fields)
            {
                int col_index = 0;
                if(f.isAnnotationPresent(CSVField.class))
                {
                    for(String h: jcHeaders)
                    {
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
                if (c.isAnnotationPresent(CSVConstructor.class)) {
                    quickCSVConstructor = c;
                }
            }

            while(fileScnr.hasNextLine())
            {
                String[] row = fileScnr.nextLine().split(delimeter + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); //-1 protects against ,,
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
                        try
                        {
                            parsedRow[i] = LocalDateTime.parse(cleanCell);
                        }catch(DateTimeParseException ex)
                        {
                            try {

                                parsedRow[i] = LocalDateTime.parse(cleanCell, DateTimeFormatter.ofPattern("M/d/y H:m"));
                            }catch(DateTimeParseException ex2)
                            {
                                try
                                {
                                    parsedRow[i] = LocalDateTime.parse(cleanCell,DateTimeFormatter.ofPattern("M/d/y H:m:s"));
                                }catch(DateTimeParseException ex3)
                                {

                                }
                            }
                            }

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

    /**
     * Build ColumnCSV objects and determines their datatype quickly by calling intuitDatatype with a parallel stream.
     * This facilitates the creation of a POJO structure mapped to the CSV.
      * @return Every single column from the CSV file and relevant information from it including datatypes, cells as a string, header names, etc.
     */
    private ArrayList<ColumnCSV> buildColumns()
    {
        Scanner fileScnr = null;
        try {
            fileScnr = new Scanner(new File(csvFilePath));
        } catch ( NullPointerException ex) {
            try {
                fileScnr = new Scanner(new BufferedReader(new InputStreamReader(csvFileURL.openStream())));
            } catch (IOException e) {
                System.out.println("ERROR: CSV file not found at the given URL. Check your URL. ");
            }
        }
        catch(FileNotFoundException ex2)
        {
            System.out.println("ERROR: CSV File Not Found. Check your File Path. ");
        }

        String headerRow = null;
        if (fileScnr.hasNextLine()) {
            headerRow = fileScnr.nextLine();
            System.out.println(headerRow);
        } else {
            System.out.println("ERROR: CSV Not Found. ");
        }

        //store String[] representation of rows in file
        ArrayList<String[]> rawRowArrays = new ArrayList<>();

        //get String[] representation of ALL rows in file
        while (fileScnr.hasNextLine()) {
            rawRowArrays.add(fileScnr.nextLine().split(delimeter + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1));
        }

        //Scanner for HEADERROW String (Not file)
        Scanner headerScnr = new Scanner(headerRow);
        headerScnr.useDelimiter(delimeter);

        //Store ColumnCSV objects
        ArrayList<ColumnCSV> columns = new ArrayList<>();

        //int representation of column in CSV
        int colIndex = 0;

        //Instantiate all ColumnCSV objects and store in Columns
        while (headerScnr.hasNext()) {
            String colName = javaQualifiedName(headerScnr.next());

            ArrayList<String> currentColumnValues = new ArrayList<>();

            for (int i = 0; i < rawRowArrays.size(); i++) {
                currentColumnValues.add(rawRowArrays.get(i)[colIndex]);
            }
            currentColumnValues = cleanCells(currentColumnValues);

            for(ColumnCSV col: columns)
            {
                if(col.getColumnName().equals(colName))
                {
                    colName = colName + "I";

                }
            }
            columns.add(new ColumnCSV(colName, colIndex, currentColumnValues));

            colIndex++;
        }

        //parallelize datatype intuition
        columns.parallelStream().forEach(columnCSV -> {columnCSV.setColumnDataType(intuitDatatype((ArrayList<String>) columnCSV.getColumnStringArray()));});

        return columns;
    }

    /**
     * Logic to determine the best datatype of the String representation of cells form a single column. This method should be called using a
     * parallelStream. Strives to be quick while also guaranteeing that the highest resolution supported datatype is chosen.
     * @param columnData
     * @return Class of the highest resolution supported datatype for a column.
     */
    private Object intuitDatatype(ArrayList<String> columnData)
    {
        Class cellBestClass = String.class;
        ArrayList<Class> potentialColumnClass = new ArrayList<>();

        Class lastSeenCellBestClass = Object.class; //prevent null issue
        String dtStringFormat = "";
        int row = 0;
        for(String cell: columnData)
        {
            //cellBestClass = String.class;
            if(lastSeenCellBestClass.equals(String.class))
            {
                break; //if there is ever a string type cell, have to read it in as string
            }
            else if(lastSeenCellBestClass.equals(Boolean.class))
            {

                    if(cell.equalsIgnoreCase("Yes") || cell.equalsIgnoreCase("No"))
                    {
                        continue;
                    }
                    else if(cell.equalsIgnoreCase("True")  || cell.equalsIgnoreCase("False"))
                    {
                        continue;
                    }
                    else
                    {
                        try
                        {
                            int val = Integer.parseInt(cell);
                            if(val == 1 || val == 0)
                            {
                                continue;
                            }
                        }catch(NumberFormatException ex)
                        {

                        }
                    }

            }
            else if(lastSeenCellBestClass.equals(Integer.class))
            {
                try
                {
                    Integer.parseInt(cell);
                    cellBestClass = Integer.class;
                    continue; //next iteration
                }catch(NumberFormatException ex)
                {

                }
            }
            else if(lastSeenCellBestClass.equals(Double.class))
            {
                boolean yesDouble = false;
                try
                {
                    Double.parseDouble(cell);
                    yesDouble = true;
                }catch(NumberFormatException ex)
                {

                }

                try
                {
                    Integer.parseInt(cell);
                }catch(NumberFormatException ex)
                {
                    if(yesDouble)
                    {
                        continue;
                    }
                }
            }
            else if(lastSeenCellBestClass.equals(LocalDate.class))
            {
                try
                {
                    LocalDate.parse(cell, DateTimeFormatter.ofPattern(dtStringFormat));
                    continue;
                }catch(DateTimeParseException ex)
                {

                }
            }
            else if(lastSeenCellBestClass.equals(LocalDateTime.class))
            {
                try
                {
                    LocalDateTime.parse(cell);
                    continue;
                }catch(DateTimeParseException ex)
                {
                    try
                    {
                        LocalDateTime.parse(cell,DateTimeFormatter.ofPattern("M/d/y H:m"));
                        continue;
                    }catch(DateTimeParseException ex2)
                    {
                        try
                        {
                            LocalDateTime.parse(cell,DateTimeFormatter.ofPattern("M/d/y H:m:s"));
                        }catch(DateTimeParseException ex3)
                        {

                        }
                    }
                }
            }

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
                    }catch(DateTimeParseException exDt) {
                        try {
                            LocalDateTime.parse(cell,DateTimeFormatter.ofPattern("M/d/y H:m"));
                            cellBestClass = LocalDateTime.class;
                            dtStringFormat = "M/d/y H:m";
                        } catch (DateTimeParseException ex3) {
                            try
                            {
                                LocalDateTime.parse(cell,DateTimeFormatter.ofPattern("M/d/y H:m:s"));
                                cellBestClass = LocalDateTime.class;
                                dtStringFormat = "M/d/y H:m:s";
                            }catch(DateTimeParseException ex4) {
                                try {
                                    LocalDate.parse(cell);
                                    cellBestClass = LocalDate.class;
                                    dtStringFormat = "yyyy-M-d";
                                } catch (DateTimeParseException exD) {
                                    try {
                                        LocalDate.parse(cell, DateTimeFormatter.ofPattern("M/d/yyyy"));
                                        cellBestClass = LocalDate.class;
                                        dtStringFormat = "M/d/yyyy";
                                    } catch (DateTimeParseException exD2) {
                                        try {
                                            LocalDate.parse(cell, DateTimeFormatter.ofPattern("M/d/yy"));
                                            cellBestClass = LocalDate.class;
                                            dtStringFormat = "M/d/yy";
                                        } catch (DateTimeParseException exD3) {
                                            try {
                                                LocalDate.parse(cell, DateTimeFormatter.ofPattern("M-d-yyyy"));
                                                cellBestClass = LocalDate.class;
                                                dtStringFormat = "M-d-yyyy";
                                            } catch (DateTimeParseException exD4) {
                                                try {
                                                    LocalDate.parse(cell, DateTimeFormatter.ofPattern("M-dd-yy"));
                                                    cellBestClass = LocalDate.class;
                                                    dtStringFormat = "M-dd-yy";
                                                } catch (DateTimeException exD5) {
                                                    try {
                                                        LocalDate.parse(cell, DateTimeFormatter.ofPattern("yyyy/M/d"));
                                                        cellBestClass = LocalDate.class;
                                                        dtStringFormat = "yyyy/M/d";
                                                    } catch (DateTimeException exD6) {
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

            lastSeenCellBestClass = cellBestClass;
            potentialColumnClass.add(cellBestClass);

        }

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

    /**
     * Build POJO class file mapped to the CSV file. This method does not dynamically compile the class, that occurs in the
     * readCSV() method after the class has been created.
     * @param columns All of the ColumnCSV objects, with the datatype instantiated, from the relevant CSV file.
     * @throws IOException
     */
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
        File csvPojo = new File(String.format("%s%s.java", srcDirPath, csvClassName));

        //Create FileWriter
        Writer buildCSVClass = null;
        try {
            buildCSVClass = new FileWriter(csvPojo);
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println(String.format("Attempt to Build CSV Class %s failed. Check the path specified for your src directory. Default is src/main/java", csvClassName));
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

        //import comparator
        buildCSVClass.write("import java.util.Comparator;\n");
        //import Predicate
        buildCSVClass.write("import java.util.function.Predicate;\n");
        //import hashmap
        buildCSVClass.write("import java.util.Map;\n");
        buildCSVClass.write("import java.util.concurrent.ConcurrentHashMap;\n\n");

        //Write Class Signature to File
        try {
            buildCSVClass.write(String.format("public class %s {\n", csvClassName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Write Fields to File
        for (ColumnCSV col : columns) {
            try {
                buildCSVClass.write("@CSVField\n");
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

        constructorParameterization = "@CSVConstructor\n" + constructorParameterization + ") {\n";

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

        //add predicates
        for(ColumnCSV col: columns)
        {
            if(col.getColumnDataType().equals("String"))
            {
                continue;
            }
            else if(col.getColumnDataType().equals("LocalDate") || col.getColumnDataType().equals("LocalDateTime"))
            {
                buildCSVClass.write(String.format("public static Predicate<%s> %sIsBefore(%s %s)\n{\n", csvClassName, col.getColumnName(), col.getColumnDataType(), col.getColumnName().toLowerCase()));
                buildCSVClass.write(String.format("return p -> p.get%s().isBefore(%s);\n}\n\n", col.getColumnName(), col.getColumnName().toLowerCase()));

                buildCSVClass.write(String.format("public static Predicate<%s> %sIsAfter(%s %s)\n{\n", csvClassName, col.getColumnName(), col.getColumnDataType(), col.getColumnName().toLowerCase()));
                buildCSVClass.write(String.format("return p -> p.get%s().isAfter(%s);\n}\n\n", col.getColumnName(), col.getColumnName().toLowerCase()));
                continue;
            }
            else if(col.getColumnDataType().equals("Boolean"))
            {
                buildCSVClass.write(String.format("public static Predicate<%s> %sIsEqualTo(%s %s)\n{\n", csvClassName, col.getColumnName(), col.getColumnDataType(), col.getColumnName().toLowerCase()));
                buildCSVClass.write(String.format("return p -> p.get%s() == true;\n}\n\n", col.getColumnName()));
                continue;

            }
            buildCSVClass.write(String.format("public static Predicate<%s> %sIsEqualTo(%s %s)\n{\n", csvClassName, col.getColumnName(), col.getColumnDataType(), col.getColumnName().toLowerCase()));
            buildCSVClass.write(String.format("return p -> p.get%s() == %s;\n}\n\n", col.getColumnName(), col.getColumnName().toLowerCase()));

            buildCSVClass.write(String.format("public static Predicate<%s> %sIsLessThanOrEqualTo(%s %s)\n{\n", csvClassName, col.getColumnName(), col.getColumnDataType(), col.getColumnName().toLowerCase()));
            buildCSVClass.write(String.format("return p -> p.get%s() <= %s;\n}\n\n", col.getColumnName(), col.getColumnName().toLowerCase()));

            buildCSVClass.write(String.format("public static Predicate<%s> %sIsGreaterThanOrEqualTo(%s %s)\n{\n", csvClassName, col.getColumnName(), col.getColumnDataType(), col.getColumnName().toLowerCase()));
            buildCSVClass.write(String.format("return p -> p.get%s() >= %s;\n}\n\n", col.getColumnName(), col.getColumnName().toLowerCase()));

        }

        //drop by key
        for(ColumnCSV col: columns)
        {
            buildCSVClass.write(String.format("public static Predicate<%s> distinctBy%s()\n{\n", csvClassName, col.getColumnName()));
            buildCSVClass.write("Map<Object, Boolean> seen = new ConcurrentHashMap<>();\n");
            buildCSVClass.write(String.format("return t -> seen.putIfAbsent(t.get%s(), Boolean.TRUE) == null;", col.getColumnName()));

            buildCSVClass.write("}\n\n");
        }

        //dropbyrow (all)
        buildCSVClass.write(String.format("public static Predicate<%s> distinctRecords()\n{\n", csvClassName));
        buildCSVClass.write("Map<Object, Boolean> seen = new ConcurrentHashMap<>();\n");
        buildCSVClass.write("return t -> seen.putIfAbsent(");

        int plusIndex = 0;
        for(ColumnCSV col: columns)
        {
            buildCSVClass.write(String.format("t.get%s().toString()", col.getColumnName()));

            if(plusIndex < columns.size() - 1)
            {
                buildCSVClass.write("+");
            }
            plusIndex++;
        }
        buildCSVClass.write(", Boolean.TRUE) == null;");
        buildCSVClass.write("}\n\n");

        //add comparables ascending
        for (ColumnCSV col : columns)
        {
           buildCSVClass.write(String.format("static class SortAscendingBy%s implements Comparator<%s> {\n", col.getColumnName(), csvClassName));

           buildCSVClass.write(String.format("public int compare(%s o1, %s o2) {\n", csvClassName, csvClassName));

            if(col.getColumnDataType().equals("String"))
            {
                buildCSVClass.write(String.format("return o1.get%s().compareToIgnoreCase(o2.get%s());\n}\n}\n\n", col.getColumnName(), col.getColumnName()));
                continue;
            }

            else if(col.getColumnDataType().equals("Boolean"))
            {
                buildCSVClass.write(String.format("return Boolean.compare(o1.get%s(),o2.get%s());}\n}\n\n", col.getColumnName(), col.getColumnName()));
                continue;
            }
            else if(col.getColumnDataType().equals("LocalDate") || col.getColumnDataType().equals("LocalDateTime"))
            {
                buildCSVClass.write(String.format("return -o1.get%s().compareTo(o2.get%s());}\n}\n\n", col.getColumnName(), col.getColumnName()));
                continue;
            }
           buildCSVClass.write(String.format("if(o1.get%s() < o2.get%s()){\n", col.getColumnName(), col.getColumnName()));

           buildCSVClass.write("return -1;\n}\n");

           buildCSVClass.write("else\n{\nreturn 1;\n}\n}\n}\n\n");

        }

        //comparables descending
        for (ColumnCSV col : columns)
        {
            buildCSVClass.write(String.format("static class SortDescendingBy%s implements Comparator<%s> {\n", col.getColumnName(), csvClassName));

            buildCSVClass.write(String.format("public int compare(%s o1, %s o2) {\n", csvClassName, csvClassName));

            if(col.getColumnDataType().equals("String"))
            {
                buildCSVClass.write(String.format("return o1.get%s().compareToIgnoreCase(o2.get%s());\n}\n}\n\n", col.getColumnName(), col.getColumnName()));
                continue;
            }
            else if(col.getColumnDataType().equals("Boolean"))
            {
                buildCSVClass.write(String.format("return -Boolean.compare(o1.get%s(),o2.get%s());}\n}\n\n", col.getColumnName(), col.getColumnName()));
                continue;
            }
            else if(col.getColumnDataType().equals("LocalDate") || col.getColumnDataType().equals("LocalDateTime"))
            {
                buildCSVClass.write(String.format("return o1.get%s().compareTo(o2.get%s());}\n}\n\n", col.getColumnName(), col.getColumnName()));
                continue;
            }
            buildCSVClass.write(String.format("if(o1.get%s() < o2.get%s()){\n", col.getColumnName(), col.getColumnName()));

            buildCSVClass.write("return 1;\n}\n");

            buildCSVClass.write(String.format("else if(o1.get%s() > o2.get%s()){\n", col.getColumnName(), col.getColumnName()));

            buildCSVClass.write("return -1;\n}\n");
            buildCSVClass.write("else\n{\nreturn 0;\n}\n}\n}\n\n");

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
    private ArrayList<String> cleanCells(ArrayList<String> currentColumnValues)
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

    //Same logic as cleanCells, but for an individual value. Needs to be improved
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

    private boolean checkIntTrue(String cell)
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

    private boolean checkIntFalse(String cell)
    {
        Integer intCell;
        try
        {
            intCell = Integer.parseInt(cell);
        }catch(NumberFormatException ex)
        {
            intCell = 1;
        }

        if(intCell == 0)
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

    public String getDelimeter() {
        return delimeter;
    }

    public void setDelimeter(String delimeter) {
        this.delimeter = delimeter;
    }

    public String getSrcDirPath() {
        return srcDirPath;
    }

    /**
     * Slighly different than "boilerplate" setter; synchronizes srcDirPath and SrcDirFileObject
     * @param srcDirPath
     */
    public void setSrcDirPath(String srcDirPath) {
        this.srcDirPath = srcDirPath;
        this.srcDirFileObject = new File(srcDirPath);
    }

    public File getSrcDirFileObject() {
        return srcDirFileObject;
    }

    /**
     * Slighly different than "boilerplate" setter; synchronizes srcDirPath and SrcDirFileObject
     * @param srcDirFileObject
     */
    public void setSrcDirFileObject(File srcDirFileObject) {
        this.srcDirFileObject = srcDirFileObject;
        this.srcDirPath = srcDirFileObject.getPath();
    }

    public String getBuildDirPath() {
        return buildDirPath;
    }

    public void setBuildDirPath(String buildDirPath) {
        this.buildDirPath = buildDirPath;
        this.buildDirFileObject = new File(buildDirPath);
    }

    public File getBuildDirFileObject() {
        return buildDirFileObject;
    }

    public void setBuildDirFileObject(File buildDirFileObject) {
        this.buildDirFileObject = buildDirFileObject;
        this.buildDirPath = buildDirFileObject.getPath();
    }
}


