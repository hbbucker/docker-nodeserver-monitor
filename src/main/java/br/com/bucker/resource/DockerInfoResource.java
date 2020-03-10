package br.com.bucker.resource;

import br.com.bucker.service.DockerService;
import br.com.bucker.service.NodeServerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.Image;

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
    @Path("/images")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Image> images() throws DockerException, InterruptedException, JsonProcessingException {
        return dockerService.getClient().listImages(DockerClient.ListImagesParam.allImages());

    }

    @GET
    @Path("/node-status")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Container> nodeStatus() throws DockerException, InterruptedException, JsonProcessingException {
        return nodeServerService.getListNodeServers();
    }

    @GET
    @Path("/node-restore")
    @Produces(MediaType.TEXT_PLAIN)
    public String nodeRestore() throws DockerException, InterruptedException, JsonProcessingException {
        return nodeServerService.restorePreviousImageVersion();
    }
}