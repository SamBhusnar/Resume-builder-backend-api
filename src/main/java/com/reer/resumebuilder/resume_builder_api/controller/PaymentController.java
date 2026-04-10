package com.reer.resumebuilder.resume_builder_api.controller;


import com.razorpay.RazorpayException;
import com.reer.resumebuilder.resume_builder_api.documents.Payment;
import com.reer.resumebuilder.resume_builder_api.service.AuthService;
import com.reer.resumebuilder.resume_builder_api.service.PaymentService;
import com.reer.resumebuilder.resume_builder_api.util.AppConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthService authService;


    // create order
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, String> request) throws RazorpayException {
        // validate the request
        String planType = request.get("planType");
        if (!AppConstant.PREMIUM.equalsIgnoreCase(planType)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid plan type"));
        }
        // call the service method
        String userId = authService.getCurrentUser().getId();

        Payment payment = paymentService.createOrder(userId, planType);

        // prepare the response object
        Map<String, Object> response = Map.of(
                "orderId", payment.getRazorpayOrderId(),
                "amount", payment.getAmount(),
                "currency", payment.getCurrency(),
                "receipt", payment.getReceipt()
        );
        // return response
        return ResponseEntity.ok(response);

    }


    // verify payment
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request) throws RazorpayException {
        // step 1 : validate the request
        String razorpayOrderId = request.get("razorpayOrderId");
        String razorpayPaymentId = request.get("razorpayPaymentId");
        String razorpaySignature = request.get("razorpaySignature");
        if (Objects.isNull(razorpayOrderId) || Objects.isNull(razorpayPaymentId) || Objects.isNull(razorpaySignature)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields", "fields",
                    Map.of("razorpayOrderId", razorpayOrderId, "razorpayPaymentId", razorpayPaymentId, "razorpaySignature", razorpaySignature), "status", "error"));
        }
        //step 2 : call the service method to verify payment
        boolean isValid = paymentService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);

        // step 3 : return the response
        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "Payment verified successfully", "status", "success"));
        }
        return ResponseEntity.ok(Map.of("message", "Payment verification failed", "status", "error"));

    }

    // payment history
    @GetMapping("/history")
    public ResponseEntity<?> getPaymentHistory() {
        // all the service method with user method name is getuserpayments
        List<Payment> payments = paymentService.getUserPayments(authService.getCurrentUser().getId());
        // return the response

        return ResponseEntity.ok(Map.of("payments", payments));
    }

    // get order by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") String orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (Objects.isNull(payment)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Payment not found with order id: " + orderId));
        }
        return ResponseEntity.ok(Map.of("payment", payment));
         
    }

}
