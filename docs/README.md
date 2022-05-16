# AutomaticCSV

#### An open source library to automatically generate ArrayLists of Plain Old Java Objects for importing, sorting, and filtering data. 

### Features

- Import CSV files as ArrayLists of Java objects, with support for the Integer, Double, Boolean, String, LocalDate, and LocalDateTime datatypes. 
- Autogenerate Comparators and Predicates for sorting and filtering your data via the Stream API. 
- Pretty Print ArrayLists in tabular format. 
- Write manipulated data out to CSV. 


- Support for hosted CSVs
### Getting Started 


1. Add the AutomaticCSV dependancy to your Gradle or Maven Project. ([Maven Central](https://mvnrepository.com/artifact/io.github.jbsooter/AutomaticCSV))

  ```Java
  //Gradle
  implementation 'io.github.jbsooter:AutomaticCSV:0.1.3'
  ```
  
2. Create a new AutoReadCSV object and read in your file as an ArrayList of the top-level **Object** class. 

```Java
AutoReadCSV rCSV = new AutoReadCSV(new URL("https://raw.githubusercontent.com/jbsooter/AutomaticCSV/2121390239d2e3b4e2dd19045cb06d018e53fb83/data/menu.csv"),"Menu.csv");
//AutoReadCSV rCSV = new AutoReadCSV("/path/to/csv"); //Local CSV syntax
ArrayList<Object> csvList = rCSV.readCSV();
```

Run your program. Now, swap out the top-level **Object** class for the newly generated class, **Menu**. 
```Java
ArrayList<Menu> csvList = rCSV.readCSV();
```
3. Done!

### Further Documentation: 

[Datatyping and Import Details](Datatype.md)

[Sorting and Filtering](SortingFiltering.md)


