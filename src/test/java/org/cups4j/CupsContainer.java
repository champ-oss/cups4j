package org.cups4j;

import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * Testcontainers container for CUPS server.
 * Uses the olbat/cupsd Docker image which provides a basic CUPS server.
 */
public class CupsContainer extends GenericContainer<CupsContainer> {

    private static final int CUPS_PORT = 631;
    private static final String DEFAULT_IMAGE = "olbat/cupsd:latest";

    public CupsContainer() {
        this(DockerImageName.parse(DEFAULT_IMAGE));
    }

    public CupsContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        withExposedPorts(CUPS_PORT);
        waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));
    }

    public int getCupsPort() {
        return getMappedPort(CUPS_PORT);
    }

    public CupsClient createCupsClient() throws Exception {
        return new CupsClient(getHost(), getCupsPort());
    }

    /**
     * Check if Docker is available for running Testcontainers.
     * @return true if Docker is available, false otherwise
     */
    public static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
