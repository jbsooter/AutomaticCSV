import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
        String csvName = csvFilePath.substring(csvFilePath.lastIndexOf("/") + 1);
        String csvNameNoExtension = csvName.substring(0, csvName.lastIndexOf("."));
        String csvClassName = csvNameNoExtension.substring(0,1).toUpperCase() + csvNameNoExtension.substring(1).toLowerCase();
            try
            {
                Class.forName(csvClassName);
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
            }
            return;
        }
        public <csvClass> ArrayList<csvClass> readCSV(Class csvClass)
        {
            ArrayList<csvClass> result = new ArrayList<csvClass>();
            ArrayList<ColumnCSV> columns = buildColumns();


            for(int i = 0; i < columns.get(0).getColumnStringArray().size(); i++){
                Object[] parameterization = new Object[columns.size()];
                Class[] datatypes = new Class[columns.size()];

                int p = 0;
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
                    else
                    {
                        parameterization[p] = col.getColumnStringArray().get(i);
                        datatypes[p] = String.class;
                    }
                    p++;
                }
                Constructor csvConst = null;
                try {
                    csvConst = csvClass.getConstructor(datatypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                try {
                    result.add((csvClass) csvConst.newInstance(parameterization));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
            return (ArrayList<csvClass>) result;
        }

    private ArrayList<ColumnCSV> buildColumns()
    {
        Scanner fileScnr = null;
        try {
            fileScnr = new Scanner(new File(csvFilePath));
        } catch (FileNotFoundException e2x) {
            //Output custom error message?
            System.out.println("filenotfound");
        }
        String headerRow = null;
        if (fileScnr.hasNextLine()) {
            headerRow = fileScnr.nextLine();
        } else {
            //error no header row
        }
        fileScnr.reset();
        fileScnr.nextLine();
        ArrayList<String[]> rawRowArrays = new ArrayList<>();

        while (fileScnr.hasNextLine()) {
            rawRowArrays.add(fileScnr.nextLine().split(","));
        }

        Scanner headerScnr = new Scanner(headerRow);
        headerScnr.useDelimiter(",");

        ArrayList<ColumnCSV> columns = new ArrayList<>();
        int colIndex = 0;

        while (headerScnr.hasNext()) {
            String colName = headerScnr.next();

            ArrayList<String> currentColumnValues = new ArrayList<>();
            for (int i = 0; i < rawRowArrays.size(); i++) {
                currentColumnValues.add(rawRowArrays.get(i)[colIndex]);
            }
            currentColumnValues = cleanCells(currentColumnValues);

            columns.add(new ColumnCSV(colName, colIndex, currentColumnValues, intuitDatatype(currentColumnValues)));
            //intuitDatatype(currentColumnValues);
            colIndex++;
        }
        fileScnr.reset();

        for (ColumnCSV c : columns) {
            System.out.println(c.toString());
            System.out.println("test");
        }
        return columns;
    }
    private Object intuitDatatype(ArrayList<String> columnData)
    {
        Boolean integerIndicator = true;

        for(String cell: columnData)
        {
            Double doubleCell = null;
            Integer integerCell = null;


            try
            {
                integerCell =  Integer.parseInt(cell);

            }
            catch(NumberFormatException ex)
            {
                try
                {
                    doubleCell = Double.parseDouble(cell);
                }
                catch(NumberFormatException ex2)
                {
                    return String.class;
                }
                integerIndicator = false;


            }
        }
        if(integerIndicator)
        {
            return Integer.class;
        }
        else
        {
            return Double.class;
        }


    }

    private void buildPOJO(ArrayList<ColumnCSV> columns) throws IOException {
        String csvName = csvFilePath.substring(csvFilePath.lastIndexOf("/") + 1);
        String csvNameNoExtension = csvName.substring(0, csvName.lastIndexOf("."));

        csvNameNoExtension = csvNameNoExtension.substring(0, 1).toUpperCase() + csvNameNoExtension.substring(1).toLowerCase();
        System.out.println(csvNameNoExtension);
        File csvPojo = new File(String.format("src/main/java/%s%s", csvNameNoExtension, ".java"));
        Writer buildCSVClass = null;
        try {
            buildCSVClass = new FileWriter(csvPojo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            buildCSVClass.write(String.format("public class %s {\n", csvNameNoExtension));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ColumnCSV col : columns) {
            try {
                buildCSVClass.write("private " + col.getColumnDataType()
                        + String.format(" %s;\n\n", col.getColumnName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        buildCSVClass.write(String.format("public %s() {\n\n}\n\n", csvNameNoExtension));

        String constructorParameterization = String.format("public %s(", csvNameNoExtension);

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

        buildCSVClass.write(constructorParameterization);

        for (ColumnCSV col : columns) {
            buildCSVClass.write(String.format("this.%s= %s;\n", col.getColumnName(), col.getColumnName()));
        }
        buildCSVClass.write("}\n\n");

        for (ColumnCSV col : columns) {
            buildCSVClass.write(String.format("public %s get%s() {\n return %s;\n}\n\n", col.getColumnDataType(), col.getColumnName(), col.getColumnName()));
            buildCSVClass.write(String.format("public void set%s(%s %s) {\nthis.%s = %s;\n}\n\n", col.getColumnName(), col.getColumnDataType(), col.getColumnName(), col.getColumnName(), col.getColumnName()));
        }

        buildCSVClass.write("@Override()\npublic String toString() {\nreturn ");

        int j = 0;
        for (ColumnCSV col : columns) {
            if (j < columns.size() - 1) {
                buildCSVClass.write(String.format("\"%s:\" +  %s\n + ", col.getColumnName(), col.getColumnName()));
            } else {
                buildCSVClass.write(String.format("\"%s:\" +  %s;\n}\n\n}", col.getColumnName(), col.getColumnName()));
            }
            j++;

        }

        buildCSVClass.close();

    }

    public ArrayList<String> cleanCells(ArrayList<String> currentColumnValues)
    {
        ArrayList<String> cleanValues = new ArrayList<>();

        for(String cell: currentColumnValues)
        {
            cleanValues.add(cell.replaceAll("\"", ""));

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


