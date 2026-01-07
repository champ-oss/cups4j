# Cups4j (Champ Titles Fork)

A Java printing library for CUPS/IPP with **HTTPS support**.

This is a fork of [org.cups4j:cups4j](https://github.com/harwey/cups4j) with modifications for secure CUPS communication.

## Fork Changes

This fork includes the following modifications from the original `org.cups4j:cups4j`:

### HTTPS Support
- All IPP/CUPS communication now uses **HTTPS** instead of HTTP
- `ipp://` protocol URLs are converted to `https://` (previously `http://`)
- Affected operations: print jobs, job management, printer discovery

### Files Modified
- `IppOperation.java` - Core HTTP client uses HTTPS
- `CupsGetPrintersOperation.java` - Printer discovery via HTTPS
- `CupsGetDefaultOperation.java` - Default printer lookup via HTTPS
- `CupsMoveJobOperation.java` - Job move operations via HTTPS
- `IppCancelJobOperation.java` - Job cancellation via HTTPS
- `IppReleaseJobOperation.java` - Job release via HTTPS
- `IppHoldJobOperation.java` - Job hold via HTTPS
- `IppGetJobAttributesOperation.java` - Job attributes via HTTPS
- `IppGetJobsOperation.java` - Job listing via HTTPS

## Requirements

- Java 8 or higher
- Maven 3.6+
- CUPS server with HTTPS enabled

## Building

```bash
# Clone the repository
git clone https://github.com/champ-oss/cups4j.git
cd cups4j

# Build the project
mvn clean install

# Build without tests (if no CUPS server available)
mvn clean install -DskipTests
```

## Usage

### Maven Dependency

Add to your `pom.xml`:

```maven-pom
<dependency>
    <groupId>com.champtitles</groupId>
    <artifactId>cups4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Note:** This artifact is not published to Maven Central. You need to either:
1. Build and install locally (`mvn clean install`)
2. Deploy to your organization's private Maven repository

### Basic Usage

```java
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

// Connect to CUPS server (uses HTTPS)
CupsClient cupsClient = new CupsClient("cups-server.example.com", 631);

// Get default printer
CupsPrinter printer = cupsClient.getDefaultPrinter();

// Print a file
InputStream inputStream = new FileInputStream("document.pdf");
PrintJob printJob = new PrintJob.Builder(inputStream).build();
PrintRequestResult result = printer.print(printJob);
```

### Connect to Custom Host

```java
// Connect to CUPS server on custom host/port
CupsClient cupsClient = new CupsClient("192.168.1.100", 631);
```

### Get Specific Printer by URL

```java
// Note: URL should use https:// for this fork
URL printerURL = new URL("https://cups-server:631/printers/my-printer");
CupsPrinter printer = cupsClient.getPrinter(printerURL);
```

### Print with Options

```java
Map<String, String> attributes = new HashMap<>();
attributes.put("compression", "none");
attributes.put("job-attributes", "print-quality:enum:3#fit-to-page:boolean:true");

PrintJob printJob = new PrintJob.Builder(documentBytes)
    .jobName("My Print Job")
    .userName("username")
    .copies(2)
    .pageRanges("1-5")
    .duplex(true)
    .portrait(true)
    .color(true)
    .pageFormat("iso-a4")
    .attributes(attributes)
    .build();

PrintRequestResult result = printer.print(printJob);
```

## License

This project is licensed under the **GNU Lesser General Public License v3.0 (LGPL-3.0)**.

### Original Authors

- Copyright (C) 2008-2009 ITS of ETH Zurich, Switzerland (Sarah Windler Burri)
- Copyright (C) 2009-2012 Harald Weyhing
- Copyright (C) 2018 Oliver Boehm

### Fork Maintainer

- CHAMP Titles Inc.

### LGPL-3.0 Notice

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library. If not, see <https://www.gnu.org/licenses/lgpl-3.0.html>.

### What LGPL-3.0 Means

- **You can** use this library in proprietary software
- **You can** modify and distribute this library
- **You must** include the original copyright notices
- **You must** license modifications to this library under LGPL-3.0
- **You must** provide source code access for any modified library versions

See the [LICENSE](LICENSE) file for the complete license text.

## Upstream

This is a fork of the original cups4j project:
- Original repository: https://github.com/harwey/cups4j
- Original Maven artifact: `org.cups4j:cups4j`
