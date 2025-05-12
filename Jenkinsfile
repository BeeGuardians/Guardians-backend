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
    env:
    - name: GIT_SSL_NO_VERIFY
      value: "true"
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
                    withCredentials([usernamePassword(
                        credentialsId: 'github-token',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_TOKEN'
                    )]) {
                        sh """
                        git config --global user.email "ci-bot@example.com"
                        git config --global user.name "CI Bot"
                        git clone --single-branch --branch feat/infra https://${GIT_USER}:${GIT_TOKEN}@github.com/BeeGuardians/Guardians-backend.git .
                        """
                    }
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

        stage('Update Deployment Image Tag and Push') {
            steps {
                container('git') {
                    withCredentials([usernamePassword(
                        credentialsId: 'github-token',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_TOKEN'
                    )]) {
                        sh """
                        echo "[INFO] Updating image tag in deployment.yaml"
                        sed -i "s|image: harbor.example.com:30443/guardians/backend:.*|image: ${FULL_IMAGE}|" cloud-cluster/backend/deployment.yaml

                        echo "[INFO] Git commit and push"
                        git add cloud-cluster/backend/deployment.yaml
                        git commit -m "release : update backend image to ${FULL_IMAGE}" || echo "No changes to commit"
                        git push https://${GIT_USER}:${GIT_TOKEN}@github.com/BeeGuardians/Guardians-Infra.git dev
                        """
                    }
                }
            }
        }
    }
}
