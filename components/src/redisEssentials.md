# Redis Essentials


### 1.入门（婴儿的脚步）
```text
本章内容
* 下载安装redis
* 基本数据类型string,list,hash
* 使用redis-cli/nodejs 与 redis交互 
```
```test
* 读写快  Redis是一个非关系ＫＶ内存数据库
* 丰富数据类型 string/list/hash/set/zset/bitmaps/hyperloglogs 比特位映射/活跃日志
* 持久化  以二进制快照文件或日志方式将内存数据保存到磁盘
* 主从复制
* 客户端分区
* 支持ＬＵＡ脚本，扩展Redis命令
* Key可设有效期
* 事务，发布订阅
```
#### 安装 
```bash
$ curl -O http://download.redis.io/releases/redis-3.0.2.tar.gz
$ tar xzvf redis-3.0.2.tar.gz
$ cd redis-3.0.2
$ sudo make install
```
####  简单的交互
```bash 
$ redis-server //启动standalone单机模式，默认端口6379
```
```bash 
$ redis-cli
127.0.0.1:6379> set hello word
127.0.0.1:6379> get hello
127.0.0.1:6379> help set
127.0.0.1:6379> keys h*
```
#### 安装nodeJs 和 redis支持库
```bash 
//安装完nodejs后，npm包管理器也安装了
$ mkdir redis-essentials && cd redis-essentials
$ npm install redis //当前目录下生成node_modules文件夹保存redis的nodejs客户端支持库
```
#### JavaScript基本语法
```text
* 变量定义 var myAge = 25;
* 注释 单行// 多行/* comments here */
* 条件语句 if (myAage > 20 ){ console.log("myAge is more than 20");} esle{...}
* 函数定义 function add(a,b){return a+b;}
* 函数执行 var sum = add(1+3);
* 函数可以作为类使用，有自己的属性 function Car(carName){ this.carName = carName }
* 函数可以作为类使用，有自己的方法 Car.prototype.run = function(){...}
* 创建类实例 Car c = new Car("toyota");
* 调用类实例方法 c.run()
* 数组与对象 var myArray = []; var myObject = {}
* Js中的回调(匿名function作为函数的参数)，var myCourse = ["math","english"]; myCourse.forEach(function(name,index){})
```
#### nodejs与redis交互
hello.js
```text hello.js
var redis = require("redis");
var client = redis.createClient();
client.set("hello1","world1");
client.get("hello1",redis.print);
client.quit();
```
```bash
$ node hello.js
```
#### 基本数据类型（string/list/hash）
#####  1.Strings
```text
* 可存储的值：int float text(xml/html/json/rawText) binaryData(imag/audio/video) maxSize:512M 
* 使用场景：缓存机制（set/get/mset/mget）自动失效的缓存机制（setex/expire/expireat）计数器（incr/decr/incrby/decrby/incrbyfloat）
```
与redis-cli交互
```bash
mset k1 v1 k2 v2
mget k1 k2
expire k1 ttlInSeconds //key存在返回1,否则返0
ttl k1 // -2 key不在了 -1 key存在,未设置有效期,其他值表示还剩余多少有效时间 
incr k3// +1
increby k4 10 //+10
incrbyfloat k5 2.4 // +2.4
```
对文章投支持/反对票（与nodejs交互）

模型：
```text
article:<id>:headline  headlineContent
article:<id>:votes  int
```
初始化数据 
```bash
$ redis-cli
> set article:1:headline "computer science"
> set article:2:headline "business english"
> set article:3:headline "highlevel math"
```
article-popularity.js
```text
var redis = require("redis");
var client = redis.createClient();
function upVote(id){
    var key = "article:" + id + ":votes";
    client.incr(key);
}

function downVote(id){
    var key = "article:" + id + ":votes";
    client.decr(key);
}

function showResult(id){
    var headlineKey = "article:" + id + ":headline";
    var voteKey = "article:" + id + ":votes";
    clieng.mget([headlineKey,voteKey],function(err,replies){
        console.log(replies[0] + "  " + replies[1]);
    });
}
//invoke here
upVote(1);
upVote(1);
upVote(1);
downVote(1);

upVote(2);
downVote(2);
upVote(2);

upVote(3);

showResult(1);
showResult(2);
showResult(3);

redis.quit()
```
运行
```bash
$ node article-popularity.js
```
#####  2.Lists
```text
* 存储字符类型的值
* 用链表LinkedList实现，可以充当 简单集合、队列（有用于阻塞的命令）、栈。
* 长度最多2^32-1
* 队头队尾操作（插入，删除）都是0(1)时间复杂度，但中间元素查找为O(N)
* 能被编码与内存优化的情况： list-max-ziplist-entries :元素数少于这个数据，list-max-ziplist-value： 每个元素的大小，小于这个配置（以字节为单位）
* 使用场景：事件队列，用户最近发表的文章
```
与redis-cli交互
```bash
lpush books "Clean Code"
lpush books "Code Complete"
lpush books "Peopleware"
lindex books 1
lindex books -1
lindex books -2
llen books
lrange bookes 1 -1
lpop books
rpop books
lrange 0 -1
```
实现一般的队列系统（nodejs交互）
queue.js
```text
function Queue(queueName,redisClien){
    this.queueName = queueName;
    this.queueKey = "queue:" + queueName;
    this.redisClient = redis.Client;
    this.timeout = 0; //无超时时间设置
}
Queue.prototype.size = function(callback){
    this.redisClient.llen(this.queueKey,callback);
}
Queue.prototype.push = function(data){
    this.redisClient.lpush(this.queueKey,data);
};
Queue.prototype.pop = function(callback){
    this.redisClient.brpop(this.queueKey,this.timeout,callback);
}
exports.Queue = Queue;
```
producer.js
```text
var redis = require("redis");
var client = redis.createClient();
var queue = require("./queue");
var logsQueue = new queue.Queue("logs",client);
var MAX = 5;
for(var i=0;i<MAX;i++){
    logsQueue.push("HelloWord #"+i);
}
console.log("create " + MAX +" logs");
client.quit();
```
consumer.js
```text
var redis = require("redis");
var client = redis.createClient();
var queue = require("./queue");
var logsQueue = new queue.Queue("logs",client);
function logMessages(){
        logsQueue.pop(function(err,replics){
            var ququeName = replies[0];
            var message = replies[1];
            console.log("Get log message: " + message);
            logsQueue.size(function(err,size){
                console.log("Size:" + size +" logs left"
            })
        });
        logMessages();
}
logMessages();
```
运行
```bash
$ node producer.js
$ node consumer.js
```
注意
```text
* 上面例子队列记为Q1，它是一个不可靠的队列系统
* 通过添加一个辅助队列 记为Q2
* 通过rpoplpush原子命令，从Q1读取放于Q2,最终返回元素
* 处理完元素后，再将元素从Q2中移除
```
##### Hashs
```text
* 用于存储对象{k1:v1,k2:v2...}
* 用链表LinkedList实现，可以充当 简单集合、队列（有用于阻塞的命令）、栈。
* 能被编码与内存优化的情况： hash-max-ziplist-entries :元素数少于这个数据，hash-max-ziplist-value： 每个元素的大小，小于这个配置（以字节为单位）
* Hash内部可以是ziplist或hash表。当ziplist时，整数保存为原来整数，内存优化，但查找不是常量时间复杂度；当为hash时整型变成字符串类型，查找快，但内存不能被优化
```
与redis-cl交互
```bash
redis-cli
hset book "title" "Clean Code"
hmset book "year" 2015 "author" "kevin"  "buyers" 1000
hincrby book "buyers" 3
hget book "title"
hmget book "title" "author"
hdel book "buyers"
hgetall book
hkeys book
hvals book
```
改进版 支持/反对投票系统（使用hash）
hash-vote-system.js
```text
var redis = require("redis");
var client = redis.createClient();

function saveArticle(id,title,author,link){
    client.hmset("link:"+id,"author",author,"title",title,"link",link,"votes",0);
}

function upVote(id){
    client.hincrby("link:"+id,"votes",1);
}

function downVote(id){
    client.hdecrby("link:"+id,"votes",-1);
}

function showDetails(id){
    client.hgetall("link:"+id,function(error,replics){
            var title = replies["title"];
            var author = replies["author"];
            var votes = replies["votes"];
            var link = replies["link"];
            console.log("Title:" +  title);
            console.log("Author:" +  author);
            console.log("Votes:" +  votes);
            console.log("Link:" +  link);
            console.log("--------------------");
    });
}

saveArticle(1,"Hello Redis","Kevin","https://redis.io");
upVote(1);
upVote(1);
downVote(1);

saveArticle(2,"Redis in action","KevinAction","https://redis.io.action");
upVote(2);
upVote(2);

showDetails(1);
showDetails(2);

client.quit();
```
运行
```bash
$ node hash-vote-system.js
```
注意
```text
* hgetall 如果字段太多，消耗内存导致redis变慢
* 使用hscan命令，直到返回0游标，才查询所有字段
```

### 2.高级数据类型（获得黑带）

```text
本章内容
* 高级数据类型set,zset,bitmap,hypeloglogs
* 使用redis-cli/nodejs 与 redis交互 
```
##### 4.Sets
```text
* 无重复无序集合，内部使用hash表存储，添删改O(1)
* 长度最多2^32-1
* 当所有元素都是整型，并少于set-max-intset-entiries时，内存可被优化 
* 使用场景：数据过滤，数据分组，成员检查等
```
与redis-cli交互
```bash
redis-cli
> sadd s1 "a"  "b" "c" //返回参数个数
> sadd s2 "c" "d" "e" 
> sdiff s1 s2 //返回s1中仅在s1中存在的元素   "a" "b"
> sdiff s2 s1 //"d","e"
> sinter s1 s2 // 返回共同元素 "c"
> sunion s1 s2 //返回并集 "a" "b" "c" "d" "e"
> srem s1 "b" //从s1中删除"b" 
> srangemember s1 //随机返回一个元素
> smembers s1 //返回元素
> sismember s1 "a" //判断"a"是否存在s1中 1在，0不在
> scard s1 //返回集合大小 2
```
交易跟踪系统（nodejs）

deal-metric.js
```text
var redis = require("redis");
var clien = redis.createClient();

function markDealAsSent(dealId,userId){
    client.sadd(dealId,userId);
}

function sentDealIfNotExist(dealId,userId){
    client.sismember(dealId,userId,function(err,reply){
        if(reply){
            console.log("has sent !!!");
        }else{
            console.log("sent dealId:" + dealId + " to userId " + userId); 
            markDealAsSent(dealId,userId);
        }
    });
}
function showUsersThatReceviceAllDeals(dealIds){
    client.sinter(dealIds,function(error,replies){
        console.log("Users:"+replies + "recevice all deals :" + dealIds);
    });
}

function showUsersThatAtLeastReceviceOneOfThatDeals(dealIds){
    client.sunion(dealIds,function(error,replies){
        console.log("Users:"+replies + "recevice at least one of deals :" + dealIds);
    });
}

markDealAsSent("deal1","user1");
markDealAsSent("deal1","user2");

markDealAsSent("deal2","user1");
markDealAsSent("deal2","user2");

sentDealIfNotExist("deal1","deal1");
sentDealIfNotExist("deal1","deal2");
sentDealIfNotExist("deal1","deal3");
sentDealIfNotExist("deal2","deal3");

showUsersThatReceviceAllDeals(["deal1","deal2"]);
showUsersThatAtLeastReceviceOneOfThatDeals(["deal1","deal2"]);

client.quit();
```
运行
```bash
$ node deal-metric.js
```

##### 5.ZSets
```text
* 无重复有序集合，CRD 时间复杂度为O(log(N))
* hashtable（跳表，用于快速查找） + ziplist(zset-max-ziplist-entries/zset-max-ziplist-values)压缩列表

```
