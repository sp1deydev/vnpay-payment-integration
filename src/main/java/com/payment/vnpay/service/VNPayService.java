package com.payment.vnpay.service;

import com.payment.vnpay.config.VNPayConfig;
import com.payment.vnpay.dto.OrderResponse;
import com.payment.vnpay.dto.OrderReturnResponse;
import com.payment.vnpay.entity.Booking;
import com.payment.vnpay.entity.Payment;
import com.payment.vnpay.enums.BookingStatus;
import com.payment.vnpay.enums.PaymentMethod;
import com.payment.vnpay.enums.PaymentStatus;
import com.payment.vnpay.repository.BookingRepository;
import com.payment.vnpay.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayService {
    BookingRepository bookingRepository;
    PaymentRepository paymentRepository;

    public OrderResponse createOrder(int amount, String bookingId) throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        amount = amount * 100;
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;


        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
//        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + bookingId);
        vnp_Params.put("vnp_OrderInfo", bookingId);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        return OrderResponse.builder()
                .bookingId(bookingId)
                .paymentUrl(paymentUrl)
                .build();
    }

    public OrderReturnResponse returnOrder(
                Integer vnp_Amount,
                String vnp_OrderInfo,
                String vnp_ResponseCode,
                String vnp_TransactionNo
    ) {
        String status = String.valueOf(PaymentStatus.FAILED);
        if(Objects.equals(vnp_ResponseCode, "00")) {
            status = String.valueOf(PaymentStatus.SUCCESS);
            Booking booking = bookingRepository.findById(vnp_OrderInfo)
                    .orElseThrow(() -> new RuntimeException("Booking Not Found"));
            booking.setStatus(BookingStatus.PAID);
            bookingRepository.save(booking);

            Payment payment = new Payment(vnp_TransactionNo, vnp_OrderInfo, vnp_Amount, status, String.valueOf(PaymentMethod.VNPAY));
            paymentRepository.save(payment);
        }
        return OrderReturnResponse.builder()
                .id(vnp_TransactionNo)
                .status(status)
                .amount(vnp_Amount/100)
                .paymentMethod(String.valueOf(PaymentMethod.VNPAY))
                .bookingId(vnp_OrderInfo)
                .build();
    }
}
