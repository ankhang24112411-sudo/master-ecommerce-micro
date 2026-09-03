package com.example.product_service.service.cache.prod;

import com.example.product_service.entity.Product;
import com.example.product_service.infra.cache.RedisInfraService;
import com.example.product_service.infra.redission.RedisDistributedLocker;
import com.example.product_service.infra.redission.RedisDistributedService;
import com.example.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j(topic = "PRODUCT-CACHE-SERVICE")
@RequiredArgsConstructor
public class ProductCacheService {
    private final RedisDistributedService redisDistributedService;
    private final RedisInfraService redisInfraService;
    private final ProductService productService;
    private String genEventItemKey(String itemId) {
        return "PRODUCT:" + itemId;
    }
    private String genEventItemKeyLock(Long itemId) {
        return "PRODUCT_LOCK" + itemId;
    }
    private final static Cache<String, Product> productLocalCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .concurrencyLevel(12)
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .build();

    //TODO First Level : Redis easy
    //Redis has it => response
    //if not => goes to DBS => response
    public Product getProductDefaultCacheNormal(String id , Long version) {
        Product product = redisInfraService.getObject(genEventItemKey(id), Product.class);
        if (product != null) {
            return product;
        }
        product = productService.getById(id);
        if (product != null) {
            redisInfraService.setObject(genEventItemKey(id), product);
        }

        return product;
    }
   //TODO Second level : Redis (Cache Aside) + distributed Lock
    public Product getProductDefaultCacheVip(String id ,Long version){

        /*
         * A và B là hai request riêng biệt.
         *
         * Mỗi request có biến ticketDetail riêng:
         * - A có ticketDetail của A.
         * - B có ticketDetail của B.
         *
         * Nhưng cả hai cùng dùng chung:
         * - Redis.
         * - Database.
         * - Distributed lock key.
         */


        /*
         * A chạy dòng này → Redis chưa có → ticketDetail của A = null.
         *
         * B cũng chạy dòng này → Redis chưa có → ticketDetail của B = null.
         */
        Product product = redisInfraService.getObject(genEventItemKey(id) , Product.class);
        log.info("CACHE HIT : id ={}" , id);
        /*
         * A: ticketDetail = null → không vào if.
         * B: ticketDetail = null → không vào if.
         *
         * Vì vậy cả A và B đều chạy tiếp xuống dưới.
         */
        if(product != null){
            return product;
        }
        log.info("CACHE MISS , GET FROM DBS");

        /*
         * id của A = 100.
         * id của B = 100.
         *
         * Vì vậy cả hai cùng lấy đối tượng lock có key:
         *
         * PRO_LOCK_KEY_ITEM100
         *
         * Hai request đang cạnh tranh cùng một lock.
         */
        // Tao lock process voi KEY
        /*
         * BƯỚC 2: Lấy đối tượng distributed lock.
         *
         * Ví dụ id = 100:
         * lock key = "PRO_LOCK_KEY_ITEM100"
         *
         * Những request cùng id = 100 sẽ dùng chung lock key.
         *
         * Request id = 100 và id = 200 dùng hai lock khác nhau,
         * nên không chặn lẫn nhau.
         *
         * Dòng này chỉ lấy đối tượng locker.
         * Lock thực sự được lấy ở dòng tryLock() phía dưới.
         */
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock("PRODUCT_LOCK" + id);
        try {
            // 1 - Tao lock

            /*
             * ĐÂY LÀ CHỖ A VÀ B BẮT ĐẦU KHÁC NHAU.
             *
             * A gọi tryLock trước:
             * → Lock đang trống.
             * → A lấy được lock.
             * → isLock của A = true.
             *
             * B gọi tryLock sau:
             * → Lock đang bị A giữ.
             * → B DỪNG NGAY TẠI DÒNG NÀY.
             * → B chờ tối đa 1 giây.
             *
             * Khi B đang chờ ở đây, B chưa chạy xuống
             * if (!isLock) và chưa đọc Redis lần hai.
             *
             * Trong lúc đó A tiếp tục chạy các dòng phía dưới.
             */
            // 1 - Tao lock
            /*
             * BƯỚC 3: Thử lấy distributed lock.
             *
             * Tham số 1:
             * - Chờ lấy lock tối đa 1 giây.
             *
             * Tham số 5:
             * - Nếu lấy được lock thì thời gian giữ tối đa là 5 giây.
             *
             * TimeUnit.SECONDS:
             * - Đơn vị của 1 và 5 là giây.
             *
             * Kết quả:
             * - true: request này lấy được lock.
             * - false: sau tối đa 1 giây vẫn không lấy được lock.
             */
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            /*
             * Nếu không lấy được lock:
             * - isLock = false.
             * - ticketDetail lúc này vẫn là null do lần đọc Redis đầu tiên.
             * - Vì vậy return ticketDetail ở đây chính là return null.
             *
             * Sau khi gặp return, Java vẫn chạy khối finally.
             */
            /*
             * REQUEST A:
             *
             * isLock của A = true.
             * !true = false.
             * A không vào if và chạy tiếp.
             *
             *
             * REQUEST B:
             *
             * B hiện vẫn đang chờ ở dòng tryLock phía trên.
             * B chưa chạy tới đoạn này.
             */
            if(!isLock){
                return product;
            }
            /*
             * Nếu chạy qua được if phía trên:
             * - isLock = true.
             * - Request hiện tại đã lấy được lock.
             */

            // stub...
            // Get cache

            /*
             * BƯỚC 4: Đọc Redis lần thứ hai.
             *
             * Đây là double-check cache.
             *
             * Trong khoảng thời gian từ lần đọc Redis đầu tiên
             * đến lúc request này lấy được lock, một request khác
             * có thể đã đọc database và ghi dữ liệu vào Redis.
             *
             * Vì vậy không được truy vấn DB ngay mà phải kiểm tra
             * Redis thêm lần nữa.
             */
            // Get cache

            /*
             * Hiện tại chỉ A đang chạy tới đây.
             *
             * A đọc Redis lần thứ hai.
             *
             * Redis vẫn chưa có dữ liệu vì chưa request nào đọc database và ghi Redis.
             *
             * Kết quả:
             * ticketDetail của A = null.
             */
            product = redisInfraService.getObject(genEventItemKey(id), Product.class);
            /*
             * ticketDetail của A = null.
             *
             * A không vào if và tiếp tục chạy xuống database.
             */
            if(product != null){
                return product;
            }
//            3 -> van khong co thi truy van DB

                    /*
                     * Chỉ A chạy dòng này.
                     *
                     * A lấy ticket 100 từ database.
                     *
                     * Giả sử database có ticket:
                     * ticketDetail của A = TicketDetail(id=100).
                     */
            // 3 -> van khong co thi truy van DB

            /*
             * BƯỚC 5: Redis vẫn không có dữ liệu sau khi đã lấy lock.
             *
             * Request hiện tại truy vấn database bằng id.
             * Đây là request đang giữ lock nên nó chịu trách nhiệm
             * lấy dữ liệu và nạp lại cache.
             */
            product = productService.getById(id);
            /*
             * Nếu database cũng trả về null:
             * - Ticket không tồn tại trong database.
             * - Code gọi setObject() với ticketDetail = null.
             * - Sau đó return null.
             *
             * setObject xử lý null như thế nào phụ thuộc hoàn toàn
             * vào code bên trong RedisInfrasService.
             */
            /*
             * Database có ticket nên ticketDetail của A khác null.
             *
             * A không vào if này.
             */
            if (product == null) { // Neu trong dbs van khong co thi return ve not exists;
//                log.info("TICKET NOT EXITS....{}", version);
                // set
                redisInfraService.setObject(genEventItemKey(id), product);
                return product;
            }
            /*
             * A ghi TicketDetail(id=100) vào Redis.
             *
             * Redis lúc này thay đổi từ:
             *
             * PRO_EVENT_ITEM100 → không có
             *
             * thành:
             *
             * PRO_EVENT_ITEM100 → TicketDetail(id=100)
             *
             * B vẫn đang chờ lock ở dòng tryLock phía trên.
             */
            redisInfraService.setObject(genEventItemKey(id) , product); // TTL
            /*
             * A chuẩn bị trả TicketDetail.
             *
             * Nhưng trước khi return thật sự,
             * Java phải chạy khối finally.
             */
            return product;



    } catch (InterruptedException e) {

            throw new RuntimeException(e);
        }finally {

            /*
             * A chạy xuống đây và unlock.
             *
             * Lock PRO_LOCK_KEY_ITEM100 được giải phóng.
             *
             * NGAY SAU KHI A UNLOCK:
             *
             * B đang chờ tại tryLock có thể lấy được lock.
             */
            locker.unlock();
        }
    }
    public Product getProductLocalCache(String id){
        try{
            return productLocalCache.getIfPresent(id);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
   public Product getProductDefaultCacheLocal(String id ){
        Product product = getProductLocalCache(id);
        if(product != null){
            log.info("GET FROM LOCAL CACHE");
            return product;
        }
        product = redisInfraService.getObject(genEventItemKey(id), Product.class);
        if(product != null){
            log.info("FROM DISTRIBUTED CACHE EXIST {}", product);
          return product;
        }
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock("PRODUCT_LOCK"+id);
        try{
            boolean isLock = locker.tryLock(1, 4 , TimeUnit.SECONDS);
            if(!isLock){
                return product;
            }
            product = redisInfraService.getObject(genEventItemKey(id), Product.class);

            if(product != null){
                productLocalCache.put(id, product);
                return product;
            }
            // 3 -> van khong co thi truy van DB
        product = productService.getById(id);

            if(product == null){
                redisInfraService.setObject(genEventItemKey(id), product);
                productLocalCache.put(id, null);
                return product;
            }
            redisInfraService.setObject(genEventItemKey(id), product);
            productLocalCache.put(id , product);
              return product;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
   }
}
