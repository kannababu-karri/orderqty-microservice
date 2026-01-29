package com.restful.orderqty.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restful.orderqty.entity.OrderQty;
import com.restful.orderqty.exception.InvalidOrderQtyException;
import com.restful.orderqty.exception.OrderQtyNotFoundException;
import com.restful.orderqty.exception.ServiceException;
import com.restful.orderqty.service.OrderQtyService;

@RestController
@RequestMapping("/api/orderqty")
public class OrderQtyController {
	
	private static final Logger _LOGGER = LoggerFactory.getLogger(OrderQtyController.class);
	
    @Autowired
    private OrderQtyService orderQtyService;
    
    public OrderQtyController() {
    	_LOGGER.info(">>> OrderQtyController LOADED. <<<");
    }

    @PostMapping
	public ResponseEntity<OrderQty> createOrderQty(@RequestBody OrderQty orderQty) throws ServiceException {
    	_LOGGER.info(">>> Inside createOrderQty. <<<");
        if (orderQty.getQuantity() == null || orderQty.getQuantity() <= 0) {
            throw new InvalidOrderQtyException("Order quantity must not be empty");
        }

        OrderQty saved = orderQtyService.saveOrUpdate(orderQty);
        
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}
	
    @DeleteMapping("/{id}")
 	public ResponseEntity<String> delete(@PathVariable Long id) {
 		_LOGGER.info(">>> Inside deleteOrderQty. <<<");		
 		
 		if (id == null || id <= 0) {
 			throw new InvalidOrderQtyException("Order id must not be empty");
 		}
 		
 		try {
 			orderQtyService.deleteByOrderId(id);
 			return ResponseEntity.ok("Order deleted successfully.");		
 	    } catch (InvalidOrderQtyException ex) {
 	        // ID not found in DB
 	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
 	                .body("Order not found with id: " + id);
 	    } catch (Exception ex) {
 	        _LOGGER.error("Error deleting order with id {}: {}", id, ex.getMessage());
 	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
 	                .body("Unexpected error occurred: " + ex.getMessage());
 	    }
 		
 	}
    
    @GetMapping
    public ResponseEntity<List<OrderQty>> getAll() {
    	_LOGGER.info(">>> Inside getAll. <<<");
    	List<OrderQty> orderQtys = orderQtyService.findAllOrderQtys();
    	
    	if (orderQtys.isEmpty()) {
            //throw new OrderQtyNotFoundException("No orders found");
        }

        return ResponseEntity.ok(orderQtys);
    	
    }

    //GET products by multiple fields
/*    @GetMapping("/searchbyqty")
    public ResponseEntity<OrderQty> getBySearchByQty(
    		@RequestBody Manufacturer manufacturer,
    		@RequestBody Product product,
    		@RequestBody User user,
    		@PathVariable Long qty) {
        try {
            OrderQty orderQty = orderQtyService.findByManufacturerAndProductAndUser(manufacturer, product, user, qty)
            		.orElseThrow(() -> new OrderQtyNotFoundException("Order not found with mfg, product and user"));
            return ResponseEntity.ok(orderQty);
        } catch (ServiceException ex) {
            _LOGGER.error("Error fetching OrderQtys by multiple fields: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }*/
    
    //GET products by multiple fields
    @GetMapping("/search/mfgproductuser")
    public ResponseEntity<List<OrderQty>> getBySearchMfgproductuser(
    		@RequestParam(required = false) Long manufacturerId,
    		@RequestParam(required = false) Long productId,
    		@RequestParam(required = false)  Long userId) {
    	_LOGGER.info(">>> Inside getBySearchMfgproductuser. manufacturerId<<<:"+manufacturerId);		
    	_LOGGER.info(">>> Inside getBySearchMfgproductuser. productId<<<:"+productId);		
    	_LOGGER.info(">>> Inside getBySearchMfgproductuser. userId<<<:"+userId);		
        try {
        	List<OrderQty> orderQtys = orderQtyService.findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId(manufacturerId, productId, userId);
            return ResponseEntity.ok(orderQtys);
        } catch (ServiceException ex) {
            _LOGGER.error("Error fetching OrderQtys by multiple fields mfg, product and user: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/userid/{userId}")
	public ResponseEntity<List<OrderQty>> getByUserid(@PathVariable Long userId) {
    	_LOGGER.info(">>> Inside getById. <<<");
    	
    	if (userId == null || (userId != null && userId.intValue() <= 0)) {
			throw new InvalidOrderQtyException("User id must not be empty");
		}
    	
    	List<OrderQty> orderQtys = orderQtyService.findByUser_UserId(userId);
    	
    	if (orderQtys.isEmpty()) {
            //throw new OrderQtyNotFoundException("No orders found");
        }
    	
		return ResponseEntity.ok(orderQtys);
    }
    
    // GET products by multiple fields
    @GetMapping("/search/mfguser")
    public ResponseEntity<List<OrderQty>> getBySearchMfguser(
            @RequestParam(required = false) Long manufacturerId,
            @RequestParam(required = false) Long userId) {
        try {
            List<OrderQty> orderQtys = orderQtyService.findByManufacturer_ManufacturerIdAndUser_UserId(manufacturerId, userId);
            return ResponseEntity.ok(orderQtys);
        } catch (ServiceException ex) {
            _LOGGER.error("Error fetching OrderQtys by multiple fields mfg and user: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET products by multiple fields
    @GetMapping("/search/productuser")
    public ResponseEntity<List<OrderQty>> getBySearchProductuser(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long userId) {
        try {
            List<OrderQty> orderQtys = orderQtyService.findByProduct_ProductIdAndUser_UserId(productId, userId);
            return ResponseEntity.ok(orderQtys);
        } catch (ServiceException ex) {
            _LOGGER.error("Error fetching OrderQtys by multiple fields product and user: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/orderid/{orderId}")
	public ResponseEntity<OrderQty> getByOrderId(@PathVariable Long orderId) {
        try {
        	OrderQty orderQty = orderQtyService
	                .findByOrderId(orderId)
	                .orElseThrow(() -> new OrderQtyNotFoundException("Order not found with id: " + orderId));
			return ResponseEntity.ok(orderQty);
        } catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByOrderId."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findByOrderId."+exp.toString());
		}
    }
}
