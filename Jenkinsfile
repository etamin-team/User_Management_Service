pipeline {
    agent any
    tools {
        maven 'Maven'
    }
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/etamin-team/User_Management_Service.git', branch: 'master'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean install'
            }
        }
        stage('Deploy to Server') {
            steps {
                script {
                    sshagent(credentials: ['server-ssh-key']) {
                        bat '''
                        ssh root@209.38.109.22 << EOF
                            cd /root/User_Management_Service
                            git pull
                            sleep 5
                            mvn clean install
                            PORT=$(yq e '.server.port' src/main/resources/application.yaml)
                            PID=$(lsof -t -i:$PORT -sTCP:LISTEN)
                            if [ -n "$PID" ]; then
                                kill $PID
                            fi
                            nohup java -jar target/User_Management_Service-0.0.1-SNAPSHOT.jar > prod.log 2>&1 &
                            exit
                        EOF
                        '''
                    }
                }
            }
        }
    }
}