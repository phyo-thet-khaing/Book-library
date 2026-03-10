pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

    environment {
        DOCKER_REPO = 'ptk-book-library'
        DOCKER_HOST_PORT = '8086'
        DOCKER_CONTAINER_PORT = '8080'

        // MySQL Config
        MYSQL_ROOT_PASSWORD = 'root'
        MYSQL_DATABASE = 'springdb'
        MYSQL_CONTAINER_NAME = 'mysql-docker'
        DOCKER_NETWORK = 'library-network'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/phyo-thet-khaing/Book-library.git'
            }
        }

        stage('JaCoCo Report') {
            steps {
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage'
                ])
            }
        }

        stage('Static Code Analysis (Checkstyle + SonarQube)') {
            steps {
                sh 'mvn checkstyle:checkstyle'

                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site',
                    reportFiles: 'checkstyle.html',
                    reportName: 'Checkstyle Report'
                ])

                withSonarQubeEnv('sonar') {
                    sh """
                    mvn sonar:sonar \
                    -Dsonar.projectKey=Book_Library \
                    -Dsonar.projectName=Book_Library
                    """
                }
            }
        }

        stage('Build Jar') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Setup Docker Network') {
            steps {
                sh """
                docker network inspect ${DOCKER_NETWORK} >/dev/null 2>&1 || \
                docker network create ${DOCKER_NETWORK}
                """
            }
        }

        stage('Run MySQL Container') {
            steps {
                sh """
                docker stop ${MYSQL_CONTAINER_NAME} || true
                docker rm ${MYSQL_CONTAINER_NAME} || true
                docker run -d --name ${MYSQL_CONTAINER_NAME} \
                    --network ${DOCKER_NETWORK} \
                    -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
                    -e MYSQL_DATABASE=${MYSQL_DATABASE} \
                    -p 3306:3306 \
                    mysql:latest
                """
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def imageTag = "${env.BUILD_NUMBER}"
                    sh "docker build -t ${DOCKER_REPO}:${imageTag} ."
                    sh "docker tag ${DOCKER_REPO}:${imageTag} ${DOCKER_REPO}:latest"
                    env.IMAGE_TAG = imageTag
                }
            }
        }

        stage('Run Spring Boot Container') {
            steps {
                sh """
                docker stop ${DOCKER_REPO} || true
                docker rm ${DOCKER_REPO} || true
                docker run -d --name ${DOCKER_REPO} \
                    --network ${DOCKER_NETWORK} \
                    -p ${DOCKER_HOST_PORT}:${DOCKER_CONTAINER_PORT} \
                    -e SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_CONTAINER_NAME}:3306/${MYSQL_DATABASE} \
                    -e SPRING_DATASOURCE_USERNAME=root \
                    -e SPRING_DATASOURCE_PASSWORD=${MYSQL_ROOT_PASSWORD} \
                    ${DOCKER_REPO}:${IMAGE_TAG}
                """
                sleep 5
            }
        }

        stage('Acceptance Test') {
            steps {
                sh 'mvn test -Dcucumber.options="classpath:features/library.feature"'
            }
        }

    }

    post {
        success {
            emailext(
                to: 'phyothetkhing2002@gmail.com',
                subject: '✅ Build SUCCESS',
                body: 'Build completed successfully.'
            )
        }
        failure {
            emailext(
                to: 'phyothetkhing2002@gmail.com',
                subject: '❌ Build FAILED',
                body: 'Build failed. Check logs.'
            )
        }
    }
}