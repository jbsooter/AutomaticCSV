import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ColumnCSV {
    private String columnName;

    private Integer columnIndex;

    private List<String> columnStringArray;

    private Object columnDataType;

    public ColumnCSV(String columnName, Integer columnIndex, List<String> columnStringArray) {
        //this.columnName = columnName.replaceAll("[^a-zA-Z]+", " ").trim().replaceAll("\\s", "");
        this.columnName = cleanColumnName(columnName);
        this.columnIndex = columnIndex;
        this.columnStringArray = columnStringArray;
    }

    private String cleanColumnName(String columnName) {
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


    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public Object getColumnDataType() {
        return columnDataType;
    }

    public void setColumnDataType(Object columnDataType) {
        this.columnDataType = columnDataType.toString().substring(columnDataType.toString().lastIndexOf("." ) + 1);
    }

    public List<String> getColumnStringArray() {
        return columnStringArray;
    }

    public void setColumnStringArray(List<String> columnStringArray) {
        this.columnStringArray = columnStringArray;
    }

    @Override
    public String toString() {
        return "ColumnCSV{" +
                "columnName='" + columnName + '\'' +
                ", columnIndex=" + columnIndex +
                ", columnStringArray=" + columnStringArray +
                ", columnDataType=" + columnDataType +
                '}';
    }
}
