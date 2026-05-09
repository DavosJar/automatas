import { useState } from 'react';
import DiagramaAutomata from './DiagramaAutomata';

export default function AutomataVisualization({ automata, title, color, cadena = [] }) {
  const [expanded, setExpanded] = useState(false);
  
  if (!automata) return null;

  const estados = Array.from(automata.estados || []);
  const estadosAceptacion = Array.from(automata.estadosAceptacion || []);
  const estadoInicial = automata.estadoInicial;
  const alfabeto = Array.from(automata.alfabeto || []);

  const getTransitions = () => {
    const transitions = [];
    const tabla = automata.tablaTransiciones || {};
    Object.entries(tabla).forEach(([estado, trans]) => {
      Object.entries(trans).forEach(([simbolo, destino]) => {
        if (Array.isArray(destino)) {
          destino.forEach(d => {
            transitions.push({ from: estado, symbol: simbolo, to: d });
          });
        } else {
          transitions.push({ from: estado, symbol: simbolo, to: destino });
        }
      });
    });
    return transitions;
  };

  const transitions = getTransitions();

  return (
    <div style={{
      background: '#f8f9fa',
      borderRadius: '12px',
      padding: '20px',
      border: `2px solid ${color}20`,
      marginBottom: '16px'
    }}>
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '16px',
        cursor: 'pointer'
      }} onClick={() => setExpanded(!expanded)}>
        <h4 style={{ margin: 0, color: '#2c3e50', fontSize: '1rem' }}>
          <span style={{ color, marginRight: '8px' }}>●</span>
          {title}
        </h4>
        <div style={{ display: 'flex', gap: '12px', fontSize: '0.85rem', color: '#000', fontWeight: 'bold' }}>
          <span>{estados.length} estados</span>
          <span>{transitions.length} transiciones</span>
          <span>{expanded ? '▲' : '▼'}</span>
        </div>
      </div>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '12px',
        marginBottom: '16px'
      }}>
        <div style={{ background: 'white', padding: '12px', borderRadius: '8px', border: '1px solid #ccc' }}>
          <div style={{ fontSize: '0.8rem', color: '#000', marginBottom: '4px', fontWeight: 'bold' }}>ESTADO INICIAL</div>
          <div style={{ fontFamily: 'monospace', color: '#667eea', fontWeight: 600 }}>{estadoInicial}</div>
        </div>
        <div style={{ background: 'white', padding: '12px', borderRadius: '8px', border: '1px solid #ccc' }}>
          <div style={{ fontSize: '0.8rem', color: '#000', marginBottom: '4px', fontWeight: 'bold' }}>ESTADOS ACEPTACIÓN</div>
          <div style={{ fontFamily: 'monospace', color: '#4caf50', fontWeight: 600 }}>
            {estadosAceptacion.join(', ') || 'Ninguno'}
          </div>
        </div>
        <div style={{ background: 'white', padding: '12px', borderRadius: '8px', border: '1px solid #ccc' }}>
          <div style={{ fontSize: '0.8rem', color: '#000', marginBottom: '4px', fontWeight: 'bold' }}>ALFABETO</div>
          <div style={{ fontFamily: 'monospace', color: '#ff9800' }}>
            {alfabeto.join(', ')}
          </div>
        </div>
      </div>

      {expanded && (
        <>
          <div style={{
            background: 'white',
            borderRadius: '8px',
            padding: '16px',
            overflowX: 'auto'
          }}>
            <div style={{ fontSize: '0.85rem', color: '#000', marginBottom: '12px', fontWeight: 'bold' }}>
              TABLA DE TRANSICIONES
            </div>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
              <thead>
                <tr style={{ background: `${color}15` }}>
                  <th style={{ padding: '10px', textAlign: 'left', color: '#000', fontWeight: 700 }}>Estado</th>
                  {alfabeto.map(s => (
                    <th key={s} style={{ padding: '10px', textAlign: 'center', color: '#000', fontWeight: 700 }}>{s}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {estados.map(estado => {
                  const isAccepting = estadosAceptacion.includes(estado);
                  const isInitial = estado === estadoInicial;
                  return (
                    <tr key={estado} style={{ borderBottom: '1px solid #eee' }}>
                      <td style={{ padding: '8px', fontFamily: 'monospace', fontWeight: 600, color: '#000' }}>
                        {isInitial && <span style={{ color: '#667eea' }}>&#9654; </span>}
                        {isAccepting && <span style={{ color: '#4caf50' }}>&#10003; </span>}
                        {estado}
                      </td>
                      {alfabeto.map(simbolo => {
                        const trans = automata.tablaTransiciones?.[estado]?.[simbolo];
                        let destino = '-';
                        if (Array.isArray(trans)) {
                          destino = trans.join(', ');
                        } else if (trans) {
                          destino = trans;
                        }
                        return (
                          <td key={simbolo} style={{ padding: '8px', textAlign: 'center', fontFamily: 'monospace', color: '#000', fontWeight: 500 }}>
                            {destino}
                          </td>
                        );
                      })}
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          <div style={{
            marginTop: '16px',
            background: 'white',
            borderRadius: '8px',
            padding: '16px'
          }}>
            <div style={{ fontSize: '0.85rem', color: '#000', marginBottom: '12px', fontWeight: 'bold' }}>
              DIAGRAMA DEL AUTÓMATA
            </div>
            
            <DiagramaAutomata automata={automata} color={color} cadena={cadena} />
          </div>
        </>
      )}
    </div>
  );
}
