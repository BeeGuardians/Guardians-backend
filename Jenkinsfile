pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    app: jenkins-kaniko
spec:
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    command: ['sleep']
    args: ['infinity']
    volumeMounts:
    - name: docker-config
      mountPath: /kaniko/.docker
    - name: workspace-volume
      mountPath: /workspace
  volumes:
  - name: docker-config
    secret:
      secretName: harbor-secret
  - name: workspace-volume
    emptyDir: {}
"""
        }
    }

    environment {
        HARBOR_HOST = "192.168.0.11:30401"
        HARBOR_IMAGE = "${HARBOR_HOST}/guardians/backend"
        IMAGE_TAG = "v${BUILD_NUMBER}"
        FULL_IMAGE = "${HARBOR_IMAGE}:${IMAGE_TAG}"
        GIT_REPO = "https://github.com/BeeGuardians/Guardians-backend.git"
        GIT_BRANCH = "feat/infra"
    }

    stages {
        stage('Clone Repository') {
            steps {
                container('kaniko') {
                    sh '''
                    cd /workspace
                    git clone -b ${GIT_BRANCH} ${GIT_REPO} .
                    '''
                }
            }
        }

        stage('Gradle Build') {
            steps {
                container('kaniko') {
                    sh '''
                    cd /workspace
                    ./gradlew clean build -x test
                    '''
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                container('kaniko') {
                    sh '''
                    /kaniko/executor \
                      --context=/workspace \
                      --dockerfile=/workspace/Dockerfile \
                      --destination=${FULL_IMAGE} \
                      --insecure \
                      --insecure-push \
                      --skip-tls-verify
                    '''
                }
            }
        }
    }
}
