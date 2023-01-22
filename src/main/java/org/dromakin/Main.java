package org.dromakin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    private static final String URI = "https://api.nasa.gov/planetary/apod?api_key=%s";
    private static final String TMP_DIR = "tmp";


    public static void main(String[] args) {

        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build()) {

            logger.info("Creating tmp dir");
            Path rootPath = Paths.get(TMP_DIR).toAbsolutePath();
            Files.createDirectory(rootPath);

            logger.info("Key management loading...");
            PropertiesManager manager = new PropertiesManager();
            manager.saveProperties();
            String key = manager.loadPropertiesFromFile();

            logger.info("Creating request...");
            HttpGet request = new HttpGet(String.format(URI, key));

            logger.info("Make request to url {}", URI);
            CloseableHttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();
            logger.info("Status code: {}", status);

            if (status != 200) {
                throw new RequestException(String.format("Status code: %s", status));
            }

            logger.info("Processing json...");
            String jsonString;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                jsonString = EntityUtils.toString(entity);
                logger.debug(jsonString);

            } else {
                throw new RequestException("json response is null!");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            final JsonNode jsonNode = new ObjectMapper().readTree(jsonString);

            logger.info("Get url and file name from json");
            String urlResource = jsonNode.get("url").asText();
            URL url = new URL(urlResource);
            String newFileName = FilenameUtils.getName(url.getPath());
            Path filePath = Paths.get(rootPath.toString(), newFileName);

            logger.info("Make new request to URL from json");
            request = new HttpGet(urlResource);
            response = httpClient.execute(request);

            status = response.getStatusLine().getStatusCode();
            logger.info("Status code: {}", status);

            if (status != 200) {
                throw new RequestException(String.format("Status code: %s", status));
            }

            entity = response.getEntity();

            logger.info("Write file: {}", filePath.toString());
            try (BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                 BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(filePath.toFile().toPath())))
            {
                int inByte;
                while ((inByte = bis.read()) != -1) {
                    bos.write(inByte);
                }
            }

        } catch (IOException | RequestException | PropertiesManagerException e) {
            logger.error(e.getMessage(), e);
        }

    }
}