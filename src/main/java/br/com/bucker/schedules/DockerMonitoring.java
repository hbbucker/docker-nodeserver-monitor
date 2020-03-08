package br.com.bucker.schedules;


import br.com.bucker.service.NodeServerService;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import io.quarkus.scheduler.Scheduled;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class DockerMonitoring {

    private static final int cores = Runtime.getRuntime().availableProcessors();
//    private static final int cores = 6;

    @Inject
    NodeServerService nodeServerService;

    @PostConstruct
    public void init() {

    }

    @Scheduled(every = "15s")
    public void monitorNodeServer() {

        List<Container> containers = null;
        try {
            containers = nodeServerService.getListNodeServers();
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (containers.size() == cores) return;
        orderContainerByName(containers);

        String nServer[] = new String[cores];

        //Ajustando container levantados a+
        containers = adjustContainerUp(containers);

        for (Container container : containers) {
            if (container.names().size() > 0) {
                int serverNumber = nodeServerService.getServerNumber(container.names().get(0));
                nServer[serverNumber] = container.names().get(0);
            }
        }

        for (int i = 0; i < nServer.length; i++) {
            if (nServer[i] == null) {
                NodeServerService.NodeServerInfo servNode = nodeServerService.getNewServerInfo(i);
                nodeServerService.createNewServer(servNode);
                System.out.println("Novo servidor criado " + servNode.toString());

            }
        }
    }

    private List<Container>  adjustContainerUp(List<Container> containers) {
        if (containers.size() > cores) {
            List<Container> kills = containers.subList(cores, containers.size());
            nodeServerService.killContainer(kills);
        }

        return containers.subList(0, containers.size() > cores ? cores : containers.size());

    }

    private void orderContainerByName(List<Container> containers) {
        // Ordenando pelo numero do servidor
        containers.sort((o1, o2) -> {
            String nameA = o1.names().get(0).replace("/"+ nodeServerService.serverName, "");
            String nameB = o2.names().get(0).replace("/"+ nodeServerService.serverName, "");

            int a = Integer.valueOf(nameA);
            int b = Integer.valueOf(nameB);

            if (a > b)
                return 1;
            else if (a < b)
                return -1;
            else
                return 0;
        });
    }


}
