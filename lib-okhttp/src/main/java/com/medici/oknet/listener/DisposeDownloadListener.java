package com.medici.oknet.listener;

/**
 * @author memedici
 * @function 监听下载进度
 */
public interface DisposeDownloadListener extends DisposeDataListener {

	/**
	 * 请求下载进度回调
	 * @param progress
	 */
	void onProgress(int progress);

}
