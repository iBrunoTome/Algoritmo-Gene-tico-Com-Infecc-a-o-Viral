package genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import gude.Graph;
import gude.Vertex;

/**
 * 
 * @author
 * 		<p>
 *         Bruno Tomé
 *         </p>
 * @author
 * 		<p>
 *         Cláudio Menezes
 *         </p>
 *         Implementação do algoritmo genético com infecção viral do artigo: "Um
 *         algoritmo genético com infecção viral para o problema do caixeiro
 *         viajante."
 *
 */

public class Genetic {

	private static Graph g;
	private static Integer maxPopulacao;
	private static List<Cromossomo> hallDaFama = new ArrayList<Cromossomo>();

	/**
	 * Seleciona os possíveis pais para realização do crossover
	 * 
	 * @param populacao
	 * 
	 * @return Populacao pais
	 */
	public static Populacao rankeiaPais(Populacao populacao) {

		Double somaRank = 0.0;
		Populacao possiveisPais = new Populacao(g);
		List<Cromossomo> listCromossomoAux = new ArrayList<Cromossomo>();

		for (Cromossomo cromossomo : populacao.getPopulacao()) {
			somaRank += cromossomo.getFitness();
			listCromossomoAux.add(cromossomo);
		}

		possiveisPais.setPopulacao(listCromossomoAux);
		possiveisPais.ordenaPopulacao();
		possiveisPais.calculaRank(somaRank);

		return possiveisPais;
	}

	/**
	 * Seleciona um pai através do método de roleta, de acordo com seu rank
	 * 
	 * @param possiveisPais
	 *            População de possíveis pais
	 * @return Cromossomo
	 */
	public static Cromossomo selecionaPai(Populacao possiveisPais) {
		Integer roleta = 0;
		Fatia[] pizza = new Fatia[possiveisPais.getPopulacao().size()];
		Double acumulado = 0.0;
		Cromossomo pai = new Cromossomo(g);

		Random rand = new Random();
		roleta = rand.nextInt(100);

		for (Integer i = 0; i < possiveisPais.getPopulacao().size(); i++) {
			Fatia fatia = new Fatia();
			if (i == 0) {
				fatia.setInicio(0.0);
				fatia.setFim(possiveisPais.getPopulacao().get(i).getFitness());
				acumulado = fatia.getFim();
				pizza[i] = fatia;
			} else {
				fatia.setInicio(acumulado + 0.1);
				fatia.setFim(possiveisPais.getPopulacao().get(i).getFitness() + fatia.getInicio());
				pizza[i] = fatia;
				acumulado = fatia.getFim();
			}
		}

		for (Integer i = 0; i < pizza.length; i++) {
			if ((roleta > pizza[i].getInicio()) && roleta < pizza[i].getFim()) {
				pai = possiveisPais.getPopulacao().get(i);
				break;
			}
		}

		return pai;
	}

	/**
	 * Realiza o crossover entre os pais, no formato OX1
	 * 
	 * @param pai
	 *            Cromossomo pai
	 * @param mae
	 *            Cromossomo mae
	 * 
	 * @return List<Cromossomo> filhos
	 */
	public static List<Cromossomo> crossOX1(Cromossomo pai, Cromossomo mae) {
		Integer faca = 0;
		Random rand = new Random();
		faca = rand.nextInt(pai.getRota().length);
		Vertex[] crossAuxPai = pai.getRota();
		List<Cromossomo> filhos = new ArrayList<Cromossomo>();

		for (Integer i = faca; i < pai.getRota().length; i++) {
			crossAuxPai[i] = mae.getRota()[i];
		}

		// cria filho1
		Cromossomo filho1 = new Cromossomo(g);
		filho1.setRota(crossAuxPai);

		Vertex[] crossAuxMae = mae.getRota();

		for (Integer i = faca; i < pai.getRota().length; i++) {
			crossAuxMae[i] = pai.getRota()[i];
		}

		// cria filho2
		Cromossomo filho2 = new Cromossomo(g);
		filho2.setRota(crossAuxMae);

		filhos.add(filho1);
		filhos.add(filho2);

		return filhos;

	}

	/**
	 * Realiza o crossover da população
	 * 
	 * @param populacao
	 * @param maxCruzamentos
	 * @return populacao
	 */
	public static Populacao crossover(Populacao populacao, Integer maxCruzamentos) {
		Populacao possiveisPais = rankeiaPais(populacao);
		List<Cromossomo> populacaoAux = populacao.getPopulacao();
		List<Cromossomo> pais = new ArrayList<Cromossomo>();

		Cromossomo pai = new Cromossomo(g);
		Cromossomo mae = new Cromossomo(g);
		Integer roleta = 0;
		Integer index = 0;

		for (Integer i = 0; i < ((Integer) (possiveisPais.getPopulacao().size() / maxCruzamentos)); i++) {
			pais.add(selecionaPai(possiveisPais));
		}

		while (!pais.isEmpty()) {
			Random rand = new Random();
			roleta = rand.nextInt(100);

			if (roleta <= 80) {
				try {
					pai = pais.get(index);
					mae = pais.get(index + 1);
					pais.remove(index);
					pais.remove(index + 1);

					List<Cromossomo> filhos = crossOX1(pai, mae);
					populacaoAux.add(filhos.get(0));
					populacaoAux.add(filhos.get(1));
					index += 2;
				} catch (Exception e) {
					break;
				}
			} else {
				pais.remove(index);
				index++;
			}

		}

		populacao.setPopulacao(populacaoAux);

		return populacao;

	}

	/**
	 * Ordena a população de cromossomos pelo critério de melhor fitness, ou
	 * seja, menor soma das distâncias entre as cidades
	 */
	public static void ordenaHallDaFama() {
		Collections.sort(hallDaFama, new Comparator<Cromossomo>() {
			@Override
			public int compare(Cromossomo p1, Cromossomo p2) {
				return Double.compare(p1.getFitness(), p2.getFitness());
			}
		});
	}

	/**
	 * Adiciona o cromossomo ao hall da fama se seu fitness for melhor que o 1º
	 * colocado. Elimina alguns cromossomos da população de acordo com sua
	 * inanição
	 * 
	 * @param populacao
	 * @return populacao
	 */
	public static Populacao entraHall(Populacao populacao) {

		// Elimina 1/4 da população caso ela seja maior que maxPopualacao
		if (populacao.getPopulacao().size() >= maxPopulacao) {
			populacao.setPopulacao(populacao.getPopulacao().subList(0,
					((int) (maxPopulacao - (populacao.getPopulacao().size() / 4)))));
		}

		// Redefine na rota a cidade de partida e o destino
		Vertex auxFim;
		Vertex auxInicio;
		for (Cromossomo cromAux : populacao.getPopulacao()) {
			for (int i = 0; i < cromAux.getRota().length; i++) {
				if (cromAux.getRota()[i].getId() == 1) {
					auxInicio = cromAux.getRota()[i];
					cromAux.getRota()[i] = cromAux.getRota()[0];
					cromAux.getRota()[0] = auxInicio;
				}

				if (cromAux.getRota()[i].getId() == g.getNumVertex()) {
					auxFim = cromAux.getRota()[i];
					cromAux.getRota()[i] = cromAux.getRota()[g.getNumVertex() - 1];
					cromAux.getRota()[g.getNumVertex() - 1] = auxFim;
				}
			}
		}

		populacao.inanicao();
		ordenaHallDaFama();

		/*
		 * Adiciona o cromossomo ao hall da fama se seu fitness for melhor que o
		 * 1º colocado
		 */

		List<Cromossomo> rota = populacao.getPopulacao();
		List<Cromossomo> rotaAux = new ArrayList<Cromossomo>(rota);
		ListIterator<Cromossomo> cromossomo = rotaAux.listIterator();

		while (cromossomo.hasNext()) {
			Cromossomo cromossomoAux = cromossomo.next();
			if (hallDaFama.get(0).getFitness() > cromossomoAux.getFitness()) {
				hallDaFama.add(0, cromossomoAux);
				// Limita o tamanho do hall da fama para 10 rotas
				if (hallDaFama.size() > 10) {
					hallDaFama = hallDaFama.subList(0, 10);
				}
			}
			/*
			 * Mata o cromossomo se seu tempo de vida passou ou a população
			 * cresceu demais
			 */
			if ((cromossomoAux.getTempoDeVida() <= 0) && (rota.size() > maxPopulacao)) {
				rota.remove(cromossomoAux);
			}
		}

		if ((hallDaFama.get(0).getRota()[g.getNumVertex() - 1].getId() != g.getNumVertex())
				|| (hallDaFama.get(0).getRota()[0].getId() != 1)) {
			hallDaFama.remove(0);
		}

		populacao.setPopulacao(rota);
		return populacao;
	}

	/**
	 * Mata os clones de uma população e a retorna
	 * 
	 * @param populacao
	 * @return populacao
	 */
	public static Populacao mataClones(Populacao populacao) {
		List<Cromossomo> rota = populacao.getPopulacao();
		List<Cromossomo> rotaAux = new ArrayList<Cromossomo>(rota);
		ListIterator<Cromossomo> cromossomo = rotaAux.listIterator();
		Double fitnessAnterior = 0.0;

		populacao.inanicao();
		ordenaHallDaFama();

		while (cromossomo.hasNext()) {
			Cromossomo cromossomoAux = cromossomo.next();
			if (cromossomoAux.getFitness().equals(fitnessAnterior)) {
				rota.remove(cromossomoAux);
			} else {
				fitnessAnterior = cromossomoAux.getFitness();
			}
		}

		populacao.setPopulacao(rota);

		return populacao;
	}

	/*
	 * 1. [Inicialização] Gerar uma população inicial de n cromossomos,
	 * aleatoriamente, e determinar a fitness de cada cromossomo. Gerar uma
	 * população inicial de vírus, aleatoriamente;
	 * 
	 * 2. [Infecção] Aplicar o operador de infecção nos melhores indivíduos
	 * da população.
	 * 
	 * 3. [Geração da Nova população] Criar uma nova população através da
	 * aplicação das seguintes etapas: a) [Seleção] Selecionar dois
	 * cromossomos-pais da população atual de acordo com sua fitness; b)
	 * [Crossover] Fazer o cruzamento dos pais para formar novos indivíduos
	 * (filhos).
	 * 
	 * 4. [Avaliar nova população] Calcular a fitness de cada cromossomo da
	 * população récem gerada;
	 * 
	 * 5. [Teste de parada] Se condição de parada satisfeita: finalizar
	 * retornando a melhor solução encontrada. Caso contrário, voltar ao
	 * passo 2.
	 */

	public static void main(String[] args) {

		final long startTime = System.currentTimeMillis();

		g = Graph.loadXML("grafo-50.xml");
		maxPopulacao = 2000;
		Integer geracaoAtual = 0;
		// Máximo de gerações
		Integer maxGeracoes = 300;
		// Cruza 1/5 da população
		Integer maxCruzamentos = 5;
		// Tamanho inicial da população de cromossomos
		Integer tamPopulacao = 500;
		// Tamanho da população de vírus
		Integer tamPopulacaoVirus = (tamPopulacao / 8);
		// Vírus de um 1/8 do tamanho da rota do cromossomo
		Integer tamVirus = 8;
		// Porcentagem de cromossomos infectados
		Integer porcentagemInfectados = 8;
		// Instancia populações
		Populacao populacao = new Populacao(g);
		PopulacaoVirus populacaoVirus = new PopulacaoVirus(g, tamVirus);

		/*
		 * 1. [Inicialização] Gerar uma população inicial de n cromossomos,
		 * aleatoriamente, e determinar a fitness de cada cromossomo. Gerar uma
		 * população inicial de vírus, aleatoriamente;
		 */
		populacao.geraPopulacao(tamPopulacao);
		// Adiciona o melhor cromossomo no hall da fama
		hallDaFama.add(populacao.getPopulacao().get(0));
		populacaoVirus.geraPopulacaoVirus(tamPopulacaoVirus);

		while (geracaoAtual < maxGeracoes) {

			/*
			 * 2. [Infecção] Aplicar o operador de infecção nos melhores
			 * indivíduos da população.
			 */

			Integer maxInfectados = 0;
			List<Cromossomo> rota = populacao.getPopulacao();
			List<Cromossomo> rotaAux = new ArrayList<Cromossomo>(rota);
			ListIterator<Cromossomo> cromossomo = rotaAux.listIterator();

			Virus virus = new Virus(g);
			virus = populacaoVirus.selecionaVirus();
			while (virus.getSubRota() == null) {
				virus = populacaoVirus.selecionaVirus();
			}

			while (cromossomo.hasNext() && (maxInfectados < Math.floor(rota.size() / porcentagemInfectados))) {
				rota.add(virus.infecta(cromossomo.next()));
				maxInfectados++;
			}

			populacao = mataClones(populacao);
			populacao.setPopulacao(rota);

			/*
			 * 3. [Geração da Nova população] Criar uma nova população
			 * através da aplicação das seguintes etapas:
			 * 
			 * a) [Seleção] Selecionar dois cromossomos-pais da população
			 * atual de acordo com sua fitness;
			 * 
			 * b) [Crossover] Fazer o cruzamento dos pais para formar novos
			 * indivíduos (filhos).
			 */

			populacao = crossover(populacao, maxCruzamentos);

			/*
			 * 4. [Avaliar nova população] Calcular a fitness de cada
			 * cromossomo da população récem gerada;
			 */

			populacao = entraHall(populacao);
			populacao.ordenaPopulacao();

			/*
			 * 5. [Teste de parada] Se condição de parada satisfeita:
			 * finalizar retornando a melhor solução encontrada. Caso
			 * contrário, voltar ao passo 2.
			 */

			geracaoAtual++;
			System.out.println("Geração nº " + geracaoAtual);
		}

		final long endTime = System.currentTimeMillis();
		System.out.println("\nHALL DA FAMA");
		System.out.print("\nCusto da rota: " + hallDaFama.get(0).getFitness());
		System.out.print("\nRota selecionada: ");
		for (int i = 0; i < hallDaFama.get(0).getRota().length; i++) {
			System.out.print(hallDaFama.get(0).getRota()[i].getId() + "\t");
		}
		System.out.println();
		System.out.println("Total execution time: " + (endTime - startTime) + " milisegundos");

	}

}
