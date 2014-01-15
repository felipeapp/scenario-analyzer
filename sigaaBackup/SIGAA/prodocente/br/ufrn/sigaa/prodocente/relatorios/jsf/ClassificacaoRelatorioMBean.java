/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/10/2007
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.jsf;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.tasks.TarefaAssincrona;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.prodocente.ClassificacaoRelatorioDao;
import br.ufrn.sigaa.arq.dao.prodocente.EmissaoRelatorioDao;
import br.ufrn.sigaa.arq.dao.prodocente.EmissaoRelatorioMediaDao;
import br.ufrn.sigaa.arq.dominio.CodigoOperacaoLote;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.batch.ProcessadorLote;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.RelatorioHelper;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.FiltroDocente;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorioMedia;
import br.ufrn.sigaa.prodocente.relatorios.dominio.RelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.TipoFiltroDocentes;

/**
 * Classe responsável pelo controle do cadastro de avaliações de relatórios dos rankings de docentes
 *
 * @author eric
 * @author Ricardo Wendell
 *
 */
@Component("classificacaoRelatorio") @Scope("request")
public class ClassificacaoRelatorioMBean  extends AbstractControllerProdocente<ClassificacaoRelatorio> {

	/**
	 * ApplicationContexto do spring que é necessário em alguns mappers que realizam consulta no SIGRH 
	 */
	//@Autowired
	private ApplicationContext ac;
	
	/**
	 * Filtros de docentes, utilizado para popular o select de filtros da jsp de geração do relatório
	 */
	private TipoFiltroDocentes tipoFiltro;
	
	/** Edital de Pesquisa selecionado pelo Usuário */
	private EditalPesquisa edital;
	
	/** Editais de Pesquisa selecionado pelo Usuário */
	private List<Integer> editaisSelecionados;

	/** Representa o relatório gerado do Ranking dos docentes */
	private ArrayList<EmissaoRelatorio> rankingDocentes = new  ArrayList<EmissaoRelatorio>();
	/** Representa a emissão do relatório de um docente   */
	private EmissaoRelatorio docenteDetalhes = new EmissaoRelatorio();
	/** Apresenta a média da emissão do relatório dos doscentes */
	private ArrayList<EmissaoRelatorioMedia> medias = new  ArrayList<EmissaoRelatorioMedia>();
	
	/** Caminho onde estão as views do caso de uso */
	private static final String CONTEXTO ="/prodocente/producao/relatorios/produtividade/ipi/";

	/** Formulário para a geração do relatório  */
	private static final String FORM = "selecao_relatorio.jsp";

	/** Ranking do relatório gerado */
	private static final String JSP_RANKING ="ranking.jsp";
	/** Ranking do relatório gerado agrupado por centro */
	private static final String JSP_RANKING_CENTROS ="ranking_centros.jsp";
	/** Ranking do relatório gerado agrupado por departamento */
	private static final String JSP_RANKING_DEPARTAMENTOS ="ranking_departamentos.jsp";
	/** Detalhamento do relatório gerado */
	private static final String JSP_DETALHES = "detalhes_docente.jsp";
	/** Possibilidade de alteração do IPI dos docentes */
	private static final String JSP_PRE_ALTERA_IPI ="pre_altera_ipi.jsp";
	/** Alteração do IPI dos docentes */
	private static final String JSP_ALTERA_IPI ="altera_ipi.jsp";

	/** Coleção dos relatórios de produtividade  */
	private Collection<ClassificacaoRelatorio> classificacoes;
	/** Usado para alterar o índice IPI do Docente */
	private EmissaoRelatorio emissaoRelatorio;
	/** Se é para geração da classificação */
	private boolean classificacao = true;
	/** Se é para geração de consulta */
	private boolean consulta = false;

	public ClassificacaoRelatorioMBean() throws SegurancaException {
		initObj();
	}

	/**
	 * Popula os dados iniciais utilizados nas operações do caso de uso.
	 * 
	 * @throws SegurancaException
	 */
	private void initObj() throws SegurancaException{
		tipoFiltro = new TipoFiltroDocentes();
		obj = new ClassificacaoRelatorio();
		obj.setRelatorioProdutividade( new RelatorioProdutividade(RelatorioProdutividade.RELATORIO_DISTRIBUICAO_COTAS) );
		obj.setAnoVigencia(CalendarUtils.getAnoAtual() - 1);
		obj.setEmissaoRelatorioCollection(new ArrayList<EmissaoRelatorio>());
		emissaoRelatorio = new EmissaoRelatorio();
		consulta = false;
		edital = new EditalPesquisa();
		ac = WebApplicationContextUtils.getWebApplicationContext(getCurrentSession().getServletContext());
	}
	
	/**
	 * Verifica se o usuário logado possui permissão de acesso a essa funcionalidade.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/prodocente.jsp</li>
	 *	</ul>
	 * 
	 * @return url inicial
	 * @throws ArqException
	 */
	public String verificarPermissao() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PESQUISA);
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return forward(CONTEXTO + "selecao_relatorio.jsp");	
	}

	/**
	 * Busca as classificações na base de dados e encaminha para a tela de listagem das mesmas.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/altera_ipi.jsp</li>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/pre_altera_ipi.0jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/prodocente.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String consultar() throws DAOException {
		classificacao = false;
		classificacoes = getDAO(ClassificacaoRelatorioDao.class).findByRelatorioAndAnoVigencia(obj);
		if (classificacoes.size() > 0) {
			consulta = true;	
		}else{
			addMensagemWarning("Não foram encontrados resultados para os critérios informados.");
		}
		return forward(CONTEXTO + "lista.jsp");
	}
	
	/**
	 * Retorna as classificações.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public Collection<ClassificacaoRelatorio> getClassificacoes() throws DAOException, SegurancaException {
		if (classificacao) {
			GenericDAO dao = getDAO(GenericDAOImpl.class);
			return classificacoes = dao.findByExactField(ClassificacaoRelatorio.class, "registroEntrada",obj.getRegistroEntrada());
		}
		return classificacoes;
	}
	
	/**
	 * Visualizar o ranking dos docentes de uma determinada classificação
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/lista.jsp</li>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/ranking_centros.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String montarRanking() throws ArqException{
		if ( !isAtivo() )
			return consultar();
		montarRanking(null);
		return forward(CONTEXTO + JSP_RANKING);
	}
	
	/**
	 * Redireciona para a JSP que lista o Ranking de IPI para poder ser alterado
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/lista.jsp</li>
	 *	</ul>
	 * 
	 */
	public String redirectPreAlterarIPI() throws ArqException	{
		if ( !isAtivo() )
			return consultar();
		montarRankingCentro();
		return forward(CONTEXTO + JSP_PRE_ALTERA_IPI);
	}

	/**
	 * Redireciona para a JSP que irá alterar o IPI
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/pre_altera_ipi.jsp</li>
	 *	</ul>
	 * 
	 */
	public String preAlterarIPI() throws DAOException {
		EmissaoRelatorioDao dao = getDAO(EmissaoRelatorioDao.class);
		emissaoRelatorio =  dao.findByPrimaryKey(getParameterInt("id"), EmissaoRelatorio.class);
		return forward(CONTEXTO + JSP_ALTERA_IPI);
	}
	
	/**
	 * Executa o processador que irá cadastrar o novo valor do IPI no banco e recalcular o FPPI
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/altera_ipi.jsp</li>
	 *	</ul>
	 * 
	 */
	public String alterarIPI() throws ArqException, NegocioException {

		// Preparar objeto da geração de classificação para persistência
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(emissaoRelatorio);

		// Processador que altera o valor do IPI e logo após executa o re-cálculo do FPPI
		mov.setCodMovimento(SigaaListaComando.ALTERAR_IPI);
		prepareMovimento(SigaaListaComando.ALTERAR_IPI);
		emissaoRelatorio =  (EmissaoRelatorio) execute(mov, getCurrentRequest());
		addMensagemInformation("Índice de produtividade de "+emissaoRelatorio.getServidor().getNome()+" alterado com sucesso.");
		
		
		montarRanking(TipoUnidadeAcademica.CENTRO, emissaoRelatorio.getClassificacaoRelatorio().getId());
		return forward(CONTEXTO + JSP_PRE_ALTERA_IPI);
	}

	/**
	 * Visualizar o ranking dos docentes de uma determinada classificação, agrupados por centros
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String montarRankingCentro() throws ArqException{
		if ( !isAtivo() )
			return consultar();
		montarRanking(TipoUnidadeAcademica.CENTRO);
		return forward(CONTEXTO + JSP_RANKING_CENTROS);
	}

	/**
	 * Visualizar o ranking dos docentes de uma determinada classificação, agrupados por departamentos
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String montarRankingDepartamento() throws ArqException{
		if ( !isAtivo() )
			return consultar();
		montarRanking(TipoUnidadeAcademica.DEPARTAMENTO);
		return forward(CONTEXTO + JSP_RANKING_DEPARTAMENTOS);
	}
	
	/**
	 * Serve para exportar o relatório espelhado para o formato Excel.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws Exception
	 */
	public String exportarRelatorio() throws Exception {
		if ( !isAtivo() )
			return consultar();
		RelatorioAvaliacaoExcelMBean mBean = getMBean("relatorioAvaliacaoExcelMBean");
		return mBean.buildSheet();
	}
	
	/**
	 * Visualizar o ranking dos docentes de um único centro
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/ranking_centros.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String montarRankingUnicoCentro() throws DAOException{
		ClassificacaoRelatorioDao dao = getDAO(ClassificacaoRelatorioDao.class);
		EmissaoRelatorioDao emissaoDao = getDAO(EmissaoRelatorioDao.class);
		EmissaoRelatorioMediaDao mediaDao = getDAO(EmissaoRelatorioMediaDao.class);

		try {
			obj = dao.findByPrimaryKey(obj.getId(), ClassificacaoRelatorio.class);
			Integer idCentro = getParameterInt("idCentro");
			rankingDocentes = (ArrayList<EmissaoRelatorio>) emissaoDao.findRankingByClassificacao(obj, TipoUnidadeAcademica.DEPARTAMENTO, idCentro);
			medias = (ArrayList<EmissaoRelatorioMedia>) mediaDao.findByClassificacao(obj);

		} finally {
			dao.close();
			emissaoDao.close();
			mediaDao.close();
		}
		return forward(CONTEXTO + JSP_RANKING_DEPARTAMENTOS);
	}

	/**
	 * Sobrecarga do método auxiliar para montar o ranking passando apenas o tipo da unidade acadêmica.
	 * 
	 * @param tipoUnidadeAcademica
	 * @throws DAOException
	 */
	private void montarRanking(Integer tipoUnidadeAcademica) throws ArqException {
		montarRanking(tipoUnidadeAcademica, getParameterInt("id"));
	}
	
	/**
	 * Método auxiliar utilizado para buscar os dados e montar o ranking do relatório informado de acordo 
	 * com o tipo da unidade acadêmica informada: TipoUnidadeAcademica.CENTRO ou TipoUnidadeAcademica.DEPARTAMENTO
	 * 
	 * @param tipoUnidadeAcademica
	 * @param id
	 * @throws DAOException
	 */
	private void montarRanking(Integer tipoUnidadeAcademica, int id) throws ArqException {
		ClassificacaoRelatorioDao dao = getDAO(ClassificacaoRelatorioDao.class);
		EmissaoRelatorioDao emissaoDao = getDAO(EmissaoRelatorioDao.class);
		EmissaoRelatorioMediaDao mediaDao = getDAO(EmissaoRelatorioMediaDao.class);

		try {
			obj = dao.findByPrimaryKey(id, ClassificacaoRelatorio.class);
			rankingDocentes = RelatorioHelper.montarRankingProdutividade(emissaoDao, obj, tipoUnidadeAcademica);
			medias = (ArrayList<EmissaoRelatorioMedia>) mediaDao.findByClassificacao(obj);

		} finally {
			dao.close();
			emissaoDao.close();
		}
	}

	
	/**
	 * Método para calcular o FPPI dos docentes rankeados no relatório de classificação.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String calculcarFPPI() throws ArqException {
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("id"), ClassificacaoRelatorio.class);

		if ( !isAtivo() )
			return consultar();
		
		// Preparar objeto da geração de classificação para persistência
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		// Executar rotinas de cálculo do FPPI
		mov.setCodMovimento(SigaaListaComando.CALCULAR_FPPI_CLASSIFICACAO);
		try {
			prepareMovimento(SigaaListaComando.CALCULAR_FPPI_CLASSIFICACAO);
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			return consultar();
		}
		return montarRanking();
	}
	

	/**
	 * Mostrar o resultado de uma avaliação para um determinado docente
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/ranking_centros.jsp</li>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/ranking_departamentos.jsp</li>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/ranking.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String mostrarDetalhesDocente() throws DAOException{

		int idEmissaoRelatorio = getParameterInt("id");
		EmissaoRelatorioDao dao = getDAO(EmissaoRelatorioDao.class);

		try {
			docenteDetalhes = dao.findByPrimaryKey(idEmissaoRelatorio, EmissaoRelatorio.class);
		} finally {
			dao.close();
		}

		return forward(CONTEXTO+JSP_DETALHES);
	}

	/**
	 * Método utilizado para calcular o IPI dos rankingDocentes de um relatório de uma unidade para um ano de vigência
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/selecao_relatorio.jsp</li>
	 *	</ul>
	 * 
	 * @return String
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String gerarClassificacao() throws NegocioException, ArqException, RemoteException {

		checkOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		if (hasErrors())
			return redirectJSF(getSubSistema().getLink());
		
		ValidatorUtil.validateRequiredId(tipoFiltro.getId(), "Filtro de Docentes", erros);
		ValidatorUtil.validateRequired(editaisSelecionados,  "Filtro de Editais", erros);
		ValidatorUtil.validateRequiredId(obj.getRelatorioProdutividade().getId(), "Relatório", erros);

		if (hasErrors())
			return null;
		
		// Preparar objeto da geração de classificação para persistência
		MovimentoCadastro mov = new MovimentoCadastro();
		obj.setDataClassificacao(new Date());
		obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		mov.setObjMovimentado(obj);

		// Persistir objeto referente a geração da classificação
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		try {
			obj = (ClassificacaoRelatorio) execute(mov);
		} catch (Exception e) {
			addMensagemErro("Erro Inesperado: " + e.getMessage());
			e.printStackTrace();
			return forward( CONTEXTO+FORM );
		}
		if (obj.getAnoVigencia() == null) {	
			addMensagemErro("Ano de Referência: Campo obrigatório não informado");
			return forward(CONTEXTO + FORM );
			
		} else if (obj.getAnoVigencia() > CalendarUtils.getAnoAtual() || obj.getAnoVigencia() <= 0) {	
			addMensagemErro("O campo \"Ano de Referência\" deve estar entre 0 e o ano atual (" + CalendarUtils.getAnoAtual() + ")");
			return forward(CONTEXTO + FORM );
		}
				
		UnidadeDao unidadeDao = getDAO(UnidadeDao.class);

		// Gerar índice de produtividade individual para os docentes e persistir o resultado
		try{

			// Popular lista de docentes de acordo com o filtro
			final Collection<Servidor> docentes = getDocentes();

			GenericDAO dao = getGenericDAO();
			RelatorioProdutividade relatorio = dao.findByPrimaryKey( obj.getRelatorioProdutividade().getId() , RelatorioProdutividade.class);
			relatorio.getGrupoRelatorioProdutividadeCollection().iterator();
			obj.setRelatorioProdutividade(relatorio);
			

			// Definição da tarefa em lote
			TarefaAssincrona<Void> tarefa = new TarefaAssincrona<Void>() {
				@Override
				public Void call() throws ArqException, NegocioException, RemoteException, InterruptedException {
					// Gerar o índice individual para os docentes encontrados
					MovimentoCadastro movClassificacao = new MovimentoCadastro();
					movClassificacao.setApplicationContext(ac);
					
					for (Servidor servidor : docentes) {

						EmissaoRelatorio emissaoRelatorio = new EmissaoRelatorio();
						emissaoRelatorio.setClassificacaoRelatorio(obj);
						emissaoRelatorio.setServidor(servidor);

						movClassificacao.setCodMovimento(SigaaListaComando.CALCULAR_IPI_DOCENTE);
						movClassificacao.setObjMovimentado(emissaoRelatorio);

						prepareMovimento(SigaaListaComando.CALCULAR_IPI_DOCENTE, getFacade(), getUsuario(), getSistema() );
						executeWithoutClosingSession(movClassificacao,  getFacade(), getUsuario(), getSistema() );
					}
					return null;
				}
			};

			// Popular a tarefa com os atributos necessários para a execução assíncrona
			inicializarTarefaAssincrona(tarefa, getCurrentRequest());

			// Executar operação em lote assincronamente
			try {
				ProcessadorLote processadorLote = new ProcessadorLote(tarefa,
						CodigoOperacaoLote.GERACAO_RELATORIO_PRODUTIVIDADE_DOCENTES,
						obj.getId(),
						docentes.size());
				new Thread( processadorLote ).start();
			} catch (NegocioException e) {
			}

			removeOperacaoAtiva();
			addMessage("Cálculo realizado com sucesso!", TipoMensagemUFRN.INFORMATION);
		} finally{
			unidadeDao.close();
		}
		return consultar();
	}

	/**
	 * Busca dos docentes de acordo com o filtro selecionado
	 *
	 * @return
	 * @throws DAOException
	 */
	private Collection<Servidor> getDocentes() throws DAOException {
		GenericDAO dao = getGenericDAO();
		tipoFiltro = dao.refresh(tipoFiltro);
		FiltroDocente filtro = ReflectionUtils.newInstance( tipoFiltro.getClasse() );
		Collection<Servidor> docentes = filtro.getDocentes(editaisSelecionados);
		return docentes;
	}
	
	/**
	 * Remove o bean da sessão e redireciona o usuário para
	 * a página inicial do subsistema atual.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/selecao_relatorio.jsp</li>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cancelar() {
		// removendo da sessão
		resetBean();
		
		try {
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Chama o processador para desativar a classificação selecionada.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/relatorios/produtividade/ipi/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String desativar() throws ArqException{
		
		if ( !isAtivo() )
			return consultar();
		
		obj.setId(getParameterInt("id"));

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.DESATIVAR);
		
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return consultar();
	}
	
	/**
	 * Serve para verificar se a classificação está ativa.
	 * @return
	 * @throws DAOException
	 */
	private boolean isAtivo() throws DAOException {
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("id"), ClassificacaoRelatorio.class);
		if ( !obj.isAtivo() )
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		return obj.isAtivo();
	}
	
	/**
	 * Retorna uma coleção de Tipos de Filtros de Docentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/selecao_relatorio.jsp</li>
	 *	</ul>
	 * @return
	 */
	public Collection<SelectItem> getFiltros() {
		return toSelectItems( TipoFiltroDocentes.getTipos() );
	}

	/**
	 * Retorna uma coleção de Editais de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/selecao_relatorio.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getEditaisCombo() throws ArqException {
		return toSelectItems( getDAO(EditalPesquisaDao.class).findByAno(CalendarUtils.getAnoAtual()) , "id", "descricao" );
	}
	
	/** 
	 * Para que o usuário retorne para a tela da listagem
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/pre_altera_ipi.jsp</li>
	 *	</ul>
	 *
	 * @throws DAOException 
	 */
	public String listar() throws DAOException {
		consultar();
		return null;
	}

	public ArrayList<EmissaoRelatorio> getRankingDocentes() {
		return rankingDocentes;
	}

	public void setRankingDocentes(ArrayList<EmissaoRelatorio> docentes) {
		rankingDocentes = docentes;
	}

	public EmissaoRelatorio getDocenteDetalhes() {
		return docenteDetalhes;
	}

	public void setDocenteDetalhes(EmissaoRelatorio docenteDetalhes) {
		this.docenteDetalhes = docenteDetalhes;
	}
	
	public TipoFiltroDocentes getTipoFiltro() {
		return tipoFiltro;
	}

	public void setTipoFiltro(TipoFiltroDocentes tipoFiltro) {
		this.tipoFiltro = tipoFiltro;
	}

	public ArrayList<EmissaoRelatorioMedia> getMedias() {
		return medias;
	}

	public void setMedias(ArrayList<EmissaoRelatorioMedia> medias) {
		this.medias = medias;
	}

	public EmissaoRelatorio getEmissaoRelatorio() {
		return emissaoRelatorio;
	}

	public void setEmissaoRelatorio(EmissaoRelatorio emissaoRelatorio) {
		this.emissaoRelatorio = emissaoRelatorio;
	}

	public EditalPesquisa getEdital() {
		return edital;
	}

	public void setEdital(EditalPesquisa edital) {
		this.edital = edital;
	}
	
	public List<Integer> getEditaisSelecionados() {
		return editaisSelecionados;
	}

	public void setEditaisSelecionados(List<Integer> editaisSelecionados) {
		this.editaisSelecionados = editaisSelecionados;
	}

	public void setClassificacoes(Collection<ClassificacaoRelatorio> classificacoes) {
		this.classificacoes = classificacoes;
	}

	public boolean isClassificacao() {
		return classificacao;
	}

	public void setClassificacao(boolean classificacao) {
		this.classificacao = classificacao;
	}

	public boolean isConsulta() {
		return consulta;
	}

	public void setConsulta(boolean consulta) {
		this.consulta = consulta;
	}

}