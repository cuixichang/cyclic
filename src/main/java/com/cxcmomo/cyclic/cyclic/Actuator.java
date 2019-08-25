package com.cxcmomo.cyclic.cyclic;

import com.cxcmomo.cyclic.cyclic.param.Frequency;
import com.cxcmomo.cyclic.cyclic.param.InitParam;
import com.cxcmomo.cyclic.cyclic.structure.StructureObj;
import com.cxcmomo.cyclic.util.JSONUtils;

import java.util.*;

/**
 * 短信次数验证
 */
public abstract class Actuator {
    // Node id
    private static final String TAGENODES = "tagenodes:";
    //Cache node id
    private static final String CACHENODES = "cachenodes:";
    // Count id
    private static final String COUNT = "count";

    private abstract class MapContent{

        protected static final String title = "title";

        protected static final String cacheNumStr = "countNum";

        protected static final String cacheTimeStr = "CurrentTime";
    }

    private Map<String,Map<String,String>> xmlStructure;

    private StructureObj xmlStructureCache;

    private final static String defaulCacheTag = "0";

    private InitParam initParamCache;

    public abstract Object orgMessage(boolean status, StructureObj xmlStructureCache);

    public Actuator(InitParam initParam,String xml) throws Exception {
        if(initParam == null ){
            throw new Exception("initParam is not null");
        }
        initParamCache = initParam;
        xmlStructureCache = initObjStructure(initParam);
        initXmlStructure(xml,initParam.getTitle());
    }

    /**
     * 检查操作状态
     * @return
     * @throws Exception
     */
    public Object checkIsOperator(String key) throws Exception {
        key = CACHENODES+key.hashCode();
            xmlStructureCache.setNode(key);
        Map<String,String> countObj = xmlStructure.get(key);
        int singleUpperLimitCount = 0;
        if(countObj != null){
            //单个用户执行操作次数
            singleUpperLimitCount = Integer.valueOf(countObj.get(MapContent.cacheNumStr));
        }else{
            Map<String,String> operatorCache = new HashMap<>();
                               operatorCache.put(MapContent.cacheNumStr, String.valueOf(singleUpperLimitCount));
                               operatorCache.put(MapContent.cacheTimeStr, String.valueOf(xmlStructureCache.getCurrentTimeMillisecond()));
            xmlStructure.put(key,operatorCache);
            xmlStructureCache.setStructureStr(JSONUtils.convertToJSON(xmlStructure));
        }
            xmlStructureCache.setSingleAccumulate(singleUpperLimitCount);
        //单功能上限
        int singleOvertop = xmlStructureCache.getSingleOvertop();

        if(singleOvertop < singleUpperLimitCount){
            xmlStructureCache.setOrgMessage(initParamCache.getSingleLimitTip());
            return orgMessage(false,xmlStructureCache);
        }
        //总功能上限
        int allOvertop = xmlStructureCache.getAllOvertop();
        //总功能累计值
        int allAccumulate = xmlStructureCache.getAllAccumulate();

        if(allOvertop < allAccumulate){
            xmlStructureCache.setOrgMessage(initParamCache.getSingleLimitTip());
            return orgMessage(false,xmlStructureCache);
        }
        List<Frequency> frequency =initParamCache.getFrequency();
        return checkSendNode(frequency,xmlStructureCache.getCurrentTimeMillisecond(),xmlStructureCache.getAllAccumulate());
    }

    /**
     * 更新操作次数
     * @param key
     * @param num
     * @return
     * @throws Exception
     */
    public Object updateOperatorNum(String key, int num)throws Exception {
        key = CACHENODES+key.hashCode();
        Map<String,String> cacheNodesObj = xmlStructure.get(key);
        int operatorNum = 0;//执行计数次数
        if(cacheNodesObj != null){
            int nums = Integer.parseInt(cacheNodesObj.get(MapContent.cacheNumStr));
            //减操作
            if((nums+num) <= 0){
                xmlStructure.remove(key);
                operatorNum = nums*-1;
            }else {
                cacheNodesObj.put(MapContent.cacheNumStr, String.valueOf(nums+num));
                xmlStructure.put(key,cacheNodesObj);
                operatorNum = num;
            }
        }
        Map<String,String> countObj = xmlStructure.get(COUNT);
        int count = Integer.parseInt(countObj.get(MapContent.cacheNumStr)) + operatorNum;
                    countObj.put(MapContent.cacheNumStr, String.valueOf(count));
        xmlStructure.put(COUNT,countObj);
        String xmlStr = JSONUtils.convertToJSON(xmlStructure);
        xmlStructureCache.setStructureStr(xmlStr);
        return xmlStr;
    }

    /**
     * 更新操作状态方法
     * @param key
     * @return
     * @throws Exception
     */
    public String exeOperator(String key) throws Exception {
        key = CACHENODES+key.hashCode();
        int cacheCount = xmlStructureCache.addAllAccumulate(1);
        Map<String,String> cacheNodesObj = xmlStructure.get(key);
        Map<String,String> countObj = xmlStructure.get(COUNT);
        countObj.put(MapContent.cacheNumStr, String.valueOf(cacheCount));

        updateTageNodeObjNode();
        if(cacheNodesObj == null){
            Map<String,String> ls = new HashMap<>();
            ls.put(MapContent.cacheNumStr,"1");
            ls.put(MapContent.cacheTimeStr, String.valueOf(xmlStructureCache.getCurrentTimeMillisecond()));
            xmlStructure.put(key,ls);
        }else {
            int tageNum = Integer.parseInt(cacheNodesObj.get(MapContent.cacheNumStr));
            cacheNodesObj.put(MapContent.cacheNumStr, String.valueOf(++tageNum ));
            xmlStructure.put(key,cacheNodesObj);
        }

        String xmlStr = JSONUtils.convertToJSON(xmlStructure);
        xmlStructureCache.setStructureStr(xmlStr);
        return xmlStr;
    }

    /**
     * 重置xml次数
     * @return
     */
    public String resetXml(){
        xmlStructure =  defaultXMl(initParamCache.getTitle());
       return JSONUtils.convertToJSON(xmlStructure);
    }

    public Object checkSendNode(List<Frequency> frequency, long currentTimeMillisecond
            , int cacheCount) throws Exception {
        xmlStructureCache.setCoolingSecond(0L);
        Object obj = null;
        int frequencyIndex = frequency.size() -1 ;
        for(int i = frequencyIndex ; i > -1  ; i--) {
            Frequency tipIndex = frequency.get(i);
            int operatorTop = tipIndex.getNum();
            long writeCyclic = tipIndex.getCooling() * 1000;
            //操作上限 < 操作计数器
            if (operatorTop < cacheCount) {
                cacheCount -= operatorTop;
                obj = checkSendNode(frequency, currentTimeMillisecond, cacheCount);
            } else if (operatorTop == cacheCount) {
                long interval = getNode(operatorTop, currentTimeMillisecond);
                //等待时间 > 周期时间
                if (interval >= writeCyclic) {
                   v xmlStructureCache.setOrgMessage(initParamCache.getSuccessMessage());
                    xmlStructureCache.setCoolingSecond(0L);
                    obj =  orgMessage(true,xmlStructureCache);
                    break;
                } else {
                    //冷却时间
                    xmlStructureCache.setOrgMessage(tipIndex.getMessage());
                    xmlStructureCache.setCoolingSecond(writeCyclic - interval);
                    obj =  orgMessage(false,xmlStructureCache);
                    break;
                }
            }
        }
        //小于最小周期
        if(obj == null){
            xmlStructureCache.setOrgMessage(initParamCache.getSuccessMessage());
            obj =  orgMessage(true,xmlStructureCache);
        }

      return obj;
    }

    private StructureObj initObjStructure(InitParam initParam) throws Exception {
        StructureObj xmlStructureCache = new StructureObj();
        //执行上限
        xmlStructureCache.setAllOvertop(initParam.getUpperLimit());
        //单功能执行上限
        xmlStructureCache.setSingleOvertop(initParam.getSingleUpperLimit());
        //当前执行操作时间

        long initDateMillisecond = initParam.getInitDateMillisecond();
        long currentTimeMillisecond = System.currentTimeMillis();

        xmlStructureCache.setCurrentTimeMillisecond(currentTimeMillisecond);
        //重置时间设置
        long cycle = initParam.getResetSecond() * 1000;
        //初始化时间与当前时间比较结果
        long nodeSendInterval = currentTimeMillisecond - initDateMillisecond;
        int divide = (int) (Math.abs(nodeSendInterval) / cycle);
        long initTimeMillis;
        if(nodeSendInterval >= 0){
            initTimeMillis = initDateMillisecond + (divide * cycle);
        }else{
            initTimeMillis = initDateMillisecond - ((divide + 1) * cycle);
        }
        //缓存开始时间
        xmlStructureCache.setCacheStartTimeMillisecond(initTimeMillis);
        //周期结束时间
        xmlStructureCache.setCacheEndTimeMillisecond(initTimeMillis + cycle);
        //剩余缓存毫秒数
        long nextTimeSecond = initTimeMillis + cycle - currentTimeMillisecond;
        xmlStructureCache.setDieSecond(nextTimeSecond);
        return xmlStructureCache;
    }

    /**
     * 更新缓存记录节点
     * @throws Exception
     */
    private void updateTageNodeObjNode() throws Exception {
        List<String> tag = new ArrayList<String>();
        int num = 0;
        int upperLimitCount =  xmlStructureCache.getSingleAccumulate();

        for(Frequency frequency:initParamCache.getFrequency()){
            num = frequency.getNum();
            //当前在标签位置
            if(num != 0){
                if( upperLimitCount % num == 0){
                    tag.add(String.valueOf(frequency.getNum()));
                }
            }
        }

        if(upperLimitCount % num == 0 && upperLimitCount >= num){
            tag.add(defaulCacheTag);
        }

        for (String m:tag){
            Map<String,String> tageNodesObj = xmlStructure.get(TAGENODES+m);
            if(tageNodesObj != null){
                tageNodesObj.put(MapContent.cacheTimeStr, String.valueOf(xmlStructureCache.getCurrentTimeMillisecond()));
                xmlStructure.put(TAGENODES+m,tageNodesObj);
            }
        }
    }

    /**
     * 初始化xml结构对象
     * @throws Exception
     */
    private void initXmlStructure(String xmlStructureStr, String title) throws Exception {
            int count = 0;
            if(xmlStructureStr == null || xmlStructureStr.trim().length()==0){
                xmlStructure = defaultXMl(title);
            }else {
                xmlStructure =  JSONUtils.convertFromJSON(xmlStructureStr,Map.class);
                //遍历keys
                Set<String> keys = xmlStructure.keySet();
                for (String key:keys){
                    Map<String,String> countObj =  xmlStructure.get(key);
                    String cacheTime = countObj.get(MapContent.cacheTimeStr);
                    //计数器
                    if(COUNT.equals(key)){
                        //根据缓存时间判断xml结构是否可用
                        long tempInterval = Long.parseLong(cacheTime) -  xmlStructureCache.getCacheStartTimeMillisecond();
                        if(tempInterval < 0){
                            xmlStructure = defaultXMl(title);
                            break;
                        }else{
                            countObj.put(MapContent.cacheTimeStr, String.valueOf(xmlStructureCache.getCacheStartTimeMillisecond()));
                        }
                        //频率记录器
                    }else if(key.contains(TAGENODES)){
                        //初始化默认频率记录节点
                        if(key.equals(TAGENODES+defaulCacheTag)){
                            //篡改数据：初始化时间大于当前，修改初始化时间为当前
                            long nodeSendInterval = xmlStructureCache.getCacheStartTimeMillisecond()- Long.parseLong(cacheTime);
                            if(nodeSendInterval>0){
                                countObj.put(MapContent.cacheTimeStr, String.valueOf(xmlStructureCache.getCurrentTimeMillisecond()));
                            }
                        }else{
                            //节点数据不在有效范围内，移除节点
                            long cacheTimeMillisecond = Long.parseLong(cacheTime);
                            if(xmlStructureCache.getCacheStartTimeMillisecond() > cacheTimeMillisecond
                                    || xmlStructureCache.getCacheEndTimeMillisecond() < cacheTimeMillisecond){
                                xmlStructure.remove(key);
                            }
                        }
                        //操作记录器
                    }else {
                        long cacheTimeMillisecond = Long.parseLong(cacheTime);
                        //操作记录不在有效范围内的移除，并且记录操作记录数
                        if(xmlStructureCache.getCacheStartTimeMillisecond() > cacheTimeMillisecond
                                || xmlStructureCache.getCacheEndTimeMillisecond() < cacheTimeMillisecond){
                            Map<String,String> countNum =  xmlStructure.get(key);
                            count += Integer.parseInt(countNum.get(MapContent.cacheNumStr));
                            xmlStructure.remove(key);
                        }
                    }
                }
            }
            //统计节点更新
            Map<String,String> countNum =  xmlStructure.get(COUNT);
            String s  = countNum.get(MapContent.cacheNumStr);
            xmlStructureCache.setAllAccumulate(Integer.parseInt(s)- count);
            xmlStructureCache.setStructureStr(JSONUtils.convertToJSON(xmlStructure));
    }

    /**
     * init defaultXMl
     */
    private Map<String,Map<String,String>> defaultXMl(String title){
        Map<String,Map<String,String>> inItParam = new HashMap<>();
        Map<String,String> count = new HashMap<>();
            count.put(MapContent.cacheNumStr,defaulCacheTag);
            count.put(MapContent.cacheTimeStr, String.valueOf(xmlStructureCache.getCacheStartTimeMillisecond()));
            count.put(MapContent.title,title);
       inItParam.put(COUNT,count);
        Map<String,String> tage = new HashMap<>();
            tage.put(MapContent.cacheTimeStr, String.valueOf(xmlStructureCache.getCurrentTimeMillisecond()));
       inItParam.put(TAGENODES+defaulCacheTag, tage);
        return inItParam;
    }

    private long getNode(int nodeObj,long currentTimeMillisecond) throws Exception {
        Map<String, String> nodeTime = xmlStructure.get(TAGENODES+nodeObj);
        if(nodeTime == null){
            nodeTime = xmlStructure.get(TAGENODES+defaulCacheTag);
            if(nodeTime == null){
                throw new Exception("XML pase exception");
            }
        }
        String nodeTimeStr = nodeTime.get(MapContent.cacheTimeStr);
        if(nodeTimeStr == null){
            throw new Exception("XML pase exception");
        }
        return currentTimeMillisecond - Long.parseLong(nodeTimeStr);
    }
}
