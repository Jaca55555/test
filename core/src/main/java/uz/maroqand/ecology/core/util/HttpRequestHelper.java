package uz.maroqand.ecology.core.util;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 26.09.2018.
 * (uz)
 * (ru)
 */
public class HttpRequestHelper {

    private static final Logger logger = LogManager.getLogger(HttpRequestHelper.class);

    public static HttpURLConnection getConnection(URL url) throws Exception {
        String protocol = url.getProtocol().toLowerCase();

        HttpURLConnection conn = null;

        if ("http".equals(protocol)) {
            conn = (HttpURLConnection) url.openConnection();
        } else if ("https".equals(protocol)) {
            conn = (javax.net.ssl.HttpsURLConnection) url.openConnection();
        }

        if(conn != null) {
            conn.setConnectTimeout(4000);

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        }

        return conn;
    }

    public static String httpRequest(String url, Map paramMap) {
        URL requestUrl;
        HttpURLConnection conn = null;
        String result = null;
        ByteArrayOutputStream out = null;
        OutputStream writer = null;
        InputStream reader = null;

        try {
            requestUrl = new URL(url);

            conn = HttpRequestHelper.getConnection(requestUrl);

            writer = conn.getOutputStream();

            String param = HttpRequestHelper.generateParam(paramMap);

            logger.info("httpRequest " + url + "  Data: " + param);

            writer.write(param.getBytes("UTF-8"));
            writer.flush();

            writer.close();

            try {
                reader = conn.getInputStream();
            } catch(IOException ioe) {
                reader = conn.getErrorStream();
            }
        /*
        out = new java.io.ByteArrayOutputStream();

        int read = -1;
        byte[] b = new byte[256];
        while((read = reader.read(b)) != -1) {
            out.write(b, 0, read);
        }
        out.flush();
        result = new String(out.toByteArray());
        */

            BufferedReader br = new BufferedReader(new InputStreamReader(reader, "UTF-8"));

            StringBuilder stringBuffer = new StringBuilder();
//            result = br.readLine();
            String buffer;
            while ((buffer = br.readLine()) != null) {
                if (stringBuffer.length() > 0) stringBuffer.append("\n");
                stringBuffer.append(buffer);
            }

            result = stringBuffer.toString();

            conn.disconnect();

            //out.close();
            reader.close();

        } catch(Exception e) {
            logger.error("httpRequest", e);
//            e.printStackTrace();
        } finally {
            if(conn != null) {
                conn.disconnect();
            }

            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

        return result;
    }

    public static String makePostRequestWithJSON(String url, String data) {
        URL requestUrl;
        HttpURLConnection conn = null;
        String result = null;
        ByteArrayOutputStream out = null;
        OutputStream writer = null;
        InputStream reader = null;

        try {
            requestUrl = new URL(url);

            conn = HttpRequestHelper.getConnection(requestUrl);

            conn.setRequestProperty("Content-Type", "application/json");

            writer = conn.getOutputStream();

//            logger.info("httpRequest " + url + "  Data: " + data);

            writer.write(data.getBytes("UTF-8"));
            writer.flush();

            writer.close();

            try {
                reader = conn.getInputStream();
            } catch (IOException ioe) {
                reader = conn.getErrorStream();
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(reader, "UTF-8"));


            StringBuilder stringBuffer = new StringBuilder();
//            result = br.readLine();
            String buffer;

            while ((buffer = br.readLine()) != null) {
                if (stringBuffer.length() > 0) stringBuffer.append("\n");
                stringBuffer.append(buffer);
            }

            result = stringBuffer.toString();

            conn.disconnect();

            //out.close();
            reader.close();

        } catch(Exception e) {
            logger.error("httpRequest", e);
//            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

        return result;
    }


    public static String makePostRequest(String url, List<NameValuePair> params) {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        String result = null;
// Request parameters and other properties.
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            System.out.println(httppost.getEntity().getContent().toString());

//Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();


                BufferedReader br = new BufferedReader(new InputStreamReader(instream, "UTF-8"));

                StringBuilder stringBuffer = new StringBuilder();
//            result = br.readLine();
                String buffer = "";

                while ((buffer = br.readLine()) != null) {
                    if (stringBuffer.length() > 0) stringBuffer.append("\n");
                    stringBuffer.append(buffer);
                }

                result = stringBuffer.toString();



                instream.close();

            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    /*["bash","request.sh","https://sso.gov.uz:8443/sso/oauth/Authorization.do","code\u003dr8oSBQm84JM0t9Y1xx9xrQ%3D%3D\u0026grant_type\u003done_authorization_code\u0026client_secret\u003dZUDs48vbGM3yoyht9L3bfQ%3D%3D\u0026client_id\u003dngo_minjust_uz"]*/

    public static String httpRequestUsingCURL(String url, Map paramMap) {
        String param = HttpRequestHelper.generateParam(paramMap);
        if (param != null && param.endsWith("&")) {
            param = param.substring(0, param.length() - 1);
        }
        String result = null;
        try {
//            String command[] = {"curl", "-X", "POST", "-s", url,
//                    "--data", "\"" + param + "\"", "--insecure", "-H","\"Content-Type: text/json\""};
            String command[] = {"bash", "request.sh", url, param};
            Process process = Runtime.getRuntime().exec(command);
            Gson gson = new Gson();
            logger.info(gson.toJson(command));
            process.waitFor();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));

            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            result = buffer.toString();
        } catch (Exception e) {
            logger.fatal(e);
        }
        return result;
    }

    private static String generateParam(Map param) {
        StringBuilder paramBuf = new StringBuilder();
        java.util.Iterator paramIter = param.entrySet().iterator();
        while (paramIter.hasNext()) {
            Map.Entry entry = (Map.Entry) paramIter.next();
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();

            paramBuf.append(HttpRequestHelper.encode(name));
            paramBuf.append("=");
            paramBuf.append(HttpRequestHelper.encode(value));
            paramBuf.append("&");
        }
        return paramBuf.toString();
    }

    private static String encode(String data) {
        if (data == null || "".equals(data.trim())) {
            return "";
        }

        try {
            return java.net.URLEncoder.encode(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return java.net.URLEncoder.encode(data);
        }
    }

    public static String httpGetRequest(String getUrl, final String username, final String password) {
        try {
            URL urlObject = new URL(getUrl);
            HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
            con.setRequestMethod("GET");

            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));

            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Authorization", "Basic " + new String(encodedAuth));

//            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            logger.debug("GET Response Code :: " + responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                logger.error("GET Response code is not 200: {}", responseCode);
                return null;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            return response.toString();

        } catch (Exception e) {
            return null;
        }
    }

}
