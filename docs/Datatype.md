## Datatyping Details

By default, AutomaticCSV checks every single cell of your CSV file for its datatype to ensure robustness when selecting a datatype for the corresponding field. If you have well formatted data, or a large dataset, you can use the **.setHeuristicDatatyping(true)** method to make assumptions of datatype based on the first 100 rows of the file to speed up the initial run. Once a class file has been generated for your CSV, this setting is irrelevant to parsing speed.  
### Overview

No primitive datatypes are used. All primitives can be represented using a wrapper class for enhanced functionality, so that is what we have adopted. 

**String:** "Arkansas Razorbacks"

**Double:** 12.37, NaN

**Integer:** 13, NaN

#### Enhanced Types

The ability to parse the following datatypes has been enhanced beyond what is present by default. 

#### Boolean
Columns with any combination of 0,1,True,False,true,false,Yes,No,yes,no are read in as Boolean. 

#### Dates and DateTimes
Parsed as **LocalDateTime** and **LocalDate** objects with any of the following formats. 

| Format | Example |
| ------- | -------|
| yyyy-mm-dd | 2020-12-03 |
| M/d/yyyy   | 3/12/2020 or 03/12/2020 |
| M/d/yy     | 3/12/20 or 03/12/20 |
| M-d-yyyy   | 3-12-2020 or 03-12-2020 |
| M-dd-yy    | 3-12-20 or 03-12-20 |
| yyyy/M/d   | 2020-3-12 or 2020-03-12 |
| yyyy-mm-ddThh:mm:ss | 2020-03-12T17:19:23 |
| M/d/y H:m | 03/12/22 17:19 or 3/12/2022 17:19 or 03/12/2022 17:19 |
| M/d/y H: m:s | 03/12/22 17:19:01 or 3/12/2022 17:19:01 or 03/12/2022 17:19:01 |


These dataypes were selected to support the vast majority of tabular data with as few datatypes as possible. 

[Return to Home](README.md)
