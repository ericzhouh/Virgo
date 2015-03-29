package com.example;

import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("myresource")
@Component
public class MyResource {
    @GET
    @Path("getit.json")
    @Produces(MediaType.APPLICATION_JSON)
    public TRest getIt(
            @ParamSpec(isRequired = true, spec = "Long:[1,99999999999]", desc = "num")
            @QueryParam("num")
            long num
    ) {
        VirgoLogger.debug("debug");
        VirgoLogger.info("debug");
        VirgoLogger.error("debug");
        VirgoLogger.warn("debug");
        TRest tRest = new TRest(num, "2222", 100, 220);
        return tRest;
    }
}
