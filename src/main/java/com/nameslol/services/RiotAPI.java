package com.nameslol.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameslol.models.Region;
import com.nameslol.models.SummonerRecordDTO;
import com.nameslol.models.exceptions.RiotAPIException;
import com.nameslol.models.exceptions.RiotException;
import com.nameslol.models.exceptions.SummonerNotFoundException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

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
        return str.trim().toUpperCase();
    }

    public SummonerRecordDTO fetchSummonerName(String name, String region) throws Exception {
        Region r = Region.valueOf(region.toUpperCase());

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format(RIOT_API_URI, r.toRiotFormat(), name)))
                .setHeader("X-Riot-Token", riotApiKey)
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (unsuccessful(response.statusCode())) {
            try {
                RiotException exception = MAPPER.readValue(response.body(), RiotException.class);
                if (response.statusCode() == 404) {
                    throw new SummonerNotFoundException(exception.getStatus().getMessage());
                } else {
                    throw new RiotAPIException(exception.getStatus().getMessage());
                }
            } catch (JsonProcessingException e) {
                LOGGER.info(e.getMessage());
                throw new RiotAPIException("Exception received from RIOT api, could not parse response: " + response.body());
            }
        }

        return MAPPER.readValue(response.body(), SummonerRecordDTO.class);
    }

    private boolean unsuccessful(int status) {
        return status < 200 || status >= 300;
    }
}
