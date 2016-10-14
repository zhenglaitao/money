package com.daily.record.money.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.daily.record.money.interfaces.TableChangedListener;

public class TableChangedMonitor {

	private List<TableChangedListener> listeners = new ArrayList<TableChangedListener>();
	static final Logger logger = Logger.getLogger(TableChangedMonitor.class);
	public static TableChangedMonitor getInstance() {

		return InnerHolder.INSTANCE;
	}

	private static class InnerHolder {
		static final TableChangedMonitor INSTANCE = new TableChangedMonitor();
	}
	
	public synchronized void addListener(TableChangedListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public synchronized void removeListener(TableChangedListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}
	public void notifyTableChanged(String did) {
		for (TableChangedListener listener : listeners) {
			listener.onTableChanged(did);
		}
	}
}
