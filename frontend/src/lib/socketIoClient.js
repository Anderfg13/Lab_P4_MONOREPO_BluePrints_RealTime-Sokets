import { io } from 'socket.io-client'

export function createSocket(baseUrl) {
  const socket = io(baseUrl, { transports: ['websocket'] })
  socket.on('connect', () => console.info('[Socket.IO] connected', socket.id))
  socket.on('disconnect', (reason) => console.warn('[Socket.IO] disconnected', reason))
  socket.io.on('reconnect_attempt', (attempt) => console.info('[Socket.IO] reconnect attempt', attempt))
  socket.io.on('reconnect', (attempt) => console.info('[Socket.IO] reconnected', attempt))
  socket.io.on('reconnect_error', (err) => console.error('[Socket.IO] reconnect error', err))
  return socket
}
