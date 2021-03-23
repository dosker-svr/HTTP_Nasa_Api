package ru.netology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=VP7dytJoIaFjds8gIRB73qDCbLCaWndjp5J2rqai");
        try {
            CloseableHttpResponse response = client.execute(request);
            JsonImage jsonImage = mapper.readValue(
                    response.getEntity().getContent(), new TypeReference<JsonImage>() {
                    });
            System.out.println(jsonImage.toString());

            HttpGet requestToImage = new HttpGet(jsonImage.getUrl());
            CloseableHttpResponse responseWithImage = client.execute(requestToImage);
            String nameImage = getNameImageFile(jsonImage.getUrl());


            /*InputStream inputStream = responseWithImage.getEntity().getContent();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            byte[] buffer = new byte[bufferedInputStream.available()];
            bufferedInputStream.read(buffer);
            inputStream.close();

            File fileImage = new File(nameImage);
            FileOutputStream fileImageOut = new FileOutputStream(fileImage);
            fileImageOut.write(buffer);

            fileImageOut.close();*/
            /////
            InputStream inputStream = responseWithImage.getEntity().getContent();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            File fileImage = new File(nameImage);
            FileOutputStream fileImageOut = new FileOutputStream(fileImage);

            byte[] buffer = new byte[16_384];
            int bytes;

            while((bytes = bufferedInputStream.read(buffer, 0, 16_384)) != -1) {
                fileImageOut.write(buffer, 0, bytes);
            }
            inputStream.close();
            fileImageOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getNameImageFile(String jsonImage) {
        String prefix = "https://apod.nasa.gov/apod/image/2103/";
        int startPosition = jsonImage.indexOf("2103/");
        String nameImage = jsonImage.substring(startPosition + 5);
        return nameImage;
    }
}
