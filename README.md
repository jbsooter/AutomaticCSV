# QuickCSV
#### :rocket: Lightweight Java Library to Automatically Create Plain Old Java Object (POJO) from any CSV File

Take advantage of the flexibility and power of the declarative Java Object structure with the "one liner" convenience of importing a CSV with Tablesaw or a scripting language. 


## Getting Started

1. Add the QuickCSV Dependancy to your Gradle or Maven Project. (TODO: Link to mvnrepository)

  ```Java
  //TODO: Maven code snippet
  ```

2. Create a Java class file in your src/main/java/ directory. (TODO: Add ability to change directory loc)
        
 ```Java
 import java.util.ArrayList;
 //TODO: import QuickCSV reqirements
 
 public class QuickCSVDemo  {
      public static void main(String[] args)
      {
        //Add additional code here
      }
 }

 ```
        
3. Create a new QuickParseCSV object initialized with the path. 

```Java
QuickParseCSV demo = new QuickParseCSV("/path/to/csv");

```
4. Add this method to create a Java class representing the CSV file. 

```Java
demo.profileCSV();
```

5. Run your main method. The Java class representing your CSV file will be generated in your source directory.

6. Read in your CSV file by adding this line. There is no need to remove the profileCSV() line. 

```Java
ArrayList<GeneratedClass> CSVasObjects = demo.readCSV(GeneratedClass.class);
```

7. Done! Your CSV is represented using the POJO structure. In addition, QuickCSV supports users adding fields and methods to the generated class so long as the original fields and constructer matching the CSV file are maintained. (Read more details below). 

### Full Demo Code: 
```Java
import java.util.ArrayList;
//TODO: import QuickCSV reqirements
 public class QuickCSVDemo  {
      public static void main(String[] args)
      {
        QuickParseCSV demo = new QuickParseCSV("/path/to/csv");
        demo.profileCSV();
        ArrayList<GeneratedClass> CSVasObjects = demo.readCSV(GeneratedClass.class);
      }
 }
```

## Documentation






