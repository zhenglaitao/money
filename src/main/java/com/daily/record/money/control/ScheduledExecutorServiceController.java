package com.daily.record.money.control;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.daily.record.money.common.PBQScheduledExecutorService;

@Controller
public class ScheduledExecutorServiceController implements Runnable{

	@RequestMapping(value="/scheduledTest.htm")
	public ModelAndView scheduledTest(){
		ModelAndView model = new ModelAndView();
		model.setViewName("testscheduled");
//		Integer time = 2;
//		PBQScheduledExecutorService.INSTANCE.schedule(this, time, TimeUnit.MINUTES);
		return model;
	}

	@Override
	public void run() {
		scheduledTest();
	}
}
