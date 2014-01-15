/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 01/08/2012
 *
 */
package br.ufrn.arq.util;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import br.ufrn.sigaa.arq.dominio.TabelaCSV;

/**
 * Esta classe provê métodos para manipular importar e exportar dados CSV.
 * 
 *  @see TabelaCSV
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class CSVUtils {
	
	/** Separador de coluna padrão. */
	public static final String SEPARADOR_DEFAULT = ",";

	/** Lê um inputStream e retorna uma tabela CSV. Pode ser utilizado para importar arquivos .CSV
	 * @param inputStream
	 *            <b>Obrigatório.</b> InputStream que será lido e processado
	 * @param possuiCabecalho
	 *            Indica se a primeira linha contem um cabeçalho. Caso possua,
	 *            este será utilizado como chave no mapeamento da coluna. Caso
	 *            contrário, a chave será: CAMPO<b>N</b>, onde <b>N</b> é o índice da
	 *            coluna iniciando em 1. Ex.: CAMPO1, CAMPO2, ..., CAMPO10,
	 *            CAMPO11, ..., CAMPON.
	 * @param codificacao
	 *            Codificação do arquivo. As codificações aceitas são as listadas no <i>IANA Charset Registry</i>. Exemplo: UTF-8, ISO-8859-1, etc.
	 * @param separador
	 *            Caso não informado, adotar-se-á a vírgula como padrão.
	 * @return
	 * @throws IOException
	 * 
	 * @see http://www.iana.org/assignments/character-sets
	 */
	public static TabelaCSV readInputStream(InputStream inputStream, String codificacao, String separador) throws IOException {
		int linhaCounter = 0;
		TabelaCSV tabelaCSV = new TabelaCSV();
		try{
			if (separador == null) separador = SEPARADOR_DEFAULT;
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String linhaOrigem;
			linhaCounter++;
			while ((linhaOrigem = in.readLine()) != null) {
				linhaOrigem = new String(linhaOrigem.getBytes(), codificacao);
				if (linhaOrigem.length() > 0) {
					tabelaCSV.addLinha(splitCSV(linhaOrigem, separador));
				}
				linhaCounter ++;
			}
		} catch (IOException e) {
			throw new IOException(
					"Não foi possível ler os dados. Por favor, verifique se o formato CSV está correto (linha "
							+ linhaCounter + ").", e);
		}
		return tabelaCSV;
	}
	
	/** Divide uma String CSV em um array de String.
	 * @param linha linha no formato CSV
	 * @param separador separador utilizado entre os campos. Caso não informado, será adotado como padrão a vírgula.
	 * @return
	 */
	public static String[] splitCSV(String linha, String separador) {
		LinkedList<String> dados = new LinkedList<String>();
		boolean abreAspas = false;
		StringBuilder dado = new StringBuilder();
		if (separador == null) separador = SEPARADOR_DEFAULT;
		for (char c : linha.toCharArray()){
			if (c == '\"') {
				abreAspas = !abreAspas;
				if (dado.length() > 0)
					dado.append(c);
			} else if (c == separador.charAt(0) && !abreAspas){
				if (dado.length() > 0 && dado.charAt(dado.length() - 1) == '\"')
					dados.add(dado.substring(0, dado.length() - 1));
				else
					dados.add(dado.toString());
				dado = new StringBuilder();
			} else {
				dado.append(c);
			}
		}
		if (dado.length() > 0 && dado.charAt(dado.length() - 1) == '\"')
			dados.add(dado.substring(0, dado.length() - 1));
		else
			dados.add(dado.toString());
		return dados.toArray(new String[dados.size()]);
	}
	
	/** Converter uma Coleção de Array de valores (Collection<Object[]>) para uma tabela CSV. Geralmente é utilizado com resultados de consultas HQL.
	 * @param dados
	 * @return
	 */
	public static TabelaCSV collectionArrayToCSV(Collection<Object[]> dados) {
		return collectionArrayToCSV(dados, false, false);
	}
	
	/** Converter uma Coleção de Array de valores (Collection<Object[]>) para uma tabela CSV. Geralmente é utilizado com resultados de consultas HQL.
	 * @param dados
	 * @param stringComAspas 
	 * @param removeQuebraLinha 
	 * @return
	 */
	public static TabelaCSV collectionArrayToCSV(Collection<Object[]> dados, boolean stringComAspas, boolean removeQuebraLinha) {
		if (isEmpty(dados)) return null;
		LinkedList<LinkedList<Object>> tabela = new LinkedList<LinkedList<Object>>();
		for (Object[] linha : dados) {
			LinkedList<Object> novaLinha = new LinkedList<Object>();
			for (Object elemento : linha) {
				if (stringComAspas && (elemento instanceof String)) elemento = "\"" + elemento + "\"";
				if (removeQuebraLinha && (elemento instanceof String)) elemento = StringUtils.removeLineBreak((String) elemento);
				novaLinha.add(elemento);
			}
			tabela.add(novaLinha);
		}
		TabelaCSV tabelaCSV = new TabelaCSV();
		tabelaCSV.setTabela(tabela);
		return tabelaCSV;
	}
	
	/** Converter uma Coleção de Mapa de valores (Collection<Map<String, Object>>) para uma tabela CSV.
	 * @param collectionMap
	 * @return
	 */
	public static TabelaCSV collectionMapToCSV(Collection<Map<String, Object>> collectionMap) {
		return collectionMapToCSV(collectionMap, false, false);
	}
	
	/** Converter uma Coleção de Mapa de valores (Collection<Map<String, Object>>) para uma tabela CSV.
	 * @param collectionMap
	 * @param stringComAspas
	 * @param removeQuebraLinha
	 * @return
	 */
	public static TabelaCSV collectionMapToCSV(Collection<Map<String, Object>> collectionMap, boolean stringComAspas, boolean removeQuebraLinha) {
		if (isEmpty(collectionMap)) return null;
		LinkedList<LinkedList<Object>> tabela = new LinkedList<LinkedList<Object>>();
		// títulos dos campos
		LinkedList<Object> cabecalho = new LinkedList<Object>();
		for (Map<String, Object> linha : collectionMap) {
			for (String key : linha.keySet()) {
				if (!cabecalho.contains(key)) {
					cabecalho.add(key);
				}
			}
		}
		// 1º linha: cabeçalho
		tabela.add(cabecalho);
		// demais linhas
		for (Map<String, Object> linha : collectionMap) {
			LinkedList<Object> novaLinha = new LinkedList<Object>();
			for (Object key : tabela.getFirst()) {
				Object elemento = linha.get(key);
				if (stringComAspas && (elemento instanceof String)) elemento = "\"" + elemento + "\"";
				if (removeQuebraLinha && (elemento instanceof String)) elemento = StringUtils.removeLineBreak((String) elemento);
				novaLinha.add(elemento);
			}
			tabela.add(novaLinha);
		}
		TabelaCSV tabelaCSV = new TabelaCSV();
		tabelaCSV.setTabela(tabela);
		return tabelaCSV;
	}
	
	/** Converte um {@link ResultSet} em uma tabela CSV.
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static TabelaCSV resultSetToCSV(ResultSet rs) throws SQLException {
		return resultSetToCSV(rs, false, false);
	}
	/** Converte um {@link ResultSet} em uma tabela CSV. 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static TabelaCSV resultSetToCSV(ResultSet rs, boolean stringComAspas, boolean removeQuebraLinha)  throws SQLException {
		LinkedList<LinkedList<Object>> tabela = new LinkedList<LinkedList<Object>>();
		LinkedList<Object> cabecalho = new LinkedList<Object>();
		for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			cabecalho.add(rs.getMetaData().getColumnName(i));
		}
		tabela.add(cabecalho);
		while (rs.next()) {
			LinkedList<Object> novaLinha = new LinkedList<Object>();
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				Object elemento = rs.getObject(i);
				if (rs.getMetaData().getColumnType(i) == Types.DATE)
					elemento = Formatador.getInstance().formatarData((Date) elemento);
				else if (elemento instanceof String) {
					if (stringComAspas) elemento = "\"" + elemento + "\"";
					if (removeQuebraLinha) elemento = StringUtils.removeLineBreak((String) elemento);
				}
				novaLinha.add(elemento);
			}
			tabela.add(novaLinha);
		}
		TabelaCSV tabelaCSV = new TabelaCSV();
		tabelaCSV.setTabela(tabela);
		return tabelaCSV;
	}
}