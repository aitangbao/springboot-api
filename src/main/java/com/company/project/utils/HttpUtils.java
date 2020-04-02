package com.company.project.utils;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 执行一个HTTP GET请求，返回请求响应的HTML
     *
     * @param url 请求的URL地址
     * @return 返回请求响应的HTML
     */
    public static int doGet(String url) {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);

        try {
            client.executeMethod(method);
        } catch (URIException e) {
            logger.error("执行HTTP Get请求时，发生异常！", e);
            return HttpStatus.SC_BAD_REQUEST;
        } catch (IOException e) {
            logger.error("执行HTTP Get请求" + url + "时，发生异常！", e);
            return HttpStatus.SC_BAD_REQUEST;
        } finally {
            method.releaseConnection();
        }
        logger.info("执行HTTP GET请求，返回码: {}", method.getStatusCode());
        return method.getStatusCode();
    }

    /**
     * 执行一个带参数的HTTP GET请求，返回请求响应的JSON字符串
     *
     * @param url 请求的URL地址
     * @return 返回请求响应的JSON字符串
     */
    public static String doGet(String url, String param) {
        // 构造HttpClient的实例
        HttpClient client = new HttpClient();
        //设置参数
        // 创建GET方法的实例
        GetMethod method = new GetMethod(url + "?" + param);
        // 使用系统提供的默认的恢复策略
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());
        try {
            // 执行getMethod
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                return StreamUtils.copyToString(method.getResponseBodyAsStream(), Charset.forName("utf-8"));
            }
        } catch (IOException e) {
            logger.error("执行HTTP Get请求" + url + "时，发生异常！", e);
        } finally {
            method.releaseConnection();
        }
        return null;
    }

    /**
     * 执行一个HTTP GET请求，返回请求响应的HTML
     *
     * @param url         请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param charset     字符集
     * @param pretty      是否美化
     * @return 返回请求响应的HTML
     */
    public static String doGet(String url, String queryString, String charset, boolean pretty) {
        //logger.info("http的请求地址为:"+url);
        logger.info("http的请求参数为：" + queryString);

        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);

        try {
            if (StringUtils.isNotBlank(queryString)) {
                method.setQueryString(URIUtil.encodeQuery(queryString));
            }

            HttpConnectionManagerParams managerParams = client.getHttpConnectionManager().getParams();

            // 设置连接的超时时间 
            managerParams.setConnectionTimeout(3 * 60 * 1000);

            // 设置读取数据的超时时间 
            managerParams.setSoTimeout(5 * 60 * 1000);

            client.executeMethod(method);
            logger.info("http的请求地址为:" + url + ",返回的状态码为" + method.getStatusCode());

            BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset));
            String line;
            while ((line = reader.readLine()) != null) {
                if (pretty) {
                    response.append(line).append(System.getProperty("line.separator"));
                } else {
                    response.append(line);
                }
            }

            reader.close();

        } catch (Exception e) {
            logger.error("执行HTTP Get请求" + url + "时，发生异常！" + e);
            return response.toString();
        } finally {
            method.releaseConnection();
        }
        return response.toString();

    }

    /**
     * 执行一个带参数的HTTP POST请求，返回请求响应的JSON字符串
     *
     * @param url 请求的URL地址
     * @param map 请求的map参数
     * @return 返回请求响应的JSON字符串
     */
    public static String doPost(String url, Map<String, Object> map) {
        // 构造HttpClient的实例
        HttpClient httpClient = new HttpClient();
        // 创建POST方法的实例
        PostMethod method = new PostMethod(url);

        // 这个basicNameValue**放在List中
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        // 创建basicNameValue***对象参数
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                nameValuePairs.add(new NameValuePair(entry.getKey(), entry.getValue().toString()));
            }
        }

        // 填入各个表单域的值
        NameValuePair[] param = nameValuePairs.toArray(new NameValuePair[nameValuePairs.size()]);

        method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 将表单的值放入postMethod中
        method.addParameters(param);
        try {
            // 执行postMethod
            int statusCode = httpClient.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                return StreamUtils.copyToString(method.getResponseBodyAsStream(), Charset.forName("utf-8"));
            }
        } catch (UnsupportedEncodingException e1) {
            logger.error(e1.getMessage());
        } catch (IOException e) {
            logger.error("执行HTTP Post请求" + url + "时，发生异常！" + e.toString());
        } finally {
            method.releaseConnection();
        }
        return null;
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url     请求的URL地址
     * @param reqStr  请求的查询参数,可以为null
     * @param charset 字符集
     * @return 返回请求响应的HTML
     */
    public static String doPost(String url, String reqStr, String contentType, String charset) {
        HttpClient client = new HttpClient();

        PostMethod method = new PostMethod(url);
        try {
            HttpConnectionManagerParams managerParams = client
                    .getHttpConnectionManager().getParams();
            managerParams.setConnectionTimeout(30000); // 设置连接超时时间(单位毫秒)
            managerParams.setSoTimeout(30000); // 设置读数据超时时间(单位毫秒)

            method.setRequestEntity(new StringRequestEntity(reqStr, contentType, "utf-8"));

            client.executeMethod(method);
            logger.info("返回的状态码为" + method.getStatusCode());
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                return StreamUtils.copyToString(method.getResponseBodyAsStream(), Charset.forName(charset));
            }
        } catch (UnsupportedEncodingException e1) {
            logger.error(e1.getMessage());
            return "";
        } catch (IOException e) {
            logger.error("执行HTTP Post请求" + url + "时，发生异常！" + e.toString());

            return "";
        } finally {
            method.releaseConnection();
        }

        return null;
    }

    /**
     * @param url
     * @param entity
     * @return
     * @throws IOException
     */
    public static String doPost(String url, HttpEntity entity) {

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createSystem();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        //设置参数到请求对象中
        httpPost.setEntity(entity);

        BufferedReader reader = null;
        try {
            CloseableHttpResponse response = client.execute(httpPost);
            logger.info("Status:" + response.getStatusLine().getStatusCode());

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine;
                StringBuffer buffer = new StringBuffer();
                while ((inputLine = reader.readLine()) != null) {
                    buffer.append(inputLine);
                }
                reader.close();
                return buffer.toString();
            }
        } catch (IOException ex) {
            logger.info("执行http post请求出错,exception={}", ex.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}