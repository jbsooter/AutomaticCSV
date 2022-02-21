import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BenchmarkTests {
    public static void runBenchmarkTests()
    {
        Map<String, Long> results = new HashMap<>();

        AutoReadCSV testRead = new AutoReadCSV("benchmark_data/allBooleanTenThousand.csv");
        long start = System.currentTimeMillis();
        ArrayList<Object>  o  = testRead.readCSV();
        results.put("allBooleanTenThousandInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allBooleanTenThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allBooleanTenThousandWithClass", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allBooleanOneHundredThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allBooleanOneHundredThousandInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allBooleanOneHundredThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allBooleanOneHundredThousandWithClass", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allBooleanOneMillion.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allBooleanOneMillionInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allBooleanOneMillion.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allBooleanOneMillionWithClass", System.currentTimeMillis() - start);

        //String
        testRead = new AutoReadCSV("benchmark_data/allStringTenThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allStringTenThousandInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allStringTenThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allStringTenThousandWithClass", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allStringOneHundredThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allStringOneHundredThousandInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allStringOneHundredThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allStringOneHundredThousandWithClass", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allStringOneMillion.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allStringOneMillionInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allStringOneMillion.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allStringOneMillionWithClass", System.currentTimeMillis() - start);

        //LocalDate
        testRead = new AutoReadCSV("benchmark_data/allLocalDateTenThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allLocalDateTenThousandInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allLocalDateTenThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allLocalDateTenThousandWithClass", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allLocalDateOneHundredThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allLocalDateOneHundredThousandInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allLocalDateOneHundredThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allLocalDateOneHundredThousandWithClass", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allLocalDateOneMillion.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allLocalDateOneMillionInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allLocalDateOneMillion.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allLocalDateOneMillionWithClass", System.currentTimeMillis() - start);

        //Double
        testRead = new AutoReadCSV("benchmark_data/allDoubleTenThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allDoubleTenThousandInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allDoubleTenThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allDoubleTenThousandWithClass", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allDoubleOneHundredThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allDoubleOneHundredThousandInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allDoubleOneHundredThousand.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allDoubleOneHundredThousandWithClass", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allDoubleOneMillion.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allDoubleOneMillionInitial", System.currentTimeMillis() - start);

        testRead = new AutoReadCSV("benchmark_data/allDoubleOneMillion.csv");
        start = System.currentTimeMillis();
        o  = testRead.readCSV();
        results.put("allDoubleOneMillionWithClass", System.currentTimeMillis() - start);



        System.out.println(mapToString(results));

    }


    public static String mapToString(Map<String,Long> map)
    {
        TreeMap<String, Long> mapTree = new TreeMap<>();

        mapTree.putAll(map);

        return mapTree.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue()/1000.0)
                .collect(Collectors.joining("\n"));
    }
}
