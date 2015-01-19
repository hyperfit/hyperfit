package org.hyperfit;

import com.bodybuilding.commerce.hyper.client.ContractConstants.Profile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class TestHelpers {
    public static LinkedHashSet<String> makeSet(String... strings){
        return new LinkedHashSet<String>(Arrays.asList(strings));
    }

    public static LinkedHashSet<String> makeSet(Profile... profiles){

        LinkedHashSet<String> m = new LinkedHashSet<String>(profiles.length);
        for(Profile p : profiles){
            m.add(p.toString());
        }
        return m;
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
