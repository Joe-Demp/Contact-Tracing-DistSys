package ie.ucd.sds.webUI.service;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class EurekaDNS implements DomainNameService {
    private static final Random random = new Random();
    private final DiscoveryClient discoveryClient;
    private final Logger logger = Logger.getLogger(EurekaDNS.class.getName());

    public EurekaDNS(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    private static ServiceInstance randomServiceInstance(List<ServiceInstance> listOfInstances) {
        int randIndex = random.nextInt(listOfInstances.size());
        return listOfInstances.get(randIndex);
    }

    @Override
    public URI find(String serviceName) {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);

        if (serviceInstances.isEmpty()) {
            logger.warning(String.format("No service instances of type %s could be found.", serviceName));
            return null;
        }
        ServiceInstance service = randomServiceInstance(serviceInstances);
        return service.getUri();
    }
}
