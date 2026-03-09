pipeline {
agent any

```
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

    stage('Unit Test'){
        steps{
            sh 'mvn test'
        }
        post {
            always {
                junit 'target/surefire-reports/*.xml'
            }
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
                -Dsonar.projectKey=book-library \
                -Dsonar.projectName=book-library
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
            echo "Running Docker container..."
            sh """
                docker stop ptk-book-library || true
                docker rm ptk-book-library || true
                docker run -d --name ptk-book-library -p ${DOCKER_HOST_PORT}:${DOCKER_CONTAINER_PORT} ${DOCKER_REPO}:${IMAGE_TAG}
            """
        }
    }
}

post {
    always {
        echo "✅ Pipeline finished."
    }
    success {
        echo "🎉 Pipeline succeeded! App running at http://localhost:${DOCKER_HOST_PORT}/"
        emailext(
            to: 'phyothetkhing2002@gmail.com',
            subject: '✅ Build SUCCESS',
            body: 'Build completed successfully.'
        )
    }
    failure {
        echo "❌ Pipeline failed."
        emailext(
            to: 'phyothetkhing2002@gmail.com',
            subject: '❌ Build FAILED',
            body: 'Build failed. Check logs.'
        )
    }
}
```

}
