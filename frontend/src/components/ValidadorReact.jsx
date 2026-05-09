import { useState, useEffect } from 'react';
import AutomataVisualization from './AutomataVisualization';
import AutomataComparison from './AutomataComparison';

export default function ValidadorReact({ tipo, nombre, ejemplos }) {
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState(null);
  const [activeExample, setActiveExample] = useState(null);

  const API_URL = '/api/automatas';

  useEffect(() => {
    const firstExample = ejemplos.find(e => e.esperado) || ejemplos[0];
    if (firstExample) {
      validar(firstExample.simbolos, firstExample.nombre);
    }
  }, []);

  async function validar(simbolos, label = '') {
    if (resultado === null) {
      setInitialLoading(true);
    }
    setLoading(true);
    setError(null);
    setActiveExample(label);
    try {
      const response = await fetch(`${API_URL}/${tipo}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(simbolos)
      });
      if (!response.ok) throw new Error(`Error ${response.status}`);
      const data = await response.json();
      setResultado(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
      setInitialLoading(false);
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    const simbolos = input.split(',').map(s => s.trim()).filter(s => s);
    if (simbolos.length > 0) {
      validar(simbolos, input);
    }
  };

  const handleExample = (ej) => {
    validar(ej.simbolos, ej.nombre);
    setInput(ej.simbolos.join(','));
  };

  const coinciden = resultado ? (
    resultado.validoAFN === resultado.validoAFDTransformado &&
    resultado.validoAFDTransformado === resultado.validoAFDMinimizado
  ) : false;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {initialLoading && (
        <div style={{
          textAlign: 'center',
          padding: '60px 20px',
          color: '#667eea',
          fontSize: '1.1rem'
        }}>
          Cargando estructura del autómata...
        </div>
      )}

      {!initialLoading && (
        <div style={{
          background: 'white',
          borderRadius: '16px',
          padding: '24px',
          boxShadow: '0 10px 40px rgba(0,0,0,0.1)'
        }}>
          <h3 style={{ margin: '0 0 16px 0', color: '#2c3e50', fontSize: '1.1rem' }}>
            Casos de Prueba
          </h3>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(160px, 1fr))',
            gap: '10px',
            marginBottom: '24px'
          }}>
            {ejemplos.map((ej, i) => (
              <button
                key={i}
                onClick={() => handleExample(ej)}
                style={{
                  padding: '12px',
                  border: `2px solid ${ej.esperado ? '#4caf50' : '#f44336'}`,
                  borderRadius: '8px',
                  background: activeExample === ej.nombre ? (ej.esperado ? '#e8f5e9' : '#ffebee') : '#fafafa',
                  cursor: 'pointer',
                  transition: 'all 0.2s',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: '6px',
                  fontWeight: 500,
                  fontSize: '0.85rem',
                  color: ej.esperado ? '#2e7d32' : '#c62828'
                }}
              >
                <span style={{ fontFamily: 'monospace', fontWeight: 'bold' }}>{ej.simbolos.join(', ')}</span>
              </button>
            ))}
          </div>

          <div style={{ borderTop: '1px solid #eee', paddingTop: '20px' }}>
            <h3 style={{ margin: '0 0 12px 0', color: '#2c3e50', fontSize: '1.1rem' }}>
              Validación Personalizada
            </h3>
            <form onSubmit={handleSubmit} style={{ display: 'flex', gap: '10px' }}>
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Ej: SYN,ACK,RST"
                style={{
                  flex: 1,
                  padding: '12px 16px',
                  border: '2px solid #e0e0e0',
                  borderRadius: '8px',
                  fontSize: '1rem',
                  transition: 'all 0.3s',
                  outline: 'none'
                }}
                onFocus={(e) => e.target.style.borderColor = '#667eea'}
                onBlur={(e) => e.target.style.borderColor = '#e0e0e0'}
              />
              <button
                type="submit"
                disabled={loading}
                style={{
                  padding: '12px 28px',
                  background: loading ? '#ccc' : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  fontWeight: 600,
                  cursor: loading ? 'not-allowed' : 'pointer',
                  transition: 'all 0.3s',
                  fontSize: '0.95rem'
                }}
              >
                {loading ? 'Validando...' : 'Validar'}
              </button>
            </form>
          </div>
        </div>
      )}

      {!initialLoading && error && (
        <div style={{
          background: '#ffebee',
          color: '#c62828',
          padding: '16px',
          borderRadius: '8px',
          borderLeft: '4px solid #f44336',
          fontWeight: 500
        }}>
          Error: {error}
        </div>
      )}

      {loading && !initialLoading && (
        <div style={{
          textAlign: 'center',
          padding: '40px',
          color: '#667eea',
          fontSize: '1.1rem'
        }}>
          Validando cadena...
        </div>
      )}

      {resultado && !loading && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div style={{
            background: coinciden ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' : '#fff3e0',
            color: coinciden ? 'white' : '#e65100',
            padding: '20px',
            borderRadius: '12px',
            boxShadow: '0 8px 30px rgba(102, 126, 234, 0.3)'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <div style={{ fontSize: '0.85rem', opacity: 0.9, marginBottom: '4px', color: '#000', fontWeight: 'bold' }}>CADENA VALIDADA</div>
                <div style={{ fontFamily: 'monospace', fontSize: '1.2rem', fontWeight: 600 }}>
                  {activeExample || input}
                </div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: '0.85rem', opacity: 0.9, marginBottom: '4px', color: '#000', fontWeight: 'bold' }}>ESTADO</div>
                <div style={{ fontSize: '1.1rem', fontWeight: 700 }}>
                  {coinciden ? 'Consistente' : 'Inconsistente'}
                </div>
              </div>
            </div>
          </div>

          <div style={{
            background: 'white',
            borderRadius: '12px',
            padding: '20px',
            boxShadow: '0 4px 20px rgba(0,0,0,0.08)'
          }}>
            <h3 style={{ margin: '0 0 16px 0', color: '#2c3e50', fontSize: '1rem' }}>
              Resultados por Nivel
            </h3>
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
              gap: '12px'
            }}>
              {[
                { name: 'AFN', valid: resultado.validoAFN, color: '#e74c3c' },
                { name: 'AFD Transformado', valid: resultado.validoAFDTransformado, color: '#3498db' },
                { name: 'AFD Minimizado', valid: resultado.validoAFDMinimizado, color: '#2ecc71' }
              ].map(({ name, valid, color }) => (
                <div key={name} style={{
                  background: valid ? '#e8f5e9' : '#ffebee',
                  border: `2px solid ${valid ? '#4caf50' : '#f44336'}`,
                  borderRadius: '8px',
                  padding: '16px',
                  textAlign: 'center'
                }}>
                  <div style={{ fontWeight: 600, color: '#333', fontSize: '0.9rem' }}>{name}</div>
                  <div style={{ color: valid ? '#2e7d32' : '#c62828', fontSize: '0.85rem', marginTop: '4px' }}>
                    {valid ? 'Aceptada' : 'Rechazada'}
                  </div>
                </div>
              ))}
            </div>
          </div>

          <AutomataComparison resultado={resultado} tipo={tipo} />

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <h3 style={{ margin: '0', color: '#ffffff', fontSize: '1.1rem', fontWeight: 700 }}>
              Ver Estados por Nivel
            </h3>
            <AutomataVisualization
              automata={resultado.afn}
              title="AFN (No Determinista)"
              color="#e74c3c"
              cadena={input.split(',').map(s => s.trim()).filter(s => s)}
            />
            <AutomataVisualization
              automata={resultado.afdTransformado}
              title="AFD Transformado (Determinista)"
              color="#3498db"
              cadena={input.split(',').map(s => s.trim()).filter(s => s)}
            />
            <AutomataVisualization
              automata={resultado.afdMinimizado}
              title="AFD Minimizado (Óptimo)"
              color="#2ecc71"
              cadena={input.split(',').map(s => s.trim()).filter(s => s)}
            />
          </div>
        </div>
      )}
    </div>
  );
}
