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
    resources:
      requests:
        cpu: "100m"
        memory: "128Mi"
      limits:
        cpu: "200m"
        memory: "256Mi"
    volumeMounts:
    - mountPath: "/home/jenkins/agent"
      name: workspace-volume

  - name: gradle
    image: gradle:8.5-jdk17
    command: ['sleep']
    args: ['infinity']
    resources:
      requests:
        cpu: "500m"
        memory: "1024Mi"
      limits:
        cpu: "1000m"
        memory: "2048Mi"
    volumeMounts:
    - mountPath: "/home/jenkins/agent"
      name: workspace-volume

  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    command: ['sleep']
    args: ['infinity']
    resources:
      requests:
        cpu: "500m"
        memory: "512Mi"
      limits:
        cpu: "1000m"
        memory: "1024Mi"
    volumeMounts:
    - mountPath: "/kaniko/.docker"
      name: docker-config
    - mountPath: "/home/jenkins/agent"
      name: workspace-volume

  - name: debug
    image: curlimages/curl:latest
    command: ['sleep']
    args: ['infinity']
    resources:
      requests:
        cpu: "100m"
        memory: "128Mi"
      limits:
        cpu: "200m"
        memory: "256Mi"
    volumeMounts:
    - mountPath: "/home/jenkins/agent"
      name: workspace-volume

  volumes:
  - name: docker-config
    secret:
      secretName: harbor-secret
      items:
      - key: .dockerconfigjson
        path: config.json
  - name: workspace-volume
    emptyDir: {}
"""
        }
    }

    environment {
        HARBOR_HOST = "harbor.example.com:30443"
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
                    echo "[START] Clone Repository"
                    set -eux
                    cd $WORKSPACE
                    rm -rf ./* ./.??* || true
                    git clone -b $GIT_BRANCH $GIT_REPO .
                    echo "[SUCCESS] Clone completed"
                    '''
                }
            }
        }

        stage('Gradle Build') {
            steps {
                container('gradle') {
                    sh '''
                    echo "[START] Gradle Build"
                    cd $WORKSPACE/guardians
                    ./gradlew clean build -x test
                    echo "[SUCCESS] Gradle Build completed"
                    '''
                }
            }
        }

        stage('Test Harbor Auth (Debug)') {
            steps {
                container('debug') {
                    sh '''
                    echo "[DEBUG] Testing Harbor API auth"
                    curl -v -u 'robot$guardians+jenkins:K1LdhKCndPcgeUUjN2kgwcGAJ2hkHEbg' \
                      -X GET https://harbor.example.com:30443/v2/guardians/backend/tags/list \
                      --insecure || true
                    '''
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                container('kaniko') {
                    sh """
                    echo "[START] Kaniko Build & Push"
                    /kaniko/executor \
                      --context=$WORKSPACE/guardians \
                      --dockerfile=$WORKSPACE/guardians/Dockerfile \
                      --destination=${FULL_IMAGE} \
                      --insecure \
                      --skip-tls-verify
                    echo "[SUCCESS] Docker Image pushed to ${FULL_IMAGE}"
                    """
                }
            }
        }
    }
}
