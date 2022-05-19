package com.example.byteplus_effects_plugin.resource;

import com.google.gson.annotations.Expose;

/**
 * Created on 2021/10/12 10:28
 */
public abstract class BaseResource {

    @Expose protected String name;
    protected ResourceListener resourceListener;

    public BaseResource(){}

    public BaseResource(String name){
        this.name = name;
    }

    abstract void asyncGetResource();

    abstract ResourceResult syncGetResource();

    public abstract void cancel();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public ResourceListener getResourceListener() {
        return resourceListener;
    }

    public void setResourceListener(ResourceListener resourceListener) {
        this.resourceListener = resourceListener;
    }

    public static class ResourceResult {
        public String path;
        public ResourceResult(){}
        public ResourceResult(String path){
            this.path = path;
        }

    }

    public interface ResourceListener {
        void onResourceSuccess(BaseResource resource, ResourceResult result);
        void onResourceFail(BaseResource resource, int errorCode, String msg);
        void onResourceStart(BaseResource resource);
        void onResourceProgressChanged(BaseResource resource, float progress);
    }
}
