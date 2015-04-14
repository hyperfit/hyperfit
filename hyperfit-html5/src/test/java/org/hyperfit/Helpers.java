package org.hyperfit;



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



    public static void validateImageLink(String url, Set<String> alreadyValidated) throws IOException {
        if(alreadyValidated.contains(url)){
            return;
        }

        alreadyValidated.add(url);

        assertThat(url, not(isEmptyOrNullString()));
        //TODO: it really should be absolute..
        if(url.startsWith("//")){
            url = "http:" + url;
        }
        URI uri = URI.create(url);

        assertTrue("should be absolute: " + uri.toString(), uri.isAbsolute());
        assertEquals(url + " should return 200", 200, ((HttpURLConnection)uri.toURL().openConnection()).getResponseCode());


    }

    private static Random r = new Random();
    public static <T> T random(T[] array) {
        return array[r.nextInt(array.length)];
    }
}
