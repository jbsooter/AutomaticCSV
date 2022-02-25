# AutomaticCSV
#### :rocket: Lightweight Java Library to Automatically Parse any CSV File Into Java Objects 
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jbsooter/AutomaticCSV.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.jbsooter%22%20AND%20a:%22AutomaticCSV%22)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Take advantage of the flexibility and power of the traditional Java object structure with the "one line" convenience of reading a CSV with Tablesaw or a scripting language. 


## Getting Started

1. Add the AutomaticCSV dependancy to your Gradle or Maven Project. ([Maven Central](https://mvnrepository.com/artifact/io.github.jbsooter/AutomaticCSV))

  ```Java
  //Gradle
  implementation 'io.github.jbsooter:AutomaticCSV:0.1.1'
  ```
  
  ```Java
  //Maven
  <dependency>
  <groupId>io.github.jbsooter</groupId>
  <artifactId>AutomaticCSV</artifactId>
  <version>0.1.1</version>
</dependency>
  ```

2. Create a Java class file "AutomaticCSVDemo".
        
 ```Java
 import java.util.ArrayList;
 
 public class AutomaticCSVDemo  {
      public static void main(String[] args)
      {
        //Add code here
      }
 }

 ```
        
3. Create a new AutoReadCSV object initialized with the path of your CSV. 

```Java
AutoReadCSV rCSV = new AutoReadCSV("/path/to/csv");

```
4. Automatically create a Java class mapped to your CSV and read in the CSV as an ArrayList of objects.  

```Java
ArrayList<Object> csvList = rCSV.readCSV();
```

5. Run your main method. The Class mapped to your CSV will appear in your source directory. 
6. Now that the class is created, you can change the type of your ArrayList to the name of the generated class to access all defined class methods. 
```Java
ArrayList<GeneratedClass> csvList = rCSV.readCSV();
```
7. Print your objects to the console to verify that they were read in correctly. 
```Java
for(Object row: csvList)
{
System.out.println(row.toString());
}
```
8. That's it! Read more about the details in the documentation below. 


Full Demo Code:

```Java
import java.util.ArrayList;

public class AutomaticCSVDemo  {
     public static void main(String[] args)
     {
       AutoReadCSV rCSV = new AutoReadCSV("/path/to/csv");
       ArrayList<Object> csvList = rCSV.readCSV();
       //after your code has ran once, the class mapped to your CSV will be generated
       //and can be specified as the type of your ArrayList
       //ArrayList<GeneratedClass> csvList = rCSV.readCSV();
       
       for(Object row: csvList)
       {
          System.out.println(row.toString());
       }
     }
}

```
## Documentation

TODO




