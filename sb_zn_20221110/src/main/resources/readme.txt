
!!!!注意！！！！！
	不支持带有请求体的GET请求，因为http请求使用HttpUrlConnection实现，
	发送带有请求体的GET请求会自动转为POST请求，所以GET请求不要有请求体
	
------------------使用说明----------------------
1 支持配置多域名，通过不同域名访问不同端口号的应用
	如  z1.com 访问 http://localhost:8888/	
	    z2.com 访问 http://localhost:9999/
	    
	    	