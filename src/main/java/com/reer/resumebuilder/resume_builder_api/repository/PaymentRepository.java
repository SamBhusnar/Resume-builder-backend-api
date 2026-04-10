package com.reer.resumebuilder.resume_builder_api.repository;

import com.reer.resumebuilder.resume_builder_api.documents.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    // findBy razor pay order id
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    // find by razor payment id
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    // find by user id created desc
    List<Payment> findByUserIdOrderByCreatedAtDesc(String userId);

    // find by status
    List<Payment> findByStatus(String status);

    
}
