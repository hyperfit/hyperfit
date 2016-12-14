package org.hyperfit.content;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.utils.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Based upon code from Matthew Champion https://bitbucket.org/mattunderscorechampion/ws-utils/src/070667099baa860db341fcbd145dee56affa3c38/structured-http-headers/?at=default
 *
 * Copyright (c) 2012, Matthew Champion
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of mattunderscore.com nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL MATTHEW CHAMPION BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

@EqualsAndHashCode
@ToString
public class ContentType {

    public static ContentType parse(String contentType) {

        if (StringUtils.isEmpty(contentType)) {
            throw new IllegalArgumentException("contentType");
        }


        final String[] typeParts = contentType.trim().split("/");
        if (typeParts.length != 2) {
            throw new IllegalArgumentException("Unparsable content type: " + contentType.trim() + ": " + typeParts.length);
        }


        final String[] lastParts = typeParts[1].trim().split(";", -1);
        final Map<String, String> map = new HashMap<String, String>();
        final double qualifier = parseParameters(lastParts, map);
        return new ContentType(typeParts[0].trim(), lastParts[0].trim(), map, qualifier);

    }

    /**
     * Parse the parameters of the content type.
     *
     * @param parameters Parameter array
     * @param map Parameter map
     * @return Qualifier
     */
    private static double parseParameters(String[] parameters, Map<String, String> map) {
        double qualifier = 1.0;

        if (parameters.length == 0) {
            throw new IllegalArgumentException("Unparsable content type: " + Arrays.toString(parameters));
        }

        //skip the first one cause it's the subtype
        for (int i = 1; i < parameters.length; i++) {
            final String[] extensions = parameters[i].trim().split("=", -1);

            if (extensions.length != 2) {
                throw new IllegalArgumentException("Invalid parameter: " + parameters[i]);
            }


            if ("q".equals(extensions[0].trim())) {
                qualifier = Double.parseDouble(extensions[1].trim());

                if (qualifier < 0.0 || qualifier > 1.0) {
                    throw new IllegalArgumentException("Invalid qualifier range.");
                }
            } else {
                map.put(extensions[0].trim(), extensions[1].trim());
            }
        }

        return qualifier;
    }


    private static final String WILDCARD = "*";

    private final String type;
    private final String subType;
    private final Map<String, String> parameters;
    private final double qualifier;

    private String toString;

    public ContentType(
        String type,
        String subType
    ) {
        this(type, subType, null, 1.0d);
    }

    /**
     * Constructor for qualified content types.
     * <P>
     * Creates an immutable object. The type and subtype may not be null. The value of the qualifier
     * must be between 0.0 and 1.0.
     *
     * @param type
     *            Type of the content type
     * @param subType
     *            Subtype of the content type
     * @param parameters
     *            Map of additional parameters
     * @param qualifier
     *            The qualifier of the content type
     * @throws IllegalArgumentException
     *             If type or subtype are null or if the qualifier is less than zero or greater than
     *             one
     */
    public ContentType(
        String type,
        String subType,
        Map<String, String> parameters,
        double qualifier
    ){
        if (type == null) {
            throw new IllegalArgumentException("Type must not be null.");
        }

        if (subType == null) {
            throw new IllegalArgumentException("SubType must not be null.");
        }

        if (qualifier < 0.0 || qualifier > 1.0) {
            throw new IllegalArgumentException("Qualifier must be between 0.0 and 1.0");
        }


        this.type = type;
        this.subType = subType;
        if (parameters != null) {
            this.parameters = Collections.unmodifiableMap(parameters);
        } else {
            this.parameters = Collections.unmodifiableMap(new HashMap<String, String>());
        }
        this.qualifier = qualifier;

    }

    /**
     * Returns a string representation of the object.
     * <P>
     * The string representation of the object is constructed lazily, it may be initialised multiple
     * times and may not be aware of other threads setting it. The objects returned will be equal
     * regardless of the thread returning the value.
     *
     * @return A string representation of this object
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            String theToString = type + "/" + subType + ";q=" + getQualifier();
            if (parameters != null) {
                for (final String name : parameters.keySet()) {
                    theToString += ";" + name + "=" + parameters.get(name);
                }
            }
            this.toString = theToString;
        }

        return this.toString;
    }

    public String toString(boolean includeParams) {
        if(includeParams){
            return this.toString();
        }

        return type + "/" + subType;
    }

    public final double getQualifier() {
        return qualifier;
    }

    /**
     * Method that returns true if the two content types can be matched against one another. Takes
     * into account wildcard matching.
     *
     * @param qct
     * @return True if they match
     */
    public final boolean compatibleWith(ContentType qct) {
        if (type.equals(qct.type) && subType.equals(qct.subType)) {
            return true;
        }

        if (type.equals(qct.type) && (WILDCARD.equals(subType) || WILDCARD.equals(qct.subType))) {
            return true;
        }

        if ((WILDCARD.equals(type) || WILDCARD.equals(qct.type)) && subType.equals(qct.subType)) {
            return true;
        }

        return ((WILDCARD.equals(type) || WILDCARD.equals(qct.type)) && (WILDCARD.equals(subType) || WILDCARD.equals(qct.subType)));

    }

    public ContentType withQ(double q){
        return new ContentType(type, subType, parameters, q);
    }


}
