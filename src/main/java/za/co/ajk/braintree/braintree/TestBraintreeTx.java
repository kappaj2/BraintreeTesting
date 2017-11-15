package za.co.ajk.braintree.braintree;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.PaymentMethod;
import com.braintreegateway.PaymentMethodRequest;
import com.braintreegateway.ResourceCollection;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.TransactionSearchRequest;


@Component
public class TestBraintreeTx {
    
    private BraintreeGateway braintreeGateway;
    
    @Autowired
    public TestBraintreeTx(BraintreeGateway braintreeGateway) {
        this.braintreeGateway = braintreeGateway;
    }
    
    public String createPaymentMethod(String customerId, String nonce){
        PaymentMethodRequest request = new PaymentMethodRequest()
                .customerId(customerId)
                .paymentMethodNonce(nonce);
    
        Result<? extends PaymentMethod> result = braintreeGateway.paymentMethod().create(request);
        String paymentToken = result.getTarget().getToken();
        return paymentToken;
        
    }
    
    public Result<Transaction> doTx(String nOnce, BigDecimal amount, String customerId, String pmMethodToken) throws com.braintreegateway.exceptions.AuthenticationException,
            com.braintreegateway.exceptions.UnexpectedException {
        
        TransactionRequest request = new TransactionRequest()
                .amount(amount)
                //.paymentMethodToken(pmMethodToken)
                .paymentMethodNonce(nOnce)
                .customerId(customerId)
                .options()
                .submitForSettlement(true)
                .done();
    
        
        return braintreeGateway.transaction().sale(request);
    }
    
    public List<Transaction> getTxs(String customerId){
    
        
        TransactionSearchRequest request = new TransactionSearchRequest()
                .customerId().is(customerId);
    
        ResourceCollection<Transaction> collection = braintreeGateway.transaction().search(request);
    
        List<Transaction> list = new ArrayList<>();
        collection.iterator().forEachRemaining(list::add);

        return list;
        
    }
    
    public Transaction getTx(String txId){
        
        Transaction transaction = braintreeGateway.transaction().find(txId);
        return transaction;
    }
    public String generateClientToken() {
        return braintreeGateway.clientToken().generate();
    }
}
