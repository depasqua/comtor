import java.io.*;

public class CheckForTagsExample
{
  public static void main(String[] args)
  {}

//////////RETURN TAGS//////////
  /**
   * Correct Example
   */
  public void setName()
  {}
  
  /**
   * Correct Example
   * @return phone number
   */
  public int getPhone()
  {}
  
  /**
   * ERROR: The tag is missing
   */
  public String getName()
  {}
  
  /**
   * ERROR: The tag has no comment
   * @return 
   */
  //public int getAge()
  //{}
  
  /**
   * ERROR: The type is missing
   * @return nothing
   */
  public void setPhone()
  {}
  
  /**
   * ERROR: Too many return tags
   * @return this
   * @return that
   */
  public int checkNum()
  {}
  
//////////PARAM TAGS//////////
  /**
   * Correct Example
   * @param firstName
   * @param lastName
   */
  public void attach(String firstName, String lastName)
  {}
  
  /**
   * ERROR: Missing param tag
   * @param firstName
   */
  public void detach(String firstName, String lastName)
  {}
  
  /**
   * ERROR: Missing parameter
   * @param name
   * @param firstName
   * @param lastName
   */
  public void call(String firstName, String lastName)
  {}
  
//////////THROW TAGS////////// 
  /**
   * Correct Example
   * @throws IOException
   */
  public void clean() throws IOException
  {}
  
  /**
   * ERROR: Missing exception
   * @throws IOException
   */
  public void add()
  {}
  
  /**
   * ERROR: Missing throws tag
   */
  public void subtract() throws IOException
  {}
}