package com.carRental.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.carRental.dto.PaymentDTO;
import com.carRental.dto.PaymentOrderDTO;
import com.carRental.entity.Booking;
import com.carRental.entity.Payment;
import com.carRental.entity.PaymentStatus;
import com.carRental.repository.BookingRepository;
import com.carRental.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final RazorpayService razorpayService;

    @Override
    public PaymentDTO makePayment(PaymentOrderDTO paymentOrderDTO) {
        try {
            System.out.println("DEBUG: Entering makePayment. BookingId: " + paymentOrderDTO.getBookingId());

            if (paymentOrderDTO.getBookingId() == null) {
                throw new RuntimeException("Booking ID is required for payment.");
            }

            // 1. Find Booking
            Booking booking = bookingRepository.findById(paymentOrderDTO.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found: " + paymentOrderDTO.getBookingId()));

            System.out.println("DEBUG: Found booking: " + booking.getBookingId() + " current status: "
                    + booking.getBookingStatus());

            // 2. Prevent Multiple Payments for same booking (Unique constraint)
            // findByBooking_BookingId check ensures we reuse the record if user hits Pay
            // again
            Payment payment = paymentRepository.findByBooking_BookingId(booking.getBookingId())
                    .orElse(new Payment());

            payment.setBooking(booking);
            payment.setAmount(
                    paymentOrderDTO.getAmount() != null ? paymentOrderDTO.getAmount() : booking.getTotalAmount());
            payment.setPaymentDate(java.time.LocalDate.now());
            payment.setPaymentStatus(com.carRental.entity.PaymentStatus.PENDING); // Initial status

            // 3. Verify Razorpay
            if (paymentOrderDTO.getRazorpayPaymentId() != null) {
                System.out.println("DEBUG: Verifying Razorpay payment: " + paymentOrderDTO.getRazorpayPaymentId());
                boolean isVerified = razorpayService.verifySignature(
                        paymentOrderDTO.getRazorpayOrderId(),
                        paymentOrderDTO.getRazorpayPaymentId(),
                        paymentOrderDTO.getRazorpaySignature());

                if (!isVerified) {
                    System.err.println("DEBUG: Verification FAILED");
                    payment.setPaymentStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);
                    throw new RuntimeException("Payment verification failed");
                }

                System.out.println("DEBUG: Verification SUCCESS");
                payment.setRazorpayPaymentId(paymentOrderDTO.getRazorpayPaymentId());
                payment.setRazorpayOrderId(paymentOrderDTO.getRazorpayOrderId());
                payment.setPaymentStatus(PaymentStatus.SUCCESS);

                // Sync Booking
                booking.setPaymentStatus(PaymentStatus.SUCCESS);
                booking.setBookingStatus(com.carRental.entity.BookingStatus.PAID);
                bookingRepository.save(booking);
                System.out.println("DEBUG: Booking status updated to PAID");
            } else {
                // Manual/Mock success
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                booking.setPaymentStatus(PaymentStatus.SUCCESS);
                booking.setBookingStatus(com.carRental.entity.BookingStatus.PAID);
                bookingRepository.save(booking);
            }

            System.out.println("DEBUG: Attempting to save payment record...");
            Payment saved = paymentRepository.save(payment);
            System.out.println("DEBUG: Payment saved successfully. ID: " + saved.getPaymentId());

            return PaymentDTO.builder()
                    .id(saved.getPaymentId())
                    .bookingId(saved.getBooking() != null ? saved.getBooking().getBookingId() : null)
                    .amount(saved.getAmount())
                    .paymentDate(saved.getPaymentDate())
                    .paymentStatus(saved.getPaymentStatus())
                    .paymentMode(saved.getPaymentMode())
                    .transactionId(saved.getRazorpayPaymentId())
                    .build();

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR in PaymentServiceImpl: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment == null)
            return null;
        return modelMapper.map(payment, PaymentDTO.class);
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDTO getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBooking_BookingId(bookingId).orElse(null);
        if (payment == null)
            return null;
        return modelMapper.map(payment, PaymentDTO.class);
    }
}
