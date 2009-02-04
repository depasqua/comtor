import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;

public class Test
{
  public static void main(String [] args)
  {
    // Check for MySQL Connector/J
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
Connection con = DriverManager.getConnection("jdbc:mysql://depasquale-1.tcnj.edu/comtor_dev", "comtor", "220cs230");
    }
    catch (Exception e)
    {
System.out.println(e);
    }
  }
}

