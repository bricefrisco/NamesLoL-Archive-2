package com.nameslol.services;

import com.nameslol.models.Region;
import com.nameslol.models.SummonerRecordDTO;
import com.nameslol.models.exceptions.BadRequestException;
import com.nameslol.models.exceptions.RiotAPIException;
import com.nameslol.models.exceptions.SummonerNotFoundException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import java.net.URI;
import java.net.URISyntaxException;

@ApplicationScoped
@Named("riotAPI")
@RegisterForReflection
public class RiotService {
    private static final String RIOT_API_URI = "https://%s.api.riotgames.com";

    @ConfigProperty(name = "riot.api-key")
    String riotApiKey;

    public String format(String str) {
        return str.trim().toUpperCase();
    }

    public SummonerRecordDTO fetchSummonerName(String name, String region) {
        try {
            Region r = Region.valueOf(region.toUpperCase());
            RiotAPI riotAPI = RestClientBuilder.newBuilder()
                    .baseUri(new URI(String.format(RIOT_API_URI, r.toRiotFormat())))
                    .build(RiotAPI.class);
            return riotAPI.getSummoner(name);
        } catch (WebApplicationException ex) {
            if (ex.getResponse().getStatus() == 404) {
                throw new SummonerNotFoundException("Summoner not found.");
            }
            throw new RiotAPIException(ex.getMessage());
        } catch (URISyntaxException e) {
            throw new BadRequestException("URL could not be parsed.");
        }
    }
}
