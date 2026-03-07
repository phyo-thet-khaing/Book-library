pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    environment {
        DOCKER_IMAGE = "phyo-thet-khaing/book-library"
        TAG = "latest"
    }

    stages {

        stage('Clone Repository') {
            steps {
                git 'https://github.com/phyo-thet-khaing/Book-library.git'
            }
        }

        stage('Build Project') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Acceptance Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:$TAG .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub',
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS'
                )]) {

                    sh 'echo $PASS | docker login -u $USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE:$TAG'
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline executed successfully"
        }

        failure {
            echo "Pipeline failed"
        }
    }
}