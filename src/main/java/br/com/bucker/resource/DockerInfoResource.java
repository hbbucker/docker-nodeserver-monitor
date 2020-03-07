package br.com.bucker.resource;

import br.com.bucker.service.DockerService;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Container> hello() throws DockerException, InterruptedException, JsonProcessingException {

        return dockerService.getContainers();

    }
}