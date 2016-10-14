package com.daily.record.money.control;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import redis.clients.jedis.Jedis;

import com.daily.record.money.common.RedisService;


@WebServlet(name="loginServlet",urlPatterns="/login.htm")
public class LoginController  extends HttpServlet {

	@Override
	public void init() throws ServletException {
		RedisService.getInstance().init();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		if(username!= null && "zlt".equals(username) && password !=null && "zlt".equals(password)){
			//RedisService.getInstance().get(key);
			resp.sendRedirect(req.getContextPath() + "index.htm");
		}else{
			resp.sendRedirect(req.getContextPath() + "login.html");
		}
	}
}
