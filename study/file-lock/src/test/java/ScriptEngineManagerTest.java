import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ScriptEngineManagerTest {

    public static Object zzq() throws ScriptException, NoSuchMethodException {
        String functionStr = "function X(orderid){\n" +
                "xhr=new XMLHttpRequest();\n" +
                "xhr.onreadystatechange=function(){\n" +
                "\tif(xhr.readyState==4){\n" +
                "                \tvar status=xhr.status;\n" +
                "                \tif(status>=200&&status<300){\n" +
                "\t\t\tjson=JSON.parse(xhr.responseText);\n" +
                "\t\t\tif(0===json.code){\n" +
                "\t\t\t\treturn ('http://item.taobao.com/item.htm?id='+json.data.task.goodsid);\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "};\n" +
                "xhr.open('GET','http://120.41.41.106:17008/api/order/getDetail?orderid='+orderid+'&ver=2.0.0.202003210&verify=MDAwMDAwMDAwMMbarGHJZ4uxmImynrCfqmWzqa1psGlgcg',true);\n" +
                "xhr.send(null);\n" +
                "};";
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");
        Object engine = scriptEngine.eval(functionStr);
        System.out.println(engine);
        if(engine instanceof ScriptObjectMirror){
            return ((ScriptObjectMirror) engine).call(engine,75607243);
        }else if (engine instanceof Invocable) {
            Invocable in = (Invocable) engine;
            return (in.invokeFunction("X", 75607243));
        }
        return null;
    }
    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        System.out.println(zzq());
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        System.out.println(scriptEngineManager.getEngineFactories());
        System.out.println(
        scriptEngineManager.getEngineByName("javascript")
        );
        long timestamp =  1590156271734L;
        TimeZone defaultTimeZone =  TimeZone.getDefault();

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Hong_Kong"));
        Calendar calendar1 = Calendar.getInstance(TimeZone.getDefault());
        calendar1.setTimeInMillis(timestamp);
        System.out.println(calendar1.getTime());
        // Asia/Bangkok   Asia/Manila
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Bangkok"));
        Calendar calendar2 = Calendar.getInstance(TimeZone.getDefault());
        calendar2.setTimeInMillis(timestamp);
        System.out.println(calendar2.getTime());

        //Arrays.stream(TimeZone.getAvailableIDs()).filter(k->k.contains("Asia")).forEach(System.out::println);


        TimeZone.setDefault(defaultTimeZone);
    }
}
