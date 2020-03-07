package br.com.bucker.service;

import br.com.bucker.builders.PortBindingBuilder;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.PortBinding;
import org.apache.commons.lang.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class NodeServerService {

    @Inject
    DockerService dockerService;

    public final String serverName = "nodeserver";
    public final String imageName = "bucker/node-simpleserver:1.0.0";
    public final int initialPort = 3000;

    public NodeServerInfo getNewServerInfo(int number){
        NodeServerInfo nodeServerInfo = new NodeServerInfo();

        nodeServerInfo.name = String.format("%s%s", serverName,  StringUtils.leftPad( number + "", 2, "0"));
        nodeServerInfo.port = (initialPort + number) + "";

        return  nodeServerInfo;
    }

    public List<Container> getListNodeServers() throws DockerException, InterruptedException {
        List<Container> containers = dockerService.getClient()
                .listContainers(DockerClient.ListContainersParam.allContainers()).parallelStream()
                .filter(contaier ->
                        contaier.names().get(0).startsWith("/" + serverName))
                .collect(Collectors.toList());

        return containers;
    }

    public void createNewServer(NodeServerInfo nodeServer) {

        String env = "NODE_PORT=" + nodeServer.port;
        Map<String, List<PortBinding>> portBinding = new PortBindingBuilder().Builder(nodeServer.port, nodeServer.port).build();
        dockerService.createContainer(imageName,
                nodeServer.name,
                new String[]{nodeServer.port},
                new String[]{env},
                portBinding
        );

    }

    public int getServerNumber(String serverName) {
        String nameA = serverName.replace("/" + this.serverName, "");
        return Integer.valueOf(nameA);
    }

    public class NodeServerInfo {
        public String name;
        public String port;

        @Override
        public String toString() {
            return "NodeServerInfo{" +
                    "name='" + name + '\'' +
                    ", port='" + port + '\'' +
                    '}';
        }
    }

}