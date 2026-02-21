package com.restful.orderqty.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restful.orderqty.entity.Manufacturer;
import com.restful.orderqty.entity.OrderQty;
import com.restful.orderqty.entity.Product;
import com.restful.orderqty.entity.User;
import com.restful.orderqty.exception.InvalidOrderQtyException;
import com.restful.orderqty.exception.ServiceException;
import com.restful.orderqty.repository.OrderQtyRepository;

@Service
public class OrderQtyService {
	private static final Logger _LOGGER = LoggerFactory.getLogger(OrderQtyService.class);

	// MongoDB Operations
	public static final String MONGODB_OPERATION_SAVE = "SAVE";
	public static final String MONGODB_OPERATION_UPDATE = "UPDATE";
	public static final String MONGODB_OPERATION_DELETE = "DELETE";

	@Autowired
	private OrderQtyRepository orderQtyRepository;

	@Autowired
	private OrderDocumentService orderDocumentService;

	@Transactional
	public OrderQty saveOrUpdate(OrderQty orderQty) throws ServiceException {	
		if (orderQtyRepository.findByManufacturerAndProduct(orderQty.getManufacturer(), orderQty.getProduct()).isPresent()) {
	        throw new InvalidOrderQtyException("Order with manufacturer and product already exists!!!");
	    }
		
    	try {
    		OrderQty orderResult = new OrderQty();
			orderResult.setManufacturer(orderQty.getManufacturer());
			orderResult.setProduct(orderQty.getProduct());
			orderResult.setUser(orderQty.getUser());
			orderResult.setDocumentType(MONGODB_OPERATION_SAVE);
			orderResult.setQuantity(orderQty.getQuantity());
			
			return orderQtyRepository.save(orderResult);
			
	    } catch (DataIntegrityViolationException e) {
	    	throw new InvalidOrderQtyException("Order with manufacturer and product already exists!!!");
	    } catch (Exception e) {
	        _LOGGER.error("saveOrUpdate failed", e);
	        throw new ServiceException("Server error while saving order");
	    }
	}

	@Transactional
	public OrderQty updateOrderQty(OrderQty orderQty) throws ServiceException {
		OrderQty updateOrderQty = null;
		try {
			_LOGGER.info(">>> Inside updateOrderQty. <<<");
			_LOGGER.info("orderQty.getUser().getUserId():"+orderQty.getUser().getUserId());
			updateOrderQty = orderQtyRepository.save(orderQty);
			// Save into mongodb intrasactions
			updateOrderQty.setDocumentType(MONGODB_OPERATION_UPDATE);
			_LOGGER.info("updateOrderQty.getUser().getUserId():"+updateOrderQty.getUser().getUserId());
			orderDocumentService.saveOrderDocument(updateOrderQty);

		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in updateOrderQty." + exp.toString());
			throw new ServiceException("ERROR: Service Exception occured in updateOrderQty." + exp.toString());
		}
		return updateOrderQty;
	}

	@Transactional
	public void deleteByOrderId(Long orderId) throws ServiceException {

		try {
			// Get the orderQty details before delete from database.
			Optional<OrderQty> resultOrderQty = findByOrderId(orderId);
			// Delete record from MySql database
			orderQtyRepository.deleteByOrderId(resultOrderQty.get().getOrderId());
			// Save into mongodb intrasactions
			resultOrderQty.get().setDocumentType(MONGODB_OPERATION_DELETE);
			orderDocumentService.saveOrderDocument(resultOrderQty.get());
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in deleteByOrderId." + exp.toString());
			throw new ServiceException("ERROR: Service Exception occured in deleteByOrderId." + exp.toString());
		}
	}

	public Optional<OrderQty> findByOrderId(Long orderId) throws ServiceException {
		try {
			return orderQtyRepository.findByOrderId(orderId);
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByOrderId." + exp.toString());
			throw new ServiceException("ERROR: Service Exception occured in findByOrderId." + exp.toString());
		}
	}
	
	public Optional<OrderQty> findByManufacturerAndProductAndUser(Manufacturer manufacturer, Product product,
			User user, Long qty) throws ServiceException {
		try {
			return orderQtyRepository.findByManufacturerAndProductAndUser(manufacturer, product, user, qty);
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByManufacturerAndProductAndUser." + exp.toString());
			throw new ServiceException(
					"ERROR: Service Exception occured in findByManufacturerAndProductAndUser." + exp.toString());
		}
	}

	public Page<OrderQty> search(Long mfgId, Long productId, Long userId, Pageable pageable) throws ServiceException {

		try {
			if (mfgId != null && mfgId.longValue() > 0 && productId != null && productId.longValue() > 0 && userId != null && userId.longValue() > 0) {
				return orderQtyRepository.findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId(mfgId, productId, userId, pageable);
			}

			if (mfgId != null  && mfgId.longValue() > 0 && userId != null && userId.longValue() > 0 ) {
				return orderQtyRepository.findByManufacturer_ManufacturerIdAndUser_UserId(mfgId, userId, pageable);
			}

			if (productId != null && productId.longValue() > 0 && userId != null && userId.longValue() > 0) {
				return orderQtyRepository.findByProduct_ProductIdAndUser_UserId(productId, userId, pageable);
			}

			if (userId != null && userId.longValue() > 0) {
				return orderQtyRepository.findByUser_UserId(userId, pageable);
			}

			return orderQtyRepository.findAll(pageable);

		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in search." + exp.toString());
			throw new ServiceException("ERROR: Service Exception occured in search." + exp.toString());
		}
	}
	










}
