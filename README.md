# portainer-api

Java API for accessing Portainer REST enpoints. 

It is built on top of Spotify Docker API for Java and makes possible to execute most of the REST commands on any of the Portainer endpoints. 

Please note that this is an initial release of the library and most of the functionality is still out there. It is intended to encourage other Java developers to play with it and eventually join the project.

It is not ready for production, that is certain. 

# Installing the library

To start using the power of Portainer in your Java application please add following Maven dependency to your pom.xml file:

```
<dependency>
  <groupId>eu.icole</groupId>
  <artifactId>portainer-api</artifactId>
  <version>0.0.1</version>
</dependency>
```
# Using the API

## Connecting to the server

First thing you must do is to initiate the connection with the Portainer server. Use url and combination of username and password to setup the connection. Please remeber that the user you will be using to browse Portainer endpoints must have access right to them.

```
  PortainerConnection connection = PortainerConnection.connect(url, username, password);
```

## Listing endpoints

Now, when your connection has been established you are ready to list the endpoints accessible by your Portainer user:

```
   PortainerEndpoints endpoints = connection.getEndpoints();
   System.out.println(endpoints.toString());
```

## Accessing single node

If you know the name of the endopint you can access it from the PortainerEndpoints object or from the PortainerConnection itself:

```
    PortainerEndpoint localEndpoint = connection.getEndpoint("local");
```

## Use Docer REST API on the endpoint

Now having the reference to the endpoint you can obtain pre-configured DockerClient to utilize on it the REST API offered by Spotify.

```
    DockerClient client = localEndpoint.getDockerClient();
```

To get the documentation on the possible REST commands please visit Spotify Docker API for Java project: https://github.com/spotify/docker-client

For instance to get a list of Containers on the given endpoint use:

```
   List<Container> containers = client.listContainers();
```

Enjoy
