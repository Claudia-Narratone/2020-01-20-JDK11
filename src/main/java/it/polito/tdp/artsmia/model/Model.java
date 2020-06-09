package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private List<Adiacenza> adiacenze;
	private ArtsmiaDAO dao;
	private List<Integer> best;
	
	public Model() {
		dao=new ArtsmiaDAO();
	}
	
	public void creaGrafo(String ruolo) {
		grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		adiacenze=dao.getAdiacenze(ruolo);
		
		for(Adiacenza a:adiacenze) {
			if(!grafo.containsVertex(a.getA1())) 
				this.grafo.addVertex(a.getA1());
			
			if(!grafo.containsVertex(a.getA2())) 
				this.grafo.addVertex(a.getA2());
			
			if(this.grafo.getEdge(a.getA1(), a.getA2())==null) {
				Graphs.addEdgeWithVertices(grafo, a.getA1(), a.getA2(), a.getPeso());
			}
		}
		
		System.out.println("Grafo creato con "+this.grafo.vertexSet().size()+" vertici e "+grafo.edgeSet().size()+" archi");
	}
	
	public List<Integer> trovaPercorso(Integer sorgente){
		this.best=new ArrayList<Integer>();
		List<Integer> parziale=new ArrayList<Integer>();
		parziale.add(sorgente);
		ricorsione(parziale, -1);
		return best;
	}
	
	private void ricorsione(List<Integer> parziale, int peso) {
		
		if(parziale.size()>best.size()) {
			this.best=new ArrayList<Integer>(parziale);
		}
		
		Integer ultimo=parziale.get(parziale.size()-1);
		//ottengo i vicini
		List<Integer> vicini=Graphs.neighborListOf(grafo, ultimo);
		for(Integer vicino:vicini) {
			if (!parziale.contains(vicino) && peso==-1) {
				parziale.add(vicino);
				ricorsione(parziale, (int)this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, vicino)));
				parziale.remove(vicino);
			}else {
				if(!parziale.contains(vicino) && this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, vicino))==peso) {
					parziale.add(vicino);
					ricorsione(parziale, peso);
					parziale.remove(vicino);
				}
			}
			
		}
	}
	
	public boolean grafoContiene(Integer id) {
		if(this.grafo.containsVertex(id))
			return true;
		return false;
	}

	public List<String> getRuoli(){
		return this.dao.getRuoli();
	}
	
	public int vertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int archi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Adiacenza> getAdiacenze(){
		return adiacenze;
	}
}
