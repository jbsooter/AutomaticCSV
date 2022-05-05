import java.util.ArrayList;

public class PrettyPrint {
    public static void printAllArrayList(ArrayList arrayList) throws IllegalAccessException {
        for(Object o: arrayList)
        {
            System.out.println(o);
        }
    }

    public static void printPreviewArrayList(ArrayList arrayList) throws IllegalAccessException {
        if(arrayList.size() < 10) {
                printAllArrayList(arrayList);
                return;
        }


        for(int i = 0; i < 5; i++)
        {
            System.out.println(arrayList.get(i));
        }


        for(int i = 0; i < 2; i++)
        {
            System.out.println(".");
        }

        for(int i = arrayList.size() - 5; i < arrayList.size(); i++)
        {
            System.out.println(arrayList.get(i));
        }

        }
    }

