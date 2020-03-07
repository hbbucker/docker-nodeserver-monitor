package br.com.bucker.service;


import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DockerService {


    private DockerClient client;

    @PostConstruct
    public void init() {
        client = new DefaultDockerClient("unix:///var/run/docker.sock");
    }

    public DockerClient getClient() {
        return client;
    }

    public List<Container> getContainers() throws DockerException, InterruptedException {
        List<Container> containers = client.listContainers(DockerClient.ListContainersParam.allContainers());
        return containers;
    }

    public boolean startContainer(String containerName) {
        boolean retval = true;
        try {
            client.startContainer(containerName);
        } catch (DockerException | InterruptedException e) {
            retval = false;
            e.printStackTrace();
        }

        return retval;
    }

    public boolean createContainer(String imageName, String containerName, String[] exposePorts, String[] envValues, Map<String, List<PortBinding>> portBinding) {
        boolean retval = true;

        ContainerConfig containerConfig = ContainerConfig.builder()
                .image(imageName)
                .exposedPorts(exposePorts)
                .env(envValues)
                .hostname(containerName)
                .hostConfig(
                        HostConfig
                                .builder()
                                .portBindings(portBinding)
                                .build()
                )
                .build();
        try {
            client.createContainer(containerConfig, containerName);
            client.close();
            init();
        } catch (DockerException | InterruptedException e) {
            retval = false;
            e.printStackTrace();
        }
        return retval;
    }


    @Destroyed(ApplicationScoped.class)
    public void destroy(){
        client.close();
    }
}
