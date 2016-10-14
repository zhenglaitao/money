package com.daily.record.money.util;

import java.io.*; 
import java.util.*; 
import java.sql.*; 
/** *//** 
* 

Title: Database Manager 

* 

Description: Connect Database 

* 

Copyright: Copyright  2002 YaoGuodong 

* 

Company: 

* @author unascribed YaoGuodong 姚国栋 
* @version 1.0 
* @email net_spirit@163.com 
* @QQ 16898283 
* @转载的时候请保留版权信息 
*/ 
public class DBManager { 
	/** *//**getConnection() 得到连接数据库的属性文件内容，并返回连接结果集 
	  @throws SQLException 
	  @throws IOException 
	  @throws Exception 
	  声明drivers，得到数据库的驱动程序 
	  声明url，得到连接远程数据库的URL 
	  声明username，得到连接远程数据库的用户名 
	  声明password，得到连接远程数据库的密码 
	  @return DriverManger.getConnection(url,username,passowrd); 
	  */ 
	public Connection getConnection() throws SQLException,IOException,Exception { 
	   Properties props = new Properties(); 
	   //String fileName = "e:\Database.Property"; 
	   //FileInputStream in = new FileInputStream(fileName); 
	   InputStream in = getClass().getResourceAsStream("/Database.Property") ; 
	   props.load(in); 
	   String drivers = props.getProperty("jdbc.drivers"); 
	   if (drivers!=null) 
	       { System.setProperty("jdbc.drives",drivers); } 
	   String url = props.getProperty("jdbc.url"); 
	   String username = props.getProperty("jdbc.username"); 
	   String password = props.getProperty("jdbc.password"); 
	   Class.forName(drivers); 
	   return DriverManager.getConnection(url,username,password); 
	} 
	/** *//**DBManager() 
	  * 连接数据库 
	  */ 
	public DBManager() { 
	   try{ 
	     conn = getConnection(); 
	     stmt=conn.createStatement(); 
	     System.out.println("Connect Database is Ok!"); 
	   } 
	   catch(Exception e){ 
	     System.out.println("Connect Database Failed!:"+e); 
	     } 
	} 
	/** *//** executeQuery()方法,查询SQL结果 
	  @param sqlwords 为传入的查询的sql语句 
	  @return rs　为返回的查询结果集 
	  */ 
	public ResultSet executeQuery(String sqlwords) { 
	   this.sqlwords=sqlwords; 
	   try{ 
	     rs=stmt.executeQuery(sqlwords); 
	   } 
	   catch(SQLException ex){ 
	     System.out.println("Execute Query Sql Failed!:" + ex.getMessage()); 
	   } 
	   return rs; 
	} 
	/** *//** executeUpdate()方法，修改数据库记录 
	  @param sqlwords 为传入的查询的sql语 
	  @return true|false 
	  */ 
	public boolean executeUpdate(String sqlwords) { 
	   this.sqlwords=sqlwords; 
	   try { 
	     stmt.executeUpdate(sqlwords); 
	     return true; 
	   } 
	   catch(SQLException ex) { 
	     System.err.println("Execute Update Sql Failed!: " + ex.getMessage()); 
	     return false; 
	   } 
	} 
	/** *//** executeInsert()方法，插入新的数据库记录 
	  @param sqlwords 为传入的插入的sql语 
	  @return true|false 
	  */ 
	  public boolean executeInsert(String sqlwords) { 
	   this.sqlwords=sqlwords; 
	   try { 
	     stmt.executeUpdate(sqlwords); 
	     return true; 
	   } 
	   catch(SQLException ex) { 
	     System.err.println("Execute Insert Sql Failed!: " + ex.getMessage()); 
	     return false; 
	   } 
	} 
	/** *//** executeDelete()方法，删除数据库记录 
	  @param sqlwords 为传入的删除的sql语 
	  @return true|false 
	  */ 
	  public boolean executeDelete(String sqlwords) { 
	   this.sqlwords=sqlwords; 
	   try { 
	     stmt.executeUpdate(sqlwords); 
	     return true; 
	   } 
	   catch(SQLException ex) { 
	     System.err.println("Execute Delete Sql Failed!: " + ex.getMessage()); 
	     return false; 
	   } 
	} 
	/** *//** close()方法，断开数据库的连接 
	  @return true|false 
	  */ 
	public boolean close() 
	{ 
	   try { 
	     if(rs != null){ rs.close(); } 
	     if(stmt != null){ stmt.close(); } 
	     if(conn != null){ conn.close(); } 
	     return true; 
	   } 
	   catch(Exception e) { 
	     System.out.print("Clost Database Connect Failed!:"+e); 
	     return false; 
	   } 
	} 
	/** *//** 
	  * 声明conn 
	  * 声明rs结果集 
	  * 声明stmt语气 
	  * 声明sqlwords关键字 
	  */ 
	Connection conn = null; 
	ResultSet rs = null; 
	Statement stmt = null; 
	private String sqlwords = null;
}