package com.nowcoder.community.entity;

public class Page {

    //当前页码
    private int current=1;
    //页面总数
    private int rows;
    //显示上限
    private int limit=10;
    //查询路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>=1)
        {
            this.current = current;
        }

    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows>=1)
        this.rows = rows;
    }

    public int getLimit() {

        return limit;
    }

    public void setLimit(int limit) {
        if(limit>=1 && limit<=100)
        this.limit = limit;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

//    获取当前页的起始页
    public int getOffest()
    {
        return (current-1) * limit;
    }

    /**
     * 获取总页数
     *
     *
     */
    public int getTotal()
    {
        if(rows%limit==0)
            return rows/limit;
        else
            return rows/limit+1;

    }


    /**
     * 获取起始页
     *
     *
     */
    public int getFrom()
    {
        return current >2 ? current-2 : current;
    }

    /**
     * 获取结束页
     *
     *
     */
    public int getTo()
    {
        return current <getTotal()-2 ? current+2 : current;
    }

}
