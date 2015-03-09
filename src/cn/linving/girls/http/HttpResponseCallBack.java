package cn.linving.girls.http;

public interface HttpResponseCallBack {
	/**
	 * 
	 * @param url
	 *            �����ַ
	 * @param result
	 *            ������
	 */
	public void onSuccess(String url, String result);

	/**
	 * 
	 * @param httpResponseCode
	 *            http ������
	 * @param errCode
	 *            ������
	 * @param err
	 *            ��������
	 * 
	 */

	public void onFailure(int httpResponseCode, int errCode, String err);

}
