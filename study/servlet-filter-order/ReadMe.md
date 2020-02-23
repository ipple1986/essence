* web.xml不允许配置servlet相同的urlPattern,并且不能包含*匹配（切忌使用/,造成死循环）
* servlet init方法只在第一次调用或容器启动时触发一次(设置loadOnStart=1可以提供实例)
* servlet destroy方法随容器关闭销毁
* servlet是单实例多线程， servletContext单例，requestDiaptcher多实例

* filter在容器启动时触发init一次，容器关闭时触发destroy方法
* filter可以配置urlPatter可以使用/*，匹配所有请求路径
>> 总结
* 启动：容器启动所有Filter无序init实例，再实例化loadOnStart=1的Servlet.init()
* 调用时：按配置的filter顺序，执行每个doFilter(),再执行匹配的servlet.service()方法
>> 区别
* filter在容器时实始化，servlet可选提前实始化，也可以在第一次调用时，实例
* filter对请求进行拦截，servlet负责接受请求与响应请求

需研究tomcat源码
* * filter 1个Tomcat实例只创建1个，servlet 1个Tomcat实例，针对不同发布war包创建每一个servlet实例

```puml
@startuml
autonumber "<b>[000]"
Bob -> Alice : Authentication Request
Bob <- Alice : Authentication Response

autonumber 15 "<b>(<u>##</u>)"
Bob -> Alice : Another authentication Request
Bob <- Alice : Another authentication Response

autonumber 40 10 "<font color=red><b>Message 0  "
Bob -> Alice : Yet another authentication Request
Bob <- Alice : Yet another authentication Response

@enduml

```
```puml
@startuml

actor Bob #red
autonumber "<b>[000]"
Bob -> Alice : Authentication Request
Bob <- Alice : Authentication Response

autonumber 15 "<b>(<u>##</u>)"
Bob -> Alice : Another authentication Request
Bob <- Alice : Another authentication Response

autonumber 40 10 "<font color=red><b>Message 0  "
Bob -> Alice : Yet another authentication Request
Bob <- Alice : Yet another authentication Response

@enduml
```

```puml
@startuml
Object <|-- ArrayList

Object : equals()
ArrayList : Object[] elementData
ArrayList : size()

Object <|.. DemoClass


class DemoClass {
- String privateStrField
+ publicIntField:Integer
#{method}void protectedMethod()
~ void packageMethod()
# {abstract}void abstractProtectedMethod()
+ {static} int publicStaticIntField
}

note as N1
 Note As Format
end note
DemoClass .. N1
note top of DemoClass :note top of Demo Class
note left of DemoClass
    In <color:#ff0000>Java</font>,<size:18>Object</size> is the <b><u>super</u> class</b> for <i>every</i> Class.
    new <s>Line</s>
   
end note

@enduml
```
