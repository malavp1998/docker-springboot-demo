
# Dockerizing a Spring Boot Application

This guide covers the steps to containerize a Spring Boot application using Docker.

## Prerequisites
- Install [Docker](https://docs.docker.com/get-docker/)
- Have a Spring Boot application ready
- A [Docker Hub](https://hub.docker.com/) account (for pushing images)

## 1. Create a `Dockerfile`

Inside the root of your Spring Boot project, create a `Dockerfile`:

```dockerfile
# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/my-spring-app.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 2. Build the Docker Image

Navigate to the project root directory and run:
```sh
docker build -t my-spring-app .
```

To verify the image:
```sh
docker images
```
Example output:
```
REPOSITORY         TAG       IMAGE ID       CREATED          SIZE
my-spring-app     latest    abcdef123456   10 minutes ago   150MB
```

## 3. Run the Docker Container

To start the container and map port 8080:
```sh
docker run -p 8080:8080 my-spring-app
```

Now, access your application at:
```
http://localhost:8080
```

To run the container in detached mode:
```sh
docker run -d -p 8080:8080 my-spring-app
```

To view running containers:
```sh
docker ps
```

## 4. Tag and Push to Docker Hub

### Tag the Image
```sh
docker tag my-spring-app your-docker-hub-username/my-spring-app:v1.0
```

### Login to Docker Hub
```sh
docker login
```

### Push the Image
```sh
docker push your-docker-hub-username/my-spring-app:v1.0
```

## 5. Pull the Image from Docker Hub

On another machine or server, pull the image:
```sh
docker pull your-docker-hub-username/my-spring-app:v1.0
```

## 6. Remove Unused Images and Containers

### Remove a Specific Container
```sh
docker rm container_id
```

### Remove a Specific Image
```sh
docker rmi my-spring-app
```

### Stop and Remove All Containers
```sh
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)
```

### Remove All Unused Images
```sh
docker image prune -a
```

## Summary
- **Dockerfile** defines how to containerize the application.
- **`docker build`** creates an image.
- **`docker run`** starts a container.
- **`docker ps`** checks running containers.
- **`docker tag` & `docker push`** publish images to Docker Hub.
- **`docker pull`** downloads images.
- **`docker rm` & `docker rmi`** clean up resources.

Happy Containerizing! 🚀







# Deploying a Spring Boot App on Kubernetes

This guide covers the steps to deploy a Spring Boot application using Kubernetes.

## Prerequisites
- Install [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- Install [kubectl](https://kubernetes.io/docs/tasks/tools/)
- Docker installed and running
- A Docker image of the Spring Boot application (pushed to Docker Hub or a local registry)

## Steps to Deploy

### 1. Create `deployment.yaml`

The `deployment.yaml` file is placed in the root directory to manage the application deployment.

**Example `deployment.yaml` file:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-spring-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-spring-app
  template:
    metadata:
      labels:
        app: my-spring-app
    spec:
      containers:
      - name: my-spring-app
        image: your-docker-hub-username/my-spring-app:latest
        ports:
        - containerPort: 8080
```

Apply the deployment:
```sh
kubectl apply -f deployment.yaml
```

To verify the deployment:
```sh
kubectl get deployments
```
Output example:
```
NAME            READY   UP-TO-DATE   AVAILABLE   AGE
my-spring-app   0/3     3            0           27s
```

To check the running pods:
```sh
kubectl get pods
```
Example output:
```
NAME                             READY   STATUS    RESTARTS   AGE
my-spring-app-5b8d8d85dd-4cqt6   1/1     Running   0          5m3s
my-spring-app-5b8d8d85dd-n5p6w   1/1     Running   0          5m3s
my-spring-app-5b8d8d85dd-njf9h   1/1     Running   0          5m3s
```

To check logs of a specific instance:
```sh
kubectl logs my-spring-app-5b8d8d85dd-4cqt6
```
Example log output:
```
:: Spring Boot :: (v3.4.4)
2025-04-03T17:55:35.279Z INFO 1 --- [docket-k8s] c.l.docket_k8s.DocketK8sApplication : Starting DocketK8sApplication v0.0.1-SNAPSHOT using Java 17.0.2
```

### 2. Create `service.yaml`

The `service.yaml` file is used to expose the application to the network.

**Example `service.yaml` file:**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-spring-app
spec:
  type: NodePort
  selector:
    app: my-spring-app
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
      nodePort: 31882
```

Apply the service configuration:
```sh
kubectl apply -f service.yaml
```
Check the created service:
```sh
kubectl get services
```
Example output:
```
NAME            TYPE        CLUSTER-IP    EXTERNAL-IP   PORT(S)          AGE
kubernetes      ClusterIP   10.96.0.1     <none>        443/TCP          156m
my-spring-app   NodePort    10.101.60.3   <none>        8080:31882/TCP   11s
```

### 3. Get Node Information

```sh
kubectl get nodes -o wide
```
Example output:
```
NAME       STATUS   ROLES           AGE    VERSION   INTERNAL-IP    EXTERNAL-IP   OS-IMAGE             KERNEL-VERSION     CONTAINER-RUNTIME
minikube   Ready    control-plane   161m   v1.26.3   192.168.49.2   <none>        Ubuntu 20.04.5 LTS   6.10.14-linuxkit   docker://23.0.2
```
Find the Minikube IP:
```sh
minikube ip
```
Example output:
```
192.168.49.2
```

### 4. Accessing the Application

Try accessing the application using the NodePort:
```
http://192.168.49.2:31882/api/hello
```
If it does not work, use Minikube’s service forwarding:
```sh
minikube service my-spring-app --url
```
Example output:
```
http://127.0.0.1:50720
```
Access the app at:
```
http://127.0.0.1:50720/api/hello
```

### 5. Scaling the Deployment

To scale the application (e.g., increase replicas to 5):
```sh
kubectl scale deployment my-spring-app --replicas=5
```
Verify the update:
```sh
kubectl get deployments
```

### 6. Deleting the Deployment and Service

To delete the deployment:
```sh
kubectl delete deployment my-spring-app
```
To delete the service:
```sh
kubectl delete service my-spring-app
```

### 7. Open Kubernetes Dashboard

To access the Kubernetes dashboard:
```sh
minikube dashboard
```

This will open the Kubernetes UI for managing resources.

## Summary
- `deployment.yaml` manages the application pods.
- `service.yaml` exposes the app to the network.
- Use `kubectl` commands to monitor deployments and services.
- Use `minikube service` to access the app if the NodePort is not working.
- The Kubernetes dashboard provides a UI to monitor deployments.
- Scaling and deletion of deployments can be done using `kubectl scale` and `kubectl delete` commands.

Happy Deploying! 🚀

