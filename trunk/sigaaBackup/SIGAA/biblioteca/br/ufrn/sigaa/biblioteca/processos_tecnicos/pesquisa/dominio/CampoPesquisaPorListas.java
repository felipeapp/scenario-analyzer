/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 *
 * <p> Classe que representa um campo da busca por listas </p>
 *
 * <p> <i> ( Auxilia na geração das consultas deixando os SQLs mais centralizados facilitando a manutenção ) </i> </p>
 * 
 * @author Jadson
 *
 */
public class CampoPesquisaPorListas {
	
	//   Constantes com os possíveis valores que o usuário pode escolher
	
	/** Nome do título. */
	public static final int TITULO = 1;
	/** Autor do título. */
	public static final int AUTOR = 2;
	/** Assunto do título. */
	public static final int ASSUNTO = 3;
	/** Série do título. */
	public static final int SERIE = 4;
	/** Local de publicação. */
	public static final int LOCAL = 5;
	/** Editora do título. */
	public static final int EDITORA = 6;
	/** Classificação 1 configurada no sistema. */
	public static final int CLASSIFICACAO_1 = 7;
	/** Classificação 2 configurada no sistema. */
	public static final int CLASSIFICACAO_2 = 8;
	/** Classificação 3 configurada no sistema. */
	public static final int CLASSIFICACAO_3 = 9;
	/** Número de chamada do título. */
	public static final int NUMERO_CHAMADA = 10;
	/** ISBN do título (para exemplares). */
	public static final int ISBN = 11;
	/** ISSN do título (para periódicos). */
	public static final int ISSN = 12;
	
	/** String amigável do campo de nome do título. */
	public static final String DESCRICAO_TITULO = "Título";
	/** String amigável do campo de autor. */
	public static final String DESCRICAO_AUTOR = "Autor";
	/** String amigável do campo de assunto. */
	public static final String DESCRICAO_ASSUNTO = "Assunto";
	/** String amigável do campo de série. */
	public static final String DESCRICAO_SERIE = "Série";
	/** String amigável do campo de local. */
	public static final String DESCRICAO_LOCAL = "Local";
	/** String amigável do campo de editora. */
	public static final String DESCRICAO_EDITORA = "Editora";
	/** String amigável do campo de nº de chamada. */
	public static final String DESCRICAO_NUMERO_CHAMADA = "Nº Chamada";
	/** String amigável do campo de ISBN. */
	public static final String DESCRICAO_ISBN = "ISBN";
	/** String amigável do campo de ISSN. */
	public static final String DESCRICAO_ISSN = "ISSN";
	
	/**   Guarda o valor digitado pelo usuário   **/
	private String valorCampo;
	
	/**   Guarda o valor do campo escolhido pelo usuário   **/
	private int tipoCampoEscolhido = -1;
	
	/** Se o campo foi selecionado pelo usuário */
	private boolean buscarCampos = false;
	
	
	/**
	 * Quando a quantidade total de campos, importante para saber na busca por todos os campos
	 * a quantidade de parâmetros que deve ser acrescentada.
	 */
	private int quantidadeTotalCampos;
	
	/**
	 *   Apaga as informações do campo.
	 */
	public void reset(){
		valorCampo = null;
		tipoCampoEscolhido = -1;
		buscarCampos = false;
	}

	/**
	 * Determina se o usuário informou algum dado para a pesquisa
	 */
	public boolean isVazio(){
		return ( selecionouCampo() && (  StringUtils.isEmpty(valorCampo) 
				|| ( tipoCampoEscolhido != CLASSIFICACAO_1 
						&& tipoCampoEscolhido != CLASSIFICACAO_2
						&& tipoCampoEscolhido != CLASSIFICACAO_3 && valorCampo.length() <= 2) ) );
	}
	
	/**
	 * Determina se o campo foi selecionado na página
	 */
	public boolean selecionouCampo(){
		return buscarCampos;
	}
	
	
	/**
	 * Determina se o usuário escolheu o tipo do campo.
	 */
	public boolean  selecionouTipoCampoEscolhido(){
		return  ! (tipoCampoEscolhido == -1);
	}
	
	
	/**
	 *   Método que gera a consulta para o campo de acordo com tipo dele.
	 *
	 * @param geradorPesquisa
	 * @param nomesAutorizados caso a busca seja de autor ou assunto tem que gerar para os nomes autorizados da base de autoridades também
	 */
	public String gerarConsulta(GeraPesquisaTextual geradorPesquisa, String... nomesAutorizados){
		
		StringBuilder sqlSelectTemp = new StringBuilder();

		if( isVazio() )
			sqlSelectTemp.append(" 1 = 0 "); // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
		else{
			
			this.quantidadeTotalCampos = 1; // por padrão apenas uma coluna do cache é buscado por vez
			
			// SEMPRE QUE ADICIONAR UM NOVO TIPO DE CAMPO PARA O USUÁRIO PESQUISAR, TEM QUE ADICIONAR AQUI.
			switch(getTipoCampoEscolhido()){
			
				case TITULO:
					sqlSelectTemp.append( " AND (  "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.outras_informacoes_titulo_ascii") +" ) " );
					break;
				case AUTOR:
					if(nomesAutorizados.length == 0){
						sqlSelectTemp.append("AND ( "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autor_ascii") );
						sqlSelectTemp.append(" OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autores_secundarios_ascii") +" ) " );
					}else{
						
						sqlSelectTemp.append(" AND ( ");
						
						sqlSelectTemp.append(geradorPesquisa.gerarMecanismoPesquisaTextual("t.autor_ascii") );
						sqlSelectTemp.append(" OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autores_secundarios_ascii"));
						
						for (int i = 0; i < nomesAutorizados.length; i++) { // do 2º para frente, vem as entradas autorizadas
			
							sqlSelectTemp.append( " OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autor_ascii") );
						}
						
						for (int i = 0; i < nomesAutorizados.length; i++) { // do 2º para frente, vem as entradas autorizadas
							
							sqlSelectTemp.append( " OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autores_secundarios_ascii") );
						}
						
						sqlSelectTemp.append(" ) ");
					}
					
					this.quantidadeTotalCampos = 2; // os parÂmetros da busca remissiva não são levados em consideração aqui
			
					break;
				
				case ASSUNTO:
						
						if(nomesAutorizados.length == 0)
							sqlSelectTemp.append( "AND ( "+  geradorPesquisa.gerarMecanismoPesquisaTextual("t.assunto_ascii") +" ) ");
						else{   // existem nomes autorizados

							sqlSelectTemp.append(" AND ( ");
							
							sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.assunto_ascii") ); // o assunto é sempre o primeiro
							
							for (int i = 0; i < nomesAutorizados.length; i++) { // do 2º para frente, vem as entradas autorizadas
				
								sqlSelectTemp.append( " OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.assunto_ascii") );
							}
							
							sqlSelectTemp.append(" ) ");
							
						}
						
						this.quantidadeTotalCampos = 1; // os parÂmetros da busca remissiva não são levados em consideração aqui
						
						break;
					case SERIE:
						sqlSelectTemp.append( " AND (  "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.serie_ascii")  +" ) ");
						break;
					case LOCAL:
						sqlSelectTemp.append( " AND (  "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.local_publicacao_ascii") +" ) ");
						break;
					case EDITORA:
						sqlSelectTemp.append( " AND (  "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.editora_ascii")+" ) " );
						break;
					case CLASSIFICACAO_1:
						sqlSelectTemp.append( " AND (  "+geradorPesquisa.gerarMecanismoPesquisaInicioPalavra("t.classificacao_1_ascii") +" ) ");
						break;
					case CLASSIFICACAO_2:
						sqlSelectTemp.append( " AND (  "+geradorPesquisa.gerarMecanismoPesquisaInicioPalavra("t.classificacao_2_ascii") +" ) ");
						break;
					case CLASSIFICACAO_3:
						sqlSelectTemp.append( " AND (  "+geradorPesquisa.gerarMecanismoPesquisaInicioPalavra("t.classificacao_3_ascii") +" ) ");
						break;
					case NUMERO_CHAMADA:
						sqlSelectTemp.append( " AND (  "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.numero_chamada") +" ) ");
						break;
					case ISBN:
						sqlSelectTemp.append(" AND (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.isbn") +" ) ");
						break;
					case ISSN:
						sqlSelectTemp.append( " AND (  "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.issn") +" ) ");
				}
			}
		
		
		return sqlSelectTemp.toString();
	}
	
	
	/**
	 *   Retorna a descrição do campo escolhido
	 * @throws DAOException 
	 */
	public String getDescricaoTipoCampoEscolhido() throws DAOException{
		switch(tipoCampoEscolhido){
		case CampoPesquisaPorListas.TITULO:
			return DESCRICAO_TITULO;
		case CampoPesquisaPorListas.AUTOR:
			return DESCRICAO_AUTOR;
		case CampoPesquisaPorListas.ASSUNTO:
			return DESCRICAO_ASSUNTO;
		case CampoPesquisaPorListas.SERIE:
			return DESCRICAO_SERIE;
		case CampoPesquisaPorListas.LOCAL:
			return DESCRICAO_LOCAL;
		case CampoPesquisaPorListas.EDITORA:
			return DESCRICAO_EDITORA;
		case CampoPesquisaPorListas.CLASSIFICACAO_1:
			return ClassificacoesBibliograficasUtil.getDescricaoClassificacao1();
		case CampoPesquisaPorListas.CLASSIFICACAO_2:
			return ClassificacoesBibliograficasUtil.getDescricaoClassificacao2();
		case CampoPesquisaPorListas.CLASSIFICACAO_3:
			return ClassificacoesBibliograficasUtil.getDescricaoClassificacao3();
		case CampoPesquisaPorListas.NUMERO_CHAMADA:
			return DESCRICAO_NUMERO_CHAMADA  ;
		case CampoPesquisaPorListas.ISBN:
			return DESCRICAO_ISBN;
		case CampoPesquisaPorListas.ISSN:
			return DESCRICAO_ISSN;
		default:
			return "";
		}
	}
	
	
	/**
	 *    Recupera os dados do campo que deve ser mostrado ao usuário. <br/>
	 *    <i>( Usado na busca por listas, onde o campo a ser mostrado para o usuário muda conforme o filtro )</i>
	 */
	public List<String> getCampoMostrarUsuarioPesquisaLista( CacheEntidadesMarc entidadesMarc ){
		
		switch ( entidadesMarc.getTipoCampoBuscaPorListas() ) {
			case CampoPesquisaPorListas.TITULO:
				List<String> temp = new ArrayList<String>();
				temp.add(entidadesMarc.getTitulo() +" "+ ( StringUtils.notEmpty( entidadesMarc.getSubTitulo() ) ?
						entidadesMarc.getSubTitulo() : " ") );
				return temp;
			case CampoPesquisaPorListas.AUTOR:
				List<String> temp1 = new ArrayList<String>();
				temp1.add(entidadesMarc.getAutor());
				temp1.addAll(entidadesMarc.getAutoresSecundariosFormatados());
				return temp1;
			case CampoPesquisaPorListas.ASSUNTO:
				return entidadesMarc.getAssuntosFormatados();
			case CampoPesquisaPorListas.SERIE:
				List<String> temp2 = new ArrayList<String>();
				temp2.add(entidadesMarc.getSerie());
				return temp2;
			case CampoPesquisaPorListas.LOCAL:
				return entidadesMarc.getLocaisPublicacaoFormatados();
			case CampoPesquisaPorListas.EDITORA:
				return entidadesMarc.getEditorasFormatadas();
			case CampoPesquisaPorListas.CLASSIFICACAO_1:
				entidadesMarc.getClassificacao1Formatada();
			case CampoPesquisaPorListas.CLASSIFICACAO_2:
				entidadesMarc.getClassificacao2Formatada();
			case CampoPesquisaPorListas.CLASSIFICACAO_3:
				entidadesMarc.getClassificacao3Formatada();
			case CampoPesquisaPorListas.NUMERO_CHAMADA:
				List<String> temp4 = new ArrayList<String>();
				temp4.add(entidadesMarc.getNumeroChamada());
				return temp4;
			case CampoPesquisaPorListas.ISBN:
				List<String> temp5 = new ArrayList<String>();
				temp5.add(entidadesMarc.getIsbn());
				return temp5;
			case CampoPesquisaPorListas.ISSN:
				List<String> temp6 = new ArrayList<String>();
				temp6.add(entidadesMarc.getIssn());
				return temp6;
			default:
					return new ArrayList<String>();
			}
	}
	
	
	//////////// sets e gets ///////////////
	
	public String getValorCampo() {
		return valorCampo;
	}

	public void setValorCampo(String valorCampo) {
		this.valorCampo = valorCampo;
	}

	public int getTipoCampoEscolhido() {
		return tipoCampoEscolhido;
	}

	public void setTipoCampoEscolhido(int tipoCampoEscolhido) {
		this.tipoCampoEscolhido = tipoCampoEscolhido;
	}

	public int getQuantidadeTotalCampos() {
		return quantidadeTotalCampos;
	}

	public boolean isBuscarCampos() {
		return buscarCampos;
	}

	public void setBuscarCampos(boolean buscarCampos) {
		this.buscarCampos = buscarCampos;
	}
	
	public boolean isPesquisaListaTitulo(){
		return tipoCampoEscolhido == TITULO;
	}
	
	public boolean isPesquisaListaAutor(){
		return tipoCampoEscolhido == AUTOR;
	}
	
	public boolean isPesquisaListaAssunto(){
		return tipoCampoEscolhido == ASSUNTO;
	}
	
	public boolean isPesquisaListaSerie(){
		return tipoCampoEscolhido == SERIE;
	}
	
	public boolean isPesquisaListaLocal(){
		return tipoCampoEscolhido == LOCAL;
	}
	
	public boolean isPesquisaListaEditora(){
		return tipoCampoEscolhido == EDITORA;
	}
	
	public boolean isPesquisaListaClassificacao1(){
		return tipoCampoEscolhido == CLASSIFICACAO_1;
	}
	
	public boolean isPesquisaListaClassificacao2(){
		return tipoCampoEscolhido == CLASSIFICACAO_2;
	}
	
	public boolean isPesquisaListaClassificacao3(){
		return tipoCampoEscolhido == CLASSIFICACAO_3;
	}
	
	public boolean isPesquisaListaNumeroChamada(){
		return tipoCampoEscolhido == NUMERO_CHAMADA;
	}
	
	public boolean isPesquisaListaIsbn(){
		return tipoCampoEscolhido == ISBN;
	}
	
	public boolean isPesquisaListaIssn(){
		return tipoCampoEscolhido == ISSN;
	}
	
}
