package com.cxcmomo.cyclic.support;

import com.cxcmomo.cyclic.cyclic.Actuator;
import com.cxcmomo.cyclic.cyclic.param.InitParam;
import com.cxcmomo.cyclic.cyclic.structure.StructureObj;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:CyclicService <br/>
 * Date: 2019/7/9 14:30 <br/>
 *
 * @author 崔希昌
 * @version 1.0
 * @see
 * @since JDK 1.8.0
 */
public class CyclicService extends Actuator {

    @Override
    public Object orgMessage(boolean status, StructureObj xmlStructureCache) {
        Map<String,String> ret = new HashMap<>();
        if(status){
            int m = xmlStructureCache.getAllOvertop();
            int m2 = xmlStructureCache.getAllAccumulate();
            int l = xmlStructureCache.getSingleOvertop();
            int l2 = xmlStructureCache.getSingleAccumulate();
            String message = xmlStructureCache.getOrgMessage();
            int a = (m-m2) < (l-l2) ? (m-m2):(l-l2);
            ret.put("code","0");
            message = MessageFormat.format(message,a);
            ret.put("message",message);
           return ret;
        }else {
            long s = xmlStructureCache.getCoolingSecond();
            ret.put("code","-1");
            String message = xmlStructureCache.getOrgMessage();
                 s = s/1000;
                 s = s == 0?1:s;
                   message = MessageFormat.format(message,s+"秒");
            ret.put("message",message);
            return ret;
        }
    }

    public CyclicService(InitParam initParam, String xml) throws Exception {
        super(initParam, xml);
    }


}
