package com.example.product_service.service.cache.flashsale.bloom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "FLASH-SALE-BLOOM-SERVICE")
public class FlashSaleBloomService {
    private static final String BLOOM_KEY_PATTERN = "flashsale:{%s}:bloom";
    private final RedissonClient redissonClient;
//    public boolean checkAndAddUser(String flashSaleId, String userId) {
//        String bloomKey = "flashsale:" + flashSaleId + ":bloom";
//        // Sử dụng Pipelining của Spring Data Redis (`executePipelined`) để gộp nhiều lệnh
//        // gửi đi trong 1 lần kết nối mạng duy nhất tới Redis, giúp tối ưu hiệu năng cực cao.
//
//        // 1. Kiểm tra xem user đã có trong Bloom Filter chưa
//        // Lệnh Redis: BF.EXISTS flashsale:{campaignId}:bloom userId
//        // Lệnh 2: Thêm user này vào Bloom Filter ngay lập tức (nếu chưa có).
//        // Cú pháp RedisBloom gốc: BF.ADD key item
//        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//            connection.execute("BF.EXISTS", bloomKey.getBytes(StandardCharsets.UTF_8), userId.getBytes(StandardCharsets.UTF_8));
//            connection.execute("BF.ADD", bloomKey.getBytes(StandardCharsets.UTF_8));
//            return null; // Pipeline yêu cầu trả về null ở hàm callback này
//        });
//
//        // Sau khi chạy pipeline, Redis trả về một danh sách kết quả chứa phản hồi của cả 2 lệnh trên (theo đúng thứ tự gọi).
//        // results.get(0) ứng với kết quả của lệnh BF.EXISTS.
//        if (results != null && !results.isEmpty()) {
//            Object existsResult = results.get(0);
//
//            if (existsResult instanceof Long && (Long) existsResult == 1l) {
//                return true;// Trả về true -> Báo hiệu user ĐÃ MUA RỒI -> Chặn!
//            }
//            if (existsResult instanceof Boolean && (Boolean) existsResult) {
//                return true;
//            }
//        }
//        return false;
//    }
   private String buildKey(String flashSaleId){
       if(flashSaleId == null || flashSaleId.isBlank()){
           throw new IllegalArgumentException("flashSaleId muss not be null");

       }
       return BLOOM_KEY_PATTERN.formatted(flashSaleId);
   }
    public boolean checkAndAddUser(String flashSaleId, String userId) {

        String key = buildKey(flashSaleId);

        RBloomFilterNative<String> bloomFilter =
                redissonClient.getBloomFilterNative(key);

        boolean added = bloomFilter.add(userId);

        return !added;
    }
}
}
