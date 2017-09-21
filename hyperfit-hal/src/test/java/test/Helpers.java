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


    private static Random r = new Random();
    public static <T> T random(T... array) {
        return array[r.nextInt(array.length)];
    }

    public static String uniqueString(){
        return UUID.randomUUID().toString();
    }
}
