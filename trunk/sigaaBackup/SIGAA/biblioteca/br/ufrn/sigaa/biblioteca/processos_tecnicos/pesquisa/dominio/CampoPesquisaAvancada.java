/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 *
 * <p> Classe que representa um campo da busca avan�ada </p>
 *
 * <p> <i> ( Para deixar a busca mais gen�rica, o usu�rio vai poder adicionar quantos campos quiser na tela da busca avan�ada ) </i> </p>
 * 
 * @author jadson
 * @version 1.2 - Adicionando o idioma na busca avan�ada, retirando as constantes de tipos primitivos e adicionando enums
 * @version 1.3 - Generalizando os campos de classifica��es, esses campos n�o poder�o ficar fixo no sistema. 
 */
public class CampoPesquisaAvancada {

	
	/**
	 * <p> Representa os tipos de campos existentes para a busca avan�ada.</p>
	 * 
	 * <p>I M P O R T A N T E :    AO ADICIONAR UM NOVO CAMPO NA BUSCA AVAN�ADA ADICION�-LO AQUI.</p>
	 * 
	 * @author jadson
	 *
	 */
	public enum TipoCampoBuscaAvancada{
		/** Tipo de campos "Todos os Campos" da busca avan�ada no acervo */
		TODOS_OS_CAMPOS(0, "Todos os Campos"),
		/** Tipo de campos Assunto da busca avan�ada no acervo  */
		ASSUNTO(1, "Assunto"),
		/** Tipo de campos Autor da busca avan�ada no acervo  */
		AUTOR(2, "Autor"),
		/** Tipo de campos T�tulo da busca avan�ada no acervo  */
		TITULO(3, "T�tulo"),
		/** Tipo de campos S�rie da busca avan�ada no acervo  */
		SERIE(4, "S�rie"),
		/** Tipo de campos Local da busca avan�ada no acervo  */
		LOCAL(5, "Local"),
		/** Tipo de campos Editora da busca avan�ada no acervo  */
		EDITORA(6, "Editora"),
		/** Tipo de campos Ano da busca avan�ada no acervo  */
		ANO(7, "Ano"),
		/** Tipo de campos Edi��o da busca avan�ada no acervo  */
		EDICAO(8, "Edi��o"),
		/** Tipo de campos Idioma da busca avan�ada no acervo  */
		IDIOMA(9, "Idioma"),
		/** Tipo de campos ISBN da busca avan�ada no acervo  */
		ISBN(10, "ISBN"),
		/** Tipo de campos ISSN da busca avan�ada no acervo  */
		ISSN(11, "ISSN"),
		/** Tipo de campos classifica��o 1 da busca avan�ada no acervo  */
		CLASSIFICACAO1(12, ""), // a descri��o s� � atribu�da no momento e mostrar para o usu�rio.
		/** Tipo de campos classifica��o 2 da busca avan�ada no acervo  */
		CLASSIFICACAO2(13, ""),  // a descri��o s� � atribu�da no momento e mostrar para o usu�rio.
		/** Tipo de campos classifica��o 3 da busca avan�ada no acervo  */
		CLASSIFICACAO3(14, ""),  // a descri��o s� � atribu�da no momento e mostrar para o usu�rio.
		/** Tipo de campos T�tulo Uniforme da busca avan�ada no acervo  */
		TITULO_UNIFORME(15, "T�tulo Uniforme"),
		/** Tipo de campos Notas da busca avan�ada no acervo  */
		NOTAS(16, "Notas"),
		/** Tipo de campos Biblioteca da busca avan�ada no acervo  */
		BIBLIOTECA(17, "Biblioteca"),
		/** Tipo de campos Cole��o da busca avan�ada no acervo  */
		COLECAO(18, "Cole��o"),
		/** Tipo de campos Tipo de Material da busca avan�ada no acervo  */
		TIPO_MATERIAL(19, "Tipo de Material"),
		/** Tipo de campos Status da busca avan�ada no acervo  */
		STATUS(20, "Status");
		
		private TipoCampoBuscaAvancada(int valor , String descricao){
			this.valor = valor;
			this.descricao = descricao;
		}
		
		/** A posicao a ser exibido na busca */
		private int valor;
		
		/** A descri��o do banco de busca */
		private String descricao;

		public int getValor() {
			return valor;
		}

		public String getDescricao() {
			return descricao;
		}
		
		
		
		/**
		 * Retorna o campo a partir da posi��o, utilizados para combox box na interface do usu�rio onde n�o � permitido interar enuns
		 *
		 * @param posicao
		 * @return
		 */
		public static TipoCampoBuscaAvancada getTipoCampoBuscaAvancada(Integer valor){
			if( valor.equals(TODOS_OS_CAMPOS.valor))
				return TODOS_OS_CAMPOS;
			if( valor.equals(ASSUNTO.valor))
				return ASSUNTO;
			if( valor.equals(AUTOR.valor))
				return AUTOR;
			if( valor.equals(TITULO.valor))
				return TITULO;
			if( valor.equals(SERIE.valor))
				return SERIE;
			if( valor.equals(LOCAL.valor))
				return LOCAL;
			if( valor.equals(EDITORA.valor))
				return EDITORA;
			if( valor.equals(ANO.valor))
				return ANO;
			if( valor.equals(EDICAO.valor))
				return EDICAO;
			if( valor.equals(IDIOMA.valor))
				return IDIOMA;
			if( valor.equals(ISBN.valor))
				return ISBN;
			if( valor.equals(ISSN.valor))
				return ISSN;
			if( valor.equals(CLASSIFICACAO1.valor))
				return CLASSIFICACAO1;
			if( valor.equals(CLASSIFICACAO2.valor))
				return CLASSIFICACAO2;
			if( valor.equals(CLASSIFICACAO3.valor))
				return CLASSIFICACAO3;
			if( valor.equals(TITULO_UNIFORME.valor))
				return TITULO_UNIFORME;
			if( valor.equals(NOTAS.valor))
				return NOTAS;
			if( valor.equals(BIBLIOTECA.valor))
				return BIBLIOTECA;
			if( valor.equals(COLECAO.valor))
				return COLECAO;
			if( valor.equals(TIPO_MATERIAL.valor))
				return TIPO_MATERIAL;
			if( valor.equals(STATUS.valor))
				return STATUS;
			
				
			return null;
		}
		
	}

	
	
	// As constantes que indicam os cruzamentos que o usu�rio pode escolher
	/** Constante para 'e' */
	public static final char E = 'E';
	/** Constante para 'ou' */
	public static final char OU = 'O';
	/** Constante para 'n�o' */
	public static final char NAO = 'N';
	
	
	/** Posi��o de um campo de busca, apenas para saber qual o campo foi selecionado pelo usuario
	 *  Normalmente � a posi��o do campo dentro da lista de campos que � mostrada ao usu�rio. */
	private int posicaoCampo;
	
	
	 /** o valor do campo digitado pelo usu�rio, pode conter tamb�m o id de uma biblioteca, cole��o *
	  * ou tipo de material caso o usu�rio escolher buscar informa��es dos materiais do T�tulo      */
	private String valorCampo; 
	
	/** Conectores l�gicos entre os campos: 'E' 'OU' ou 'NOR', o primeiro campo � sempre 'E' */
	private char conexao;
	
	/** O tipo do campo que o usu�rio escolheu, entre os tipos que est�o dispon�veis para crit�rio de busca   *
	 * Todos os campos, t�tulo, autor, ...*/
	private TipoCampoBuscaAvancada tipoCampo;


	/** Indica se o usu�rio selecionou ou n�o o campo na tela */
	private boolean buscarCampo;
	
	/** Indica se o campo deve ser renderizado como um combox de bibliotecas */
	private boolean renderizaComboxBibliotecaCampo;
	
	/** Indica se o campo deve ser renderizado como um combox de cole��es */
	private boolean renderizaComboxColecaoCampo;
	
	/** Indica se o campo deve ser renderizado como um combox de tipos de materiais */
	private boolean renderizaComboxTipoMaterialCampo;
	
	/** Indica se o campo deve ser renderizado como um combox de tipos de materiais */
	private boolean renderizaComboxStatusCampo;
	
	/** Quando a quantidade total de campos, importante para saber na busca por todos os campos 
	 * a quantidade de par�metros que deve ser acrescentada.
	 */
	private int quantidadeTotalCampos;

	/** Indica se � uma busca p�blica ou interna */
	private boolean buscaPublica;
	
	
	/**
	 * Construtor que cria um campo da pesquisa avan�ada com os valores padr�o
	 */
	public CampoPesquisaAvancada(int posicaoCampo, boolean buscaPublica) {
		this.posicaoCampo = posicaoCampo;
		this.valorCampo = "";
		this.conexao = E;
		this.tipoCampo = TipoCampoBuscaAvancada.TODOS_OS_CAMPOS;
		this.buscaPublica = buscaPublica;
	}


	/**
	 * Construtor que cria um campo da pesquisa avan�ada com os valores passados pelo usu�rio.
	 * 
	 * @param valorCampo
	 * @param conexao
	 * @param tipoCampo
	 */
	public CampoPesquisaAvancada(int posicaoCampo, String valorCampo, char conexao, TipoCampoBuscaAvancada tipoCampo) {
		this.posicaoCampo = posicaoCampo;
		this.valorCampo = valorCampo;
		this.conexao = conexao;
		this.tipoCampo = tipoCampo;
	}

	
	/**
	 *   Retorna a descri��o do conector do campo
	 *
	 * @return
	 */
	public String getDescricaoConector(){
		
		if(isConectorE())
			return "E";
		
		if(isConectorOU())
			return "OU";
		
		if(isConectorNAO())
			return "E N�O";
		
		return "";
		
	}
	
	
	/**
	 *     Verifica se o usu�rio escolheu algum campo de pesquisa sobre informa��es dos materiais
	 *
	 *
	 * @param tipoCampoSelecionado
	 * @return
	 */
	public boolean isCampoBuscaInformacoesMateriais(){
		if(this.tipoCampo == TipoCampoBuscaAvancada.BIBLIOTECA || this.tipoCampo  == TipoCampoBuscaAvancada.COLECAO || this.tipoCampo  == TipoCampoBuscaAvancada.TIPO_MATERIAL || this.tipoCampo  == TipoCampoBuscaAvancada.STATUS )
			return true;
		else
			return false;
	}
	
	
	/**
	 * 
	 *  Verifica se o usu�rio escolheu algum campo de pesquisa de classifica��o bibliogr�fica.
	 *  
	 * @return
	 */
	public boolean isCamposBuscaClassificacoes(){
		if(this.tipoCampo == TipoCampoBuscaAvancada.CLASSIFICACAO1 
				|| this.tipoCampo == TipoCampoBuscaAvancada.CLASSIFICACAO2 
				|| this.tipoCampo == TipoCampoBuscaAvancada.CLASSIFICACAO3) 
			return true;
		else
			return false;
	}
	
	
	
	/**
	 *   Verifica se a informa��o do campo foi digitada
	 *
	 * @return
	 */
	public boolean contemInformacoes(){
		if( isCampoBuscaInformacoesMateriais()){
			if(  "-1".equalsIgnoreCase( valorCampo) )
				return false;
			else
				return true;
		}else{
			if(StringUtils.isEmpty(valorCampo) )
				return false;
			else
				return true;
		}
	}
	
	
	
	/**
	 *   Verifica qual o valor atual do tipoCampo do campo, caso seja algum valor que deve ser 
	 *   renderizado diferente na tela, atualiza a vari�vel que indica isso.
	 *
	 */
	public void verificaExibicaoTipoCampo(){
		
		renderizaComboxBibliotecaCampo = false;
		renderizaComboxColecaoCampo = false;
		renderizaComboxTipoMaterialCampo = false;
		setRenderizaComboxStatusCampo(false);
		
		if(tipoCampo == TipoCampoBuscaAvancada.BIBLIOTECA )
			renderizaComboxBibliotecaCampo = true;
		
		if(tipoCampo == TipoCampoBuscaAvancada.COLECAO )
			renderizaComboxColecaoCampo = true;
		
		if(tipoCampo == TipoCampoBuscaAvancada.TIPO_MATERIAL )
			renderizaComboxTipoMaterialCampo = true;
		
		if(tipoCampo == TipoCampoBuscaAvancada.STATUS )
			renderizaComboxStatusCampo = true;
		
		valorCampo = null;
	}
	
	/**
	 * Verifica se o conector l�gico do campo � um AND
	 */
	public boolean isConectorE(){
		return this.conexao == E;
	}
	
	/**
	 * Verifica se o conector l�gico do campo � um OR
	 */
	public boolean isConectorOU(){
		return this.conexao == OU;
	}
	
	/**
	 * Verifica se o conector l�gico do campo � um NOR
	 */
	public boolean isConectorNAO(){
		return this.conexao == NAO;
	}
	
	/**
	 *   Volta o campo para as informa��es padr�o.
	 *
	 */
	public void resetaDadosCampo(){
		buscarCampo = false;
		this.valorCampo = "";
		this.conexao = E;
		this.tipoCampo = TipoCampoBuscaAvancada.TODOS_OS_CAMPOS;
		renderizaComboxBibliotecaCampo = false;
		renderizaComboxColecaoCampo = false;
		renderizaComboxTipoMaterialCampo = false;
		renderizaComboxStatusCampo = false;
	}
	
	
	/**
	 *   M�todo que gera a consulta para o campo de acordo com tipo dele.
	 *
	 *   <p>I M P O R T A N T E :    AO ADICIONAR UM NOVO CAMPO NA BUSCA AVAN�ADA ADICIONAR SUA BUSCA AQUI.</p>
	 *
	 * @param geradorPesquisa
	 * @param nomesAutorizados caso a busca seja de autor ou assunto tem que gerar para os nomes autorizados da base de autoriades tamb�m
	 * @return
	 */
	public String gerarConsulta(GeraPesquisaTextual geradorPesquisa, String... nomesAutorizados){
		
		StringBuilder sqlSelectTemp = new StringBuilder();
		
		if(! StringUtils.isEmpty(valorCampo)){
			

			if( ! contemInformacoes() 
					|| ( ! isCampoBuscaInformacoesMateriais() && ! isCamposBuscaClassificacoes() && BibliotecaUtil.retornaPalavrasBusca(valorCampo).length == 0 )
					|| ( isCamposBuscaClassificacoes() && StringUtils.isEmpty( valorCampo) )  ) 
				sqlSelectTemp.append(" 1 = 0 "); // N�o existe palavras para a busca, usu�rio digitou por exemplo: "a a a a a a";
			else{
					
				this.quantidadeTotalCampos = 1; // Por padr�o 
				
				// SEMPRE QUE ADICIONAR UM NOVO TIPO DE CAMPO PARA O USU�RIO PESQUISAR, TEM QUE ADICIONAR AQUI.
				switch(getTipoCampo()){
					case TODOS_OS_CAMPOS:
						sqlSelectTemp.append("( ");
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.outras_informacoes_titulo_ascii") );
						sqlSelectTemp.append( " OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.assunto_ascii") );
						sqlSelectTemp.append( " OR "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.autor_ascii") );
						sqlSelectTemp.append( " OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autores_secundarios_ascii") );
						sqlSelectTemp.append( " OR "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.serie_ascii") );
						sqlSelectTemp.append( " OR "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.local_publicacao_ascii") );
						sqlSelectTemp.append( " OR "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.editora_ascii") );
						sqlSelectTemp.append( " OR "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.ano") );
						sqlSelectTemp.append( " OR "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.edicao") );
						sqlSelectTemp.append( " OR "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.isbn") );
						sqlSelectTemp.append( " OR "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.issn") );
						sqlSelectTemp.append( " OR "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.titulo_uniforme_ascii") );
						sqlSelectTemp.append( " OR "+geradorPesquisa.gerarMecanismoPesquisaTextual("t.notas_ascii") );
						sqlSelectTemp.append(" ) ");
						
						this.quantidadeTotalCampos = 13; // Acrescentar esse valor quando adicionar um novo campo.
						
						break;
					case ASSUNTO:  
						
						if(nomesAutorizados.length == 0)
							sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.assunto_ascii") );
						else{   // existem nomes autorizados

							sqlSelectTemp.append(" ( ");
							
							sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.assunto_ascii") ); // o assunto � sempre o primeiro
							
							for (int i = 0; i < nomesAutorizados.length; i++) { // do 2� para frente, vem as entradas autorizadas
				
								sqlSelectTemp.append( " OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.assunto_ascii") );
							}
							
							sqlSelectTemp.append(" ) ");
							
						}
						
						this.quantidadeTotalCampos = 1; // os par�metros da busca remissiva n�o s�o levados em considera��o aqui 
						
						break;
					case AUTOR: 
						if(nomesAutorizados.length == 0){
							sqlSelectTemp.append("( "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autor_ascii") );
							sqlSelectTemp.append(" OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autores_secundarios_ascii") +" ) " );
						}else{
							
							sqlSelectTemp.append(" ( ");
							
							sqlSelectTemp.append(geradorPesquisa.gerarMecanismoPesquisaTextual("t.autor_ascii") );
							sqlSelectTemp.append(" OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autores_secundarios_ascii"));
							
							for (int i = 0; i < nomesAutorizados.length; i++) { // do 2� para frente, vem as entradas autorizadas
				
								sqlSelectTemp.append( " OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autor_ascii") );
							}
							
							for (int i = 0; i < nomesAutorizados.length; i++) { // do 2� para frente, vem as entradas autorizadas
								
								sqlSelectTemp.append( " OR "+ geradorPesquisa.gerarMecanismoPesquisaTextual("t.autores_secundarios_ascii") );
							}
							
							sqlSelectTemp.append(" ) ");
						}
						
						this.quantidadeTotalCampos = 2; // os par�metros da busca remissiva n�o s�o levados em considera��o aqui 
				
						break;
					case TITULO:
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.outras_informacoes_titulo_ascii") );
						break;
					case SERIE:           
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.serie_ascii") );
						break;
					case LOCAL:
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.local_publicacao_ascii") );
						break;
					case EDITORA:
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.editora_ascii") );
						break;
					case ANO: 
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.ano") );
						//sqlSelectTemp.append(  "ano_publicacao = " + valorCampo );
						break;
					case EDICAO:
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.edicao") );
						break;
					case IDIOMA:
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.idioma_ascii") );
						break;
					case ISBN:
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.isbn") );
						break;
					case ISSN:
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.issn") );
						break;
					case CLASSIFICACAO1:
						sqlSelectTemp.append(  geradorPesquisa.gerarMecanismoPesquisaInicioPalavra("t.classificacao_1_ascii"));
						break;
					case CLASSIFICACAO2:
						sqlSelectTemp.append(  geradorPesquisa.gerarMecanismoPesquisaInicioPalavra("t.classificacao_2_ascii"));
						break;
					case CLASSIFICACAO3:
						sqlSelectTemp.append(  geradorPesquisa.gerarMecanismoPesquisaInicioPalavra("t.classificacao_3_ascii"));
						break;
					case TITULO_UNIFORME:
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.titulo_uniforme_ascii") );
						break;
					case NOTAS:
						sqlSelectTemp.append( geradorPesquisa.gerarMecanismoPesquisaTextual("t.notas_ascii") );
						break;
					case BIBLIOTECA:
						
						int idBiblioteca = -1;
						
						try{
							idBiblioteca = Integer.parseInt( valorCampo );
						}catch (NumberFormatException nfe) {
							idBiblioteca = -1;
						}
						if(isConectorNAO())
							sqlSelectTemp.append("  material.id_biblioteca != "+idBiblioteca+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ");
						else
							sqlSelectTemp.append("  material.id_biblioteca = "+idBiblioteca+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ");
						break;
					case COLECAO:    
						
						int idColecao = -1;
						
						try{
							idColecao = Integer.parseInt( valorCampo );
						}catch (NumberFormatException nfe) {
							idColecao = -1;
						}
						if(isConectorNAO())
							sqlSelectTemp.append("  material.id_colecao != "+idColecao+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ");
						else
							sqlSelectTemp.append("  material.id_colecao = "+idColecao+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ");
						break;
					case TIPO_MATERIAL:
						
						int idTipoMaterial = -1;
						
						try{
							idTipoMaterial = Integer.parseInt( valorCampo );
						}catch (NumberFormatException nfe) {
							idTipoMaterial = -1;
						}
						if(isConectorNAO())
							sqlSelectTemp.append("  material.id_tipo_material != "+idTipoMaterial+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ");
						else
							sqlSelectTemp.append("  material.id_tipo_material = "+idTipoMaterial+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ");
						break;
					case STATUS:
						
						int idStatus = -1;
						
						try{
							idStatus = Integer.parseInt( valorCampo );
						}catch (NumberFormatException nfe) {
							idStatus = -1;
						}
						if(isConectorNAO())
							sqlSelectTemp.append("  material.id_status_material_informacional != "+idStatus+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ");
						else
							sqlSelectTemp.append("  material.id_status_material_informacional = "+idStatus+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ");
						break;
						
				}
			}
		}
		
		
		return sqlSelectTemp.toString();
	}
	
	
	/**
	 * <p>Retorna os campos de pesquisa para mostrar na tela para sele��o dos bibliotec�rios e funcion�rios.</p>
	 *
	 * <p>I M P O R T A N T E :    AO ADICIONAR UM NOVO CAMPO NA BUSCA AVAN�ADA ADICION�-LO AQUI.</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public Collection <SelectItem> getCamposPesquisaAvancadaComboBox() throws DAOException{
		
		List<SelectItem> itens = new ArrayList<SelectItem>();
		
		SelectItem item1 = new SelectItem(TipoCampoBuscaAvancada.TODOS_OS_CAMPOS.valor, TipoCampoBuscaAvancada.TODOS_OS_CAMPOS.descricao);
		SelectItem item2 = new SelectItem(TipoCampoBuscaAvancada.ASSUNTO.valor, TipoCampoBuscaAvancada.ASSUNTO.descricao);
		SelectItem item3 = new SelectItem(TipoCampoBuscaAvancada.AUTOR.valor, TipoCampoBuscaAvancada.AUTOR.descricao);
		SelectItem item4 = new SelectItem(TipoCampoBuscaAvancada.TITULO.valor, TipoCampoBuscaAvancada.TITULO.descricao);
		SelectItem item5 = new SelectItem(TipoCampoBuscaAvancada.SERIE.valor, TipoCampoBuscaAvancada.SERIE.descricao);
		SelectItem item6 = new SelectItem(TipoCampoBuscaAvancada.LOCAL.valor, TipoCampoBuscaAvancada.LOCAL.descricao);
		SelectItem item7 = new SelectItem(TipoCampoBuscaAvancada.EDITORA.valor, TipoCampoBuscaAvancada.EDITORA.descricao);
		SelectItem item8 = new SelectItem(TipoCampoBuscaAvancada.ANO.valor, TipoCampoBuscaAvancada.ANO.descricao);
		SelectItem item9 = new SelectItem(TipoCampoBuscaAvancada.EDICAO.valor, TipoCampoBuscaAvancada.EDICAO.descricao);
		SelectItem item10 = new SelectItem(TipoCampoBuscaAvancada.IDIOMA.valor, TipoCampoBuscaAvancada.IDIOMA.descricao);
		SelectItem item11 = new SelectItem(TipoCampoBuscaAvancada.ISBN.valor, TipoCampoBuscaAvancada.ISBN.descricao);
		SelectItem item12 = new SelectItem(TipoCampoBuscaAvancada.ISSN.valor, TipoCampoBuscaAvancada.ISSN.descricao);
		
		// A descri��o das classifica��es utilizadas s�o adicionadas aqui, no momento da exibi��o para o usu�rio //
		SelectItem item13 = new SelectItem(TipoCampoBuscaAvancada.CLASSIFICACAO1.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao1()); 
		SelectItem item14 = new SelectItem(TipoCampoBuscaAvancada.CLASSIFICACAO2.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao2());
		SelectItem item15 = new SelectItem(TipoCampoBuscaAvancada.CLASSIFICACAO3.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao3());
		
		SelectItem item16 = new SelectItem(TipoCampoBuscaAvancada.TITULO_UNIFORME.valor, TipoCampoBuscaAvancada.TITULO_UNIFORME.descricao);
		SelectItem item17 = new SelectItem(TipoCampoBuscaAvancada.NOTAS.valor, TipoCampoBuscaAvancada.NOTAS.descricao);
		SelectItem item18 = new SelectItem(TipoCampoBuscaAvancada.BIBLIOTECA.valor, TipoCampoBuscaAvancada.BIBLIOTECA.descricao);
		SelectItem item19 = new SelectItem(TipoCampoBuscaAvancada.COLECAO.valor, TipoCampoBuscaAvancada.COLECAO.descricao);
		SelectItem item20 = new SelectItem(TipoCampoBuscaAvancada.TIPO_MATERIAL.valor, TipoCampoBuscaAvancada.TIPO_MATERIAL.descricao);
		SelectItem item21 = new SelectItem(TipoCampoBuscaAvancada.STATUS.valor, TipoCampoBuscaAvancada.STATUS.descricao);
		
		itens.add(item1);
		itens.add(item2);
		itens.add(item3);
		itens.add(item4);
		itens.add(item5);
		itens.add(item6);
		itens.add(item7);
		itens.add(item8);
		itens.add(item9);
		itens.add(item10);
		itens.add(item11);
		itens.add(item12);
		
		if(ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao1())
			itens.add(item13);
		if(ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao2())
			itens.add(item14);
		if(ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao3())
			itens.add(item15);
		
		itens.add(item16);
		itens.add(item17);
		itens.add(item18);
		itens.add(item19);
		itens.add(item20);
		
		if(!buscaPublica)
			 itens.add(item21);
		
		return itens;
		
	}
	
	
	
	/**
	 *   M�todo que verifica se a consulta gerou algum par�metro.
	 *
	 * @return
	 */
	public boolean pequisaGerouParametro(){
		if(StringUtils.notEmpty(valorCampo)){
			
			if(isCampoBuscaInformacoesMateriais())
				return true;
			
			if(isCamposBuscaClassificacoes())
				if(valorCampo.length() > 0)
					return true;
			
			if( BibliotecaUtil.retornaPalavrasBusca(valorCampo).length > 0 )
				return true;
		}
			
		return false;
	}
	
	
	/**
	 *   M�todo que retorna as palavras para realizar a busca a partir do valor do campo e do mecanismo 
	 * que se est� usando para gerar as pesquisas.  
	 *
	 * @return
	 */
	public String getParametrosPesquisaGerada(GeraPesquisaTextual geradorPesquisa){
		
		if( StringUtils.notEmpty(valorCampo)){
			
			String[] palavrasBusca;
			
			if(! isCamposBuscaClassificacoes() )
				palavrasBusca = BibliotecaUtil.retornaPalavrasBusca(valorCampo);
			else
				return geradorPesquisa.formataTextoPesquisaInicioPalavra(valorCampo); // vai fazer uma consulta com like '?%' neste caso
			
			if(tipoCampo != TipoCampoBuscaAvancada.BIBLIOTECA && tipoCampo != TipoCampoBuscaAvancada.COLECAO && tipoCampo != TipoCampoBuscaAvancada.TIPO_MATERIAL && tipoCampo != TipoCampoBuscaAvancada.STATUS)
				return geradorPesquisa.formataTextoPesquisaTextual(palavrasBusca);
			else
				return "";  // Aqui vai o valor digita direto na consulta porque � inteiro
		}
		
		return "";
	}
	
	
	
	/**
	 *  M�todo que verifica se o usu�rio est� realizando a opera��o l�gica "OU" com informa��es dos materiais. <br/>
	 *  Com a opera��o "OU" a quantidade de resultados pode ser muito grande, por causa de uma busca sequencial
	 *  na tabela material_informacional. <br/>
	 *  Isso torna a busca muito lenta, por isso esta opera��o foi desabilitada <br/>
	 *
	 * @return
	 */
	public boolean fazendoOuLogicoComInformacoesMateriais(){
		if( isBuscarCampo() && isConectorOU() && isCampoBuscaInformacoesMateriais())
			return true;
		return false;
	}
	
	
	
	
	public String getValorCampo() {
		return valorCampo;
	}


	public char getConexao() {
		return conexao;
	}


	public TipoCampoBuscaAvancada getTipoCampo() {
		return tipoCampo;
	}
	

	public boolean isBuscarCampo() {
		return buscarCampo;
	}


	public void setBuscarCampo(boolean buscarCampo) {
		this.buscarCampo = buscarCampo;
	}


	public void setValorCampo(String valorCampo) {
		this.valorCampo = valorCampo;
	}


	public void setConexao(char conexao) {
		this.conexao = conexao;
	}


	public void setTipoCampo(TipoCampoBuscaAvancada tipoCampo) {
		this.tipoCampo = tipoCampo;
	}
	
	public void setValorTipoCampo(int valor) {
		this.tipoCampo = TipoCampoBuscaAvancada.getTipoCampoBuscaAvancada(valor);
	}

	public int getValorTipoCampo(){
		return this.tipoCampo.getValor();
	}
	
	public boolean isRenderizaComboxBibliotecaCampo() {
		return renderizaComboxBibliotecaCampo;
	}


	public void setRenderizaComboxBibliotecaCampo(
			boolean renderizaComboxBibliotecaCampo) {
		this.renderizaComboxBibliotecaCampo = renderizaComboxBibliotecaCampo;
	}


	public boolean isRenderizaComboxColecaoCampo() {
		return renderizaComboxColecaoCampo;
	}


	public void setRenderizaComboxColecaoCampo(boolean renderizaComboxColecaoCampo) {
		this.renderizaComboxColecaoCampo = renderizaComboxColecaoCampo;
	}


	public boolean isRenderizaComboxTipoMaterialCampo() {
		return renderizaComboxTipoMaterialCampo;
	}


	public void setRenderizaComboxTipoMaterialCampo(
			boolean renderizaComboxTipoMaterialCampo) {
		this.renderizaComboxTipoMaterialCampo = renderizaComboxTipoMaterialCampo;
	}
	
	public boolean isRenderizaComboxStatusCampo() {
		return renderizaComboxStatusCampo;
	}


	public void setRenderizaComboxStatusCampo(boolean renderizaComboxStatusCampo) {
		this.renderizaComboxStatusCampo = renderizaComboxStatusCampo;
	}

	public int getPosicaoCampo() {
		return posicaoCampo;
	}

	public void setPosicaoCampo(int posicaoCampo) {
		this.posicaoCampo = posicaoCampo;
	}
	
	
	public int getQuantidadeTotalCampos() {
		return quantidadeTotalCampos;
	}

	

	/**
	 * Usado para exibir o valor dos campos escolhidos no resultado da busca em formato de relat�rio.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(StringUtils.isEmpty(valorCampo) || ! isBuscarCampo())
			return " ";
		
		String valorCampoVisualizacao = valorCampo;
		
		
		try {
			if(isRenderizaComboxBibliotecaCampo())
				valorCampoVisualizacao = BibliotecaUtil.getDescricaoBiblioteca( Integer.parseInt( valorCampo ));
			if(isRenderizaComboxColecaoCampo())
				valorCampoVisualizacao = BibliotecaUtil.getDescricaoColecao( Integer.parseInt( valorCampo ));
			if(isRenderizaComboxTipoMaterialCampo())
				valorCampoVisualizacao = BibliotecaUtil.getDescricaoTipoMaterial( Integer.parseInt( valorCampo ));
			if(isRenderizaComboxStatusCampo())
				valorCampoVisualizacao = BibliotecaUtil.getDescricaoStatus( Integer.parseInt( valorCampo ));
			
		} catch (DAOException e) {
			e.printStackTrace();
			valorCampoVisualizacao = " -- ";
		} catch (NumberFormatException e) {
			e.printStackTrace();
			valorCampoVisualizacao = " -- ";
		}

		
		return ( posicaoCampo != 0 ? "<strong>"+getDescricaoConector()+"</strong>" : " ") +" "+ "<strong>"+tipoCampo.descricao+"</strong>"+" = <i>"+valorCampoVisualizacao+"</i>";
	}





	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + posicaoCampo;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampoPesquisaAvancada other = (CampoPesquisaAvancada) obj;
		if (posicaoCampo != other.posicaoCampo)
			return false;
		return true;
	}

	
}
