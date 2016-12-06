package com.daily.record.money.util;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ResultSetUtils {

	static Logger logger = Logger.getLogger(ResultSetUtils.class);

	public static List<Object> convert(String[] columns, Map<String, Object>... maps) {
		Map<String, Object> mapall = new HashMap<String, Object>();
		for (Map<String, Object> map : maps) {
			mapall.putAll(map);
		}

		List<Object> head = new ArrayList<Object>();
		for (String column : columns) {
			if (mapall.containsKey(column)) {
				Object object = mapall.get(column);
				head.add(object);
			}
		}
		return head;
	}

	public static Map<String, Object> headToMap(ResultSet rs) throws SQLException {
		return headToMap(rs.getMetaData());
	}

	public static Map<String, Object> headToMap(ResultSetMetaData msd) throws SQLException {

		ResultSetMetaData metaData = msd;
		int columnCount = metaData.getColumnCount();

		Map<String, Object> head = new HashMap<String, Object>();
		// convert head
		for (int i = 0; i < columnCount; i++) {
			String columnName = metaData.getColumnLabel(i + 1);
			int columnType = metaData.getColumnType(i + 1);
			ColumnInfo info = getColumnInfo(columnName, columnType);
			// logger.info("columnName---->type" + columnName + "------>" +
			// msd.getColumnTypeName(i + 1) + "," + columnType);
			head.put(columnName, info);
		}

		return head;
	}

	/**
	 * 对于ResultSet确定，但其中部分数据需要特别处理的情况， 可以在组装用于打包的数据集list之前，调用此方法直接把头信息添加至list(0)
	 */
	public static List<Object> headToList(ResultSetMetaData msd) throws SQLException {

		ResultSetMetaData metaData = msd;
		int columnCount = metaData.getColumnCount();

		List<Object> head = new ArrayList<Object>();
		String columnName = "";
		int columnType = 0;
		// convert head
		for (int i = 0; i < columnCount; i++) {
			columnName = metaData.getColumnLabel(i + 1);
			columnType = metaData.getColumnType(i + 1);
			ColumnInfo info = getColumnInfo(columnName, columnType);
			head.add(info);
		}

		return head;
	}

	private static ColumnInfo getColumnInfo(String columnName, int columnType) {
		ColumnInfo info = new ColumnInfo(columnName);
		if (columnType == Types.CHAR || columnType == Types.VARCHAR || columnType == Types.LONGVARCHAR) {
			info.setColumnType(COL_TYPE.COLTYPE_STRING);
		} else if (columnType == Types.DECIMAL || columnType == Types.DOUBLE || columnType == Types.FLOAT || columnType == Types.REAL) {
			info.setColumnType(COL_TYPE.COLTYPE_DOUBLE);
		} else if (columnType == Types.INTEGER || columnType == Types.SMALLINT || columnType == Types.NUMERIC || columnType == Types.TINYINT) {
			info.setColumnType(COL_TYPE.COLTYPE_INT);
		} else if (columnType == Types.BIGINT) {
			info.setColumnType(COL_TYPE.COLTYPE_LONG);
		} else if (columnType == Types.BLOB || columnType == Types.CLOB || columnType == Types.NCLOB || columnType == Types.BINARY || columnType == Types.VARBINARY || columnType == Types.LONGVARBINARY) {
			info.setColumnType(COL_TYPE.COLTYPE_RAW);
		} else {
			info.setColumnType(COL_TYPE.COLTYPE_UNKNOW);
		}
		return info;
	}

	public static List<List<Object>> toArray(Statement st) throws SQLException, InterruptedException {
		List<List<Object>> result = new ArrayList<List<Object>>();
		do {
			ResultSet resultSet = st.getResultSet();
			List<List<Object>> array = toArray(resultSet);
			array.remove(0);
			result.addAll(array);
		} while (st.getMoreResults());
		return result;
	}

	public static List<List<Object>> toArray(ResultSet rs) throws SQLException, InterruptedException {
		// int rowCount = 0;
		final List<List<Object>> data = new ArrayList<List<Object>>();

		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		List<Object> head = new ArrayList<Object>();
		// convert head
		for (int i = 0; i < columnCount; i++) {
			String columnName = metaData.getColumnLabel(i + 1);
			int columnType = metaData.getColumnType(i + 1);
			ColumnInfo info = getColumnInfo(columnName, columnType);
			head.add(info);
		}

		data.add(head);
		// rowCount++;
		// convert rows
		while (rs.next()) {
			ThreadContext.checkThreadInterrupted();

			List<Object> row = new ArrayList<Object>();
			for (int i = 0; i < columnCount; i++) {
				ColumnInfo info = (ColumnInfo) head.get(i);
				int dataIndex = i + 1;

				if (info.getColumnType() != COL_TYPE.COLTYPE_RAW) {
					row.add(rs.getObject(dataIndex));
				} else {
					row.add(rs.getBytes(dataIndex));
				}
			}
			data.add(row);
			// rowCount++;
		}
		return data;
	}

	public static JSONArray toJSONArray(ResultSet rs) throws SQLException, InterruptedException {
		return convertToJSONArray(rs, null);
	}

	public static JSONArray toJSONArrayDefaultEmpty(ResultSet rs) throws SQLException, InterruptedException {
		return convertToJSONArray(rs, "");
	}

	private static JSONArray convertToJSONArray(ResultSet rs, String defaultValue) throws SQLException, InterruptedException {
		final JSONArray data = new JSONArray();

		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		List<Object> head = new ArrayList<Object>();
		// convert head
		for (int i = 0; i < columnCount; i++) {
			String columnName = metaData.getColumnLabel(i + 1);
			int columnType = metaData.getColumnType(i + 1);
			ColumnInfo info = getColumnInfo(columnName, columnType);
			head.add(info);
		}

		while (rs.next()) {
			ThreadContext.checkThreadInterrupted();

			JSONObject row = new JSONObject();
			for (int i = 0; i < columnCount; i++) {
				ColumnInfo info = (ColumnInfo) head.get(i);
				int dataIndex = i + 1;
				if (rs.getObject(dataIndex) == null) {
					row.put(info.getColumnName(), defaultValue == null ? JSONNull.getInstance() : "");
				} else {
					Object val = rs.getObject(dataIndex);
					if(val instanceof Long)
						row.put(info.getColumnName(), String.valueOf(val));
					else if(val instanceof Double){//精度问题
//						DecimalFormat df= new DecimalFormat("0"); //格式化,取整
//						String fmtVal = df.format(val);
//						if(new BigDecimal(val.toString()).compareTo(new BigDecimal(fmtVal)) == 0) {
//							row.put(info.getColumnName(), new BigDecimal(fmtVal).toPlainString());
//						} else {
//							row.put(info.getColumnName(), new BigDecimal(rs.getObject(dataIndex).toString()).toPlainString());
//						}
						DecimalFormat df= new DecimalFormat("#.####################");
						row.put(info.getColumnName(), df.format(val));
					}else{
						row.put(info.getColumnName(), rs.getObject(dataIndex));
					}
				}
			}
			data.add(row);
		}
		return data;
	}

	public static JSONObject toJSONObject(ResultSet rs) throws Exception {
		final JSONObject data = new JSONObject();
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnLabel(i);
				data.put(columnName, rs.getObject(i));
			}
		}
		return data;
	}

	public static JSONObject toJSONObjectDefaultEmpty(ResultSet rs) throws Exception {
		final JSONObject data = new JSONObject();
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnLabel(i);
				if (rs.getObject(i) == null) {
					data.put(columnName,"");
				} else {
					Object val = rs.getObject(i);
					if(val instanceof Long){
						data.put(columnName, String.valueOf(val));
					}else if(val instanceof Double){
						DecimalFormat df= new DecimalFormat("#.####################");
						data.put(columnName, df.format(val));
//						if(new BigDecimal(val.toString()).compareTo(new BigDecimal(fmtVal)) == 0) {
//							data.put(columnName, new BigDecimal(fmtVal).toPlainString());
//						} else {
//							data.put(columnName, new BigDecimal(rs.getObject(i).toString()).toPlainString());
//						}
					}else{
						data.put(columnName,val);
					}
				}
			}
		}
		return data;
	}
	
	public static HashMap<Object, List<List<Object>>> toMapArray(ResultSet rs, String groupLabel) throws SQLException, InterruptedException {
		return toMapArray(rs, groupLabel, 5);
	}

	public static HashMap<Object, List<List<Object>>> toMapArray(ResultSet rs, String groupLabel, int initalSize) throws SQLException, InterruptedException {

		HashMap<Object, List<List<Object>>> result = new HashMap<Object, List<List<Object>>>(initalSize);

		int columnCount = rs.getMetaData().getColumnCount();
		List<Object> head = headToList(rs.getMetaData());
		// convert rows
		while (rs.next()) {
			ThreadContext.checkThreadInterrupted();

			Object group = rs.getObject(groupLabel);
			if (group instanceof Double || group instanceof Long || group instanceof Integer) {
				group = String.valueOf(group);
			}
			if (!result.containsKey(group)) {
				result.put(group, new ArrayList<List<Object>>());
			}
			List<List<Object>> data = result.get(group);
			List<Object> row = new ArrayList<Object>();
			for (int i = 0; i < columnCount; i++) {
				ColumnInfo info = (ColumnInfo) head.get(i);
				int dataIndex = i + 1;

				if (info.getColumnType() != COL_TYPE.COLTYPE_RAW) {
					row.add(rs.getObject(dataIndex));
				} else {
					row.add(rs.getBytes(dataIndex));
				}
			}
			data.add(row);
		}
		return result;
	}

	public static HashMap<Object, List<List<Object>>> toMapArray(ResultSet rs, int initalSize, String... groupLabel) throws SQLException, InterruptedException {

		HashMap<Object, List<List<Object>>> result = new HashMap<Object, List<List<Object>>>(initalSize);

		int columnCount = rs.getMetaData().getColumnCount();
		List<Object> head = headToList(rs.getMetaData());
		// convert rows
		while (rs.next()) {
			ThreadContext.checkThreadInterrupted();

			String groupKey = "";
			for (int i = 0; i < groupLabel.length; i++) {
				Object group = rs.getObject(groupLabel[i]);
				if (group instanceof Double || group instanceof Long || group instanceof Integer) {
					if (i == 0) {
						groupKey = String.valueOf(group);
					} else {
						groupKey = groupKey + "-" + String.valueOf(group);
					}
				} else {
					if (i == 0) {
						groupKey = (String) group;
					} else {
						groupKey = groupKey + "-" + (String) group;
					}
				}
			}
			if (!result.containsKey(groupKey)) {
				result.put(groupKey, new ArrayList<List<Object>>());
			}
			List<List<Object>> data = result.get(groupKey);
			List<Object> row = new ArrayList<Object>();
			for (int i = 0; i < columnCount; i++) {
				ColumnInfo info = (ColumnInfo) head.get(i);
				int dataIndex = i + 1;

				if (info.getColumnType() != COL_TYPE.COLTYPE_RAW) {
					row.add(rs.getObject(dataIndex));
				} else {
					row.add(rs.getBytes(dataIndex));
				}
			}
			data.add(row);
		}
		return result;
	}

	/**
	 * 按照 groupLabel(CO),timeLabel(时间列,年月)分组
	 * 
	 * @param rs
	 * @param groupLabel
	 * @param timeLabel
	 * @param type
	 *            :使用Calender
	 * @return
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public static Map<Object, LinkedHashMap<String, List<List<Object>>>> toMapMapTimeArray(ResultSet rs, String groupLabel, String timeLabel, int type) throws SQLException, InterruptedException {
		// HashMap<Object, List<List<Object>>> result = new HashMap<Object,
		// List<List<Object>>>();
		// java.util.Calendar.YEAR;
		Map<Object, LinkedHashMap<String, List<List<Object>>>> results = new HashMap<Object, LinkedHashMap<String, List<List<Object>>>>(); // map
		int columnCount = rs.getMetaData().getColumnCount();
		List<Object> head = headToList(rs.getMetaData());
		LinkedHashMap<String, List<List<Object>>> dateMap = null;
		// convert rows
		while (rs.next()) {
			ThreadContext.checkThreadInterrupted();

			Object group = rs.getObject(groupLabel);
			/*
			 * if(group instanceof Double || group instanceof Long || group instanceof Integer){ group=String.valueOf(group); }
			 */
			Object dateGroup = rs.getObject(timeLabel);
			String time = String.valueOf(dateGroup);
			String year = "";
			if (type == Calendar.YEAR) { // 以年作为key
				year = time.substring(0, 4); // 年作为key
			} else if (type == Calendar.MONTH) {
				year = time.substring(0, 6); // 年,月作为key
			}
			if (year.equals("")) {
				throw new java.lang.RuntimeException("type parameter error");
			}
			if (!results.containsKey(group)) {
				dateMap = new LinkedHashMap<String, List<List<Object>>>();
				dateMap.put(year, new ArrayList<List<Object>>());
				results.put(group, dateMap);
			} else { // 存在co的情况
				dateMap = results.get(group);
				if (!dateMap.containsKey(year)) {
					dateMap.put(year, new ArrayList<List<Object>>(columnCount));
				}
			}

			List<List<Object>> data = results.get(group).get(year);
			List<Object> row = new ArrayList<Object>();
			for (int i = 0; i < columnCount; i++) {
				ColumnInfo info = (ColumnInfo) head.get(i);
				int dataIndex = i + 1;
				if (info.getColumnType() != COL_TYPE.COLTYPE_RAW) {
					row.add(rs.getObject(dataIndex));
				} else {
					row.add(rs.getBytes(dataIndex));
				}
			}
			data.add(row);
		}
		return results;
	}

	public static List<List<Object>> convertArrayByfids(String[] fids, List<List<Object>> data) throws SQLException, InterruptedException {

		List<List<Object>> convertData = new ArrayList<List<Object>>();
		List<Integer> convert = new ArrayList<Integer>();
		List<Object> convertheaddata = new ArrayList<Object>();

		for (String fid : fids) {
			List<Object> subdatahead = data.get(0);
			for (int i = 0; i < subdatahead.size(); i++) {
				ColumnInfo columnInfo = (ColumnInfo) subdatahead.get(i);
				String colname = columnInfo.getColumnName();
				if (StringUtils.equalsIgnoreCase(fid, colname)) {
					convertheaddata.add(columnInfo);
					convert.add(i);
				}
			}
		}

		convertData.add(convertheaddata);
		for (int i = 1; i < data.size(); i++) {
			List<Object> conversubdata = new ArrayList<Object>();
			List<Object> subdata = data.get(i);
			for (Integer index : convert) {
				conversubdata.add(subdata.get(index));
			}
			convertData.add(conversubdata);
		}

		return convertData;
	}

	public static List resultSetToList(ResultSet rs, Class cls)throws Exception {  
	      
		  
        //取得Method   
        Method[] methods = cls.getDeclaredMethods();   
//       System.out.println(methods[0].getName());  
        List lst = new ArrayList();  
        // 用于获取列数、或者列类型  
        ResultSetMetaData meta = rs.getMetaData();  
        Object obj = null;  
        while (rs.next()) {  
            // 获取formbean实例对象  
            obj = cls.newInstance(); // 用Class.forName方法实例化对象和new创建实例化对象是有很大区别的，它要求JVM首先从类加载器中查找类，然后再实例化，并且能执行类中的静态方法。而new仅仅是新建一个对象实例  
            // 循环获取指定行的每一列的信息  
            for (int i = 1; i <= meta.getColumnCount(); i++) {  
                // 当前列名  
                String colName = meta.getColumnName(i);  
                  
                // 设置方法名  
                String setMethodName = "set" + colName;  
                    
                  
                 //遍历Method   
                for (int j = 0; j < methods.length; j++) {   
                    if (methods[j].getName().equalsIgnoreCase(setMethodName)) {   
                        setMethodName = methods[j].getName();   
                          
//                        System.out.println(setMethodName);  
                        // 获取当前位置的值，返回Object类型  
                        Object value = rs.getObject(colName);   
                        if(value == null){  
                            continue;  
                        }  

                        //实行Set方法   
                        try {   
                            //// 利用反射获取对象  
                            //JavaBean内部属性和ResultSet中一致时候   
                        	//System.out.println(value.getClass());
                            Method setMethod = obj.getClass().getMethod(   
                                    setMethodName, value.getClass());   
                            //System.out.println(setMethod+"----"+setMethodName);
                            setMethod.invoke(obj, value);   
                        } catch (Exception e) {   
                            //JavaBean内部属性和ResultSet中不一致时候，使用String来输入值。   
                        	Method setMethod = obj.getClass().getMethod(   
                                    setMethodName, String.class);   
                            setMethod.invoke(obj, value.toString()); 
                        }   
                    }   
                }   
            }  
            lst.add(obj);  
        }  

        return lst;  
      
}  

}