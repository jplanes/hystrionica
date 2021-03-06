package com.github.jplanes.restful.turbine.api;

import com.github.jplanes.restful.turbine.api.dto.InstanceDTO;
import com.github.jplanes.restful.turbine.api.exceptions.InvalidBodyException;
import com.github.jplanes.restful.turbine.services.InstanceRegistrationService;
import com.netflix.turbine.discovery.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/instances")
public class InstancesController {
    private InstanceRegistrationService instanceRegistration;

    @Autowired
    public InstancesController(InstanceRegistrationService instanceRegistration) {
        this.instanceRegistration = instanceRegistration;
    }

    @RequestMapping(method = GET)
    public Collection<InstanceDTO> findAll(@RequestParam(name = "cluster") String clusterName) {
        return this .instanceRegistration.findAllRegisteredInstances(clusterName).stream()
                    .map(i -> new InstanceDTO(i.getHostname(), i.getCluster()))
                    .collect(toList());
    }

    @RequestMapping(method = POST)
    public void createNew(@RequestBody(required = true) InstanceDTO instance) {
        if(instance.getHost() == null || instance.getHost().trim().equals(""))
            throw new InvalidBodyException("The host field is required.");

        if(instance.getCluster() == null || instance.getCluster().trim().equals(""))
            throw new InvalidBodyException("The cluster field is required");

        this.instanceRegistration.registerInstance(new Instance(instance.getHost(), instance.getCluster(), true));
    }

    @RequestMapping(
            method = DELETE,
            path = "/{clusterName}/{instanceName}"
    )
    public void delete(@PathVariable String clusterName, @PathVariable String instanceName) {
        this.instanceRegistration.unregisterInstance(new Instance(instanceName, clusterName, true));
    }

}
