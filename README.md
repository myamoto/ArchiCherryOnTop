# ArchiCherryOnTop

[![license](https://img.shields.io/github/license/myamoto/ArchiCherryOnTop.svg)](LICENSE)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg?style=flat-square)](https://github.com/myamoto/ArchiCherryOnTop)

Secured REST API to transform well-formed [Archi](https://www.archimatetool.com/download) documents into [MxGraph](https://jgraph.github.io/mxgraph/) schemas.\
At the time or writing (23/03/2021) it is compatible with Archi 4.8.1 (January 18 2021) and mxGraph 4.2.2 (October 28 2020).\
\


## Sample


## Install

### create from source

It will be deployed on maven central shortly.\
In the meantime, locally clone and install dependencies then clone and package this project.\

```bash
git clone https://github.com/myamoto/HeavyFeather.git
cd HeavyFeather
mvn clean install

git clone https://github.com/myamoto/ArchiDuchess.git
cd HeavyFeather
mvn clean install

git clone https://github.com/myamoto/ArchiCherryOnTop.git
cd HeavyFeather
mvn clean package
```

You get archicherryontop-1.0.0.jar (target directory).
### deploy on linux

archicherryontop uses Spring-boot so it gets packaged in a single jar including embedded tomcat server. It can work with a spring-cloud-config server to hold its properties or a local configuration file.\
This is how to make it work with everything included in a single directory on Linux including a local configuration file.\

1) Copy the content of directory [archicherryontop](./src/deploy/archicherryontop) on your linux server.


## Usage


## known issues

## Contributing

PRs accepted.

## License

the code is available under the [Apache License](LICENSE).
