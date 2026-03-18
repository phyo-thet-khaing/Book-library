pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

    environment 
    {
        IMAGE_NAME = 'ptk-book-library'
        VERSION = "${BUILD_NUMBER}"
        CONTAINER_NAME = 'ptk-book-library-container'
        DOCKER_NETWORK = 'library-network'
    }

stages {

    stage('Checkout') {
        steps {
            git branch: 'main',
                url: 'https://github.com/phyo-thet-khaing/Book-library.git'
        }
    }

    stage('Build') {
        steps {
            sh 'mvn -B clean compile'
        }
    }

    // stage('Unit Tests') {
    //     steps {
    //         sh 'mvn test'
    //     }
    //     post {
    //         always {
    //             junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
    //         }
    //     }
    // }

    // stage('Code Quality') {
    //     parallel {

    //         stage('Checkstyle') {
    //             steps {
    //                 sh 'mvn checkstyle:checkstyle'
    //             }
    //             post {
    //                 always {
    //                     publishHTML(target: [
    //                         allowMissing: true,
    //                         keepAll: true,
    //                         alwaysLinkToLastBuild: true,
    //                         reportDir: 'target/site',
    //                         reportFiles: 'checkstyle.html',
    //                         reportName: 'Checkstyle Report'
    //                     ])
    //                 }
    //             }
    //         }

    //         stage('Coverage') {
    //             steps {
    //                 sh 'mvn jacoco:report'
    //             }
    //             post {
    //                 always {
    //                     publishHTML(target: [
    //                         allowMissing: true,
    //                         keepAll: true,
    //                         alwaysLinkToLastBuild: true,
    //                         reportDir: 'target/site/jacoco',
    //                         reportFiles: 'index.html',
    //                         reportName: 'Coverage Report'
    //                     ])
    //                 }
    //             }
    //         }
    //     }
    // }

    // stage('Code Analysis') {
    //     steps {
    //         withSonarQubeEnv('sonar') {
    //             sh """
    //                 mvn clean verify sonar:sonar \
    //                -Dsonar.projectKey=Book_Library \
    //                 -Dsonar.projectName=Book_Library \
                    
    //             """
    //         }
    //     }
    // }

    stage('Package') {
        steps {
            sh 'mvn -B package -DskipTests'
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }

    stage('Docker Build') {
        steps {
            sh """
                docker build \
                -t ${IMAGE_NAME}:${VERSION} \
                -t ${IMAGE_NAME}:latest .
            """
        }
    }

    stage('Setup Network') {
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
                docker stop ${CONTAINER_NAME} || true
                docker rm ${CONTAINER_NAME} || true

                docker run -d \
                    --name ${CONTAINER_NAME} \
                    --network ${DOCKER_NETWORK} \
                    -p 8086:8080 \
                    ${IMAGE_NAME}:${VERSION}
            """
        }
    }

    stage('Wait For App') {
        steps {
            sh '''
                for i in {1..20}
                do
                  curl -s http://localhost:8086 && break
                  echo "Waiting for app..."
                  sleep 3
                done
            '''
        }
    }

    // stage('Acceptance Test') {
    //     steps {
    //         sh 'mvn verify'
    //     }

    //     post {
    //         always {
    //             junit allowEmptyResults: true, testResults: '**/target/cucumber-reports/*.xml'

    //             publishHTML(target: [
    //                 allowMissing: true,
    //                 keepAll: true,
    //                 alwaysLinkToLastBuild: true,
    //                 reportDir: 'target/cucumber-reports',
    //                 reportFiles: 'cucumber-report.html',
    //                 reportName: 'Acceptance Report'
    //             ])
    //         }
    //     }
    // }

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
                body: 'Build failed. Check Jenkins logs.'
            )
        }
    }
}