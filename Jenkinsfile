pipeline {
  agent {
    kubernetes {
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
    - name: kaniko
      image: gcr.io/kaniko-project/executor:latest
      command:
        - cat
      tty: true
      volumeMounts:
        - name: docker-config
          mountPath: /kaniko/.docker
  volumes:
    - name: docker-config
      secret:
        secretName: harbor-secret
"""
    }
  }

  environment {
    HARBOR_HOST = "192.168.0.11:30401"
    HARBOR_IMAGE = "${HARBOR_HOST}/guardians/backend"
    IMAGE_TAG = "v${env.BUILD_NUMBER}"
    FULL_IMAGE = "${HARBOR_IMAGE}:${IMAGE_TAG}"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Gradle Build') {
      steps {
        dir('guardians-backend') {
          sh './gradlew clean build -x test'
        }
      }
    }

    stage('Kaniko Build & Push') {
      steps {
        container('kaniko') {
          sh '''
            /kaniko/executor \
              --context=guardians-backend \
              --dockerfile=guardians-backend/Dockerfile \
              --destination=$FULL_IMAGE \
              --insecure \
              --skip-tls-verify
          '''
        }
      }
    }

    stage('Update Deployment Manifest') {
      steps {
        dir('temp-infra-repo') {
          git url: 'git@github.com:BeeGuardians/Guardians-Infra.git', branch: 'dev', credentialsId: 'github-ssh'
          sh "sed -i 's|image: .*|image: $FULL_IMAGE|' cloud-cluster/backend/deployment.yaml"
          sh 'git config user.email "ci@yourdomain.com"'
          sh 'git config user.name "Jenkins CI"'
          sh 'git commit -am "release : update image tag to $FULL_IMAGE"'
          sh 'git push origin dev'
        }
      }
    }
  }

  post {
    success {
      echo "✅ Pipeline completed successfully."
    }
    failure {
      echo "❌ Pipeline failed."
    }
  }
}
