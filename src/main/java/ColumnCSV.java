import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ColumnCSV {
    private String columnName;

    private Integer columnIndex;

    private List<String> columnStringArray;

    private Object columnDataType;

    ColumnCSV(String columnName, Integer columnIndex, List<String> columnStringArray) {
        //this.columnName = columnName.replaceAll("[^a-zA-Z]+", " ").trim().replaceAll("\\s", "");
        this.columnName = columnName;
        this.columnIndex = columnIndex;
        this.columnStringArray = columnStringArray;
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
