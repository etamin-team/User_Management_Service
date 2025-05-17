pipeline {
    agent any
    tools {
        maven 'Maven'  // Ensure 'Maven' is configured in Global Tool Configuration
    }
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/etamin-team/User_Management_Service.git', branch: 'main'
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
                    def deployScript = '''
                        cd /root/User_Management_Service
                        git pull
                        mvn clean install
                        pkill -f 'java -jar.*User_Management_Service' || true
                        nohup java -jar target/User_Management_Service-0.0.1-SNAPSHOT.jar > prod.log 2>&1 &
                        exit
                    '''
                    sshagent(credentials: ['server-ssh-key']) {
                        bat """
                        scp target/User_Management_Service-0.0.1-SNAPSHOT.jar root@209.38.109.22:/root/User_Management_Service/
                        ssh root@209.38.109.22 << EOF
                        ${deployScript}
                        EOF
                        """
                    }
                }
            }
        }
    }
}