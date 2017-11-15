package za.co.ajk.braintree.braintree.config;


import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;

@Configuration
public class ConfigureBraintree {
    
    private static Logger log = Logger.getLogger(ConfigureBraintree.class.toString());
    
    /*
        Add keys here...
        
    
     */

    
    
    @Bean
    public BraintreeGateway braintreeGateway() {
        //
        //  For production we must use the PRODUCTION environment. For Development and QA we must use the SANDBOX environment.
        //
        Environment environment;
        if (1 == 2) {
            environment = Environment.PRODUCTION;
        } else {
            environment = Environment.SANDBOX;
        }
        BraintreeGateway gateway = new BraintreeGateway(
                environment,
                MERCHANT_ID,
                PUBLIC_KEY,
                PRIVATE_KEY
        );
        
        gateway.getConfiguration().setTimeout(10000);
        gateway.getConfiguration().setLogger(log);
        
        gateway.getConfiguration();
        return gateway;
    }
}
