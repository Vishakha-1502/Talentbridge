#!/bin/bash
# TalentBridge - Unified Run Script

export JAVA_HOME="/opt/anaconda3/envs/talentbridge"
export PATH="$JAVA_HOME/bin:/usr/local/bin:$PATH"

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"

case "$1" in
  backend)
    echo "Starting TalentBridge Backend on http://localhost:8080..."
    cd "$PROJECT_ROOT/backend" && java -jar target/backend-0.0.1-SNAPSHOT.jar
    ;;
  frontend)
    echo "Starting TalentBridge Frontend on http://localhost:3000..."
    cd "$PROJECT_ROOT/frontend" && npm run dev
    ;;
  build)
    echo "Building TalentBridge Backend package..."
    cd "$PROJECT_ROOT/backend" && mvn clean package -DskipTests
    ;;
  *)
    echo "Starting TalentBridge Backend (http://localhost:8080) and Frontend (http://localhost:3000)..."
    cd "$PROJECT_ROOT/backend" && java -jar target/backend-0.0.1-SNAPSHOT.jar &
    BACKEND_PID=$!
    cd "$PROJECT_ROOT/frontend" && npm run dev &
    FRONTEND_PID=$!
    trap "kill $BACKEND_PID $FRONTEND_PID" SIGINT SIGTERM
    wait
    ;;
esac
