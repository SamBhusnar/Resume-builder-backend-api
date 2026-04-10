package com.reer.resumebuilder.resume_builder_api.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.reer.resumebuilder.resume_builder_api.documents.Payment;
import com.reer.resumebuilder.resume_builder_api.documents.User;
import com.reer.resumebuilder.resume_builder_api.repository.PaymentRepository;
import com.reer.resumebuilder.resume_builder_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.reer.resumebuilder.resume_builder_api.util.AppConstant.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    // user repository
    private final UserRepository userRepository;
    // load razorpay pay id and secret from application.properties
    @Value(("${razorpay.key}"))
    private String razorpayId;

    @Value(("${razorpay.secret}"))
    private String razorpaySecret;


    public Payment createOrder(String userId, String planType) throws RazorpayException {


        // step 1: initialize razorpay client
        RazorpayClient razorpayClient = new RazorpayClient(razorpayId, razorpaySecret);

        // step 2: prepare jsonObject
        int amount = 10000;// 100 in paise
        // generate receipt by accommodating plan type and uuid and after substring method from 0 to 8

        String receipt = PREMIUM + "_" + UUID.randomUUID().toString().substring(0, 8);
        // currency is in INR
        String currency = "INR";

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount);
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", receipt);


        // step 3: call the razorpay api

        Order order = razorpayClient.orders.create(orderRequest);


        // step 4: save the order details in database
        Payment newPayment = Payment.builder()
                .userId(userId)
                .razorpayOrderId(order.get("id"))
                .amount(amount)
                .status("created")
                .currency(currency)
                .planType(planType)
                .receipt(receipt)
                .build();

        // step 5: return the result

        return paymentRepository.save(newPayment);


    }

    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws RazorpayException {
        log.info("Verifying payment");
        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", razorpayOrderId);
        attributes.put("razorpay_payment_Id", razorpayPaymentId);
        attributes.put("razorpay_signature", razorpaySignature);
        // verify it from util class of razor pay and method of this calss is verifyPaymentSignature
        boolean isValid = Utils.verifyPaymentSignature(attributes, razorpaySecret);
        if (isValid) {
            log.info("Payment verified successfully");
            Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId).orElseThrow(() -> new RuntimeException("Payment not found"));
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setRazorpaySignature(razorpaySignature);
            payment.setStatus("paid");
            paymentRepository.save(payment);
            log.info("upgrading status of verified payment");
            // update subscription plan of user to premium
            // update subscriptionplan of user
            User user = userRepository.findById(payment.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

            // private method to update subscription plan of user
            updateSubscriptionPlan(user, payment.getPlanType());
            return true;
        } else {
            log.error("Payment verification failed {} ", razorpayOrderId);
            return false;
        }

    }

    private void updateSubscriptionPlan(User user, String planType) {
        log.info("upgrading user subscription plan to premium");
        user.setSubscriptionPlan(planType);
        userRepository.save(user);
        log.info("user subscription plan upgraded to premium");
    }

    public List<Payment> getUserPayments(String id) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(id);
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByRazorpayOrderId(orderId).orElse(null);
    }

}
