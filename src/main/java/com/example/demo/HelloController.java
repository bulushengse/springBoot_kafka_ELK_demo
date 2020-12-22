package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HelloController {
	
	protected static final Logger logger = LoggerFactory.getLogger(HelloController.class);
	
	/**
	 *  测试      http://localhost:8080/hello
	 *
	 */
	@GetMapping(value="/hello")  
	public ModelAndView hello() { 
		logger.info("用户xxx请求登录。。。ip=xxx");
		ModelAndView mv = new ModelAndView();
		mv.addObject("msg","hello word!");
		mv.setViewName("hello");
		logger.info("用户xxx登录成功~~");
		return mv;  
	} 
	
}
