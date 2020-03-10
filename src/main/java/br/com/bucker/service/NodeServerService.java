package br.com.bucker.service;

import br.com.bucker.builders.PortBindingBuilder;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;
import org.apache.commons.lang.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class NodeServerService {

    @Inject
    DockerService dockerService;

    public final String serverName = "nodeserver";
    public final String imageName = "bucker/node-simpleserver:latest";
    public final int initialPort = 3000;
    private String netName = "rede1";

    public NodeServerInfo getNewServerInfo(int number) {
        NodeServerInfo nodeServerInfo = new NodeServerInfo();

        nodeServerInfo.name = String.format("%s%s", serverName, StringUtils.leftPad(number + "", 2, "0"));
        nodeServerInfo.port = (initialPort + number) + "";

        return nodeServerInfo;
    }

    public List<Container> getListNodeServers() throws DockerException, InterruptedException {
        List<Container> containers = dockerService.getClient()
                .listContainers(DockerClient.ListContainersParam.allContainers()).parallelStream()
                .filter(contaier ->
                        contaier.names().get(0).startsWith("/" + serverName))
                .collect(Collectors.toList());

        return containers;
    }

    public void killContainer(List<Container> containers) {
        //Ajustando container levantados a+
        for (Container container : containers) {
            dockerService.killContainer(container);
            System.out.println("Matando container " + container.names().get(0));

        }
    }

    public void killAllContainers() throws DockerException, InterruptedException {
        // docker images bucker/node-simpleserver --format "{{.Tag}} {{.ID}}" | sort -r
        killContainer(getListNodeServers());
    }

    public String restorePreviousImageVersion() throws DockerException, InterruptedException {
        List<Image> images = dockerService.getClient().listImages(DockerClient.ListImagesParam.byName(imageName.split("\\:")[0]));

        if (images.size() >= 2) {

            orderContainerByTagReverse(images);

            killAllContainers();

            String latestImage = images.get(0).repoTags().get(0);
            String previousImage = images.get(1).repoTags().get(0);

            dockerService.removeImage(latestImage.split("\\:")[0], latestImage.split("\\:")[1]);
            dockerService.renameImageTag(latestImage.split("\\:")[0],
                    previousImage.split("\\:")[1], "latest");

            return "Imagem Restaurada " + previousImage;

        } else
            throw new DockerException("Não há imagem anterior para restaurar!");

    }

    private void orderContainerByTagReverse(List<Image> images) {
        images.sort(new Comparator<Image>() {
            @Override
            public int compare(Image o1, Image o2) {
                try {
                    String tag1 = o1.repoTags().get(0).split("\\:")[1];
                    String tag2 = o2.repoTags().get(0).split("\\:")[1];

                    Collator collator = Collator.getInstance();
                    return collator.compare(tag2, tag1);
                } catch (Exception e) {
                    return 0;
                }
            }
        });
    }

    public void createNewServer(NodeServerInfo nodeServer) {

        String env = "NODE_PORT=" + nodeServer.port;
        Map<String, List<PortBinding>> portBinding = new PortBindingBuilder().Builder(nodeServer.port, nodeServer.port).build();
        dockerService.createContainer(imageName,
                nodeServer.name,
                new String[]{nodeServer.port},
                new String[]{env},
                new String[]{"/etc/localtime:/etc/localtime:ro"}, //ajuste do horario do container com o hospedeiro
                netName
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