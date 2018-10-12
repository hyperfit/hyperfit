package org.hyperfit;

import org.hyperfit.exception.NoClientRegisteredForSchemeException;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.Request;
import org.hyperfit.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//TODO: switch this back to builder in v2
class SchemeBasedHyperClientSelectionStrategy implements HyperfitProcessor.HyperClientSelectionStrategy{
    private static final Logger LOG = LoggerFactory.getLogger(SchemeBasedHyperClientSelectionStrategy.class);


    private final Map<String, HyperClient> schemeToClientMap = new HashMap<String, HyperClient>();

    SchemeBasedHyperClientSelectionStrategy(
        Map<String, HyperClient> clients,
        //TODO: remove this in v2 because clients must be immutable
        Set<String> acceptedContentTypes
    ){

        //get the distinct hyperclient from the map and call the setAcceptedContentTypes on each
        //TODO: remove this in v2
        Set<HyperClient> uniqueClients = new HashSet<HyperClient>();


        for(Map.Entry<String,HyperClient> entry : clients.entrySet()){
            HyperClient c = entry.getValue();
            String scheme = entry.getKey();

            uniqueClients.add(c);

            if(schemeToClientMap.containsKey(scheme)){
                LOG.warn(
                    "Replacing {} with {} for scheme {}",
                    schemeToClientMap.get(scheme),
                    c,
                    scheme
                );
            }

            schemeToClientMap.put(scheme, c);

        }

        for(HyperClient hyperClient: uniqueClients){
            hyperClient.setAcceptedContentTypes(acceptedContentTypes);
        }
    }

    public HyperClient chooseClient(
        Request request
    ) {

        //find the scheme
        int pos = request.getUrl().indexOf(":");

        String scheme = null;
        if(pos > 0) {
            scheme = request.getUrl().substring(0, pos);
        }

        if(StringUtils.isEmpty(scheme)){
            throw new IllegalArgumentException("The request url does not have a scheme");
        }

        HyperClient client = this.schemeToClientMap.get(scheme);

        if(client == null){
            throw new NoClientRegisteredForSchemeException(scheme);
        }

        return client;
    }

}