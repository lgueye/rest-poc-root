/**
 * 
 */
package org.diveintojee.poc.rest.domain;

/**
 * @author louis.gueye@gmail.com
 */
public class Product extends AbstractPersistableEntity {

   private String name;

   private String description;

   /**
    * @return
    */
   public String getName() {
      return name;
   }

   /**
    * @param name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return
    */
   public String getDescription() {
      return description;
   }

   /**
    * @param description
    */
   public void setDescription(String description) {
      this.description = description;
   }

}
