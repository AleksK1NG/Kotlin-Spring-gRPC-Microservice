### Kotlin, Spring WebFlux, gRPC microservice 👋✨💫

#### 👨‍💻 Full list what has been used:
[Spring](https://spring.io/) web framework <br/>
[Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) Reactive REST Services <br/>
[gRPC](https://grpc.io/docs/languages/kotlin/quickstart/) Kotlin gRPC <br/>
[gRPC-Spring-Boot-Starter](https://yidongnan.github.io/grpc-spring-boot-starter/en/) gRPC Spring Boot Starter <br/>
[Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc) a specification to integrate SQL databases using reactive drivers <br/>
[Zipkin](https://zipkin.io/) open source, end-to-end distributed [tracing](https://opentracing.io/) <br/>
[Spring Cloud Sleuth](https://docs.spring.io/spring-cloud-sleuth/docs/current-SNAPSHOT/reference/html/index.html) autoconfiguration for distributed tracing <br/>
[Prometheus](https://prometheus.io/) monitoring and alerting <br/>
[Grafana](https://grafana.com/) for to compose observability dashboards with everything from Prometheus <br/>
[Kubernetes](https://kubernetes.io/) automating deployment, scaling, and management of containerized applications <br/>
[Docker](https://www.docker.com/) and docker-compose <br/>
[Helm](https://helm.sh/) The package manager for Kubernetes <br/>
[Flywaydb](https://flywaydb.org/) for migrations <br/>

All UI interfaces will be available on ports:

#### Swagger UI: http://localhost:8000/webjars/swagger-ui/index.html
<img src="https://i.postimg.cc/KcVmx9mV/Swagger-UI-2022-10-22-16-07-17.png" alt="Swagger"/>

#### Grafana UI: http://localhost:3000
<img src="https://i.postimg.cc/nVsC49zy/Spring-Boot-2-1-System-Monitor-Dashboards-Grafana-2022-10-22-15-57-45.png" alt="Grafana"/>

#### Zipkin UI: http://localhost:9411
<img src="https://i.postimg.cc/JzCmBGD5/Zipkin-2022-10-22-16-06-29.png" alt="Zipkin"/>

#### Prometheus UI: http://localhost:9090
<img src="https://i.postimg.cc/x1Q8VsRQ/Prometheus-Time-Series-Collection-and-Processing-Server-2022-10-22-15-58-29.png" alt="Prometheus"/>


For local development 🙌👨‍💻🚀:

```
make local // for run docker compose
```
or
```
make develop // run all in docker compose
```