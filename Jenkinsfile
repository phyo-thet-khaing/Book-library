pipeline {
    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }

    tools {
        maven "maven3.9"
    }

    environment {
        DOCKER_REPO = 'ptk-book-library'
        DOCKER_HOST_PORT = '8086'
        DOCKER_CONTAINER_PORT = '8080'
        DOCKER_NETWORK = 'library-network'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/phyo-thet-khaing/Book-library.git'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Code Quality Analysis (SonarQube)') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh """
                    mvn sonar:sonar \
                    -Dsonar.projectKey=Book_Library \
                    -Dsonar.projectName=Book_Library \
                    -Dsonar.host.url=$SONAR_HOST_URL
                    """
                }
            }
        }

        stage('Code Coverage (JaCoCo)') {
            steps {
                sh 'mvn jacoco:report'
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

        stage('Coding Standards (Checkstyle)') {
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
            }
        }

        stage('Docker Build') {
            steps {
                sh """
                docker build -t ${DOCKER_REPO}:${IMAGE_TAG} .
                docker tag ${DOCKER_REPO}:${IMAGE_TAG} ${DOCKER_REPO}:latest
                """
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

        stage('Deploy Container') {
            steps {
                sh """
                docker stop ${DOCKER_REPO} || true
                docker rm ${DOCKER_REPO} || true

                docker run -d --name ${DOCKER_REPO} \
                --network ${DOCKER_NETWORK} \
                -p ${DOCKER_HOST_PORT}:${DOCKER_CONTAINER_PORT} \
                ${DOCKER_REPO}:${IMAGE_TAG}
                """
            }
        }

        stage('Wait For App') {
            steps {
                sh """
                for i in {1..20}; do
                    curl -s http://localhost:${DOCKER_HOST_PORT} && break
                    echo "Waiting for app..."
                    sleep 3
                done
                """
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
                subject: "✅ Build SUCCESS #${BUILD_NUMBER}",
                body: "Build completed successfully."
            )
        }

        failure {
            emailext(
                to: 'phyothetkhing2002@gmail.com',
                subject: "❌ Build FAILED #${BUILD_NUMBER}",
                body: "Build failed. Check Jenkins logs."
            )
        }
    }
}