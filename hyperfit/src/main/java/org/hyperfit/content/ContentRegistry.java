package org.hyperfit.content;

import org.javatuples.Pair;

import java.util.*;

public class ContentRegistry {
    public static enum Purpose {
        PARSE_RESPONSE,
        PREPARE_REQUEST
        ;
    }

    private final List<Pair<ContentType, ContentTypeHandler>> typeRegistry = new ArrayList<Pair<ContentType, ContentTypeHandler>>();

    public ContentRegistry(){

    }

    public ContentRegistry(ContentRegistry source){
        typeRegistry.addAll(source.typeRegistry);
    }

    public void add(ContentTypeHandler handler, ContentType... types){
        if(handler == null){
            throw new IllegalArgumentException("handler cannot be null");
        }

        for( ContentType t : types){
            if(t != null) {
                typeRegistry.add(Pair.with(t, handler));
            }
        }
    }


    public void add(ContentTypeHandler handler){
        this.add(handler, handler.getDefaultContentType());
    }

    public void remove(ContentType type){
        if(type == null){
            throw new IllegalArgumentException("type cannot be null");
        }

        Iterator<Pair<ContentType,ContentTypeHandler>> it = typeRegistry.iterator();
        while(it.hasNext()){
            Pair<ContentType,ContentTypeHandler> entry = it.next();
            if(entry.getValue0().equals(type)){
                it.remove();
            }
        }

    }

    public void remove(ContentTypeHandler handler){
        if(handler == null){
            throw new IllegalArgumentException("handler cannot be null");
        }

        Iterator<Pair<ContentType,ContentTypeHandler>> it = typeRegistry.iterator();
        while(it.hasNext()){
            Pair<ContentType,ContentTypeHandler> entry = it.next();
            if(entry.getValue1().equals(handler)){
                it.remove();
            }
        }
    }


    /**
     * returns the ContentTypeHandler that best matches the ContentType.  Best match means using the ordered list from first to last, check if media type & subtype matches exactly, if so return that one.
     * q factor is not considered in this implementation
     * @param type
     * @return
     */
    public ContentTypeHandler getHandler(ContentType type, Purpose purpose){
        if(type == null){
            throw new IllegalArgumentException("type cannot be null");
        }

        for(Pair<ContentType,ContentTypeHandler> entry : typeRegistry){
            if((entry.getValue1().canParseResponse() && purpose == Purpose.PARSE_RESPONSE) || (entry.getValue1().canPrepareRequest() && purpose == Purpose.PREPARE_REQUEST)){
                if(entry.getValue0().compatibleWith(type)) {
                    return entry.getValue1();
                }
            }
        }

        return null;
    }


    public boolean canHandler(ContentType type, Purpose purpose){
        if(type == null){
            throw new IllegalArgumentException("type cannot be null");
        }

        return getHandler(type, purpose) != null;
    }

    public Set<String> getResponseParsingContentTypes(){
        Set<String> result = new HashSet<String>();

        for(Pair<ContentType,ContentTypeHandler> entry : typeRegistry){
            if(entry.getValue1().canParseResponse()) {
                result.add(entry.getValue0().toString());
            }
        }

        return result;
    }


}
