package com.cxcmomo.cyclic.cyclic.param;

import java.io.Serializable;

/**
 * ClassName: Frequency 循环周期对象 <br/>
 * date: 2019-3-22 11:17:46 <br/>
 *
 * @author cuixichang
 * @version
 * @since JDK 1.8.0
 */
public class Frequency implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 周期循环上限次数
     */
    private int num;

    /**
     * 冷却时间/秒
     */
    private long cooling;

    /**
     * 上限提示信息
     */
    private String message = "请等待 {0} 后继续该操作";

    public Frequency(int num, long cooling) throws Exception {
        if(num <=0){
            throw new Exception("周期循环上限次数需 > 0");
        }
        this.num = num;
        this.cooling = cooling;
    }

    public Frequency(int num, long cooling, String message)throws Exception {
        if(num <=0){
            throw new Exception("周期循环上限次数需 > 0");
        }
        this.num = num;
        this.cooling = cooling;
        this.message = message;
    }

    public int getNum() {
        return num;
    }

    public long getCooling() {
        return cooling;
    }

    public String getMessage() {
        return message;
    }
}
