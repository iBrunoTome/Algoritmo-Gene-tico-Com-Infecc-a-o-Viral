package genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import gude.Graph;
import gude.Vertex;

public class Virus {

	private Graph g;
	private Double rank;
	private Integer infectabilidade;
	private Vertex[] subRota;

	private final Integer infectabilidadeDefault = 5;

	public Virus(Graph g) {
		this.infectabilidade = this.infectabilidadeDefault;
		this.g = g;
	}

	public Graph getG() {
		return g;
	}

	public void setG(Graph g) {
		this.g = g;
	}

	public Vertex[] getSubRota() {
		return this.subRota;
	}

	public void setSubRota(Vertex[] subRota) {
		this.subRota = subRota;
	}

	public Integer getInfectabilidade() {
		return infectabilidade;
	}

	public void setInfectabilidade(Integer infectabilidade) {
		this.infectabilidade = infectabilidade;
	}

	public Double getRank() {
		return rank;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}

	/**
	 * Gera um range válido para inserção do vírus
	 * 
	 * @param tamCromossomo
	 * @return range[2]
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
		} while (validaRand != this.subRota.length);

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
	 * Copia uma nova rota para o vírus e seta sua infectabilidade para default
	 * 
	 * @param cromossomo
	 */

	public void transducao(Cromossomo cromossomo) {
		Integer[] range = this.geraRange(cromossomo.getRota().length);
		Integer aux = 0;

		for (Integer i = range[0]; i < range[1]; i++) {
			this.subRota[aux] = cromossomo.getRota()[i];
		}

		this.setInfectabilidade(this.infectabilidadeDefault);
	}

	/**
	 * Recebe o cromossomo e o infecta
	 * 
	 * @param cromossomo
	 * @return Cromossomo
	 */

	public Cromossomo infecta(Cromossomo cromossomo) {
		Integer[] range = this.geraRange(cromossomo.getRota().length);

		ArrayList<Vertex> oldRota = new ArrayList(Arrays.asList(cromossomo.getRota()));
		ArrayList<Vertex> rotaInfectada = new ArrayList();
		// inicializa com null senao o size do ArrayList sera 0
		for (int i = 0; i < oldRota.size(); i++) {
			rotaInfectada.add(null);
		}

		ArrayList<Vertex> subRota_ = new ArrayList(Arrays.asList(this.subRota));
		Double fitnessVelho = cromossomo.getFitness();
		Integer aux = 0;

		// Copia o vírus como uma subrota
		for (Integer i = range[0]; i < range[1]; i++) {
			rotaInfectada.set(i, subRota_.get(aux));
			aux++;
		}

		/*
		 * Copia o resto da rota, respeitando as posições do vírus e sem
		 * repetições
		 */
		int primeiraPosicaoNula = 0;
		for (Vertex candidato : oldRota) {
			if (!rotaInfectada.contains(candidato)) {
				primeiraPosicaoNula = rotaInfectada.indexOf(null);
				rotaInfectada.add(primeiraPosicaoNula, candidato);
			}
		}

		// Remove possíveis posições nulas
		rotaInfectada.removeAll(Collections.singleton(null));
		cromossomo.setRota(rotaInfectada.toArray(new Vertex[rotaInfectada.size()]));

		/*
		 * Atualiza a infectabilidade do vírus de acordo com o fitness do
		 * cromossomo infectado
		 */
		if (fitnessVelho > cromossomo.getFitness()) {
			this.infectabilidade++;
		} else {
			this.infectabilidade--;
		}

		/*
		 * Realiza o processo de transdução quando a infectabilidade de um vírus
		 * chega a zero, ou seja, copia uma nova subrota do cromossomo infectado
		 * e seta sua infectabilidade para default novamente
		 */
		if (this.infectabilidade == 0) {
			this.transducao(cromossomo);
		}

		return cromossomo;
	}

}
