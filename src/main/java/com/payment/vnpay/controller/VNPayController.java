package com.payment.vnpay.controller;

import com.payment.vnpay.dto.OrderResponse;
import com.payment.vnpay.dto.OrderReturnResponse;
import com.payment.vnpay.service.VNPayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/v1/vnpay-payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayController {
    VNPayService vnPayService;

    @GetMapping
    public OrderResponse createOrder(@RequestParam int amount, @RequestParam String bookingId) throws UnsupportedEncodingException {
        return vnPayService.createOrder(amount, bookingId);
    }

    @GetMapping("/result")
    public OrderReturnResponse returnOrder(
            @RequestParam Integer vnp_Amount,
            @RequestParam String vnp_OrderInfo,
            @RequestParam String vnp_ResponseCode,
            @RequestParam String vnp_TransactionNo
    ) {
        return vnPayService.returnOrder(vnp_Amount, vnp_OrderInfo, vnp_ResponseCode, vnp_TransactionNo);
    }
}
