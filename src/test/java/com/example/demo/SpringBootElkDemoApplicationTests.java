package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest							
public class SpringBootElkDemoApplicationTests {

	 private final static Logger log = LoggerFactory.getLogger(Test.class);
	 
	@Before
	public void setUp() throws Exception{
		
	}
	
	@Test
	public void hello() throws Exception{
		log.warn("我是一个warn");
    	log.info("我是一个info");
    	log.error("我是一个error");
    	log.trace("我是一个trace");
        log.debug("我是一个debug");
	}

}
