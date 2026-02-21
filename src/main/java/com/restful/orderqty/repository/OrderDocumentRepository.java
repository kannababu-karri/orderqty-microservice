package com.restful.orderqty.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.restful.orderqty.entity.OrderDocument;

/**
 * Mongo db intraaction
 */
@Repository
public interface OrderDocumentRepository extends MongoRepository<OrderDocument, String> {
	Page<OrderDocument> findAll(Pageable pageable);

	Page<OrderDocument> findByUserId(Long userId, Pageable pageable);
    
	Page<OrderDocument> findByManufacturerId(Long manufacturerId, Long userId, Pageable pageable);
    
	Page<OrderDocument> findByProductId(Long productId, Long userId, Pageable pageable);
    
    // Find by user, mfg and product id
	Page<OrderDocument> findByManufacturerIdAndProductIdAndUserId(Long manufacturerId, Long productId, Long userId, Pageable pageable);
}
