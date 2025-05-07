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
    IMAGE_TAG = "v${BUILD_NUMBER}"
    FULL_IMAGE = "${HARBOR_IMAGE}:${IMAGE_TAG}"
  }

  stages {
    stage('Checkout') {
      steps {
        sh 'echo 💡 Checking out branch: ${env.BRANCH_NAME}''
        git branch: "${env.BRANCH_NAME}", url: 'https://github.com/BeeGuardians/Guardians-backend.git'
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
          // 환경 변수 확인용 디버깅
          sh "echo '📦 FULL_IMAGE=${FULL_IMAGE}'"
          sh "ls -alh guardians-backend"

          // 이미지 빌드 및 푸시
          sh """
            /kaniko/executor \
              --context=guardians-backend \
              --dockerfile=guardians-backend/Dockerfile \
              --destination=${FULL_IMAGE} \
              --insecure \
              --skip-tls-verify \
              --verbosity=debug
          """
        }
      }
    }

    stage('Update Deployment Manifest') {
      steps {
        dir('temp-infra-repo') {
          git url: 'git@github.com:BeeGuardians/Guardians-Infra.git', branch: 'dev', credentialsId: 'github-ssh'
          sh "sed -i 's|image: .*|image: ${FULL_IMAGE}|' cloud-cluster/backend/deployment.yaml"
          sh 'git config user.email "ci@yourdomain.com"'
          sh 'git config user.name "Jenkins CI"'
          sh 'git commit -am "release : update image tag to ${FULL_IMAGE}"'
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
