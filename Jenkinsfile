pipeline {
    agent any
    stages {
        stage('Checkout Code') {
            steps {
                echo "Fetching the latest code from GitHub..."
                git url: 'https://github.com/etamin-team/User_Management_Service.git', branch: 'master'
            }
        }
        stage('Prepare Deployment Script') {
            steps {
                echo "Writing the deployment script (deploy.sh)..."
                writeFile file: 'deploy.sh', text: '''
                    #!/bin/bash
                    echo "Starting deployment on remote server..."
                    echo "Moving to project directory..."
                    cd /root/User_Management_Service || { echo "Directory not found"; exit 1; }
                    echo "Pulling the latest code..."
                    git pull || { echo "Git pull failed"; exit 1; }
                    echo "Checking application.yaml..."
                    cat src/main/resources/application.yaml || { echo "application.yaml not found"; exit 1; }
                    echo "Restarting PostgreSQL service..."
                    sudo systemctl restart postgresql
                    sleep 5
                    echo "Testing database connection..."
                    export PGPASSWORD=postgres
                    psql -h 127.0.0.1 -U postgres -d world_medicine -c "SELECT 1;" || { echo "Database connection failed"; exit 1; }
                    echo "Building the application with Maven..."
                    mvn clean install || { echo "Maven Roslyn build failed"; exit 1; }
                    echo "Finding server port from application.yaml..."
                    PORT=$(yq e '.server.port' src/main/resources/application.yaml)
                    echo "Killing any process running on port $PORT..."
                    PID=$(lsof -t -i:$PORT -sTCP:LISTEN)
                    if [ -n "$PID" ]; then
                        kill -9 $PID
                        echo "Killed process with PID $PID"
                    else
                        echo "No process found on port $PORT"
                    fi
                    echo "Starting the application in the background..."
                    nohup java -jar target/User_Management_Service-0.0.1-SNAPSHOT.jar > prod.log 2>&1 &
                    sleep 5
                    echo "Checking the latest logs..."
                    tail -n 20 prod.log
                '''
            }
        }
        stage('Copy Script to Remote Server') {
            steps {
                echo "Copying deploy.sh to the remote server (209.38.109.22)..."
                withCredentials([sshUserPrivateKey(credentialsId: 'server-ssh-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                    bat 'scp -i %SSH_KEY% deploy.sh %SSH_USER%@209.38.109.22:/tmp/deploy.sh'
                }
            }
        }
        stage('Execute Deployment on Remote Server') {
            steps {
                echo "Running deploy.sh on the remote server..."
                withCredentials([sshUserPrivateKey(credentialsId: 'server-ssh-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                    bat 'ssh -i %SSH_KEY% %SSH_USER%@209.38.109.22 "bash /tmp/deploy.sh"'
                }
            }
        }
        stage('Verify Deployment Status') {
            steps {
                echo "Checking the deployment logs on the remote server..."
                withCredentials([sshUserPrivateKey(credentialsId: 'server-ssh-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                    bat 'ssh -i %SSH_KEY% %SSH_USER%@209.38.109.22 "tail -n 20 /root/User_Management_Service/prod.log"'
                }
            }
        }
    }
}