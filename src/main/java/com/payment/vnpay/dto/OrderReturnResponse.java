package com.payment.vnpay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderReturnResponse {
    String id;
    String bookingId;
    String status;
    Integer amount;
    String paymentMethod;
}
