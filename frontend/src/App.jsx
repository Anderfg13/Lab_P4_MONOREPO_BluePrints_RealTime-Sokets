import { useEffect, useRef, useState } from 'react'
import { createStompClient, subscribeBlueprint } from './lib/stompClient.js'
import { createSocket } from './lib/socketIoClient.js'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080' // Spring
const IO_BASE  = import.meta.env.VITE_IO_BASE  ?? 'http://localhost:3001' // Node/Socket.IO

export default function App() {
  const [tech, setTech] = useState('stomp')
  const [author, setAuthor] = useState('juan')
  const [name, setName] = useState('plano-1')
  const [errorMsg, setErrorMsg] = useState('')
  const canvasRef = useRef(null)

  const stompRef = useRef(null)
  const unsubRef = useRef(null)
  const socketRef = useRef(null)

  useEffect(() => {
    setErrorMsg('');
    fetch(`${tech==='stomp'?API_BASE:IO_BASE}/api/blueprints/${author}/${name}`)
      .then(r => {
        if (!r.ok) throw new Error('Blueprint no encontrado');
        return r.json();
      })
      .then(drawAll)
      .catch(() => {
        drawAll({ points: [] });
        setErrorMsg('Blueprint no encontrado para ese autor/plano');
      });
  }, [tech, author, name])

  function drawAll(bp) {
    // Soporta respuesta { code, message, data }
    const points = bp?.points || bp?.data?.points || [];
    console.log('drawAll:', points);
    const ctx = canvasRef.current?.getContext('2d');
    if (!ctx) return;
    ctx.clearRect(0,0,600,400);
    ctx.beginPath();
    if (Array.isArray(points)) {
      points.forEach((p,i)=> {
        if (i===0) ctx.moveTo(p.x,p.y); else ctx.lineTo(p.x,p.y);
      });
      ctx.stroke();
      // Dibuja un círculo en cada punto
      points.forEach((p) => {
        ctx.beginPath();
        ctx.arc(p.x, p.y, 5, 0, 2 * Math.PI);
        ctx.fillStyle = '#007bff';
        ctx.fill();
      });
    }
  }

  useEffect(() => {
    if (typeof unsubRef.current === 'function') {
      unsubRef.current();
    }
    unsubRef.current = null;
    stompRef.current?.deactivate?.(); stompRef.current = null;
    socketRef.current?.disconnect?.(); socketRef.current = null;

    if (tech === 'stomp') {
      const client = createStompClient(API_BASE)
      stompRef.current = client
      client.onConnect = () => {
        unsubRef.current = subscribeBlueprint(client, author, name, (upd)=> {
          console.log('STOMP update:', upd);
          drawAll({ points: upd.points })
        })
      }
      client.activate()
    } else {
      const s = createSocket(IO_BASE)
      socketRef.current = s
      const room = `blueprints.${author}.${name}`
      s.emit('join-room', room)
      s.on('blueprint-update', (upd)=> {
        console.log('Socket.IO update:', upd);
        drawAll({ points: upd.points })
      })
    }
    return () => {
      if (typeof unsubRef.current === 'function') {
        unsubRef.current();
      }
      unsubRef.current = null;
      stompRef.current?.deactivate?.();
      socketRef.current?.disconnect?.();
    }
  }, [tech, author, name])

  function onClick(e) {
    const rect = e.target.getBoundingClientRect()
    const point = { x: Math.round(e.clientX - rect.left), y: Math.round(e.clientY - rect.top) }

    if (tech === 'stomp' && stompRef.current?.connected) {
      stompRef.current.publish({ destination: '/app/draw', body: JSON.stringify({ author, name, point }) })
    } else if (tech === 'socketio' && socketRef.current?.connected) {
      const room = `blueprints.${author}.${name}`
      socketRef.current.emit('draw-event', { room, author, name, point })
    }
  }

  return (
    <div style={{fontFamily:'Inter, system-ui', padding:16, maxWidth:900}}>
      <h2>BluePrints RT – Socket.IO vs STOMP</h2>
      <div style={{display:'flex', gap:8, alignItems:'center', marginBottom:8}}>
        <label>Tecnología:</label>
        <select value={tech} onChange={e=>setTech(e.target.value)}>
          <option value="stomp">STOMP (Spring)</option>
          <option value="socketio">Socket.IO (Node)</option>
        </select>
        <input value={author} onChange={e=>setAuthor(e.target.value)} placeholder="autor"/>
        <input value={name} onChange={e=>setName(e.target.value)} placeholder="plano"/>
      </div>
      {errorMsg && <div style={{color:'red', marginBottom:8}}>{errorMsg}</div>}
      <canvas
        ref={canvasRef}
        width={600}
        height={400}
        style={{border:'1px solid #ddd', borderRadius:12}}
        onClick={onClick}
      />
      <p style={{opacity:.7, marginTop:8}}>Tip: abre 2 pestañas y dibuja alternando para ver la colaboración.</p>
    </div>
  )
}
