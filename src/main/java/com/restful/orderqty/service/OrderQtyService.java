package com.restful.orderqty.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restful.orderqty.entity.Manufacturer;
import com.restful.orderqty.entity.OrderQty;
import com.restful.orderqty.entity.Product;
import com.restful.orderqty.entity.User;
import com.restful.orderqty.exception.ServiceException;
import com.restful.orderqty.repository.OrderQtyRepository;

@Service
public class OrderQtyService {
	private static final Logger _LOGGER = LoggerFactory.getLogger(OrderQtyService.class);
	
	//MongoDB Operations
	public static final String MONGODB_OPERATION_SAVE = "SAVE";
	public static final String MONGODB_OPERATION_UPDATE = "UPDATE";
	public static final String MONGODB_OPERATION_DELETE = "DELETE";
	
	@Autowired
	private OrderQtyRepository orderQtyRepository;
	
	@Autowired
	private OrderDocumentService orderDocumentService;

    @Transactional
	public OrderQty saveOrUpdate(OrderQty orderQty) throws ServiceException {
       	OrderQty saveOrderQty = null;
    	try {
			// 1️. Save to MySQL
    		OrderQty orderResult = null;
    		Optional<OrderQty> optionalOrderResult = orderQtyRepository.findByManufacturerAndProductAndUser(orderQty.getManufacturer(), orderQty.getProduct(), orderQty.getUser(), orderQty.getQuantity());
			if (optionalOrderResult.isEmpty() && !optionalOrderResult.isPresent()) {
				orderResult = new OrderQty();
				orderResult.setManufacturer(orderQty.getManufacturer());
				orderResult.setProduct(orderQty.getProduct());
				orderResult.setUser(orderQty.getUser());
				orderResult.setDocumentType(MONGODB_OPERATION_SAVE);
			} else {
				orderResult = optionalOrderResult.get();
				orderResult.setDocumentType(MONGODB_OPERATION_UPDATE);
			}
			orderResult.setQuantity(orderQty.getQuantity());
				
			//Save into mongodb intrasactions
			saveOrderQty = orderQtyRepository.save(orderResult);
			
			//saveOrderQty = orderResult;
			if(saveOrderQty != null && saveOrderQty.getOrderId() > 0) {
				//Send the value to kafka producer
				
				orderDocumentService.saveOrderDocument(orderResult);
			}
			
    	} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in saveOrUpdate."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in saveOrUpdate."+exp.toString());
		}
		return saveOrderQty;
	}
	
    @Transactional
	public OrderQty updateOrderQty(OrderQty orderQty) throws ServiceException {
    	OrderQty updateOrderQty = null;
    	try {	
	    	updateOrderQty = orderQtyRepository.save(orderQty);
	    	//Save into mongodb intrasactions
	    	updateOrderQty.setDocumentType(MONGODB_OPERATION_UPDATE);
			orderDocumentService.saveOrderDocument(updateOrderQty);
	    	
    	} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in updateOrderQty."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in updateOrderQty."+exp.toString());
		}
		return updateOrderQty;
	}
    
    @Transactional
	public void deleteByOrderId(Long orderId) throws ServiceException {
    	
		try {
			//Get the orderQty details before delete from database.
			Optional<OrderQty> resultOrderQty = findByOrderId(orderId);
			//Delete record from MySql database
			orderQtyRepository.deleteByOrderId(resultOrderQty.get().getOrderId());
			//Save into mongodb intrasactions
			resultOrderQty.get().setDocumentType(MONGODB_OPERATION_DELETE);
			orderDocumentService.saveOrderDocument(resultOrderQty.get());
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in deleteByOrderId."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in deleteByOrderId."+exp.toString());
		}
	}

	public List<OrderQty> findAllOrderQtys() throws ServiceException {
		try {
			return orderQtyRepository.findAll();
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findAllOrderQtys."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findAllOrderQtys."+exp.toString());
		}
	}
	
	public Optional<OrderQty> findByManufacturerAndProductAndUser(Manufacturer manufacturer, Product product, User user, Long qty) throws ServiceException {
		try {
			return orderQtyRepository.findByManufacturerAndProductAndUser(manufacturer, product, user, qty);
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByManufacturerAndProductAndUser."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findByManufacturerAndProductAndUser."+exp.toString());
		}
	}
	
	// Find by userid
	public List<OrderQty> findByUser_UserId(Long userId) throws ServiceException {
		try {
			return orderQtyRepository.findByUser_UserId(userId);
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByUser_UserId."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findByUser_UserId."+exp.toString());
		}
	}

    // Find by mfg id
	public List<OrderQty> findByManufacturer_ManufacturerIdAndUser_UserId(Long manufacturerId, Long userId) throws ServiceException {
		try {
			return orderQtyRepository.findByManufacturer_ManufacturerIdAndUser_UserId(manufacturerId, userId);
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByManufacturer_ManufacturerIdAndUser_UserId."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findByManufacturer_ManufacturerIdAndUser_UserId."+exp.toString());
		}
	}
    
    // Find by product id
	public List<OrderQty> findByProduct_ProductIdAndUser_UserId(Long productId, Long userId) throws ServiceException {
		try {
			return orderQtyRepository.findByProduct_ProductIdAndUser_UserId(productId, userId);
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByProduct_ProductIdAndUser_UserId."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findByProduct_ProductIdAndUser_UserId."+exp.toString());
		}
	}

    // Find by user, mfg and product id
	public List<OrderQty> findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId(Long manufacturerId, Long productId, Long userId) throws ServiceException {
		try {
			return orderQtyRepository.findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId(manufacturerId, productId, userId);
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId."+exp.toString());
		}
	}
	
	public Optional<OrderQty> findByOrderId(Long orderId) throws ServiceException {
        try {
			return orderQtyRepository
	                .findByOrderId(orderId);
        } catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByOrderId."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findByOrderId."+exp.toString());
		}
    }
}
