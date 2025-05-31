pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/SoloSoyJuan/ecommerce-microservice-backend-app.git'
            }
        }
        stage('Build') {
            steps {

                sh 'mvn clean package'
            }
        }
        stage('Unit and Integration Tests') {
      steps {
        sh '''
          mvn clean verify -DskipTests=false
        '''
      }
    }
    stage('Docker Build & Push') {
        steps {
            script {
                dockerImage = docker.build("yourdockerhub/user-service:${env.BUILD_ID}")
                docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                    dockerImage.push("${env.BUILD_ID}")
                    dockerImage.push("latest")
                }
            }
            {
                sh '''
                    echo "Logging in to Docker Hub..."
                    echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

                    echo "Building and pushing Docker images..."
                    docker-compose -f core.yml build
                    docker-compose -f core.yml push
                    docker-compose -f compose.yml build
                    docker-compose -f compose.yml push

                    echo "Logout from Docker Hub..."
                    docker logout
                '''
            }
        }
    }
    stage('Deploy to Kubernetes') {
        steps {
            sh '''
                echo "cores deployment started"
                kubectl apply -f k8s/core/zipkin-deployment.yml
                kubectl wait --for=condition=ready pod -l app=zipkin --timeout=200s
                
                kubectl apply -f k8s/core/service-discovery-deployment.yml
                kubectl wait --for=condition=ready pod -l app=service-discovery --timeout=300s

                kubectl apply -f $k8s/core/cloud-config-deployment.yml
                kubectl wait --for=condition=ready pod -l app=cloud-config --timeout=300s

                kubectl apply -f $k8s/core/locust-deployment.yml
                kubectl wait --for=condition=ready pod -l app=cloud-config --timeout=300s

                echo "cores deployment finished"
            '''
        }
    }
    stage('Desplegar microservicios') {
      steps {
        sh """
          echo "Deploying Microservices..."
          kubectl apply -f k8s/dev/api-gateway-deployment.yml
          kubectl wait --for=condition=ready pod -l app=api-gateway --timeout=300s
          
          kubectl apply -f k8s/dev/order-service-deployment.yml
          kubectl wait --for=condition=ready pod -l app=order-service --timeout=300s
          
          kubectl apply -f k8s/dev/product-service-deployment.yml
          kubectl wait --for=condition=ready pod -l app=product-service --timeout=300s
          
          kubectl apply -f k8s/dev/user-service-deployment.yml
          kubectl wait --for=condition=ready pod -l app=user-service --timeout=300s
        """
      }
    }
    }
}