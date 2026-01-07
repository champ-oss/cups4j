package org.cups4j;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Integration tests for {@link CupsClient} class.
 * Uses Testcontainers to spin up a CUPS server in Docker.
 * Tests will be skipped if Docker is not available.
 *
 * Run with -Dcups4j.ssl.trustAll=true to trust self-signed certificates.
 *
 * @author oliver (boehm@javatux.de)
 */
public class CupsClientTest {

  private static CupsClient client;
  private static final Logger LOG = LoggerFactory.getLogger(CupsClientTest.class);
  private static boolean dockerAvailable;
  private static CupsContainer cupsContainer;

  @BeforeClass
  public static void checkDockerAndSetUp() throws Exception {
    dockerAvailable = CupsContainer.isDockerAvailable();
    Assume.assumeTrue("Docker is not available - skipping integration tests", dockerAvailable);

    cupsContainer = new CupsContainer();
    cupsContainer.start();

    client = cupsContainer.createCupsClient();
    LOG.info("Connected to CUPS server at {}:{}", cupsContainer.getHost(), cupsContainer.getCupsPort());
  }

  @AfterClass
  public static void tearDown() {
    if (cupsContainer != null) {
      cupsContainer.stop();
    }
  }

  @Test
  public void getPrinters() throws Exception {
    Assume.assumeTrue("Docker is not available", dockerAvailable);

    List<CupsPrinter> printers = client.getPrinters();

    for (CupsPrinter printer : printers) {
      LOG.info("printer: " + printer.getName() + "[isClass=" + printer.isPrinterClass() + "]");
    }

    // Note: empty printers list is valid - the container may not have any printers configured
    LOG.info("Found {} printers", printers.size());
  }

  @Test
  public void testMakeAndModel() throws Exception {
    Assume.assumeTrue("Docker is not available", dockerAvailable);

    List<CupsPrinter> printers = client.getPrinters();

    for (CupsPrinter printer : printers) {
      LOG.info("printer: " + printer.getName() + "[makeAndModel=" + printer.getMakeAndModel() + "]");
    }
  }

}
