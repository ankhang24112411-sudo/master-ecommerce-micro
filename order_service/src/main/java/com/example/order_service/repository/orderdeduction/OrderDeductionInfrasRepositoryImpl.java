package com.example.order_service.repository.orderdeduction;

import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class OrderDeductionInfrasRepositoryImpl implements OrderDeductionRepository {

    @Autowired
    private EntityManager entityManager;

    private static final String tablePrefix = "orders_";
    private static final String itemTablePrefix = "order_items_";

    private static final Map<String, Boolean> tableCreatedCache =
            new java.util.concurrent.ConcurrentHashMap<>();

    private String getTableName(String monthOrder) {
        return tablePrefix + monthOrder;
    }

    private String getItemTableName(String monthOrder) {
        return itemTablePrefix + monthOrder;
    }

    // =========================================================
    // INSERT ORDER
    // =========================================================

    @Override
    @Transactional
    public void insertOrder(String yearMonth, OrderEntity order) {

        ensureTableExists(yearMonth);

        String tableName = getTableName(yearMonth);

        if (order.getId() == null) {
            order.setId(UUID.randomUUID().toString());
        }

        LocalDateTime now = LocalDateTime.now();

        order.setCreatedDate(now);
        order.setLastModifiedDate(now);

        if (order.getIsDeleted() == null) {
            order.setIsDeleted(false);
        }

        String sql = "INSERT INTO " + tableName
                + " (order_number, customer_id, status, total_amount"
                + " is_deleted, created_date, created_by,"
                + " last_modified_date, last_modified_by) "
                + "VALUES (:orderNumber, :customerId, :status, :totalAmount,"
                + " :isDeleted, :createdDate, :createdBy,"
                + " :lastModifiedDate, :lastModifiedBy)";

        entityManager.createNativeQuery(sql)
                .setParameter("orderNumber", order.getOrderNumber())
                .setParameter("customerId", order.getCustomerId())
                .setParameter("status", order.getStatus())
                .setParameter("totalAmount", order.getTotalAmount())
                .setParameter("isDeleted", order.getIsDeleted())
                .setParameter("createdDate", order.getCreatedDate())
                .setParameter("createdBy", order.getCreatedBy())
                .setParameter("lastModifiedDate", order.getLastModifiedDate())
                .setParameter("lastModifiedBy", order.getLastModifiedBy())
                .executeUpdate();
    }



    @Override
    @Transactional
    public void insertOrderItem(String yearMonth, OrderItemEntity orderItem) {

        ensureTableExists(yearMonth);

        String tableName = getItemTableName(yearMonth);

        if (orderItem.getId() == null) {
            orderItem.setId(UUID.randomUUID().toString());
        }

        LocalDateTime now = LocalDateTime.now();

        orderItem.setCreatedDate(now);
        orderItem.setLastModifiedDate(now);

        if (orderItem.getIsDeleted() == null) {
            orderItem.setIsDeleted(false);
        }

        String sql = "INSERT INTO " + tableName
                + " (order_id, product_id, price, quantity,"
                + " is_deleted, created_date, created_by,"
                + " last_modified_date, last_modified_by) "
                + "VALUES (:orderId, :productId, :price, :quantity,"
                + " :isDeleted, :createdDate, :createdBy,"
                + " :lastModifiedDate, :lastModifiedBy)";

        entityManager.createNativeQuery(sql)
                .setParameter("orderId", orderItem.getOrderId())
                .setParameter("productId", orderItem.getProductId())
                .setParameter("price", orderItem.getPrice())
                .setParameter("quantity", orderItem.getQuantity())
                .setParameter("isDeleted", orderItem.getIsDeleted())
                .setParameter("createdDate", orderItem.getCreatedDate())
                .setParameter("createdBy", orderItem.getCreatedBy())
                .setParameter("lastModifiedDate", orderItem.getLastModifiedDate())
                .setParameter("lastModifiedBy", orderItem.getLastModifiedBy())
                .executeUpdate();
    }



    @Override
    public List<Object[]> findAll(String yearMonth) {

        String tableName = getTableName(yearMonth);

        String sql =
                "SELECT * FROM " + tableName
                        + " ORDER BY created_date DESC";

        return entityManager
                .createNativeQuery(sql)
                .getResultList();
    }

    @Override
    public Object[] findByOrderNumber(
            String yearMonth,
            String orderNumber
    ) {

        String tableName = getTableName(yearMonth);

        String sql =
                "SELECT * FROM " + tableName + " WHERE order_number = :orderNumber";

        List<Object[]> resultList =
                entityManager.createNativeQuery(sql)
                        .setParameter("orderNumber", orderNumber)
                        .getResultList();

        return resultList.isEmpty()
                ? null
                : resultList.get(0);
    }

    @Override
    public List<Object[]> findByDateRange(
            String yearMonth,
            Instant startDate,
            Instant endDate
    ) {
        return List.of();
    }

    @Override
    public List<Object[]> findByDateRange(
            String yearMonth,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        String tableName = getTableName(yearMonth);

        String sql =
                "SELECT * FROM " + tableName
                        + " WHERE created_date BETWEEN :startDate AND :endDate";

        return entityManager.createNativeQuery(sql)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    // =========================================================
    // POSTGRESQL DDL
    // =========================================================

    /**
     * PostgreSQL:
     *
     * Java BigDecimal     -> NUMERIC(10,3)
     * Java LocalDateTime  -> TIMESTAMP(6)
     */
    private static final String CREATE_TABLE_TEMPLATE =
            "CREATE TABLE IF NOT EXISTS %s ("
                    + "  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                    + " customer_id VARCHAR(36),"
                    + " status VARCHAR(255),"
                    + " total_amount NUMERIC(10,3),"
                    + " order_number VARCHAR(255) UNIQUE,"
                    + " is_deleted BOOLEAN,"
                    + " created_date TIMESTAMP(6),"
                    + " created_by VARCHAR(255),"
                    + " last_modified_date TIMESTAMP(6),"
                    + " last_modified_by VARCHAR(255)"
                    + ")";


    private static final String CREATE_ITEM_TABLE_TEMPLATE =
            "CREATE TABLE IF NOT EXISTS %s ("
                    + "  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                    + " order_id BIGINT,"
                    + " product_id VARCHAR(36),"
                    + " price NUMERIC(10,3),"
                    + " quantity INTEGER,"
                    + " is_deleted BOOLEAN,"
                    + " created_date TIMESTAMP(6),"
                    + " created_by VARCHAR(255),"
                    + " last_modified_date TIMESTAMP(6),"
                    + " last_modified_by VARCHAR(255)"
                    + ")";


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    @Transactional
    public boolean updateOrderStatus(
            String yearMonth,
            String orderNumber,
            String status
    ) {

        String tableName = getTableName(yearMonth);

        String sql =
                "UPDATE " + tableName
                        + " SET status = :status,"
                        + " last_modified_date = :lastModifiedDate"
                        + " WHERE order_number = :orderNumber";

        int result =
                entityManager.createNativeQuery(sql)
                        .setParameter("status", status)
                        .setParameter(
                                "lastModifiedDate",
                                LocalDateTime.now()
                        )
                        .setParameter(
                                "orderNumber",
                                orderNumber
                        )
                        .executeUpdate();

        return result > 0;
    }

    // =========================================================
    // PAGINATION
    // =========================================================

    @Override
    public List<Object[]> findPage(
            String yearMonth,
            Integer lastId,
            int limit
    ) {

        String tableName = getTableName(yearMonth);

        if (lastId == null || lastId == 0) {

            String sql =
                    "SELECT * FROM " + tableName
                            + " ORDER BY id DESC LIMIT :limit";

            return entityManager.createNativeQuery(sql)
                    .setParameter("limit", limit)
                    .getResultList();
        }

        String sql =
                "SELECT * FROM " + tableName
                        + " WHERE id < :lastId"
                        + " ORDER BY id DESC"
                        + " LIMIT :limit";

        return entityManager.createNativeQuery(sql)
                .setParameter("lastId", lastId)
                .setParameter("limit", limit)
                .getResultList();
    }

    // =========================================================
    // ORDER ITEMS
    // =========================================================

    @Override
    public List<Object[]> findItemsByOrderId(
            String yearMonth,
            Integer orderId
    ) {

        String tableName = getItemTableName(yearMonth);

        String sql =
                "SELECT * FROM " + tableName
                        + " WHERE order_id = :orderId";

        return entityManager.createNativeQuery(sql)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    // =========================================================
    // CREATE DYNAMIC TABLE
    // =========================================================

    private void ensureTableExists(String yearMonth) {

        String orderTableName =
                getTableName(yearMonth);

        String itemTableName =
                getItemTableName(yearMonth);

        ensureOneTableExists(
                orderTableName,
                CREATE_TABLE_TEMPLATE,
                true
        );

        ensureOneTableExists(
                itemTableName,
                CREATE_ITEM_TABLE_TEMPLATE,
                false
        );
    }


    private void ensureOneTableExists(
            String tableName,
            String ddlTemplate,
            boolean orderTable
    ) {

        if (tableCreatedCache.containsKey(tableName)) {
            return;
        }

        synchronized (tableCreatedCache) {

            if (tableCreatedCache.containsKey(tableName)) {
                return;
            }

            log.info(
                    "Checking and creating PostgreSQL table if not exists: {}",
                    tableName
            );

            try {

                // CREATE TABLE
                String sql =
                        String.format(
                                ddlTemplate,
                                tableName
                        );

                entityManager
                        .createNativeQuery(sql)
                        .executeUpdate();

                // CREATE INDEX
                if (orderTable) {
                    createOrderIndexes(tableName);
                } else {
                    createOrderItemIndexes(tableName);
                }

                tableCreatedCache.put(
                        tableName,
                        true
                );

            } catch (Exception e) {

                log.error(
                        "Failed to ensure table exists for: {}",
                        tableName,
                        e
                );

                throw e;
            }
        }
    }

    // =========================================================
    // POSTGRESQL INDEX
    // =========================================================

    private void createOrderIndexes(String tableName) {

        String customerIndex =
                "CREATE INDEX IF NOT EXISTS "
                        + "idx_" + tableName + "_customer_id "
                        + "ON " + tableName + " (customer_id)";

        String createdDateIndex =
                "CREATE INDEX IF NOT EXISTS "
                        + "idx_" + tableName + "_created_date "
                        + "ON " + tableName + " (created_date)";

        entityManager
                .createNativeQuery(customerIndex)
                .executeUpdate();

        entityManager
                .createNativeQuery(createdDateIndex)
                .executeUpdate();
    }


    private void createOrderItemIndexes(String tableName) {

        String orderIdIndex =
                "CREATE INDEX IF NOT EXISTS "
                        + "idx_" + tableName + "_order_id "
                        + "ON " + tableName + " (order_id)";

        String productIdIndex =
                "CREATE INDEX IF NOT EXISTS "
                        + "idx_" + tableName + "_product_id "
                        + "ON " + tableName + " (product_id)";

        String createdDateIndex =
                "CREATE INDEX IF NOT EXISTS "
                        + "idx_" + tableName + "_created_date "
                        + "ON " + tableName + " (created_date)";

        entityManager
                .createNativeQuery(orderIdIndex)
                .executeUpdate();

        entityManager
                .createNativeQuery(productIdIndex)
                .executeUpdate();

        entityManager
                .createNativeQuery(createdDateIndex)
                .executeUpdate();
    }
}