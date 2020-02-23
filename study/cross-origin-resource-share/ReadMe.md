>> CORS 跨源资源共享
* Ajax 跨域 
* Cookies 跨域
* IFrame 跨域

同源策略：协议，域名，端口       同时相同
注：http/https不同源，二级域名不同也是不同源

例子
JSonP : 局限于网页加载时，使用<script>段进行调用xxx?callback=fk，返回数据调用本地fk(data)取数据
调用必应提供的jsonp接口,window.bing.sug本地js方法，q为查询参数
https://api.bing.com/qsonhs.aspx?type=cb&q=nginx&cb=window.bing.sug

IFrame的hash/window.name/postMessage Api

