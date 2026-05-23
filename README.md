# webapp

A Spring Boot REST API built as the deployment target for my AWS cloud computing coursework.

**This repo is intentionally simple.** The substantive work for this project — Terraform infrastructure, AMI baking with Packer, autoscaling, load balancing, IAM, KMS, Secrets Manager, CI/CD on AWS — lives in the companion repo: **[tf-aws-infra →](https://github.com/kms-kalyan/tf-aws-infra)**

This service exists to give that infrastructure something to deploy. It exposes a small set of authenticated and unauthenticated REST endpoints, persists to Postgres, and is packaged into an AMI by Packer for deployment via the Terraform stack.

## Stack

- **Language:** Java 17
- **Framework:** Spring Boot, Hibernate ORM
- **Database:** PostgreSQL
- **Build:** Maven
- **Containerization:** Docker, docker-compose (for local dev)
- **Image baking:** Packer (AMI used by [tf-aws-infra](https://github.com/kms-kalyan/tf-aws-infra))
- **CI/CD:** GitHub Actions
- **Load testing:** jMeter

## Running locally

```bash
git clone https://github.com/kms-kalyan/webapp.git
cd webapp

# Configure your local Postgres in src/main/resources/application.properties
mvn clean package
java -jar target/webapp-0.0.1-SNAPSHOT.jar

# API available at http://localhost:8080
```

Health check: `GET /healthz`

## Related

- **[tf-aws-infra](https://github.com/kms-kalyan/tf-aws-infra)** — the Terraform stack that provisions and deploys this service on AWS. Start there if you're evaluating my cloud / IaC work.
