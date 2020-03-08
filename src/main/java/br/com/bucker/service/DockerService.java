package br.com.bucker.service;


import com.google.common.collect.ImmutableMap;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
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

    public boolean createContainer(String imageName, String containerName, String[] exposePorts, String[] envValues, String netName) {
        boolean retval = true;

        ProcessBuilder processBuilder = new ProcessBuilder();

        String tmplPort = "-p %s:%s ";
        String tmplEnv = "-e %s ";
        String cmd = "docker run --net=%s --rm -dit %s %s --name %s %s";

        String ports = "";
        for (String port : exposePorts) {
            ports += String.format(tmplPort, port, port);
        }

        String envs = "";
        for (String env : envValues) {
            envs += String.format(tmplEnv, env);
        }

        cmd = String.format(cmd, netName, envs, ports, containerName, imageName);

        // Run a shell command
        processBuilder.command("sh", "-c", cmd);

        try {

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Container criado!");
                System.out.println(output);
            } else {
                retval = false;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            retval = false;

        }

        return retval;
    }

    public boolean killContainer(Container container) {
        boolean retval = true;
        try {
            client.killContainer(container.id(), DockerClient.Signal.SIGKILL);
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
            retval = false;
        }

        return retval;
    }

    public Network getOrCreateNetwork(String netName) {
        NetworkConfig networkConfig = NetworkConfig.builder()
                .name(netName)
                .build();

        try {
            List<Network> nets = client.listNetworks(DockerClient.ListNetworksParam.byNetworkName(netName));
            if (nets.size() == 0) {
                NetworkCreation createNetwork = client.createNetwork(networkConfig);
                nets = client.listNetworks(DockerClient.ListNetworksParam.byNetworkId(createNetwork.id()));
            }

            return nets.get(0);


        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Destroyed(ApplicationScoped.class)
    public void destroy() {
        client.close();
    }
}
