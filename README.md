<h1 align="center">
  <br>
    inspectory
   <br>
</h1>

<h4 align="center">Inspect your repository.</h4>


[![Build Status](https://travis-ci.com/LaviniaCioloca/inspectory.svg?token=wZgM2vdBUk6rczwiApsx&branch=master)](https://travis-ci.com/LaviniaCioloca/inspectory)
[![codecov](https://codecov.io/gh/LaviniaCioloca/inspectory/branch/master/graph/badge.svg?token=mB1mfcoCji)](https://codecov.io/gh/LaviniaCioloca/inspectory)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## About

Inspectory is a static analysis tool created using Java and designed to inspect Git repositories for Java projects. Its goal is to detect some anti-patterns using metrics.

## Key features

* detect Supernova methods: methods that grow abruptly in size at some point in time
* detect Pulsar methods: methods that repeatedly grow and shrink in size
* detect Ownership problems on classes and methods

## How To Use

### Prerequisites

To run the tool you need to have minimum [Java](https://java.com/en/download/) 8 and [Git](https://git-scm.com) installed on your computer. As well, you need to download the latest [ChronoLens](https://github.com/andreihh/chronolens) 0.1.X from [here](https://github.com/andreihh/chronolens/releases) (currently, the latest version is [metanalysis-0.1.8](https://github.com/andreihh/chronolens/releases/tag/0.1.8) released on Feb 6).

### Usage steps

First, after assuring the prerequisites are met, download the latest release of Inspectory from [here](https://github.com/LaviniaCioloca/inspectory/releases). Using the command line, either on Unix-like or Windows systems, follow the next steps:
```
# Clone the repository to be inspected into the current working directory.
git clone <desired_Git_repository_URL>

# Copy the metanalysis-0.1.X folder into the repository's folder.

# Persist the repository to get its history model.
./metanalysis-<version>/bin/metanalysis persist

# After the ChronoLens tool finished, copy Inspectory .jar into the repository's folder.

# Run all the metrics available using '-all' option. For more options, use '-h' instead.
java -jar inspectory-<version>.jar -all

# Analyze CSV and JSON results in .inspectory folder.

# Clean the repository by deleting ChronoLens and Inspectory results.
./metanalysis-<version>/bin/metanalysis clean

java -jar inspectory-<version>.jar --clean
```
## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/LaviniaCioloca/inspectory/blob/master/LICENSE) file for details.
