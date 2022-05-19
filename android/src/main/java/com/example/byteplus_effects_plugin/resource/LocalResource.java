package com.example.byteplus_effects_plugin.resource;


/**
 * Created on 2021/10/12 10:31
 */
public class LocalResource extends com.example.byteplus_effects_plugin.resource.BaseResource {

    private final String path;

    public LocalResource(String name, String path) {
        super(name);
        this.path = path;
    }

    @Override
   void asyncGetResource() {
        resourceListener.onResourceSuccess(this, new ResourceResult(path));
    }

    @Override
    ResourceResult syncGetResource() {
        return new ResourceResult(path);
    }

    @Override
    public void cancel() {
        throw new IllegalStateException("no cancel for local resource");
    }
}
