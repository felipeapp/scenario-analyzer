/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on  13/05/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ImpressaoEtiquetasDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.EtiquetaImpressaoDataSource;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoPaginaImpressaoEtiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoEtiquetaImpressaoBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoRemoveMateriaisMarcadosGeracaoEtiqueta;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.EtiquetasImpressaoUtil;
import br.ufrn.sigaa.biblioteca.util.MaterialByCodigoDeBarrasComparator;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 * MBean respons�vel pelo gerenciamento da impress�o das etiquetas para materiais informacionais.
 * N�o confundir com etiquetas de t�tulo, que s�o respons�veis por identificar os campos de um t�tulo.
 *
 * @author Fred_Castro
 * @since 13/05/2009
 * @version 1.0 cria��o da classe
 * @version 1.5 jadson - Tornando o caso de uso de impress�o mais gen�rico, usu�rio vai pode escolher qual a informa��o do material que vai sair na etiqueta de lombada
 * @version 1.8 jadson 06/05/2011 - Trabalhando com formatos maiores de etiqueta, alterando para suporte um n�mero de colunas diferente, as etiquetas 
 *                                  maiores s� s�o impressas 2 colunas na folha, as menores 3 colunas.
 */

@Component(value="geraEtiquetaImpressao")
@Scope(value="request")
public class GeraEtiquetaImpressaoMBean  extends SigaaAbstractController<Object> {
	
	/** Formul�rio de gera��o de etiquetas. */
	public static final String PAGINA_FORM_GERAR_ETIQUETAS = "/biblioteca/processos_tecnicos/servicos/formGerarEtiquetas.jsp";
	
	
	/** Constante que identifica a etiqueta de c�digo de barras para impress�o */
	public static final int ETIQUETA_CODIGO_BARRAS = 1;
	
	/** Constante que identifica a etiqueta de lombada para impress�o */
	public static final int ETIQUETA_LOMBADA = 2;
	
	/** Constante que identifica quando � para imprimir as duas etiquetas para impress�o */
	public static final int AMBAS_ETIQUETAS = 3;
	
	
	/** Guarda os formatos de p�ginas utililzados na gera��o de etiqueta em processos t�cnicos */
	private static List <FormatoPaginaImpressaoEtiqueta> formatosPaginas = new ArrayList <FormatoPaginaImpressaoEtiqueta> ();
	
	
	static {
		
		List<TipoEtiquetaImpressaoBiblioteca> tiposEtiquetasSuportadas  = new ArrayList<TipoEtiquetaImpressaoBiblioteca>();
		tiposEtiquetasSuportadas.add(TipoEtiquetaImpressaoBiblioteca.CODIGO_BARRAS);   
		tiposEtiquetasSuportadas.add(TipoEtiquetaImpressaoBiblioteca.LOMBADA);
		
		
		formatosPaginas.add(new FormatoPaginaImpressaoEtiqueta(1, "N�mero 27 (63,5x31 cm - 3x9 ) - Sem Margem"
				, "etiqueta_CB_L.jasper", 3, tiposEtiquetasSuportadas));
		
		formatosPaginas.add(new FormatoPaginaImpressaoEtiqueta(2, "N�mero 27 (63,5x31 cm - 3x9 )"
				, "etiqueta_CB_L_SemMargem.jasper", 3, tiposEtiquetasSuportadas));
		
		formatosPaginas.add(new FormatoPaginaImpressaoEtiqueta(3, "N�mero 27 (63,5x31 cm - 3x9 ) -  Espa�amento � esquerda"
				, "etiqueta_CB_L_PaddingLeft.jasper", 3, tiposEtiquetasSuportadas));
		
		formatosPaginas.add(new FormatoPaginaImpressaoEtiqueta(4, "N�mero 27 (63,5x31 cm - 3x9 ) - Sem Margem : espa�amento � esquerda"
				, "etiqueta_CB_L_SemMargem_PaddingLeft.jasper", 3, tiposEtiquetasSuportadas));
		
		formatosPaginas.add(new FormatoPaginaImpressaoEtiqueta(5, "N�mero 18 (63,5x46,6 cm - 3x6 ) "
				, "etiqueta_cantos_arredondados_18_CB_L.jasper", 3, tiposEtiquetasSuportadas));
		
		formatosPaginas.add(new FormatoPaginaImpressaoEtiqueta(6, "N�mero 12 (63,5x72 cm - 3x4 ) "
				, "etiqueta_cantos_arredondados_12_CB_L.jasper", 3, tiposEtiquetasSuportadas));
		
		formatosPaginas.add(new FormatoPaginaImpressaoEtiqueta(7, "N�mero 10 (99x55,8 cm - 2x5 ) "
				, "etiqueta_cantos_arredondados_10_CB_L.jasper", 2, tiposEtiquetasSuportadas));
		
		formatosPaginas.add(new FormatoPaginaImpressaoEtiqueta(8, "N�mero 8 (99,1x67,7 cm - 2x4 ) "
				, "etiqueta_cantos_arredondados_8_CB_L.jasper", 2, tiposEtiquetasSuportadas));
	}
	
	
	///////////////// A busca que o usu�rio pode realizar na p�gina /////////////////
	
	/** Indica que a busca ser� individual. */
	public static final int BUSCA_INDIVIDUAL = 1;
	/** Indica que a busca ser� por faixa. */
	public static final int BUSCA_POR_FAIXA = 2;
	
	
	/** Guarda o c�digo de barras que o usu�rio digitou na p�gina para adicion�-lo � lista de impress�o */
	private String codigoBarras;
	
	/** Guarda o c�digo de barras inicial, caso o usu�rio busque os materiais na p�gina por intervalo de c�digo de barras */
	private String codigoBarrasInicial;
	/** Guarda o c�digo de barras final, caso o usu�rio busque os materiais na p�gina por intervalo de c�digo de barras */
	private String codigoBarrasFinal;
	
	
	/////////////////////////////////////////////////////////////
	
	
	
	
	/** A posi��o inicial de impress�o na p�gina  */
	private Integer posicaoInicial = 1;
	
	/** O formato de p�gina escolhido. */
	private FormatoPaginaImpressaoEtiqueta formatoPaginaEscolhidoImpressao;
	
	
	/** Quantidade de vezes que as etiquetas de c�digo de barras dever�o ser impressas. */
	private Integer copiasCodigoBarras = 2; // por padr�o � 2, uma dentro do livro e outra fora.
	
	/** Quantidade de vezes que as etiquetas de lombada dever�o ser impressas. */
	private Integer copiasLombada = 1;
	
	/** 
	 *  <p>Lista de linhas de etiquetas a imprimir no pdf.</p>
	 *
	 *  <p>Cada linha � um lista de Mapas <String, Object></p>
	 *
	 */
	private List <List <Map <String, Object>>> linhasDeEtiquetas = new ArrayList <List <Map <String, Object>>> ();
	
	
	/** O tipos de etiqueta que o usu�rio selecionou para ser gerada */
	private List<TipoEtiquetaImpressaoBiblioteca> tiposEtiquetaEscolhido;
	
	
	/** Guarda os materiais que realmente v�o ser geradas as etiquetas (os que est�o marcados) */
	private List<MaterialInformacional> materiaisGeracaoEtiqueta = new ArrayList<MaterialInformacional>();
	
	/** Guarda os materiais que o usu�rio adicionou na p�gina de gera��o de etiquetas   */
	private List<MaterialInformacional> materiaisAdicionadosGeracaoEtiqueta = new ArrayList<MaterialInformacional>();
	
	/**
	 * Guarda os materiais gravados pelo usu�rio para a gera��o de etiquetas, isso permite ao usu�rio
	 * imprimir as etiquetas dos materiais inclusos por eles sem a necessidade de realizar buscas.
	 */
	private List<MaterialInformacional> materiaisGravadosGeracaoEtiqueta = new ArrayList<MaterialInformacional>();
	
	
	/** Para iterar sobre os materiais na p�gina */
	private DataModel  dataModelMateriais;
	
	
	/** Se vai ser uma busca por faixa de c�digos ou individual, setado no comboBox da p�gina. */
	private int tipoBusca = BUSCA_INDIVIDUAL;
	

	/**
	 * Indica que o usu�rio s� deseja ver os seus materiais que est�o pendentes para gera��o
	 * de etiqueta, ou todos os materiais inclu�dos por todos os bibliotec�rios.
	 */
	private boolean apenasMeusMateriasPedentes = true;
	
	// Guardam o nome e a descri��o do sistema que devem ser impressos nas etiquetas
	
	/** Nome da institui��o que aparece na etiqueta. */
	private final String nomeInstituicao;
	/** Nome do sistema que aparece na etiqueta. */
	private final String nomeSistema;
	/** Nome do subsistema que aparece na etiqueta. */
	private final String descricaoSubSistema;
	
	
	/**
	 * <p> Representa��o dos Campos que podem ser impressos na etiqueta de lombada </p>
	 * 
	 * @author jadson
	 *
	 */
	public enum CampoEtiquetaLombada{
	
		/** O campo n�mero de chamada para ser inclu�do da etiqueta */
		NUMERO_CHAMADA("N�mero de Chamada"), 
		/** O campo segunda localiza��o para ser inclu�do da etiqueta */
		SEGUNDA_LOCALIZACAO("Segunda Localiza��o"), 
		/** O campo biblioteca para ser inclu�do da etiqueta */
		BIBLIOTECA("Biblioteca"),
		/** O campo cole��o para ser inclu�do da etiqueta */
		COLECAO("Cole��o"), 
		/** O campo situa��o para ser inclu�do da etiqueta */
		SITUACAO("Situa��o"), 
		/** O campo status para ser inclu�do da etiqueta */
		STATUS("Status"), 
		/** O campotipo de material para ser inclu�do da etiqueta */
		TIPO_MATERIAL("Tipo do Material"), 
		/** O campo forma de documento para ser inclu�do da etiqueta */
		FORMA_DOCUMENTO("Forma do Documento");
		
		
		/**
		 * A descri��o a ser mostrada para o usu�rio.
		 */
		private String descricao;
		
		private CampoEtiquetaLombada(String descricao){
			this.descricao = descricao;
		}

		/**  A descri��o a ser mostrada para o usu�rio.  */
		@Override
		public String toString() {
			return descricao;
		}
		
		
		/**
		 * Recupera o campo a partir do seu valor ordinal
		 *
		 * @param valor
		 * @return
		 */
		public static CampoEtiquetaLombada recuperaCampoEtiquetaLombada(int valor){
			
			for (CampoEtiquetaLombada campo : CampoEtiquetaLombada.values()) {
				
				if(campo.ordinal() == valor)
					return campo;
				
			} 
			
			return null; // nunca deve chegar aqui
		}
		
		/**
		 * Retorna a partir do campo adicionado pelo usu�rio a informa��o do campo equivalente do material selecionado.
		 * 
		 *
		 * @param m
		 * @return cada posi��o do array equivale a uma linha a ser impressa na etiqueta de lombada
		 */
		public List<String> getLinhasEtiquetaLombada(MaterialInformacional m){
			
			List<String> retorno = new ArrayList<String>() ;
			
			switch (this) {
			case NUMERO_CHAMADA:
				if(m.getNumeroChamada() != null){
					String[] info = m.getNumeroChamada().split(" ");
					retorno.addAll( Arrays.asList(info));
				}
				break;
			case SEGUNDA_LOCALIZACAO:
				if(m.getSegundaLocalizacao() != null)
				retorno.add( m.getSegundaLocalizacao());
				break;
			case BIBLIOTECA:
				if(m.getBiblioteca() != null)
				retorno.add(m.getBiblioteca().getDescricaoResumida());
				break;
			case COLECAO:
				if(m.getColecao() != null)
				retorno.add(m.getColecao().getDescricao());
				break;
			case SITUACAO:
				if(m.getSituacao() != null)
				retorno.add(m.getSituacao().getDescricao());
				break;
			case STATUS:
				if(m.getStatus() != null)
				retorno.add(m.getStatus().getDescricao());
				break;
			case TIPO_MATERIAL:
				if(m.getTipoMaterial() != null)
				retorno.add(m.getTipoMaterial().getDescricao());
				break;
			case FORMA_DOCUMENTO:
				if(m.getFormasDocumento() != null){
					for (FormaDocumento formato : m.getFormasDocumento()) {
						retorno.add(formato.getDenominacao());
					}
				}
				break;
			default:
				break;
			}
			
			return retorno;
		}
	}
	
	
	/**
	 * <p>A lista que vai guardar os campos que ser�o impressos na etiqueta de lombada.</p>
	 * 
	 * <p>Por padr�o � adicionado pelo sistema a n�mero de chamada, mas o usu�rio pode retirar e adicionar novos.</p>
	 */
	private List<CampoEtiquetaLombada> camposEtiquetaLombada = new ArrayList<CampoEtiquetaLombada>();
	
	
	/**
	 * Utilizado para acesar um posi��o exata da lista n� p�gina JSP
	 */
	private DataModel camposEtiquetaLombadaDataModel;
	
	
	/**
	 * <p>Guarda a informa��o do campo de etiquta que o usu�rio selecionou ou removeu. </p>
	 */
	private CampoEtiquetaLombada campoEtiquetaLombadaSelecionado;
	
	
	
	/**
	 * <p> Construtor Padr�o </p>
	 * 
	 * <p>
	 * <i>Por enquanto, novos formatos n�o s�o cadastrados, sei arquivos s�o gerados e s�o carregados manualmente nesse m�todos.
	 * O sistema ainda n�o trabalho com todos os formatos de etiquetas bibliogr�ficas.</i>
	 * </p>
	 */
	public GeraEtiquetaImpressaoMBean () {
	
		nomeInstituicao = RepositorioDadosInstitucionais.get("siglaInstituicao");
		nomeSistema = RepositorioDadosInstitucionais.get("siglaSigaa");
		descricaoSubSistema = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.DESCRICAO_SUB_SISTEMA_BIBLIOTECA);
		
		formatoPaginaEscolhidoImpressao = new FormatoPaginaImpressaoEtiqueta();
		
		/**
		 * Por padr�o � impresso o n�mero de chamada nas etiquetas de lombada
		 */
		camposEtiquetaLombada.add( CampoEtiquetaLombada.NUMERO_CHAMADA );
		camposEtiquetaLombadaDataModel  = new ListDataModel(camposEtiquetaLombada);
		campoEtiquetaLombadaSelecionado = CampoEtiquetaLombada.NUMERO_CHAMADA;
	}
	
	
	
	
	/**
	 * Inicia a p�gina que configura as etiquetas para impress�o.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	public String iniciar() throws SegurancaException, DAOException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		//this.materiais = new ArrayList <MaterialInformacional> ();
		
		configuraListasMaterias(true);
		
		return forward(PAGINA_FORM_GERAR_ETIQUETAS);
	}
	
	
	/**
	 *       Atualiza os t�tulos incompletos mostrados ao usu�rio de acordo com a op��o desejada pelo usu�rio de ver todos
	 *  os t�tulos ou apenas os inclu�dos por ele.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
	 */
	public void atualizaMateriaisPendentes(ValueChangeEvent evt) throws DAOException {
	
		apenasMeusMateriasPedentes = ( Boolean ) evt.getNewValue();
		
		configuraListasMaterias(false);
		
		forward(PAGINA_FORM_GERAR_ETIQUETAS);
	}
	
	
	/**
	 *       Configura as tr�s listas de materiais para a gera��o de etiquetas. A de materiais pendentes
	 *   salvos no banco, a de materiais adicionados na p�gina pelo usu�rio e a de materiais marcados
	 *   para gera��o do c�digo de barras.
	 */
	private void configuraListasMaterias(boolean iniciandoCasoDeUso) throws DAOException{
	
		posicaoInicial = 1;
		tiposEtiquetaEscolhido = new ArrayList<TipoEtiquetaImpressaoBiblioteca>();
		formatoPaginaEscolhidoImpressao = new FormatoPaginaImpressaoEtiqueta ();
		copiasCodigoBarras = 2; // por padr�o � 2, uma dentro do livro e outra fora.
		copiasLombada = 1;
		
		materiaisGravadosGeracaoEtiqueta.clear();
		materiaisGeracaoEtiqueta.clear();
		
		ImpressaoEtiquetasDao dao = getDAO(ImpressaoEtiquetasDao.class);
		
		
		/* O usu�rio pode visualizar os materiais inclu�dos no acervo por ele ou pelos outro usu�rios     *
		 * da mesma biblioteca. O administrador geral pode ver e consequentemente imprimir os materiais   *
		 * de todas as bibliotecas.                                                                       */
		
		if(apenasMeusMateriasPedentes){
			
			if(isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL))
				materiaisGravadosGeracaoEtiqueta =  dao.findMateriaisPendentesGeracaoEtiquetaDoUsuario(getUsuarioLogado().getId(), null);
			else{
				
				List<Integer> bibliotecas = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario( getUsuarioLogado(), new Papel( SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO));
				
				if(bibliotecas == null || bibliotecas.size() == 0){
					
					// pode ser apenas do setor de cataloga��o para gerar as etiquetas
					bibliotecas = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario( getUsuarioLogado(), new Papel( SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO));
				}
				
				if(bibliotecas != null && bibliotecas.size() > 0)
					materiaisGravadosGeracaoEtiqueta =  dao.findMateriaisPendentesGeracaoEtiquetaDoUsuario(getUsuarioLogado().getId(), bibliotecas);
			}
		}else{
			if(isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL))
				materiaisGravadosGeracaoEtiqueta =  dao.findMateriaisPendentesGeracaoEtiqueta(null);
			else{
				List<Integer> bibliotecas = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario( getUsuarioLogado(), new Papel( SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO));
				
				if(bibliotecas == null || bibliotecas.size() == 0){
					
					// pode ser apenas do setor de cataloga��o para gerar as etiquetas
					bibliotecas = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario( getUsuarioLogado(), new Papel( SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO));
				}
				
				if(bibliotecas != null && bibliotecas.size() > 0)
					materiaisGravadosGeracaoEtiqueta =  dao.findMateriaisPendentesGeracaoEtiqueta(bibliotecas);
			}
		}
		
		// inicialmente todos gravados s�o selecionados para gera��o etiqueta
		materiaisGeracaoEtiqueta.addAll(materiaisGravadosGeracaoEtiqueta);

		for (MaterialInformacional m : materiaisGeracaoEtiqueta) {
			m.setSelecionado(true);
		}
		
		dataModelMateriais = new ListDataModel(materiaisGeracaoEtiqueta);
		
		
		if(! iniciandoCasoDeUso){ // chamado depois que o usu�rio ter adicionado algum material � lista
			
			materiaisGeracaoEtiqueta.addAll(materiaisAdicionadosGeracaoEtiqueta);
			
			Collections.sort(materiaisGeracaoEtiqueta, new MaterialByCodigoDeBarrasComparator());
			
			dataModelMateriais = new ListDataModel(materiaisGeracaoEtiqueta);
		}
		
		
		
	}
	
	
	/**
	 * Adiciona um material � lista de materiais para os quais v�o ser geradas etiquetas a partir
	 * de um �nico c�digo de barras, ou de um intervalo.
	 * 
	 * <br/><br/>
	 * Usado na p�gina: /sigaa.war/biblioteca/processos_tecnicos/servicos/formGerarEtiquetas.jsp
	 */
	public void adicionarCodigoBarras (ActionEvent e) throws DAOException {
		
		ImpressaoEtiquetasDao dao = getDAO(ImpressaoEtiquetasDao.class);
		
		if( StringUtils.isEmpty(codigoBarrasInicial) && StringUtils.isEmpty(codigoBarrasFinal) && StringUtils.isEmpty(codigoBarras)){
			addMensagemWarning("Informe o C�digo de Barras do Material.");
			return;
		}
		
		if( tipoBusca == BUSCA_POR_FAIXA){
			
			if( StringUtils.isNotEmpty(codigoBarrasInicial) && StringUtils.isEmpty(codigoBarrasFinal)){
				
				addMensagemWarning("Informe o C�digo de Barras Final do Material.");
				
			}else{
				if(StringUtils.isEmpty(codigoBarrasInicial) && StringUtils.isNotEmpty(codigoBarrasFinal) ){
				
					addMensagemWarning("Informe o C�digo de Barras Inicial do Material.");
				
				}else{
				
					if(codigoBarrasFinal.compareTo(codigoBarrasInicial) < 0){
						
						addMensagemErro("O C�digo de Barras Final deve ser maior que o C�digo de Barras Inicial.");
					
					}else{
						
						Integer quantidadeMateriais = dao.countMateriaisAtivosByFaixaCodigosBarras(codigoBarrasInicial, codigoBarrasFinal);
						
						if(quantidadeMateriais >  100){
							addMensagemErro("S� podem ser adicionados 100 C�digos de Barras por vez");
						}else{
						
							List <MaterialInformacional> ms = dao.findMateriaisAtivosByFaixaCodigosBarras(codigoBarrasInicial, codigoBarrasFinal);
							
							if (ms != null){
								for (MaterialInformacional material : ms) {
									if(! materiaisGeracaoEtiqueta.contains(material)){
										
										material.setSelecionado(true);
										adicionaMaterialGeracaoEtiqueta(material);
										materiaisAdicionadosGeracaoEtiqueta.add(material);
									}
								}
								
								codigoBarrasInicial = null;
								codigoBarrasFinal = null;
								codigoBarras = null;
								
								if(ms.size() == 0){
									addMensagemErro("Materiais com o c�digo de barras entre: "+codigoBarrasInicial+" e "+codigoBarrasFinal+" n�o encontrados.");
								}
							}
						}
						
					}
				}
			}
			
		}else{
		
		
			if( ! StringUtils.isEmpty(codigoBarras)){
				
				MaterialInformacional material = dao.findMaterialAtivoByCodigoBarras(codigoBarras);
				
				if(material == null)
					addMensagemErro("Material com o c�digo de barras "+codigoBarras+" n�o encontrado.");
				else{
					
					if (!materiaisGeracaoEtiqueta.contains(material)){
						codigoBarras = "";
						
						material.setSelecionado(true);
						adicionaMaterialGeracaoEtiqueta(material);
						materiaisAdicionadosGeracaoEtiqueta.add(material);
						
						codigoBarrasInicial = null;
						codigoBarrasFinal = null;
						codigoBarras = null;
						
					} else
						addMensagemErro("Material com o c�digo de barras "+codigoBarras+" j� se encontra na lista.");
				}
				
			}
		}
		
	}
	
	
	/**
	 * M�todo que adiciona um material � lista de gera��o mantendo o sincronismo com o dado model
	 */
	private void adicionaMaterialGeracaoEtiqueta(MaterialInformacional m){
		
		if(materiaisGeracaoEtiqueta == null)
			materiaisGeracaoEtiqueta = new ArrayList<MaterialInformacional>();
		
		materiaisGeracaoEtiqueta.add(m);
		
		Collections.sort(materiaisGeracaoEtiqueta, new MaterialByCodigoDeBarrasComparator());
		
		dataModelMateriais = new ListDataModel(materiaisGeracaoEtiqueta);
	}
	
	
	/**
	 * Apaga os materiais do usu�rio que estavam selecionados para gera��o de etiquetas.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/servicos/formGerarEtiquetas.jsp
	 */
	public String removerMaterialGeracaoEtiquetas() throws ArqException{
		
		System.out.println("aqui");
		
		if(dataModelMateriais != null){
			
			MaterialInformacional material = (MaterialInformacional) dataModelMateriais.getRowData();
			
			if(materiaisAdicionadosGeracaoEtiqueta.contains(material)){ // s� apaga na mem�ria
				
				materiaisAdicionadosGeracaoEtiqueta.remove(dataModelMateriais.getRowData());
				materiaisGeracaoEtiqueta.remove(dataModelMateriais.getRowData());
				
			}else{
				if(materiaisGravadosGeracaoEtiqueta.contains(material)){ // precisa pagar os que o sistema salvou
					
					apagaMaterialGravadoSistema(material);
					materiaisGeracaoEtiqueta.remove(dataModelMateriais.getRowData());
				}
			}
		}
		
		return telaGerarEtiquetas();
	}
	
	
	
	
	/**
	 * Remove todos os materiais selecionados
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/servicos/formGerarEtiquetas.jsp
	 */
	public String removerMateriaisSelecionados() throws ArqException{
		
		List<MaterialInformacional> listaTempRemocao = new ArrayList<MaterialInformacional>();
		
		for (MaterialInformacional m : materiaisGeracaoEtiqueta) {
			
			if(m.isSelecionado()){
				listaTempRemocao.add(m);
			}
			
		}
		
		for (MaterialInformacional m : listaTempRemocao) {
			
			if(materiaisAdicionadosGeracaoEtiqueta.contains(m)){
				materiaisAdicionadosGeracaoEtiqueta.remove(m);
				materiaisGeracaoEtiqueta.remove(m);
			}else{
				if(materiaisGravadosGeracaoEtiqueta.contains(m)){ // precisa pagar os que o sistema salvou
					apagaMaterialGravadoSistema(m);
					materiaisGeracaoEtiqueta.remove(m);
				}
			}
		}
		
		return telaGerarEtiquetas();
	}
	
	
	
	
	/**
	 * Apaga os materiais que o sistema marcou como pendentes de gera��o de etiquetas.
	 */
	private void apagaMaterialGravadoSistema(MaterialInformacional material) throws ArqException{
		
		List<Integer> idsMateriais = new ArrayList<Integer>();
		idsMateriais.add(material.getId()); 
		
	
	
		prepareMovimento(SigaaListaComando.REMOVE_MATERIAIS_MARCADOS_GERACAO_ETIQUETA);
		MovimentoRemoveMateriaisMarcadosGeracaoEtiqueta mov = new MovimentoRemoveMateriaisMarcadosGeracaoEtiqueta(idsMateriais);
		mov.setCodMovimento(SigaaListaComando.REMOVE_MATERIAIS_MARCADOS_GERACAO_ETIQUETA);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagemErro("Erro ao apagar os materiais marcados para gera��o de etiquetas ");
		}
		
	}
	
	
	
	

	
	///////  M�todos que manipulam as informa��es que ser�o impressos nas  etiquetas de lombada  ///// 
	
	/**
	 * 
	 *   Retorna todos as informa��es que podem ser impressas na etiqueta de bombada
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/formGerarEtiquetas.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public Collection <SelectItem> getallInformacoesEtiquetaLombadaComboBox(){
		
		Collection <SelectItem>  selectsItens = new ArrayList<SelectItem>();
		selectsItens.add(new SelectItem(CampoEtiquetaLombada.NUMERO_CHAMADA.ordinal(), CampoEtiquetaLombada.NUMERO_CHAMADA.descricao));
		selectsItens.add(new SelectItem(CampoEtiquetaLombada.SEGUNDA_LOCALIZACAO.ordinal(), CampoEtiquetaLombada.SEGUNDA_LOCALIZACAO.descricao));
		selectsItens.add(new SelectItem(CampoEtiquetaLombada.BIBLIOTECA.ordinal(), CampoEtiquetaLombada.BIBLIOTECA.descricao));
		selectsItens.add(new SelectItem(CampoEtiquetaLombada.COLECAO.ordinal(), CampoEtiquetaLombada.COLECAO.descricao));
		selectsItens.add(new SelectItem(CampoEtiquetaLombada.SITUACAO.ordinal(), CampoEtiquetaLombada.SITUACAO.descricao));
		selectsItens.add(new SelectItem(CampoEtiquetaLombada.STATUS.ordinal(), CampoEtiquetaLombada.STATUS.descricao));
		selectsItens.add(new SelectItem(CampoEtiquetaLombada.TIPO_MATERIAL.ordinal(), CampoEtiquetaLombada.TIPO_MATERIAL.descricao));
		selectsItens.add(new SelectItem(CampoEtiquetaLombada.FORMA_DOCUMENTO.ordinal(), CampoEtiquetaLombada.FORMA_DOCUMENTO.descricao));
		return selectsItens;
	}
	
	/**
	 *  M�todo chamado para adicionar uma informa��o do campo para ser impresso na etiqueta de lombada
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/formGerarEtiquetas.jsp</li>
	 *   </ul>
	 */
	public void adicionarInformacaoEtiquetaLombada(ActionEvent evt){
		if (camposEtiquetaLombada == null){
			camposEtiquetaLombada = new ArrayList<CampoEtiquetaLombada>();
		}
		
		if(camposEtiquetaLombada.contains(campoEtiquetaLombadaSelecionado )){
			addMensagemErroAjax("Etiqueta de lombada j� possui essa informa��o");
		}else{
			camposEtiquetaLombada.add( campoEtiquetaLombadaSelecionado);
			camposEtiquetaLombadaDataModel = new ListDataModel(camposEtiquetaLombada);
		}
	}
	
	
	/**
	 * Remove da posi��o que o usu�rio selecionou na p�gina a informa��o que ia ser impressa na etiqueta de lombada
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/formGerarEtiquetas.jsp</li>
	 *   </ul>
	 * 
	 */
	public void removerInformacaoEtiquetaLombada(ActionEvent e){
		
		//Integer posicaoRemocao = getParameterInt("posicaoInformacao");
		
		List<CampoEtiquetaLombada> camposEtiquetaLombadaTemp = new ArrayList<CampoEtiquetaLombada>(); 
		
		int posicao = 0;
		
		for (CampoEtiquetaLombada campoEtiquetaLombada : camposEtiquetaLombada) {
			
			if(posicao != camposEtiquetaLombadaDataModel.getRowIndex())
				camposEtiquetaLombadaTemp.add( campoEtiquetaLombada);
			posicao++;
		}
		
		camposEtiquetaLombada = camposEtiquetaLombadaTemp;
		
		camposEtiquetaLombadaDataModel = new ListDataModel(camposEtiquetaLombada);
	}
	
	
	
	/**
	 *  <p>Chamado com combo box das informa��es do usu�rio para setar o valor do campo da etiqueta seleciondo.</p>
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/formGerarEtiquetas.jsp</li>
	 *   </ul>
	 * 
	 */
	public void setValorCampoEtiquetaLombadaSelecionado(int valor){
		campoEtiquetaLombadaSelecionado = CampoEtiquetaLombada.recuperaCampoEtiquetaLombada(valor);
	}
	
	/**
	 *  <p>Chamado com combo box das informa��es do usu�rio para setar o valor do campo da etiqueta seleciondo.</p>
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/formGerarEtiquetas.jsp</li>
	 *   </ul>
	 * 
	 */
	public int getValorCampoEtiquetaLombadaSelecionado(){
		return campoEtiquetaLombadaSelecionado.ordinal();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////  A parte de gera��o das etiqueta propriamente dita ////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * Gera as etiquetas dos materiais marcados pelo usu�rio, al�m de pagar os materiais pendentes
	 * de gera��o de etiqueta, caso o usu�rio gere a etiqueta de um desses materiais.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/controle_estatistico/formGerarEtiquetas.jsp
	 */
	public String gerar() throws ArqException {
		
		linhasDeEtiquetas = new ArrayList <List <Map <String, Object>>> ();
		
		if (tiposEtiquetaEscolhido == null || tiposEtiquetaEscolhido.size() == 0)
			addMensagemErro("Selecione o Tipo de Etiqueta da Impress�o.");
		
		
		List<MaterialInformacional> materiaisMarcados = new ArrayList<MaterialInformacional>();
		
		if (materiaisGeracaoEtiqueta == null || materiaisGeracaoEtiqueta.isEmpty())
			addMensagemErro("Selecione pelo menos um material para gerar a etiqueta.");
		else{
			for (MaterialInformacional material : materiaisGeracaoEtiqueta) {
				
				if(material.isSelecionado()){
					materiaisMarcados.add(material);
				}
			}
			
			if(materiaisMarcados.size() == 0)
				addMensagemErro("Selecione pelo menos um material para gerar a etiqueta.");
		}
		
		if ((copiasCodigoBarras == null || copiasCodigoBarras <= 0) || (copiasLombada == null || copiasLombada <= 0))
			addMensagemErro("A quantidade de c�pias deve ser maior ou igual a um.");
		
		if (formatoPaginaEscolhidoImpressao == null || formatoPaginaEscolhidoImpressao.getId() <= 0)
			addMensagemErro("Selecione o Formato da P�gina de Impress�o das Etiquetas.");
		
		if (posicaoInicial == null)
			addMensagemErro("Indique qual a Posi��o Inicial da Impress�o.");
		else{
			if( posicaoInicial < 1)
				addMensagemErro("A posi��o Incial de Impress�o deve ser maior ou igual a um.");
		}
		
		if (!hasErrors()){
			
			
			// Adiciona etiquetas em branco no in�cio da lista para livrar as etiquetas j� utilizadas da p�gina.
			for (int i = 1; i < posicaoInicial; i++)
				adicionarEtiquetaEmBranco ();
			
			boolean ok = true;
			//List<String> materiaisInvalidos = new ArrayList<String>();
			
			for (MaterialInformacional m : materiaisMarcados){
				// Insere as etiquetas na quantidade solicitada pelo usu�rio.
				if (tiposEtiquetaEscolhido.contains(TipoEtiquetaImpressaoBiblioteca.CODIGO_BARRAS) )
					for (int i = 0; i < copiasCodigoBarras; i++)
						adicionarEtiquetaCodigoBarras(m);
				
				// Por causa do formato das etiquetas deste jasper, uma linha pode ter no m�ximo 30 caracteres.
				// Mais que isso, o texto passaria da borda da etiqueta e seria impresso em cima de outra etiqueta.
				if (tiposEtiquetaEscolhido.contains(TipoEtiquetaImpressaoBiblioteca.LOMBADA) ) {
					for (int i = 0; i < copiasLombada; i++){
						
						try{
							adicionarEtiquetaLombada(m);
						//} catch (TextoLombadaMuitoGrandeException ex) {
						//	ok = false;
						//	materiaisInvalidos.add(m.getCodigoBarras());
						} catch(NegocioException ne) {
							ok = false;
							addMensagens(ne.getListaMensagens());
						}
						
					}
				}
			}
			
			if ( ! ok ) {
//				if ( ! materiaisInvalidos.isEmpty() ) {
//					String msg;
//					if ( materiaisInvalidos.size() == 1)
//						msg = "O material de c�digo de barra " + materiaisInvalidos.get(0) +
//								" tem um n�mero de chamada muito grande para a etiqueta de lombada.";
//					else
//						msg = "Os materiais de c�digos de barra " +
//								StringUtils.transformaEmLista( materiaisInvalidos ) +
//								" t�m n�meros de chamada muito grandes para a etiqueta de lombada.";
//					
//					addMensagemErro(msg);
//				}
				return forward(PAGINA_FORM_GERAR_ETIQUETAS);
			}
			
			completaLista();
			
			// Par�metros externos para o relat�rio.
			Map<String, Object> paramentros = new HashMap<String, Object>();
			paramentros.put("nomeInstituicao", nomeInstituicao);
			paramentros.put("nomeSistema", nomeSistema);
			paramentros.put("descricaoSistema", descricaoSubSistema);
			
			// Gera o pdf passando o arquivo fonte do jasper e as etiquetas a gerar.
			EtiquetasImpressaoUtil.gerarPdfParaContextoAtualDoFaces(formatoPaginaEscolhidoImpressao.getArquivoJasperUtilizado(), new EtiquetaImpressaoDataSource (linhasDeEtiquetas), paramentros);
			
			
			
			/* ************************************************************************************
			 * Apaga os materiais do usu�rio que estavam pendentes de gera��o de etiquetes porque
			 * as etiqueta acabaram de ser geradas.
			 **************************************************************************************/
			
			List<Integer> idsMateriais = new ArrayList<Integer>();
				
			for (MaterialInformacional materialVaiGerar : materiaisGeracaoEtiqueta) {
			
				if(materialVaiGerar.isSelecionado()){
					idsMateriais.add(materialVaiGerar.getId());
				}
			}
			
			
			
			prepareMovimento(SigaaListaComando.REMOVE_MATERIAIS_MARCADOS_GERACAO_ETIQUETA);
			MovimentoRemoveMateriaisMarcadosGeracaoEtiqueta mov = new MovimentoRemoveMateriaisMarcadosGeracaoEtiqueta(idsMateriais);
			mov.setCodMovimento(SigaaListaComando.REMOVE_MATERIAIS_MARCADOS_GERACAO_ETIQUETA);
			
			try {
				execute(mov);
			} catch (NegocioException e) {
				e.printStackTrace();
				addMensagemErro("Erro ao apagar os materiais marcados para gera��o de etiquetas ");
			}
			
			
			
		}// ! hasErros
		
		
		return forward(PAGINA_FORM_GERAR_ETIQUETAS);
	}
	
	/**
	 * Adiciona uma etiqueta em branco, para evitar a impress�o em uma etiqueta j� utilizada no papel.
	 */
	private void adicionarEtiquetaEmBranco (){
		Map <String, Object> e = new HashMap <String, Object> ();
		e.put("tipo", 0);
		
		adicionarEtiqueta(e);
	}
	
	
	
	
	/**
	 * Monta uma etiqueta de C�digo de Barras e adiciona � lista de etiquetas a imprimir.
	 */
	private void adicionarEtiquetaCodigoBarras (MaterialInformacional m){
		Map <String, Object> e = new HashMap <String, Object> ();
		
		Image imagem = EtiquetasImpressaoUtil.geraImagemCodigoBarras(m.getCodigoBarras().trim());
		
		e.put("tipo", new Integer (ETIQUETA_CODIGO_BARRAS));
		e.put("codigoBarras", m.getCodigoBarras().trim());
		e.put("imagemCodigoBarras", imagem);
		
		adicionarEtiqueta(e);
	}
	
	
	
	/**
	 * Monta uma etiqueta de Lombada e adiciona � lista de etiquetas a imprimir.
	 */
	private void adicionarEtiquetaLombada (MaterialInformacional m) throws NegocioException{
		Map <String, Object> e = new HashMap <String, Object> ();
		
		e.put("tipo", new Integer (ETIQUETA_LOMBADA));
		e.put("numeroChamada", EtiquetasImpressaoUtil.montaTextoLombada(camposEtiquetaLombada, m));
		
		adicionarEtiqueta(e);
	}
	
	
	
	
	
	/**
	 * Adiciona uma etiqueta � linha, de acordo com a quantidade de etiquetas por linha. Se a quantidade de etiquetas por
	 * linha j� foi atingida, cria uma nova linha.
	 */
	private void adicionarEtiqueta (Map <String, Object> e){
		
		// Se for o primeiro objeto a se inserir, cria a primeira linha.
		if (linhasDeEtiquetas.size() == 0)
			linhasDeEtiquetas.add(new ArrayList <Map <String, Object>>());
		
		// Pega a �ltima linha.
		List <Map <String, Object>> ultimalinha = linhasDeEtiquetas.get(linhasDeEtiquetas.size() - 1);
		
		
		if (   isLinhaPreenchida( ultimalinha, formatoPaginaEscolhidoImpressao.getQuantidadeEtiquetasPorLinha())  ){
			ultimalinha = new ArrayList <Map <String, Object>>(); // Cria uma novo linha e adiciona a lista de linhas
			linhasDeEtiquetas.add(ultimalinha);
		}
		
		ultimalinha.add(e);
	}
	
	
	/**
	 * Verifica se a linha da impress�o est� cheia
	 *
	 * @param linha
	 * @param quantitadeMaximaEtiquetaLinha
	 * @return
	 */
	private boolean isLinhaPreenchida(List <Map <String, Object>>  linha, int quantitadeMaximaEtiquetaLinha){
		if (linha.size() >= quantitadeMaximaEtiquetaLinha )
			return true;
		else
			return false;
	}
	
	
	/**
	 * O Jasper precisa que as linhas estejam completas, ent�o adiciona etiquetas em branco na �ltima linha, caso esta n�o esteja completa.
	 */
	private void completaLista (){
		if (linhasDeEtiquetas.size() > 0){
			
			List <Map <String, Object>> ultimaLinha = linhasDeEtiquetas.get(linhasDeEtiquetas.size() - 1);
			
			while ( ! isLinhaPreenchida(ultimaLinha, formatoPaginaEscolhidoImpressao.getQuantidadeEtiquetasPorLinha()) ){
				adicionarEtiquetaEmBranco();
			}
		}
	}
	
	/**
	 * Retorna a quantidade de t�tulos gravados pelo usu�rio logado para exporta��o.
	 * <p>
	 * Chamado a partir da p�gina:  /sigaa.war/biblioteca/processos_tecnicos/servicos/formGerarEtiquetas.jsp
	 */
	public int getQtdMateriaisGeracaoEtiqueta(){
		if (materiaisGeracaoEtiqueta != null)
			return materiaisGeracaoEtiqueta.size();
		else
			return 0;
	}
	
	/**
	 * Usado no combo box de formatos de materiais da p�gina
	 * <p>
	 * Chamado a partir da p�gina:  /sigaa.war/biblioteca/processos_tecnicos/servicos/formGerarEtiquetas.jsp
	 */
	public Integer getNumeroFormatoPagina (){
		if(formatoPaginaEscolhidoImpressao == null)
			return -1; // N�o existem formatos de p�gina com id negativos, ver construtor da classe.
		
		return formatoPaginaEscolhidoImpressao.getId();
	}
	
	/**
	 * Configura o formato da p�gina de acordo com o formato da p�gina que o usu�rio escolher
	 * no combo box
	 * <p>
	 * Chamado a partir da p�gina:  /sigaa.war/biblioteca/processos_tecnicos/servicos/formGerarEtiquetas.jsp
	 */
	public void setNumeroFormatoPagina (Integer idFormatoPagina) {
		
		if (idFormatoPagina == null || idFormatoPagina.compareTo(new Integer(0)) <= 0)
			formatoPaginaEscolhidoImpressao.setId(-1);
		else
			for (FormatoPaginaImpressaoEtiqueta f : formatosPaginas)
				if (f.getId() == idFormatoPagina){
					formatoPaginaEscolhidoImpressao.setId(f.getId());
					formatoPaginaEscolhidoImpressao.setArquivoJasperUtilizado(f.getArquivoJasperUtilizado());
					formatoPaginaEscolhidoImpressao.setQuantidadeEtiquetasPorLinha(f.getQuantidadeEtiquetasPorLinha());
					formatoPaginaEscolhidoImpressao.setTipoEtiquetasSuportadasPagina(f.getTipoEtiquetasSuportadasPagina());
					formatoPaginaEscolhidoImpressao.setLabel(f.getLabel());
					break;
				}
	}
	
	/**
	 * Retorna os formatos da p�gina para o usu�rio escolher.
	 * <p>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/servicos/formGerarEtiquetas.jsp
	 */
	public List <SelectItem> getFormatosPaginasComboBox(){
		
		//Cria a lista manualmente para manter ordenado, sen�o vai ordernar pelo label o que n�o se deseja neste caso.
		List<SelectItem> formatos = new ArrayList<SelectItem>();
		
		for (FormatoPaginaImpressaoEtiqueta formato : formatosPaginas) {
			formatos.add( new SelectItem(formato.getId(), formato.getLabel()));
		}
		return formatos;
	}
	
	
	/**
	 * Atualiza os valores do tipo de etiqueta escolhido pelo usu�rio.
	 * <p>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/servicos/formGerarEtiquetas.jsp
	 */
	public void atualizaValorEtiquetaEscolhido(ActionEvent event){
		// N�o faz nada, apenas para ser chamado via ajax e atualiza a tela.
	}
	
	////////////////////////// Telas de navega��o ////////////////////////////////
	
	/**
	 *   Redireciona para a p�gina de gera��o de etiquetas.
	 *<br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/servicos/formGerarEtiquetas.jsp
	 */
	public String telaGerarEtiquetas(){
		return forward(PAGINA_FORM_GERAR_ETIQUETAS);
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	// Set's e get's

	

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public Integer getPosicaoInicial() {
		return posicaoInicial;
	}

	public void setPosicaoInicial(Integer posicaoInicial) {
		this.posicaoInicial = posicaoInicial;
	}

	public List<TipoEtiquetaImpressaoBiblioteca> getTiposEtiquetaEscolhido() {
		return tiposEtiquetaEscolhido;
	}

	public void setTiposEtiquetaEscolhido(List<TipoEtiquetaImpressaoBiblioteca> tiposEtiquetaEscolhido) {
		this.tiposEtiquetaEscolhido = tiposEtiquetaEscolhido;
	}


	/** Seta o tipo de etiqueta que ser� impressa. */
	public void setValorEtiquetasEscolhidas(Integer tipoEtiquetas) {
		this.tiposEtiquetaEscolhido = new ArrayList<TipoEtiquetaImpressaoBiblioteca>();
		
		if(tipoEtiquetas == AMBAS_ETIQUETAS){
			this.tiposEtiquetaEscolhido.add(TipoEtiquetaImpressaoBiblioteca.CODIGO_BARRAS);
			this.tiposEtiquetaEscolhido.add(TipoEtiquetaImpressaoBiblioteca.LOMBADA);
		}
		
		if(tipoEtiquetas == ETIQUETA_CODIGO_BARRAS){
			this.tiposEtiquetaEscolhido.add(TipoEtiquetaImpressaoBiblioteca.CODIGO_BARRAS);
		}
		
		if(tipoEtiquetas == ETIQUETA_LOMBADA){
			this.tiposEtiquetaEscolhido.add(TipoEtiquetaImpressaoBiblioteca.LOMBADA);
		}
	}
	
	public Integer getValorEtiquetasEscolhidas() {
		if (tiposEtiquetaEscolhido == null)
			return -1;
		
		if(tiposEtiquetaEscolhido.contains(TipoEtiquetaImpressaoBiblioteca.CODIGO_BARRAS) 
				&& tiposEtiquetaEscolhido.contains(TipoEtiquetaImpressaoBiblioteca.LOMBADA)){
			return AMBAS_ETIQUETAS;
		}
		
		if(tiposEtiquetaEscolhido.contains(TipoEtiquetaImpressaoBiblioteca.CODIGO_BARRAS)){
			return ETIQUETA_CODIGO_BARRAS;
		}
		
		if(tiposEtiquetaEscolhido.contains(TipoEtiquetaImpressaoBiblioteca.LOMBADA)){
			return ETIQUETA_LOMBADA;
		}
		
		return -1;
	}
	
	public FormatoPaginaImpressaoEtiqueta getFormatoPaginaEscolhidoImpressao() {
		return formatoPaginaEscolhidoImpressao;
	}

	public void setFormatoPaginaEscolhidoImpressao(FormatoPaginaImpressaoEtiqueta formatoPaginaEscolhidoImpressao) {
		this.formatoPaginaEscolhidoImpressao = formatoPaginaEscolhidoImpressao;
	}

	public String getCodigoBarrasInicial() {
		return codigoBarrasInicial;
	}

	public void setCodigoBarrasInicial(String codigoBarrasInicial) {
		this.codigoBarrasInicial = codigoBarrasInicial;
	}

	public String getCodigoBarrasFinal() {
		return codigoBarrasFinal;
	}

	public void setCodigoBarrasFinal(String codigoBarrasFinal) {
		this.codigoBarrasFinal = codigoBarrasFinal;
	}

	public List<List<Map<String, Object>>> getLinhasDeEtiquetas() {
		return linhasDeEtiquetas;
	}

	public void setLinhasDeEtiquetas(List<List<Map<String, Object>>> linhasDeEtiquetas) {
		this.linhasDeEtiquetas = linhasDeEtiquetas;
	}


	public List<FormatoPaginaImpressaoEtiqueta> getFormatosPaginas() {
		return formatosPaginas;
	}

	public List<MaterialInformacional> getMateriaisGravadosGeracaoEtiqueta() {
		return materiaisGravadosGeracaoEtiqueta;
	}

	public void setMateriaisGravadosGeracaoEtiqueta(List<MaterialInformacional> materiaisGravadosGeracaoEtiqueta) {
		this.materiaisGravadosGeracaoEtiqueta = materiaisGravadosGeracaoEtiqueta;
	}

	public DataModel getDataModelMateriais() {
		return dataModelMateriais;
	}

	public void setDataModelMateriais(DataModel dataModelMateriais) {
		this.dataModelMateriais = dataModelMateriais;
	}

	public List<MaterialInformacional> getMateriaisGeracaoEtiqueta() {
		return materiaisGeracaoEtiqueta;
	}

	public void setMateriaisGeracaoEtiqueta(List<MaterialInformacional> materiaisGeracaoEtiqueta) {
		this.materiaisGeracaoEtiqueta = materiaisGeracaoEtiqueta;
	}

	public List<MaterialInformacional> getMateriaisAdicionadosGeracaoEtiqueta() {
		return materiaisAdicionadosGeracaoEtiqueta;
	}

	public void setMateriaisAdicionadosGeracaoEtiqueta(List<MaterialInformacional> materiaisAdicionadosGeracaoEtiqueta) {
		this.materiaisAdicionadosGeracaoEtiqueta = materiaisAdicionadosGeracaoEtiqueta;
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public Integer getCopiasCodigoBarras() {
		return copiasCodigoBarras;
	}

	public void setCopiasCodigoBarras(Integer copiasCodigoBarras) {
		this.copiasCodigoBarras = copiasCodigoBarras;
	}

	public Integer getCopiasLombada() {
		return copiasLombada;
	}

	public void setCopiasLombada(Integer copiasLombada) {
		this.copiasLombada = copiasLombada;
	}

	public boolean isApenasMeusMateriasPedentes() {
		return apenasMeusMateriasPedentes;
	}

	public void setApenasMeusMateriasPedentes(boolean apenasMeusMateriasPedentes) {
		this.apenasMeusMateriasPedentes = apenasMeusMateriasPedentes;
	}

	public List<CampoEtiquetaLombada> getCamposEtiquetaLombada() {
		return camposEtiquetaLombada;
	}

	public void setCamposLombada(List<CampoEtiquetaLombada> camposEtiquetaLombada) {
		this.camposEtiquetaLombada = camposEtiquetaLombada;
	}

	public CampoEtiquetaLombada getCampoEtiquetaLombadaSelecionado() {
		return campoEtiquetaLombadaSelecionado;
	}

	public void setCampoEtiquetaLombadaSelecionado(CampoEtiquetaLombada campoEtiquetaLombadaSelecionado) {
		this.campoEtiquetaLombadaSelecionado = campoEtiquetaLombadaSelecionado;
	}

	public DataModel getCamposEtiquetaLombadaDataModel() {
		return camposEtiquetaLombadaDataModel;
	}

	public void setCamposEtiquetaLombadaDataModel(DataModel camposEtiquetaLombadaDataModel) {
		this.camposEtiquetaLombadaDataModel = camposEtiquetaLombadaDataModel;
	}
	
}
