package com.haogre.pay.wechat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value="")
@Controller
public class HomeController{
	
	/**
	 * 访问地址无法找到，自动跳转404页面
	 * @return
	 */
	@RequestMapping(value="/error/{code}")
	public String errorCode(@PathVariable int code){
		if(404 == code){
			return "404";
		}else if(500 == code){
			return "serverError";
		}else if(502 == code){
			return "502";
		}else if(503 == code){
			return "503";
		}else if(504 == code){
			return "504";
		}
		return "";
	}
	
}