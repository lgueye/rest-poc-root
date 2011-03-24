/**
 * 
 */
package org.diveintojee.poc.rest.client.springmvc;

import java.util.List;

import org.diveintojee.poc.rest.domain.Product;
import org.diveintojee.poc.rest.domain.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author louis.gueye@gmail.com
 */
@Controller
@RequestMapping("/product")
public class ProductResource {

   @Autowired
   ProductService productService;

   /**
    * @return
    */
   @RequestMapping(value = "/list", method = RequestMethod.GET, headers = "Accept=text/xml, text/html, application/json, application/xml")
   @ResponseStatus(HttpStatus.OK)
   public @ResponseBody
   List<Product> list() {
      return productService.list();
   }

   /**
    * @param product
    * @return
    */
   @RequestMapping(value = "/add", method = RequestMethod.POST)
   @ResponseStatus(HttpStatus.CREATED)
   public @ResponseBody
   Long add(@RequestBody Product product) {
      return productService.add(product);
   }

   /**
    * @param product
    */
   @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public @ResponseBody
   void update(@RequestBody Product product) {
      productService.update(product);
   }

   /**
    * @param id
    * @return
    */
   @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=text/*, application/json, application/xml")
   @ResponseStatus(HttpStatus.OK)
   public @ResponseBody
   Product get(@PathVariable Long id) {
      return productService.get(id);
   }

   /**
    * @param id
    */
   @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public @ResponseBody
   void delete(@PathVariable("id") Long id) {
      productService.delete(id);
   }

   /**
    * @param term
    * @return
    * @throws Throwable
    */
   @RequestMapping(value = "/find/by/description/term/{term}", headers = "Accept=text/xml, text/html, application/json, application/xml")
   @ResponseStatus(HttpStatus.OK)
   public @ResponseBody
   List<Product> findByDescription(@PathVariable("term") String term) throws Throwable {
      return productService.findByDescription(term);
   }

}
