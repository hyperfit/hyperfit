package org.hyperfit.resource.hal.json;

import org.hyperfit.exception.HyperClientException;
import org.hyperfit.message.Messages;
import org.hyperfit.http.Response;
import org.hyperfit.resource.BaseHyperResource;
import org.hyperfit.resource.HyperLink;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.HyperResourceException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.utils.StringUtils;

/**
 * Hal Json Implementation of a HyperMedia Resource
 */
@ToString(exclude = {"linkCache"})
@EqualsAndHashCode(exclude = {"linkCache"})
public class HalJsonResource extends BaseHyperResource {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectReader OBJECT_READER = OBJECT_MAPPER.reader(JsonNode.class);
    private final JsonNode jsonResource;

    private final HashMap<String, HyperLink[]> linkCache = new HashMap<String, HyperLink[]>(5);

    public HalJsonResource(Response response) {
        try {
            this.jsonResource = OBJECT_READER.readTree(response.getBody());
        } catch (Exception ex) {
            throw new HyperClientException(ex,
                    Messages.MSG_ERROR_MEDIATYPE_CANNOT_CREATE_RESOURCE,
                    response, HalJsonResource.class);
        }

        if (this.jsonResource == null) {
            throw new NullPointerException(Messages.MSG_ERROR_RESOURCE_DATA_SOURCE_NULL);
        }
    }

    public HalJsonResource(JsonNode jsonResource) {
        if (jsonResource == null) {
            throw new NullPointerException(Messages.MSG_ERROR_RESOURCE_DATA_SOURCE_NULL);
        }

        this.jsonResource = jsonResource;
    }

    /**
     * Get resource by given path and root resource
     *
     * @param root {@link JsonNode} root JsonNode to get resource
     * @param path varargs variable
     * @return {@link JsonNode}
     */
    protected JsonNode getJsonNode(JsonNode root, String... path) {
        if (root == null) {
            throw new NullPointerException(Messages.MSG_ERROR_RESOURCE_DATA_SOURCE_NULL);
        }

        if (path == null || path.length <= 0) {
            throw new IllegalArgumentException(
                    String.format(Messages.MSG_ERROR_RESOURCE_DATA_SOURCE_CANNOT_BE_TRAVERSED, Arrays.toString(path)));
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
            throw new IllegalArgumentException(Messages.MSG_ERROR_LINK_WITHOUT_REL);
        }

        if(!linkCache.containsKey(relationship)){
            linkCache.put(relationship, doLinkParsing(relationship, jsonResource.path("_links").path(relationship)));
        }

        return linkCache.get(relationship);
    }

    private static HyperLink[] doLinkParsing(String relationship, JsonNode matchingLinks){
        if(matchingLinks.isMissingNode()){
            return new HyperLink[0];
        }

        if(matchingLinks.isArray()){
            ArrayNode links = (ArrayNode)matchingLinks;
            HyperLink[] results = new HyperLink[links.size()];
            int i = 0;
            for(JsonNode link : links){
                results[i] = linkFromHalJSON(relationship, link);
                i++;
            }

            return results;
        } else {
            return new HyperLink[]{linkFromHalJSON(relationship, matchingLinks)};
        }
    }


    public boolean canResolveLinkLocal(String relationship) {
        return !jsonResource.path("_embedded").path(relationship).isMissingNode();
    }

    public HyperResource resolveLinkLocal(String relationship) {
        JsonNode node = jsonResource.path("_embedded").path(relationship);

        if (node.isMissingNode()) {
            throw new HyperResourceException(Messages.MSG_ERROR_RESOURCE_LINK_NOT_FOUND, relationship, jsonResource);
        }

        return new HalJsonResource(node);
    }

    public HyperResource[] resolveLinksLocal(String relationship) {
        JsonNode node = jsonResource.path("_embedded").path(relationship);

        if (node.isMissingNode()) {
            throw new HyperResourceException(Messages.MSG_ERROR_RESOURCE_LINK_NOT_FOUND, relationship, jsonResource);
        }

        if(node.isArray()){
            ArrayNode resources = (ArrayNode)node;
            HalJsonResource[] results = new HalJsonResource[resources.size()];
            int i = 0;
            for(JsonNode resource : resources){
                results[i] = new HalJsonResource(resource);
                i++;
            }

            return results;

        } else {
            return new HalJsonResource[]{ new HalJsonResource(node) };
        }

    }



    public boolean isMultiLink(String relationship) {
        return jsonResource.path("_links").path(relationship).isArray() || jsonResource.path("_embedded").path(relationship).isArray();
    }


    public LinkedHashSet<String> getProfiles(){
        HyperLink[] profileLinks = this.getLinks("profile");
        LinkedHashSet<String> profiles = new LinkedHashSet<String>(profileLinks.length);
        for(HyperLink l : profileLinks){
            profiles.add(l.getHref());
        }

        return profiles;

    }

    public boolean hasLink(String relationship){
        //HAL can embed links, so let's check if it's embedded first, otherwise use default implementation
        return this.canResolveLinkLocal(relationship) || super.hasLink(relationship);
    }


    public boolean hasPath(String... path) {
        JsonNode nodeValue = getJsonNode(jsonResource, path);

        return (nodeValue != null && !nodeValue.isMissingNode());
    }

    public <T> T getPathAs(Class<T> classToReturn, String... path) {
        JsonNode nodeValue = getJsonNode(jsonResource, path);

        if (nodeValue == null || nodeValue.isMissingNode()) {
            throw new HyperResourceException(Messages.MSG_ERROR_RESOURCE_LINK_NOT_FOUND, path, jsonResource);
        }

        return OBJECT_MAPPER.convertValue(nodeValue, classToReturn);
    }


    //TODO: we could use data binding here if that were faster...and any of us had a clue how to use JACKSON
    public static HyperLink linkFromHalJSON(String relationship, JsonNode node){
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

        return new HyperLink(
            href.textValue(),
            relationship,
            template.asBoolean(false),
            type.isMissingNode() ? null : type.textValue(),
            deprecation.isMissingNode() ? null : deprecation.textValue(),
            name.isMissingNode() ? null : name.textValue(),
            profile.isMissingNode() ? null : profile.textValue(),
            title.isMissingNode() ? null : title.textValue(),
            hrefLang.isMissingNode() ? null : hrefLang.textValue()
        );
    }

}
