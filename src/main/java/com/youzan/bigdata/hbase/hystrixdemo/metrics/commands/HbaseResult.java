package com.youzan.bigdata.hbase.hystrixdemo.metrics.commands;

public class HbaseResult<T> {
    private boolean success;
    private T result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public HbaseResult(boolean success, T result) {
        this.success = success;
        this.result = result;
    }
}
