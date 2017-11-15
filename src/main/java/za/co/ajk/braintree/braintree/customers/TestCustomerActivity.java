package za.co.ajk.braintree.braintree.customers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.braintreegateway.Address;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.PaymentMethod;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.ValidationError;
import com.braintreegateway.exceptions.UnexpectedException;


@Component
public class TestCustomerActivity {
    
    private BraintreeGateway braintreeGateway;
    
    public TestCustomerActivity(BraintreeGateway braintreeGateway) {
        this.braintreeGateway = braintreeGateway;
    }
    
    public void testCreateCustomer() {
        
        try {
            String validNOnce = "fake-valid-nonce";
            String firstName = "Mark";
            String lastName = "Jones";
            String streetName = "Invalid street address in Capetown";
            String postalCode = "1234";
            
            CustomerRequest request = new CustomerRequest()
                    //                    .firstName(firstName)
                    //                    .lastName(lastName)
                    
                    //                .company("Jones Co.")
                    //                .email("mark.jones@example.com")
                    //                .fax("419-555-1234")
                    //                .phone("614-555-1234")
                    //                .website("http://example.com");
                                        .creditCard()                               //  Option A
                                        .cardholderName("CardHolder name")          //  Option A
                                        .billingAddress()
                                        .firstName(null)
                                        .lastName(null)
                                        .streetAddress(null)
                                        .postalCode(null)
                    //                    .streetAddress(streetName)
                    //                    .postalCode(postalCode)
                                        .done()   // Done for BillingAddress()
                                        .done()   //   Done for CreditCard()    //  Option A
                    
                    //.done()//
                    .paymentMethodNonce(validNOnce)
                    // .expirationDate("01/1211")
                    /// .cvv("3211")
                    // .number("41111111111111111")
                    // .done()
                    ;
            Result<Customer> result = braintreeGateway.customer().create(request);
            
            System.out.println("Create customer status : " + result.isSuccess());
            
            if (result.isSuccess()) {
                
                Customer customer = result.getTarget();
                String customerId = customer.getId();
                
                List<CreditCard> creditCardList = customer.getCreditCards();
                for (CreditCard card:creditCardList) {
                    System.out.println("Card isDefault   : "+card.isDefault());
                    Address address = card.getBillingAddress();
                    System.out.println("Card CountryName : " +address.getCountryName());
                    System.out.println("Card CountryA2   : " +address.getCountryCodeAlpha2());
                    System.out.println("Card CountryA3   : " +address.getCountryCodeAlpha3());
                    System.out.println("Card CountryNum  : " +address.getCountryCodeNumeric());
                    System.out.println("Card FirstName   : " +address.getFirstName());
                    System.out.println("Card LAstName    : " +address.getLastName());
    
                    System.out.println("Card cardholderNAme : " +card.getCardholderName());
                }
                
                System.out.println("Size for payment methods is : " + customer.getPaymentMethods().size());
                List<? extends PaymentMethod> methods = customer.getPaymentMethods();
                for (PaymentMethod method : methods) {
                    System.out.println("    Payment method token    : " + method.getToken());
                    System.out.println("    Size for subscriptions  : " + method.getSubscriptions().size());
                    List<Subscription> subscriptions = method.getSubscriptions();
                    for (Subscription sub : subscriptions) {
                        System.out.println("        Sub ID      : " + sub.getId());
                        System.out.println("        subBalance  : " + sub.getBalance());
                    }
                }
                
                System.out.println("Created CustomerId : " + customerId);
    
    
    
    
                //PaymentMethod paymentMethod = braintreeGateway.paymentMethod().find("token");
                
            } else {
                System.out.println("Error creating customer : " + result.getMessage());
                List<ValidationError> validationErrors = result.getErrors().getAllDeepValidationErrors();
                System.out.println("Error list size : " + validationErrors.size());
            }
        } catch (UnexpectedException unex) {
            System.out.println("Unexpected Exception : " + unex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void testUpdateCustomer(String customerId,
                                   String customerName,
                                   String customerLastname) {
        CustomerRequest request = new CustomerRequest()
                .firstName(customerName)
                .lastName(customerLastname);
        
        Result<Customer> updateResult = braintreeGateway.customer().update(customerId, request);
    }
    
    public void testGetCustomer(String customerId) {
        Customer customer = braintreeGateway.customer().find(customerId);
        System.out.println("Customer found : ");
        System.out.println("Customer found - id        : " + customer.getId());
        System.out.println("Customer found - address   : " + customer.getAddresses());
        System.out.println("Customer found - firstName : " + customer.getFirstName());
        System.out.println("Customer found - lastNAme  : " + customer.getLastName());
        
    }
}
