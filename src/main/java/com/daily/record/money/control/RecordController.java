package com.daily.record.money.control;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.daily.record.money.common.CacheManagerService;
import com.daily.record.money.common.RedisService;
import com.daily.record.money.entity.Daily;
import com.daily.record.money.util.AbstractRequest;
import com.daily.record.money.util.ResultSetUtils;

@Controller
public class RecordController extends AbstractRequest{

	private static final Logger logger = Logger.getLogger(RecordController.class);
	
	private static List<String> dateList;
	private static Map<String,String> dateMap;
	private static String showDate;
	private static List<String> dayListOneMonth;
	private String[] tableSet = { "daily"};
	static{
		try {
			initDate(null);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/index.htm")
	public ModelAndView query(HttpServletRequest request) throws Exception{
//		System.out.println(RedisService.getInstance().pool);
//		Set<String> set = RedisService.getInstance().pool.getResource().keys("*");
//		Iterator<String> it = set.iterator();
//		while(it.hasNext()){
//			System.out.println(it.next());
//			System.out.println(RedisService.getInstance().get(it.next()));
//		}
//		System.out.println(RedisService.getInstance().get(request.getSession().getId())+"---------------");
		Cache cache = CacheManagerService.getDefaultCache();
		ModelAndView model = new ModelAndView();
		model.setViewName("show");
		String date = request.getParameter("date");
		if(date == null){
			date = showDate;
		}
		String key = com.daily.record.money.util.KeyUtils.GetMd5Key("RecordController" + date);
		
		Calendar now = Calendar.getInstance();
		String newShowDate = new SimpleDateFormat("yyyyMM").format(now.getTime());
		if(!newShowDate.equals(showDate) || !date.equals(showDate) ){
			initDate(date);
		}
		model.addObject("showDate", showDate);
		String startDate = date+"01";
		String endDate = dateMap.get(date);
		endDate = endDate+"01";
		System.out.println("date from "+startDate+" to ---"+endDate);
		Connection conn = null;
		List<Daily> list = new ArrayList<>();
		List<Daily> beferList = new ArrayList<>();
		List<Daily> afterList = new ArrayList<>();
		Map<String,Daily> dataMap = new HashMap<>();
		double totle_shuai = 0;
		double totle_notshuai = 0;
		try {
			//取cache
			if(cache.get(key+"List") != null){
				logger.info("read data from cache true,key-----"+key+"List");
				list = (List<Daily>) cache.get(key+"List").getObjectValue();
			}else{
				//查询
				logger.info("read data from cache false");
				conn = getMysqlConn();
				PreparedStatement stat = conn.prepareStatement("select * from daily where date >= ? and date < ? ");
				stat.setInt(1, Integer.valueOf(startDate));
				stat.setInt(2, Integer.valueOf(endDate));
				ResultSet rs = stat.executeQuery();
				
				list = ResultSetUtils.resultSetToList(rs, Daily.class);
				logger.info("list==="+list.size());
				Element element = new Element(key+"List", list);
				cache.put(element);
				removeOnTableChanged(key+"List", tableSet);
			}
			//循环处理得出totle
			for(int i=0;i<list.size();i++){
				//System.out.println(list.get(i).getDate());
				dataMap.put(list.get(i).getDate()+"", list.get(i));//前台保证 日期不为空
				if(list.get(i).getTotle_shuai() != null){
					totle_shuai += list.get(i).getTotle_shuai().doubleValue();
				}
				if(list.get(i).getTotle_notshuai() != null){
					totle_notshuai += list.get(i).getTotle_notshuai().doubleValue();
				}
			}
			for(int j=0;j<dayListOneMonth.size();j++){
				if(j<=15){
					if(dataMap.containsKey(dayListOneMonth.get(j))){
						Daily daily = dataMap.get(dayListOneMonth.get(j));
						daily.setOperation("update");
						daily.setIndex(j);
						beferList.add(daily);
					}else{
						Daily daily = new Daily();
						daily.setOperation("insert");
						daily.setDate(Integer.valueOf(dayListOneMonth.get(j)));
						daily.setIndex(j);
						beferList.add(daily);
					}
				}else{
					if(dataMap.containsKey(dayListOneMonth.get(j))){
						Daily daily = dataMap.get(dayListOneMonth.get(j));
						daily.setOperation("update");
						daily.setIndex(j);
						afterList.add(daily);
					}else{
						Daily daily = new Daily();
						daily.setOperation("insert");
						daily.setIndex(j);
						daily.setDate(Integer.valueOf(dayListOneMonth.get(j)));
						afterList.add(daily);
					}
				}
			}
			model.addObject("beferlist", beferList);
			model.addObject("afterlist", afterList);
			model.addObject("dateList", dateList);
			model.addObject("totle_shuai", totle_shuai);
			model.addObject("totle_notshuai", totle_notshuai);
			model.addObject("totlenum", totle_shuai+totle_notshuai);
			double avg = (totle_shuai+totle_notshuai)/3*2;//求得平均数
			double shuaichu = avg-totle_shuai;//每月帅出
			BigDecimal shuaichuVal = new BigDecimal(shuaichu);
			shuaichuVal = shuaichuVal.setScale(2, BigDecimal.ROUND_HALF_UP);
			model.addObject("shuaichu", shuaichuVal);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return model;
	}
	
	public static List<String> initDate(String date) throws ParseException{
		Calendar now = Calendar.getInstance();
		showDate = new SimpleDateFormat("yyyyMM").format(now.getTime());
		if(date != null){
			now.setTime(new SimpleDateFormat("yyyyMM").parse(date));
			showDate = date;
		}
		initDays(now);
		String year = String.valueOf(now.get(Calendar.YEAR));
		now.add(Calendar.YEAR, 1);
		String nextYear = String.valueOf(now.get(Calendar.YEAR));
		dateMap = new HashMap<String, String>();
		dateList = new ArrayList<>();
		dateList.add(year+"01");
		dateMap.put(year+"01", year+"02");
		dateList.add(year+"02");
		dateMap.put(year+"02", year+"03");
		dateList.add(year+"03");
		dateMap.put(year+"03", year+"04");
		dateList.add(year+"04");
		dateMap.put(year+"04", year+"05");
		dateList.add(year+"05");
		dateMap.put(year+"05", year+"06");
		dateList.add(year+"06");
		dateMap.put(year+"06", year+"07");
		dateList.add(year+"07");
		dateMap.put(year+"07", year+"08");
		dateList.add(year+"08");
		dateMap.put(year+"08", year+"09");
		dateList.add(year+"09");
		dateMap.put(year+"09", year+"10");
		dateList.add(year+"10");
		dateMap.put(year+"10", year+"11");
		dateList.add(year+"11");
		dateMap.put(year+"11", year+"12");
		dateList.add(year+"12");
		dateMap.put(year+"12", nextYear+"01");
		return dateList;
	}

	private static void initDays(Calendar now) {
		dayListOneMonth = new ArrayList<>();
		int maxDay = now.getActualMaximum(Calendar.DAY_OF_MONTH); 
		//System.out.println("max day----"+maxDay);
		String daySuffix="";
		for(int i=1;i<=maxDay;i++){
			daySuffix = "";
			if(i<10){
				daySuffix = "0"+i+"";
			}else{
				daySuffix = ""+i;
			}
			//System.out.println(showDate+daySuffix);
			dayListOneMonth.add(showDate+daySuffix);
		}
		
	}
}
