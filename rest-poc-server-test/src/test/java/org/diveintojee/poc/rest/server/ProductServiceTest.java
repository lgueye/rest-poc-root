package org.diveintojee.poc.rest.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.diveintojee.poc.rest.domain.Product;
import org.diveintojee.poc.rest.domain.services.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {Constants.TESTS_CONTEXT})
public class ProductServiceTest {

   private static final int PRODUCTS_INITIAL_SIZE = 5;
   @Autowired
   @Qualifier("productServiceProxy")
   private ProductService productService;

   /**
    * 
    */
   @Before
   public void before() {
      assertNotNull(productService);
      setUpNewProductRepository();
   }

   /**
    * 
    */
   @Test
   public void testList() {
      List<Product> products = productService.list();
      assertNotNull(products);
      assertEquals(PRODUCTS_INITIAL_SIZE, products.size());
   }

   /**
    * 
    */
   @Test
   public void testFindByDescription() {

      Product p = new Product();

      String name = "doliprane";

      String description = "Ce médicament est un antalgique et un antipyrétique qui contient du paracétamol."
            + "\nIl est utilisé pour faire baisser la fièvre et dans le traitement des affections douloureuses.";

      p.setName(name);

      p.setDescription(description);

      productService.add(p);

      assertEquals(PRODUCTS_INITIAL_SIZE + 1, productService.list().size());

      String term = "description";

      assertEquals(PRODUCTS_INITIAL_SIZE, productService.findByDescription(term).size());

   }

   /**
    * 
    */
   @Test
   public void testAdd() {

      Product p = new Product();

      String name = "doliprane";

      String description = "Ce médicament est un antalgique et un antipyrétique qui contient du paracétamol."
            + "\nIl est utilisé pour faire baisser la fièvre et dans le traitement des affections douloureuses.";

      p.setName(name);

      p.setDescription(description);

      Long productId = productService.add(p);

      assertNotNull(productService.get(productId));

   }

   /**
    * 
    */
   @Test
   public void testUpdate() {

      Product p = new Product();

      String name = "doliprane";

      String description = "Ce médicament est un antalgique et un antipyrétique qui contient du paracétamol."
            + "\nIl est utilisé pour faire baisser la fièvre et dans le traitement des affections douloureuses.";

      p.setName(name);

      p.setDescription(description);

      Long productId = productService.add(p);

      p = productService.get(productId);

      assertNotNull(p);

      String newName = "prozac";

      p.setName(newName);

      String newDescription = "Ce médicament est un antidépresseur de la famille des inhibiteurs de la recapture de la sérotonine.";

      newDescription += "Il est utilisé chez l'adulte dans le traitement : -des états dépressifs ; \n-des troubles obsessionnels compulsifs ;";

      newDescription += "\n- de la boulimie (en complément d'une psychothérapie). ;";

      p.setDescription(newDescription);

      productService.update(p);

      p = productService.get(productId);

      assertEquals(newDescription, p.getDescription());

      assertEquals(newName, p.getName());

   }

   /**
    * 
    */
   @Test
   public void testGet() {

      Product p = new Product();

      String name = "doliprane";

      String description = "Ce médicament est un antalgique et un antipyrétique qui contient du paracétamol."
            + "\nIl est utilisé pour faire baisser la fièvre et dans le traitement des affections douloureuses.";

      p.setName(name);

      p.setDescription(description);

      Long productId = productService.add(p);

      p = productService.get(productId);

      assertNotNull(p);

   }

   /**
    * 
    */
   @Test
   public void testDelete() {

      Product p = new Product();

      String name = "doliprane";

      String description = "Ce médicament est un antalgique et un antipyrétique qui contient du paracétamol."
            + "\nIl est utilisé pour faire baisser la fièvre et dans le traitement des affections douloureuses.";

      p.setName(name);

      p.setDescription(description);

      Long productId = productService.add(p);

      assertNotNull(productService.get(productId));

      productService.delete(productId);

      assertNull(productService.get(productId));

   }

   /**
    * 
    */
   private void setUpNewProductRepository() {
      productService.clear();

      for (int i = 0; i < PRODUCTS_INITIAL_SIZE; i++) {
         Product p = new Product();
         String name = "name" + i;
         String description = "description" + i;
         p.setName(name);
         p.setDescription(description);
         productService.add(p);

      }

   }
}
