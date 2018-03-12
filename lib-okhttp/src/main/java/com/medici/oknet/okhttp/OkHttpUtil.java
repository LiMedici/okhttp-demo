package com.medici.oknet.okhttp;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.cnbi.ic9.util.constant.KeyConstant;
import com.cnbi.ic9.util.http.NovateCookieManager;
import com.cnbi.ic9.util.tool.DialogUtil;
import com.cnbi.ic9.util.tool.SystemUtil;
import com.cnbi.ic9.util.tool.UIUtil;
import com.cnbi.ic9.util.tool.blankj.SPUtil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * @desc okhttp工具类 构建者模式
 * @author 李宗好
 * @time:2017年1月5日 下午3:20:30
 */
public final class OkHttpUtil {

	//上下文
	private Activity mActivity;
	private Fragment mFragment;
	private Object mModel;
	//请求头
	private Map<String,String> headers = null;
	//请求参数
	private Map<String,Object> params = null;
	//CookieManager
	private NovateCookieManager cookieManager = null;

	private static OkHttpClient client = null;

	private OkHttpUtil(){};

	private void applyParams(){
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.readTimeout(10, TimeUnit.SECONDS)
			   .connectTimeout(10,TimeUnit.SECONDS)
		       .addInterceptor(new HttpLoggingInterceptor(new OkHttpLogger()));
		if(null!=headers){
			builder.addInterceptor(new OkHttpIntercepter(headers));
		}
		if(null!=cookieManager){
			builder.cookieJar(cookieManager);
		}

		client = builder.build();
	}

	/**
	 * OkHttpUtil内置的Builder
	 */
	public class Builder{

		//上下文
		private Activity mActivity;
		private Fragment mFragment;
		private Object mModel;
		//请求头
		private Map<String,String> headers = null;
		//请求参数
		private Map<String,Object> params = null;
		//CookieManager
		private NovateCookieManager cookieManager = null;


		public Builder(Activity mActivity){
			this.mActivity = mActivity;
		}

		public Builder(Fragment mFragment){
			this.mFragment = mFragment;
		}

		public Builder(Object mModel){
			this.mModel = mModel;
		}

		public Builder addHeader(Map<String,String> headers){
			this.headers = headers;
			return this;
		}

		public Builder addCookieManager(NovateCookieManager cookieManager){
			this.cookieManager = cookieManager;
			return this;
		}

		public Builder applyParams(Map<String,Object> params){
			this.params = params;
			return this;
		}

		public OkHttpUtil build(){
			OkHttpUtil util = new OkHttpUtil();
			util.mActivity = mActivity;
			util.mFragment = mFragment;
			util.mModel = mModel;
			util.cookieManager = cookieManager;
			util.applyParams();
			return util;
		}
	}
	
	/**
	 * 文件下载 get请求
	 * @param url 文件下载地址url
	 * @param progressResponseListener 进度响应
	 */
	/*public synchronized void download(String url, ProgressResponseListener progressResponseListener) {
		// 构造请求
		Request request = new Request.Builder().url(url).build();
		// 包装Response使其支持进度回调
		ProgressHelper.addProgressResponseListener(client, progressResponseListener).newCall(request)
				.enqueue(progressResponseListener);
	}*/
	
	/**
	 * 文件上传 post请求
	 * @param url 文件上传地址url
	 * @param map 参数
	 * @param ic_file_icon 上传的文件
	 * @param progressRequestListener 上传进度回调 callback回调
	 */
	/*public synchronized void upload(String url,File ic_file_icon,Map<String,Object> map,ProgressRequestListener progressRequestListener) {
		String token = getToken();
		if(TextUtils.isEmpty(token)){
			return;
		}

		// 构造上传请求，类似web表单

		MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM)
				.addFormDataPart("ic_file_icon", ic_file_icon.getName(), RequestBody.create(null, ic_file_icon))
				.addFormDataPart("token", token);
				*//*.addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""),
						RequestBody.create(MediaType.parse("application/octet-stream"), ic_file_icon));*//*

		if(null!=map && map.size() > 0){
			Set<Entry<String,Object>> set = map.entrySet();
			for (Entry<String, Object> entry : set) {
				if(entry.getValue() instanceof String){
					builder.addFormDataPart(entry.getKey(), (String)entry.getValue());
				}
			}
		}

		RequestBody requestBody = builder.build();

		// 进行包装，使其支持进度回调
		Request request = new Request.Builder().url(url)
				.post(ProgressHelper.addProgressRequestListener(requestBody, progressRequestListener)).addHeader("User-Agent", getUserAgent()).build();
		// 开始请求
		client.newCall(request).enqueue(progressRequestListener);
	}*/
	
	/**
	 * 模拟表单,post请求 异步请求 
	 * @param map 只能是String
	 */
	/*public void post(String url,Map<String,Object> map,Callback responseCallback) {
		//模拟表单
		MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
		if(null!=map && map.size() > 0){
			Set<Entry<String,Object>> set = map.entrySet();
			for (Entry<String, Object> entry : set) {
				if(entry.getValue() instanceof File){
					File ic_file_icon = (File) entry.getValue();
					builder.addFormDataPart("ic_file_icon", ic_file_icon.getName(), RequestBody.create(null, ic_file_icon));
					continue;
				}

				if(entry.getValue() instanceof String){
					builder.addFormDataPart(entry.getKey(), (String)entry.getValue());
				}
			}
		}

		//表单对象生成请求Body
		RequestBody requestBody = builder.build();
		Request request = new Request.Builder().url(url).post(requestBody).build();
		// 开始异步请求
		client.newCall(request).enqueue(responseCallback);
	}
	
	*//**
	 * get请求 异步请求 
	 * @param url 地址
	 *//*
	public void get(String url,Callback responseCallback){
		try {
			String token = getToken();
			if(TextUtils.isEmpty(token)){
				return;
			}
			if(url.contains("?")){			
				url += "&token="+token;
			}else{
				url += "?token="+token;
			}
			Request request = new Request.Builder().url(url).addHeader("User-Agent", getUserAgent()).build();
			//开始异步请求
			client.newCall(request).enqueue(responseCallback);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			UIUtils.showToastSafe("服务器错误-230");
		}
	}

	*//**
	 * 添加自己的某些信息
	 *//*
	public OkHttpUtil addLoginedInfo(Map<String,Object> map){
		UserEntity entity = Ic9TaskManager.getInstance().getMeEntity();
		Object obj = map.get("gitignore");
		if(null == obj){			
			map.put("avatar", entity.getAvatar());
			map.put("trueName", entity.getTrueName());
		}
		return mOkHttpUtils;
	}*/
	
	/**
	 * 返回userAgent参数
	 * @return
	 */
	public static String getUserAgent(){
		String userAgent = "android";
		boolean isPad = SystemUtil.isPad(UIUtil.getContext());
		if(!isPad){//是平板
			userAgent += "|mobile";
		}
		return userAgent;
	}
	
	/**
	 * 返回token
	 */
	public static String getToken(){
		String token = SPUtil.getInstance().getString(KeyConstant.TOKEN);
		if(TextUtils.isEmpty(token)){
			DialogUtil.closeDialog();
			UIUtil.showToastSafe("失去连接,等待重连接");
		}
		return token;
	}
}
