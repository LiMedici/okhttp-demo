package com.medici.oknet.okhttp;

import android.text.TextUtils;

import com.cnbi.ic9.dbentity.UserEntity;
import com.cnbi.ic9.util.constant.KeyConstant;
import com.cnbi.ic9.util.manager.Ic9TaskManager;
import com.cnbi.ic9.util.tool.DialogUtil;
import com.cnbi.ic9.util.tool.SystemUtil;
import com.cnbi.ic9.util.tool.UIUtil;
import com.cnbi.ic9.util.tool.blankj.SPUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @desc okhttp工具类 单例设计模式
 * @author 李宗好
 * @time:2017年1月5日 下午3:20:30
 */
public final class OkHttpUtils {

	private static OkHttpClient client = new OkHttpClient();
	private static OkHttpUtils mOkHttpUtils = null;
	
	static{
		//设置超时，不设置可能会报异常
	    client.setConnectTimeout(10000, TimeUnit.MILLISECONDS);
	    client.setReadTimeout(10000, TimeUnit.MILLISECONDS);
	    client.setWriteTimeout(10000, TimeUnit.MILLISECONDS);
	}
	
	private OkHttpUtils(){};
	
	/**
	 * 返回工具类的实例
	 * @return
	 */
	public static synchronized OkHttpUtils getInstance(){
		if(mOkHttpUtils == null){
			mOkHttpUtils = new OkHttpUtils();
		}
		return mOkHttpUtils;
	}
	
	/**
	 * 文件下载 get请求
	 * @param url 文件下载地址url
	 * @param progressResponseListener 进度响应
	 */
	public synchronized void download(String url, ProgressResponseListener progressResponseListener) {
		String token = getToken();
		if(TextUtils.isEmpty(token)){
			return;
		}

		//拼接token
		if(url.contains("?")){
			url += "&token="+token;
		}else{
			url += "?token="+token;
		}

		// 构造请求
		Request request = new Request.Builder().url(url).addHeader("User-Agent", getUserAgent()).build();
		// 包装Response使其支持进度回调
		ProgressHelper.addProgressResponseListener(client, progressResponseListener).newCall(request)
				.enqueue(progressResponseListener);
	}
	
	/**
	 * 文件上传 post请求
	 * @param url 文件上传地址url
	 * @param map 参数
	 * @param file 上传的文件
	 * @param progressRequestListener 上传进度回调 callback回调
	 */
	public synchronized void upload(String url,File file,Map<String,Object> map,ProgressRequestListener progressRequestListener) {
		String token = getToken();
		if(TextUtils.isEmpty(token)){
			return;
		}

		// 构造上传请求，类似web表单

		MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM)
				.addFormDataPart("file", file.getName(), RequestBody.create(null, file))
				.addFormDataPart("token", token);
				/*.addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""),
						RequestBody.create(MediaType.parse("application/octet-stream"), ic_file_icon));*/

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
	}
	
	/**
	 * 模拟表单,post请求 异步请求 
	 * @param map 只能是String
	 */
	public void post(String url,Map<String,Object> map,Callback responseCallback) {
		String token = getToken();
		if(TextUtils.isEmpty(token)){
			return;
		}
		//模拟表单
		MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
		if(null!=map && map.size() > 0){
			Set<Entry<String,Object>> set = map.entrySet();
			for (Entry<String, Object> entry : set) {
				if(entry.getValue() instanceof File){
					File file = (File) entry.getValue();
					builder.addFormDataPart("ic_file_icon", file.getName(), RequestBody.create(null, file));
					continue;
				}

				if(entry.getValue() instanceof String){
					builder.addFormDataPart(entry.getKey(), (String)entry.getValue());
				}
			}
		}

		builder.addFormDataPart("token", token);
		//表单对象生成请求Body
		RequestBody requestBody = builder.build();
		Request request = new Request.Builder().url(url).post(requestBody).addHeader("User-Agent", getUserAgent()).build();
		// 开始异步请求
		client.newCall(request).enqueue(responseCallback);
	}
	
	/**
	 * get请求 异步请求 
	 * @param url 地址
	 */
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
			UIUtil.showToastSafe("服务器错误-230");
		}
	}

	/**
	 * 添加自己的某些信息
	 */
	public OkHttpUtils addLoginedInfo(Map<String,Object> map){
		UserEntity entity = Ic9TaskManager.getInstance().getMeEntity();
		Object obj = map.get("gitignore");
		if(null == obj){			
			map.put("avatar", entity.getAvatar());
			map.put("trueName", entity.getTrueName());
		}
		return mOkHttpUtils;
	}
	
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
