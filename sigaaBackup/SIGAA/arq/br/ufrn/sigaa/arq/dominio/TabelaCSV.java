/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Comma-separated values (CSV), em portugu�s Valores Separados por V�rgula, �
 * um formato de arquivo que armazena dados tabelados, cujo grande uso data da
 * �poca dos mainframes. Por serem bastante simples, arquivos .csv s�o comuns em
 * todas as plataformas de computador. O CSV � um implementa��o particular de
 * arquivos de texto separados por um delimitador, que usa a v�rgula e a quebra
 * de linha para separar os valores. O formato tamb�m usa as aspas em campos no
 * qual s�o usados os caracteres reservados (v�rgula e quebra de linha). Essa
 * robustez no formato torna o CSV mais amplo que outros formatos digitais do
 * mesmo segmento.
 * </p>
 * 
 * <p>
 * Embora n�o exista uma especifica��o formal do formato CSV, o RFC 4180
 * descreve um formato comum e estabelece text/csv como um tipo MIME registrado
 * na IANA. Formalmente, o CSV � um formato de dados delimitado que possui
 * campos (colunas) separados por caracteres de v�rgula e registros (linhas)
 * separados por caracteres de quebra de linha. Campos que cont�m caracteres
 * especiais (v�rgula, quebra de linha ou aspas) devem ser envolvidos em aspas.
 * Entretanto, se uma linha contiver uma �nica entrada que seja uma cadeia
 * vazia, ela tamb�m pode ser envolvida por aspas. Se um campo contiver um
 * caractere de aspas, ele � discernido posicionando outro caractere igual logo
 * em seguida. O formato CSV n�o requisita uma codifica��o de caracteres, uma
 * ordena��o de bytes ou um formato de terminador de linha.
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
 * @author �dipo Elder F. de Melo
 * 
 */
public class TabelaCSV {

	/** Tabela contendo os dados CSV. */
	private LinkedList<LinkedList<Object>> tabela;
	/** Delimitador padr�o de dados. Por padr�o, o delimitador � a v�rgula. */
	private char delimitador;

	/**
	 * Construtor padr�o.
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

	/** Retorna o n�mero de linhas da tabela CSV.
	 * @return
	 */
	public int getNumLinhas() {
		return tabela.size();
	}

	/** Retorna o n�mero de colunas da tabela CSV.
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

	/** Adiciona uma linha nova � tabela CSV.
	 * @param linha
	 */
	public void addLinha(Object[] linha) {
		LinkedList<Object> novaLinha = new LinkedList<Object>();
		for (Object elemento : linha)
			novaLinha.add(elemento);
		tabela.add(novaLinha);
	}

	/** Adiciona uma nova linha � tabela CSV e retorna o �ndice da linha adicionada
	 * @return �ndice da linha adicionada
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

	/** Retorna o valor de uma linha/coluna, onde a coluna � indicada pelo cabe�alho.
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
	
	/** Define o valor de uma linha/coluna, onde a coluna � localizada pelo t�tulo do cabe�alho.
	 * Caso n�o exista a coluna, ela ser� inclu�da.
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
		// n�o encontrou
		if (indexColuna < 0)
			indexColuna = novaColuna(coluna);
		set(linha, indexColuna, elemento);
	}
	
	/** Adiciona uma nova coluna � tabela, retornando o �ndice da nova coluna.
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

	/** Retorna o cabe�alho da tabela CSV
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

	/** Indica se a tabela CSV est� vazia.
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
	
	/** Retorna uma representa��o String desta tabela CSV. Caso n�o tenha sido definido o delimitador, ser� utilizado como padr�o a v�rgula.
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
	
	/** Retorna uma representa��o String desta tabela CSV. Caso n�o tenha sido definido o delimitador, ser� utilizado como padr�o a v�rgula.
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
