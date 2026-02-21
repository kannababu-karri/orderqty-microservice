package com.restful.orderqty.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.restful.orderqty.entity.OrderDocument;
import com.restful.orderqty.entity.OrderQty;
import com.restful.orderqty.exception.ServiceException;
import com.restful.orderqty.repository.OrderDocumentRepository;

@Service
public class OrderDocumentService {
	private static final Logger _LOGGER = LoggerFactory.getLogger(OrderDocumentService.class);
	
	@Autowired
	private OrderDocumentRepository orderDocumentRepository;
	
	public boolean saveOrderDocument(OrderQty orderQty) throws ServiceException {
		_LOGGER.info(">>> Inside saveOrderDocument.<<<");
		boolean result = false;
		try {
			//Create json object in the mongodb
			//2. Convert to Mongo document
			OrderDocument doc = writeMongoDbOrderDocument(orderQty);
			doc.setDocumentType(orderQty.getDocumentType());
			
			//3. Save to MongoDB
			orderDocumentRepository.save(doc);
			result = true;
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in saveOrderDocument."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in saveOrderDocument."+exp.toString());
		}
		
		return result;
	}
	
	public Page<OrderDocument> searchMongo(Long mfgId, Long productId, Long userId, Pageable pageable) throws ServiceException {

		try {
			if (mfgId != null && mfgId.longValue() > 0 && productId != null && productId.longValue() > 0 && userId != null && userId.longValue() > 0) {
				return orderDocumentRepository.findByManufacturerIdAndProductIdAndUserId(mfgId, productId, userId, pageable);
			}

			if (mfgId != null  && mfgId.longValue() > 0 && userId != null && userId.longValue() > 0 ) {
				return orderDocumentRepository.findByManufacturerId(mfgId, userId, pageable);
			}

			if (productId != null && productId.longValue() > 0 && userId != null && userId.longValue() > 0) {
				return orderDocumentRepository.findByProductId(productId, userId, pageable);
			}

			if (userId != null && userId.longValue() > 0) {
				return orderDocumentRepository.findByUserId(userId, pageable);
			}

			return orderDocumentRepository.findAll(pageable);

		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in search." + exp.toString());
			throw new ServiceException("ERROR: Service Exception occured in search." + exp.toString());
		}
	}
    
	/**
	 * Write the data into mondo db database as order document
	 * @param order
	 * @return
	 */
	private OrderDocument writeMongoDbOrderDocument(OrderQty orderQty) {
		_LOGGER.info(">>> Inside writeMongoDbOrderDocument.<<<");
		OrderDocument doc = new OrderDocument();
		doc.setOrderQtyId(orderQty.getOrderId());
		doc.setUserId(orderQty.getUser().getUserId());
		doc.setUserName(orderQty.getUser().getUserName());
		doc.setRole(orderQty.getUser().getRole());
		doc.setManufacturerId(orderQty.getManufacturer().getManufacturerId());
		doc.setMfgName(orderQty.getManufacturer().getMfgName());
		doc.setProductId(orderQty.getProduct().getProductId());
		doc.setProductName(orderQty.getProduct().getProductName());
		doc.setCasNumber(orderQty.getProduct().getCasNumber());
		doc.setProductDescription(orderQty.getProduct().getProductDescription());
		doc.setQuantity(orderQty.getQuantity());
		doc.setCreatedAt(LocalDateTime.now());
		_LOGGER.info(">>> Inside writeMongoDbOrderDocument doc.<<<"+doc.toString());
		return doc;
	}

}
