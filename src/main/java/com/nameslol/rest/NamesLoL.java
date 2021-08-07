package com.nameslol.rest;

import com.nameslol.models.SummonerRecordDTO;
import com.nameslol.models.exceptions.RateLimitException;
import com.nameslol.services.RESTRateLimiter;
import com.nameslol.services.RiotAPI;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/test")
public class NamesLoL {
    private static final Logger LOGGER = LoggerFactory.getLogger(NamesLoL.class);

    final RiotAPI riotAPI;
    final RESTRateLimiter restRateLimiter;

    public NamesLoL(RiotAPI riotAPI, RESTRateLimiter restRateLimiter) {
        this.riotAPI = riotAPI;
        this.restRateLimiter = restRateLimiter;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SummonerRecordDTO fetchFromRiot(@QueryParam("region") String region, @QueryParam("name") String name, @Context HttpServerRequest request) throws IOException, InterruptedException {
        if (restRateLimiter.isLimited(request)) throw new RateLimitException("Too many requests. Please try again later.");
        return riotAPI.fetchSummonerName(name, region);
    }
}
