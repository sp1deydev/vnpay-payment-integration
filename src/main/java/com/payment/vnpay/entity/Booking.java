package com.payment.vnpay.entity;

import com.payment.vnpay.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Enumerated(EnumType.STRING)
    BookingStatus status;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    User user;
//
//    @ManyToOne
//    @JoinColumn(name = "showtime_id", nullable = false)
//    Showtime showtime;
//
//    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<Ticket> tickets;

}
