package genetic;

import gude.Graph;
import gude.Vertex;

public class Cromossomo {

	private Graph g;
	private Double fitness;
	private Vertex[] rota;
	private Double rank;
	private Integer tempoDeVida = 3;

	public Cromossomo(Graph g) {
		this.g = g;
	}

	public Graph getG() {
		return g;
	}

	public void setG(Graph g) {
		this.g = g;
	}

	public Double getFitness() {
		return fitness;
	}

	public Vertex[] getRota() {
		return rota;
	}

	public void setRota(Vertex[] rota) {
		this.rota = rota;
		this.setFitness(rota);
	}

	public Integer getTempoDeVida() {
		return tempoDeVida;
	}

	public void setTempoDeVida(Integer tempoDeVida) {
		this.tempoDeVida = tempoDeVida;
	}

	/**
	 * Seta o fitness de uma rota, que é a soma das distâncias entre elas
	 * 
	 * @param rota
	 */

	public void setFitness(Vertex[] rota) {
		Integer i;
		Double soma = 0.0;

		for (i = 1; i < rota.length; i++) {
			try {
				soma += this.g.getEdge(rota[i - 1].getId(), rota[i].getId()).getDistancia();
			} catch (Exception e) {
				if (rota[i] == rota[i - 1]) {
					soma += 0;
				} else {
					soma += this.g.getEdge(rota[i].getId(), rota[i - 1].getId()).getDistancia();
				}
			}
		}

		this.fitness = soma;
	}

	public Double getRank() {
		return rank;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}

}
