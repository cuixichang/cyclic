package com.cxcmomo.cyclic.cyclic.param;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ClassName: InitParam 参数初始化 <br/>
 * date: 2019-3-22 11:17:46 <br/>
 *
 * @author cuixichang
 * @version
 * @since JDK 1.8.0
 */
public class InitParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private long initDateMillisecond;

    /**
     * 重置时间周期/秒
     */
    private long resetSecond = 86400;

    /**
     * 周期排列
     */
    private List<Frequency> frequency;

    /**
     * 计时器操作上限(总计)
     */
    private int upperLimit;

    /**
     * 达到最大操作次数时候的提示信息(总计)
     */
    private String upperLimitHint = "统计操作次数已达上限";

    /**
     * 独立操作最大次数
     */
    private int singleUpperLimit;

    /**
     * 类型操作上限提示
     */
    private String singleLimitTip = "该操作次数已达上限";

    private String title;

    private String successMessage;

    public InitParam(Long initDateMillisecond, List<Frequency> frequency, int upperLimit, int singleUpperLimit, String title, String successMessage) throws Exception {
         this.InitParam(initDateMillisecond,frequency,upperLimit,this.upperLimitHint,singleUpperLimit, this.singleLimitTip,title,successMessage, this.resetSecond);
    }

    public InitParam(Long initDateMillisecond, List<Frequency> frequency, int upperLimit, int singleUpperLimit, String title, String successMessage, long resetSecond) throws Exception {
        this.InitParam(initDateMillisecond,frequency,upperLimit,this.upperLimitHint,singleUpperLimit, this.singleLimitTip,title,successMessage,resetSecond);
    }

    public InitParam(Long initDateMillisecond, List<Frequency> frequency, int upperLimit,
                     String upperLimitHint, int singleUpperLimit, String singleLimitTip, String title, String successMessage, long resetSecond) throws Exception {
        this.InitParam(initDateMillisecond,frequency,upperLimit,upperLimitHint,singleUpperLimit,singleLimitTip,title,successMessage, resetSecond);
    }

    private void InitParam(Long initDateMillisecond, List<Frequency> frequency, int upperLimit,
                           String upperLimitHint, int singleUpperLimit, String singleLimitTip, String title, String successMessage, long resetSecond) throws Exception {

        if(initDateMillisecond == 0){
            this.initDateMillisecond = System.currentTimeMillis();
        }else {
            this.initDateMillisecond = initDateMillisecond;
        }

        if(frequency == null || frequency.size()==0){
            throw new Exception("Frequency周期排列异常，请检查");
        }else {
            orderFrequency(frequency);
            long cooling = 0L;
            for (Frequency f:frequency){
                if(cooling > f.getCooling()){
                    throw new Exception("Frequency周期排列异常，请检查");
                }else {
                    cooling = f.getCooling();
                }
            }
            this.frequency = frequency;
        }

        if(upperLimit <0){
            throw new Exception("UpperLimit计数器配置异常，请检查");
        }else {
            this.upperLimit = upperLimit;
        }

        if(singleUpperLimit < 0){
            throw new Exception("SingleUpperLimit计数器配置异常，请检查");
        }else {
            this.singleUpperLimit = singleUpperLimit;
        }


        if(resetSecond <= 0){
            throw new Exception("配置重置时间周期信息异常，请检查");
        }else {
            this.resetSecond = resetSecond;
        }

        this.upperLimitHint = upperLimitHint;
        this.singleLimitTip = singleLimitTip;
        this.title = title;
        this.successMessage = successMessage;
    }

    public long getInitDateMillisecond() {
        return initDateMillisecond;
    }

    public long getResetSecond() {
        return resetSecond;
    }

    public List<Frequency> getFrequency() {
        return frequency;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    public String getUpperLimitHint() {
        return upperLimitHint;
    }

    public int getSingleUpperLimit() {
        return singleUpperLimit;
    }

    public String getSingleLimitTip() {
        return singleLimitTip;
    }

    public String getTitle() {
        return title;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    private void orderFrequency(List<Frequency> frequency){
        if(frequency==null || frequency.size()==0){
            frequency = null;
        }else {
            Collections.sort(frequency,new Comparator<Frequency>(){
                @Override
                public int compare(Frequency arg0, Frequency arg1) {
                    return String.valueOf(arg0.getNum()).compareTo(String.valueOf(arg1.getNum()));
                }
            });
        }
    }
}
