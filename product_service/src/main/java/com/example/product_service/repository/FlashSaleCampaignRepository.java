package com.example.product_service.repository;

import com.example.product_service.dto.FlashSaleCampaignProjection;
import com.example.product_service.entity.FlashSaleCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FlashSaleCampaignRepository extends JpaRepository<FlashSaleCampaign,String > {
    @Query("""

select 
    f as flashSaleCampaign,
    p.name as productName,
    c.name as categoryName
from FlashSaleCampaign f
join Product p 
on p.id = f.productId
join Category c
on c.id = p.categoryId
where f.id = :id
""")
    FlashSaleCampaignProjection findCacheById(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("update FlashSaleCampaign f set f.updatedAt = CURRENT_TIMESTAMP," +
             " f.stock =:oldStockAvailable - :quantity " +
            "where f.id =:flashSaleId and f.stock =:oldStockAvailable")
    int decreaseStockV3CAS(@Param("flashSaleId") String flashSaleId , @Param("oldStockAvailable")int oldStockAvailable, int quantity );

    @Modifying
    @Transactional
    @Query("update FlashSaleCampaign f set f.updatedAt = CURRENT_TIMESTAMP," +
            " f.stock = f.stock - :quantity " +
            "where f.id =:flashSaleId")
    int decreaseStockV1(@Param("flashSaleId") String flashSaleId , @Param("oldStockAvailable")int quantity );
    }