package moe.imtop1.bot.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.bot.domain.ServerInfo;
import moe.imtop1.bot.domain.vo.ServerDetailVO;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class NezhaApi {

    @Value(value = "${nezha.address}")
    private String address;
    @Value(value = "${nezha.token}")
    private String token;
    @Value(value = "${nezha.path.serverInfo}")
    private String serverInfoPath;
    @Value(value = "${nezha.path.serverDetail}")
    private String serverDetailPath;


    public List<ServerInfo> getServerList(String tag) {
        String path = address + serverInfoPath;
        if (StringUtils.hasText(tag)) {
            path = path + "?tag=" + tag;
        }
        List<ServerInfo> serverInfoList = new ArrayList<>();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(path);

            // 请求头配置
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            httpGet.setHeader("Authorization", token);

            // 执行请求
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int code = response.getCode();

            if (code == 200) {
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseBody);
                if (root.path("code").asInt() == 0) {
                    JsonNode results = root.path("result");
                    if (results.isArray()) {
                        for (JsonNode result : results) {
                            ServerInfo server = mapper.treeToValue(result, ServerInfo.class);
                            serverInfoList.add(server);
                        }
                    }
                }
                log.info("query server info success: " + serverInfoList.toString());
            } else {
                log.error("Error: Failed to fetch server list. Status code: " + code);
            }


        } catch (Exception e) {
            log.error("query server info error: " + e);
        }

        return serverInfoList;
    }

    public List<ServerDetailVO> getServerDeList(String tag, Long id) {
        String path = address + serverDetailPath;
        if (StringUtils.hasText(tag)) {
            path = path + "?tag=" + tag;
        }
        if (!ObjectUtils.isEmpty(id)) {
            path = path + "?id=" + id;
        }
        List<ServerDetailVO> serverInfoList = new ArrayList<>();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(path);

            // 请求头配置
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            httpGet.setHeader("Authorization", token);

            // 执行请求
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int code = response.getCode();

            if (code == 200) {
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                JsonNode root = mapper.readTree(responseBody);
                if (root.path("code").asInt() == 0) {
                    JsonNode results = root.path("result");
                    if (results.isArray()) {
                        for (JsonNode result : results) {
                            ServerDetailVO server = mapper.treeToValue(result, ServerDetailVO.class);
                            serverInfoList.add(server);
                        }
                    }
                }
                log.info("query server info success: " + serverInfoList.toString());
            } else {
                log.error("Error: Failed to fetch server list. Status code: " + code);
            }


        } catch (Exception e) {
            log.error("query server info error: " + e);
        }

        return serverInfoList;
    }
}
