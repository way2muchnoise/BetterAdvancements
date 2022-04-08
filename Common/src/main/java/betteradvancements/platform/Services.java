package betteradvancements.platform;

import betteradvancements.reference.Constants;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> serviceClass) {
        T loadedService = ServiceLoader.load(serviceClass)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + serviceClass.getName()));
        Constants.log.debug("Loaded {} for service {}", loadedService, serviceClass);
        return loadedService;
    }
}
