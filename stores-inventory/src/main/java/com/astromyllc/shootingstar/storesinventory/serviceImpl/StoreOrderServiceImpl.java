package com.astromyllc.shootingstar.storesinventory.serviceImpl;

import com.astromyllc.shootingstar.storesinventory.config.FinanceLedgerClient;
import com.astromyllc.shootingstar.storesinventory.dto.request.OrderLineRequest;
import com.astromyllc.shootingstar.storesinventory.dto.request.OrderRequest;
import com.astromyllc.shootingstar.storesinventory.dto.request.StoreFetchRequest;
import com.astromyllc.shootingstar.storesinventory.dto.response.StoreOrderResponse;
import com.astromyllc.shootingstar.storesinventory.event.LedgerPostRequest;
import com.astromyllc.shootingstar.storesinventory.model.OrderLineItem;
import com.astromyllc.shootingstar.storesinventory.model.StockMovement;
import com.astromyllc.shootingstar.storesinventory.model.StoreItem;
import com.astromyllc.shootingstar.storesinventory.model.StoreOrder;
import com.astromyllc.shootingstar.storesinventory.repository.StockMovementRepository;
import com.astromyllc.shootingstar.storesinventory.repository.StoreItemRepository;
import com.astromyllc.shootingstar.storesinventory.repository.StoreOrderRepository;
import com.astromyllc.shootingstar.storesinventory.service.StoreOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreOrderServiceImpl implements StoreOrderService {

    private final StoreOrderRepository orderRepository;
    private final StoreItemRepository itemRepository;
    private final StockMovementRepository movementRepository;
    private final FinanceLedgerClient financeLedgerClient;

    // ── Place order ───────────────────────────────────────────────────────────

    @Override
    public StoreOrderResponse placeOrder(OrderRequest req) {
        List<OrderLineItem> lineItems = new ArrayList<>();
        double total = 0.0;

        for (OrderLineRequest line : req.getLineItems()) {
            StoreItem item = itemRepository.findById(line.getStoreItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + line.getStoreItemId()));

            if (!item.getActive()) throw new RuntimeException("Item is no longer available: " + item.getItemName());

            // Channel check
            boolean channelAllowed = item.getChannel().equals("BOTH")
                    || item.getChannel().equals(req.getChannel());
            if (!channelAllowed)
                throw new RuntimeException(item.getItemName() + " is not available via " + req.getChannel());

            if (item.getQuantityInStock() < line.getQuantity())
                throw new RuntimeException("Insufficient stock for: " + item.getItemName()
                        + " (requested " + line.getQuantity() + ", available " + item.getQuantityInStock() + ")");

            double lineTotal = item.getUnitPrice() * line.getQuantity();
            lineItems.add(OrderLineItem.builder()
                    .storeItemId(item.getId())
                    .itemName(item.getItemName())
                    .itemCode(item.getItemCode())
                    .quantity(line.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .lineTotal(lineTotal)
                    .build());
            total += lineTotal;
        }

        String orderRef = generateOrderRef(req.getInstitutionCode());
        StoreOrder order = StoreOrder.builder()
                .institutionCode(req.getInstitutionCode())
                .orderRef(orderRef)
                .buyerId(req.getBuyerId())
                .buyerName(req.getBuyerName())
                .buyerContact(req.getBuyerContact())
                .channel(req.getChannel())
                .status("PENDING")
                .lineItems(lineItems)
                .totalAmount(total)
                .paymentMethod(req.getPaymentMethod())
                .paymentReference(req.getPaymentReference())
                .paymentConfirmed(req.getPaymentConfirmed() != null ? req.getPaymentConfirmed() : false)
                .processedBy(req.getProcessedBy())
                .ledgerPosted(false)
                .orderDate(LocalDateTime.now())
                .build();

        // For in-store cash sales — immediately CONFIRMED
        if ("INSTORE".equals(req.getChannel()) && "CASH".equals(req.getPaymentMethod())) {
            order.setPaymentConfirmed(true);
            order.setStatus("CONFIRMED");
        }

        return toResponse(orderRepository.save(order));
    }

    // ── Confirm payment ───────────────────────────────────────────────────────

    @Override
    public StoreOrderResponse confirmOrder(String orderId, String paymentReference) {
        StoreOrder order = getOrder(orderId);
        if (!"PENDING".equals(order.getStatus()))
            throw new RuntimeException("Order " + order.getOrderRef() + " is not PENDING");

        order.setPaymentReference(paymentReference);
        order.setPaymentConfirmed(true);
        order.setStatus("CONFIRMED");
        return toResponse(orderRepository.save(order));
    }

    // ── Fulfil order — deduct stock + post to Finance ledger ─────────────────

    @Override
    public StoreOrderResponse fulfilOrder(String orderId, String processedBy) {
        StoreOrder order = getOrder(orderId);

        if (!"CONFIRMED".equals(order.getStatus()))
            throw new RuntimeException("Order " + order.getOrderRef() + " must be CONFIRMED before fulfilment");

        // 1 — Deduct stock and record movements
        for (OrderLineItem line : order.getLineItems()) {
            StoreItem item = itemRepository.findById(line.getStoreItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + line.getStoreItemId()));

            if (item.getQuantityInStock() < line.getQuantity())
                throw new RuntimeException("Stock depleted for " + item.getItemName());

            int newQty = item.getQuantityInStock() - line.getQuantity();
            item.setQuantityInStock(newQty);
            item.setUpdatedAt(LocalDateTime.now());
            itemRepository.save(item);

            movementRepository.save(StockMovement.builder()
                    .institutionCode(order.getInstitutionCode())
                    .storeItemId(item.getId())
                    .itemName(item.getItemName())
                    .movementType("PURCHASE_SALE")
                    .quantityChange(-line.getQuantity())
                    .balanceAfter(newQty)
                    .referenceId(order.getId())
                    .performedBy(processedBy)
                    .notes("Sale via " + order.getChannel() + " – " + order.getOrderRef())
                    .movementDate(LocalDateTime.now())
                    .build());

            if (newQty <= item.getReorderLevel())
                log.warn("LOW STOCK ALERT: {} ({}) now at {} units (reorder level {})",
                        item.getItemName(), item.getItemCode(), newQty, item.getReorderLevel());
        }

        // 2 — Post to Finance ledger
        String description = buildDescription(order);
        LedgerPostRequest ledgerReq = LedgerPostRequest.builder()
                .institutionCode(order.getInstitutionCode())
                .orderRef(order.getOrderRef())
                .buyerId(order.getBuyerId())
                .buyerName(order.getBuyerName())
                .amount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentReference(order.getPaymentReference())
                .description(description)
                .transactionDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .postedBy(processedBy)
                .build();

        Optional<String> ledgerRef = financeLedgerClient.postStoreSaleLedger(ledgerReq);

        // 3 — Mark fulfilled
        order.setStatus("FULFILLED");
        order.setProcessedBy(processedBy);
        order.setFulfilledDate(LocalDateTime.now());
        order.setLedgerPosted(ledgerRef.isPresent());
        ledgerRef.ifPresent(order::setLedgerReference);

        StoreOrder saved = orderRepository.save(order);

        if (!saved.getLedgerPosted())
            log.error("Ledger post FAILED for fulfilled order {} – will be retried", order.getOrderRef());

        return toResponse(saved);
    }

    // ── Cancel order ─────────────────────────────────────────────────────────

    @Override
    public StoreOrderResponse cancelOrder(String orderId, String reason) {
        StoreOrder order = getOrder(orderId);
        if ("FULFILLED".equals(order.getStatus()))
            throw new RuntimeException("Fulfilled orders cannot be cancelled; raise a return instead");

        order.setStatus("CANCELLED");
        return toResponse(orderRepository.save(order));
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    public Optional<StoreOrderResponse> getOrderByRef(String orderRef) {
        return orderRepository.findByOrderRef(orderRef).map(this::toResponse);
    }

    @Override
    public List<StoreOrderResponse> getOrdersByInstitution(StoreFetchRequest req) {
        List<StoreOrder> orders;
        if (req.getStatus() != null) {
            orders = orderRepository.findByInstitutionCodeAndStatus(req.getInstitutionCode(), req.getStatus());
        } else if (req.getChannel() != null) {
            orders = orderRepository.findByInstitutionCodeAndChannel(req.getInstitutionCode(), req.getChannel());
        } else {
            orders = orderRepository.findByInstitutionCode(req.getInstitutionCode());
        }
        return orders.stream().map(this::toResponse).toList();
    }

    @Override
    public List<StoreOrderResponse> getOrdersByBuyer(String institutionCode, String buyerId) {
        return orderRepository.findByInstitutionCodeAndBuyerId(institutionCode, buyerId)
                .stream().map(this::toResponse).toList();
    }

    // ── Retry failed ledger posts ─────────────────────────────────────────────

    @Override
    public void retryFailedLedgerPosts(String institutionCode) {
        List<StoreOrder> failed = orderRepository
                .findByInstitutionCodeAndLedgerPostedFalseAndStatus(institutionCode, "FULFILLED");

        log.info("Retrying {} failed ledger posts for {}", failed.size(), institutionCode);
        for (StoreOrder order : failed) {
            LedgerPostRequest req = LedgerPostRequest.builder()
                    .institutionCode(order.getInstitutionCode())
                    .orderRef(order.getOrderRef())
                    .buyerId(order.getBuyerId())
                    .buyerName(order.getBuyerName())
                    .amount(order.getTotalAmount())
                    .paymentMethod(order.getPaymentMethod())
                    .paymentReference(order.getPaymentReference())
                    .description(buildDescription(order))
                    .transactionDate(order.getFulfilledDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .postedBy("SYSTEM_RETRY")
                    .build();

            financeLedgerClient.postStoreSaleLedger(req).ifPresent(ref -> {
                order.setLedgerPosted(true);
                order.setLedgerReference(ref);
                orderRepository.save(order);
                log.info("Ledger retry succeeded for order {} → ref {}", order.getOrderRef(), ref);
            });
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private StoreOrder getOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    private String generateOrderRef(String institutionCode) {
        String shortCode = institutionCode.length() >= 3
                ? institutionCode.substring(0, 3).toUpperCase()
                : institutionCode.toUpperCase();
        return "ORD-" + shortCode + "-" + System.currentTimeMillis();
    }

    private String buildDescription(StoreOrder order) {
        StringBuilder sb = new StringBuilder("Store sale");
        if (order.getLineItems() != null && !order.getLineItems().isEmpty()) {
            sb.append(": ");
            order.getLineItems().forEach(l -> sb.append(l.getItemName()).append(" x").append(l.getQuantity()).append(", "));
            sb.setLength(sb.length() - 2); // trim trailing comma
        }
        sb.append(" – ").append(order.getOrderRef());
        return sb.toString();
    }

    private StoreOrderResponse toResponse(StoreOrder o) {
        return StoreOrderResponse.builder()
                .id(o.getId())
                .institutionCode(o.getInstitutionCode())
                .orderRef(o.getOrderRef())
                .buyerId(o.getBuyerId())
                .buyerName(o.getBuyerName())
                .buyerContact(o.getBuyerContact())
                .channel(o.getChannel())
                .status(o.getStatus())
                .lineItems(o.getLineItems())
                .totalAmount(o.getTotalAmount())
                .paymentMethod(o.getPaymentMethod())
                .paymentReference(o.getPaymentReference())
                .paymentConfirmed(o.getPaymentConfirmed())
                .processedBy(o.getProcessedBy())
                .ledgerPosted(o.getLedgerPosted())
                .ledgerReference(o.getLedgerReference())
                .orderDate(o.getOrderDate())
                .fulfilledDate(o.getFulfilledDate())
                .build();
    }
}
