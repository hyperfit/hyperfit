package test;



import org.hyperfit.resource.controls.link.HyperLink;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.*;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;


public class Helpers {
    public static HyperLink makeLink(String rel) {
        return makeLink(rel, null);
    }

    public static HyperLink makeLink(String rel, String name) {
        return new HyperLink("http://host/" + UUID.randomUUID().toString(), rel, false, null, null, name, null, null, null){};
    }

    public static LinkedHashSet<String> makeSet(String... strings){
        return new LinkedHashSet<String>(Arrays.asList(strings));
    }


    private static Random r = new Random();
    public static <T> T random(T... array) {
        return array[r.nextInt(array.length)];
    }
}
