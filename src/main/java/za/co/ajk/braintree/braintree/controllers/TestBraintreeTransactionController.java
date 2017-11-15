package za.co.ajk.braintree.braintree.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.braintreegateway.Customer;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.ValidationError;
import com.braintreegateway.ValidationErrorCode;
import za.co.ajk.braintree.braintree.TestBraintreeTx;
import za.co.ajk.braintree.braintree.customers.TestCustomerActivity;


@RestController
public class TestBraintreeTransactionController {
    
    private TestBraintreeTx testBraintreeTx;
    private TestCustomerActivity testCustomerActivity;
    
    public TestBraintreeTransactionController(TestBraintreeTx testBraintreeTx,
                                              TestCustomerActivity testCustomerActivity) {
        this.testBraintreeTx = testBraintreeTx;
        this.testCustomerActivity = testCustomerActivity;
    }
    
    @GetMapping("/getClientToken")
    public String getClientToken() {
        return testBraintreeTx.generateClientToken();
    }
    
    @GetMapping("/testTxValid/{customerId}/{txcnt}")
    public void testTxValid(@PathVariable String customerId, @PathVariable int txcnt) {
        
        for (int ii = 0; ii < txcnt; ii++) {
            
            BigDecimal txAmount = new BigDecimal("20" + ii);
            String validNOnce = "fake-valid-nonce";
            
            boolean reachedBraintree = false;
            
            try {
                Result<Transaction> result = testBraintreeTx.doTx(validNOnce, txAmount, customerId, null);
                /*
                    Sometimes the txId is valid, sometimes it show up as a duplicate tx. Check for both instances.
                 */
                Transaction trans = result.getTarget();
                String transactionId = trans.getId();
                String authCode = trans.getProcessorAuthorizationCode();
                
                // tx id => pgxkzpx5
                
                trans.getCustomer().getId();
                
                if (result.isSuccess() && result.getTarget() != null) {
                    String responseCode = result.getTarget().getProcessorResponseCode();
                
                    /*
                        Expected result code is "1000"
                    */
                    if (responseCode.equals("1000")) {
                        reachedBraintree = true;
                    }
                }
                
                if (!result.isSuccess() && result.getTransaction() != null) {
                    Transaction.GatewayRejectionReason gReason = result.getTransaction().getGatewayRejectionReason();
                    int rejectionCode = gReason.compareTo(Transaction.GatewayRejectionReason.DUPLICATE);
                    if (rejectionCode == 0) {
                        System.out.println("Rejected as duplicate");
                        reachedBraintree = true;
                    }
                }
                
                if (!result.isSuccess() && result.getErrors() != null
                        && result.getErrors().getAllDeepValidationErrors().size() != 0) {
                    ValidationError validationError = result.getErrors().getAllDeepValidationErrors().get(0);
                    if (validationError.getCode() == ValidationErrorCode.TRANSACTION_PAYMENT_METHOD_NONCE_UNKNOWN) {
                        reachedBraintree = false;
                    }
                    throw new RuntimeException("Error - unkown condition!!!!!!!!!!!!!!");
                }
                
            } catch (com.braintreegateway.exceptions.AuthenticationException ex) {
                System.out.println("Invalid configuration!!!");
            } catch (com.braintreegateway.exceptions.UnexpectedException uh) {
                System.out.println("Unkown host exception - possible network error");
            }
            
            System.out.println("Done doing tx (reachedBraintree): " + reachedBraintree);
        }
    }
    
    @GetMapping("/testTxInValid")
    public void testTxInValid() {
        
        BigDecimal txAmount = new BigDecimal("20");
        String invalidNOnce = "1234567890";
        
        boolean invalidNonce = false;
        try {
            
            Result<Transaction> result = testBraintreeTx.doTx(invalidNOnce, txAmount, "12345", null);
            
            if (result.getTarget() != null) {
                String responseCode = result.getTarget().getProcessorResponseCode();
            }
            
            if (!result.isSuccess() && result.getErrors() != null
                    && result.getErrors().getAllDeepValidationErrors().size() != 0) {
                ValidationError validationError = result.getErrors().getAllDeepValidationErrors().get(0);
                if (validationError.getCode() == ValidationErrorCode.TRANSACTION_PAYMENT_METHOD_NONCE_UNKNOWN) {
                    invalidNonce = true;
                }
            }
            
            String resulMessage = result.getMessage();
            if (result.getTarget() != null) {
                String txId = result.getTarget().getId();
                Customer customer = result.getTarget().getCustomer();
            }
            
            System.out.println("Done doing tx (invalidNonce): " + invalidNonce);
            System.out.println("Result message              : " + resulMessage);
            
            String clientToken = testBraintreeTx.generateClientToken();
            System.out.println("Client token : " + clientToken);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @GetMapping("/createCustomer")
    public void testCustomer() {
        testCustomerActivity.testCreateCustomer();
    }
    
    @GetMapping("/updateCustomer/{customerId}/{customerName}/{customerLastname}")
    public void testUpdateCustomer(@PathVariable String customerId,
                                   @PathVariable String customerName,
                                   @PathVariable String customerLastname) {
        testCustomerActivity.testUpdateCustomer(customerId, customerName, customerLastname);
    }
    
    @GetMapping("/getCustomer/{customerId}")
    public void getCustomer(@PathVariable String customerId) {
        testCustomerActivity.testGetCustomer(customerId);
    }
    
    @GetMapping("/getTxs/{customerId}")
    public void getTxs(@PathVariable String customerId) {
        List<Transaction> txList = testBraintreeTx.getTxs(customerId);
    }
    
    @GetMapping("/getTx/{txId}")
    public void getTx(@PathVariable String txId) {
        testBraintreeTx.getTx(txId);
    }
    
    @GetMapping("/createPM/{customerId}/{noance}")
    public void createPM(@PathVariable String customerId, @PathVariable String noance) {
        testBraintreeTx.createPaymentMethod(customerId, noance);
    }
    
    @GetMapping("/doTxValid/{noance}/{pmttoken}/{customerId}/{amt}/{cnt}")
    public void testTxValid(@PathVariable String noance,
                            @PathVariable String pmttoken,
                            @PathVariable String customerId,
                            @PathVariable String amt,
                            @PathVariable int cnt) {
        
        for (int ii = 0; ii< cnt; ii++) {
            
            BigDecimal txAmount = new BigDecimal(amt.concat(""+ii));
            
            //  doTx(String nOnce, BigDecimal amount, String customerId, String pmMethodToken)
            Result<Transaction> result = testBraintreeTx.doTx(noance, txAmount, customerId, pmttoken);
            if (!result.isSuccess()) {
                System.out.println("Error :!!!! : " + result.getMessage());
            } else {
        
                Transaction trans = result.getTarget();
                System.out.println("TransId : "+trans.getId());
                System.out.println("AuthCoce : "+trans.getProcessorAuthorizationCode());
            }
        }
    }
}
