package com.ray.gg.init;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.disk.NoOpDiskTrimmableRegistry;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.memory.DefaultFlexByteArrayPoolParams;
import com.facebook.imagepipeline.memory.PoolConfig;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.facebook.imagepipeline.memory.PoolParams;
import com.ray.gg.App;

import java.util.HashSet;

import okhttp3.OkHttpClient;

public class FrescoConfig {
      static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();//分配的可用内存
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;//使用的缓存数量

    public static final int MAX_SMALL_DISK_VERYLOW_CACHE_SIZE = 16 * ByteConstants.MB;//小图极低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static final int MAX_SMALL_DISK_LOW_CACHE_SIZE = 32 * ByteConstants.MB;//小图低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static final int MAX_SMALL_DISK_CACHE_SIZE = 64 * ByteConstants.MB;//小图磁盘缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）

    public static final int MAX_DISK_CACHE_VERYLOW_SIZE = 16 * ByteConstants.MB;//默认图极低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_LOW_SIZE = 64 * ByteConstants.MB;//默认图低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_SIZE = 128 * ByteConstants.MB;//默认图磁盘缓存的最大值

//      static final int RETRY_COUNT = 2;
//      static final int RETRY_WAIT_TIME = 2000;


    public static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "fresco_small_img_cache";//小图所放路径的文件夹名
    public static final String IMAGE_PIPELINE_CACHE_DIR = "fresco_img_cache";//默认图所放路径的文件夹名

      static ImagePipelineConfig sImagePipelineConfig;


    static class EncodedMemoryCacheParamsSupplier implements Supplier<MemoryCacheParams> {

        // We want memory cache to be bound only by its memory consumption
          static final int MAX_CACHE_ENTRIES = Integer.MAX_VALUE;
          static final int MAX_EVICTION_QUEUE_ENTRIES = MAX_CACHE_ENTRIES;

        @Override
        public MemoryCacheParams get() {
            final int maxCacheSize = getMaxCacheSize();
            final int maxCacheEntrySize = maxCacheSize / 8;
            return new MemoryCacheParams(
                    maxCacheSize,
                    MAX_CACHE_ENTRIES,
                    maxCacheSize,
                    MAX_EVICTION_QUEUE_ENTRIES,
                    maxCacheEntrySize);
        }

          int getMaxCacheSize() {
            final int maxMemory = (int) Math.min(Runtime.getRuntime().maxMemory(), Integer.MAX_VALUE);
            if (maxMemory < 16 * ByteConstants.MB) {
                return 4 * ByteConstants.MB;
            } else if (maxMemory < 32 * ByteConstants.MB) {
                return 8 * ByteConstants.MB;
            } else {
                return 16 * ByteConstants.MB;
            }
        }
    }


      FrescoConfig() {

    }

    public  static ImagePipelineConfig getImagePipelineConfig(Context context) {
        if (sImagePipelineConfig == null) {
            sImagePipelineConfig = configureCaches(context);
        }
        return sImagePipelineConfig;
    }

    public static final int DEFAULT_MAX_BYTE_ARRAY_SIZE = 24 * ByteConstants.MB;
    // the min buffer size we'll use
      static final int DEFAULT_MIN_BYTE_ARRAY_SIZE = 512 * ByteConstants.KB;
    // the maximum number of threads permitted to touch this pool
    public static final int DEFAULT_MAX_NUM_THREADS = Runtime.getRuntime().availableProcessors();


      static ImagePipelineConfig configureCaches(Context context) {
        //内存配置
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE,
                Integer.MAX_VALUE,
                MAX_MEMORY_CACHE_SIZE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE);


        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };

        Supplier<MemoryCacheParams> mSupplierEncMemoryCacheParams = new EncodedMemoryCacheParamsSupplier();

        //小图片的磁盘配置
        DiskCacheConfig diskSmallCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())//缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)//文件夹名
                .setMaxCacheSize(FrescoConfig.MAX_SMALL_DISK_CACHE_SIZE)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_SMALL_DISK_LOW_CACHE_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_SMALL_DISK_VERYLOW_CACHE_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .setDiskTrimmableRegistry(NoOpDiskTrimmableRegistry.getInstance())
//                .setCacheEventListener(WebImageCacheEventListener.getInstance())
                .build();

        //默认图片的磁盘配置
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(context.getApplicationContext().getCacheDir()/*Environment.getExternalStorageDirectory().getAbsoluteFile()*/)//缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)//文件夹名
                .setMaxCacheSize(FrescoConfig.MAX_DISK_CACHE_SIZE)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .setDiskTrimmableRegistry(NoOpDiskTrimmableRegistry.getInstance())
//                .setCacheEventListener(WebImageCacheEventListener.getInstance())
                .build();

        HashSet t = new HashSet();
        t.add(new RequestLoggingListener());
        PoolConfig poolConfig = PoolConfig
                .newBuilder()
                .setFlexByteArrayPoolParams( new PoolParams(
        /* maxSizeSoftCap */ DEFAULT_MAX_BYTE_ARRAY_SIZE,
        /* maxSizeHardCap */ DEFAULT_MAX_NUM_THREADS * DEFAULT_MAX_BYTE_ARRAY_SIZE,
        /* bucketSizes */ DefaultFlexByteArrayPoolParams.generateBuckets(
                        DEFAULT_MIN_BYTE_ARRAY_SIZE,
                        DEFAULT_MAX_BYTE_ARRAY_SIZE,
                        DEFAULT_MAX_NUM_THREADS),
        /* minBucketSize */  DEFAULT_MIN_BYTE_ARRAY_SIZE,
        /* maxBucketSize */  DEFAULT_MAX_BYTE_ARRAY_SIZE,
        /* maxNumThreads */  DEFAULT_MAX_NUM_THREADS))
                .build();
        PoolFactory poolFactory = new PoolFactory(poolConfig);

        OkHttpClient client = new OkHttpClient();
        return OkHttpImagePipelineConfigFactory.newBuilder(context, client)
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)
                .setEncodedMemoryCacheParamsSupplier(mSupplierEncMemoryCacheParams)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setRequestListeners(t)
                .setBitmapsConfig(Bitmap.Config.ARGB_8888)
                .setNetworkFetcher(new OkHttpNetworkFetcher(client))
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                .setPoolFactory(poolFactory)
                .setSmallImageDiskCacheConfig(diskSmallCacheConfig)
                .build();
    }



    public static void initDefaultFresco(){
        Fresco.initialize(App.get(),FrescoConfig.getImagePipelineConfig(App.get()));
    }


    public static long getImageCacheSize() {
        Fresco.getImagePipelineFactory().getMainFileCache().trimToMinimum();
        Fresco.getImagePipelineFactory().getSmallImageFileCache().trimToMinimum();
        return Fresco.getImagePipelineFactory().getMainFileCache().getSize()
                + Fresco.getImagePipelineFactory().getSmallImageFileCache().getSize();
    }

    public static void pause(){
//        if (!Fresco.getImagePipeline().isPaused()) {
//            Fresco.getImagePipeline().pause();
//        }
    }

    public static void resume(){
//        if (Fresco.getImagePipeline().isPaused()) {
//            Fresco.getImagePipeline().resume();
//        }
    }
}
