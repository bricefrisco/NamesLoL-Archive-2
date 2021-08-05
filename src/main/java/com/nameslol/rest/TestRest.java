package com.nameslol.rest;

import com.nameslol.models.Region;
import com.nameslol.models.SummonerRecordDTO;
import com.nameslol.services.RiotAPI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/test")
public class TestRest {
    final RiotAPI riotAPI;

    public TestRest(RiotAPI riotAPI) {
        this.riotAPI = riotAPI;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SummonerRecordDTO fetchFromRiot(@QueryParam("region") String region, @QueryParam("name") String name) throws IOException, InterruptedException {
        return riotAPI.fetchSummonerName(name, region);
    }
}
