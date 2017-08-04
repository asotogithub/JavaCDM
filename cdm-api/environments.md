# Environments Overview

The TruAdvertiser project is deployed on top of [Apache Mesos](http://mesos.apache.org/) and its closely related tools:  [Marathon](https://mesosphere.github.io/marathon/) and [Chronos](https://github.com/mesos/chronos).  These tools provide a solid foundation enabling platform fault tolerance and velocity.  Teaching Mesos and Marathon is out of the scope of this document so if you are interested please follow the guides at the respective links.

## Deployment Overview

A TruAdvertiser deployment is composed of several modules:

[Load Balancer + SSL (A10)] -> [Proxy (HAProxy)] -> [User Interface (NodeJS)] -> [API (Tomcat)]

Unless otherwise noted all instances are deployed as Docker containers.

### Load Balancer

The hardware load balancer handles TLS offloading so the backend applications do not need to support it.  These are configured by IT and point to the clusters primary DNS address on port `32000`.  See the configuration section later in this document for the as-built.

### Proxy

The proxy acts as the 'front door' of an application stack.  You can find it in GIT [here](http://stash.trueffect.com/projects/IN/repos/platform-proxy/browse).  The proxy uses information from Marathon to build a HAProxy configuration every 15 seconds that reflects the current application state.  The proxy is bound to listen on port `32000` and will forward this to the correct backend based on the rules found [here](http://stash.trueffect.com/projects/IN/repos/platform-proxy/browse/lib/haproxy-marathon-bridge.sh).  The proxy should be deployed on every node but the load balancers will automatically redirect to nodes with working proxies.

### User Interface

The User Interface is a packaged version of the ui folder in this project.  It sources the backend address from environment variables passed in to the launch process (see configuration section).

### API

Like the UI, the API is a packaged version of the code found in this project.  It sources its database configuration from environment variables at launch.  (see configuration section).

## Configuration

Each environments configuration is source controlled as much as possible.  All environments expose Mesos at `<ENV DNS ADDRESS>:5050` and Marathon at: `<ENV DNS ADDRESS>:8080`.  The Proxy is accessible on every environment node at `<ENV DNS ADDRESS>:32000`.

| Name | Env Address                  | Config File                                  |
|------|------------------------------|----------------------------------------------|
| DEV  | https://my-dev.trueffect.com | [deploy/config/DEV.sh](deploy/config/DEV.sh) |
| STG  | https://my-stg.trueffect.com | [deploy/config/STG.sh](deploy/config/STG.sh) |
| PRD  | https://my.trueffect.com     | [deploy/config/DEV.sh](deploy/config/DEV.sh) |

## Deployment

Code is deployed to the environments via simple scripts which take a single parameter:

`deploy/api_deploy.sh [DEV|STG|PROD]`

`deploy/ui_deploy.sh [DEV|STG|PROD]`

The environment names correspond to config file entires in the [deploy/config](deploy/config) folder.  These files also specify the Docker image that will be deployed.  The scheme for generating docker image tags can be found [here](deploy/functions.sh) (see the setVersions function).
