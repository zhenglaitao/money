package com.daily.record.money.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.daily.record.money.common.CacheManagerService;
import com.daily.record.money.common.TableChangedMonitor;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public abstract class AbstractRequest {

	static{
		CacheManagerService.getInstance().start();
	}
	private static final Logger logger = Logger.getLogger(AbstractRequest.class);
//	static CacheManager manager;
	public Connection getMysqlConn(){
		try {
			return DBUtil.getConnection("mysql", "127.0.0.1/exam", "root", "rootzlt");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		return null;
	}
	
	public String convertNull(String str){
		return str == null ? "" : str;
	}
	
//	public Cache getDefaultCache(){
//		Cache cache = null;
//		if(manager == null){
//			manager = CacheManager.create();
//			logger.info("manager init success!.........");
//		}
////		CacheManager manager = CacheManager.create();
//		cache = manager.getCache("cache1");
//		logger.debug("cache get success!.........");
//		return cache;
//	}
	
	public boolean removeOnTableChanged(String key, String[] didset) {
		return com.daily.record.money.common.CacheManagerService.getInstance().removeOnTableChanged(key, didset);
	}
	
	public void notifyTableChanged(String... tableNames) {
		 for (String table : tableNames) {
			 logger.info("remove changed key table:" + table);
			 TableChangedMonitor.getInstance().notifyTableChanged(table);
		 }
	}
}
