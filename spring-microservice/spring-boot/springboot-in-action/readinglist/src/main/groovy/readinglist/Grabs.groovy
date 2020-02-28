@Grab("h2")
@Grab("spring-boot-starter-thymeleaf")
class Grabs{}
//Groovy代码里没有关于h2/thymeleaf类的引用，cli无法识别兼容失败，无法触发自动依赖传递管理