# AutomaticCSV
#### :rocket: Lightweight Java Library to Automatically Create Plain Old Java Object (POJO) from any CSV File
#### WIP
Take advantage of the flexibility and power of the Java Object structure with the "one liner" convenience of importing a CSV with Tablesaw or a scripting language. 


## Getting Started

1. Add the AutomaticCSV Dependancy to your Gradle or Maven Project. (TODO: Link to mvnrepository)

  ```Java
  //TODO: Maven code snippet
  ```

2. Create a Java class file in your src/main/java/ directory. (TODO: Add ability to change directory loc)
        
 ```Java
 import java.util.ArrayList;
 //TODO: import AutomaticCSV reqirements
 
 public class AutomaticCSVDemo  {
      public static void main(String[] args)
      {
        //Add additional code here
      }
 }

 ```
        
3. Create a new AutoReadCSV object initialized with the path. 

```Java
AutoReadCSV demo = new AutoReadCSV("/path/to/csv");

```
4. Add this method to create a Java class representing the CSV file. 

```Java
ArrayList<Object> csvList = demo.readCSV();
```

5. Run your main method. The Java class representing your CSV file will be generated in your source directory.

6. Access all of the methods of your newly created class by changing the ArrayList type to <GeneratedClass>. 

```Java
ArrayList<GeneratedClass> csvList = demo.readCSV();
```

7. Done! Your CSV is represented using the POJO structure. In addition, AutomaticCSV supports users adding fields and methods to the generated class so long as the original fields and constructer matching the CSV file are maintained. (Read more details below). 

### Full Demo Code: 
```Java
import java.util.ArrayList;
//TODO: import AutomaticCSV reqirements
 public class AutomaticCSVDemo  {
      public static void main(String[] args)
      {
        AutoReadCSV demo = new AutoReadCSV("/path/to/csv");
        demo.readCSV();
        ArrayList<Object> CSVasObjects = demo.readCSV();
        //Once class has been generated. 
        //ArrayList<GeneratedClass> CSVasObjects = demo.readCSV();
      }
 }
```

## Documentation






