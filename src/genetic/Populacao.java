package genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import gude.Graph;
import gude.Vertex;

public class Populacao {

	private Graph g;
	private List<Cromossomo> populacao = new ArrayList<Cromossomo>();
	private List<Vertex> rotaAux = new ArrayList<Vertex>();

	public Populacao(Graph g) {
		this.g = g;
		this.rotaAux = g.getArrayListOfVertexes();
	}

	public List<Cromossomo> getPopulacao() {
		return this.populacao;
	}

	public void setPopulacao(List<Cromossomo> populacao) {
		this.populacao = populacao;
	}

	/**
	 * Calcula e seta o rank de cada cromossomo pela divisão de seu fitness e o
	 * somatório de fitness de todos os pais selecionados
	 * 
	 * @param somaRank
	 */

	public void calculaRank(Double somaRank) {

		Double rank = 0.0;

		for (Cromossomo cromossomo : this.populacao) {
			rank = (100 - (cromossomo.getFitness() / somaRank));
			cromossomo.setRank(rank);
		}

	}

	/**
	 * Ordena a população de cromossomos pelo critério de melhor fitness, ou
	 * seja, menor soma das distâncias entre as cidades
	 */

	public void ordenaPopulacao() {
		Collections.sort(this.populacao, new Comparator<Cromossomo>() {
			@Override
			public int compare(Cromossomo p1, Cromossomo p2) {
				return Double.compare(p1.getFitness(), p2.getFitness());
			}
		});
	}

	/**
	 * Diminui o tempo de vida dos cromossomos em -1 unidade
	 */

	public void inanicao() {
		for (Cromossomo cromossomo : this.populacao) {
			Integer vidaAux = 0;
			vidaAux = cromossomo.getTempoDeVida() - 1;
			cromossomo.setTempoDeVida(vidaAux);
		}
	}

	/**
	 * Gera a população de cromossomos
	 * 
	 * @param tamPopulacao
	 *            Tamanho da populacao
	 */

	public void geraPopulacao(Integer tamPopulacao) {
		Integer contPermutacao = 0;
		Vertex[] rotaAux = new Vertex[this.g.getNumVertex()];

		while (contPermutacao < tamPopulacao) {
			Integer i = 0;
			Collections.shuffle(this.rotaAux);

			/**
			 * Converte a lista em um vetor para facilitar a permutação das
			 * cidades
			 */

			for (Vertex index : this.rotaAux) {
				rotaAux[i] = index;
				i++;
			}

			Cromossomo cromossomo = new Cromossomo(this.g);
			cromossomo.setRota(rotaAux);

			/**
			 * Adiciona o rotaAux gerado a população
			 */

			this.populacao.add(cromossomo);
			contPermutacao++;

		}

		this.ordenaPopulacao();

	}

}
