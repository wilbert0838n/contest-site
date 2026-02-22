# frontend/Dockerfile  (replace your existing one with this)

# ── Stage 1: Build React ──────────────────────────────────────────────────────
FROM node:18-alpine AS build
WORKDIR /app

# Accept the API URL as a build-time argument
# This gets baked into the JS bundle by Vite
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# ── Stage 2: Serve with Nginx ─────────────────────────────────────────────────
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
