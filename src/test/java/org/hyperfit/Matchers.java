package org.hyperfit;


import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;


public class Matchers {
    public static interface Function2<F, G, T> {
         T apply(F input1, G input2);
    }

    public static <T> org.hamcrest.Matcher<T[]> hasDuplicateInArray() {
        return IsArrayWithDuplicates.<T>array();
    }

    public static <T> org.hamcrest.Matcher<T[]> hasDuplicateInArray(Function2<T, T, Boolean> equalsFunction) {
        return IsArrayWithDuplicates.<T>array(equalsFunction);
    }

    public static class IsArrayWithDuplicates<T> extends TypeSafeMatcher<T[]> {

        private final Function2<T, T, Boolean> equalsFunction;

        public IsArrayWithDuplicates() {
            this.equalsFunction = new Function2<T, T, Boolean>() {
                public Boolean apply(T input1, T input2) {
                    return input1.equals(input2);
                }
            };
        }

        public IsArrayWithDuplicates(Function2<T, T, Boolean> equalsFunction) {
            this.equalsFunction = equalsFunction;
        }

        @Override
        public boolean matchesSafely(T[] array) {
            //simple case, can't be dupes in an empty or 1 entry array
            if(array.length < 2) return false;

            ArrayList<T> itemsSeen = new ArrayList<T>();

            for(final T t : array) {

                //check if we've already seen it
                T found = Iterables.find(itemsSeen, new Predicate<T>() {
                        public boolean apply(T input) {
                            return equalsFunction.apply(t, input);
                        }
                    },
                    null);

                //If it was found..then there are duplicates
                if (found != null) {
                    return true;
                }

                itemsSeen.add(t);
            }


            //if we never find a dupe..then there are no dupes
            return false;
        }

        @Override
        public void describeMismatchSafely(T[] actual, Description mismatchDescription) {
            mismatchDescription.appendText("TODO: explain what dupes there are");
        }

        public void describeTo(Description description) {

            description.appendText("duplicates in array");

        }



        /**
         * Creates a matcher that matches arrays whose elements are satisfied by the specified matchers.  Matches
         * positively only if the number of matchers specified is equal to the length of the examined array and
         * each matcher[i] is satisfied by array[i].
         * <p/>
         * For example:
         * <pre>assertThat(new Integer[]{1,2,3}, is(array(equalTo(1), equalTo(2), equalTo(3))))</pre>
         *
         */
        @Factory
        public static <T> IsArrayWithDuplicates<T> array() {
            return new IsArrayWithDuplicates<T>();
        }


        @Factory
        public static <T> IsArrayWithDuplicates<T> array(Function2<T, T, Boolean> equalsFunction) {
            return new IsArrayWithDuplicates<T>(equalsFunction);
        }

    }
}
