package com.Projects.ResumeBuilder.Controller;

import com.Projects.ResumeBuilder.Entity.Payment;
import com.Projects.ResumeBuilder.Service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.Projects.ResumeBuilder.Utilities.AppConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(PAYMENTS)
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(CREATE_ORDER)
    public ResponseEntity<?> createOrder(@RequestBody Map<String,String> request, Authentication authentication) throws RazorpayException {
        String planType = request.get("planType");
        if(!PREMIUM.equalsIgnoreCase(planType)){
            return ResponseEntity.badRequest().body(Map.of("message","Invalid Plan Type"));
        }
        Payment payment = paymentService.createOrder(authentication.getPrincipal(),planType);
        Map<String,Object> response = Map.of(
                "orderId",payment.getRazorpayPaymentsId(),
                "amount",payment.getAmount(),
                "currency",payment.getCurrency(),
                "receipt",payment.getReceipt()
        );
        return ResponseEntity.ok("jhjhjn");
    }

    @PostMapping(VERIFY_PAYMENT)
    public ResponseEntity<?> verifiedPayments(@RequestBody Map<String,String> request){
        return null;
    }

    @GetMapping(HISTORY_PAYMENT)
    public ResponseEntity<?> getPaymentHistory(Authentication authentication){
        return null;
    }

    @GetMapping(ORDER_PAYMENT_ID)
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){
        return null;
    }
}
