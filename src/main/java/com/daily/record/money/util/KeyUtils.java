package com.daily.record.money.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

public class KeyUtils {

	private static Logger logger = Logger.getLogger(KeyUtils.class);

	public static String GetMd5Key(String instr) {
		String temp = instr.toLowerCase();

		return DigestUtils.md5Hex(temp);
	}

	/**
	 * 多IP处理，可以得到最终ip
	 * 
	 * @return
	 */
	public static List<String> getIp() {
		List<String> localhosts = null;
		try {
			// System.setProperty("java.net.preferIPv4Stack", "true");
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			localhosts = new ArrayList<String>();
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				if (ni.isLoopback() || ni.isVirtual() || !ni.isUp())
					continue;
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!"127.0.0.1".equals(ip.getHostAddress())) {
						if (ip instanceof Inet4Address) {
							localhosts.add(ip.getHostAddress());
						}
					}
				}
			}

			if (localhosts == null || localhosts.size() == 0) {
				localhosts.add("127.0.0.1");
				logger.error(" get local ip address null, set to common 127.0.0.1 !");
			}

		} catch (Exception e) {
			localhosts.add("127.0.0.1");
			logger.error(" get local ip address error, set to common 127.0.0.1 !");
		}

		return localhosts;
	}
}
