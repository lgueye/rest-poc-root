package org.diveintojee.poc.rest.client.springmvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.diveintojee.poc.rest.domain.Product;
import org.diveintojee.poc.rest.domain.services.ProductService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {Constants.TESTS_CONTEXT})
public class ProductResourceTest {

   @Autowired
   RestTemplate restTemplate;

   private static final String BASE_URL = ResourceBundle.getBundle(Constants.CONFIG_BUNDLE_NAME).getString(Constants.REST_POC_CLIENT_URL_KEY);

   private static final String JETTY_PORT = ResourceBundle.getBundle(Constants.CONFIG_BUNDLE_NAME).getString(Constants.JETTY_PORT_KEY);

   private static final String CLIENT_MODULE_CONTEXT = ResourceBundle.getBundle(Constants.CONFIG_BUNDLE_NAME).getString(Constants.CLIENT_MODULE_CONTEXT_KEY);

   private static final String SERVER_MODULE_CONTEXT = ResourceBundle.getBundle(Constants.CONFIG_BUNDLE_NAME).getString(Constants.SERVER_MODULE_CONTEXT_KEY);

   private static final int PRODUCTS_INITIAL_SIZE = 5;

   @Autowired
   @Qualifier("productServiceProxy")
   private ProductService productService;

   private static Server server;

   /**
    * @throws Throwable
    */
   @BeforeClass
   public static void beforeClass() throws Throwable {

      server = new Server();

      QueuedThreadPool threadPool = new QueuedThreadPool();
      threadPool.setMaxThreads(100);
      server.setThreadPool(threadPool);

      SelectChannelConnector connector = new SelectChannelConnector();
      connector.setPort(Integer.valueOf(JETTY_PORT));
      connector.setMaxIdleTime(30000);
      connector.setConfidentialPort(8443);
      server.setConnectors(new Connector[] {connector});

      WebAppContext restPocClientWebApp = new WebAppContext();
      restPocClientWebApp.setWar("../rest-poc-client-springmvc/target/rest-poc-client-springmvc.war");
      restPocClientWebApp.setContextPath("/" + CLIENT_MODULE_CONTEXT);
      server.addHandler(restPocClientWebApp);

      WebAppContext restPocServerWebApp = new WebAppContext();
      restPocServerWebApp.setWar("../rest-poc-server/target/rest-poc-server.war");
      restPocServerWebApp.setContextPath("/" + SERVER_MODULE_CONTEXT);
      server.addHandler(restPocServerWebApp);

      server.start();

      server.setStopAtShutdown(true);

      server.setSendServerVersion(true);
   }

   /**
    * @throws Throwable
    */
   @AfterClass
   public static void afterClass() throws Throwable {

      server.stop();
      server.join();

   }

   /**
    * 
    */
   @Before
   public void before() {

      assertNotNull(productService);

      setUpNewProductRepository();

   }

   /**
    * @throws Throwable
    */
   @Test
   public final void testList() throws Throwable {

      List<Product> products = listProducts();

      assertNotNull(products);

      assertEquals(PRODUCTS_INITIAL_SIZE, products.size());

   }

   /**
    * @throws Throwable
    */
   @Test
   public void testAdd() throws Throwable {

      String name = "doliprane";

      String description = "Ce médicament est un antalgique et un antipyrétique qui contient du paracétamol."
            + "\nIl est utilisé pour faire baisser la fièvre et dans le traitement des affections douloureuses.";

      Long id = addProduct(name, description);

      assertNotNull(id);

      Product product = getProduct(id);

      assertNotNull(product);

      assertEquals(name, product.getName());

      assertEquals(description, product.getDescription());
   }

   /**
    * @throws Throwable
    */
   @Test
   public void testFindByDescription() throws Throwable {

      String name = "doliprane";

      String description = "Ce médicament est un antalgique et un antipyrétique qui contient du paracétamol."
            + "\nIl est utilisé pour faire baisser la fièvre et dans le traitement des affections douloureuses.";

      addProduct(name, description);

      assertEquals(PRODUCTS_INITIAL_SIZE + 1, listProducts().size());

      String term = "description";

      assertEquals(PRODUCTS_INITIAL_SIZE, findByDescription(term).size());

   }

   /**
    * @throws Throwable
    */
   @Test
   public void testUpdate() throws Throwable {

      String uri = null;

      String name = "doliprane";

      String description = "Ce médicament est un antalgique et un antipyrétique qui contient du paracétamol."
            + "\nIl est utilisé pour faire baisser la fièvre et dans le traitement des affections douloureuses.";

      Long id = addProduct(name, description);

      assertNotNull(id);

      String newName = "prozac";

      String newDescription = "Ce médicament est un antidépresseur de la famille des inhibiteurs de la recapture de la sérotonine.";

      newDescription += "Il est utilisé chez l'adulte dans le traitement : -des états dépressifs ; \n-des troubles obsessionnels compulsifs ;";

      newDescription += "\n- de la boulimie (en complément d'une psychothérapie). ;";

      uri = "/product/update/" + id;

      Product product = new Product();

      product.setId(id);

      product.setDescription(newDescription);

      product.setName(newName);

      restTemplate.put(BASE_URL + uri, product);

      Product p = getProduct(id);

      assertNotNull(p);

      assertEquals(newName, p.getName());

      assertEquals(newDescription, p.getDescription());
   }

   /**
    * @throws Throwable
    */
   @Test
   public void testDelete() throws Throwable {

      String uri = null;

      String name = "doliprane";

      String description = "Ce médicament est un antalgique et un antipyrétique qui contient du paracétamol."
            + "\nIl est utilisé pour faire baisser la fièvre et dans le traitement des affections douloureuses.";

      Long id = addProduct(name, description);

      assertNotNull(id);

      uri = "/product/delete/" + id;

      restTemplate.delete(BASE_URL + uri);

      uri = "/product/" + id;

      Product product = null;

      // UGLY !!!!!!!!!!!! I'd rather have a non null response with a 404 code
      try {

         product = getProduct(id);

         fail("RestClientException expected");

      } catch (RestClientException e) {
      }

      assertNull(product);

   }

   @Test
   public final void xmlExtensionWillGetXmlContent() throws Throwable {

      String name = "test";

      String description = "test";

      Long id = addProduct(name, description);

      String uri = null;

      uri = "/product/" + id + ".xml";

      HttpHeaders headers = new HttpHeaders();

      List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();

      acceptableMediaTypes.add(MediaType.APPLICATION_XML);

      headers.setAccept(acceptableMediaTypes);

      HttpEntity<String> entity = new HttpEntity<String>(headers);

      ResponseEntity<Product> response = restTemplate.exchange(BASE_URL + uri, HttpMethod.GET, entity, Product.class);

      assertNotNull(response);

      assertEquals(response.getStatusCode(), HttpStatus.OK);

      assertEquals(MediaType.APPLICATION_XML, response.getHeaders().getContentType());
   }

   @Test
   public final void gsonExtensionWillGetJsonContent() throws Throwable {

      String name = "test";

      String description = "test";

      Long id = addProduct(name, description);

      String uri = null;

      uri = "/product/" + id + ".json";

      HttpHeaders headers = new HttpHeaders();

      List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();

      acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

      headers.setAccept(acceptableMediaTypes);

      HttpEntity<String> entity = new HttpEntity<String>(headers);

      ResponseEntity<Product> response = restTemplate.exchange(BASE_URL + uri, HttpMethod.GET, entity, Product.class);

      assertNotNull(response);

      assertEquals(response.getStatusCode(), HttpStatus.OK);

      assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
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

   /**
    * @param name
    * @param description
    * @return
    * @throws Throwable
    */
   private Long addProduct(String name, String description) throws Throwable {

      String uri = null;

      uri = "/product/add";

      Product product = new Product();

      product.setName(name);

      product.setDescription(description);

      Long id = restTemplate.postForObject(BASE_URL + uri, product, Long.class);

      return id;

   }

   /**
    * @param id
    * @return
    * @throws Throwable
    */
   private Product getProduct(Long id) throws Throwable {

      String uri = null;

      uri = "/product/" + id + ".xml";

      Product product = restTemplate.getForObject(BASE_URL + uri, Product.class);

      return product;

   }

   /**
    * @return
    * @throws Throwable
    */
   private List<Product> listProducts() throws Throwable {

      String uri = "/product/list";

      @SuppressWarnings("unchecked")
      List<Product> products = restTemplate.getForObject(BASE_URL + uri, List.class);

      return products;

   }

   /**
    * @param term
    * @return
    * @throws Throwable
    */
   private List<Product> findByDescription(String term) throws Throwable {

      String uri = "/product/find/by/description/term/" + term;

      @SuppressWarnings("unchecked")
      List<Product> products = restTemplate.getForObject(BASE_URL + uri, List.class);

      return products;

   }

}
