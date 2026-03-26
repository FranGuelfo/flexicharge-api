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
                        withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar -Dsonar.projectKey=flexicharge -DskipTests'                        }
                        // Esta es la línea mágica que espera la respuesta del Webhook
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }

        stage('📦 Build & Push Docker Image') {
                    // Solo se ejecuta si estamos en la rama main o develop y lo anterior fue bien
                    when {
                        expression { env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'develop' }
                    }
                    steps {
                        script {
                            // Sustituye 'tu-usuario' por tu nombre en Docker Hub
                            def dockerImage = 'franguelfo/flexicharge-app'

                            // Construimos la imagen usando las credenciales
                            withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDS',
                                             usernameVariable: 'DOCKER_USER',
                                             passwordVariable: 'DOCKER_PASS')]) {

                                sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                                sh "docker build -t ${dockerImage}:${env.BUILD_NUMBER} ."
                                sh "docker build -t ${dockerImage}:latest ."
                                sh "docker push ${dockerImage}:${env.BUILD_NUMBER}"
                                sh "docker push ${dockerImage}:latest"
                            }
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