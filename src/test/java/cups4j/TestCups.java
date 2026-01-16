package cups4j;

import org.cups4j.CupsClient;
import org.cups4j.CupsContainer;
import org.cups4j.CupsPrinter;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Integration tests for CUPS client functionality.
 * Uses Testcontainers to spin up a CUPS server in Docker.
 * Tests will be skipped if Docker is not available.
 */
public class TestCups {

  private static CupsContainer cupsContainer;
  private static boolean dockerAvailable;

  @BeforeClass
  public static void setUp() {
    dockerAvailable = CupsContainer.isDockerAvailable();
    Assume.assumeTrue("Docker is not available - skipping integration tests", dockerAvailable);

    cupsContainer = new CupsContainer();
    cupsContainer.start();
  }

  @AfterClass
  public static void tearDown() {
    if (cupsContainer != null) {
      cupsContainer.stop();
    }
  }

  @Test
  public void testCupsClient() throws Exception {
    Assume.assumeTrue("Docker is not available", dockerAvailable);

    CupsClient client = getCupsClient();
    List<CupsPrinter> printers = client.getPrinters();
    for (CupsPrinter p : printers) {
      System.out.println("Printer: " + p.toString());
      System.out.println(" Media supported:");
      for (String media : p.getMediaSupported()) {
        System.out.println("  - " + media);
      }
      System.out.println(" Resolution supported:");
      for (String res : p.getResolutionSupported()) {
        System.out.println("  - " + res);
      }
      System.out.println(" Mime-Types supported:");
      for (String mime : p.getMimeTypesSupported()) {
        System.out.println("  - " + mime);
      }
    }
  }

  /**
   * Creates a CupsClient connected to the Testcontainers CUPS server.
   *
   * @return CupsClient for testing
   */
  public static CupsClient getCupsClient() throws Exception {
    return cupsContainer.createCupsClient();
  }

  /**
   * Returns the CUPS container for use in other tests.
   * @return the CUPS container instance
   */
  public static CupsContainer getCupsContainer() {
    return cupsContainer;
  }
}
