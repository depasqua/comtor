import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;
//import org.antlr.runtime.*;

public class SystemCheck
{
  public static void main(String [] args)
  {
    // Check for MySQL Connector/J
    System.out.print("MySQL: ");
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
      System.out.println("Succeed");
    }
    catch (Exception e)
    {
      System.out.println("Failed");
    }
  }
}

