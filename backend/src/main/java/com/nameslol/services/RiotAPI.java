package com.nameslol.services;

import com.nameslol.models.SummonerRecordDTO;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/lol/summoner/v4")
@RegisterRestClient
@RegisterClientHeaders(RiotAPIHeaders.class)
public interface RiotAPI {

    @GET
    @Path("/summoners/by-name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    SummonerRecordDTO getSummoner(@PathParam("name") String name);
}
