import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class PrettyPrint {
    public static void printAllArrayList(ArrayList arrayList) throws IllegalAccessException {
        //grab fields
        Field[] fields = arrayList.get(0).getClass().getDeclaredFields();

        for(Field f: fields)
        {
            f.setAccessible(true);
            System.out.print(String.format("    %s    |", f.getName()));
        }
        //new row
        System.out.println();

        for(int i = 0; i < arrayList.size(); i++)
        {
            for(Field f: fields)
            {
                if(f.getType().equals(Double.class))
                {
                    System.out.print(String.format("        %.2f        |",f.get(arrayList.get(i))));
                }
                System.out.print(String.format("        %s        |",f.get(arrayList.get(i))));
            }
            //new row
            System.out.println();
        }
    }

    public static void printPreviewArrayList(ArrayList arrayList) throws IllegalAccessException {
        if(arrayList.size() < 10) {
                printAllArrayList(arrayList);
                return;
        }

        //grab fields
        Field[] fields = arrayList.get(0).getClass().getDeclaredFields();
        System.out.println(Arrays.toString(fields));
        for(Field f: fields)
        {
            f.setAccessible(true);
            System.out.print(String.format("    %s    |", f.getName()));

        }
        //new row
        System.out.println();

        for(int i = 0; i < 5; i++)
        {
            for(Field f: fields)
            {
                if(f.getType().equals(Double.class))
                {
                    System.out.print(String.format("        %.2f        |",f.get(arrayList.get(i))));
                }
                System.out.print(String.format("        %s        |",f.get(arrayList.get(i))));
            }
            //new row
            System.out.println();
        }


        for(int i = 0; i < 2; i++)
        {
            System.out.println(".");
        }

        for(int i = arrayList.size() - 5; i < arrayList.size(); i++)
        {
            for(Field f: fields)
            {
                if(f.getType().equals(Double.class))
                {
                    System.out.print(String.format("        %.2f        |",f.get(arrayList.get(i))));
                }
                System.out.print(String.format("        %s        |",f.get(arrayList.get(i))));
            }
            //new row
            System.out.println();
        }
        }
    }

