pipeline {
 agent any
 options { timestamps(); disableConcurrentBuilds() }
 stages {
  stage('Java 21 tests') { steps { dir('services/incident-service') { sh 'mvn -B test' }; dir('services/notification-service') { sh 'mvn -B test' }; dir('services/legacy-soap-adapter') { sh 'mvn -B test' } } }
  stage('React build') { steps { dir('web') { sh 'npm ci || npm install'; sh 'npm run build' } } }
  stage('Container build') { when { expression { return env.BUILD_CONTAINERS == 'true' } } steps { sh 'docker compose --env-file .env.example build' } }
 }
 post { always { archiveArtifacts artifacts: 'services/**/target/surefire-reports/**', allowEmptyArchive: true } }
}
