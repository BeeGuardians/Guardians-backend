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
    - mountPath: "/home/jenkins/agent"
      name: workspace-volume

  - name: gradle
    image: gradle:8.5-jdk17
    command: ['sleep']
    args: ['infinity']
    volumeMounts:
    - mountPath: "/home/jenkins/agent"
      name: workspace-volume

  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    command: ['sleep']
    args: ['infinity']
    volumeMounts:
    - mountPath: "/kaniko/.docker"
      name: docker-config
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
        GIT_BRANCH = "feat/infra"
    }

    stages {
        stage('Clone Repository') {
            steps {
                container('git') {
                    sh '''
                    set -eux
                    echo "[DEBUG] 현재 작업 디렉토리: $PWD"
                    echo "[DEBUG] Jenkins Workspace: $WORKSPACE"
                    cd $WORKSPACE
                    rm -rf ./* ./.??* || true
                    git clone -b $GIT_BRANCH $GIT_REPO .
                    echo "[DEBUG] Clone 완료"
                    ls -al
                    '''
                }
            }
        }

        stage('Gradle Build') {
            steps {
                container('gradle') {
                    sh '''
                    cd $WORKSPACE/guardians
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
                    --context=$WORKSPACE/guardians \
                    --dockerfile=$WORKSPACE/guardians/Dockerfile \
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
