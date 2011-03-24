/**
 * 
 */
package org.diveintojee.poc.rest.client.jaxrs;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.diveintojee.poc.rest.domain.Product;
import org.diveintojee.poc.rest.domain.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author louis.gueye@gmail.com
 */
@Component
@Path("/product")
public class ProductResource {

   @Autowired
   ProductService productService;

   @Autowired
   ObjectMapper objectMapper;

   /**
    * @return
    * @throws JsonGenerationException
    * @throws JsonMappingException
    * @throws IOException
    */
   @GET
   @Path("list")
   public Response list() throws JsonGenerationException, JsonMappingException, IOException {
      byte[] response = objectMapper.writeValueAsBytes(productService.list());
      return Response.ok(response, MediaType.APPLICATION_JSON).build();
   }

   /**
    * @param name
    * @param description
    * @return
    * @throws Throwable
    */
   @POST
   @Path("add")
   @Consumes("application/x-www-form-urlencoded")
   public Response add(@FormParam("name") String name, @FormParam("description") String description) throws Throwable {
      Product product = new Product();
      product.setName(name);
      product.setDescription(description);
      byte[] response = objectMapper.writeValueAsBytes(productService.add(product));
      return Response.ok(response, MediaType.APPLICATION_JSON).build();
   }

   /**
    * @param id
    * @param name
    * @param description
    * @return
    * @throws Throwable
    */
   @POST
   @Path("update/{id}")
   @Consumes("application/x-www-form-urlencoded")
   public Response update(@PathParam("id") Long id, @FormParam("name") String name, @FormParam("description") String description) throws Throwable {
      Product product = new Product();
      product.setId(id);
      product.setName(name);
      product.setDescription(description);
      productService.update(product);
      return Response.ok().build();
   }

   /**
    * @param id
    * @return
    * @throws Throwable
    */
   @GET
   @Path("{id}")
   public Response get(@PathParam("id") Long id) throws Throwable {
      Product product = productService.get(id);
      if (product == null)
         return Response.status(Status.NOT_FOUND).build();
      else
         return Response.ok(objectMapper.writeValueAsBytes(product), MediaType.APPLICATION_JSON).build();
   }

   /**
    * @param id
    * @return
    * @throws Throwable
    */
   @GET
   @Path("delete/{id}")
   public Response delete(@PathParam("id") Long id) throws Throwable {
      productService.delete(id);
      return Response.ok().build();
   }

   /**
    * @param term
    * @return
    * @throws Throwable
    */
   @GET
   @Path("find/by/description/term/{term}")
   public Response delete(@PathParam("term") String term) throws Throwable {
      List<Product> products = productService.findByDescription(term);
      if (CollectionUtils.isEmpty(products))
         return Response.status(Status.NOT_FOUND).build();
      else
         return Response.ok(objectMapper.writeValueAsBytes(products), MediaType.APPLICATION_JSON).build();
   }

}
