package com.payment.vnpay.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    String id;

    @Column(name = "booking_id")
    String bookingId;

    @Column(name = "amount")
    Integer amount;

    @Column(name = "status")
    String status;

    @Column(name = "payment_method")
    String paymentMethod;

}

