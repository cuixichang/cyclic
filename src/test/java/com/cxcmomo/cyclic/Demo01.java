package com.cxcmomo.cyclic;

import com.cxcmomo.cyclic.cyclic.Actuator;
import com.cxcmomo.cyclic.cyclic.param.Frequency;
import com.cxcmomo.cyclic.cyclic.param.InitParam;
import com.cxcmomo.cyclic.redis.RedisUtil;
import com.cxcmomo.cyclic.support.CyclicService;
import redis.clients.jedis.Jedis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClassName:Demo01 <br/>
 * Date: 2019/7/15 23:31 <br/>
 *
 * @author 崔希昌
 * @version 1.0
 * @see
 * @since JDK 1.8.0
 */
public class Demo01 {

    private static Jedis jedis;

    private static DateFormat df;

    /**
     * 连接redis服务器
     */
    static {
        jedis= RedisUtil.getJedis();

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static void main(String[] args) throws Exception{
        String command = "null";
        if(args != null && args.length==1) command = args[0];

         if("run".equals(command)){
             System.out.println("Run the beginning");
             test();
             System.out.println("Run the end");
         }else if("clear".equals(command)){
             System.out.println("Clear the beginning");
             clean("cacheKey");
             System.out.println("Clear the end");
         }else {
             System.out.println("Command error");
         }

    }

    private static void test() throws Exception{
        String structureStr = jedis.get("cacheKey");
        List<Frequency> ss= new ArrayList<Frequency>();
        ss.add(new Frequency(1,10));
        //ss.add(new Frequency(6,120));
        //ss.add(new Frequency(4,60));
        InitParam initParam = new InitParam(df.parse("2019-03-20 20:15:41").getTime(),ss, 20,20,"SMS CACHE"
                ,"短信发送成功,剩余{0}次执行次数");

        Actuator actuator = new CyclicService(initParam,structureStr);

        Map<String,String> f =  (Map<String,String>)actuator.checkIsOperator("key");
        String code = f.get("code");
        String message = f.get("message");
        if("0".equals(code)){
            System.out.println(message);
            jedis.del("cacheKey");
            String fs = actuator.exeOperator("key");
            jedis.set("cacheKey",fs);
        }else {
            System.out.println(message);
        }
    }

    private static void clean(String key){
        jedis.del(key);
    }
}
