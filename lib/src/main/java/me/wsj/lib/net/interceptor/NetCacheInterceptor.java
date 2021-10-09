package me.wsj.lib.net.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 缓存拦截器
 * 如果请求时添加了Cache-Control，则相应时设置相同的Cache-Control
 */
public class NetCacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originResponse = chain.proceed(request);

        if (request.header("Cache-Control") != null && !request.header("Cache-Control").isEmpty()) {
//            LogUtil.e("Cache-Control: " + request.header("Cache-Control"));
            originResponse = originResponse.newBuilder()
                    .removeHeader("pragma")
                    .header("Cache-Control", request.header("Cache-Control"))
                    .build();
        }

        return originResponse;
    }

}
