/*
import java.io.FileWriter;
import java.io.IOException;

class GenerateBenchmarkTests {

    public static void generateBooleanTests() throws IOException {
        FileWriter f = new FileWriter("benchmark_data/allBooleanTenThousand.csv");
        for(int i = 0; i <10000; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                f.write("yes,no,true,false,1,0,");
            }
            f.write("yes\n");

        }
        f.close();

        f = new FileWriter("benchmark_data/allBooleanOneHundredThousand.csv");
        for(int i = 0; i <100000; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                f.write("yes,no,true,false,1,0,");
            }
            f.write("yes\n");

        }
        f.close();

        f = new FileWriter("benchmark_data/allBooleanOneMillion.csv");
        for(int i = 0; i <10000; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                f.write("yes,no,true,false,1,0,");
            }
            f.write("yes\n");

        }
        f.close();

    }

    public static void generateStringTests() throws IOException {
        FileWriter f = new FileWriter("benchmark_data/allStringTenThousand.csv");
        for(int j = 0; j < 10000; j++)
        {
            for(int i = 0; i < 24; i++)
            {
                f.write("test,");
            }

            f.write("test\n");
        }
        f.close();

        f = new FileWriter("benchmark_data/allStringOneHundredThousand.csv");
        for(int j = 0; j < 100000; j++)
        {
            for(int i = 0; i < 24; i++)
            {
                f.write("test,");
            }

            f.write("test\n");
        }
        f.close();

        f = new FileWriter("benchmark_data/allStringOneMillion.csv");
        for(int j = 0; j < 1000000; j++)
        {
            for(int i = 0; i < 24; i++)
            {
                f.write("test,");
            }

            f.write("test\n");
        }
        f.close();
    }

    public static void generateDoubleTests() throws IOException {
        FileWriter f = new FileWriter("benchmark_data/allDoubleTenThousand.csv");
        for(int j = 0; j < 10000; j++)
        {
            for(int i = 0; i < 24; i++)
            {
                f.write(String.format("%s,", Math.random()));
            }

            f.write(String.format("%s\n", Math.random()));
        }
        f.close();

        f = new FileWriter("benchmark_data/allDoubleOneHundredThousand.csv");
        for(int j = 0; j < 100000; j++)
        {
            for(int i = 0; i < 24; i++)
            {
                f.write(String.format("%s,", Math.random()));
            }

            f.write(String.format("%s\n", Math.random()));
        }
        f.close();

        f = new FileWriter("benchmark_data/allDoubleOneMillion.csv");
        for(int j = 0; j < 1000000; j++)
        {
            for(int i = 0; i < 24; i++)
            {
                f.write(String.format("%s,", Math.random()));
            }

            f.write(String.format("%s\n", Math.random()));
        }
        f.close();
    }

    public static void generateLocalDateTests() throws IOException {
        FileWriter f = new FileWriter("benchmark_data/allLocalDateTenThousand.csv");
        for(int j = 0; j < 10000; j++)
        {
            for(int i = 0; i < 4; i++)
            {
                f.write(String.format("01-01-2021,01/01/2021,01/01/21,01-01-2021,01-01-21,2021/01/01,"));
            }

            f.write("01-01-2021\n");

        }
        f.close();

        f = new FileWriter("benchmark_data/allLocalDateOneHundredThousand.csv");
        for(int j = 0; j < 100000; j++)
        {
            for(int i = 0; i < 4; i++)
            {
                f.write(String.format("01-01-2021,01/01/2021,01/01/21,01-01-2021,01-01-21,2021/01/01,"));
            }

            f.write("01-01-2021\n");

        }
        f.close();


        f = new FileWriter("benchmark_data/allLocalDateOneMillion.csv");
        for(int j = 0; j < 1000000; j++)
        {
            for(int i = 0; i < 4; i++)
            {
                f.write(String.format("01-01-2021,01/01/2021,01/01/21,01-01-2021,01-01-21,2021/01/01,"));
            }

            f.write("01-01-2021\n");

        }
        f.close();
    }

    public static void generateAllTests() throws IOException {
        generateBooleanTests();
        generateDoubleTests();
        generateStringTests();
        generateLocalDateTests();
    }
}

 */
