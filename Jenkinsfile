pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        SONAR_TOKEN = credentials('SONAR_TOKEN')
    }

    stages {
        stage('🚚 Checkout') {
            steps {
                checkout scm
            }
        }

        stage('🔨 Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('🔍 SonarQube Analysis') {
            steps {
                sh 'mvn sonar:sonar ' +
                   '-Dsonar.projectKey=flexicharge ' +
                   '-Dsonar.host.url=http://sonarqube:9000 ' +
                   '-Dsonar.login=${SONAR_TOKEN}'
            }
        }
    }
}