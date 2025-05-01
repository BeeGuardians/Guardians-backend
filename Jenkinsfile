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
    HARBOR_IMAGE = "harbor.yourdomain.com/yourproject/guardians"
    IMAGE_TAG = "v${env.BUILD_NUMBER}"
    FULL_IMAGE = "${HARBOR_IMAGE}:${IMAGE_TAG}"
  }

  stages {
    stage('Checkout') {
      steps {
        echo "[SKIPPED] Git checkout skipped."
        // checkout scm
      }
    }

    stage('Gradle Build') {
      steps {
        echo "[SKIPPED] Gradle build skipped."
        // dir('guardians') {
        //   sh './gradlew clean build -x test'
        // }
      }
    }

    stage('Kaniko Build & Push') {
      steps {
        echo "[SKIPPED] Kaniko build skipped."
        // container('kaniko') {
        //   sh '''
        //     /kaniko/executor \
        //       --context=./guardians \
        //       --dockerfile=./guardians/Dockerfile \
        //       --destination=$FULL_IMAGE \
        //       --insecure \
        //       --skip-tls-verify
        //   '''
        // }
      }
    }

    stage('Update Deployment Manifest') {
      steps {
        echo "[SKIPPED] Manifest update skipped."
        // dir('temp-infra-repo') {
        //   git url: 'git@github.com:yourorg/infra-repo.git', branch: 'main', credentialsId: 'github-ssh'
        //   sh "sed -i 's|image: .*|image: $FULL_IMAGE|' infra/manifest/deployment.yaml"
        //   sh 'git config user.email "ci@yourdomain.com"'
        //   sh 'git config user.name "Jenkins CI"'
        //   sh 'git commit -am "ci: update image tag to $FULL_IMAGE"'
        //   sh 'git push origin main'
        // }
      }
    }
  }

  post {
    success {
      echo "✅ [DRY RUN] Pipeline completed successfully (no steps executed)."
    }
    failure {
      echo "❌ Pipeline failed (though all stages are currently skipped)."
    }
  }
}
