/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 22/04/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufrn.arq.util.StringUtils;


/**
 *
 * <p> Classe que armazena temporariamente os dados retornados pelo relatório de inventário no acervo.</p>
 *
 * <p> <i> (Serve para organizar melhor os dados antes de mostrar para o usuário ) </i> </p>
 * 
 * @author jadson
 *
 */
public class DadosRelatorioListagemMaterial implements Comparable<DadosRelatorioListagemMaterial>{

	
	/**
	 * <p> Os padrões para ordenação CDU, já compilados para melhor performance. Elementos
	 * com maior importância vêm antes.
	 * <p> Obs.: Matchers não são usados diretamente por que não são thread-safe. */
	private static final List<Pattern> PADROES_DE_ORDENACAO_CDU = new ArrayList<Pattern>();
	static {
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("\\+")); // adição
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("/")); // extensão consecutiva
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("\\z")); // vazio (número simples)
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile(":(?=[^:])")); // relação simples (usando look-ahead)
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("::")); // ordenação
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("=")); // Língua
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("\\(0[^)]*\\)")); // Forma
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("\\([1-9]+[^\\)]\\)")); // Lugar
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("\\(=[^)]*\\)")); // Raça
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("\\\"[^\"]*\\\"")); // Tempo
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("\\*")); // asterisco
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("\\p{Alnum}+")); // subdivisão alfabética
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("\\.00(0?)")); // ponto de vista
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("-0[0-9.]*")); // auxiliar propriedade(-02)/materiais(-03)/pessoas(-05)
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("-[1-9][0-9.]*")); // analítica de traço
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("\\.0[1-9]*")); // analítica
		PADROES_DE_ORDENACAO_CDU.add(Pattern.compile("'")); // apóstrofo
	}
	
	/** Elementos de ordenação CDU. Esses são apenas os auxiliares independentes. */
	private static final List<Pattern> ORDENACAO_CDU_AUX_INDEP = new ArrayList<Pattern>();
	static {
		ORDENACAO_CDU_AUX_INDEP.add(Pattern.compile("=")); // Língua
		ORDENACAO_CDU_AUX_INDEP.add(Pattern.compile("\\(0[^)]*\\)")); // Forma
		ORDENACAO_CDU_AUX_INDEP.add(Pattern.compile("\\([1-9][^)]*\\)")); // Lugar
		ORDENACAO_CDU_AUX_INDEP.add(Pattern.compile("\\(=[^)]*\\)")); // Raça
		ORDENACAO_CDU_AUX_INDEP.add(Pattern.compile("\\\"[^\"]*\\\"")); // Tempo
	}
	
	/** Código de barras do material. */
	private String codigoBarras;
	
	/** Número do sistema do título. */
	private Integer numerodoSistema;
	
	/** Número do patrimônio do material. */
	private Long numeroPatrimonio;
	
	/** Nome do título. */
	private String titulo;
	
	/** Autor da obra. */
	private String autor;
	
	/** Edição da obra. */
	private String edicao;
	
	/** Ano de publicação da obra. */
	private String ano;
	
	/** Número de chamada do material. */
	private String chamada;

	/** Indica se o relatório será ordenado pelo código de barras do material. */
	private boolean ordenarPorCodigoBarras;
	
	/** Indica se o relatório será ordenado pelo título do material. */
	private boolean ordenarPorTitulo;
	
	/** Indica se o relatório será ordenado pela localização do material. */
	private boolean ordenarPorLocalizacao;
	
	/**
	 * Construtor para exemplares
	 */
	public DadosRelatorioListagemMaterial(Integer numerodoSistema, String codigoBarras, BigInteger numeroPatrimonio,
			String titulo, String autor, String edicao, String ano, String chamada,
			boolean ordenarPorCodigoBarras, boolean ordenarPorTitulo, boolean ordenarPorLocalizacao) {
		this.codigoBarras = codigoBarras;
		this.numerodoSistema = numerodoSistema;
		
		if(numeroPatrimonio != null ) this.numeroPatrimonio =  numeroPatrimonio.longValue();
		
		this.titulo = titulo;
		this.autor = autor;
		this.edicao = edicao;
		this.ano = ano;
		this.chamada = chamada;
		
		this.ordenarPorCodigoBarras = ordenarPorCodigoBarras;
		this.ordenarPorTitulo = ordenarPorTitulo;
		this.ordenarPorLocalizacao = ordenarPorLocalizacao;
	}
	
	/**
	 * Construtor para fascículos
	 */
	public DadosRelatorioListagemMaterial(Integer numerodoSistema,  String codigoBarras,
			String titulo, String autor, String edicao, String ano, String chamada,
			boolean ordenarPorCodigoBarras, boolean ordenarPorTitulo, boolean ordenarPorLocalizacao) {
		this.codigoBarras = codigoBarras;
		this.numerodoSistema = numerodoSistema;
		this.titulo = titulo;
		this.autor = autor;
		this.edicao = edicao;
		this.ano = ano;
		this.chamada = chamada;
		
		this.ordenarPorCodigoBarras = ordenarPorCodigoBarras;
		this.ordenarPorTitulo = ordenarPorTitulo;
		this.ordenarPorLocalizacao = ordenarPorLocalizacao;
		
	}
	
	/**
	 * <p>Contém a lógica de ordenação do relatório.</p>
	 * 
	 * <p>
	 * 		A ordenação pode ser por código de barras, Título ou Localização.<br/>
	 *      No caso de Título, como podem haver resultados iguais, ordena também pelo código de barras<br/>
	 *      No caso de Localização, como podem haver resultados iguais, ordena também pelo número do sistema.<br/>
	 * </p>
	 */
	@Override
	public int compareTo(DadosRelatorioListagemMaterial other) {
		
		if(ordenarPorCodigoBarras){
			
			if(this.getCodigoBarras() == null )
				return -1;
			
			if(other.getCodigoBarras() == null )
				return 1;
			
			return this.codigoBarras.compareTo(other.codigoBarras); // Código de barras sempre dá resultado diferentes, porque não se repete.
			
		}else{
			if(ordenarPorTitulo){
				
				if(this.getTitulo() == null )
					return -1;
				
				if(other.getTitulo() == null )
					return 1;
				
				int resultadoComparacao = this.getTitulo().compareTo(other.getTitulo());
				
				// Como os Títulos podem ser iguais, caso sejam, compara pelo código de barras
				if(resultadoComparacao == 0)
					return this.codigoBarras.compareTo(other.codigoBarras);
				else{
					return resultadoComparacao;
				}
				
			} else{
				if (ordenarPorLocalizacao){
					int resultadoComparacao = compararNumeroChamada( this.getChamada(), other.getChamada() );
					
					// Como os número de chamada podem ser iguais, caso sejam, compara pelo número do sistema
					if(resultadoComparacao == 0)
						return this.numerodoSistema.compareTo(other.numerodoSistema);
					else{
						return resultadoComparacao;
					}
				}else
					return this.numerodoSistema.compareTo(other.numerodoSistema); // Nunca era para chegar aqui, sempre deve cair num dos "if" anteriores
				
			}
		}
	}
	
	
	
	//////////////////////// Essa parte aqui é só para comparar números de chamadas para o relatório da listage geral //////////////
	
	
	/**
	 * <p>Compara dois número de chamadas, de acordo com a ordem de arquivamento.</p>
	 * 
	 * <p>Não é uma comparação textual simples, existem algumas regras, como por exemplo. 
	 * Se começar por "FE" (Fora de Empréstimo), desconsidera esse texto na ordenação.</p>
	 * 
	 */
	private static int compararNumeroChamada( String numeroChamada1, String numeroChamada2) {
		if ( numeroChamada1 == null || numeroChamada1.length() == 0 )
			return 1;
		else if ( numeroChamada2 == null || numeroChamada2.length() == 0 )
			return -1;
		
		// Retira o prefixo "FE" (fora de empréstimo)
		if ( numeroChamada1.startsWith("FE") )
			numeroChamada1 = numeroChamada1.substring(2);
		if ( numeroChamada2.startsWith("FE") )
			numeroChamada2 = numeroChamada2.substring(2);
		
		// Primeiro são testados os casos nos quais o CDU é formado
		// somente por um auxiliar independente
		for ( Pattern p : ORDENACAO_CDU_AUX_INDEP ) {
			Matcher m1 = p.matcher( numeroChamada1 );
			Matcher m2 = p.matcher( numeroChamada2 );
			boolean ok1 = m1.lookingAt();
			boolean ok2 = m2.lookingAt();
			if ( ok1 && ! ok2 )
				return -1;
			else if ( ! ok1 && ok2 )
				return 1;
			else if ( ok1 && ok2 ) {
				String a = numeroChamada1.substring(0, m1.end() );
				String b = numeroChamada2.substring(0, m2.end() );
				if ( a.equals(b) )
					continue;
				else
					return a.compareTo(b);
			} else
				continue;
		}
		
		// Se nenhum dos dois é formado por um auxiliar independente,
		// a comparação normal é feita
		return comparaNumeroChamadaRecursivo(numeroChamada1, numeroChamada2);
	}
	
	
	
	/**
	 * <p> Compara duas classes CDUs. Somente classes que não sejam iniciadas
	 * por auxiliares independentes podem ser comparadas por esse método.
	 * <p> A comparação é feita, recursivamente, a cada parte separada por um
	 * dos separadores definidos em {@link PADROES_DE_ORDENACAO_CDU}.
	 * <p>É utilizado por {@link compararCDU}.
	 */
	private static int comparaNumeroChamadaRecursivo(String cdu1, String cdu2) {
		
		// retira espaços em branco
		cdu1 = cdu1.replaceAll("\\p{Space}", "");
		cdu2 = cdu2.replaceAll("\\p{Space}", "");
		
		// Primeiramente, é comparado o número simples
		// Se eles já forem diferentes, já está decidido
		
		String n1 = getProximoNumeroSimples(cdu1);
		String n2 = getProximoNumeroSimples(cdu2);
		
		int cmp = n1.compareTo(n2);
		if ( cmp != 0 )
			return cmp;
		
		// Se forem iguais, o sufixo decidirá...
		
		String s1 = getSufixoNumeroChamada(cdu1);
		String s2 = getSufixoNumeroChamada(cdu2);
		
		// Se não há sufixo, eles são iguais, exceto se um for rodeado por colchetes
		if ( s1.length() == 0 && s2.length() == 0 ) {
			boolean colchete1 =  cdu1.charAt(0) == '[';
			boolean colchete2 =  cdu2.charAt(0) == '[';
			if ( colchete1 && colchete2 )
				return 0;
			else if ( ! colchete1 && colchete2 )
				return -1;
			else if ( colchete1 && ! colchete2 )
				return 1;
			else
				return 0;
		}
		
		// se há, eles são comparados
		for ( Pattern p : PADROES_DE_ORDENACAO_CDU ) {
			Matcher m1 = p.matcher( s1 );
			Matcher m2 = p.matcher( s2 );
			boolean ok1 = m1.lookingAt();
			boolean ok2 = m2.lookingAt();
			
			if ( ok1 && ! ok2 )
				return -1;
			else if ( ! ok1 && ok2 )
				return 1;
			else if ( ok1 && ok2 ) {
				
				// Compara os dois auxiliares, já que alguns
				// têm conteúdo ordenável
				String x1 = s1.substring(0, m1.end());
				String x2 = s2.substring(0, m2.end());
				int comp = x1.compareTo(x2);
				if ( comp != 0 )
					return comp;
				
				String p1 = s1.substring(m1.end());
				String p2 = s2.substring(m2.end());
				
				// evita loop infinito no caso de não haver mais nada depois
				if ( p1.equals(s1) && p2.equals(s2) )
					break;
				if ( p1.isEmpty() )
					return -1;
				else if ( p2.isEmpty() )
					return 1;
				
				return comparaNumeroChamadaRecursivo( p1, p2 );
			} else
				continue;
		}
		
		// Chega aqui somente se os dois CDUs são inválidos.
		// Utiliza a ordem lexicográfica normal manter uma ordem
		// fixa e garantir a propriedade de ordem total
		return cdu1.compareTo(cdu2);
					
	}
	
	/**
	 * <p>Retorna a parte de uma classe CDU que vem depois do
	 * número simples.
	 * <p>É utilizado por {@link comparaNumeroChamadaRecursivo}.
	 */
	private static String getSufixoNumeroChamada( String cdu ) {
		
		String n = getProximoNumeroSimples(cdu);
		
		// consome todos os dígitos de n:
		while ( n.length() > 0 ) {
			char c = n.charAt(0);
			char d = cdu.charAt(0);
			if ( d == c ) {
				n = n.substring(1);
				cdu = cdu.substring(1);
			} else if ( d == '.' || d == '[' || d == ']' ) {
				cdu = cdu.substring(1);
			}
		}
		
		// consome o que restar de colchetes
		while ( cdu.length() > 0 &&  cdu.charAt(0) == ']' )
			cdu = cdu.substring(1);
		
		return cdu;
	}
	
	
	/**
	 * <p> Dada uma classe CDU, retorna o próximo número simples (formado
	 * por dígitos e pontos, e opcionalmente colchetes). Retorna somente os
	 * dígitos: não retorna os pontos e colchetes.
	 * <p>É utilizado por {@link getSufixoNumeroChamada}.
	 */
	private static String getProximoNumeroSimples( String cdu ) {
		String r = "";
		char cAnt = '?';
		char c = '?';
		
		for ( int i = 0; i < cdu.length() ; i++ ) {
			cAnt = c;
			c = cdu.charAt(i);
			
			if ( '1' <= c && c <= '9' ) {
				r += c;
				continue;
			} else if ( c == '[' || c == '.' )
				continue;
			else if ( c == '0' ) {
				if ( i > 0 && cAnt == '.' ) break;
				else {
					r += c;
					continue;
				}
			}
			break;
		}
		return r;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	// sets e gets //
	
	public String getCodigoBarras() {
		return codigoBarras;
	}

	public String getNumerodoSistema() {
		return String.valueOf(numerodoSistema);
	}

	/** Retorna o número do patrimônio do material. */
	public String getNumeroPatrimonio() {
		
		String np = String.valueOf(numeroPatrimonio);
		
		if( StringUtils.isEmpty(np) || np.equals("null"))
			return null;
		
		return np;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getAutor() {
		return autor;
	}

	public String getEdicao() {
		return edicao;
	}

	/**
	 *   Podem existir vários anos separados por "#$&" no cache, já que o campo marc pode ser repetido.
	 */
	public String getAno() {
	
		StringBuilder anoRetorno = new StringBuilder();
		
		List<String> nomesList = new ArrayList<String>();
		
		if(ano != null){
			StringTokenizer tokens = new StringTokenizer(ano, "#$&");
			
			
			while(tokens.hasMoreTokens()){
				
				String ano = tokens.nextToken();
				if(StringUtils.notEmpty(ano) && ! ano.equalsIgnoreCase("null"))
					nomesList.add(ano);
			}
		}
		
		for (String string : nomesList) {
			anoRetorno.append(string+" ");
		}
		
		return anoRetorno.toString();
	}

	public String getChamada() {
		return chamada;
	}

}
