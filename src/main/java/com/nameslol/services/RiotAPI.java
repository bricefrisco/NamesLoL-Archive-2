package com.nameslol.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameslol.models.Region;
import com.nameslol.models.SummonerRecordDTO;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
@Named("riotAPI")
@RegisterForReflection
public class RiotAPI {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String RIOT_API_URI = "https://%s.api.riotgames.com/lol/summoner/v4/summoners/by-name/%s";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(RiotAPI.class);

    @ConfigProperty(name = "riot.api-key")
    String riotApiKey;

    public String format(String str) {
        return str.replace("\n", "").replace("\r", "").trim();
    }

    public SummonerRecordDTO fetchSummonerName(String name, String region) throws IOException, InterruptedException {
        Region r = Region.valueOf(region);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format(RIOT_API_URI, r.toRiotFormat(), name)))
                .setHeader("X-Riot-Token", riotApiKey)
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return MAPPER.readValue(response.body(), SummonerRecordDTO.class);
    }
}
