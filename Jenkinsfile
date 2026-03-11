pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

    environment {
        DOCKER_REPO = 'ptk-book-library'
        DOCKER_HOST_PORT = '8086'
        DOCKER_CONTAINER_PORT = '8080'
        DOCKER_NETWORK = 'library-network'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/phyo-thet-khaing/Book-library.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Code Quality Analysis (SonarQube)') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh """
                    mvn sonar:sonar \
                    -Dsonar.projectKey=Book_Library \
                    -Dsonar.projectName=Book_Library
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
                script {
                    def imageTag = "${env.BUILD_NUMBER}"
                    sh "docker build -t ${DOCKER_REPO}:${imageTag} ."
                    sh "docker tag ${DOCKER_REPO}:${imageTag} ${DOCKER_REPO}:latest"
                    env.IMAGE_TAG = imageTag
                }
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

        stage('Run Spring Boot Container') {
            steps {
                sh """
                docker stop ${DOCKER_REPO} || true
                docker rm ${DOCKER_REPO} || true

                docker run -d --name ${DOCKER_REPO} \
                --network ${DOCKER_NETWORK} \
                -p ${DOCKER_HOST_PORT}:${DOCKER_CONTAINER_PORT} \
                ${DOCKER_REPO}:${IMAGE_TAG}
                """
                sleep 10
            }
        }

        // stage('Acceptance Test') {
        //     steps {
        //         sh 'mvn test -Dcucumber.options="classpath:features/library.feature"'
        //     }
        // }


        stage('Acceptance Test') {
            steps {
                sh 'mvn verify -Pacceptance'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/cucumber-reports/*.xml'

                    publishHTML(target: [
                        allowMissing: true,
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        reportDir: 'target/cucumber-reports',
                        reportFiles: 'cucumber-report.html',
                        reportName: 'Acceptance Report'
                    ])
                }
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
                body: 'Build failed. Check Jenkins logs.'
            )
        }
    }
}