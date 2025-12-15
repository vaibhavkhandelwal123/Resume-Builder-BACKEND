package com.Projects.ResumeBuilder.Controller;

import com.Projects.ResumeBuilder.Entity.Payment;
import com.Projects.ResumeBuilder.Service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                "orderId",payment.getRazorpayOrderId(),
                "amount",payment.getAmount(),
                "currency",payment.getCurrency(),
                "receipt",payment.getReceipt()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping(VERIFY_PAYMENT)
    public ResponseEntity<?> verifiedPayments(@RequestBody Map<String,String> request) throws RazorpayException {
        String razorpayOrderId = request.get("razorpay_order_id");
        String razorpayPaymentId = request.get("razorpay_payment_id");
        String razorpaySignature = request.get("razorpay_signature");

        if(Objects.isNull(razorpayOrderId) || Objects.isNull(razorpayPaymentId) || Objects.isNull(razorpaySignature)){
            return ResponseEntity.badRequest().body(Map.of("message","Missing required payment parameters"));
        }

        boolean isValid = paymentService.verifiedPayment(razorpaySignature,razorpayPaymentId,razorpayOrderId);
        if(isValid){
            return ResponseEntity.ok(Map.of(
                    "message","Payment verified successfully",
                    "status","success"
            ));
        }else{
            return ResponseEntity.badRequest().body(Map.of("message","Payment verification failed"));
        }
    }

    @GetMapping(HISTORY_PAYMENT)
    public ResponseEntity<?> getPaymentHistory(Authentication authentication){
        List<Payment> payments = paymentService.getPaymentHistory(authentication.getPrincipal());
        return ResponseEntity.ok(payments);
    }

    @GetMapping(ORDER_PAYMENT_ID)
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){
        Payment paymentDetails = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(paymentDetails);
    }
}
