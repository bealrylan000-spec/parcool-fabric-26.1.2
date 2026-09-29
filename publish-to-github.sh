#!/bin/bash
set -e
cd "$(dirname "$0")"

echo "ParCool Fabric 26.1.2 - GitHub publisher (macOS Terminal version)"
echo
read -r -p "Paste the empty GitHub repository URL (HTTPS): " REPO_URL

if [ -z "$REPO_URL" ]; then
  echo "No repository URL provided."
  exit 1
fi

if ! command -v git >/dev/null 2>&1; then
  echo "Git is not installed. Run: xcode-select --install"
  exit 1
fi

if ! git config user.name >/dev/null 2>&1 || ! git config user.email >/dev/null 2>&1; then
  echo
  echo "Git needs your name and email before the first commit."
  echo "Run these two commands, then rerun this script:"
  echo '  git config --global user.name "Your Name"'
  echo '  git config --global user.email "you@example.com"'
  exit 1
fi

if [ ! -d .git ]; then
  git init
fi

git add .
if ! git diff --cached --quiet; then
  git commit -m "Initial ParCool Fabric 26.1.2 port"
fi

git branch -M main
if git remote get-url origin >/dev/null 2>&1; then
  git remote set-url origin "$REPO_URL"
else
  git remote add origin "$REPO_URL"
fi

echo
echo "Pushing to GitHub..."
git push -u origin main

echo
echo "Push complete. On GitHub: Actions -> Build Fabric JAR -> Run workflow."
