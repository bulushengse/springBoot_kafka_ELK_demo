 package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration  
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter{
		
	  /**
	   * 控制层controller返回view添加设置前缀 和后缀，两种方法
	   * 1.重写viewResolver方法
	   * 2.在application.properties文件中配置
	   * @return
	   */
	   @Bean  
	   public InternalResourceViewResolver viewResolver(){  
	        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();  
	        viewResolver.setPrefix("/WEB-INF/jsp/");  
	        viewResolver.setSuffix(".jsp");  
	        viewResolver.setViewClass(JstlView.class);  
	        return  viewResolver;  
	    } 
	   
	   /**
	    * 对静态资源的访问，也可在application.properties文件中配置
	    */
	    @Override  
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {  
	        //super.addResourceHandlers(registry);  
	        //addResourceHandler指的是对外暴露的访问路径，addResourceLocations指的是文件放置的目录
	        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");  
	        //WebJars以jar包的形式来使用前端的各种框架、组件
	        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	        
	    }
	    
	   /**
	    * 重写configurePathMatch方法可忽略"."后面的参数
	    */
	    public void configurePathMatch(PathMatchConfigurer configurer) {
			// super.configurePathMatch(configurer);
			configurer.setUseSuffixPatternMatch(false);
		}  

	   /**
	    * 重写addViewControllers方法，实现一个请求到视图的映射，设置默认的首页
	    * @param registry 
	    */
	    @Override  
	    public void addViewControllers(ViewControllerRegistry registry){  
	    	//super.addViewControllers(registry);
	        registry.addViewController("/").setViewName("/hello");  
	        
	    } 
}
