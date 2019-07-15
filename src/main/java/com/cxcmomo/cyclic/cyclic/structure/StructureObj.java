package com.cxcmomo.cyclic.cyclic.structure;

import java.io.Serializable;

/**
 * ClassName:StructureObj <br/>
 * Date: 2019/3/22 13:59 <br/>
 *
 * @author 崔希昌
 * @version 1.0
 * @see
 * @since JDK 1.8.0
 */
public class StructureObj implements Serializable {

    private static final long serialVersionUID = 1L;

    //cache obj
    private String structureStr;

    // current node key
    private String node;

    //count All
    private int allOvertop;

    //overtop
    private int allAccumulate;

    //count bin
    private int singleOvertop;

    //overtop
    private int singleAccumulate;

    private long cacheStartTimeMillisecond;

    private long currentTimeMillisecond;

    private long cacheEndTimeMillisecond;

    //cache die time second
    private long dieSecond;

    //operator cooling Second
    private long coolingSecond;

    /**
     * 提示信息
     */
    private String orgMessage;


    public String getStructureStr() {
        return structureStr;
    }

    public void setStructureStr(String structureStr) {
        this.structureStr = structureStr;
    }

    public String getOrgMessage() {
        return orgMessage;
    }

    public void setOrgMessage(String orgMessage) {
        this.orgMessage = orgMessage;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int addAllAccumulate(int num){
        return allAccumulate += num;
    }

    public int getAllOvertop() {
        return allOvertop;
    }

    public void setAllOvertop(int allOvertop) {
        this.allOvertop = allOvertop;
    }

    public int getAllAccumulate() {
        return allAccumulate;
    }

    public void setAllAccumulate(int allAccumulate) {
        this.allAccumulate = allAccumulate;
    }

    public int getSingleOvertop() {
        return singleOvertop;
    }

    public void setSingleOvertop(int singleOvertop) {
        this.singleOvertop = singleOvertop;
    }

    public int getSingleAccumulate() {
        return singleAccumulate;
    }

    public void setSingleAccumulate(int singleAccumulate) {
        this.singleAccumulate = singleAccumulate;
    }

    public long getDieSecond() {
        return dieSecond;
    }

    public void setDieSecond(long dieSecond) {
        this.dieSecond = dieSecond;
    }

    public long getCoolingSecond() {
        return coolingSecond;
    }

    public void setCoolingSecond(long coolingSecond) {
        this.coolingSecond = coolingSecond;
    }

    public long getCurrentTimeMillisecond() {
        return currentTimeMillisecond;
    }

    public void setCurrentTimeMillisecond(long currentTimeMillisecond) {
        this.currentTimeMillisecond = currentTimeMillisecond;
    }

    public long getCacheStartTimeMillisecond() {
        return cacheStartTimeMillisecond;
    }

    public void setCacheStartTimeMillisecond(long cacheStartTimeMillisecond) {
        this.cacheStartTimeMillisecond = cacheStartTimeMillisecond;
    }

    public long getCacheEndTimeMillisecond() {
        return cacheEndTimeMillisecond;
    }

    public void setCacheEndTimeMillisecond(long cacheEndTimeMillisecond) {
        this.cacheEndTimeMillisecond = cacheEndTimeMillisecond;
    }
}
