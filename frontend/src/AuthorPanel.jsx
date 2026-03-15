import { useEffect, useState } from 'react'

export default function AuthorPanel({ author, tech, onSelectBlueprint }) {
  const [blueprints, setBlueprints] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!author) {
      setBlueprints({}); 
      return;
    }
    setLoading(true)
    setError('')
    fetch(`${tech==='stomp'?import.meta.env.VITE_API_BASE ?? 'http://localhost:8080':import.meta.env.VITE_IO_BASE ?? 'http://localhost:3001'}/api/blueprints?author=${author}`)
      .then(r => {
        if (!r.ok) throw new Error('No se encontraron planos para el autor')
        return r.json()
      })
      .then(data => setBlueprints(data))
      .catch(err => {
        // Mostrar mensaje personalizado si es error de red
        if (err.message.includes('Failed to fetch')) {
          setError('No está disponible');
        } else {
          setError(err.message);
        }
        setBlueprints({}); 
      })
      .finally(() => setLoading(false))
  }, [author, tech])

  const safeBlueprints = Array.isArray(blueprints.data) ? blueprints.data : [];
  const totalPoints = safeBlueprints.reduce((acc, bp) => acc + (bp.points?.length || 0), 0)

  return (
    <div style={{marginBottom:24}}>
      <h3>Panel del Autor</h3>
      {loading && <div>Cargando planos...</div>}
      {error && <div style={{color:'red'}}>{error}</div>}
      {!author || error ? <div>No hay planos para este autor</div> : <table style={{width:'100%', borderCollapse:'collapse', marginBottom:8}}>
        <thead>
          <tr>
            <th style={{borderBottom:'1px solid #ccc', textAlign:'left'}}>Planos ({author})</th>
            <th style={{borderBottom:'1px solid #ccc', textAlign:'left'}}>Puntos</th>
          </tr>
        </thead>
        <tbody>
          {safeBlueprints.map(bp => (
            <tr key={bp.name} style={{cursor:'pointer'}} onClick={() => onSelectBlueprint(bp.name)}>
              <td style={{padding:'4px 8px'}}>{bp.name}</td>
              <td style={{padding:'4px 8px'}}>{bp.points?.length || 0}</td>
            </tr>
          ))}
        </tbody>
      </table>}
      <div><b>Total de puntos:</b> {totalPoints}</div>
    </div>
  )
}
