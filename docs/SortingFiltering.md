## Sorting and Filtering
By default, AutomaticCSV generates custom Predicates and Comparators that allow the user to filter ArrayLists of data in a variety of ways using the same Java methods of the ArrayList class available if they had written comparators themselves. 

### Sorting
For all datatypes, AutomaticCSV generates Comparators to sort by a field in ascending or descending order. These methods can be used with the .sort() method of the ArrayList class to sort the CSV data. 
By default, these methods are housed within the AutomaticCSV class to prevent namespace issues. 

**General Comparator Format**

```Java
  listName.Sort{Ascending/Descending}By{FieldName}
```

**Single Level Sort Example**

```Java
  auto_data.sort(new Autompg.SortAscendingBympg());
```

**Multi-level Sort Example**

```java
 auto_data.sort(new Autompg.SortAscendingBympg().thenComparing(new Autompg.SortDescendingByorigin()));
```

### Filtering

For all datatypes, AutomaticCSV generates predicates that enable filtering of Lists through the Streams API. 

**Standard Syntax**
```
ArrayList<Object> filteredList = listName.stream().filter(csvClass.{field}{isGreaterThanOrEqualTo(value)/isLessThanOrEqualTo(value)}).collect(Collectors.toCollection(ArrayList::new));
```

**LocalDate and LocalDateTime Syntax**
```Java
ArrayList<Object> filteredList = listName.stream().filter(csvClass.{field}{isBefore(date)/isAfter(date)}).collect(Collectors.toCollection(ArrayList::new));
```

Additionally, you can return only records with distinct values for a given field. 

**Distinct Records Syntax**
```Java
ArrayList<Object> filteredList = listName.stream().filter(csvClass.distinctBy{field}).collect(Collectors.toCollection(ArrayList::new));
```

["Return to Home"]("to do")
