package za.ac.uct.cs.powerqope.fragment;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class urlTest {
    // to handle exceptions include throws
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args)
    {
        List<String> listOfStrings = new ArrayList<String>();
        try {
            listOfStrings = Files.readAllLines(Paths.get("app/src/main/assets/sites.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] links = listOfStrings.toArray(new String[0]);

        for (int i = 0; i < links.length; i++) {

            System.out.println(links[i]);

        }

    }
}