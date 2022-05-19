package com.example.byteplus_effects_plugin.resource;

import android.content.Context;
import android.util.Log;

import com.example.byteplus_effects_plugin.resource.network.NetworkManager;

import java.lang.ref.WeakReference;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2021/11/15 19:55
 */
public class ResourceManager implements BaseResource.ResourceListener {
    public static final String TAG = "ResourceManager";
    public static final int MAX_LOADING_RESOURCE = 5;

    private final com.example.byteplus_effects_plugin.resource.RemoteResource.DownloadContext mDownloadContext;
    private final Vector<BaseResource> mLoadingResource;
    private final ConcurrentHashMap<BaseResource, WeakReference<BaseResource.ResourceListener>> mLoadingResourceListener;

    private static ResourceManager sInstance;

    private ResourceManager(Context context) {
        mLoadingResource = new Vector<>();
        mLoadingResourceListener = new ConcurrentHashMap<>();
        mDownloadContext = new com.example.byteplus_effects_plugin.resource.RemoteResource.DownloadContext(context.getApplicationContext(),
                new NetworkManager());
    }

    public static ResourceManager getInstance(Context context) {
        synchronized (ResourceManager.class) {
            if (sInstance == null) {
                synchronized (ResourceManager.class) {
                    if (sInstance == null) {
                        sInstance = new ResourceManager(context);
                    }
                }
            }
        }
        return sInstance;
    }

    public BaseResource.ResourceResult syncGetResource(BaseResource resource) {
        return resource.syncGetResource();
    }

    public void asyncGetResource(BaseResource resource, BaseResource.ResourceListener listener) {
        boolean addRet = addLoadingResource(resource, listener);
        if (!addRet) {
            return;
        }

        prepareResource(resource);
        resource.asyncGetResource();
    }

    public synchronized void clearLoadingResource() {
        synchronized (this) {
            for (BaseResource resource : mLoadingResource) {
                resource.cancel();
            }

            mLoadingResource.clear();
            mLoadingResourceListener.clear();
        }
    }

    @Override
    public void onResourceSuccess(BaseResource resource, BaseResource.ResourceResult result) {
        BaseResource.ResourceListener listener = listenerForResource(resource);
        if (listener == null) {
            Log.e(TAG, "listenerForResource name = " + resource.name + " is null");
            return;
        }
        removeLoadingResource(resource);
        listener.onResourceSuccess(resource, result);
    }

    @Override
    public void onResourceFail(BaseResource resource, int errorCode, String msg) {
        BaseResource.ResourceListener listener = listenerForResource(resource);
        if (listener == null) {
            Log.e(TAG, "listenerForResource name = " + resource.name + " is null");
            return;
        }
        removeLoadingResource(resource);
        listener.onResourceFail(resource, errorCode, msg);
    }

    @Override
    public void onResourceStart(BaseResource resource) {
        BaseResource.ResourceListener listener = listenerForResource(resource);
        if (listener == null) {
            Log.e(TAG, "listenerForResource name = " + resource.name + " is null");
            return;
        }
        listener.onResourceStart(resource);
    }

    @Override
    public void onResourceProgressChanged(BaseResource resource, float progress) {
        BaseResource.ResourceListener listener = listenerForResource(resource);
        if (listener == null) {
            Log.e(TAG, "listenerForResource name = " + resource.name + " is null");
            return;
        }
        listener.onResourceProgressChanged(resource, progress);
    }

    private synchronized boolean addLoadingResource(BaseResource resource, BaseResource.ResourceListener listener) {
        if (mLoadingResource.contains(resource)) {
            return false;
        }

        mLoadingResource.add(resource);
        mLoadingResourceListener.put(resource, new WeakReference<>(listener));

        while (mLoadingResource.size() > MAX_LOADING_RESOURCE) {
            BaseResource r = mLoadingResource.firstElement();
            mLoadingResource.remove(r);
            r.cancel();
        }
        return true;
    }

    private synchronized void removeLoadingResource(BaseResource resource) {
        mLoadingResource.remove(resource);
        mLoadingResourceListener.remove(resource);
    }

    private synchronized BaseResource.ResourceListener listenerForResource(BaseResource resource) {
        WeakReference<BaseResource.ResourceListener> r = mLoadingResourceListener.get(resource);
        if (r == null) {
            return null;
        }
        return r.get();
    }

    private void prepareResource(BaseResource resource) {
        if (resource instanceof com.example.byteplus_effects_plugin.resource.RemoteResource) {
            ((com.example.byteplus_effects_plugin.resource.RemoteResource) resource).setContext(mDownloadContext);
        }
        resource.setResourceListener(this);
    }
}
