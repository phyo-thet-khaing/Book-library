pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

    environment {
        DOCKER_REPO = 'ptk-book-library'
        DOCKER_HOST_PORT = '8086'
        DOCKER_CONTAINER_PORT = '8080'
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

        stage('Run Docker Container') {
            steps {
                sh """
                    docker stop ptk-book-library || true
                    docker rm ptk-book-library || true
                    docker run -d --name ptk-book-library -p ${DOCKER_HOST_PORT}:${DOCKER_CONTAINER_PORT} ${DOCKER_REPO}:${IMAGE_TAG}
                """
                // Give the container a few seconds to start
                sleep 5
            }
        }

        // ✅ Added Acceptance Test Stage
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