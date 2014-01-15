/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 01/08/2008
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.integracao.dto.biblioteca.InformacoesTombamentoMateriaisDTO;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.circulacao.jsf.NotasCirculacaoMBean;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DadosMateriaisTombados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoCadastraAnexoExemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoCadastraExemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoIncluiFasciculoNoAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisDeUmTituloMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaTituloCatalograficoMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.ExemplarByBibliotecaCodigoBarrasComparator;
import br.ufrn.sigaa.biblioteca.util.FasciculoByBibliotecaAnoCronologicoNumeroComparator;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 * Classe que ger�ncia a inclus�o de materiais (exemplares e fasc�culos) no acervo.
 *
 * @author Fred
 * @version 1.0 Cria��o da classe
 *          2.0 Refactor da classe para suportar 3 casos separados agora, inclus�o de exemplares, fasc�culos e anexo de exemplares
 *
 */
@Component(value="materialInformacionalMBean")
@Scope(value="request")
public class MaterialInformacionalMBean extends SigaaAbstractController<MaterialInformacional> implements PesquisarAcervoBiblioteca{
	
	/**
	 * pagina inclus�o de materiais, inclui a p�gina de inclus�o de exemplares e fasc�culos 
	 */
	public static final String PAGINA_INCLUSAO_MATERIAIS = "/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoMateriais.jsp";
	
	/**
	 * P�gina usado somento quando os bens n�o foram tombados para a mesma unidade de permiss�o 
	 * do catalogador, neste caso ele precisa confirmar os materiais selecionados, para diminuir 
	 * a probabilidade dele incluir materiais na biblioteca errada. 
	 */
	public static final String PAGINA_CONFIRMA_UNIDADE_DOS_MATERIAIS = "/biblioteca/processos_tecnicos/catalogacao/paginaConfirmaUnidadeDosMateriais.jsp";
	
	/**
	 * P�gina para inclus�o de anexos de exemplares
	 */
	public static final String PAGINA_INCLUSAO_ANEXO_EXEMPLAR = "/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoAnexo.jsp";
	
	
	/** Guarda o T�tulo ao qual os materiais ser�o adicionados */
	private TituloCatalografico titulo;
	
	
	/**
	 * Guarda os n�meros de tombamento ainda n�o inseridos no acervo do sigaa. No formato:  Map<IdBem, NumeroTombamento>
	 */
	private Map<Integer, Long> numerosPatrimonioNaoUsados;  
	 
	/**
	 * Os tipos dos tombamentos dos materiais comprados daquele titulo no formato: Map<IdBem, TipoTombamento>
	 */
	private Map <Integer, Short> tiposTombamentos;  
	
	/**
	 * Os bens comprados com as unidade para onde foram tombados  no formato:  Map<IdBem, IdUnidade> 
	 */
	private Map <Integer, Integer> unidadesTombamento; 
	 
	/**
	 *  Guarda a descricao da lista de exemplares que o bibliotecario n�o tem permiss�o de incluir materiais 
	 * <CodigoBarras, DescricaoUnidade>
	 */
	private List<DadosMateriaisTombados> dadosBensSemPermisaoUnidade; 
	
	/**
	 * Guarda a descri��o das unidade onde o usu�rio tem permiss�o de cataloga��o.
	 * Se o bem for tombado para uma unidade diferente ele ainda vai ter que confirma a inclus�o
	 * 
	 */
	private List<String> mensagemUnidadesPermisaoUsuario;
	
	
	/** Guarda o T�tulo pra ser mostrado ao usu�rio na p�gina; */
	private String tituloEmFormatoReferencia;
	
	
	/** 
	 * Se o usu�rio selecionou um t�tulo na tela de t�tulos n�o finalizados essa vari�vel
	 * vai ser setada. Nesse caso depois do usu�rio incluir materiais no t�tulo, vai poder voltar 
	 * para a p�gina de t�tulos n�o finalizados e escolher o pr�ximo para trabalhar na cataloga��o.
	 */
	private boolean abilitaBotaoVoltarTelaTitulosNaoFinalizados = false;
	
	
	/** 
	 * Guarda as Informa��es do exemplar inclu�das pelo bibliotec�rio no formul�rio de inclus�o de exemplares. 
	 */
	private Exemplar exemplar;
	
	/** 
	 *  Guarda os ids Escolhidos pelo usu�rio na p�gina com o selectManyList, quando a cataloga��o � 
	 *  com tombamento, onde as informa��es dos c�digos de barras vem do n�mero de tombamento doSIPAC.
	 */
	private List<Integer> idsExemplaresEscolhidos;
	
	/** 
	 *  Guarda os ids Escolhidos pelo usu�rio na p�gina com o selectManyList, quanto
	 *  as formas de documento que um material possui. 
	 */
	private List<String> idsFormasDocumentoEscolhidos;
	
	/**
	 * Guarda os exemplares j� cadastrados do T�tulo escolhido para mostra ao usu�rio
	 */
	private List<Exemplar> exemplaresDoTitulo;
	
	
	/** Guarda os exemplares selecionados para serem inclu�dos no acervo pelo usu�rio */
	private List<Exemplar> exemplares = new ArrayList<Exemplar>();
	
	/**
	 * Guarda os ids dos bens dos exemplares criados pelo bibliotec�rio, para depois da inclus�o 
	 * remover os bens da lista de <code>numerosPatrimonioNaoUsados </code>
	 */
	private List<Integer> idBensExemplaresCriados = new ArrayList<Integer>();
	
	
	/////////////////// Informa��es quando � inclus�o de fasc�culos ///////////////////////////////
	
	
	/**
	 *  Traz todas as assinatura que j� pertencem ao T�tulo ou que n�o pertencem a nenhum outro t�tulo,
	 * para o usu�rio escolher em qual assinatura deseja incluir o fasc�culo. 
	 * Exemplar: Um T�tulo: "veja" pode ter v�rias assinatura espalhadas por v�rias bibliotecas.
	 **/
	private List<Assinatura> assinaturasPossiveisInclusaoFasciculo; 
	
	/**
	 * Guarda as assintura que est�o sem T�tulo. (A t�tulo vai ser associado caso o usu�rio inclui o fasc�culo) 
	 */
	private List<Assinatura> assinaturasSemTitulo;
	
	/**
	 *  guarda as assintura que j� est�o associadas ao T�tulo escolhido
	 */
	private List<Assinatura> assinaturasDoTituloSelecionado;
	
	/**
	 *  assinatura que o usu�rio selecionou para incluir o fasc�culo
	 */
	private Assinatura assinaturaSelecionada;   
	
	/**
	 *  os fasc�culo cuja chegada foi registrada mais n�o foram inclu�do no acervo ainda.
	 */
	private List<Fasciculo> fasciculosDaAssinaturaNaoInclusos; 
	
	/**
	 * O fasc�culo selecionado para inclus�o no acervo. (completar as suas informa��es e ele ficar vis�vel para o usu�rio.)
	 */
	private Fasciculo fasciculoSelecionado;
	
	/**
	 * Indica na p�gina se a assinatura foi criada no setor de compras
	 */
	private boolean assinaturaCadastrada = false;
	
	
	
	/*  Usado na parte de inclus�o de anexos */
	
	/**
	 * Exemplar principal do anexo que vai ser inclu�do
	 */
	private Exemplar exemplarPrincipal;
	
	/**
	 * Exemplar anexo que vai ser inclu�do
	 */
	private Exemplar exemplarAnexo;
	
	/**
	 * O t�tulo do anexo, pode ser diferente do t�tulo do principal
	 */
	private TituloCatalografico tituloDoAnexo;
	
	/**
	 * Informa��es do t�tulo do anexo, para mostrar na p�gina.
	 */
	private String tituloDoAnexoEmFormatoReferencia;
	
	/*  ******************************************************** */
	
	/**  Em qual opera��o se est� no momento. */
	private int operacao = 0;
	
	/**  Opera��o onde o usu�rio vai incluir um anexo de um exemplar a partir da p�gina de busca. */
	private static final int INCLUIR_EXEMPLARES = 1;
	/**  Opera��o onde o usu�rio vai incluir um anexo de um exemplar a partir da p�gina de busca. */
	private static final int INCLUIR_FASCICULOS = 2;
	/**  Opera��o onde o usu�rio vai incluir um anexo de um exemplar a partir da p�gina de busca. */
	private static final int INCLUR_ANEXO_A_PARTIR_BUSCA = 3;
	
	
	/**
	 * serve para saber para qual tela voltar no bot�o voltar da p�gina
	 */
	private boolean incluindoMateriaisApartirTelaBusca = false;
	
	
	/**
	 * serve para saber para qual tela voltar no bot�o voltar da p�gina
	 */
	private boolean incluindoMateriaisApartirTelaCatalogacao = false;
	
	
	/**
	 * Para saber se est� no caso de uso de inclus�o de exemplares sem tombamento. 
	 */
	private boolean incluindoMateriaisSemTombamento = false;
	
	
	/** Flag que indica se o usu�rio escolheu mostrar ou n�o as informa��es dos outras exemplares do T�tulo.<br/>
	 * O usu�rio possui a op��o de esconder, porque quando a quantidade de exemplare era grande, a visualiza��o
	 * do formul�rio de inclus�o de um novo exemplar ficava um pouco tumultuada. */
	private boolean mostrarExemplaresDoTitulo = true;
	
	/** 
	 *  Usado se o usu�rio decidir trocar o T�tulo de um anexo de um exemplar <br/>
	 *  
	 *  Neste caso, como vai chamar a tela de pesquisa de t�tulo com outra opera��o, precisa guardar 
	 *  qual a opera��o anterior, e quando o usu�rio voltar da tela de pesquisa do t�tulo do anexo,
	 *  retorna a opera��o anterior da tela de pesquisa. 
	 */
	private int operacaoPesquisaAnterior = -1;
	
	/**
	 * Construtor padr�o do bean
	 * 
	 * @throws DAOException
	 */
	public MaterialInformacionalMBean(){
	
	}
	
	/**
	 *    M�todo que inicia o caso de uso de adi��o de novos exemplares ao acervo.
	 *    
	 *    M�todo n�o invocado por jsp
	 * 
	 * @param paginaRetornoErro  indica para onde deve retornar se der erro. � necess�rio pois
	 *          esse m�todo � chamado de v�rios lugares e n�o tem como saber para onde deve voltar.   
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarParaAdicaoExemplares(InformacoesTombamentoMateriaisDTO infomacoesTituloCompra, String paginaRetornoErro) throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		limpaDadosExemplar();
		
		
		this.titulo = new TituloCatalografico(infomacoesTituloCompra.idTituloCatalograficoSigaa);
		this.numerosPatrimonioNaoUsados = infomacoesTituloCompra.numerosPatrimonioNaoUsados;
		this.unidadesTombamento = infomacoesTituloCompra.unidadesTombamento;
		this.tiposTombamentos = infomacoesTituloCompra.tiposTombamentos;
		
		ExemplarDao dao = getDAO(ExemplarDao.class);
		
		this.titulo = dao.refresh(titulo);
		
		exemplar.setNumeroChamada(getDAO(TituloCatalograficoDao.class).encontraNumeroChamadaTitulo(titulo.getId()));
		
		exemplaresDoTitulo = dao.findExemplaresAtivosDeUmTitulo(titulo.getId());
		
		Collections.sort(exemplaresDoTitulo, new ExemplarByBibliotecaCodigoBarrasComparator());
		
		tituloEmFormatoReferencia = "N� do Sistema <strong>"+titulo.getNumeroDoSistema()+"</strong> - "+new FormatosBibliograficosUtil().gerarFormatoReferencia(titulo, true);
		
		operacao = INCLUIR_EXEMPLARES;
		
		prepareMovimento(SigaaListaComando.CADASTRA_EXEMPLAR);
		
		if(getQuanidadeMateriaisNaoUsados() == 0 ){
			addMensagemWarning("Todos os materiais para o T�tulo \" "+infomacoesTituloCompra.getTitulo()+" \"  do Termo de Responsabilidade N� "
					+infomacoesTituloCompra.getDescricaoTermoResponsabibliodade()+" foram inclu�dos no acervo.");
		}
			
		return telaIncluirNovoMaterial();
		
	}
	
	
	/**
	 *    Final do caso de uso da cataloga��o de exemplares n�o tombados.
	 *    
	 *    Aqui o sistema simplesmente vai exibir uma tela com campo para o usu�rio digitar o que 
	 *    quiser no c�digo de barras do exemplar.
	 *    
	 *    M�todo n�o invocado por jsp.
	 *    
	 * @param paginaRetornoErro  indica para onde deve retornar se der erro. � necess�rio pois
	 *          esse m�todo � chamado de v�rios lugares e n�o tem como saber para onde deve voltar.   
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarParaAdicaoExemplaresNaoTombados(TituloCatalografico tituloPassado,  String paginaRetornoErro) throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_SEM_TOMBAMENTO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		limpaDadosExemplar();
		
		ExemplarDao dao = getDAO(ExemplarDao.class);
		TituloCatalograficoDao tituloDao = getDAO(TituloCatalograficoDao.class);
		
		
		this.titulo = tituloDao.findTituloByIdInicializandoDados(tituloPassado.getId());
		
		exemplar.setNumeroChamada(tituloDao.encontraNumeroChamadaTitulo(titulo.getId()));
		
		exemplaresDoTitulo = dao.findExemplaresAtivosDeUmTitulo(titulo.getId());
		
		Collections.sort(exemplaresDoTitulo, new ExemplarByBibliotecaCodigoBarrasComparator());
		
		tituloEmFormatoReferencia = "N� do Sistema <strong>"+titulo.getNumeroDoSistema()+"</strong> - "+ new FormatosBibliograficosUtil().gerarFormatoReferencia(titulo, true);
		
		operacao = INCLUIR_EXEMPLARES;
		
		incluindoMateriaisSemTombamento = true;
		
		prepareMovimento(SigaaListaComando.CADASTRA_EXEMPLAR);
		
		return telaIncluirNovoMaterial();
	}
	

	/**
	 *      Inicia o caso de uso de incluir novos fasc�culos, nesse caso vai ser buscada a assinatura 
	 *  a partir do n�mero de tombamento e os fasc�culos que foram registrados mas n�o inclu�dos ainda pelo
	 *  setor de cataloga��o.
	 *  
	 *      <p>OBS.: A inclus�o de um fasc�culo s� pode ocorrer se ele for registrado pelo setor de compras.</p> 
	 *
	 *      M�todo n�o invocado por jsp
	 * 
	 * @param infomacoesTituloCompra
	 * @return
	 */
	public String iniciarParaAdicaoFasciculos(InformacoesTombamentoMateriaisDTO infomacoesTituloCompra)throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		addMensagemErro("O T�tulo escolhido foi um T�tulo cujo formato de matrial � de PERI�DICO, "
				+" logo todos dos fasc�culo devem ser adicionados a ele pela op��o Cataloga��o sem Tombamento, "
				+"pois diferente dos exemplares, nem os fasc�culo, nem as assinatura s�o tombados no patrim�nio da institui��o at� o presente momento.");
		
		return null;
		
	}
	
	
	/**
	 * 
	 * Caso de uso de incluir fasc�culos em assinaturas n�o tombadas.
	 * 
	 * Nesse caso, se o t�tulo n�o possuir uma assinatura de fasc�culos ainda,  o usu�rio vai 
	 * precisar selecionar a assinatura do fasc�culo primeiro. Assim que selecionar, 
	 * redireciona para a p�gina de inclus�o de fasc�culo. Caso j� possu�a uma assinatura, vai para a 
	 * p�gina de incluir fasc�culos diretamente. 
	 * 
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String iniciarParaAdicaoFasciculosNaoTombados(TituloCatalografico tituloPassado)throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		TituloCatalograficoDao tituloDao = getDAO(TituloCatalograficoDao.class);
		
		// this.titulo = tituloDao.findTituloByIdInicializandoDados(tituloPassado.getId());
		this.titulo = tituloDao.findByPrimaryKey(tituloPassado.getId(), TituloCatalografico.class); // ficou mais r�pido assim
		
		tituloEmFormatoReferencia = "N� do Sistema <strong>"+titulo.getNumeroDoSistema()+"</strong> - "+ new FormatosBibliograficosUtil().gerarFormatoReferencia(titulo, true);
		
		operacao = INCLUIR_FASCICULOS;
		
		incluindoMateriaisSemTombamento = true;
		
		AssinaturaDao assinaturaDao = getDAO(AssinaturaDao.class);
		
		// Se � administrador geral, traz assinatura de todas as unidades 
		if(isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			
			assinaturasPossiveisInclusaoFasciculo = assinaturaDao.findAssinaturasAtivasPossiveisInclusaoFasciculos(this.titulo.getId(), null, null, null, null);
		}else{ // se � s� catalogador vai poder incluir apenas na assinatura de sua unidade
			
			List<Integer> idBibliotecas = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario(getUsuarioLogado(), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS) );
			
			if(idBibliotecas == null || idBibliotecas.size() == 0){
				addMensagemErro("Usu�rio n�o tem permiss�o de cataloga��o.");
				return null;
			}
			
			assinaturasPossiveisInclusaoFasciculo = assinaturaDao.findAssinaturasPossiveisInclusaoFasciculosByUnidadeDestino(this.titulo.getId(), idBibliotecas );
		}
		
		assinaturasSemTitulo = new ArrayList<Assinatura>();
		assinaturasDoTituloSelecionado = new ArrayList<Assinatura>();
		
		// Separa em duas lista para melhorar a visualiza��o na tela para o usu�rio.
		for (Assinatura assinatura : assinaturasPossiveisInclusaoFasciculo) {
			
			if(assinatura.getTituloCatalografico() == null){
				assinaturasSemTitulo.add(assinatura);
			}else{
				assinaturasDoTituloSelecionado.add(assinatura);
			}
			
		}
		
		if(assinaturasPossiveisInclusaoFasciculo.size() == 0){
			addMensagemWarning("N�o existem assinaturas dispon�veis da sua unidade para inclus�o de fasc�culos.");
		}
			
		
		return telaIncluirNovoMaterial();

	}
	
	
	
	/**
	 * M�todo chamado quando o usu�rio escolhe a assinatura que pode ter fasc�culos que pertencem ao
	 * t�tulo.<br/>
	 *  O sistema vai mostrar os fasc�culo que n�o foram inclu�dos no acervo ainda para a 
	 * assinatura selecionada.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInformacoesFasciculos.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public String visualizarFasciculosNaoIncluidosDaAssinatura() throws DAOException{
		
		int idAssinaturaSelecionada = getParameterInt("idAssinaturaSelecionada");
		
		for (Assinatura a : assinaturasPossiveisInclusaoFasciculo) {
			
			if(a.getId() == idAssinaturaSelecionada){
				assinaturaSelecionada = a;
				break;
			}
		}
		
		FasciculoDao fasciculoDao = getDAO(FasciculoDao.class);
		fasciculosDaAssinaturaNaoInclusos = fasciculoDao.findFasciculosRegistradosDaAssinatura(assinaturaSelecionada.getId());
		
		fasciculoSelecionado = null;
		
		return telaIncluirNovoMaterial();
		
	}
	
		
	/**
	 * M�todo chamado quando o usu�rio seleciona um fasc�culo apenas registrado e agora v�o 
	 * ser inclu�das as suas informa��es.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInformacoesFasciculos.jsp
	 * 
	 * @throws ArqException 
	 */
	public String selecionarFasciculoParaInclusao() throws ArqException{
		
		TituloCatalograficoDao dao = null;
		
		try{
			dao = getDAO(TituloCatalograficoDao.class);
			
			fasciculoSelecionado = dao.findByPrimaryKey(getParameterInt("idFasciculoSelecionadoInclusao"), Fasciculo.class);
			
			fasciculoSelecionado.setNumeroChamada(dao.encontraNumeroChamadaTitulo(titulo.getId()));
			
			assinaturaSelecionada = fasciculoSelecionado.getAssinatura();
			
			fasciculoSelecionado.setAssinatura(assinaturaSelecionada);
			fasciculoSelecionado.setBiblioteca( assinaturaSelecionada.getUnidadeDestino() );
			fasciculoSelecionado.setColecao( new Colecao(-1));
			fasciculoSelecionado.setSituacao(new SituacaoMaterialInformacional( -1));
			fasciculoSelecionado.setStatus(new StatusMaterialInformacional(-1));
			fasciculoSelecionado.setTipoMaterial(new TipoMaterial(-1));
			
		}finally{
			if(dao != null) dao.close();
		}
		
		
		
		return telaIncluirNovoMaterial();
	}

	
	/**
	 *  <p> M�todo que monta a lista de materiais escolhidos pelo usu�rio na tela de inclus�o de materiais. </p>
	 *  <p> <strong>Caso 1:</strong> Se todos os materias foram catalogados para a mesma unidade onde o bibliotec�rio tem permiss�o de cataloga��o,
	 *  chama diretamento o m�todo <code>confirmaCadastroExemplar()</code> inclu�-los no banco. </p>
	 *  <p> <strong>Caso 2:</strong> Se algum material n�o foi catalogado para a mesma unidade onde o bibliotec�rio tem permiss�o de cataloga��o,
	 *  retireciona para tela onde o bibliotec�rio vai ter que confirma a inclus�o dos materiais.</p> 
	 *  <p> Esse procedimento � para evitar que bibliotec�rios 
	 *  inclu�o materiais de bibliotecas que n�o sejam a sua.</p>
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina:   /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplar.jsp
	 * 
	 * @return
	 * @throws Exception
	 */
	public String cadastrarExemplar() throws Exception{
		
		if(usuarioPreencheuDadosExemplarCorretamente(exemplar, false)){
			
			PermissaoDAO dao = null;
			exemplar.popularFormasDocumento(getIdsFormasDocumentoEscolhidos());
			
			try {
				
				if( ! incluindoMateriaisSemTombamento){
					
					mensagemUnidadesPermisaoUsuario = new ArrayList<String>(); 
					dadosBensSemPermisaoUnidade = new ArrayList<DadosMateriaisTombados>();
					exemplares = new ArrayList<Exemplar>();
					idBensExemplaresCriados = new ArrayList<Integer>();
					
					boolean possuiMaterialEmOutraUnidade = false;
					
					dao = getDAO(PermissaoDAO.class);
					
					List<Permissao> permissoes = dao.findPermissoesByPapeis(getUsuarioLogado()
							, new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO)
							,new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS));
					
					Permissao[] pesmissoesUsuario = new Permissao[permissoes.size()];
					
					int quantidadesPermissao = 0;
					
					for (Permissao permissao : permissoes) {
						Unidade unidadePermissaoUsuario = dao.findByPrimaryKey(permissao.getUnidadePapel().getId(), Unidade.class, "hierarquia", "codigo", "nome"); //carrega os dados da unidades
						permissao.setUnidadePapel(unidadePermissaoUsuario);
						
						mensagemUnidadesPermisaoUsuario.add(unidadePermissaoUsuario.getCodigoFormatado()+" - "+unidadePermissaoUsuario.getNome());
					
						pesmissoesUsuario[quantidadesPermissao] = permissao;
						
						quantidadesPermissao++;
					}
					int qtd = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.QUANTIDADE_MAXIMA_EXEMPLARES_INCLUIDA_POR_VEZ);
					if(idsExemplaresEscolhidos.size() > qtd){
						addMensagemErro("A quantidade m�xima permitida para ser selecionada � de "+qtd+" n�meros de tombamento por vez.");
						return null;
					}
					
					/// Salva um exemplar para cada n�mero de patrim�nio escolhido pelo usu�rio na tela
					for (int posicao = 0; posicao< idsExemplaresEscolhidos.size(); posicao++) {
						
						Exemplar exemplarTemp = new Exemplar();
						
						// Apesar de idsExemplaresEscolhidos ser uma lista de Integer estava vindo uma lista
						// de Strings ele n�o convertia direto
						String idBemString = (String.valueOf(idsExemplaresEscolhidos.get(posicao)));
						Integer idBem = Integer.parseInt(idBemString);
						
						idBensExemplaresCriados.add(idBem);
						
						exemplarTemp.setIdBem(idBem);
						exemplarTemp.setNumeroPatrimonio(numerosPatrimonioNaoUsados.get(idBem)); // pega no mapa o n�mero do patrim�nio
						exemplarTemp.setTipoTombamento(tiposTombamentos.get(idBem)); // pega no mapa o n�mero do patrim�nio
					
						// Copia as informa��es do exemplar que foram preenchidas na tela
						exemplarTemp.setStatus( exemplar.getStatus()); 
						exemplarTemp.setColecao(exemplar.getColecao());
						exemplarTemp.setBiblioteca(exemplar.getBiblioteca());
						exemplarTemp.setTipoMaterial(exemplar.getTipoMaterial());
						exemplarTemp.setSituacao(exemplar.getSituacao()); 
						exemplarTemp.setTituloCatalografico(titulo);
						exemplarTemp.setNumeroChamada(exemplar.getNumeroChamada());
						exemplarTemp.setSegundaLocalizacao(exemplar.getSegundaLocalizacao());
						exemplarTemp.setNotaConteudo(exemplar.getNotaConteudo());
						exemplarTemp.setNotaTeseDissertacao(exemplar.getNotaTeseDissertacao());
						exemplarTemp.setNotaGeral(exemplar.getNotaGeral());
						exemplarTemp.setNotaUsuario(exemplar.getNotaUsuario());
						exemplarTemp.setNumeroVolume(exemplar.getNumeroVolume());
						exemplarTemp.setTomo(exemplar.getTomo());
						
						// Para exemplares tombados o n�mero do patrim�nio � igual ao c�digo de barras.
						exemplarTemp.setCodigoBarras( String.valueOf(exemplarTemp.getNumeroPatrimonio()));
						
						
						exemplares.add(exemplarTemp);
						
						int idUnidadeBem = unidadesTombamento.get(idBem);
						
						Unidade unidadeDeTombamentoBem = getGenericDAO().findByPrimaryKey(idUnidadeBem, Unidade.class, "id", "nome", "codigo");
						
						
						if(! bibliotecarioTemPermisaoIncluirExemplaresDaUnidadeTombamentoBem(unidadeDeTombamentoBem, pesmissoesUsuario)){
							dadosBensSemPermisaoUnidade.add( new DadosMateriaisTombados(exemplarTemp.getCodigoBarras(), unidadeDeTombamentoBem.getCodigoFormatado()+" - "+unidadeDeTombamentoBem.getNome()));
						
							possuiMaterialEmOutraUnidade = true;
						}
						
					}	
				
					
					if(possuiMaterialEmOutraUnidade){
						addMensagemErro(" Algums materiais selecionados foram tombados para outras unidades");
						return telaConfirmaInclusaoExemplaresOutraUnidades();
					}else
						return confirmaCadastroExemplar();
					
				}else{
					
					exemplares = new ArrayList<Exemplar>();
					
					exemplar.setTituloCatalografico(titulo);
					
					if(StringUtils.notEmpty(exemplar.getCodigoBarras())){
						exemplar.setCodigoBarras(exemplar.getCodigoBarras().toUpperCase()); // Coloca para UPPER CASE para evitar erros de digita��o.
					}
					
					exemplares.add(exemplar);
					
					return confirmaCadastroExemplar();
				}
				        
			
			} catch (NegocioException ne){
				ne.printStackTrace();
				addMensagens(ne.getListaMensagens());
				return null;
			}finally{
				if(dao != null) dao.close();
			}
			
		}else{
			return null; // Fica na mesma p�gina mostrando as mensagem de erro
		}
		
	}
	
	/**
	 * 
	 * <p> M�todo que cadastra o exemplar e logo ap�s redireciona para a caso de uso de incluir nota de 
	 * circula��o para incluir uma nota no exemplar que acabou de ser cadastrado. </p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplar.jsp</li>
	 *   </ul>
	 *
	 *   
	 *
	 * @return
	 * @throws Exception
	 */
	public String incluirNotaCirculacaoExemplar() throws Exception{
	
		GenericDAO dao = null;
		
		try{
	
			cadastrarExemplar();
	
			if(! hasOnlyErrors()){ // Se n�o tem erros � porque conseguir cadastrar o exemplar no acervo
			
				 dao = getGenericDAO();
				
				//////////////////////////////////////////////////////////////////////////////////////
				// Inicializa a unidade e situa��o dos materiais antes de pode chamar o Caso de uso de nota de Circula��o //
				//////////////////////////////////////////////////////////////////////////////////////
				
				List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
				
				for (Exemplar temp : exemplares) {
					
					Exemplar exemplarTemp = new Exemplar();
					exemplarTemp.setId( temp.getId());
					exemplarTemp.setCodigoBarras( temp.getCodigoBarras());
					exemplarTemp.setAtivo( temp.isAtivo());
					
					exemplarTemp.setSituacao( dao.findByPrimaryKey(temp.getSituacao().getId(), SituacaoMaterialInformacional.class, "id", "situacaoDisponivel", "situacaoEmprestado", "situacaoDeBaixa")  );
					
					Biblioteca biblioteca = new Biblioteca();
					biblioteca.setUnidade( new Unidade(temp.getBiblioteca().getUnidade().getId()));   // S� para n�o passsar o objeto proxy do hibernate
					exemplarTemp.setBiblioteca( biblioteca );
					
					materiais.add( exemplarTemp );
				}
				
				NotasCirculacaoMBean mbean = getMBean("notasCirculacaoMBean");
				return mbean.iniciarConfigurandoDiretamenteMaterialIncluirNota(materiais, PesquisaTituloCatalograficoMBean.PAGINA_PESQUISA_TITULO);
				
			}else
				return null; // Caso d� algum erro do cadastro do exemplar, fica na mesma p�gina.
			
		}catch (Exception ex) {
			ex.printStackTrace();
			addMensagemErro(ex.getMessage());
			return null;  // Caso d� alguma exce��o do cadastro do exemplar, fica na mesma p�gina.
		}finally{
			if(dao != null )  dao.close(); 
		}
	}
	
	
	
	/**
	 *   <p>Realiza o cadastro dos exemplares no sistema.</p>
	 *   <p>Caso o tombamento dos exemplares tenham sido para a mesma unidade da permiss�o do usu�rio esse m�todo � chamado 
	 *   diretamente do m�todo <code>cadastrarExemplar()</code>, caso contr�rio, esse m�todo � chamado da p�gina declarada abaixo,
	 *   depois que o usu�rio confirmar que realmente deseja incluir os exemplares no acervo, mesmo ele tendo sido tombados para 
	 *   outra unidade. </p>
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina:   /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaConfirmaUnidadeDosMateriais.jsp
	 * 
	 * @return
	 * @throws Exception
	 */
	public String confirmaCadastroExemplar() throws Exception{
		
		/* Chama o processador com a lista de exemplares escolhidos pelo usu�rio, essa lista � configurada no m�todo anterior: 
		 * cadastrarExemplar()
		 */
		
		MovimentoCadastraExemplar mov = new MovimentoCadastraExemplar(exemplares);
		
		mov.setCodMovimento(SigaaListaComando.CADASTRA_EXEMPLAR);
		
		execute(mov);
		
		
		if( ! incluindoMateriaisSemTombamento){
		
			for (Integer idBemExemplarCriado : idBensExemplaresCriados) {
				numerosPatrimonioNaoUsados.remove(idBemExemplarCriado); // remove da p�gina para o usu�rio
			}
			
			// Depois de cadastrar se prepara para adicionar novos       
			
			InformacoesTombamentoMateriaisDTO info = new InformacoesTombamentoMateriaisDTO();
			info.idTituloCatalograficoSigaa = this.titulo.getId();
			info.numerosPatrimonioNaoUsados = this.numerosPatrimonioNaoUsados;
			info.unidadesTombamento = this.unidadesTombamento;
			
			addMensagemInformation("Exemplar(es) inclu�do(s) com sucesso");
			
			return iniciarParaAdicaoExemplares(info, PAGINA_INCLUSAO_MATERIAIS);
			
		}else{
			addMensagemInformation("Exemplar inclu�do com sucesso");
			
			return iniciarParaAdicaoExemplaresNaoTombados(titulo, PAGINA_INCLUSAO_MATERIAIS);
		}
		
		
		
	}
	
	
	/**
	 * 
	 * <p> M�todo que verifica se a hierarquia da unidade de tombamento do bem possui a mesma unidade da biblioteca onde o usu�rio tem permiss�o de cataloga��o. </p>
	 * <p> Caso n�o seja a mesma unidade o sistema mostrar� um aviso para o usu�rio. </p> 
	 * <p> Este m�todo serve para impedir que bibliotec�rios incluam na sua biblioteca bens tombados para outras bibliotecas. 
	 * J� que neste ponto o exemplar ainda n�o est� em uma biblioteca, n�o tem como realizar o controle de altera��o pela biblioteca.</p> 
	 *
	 * @param idUnidadeTombamentoBem
	 * @throws DAOException 
	 */
	private boolean bibliotecarioTemPermisaoIncluirExemplaresDaUnidadeTombamentoBem(Unidade unidadeTombamentoBem, Permissao... permisoesCatalogacaoUsuario) throws DAOException{
		
		if(isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)) /// Administrador Geral insere em qualquer unidade.
			return true;
		
		/* Verifica se alguma unidade das PERMISS�ES DE CATALOGA��O do bibliotec�rio EST� NA HIERARQUIA da unidade DO BEM
		 * Apesar do nome do m�todo ser isUnidadeFilha(), ele verifica se a unidade est� na hierarquina
		 */
		for (Permissao permissaoCatalogacaoUsuario : permisoesCatalogacaoUsuario) {
			if( permissaoCatalogacaoUsuario.getUnidadePapel().isUnidadeFilha( unidadeTombamentoBem.getId() ) ){
				return true;
			}
		}
		
		return false;
			
	}
	
	
	/**
	 * <p>M�todo que realiza a a��o de incluir um f�sc�culo do acervo para o sistema.</p>
	 * 
	 * <p>Atualiza o fasc�culo com as informa��es digitadas pelo usu�rio de cataloga��o. A cria��o 
	 * desse fasc�culo j� foi feita pelo usu�rio de aquisi��o na hora que ele registrou a sua chegada.</p> 
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaGerenciamentoMaterial.jsp
	 * 
	 * @return
	 * @throws Exception
	 */
	public String atualizarFasciculo() throws Exception{
		
		if(usuarioPreencheuDadosFasciculoCorretamente()){
		
			try{
				
				assinaturaSelecionada = getGenericDAO().refresh(assinaturaSelecionada);
				assinaturaSelecionada.setTituloCatalografico(titulo);
				fasciculoSelecionado.setAssinatura(assinaturaSelecionada);
				
				fasciculoSelecionado.popularFormasDocumento(getIdsFormasDocumentoEscolhidos());
				MovimentoIncluiFasciculoNoAcervo mov = new MovimentoIncluiFasciculoNoAcervo(fasciculoSelecionado, assinaturaSelecionada, titulo);
				mov.setCodMovimento(SigaaListaComando.INCLUI_FASCICULO_ACERVO);
				
				prepareMovimento(SigaaListaComando.INCLUI_FASCICULO_ACERVO);
				
				execute(mov);
			
				addMensagemInformation("Fasc�culo inclu�do com sucesso");
				
				limpaDadosFasciculo();
				
				return telaIncluirNovoMaterial();
				
			}catch(NegocioException ne){
				addMensagens(ne.getListaMensagens());
				return telaIncluirNovoMaterial();
			}
			
		}else{
			return null;  // fica na mesma tela mostrando as mensagens de erro
		}
	}

	
	
	/**
	 * 
	 * <p> M�todo que inclui o fasc�culo no acervo e logo ap�s redireciona para a caso de uso de incluir nota de 
	 * circula��o para incluir uma nota no fasc�culo que acabou de ser inclu�do. </p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplar.jsp</li>
	 *   </ul>
	 *
	 *   
	 *
	 * @return
	 * @throws Exception
	 */
	public String incluirNotaCirculacaoFasciculo() throws Exception{
	
		GenericDAO dao = null;
		
		try{
	
			///////////////////////////////////////////////////////////////////////////////
			// � preciso guardar os dado do fasc�culo antes de chamar o m�todo de atualizar
			///////////////////////////////////////////////////////////////////////////////
			
			int idFasciculo = fasciculoSelecionado.getId();
			String codigoBarras = fasciculoSelecionado.getCodigoBarras();
			boolean fasciculoAtivo = fasciculoSelecionado.isAtivo();
			int idSituacaoFasciculo = fasciculoSelecionado.getSituacao().getId();
			int idUnidadeFasciculo = fasciculoSelecionado.getBiblioteca().getUnidade().getId();
			
			atualizarFasciculo();
	
			if(! hasOnlyErrors()){ // Se n�o tem erros � porque conseguir cadastrar o exemplar no acervo
			

				//////////////////////////////////////////////////////////////////////////////////////
				// Inicializa a unidade e situa��o dos materiais antes de pode chamar o Caso de uso de nota de Circula��o //
				//////////////////////////////////////////////////////////////////////////////////////
				
				dao = getGenericDAO();
				
				List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
				
				Fasciculo fasciculoTemp = new Fasciculo();
				fasciculoTemp.setId( idFasciculo );
				fasciculoTemp.setCodigoBarras( codigoBarras );
				fasciculoTemp.setAtivo( fasciculoAtivo );
				
				fasciculoTemp.setSituacao( dao.findByPrimaryKey(idSituacaoFasciculo, SituacaoMaterialInformacional.class, "id", "situacaoDisponivel", "situacaoEmprestado", "situacaoDeBaixa")  );
				
				Biblioteca biblioteca = new Biblioteca();
				biblioteca.setUnidade( new Unidade(idUnidadeFasciculo) );   
				fasciculoTemp.setBiblioteca( biblioteca );
				
				materiais.add( fasciculoTemp );
				
				NotasCirculacaoMBean mbean = getMBean("notasCirculacaoMBean");
				return mbean.iniciarConfigurandoDiretamenteMaterialIncluirNota(materiais, PesquisaTituloCatalograficoMBean.PAGINA_PESQUISA_TITULO);
				
			}else
				return null; // Caso d� algum erro do cadastro do exemplar, fica na mesma p�gina.
			
		}catch (Exception ex) {
			ex.printStackTrace();
			addMensagemErro(ex.getMessage());
			return null;  // Caso d� alguma exce��o do cadastro do exemplar, fica na mesma p�gina.
		}finally{
			if(dao != null )  dao.close(); 
		}
	}
	
	
	/**
	 * M�todo que redireciona para outra p�gina para o usu�rio incluir o anexo do exemplar selecionado
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaGerenciamentoMaterial.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String preparaIncluirAnexo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.CADASTRA_ANEXO_EXEMPLAR);
		
		int idExemplarPrincipal = getParameterInt("idExemplarPrincipal");
		
		exemplarPrincipal = exemplaresDoTitulo.get(exemplaresDoTitulo.indexOf(new Exemplar(idExemplarPrincipal)));
		
		iniciaExemplarAnexo();
		
		return telaIncluirAnexoExemplar();
	}
	
	
	
	
	
	/**
	 * M�todo chamado para incluir um anexo a um exemplar a partir da p�gina de visualiza��o
	 * das informa��es dos exemplares de um t�tulo
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaDetalhesMateriais.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String preparaIncluirAnexoApartirDaPesquisa() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.CADASTRA_ANEXO_EXEMPLAR);
		
		operacao = INCLUR_ANEXO_A_PARTIR_BUSCA;
		
		int idExemplarPrincipal = getParameterInt("idExemplarPrincipal");
		
		exemplarPrincipal = new Exemplar(idExemplarPrincipal);
		
		iniciaExemplarAnexo();
		
		return telaIncluirAnexoExemplar();
	}
	
	
	
	
	
	/**
	 * Chama a p�gina de pesquisa para o usu�rio escolher o T�tulo do anexo
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoAnexo.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String abrirTelaSubstituicaoTitulo() throws SegurancaException, DAOException{
		
		PesquisaTituloCatalograficoMBean mBean = getMBean("pesquisaTituloCatalograficoMBean");
		
		/* Precisa guardar a opera��o anterior da p�gina da pesquisa de t�tulo   //
		 * porque a opera��o vai ser substitu�da pela opera��o de pesquisar o T�tulo do anexo     //
		 * depois de buscar o t�tulo do anexo, tem que voltar para a opera��o anterior caso o 
		 * usu�rio utilize o bot�o voltar
		 */
		//operacaoPesquisaAnterior = mBean.getOperacao();
		
		mBean.limpaResultadoPesquisa();
		return mBean.iniciarPesquisaSelecionarTitulo(this);
	}
	
	
	
	
	
//	/**
//	 * M�todo que substitui o T�tulo do anexo por outro diferente do T�tulo do exemplar principal.
//	 * Isso ocorre em poucos casos nos quais o exemplar e seu anexo s�o de t�tulos diferentes,
//	 * mas pode existir.
//	 *
//	 * <br/><br/>
//	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisao_acervo/resultadoPesquisaTituloCatalografico.jsp
//	 * 
//	 * @return
//	 * @throws DAOException 
//	 */
//	public String substituloTituloAnexo() throws DAOException{
//		
//		int idTitulo = getParameterInt("idTituloParaSubstituir");
//		
//		tituloDoAnexo = getGenericDAO().findByPrimaryKey(idTitulo, TituloCatalografico.class);
//		
//		tituloDoAnexoEmFormatoReferencia = new FormatosBibliograficosUtil().gerarFormatoReferencia(tituloDoAnexo, true);
//		
//		exemplarAnexo.setTituloCatalografico(tituloDoAnexo);
//		
//		/* Precisa guardar a opera��o anterior da p�gina da pesquisa de t�tulo   //
//	     * porque a opera��o vai ser substitu�da pela opera��o de pesquisar o T�tulo do anexo     //
//		 * depois de buscar o t�tulo do anexo, tem que voltar para a opera��o anterior caso o 
//		 * usu�rio utilize o bot�o voltar */
//		PesquisaTituloCatalograficoMBean mBean = getMBean("pesquisaTituloCatalograficoMBean");
//		mBean.setOperacao(operacaoPesquisaAnterior);
//		
//		return telaIncluirAnexoExemplar();
//	}
	
	
	
	
	/**
	 * Inicia os dados do exemplar anexo com os dados do exemplar principal. 
	 * O usu�rio poder� alterar antes de salvar.
	 */
	private void iniciaExemplarAnexo() throws DAOException{
		
		exemplarPrincipal = getGenericDAO().refresh(exemplarPrincipal);
		
		exemplarAnexo = new Exemplar();
		
		// Anexos n�o tem n�mero de patrim�nio
		// exemplarAnexo.setNumeroPatrimonio( XXXXXXXXXX );
		
		// Copia as informa��es do exemplar que foram preenchidas na tela
		exemplarAnexo.setStatus( new StatusMaterialInformacional(exemplarPrincipal.getStatus().getId())); 
		exemplarAnexo.setColecao( new Colecao( exemplarPrincipal.getColecao().getId() ));
		exemplarAnexo.setBiblioteca( new Biblioteca( exemplarPrincipal.getBiblioteca().getId() ) );
		exemplarAnexo.setTipoMaterial( new TipoMaterial( exemplarPrincipal.getTipoMaterial().getId() ));
		exemplarAnexo.setSituacao( new SituacaoMaterialInformacional ( exemplarPrincipal.getSituacao().getId() ) ); 
		
		tituloDoAnexo = exemplarPrincipal.getTituloCatalografico();
		
		tituloDoAnexoEmFormatoReferencia = new FormatosBibliograficosUtil().gerarFormatoReferencia(tituloDoAnexo, true);
		
		exemplarAnexo.setTituloCatalografico(tituloDoAnexo);
		
		exemplarAnexo.setNumeroChamada(exemplarPrincipal.getNumeroChamada());
		exemplarAnexo.setSegundaLocalizacao(exemplarPrincipal.getSegundaLocalizacao());
		exemplarAnexo.setExemplarDeQuemSouAnexo(exemplarPrincipal);
	}
	
	
	
	/**
	 * Salva o exemplar anexo no banco, � preciso atualizar o exemplarPrincipal para 
	 * atualizar o n�mero gerador do c�digo de barras.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoAnexo.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String incluirAnexo() throws ArqException{
		
		if(usuarioPreencheuDadosExemplarCorretamente(exemplarAnexo, true)){
		
			exemplarAnexo.popularFormasDocumento(getIdsFormasDocumentoEscolhidos());
			MovimentoCadastraAnexoExemplar mov 
				= new MovimentoCadastraAnexoExemplar(exemplarAnexo, exemplarPrincipal);
			mov.setCodMovimento(SigaaListaComando.CADASTRA_ANEXO_EXEMPLAR);
			
			try {
				execute(mov);
			} catch (NegocioException e) {
				e.printStackTrace();
				addMensagens(e.getListaMensagens());
				return null;
			}
			
			addMensagemInformation(" Anexo inclu�do com sucesso. ");
			
			if(isIncluirAnexoApartirBusca()){
				DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");
				return bean.telaInformacoesMateriais();
			}
			
			if( ! incluindoMateriaisSemTombamento) {
			
				InformacoesTombamentoMateriaisDTO info = new InformacoesTombamentoMateriaisDTO();
				info.idTituloCatalograficoSigaa = this.titulo.getId();
				info.numerosPatrimonioNaoUsados = this.numerosPatrimonioNaoUsados;
				info.unidadesTombamento = this.unidadesTombamento;
				
				return iniciarParaAdicaoExemplares(info, PAGINA_INCLUSAO_ANEXO_EXEMPLAR);
		
			}else{	
				return iniciarParaAdicaoExemplaresNaoTombados(titulo, PAGINA_INCLUSAO_ANEXO_EXEMPLAR);
			}
			
		}else{
			return null;
		}
	}
	

	
	/**
	 * M�todo chamado quando o usu�rio clica no bot�o "voltar" da p�gina de adi��o de materiais vindo
	 * da p�gina de cataloga��o do T�tulo.
	 * 
	 * Nesse caso precisa voltar para a p�gina para o usu�rio editar novamente o T�tulo.  
	 * Sempre "editar o t�tulo" porque se ele chegou at� a p�gina da inclus�o de materiais � porque 
	 * o T�tulo j� foi salvo.
	 * 
	 * Como o MBean da cataloga��o ficou sendo mantido vivo na p�gina de adi��o de materiais
	 * com o uso do <code>a4j:keepAlive</code>, as informa��es de � cataloga��o por 
	 * tombamento ou sem tombamento j� est�o l� no MBean. 
	 * <br/><br/>
	 * Chamado a partir da p�gina: <ul><li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplares.jsp</li>
	 *                             <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoFasciculos.jsp</li></ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String voltarTelaAtualizarTitulo() throws ArqException{
		
		CatalogacaoMBean bean =  getMBean("catalogacaoMBean");
		TituloCatalograficoDao tituloDao = null;
		
		try{
			tituloDao =  getDAO(TituloCatalograficoDao.class);
			titulo = tituloDao.findTituloByIdInicializandoDados(titulo.getId());
		}finally{
			if(tituloDao != null)
				tituloDao.close(); // Precisa fechar porque vai abrir outra sess�o para o mesmo objeto e vai dar erro
		}
		
		return bean.iniciarParaEdicao(titulo);
	}
	
	
	
	/**
	 * M�todo chamado quando o usu�rio clica no bot�o para catalogar o pr�ximo t�tulo n�o finalizado.
	 * 
	 * Esse bot�o s� � habilitado quando existe t�tulos n�o finalizados. � uma maneira de catalogar o
	 * pr�ximo t�tulo n�o finalizado sem precisar voltar para a p�gina inicial do sistema.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplares.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String iniciaCatalogacaoProximoTituloIncompleto() throws ArqException{
		BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
		bean.setTitulosIncompletos(null); // para o bean buscar novamente
		return bean.iniciarBuscaTitulosIncompletosMantendoOperacao();
	}
	
	
	
	/**
	 * Apenas volta para a tela anterior de cadastro de exemplares
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoAnexo.jsp
	 * @return
	 */
	public String voltarTelaExemplar(){
		return telaIncluirNovoMaterial();
	}
	
	
	
	
	/**
	 * 
	 *    <p>Normalmente chamado quando se est� voltando da p�gina de edi��o das informa��es de algum exemplar.</p>
	 * 
	 *    <p>Al�m de voltar para a tela de inclus�o de novos exemplares � preciso recarregar as informa��es
	 *  dos exemplares do T�tulo, pois alguma dessas informa��es podem ter sido alteradas.</p>
	 *
	 *    M�todo chamado de nenhuma jsp.
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String voltaTelaIncluirNovoMaterial() throws ArqException{
		
		if( ! incluindoMateriaisSemTombamento) {
			
			InformacoesTombamentoMateriaisDTO info = new InformacoesTombamentoMateriaisDTO();
			info.idTituloCatalograficoSigaa = this.titulo.getId();
			info.numerosPatrimonioNaoUsados = this.numerosPatrimonioNaoUsados;
			info.unidadesTombamento = this.unidadesTombamento;
			
			return iniciarParaAdicaoExemplares(info, PAGINA_INCLUSAO_ANEXO_EXEMPLAR);
	
		}else{	
			return iniciarParaAdicaoExemplaresNaoTombados(titulo, PAGINA_INCLUSAO_ANEXO_EXEMPLAR);
		}
	}
	
	
	
	
	
	/**
	 * Retorna todos os n�meros de patrim�nio vindos do SIPAC. Usado na inclus�o de exemplares 
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaGerenciamentoMaterial.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem>  getAllNumeroPatrimonioNaoUsadosComboBox(){
		
		// Ordena pela chave (id do bem)
		// Se o bem que tiver o menor id tiver o menor n�mero de patrim�nio vai dar certo, parece que
		// o SIPAC segue essa ordem
		Map<Integer, Long> sortedMap = new TreeMap<Integer, Long>(numerosPatrimonioNaoUsados); 
		
		Set<Integer> chaves = sortedMap.keySet();
		
		List<SelectItem> retorno = new ArrayList<SelectItem>();
		
		for (Integer chave : chaves) {
			// OBS.: O valor do selectItem vai ser o id do bem para poder recuperar depois
			retorno.add(new SelectItem(chave, String.valueOf(sortedMap.get(chave)) )); 
		}
		
		return retorno;
	
	}
	
	
	
	/**
	 * M�todo que checa algumas informa��es preenchidas pelo usu�rio. 
	 * As outras valida��es est�o no m�todo validate do pr�prio objeto.
	 * 
	 * 
	 * @throws DAOException 
	 * 
	 */
	private boolean usuarioPreencheuDadosExemplarCorretamente(Exemplar e, boolean anexo){
		
		boolean contenErro = false;
		
		
		if( e.getNotaUsuario() != null && e.getNotaUsuario().length() > 100 ){
			addMensagemErro("O tamanho m�ximo do campo Nota ao Usu�rio � de 100 caracteres.");
			contenErro = true;
		}
		
		if( e.getNotaGeral() != null && e.getNotaGeral().length() > 200){
			addMensagemErro("O tamanho m�ximo do campo Nota Geral � de 200 caracteres.");
			contenErro = true;
		}
		
		if( e.getTipoMaterial().getId() == -1){
			addMensagemErro("� preciso informar o tipo de material do exemplar");
			contenErro = true;
		}
		
		if ( e.getSituacao().getId() == -1){
			addMensagemErro("� preciso informar a situa��o do exemplar"); 
			contenErro = true;
		}
		
		if ( StringUtils.isEmpty(e.getNumeroChamada())){
			addMensagemErro("� preciso informar o n�mero de chamdada do exemplar ");
			contenErro = true;
		}
		
		if ( e.getStatus().getId() == -1){
			addMensagemErro("� preciso informar o status do exemplar "); 
			contenErro = true;
		}
		
		if ( e.getColecao().getId() == -1){
			addMensagemErro("� preciso informar a cole��o do exemplar ");
			contenErro = true;
		}
		
		if ( e.getBiblioteca().getId() == -1){
			addMensagemErro("� preciso informar a  biblioteca onde o exemplar est� localizado");
			contenErro = true;
		}
		
		if(! anexo){
			if( incluindoMateriaisSemTombamento && StringUtils.isEmpty(e.getCodigoBarras())){
				addMensagemErro("Informe o c�digo de barras do exemplar");
				contenErro = true;
			}
		}
		
		
		if(! anexo && ! incluindoMateriaisSemTombamento){
		
			if( idsExemplaresEscolhidos == null || idsExemplaresEscolhidos.size() == 0){
				addMensagemErro("Selecione pelo menos um n�mero do patrim�nio");
				contenErro = true;
			} 
		
		}
		
		return ! contenErro ;
	}
	
	
	
	/**
	 * Verifica se os dados do fasc�culo foram informados corretamente
	 */
	private boolean usuarioPreencheuDadosFasciculoCorretamente(){

		boolean contenErro = false;
		
		if ( fasciculoSelecionado.getColecao().getId() == -1){
			addMensagemErro("� preciso informar a cole��o do fasc�culo ");
			contenErro = true;
		}
		
		if ( fasciculoSelecionado.getStatus().getId() == -1){
			addMensagemErro("� preciso informar o status do fasc�culo "); 
			contenErro = true;
		}
		
		if ( fasciculoSelecionado.getSituacao().getId() == -1){
			addMensagemErro("� preciso informar a situa��o do fasc�culo"); 
			contenErro = true;
		}
		
		if ( fasciculoSelecionado.getBiblioteca().getId() == -1){
			addMensagemErro("� preciso informar a  biblioteca onde o fasc�culo est� localizado");
			contenErro = true;
		}
		
		if( fasciculoSelecionado.getTipoMaterial().getId() == -1){
			addMensagemErro("� preciso informar o tipo de material do fasc�culo");
			contenErro = true;
		}
		
		
		if( fasciculoSelecionado.getNotaGeral() != null && fasciculoSelecionado.getNotaGeral().length() > 200){
			addMensagemErro("O tamanho m�ximo do campo Nota Geral � de 200 caracteres.");
			contenErro = true;
		}
		
		if( fasciculoSelecionado.getNotaUsuario() != null && fasciculoSelecionado.getNotaUsuario().length() > 100 ){
			addMensagemErro("O tamanho m�ximo do campo Nota ao Usu�rio � de 100 caracteres.");
			contenErro = true;
		}
		
		if( StringUtils.isEmpty( fasciculoSelecionado.getNumero() ) && StringUtils.isEmpty( fasciculoSelecionado.getVolume() ) && fasciculoSelecionado.getAnoCronologico() == null && StringUtils.isEmpty( fasciculoSelecionado.getAno() ) && StringUtils.isEmpty( fasciculoSelecionado.getEdicao() ) ){
			addMensagemErro("Para incluir um fasc�culo no acervo, pelo menos um dos seguintes campos deve ser informado: Ano Cronol�gico, Ano, Volume, N�mero ou Edi��o.");
			contenErro = true;
		}
		
		return ! contenErro ;
	}
	
	
	/**
	 * Limpa os dados sempre antes de entrar na p�gina 
	 */
	private void limpaDadosExemplar(){
		this.titulo = new TituloCatalografico();
		this.numerosPatrimonioNaoUsados = null;
		exemplar = new Exemplar();
		exemplar.setStatus(new StatusMaterialInformacional("")); 
		exemplar.setColecao(new Colecao());
		exemplar.setBiblioteca(new Biblioteca());
		exemplar.setTipoMaterial(new TipoMaterial(""));
		exemplar.setSituacao(new SituacaoMaterialInformacional()); 
		exemplar.setTituloCatalografico(this.titulo);
		operacao = 0;
		setIdsExemplaresEscolhidos(new ArrayList<Integer>());
	}
	
	
	
	
	/**
	 * Prepara para incluir um novo fasc�culo se o usu�rio desejar
	 *  
	 * @throws DAOException
	 */
	private void limpaDadosFasciculo() throws DAOException{
		fasciculoSelecionado = null;
		
		fasciculosDaAssinaturaNaoInclusos = getDAO(FasciculoDao.class).findFasciculosRegistradosDaAssinatura(assinaturaSelecionada.getId());
		
		Collections.sort(fasciculosDaAssinaturaNaoInclusos, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
	}
	
	
	
	/**
	 *   Implementa a a��o de habilitar ou desabilitar a visualia��o dos exemplares do T�tulo
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnivos/catalogacao/paginaInclusaoExemplares.jsp</li>
	 *   </ul>
	 *
	 * @param evt
	 */
	public void habilitaVisualizacaoExemplares(ActionEvent evt){
		
		mostrarExemplaresDoTitulo = ! mostrarExemplaresDoTitulo;
	}
	
	
	/**
	 *    M�todo que retorna o texto que � mostrado no link que habilita ou desabilita a visualiza��o dos exemplares.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnivos/catalogacao/paginaInclusaoExemplares.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String getTextoLinkHabilitarExemplares(){
		if(! mostrarExemplaresDoTitulo)
			return "T�tulo possui "+(exemplaresDoTitulo!= null ? exemplaresDoTitulo.size() : 0)+" exemplar(es), clique aqui para visualiz�-lo(s).";
		else
			return "Ocultar os exemplares do T�tulo";
	}
	
	
	
	// M�todos dos combo box's da tela //
	
	/**
	 * 
	 * Retorna todas as bibliotecas internas cuja unidade o usu�rio tem papel de "cataloga��o bibliotec�rio".
	 * Ent�o vai aparecer somente as bibliotecas que ele tem permiss�o para incluir materiais.
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getBibliotecasInternasComPermissaoUsuario() throws DAOException{
		
		Collection <Biblioteca> b = new ArrayList<Biblioteca>();
		
		if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
						getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
										  , SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
			
			b  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades);
			
		}else{
			b  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		}
		
		return toSelectItems(b, "id", "descricaoCompleta");
	}

	/**
	 * 
	 * Retorna todos os status Ativos do material
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getStatusAtivos() throws DAOException{
		Collection <StatusMaterialInformacional> si 
			= getGenericDAO().findByExactField(StatusMaterialInformacional.class, "ativo", true);		
		return toSelectItems(si, "id", "descricao");
	}

	/**
	 * 
	 * Retorna todas as situa��es que o usu�rio pode atribuir a um material na cria��o. 
	 * N�o retorna a situa��o de baixa nem as de empr�stimo, pois o usu�rio n�o vai poder criar 
	 * um material em baixa ou emprestado.
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getSituacoes() throws DAOException{
		Collection <SituacaoMaterialInformacional> si = getDAO(SituacaoMaterialInformacionalDao.class).findSituacoesUsuarioPodeAtribuirMaterial();
		return toSelectItems(si, "id", "descricao");
	}
	
	/**
	 * 
	 * Retorna todas as cole��es cadastradas
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getColecoes() throws DAOException{
		Collection <Colecao> c = getGenericDAO().findByExactField(Colecao.class, "ativo", true);
		return toSelectItems(c, "id", "descricaoCompleta");
	}
	
	/**
	 * 
	 * Retorna os tipos de materiais
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getTiposMaterial() throws DAOException{
		Collection <TipoMaterial> t = getGenericDAO().findByExactField(TipoMaterial.class, "ativo", true);
		return toSelectItems(t, "id", "descricao");
	}
	
	
	
	/**
	 * Retorna a quantidade de materiais n�o inseridos ainda no acervo 
	 *
	 * @return
	 */
	public int getQuanidadeMateriaisNaoUsados(){
		if (numerosPatrimonioNaoUsados != null)
			return numerosPatrimonioNaoUsados.size();
		else
			return 0;
	}
	
	
	
	/**
	 * Retorna a quantidade de fasc�culos registrados pelo setor de aquisi��o, mas n�o inclu�dos no 
	 * acervo ainda pelo setor de cataloga��o.
	 *
	 * @return
	 */
	public int getQuantidadeFasciculosNaoInclusosNoAcervo(){
		if (fasciculosDaAssinaturaNaoInclusos != null)
			return fasciculosDaAssinaturaNaoInclusos.size();
		else
			return 0;
	}
	
	
	
	/////////////////////////// Tela de navega��o ////////////////////////////////////
	
	/**
	 * M�todo invocado de nenhuma p�gina jsp
	 */
	public String telaIncluirNovoMaterial(){
		return forward(PAGINA_INCLUSAO_MATERIAIS);
	}
	
	
	
	/**
	 * M�todo invocado de nenhuma p�gina jsp
	 */
	public String telaConfirmaInclusaoExemplaresOutraUnidades(){
		return forward(PAGINA_CONFIRMA_UNIDADE_DOS_MATERIAIS);
	}
	

	/**
	 * Chamado a partir da p�gina: 
	 * <br/><br/>
	 * <ul>
	 * <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp<li/>
	 * <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp<li/>
	 * <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp<li/>
	 * </ul>
	 *
	 * @return
	 */
	public String telaIncluirAnexoExemplar(){
		return forward(PAGINA_INCLUSAO_ANEXO_EXEMPLAR); 
	}
	
	/**
	 * <p>
	 * Precisa guardar a opera��o anterior da p�gina da pesquisa de t�tulo   //
     * porque a opera��o vai ser substitu�da pela opera��o de pesquisar o T�tulo do anexo     //
	 * depois de buscar o t�tulo do anexo, tem que voltar para a opera��o anterior caso o 
	 * usu�rio utilize o bot�o voltar
	 * </p>
	 * 
	 * Chamado a partir da p�gina: 
	 * <br/><br/>
	 * <ul>
	 * <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp<li/>
	 * <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp<li/>
	 * <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp<li/>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltaTelaIncluirAnexoExemplarVindoTelaPesquisaTituloDoAnexo(){
		
		PesquisaTituloCatalograficoMBean mBean = getMBean("pesquisaTituloCatalograficoMBean");
		mBean.setOperacao(operacaoPesquisaAnterior);
		
		return telaIncluirAnexoExemplar();
		
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////
	
	
	// Sets e gets
	
	public boolean isInclusaoExemplares() {
		return operacao == INCLUIR_EXEMPLARES;
	}

	public Exemplar getExemplar() {
		return exemplar;
	}
	
	public void setExemplar(Exemplar exemplar) {
		this.exemplar = exemplar;
	}

	public List<Integer> getIdsExemplaresEscolhidos() {
		return idsExemplaresEscolhidos;
	}

	public void setIdsExemplaresEscolhidos(List<Integer> idsExemplaresEscolhidos) {
		this.idsExemplaresEscolhidos = idsExemplaresEscolhidos;
	}
	
	public List<String> getIdsFormasDocumentoEscolhidos() {
		return idsFormasDocumentoEscolhidos;
	}

	/** Seta as formas de documento escolhidas pelo usu�rio eliminando os ids negativos que n�o s�o formas de documento v�lidas. */
	public void setIdsFormasDocumentoEscolhidos(List<String> idsFormasDocumentoEscolhidos) {
		this.idsFormasDocumentoEscolhidos = new ArrayList<String>();
		
		for (String string : idsFormasDocumentoEscolhidos) {
			if( Integer.parseInt(string) > 0 )
				this.idsFormasDocumentoEscolhidos.add(string);
		}
	}

	public String getTituloEmFormatoReferencia() {
		return tituloEmFormatoReferencia;
	}

	public void setTituloEmFormatoReferencia(String tituloEmFormatoReferencia) {
		this.tituloEmFormatoReferencia = tituloEmFormatoReferencia;
	}
	
	public boolean isInclusaoFasciculos() {
		return operacao == INCLUIR_FASCICULOS;
	}


	public TituloCatalografico getTitulo() {
		return titulo;
	}


	public void setTitulo(TituloCatalografico titulo) {
		this.titulo = titulo;
	}


	public Map<Integer, Long> getNumerosPatrimonioNaoUsados() {
		return numerosPatrimonioNaoUsados;
	}


	public void setNumerosPatrimonioNaoUsados(
			Map<Integer, Long> numerosPatrimonioNaoUsados) {
		this.numerosPatrimonioNaoUsados = numerosPatrimonioNaoUsados;
	}


	public boolean isAssinaturaCadastrada() {
		return assinaturaCadastrada;
	}


	public void setAssinaturaCadastrada(boolean assinaturaCadastrada) {
		this.assinaturaCadastrada = assinaturaCadastrada;
	}


	public List<Fasciculo> getFasciculosDaAssinatura() {
		return fasciculosDaAssinaturaNaoInclusos;
	}


	public void setFasciculosDaAssinatura(List<Fasciculo> fasciculosDaAssinatura) {
		this.fasciculosDaAssinaturaNaoInclusos = fasciculosDaAssinatura;
	}

	public Fasciculo getFasciculoSelecionado() {
		return fasciculoSelecionado;
	}


	public void setFasciculoSelecionado(Fasciculo fasciculoSelecionado) {
		this.fasciculoSelecionado = fasciculoSelecionado;
	}


	public List<Exemplar> getExemplaresDoTitulo() {
		return exemplaresDoTitulo;
	}


	public void setExemplaresDoTitulo(List<Exemplar> exemplaresDoTitulo) {
		this.exemplaresDoTitulo = exemplaresDoTitulo;
	}


	public Exemplar getExemplarPrincipal() {
		return exemplarPrincipal;
	}


	public void setExemplarPrincipal(Exemplar exemplarPrincipal) {
		this.exemplarPrincipal = exemplarPrincipal;
	}


	public Exemplar getExemplarAnexo() {
		return exemplarAnexo;
	}


	public void setExemplarAnexo(Exemplar exemplarAnexo) {
		this.exemplarAnexo = exemplarAnexo;
	}


	public String getTituloDoAnexoEmFormatoReferencia() {
		return tituloDoAnexoEmFormatoReferencia;
	}


	public boolean isIncluirAnexoApartirBusca() {
		return operacao == INCLUR_ANEXO_A_PARTIR_BUSCA;
	}


	public boolean isAbilitaBotaoVoltarTelaTitulosNaoFinalizados() {
		return abilitaBotaoVoltarTelaTitulosNaoFinalizados;
	}


	public void setAbilitaBotaoVoltarTelaTitulosNaoFinalizados(boolean abilitaBotaoVoltarTelaTitulosNaoFinalizados) {
		this.abilitaBotaoVoltarTelaTitulosNaoFinalizados = abilitaBotaoVoltarTelaTitulosNaoFinalizados;
	}

	public boolean isIncluindoMateriaisSemTombamento() {
		return incluindoMateriaisSemTombamento;
	}

	public void setIncluindoMateriaisSemTombamento(boolean incluindoMateriaisSemTombamento) {
		this.incluindoMateriaisSemTombamento = incluindoMateriaisSemTombamento;
	}

	public boolean isIncluindoMateriaisApartirTelaBusca() {
		return incluindoMateriaisApartirTelaBusca;
	}


	public boolean isIncluindoMateriaisApartirTelaCatalogacao() {
		return incluindoMateriaisApartirTelaCatalogacao;
	}

	/** Configura que se est� inclu�ndo o material a partir da tela de busca. */
	public void setIncluindoMateriaisApartirTelaBusca(boolean incluindoMateriaisApartirTelaBusca) {
		this.incluindoMateriaisApartirTelaBusca = incluindoMateriaisApartirTelaBusca;
		this.incluindoMateriaisApartirTelaCatalogacao = ! incluindoMateriaisApartirTelaBusca;
	}	
	
	/** Configura que se est� inclu�ndo o material a partir da tela de cataloga��o. */
	public void setIncluindoMateriaisApartirTelaCatalogacao(boolean incluindoMateriaisApartirTelaCatalogacao) {
		this.incluindoMateriaisApartirTelaCatalogacao = incluindoMateriaisApartirTelaCatalogacao;
		this.incluindoMateriaisApartirTelaBusca = ! incluindoMateriaisApartirTelaCatalogacao;
	}
	
	
	public List<Fasciculo> getFasciculosDaAssinaturaNaoInclusos() {
		return fasciculosDaAssinaturaNaoInclusos;
	}


	public void setFasciculosDaAssinaturaNaoInclusos(List<Fasciculo> fasciculosDaAssinaturaNaoInclusos) {
		this.fasciculosDaAssinaturaNaoInclusos = fasciculosDaAssinaturaNaoInclusos;
	}


	public Assinatura getAssinaturaSelecionada() {
		return assinaturaSelecionada;
	}


	public void setAssinaturaSelecionada(Assinatura assinaturaSelecionada) {
		this.assinaturaSelecionada = assinaturaSelecionada;
	}


	public List<Assinatura> getAssinaturasPossiveisInclusaoFasciculo() {
		return assinaturasPossiveisInclusaoFasciculo;
	}


	public void setAssinaturasPossiveisInclusaoFasciculo(List<Assinatura> assinaturasPossiveisInclusaoFasciculo) {
		this.assinaturasPossiveisInclusaoFasciculo = assinaturasPossiveisInclusaoFasciculo;
	}

	public boolean isMostrarExemplaresDoTitulo() {
		return mostrarExemplaresDoTitulo;
	}

	public void setMostrarExemplaresDoTitulo(boolean mostrarExemplaresDoTitulo) {
		this.mostrarExemplaresDoTitulo = mostrarExemplaresDoTitulo;
	}

	

	public List<DadosMateriaisTombados> getDadosBensSemPermisaoUnidade() {
		return dadosBensSemPermisaoUnidade;
	}

	public void setMensagemBensSemPermisaoUnidade(List<DadosMateriaisTombados> dadosBensSemPermisaoUnidade) {
		this.dadosBensSemPermisaoUnidade = dadosBensSemPermisaoUnidade;
	}

	public List<String> getMensagemUnidadesPermisaoUsuario() {
		return mensagemUnidadesPermisaoUsuario;
	}

	public void setMensagemUnidadesPermisaoUsuario(List<String> mensagemUnidadesPermisaoUsuario) {
		this.mensagemUnidadesPermisaoUsuario = mensagemUnidadesPermisaoUsuario;
	}

	public Map<Integer, Integer> getUnidadesTombamento() {
		return unidadesTombamento;
	}

	public void setUnidadesTombamento(Map<Integer, Integer> unidadesTombamento) {
		this.unidadesTombamento = unidadesTombamento;
	}

	public List<Assinatura> getAssinaturasSemTitulo() {
		return assinaturasSemTitulo;
	}

	public void setAssinaturasSemTitulo(List<Assinatura> assinaturasSemTitulo) {
		this.assinaturasSemTitulo = assinaturasSemTitulo;
	}

	public List<Assinatura> getAssinaturasDoTituloSelecionado() {
		return assinaturasDoTituloSelecionado;
	}

	public void setAssinaturasDoTituloSelecionado(List<Assinatura> assinaturasDoTituloSelecionado) {
		this.assinaturasDoTituloSelecionado = assinaturasDoTituloSelecionado;
	}
	
	
	//////////////////M�todos da interface de busca no acervo  ///////////////////////

	
	
	/**
	 * <p>Chamado da pesquisa no acervo para configurar o t�tulo do anexo do exemplar com o t�tulo escolhido pelo usu�rio.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#setTitulo(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc)
	 */
	@Override
	public void setTitulo(CacheEntidadesMarc cache) throws ArqException {
		tituloDoAnexo = getGenericDAO().findByPrimaryKey(cache.getIdTituloCatalografico(), TituloCatalografico.class);
		
	}

	
	/**
	 * <p>Chamado da pesquisa no acervo para realizar a opera��o de substituir o t�tulo de exemplar anexo.</p>
	 *
	 * <p>M�todo n�o chamado por p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#selecionaTitulo()
	 */
	@Override
	public String selecionaTitulo() throws ArqException {
		tituloDoAnexoEmFormatoReferencia = new FormatosBibliograficosUtil().gerarFormatoReferencia(tituloDoAnexo, true);
		
		exemplarAnexo.setTituloCatalografico(tituloDoAnexo);
		
		configuraTelaPesquisaParaPesquisaNormal();
		
		return telaIncluirAnexoExemplar();
	}

	
	
	
	/**
	 * Se na busca de t�tulos para substituir o T�tulo do Anexo o usu�rio utilizar o bot�o voltar, vote para a tela de incluir anexo..<br/>
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#voltarBuscaAcervo()
	 */
	@Override
	public String voltarBuscaAcervo() throws ArqException {
		configuraTelaPesquisaParaPesquisaNormal();
		return telaIncluirAnexoExemplar();
	}
	
	

	/**
	 * Configura que o bot�o voltar na pesquisa de t�tulos para substituir o t�tulo do anexo vai ser utilizado.
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return true;
	}
	
	////////////Fim dos m�todos padr�o da interface de busca no acervo //////////////////
	
	
	
	
	/**
	 * <p>Precisa Volta � pesquisa Normal se voltou da tela de pesquisa de T�tulo para substitu���o do T�tulo de um anexo.</p>
	 * 
	 * <p>Isso � necess�rio porque a tela de busca no acervo � usada 2 vezes nesse casos de uma para opera��es diferentes.</p>
	 */
	private void configuraTelaPesquisaParaPesquisaNormal(){
		
		PesquisaTituloCatalograficoMBean mBean = getMBean("pesquisaTituloCatalograficoMBean");
		mBean.setMbeanChamadoPesquisaTitulo(null);
		mBean.setOperacao(PesquisaTituloCatalograficoMBean.PESQUISA_NORMAL);
		mBean.limpaResultadoPesquisa();
	}
	
	
}

