# --- Stage 1: The "Build" Stage ---
# Use Node to install dependencies and run the Vite build process
FROM node:18-alpine AS builder

# Set the working directory
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the frontend code and build it
COPY . .
RUN npm run build


# --- Stage 2: The "Run" Stage ---
FROM nginx:alpine

# Copy the static files from the Vite 'dist' folder into the Nginx public folder
COPY --from=builder /app/dist /usr/share/nginx/html

# Replace default Nginx config with custom
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 (default Nginx port)
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]