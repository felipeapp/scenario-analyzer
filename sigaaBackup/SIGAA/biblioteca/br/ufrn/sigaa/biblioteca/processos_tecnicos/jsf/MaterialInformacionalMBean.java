/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Classe que gerência a inclusão de materiais (exemplares e fascículos) no acervo.
 *
 * @author Fred
 * @version 1.0 Criação da classe
 *          2.0 Refactor da classe para suportar 3 casos separados agora, inclusão de exemplares, fascículos e anexo de exemplares
 *
 */
@Component(value="materialInformacionalMBean")
@Scope(value="request")
public class MaterialInformacionalMBean extends SigaaAbstractController<MaterialInformacional> implements PesquisarAcervoBiblioteca{
	
	/**
	 * pagina inclusão de materiais, inclui a página de inclusão de exemplares e fascículos 
	 */
	public static final String PAGINA_INCLUSAO_MATERIAIS = "/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoMateriais.jsp";
	
	/**
	 * Página usado somento quando os bens não foram tombados para a mesma unidade de permissão 
	 * do catalogador, neste caso ele precisa confirmar os materiais selecionados, para diminuir 
	 * a probabilidade dele incluir materiais na biblioteca errada. 
	 */
	public static final String PAGINA_CONFIRMA_UNIDADE_DOS_MATERIAIS = "/biblioteca/processos_tecnicos/catalogacao/paginaConfirmaUnidadeDosMateriais.jsp";
	
	/**
	 * Página para inclusão de anexos de exemplares
	 */
	public static final String PAGINA_INCLUSAO_ANEXO_EXEMPLAR = "/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoAnexo.jsp";
	
	
	/** Guarda o Título ao qual os materiais serão adicionados */
	private TituloCatalografico titulo;
	
	
	/**
	 * Guarda os números de tombamento ainda não inseridos no acervo do sigaa. No formato:  Map<IdBem, NumeroTombamento>
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
	 *  Guarda a descricao da lista de exemplares que o bibliotecario não tem permissão de incluir materiais 
	 * <CodigoBarras, DescricaoUnidade>
	 */
	private List<DadosMateriaisTombados> dadosBensSemPermisaoUnidade; 
	
	/**
	 * Guarda a descrição das unidade onde o usuário tem permissão de catalogação.
	 * Se o bem for tombado para uma unidade diferente ele ainda vai ter que confirma a inclusão
	 * 
	 */
	private List<String> mensagemUnidadesPermisaoUsuario;
	
	
	/** Guarda o Título pra ser mostrado ao usuário na página; */
	private String tituloEmFormatoReferencia;
	
	
	/** 
	 * Se o usuário selecionou um título na tela de títulos não finalizados essa variável
	 * vai ser setada. Nesse caso depois do usuário incluir materiais no título, vai poder voltar 
	 * para a página de títulos não finalizados e escolher o próximo para trabalhar na catalogação.
	 */
	private boolean abilitaBotaoVoltarTelaTitulosNaoFinalizados = false;
	
	
	/** 
	 * Guarda as Informações do exemplar incluídas pelo bibliotecário no formulário de inclusão de exemplares. 
	 */
	private Exemplar exemplar;
	
	/** 
	 *  Guarda os ids Escolhidos pelo usuário na página com o selectManyList, quando a catalogação é 
	 *  com tombamento, onde as informações dos códigos de barras vem do número de tombamento doSIPAC.
	 */
	private List<Integer> idsExemplaresEscolhidos;
	
	/** 
	 *  Guarda os ids Escolhidos pelo usuário na página com o selectManyList, quanto
	 *  as formas de documento que um material possui. 
	 */
	private List<String> idsFormasDocumentoEscolhidos;
	
	/**
	 * Guarda os exemplares já cadastrados do Título escolhido para mostra ao usuário
	 */
	private List<Exemplar> exemplaresDoTitulo;
	
	
	/** Guarda os exemplares selecionados para serem incluídos no acervo pelo usuário */
	private List<Exemplar> exemplares = new ArrayList<Exemplar>();
	
	/**
	 * Guarda os ids dos bens dos exemplares criados pelo bibliotecário, para depois da inclusão 
	 * remover os bens da lista de <code>numerosPatrimonioNaoUsados </code>
	 */
	private List<Integer> idBensExemplaresCriados = new ArrayList<Integer>();
	
	
	/////////////////// Informações quando é inclusão de fascículos ///////////////////////////////
	
	
	/**
	 *  Traz todas as assinatura que já pertencem ao Título ou que não pertencem a nenhum outro título,
	 * para o usuário escolher em qual assinatura deseja incluir o fascículo. 
	 * Exemplar: Um Título: "veja" pode ter várias assinatura espalhadas por várias bibliotecas.
	 **/
	private List<Assinatura> assinaturasPossiveisInclusaoFasciculo; 
	
	/**
	 * Guarda as assintura que estão sem Título. (A título vai ser associado caso o usuário inclui o fascículo) 
	 */
	private List<Assinatura> assinaturasSemTitulo;
	
	/**
	 *  guarda as assintura que já estão associadas ao Título escolhido
	 */
	private List<Assinatura> assinaturasDoTituloSelecionado;
	
	/**
	 *  assinatura que o usuário selecionou para incluir o fascículo
	 */
	private Assinatura assinaturaSelecionada;   
	
	/**
	 *  os fascículo cuja chegada foi registrada mais não foram incluído no acervo ainda.
	 */
	private List<Fasciculo> fasciculosDaAssinaturaNaoInclusos; 
	
	/**
	 * O fascículo selecionado para inclusão no acervo. (completar as suas informações e ele ficar visível para o usuário.)
	 */
	private Fasciculo fasciculoSelecionado;
	
	/**
	 * Indica na página se a assinatura foi criada no setor de compras
	 */
	private boolean assinaturaCadastrada = false;
	
	
	
	/*  Usado na parte de inclusão de anexos */
	
	/**
	 * Exemplar principal do anexo que vai ser incluído
	 */
	private Exemplar exemplarPrincipal;
	
	/**
	 * Exemplar anexo que vai ser incluído
	 */
	private Exemplar exemplarAnexo;
	
	/**
	 * O título do anexo, pode ser diferente do título do principal
	 */
	private TituloCatalografico tituloDoAnexo;
	
	/**
	 * Informações do título do anexo, para mostrar na página.
	 */
	private String tituloDoAnexoEmFormatoReferencia;
	
	/*  ******************************************************** */
	
	/**  Em qual operação se está no momento. */
	private int operacao = 0;
	
	/**  Operação onde o usuário vai incluir um anexo de um exemplar a partir da página de busca. */
	private static final int INCLUIR_EXEMPLARES = 1;
	/**  Operação onde o usuário vai incluir um anexo de um exemplar a partir da página de busca. */
	private static final int INCLUIR_FASCICULOS = 2;
	/**  Operação onde o usuário vai incluir um anexo de um exemplar a partir da página de busca. */
	private static final int INCLUR_ANEXO_A_PARTIR_BUSCA = 3;
	
	
	/**
	 * serve para saber para qual tela voltar no botão voltar da página
	 */
	private boolean incluindoMateriaisApartirTelaBusca = false;
	
	
	/**
	 * serve para saber para qual tela voltar no botão voltar da página
	 */
	private boolean incluindoMateriaisApartirTelaCatalogacao = false;
	
	
	/**
	 * Para saber se está no caso de uso de inclusão de exemplares sem tombamento. 
	 */
	private boolean incluindoMateriaisSemTombamento = false;
	
	
	/** Flag que indica se o usuário escolheu mostrar ou não as informações dos outras exemplares do Título.<br/>
	 * O usuário possui a opção de esconder, porque quando a quantidade de exemplare era grande, a visualização
	 * do formulário de inclusão de um novo exemplar ficava um pouco tumultuada. */
	private boolean mostrarExemplaresDoTitulo = true;
	
	/** 
	 *  Usado se o usuário decidir trocar o Título de um anexo de um exemplar <br/>
	 *  
	 *  Neste caso, como vai chamar a tela de pesquisa de título com outra operação, precisa guardar 
	 *  qual a operação anterior, e quando o usuário voltar da tela de pesquisa do título do anexo,
	 *  retorna a operação anterior da tela de pesquisa. 
	 */
	private int operacaoPesquisaAnterior = -1;
	
	/**
	 * Construtor padrão do bean
	 * 
	 * @throws DAOException
	 */
	public MaterialInformacionalMBean(){
	
	}
	
	/**
	 *    Método que inicia o caso de uso de adição de novos exemplares ao acervo.
	 *    
	 *    Método não invocado por jsp
	 * 
	 * @param paginaRetornoErro  indica para onde deve retornar se der erro. É necessário pois
	 *          esse método é chamado de vários lugares e não tem como saber para onde deve voltar.   
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
		
		tituloEmFormatoReferencia = "Nº do Sistema <strong>"+titulo.getNumeroDoSistema()+"</strong> - "+new FormatosBibliograficosUtil().gerarFormatoReferencia(titulo, true);
		
		operacao = INCLUIR_EXEMPLARES;
		
		prepareMovimento(SigaaListaComando.CADASTRA_EXEMPLAR);
		
		if(getQuanidadeMateriaisNaoUsados() == 0 ){
			addMensagemWarning("Todos os materiais para o Título \" "+infomacoesTituloCompra.getTitulo()+" \"  do Termo de Responsabilidade Nº "
					+infomacoesTituloCompra.getDescricaoTermoResponsabibliodade()+" foram incluídos no acervo.");
		}
			
		return telaIncluirNovoMaterial();
		
	}
	
	
	/**
	 *    Final do caso de uso da catalogação de exemplares não tombados.
	 *    
	 *    Aqui o sistema simplesmente vai exibir uma tela com campo para o usuário digitar o que 
	 *    quiser no código de barras do exemplar.
	 *    
	 *    Método não invocado por jsp.
	 *    
	 * @param paginaRetornoErro  indica para onde deve retornar se der erro. É necessário pois
	 *          esse método é chamado de vários lugares e não tem como saber para onde deve voltar.   
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
		
		tituloEmFormatoReferencia = "Nº do Sistema <strong>"+titulo.getNumeroDoSistema()+"</strong> - "+ new FormatosBibliograficosUtil().gerarFormatoReferencia(titulo, true);
		
		operacao = INCLUIR_EXEMPLARES;
		
		incluindoMateriaisSemTombamento = true;
		
		prepareMovimento(SigaaListaComando.CADASTRA_EXEMPLAR);
		
		return telaIncluirNovoMaterial();
	}
	

	/**
	 *      Inicia o caso de uso de incluir novos fascículos, nesse caso vai ser buscada a assinatura 
	 *  a partir do número de tombamento e os fascículos que foram registrados mas não incluídos ainda pelo
	 *  setor de catalogação.
	 *  
	 *      <p>OBS.: A inclusão de um fascículo só pode ocorrer se ele for registrado pelo setor de compras.</p> 
	 *
	 *      Método não invocado por jsp
	 * 
	 * @param infomacoesTituloCompra
	 * @return
	 */
	public String iniciarParaAdicaoFasciculos(InformacoesTombamentoMateriaisDTO infomacoesTituloCompra)throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		addMensagemErro("O Título escolhido foi um Título cujo formato de matrial é de PERIÓDICO, "
				+" logo todos dos fascículo devem ser adicionados a ele pela opção Catalogação sem Tombamento, "
				+"pois diferente dos exemplares, nem os fascículo, nem as assinatura são tombados no patrimônio da instituição até o presente momento.");
		
		return null;
		
	}
	
	
	/**
	 * 
	 * Caso de uso de incluir fascículos em assinaturas não tombadas.
	 * 
	 * Nesse caso, se o título não possuir uma assinatura de fascículos ainda,  o usuário vai 
	 * precisar selecionar a assinatura do fascículo primeiro. Assim que selecionar, 
	 * redireciona para a página de inclusão de fascículo. Caso já possuía uma assinatura, vai para a 
	 * página de incluir fascículos diretamente. 
	 * 
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String iniciarParaAdicaoFasciculosNaoTombados(TituloCatalografico tituloPassado)throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		TituloCatalograficoDao tituloDao = getDAO(TituloCatalograficoDao.class);
		
		// this.titulo = tituloDao.findTituloByIdInicializandoDados(tituloPassado.getId());
		this.titulo = tituloDao.findByPrimaryKey(tituloPassado.getId(), TituloCatalografico.class); // ficou mais rápido assim
		
		tituloEmFormatoReferencia = "Nº do Sistema <strong>"+titulo.getNumeroDoSistema()+"</strong> - "+ new FormatosBibliograficosUtil().gerarFormatoReferencia(titulo, true);
		
		operacao = INCLUIR_FASCICULOS;
		
		incluindoMateriaisSemTombamento = true;
		
		AssinaturaDao assinaturaDao = getDAO(AssinaturaDao.class);
		
		// Se é administrador geral, traz assinatura de todas as unidades 
		if(isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			
			assinaturasPossiveisInclusaoFasciculo = assinaturaDao.findAssinaturasAtivasPossiveisInclusaoFasciculos(this.titulo.getId(), null, null, null, null);
		}else{ // se é só catalogador vai poder incluir apenas na assinatura de sua unidade
			
			List<Integer> idBibliotecas = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario(getUsuarioLogado(), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS) );
			
			if(idBibliotecas == null || idBibliotecas.size() == 0){
				addMensagemErro("Usuário não tem permissão de catalogação.");
				return null;
			}
			
			assinaturasPossiveisInclusaoFasciculo = assinaturaDao.findAssinaturasPossiveisInclusaoFasciculosByUnidadeDestino(this.titulo.getId(), idBibliotecas );
		}
		
		assinaturasSemTitulo = new ArrayList<Assinatura>();
		assinaturasDoTituloSelecionado = new ArrayList<Assinatura>();
		
		// Separa em duas lista para melhorar a visualização na tela para o usuário.
		for (Assinatura assinatura : assinaturasPossiveisInclusaoFasciculo) {
			
			if(assinatura.getTituloCatalografico() == null){
				assinaturasSemTitulo.add(assinatura);
			}else{
				assinaturasDoTituloSelecionado.add(assinatura);
			}
			
		}
		
		if(assinaturasPossiveisInclusaoFasciculo.size() == 0){
			addMensagemWarning("Não existem assinaturas disponíveis da sua unidade para inclusão de fascículos.");
		}
			
		
		return telaIncluirNovoMaterial();

	}
	
	
	
	/**
	 * Método chamado quando o usuário escolhe a assinatura que pode ter fascículos que pertencem ao
	 * título.<br/>
	 *  O sistema vai mostrar os fascículo que não foram incluídos no acervo ainda para a 
	 * assinatura selecionada.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInformacoesFasciculos.jsp
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
	 * Método chamado quando o usuário seleciona um fascículo apenas registrado e agora vão 
	 * ser incluídas as suas informações.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInformacoesFasciculos.jsp
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
	 *  <p> Método que monta a lista de materiais escolhidos pelo usuário na tela de inclusão de materiais. </p>
	 *  <p> <strong>Caso 1:</strong> Se todos os materias foram catalogados para a mesma unidade onde o bibliotecário tem permissão de catalogação,
	 *  chama diretamento o método <code>confirmaCadastroExemplar()</code> incluí-los no banco. </p>
	 *  <p> <strong>Caso 2:</strong> Se algum material não foi catalogado para a mesma unidade onde o bibliotecário tem permissão de catalogação,
	 *  retireciona para tela onde o bibliotecário vai ter que confirma a inclusão dos materiais.</p> 
	 *  <p> Esse procedimento é para evitar que bibliotecários 
	 *  incluão materiais de bibliotecas que não sejam a sua.</p>
	 *
	 * <br/><br/>
	 * Chamado a partir da página:   /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplar.jsp
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
						addMensagemErro("A quantidade máxima permitida para ser selecionada é de "+qtd+" números de tombamento por vez.");
						return null;
					}
					
					/// Salva um exemplar para cada número de patrimônio escolhido pelo usuário na tela
					for (int posicao = 0; posicao< idsExemplaresEscolhidos.size(); posicao++) {
						
						Exemplar exemplarTemp = new Exemplar();
						
						// Apesar de idsExemplaresEscolhidos ser uma lista de Integer estava vindo uma lista
						// de Strings ele não convertia direto
						String idBemString = (String.valueOf(idsExemplaresEscolhidos.get(posicao)));
						Integer idBem = Integer.parseInt(idBemString);
						
						idBensExemplaresCriados.add(idBem);
						
						exemplarTemp.setIdBem(idBem);
						exemplarTemp.setNumeroPatrimonio(numerosPatrimonioNaoUsados.get(idBem)); // pega no mapa o número do patrimônio
						exemplarTemp.setTipoTombamento(tiposTombamentos.get(idBem)); // pega no mapa o número do patrimônio
					
						// Copia as informações do exemplar que foram preenchidas na tela
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
						
						// Para exemplares tombados o número do patrimônio é igual ao código de barras.
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
						exemplar.setCodigoBarras(exemplar.getCodigoBarras().toUpperCase()); // Coloca para UPPER CASE para evitar erros de digitação.
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
			return null; // Fica na mesma página mostrando as mensagem de erro
		}
		
	}
	
	/**
	 * 
	 * <p> Método que cadastra o exemplar e logo após redireciona para a caso de uso de incluir nota de 
	 * circulação para incluir uma nota no exemplar que acabou de ser cadastrado. </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	
			if(! hasOnlyErrors()){ // Se não tem erros é porque conseguir cadastrar o exemplar no acervo
			
				 dao = getGenericDAO();
				
				//////////////////////////////////////////////////////////////////////////////////////
				// Inicializa a unidade e situação dos materiais antes de pode chamar o Caso de uso de nota de Circulação //
				//////////////////////////////////////////////////////////////////////////////////////
				
				List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
				
				for (Exemplar temp : exemplares) {
					
					Exemplar exemplarTemp = new Exemplar();
					exemplarTemp.setId( temp.getId());
					exemplarTemp.setCodigoBarras( temp.getCodigoBarras());
					exemplarTemp.setAtivo( temp.isAtivo());
					
					exemplarTemp.setSituacao( dao.findByPrimaryKey(temp.getSituacao().getId(), SituacaoMaterialInformacional.class, "id", "situacaoDisponivel", "situacaoEmprestado", "situacaoDeBaixa")  );
					
					Biblioteca biblioteca = new Biblioteca();
					biblioteca.setUnidade( new Unidade(temp.getBiblioteca().getUnidade().getId()));   // Só para não passsar o objeto proxy do hibernate
					exemplarTemp.setBiblioteca( biblioteca );
					
					materiais.add( exemplarTemp );
				}
				
				NotasCirculacaoMBean mbean = getMBean("notasCirculacaoMBean");
				return mbean.iniciarConfigurandoDiretamenteMaterialIncluirNota(materiais, PesquisaTituloCatalograficoMBean.PAGINA_PESQUISA_TITULO);
				
			}else
				return null; // Caso dê algum erro do cadastro do exemplar, fica na mesma página.
			
		}catch (Exception ex) {
			ex.printStackTrace();
			addMensagemErro(ex.getMessage());
			return null;  // Caso dê alguma exceção do cadastro do exemplar, fica na mesma página.
		}finally{
			if(dao != null )  dao.close(); 
		}
	}
	
	
	
	/**
	 *   <p>Realiza o cadastro dos exemplares no sistema.</p>
	 *   <p>Caso o tombamento dos exemplares tenham sido para a mesma unidade da permissão do usuário esse método é chamado 
	 *   diretamente do método <code>cadastrarExemplar()</code>, caso contrário, esse método é chamado da página declarada abaixo,
	 *   depois que o usuário confirmar que realmente deseja incluir os exemplares no acervo, mesmo ele tendo sido tombados para 
	 *   outra unidade. </p>
	 *
	 * <br/><br/>
	 * Chamado a partir da página:   /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaConfirmaUnidadeDosMateriais.jsp
	 * 
	 * @return
	 * @throws Exception
	 */
	public String confirmaCadastroExemplar() throws Exception{
		
		/* Chama o processador com a lista de exemplares escolhidos pelo usuário, essa lista é configurada no método anterior: 
		 * cadastrarExemplar()
		 */
		
		MovimentoCadastraExemplar mov = new MovimentoCadastraExemplar(exemplares);
		
		mov.setCodMovimento(SigaaListaComando.CADASTRA_EXEMPLAR);
		
		execute(mov);
		
		
		if( ! incluindoMateriaisSemTombamento){
		
			for (Integer idBemExemplarCriado : idBensExemplaresCriados) {
				numerosPatrimonioNaoUsados.remove(idBemExemplarCriado); // remove da página para o usuário
			}
			
			// Depois de cadastrar se prepara para adicionar novos       
			
			InformacoesTombamentoMateriaisDTO info = new InformacoesTombamentoMateriaisDTO();
			info.idTituloCatalograficoSigaa = this.titulo.getId();
			info.numerosPatrimonioNaoUsados = this.numerosPatrimonioNaoUsados;
			info.unidadesTombamento = this.unidadesTombamento;
			
			addMensagemInformation("Exemplar(es) incluído(s) com sucesso");
			
			return iniciarParaAdicaoExemplares(info, PAGINA_INCLUSAO_MATERIAIS);
			
		}else{
			addMensagemInformation("Exemplar incluído com sucesso");
			
			return iniciarParaAdicaoExemplaresNaoTombados(titulo, PAGINA_INCLUSAO_MATERIAIS);
		}
		
		
		
	}
	
	
	/**
	 * 
	 * <p> Método que verifica se a hierarquia da unidade de tombamento do bem possui a mesma unidade da biblioteca onde o usuário tem permissão de catalogação. </p>
	 * <p> Caso não seja a mesma unidade o sistema mostrará um aviso para o usuário. </p> 
	 * <p> Este método serve para impedir que bibliotecários incluam na sua biblioteca bens tombados para outras bibliotecas. 
	 * Já que neste ponto o exemplar ainda não está em uma biblioteca, não tem como realizar o controle de alteração pela biblioteca.</p> 
	 *
	 * @param idUnidadeTombamentoBem
	 * @throws DAOException 
	 */
	private boolean bibliotecarioTemPermisaoIncluirExemplaresDaUnidadeTombamentoBem(Unidade unidadeTombamentoBem, Permissao... permisoesCatalogacaoUsuario) throws DAOException{
		
		if(isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)) /// Administrador Geral insere em qualquer unidade.
			return true;
		
		/* Verifica se alguma unidade das PERMISSÔES DE CATALOGAÇÂO do bibliotecário ESTÀ NA HIERARQUIA da unidade DO BEM
		 * Apesar do nome do método ser isUnidadeFilha(), ele verifica se a unidade está na hierarquina
		 */
		for (Permissao permissaoCatalogacaoUsuario : permisoesCatalogacaoUsuario) {
			if( permissaoCatalogacaoUsuario.getUnidadePapel().isUnidadeFilha( unidadeTombamentoBem.getId() ) ){
				return true;
			}
		}
		
		return false;
			
	}
	
	
	/**
	 * <p>Método que realiza a ação de incluir um fáscículo do acervo para o sistema.</p>
	 * 
	 * <p>Atualiza o fascículo com as informações digitadas pelo usuário de catalogação. A criação 
	 * desse fascículo já foi feita pelo usuário de aquisição na hora que ele registrou a sua chegada.</p> 
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaGerenciamentoMaterial.jsp
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
			
				addMensagemInformation("Fascículo incluído com sucesso");
				
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
	 * <p> Método que inclui o fascículo no acervo e logo após redireciona para a caso de uso de incluir nota de 
	 * circulação para incluir uma nota no fascículo que acabou de ser incluído. </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			// É preciso guardar os dado do fascículo antes de chamar o método de atualizar
			///////////////////////////////////////////////////////////////////////////////
			
			int idFasciculo = fasciculoSelecionado.getId();
			String codigoBarras = fasciculoSelecionado.getCodigoBarras();
			boolean fasciculoAtivo = fasciculoSelecionado.isAtivo();
			int idSituacaoFasciculo = fasciculoSelecionado.getSituacao().getId();
			int idUnidadeFasciculo = fasciculoSelecionado.getBiblioteca().getUnidade().getId();
			
			atualizarFasciculo();
	
			if(! hasOnlyErrors()){ // Se não tem erros é porque conseguir cadastrar o exemplar no acervo
			

				//////////////////////////////////////////////////////////////////////////////////////
				// Inicializa a unidade e situação dos materiais antes de pode chamar o Caso de uso de nota de Circulação //
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
				return null; // Caso dê algum erro do cadastro do exemplar, fica na mesma página.
			
		}catch (Exception ex) {
			ex.printStackTrace();
			addMensagemErro(ex.getMessage());
			return null;  // Caso dê alguma exceção do cadastro do exemplar, fica na mesma página.
		}finally{
			if(dao != null )  dao.close(); 
		}
	}
	
	
	/**
	 * Método que redireciona para outra página para o usuário incluir o anexo do exemplar selecionado
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaGerenciamentoMaterial.jsp
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
	 * Método chamado para incluir um anexo a um exemplar a partir da página de visualização
	 * das informações dos exemplares de um título
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaDetalhesMateriais.jsp
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
	 * Chama a página de pesquisa para o usuário escolher o Título do anexo
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoAnexo.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String abrirTelaSubstituicaoTitulo() throws SegurancaException, DAOException{
		
		PesquisaTituloCatalograficoMBean mBean = getMBean("pesquisaTituloCatalograficoMBean");
		
		/* Precisa guardar a operação anterior da página da pesquisa de título   //
		 * porque a operação vai ser substituída pela operação de pesquisar o Título do anexo     //
		 * depois de buscar o título do anexo, tem que voltar para a operação anterior caso o 
		 * usuário utilize o botão voltar
		 */
		//operacaoPesquisaAnterior = mBean.getOperacao();
		
		mBean.limpaResultadoPesquisa();
		return mBean.iniciarPesquisaSelecionarTitulo(this);
	}
	
	
	
	
	
//	/**
//	 * Método que substitui o Título do anexo por outro diferente do Título do exemplar principal.
//	 * Isso ocorre em poucos casos nos quais o exemplar e seu anexo são de títulos diferentes,
//	 * mas pode existir.
//	 *
//	 * <br/><br/>
//	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisao_acervo/resultadoPesquisaTituloCatalografico.jsp
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
//		/* Precisa guardar a operação anterior da página da pesquisa de título   //
//	     * porque a operação vai ser substituída pela operação de pesquisar o Título do anexo     //
//		 * depois de buscar o título do anexo, tem que voltar para a operação anterior caso o 
//		 * usuário utilize o botão voltar */
//		PesquisaTituloCatalograficoMBean mBean = getMBean("pesquisaTituloCatalograficoMBean");
//		mBean.setOperacao(operacaoPesquisaAnterior);
//		
//		return telaIncluirAnexoExemplar();
//	}
	
	
	
	
	/**
	 * Inicia os dados do exemplar anexo com os dados do exemplar principal. 
	 * O usuário poderá alterar antes de salvar.
	 */
	private void iniciaExemplarAnexo() throws DAOException{
		
		exemplarPrincipal = getGenericDAO().refresh(exemplarPrincipal);
		
		exemplarAnexo = new Exemplar();
		
		// Anexos não tem número de patrimônio
		// exemplarAnexo.setNumeroPatrimonio( XXXXXXXXXX );
		
		// Copia as informações do exemplar que foram preenchidas na tela
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
	 * Salva o exemplar anexo no banco, é preciso atualizar o exemplarPrincipal para 
	 * atualizar o número gerador do código de barras.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoAnexo.jsp
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
			
			addMensagemInformation(" Anexo incluído com sucesso. ");
			
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
	 * Método chamado quando o usuário clica no botão "voltar" da página de adição de materiais vindo
	 * da página de catalogação do Título.
	 * 
	 * Nesse caso precisa voltar para a página para o usuário editar novamente o Título.  
	 * Sempre "editar o título" porque se ele chegou até a página da inclusão de materiais é porque 
	 * o Título já foi salvo.
	 * 
	 * Como o MBean da catalogação ficou sendo mantido vivo na página de adição de materiais
	 * com o uso do <code>a4j:keepAlive</code>, as informações de é catalogação por 
	 * tombamento ou sem tombamento já estão lá no MBean. 
	 * <br/><br/>
	 * Chamado a partir da página: <ul><li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplares.jsp</li>
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
				tituloDao.close(); // Precisa fechar porque vai abrir outra sessão para o mesmo objeto e vai dar erro
		}
		
		return bean.iniciarParaEdicao(titulo);
	}
	
	
	
	/**
	 * Método chamado quando o usuário clica no botão para catalogar o próximo título não finalizado.
	 * 
	 * Esse botão só é habilitado quando existe títulos não finalizados. É uma maneira de catalogar o
	 * próximo título não finalizado sem precisar voltar para a página inicial do sistema.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplares.jsp
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
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoAnexo.jsp
	 * @return
	 */
	public String voltarTelaExemplar(){
		return telaIncluirNovoMaterial();
	}
	
	
	
	
	/**
	 * 
	 *    <p>Normalmente chamado quando se está voltando da página de edição das informações de algum exemplar.</p>
	 * 
	 *    <p>Além de voltar para a tela de inclusão de novos exemplares é preciso recarregar as informações
	 *  dos exemplares do Título, pois alguma dessas informações podem ter sido alteradas.</p>
	 *
	 *    Método chamado de nenhuma jsp.
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
	 * Retorna todos os números de patrimônio vindos do SIPAC. Usado na inclusão de exemplares 
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaGerenciamentoMaterial.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem>  getAllNumeroPatrimonioNaoUsadosComboBox(){
		
		// Ordena pela chave (id do bem)
		// Se o bem que tiver o menor id tiver o menor número de patrimônio vai dar certo, parece que
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
	 * Método que checa algumas informações preenchidas pelo usuário. 
	 * As outras validações estão no método validate do próprio objeto.
	 * 
	 * 
	 * @throws DAOException 
	 * 
	 */
	private boolean usuarioPreencheuDadosExemplarCorretamente(Exemplar e, boolean anexo){
		
		boolean contenErro = false;
		
		
		if( e.getNotaUsuario() != null && e.getNotaUsuario().length() > 100 ){
			addMensagemErro("O tamanho máximo do campo Nota ao Usuário é de 100 caracteres.");
			contenErro = true;
		}
		
		if( e.getNotaGeral() != null && e.getNotaGeral().length() > 200){
			addMensagemErro("O tamanho máximo do campo Nota Geral é de 200 caracteres.");
			contenErro = true;
		}
		
		if( e.getTipoMaterial().getId() == -1){
			addMensagemErro("É preciso informar o tipo de material do exemplar");
			contenErro = true;
		}
		
		if ( e.getSituacao().getId() == -1){
			addMensagemErro("É preciso informar a situação do exemplar"); 
			contenErro = true;
		}
		
		if ( StringUtils.isEmpty(e.getNumeroChamada())){
			addMensagemErro("É preciso informar o número de chamdada do exemplar ");
			contenErro = true;
		}
		
		if ( e.getStatus().getId() == -1){
			addMensagemErro("É preciso informar o status do exemplar "); 
			contenErro = true;
		}
		
		if ( e.getColecao().getId() == -1){
			addMensagemErro("É preciso informar a coleção do exemplar ");
			contenErro = true;
		}
		
		if ( e.getBiblioteca().getId() == -1){
			addMensagemErro("É preciso informar a  biblioteca onde o exemplar está localizado");
			contenErro = true;
		}
		
		if(! anexo){
			if( incluindoMateriaisSemTombamento && StringUtils.isEmpty(e.getCodigoBarras())){
				addMensagemErro("Informe o código de barras do exemplar");
				contenErro = true;
			}
		}
		
		
		if(! anexo && ! incluindoMateriaisSemTombamento){
		
			if( idsExemplaresEscolhidos == null || idsExemplaresEscolhidos.size() == 0){
				addMensagemErro("Selecione pelo menos um número do patrimônio");
				contenErro = true;
			} 
		
		}
		
		return ! contenErro ;
	}
	
	
	
	/**
	 * Verifica se os dados do fascículo foram informados corretamente
	 */
	private boolean usuarioPreencheuDadosFasciculoCorretamente(){

		boolean contenErro = false;
		
		if ( fasciculoSelecionado.getColecao().getId() == -1){
			addMensagemErro("É preciso informar a coleção do fascículo ");
			contenErro = true;
		}
		
		if ( fasciculoSelecionado.getStatus().getId() == -1){
			addMensagemErro("É preciso informar o status do fascículo "); 
			contenErro = true;
		}
		
		if ( fasciculoSelecionado.getSituacao().getId() == -1){
			addMensagemErro("É preciso informar a situação do fascículo"); 
			contenErro = true;
		}
		
		if ( fasciculoSelecionado.getBiblioteca().getId() == -1){
			addMensagemErro("É preciso informar a  biblioteca onde o fascículo está localizado");
			contenErro = true;
		}
		
		if( fasciculoSelecionado.getTipoMaterial().getId() == -1){
			addMensagemErro("É preciso informar o tipo de material do fascículo");
			contenErro = true;
		}
		
		
		if( fasciculoSelecionado.getNotaGeral() != null && fasciculoSelecionado.getNotaGeral().length() > 200){
			addMensagemErro("O tamanho máximo do campo Nota Geral é de 200 caracteres.");
			contenErro = true;
		}
		
		if( fasciculoSelecionado.getNotaUsuario() != null && fasciculoSelecionado.getNotaUsuario().length() > 100 ){
			addMensagemErro("O tamanho máximo do campo Nota ao Usuário é de 100 caracteres.");
			contenErro = true;
		}
		
		if( StringUtils.isEmpty( fasciculoSelecionado.getNumero() ) && StringUtils.isEmpty( fasciculoSelecionado.getVolume() ) && fasciculoSelecionado.getAnoCronologico() == null && StringUtils.isEmpty( fasciculoSelecionado.getAno() ) && StringUtils.isEmpty( fasciculoSelecionado.getEdicao() ) ){
			addMensagemErro("Para incluir um fascículo no acervo, pelo menos um dos seguintes campos deve ser informado: Ano Cronológico, Ano, Volume, Número ou Edição.");
			contenErro = true;
		}
		
		return ! contenErro ;
	}
	
	
	/**
	 * Limpa os dados sempre antes de entrar na página 
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
	 * Prepara para incluir um novo fascículo se o usuário desejar
	 *  
	 * @throws DAOException
	 */
	private void limpaDadosFasciculo() throws DAOException{
		fasciculoSelecionado = null;
		
		fasciculosDaAssinaturaNaoInclusos = getDAO(FasciculoDao.class).findFasciculosRegistradosDaAssinatura(assinaturaSelecionada.getId());
		
		Collections.sort(fasciculosDaAssinaturaNaoInclusos, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
	}
	
	
	
	/**
	 *   Implementa a ação de habilitar ou desabilitar a visualiação dos exemplares do Título
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *    Método que retorna o texto que é mostrado no link que habilita ou desabilita a visualização dos exemplares.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnivos/catalogacao/paginaInclusaoExemplares.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String getTextoLinkHabilitarExemplares(){
		if(! mostrarExemplaresDoTitulo)
			return "Título possui "+(exemplaresDoTitulo!= null ? exemplaresDoTitulo.size() : 0)+" exemplar(es), clique aqui para visualizá-lo(s).";
		else
			return "Ocultar os exemplares do Título";
	}
	
	
	
	// Métodos dos combo box's da tela //
	
	/**
	 * 
	 * Retorna todas as bibliotecas internas cuja unidade o usuário tem papel de "catalogação bibliotecário".
	 * Então vai aparecer somente as bibliotecas que ele tem permissão para incluir materiais.
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
	 * Retorna todas as situações que o usuário pode atribuir a um material na criação. 
	 * Não retorna a situação de baixa nem as de empréstimo, pois o usuário não vai poder criar 
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
	 * Retorna todas as coleções cadastradas
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
	 * Retorna a quantidade de materiais não inseridos ainda no acervo 
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
	 * Retorna a quantidade de fascículos registrados pelo setor de aquisição, mas não incluídos no 
	 * acervo ainda pelo setor de catalogação.
	 *
	 * @return
	 */
	public int getQuantidadeFasciculosNaoInclusosNoAcervo(){
		if (fasciculosDaAssinaturaNaoInclusos != null)
			return fasciculosDaAssinaturaNaoInclusos.size();
		else
			return 0;
	}
	
	
	
	/////////////////////////// Tela de navegação ////////////////////////////////////
	
	/**
	 * Método invocado de nenhuma página jsp
	 */
	public String telaIncluirNovoMaterial(){
		return forward(PAGINA_INCLUSAO_MATERIAIS);
	}
	
	
	
	/**
	 * Método invocado de nenhuma página jsp
	 */
	public String telaConfirmaInclusaoExemplaresOutraUnidades(){
		return forward(PAGINA_CONFIRMA_UNIDADE_DOS_MATERIAIS);
	}
	

	/**
	 * Chamado a partir da página: 
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
	 * Precisa guardar a operação anterior da página da pesquisa de título   //
     * porque a operação vai ser substituída pela operação de pesquisar o Título do anexo     //
	 * depois de buscar o título do anexo, tem que voltar para a operação anterior caso o 
	 * usuário utilize o botão voltar
	 * </p>
	 * 
	 * Chamado a partir da página: 
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

	/** Seta as formas de documento escolhidas pelo usuário eliminando os ids negativos que não são formas de documento válidas. */
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

	/** Configura que se está incluíndo o material a partir da tela de busca. */
	public void setIncluindoMateriaisApartirTelaBusca(boolean incluindoMateriaisApartirTelaBusca) {
		this.incluindoMateriaisApartirTelaBusca = incluindoMateriaisApartirTelaBusca;
		this.incluindoMateriaisApartirTelaCatalogacao = ! incluindoMateriaisApartirTelaBusca;
	}	
	
	/** Configura que se está incluíndo o material a partir da tela de catalogação. */
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
	
	
	//////////////////Métodos da interface de busca no acervo  ///////////////////////

	
	
	/**
	 * <p>Chamado da pesquisa no acervo para configurar o título do anexo do exemplar com o título escolhido pelo usuário.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#setTitulo(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc)
	 */
	@Override
	public void setTitulo(CacheEntidadesMarc cache) throws ArqException {
		tituloDoAnexo = getGenericDAO().findByPrimaryKey(cache.getIdTituloCatalografico(), TituloCatalografico.class);
		
	}

	
	/**
	 * <p>Chamado da pesquisa no acervo para realizar a operação de substituir o título de exemplar anexo.</p>
	 *
	 * <p>Método não chamado por página jsp.</p>
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
	 * Se na busca de títulos para substituir o Título do Anexo o usuário utilizar o botão voltar, vote para a tela de incluir anexo..<br/>
	 *
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Configura que o botão voltar na pesquisa de títulos para substituir o título do anexo vai ser utilizado.
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return true;
	}
	
	////////////Fim dos métodos padrão da interface de busca no acervo //////////////////
	
	
	
	
	/**
	 * <p>Precisa Volta à pesquisa Normal se voltou da tela de pesquisa de Título para substituíção do Título de um anexo.</p>
	 * 
	 * <p>Isso é necessário porque a tela de busca no acervo é usada 2 vezes nesse casos de uma para operações diferentes.</p>
	 */
	private void configuraTelaPesquisaParaPesquisaNormal(){
		
		PesquisaTituloCatalograficoMBean mBean = getMBean("pesquisaTituloCatalograficoMBean");
		mBean.setMbeanChamadoPesquisaTitulo(null);
		mBean.setOperacao(PesquisaTituloCatalograficoMBean.PESQUISA_NORMAL);
		mBean.limpaResultadoPesquisa();
	}
	
	
}

