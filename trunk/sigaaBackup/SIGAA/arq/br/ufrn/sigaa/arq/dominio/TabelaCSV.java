/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 01/08/2012
 *
 */
package br.ufrn.sigaa.arq.dominio;

import java.util.LinkedList;

import br.ufrn.arq.util.StringUtils;

/**
 * Esta classe permite manipular dados importados de um CSV - Comma-Separted
 * Values.
 * <p>
 * Comma-separated values (CSV), em português Valores Separados por Vírgula, é
 * um formato de arquivo que armazena dados tabelados, cujo grande uso data da
 * época dos mainframes. Por serem bastante simples, arquivos .csv são comuns em
 * todas as plataformas de computador. O CSV é um implementação particular de
 * arquivos de texto separados por um delimitador, que usa a vírgula e a quebra
 * de linha para separar os valores. O formato também usa as aspas em campos no
 * qual são usados os caracteres reservados (vírgula e quebra de linha). Essa
 * robustez no formato torna o CSV mais amplo que outros formatos digitais do
 * mesmo segmento.
 * </p>
 * 
 * <p>
 * Embora não exista uma especificação formal do formato CSV, o RFC 4180
 * descreve um formato comum e estabelece text/csv como um tipo MIME registrado
 * na IANA. Formalmente, o CSV é um formato de dados delimitado que possui
 * campos (colunas) separados por caracteres de vírgula e registros (linhas)
 * separados por caracteres de quebra de linha. Campos que contêm caracteres
 * especiais (vírgula, quebra de linha ou aspas) devem ser envolvidos em aspas.
 * Entretanto, se uma linha contiver uma única entrada que seja uma cadeia
 * vazia, ela também pode ser envolvida por aspas. Se um campo contiver um
 * caractere de aspas, ele é discernido posicionando outro caractere igual logo
 * em seguida. O formato CSV não requisita uma codificação de caracteres, uma
 * ordenação de bytes ou um formato de terminador de linha.
 * </P>
 * <p>
 * Exemplo:
 * <table>
 * <tr>
 * <td>1997</td>
 * <td>Ford E350</td>
 * <td>ac, abs, moon</td>
 * <td>3000.00</td>
 * <tr>
 * <tr>
 * <td>1999</td>
 * <td>Chevy Venture "Extended Edition"</td>
 * <td></td>
 * <td>4900.00</td>
 * <tr>
 * <tr>
 * <td>1996</td>
 * <td>Jeep Grand Cherokee MUST SELL!</td>
 * <td>air, moon roof, loaded</td>
 * <td>4799.00</td>
 * <tr>
 * </table>
 * A tabela acima pode ser ser representada em CSV da seguinte maneira:<br/>
 * 
 * <pre>
 * 1997,Ford,E350,"ac, abs, moon",3000.00
 * 1999,Chevy,"Venture ""Extended Edition""",,4900.00 1996,Jeep,Grand
 * Cherokee,"MUST SELL! air, moon roof, loaded",4799.00 [editar]
 * </pre>
 * 
 * </p>
 * <p>
 * <small>fonte: <a href="http://pt.wikipedia.org/wiki/Comma-separated_values"
 * target="_blank">Wikipedia.org</a>
 * </p>
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class TabelaCSV {

	/** Tabela contendo os dados CSV. */
	private LinkedList<LinkedList<Object>> tabela;
	/** Delimitador padrão de dados. Por padrão, o delimitador é a vírgula. */
	private char delimitador;

	/**
	 * Construtor padrão.
	 */
	public TabelaCSV() {
		tabela = new LinkedList<LinkedList<Object>>();
		delimitador = ',';
	}
	
	/** Construtor parametrizado
	 * @param delimitador
	 */
	public TabelaCSV(char delimitador) {
		this();
		this.delimitador = delimitador;
	}

	/** Retorna o número de linhas da tabela CSV.
	 * @return
	 */
	public int getNumLinhas() {
		return tabela.size();
	}

	/** Retorna o número de colunas da tabela CSV.
	 * @return
	 */
	public int getNumColunas() {
		return tabela.getFirst().size();
	}

	/** Retorna um array de String com os dados de uma linha da tabela CSV.
	 * @param index
	 * @return
	 */
	public String[] getLinha(int index) {
		return tabela.get(index).toArray(new String[getNumColunas()]);
	}

	/** Adiciona uma linha nova à tabela CSV.
	 * @param linha
	 */
	public void addLinha(Object[] linha) {
		LinkedList<Object> novaLinha = new LinkedList<Object>();
		for (Object elemento : linha)
			novaLinha.add(elemento);
		tabela.add(novaLinha);
	}

	/** Adiciona uma nova linha à tabela CSV e retorna o índice da linha adicionada
	 * @return índice da linha adicionada
	 */
	public int novaLinha() {
		LinkedList<Object> novaLinha = new LinkedList<Object>();
		for (int i = 0; i < getNumColunas(); i++)
			novaLinha.add(null);
		tabela.add(novaLinha);
		return tabela.size() - 1;
	}
	
	/** Retorna todos os dados de uma determinada coluna da tabela CSV.
	 * @param index
	 * @return
	 */
	public Object[] getColuna(int index) {
		Object[] coluna = new String[getNumLinhas()];
		int i = 0;
		for (LinkedList<Object> linha : tabela) {
			coluna[i++] = linha.get(index);
		}
		return coluna;
	}

	/** Retorna o valor de uma linha/coluna.
	 * @param linha
	 * @param coluna
	 * @return
	 */
	public Object get(int linha, int coluna) {
		Object dado = null;
		try {
			dado = tabela.get(linha).get(coluna);
		} catch(IndexOutOfBoundsException e){}
		return dado;
	}

	/** Retorna o valor de uma linha/coluna, onde a coluna é indicada pelo cabeçalho.
	 * @param linha
	 * @param cabecalho
	 * @return
	 */
	public Object get(int linha, String cabecalho) {
		Object dado = null;
		try {
			int coluna = tabela.getFirst().indexOf(cabecalho);
			dado = get(linha, coluna);
		} catch (IndexOutOfBoundsException e) {}
		return dado;
	}

	/** Define o valor de uma linha/coluna
	 * @param linha
	 * @param coluna
	 * @param elemento
	 */
	public void set(int linha, int coluna, Object elemento) {
		LinkedList<Object> l = tabela.get(linha);
		if (l == null) {
			l = new LinkedList<Object>();
			tabela.set(linha, l);
		} 
		l.set(coluna, elemento);
	}
	
	/** Define o valor de uma linha/coluna, onde a coluna é localizada pelo título do cabeçalho.
	 * Caso não exista a coluna, ela será incluída.
	 * @param linha
	 * @param coluna
	 * @param elemento
	 */
	public void set(int linha, String coluna, Object elemento) {
		LinkedList<Object> l = tabela.get(linha);
		if (l == null) {
			l = new LinkedList<Object>();
			tabela.set(linha, l);
		}
		int indexColuna = tabela.get(0).indexOf(coluna);
		// não encontrou
		if (indexColuna < 0)
			indexColuna = novaColuna(coluna);
		set(linha, indexColuna, elemento);
	}
	
	/** Adiciona uma nova coluna à tabela, retornando o índice da nova coluna.
	 * @param cabecalho
	 * @return
	 */
	public int novaColuna(String cabecalho) {
		tabela.get(0).add(cabecalho);
		for (int i = 1; i < getNumLinhas(); i++) {
			tabela.get(i).add(null);
		}
		return tabela.get(0).size() - 1;
	}

	/** Retorna o cabeçalho da tabela CSV
	 * @return
	 */
	public Object[] getCabecalho() {
		return getLinha(0);
	}

	/** Retorna a tabela de dados.
	 * @return
	 */
	public LinkedList<LinkedList<Object>> getTabela() {
		return tabela;
	}

	/** Define a tabela de dados.
	 * @param tabela
	 */
	public void setTabela(LinkedList<LinkedList<Object>> tabela) {
		this.tabela = tabela;
	}

	/** Indica se a tabela CSV está vazia.
	 * @return
	 */
	public boolean isEmpty() {
		return tabela.isEmpty();
	}
	
	/** Retorna o delimitador utilizado na tabela CSV.
	 * @return
	 */
	public char getDelimitador() {
		return delimitador;
	}
	
	/** Define o delimitador utilizado na tabela CSV.
	 * @param delimitador
	 */
	public void setDelimitador(char delimitador) {
		this.delimitador = delimitador;
	}
	
	/** Retorna uma representação String desta tabela CSV. Caso não tenha sido definido o delimitador, será utilizado como padrão a vírgula.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (LinkedList<Object> linha : tabela) {
			for (Object obj : linha) {
				str.append(obj).append(delimitador);
			}
			if (str.lastIndexOf(String.valueOf(delimitador)) > 0)
				str.delete(str.lastIndexOf(String.valueOf(delimitador)), str.length());
			str.append("\n");
		}
		str.delete(str.lastIndexOf("\n"), str.length());
		return str.toString(); 
	}
	
	/** Retorna uma representação String desta tabela CSV. Caso não tenha sido definido o delimitador, será utilizado como padrão a vírgula.
	 * @see java.lang.Object#toString()
	 */
	public String toStringFormatado(boolean stringComAspas, boolean removeQuebraLinha) {
		StringBuilder str = new StringBuilder();
		for (LinkedList<Object> linha : tabela) {
			for (Object obj : linha) {
				if (stringComAspas && (obj instanceof String)) obj = "\"" + obj + "\"";
				if (removeQuebraLinha && (obj instanceof String)) obj = StringUtils.removeLineBreak((String) obj);
				str.append(obj).append(delimitador);
			}
			if (str.lastIndexOf(String.valueOf(delimitador)) > 0)
				str.delete(str.lastIndexOf(String.valueOf(delimitador)), str.length());
			str.append("\n");
		}
		str.delete(str.lastIndexOf("\n"), str.length());
		return str.toString();
	}

}
