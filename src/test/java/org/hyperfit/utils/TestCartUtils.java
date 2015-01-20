package org.hyperfit.utils;

import static com.bodybuilding.commerce.hyper.client.CommerceAPIHelper.builder;
import static com.bodybuilding.commerce.hyper.client.ContractConstants.REL_ADD_TO_CART;

import java.util.Random;
import java.util.Stack;

import org.hyperfit.RootResourceBuilder;
import org.hyperfit.resource.HyperLink;

import com.bodybuilding.commerce.hyper.client.profile.BasicUser;
import com.bodybuilding.commerce.hyper.client.profile.CommunityProfileAPIHelper;
import com.bodybuilding.commerce.hyper.client.resource.Root;
import com.bodybuilding.commerce.hyper.client.resource.catalog.Product;
import com.bodybuilding.commerce.hyper.client.resource.catalog.Sku;
import com.bodybuilding.commerce.hyper.client.resource.catalog.Skugroup;
import com.bodybuilding.commerce.hyper.client.utils.ATGCookieHandler;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class TestCartUtils {

    public static Root getUserAuthenticatedRoot(String userCountry) {
        BasicUser user = CommunityProfileAPIHelper.get().createRandomNewUser(userCountry);
        String bbTokenValue = user.getToken();

        RootResourceBuilder builder = builder();

        ATGCookieHandler atgCookies = new ATGCookieHandler();
        atgCookies.setBBTokenValue(bbTokenValue);

        builder.getHyperClient().setCookieHandler(atgCookies);

        return builder.build(Root.class);
    }
    
    public static Stack<Sku> getSkusForAdding2Cart(Root root, int numberToAdd) {
        Stack<Sku> skusICanAdd = new Stack<Sku>();

        for(Product p : root.top50Products().items()){
            //Can't add combo's yet
            if(!p.getType().equals("combo")){
                p = p.self();
                for(Skugroup skug : p.skugroups().items()){
                    for(final Sku s : skug.skus().items()){

                        if(s.canAddToCart()){
                            boolean alreadyThere = Iterables.any(skusICanAdd, new Predicate<Sku>() {
                                public boolean apply(Sku input) {
                                    //Funny..but with mock data self link isn't definitive, so we'll use add to cart link
                                    return input.getAddToCartLink().equals(s.getAddToCartLink());
                                }
                            });

                            //Don't use dupes (they show up in the api)
                            if(alreadyThere){
                                continue;
                            }
                            skusICanAdd.add(s);
                            break; //I only want 1 per skugroup
                        }
                    }

                    if(skusICanAdd.size() == numberToAdd) break;
                }
            }

            if(skusICanAdd.size() == numberToAdd) break;
        }
        if(skusICanAdd.size() < numberToAdd) throw new RuntimeException("didn't find " + numberToAdd + " skus to add");
        return skusICanAdd;
    }
    
    public static void addSkusToCart(Stack<Sku> skusICanAdd, String csrf) {
        
        Random r = new Random();
    
        while(!skusICanAdd.empty()) {
            Sku aSkuICanAdd = skusICanAdd.pop();

            int quantityToAdd = r.nextInt(10) + 1;

            HyperLink addLink = aSkuICanAdd.getLink(REL_ADD_TO_CART);
            System.out.println("About to add " + quantityToAdd + " of type " + aSkuICanAdd.getType() + " with: " + addLink.getHref());
            aSkuICanAdd.addToCart(quantityToAdd, csrf);            
        }
    }
}
