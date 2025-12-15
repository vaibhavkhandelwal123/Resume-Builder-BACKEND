package com.Projects.ResumeBuilder.Service;

import com.Projects.ResumeBuilder.Dto.AuthResponse;
import com.Projects.ResumeBuilder.Entity.Payment;
import com.Projects.ResumeBuilder.Entity.User;
import com.Projects.ResumeBuilder.Repository.PaymentRepository;
import com.Projects.ResumeBuilder.Repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.Projects.ResumeBuilder.Utilities.AppConstants.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PaymentRepository paymentRepository;

    public Payment createOrder(Object principal, String planType) throws RazorpayException {

        AuthResponse authResponse = authService.getProfile(principal);

        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId,razorpayKeySecret);

        int amount = 99900;
        String currency = "INR";
        String receipt = PREMIUM + "_" + UUID.randomUUID().toString().substring(0,8);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",amount);
        orderRequest.put("currency",currency);
        orderRequest.put("receipt",receipt);

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        Payment newPayment = Payment.builder()
                .userId(authResponse.getId())
                .razorpayOrderId(razorpayOrder.get("id"))
                .currency(currency)
                .amount(amount)
                .planType(planType)
                .status("created")
                .receipt(receipt)
                .build();

        paymentRepository.save(newPayment);
        return newPayment;
    }

    public boolean verifiedPayment(String razorpaySignature, String razorpayPaymentId, String razorpayOrderId) throws RazorpayException {
        try{
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id",razorpayOrderId);
            attributes.put("razorpay_payment_id",razorpayPaymentId);
            attributes.put("razorpay_signature",razorpaySignature);

            boolean isValidSignature = Utils.verifyPaymentSignature(attributes,razorpayKeySecret);
            if(isValidSignature){
                Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                        .orElseThrow(()->new RuntimeException("Payment not found"));
                payment.setRazorpayPaymentsId(razorpayPaymentId);
                payment.setRazorpaySignature(razorpaySignature);
                payment.setStatus("paid");
                paymentRepository.save(payment);

                upgradeUserSubscription(payment.getUserId(),payment.getPlanType());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error verifying the payment: ",e);
            return false;
        }
    }

    private void upgradeUserSubscription(String userId, String planType) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        existingUser.setSubscriptionPlan(planType);
        userRepository.save(existingUser);
        log.info("User {} upgraded to {} plan",userId,planType);
    }

    public List<Payment> getPaymentHistory(Object principal) {
        AuthResponse authResponse = authService.getProfile(principal);
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(authResponse.getId());
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByRazorpayOrderId(orderId).orElseThrow(()->new RuntimeException("Payment not found"));
    }
}
