package com.daily.record.money.common;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;

import com.daily.record.money.interfaces.TableChangedListener;
import com.google.common.util.concurrent.AbstractService;

public class CacheManagerService extends AbstractService implements TableChangedListener{

	
	private static final Logger logger = Logger.getLogger(CacheManagerService.class);
	static CacheManager manager;
	private CacheManagerService() {
	}

	public static CacheManagerService getInstance() {

		return InnerHolder.INSTANCE;
	}
	
	private static class InnerHolder {
		static final CacheManagerService INSTANCE = new CacheManagerService();
	}
	
	ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> cacheMaps = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
	
	
	public boolean removeOnTableChanged(String key, int[] didset) {

		if (didset != null && didset.length > 0) {
			String[] didStringArray = new String[didset.length];
			for (int i = 0; i < didset.length; i++) {
				didStringArray[i] = String.valueOf(didset[i]);
			}
			return removeOnTableChanged(key, didStringArray);
		}
		return false;
	}
	
	public boolean removeOnTableChanged(String key, String[] didset) {
		if (didset != null && didset.length > 0) {
			for (String did : didset) {
				ConcurrentHashMap<String, Boolean> keys = null;
				if (cacheMaps.containsKey(did)) {
					keys = cacheMaps.get(did);
				} else {
					keys = new ConcurrentHashMap<String, Boolean>();
				}
				keys.putIfAbsent(key, Boolean.TRUE);
				cacheMaps.putIfAbsent(did, keys);
			}
			return true;
		}
		return false;
	}

	@Override
	public void onTableChanged(String tableName) {
		try {
			Cache cache = getDefaultCache();
			ConcurrentHashMap<String, Boolean> keys = cacheMaps.get(tableName);
			if (keys != null) {
				Iterator<Entry<String, Boolean>> iter = keys.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, Boolean> entry = iter.next();
					String key = entry.getKey();
					if (!cache.remove(key))
						iter.remove();
					logger.info("table:" + tableName + " changed remove cache md5Key:" + key);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static Cache getDefaultCache(){
		Cache cache = null;
		if(manager == null){
			manager = CacheManager.create();
			logger.info("manager init success!.........");
		}
//		CacheManager manager = CacheManager.create();
		cache = manager.getCache("cache1");
		logger.debug("cache get success!.........");
		return cache;
	}

	@Override
	protected void doStart() {
		logger.info("doStart-------------doStart-----");
		TableChangedMonitor.getInstance().addListener(this);
	}

	@Override
	protected void doStop() {
		logger.info("doStop-----------doStop-------");
		TableChangedMonitor.getInstance().removeListener(this);
	}
}
