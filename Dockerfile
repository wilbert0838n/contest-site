# Start with Alpine (Tiny)
FROM alpine:latest

# Install G++ ONCE during the build process
RUN apk add --no-cache build-base