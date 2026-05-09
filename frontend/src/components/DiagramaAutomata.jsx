import React, { useEffect, useRef } from 'react';
import { Network } from 'vis-network';
import { DataSet } from 'vis-data';

export default function DiagramaAutomata({ automata, color = '#667eea', cadena = [] }) {
  const containerRef = useRef(null);
  const networkRef = useRef(null);
  const nodesRef = useRef(null);

  useEffect(() => {
    if (!automata || !containerRef.current) return;

    const estados = Array.from(automata.estados || []);
    const estadosAceptacion = Array.from(automata.estadosAceptacion || []);
    const estadoInicial = automata.estadoInicial;
    const tabla = automata.tablaTransiciones || {};

    // Preparar nodos
    const nodes = estados.map((estado) => {
      const isAccepting = estadosAceptacion.includes(estado);

      return {
        id: estado,
        label: estado,
        shape: 'circle',
        color: {
          background: '#1e293b',
          border: isAccepting ? '#ffffff' : color,
          highlight: { background: color, border: '#ffffff' },
          hover: { background: '#334155', border: color }
        },
        font: { color: '#ffffff', face: 'monospace', bold: isAccepting },
        borderWidth: isAccepting ? 4 : 2,
        borderDashes: false,
      };
    });

    // Nodo invisible de inicio (para la flecha de entrada)
    const startNodeId = '__start__';
    nodes.push({
      id: startNodeId,
      label: '',
      shape: 'point',
      size: 0,
      color: 'transparent'
    });

    const edges = [];
    if (estadoInicial) {
      edges.push({
        from: startNodeId,
        to: estadoInicial,
        arrows: 'to',
        color: { color: color },
        length: 50,
      });
    }

    // Agrupar transiciones entre el mismo par de nodos
    const edgesMap = {};
    Object.entries(tabla).forEach(([from, trans]) => {
      Object.entries(trans).forEach(([simbolo, destino]) => {
        const destinos = Array.isArray(destino) ? destino : [destino];
        destinos.forEach(to => {
          const key = `${from}->${to}`;
          if (!edgesMap[key]) {
            edgesMap[key] = { from, to, symbols: new Set() };
          }
          edgesMap[key].symbols.add(simbolo);
        });
      });
    });

    Object.values(edgesMap).forEach(({ from, to, symbols }) => {
      edges.push({
        from,
        to,
        label: Array.from(symbols).join(', '),
        arrows: 'to',
        font: { color: '#94a3b8', size: 12, face: 'monospace', background: '#0f172a', strokeWidth: 0 },
        color: { color: '#475569', highlight: color, hover: color },
        smooth: { type: 'continuous', roundness: from === to ? 0.5 : 0.2 }
      });
    });

    const nodesData = new DataSet(nodes);
    nodesRef.current = nodesData;

    const options = {
      autoResize: true,
      physics: {
        enabled: true,
        solver: 'forceAtlas2Based',
        forceAtlas2Based: {
          gravitationalConstant: -100,
          centralGravity: 0.01,
          springLength: 150,
          springConstant: 0.08,
          damping: 0.4,
          avoidOverlap: 1
        },
        stabilization: { iterations: 100 }
      },
      interaction: {
        dragNodes: false,
        dragView: false,
        zoomView: false,
        hover: false,
        selectable: false,
      },
      layout: { randomSeed: 42 }
    };

    networkRef.current = new Network(containerRef.current, { nodes: nodesData, edges }, options);

    // Desactivar físicas después de estabilizar para que quede fijo
    networkRef.current.once('stabilizationIterationsDone', function () {
      networkRef.current.setOptions({ physics: false });
    });

    return () => {
      if (networkRef.current) {
        networkRef.current.destroy();
        networkRef.current = null;
        nodesRef.current = null;
      }
    };
  }, [automata, color]);

  // Efecto para animar el recorrido de la cadena
  useEffect(() => {
    if (!automata || !nodesRef.current) return;

    const estadosAceptacion = Array.from(automata.estadosAceptacion || []);
    const tabla = automata.tablaTransiciones || {};

    // Función helper para resetear todos los nodos a su color base
    const resetNodes = (activeSet = new Set()) => {
      const updates = nodesRef.current.get()
        .filter(n => n.id !== '__start__')
        .map(node => {
          const isAccepting = estadosAceptacion.includes(node.id);
          const isActive = activeSet.has(node.id);
          return {
            id: node.id,
            color: {
              background: isActive ? '#facc15' : '#1e293b',
              border: isActive ? '#f97316' : (isAccepting ? '#ffffff' : color),
              highlight: { background: color, border: '#ffffff' },
            },
            font: { color: isActive ? '#000000' : '#ffffff', face: 'monospace' }
          };
        });
      nodesRef.current.update(updates);
    };

    if (cadena.length === 0) {
      // Sin cadena: estado inicial resaltado
      resetNodes(new Set([automata.estadoInicial]));
      return;
    }

    // Animación paso a paso
    // Paso 0: resaltar estado inicial
    let step = 0;
    let activeStates = new Set([automata.estadoInicial]);
    resetNodes(activeStates);

    const interval = setInterval(() => {
      if (step >= cadena.length) {
        clearInterval(interval);
        return;
      }

      const symbol = cadena[step];
      const nextStates = new Set();
      activeStates.forEach(s => {
        const t = tabla[s]?.[symbol];
        if (Array.isArray(t)) t.forEach(x => nextStates.add(x));
        else if (t) nextStates.add(t);
      });

      activeStates = nextStates;
      resetNodes(activeStates);
      step++;
    }, 800);

    return () => clearInterval(interval);
  }, [automata, cadena, color]);

  return (
    <div style={{
      width: '100%',
      height: '400px',
      background: '#0f172a',
      borderRadius: '8px',
      overflow: 'hidden',
      border: `1px solid ${color}40`
    }}>
      <div ref={containerRef} style={{ width: '100%', height: '100%' }} />
    </div>
  );
}
