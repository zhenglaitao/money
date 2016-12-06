package com.daily.record.money.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;

public class MoneyService {

	private Connection conn;
	
	private static final String INSERT_SQL = "insert into daily (date,breakfast,lunch,dinner,other,totle_notshuai,totle_shuai,change_times) values (?,?,?,?,?,?,?,?)";
	private static final String UPDATE_SQL = "update daily set breakfast = ?, lunch = ?, dinner = ?, other = ?, totle_notshuai = ?, totle_shuai = ?, last_update_time = ?, change_times = ? where id =?";
	
	public MoneyService() {
		// TODO Auto-generated constructor stub
	}
	public MoneyService(Connection conn) {
		this.conn = conn;
	}
	public String update(JSONObject json) throws SQLException {
		try {
			PreparedStatement stat = conn.prepareStatement(UPDATE_SQL);
			stat.setObject(1, convertNullStr(json.getString("breakfast")));
			stat.setObject(2, convertNullStr(json.getString("lunch")));
			stat.setObject(3, convertNullStr(json.getString("dinner")));
			stat.setObject(4, convertNullStr(json.getString("other")));
			stat.setObject(5, convertNullStr(json.getString("totle_notshuai")));
			stat.setObject(6, convertNullStr(json.getString("totle_shuai")));
			stat.setString(7, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			stat.setInt(8, StringUtils.isBlank(json.getInt("change_times")+"")?1:1+json.getInt("change_times"));
			stat.setInt(9, json.getInt("id"));
			if(stat.executeUpdate() > 0){
				return "200";
			}else{
				return "500";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			conn.close();
		}
		return null;
	}
	public String insert(JSONObject json) throws SQLException {
		try {
			PreparedStatement stat = conn.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS);
			stat.setInt(1, json.getInt("date"));
			stat.setObject(2, convertNullStr(json.getString("breakfast")));
			stat.setObject(3, convertNullStr(json.getString("lunch")));
			stat.setObject(4, convertNullStr(json.getString("dinner")));
			stat.setObject(5, convertNullStr(json.getString("other")));
			stat.setObject(6, convertNullStr(json.getString("totle_notshuai")));
			stat.setObject(7, convertNullStr(json.getString("totle_shuai")));
			stat.setObject(8, 1);
			if(stat.executeUpdate() > 0){
				ResultSet rs = stat.getGeneratedKeys();  
				Object retId = null;;
				if (rs.next())  
	                retId = rs.getObject(1);  
				if(retId != null){
					return "200/"+retId;
				}
				return "200";
			}else{
				return "500";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			conn.close();
		}
		return null;
	}
	
	public static String convertNullStr(String str){
		if("".equals(str)){
			str = null;
		}
		return str;
	}
}
