pipeline {
    options {
        skipDefaultCheckout()
    }

    triggers {
        pollSCM('* * * * *')
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
        memory: "2048Mi"
    volumeMounts:
    - mountPath: "/kaniko/.docker"
      name: docker-config
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
    }

    stages {
        stage('Checkout') {
            steps {
                container('git') {
                    checkout scm
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

        stage('Update Deployment Image Tag') {
            steps {
                container('git') {
                    sh """
                    echo "[INFO] Updating image tag in deployment.yaml"
                    sed -i "s|image: harbor.example.com:30443/guardians/backend:.*|image: ${FULL_IMAGE}|" cloud-cluster/backend/deployment.yaml

                    echo "[INFO] Committing updated deployment.yaml"
                    git config user.email "ci-bot@example.com"
                    git config user.name "CI Bot"
                    git add cloud-cluster/backend/deployment.yaml
                    git commit -m "release : update backend image to ${FULL_IMAGE}" || echo "No changes to commit"

                    echo "[INFO] Pushing to dev branch"
                    git push origin dev
                    """
                }
            }
        }
    }
}
