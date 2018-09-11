package org.hyperfit.resource.hal.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.Response;
import org.hyperfit.resource.BaseHyperResource;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.HyperResourceException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.resource.controls.link.HyperLink;
import org.hyperfit.resource.hal.json.controls.link.HalHyperLink;
import org.hyperfit.utils.StringUtils;

/**
 * Hal Json Implementation of a HyperMedia Resource
 */
@ToString(exclude = {"linkCache"})
@EqualsAndHashCode(exclude = {"linkCache"})
public class HalJsonResource extends BaseHyperResource {

    //TODO: make this configurable and give it a better name
    private static final Set<String> WHITELISTED_RESERVED_FIELD_NAMES = new HashSet<String>();

    static {
        WHITELISTED_RESERVED_FIELD_NAMES.add("_id");
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final ObjectReader OBJECT_READER = OBJECT_MAPPER.reader(JsonNode.class);
    private final JsonNode jsonResource;
    private final String baseURI;
    private final HashMap<String, HyperLink[]> linkCache = new HashMap<String, HyperLink[]>(5);

    public HalJsonResource(
        Response response
    ) {
        try {
            this.jsonResource = OBJECT_READER.readTree(response.getBody());
            this.baseURI = response.getRequest().getUrl();

        } catch (Exception ex) {
            throw new HyperfitException(
                "The response [" + response + "] cannot be read into a json tree.",
                ex
            );
        }

        if (this.jsonResource == null) {
            throw new IllegalArgumentException("jsonResource cannot be null.");
        }
    }

    public HalJsonResource(
        JsonNode jsonResource,
        String baseURI
    ) {
        if (jsonResource == null) {
            throw new IllegalArgumentException("jsonResource cannot be null.");
        }

        this.baseURI = baseURI;
        this.jsonResource = jsonResource;
    }

    /**
     * Get resource by given path and root resource
     *
     * @param root {@link JsonNode} root JsonNode to get resource
     * @param path varargs variable
     * @return {@link JsonNode}
     */
    protected JsonNode getJsonNode(
        JsonNode root,
        String... path
    ) {
        if (root == null) {
            throw new IllegalArgumentException("root cannot be null.");
        }

        if (path == null || path.length <= 0) {
            throw new IllegalArgumentException(
                "Resource data source [" + Arrays.toString(path) + "] cannot be traversed. Search path is either null or empty."
            );
        }

        JsonNode result = root.path(path[0]);

        for (int i = 1; i < path.length; i++) {
            result = result.path(path[i]);
        }

        return result;
    }


    public HyperLink[] getLinks() {
        //NOTE: if a getRels became useful this could move to baseresource adn depend on that
        java.util.Iterator<String> rels = jsonResource.path("_links").fieldNames();

        List<HyperLink> links = new ArrayList<HyperLink>();

        while(rels.hasNext()){
            Collections.addAll(links, getLinks(rels.next()));
        }

        return links.toArray(new HyperLink[links.size()]);
    }

    public HyperLink[] getLinks(String relationship) {
        if (StringUtils.isEmpty(relationship)) {
            throw new IllegalArgumentException("relationship cannot be empty");
        }

        if(!linkCache.containsKey(relationship)){
            linkCache.put(relationship, doLinkParsing(relationship, jsonResource.path("_links").path(relationship), baseURI));
        }

        return linkCache.get(relationship);
    }

    private static HyperLink[] doLinkParsing(String relationship, JsonNode matchingLinks, String baseURI){
        if(matchingLinks.isMissingNode()){
            return new HyperLink[0];
        }

        if(matchingLinks.isArray()){
            ArrayNode links = (ArrayNode)matchingLinks;
            HyperLink[] results = new HyperLink[links.size()];
            int i = 0;
            for(JsonNode link : links){
                results[i] = linkFromHalJSON(relationship, link, baseURI);
                i++;
            }

            return results;
        } else {
            return new HyperLink[]{linkFromHalJSON(relationship, matchingLinks, baseURI)};
        }
    }


    public boolean canResolveLinkLocal(String relationship) {
        return !jsonResource.path("_embedded").path(relationship).isMissingNode();
    }

    public HyperResource resolveLinkLocal(String relationship) {
        JsonNode node = jsonResource.path("_embedded").path(relationship);

        if (node.isMissingNode()) {
            throw new HyperResourceException("Embedded Resource with rel [" + relationship + "] was not found in [" + jsonResource + "]");
        }

        return new HalJsonResource(node, this.baseURI);
    }

    public HyperResource[] resolveLinksLocal(String relationship) {
        JsonNode node = jsonResource.path("_embedded").path(relationship);

        if (node.isMissingNode()) {
            throw new HyperResourceException("Embedded Resource with rel [" + relationship + "] was not found in [" + jsonResource + "]");
        }

        if(node.isArray()){
            ArrayNode resources = (ArrayNode)node;
            HalJsonResource[] results = new HalJsonResource[resources.size()];
            int i = 0;
            for(JsonNode resource : resources){
                results[i] = new HalJsonResource(resource, baseURI);
                i++;
            }

            return results;

        } else {
            return new HalJsonResource[]{ new HalJsonResource(node, baseURI) };
        }

    }



    public boolean isMultiLink(String relationship) {
        return jsonResource.path("_links").path(relationship).isArray() || jsonResource.path("_embedded").path(relationship).isArray();
    }



    public boolean hasLink(String relationship){
        //HAL can embed links, so let's check if it's embedded first, otherwise use default implementation
        return this.canResolveLinkLocal(relationship) || super.hasLink(relationship);
    }


    public boolean hasPath(String... path) {
        if (path == null || path.length == 0) return false;

        JsonNode nodeValue = getJsonNode(jsonResource, path);

        return (nodeValue != null && !nodeValue.isMissingNode());
    }

    public <T> T getPathAs(
        Class<T> classToReturn,
        boolean nullWhenMissing,
        String... path
    ) {
        //NOTE: we don't care if they try to access reserved _ stuff as a datafield...let em do it if they want
        //IE they can get to the _embedded.dog if they need to

        JsonNode nodeValue = getJsonNode(jsonResource, path);

        if (nodeValue == null || nodeValue.isMissingNode()) {
            if(nullWhenMissing){
                return null;
            } else {
                throw new HyperResourceException("Resource data with path [" + Arrays.toString(path) + "] was not found in [" + jsonResource + "]");
            }
        }

        return OBJECT_MAPPER.convertValue(nodeValue, classToReturn);
    }

    public String[] getDataFieldNames(){
        Iterator<String> fieldNames = jsonResource.fieldNames();

        ArrayList<String> names = new ArrayList<String>();

        while(fieldNames.hasNext()){
            String fieldName = fieldNames.next();

            //fields starting with _ isn't technically a reserved namespace
            //but they should be for hal
            //TODO: add some config way to override this behaviour for weird fields
            if(!fieldName.startsWith("_") || WHITELISTED_RESERVED_FIELD_NAMES.contains(fieldName)){
                names.add(fieldName);
            }
        }

        return names.toArray(new String[names.size()]);

    }


    //TODO: we could use data binding here if that were faster...and any of us had a clue how to use JACKSON
    public static HyperLink linkFromHalJSON(String relationship, JsonNode node, String baseURI){
        JsonNode href = node.path("href");
        JsonNode template = node.path("templated");
        JsonNode type = node.path("type");
        JsonNode profile = node.path("profile");
        JsonNode title = node.path("title");
        JsonNode name = node.path("name");
        JsonNode hrefLang = node.path("hrefLang");
        JsonNode deprecation = node.path("deprecation");

        if (href.isMissingNode()) {
            throw new IllegalArgumentException("Malformed HAL link node. href field not present in json: " + node.toString());
        }



        return new HalHyperLink(
            href.textValue(),
            relationship,
            template.asBoolean(false),
            type.isMissingNode() ? null : type.textValue(),
            deprecation.isMissingNode() ? null : deprecation.textValue(),
            name.isMissingNode() ? null : name.textValue(),
            profile.isMissingNode() ? null : profile.textValue(),
            title.isMissingNode() ? null : title.textValue(),
            hrefLang.isMissingNode() ? null : hrefLang.textValue(),
            baseURI
        );
    }


    @Override
    public Form getForm(String formName) {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean hasForm(String formName) {
        return false;
    }

    @Override
    public Form[] getForms() {
        return new Form[0];
    }
}
