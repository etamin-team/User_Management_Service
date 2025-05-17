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
                    withCredentials([sshUserPrivateKey(credentialsId: 'server-ssh-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                        // Write a Bash script for remote execution
                        writeFile file: 'deploy.sh', text: '''
                            #!/bin/bash
                            cd /root/User_Management_Service
                            git pull
                            # Debug: Check application.yaml
                            cat src/main/resources/application.yaml
                            # Restart PostgreSQL and wait
                            sudo systemctl restart postgresql
                            sleep 5
                            # Test database connection
                            export PGPASSWORD=postgres
                            psql -h 127.0.0.1 -U postgres -d world_medicine -c "SELECT 1;" || echo "Database connection failed"
                            # Build and run the application
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
                        '''
                        // Use Windows batch to copy and execute the script remotely
                        bat '''
                            scp -i %SSH_KEY% deploy.sh %SSH_USER%@209.38.109.22:/tmp/deploy.sh
                            ssh -i %SSH_KEY% %SSH_USER%@209.38.109.22 "bash /tmp/deploy.sh"
                        '''
                    }
                }
            }
        }
    }
}