package com.restful.orderqty.repository;

import java.util.List;
import java.util.Optional;

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
	
	// Find by userid
    List<OrderQty> findByUser_UserId(Long userId);

    // Find by mfg id
    List<OrderQty> findByManufacturer_ManufacturerIdAndUser_UserId(Long manufacturerId, Long userId);
    
    // Find by product id
    List<OrderQty> findByProduct_ProductIdAndUser_UserId(Long productId, Long userId);

    // Find by user, mfg and product id
    List<OrderQty> findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId(Long manufacturerId, Long productId, Long userId);
}
