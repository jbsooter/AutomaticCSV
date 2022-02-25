# AutomaticCSV
#### :rocket: Lightweight Java Library to Automatically Parse any CSV File Into Java Objects 
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jbsooter/AutomaticCSV.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.jbsooter%22%20AND%20a:%22AutomaticCSV%22)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Take advantage of the flexibility and power of the traditional Java object structure with the "one line" convenience of reading a CSV with Tablesaw or a scripting language. 


## Getting Started

1. Add the AutomaticCSV dependancy to your Gradle or Maven Project. ([Maven Central](https://mvnrepository.com/artifact/io.github.jbsooter/AutomaticCSV))

  ```Java
  implementation 'io.github.jbsooter:AutomaticCSV:0.1.1'
  ```

2. Create a Java class file in your Gradle project.
        
 ```Java
 import java.util.ArrayList;
 
 public class AutomaticCSVDemo  {
      public static void main(String[] args)
      {
        //Add code here
      }
 }

 ```
        
3. Create a new AutoReadCSV object initialized with the path. 

```Java
AutoReadCSV rCSV = new AutoReadCSV("/path/to/csv");

```
4. Automatically create a Java class mapped to your CSV and read in the CSV as an ArrayList of objects.  

```Java
ArrayList<Object> csvList = demo.readCSV();
```

5. Run your main method. The Java class representing your CSV file will appear in your source directory.

6. Once you have read in your CSV once, you can change the type of your ArrayList from <Object> to <YourCSVClassName> to take advantage of all class methods. 

```Java
ArrayList<GeneratedClass> csvList = demo.readCSV();
```

7. Done! Your CSV is represented using the POJO structure. In addition, AutomaticCSV supports users adding fields and methods to the generated class so long as the original annotated fields and constructer matching the CSV file are maintained. (Read more details below). 

### Full Demo Code: 
```Java
 import java.util.ArrayList;
 
 public class AutomaticCSVDemo  {
      public static void main(String[] args)
      {
        AutoReadCSV rCSV = new AutoReadCSV("/path/to/csv");
        ArrayList<Object> csvList = demo.readCSV();
        //ArrayList<GeneratedClass> csvList = demo.readCSV();
      }
 }
```

## Documentation

TODO




