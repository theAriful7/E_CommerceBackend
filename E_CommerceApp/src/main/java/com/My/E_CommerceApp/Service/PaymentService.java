package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.PaymentRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.PaymentResponseDTO;
import com.My.E_CommerceApp.Entity.Order;
import com.My.E_CommerceApp.Entity.Payment;
import com.My.E_CommerceApp.Repository.OrderRepo;
import com.My.E_CommerceApp.Repository.PaymentRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final OrderRepo orderRepo;

    public PaymentService(PaymentRepo paymentRepo, OrderRepo orderRepo) {
        this.paymentRepo = paymentRepo;
        this.orderRepo = orderRepo;
    }

    // 🧱 DTO → Entity conversion
    public Payment toEntity(PaymentRequestDTO dto) {
        Payment payment = new Payment();

        // orderId থেকে Order Entity বের করা
        Order order = orderRepo.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        payment.setOrder(order);
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentStatus("PENDING"); // শুরুতে সবসময় Pending
        payment.setPaymentDate(LocalDateTime.now());

        return payment;
    }

    // 🧾 Entity → Response DTO conversion
    public PaymentResponseDTO toResponse(Payment payment) {
        PaymentResponseDTO res = new PaymentResponseDTO();
        res.setId(payment.getId());
        res.setOrderId(payment.getOrder().getId());
        res.setAmount(payment.getAmount());
        res.setPaymentMethod(payment.getPaymentMethod());
        res.setPaymentStatus(payment.getPaymentStatus());
        res.setPaymentDate(payment.getPaymentDate());
        return res;
    }

    // ✅ Create Payment
    public PaymentResponseDTO createPayment(PaymentRequestDTO dto) {
        Payment payment = toEntity(dto);
        Payment saved = paymentRepo.save(payment);
        return toResponse(saved);
    }

    // ✅ Get Payment by ID
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
        return toResponse(payment);
    }

    // ✅ Get All Payments
    public List<PaymentResponseDTO> getAllPayments() {
        List<Payment> payments = paymentRepo.findAll();
        return payments.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ✅ Update Payment Status
    public PaymentResponseDTO updatePaymentStatus(Long id, String status) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));

        payment.setPaymentStatus(status.toUpperCase());
        paymentRepo.save(payment);
        return toResponse(payment);
    }

    // ✅ Delete Payment
    public void deletePayment(Long id) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
        paymentRepo.delete(payment);
    }
}
