pipeline {
    agent {
        kubernetes {
            label 'jhipster'
            containerTemplate {
                name 'jhipster'
                image 'jhipster/jhipster:v7.8.1'
            }
        }
    }
    stages {
        stage('check java') {
            steps {
                sh 'java -version'
            }
        }

        stage('clean') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -ntp clean -P-webpack'
            }
        }

        stage('nohttp') {
            when {
                not {
                    branch 'main'
                }
            }
            steps {
                sh './mvnw -ntp checkstyle:check'
            }
        }

        stage('install tools') {
            steps {
                sh './mvnw -ntp com.github.eirslett:frontend-maven-plugin:install-node-and-npm -DnodeVersion=v14.17.0 -DnpmVersion=6.14.13'
            }
        }

        stage('npm install') {
            steps {
                sh './mvnw -ntp com.github.eirslett:frontend-maven-plugin:npm'
            }
        }

        stage('backend tests') {
            when {
                not {
                    branch 'main'
                }
            }
            steps {
                script {
                    try {
                        sh './mvnw -ntp verify -P-webpack'
                    } catch (err) {
                        throw err
                    } finally {
                        junit '**/target/test-results/**/TEST-*.xml'
                    }
                }
            }
        }

        stage('frontend tests') {
            when {
                not {
                    branch 'main'
                }
            }
            steps {
                script {
                    try {
                        sh "./mvnw -ntp com.github.eirslett:frontend-maven-plugin:npm -Dfrontend.npm.arguments='run test-ci'"
                    } catch (err) {
                        throw err
                    } finally {
                        junit '**/target/test-results/**/TEST-*.xml'
                    }
                }
            }
        }

        stage('quality analysis') {
            when {
                not {
                    branch 'main'
                }
            }
            steps {
                withSonarQubeEnv('sonar') {
                    sh './mvnw -ntp initialize sonar:sonar'
                }
            }
        }

        stage('packaging') {
            steps {
                sh './mvnw -ntp verify -P-webpack -Pprod -DskipTests'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }

        stage('publish docker') {
            when {
                branch 'main'
            }
            steps {
                withCredentials([string(credentialsId: 'REGISTRY_USERNAME', variable: 'REGISTRY_USERNAME'), string(credentialsId: 'REGISTRY_PASSWORD', variable: 'REGISTRY_PASSWORD')]) {                    // A pre-requisite to this step is to setup authentication to the docker registry
                    // https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#authentication-methods
                    sh './mvnw -ntp jib:build -Djib.to.auth.username="${REGISTRY_USERNAME}" -Djib.to.auth.password="${REGISTRY_PASSWORD}"'
                }
            }
        }
    }
}
