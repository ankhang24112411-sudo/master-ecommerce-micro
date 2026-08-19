package com.example.product_service.repository;

import com.example.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
   List<Product> findAllByIdn(List<String> ids);
   List<Product> findByIdInAndIsDeleted(List<String> ids, Boolean isDeleted);

}
