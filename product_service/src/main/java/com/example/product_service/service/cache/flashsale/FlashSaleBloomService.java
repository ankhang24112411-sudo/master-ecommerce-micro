package com.example.product_service.service.cache.flashsale;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "FLASH-SALE-BLOOM-SERVICE")
public class FlashSaleBloomService {
    private final StringRedisTemplate redisTemplate;

    /**
     * Kiểm tra và thêm User vào Redis Bloom Filter
     * Sử dụng lệnh trực tiếp của RedisBloom module: BF.EXISTS và BF.ADD
     *
     * @return true nếu user ĐÃ TỒN TẠI trong bloom (đã mua rồi), false nếu chưa
     */
    public boolean checkAndAddUser(String flashSaleId, String userId) {
        String bloomKey = "flashsale:" + flashSaleId + ":bloom";
        // Sử dụng Pipelining của Spring Data Redis (`executePipelined`) để gộp nhiều lệnh
        // gửi đi trong 1 lần kết nối mạng duy nhất tới Redis, giúp tối ưu hiệu năng cực cao.

        // 1. Kiểm tra xem user đã có trong Bloom Filter chưa
        // Lệnh Redis: BF.EXISTS flashsale:{campaignId}:bloom userId
        // Lệnh 2: Thêm user này vào Bloom Filter ngay lập tức (nếu chưa có).
        // Cú pháp RedisBloom gốc: BF.ADD key item
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.execute("BF.EXISTS", bloomKey.getBytes(StandardCharsets.UTF_8), userId.getBytes(StandardCharsets.UTF_8));
            connection.execute("BF.ADD", bloomKey.getBytes(StandardCharsets.UTF_8));
            return null; // Pipeline yêu cầu trả về null ở hàm callback này
        });

        // Sau khi chạy pipeline, Redis trả về một danh sách kết quả chứa phản hồi của cả 2 lệnh trên (theo đúng thứ tự gọi).
        // results.get(0) ứng với kết quả của lệnh BF.EXISTS.
        if (results != null && !results.isEmpty()) {
            Object existsResult = results.get(0);

            if (existsResult instanceof Long && (Long) existsResult == 1l) {
                return true;// Trả về true -> Báo hiệu user ĐÃ MUA RỒI -> Chặn!
            }
            if (existsResult instanceof Boolean && (Boolean) existsResult) {
                return true;
            }
        }
        return false;
    }
}
