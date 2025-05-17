pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/etamin-team/User_Management_Service.git', branch: 'master'
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
                            # Debug: Verify application.yaml
                            cat src/main/resources/application.yaml
                            # Ensure PostgreSQL is running
                            sudo systemctl restart postgresql
                            sleep 5
                            # Test database connection
                            psql -h 127.0.0.1 -U postgres -d world_medicine -c "SELECT 1;" || echo "DB connection failed"
                            mvn clean install
                            PORT=$(yq e '.server.port' src/main/resources/application.yaml)
                            PID=$(lsof -t -i:$PORT -sTCP:LISTEN)
                            if [ -n "$PID" ]; then
                                kill $PID
                            fi
                            nohup java -jar target/User_Management_Service-0.0.1-SNAPSHOT.jar > prod.log 2>&1 &
                            sleep 5
                            # Debug: Check logs
                            cat prod.log
                            exit
                        EOF
                        '''
                    }
                }
            }
        }
    }
}