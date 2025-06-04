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
  affinity:
    nodeAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
        nodeSelectorTerms:
        - matchExpressions:
          - key: workload
            operator: In
            values:
              - guardians7
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
        cpu: "4000m"
        memory: "3072Mi"
    volumeMounts:
    - mountPath: "/kaniko/.docker"
      name: docker-config
    - mountPath: "/home/jenkins/agent"
      name: workspace-volume
    - mountPath: "/root/.gradle"
      name: gradle-cache

  volumes:
  - name: docker-config
    secret:
      secretName: harbor-secret
      items:
      - key: .dockerconfigjson
        path: config.json
  - name: workspace-volume
    emptyDir: {}
  - name: gradle-cache
    emptyDir: {}

"""
        }
    }

    environment {
        HARBOR_HOST = "harbor.example.com:30443"
        HARBOR_IMAGE = "${HARBOR_HOST}/guardians/backend"
    }

    stages {
        stage('Notify Start') {
            steps {
                script {
                    slackSend color: '#439FE0', message: ":rocket: *Build Started* for `${env.JOB_NAME}` <${env.BUILD_URL}|#${env.BUILD_NUMBER}>"
                }
            }
        }

        stage('Checkout') {
            steps {
                container('git') {
                    checkout scm
                    script {
                        sh "git config --global --add safe.directory ${env.WORKSPACE}"
                        IMAGE_TAG = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                        FULL_IMAGE = "${HARBOR_IMAGE}:${IMAGE_TAG}"
                        echo "Docker Image Tag: ${IMAGE_TAG}"
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
                      --skip-tls-verify \
                      --push-retry=3
                    echo "[SUCCESS] Docker Image pushed to ${FULL_IMAGE}"
                    """
                }
            }
        }

        stage('Update Deployment YAML in Infra Repo') {
            steps {
                container('git') {
                    withCredentials([usernamePassword(
                        credentialsId: 'github-token',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_TOKEN'
                    )]) {
                        script {
                            def branch = env.BRANCH_NAME
                            def deploymentFile = branch == "main" ? "cloud-cluster/backend/deployment.yaml" : "cloud-cluster/backend/deployment-${branch}.yaml"

                            sh """
                            echo "[CLONE] Guardians-Infra"
                            git clone --single-branch --branch dev https://${GIT_USER}:${GIT_TOKEN}@github.com/BeeGuardians/Guardians-Infra.git infra

                            echo "[PATCH] Updating ${deploymentFile}"
                            sed -i "s|image: .*|image: ${FULL_IMAGE}|" infra/${deploymentFile}

                            cd infra
                            git config user.email "ci-bot@example.com"
                            git config user.name "CI Bot"
                            git add ${deploymentFile}
                            git commit -m "release : update backend image to guardians/backend:${IMAGE_TAG}" || echo "No changes to commit"
                            git push https://${GIT_USER}:${GIT_TOKEN}@github.com/BeeGuardians/Guardians-Infra.git dev
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            slackSend color: 'good', message: ":white_check_mark: *Build Success* for `${env.JOB_NAME}` <${env.BUILD_URL}|#${env.BUILD_NUMBER}> :tada:"
        }
        failure {
            slackSend color: 'danger', message: ":x: *Build Failed* for `${env.JOB_NAME}` <${env.BUILD_URL}|#${env.BUILD_NUMBER}>"
        }
        unstable {
            slackSend color: 'warning', message: ":warning: *Build Unstable* for `${env.JOB_NAME}` <${env.BUILD_URL}|#${env.BUILD_NUMBER}>"
        }
    }
}
