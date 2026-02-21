package com.restful.orderqty.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restful.orderqty.entity.Manufacturer;
import com.restful.orderqty.entity.OrderQty;
import com.restful.orderqty.entity.Product;
import com.restful.orderqty.entity.User;

@Repository
public interface OrderQtyRepository extends JpaRepository<OrderQty, Long>{
	Optional<OrderQty> findByOrderId(Long id);
	
	void deleteByOrderId(Long id);
	
	Optional<OrderQty> findByManufacturerAndProductAndUser(Manufacturer manufacturer, Product product, User user, Long qty);
	
	Optional<OrderQty> findByManufacturerAndProduct(Manufacturer manufacturer, Product product);
	
	// Find by userid
	Page<OrderQty> findByUser_UserId(Long userId, Pageable pageable);

    // Find by mfg id
	Page<OrderQty> findByManufacturer_ManufacturerIdAndUser_UserId(Long manufacturerId, Long userId, Pageable pageable);
    
    // Find by product id
	Page<OrderQty> findByProduct_ProductIdAndUser_UserId(Long productId, Long userId, Pageable pageable);

    // Find by user, mfg and product id
	Page<OrderQty> findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId(Long manufacturerId, Long productId, Long userId, Pageable pageable);
}
