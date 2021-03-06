package gude;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Classe que representa um grafo.
 *
 * <p>
 * Um objeto da classe Graph comporta-se como uma fábrica de vértices e de
 * arestas. Um grafo contém uma coleção de vértices e uma coleção de arestas.
 * </p>
 *
 * @author Walace
 * @version 1.0
 */
public class Graph implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1499907369115352868L;

	/**
	 * Classe interna responsável por gerenciar a criação de chaves únicas para
	 * os elementos do grafo.
	 *
	 * <p>
	 * Cuidado: Não mude isso!
	 * </p>
	 */
	private class Semente implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5250793218536865000L;
		int inteiro;

		/** Construtor. */
		public Semente(int inteiro) {
			this.inteiro = inteiro;
		}

		/** Define a semente. */
		public void setSemente(int inteiro) {
			this.inteiro = inteiro;
		}

		/** Retorna uma chave única para um novo elemento do grafo. */
		public int getSemente() {
			return ++inteiro;
		}
	}

	/**
	 * Semente para gerar chaves para elementos do grafo.
	 */
	private Semente idControlVertex, idControlEdge;
	/**
	 * Armazena os vértices do grafo.
	 */
	private TreeMap<Integer, Vertex> vertexTree;
	/**
	 * Armazena as arestas do grafo.
	 */
	private TreeMap<Integer, Edge> edgeTree;

	/**
	 * Cria um grafo vazio, sem vértices e sem arestas.
	 *
	 */
	public Graph() {
		vertexTree = new TreeMap<Integer, Vertex>();
		edgeTree = new TreeMap<Integer, Edge>();
		idControlEdge = new Semente(0);
		idControlVertex = new Semente(0);
	}

	/**
	 * Retorna um identificador único para vértice, usado como chave.<br>
	 * (uso interno, chamado na criacao de vértices)
	 *
	 * @result id de vértice.
	 */
	int generateIdVertex() {
		return idControlVertex.getSemente();
	}

	/**
	 * Retorna um identificador único para aresta, usado como chave.<br>
	 * (uso interno, chamado na criação de arestas)
	 *
	 * @result id de aresta.
	 */
	int generateIdEdge() {
		return idControlEdge.getSemente();
	}

	/**
	 * Insere novo vértice na coleção de vértices do grafo.<br>
	 * (uso interno, chamado na criacao de vértices)
	 *
	 * @param id
	 *            id do vértice.
	 * @param v
	 *            vértice.
	 */
	void insertVertexInList(int id, Vertex v) {
		getVertexTree().put(id, v);
	}

	/**
	 * Insere nova aresta na coleção de arestas do grafo.<br>
	 * (uso interno, chamado na criação de arestas)
	 *
	 * @param id
	 *            id da aresta.
	 * @param a
	 *            aresta.
	 */
	void insertEdgeInList(int id, Edge a) {
		getEdgeTree().put(id, a);
	}

	/**
	 * Remove um vértice da coleção de vértices do grafo.<br>
	 * (uso interno, chamado na remoção de vértices)
	 *
	 * @param id
	 *            id do vértice que será removido.
	 */
	void removeVertexFromList(int id) {
		getVertexTree().remove(id);
	}

	/**
	 * Remove uma aresta da coleção de arestas do grafo.<br>
	 * (uso interno, chamado na remoção de arestas)
	 *
	 * @param id
	 *            id da aresta que será removida.
	 */
	void removeEdgeFromList(int id) {
		getEdgeTree().remove(id);
	}

	/**
	 * Remove um vértice do grafo.
	 *
	 * @param id
	 *            id do vértice que será removido.
	 * @see #removeEdge(int)
	 */
	public void removeVertex(int id) {
		getVertex(id).destroy();
	}

	/**
	 * Remove uma aresta do grafo.
	 *
	 * @param id
	 *            id da aresta que será removida.
	 * @see #removeVertex(int)
	 */
	public void removeEdge(int id) {
		getEdge(id).destroy();
	}

	/**
	 * Cria e insere um novo vértice no grafo.
	 *
	 * @return o vértice criado.
	 * @see #createEdge(int,int)
	 */
	public Vertex createVertex() {
		return new Vertex(this);
	}

	/**
	 * Cria e insere uma nova aresta no grafo.
	 *
	 * @param alpha
	 *            id do vértice de partida da aresta.
	 * @param omega
	 *            id do vértice de chegada da aresta.
	 * @return a aresta criada.
	 * @see #createVertex()
	 */
	public Edge createEdge(int alpha, int omega, Double distancia) {
		if (idVertexExists(alpha) && idVertexExists(omega))
			return new Edge(alpha, omega, distancia, this);
		else
			return null;
	}

	/**
	 * Retorna o número de vértices que existem no grafo.
	 *
	 * @return número de vértices no grafo.
	 * @see #getNumEdge()
	 */
	public int getNumVertex() {
		return getVertexTree().size();
	}

	/**
	 * Retorna o número de arestas que existem no grafo.
	 *
	 * @return número de arestas no grafo.
	 * @see #getNumVertex()
	 */
	public int getNumEdge() {
		return getEdgeTree().size();
	}

	/**
	 * Salva o grafo em arquivo, serializado.
	 *
	 * @param grafo
	 *            grafo que será salvo.
	 * @param nomeArq
	 *            nome do arquivo.
	 * @return true se operação foi concluída com sucesso, false caso contrário.
	 * @see #saveFile(String)
	 */
	public static boolean saveFile(Graph grafo, String nomeArq) {
		boolean ok = true;
		try {
			FileOutputStream fs = new FileOutputStream(nomeArq);
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(grafo);
			os.close();
		} catch (Exception ex) {
			ok = false;
		}
		return ok;
	}

	/**
	 * Carrega um grafo que foi salvo previamente, serializado.
	 *
	 * @param nomeArq
	 *            nome do arquivo.
	 * @return o grafo lido, ou null em caso de erro.
	 * @see #loadFile(Graph,String)
	 */
	public static Graph loadFile(String nomeArq) {
		Graph grafo = null;
		try {
			FileInputStream fs = new FileInputStream(nomeArq);
			ObjectInputStream os = new ObjectInputStream(fs);
			grafo = (Graph) os.readObject();
			os.close();
		} catch (Exception ex) {
			grafo = null;
		}
		return grafo;
	}

	/**
	 * Lê um xml e cria o grafo
	 * 
	 * @param nomeArquivo
	 * @return
	 */

	public static Graph loadXML(String nomeArquivo) {
		File f = new File(nomeArquivo);
		Graph g = new Graph();
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(f);
			Element root = (Element) doc.getRootElement();

			// get vertexes
			List<Element> vertexes = root.getChild("Vertexes").getChildren();
			Iterator<Element> i = vertexes.iterator();

			while (i.hasNext()) {
				Element vertex = (Element) i.next();
				String idVertex = vertex.getAttributeValue("id");
				Vertex vertexAux = g.createVertex();
				vertexAux.setId(Integer.parseInt(idVertex));
			}

			// get edges
			List<Element> edges = root.getChild("Edges").getChildren();
			i = edges.iterator();

			while (i.hasNext()) {
				Element edge = (Element) i.next();
				String idEdge = edge.getAttributeValue("id");
				String alpha = edge.getAttributeValue("alpha");
				String omega = edge.getAttributeValue("omega");
				String distancia = edge.getAttributeValue("distancia");
				Edge edgeAux = g.createEdge(Integer.parseInt(alpha), Integer.parseInt(omega),
						Double.parseDouble(distancia));
				edgeAux.setId(Integer.parseInt(idEdge));
			}

			return g;

		} catch (JDOMException | IOException ex) {
			Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	public static void txt2xml(String nomeArquivo) {
		try {
			BufferedWriter arquivoXML = new BufferedWriter(
					new FileWriter(nomeArquivo.substring(0, nomeArquivo.length() - 4) + ".xml"));

			arquivoXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n<Graph>");

			FileReader arquivoTXT = new FileReader(nomeArquivo);
			BufferedReader lerArq = new BufferedReader(arquivoTXT);
			String linha = lerArq.readLine();
			String[] aux = null;
			Integer vertexAtual = 1;
			while (linha != null) {
				System.out.printf("%s\n", linha);
				aux = linha.split(" ");

				if (aux[0].equals("p")) {
					arquivoXML.append("\n\t<Attributes idControlVertex=\"" + aux[2] + "\" idControlEdge=\"" + aux[3]
							+ "\" />" + "\n\t<Vertexes>");
					for (int i = 1; i <= Integer.parseInt(aux[2]); i++) {
						arquivoXML.append("\n\t\t<Vertex id=\"" + i + "\" />");
					}

					arquivoXML.append("\n\t</Vertexes>\n\t<Edges>");
				} else if (aux[0].equals("e")) {
					arquivoXML.append("\n\t\t<Edge id=\"" + vertexAtual + "\" alpha=\"" + aux[1] + "\" omega=\""
							+ aux[2] + "\" />");
					vertexAtual++;
				}

				linha = lerArq.readLine();
			}
			arquivoXML.append("\n\t</Edges>\n</Graph>");
			arquivoTXT.close();
			arquivoXML.close();
		} catch (IOException e) {
			System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
		}
	}

	/**
	 * Limpa o grafo, ou seja, remove todos os vértices e as arestas dele.
	 */
	public void clear() {
		Iterator<?> it = getVertexTree().navigableKeySet().iterator();
		while (it.hasNext()) {
			((Vertex) it.next()).destroy();
		}
		getEdgeTree().clear();
		getVertexTree().clear();
		idControlEdge.setSemente(0);
		idControlVertex.setSemente(0);
	}

	/**
	 * Busca um vértice no grafo.
	 *
	 * @param id
	 *            id do vértice solicitado.
	 * @return o vértice requisitado ou null se ele não existir.
	 * @see #getEdge(int)
	 */
	public Vertex getVertex(int id) {
		return getVertexTree().get(id);
	}

	/**
	 * Retorna o primeiro vértice do grafo.
	 *
	 * @return o primeiro vértice ou null se não existir nenhum.
	 * @see #nextVertex(int)
	 */
	public Vertex firstVertex() {
		if (getNumVertex() == 0)
			return null;
		else {
			int id = getVertexTree().firstKey();
			return getVertex(id);
		}
	}

	/**
	 * Retorna o próximo vértice do grafo depois do vértice com chave id.
	 *
	 * @param id
	 *            identificador de vértice.
	 * @return o próximo vértice ou null se não existir mais nenhum.
	 * @see #firstVertex()
	 */
	public Vertex nextVertex(int id) {
		if (!idVertexExists(id) || getVertexTree().lastKey() == id)
			return null;
		else {
			SortedMap<Integer, Vertex> map = getVertexTree().tailMap(id + 1);
			int novoId = map.firstKey();
			return getVertex(novoId);
		}
	}

	/**
	 * Retorna uma lista com os vértices contidos no grafo.
	 *
	 * @return arrayList contendo os vértices do grafo.
	 * @see #firstVertex()
	 * @see #nextVertex(int)
	 */
	public ArrayList<Vertex> getArrayListOfVertexes() {
		ArrayList<Vertex> lista = new ArrayList<Vertex>();
		for (Vertex v = firstVertex(); v != null; v = nextVertex(v.getId()))
			lista.add(v);
		return lista;
	}

	/**
	 * Testa se existe um vértice com um certo identificador.
	 *
	 * @param id
	 *            id de vértice.
	 * @return true se existir, false se não existir.
	 * @see #idEdgeExists(int)
	 */
	public boolean idVertexExists(int id) {
		return getVertexTree().containsKey(id);
	}

	/**
	 * Busca uma aresta no grafo.
	 *
	 * @param id
	 *            id da aresta solicitada.
	 * @return a aresta requisitada ou null se ela não existir.
	 * @see #getVertex(int)
	 * @see #getEdge(int,int)
	 */
	public Edge getEdge(int id) {
		return getEdgeTree().get(id);
	}

	/**
	 * Retorna a primeira aresta do grafo.
	 *
	 * @return a primeira aresta ou null se não existir nenhuma.
	 * @see #nextEdge(int)
	 */
	public Edge firstEdge() {
		if (getNumEdge() == 0)
			return null;
		else {
			int id = getEdgeTree().firstKey();
			return getEdge(id);
		}
	}

	/**
	 * Retorna a próxima aresta do grafo depois da aresta com chave id.
	 *
	 * @param id
	 *            identificador de aresta.
	 * @return a próxima aresta ou null se não existir mais nenhuma.
	 * @see #firstEdge()
	 */
	public Edge nextEdge(int id) {
		if (!idEdgeExists(id) || getEdgeTree().lastKey() == id)
			return null;
		else {
			SortedMap<Integer, Edge> map = getEdgeTree().tailMap(id + 1);
			int novoId = map.firstKey();
			return getEdge(novoId);
		}
	}

	/**
	 * Retorna uma lista com as arestas contidas no grafo.
	 *
	 * @return arrayList contendo as arestas do grafo.
	 * @see #firstEdge()
	 * @see #nextEdge(int)
	 */
	public ArrayList<Edge> getArrayListOfEdges() {
		ArrayList<Edge> lista = new ArrayList<Edge>();
		for (Edge a = firstEdge(); a != null; a = nextEdge(a.getId()))
			lista.add(a);
		return lista;
	}

	/**
	 * Testa se existe uma aresta com um certo identificador.
	 *
	 * @param id
	 *            id de aresta.
	 * @return true se existir, false se não existir.
	 * @see #idVertexExists(int)
	 */
	public boolean idEdgeExists(int id) {
		return getEdgeTree().containsKey(id);
	}

	/**
	 * Testa se existe uma aresta unindo dois vértices específicos, considerando
	 * o grafo direcionado.
	 *
	 * @param alpha
	 *            id do vértice de partida.
	 * @param omega
	 *            id do vértice de chegada.
	 * @return true se existir, false se não existir.
	 * @see #idEdgeExists(int)
	 * @see #linkExists(int,int)
	 */
	public boolean edgeExists(int alpha, int omega) {
		Edge a = getEdge(alpha, omega);
		return (a == null) ? false : true;
	}

	/**
	 * Busca uma aresta no grafo.
	 *
	 * @param alpha
	 *            id do vértice de partida.
	 * @param omega
	 *            id do vértice de chegada.
	 * @return a aresta requisitada ou null se ela não existir.
	 * @see #getEdge(int)
	 * @see #idEdgeExists(int)
	 * @see #linkExists(int,int)
	 */
	public Edge getEdge(int alpha, int omega) {
		if (!idVertexExists(alpha) || !idVertexExists(omega)) {
			return null;
		}
		Vertex v = getVertex(alpha);
		for (Edge a = v.firstOut(); a != null; a = v.nextOut(a.getId())) {
			if (a.getOmega().getId() == omega) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Testa se existe uma aresta ligando os vértices A e B, considerando o
	 * grafo não direcionado.
	 *
	 * @param idA
	 *            id do vértice A.
	 * @param idB
	 *            id do vértice B.
	 *
	 * @return edgeExists(idA,idB) || edgeExists(idB,idA)
	 *
	 * @see #edgeExists(int,int)
	 */
	public boolean linkExists(int idA, int idB) {
		Vertex v = getVertex(idA);
		if (v != null && idVertexExists(idB)) {
			for (Edge a = v.firstEdge(); a != null; a = v.nextEdge(a.getId())) {
				if (a.neighbour(idA).getId() == idB)
					return true;
			}
		}
		return false;
	}

	/**
	 * Destrói o grafo.
	 */
	public void destroy() {
		clear();
		try {
			this.finalize();
		} catch (Throwable ex) {
			Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Retorna a coleção de vértices do grafo.<br>
	 * (uso interno)
	 */
	TreeMap<Integer, Vertex> getVertexTree() {
		return vertexTree;
	}

	/**
	 * Define a coleção de vértices do grafo.<br>
	 * (uso interno)
	 */
	void setVertexTree(TreeMap<Integer, Vertex> arvoreVertices) {
		this.vertexTree = arvoreVertices;
	}

	/**
	 * Retorna a coleção de arestas do grafo.<br>
	 * (uso interno)
	 */
	TreeMap<Integer, Edge> getEdgeTree() {
		return edgeTree;
	}

	/**
	 * Define a coleção de arestas do grafo.<br>
	 * (uso interno)
	 */
	void setEdgeTree(TreeMap<Integer, Edge> arvoreArestas) {
		this.edgeTree = arvoreArestas;
	}

	/**
	 * Retorna a semente para identificadores de arestas.<br>
	 * (uso interno)
	 */
	Semente getIdControlEdge() {
		return idControlEdge;
	}

	/**
	 * Define a semente para identificadores de arestas.<br>
	 * (uso interno)
	 */
	void setIdControlEdge(Semente idControlEdge) {
		this.idControlEdge = idControlEdge;
	}

	/**
	 * Retorna a semente para identificadores de vértice.<br>
	 * (uso interno)
	 */
	Semente getIdControlVertex() {
		return idControlVertex;
	}

	/**
	 * Define a semente para identificadores de vértices.<br>
	 * (uso interno)
	 */
	void setIdControlVertex(Semente idControlVertex) {
		this.idControlVertex = idControlVertex;
	}

	public List<Vertex> ordenaGrau() {

		List<Vertex> vertexList = this.getArrayListOfVertexes();

		for (int i = vertexList.size(); i > 1; i--) {
			for (int j = 1; j < i; j++) {
				if ((vertexList.get(j - 1).getDegree()) > (vertexList.get(j).getDegree())) {
					Collections.swap(vertexList, j, j - 1);
				}
			}
		}

		return vertexList;
	}

	public List<Vertex> ordenaGrauDecrescente(List<Vertex> vertexListAux) {
		List<Vertex> vertexListDecrescente = new ArrayList<>();
		Integer j = vertexListAux.size() - 1;
		while (j >= 0) {
			vertexListDecrescente.add(vertexListAux.get(j));
			j--;
		}
		return vertexListDecrescente;
	}

	public List<Vertex> getAdjacentes(Integer id) {

		List<Vertex> adjacentes = new ArrayList<Vertex>();

		for (Integer i = 1; i <= this.getArrayListOfVertexes().size(); i++) {
			if (this.linkExists(i, id)) {
				adjacentes.add(this.getVertex(i));
			}
		}

		return adjacentes;
	}

}
