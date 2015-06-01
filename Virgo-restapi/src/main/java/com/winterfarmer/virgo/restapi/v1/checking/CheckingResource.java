package com.winterfarmer.virgo.restapi.v1.checking;

import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by yangtianhang on 15/6/1.
 */
@Path("checking")
@ResourceOverview(desc = "检测接口")
@Component("checkingResource")
public class CheckingResource {
    @Path("upstream_check.json")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RestApiInfo(
            desc = "检测状态"
    )
    public String upstreamCheck() {
        return "Tower presses monsters in the river!";
    }
}
