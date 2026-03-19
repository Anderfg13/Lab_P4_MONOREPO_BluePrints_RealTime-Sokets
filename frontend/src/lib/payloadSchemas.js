import { z } from 'zod'

export const pointSchema = z.object({
  x: z.number().int().min(0).max(600),
  y: z.number().int().min(0).max(400)
})

export const blueprintPayloadSchema = z.object({
  author: z.string().trim().min(1, 'author es obligatorio'),
  name: z.string().trim().min(1, 'name es obligatorio'),
  points: z.array(pointSchema).max(5000, 'Demasiados puntos en el payload')
})

export const drawEventSchema = z.object({
  author: z.string().trim().min(1, 'author es obligatorio'),
  name: z.string().trim().min(1, 'name es obligatorio'),
  point: pointSchema
})
