<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Cors Demo</title>
</head>
<script type="text/javascript">
// reference : https://blog.csdn.net/sinat_39430615/article/details/78079316
window.baidu = {
    data : {},
    sug : function(jsonData){
        // save data
        this.data = jsonData;
    }
}
JSonPDatas = {
    sugurl : "http://suggestion.baidu.com/su?wd=#content#&cb=window.baidu.sug",
    scriptDom : null,
    getDatas : function(q){
            q = typeof q =='undefined' && 'nginx'|| q;
            var sugurlTemp = this.sugurl.replace("#content#", q);
            var scriptDom = document.createElement("script");
            scriptDom.src = sugurlTemp;
            var isReturn = false;
            scriptDom.onload=function(d){
            alert(d + "=="+ (typeof d))
                //isReturn = true;
                document.getElementsByTagName("head")[0].removeChild(scriptDom);
            }
            document.getElementsByTagName("head")[0].appendChild(scriptDom);
            //while(!isReturn){}
            return this.data;
    }
}
</script>

<body>

<h3>JSonP Demo </h3>
<input type="button" onclick="alert(JSON.stringify(JSonPDatas.getDatas()))" value="getJSonP Data From Baidu JSONP api"  />

<h3>JSonP Demo </h3>


<h3>JSonP Demo </h3>


</body>
</html>