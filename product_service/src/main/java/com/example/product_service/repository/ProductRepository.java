package com.example.product_service.repository;

import com.example.product_service.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
//   List<Product> findAllById(List<String> ids);
   List<Product> findByIdInAndIsDeleted(List<String> ids, Boolean isDeleted);

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   @Query("select p from Product p where p.id in :ids")
   List<Product> findByIdInForUpdate(@Param("ids") List<String> ids);


}
