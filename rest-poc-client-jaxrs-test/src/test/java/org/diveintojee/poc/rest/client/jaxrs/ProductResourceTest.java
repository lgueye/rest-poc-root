package org.diveintojee.poc.rest.client.jaxrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author louis.gueye@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {Constants.TESTS_CONTEXT})
public class ProductResourceTest {

   HttpClient httpClient;

   @Autowired
   ObjectMapper mapper;

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
      restPocClientWebApp.setWar("../rest-poc-client-jaxrs/target/rest-poc-client-jaxrs.war");
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

      httpClient = new HttpClient();

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

      HttpMethod httpMethod = null;

      NameValuePair[] queryString = null;

      int statusCode;

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

      httpMethod = new PostMethod(BASE_URL + uri);

      httpMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

      queryString = new NameValuePair[] {new NameValuePair("name", newName), new NameValuePair("description", newDescription)};

      httpMethod.setQueryString(queryString);

      statusCode = httpClient.executeMethod(httpMethod);

      assertEquals(HttpStatus.SC_OK, statusCode);

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

      HttpMethod httpMethod = null;

      int statusCode;

      String name = "doliprane";

      String description = "Ce médicament est un antalgique et un antipyrétique qui contient du paracétamol."
            + "\nIl est utilisé pour faire baisser la fièvre et dans le traitement des affections douloureuses.";

      Long id = addProduct(name, description);

      assertNotNull(id);

      uri = "/product/delete/" + id;

      httpMethod = new GetMethod(BASE_URL + uri);

      statusCode = httpClient.executeMethod(httpMethod);

      assertEquals(HttpStatus.SC_OK, statusCode);

      uri = "/product/" + id;

      httpMethod = new GetMethod(BASE_URL + uri);

      statusCode = httpClient.executeMethod(httpMethod);

      assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);

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

      HttpMethod httpMethod = null;

      NameValuePair[] queryString = null;

      int statusCode;

      byte[] responseBody = null;

      uri = "/product/add";

      httpMethod = new PostMethod(BASE_URL + uri);

      httpMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

      queryString = new NameValuePair[] {new NameValuePair("name", name), new NameValuePair("description", description)};

      httpMethod.setQueryString(queryString);

      statusCode = httpClient.executeMethod(httpMethod);

      responseBody = httpMethod.getResponseBody();

      assertEquals(HttpStatus.SC_OK, statusCode);

      Long id = mapper.readValue(new ByteArrayInputStream(responseBody), Long.class);

      return id;

   }

   /**
    * @param id
    * @return
    * @throws Throwable
    */
   private Product getProduct(Long id) throws Throwable {

      String uri = null;

      HttpMethod httpMethod = null;

      int statusCode;

      byte[] responseBody = null;

      uri = "/product/" + id;

      httpMethod = new GetMethod(BASE_URL + uri);

      statusCode = httpClient.executeMethod(httpMethod);

      responseBody = httpMethod.getResponseBody();

      assertEquals(HttpStatus.SC_OK, statusCode);

      Product product = mapper.readValue(new ByteArrayInputStream(responseBody), Product.class);

      return product;

   }

   /**
    * @return
    * @throws Throwable
    */
   private List<Product> listProducts() throws Throwable {

      String uri = "/product/list";

      HttpMethod method = new GetMethod(BASE_URL + uri);

      int statusCode = httpClient.executeMethod(method);

      assertEquals(HttpStatus.SC_OK, statusCode);

      byte[] responseBody = method.getResponseBody();

      List<Product> products = mapper.readValue(new ByteArrayInputStream(responseBody), TypeFactory.collectionType(ArrayList.class, Product.class));

      return products;
   }

   /**
    * @param term
    * @return
    * @throws Throwable
    */
   private List<Product> findByDescription(String term) throws Throwable {

      String uri = "/product/find/by/description/term/" + term;

      HttpMethod method = new GetMethod(BASE_URL + uri);

      int statusCode = httpClient.executeMethod(method);

      assertEquals(HttpStatus.SC_OK, statusCode);

      byte[] responseBody = method.getResponseBody();

      List<Product> products = mapper.readValue(new ByteArrayInputStream(responseBody), TypeFactory.collectionType(ArrayList.class, Product.class));

      return products;
   }

}
