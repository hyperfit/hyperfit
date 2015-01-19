package org.hyperfit.errorhandler;

import org.hyperfit.exception.*;
import org.junit.Before;
import org.junit.Test;

import org.hyperfit.exception.ServiceException;

import static org.junit.Assert.assertEquals;

public class DefaultErrorHandlerTest {
    
    private String [] error404 = {"404", "error404", "Resource not found"};
    private String [] error401 = {"401", "error401", "Authorization error"};
    private String [] error403 = {"403", "error403", "Resource unavailable"};
    private String [] error501 = {"501", "error501", "Service unavailable"};
    private String [] error505 = {"505", "error505", "Generic error"};
    
    private DefaultErrorHandler defaultHyperErrorHandler;
    
    @Before
    public void setup() {
        defaultHyperErrorHandler = new DefaultErrorHandler();
    }
    
    private ResponseError loadError(String [] values) {
        return new ResponseError(Integer.valueOf(values[0]), values[1], values[2]);
    }
    
    @Test
    public void testRightErrorMesageInException() {
       RuntimeException ex = defaultHyperErrorHandler.handleError(loadError(error404));
       assertEquals(error404[2], ex.getMessage());
    }
    
    @Test(expected=ResourceNotFoundException.class)
    public void test404Exception() {
        throw defaultHyperErrorHandler.handleError(loadError(error404));        
    }
    
    @Test(expected=UnauthorizedException.class)
    public void test401Exception() {
        throw defaultHyperErrorHandler.handleError(loadError(error401));        
    }
    
    @Test(expected=ResourceUnavailableException.class)
    public void test403Exception() {
        throw defaultHyperErrorHandler.handleError(loadError(error403));        
    }
    
    @Test(expected=ServiceUnavailableException.class)
     public void test501Exception() {
        throw defaultHyperErrorHandler.handleError(loadError(error501));        
    }
    
    @Test(expected=ServiceException.class)
    public void testGenericException() {
       throw defaultHyperErrorHandler.handleError(loadError(error505));        
   }
}
