package com.daily.record.money.util;

import java.util.Map;

import org.apache.log4j.Logger;

public final class ThreadContext {
	private static final Logger logger = Logger.getLogger(ThreadContext.class);

	private static InheritableThreadLocal<Integer> result = new InheritableThreadLocal<Integer>();
	// first time when run
	private static InheritableThreadLocal<Long> boot = new InheritableThreadLocal<Long>();
	// pbq name
	private static InheritableThreadLocal<String> pbq = new InheritableThreadLocal<String>();
	// request class
	private static InheritableThreadLocal<String> request = new InheritableThreadLocal<String>();

	private static InheritableThreadLocal<Map<String, Object>> userDataMap = new InheritableThreadLocal<Map<String, Object>>();

	private static InheritableThreadLocal<Integer> timeout = new InheritableThreadLocal<Integer>();

	public static void checkThreadInterrupted() throws InterruptedException {
		if (Thread.interrupted()) {
			throw new InterruptedException("thread <" + Thread.currentThread().getId() + "> has been interrupted");
		}
	}

	public static void setThreadTimeout(Integer t1) {
		timeout.set(t1);
	}

	public static void removeThreadTimeout() {
		timeout.remove();
	}

	public static void setRequestResult(int ret) {
		result.set(ret);
	}

	public static Integer getRequestResult() {
		return result.get();
	}

	public static void setRequestContext(String pbqName, String requestClass) {
		pbq.set(pbqName);
		request.set(requestClass);
	}

	public static void setUserDataMap(Map<String, Object> value) {
		userDataMap.set(value);
	}

	public static Map<String, Object> getUserDataMap() {
		return userDataMap.get();
	}

	public static String getContextPbqName() {
		return pbq.get();
	}

	public static String getContextRequestClass() {
		return request.get();
	}

	public static void setRequestStartTime(Long time) {
		boot.set(time);
	}

	public static Long getRequestStartTime() {
		return boot.get() != null ? boot.get() : System.currentTimeMillis();
	}
}
