package za.co.ajk.braintree.braintree;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import com.braintreegateway.Customer;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.ValidationError;
import com.braintreegateway.ValidationErrorCode;

@SpringBootApplication
public class BraintreeApplication {
    
    
    private static TestBraintreeTx testBraintreeTx;
    
    public static void main(String[] args) {
        
        ConfigurableApplicationContext ctx = SpringApplication.run(BraintreeApplication.class, args);
    }
   
}
