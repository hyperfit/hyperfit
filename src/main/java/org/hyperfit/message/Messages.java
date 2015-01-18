package org.hyperfit.message;

/**
 * Collection of output messages
 */
public class Messages {

    private Messages() {
    }

    // -- CACHE RELATED --
    public static final String MSG_WARN_CACHE_FOUND_EMPTY_KEY
            = "Cache found an empty key. No empty key will be saved in cache. "
            + "All items with empty keys will be addressed as invalid."
            + "Any item being extracted with an empty key will result in null.";

    public static final String MSG_DEBUG_CACHE_ITEM_SAVED = "Cache item with key [{}] value [{}] was saved in cache.";
    public static final String MSG_DEBUG_CACHE_ITEM_REMOVED = "Cache item with key [{}] was removed from cache.";
    public static final String MSG_DEBUG_CACHE_ITEMS_REMOVED = "Cache item with key [{}] class [{}] was retrieved from cache [{}].";
    public static final String MSG_DEBUG_CACHE_ITEM_FOUND = "Cache item with key [{}] class [{}] was found in cache.";
    public static final String MSG_DEBUG_CACHE_ITEM_REFRESH = "Cache item with key [{}] is being refreshed.";
    public static final String MSG_ERROR_CACHE_ITEM_REFRESH = "Error while refreshing item with key [{}].";
    public static final String MSG_ERROR_CACHE_CAST_CLASS_NULL = "Error while getting item from cache. Cast class cannot be null.";
    public static final String MSG_ERROR_CACHE_REFRESH_ACTION_NULL = "Error while getting item from cache. Refresh action cannot be null.";
    public static final String MSG_ERROR_CACHE_CACHE_MAP_NULL = "Error while creating cache strategy. Cache map cannot be null.";

    // -- MEDIATYPE RELATED --
    public static final String MSG_ERROR_MEDIATYPE_CANNOT_CREATE_RESOURCE = "The raw message [{}] cannot be transformed to the following resource [{}].";
    public static final String MSG_ERROR_MEDIATYPE_HYPER_RESPONSE_NULL = "Error while transforming hyper response to hyper resource. Hyper response cannot be null.";

    // -- PROVIDER RELATED --
    public static final String MSG_DEBUG_CLIENT_REQUEST = "Provider executing request [{}].";
    public static final String MSG_DEBUG_CLIENT_RESPONSE = "Provider generating response [{}].";
    public static final String MSG_ERROR_CLIENT_REQUEST_FAILURE = "The request [{}] could not be executed.";
    public static final String MSG_ERROR_CLIENT_REQUEST_RESPONSE_FAILURE = "The response [{}] could not be generated correctly.";
    public static final String MSG_ERROR_CLIENT_REQUEST_NULL = "Error while generating client request. Request cannot be null.";
    public static final String MSG_ERROR_CLIENT_REQUEST_URL_NULL = "Error while generating client request. Request url cannot be empty.";
    public static final String MSG_ERROR_CLIENT_REQUEST_METHOD_NULL = "Error while generating client request. Request method cannot be null.";
    public static final String MSG_ERROR_CLIENT_NULL = "Error while creating http client. Client cannot be null.";

    // -- REQUEST RELATED --
    public static final String MSG_ERROR_REQUEST_URL_CANNOT_BE_EXPANDED = "Request url [{}] cannot be expanded with [{}]";
    public static final String MSG_ERROR_REQUEST_URL_EMPTY = "Request url cannot be empty or null.";
    public static final String MSG_ERROR_REQUEST_METHOD_NULL = "Request method cannot be null.";
    public static final String MSG_ERROR_REQUEST_CONTENT_TYPE_EMPTY = "Request content type cannot be empty or null.";
    public static final String MSG_ERROR_REQUEST_CONTENT_BODY_NULL = "Request content body cannot be null.";
    public static final String MSG_ERROR_REQUEST_HEADER_NAME_EMPTY = "Request header cannot be empty or null.";
    public static final String MSG_ERROR_REQUEST_URL_PARAM_NAME_EMPTY = "Request url parameter cannot be empty or null.";

    // -- RESPONSE RELATED --
    public static final String MSG_ERROR_RESPONSE_HEADER_NAME_EMPTY = "Response header cannot be empty or null.";
    public static final String MSG_ERROR_HYPER_MEDIA_TYPE_HANDLER_NOT_FOUND_FOR_CONTENT_TYPE = "Hyper media type handLer not found for content type [{}]";
    public static final String MSG_ERROR_NO_CONTENT_TYPE = "Response must have a content type";

    // -- RESOURCE RELATED --
    public static final String MSG_ERROR_RESOURCE_DATA_SOURCE_NULL = "Resource data source cannot be null.";
    public static final String MSG_ERROR_RESOURCE_LINK_NOT_FOUND = "Resource link with path {} was not found in [{}]";
    public static final String MSG_ERROR_RESOURCE_DATA_SOURCE_CANNOT_BE_TRAVERSED = "Resource data source [%s] cannot be traversed. Search path is either null or empty.";

    public static final String MSG_ERROR_RESOURCE_CANNOT_BE_BUILT = "Resource cannot be built";

    //LINK Related
    public static final String MSG_ERROR_LINK_WITHOUT_REL = "Link must have a relationship";
    public static final String MSG_ERROR_LINK_WITH_NAME_NOT_FOUND = "Could not find a link with relationship [{}] and name [{}]";
    public static final String MSG_ERROR_LINK_WITH_NAME_FOUND_MORE_THAN_ONE = "Found more than one link with relationship [{}] and name [{}]";
    public static final String MSG_ERROR_LINK_NOT_FOUND = "Could not find a link with relationship [{}]";
    public static final String MSG_ERROR_LINK_FOUND_MORE_THAN_ONE = "Found more than one link with relationship [{}]";

    // -- REFLECTION RELATED --
    public static final String MSG_ERROR_REFLECTION_CANNOT_CAST = "Cannot cast object [{}] to [{}].";


    // -- PROXY RELATED --
    public static final String MSG_ERROR_PROXY_CANNOT_HANDLE_METHOD_INVOCATION
            = "Cannot handle proxy method invocation [{}] in [{}] with arguments {}.";

    public static final String MSG_ERROR_LOOKING_FOR_PARAM = "No type param called [{}] was found in typeParamsLookup map";
    
    public static final String MSG_ERROR_PROXY_UNEXPECTED_ERROR =
            "Unexpected error occurred when handling proxy method invocation [{}] in [{}] with arguments {}.";

}
