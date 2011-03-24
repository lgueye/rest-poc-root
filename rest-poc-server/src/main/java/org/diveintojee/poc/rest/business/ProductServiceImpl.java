/**
 * 
 */
package org.diveintojee.poc.rest.business;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.diveintojee.poc.rest.domain.Product;
import org.diveintojee.poc.rest.domain.services.ProductService;
import org.diveintojee.poc.rest.persistence.PersistenceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author louis.gueye@gmail.com
 */
@Component(ProductService.BEAN_ID)
@WebService(endpointInterface = ProductService.WEBSERVICE_ENDPOINT_INTERFACE)
public class ProductServiceImpl implements ProductService {

   @Autowired
   private PersistenceManager persistenceManager;

   /**
    * @see org.diveintojee.poc.rest.domain.services.ProductService#list()
    */
   @Override
   public List<Product> list() {

      return persistenceManager.findAll(Product.class);

   }

   /**
    * @see org.diveintojee.poc.rest.domain.services.ProductService#findByDescription(java.lang.String)
    */
   @Override
   public List<Product> findByDescription(String term) {

      List<Product> results = new ArrayList<Product>();

      if (StringUtils.isEmpty(term))
         return results;

      List<Product> products = list();

      for (Product product : products) {

         if (product.getDescription().contains(term))
            results.add(product);

      }

      return results;

   }

   /**
    * @see org.diveintojee.poc.rest.domain.services.ProductService#add(org.diveintojee.poc.rest.domain.Product)
    */
   @Override
   public Long add(Product product) {

      if (product == null)
         return null;

      return persistenceManager.persist(product);

   }

   /**
    * @see org.diveintojee.poc.rest.domain.services.ProductService#update(org.diveintojee.poc.rest.domain.Product)
    */
   @Override
   public void update(Product product) {

      if (product == null)
         return;

      if (product.getId() == null)
         throw new IllegalArgumentException();

      persistenceManager.persist(product);

   }

   /**
    * @see org.diveintojee.poc.rest.domain.services.ProductService#get(java.lang.Long)
    */
   @Override
   public Product get(Long id) {

      return persistenceManager.get(Product.class, id);

   }

   /**
    * @see org.diveintojee.poc.rest.domain.services.ProductService#delete(java.lang.Long)
    */
   @Override
   public void delete(Long id) {

      persistenceManager.delete(Product.class, id);

   }

   /**
    * @see org.diveintojee.poc.rest.domain.services.ProductService#clear()
    */
   @Override
   public void clear() {

      persistenceManager.clear(Product.class);

   }

}
