input {
  #输入源 stdin,tcp,file,kafka,redis,beats,syslog 。。。
  #stdin { }  #从控制台中输入来源
  tcp {
    mode => "server"
    host => "0.0.0.0"   # 允许任意主机发送日志
	port => 5432    	# 要于logback-spring.xml中配置输出源port相同
    codec => json_lines  # 数据格式
	#add_field => {"test"=>"test1"}  #添加自定义的字段
	#type => "elk1"      # 设定type以区分每个输入源
  }
  file {
	path => "E:/xxx/data/*" 	
	#path => ["E:/software/logstash-1.5.4/logstash-1.5.4/data/*.log","F:/*.log"] #监听文件的多个路径
	#exclude => "1.log"           #排除不想监听的文件
    #stat_interval => 1           #设置多长时间检查一次被监听文件状态（是否有更新），默认是 1 秒。
	start_position => beginning   #监听文件的起始位置，默认是end。beginning表示从头开始读取文件，end表示读取最新的
    #discover_interval => 15      #设置多长时间扫描监听目录下是否有新文件，默认是 15 秒。
	#close_older => 3600 		  #已监听中的文件，如果超过这个值的时间内没有更新内容，就关闭监听它的文件句柄。默认是 3600 秒，即一小时。
	#ignore_older => 86400  	  #检查文件列表时，如果一个文件的最后修改时间超过这个值，就忽略这个文件。默认是 86400 秒，即一天。
	sincedb_path => "E:/xxx/data/access_progress" #文件读取进度的记录，每行表示一个文件，每行有两个数字，第一个表示文件的inode，第二个表示文件读取到的位置
	#sincedb_write_interval => 15  #设置多长时间写一次 sincedb 文件，默认是 15 秒。
    #add_field => {"test"=>"test1"}  #添加自定义的字段
	#type => "elk2"

  }
  kafka{   #kafka更多配置说明 https://segmentfault.com/a/1190000016595992
    bootstrap_servers => "localhost:9092"
	#client_id => "test"
    #group_id => "test"
    topics => ["logger-channel"]     #消费topic  多个用逗号隔开
    auto_offset_reset => "latest"    #从最新的偏移量开始
	#decorate_events => true         #此属性会将当前topic、offset、group、partition等信息也带到message中	
	#add_field => {"test"=>"test1"}  #添加自定义的字段
    #type => "elk3"
  }
  
} 
filter {
  #在json化之前,使用mutte对\\x字符串进行替换，防止以下错误：ParserError: Unrecognized character escape 'x' (code 120)
    mutate {
        gsub => ["message", "\\x", "\\\x"]
    }
    json {
        source => "message"
        #删除无用字段，节约空间
        remove_field => "@version"
		remove_field => "@version"
    }
    date {
        #用nginx请求时间替换logstash生成的时间
        match => ["time_local", "ISO8601"]
        target => "@timestamp"
    }
    grok {
        #从时间中获取day
        match => { "time_local" => "(?<day>.{10})" }
    }
    grok {
        #将request解析成2个字段：method\url
        match => { "request" => "%{WORD:method} (?<url>.* )" }
    }
    grok {
        #截取http_referer问号前的部分，问号后的信息无价值，浪费空间
        match => { "http_referer" => "(?<referer>-|%{URIPROTO}://(?:%{USER}(?::[^@]*)?@)?(?:%{URIHOST})?)" }
    }
    mutate {
        #解析出新的字段后，原字段丢弃
        remove_field => "request"
        remove_field => "http_referer"
		#rename重命名某个字段，如果目的字段已经存在，会被覆盖掉：
        rename => { "http_user_agent" => "agent" }
        rename => { "upstream_response_time" => "response_time" }
        rename => { "host" => "log_source" }
        rename => { "http_x_forwarded_for" => "x_forwarded_for" }
        #以下2个字段以逗号分隔后，以数组形式入库
        split => { "x_forwarded_for" => ", " }
        split => { "response_time" => ", " }
    }
    #alter {
    #    #不满足elasticsearch索引模型的，入库会失败，因此做以下数据转换
    #    condrewrite => [
    #        "x_forwarded_for", "-", "0.0.0.0",
    #        "x_forwarded_for", "unknown", "0.0.0.0",
    #        "response_time", "-", "0",
    #        "real_ip", "", "0.0.0.0"
    #    ]
    #}
}
	
output {
  # if [type] == "elk1" { elasticsearch{XXX} } 
  elasticsearch {
    hosts => "localhost:9200"
    #index => "%{[appname]}-%{+YYYY.MM.dd}"  #tcp的输入源可指定索引名称，[appname]要在logback-spring.xml中配置
	index => "springboot-%{+YYYY.MM.dd}"
  }
  
  stdout { 
    codec => rubydebug   #logstash控制台输出格式，默认rubydebug，可选json
  }
}