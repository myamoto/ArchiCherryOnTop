# ArchiCherryOnTop

[![license](https://img.shields.io/github/license/myamoto/ArchiCherryOnTop.svg)](LICENSE)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg?style=flat-square)](https://github.com/myamoto/ArchiCherryOnTop)

Secured REST API to retrieve [MxGraph](https://jgraph.github.io/mxgraph/) schemas [Archi](https://www.archimatetool.com/download) files stored in Gitlab.\
At the time or writing (23/03/2021) it is compatible with Archi 4.8.1 (January 18 2021) and mxGraph 4.2.2 (October 28 2020).\
\


## Sample


## Install

archicherryontop uses Spring-boot so it gets packaged in a single jar including embedded tomcat server. It can work with a spring-cloud-config server to hold its properties or a local configuration file.\

### create from source

It will be deployed on maven central shortly.\
In the meantime, locally clone and install dependencies then clone and package this project.\

```bash
mkdirs /data/toolup/projects
cd /data/toolup/projects

git clone https://github.com/myamoto/HeavyFeather.git
cd HeavyFeather
mvn clean install

cd /data/toolup/projectss
git clone https://github.com/myamoto/ArchiDuchess.git
cd ArchiDuchess
mvn clean install

cd /data/toolup/projects
git clone https://github.com/myamoto/ArchiCherryOnTop.git
cd ArchiCherryOnTop
mvn clean package
```

your artifact is there : /data/toolup/projects/ArchiCherryOnTop/target/archicherryontop-1.0.0.jar
### deploy on linux

This is how to make it work with everything included in a single directory on Linux including a local configuration file.\

1) create directory /opt/services/archicherryontop
2) copy archicherryontop-1.0.0.jar in /opt/services/archicherryontop and rename to archicherryontop.jar
3) download [archicherryontop](./src/deploy/archicherryontop) and copy in /opt/services/archicherryontop
You should now have this :\
- /opt/services/archicherryontop/archicherryontop.jar
- /opt/services/archicherryontop/launch.sh
- /opt/services/archicherryontop/Log4j_custom.properties
- /opt/services/archicherryontop/application.properties
4) Customize
- edit [gitlab settings](./src/deploy/archicherryontop/application.properties)
- edit [server and OAuth settings](./src/deploy/archicherryontop/launch.sh)
- edit [log path](https://github.com/myamoto/ArchiCherryOnTop/blob/master/src/deploy/archicherryontop/Log4j_custom.properties#L7)

## Usage


## known issues

## Contributing

PRs accepted.

## License

the code is available under the [Apache License](LICENSE).
