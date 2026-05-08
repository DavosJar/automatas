import { useState } from 'react';

export default function AutomataComparison({ resultado, tipo }) {
  const [activeTab, setActiveTab] = useState('table');

  if (!resultado) return null;

  const { afn, afdTransformado, afdMinimizado, validoAFN, validoAFDTransformado, validoAFDMinimizado } = resultado;

  const getStats = (aut) => ({
    estados: Array.from(aut.estados || []).length,
    aceptacion: Array.from(aut.estadosAceptacion || []).length,
    transiciones: Object.values(aut.tablaTransiciones || {}).reduce(
      (acc, t) => acc + Object.keys(t || {}).length, 0
    ),
    alfabeto: Array.from(aut.alfabeto || []).length
  });

  const statsAFN = getStats(afn);
  const statsAFD = getStats(afdTransformado);
  const statsMin = getStats(afdMinimizado);

  const coinciden = validoAFN === validoAFDTransformado && validoAFDTransformado === validoAFDMinimizado;

  return (
    <div style={{ marginTop: '24px' }}>
      <div style={{
        display: 'flex',
        gap: '12px',
        marginBottom: '20px',
        borderBottom: '2px solid #eee',
        paddingBottom: '12px'
      }}>
        {['table', 'transitions', 'comparison'].map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            style={{
              padding: '8px 16px',
              border: 'none',
              background: activeTab === tab ? '#667eea' : 'transparent',
              color: activeTab === tab ? 'white' : '#666',
              borderRadius: '6px',
              cursor: 'pointer',
              fontWeight: 600,
              fontSize: '0.9rem',
              textTransform: 'capitalize'
            }}
          >
            {tab === 'table' ? '📊 Tabla' : tab === 'transitions' ? '🔄 Transiciones' : '⚖️ Comparación'}
          </button>
        ))}
      </div>

      {activeTab === 'table' && (
        <div style={{
          background: 'white',
          borderRadius: '12px',
          padding: '20px',
          boxShadow: '0 4px 20px rgba(0,0,0,0.08)'
        }}>
          <h4 style={{ margin: '0 0 16px 0', color: '#2c3e50' }}>Comparación de Niveles</h4>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.9rem' }}>
            <thead>
              <tr style={{ background: '#f5f5f5' }}>
                <th style={{ padding: '12px', textAlign: 'left', color: '#666' }}>Métrica</th>
                <th style={{ padding: '12px', textAlign: 'center', color: '#e74c3c' }}>AFN</th>
                <th style={{ padding: '12px', textAlign: 'center', color: '#3498db' }}>AFD Transformado</th>
                <th style={{ padding: '12px', textAlign: 'center', color: '#2ecc71' }}>AFD Minimizado</th>
              </tr>
            </thead>
            <tbody>
              {[
                { metric: 'Estados', afn: statsAFN.estados, afd: statsAFD.estados, min: statsMin.estados, color: '#667eea' },
                { metric: 'Transiciones', afn: statsAFN.transiciones, afd: statsAFD.transiciones, min: statsMin.transiciones, color: '#ff9800' },
                { metric: 'Estados Acept.', afn: statsAFN.aceptacion, afd: statsAFD.aceptacion, min: statsMin.aceptacion, color: '#4caf50' },
                { metric: 'Tamaño Alfabeto', afn: statsAFN.alfabeto, afd: statsAFD.alfabeto, min: statsMin.alfabeto, color: '#9c27b0' }
              ].map(({ metric, afn: a, afd: d, min: m, color }) => (
                <tr key={metric} style={{ borderBottom: '1px solid #eee' }}>
                  <td style={{ padding: '10px', fontWeight: 600, color: '#333' }}>{metric}</td>
                  <td style={{ padding: '10px', textAlign: 'center', fontFamily: 'monospace', color: '#e74c3c', fontWeight: 600 }}>{a}</td>
                  <td style={{ padding: '10px', textAlign: 'center', fontFamily: 'monospace', color: '#3498db', fontWeight: 600 }}>{d}</td>
                  <td style={{ padding: '10px', textAlign: 'center', fontFamily: 'monospace', color: '#2ecc71', fontWeight: 600 }}>{m}</td>
                </tr>
              ))}
              <tr style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '10px', fontWeight: 600, color: '#333' }}>Validación</td>
                <td style={{ padding: '10px', textAlign: 'center', fontSize: '1.2rem' }}>{validoAFN ? '✅' : '❌'}</td>
                <td style={{ padding: '10px', textAlign: 'center', fontSize: '1.2rem' }}>{validoAFDTransformado ? '✅' : '❌'}</td>
                <td style={{ padding: '10px', textAlign: 'center', fontSize: '1.2rem' }}>{validoAFDMinimizado ? '✅' : '❌'}</td>
              </tr>
            </tbody>
          </table>
          
          <div style={{
            marginTop: '16px',
            padding: '12px',
            borderRadius: '8px',
            background: coinciden ? '#e8f5e9' : '#fff3e0',
            color: coinciden ? '#2e7d32' : '#e65100',
            fontWeight: 600,
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}>
            {coinciden ? '✓' : '⚠'} 
            {coinciden ? 'Todos los niveles coinciden en la validación' : 'Inconsistencia detectada entre niveles'}
          </div>
        </div>
      )}

      {activeTab === 'transitions' && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '16px' }}>
          <TransitionCard title="AFN" color="#e74c3c" automata={afn} />
          <TransitionCard title="AFD Transformado" color="#3498db" automata={afdTransformado} />
          <TransitionCard title="AFD Minimizado" color="#2ecc71" automata={afdMinimizado} />
        </div>
      )}

      {activeTab === 'comparison' && (
        <ComparisonView afn={afn} afd={afdTransformado} minimized={afdMinimizado} />
      )}
    </div>
  );
}

function TransitionCard({ title, color, automata }) {
  const [expanded, setExpanded] = useState(false);
  const estados = Array.from(automata.estados || []);
  const estadosAceptacion = Array.from(automata.estadosAceptacion || []);

  return (
    <div style={{
      background: 'white',
      borderRadius: '12px',
      padding: '16px',
      boxShadow: '0 4px 20px rgba(0,0,0,0.08)',
      borderTop: `3px solid ${color}`
    }}>
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '12px',
        cursor: 'pointer'
      }} onClick={() => setExpanded(!expanded)}>
        <h4 style={{ margin: 0, color: '#2c3e50', fontSize: '0.95rem' }}>
          <span style={{ color, marginRight: '6px' }}>●</span>
          {title}
        </h4>
        <span style={{ fontSize: '0.8rem', color: '#999' }}>{expanded ? '▲' : '▼'}</span>
      </div>

      <div style={{
        display: 'flex',
        gap: '8px',
        flexWrap: 'wrap',
        marginBottom: '12px'
      }}>
        {estados.map(e => (
          <span key={e} style={{
            background: estadosAceptacion.includes(e) ? '#e8f5e9' : '#f0f0f0',
            color: estadosAceptacion.includes(e) ? '#4caf50' : '#333',
            padding: '4px 10px',
            borderRadius: '4px',
            fontSize: '0.85rem',
            fontFamily: 'monospace',
            border: estadosAceptacion.includes(e) ? '1px solid #4caf50' : '1px solid #ddd'
          }}>
            {e}
          </span>
        ))}
      </div>

      {expanded && (
        <div style={{ fontSize: '0.8rem', maxHeight: '200px', overflowY: 'auto' }}>
          {Object.entries(automata.tablaTransiciones || {}).map(([estado, trans]) => (
            <div key={estado} style={{
              padding: '6px 0',
              borderBottom: '1px solid #f0f0f0',
              display: 'flex',
              gap: '8px',
              alignItems: 'center'
            }}>
              <span style={{ fontFamily: 'monospace', fontWeight: 600, minWidth: '60px' }}>{estado}</span>
              <span style={{ color: '#999' }}>→</span>
              <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                {Object.entries(trans || {}).map(([s, d]) => (
                  <span key={s} style={{
                    background: '#f5f5f5',
                    padding: '2px 8px',
                    borderRadius: '4px',
                    fontSize: '0.8rem'
                  }}>
                    {s}→{Array.isArray(d) ? d.join(',') : d}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function ComparisonView({ afn, afd, minimized }) {
  const afnStates = Array.from(afn.estados || []);
  const afdStates = Array.from(afd.estados || []);
  const minStates = Array.from(minimized.estados || []);

  const reduction1 = ((1 - afdStates.length / afnStates.length) * 100).toFixed(1);
  const reduction2 = ((1 - minStates.length / afdStates.length) * 100).toFixed(1);
  const totalReduction = ((1 - minStates.length / afnStates.length) * 100).toFixed(1);

  return (
    <div style={{
      background: 'white',
      borderRadius: '12px',
      padding: '24px',
      boxShadow: '0 4px 20px rgba(0,0,0,0.08)'
    }}>
      <h4 style={{ margin: '0 0 20px 0', color: '#2c3e50' }}>Análisis de Reducción de Estados</h4>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
        gap: '16px',
        marginBottom: '24px'
      }}>
        <div style={{ background: '#ffebee', padding: '20px', borderRadius: '8px', textAlign: 'center' }}>
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#e74c3c' }}>{afnStates.length}</div>
          <div style={{ fontSize: '0.85rem', color: '#666', marginTop: '4px' }}>AFN - Estados Iniciales</div>
        </div>
        <div style={{ background: '#e3f2fd', padding: '20px', borderRadius: '8px', textAlign: 'center' }}>
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#3498db' }}>{afdStates.length}</div>
          <div style={{ fontSize: '0.85rem', color: '#666', marginTop: '4px' }}>AFD - Tras Transformar</div>
          <div style={{ fontSize: '0.9rem', color: '#3498db', fontWeight: 600, marginTop: '8px' }}>
            {reduction1 > 0 ? `↓ ${reduction1}%` : `↑ ${Math.abs(reduction1)}%`}
          </div>
        </div>
        <div style={{ background: '#e8f5e9', padding: '20px', borderRadius: '8px', textAlign: 'center' }}>
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#2ecc71' }}>{minStates.length}</div>
          <div style={{ fontSize: '0.85rem', color: '#666', marginTop: '4px' }}>AFD - Minimizado</div>
          <div style={{ fontSize: '0.9rem', color: '#2ecc71', fontWeight: 600, marginTop: '8px' }}>
            ↓ {totalReduction}% del total
          </div>
        </div>
      </div>

      <div style={{
        background: '#f8f9fa',
        padding: '16px',
        borderRadius: '8px'
      }}>
        <div style={{ fontSize: '0.85rem', color: '#666', marginBottom: '12px', fontWeight: 600 }}>
          DIFERENCIAS PRINCIPALES
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.9rem' }}>
            <span style={{ color: '#e74c3c', fontSize: '1.2rem' }}>●</span>
            <strong>AFN:</strong> 
            <span>Puede tener múltiples estados destino (no determinista)</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.9rem' }}>
            <span style={{ color: '#3498db', fontSize: '1.2rem' }}>●</span>
            <strong>AFD Transformado:</strong> 
            <span>Super-estados que agrupan estados del AFN (determinista)</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.9rem' }}>
            <span style={{ color: '#2ecc71', fontSize: '1.2rem' }}>●</span>
            <strong>AFD Minimizado:</strong> 
            <span>Estados equivalentes unidos (mínimo posible)</span>
          </div>
        </div>
      </div>
    </div>
  );
}
