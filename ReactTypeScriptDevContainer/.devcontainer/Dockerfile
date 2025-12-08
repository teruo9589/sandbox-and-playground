# Use Node.js 22 LTS on Debian Bullseye
FROM node:22-bullseye

# Set working directory
WORKDIR /workspace

# Configure user permissions - use the existing node user
# The node user is already created in the base image with UID 1000
USER node

# Set the default shell for the node user
SHELL ["/bin/bash", "-c"]
