package za.ac.uct.cs.powerqope.utils;

import java.util.ArrayList;
import java.util.List;

public class StoredData {

    public static List<Long> downloadList = new ArrayList<>();
    public static List<Long> uploadList = new ArrayList<>();

    public static long downloadSpeed = 0;
    public static long uploadSpeed = 0;
    public static boolean isSetData = false;

    public static void setZero() {
        isSetData = true;
        for (int i = 0; i < 300; i++) {
            downloadList.add(0L);
            uploadList.add(0L);

        }

    }
}
