package br.com.bucker.resource;

import br.com.bucker.service.DockerService;
import br.com.bucker.service.NodeServerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/docker")
@Produces(MediaType.APPLICATION_JSON)
public class DockerInfoResource {

    @Inject
    DockerService dockerService;

    @Inject
    NodeServerService nodeServerService;

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Container> status() throws DockerException, InterruptedException, JsonProcessingException {
        return dockerService.getContainers();

    }

    @GET
    @Path("/nodestatus")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Container> nodeStatus() throws DockerException, InterruptedException, JsonProcessingException {
        return nodeServerService.getListNodeServers();
    }
}