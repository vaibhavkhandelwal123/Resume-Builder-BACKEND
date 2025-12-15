package com.Projects.ResumeBuilder.Repository;

import com.Projects.ResumeBuilder.Entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment,String> {
}
