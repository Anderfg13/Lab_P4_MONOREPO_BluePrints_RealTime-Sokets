# Dockerfile para frontend React (Vite)
FROM node:18-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage para servir la app
FROM node:18-alpine AS serve
WORKDIR /app
COPY --from=build /app/dist ./dist
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/package.json ./package.json
EXPOSE 5173
ENV VITE_API_BASE="http://localhost:8080"
ENV VITE_IO_BASE="http://localhost:3001"
CMD ["npm", "run", "preview"]
