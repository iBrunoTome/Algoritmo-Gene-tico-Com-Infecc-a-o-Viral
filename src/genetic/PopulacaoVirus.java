package genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import gude.Graph;
import gude.Vertex;

public class PopulacaoVirus {

	private Graph g;
	private List<Virus> populacaoVirus = new ArrayList<Virus>();
	private List<Vertex> rotaList = new ArrayList<Vertex>();
	private Integer tamVirus = 0;

	public PopulacaoVirus(Graph g, Integer tamVirus) {
		this.g = g;
		this.tamVirus = tamVirus;
		for (Vertex v : g.getArrayListOfVertexes()) {
			this.rotaList.add(v);
		}
	}

	public Integer getTamVirus() {
		return tamVirus;
	}

	public void setTamVirus(Integer tamVirus) {
		this.tamVirus = tamVirus;
	}

	public List<Virus> getPopulacaoVirus() {
		return populacaoVirus;
	}

	public void setPopulacaoVirus(List<Virus> populacaoVirus) {
		this.populacaoVirus = populacaoVirus;
	}

	public List<Vertex> getRotaAux() {
		return rotaList;
	}

	public void setRotaAux(List<Vertex> rotaList) {
		this.rotaList = rotaList;
	}

	/**
	 * Ordena a população de cromossomos pelo critério de melhor fitness, ou
	 * seja, menor soma das distâncias entre as cidades
	 */

	public void ordenaPopulacao() {
		Collections.sort(this.populacaoVirus, new Comparator<Virus>() {
			@Override
			public int compare(Virus v1, Virus v2) {
				return Double.compare(v1.getInfectabilidade(), v2.getInfectabilidade());
			}
		});
	}

	/**
	 * Seleciona um virus através do método de roleta, de acordo com seu rank
	 * 
	 * @return virusSelecionado
	 */

	public Virus selecionaVirus() {
		Integer roleta = 0;
		Fatia[] pizza = new Fatia[this.populacaoVirus.size()];
		Double acumulado = 0.0;
		Virus virusSelecionado = new Virus(this.g);

		roleta = this.geraRange(100)[1];

		for (Integer i = 0; i < this.populacaoVirus.size(); i++) {
			Fatia fatia = new Fatia();
			if (i == 0) {
				fatia.setInicio(0.0);
				fatia.setFim((double) this.populacaoVirus.get(i).getInfectabilidade());
				acumulado = fatia.getFim();
				pizza[i] = fatia;
			} else {
				fatia.setInicio(acumulado + 0.1);
				fatia.setFim(this.populacaoVirus.get(i).getInfectabilidade() + fatia.getInicio());
				pizza[i] = fatia;
				acumulado = fatia.getFim();
			}
		}

		for (Integer i = 0; i < pizza.length; i++) {
			if ((roleta > pizza[i].getInicio()) && roleta < pizza[i].getFim()) {
				virusSelecionado = this.populacaoVirus.get(i);
				break;
			}
		}

		return virusSelecionado;
	}

	/**
	 * Calcula e seta o rank de cada virus pela divisão de seu fitness e o
	 * somatório de fitness de todos os pais selecionados
	 */

	public void rankeiaVirus() {

		Double rank = 0.0;
		Double somaRank = 0.0;

		for (Virus virus : this.populacaoVirus) {
			somaRank += virus.getInfectabilidade();
		}

		for (Virus virus : this.populacaoVirus) {
			rank = (virus.getInfectabilidade() / somaRank);
			virus.setRank(rank);
		}

	}

	/**
	 * Gera um range válido para inserção do vírus
	 * 
	 * @param tamCromossomo
	 * @return range
	 */

	public Integer[] geraRange(Integer tamCromossomo) {

		Integer rand1 = 0;
		Integer rand2 = 0;
		Integer validaRand = 0;
		Integer[] range = new Integer[2];
		Random gerador = new Random();

		do {
			rand1 = gerador.nextInt(tamCromossomo);
			rand2 = gerador.nextInt(tamCromossomo);
			validaRand = Math.abs(rand2 - rand1);

			if (validaRand == 0) {
				continue;
			}
		} while (validaRand != (Math.floor((tamCromossomo / this.tamVirus))));

		if (rand2 > rand1) {
			range[0] = rand1;
			range[1] = rand2;
		} else {
			range[0] = rand2;
			range[1] = rand1;
		}

		return range;

	}

	/**
	 * Gera uma nova permutação de cidades, cria o vírus e o adiciona a uma
	 * população de vírus
	 * 
	 * @param tamPopulacaoVirus
	 *            Tamanho da população de vírus
	 */

	public void geraPopulacaoVirus(Integer tamPopulacaoVirus) {
		Integer contPermutacao = 0;
		Vertex[] subRota = new Vertex[(int) Math.floor((this.g.getNumVertex() / this.tamVirus))];

		while (contPermutacao < tamPopulacaoVirus) {
			Integer i = 0, j = 0;
			Collections.shuffle(this.rotaList);
			Integer[] range = this.geraRange(this.g.getNumVertex());

			for (i = range[0]; i < range[1]; i++) {
				subRota[j] = this.rotaList.get(i);
				j++;
			}

			/**
			 * Seta o novo virus
			 */

			Virus virus = new Virus(this.g);
			virus.setSubRota(subRota);

			/**
			 * Adiciona o rotaList gerado a população
			 */

			this.populacaoVirus.add(virus);
			contPermutacao++;

		}

		this.rankeiaVirus();
		this.ordenaPopulacao();

	}

}
