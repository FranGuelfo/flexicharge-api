pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    stages {
        stage('🚚 Checkout') {
            steps {
                // 'checkout scm' es perfecto para Multibranch
                checkout scm
            }
        }

        stage('🔨 Build & Test') {
            steps {
                // El flag -B es para 'Batch mode' (evita logs basura de descarga)
                sh 'mvn -B clean verify'
            }
        }

        stage('🔍 SonarQube Analysis') {
            steps {
                // Usamos el nombre exacto que pusiste en Manage Jenkins -> System
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=flexicharge'
                }
            }
        }
    }

    post {
        always {
            // Limpia el espacio de trabajo para no llenar el disco de Docker
            deleteDir()
        }
    }
}