# SpringBoot_ELK_demo

Kafka是一种分布式的，基于发布/订阅的消息系统，用于处理活跃的流式数据,大数据量的数据处理上。常用日志采集，数据采集上。

对比RabbitMQ,RabbitMQ遵循AMQP协议，由内在高并发的erlang语言开发，用在实时的对可靠性要求高的消息传递上，

kafka broker:消息的代理，Producers往Brokers里面的指定Topic中写消息，Consumers从Brokers里面拉取指定Topic的消息，broker在中间起到一个代理保存消息的中转站。

官网下载 http://kafka.apache.org/downloads.html  2.3版本，选Binary downloads下tgz包 （2.3以上版本，启动服务报错：命令语法不正确）

解压

需注意kafka_2.12-2.3.0\config下配置文件：    
zookeeper.properties 
consumer.properties
producer.properties 
server.properties 所有配置参数说明：https://blog.csdn.net/lizhitao/article/details/25667831

修改server.properties文件：
log.dirs=D:/DevelopmenteTools/kafka_2.12-2.3.0/logs

启动服务前需要运行环境 jdk,zookeeper

你可以另外下载zookeeper（详细使用看我的zookeeper-3.4.14），也可以用kafka内置zookeeper服务：
首先要修改zookeeper.properties文件：
dataDir=D:/DevelopmenteTools/kafka_2.12-2.3.0/logs/zookeeper

在kafka_2.12-2.3.0目录下cmd执行启动zookeeper命令：  .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

kafka命令,在kafka_2.12-2.3.0目录下cmd执行

服务启动命令：.\bin\windows\kafka-server-start.bat .\config\server.properties
cmd控制台ctrl+c 安全退出

创建topic：.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic 【topic name】

删除topic：.\bin\windows\kafka-topics.bat --delete --zookeeper localhost:2181 --topic 【topic name】

查看topic: .\bin\windows\kafka-topics.bat --list --zookeeper localhost:2181 

启动生产者producer(这里的ip和port是producer.properties配置文件里bootstrap.servers)： 
.\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic 【topic name】

启动消费者consumer(这里的ip和port是consumer.properties配置文件里bootstrap.servers)： 
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic 【topic name】 --from-beginning


springboot+kafka+ELK日志分析系统

ELK Stack 是Elasticsearch、Logstash、Kibana三个开源软件的组合。在实时数据检索和分析场合，三者通常是配合共用，而且又都先后归于 Elastic.co 公司名下，故有此简称。

ElasticSearch是一个开源分布式时实分析搜索引擎，建立在全文搜索引擎库Apache Lucene基础上，提供搜集、分析、存储数据三大功能；
官网下载速度太慢，借此在https://www.newbe.pro/Mirrors/Mirrors-Elasticsearch/下载解压

需注意elasticsearch-7.8.0\config下的配置文件:
elasticsearch.yml

启动服务前需要运行环境 jdk

双击elasticsearch-7.8.0\bin\elasticsearch.bat启动服务
如警告jdk版本不符的话，
由于elasticsearch-7.8内置了Jdk11，只需修改elasticsearch-7.8.0\bin\elasticsearch-env.bat文件
在41行插入：set JAVA_HOME=D:\DevelopmenteTools\ELK-logSystem\elasticsearch-7.8.0\jdk

Elasticsearch 5 需要 Java 8 以上版本；
Elasticsearch 6.5 开始支持 Java 11;
Elasticsearch 7.0 开始，内置了 Java 环境，所以说，安装 7.0+ 版本会方便很多。

浏览器输入测试：http://localhost:9200，启动成功。
cmd控制台 ctrl+c 安全退出

Logstash是一个用来搜集、分析、过滤日志的工具，并输出给Elasticarch。 
官网下载速度太慢，借此在https://www.newbe.pro/Mirrors/Mirrors-Logstash/下载解压

在logstash-7.8.0\bin目录下cmd执行启动命令：logstash -e "input { stdin { } } output { stdout {} }"

浏览器输入测试：http://localhost:9600，启动成功。
cmd控制台ctrl+c 安全退出

Kibana是一个基于Web的图形界面，用于搜索、分析和可视化存储在Elasticsearch指标中的日志数据。
官网下载速度太慢，借此在https://www.newbe.pro/Mirrors/Mirrors-Kibana/下载解压

需注意elasticsearch-7.8.0\config下的配置文件:
kibana.yml      elasticsearch.url启动的elasticsearch(http://localhost:9200/)(其实按照默认可以不用修改配置文件)

双击kibana-7.8.0-windows-x86_64\bin\kibana.bat启动服务

浏览器输入测试：http://localhost:5601，启动成功。
cmd控制台ctrl+c 安全退出


springboot + ELK 日志监控系统搭建

需要修改的配置：
在elasticsearch-7.8.0\config下修改配置文件elasticsearch.yml：
cluster.name: my-application     #集群名称 

在logstash-7.8.0\bin下创建文件logstash.conf，配置说明参考logstash_conf_readme.txt
input {
  #tcp {
    #mode => "server"
    #host => "0.0.0.0"   # 允许任意主机发送日志
	#port => 5432    	 # 要于logback-spring.xml中配置输出源port相同
    #codec => json_lines    # 数据格式
	#type => "elk1"      # 设定type以区分每个输入源
  #}
  kafka{ 
    bootstrap_servers => "localhost:9092"
    topics => ["log-channel"]       # 消费topic  多个用逗号隔开
    auto_offset_reset => "latest"   # 从最新的偏移量开始
    type => "springboot-elk-demo"
  }
} 
filter {
  #Only matched data are send to output.
}
	
output {
  if [type] == "springboot-elk-demo" { 
    elasticsearch {
	  hosts => "localhost:9200"
	  index => "springboot-elk-demo-%{+YYYY.MM.dd}"  #指定索引名称，[type]上面input配置
    }
  } 

  stdout { 
    codec => rubydebug   #logstash控制台输出格式，默认rubydebug，可选json
  }
}

在logstash-7.8.0\bin目录下cmd执行启动命令：logstash -f logstash.conf


