# 对比JQuery/Vue方式
例子
```text
请求一个数据列表，然后在页面上显示
```
jQuery方式
```text
<ul class="js_items"></ul>
<script>
$(function(){
    $.get("http://example.com/items.json").then(function(data){
        $itemUl = $(".js_items");
        if(!data.items.length){
            var $noItems = $("li");
            $noItems.text("Sorroy,There are no items.");
            $itemUl.append($noItems);   
        }else{
            data.items.forEach(function(item){
                var $newItem = $("li");
                $newItem.text(item);
                if(item.include("blue")){
                    $newItem.addClass("is-blue")
                }
                $itemUl.append($newItem);        
            });
        }
    })
});
</script>
```
Vue方式(视图逻辑与业务逻辑分离)
```text
<ul class="js_items">
    <li v-if ="!items.length">Sorry,There are no items.</li>
    <li v-for ="item in items" :class="{'is-blue':item.include('blue') }" >{{ item }}</li>
</ul>
<script>
new Vue({
    el:'.js_items',
    data:{
        items:[]
    },
    created(){
        fetch("http://example.com/items.json").then( (res) => { res.json() })
                .then( (data) = > { this.items = data.items ;}
    }
});
</script>

```
添加Webpack加载器vue-loader 和vue-cli
```text
npm install vue-loader
npm install --global webpack
```
```text
npm install --global vue-cli
vue init webpack //安装webpack+vue-load,输入项目名，Vue构建standalone 等信息


```

例子：根据时间判断显示早上，下午，晚上
```text
<div id="app">
    <p v-if="isMorning">Good morning!</p>
    <p v-if="isAfternoon">Good afternoon!</p>
    <p v-if="isEvening">Good evening!</p>
</div>
<script>
var hours = new Date().getHours();
new Vue({
    el: '#app',
    data: {
        isMorning: hours < 12,
        isAfternoon: hours >= 12 && hours < 18,
        isEvening: hours >= 18
    }
});
</script>
```
等价形式--简单版本
```text
<div id="app">
    <p v-if="hours < 12">Good morning!</p>
    <p v-if="hours >= 12 && hours < 18">Good afternoon!</p>
    <p v-if="hours >= 18">Good evening!</p>
</div>
<script>
new Vue({
    el: '#app',
    data: {
        hours: new Date().getHours()
    }
});
</script>
```
模板视图逻辑，使用指令v-if/v-for和插入符{{}}来定义视图逻辑
```text
<div id="app">
<p>Hello, {{ greetee }}!</p>
</div>
<script>
new Vue({
    el: '#app',
    data: {
        greetee: 'world'
    }
});
</script>
结果显示：
<div id="app">
<p>Hello, world!</p>
</div>
```
v-else指令
```text
<div id="app">
    <p v-if="path === '/'">You are on the home page</p>
    <p v-else>You're on {{ path }}</p>
</div>
<script>
new Vue({
    el: '#app',
    data: {
        path: location.pathname
    }
});
</script>
```
data字段定义在模板上显示的数据项，除了传递数字/字符，还可以是其他数据，如数组
```text
<div id="app">
    <p>The second dog is {{ dogs[1] }}</p>
    <p>All the dogs are {{ dogs }}</p>
</div>
<script>
new Vue({
    el: '#app',
    data: {
        dogs: ['Rex', 'Rover', 'Henrietta', 'Alan']
    }
});
</script>
结果输出：
The second dog is Rover
All the dogs are [ "Rex", "Rover", "henrietta", "Alan" ]
```
v-if与v-show对比,在更新频繁的情况下使用v-show得到更好性能，以及图片预加载v-show先隐式下载再显示
```text
<div v-show="true">one</div>
<div v-show="false">two</div>
结果输出
<div>one</div>
<div style="display: none">one</div>
```
另外一个例子
```text
<div id="app">
    <div v-show="user">//换成v-if就不会执行user.name这个不存在属性，些例子会报错
        <p>User name: {{ user.name }}</p>
    </div>
</div>
<script>
new Vue({
    el: '#app',
    data: {
        user: undefined
    }
});
</script>
```
Vue if多条件判断
```text
<div v-if="state === 'loading'">Loading…</div>
<div v-else-if="state === 'error'">An error occurred</div>
<div v-else>…our content!</div>
```
v-for:模板中迭代数组，对象
```text
<div id="app">
    <ul>
        <li v-for="dog in dogs">{{ dog }}</li>
    </ul>
</div>
<script>
new Vue({
    el: '#app',
    data: {
        dogs: ['Rex', 'Rover', 'Henrietta', 'Alan']
    }
});
</script>
结果输出：
<div id="app">
    <ul>
        <li>Rex</li>
        <li>Rover</li>
        <li>Henrietta</li>
        <li>Alan</li>
    </ul>
</div>
```
```text
<div id="app">
    <ul>
        <li v-for="(rent, city) in averageRent"> //注意！！！(value，key) in object
            The average rent in {{ city }} is ${{ rent }}
         </li>
    </ul>
</div>
<script>
new Vue({
Looping in Templates  |  11
    el: '#app',
    data: {
        averageRent: {
            london: 1650,
            paris: 1730,
            NYC: 3680
        }
    }
});
</script>
```
迭代0到9 
```text
<div id="app">
    <ul>
        <li v-for="n in 10">{{ n }}</li>
    </ul>
</div>
<script>
new Vue({
    el: '#app'
});
</script>
```
绑定参数,即修改属性的值
```text
<div id="app">
    <button v-bind:type="buttonType">Test button</button>
</div>
<script>
new Vue({
    el: '#app',
    data: {
        buttonType: 'submit'
    }
});
</script>
结果 输出：
<button type="submit">Test button</button>
```
```text
<div id="app">
    <button v-bind:disabled="buttonDisabled">Test button</button>
</div>
<script>
new Vue({
    el: '#app',
    data: {
        buttonDisabled: true
    }
});
</script>
```
可省略v-bind前缀，上面等价于下面,但要保持统一
```text
<div id="app">
    <button :disabled="buttonDisabled">Test button</button>//此处省略 v-bind 前缀
</div>
<script>
new Vue({
    el: '#app',
    data: {
        buttonDisabled: true
    }
});
</script>
```
响应
```text
<div id="app">
<p>{{ seconds }} seconds have elapsed since you opened the page.</p>
</div>
<script>
new Vue({
    el: '#app',
    data: {
        seconds: 0
    },
    created() {
        setInterval(() => {//每隔1秒修改data.seconds的值 ，以达到更新模板视图，
        //如果在绑定参数时，这里 加上 this.buttonDisabled = !this.buttonDisabled，同样可以达到更新作用
        this.seconds++;
        }, 1000);
        }
    });
</script>
```
当data对象对修改，Vue会被通知去更新模板，原理：通过对Data对象的属性的get/set做代理，原理如下
```text
//假设这个是data 对象
const data = {  
    userId: 'abc'
}
//存储模板的数据
const storeData = {};
storeData.userId = data.userId;
 
Object.denfineProperty(data,'userId'{
        get(){//渲染时调用get
            return storeData.userId；
        },
        set(value){//被修改时触发这个
            console.log("Data对象的属性被 修改了");///数组方法.splice更新data对象属性也会触发
            storeData.userId = value;
        }
        configurable: true,
        enumerable: true
});
```
陷阱：

1.由于set/get方法是在初始化时产生的，所以初始化后，新加属性将不会响应式
```text
const vm = new Vue({//先实例
    data: {
        formData: {
            username: 'someuser'
        }
    }
});
//后添加name属性
vm.formData.name = 'Some User';
```
如何破？
```text
方式一：提前加undefined占位
formData: {
    username: 'someuser',
    name: undefined
}
方式二：常用，Object.assgin()方法，创建新的添加了代码属性formData
vm.formData = Object.assgin({},vm.formData,{name:'Some User'....多属性在这里设置});
方式三：使用Vue.set()方法
Vue.set(vm.formData,'name'，'Some User')

```
2.数组用索引修改值 ,不响应
```text
const vm = new Vue({
    data: {
        dogs: ['Rex', 'Rover', 'Henrietta', 'Alan']
    }
});
vm.dogs[2] = 'Bob'//修改不更新模板
```
如何破？
```text
方式一：数组splice
vm.dogs.splice(2,1,'Bob')
方式二：Vue.set()
Vue.set(vm.dogs,2,'Bob')
```
3.设置数组长度
```text
使用splice(newLength)
```
上面都是单向绑定，双向数据绑定呢？下面例子你怎么修改输入，最终都还是保持着初始化值“initial value”
```text
<div id="app">
    <input type="text" v-bind:value="inputText">
    <p>inputText: {{ inputText }}</p>
</div>
<script>
new Vue({
    el: '#app',
    data: {
        inputText: 'initial value'
    }
});
</script>
```
使用v-model