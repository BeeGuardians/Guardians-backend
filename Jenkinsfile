pipeline {
    options {
        skipDefaultCheckout()
    }

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
  - name: git
    image: alpine/git:latest
    command: ['sleep']
    args: ['infinity']
    volumeMounts:
    - mountPath: "/workspace"
      name: workspace-volume
    - mountPath: "/home/jenkins/agent"
      name: workspace-volume

  - name: gradle
    image: gradle:8.5-jdk17
    command: ['sleep']
    args: ['infinity']
    volumeMounts:
    - mountPath: "/workspace"
      name: workspace-volume
    - mountPath: "/home/jenkins/agent"
      name: workspace-volume

  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    command: ['sleep']
    args: ['infinity']
    volumeMounts:
    - mountPath: "/kaniko/.docker"
      name: docker-config
    - mountPath: "/workspace"
      name: workspace-volume
    - mountPath: "/home/jenkins/agent"
      name: workspace-volume

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
        GIT_BRANCH = "dev"
    }

    stages {
      stage('Clone Repository') {
          steps {
              container('git') {
                sh '''
                set -eux
                echo "[DEBUG] Checking GIT_REPO and GIT_BRANCH"
                echo "GIT_REPO=$GIT_REPO"
                echo "GIT_BRANCH=$GIT_BRANCH"

                echo "[DEBUG] Cleaning /workspace"
                ls -al /workspace || true
                cd /workspace
                rm -rf ./* ./.??* || true

                echo "[DEBUG] Starting clone"
                git clone -b $GIT_BRANCH $GIT_REPO .

                echo "[DEBUG] Clone complete"
                ls -al
                '''
              }
          }
      }

        stage('Gradle Build') {
            steps {
                container('gradle') {
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
                  sh """
                  /kaniko/executor \
                    --context=/workspace \
                    --dockerfile=/workspace/Dockerfile \
                    --destination=${FULL_IMAGE} \
                    --insecure \
                    --insecure-push \
                    --skip-tls-verify
                  """
                }
            }
        }
    }
}
