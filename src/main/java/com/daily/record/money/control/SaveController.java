package com.daily.record.money.control;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.daily.record.money.service.impl.MoneyService;
import com.daily.record.money.util.AbstractRequest;

@Controller
public class SaveController extends AbstractRequest {

	private String[] tabset ={"daily"};
	@RequestMapping(value="/html/save.htm")
	public @ResponseBody JSONObject save(HttpServletRequest req) throws SQLException{
		String arg = req.getParameter("arg");
		System.out.println(arg);
		JSONObject json = JSONObject.fromObject(arg);
		JSONObject result = new JSONObject();
		result.put("id", "");
		String oper = convertNull(json.getString("operation"));
		Connection conn = getMysqlConn();
		MoneyService service = new MoneyService(conn);
		String responseCode = "";
		if("update".equals(oper)){
			//update 不返回id
			responseCode  = service.update(json);
			result.put("responseCode", responseCode);
		}else if("insert".equals(oper)){
			//插入返回 responseCode+id ---example:200/12;
			responseCode = service.insert(json);
			if(responseCode.indexOf("/") > -1){
				result.put("id", responseCode.substring(responseCode.indexOf("/")+1,responseCode.length()));
				result.put("responseCode", responseCode.substring(0,responseCode.indexOf("/")));
			}else{
				result.put("responseCode", responseCode);
			}
		}
		notifyTableChanged(tabset);
		return result;
	}
}
