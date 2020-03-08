package br.com.bucker.builders;

import com.spotify.docker.client.messages.PortBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortBindingBuilder {

    private String portExpose;
    private List<PortBinding> portBindingList = new ArrayList<>();

    public PortBindingBuilder Builder() {
        return this;
    }

    public PortBindingBuilder Builder(String portExpose, String portBinding) {
        this.portExpose = portExpose + "/tcp";
        this.portBindingList.add(PortBinding.create("0.0.0.0", portBinding));
        return this;
    }

    public PortBindingBuilder addPortBinding(String portBinding) {
        this.portBindingList.add(PortBinding.create("0.0.0.0", portBinding));
        return this;
    }

    public Map<String, List<PortBinding>> build(){
        Map<String, List<PortBinding>> build = new HashMap<>();
        build.put(portExpose, portBindingList);
        return  build;
    }

}
