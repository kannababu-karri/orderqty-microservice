package com.restful.orderqty.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restful.orderqty.entity.OrderDocument;
import com.restful.orderqty.entity.OrderQty;
import com.restful.orderqty.entity.PageResponseDto;
import com.restful.orderqty.exception.InvalidOrderQtyException;
import com.restful.orderqty.exception.OrderQtyNotFoundException;
import com.restful.orderqty.exception.ServiceException;
import com.restful.orderqty.service.OrderDocumentService;
import com.restful.orderqty.service.OrderQtyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orderqty")
public class OrderQtyController {
	
	private static final Logger _LOGGER = LoggerFactory.getLogger(OrderQtyController.class);
	
    @Autowired
    private OrderQtyService orderQtyService;
	
    @Autowired
    private OrderDocumentService orderDocumentService;
    
    public OrderQtyController() {
    	_LOGGER.info(">>> OrderQtyController LOADED. <<<");
    }

    @PostMapping({ "", "/" })
	public ResponseEntity<OrderQty> createOrderQty(@Valid @RequestBody OrderQty orderQty) throws ServiceException {
    	_LOGGER.info(">>> Inside createOrderQty. <<<");
    	
    	_LOGGER.info(">>> Inside getManufacturerId. <<<"+orderQty.getManufacturer().getManufacturerId());
    	_LOGGER.info(">>> Inside getProductId. <<<"+orderQty.getProduct().getProductId());
    	_LOGGER.info(">>> Inside createOrderQty. <<<"+orderQty.getQuantity());
       
        OrderQty saved = orderQtyService.saveOrUpdate(orderQty);
        
        //return new ResponseEntity<>(saved, HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
    
    @PutMapping("/{id}")
    public ResponseEntity<OrderQty> update(@PathVariable Long id,
                                               @RequestBody OrderQty orderQty) {
    	_LOGGER.info(">>> Inside update. <<<");
    	orderQty.setOrderId(id);
    	OrderQty updated = orderQtyService.updateOrderQty(orderQty);
        return ResponseEntity.ok(updated);
    }
	
    @DeleteMapping("/{id}")
 	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
 		_LOGGER.info(">>> Inside deleteOrderQty. <<<");		
 		
 		if (id == null || id <= 0) {
 			throw new InvalidOrderQtyException("Order id must not be empty");
 		}
 		
 		Map<String, String> response = new HashMap<>();
 		
 		try {
 			orderQtyService.deleteByOrderId(id);
 			//return ResponseEntity.ok("Order deleted successfully.");
 			response.put("message", "Deleted successfully");
			
		    return ResponseEntity.ok(response);
		    
	    } catch (InvalidOrderQtyException ex) {
	    	_LOGGER.error("Error deleting orderqty InvalidOrderQtyException with id {}: {}", id, ex.getMessage());
	        response.put("status", HttpStatus.NOT_FOUND.toString());
	        response.put("message", "OrderQty not found with id: " + id);
	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body(response);
	    } catch (Exception ex) {
	        _LOGGER.error("Error deleting orderqty exception with id {}: {}", id, ex.getMessage());
	        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.put("message", "Unexpected error occurred: " + ex.getMessage());
	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body(response);
	    }
 	}
    
    @GetMapping({"", "/"})
    public ResponseEntity<PageResponseDto<OrderQty>> getAll(
    		@PageableDefault(size = 5)
    	    Pageable pageable
    		) {
    	_LOGGER.info(">>> Inside getAll. <<<");
    	
    	Page<OrderQty> page = orderQtyService.search(null, null, null, pageable);
    	
    	PageResponseDto<OrderQty> dto = new PageResponseDto<>();
    	
    	if(page != null) {
	    	dto.setContent(page.getContent());
	        dto.setTotalPages(page.getTotalPages());
	        dto.setTotalElements(page.getTotalElements());
	        dto.setPageNumber(page.getNumber());
	        dto.setPageSize(page.getSize());
    	}
    	
    	if (page != null && page.isEmpty()) {
            //throw new OrderQtyNotFoundException("No orders found");
        }

        return ResponseEntity.ok(dto);	
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
    
    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<OrderQty>> searchOrders(
            @RequestParam(required = false) Long manufacturerId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 5)
    	    Pageable pageable) {
    	
    	_LOGGER.info(">>> Inside searchOrders. manufacturerId<<<:"+manufacturerId);		
    	_LOGGER.info(">>> Inside searchOrders. productId<<<:"+productId);		
    	_LOGGER.info(">>> Inside searchOrders. userId<<<:"+userId);
    	
    	try {
        	Page<OrderQty> page = orderQtyService.search(manufacturerId, productId, userId, pageable);
        	
        	PageResponseDto<OrderQty> dto = new PageResponseDto<>();
        	
        	if(page != null) {
    	    	dto.setContent(page.getContent());
    	        dto.setTotalPages(page.getTotalPages());
    	        dto.setTotalElements(page.getTotalElements());
    	        dto.setPageNumber(page.getNumber());
    	        dto.setPageSize(page.getSize());
        	}
        	
        	return new ResponseEntity<>(dto, HttpStatus.OK);
        	
        } catch (ServiceException ex) {
            _LOGGER.error("Error fetching OrderQtys by multiple fields mfg, product and user: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }	
    }
    
    @GetMapping("/searchmongo")
    public ResponseEntity<PageResponseDto<OrderDocument>> searchMongo(
            @RequestParam(required = false) Long manufacturerId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 5)
    	    Pageable pageable) {
    	
    	_LOGGER.info(">>> Inside searchMongo. manufacturerId<<<:"+manufacturerId);		
    	_LOGGER.info(">>> Inside searchMongo. productId<<<:"+productId);		
    	_LOGGER.info(">>> Inside searchMongo. userId<<<:"+userId);
    	try {
    		Page<OrderDocument> page = orderDocumentService.searchMongo(manufacturerId, productId, userId, pageable);
    		
    		PageResponseDto<OrderDocument> dto = new PageResponseDto<>();
        	
        	if(page != null) {
    	    	dto.setContent(page.getContent());
    	        dto.setTotalPages(page.getTotalPages());
    	        dto.setTotalElements(page.getTotalElements());
    	        dto.setPageNumber(page.getNumber());
    	        dto.setPageSize(page.getSize());
        	}
        	
        	return new ResponseEntity<>(dto, HttpStatus.OK);
    		
    	} catch (ServiceException ex) {
            _LOGGER.error("Error fetching searchMongo by multiple fields product and user: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
