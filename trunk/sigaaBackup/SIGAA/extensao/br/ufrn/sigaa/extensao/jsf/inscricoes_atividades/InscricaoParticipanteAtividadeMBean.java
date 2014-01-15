/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 31/10/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import java.util.Date;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.rh.dominio.Servidor;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoAtividadeParticipanteDao;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.AtividadeUnidadeDao;
import br.ufrn.sigaa.extensao.dao.LocalRealizacaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoParticipanteAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoInscreveParticipanteCursoEventoExtensao;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;

/**
 *
 * <p>MBean respons�vel por gerenciar <strong> *** APENAS *** </strong> as inscri��es em cursos e eventos de extens�o. </p>
 *
 * <p> <i> Esse MBean gerencia as inscri��es que o usu�rio vai fazer em cursos e eventos de extens�o na �rea interna 
 *     da parte p�blica do SIGAA</i>     
 * </p>
 * 
 * <p> <i> Para fazer a inscri��o o usu�rio precisa ter feito o seu cadastro e est� "logado" na �rea interna da parte p�blica.</i>     
 * </p>
 * 
 * @author jadson
 *
 */
@Component("inscricaoParticipanteAtividadeMBean")
@Scope("session")
public class InscricaoParticipanteAtividadeMBean extends AbstractInscricaoParticipanteMBean{

	/** Lista os per�odos de inscri��o abertos para  cursos e eventos de  extens�o na �rea p�blica. Para todo mundo ver, mesmo n�o estando logado.  */
	public final static String PAGINA_LISTA_PERIODOS_INSCRICOES_ATIVIDADES_PUBLICO = "/public/extensao/paginaListaPeriodosInscricoesAtividadesPublico.jsp";
	
	/** Lista os per�odos de inscri��o abertos para  cursos e eventos de  extens�o. (Quando o usu�rio est� logado na �rea interna.) */
	public final static String PAGINA_LISTA_PERIODOS_INSCRICOES_ATIVIDADES_ABERTOS = "/public/extensao/paginaListaPeriodosInscricoesAbertosAtividades.jsp";
	
	/** Formul�rio de inscria��o em um curso ou evento de extens�o */
	public final static String PAGINA_REALIZA_INSCRICOES_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/paginaRealizaInscricaoCursosEventosExtensao.jsp";
	
	/** Visualiza os dados cujo curso ou evento est� aberto */
	public final static String PAGINA_VISUALIZA_DADOS_CURSO_EVENTO_EXTENSAO	 =  "/public/extensao/viewDadosCursoEvento.jsp";
	
	/** Atividade selecionada para visualizar os seus dados */
	private AtividadeExtensao atividadeSelecionada;
	
	/** Atividade selecionada para visualizar os seus dados */
	private int idAtividadeSelecionada;
	
	
	/////////////////////////// Dados par filtrar os per�odos de inscri��o  /////////////////////////////
	
	/** Dados par filtrar os per�odos de inscri��o */
	private boolean checkBuscaTitulo;
	/** Dados par filtrar os per�odos de inscri��o */
	private String tituloAtividade;
	/** Dados par filtrar os per�odos de inscri��o */
	private boolean checkBuscaTipoAtividade;
	/** Dados par filtrar os per�odos de inscri��o */
	private int idTipoAtividade;
	/** Dados par filtrar os per�odos de inscri��o */
	private boolean checkBuscaAreaTematica;
	/** Dados par filtrar os per�odos de inscri��o */
	private int idAreaTematica;
	/** Dados par filtrar os per�odos de inscri��o */
	private boolean checkBuscaCoordenador;
	/** Dados par filtrar os per�odos de inscri��o */
	private Servidor coordenador = new Servidor();
	/** Dados par filtrar os per�odos de inscri��o */
	private boolean checkBuscaPeriodo;
	/** Dados par filtrar os per�odos de inscri��o */
	private Date dataInicioPeriodoInscricao;
	/** Dados par filtrar os per�odos de inscri��o */
	private Date dataFimPeriodoInscicao;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * <p>Carrega os per�odos de inscri��o de extens�o para os usu�rios n�o logados.</p>
	 * 
	 * <p>Essa opera��o permite ao usu�rio apenas visualzar os cursos e eventos abertos.</p>
	 * 
	 * <p>Consulta usada por todos que acessem o SIGAA. Para realizar a inscri��o o usu�rio vai precisar se logar </p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/paginaListaPeriodosInscricoesAtividadesPublico.jsf</li>
	 * </ul>
	 * @throws DAOException 
	 */
	public String getCarregaAtividadesPeriodosInscricaoAbertos() throws DAOException {
		
		if(getQtdPeriodosInscricaoAbertos() == 0){
			buscaPeriodosInscricoesAbertos(0);
		}
		return "";
	}
	
	
	
	
	/**
	 * <p>Inicia o caso de uso para se inscreve em um curso ou evento de inscri��o que esteja com o per�odo 
	 * de inscri��es aberto no momento.</p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/painelLateralAreaInternaCursosEventosExtensao.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 */
	public String iniciaInscricaoCursosEEventosAbertos() throws DAOException {
		
		GerenciaAreaInternaCursosEventoExtensaoMBean mBean = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		buscaPeriodosInscricoesAbertos(mBean.getParticipanteLogadoAreaInterna().getId());
		
		return telaListaPeriodosInscricoesAtividadesAbertos();
		
	}
	
	
	/**
	 * M�todo que realizar a busca quando o usu�rio filtra a busca pelo formul�rio de busca na p�gina
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/public/extensao/pagianListaPeriodosInscricoesAtividadesPublico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @param evt
	 * @throws DAOException
	 */
	public void buscaPeriodosInscricoesAbertos(ActionEvent evt) throws DAOException{
		
		GerenciaAreaInternaCursosEventoExtensaoMBean mBean = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		
		if(mBean.getParticipanteLogadoAreaInterna() != null)
			buscaPeriodosInscricoesAbertos(mBean.getParticipanteLogadoAreaInterna().getId());
		else
			buscaPeriodosInscricoesAbertos(0);
	}
	
	
	
	/** M�todo que realizar de fato a busca de per�odos de inscri��es abertos */
	private void buscaPeriodosInscricoesAbertos(int idCadastroParticipanteLogado) throws DAOException{
		
		
		if(! checkBuscaTitulo) tituloAtividade= null;
		if(! checkBuscaTipoAtividade) idTipoAtividade = 0;
		if(! checkBuscaAreaTematica) idAreaTematica = 0;
		
		if(! checkBuscaCoordenador) coordenador = new Servidor();
		if(! checkBuscaPeriodo) dataInicioPeriodoInscricao = dataFimPeriodoInscicao = null;
		
		InscricaoParticipanteAtividadeExtensaoDao dao = null;
		
		try {
			
			dao = getDAO(InscricaoParticipanteAtividadeExtensaoDao.class);	
			periodosInscricoesAbertos = dao.findAtividadesComPeriodosDeInscricoesAbertos(tituloAtividade, idTipoAtividade, idAreaTematica, coordenador.getId(),
					dataInicioPeriodoInscricao, dataFimPeriodoInscicao, idCadastroParticipanteLogado);
			
		} finally {
			if(dao != null) dao.close();
		}		
	}

	public String visualizarDadosAcaoCursoEvento() throws DAOException {
		visualizacao(idAtividadeSelecionada);
		atividadeSelecionada.getAtividades().iterator();
		atividadeSelecionada.getAtividadesPai().iterator();
		atividadeSelecionada.getProjeto().getEquipe().iterator();
		atividadeSelecionada.getProjeto().getFotos().iterator();
		
		atividadeSelecionada.setLocalRealizacao(null);

		if (hasOnlyErrors()) {
			return null;
		}
		return redirectJSF("/sigaa/public/extensao/viewDadosCursoEvento.jsf");
	}

	/**
	 * <p>M�todo que popula os dados de uma atividade ineficientemente para visualiza��o do usu�rio.</p>
	 * 
	 * <p>Essas consultas precisam ser otimizadas</p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li> /sigaa.war/public/extensao/paginaListaPeriodosInscricoesAtividadesAbertos.jsp</li>
	 *   </ul>
	 * @throws DAOException 
	 * 
	 */
	public String visualizarDadosCursoEvento() throws DAOException {
		idAtividadeSelecionada = getParameterInt("idAtividadeExtensaoSelecionada", 0);
		visualizacao(idAtividadeSelecionada);
		if (hasOnlyErrors()) {
			return null;
		}
		return forward(PAGINA_VISUALIZA_DADOS_CURSO_EVENTO_EXTENSAO);
	}

	private void visualizacao(Integer idAtividadeExtensaoSelecionada) throws DAOException {

		if (idAtividadeExtensaoSelecionada != null && idAtividadeExtensaoSelecionada > 0) {
			LocalRealizacaoDao dao = getDAO(LocalRealizacaoDao.class);
			AtividadeUnidadeDao atividadeDao = getDAO(AtividadeUnidadeDao.class);
			try {
				atividadeSelecionada = dao.findAndFetch(idAtividadeExtensaoSelecionada, AtividadeExtensao.class, "projeto.areaConhecimentoCnpq");
				
				atividadeSelecionada.setOrcamentosDetalhados(dao.findByExactField(OrcamentoDetalhado.class, "projeto.id", atividadeSelecionada.getProjeto().getId()));
				atividadeSelecionada.setLocaisRealizacao( dao.findLocaisRealizacao(atividadeSelecionada) );
				atividadeSelecionada.setUnidadesProponentes( (atividadeDao.findAtividades(atividadeSelecionada) ));
				
				// carregando todos os objetivos pra evitar erro de lazy
				if ((atividadeSelecionada.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO) && (atividadeSelecionada.getProjetoExtensao() != null)) {
					atividadeSelecionada.setObjetivo(dao.findByExactField( Objetivo.class,"atividadeExtensao.id",atividadeSelecionada.getProjetoExtensao().getId()));

					for (Objetivo objetivo : atividadeSelecionada.getObjetivo())
						objetivo.getAtividadesPrincipais().iterator();

				}
				

			} finally{
				if(dao != null) dao.close();
			}
			
		} else{
			addMensagemErro("A��o de extensao n�o selecionada");
			return;
		}
	}
	
	
	/**
	 * <p>Redireciona para a p�gina na qual o usu�rio vai confirma a inscri��o.</p>
	 * 
	 * <p>Mostras as informa��es do per�odo de inscri��o, como valor da inscri��o. Se a inscri��o 
	 * possuir algum question�rio relacionado o usu�rio deve responde aqui tamb�m.</p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> /sigaa.war/public/extensao/paginaListaPeriodosInscricoesAtividadesAbertos.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String preIncreverParticipante() throws ArqException {

		prepareMovimento(SigaaListaComando.INSCREVE_PARTICIPANTE_ATIVIDADE_EXTENSAO);
		
		idMolidadeParticipanteSelecionada = -1;
		
		int idPeriodoAbertoSelecionado = getParameterInt("idPeriodoAbertoSelecionado");
		
		InscricaoAtividade periodoInscricaoSelecionado = null;
		
		for (InscricaoAtividade periodo : periodosInscricoesAbertos) {
			if(periodo.getId() == idPeriodoAbertoSelecionado){
				periodoInscricaoSelecionado = periodo;
			}
		}
		
		if(periodoInscricaoSelecionado == null){
			addMensagemErro("Selecione um per�odo de inscri��o.");
			return null;
		}
		
		
		obj = new InscricaoAtividadeParticipante();
		GerenciaAreaInternaCursosEventoExtensaoMBean mBean = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		obj.setCadastroParticipante(mBean.getParticipanteLogadoAreaInterna());
		
		
		InscricaoAtividadeExtensaoDao dao = null;
		InscricaoAtividade periodoInscricao = null;
		QuestionarioDao daoQuestionario = null;
		InscricaoAtividadeParticipanteDao daoParticipante = null;
		
		try{
			
			dao = getDAO(InscricaoAtividadeExtensaoDao.class);
			daoQuestionario = getDAO(QuestionarioDao.class);
			daoParticipante = getDAO(InscricaoAtividadeParticipanteDao.class);
			
			periodoInscricao = dao.findInformacoesInscricoesAtividadeParaAlteracao(periodoInscricaoSelecionado.getId());
			
			// As informa��es transientes que n�o vem na consulta //
			periodoInscricao.setQuantidadeInscritos( periodoInscricaoSelecionado.getQuantidadeInscritos() );
			periodoInscricao.setQuantidadeInscritosAprovados( periodoInscricaoSelecionado.getQuantidadeInscritosAprovados() );
			
			if( periodoInscricao.getQuestionario() !=  null){
			    // Buscas as informa��es do question�rio //
				periodoInscricao.setQuestionario(daoQuestionario.findInformacaoQuestionario(periodoInscricao.getQuestionario().getId()));
			} 
			
			periodoInscricao.setAtividade(periodoInscricaoSelecionado.getAtividade());
			
			obj.setInscricaoAtividade(periodoInscricao);
			
			// Verificas se o participante j� se inscreveu nesse atividade
			inscricoesAnteriores = daoParticipante
					.findInscricoesAtivasParticipanteAtividade(obj.getInscricaoAtividade().getAtividade().getId(), obj.getCadastroParticipante().getId());
			inscricoesAnterioresSubAtividades = daoParticipante.findInscricoesParticipanteAtivasSubAtividadeDaAtividade(obj.getInscricaoAtividade().getAtividade().getId(), obj.getCadastroParticipante().getId());
			
			if(inscricoesAnteriores.size() > 0){
				addMensagemWarning("Voc� j� realizou uma inscri��o para essa atividade, caso se inscreva de novo as anteriores ser�o canceladas.");
			}
			
		}finally{
			if(dao != null) dao.close();
			if(daoQuestionario != null) daoQuestionario.close();
			if(daoParticipante != null) daoParticipante.close();
		}
		
		
		
		
		
		
		// Carregas as informa��es do question�rio a ser respondido pelo usu�rio
		if ( periodoInscricao.getQuestionario() !=  null ) {
			((QuestionarioRespostasMBean) getMBean("questionarioRespostasBean")).inicializarQuestionarioAtividade(obj);
		}
		
		
		/*
		 *  Esse n�mero s�o transientes e s�o recuperados na busca desse caso de uso, se n�o 
		 *  recuperar na busca esse calculo n�o vai funcionar
		 */
		if (  periodoInscricaoSelecionado.getQuantidadeVagasRestantes() <= 0   ){
			
//			if(periodoInscricaoSelecionado.getMetodoPreenchimento() == InscricaoAtividade.COM_CONFIRMACAO){
//			
//				addMensagemWarning("Esse curso ou evento possui o n�mero de inscri��o realizadas maior que o n�mero de vagas dispon�veis por�m, ainda est� aberta para inscri��es. " +
//					 " Sua aceita��o como participante estar� dependente do aumento no n�mero de vagas pelo coordenador ou a desist�ncia de algum participante.");
//			}
			
			// Se � autom�tico o preencimento da vagas, j� bloqueia a inscri��o do usu�rio
//			if(periodoInscricaoSelecionado.getMetodoPreenchimento() == InscricaoAtividade.PREENCHIMENTO_AUTOMATICO ){
				addMensagemWarning("Esse curso ou evento encontra-se com o n�mero de vagas esgotado. ");
				return null;
//			}
			
		}
		
		return telaRealizaInscricaoCursosEventosExtensao();
		
	}
	
	/**
	 *  <p>Cria a inscri��o do usu�rio validando todas as regras de neg�cio.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaRealizaInscricaoCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String inscreverParticipante() throws ArqException{
		
		MovimentoInscreveParticipanteCursoEventoExtensao movimento = new MovimentoInscreveParticipanteCursoEventoExtensao(obj, inscricoesAnteriores, inscricoesAnterioresSubAtividades);
		
		try {
			
			if( obj.getInscricaoAtividade().isCobrancaTaxaMatricula() &&  ! usuarioConcordaCondicoesPagamento ){
				addMensagemErro("Concorde com os termos da inscri��o.");
				return null;
			}
			
			if(obj.getInscricaoAtividade().getQuestionario() != null )
				obj.setQuestionarioRespostas(((QuestionarioRespostasMBean) getMBean("questionarioRespostasBean")).getObj());
			
			execute(movimento);
			
			
			if(obj.getInscricaoAtividade().getQuantidadePeriodosInscricoesMiniAtividade() > 0 ){
				addMensagemInformation("Essa atividade possue mini atividades com per�odos de inscri��o abertos.");
			}
			
			
			if( obj.getInscricaoAtividade().getMetodoPreenchimento() == InscricaoAtividade.COM_CONFIRMACAO 
					||  obj.getInscricaoAtividade().isCobrancaTaxaMatricula() ){
				addMensagemInformation("Sua inscri��o est� pendente de aprova��o pelo coordenador da atividade!");
				addMensagemInformation("Inscri��o submetida com sucesso!");
			}else{
				addMensagemInformation("Inscri��o realizada com sucesso!");
			}
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		GerenciaMeusCursosEventosExtensaoMBean mbean = getMBean("gerenciaMeusCursosEventosExtensaoMBean");
		mbean.carregarInscricoesParticipacoesSemInscricao();
		mbean.setDataInicioInscricao(new Date());
		mbean.setDataFinalInscricao(new Date());
		getCurrentRequest().setAttribute("idInscricaoParticipanteSelecionada", obj.getId());
		return mbean.acessarInscricaoSelecionada();

	}
	
	
	
	
	
	
	
	/**
	 * Tela de listagem.
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaRealizaInscricaoCursosEventosExtensao(){
		GerenciaAreaInternaCursosEventoExtensaoMBean gerenciaAreaInterna = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		return gerenciaAreaInterna.forwardPaginaInterna(PAGINA_REALIZA_INSCRICOES_CURSOS_EVENTOS_EXTENSAO);
	}
	

	/**
	 * Tela de listagem.
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaListaPeriodosInscricoesAtividadesAbertos(){
		GerenciaAreaInternaCursosEventoExtensaoMBean gerenciaAreaInterna = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		return gerenciaAreaInterna.forwardPaginaInterna(PAGINA_LISTA_PERIODOS_INSCRICOES_ATIVIDADES_ABERTOS);
	}
	
	public String getUrlAcesso() {
		return RepositorioDadosInstitucionais.getLinkSigaa() + "/sigaa/link/public/extensao/visualizacaoAcaoExtensao/" + idAtividadeSelecionada;
	}	

	public AtividadeExtensao getAtividadeSelecionada() {
		return atividadeSelecionada;
	}
	public void setAtividadeSelecionada(AtividadeExtensao atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}
	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}
	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}
	public boolean isCheckBuscaTipoAtividade() {
		return checkBuscaTipoAtividade;
	}
	public void setCheckBuscaTipoAtividade(boolean checkBuscaTipoAtividade) {
		this.checkBuscaTipoAtividade = checkBuscaTipoAtividade;
	}
	public int getIdTipoAtividade() {
		return idTipoAtividade;
	}
	public void setIdTipoAtividade(int idTipoAtividade) {
		this.idTipoAtividade = idTipoAtividade;
	}
	public String getTituloAtividade() {
		return tituloAtividade;
	}
	public void setTituloAtividade(String tituloAtividade) {
		this.tituloAtividade = tituloAtividade;
	}
	public boolean isCheckBuscaAreaTematica() {
		return checkBuscaAreaTematica;
	}
	public void setCheckBuscaAreaTematica(boolean checkBuscaAreaTematica) {
		this.checkBuscaAreaTematica = checkBuscaAreaTematica;
	}
	public int getIdAreaTematica() {
		return idAreaTematica;
	}
	public void setIdAreaTematica(int idAreaTematica) {
		this.idAreaTematica = idAreaTematica;
	}
	public boolean isCheckBuscaCoordenador() {
		return checkBuscaCoordenador;
	}
	public void setCheckBuscaCoordenador(boolean checkBuscaCoordenador) {
		this.checkBuscaCoordenador = checkBuscaCoordenador;
	}
	public Servidor getCoordenador() {
		return coordenador;
	}
	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}
	public boolean isCheckBuscaPeriodo() {
		return checkBuscaPeriodo;
	}
	public void setCheckBuscaPeriodo(boolean checkBuscaPeriodo) {
		this.checkBuscaPeriodo = checkBuscaPeriodo;
	}
	public Date getDataInicioPeriodoInscricao() {
		return dataInicioPeriodoInscricao;
	}
	public void setDataInicioPeriodoInscricao(Date dataInicioPeriodoInscricao) {
		this.dataInicioPeriodoInscricao = dataInicioPeriodoInscricao;
	}
	public Date getDataFimPeriodoInscicao() {
		return dataFimPeriodoInscicao;
	}
	public void setDataFimPeriodoInscicao(Date dataFimPeriodoInscicao) {
		this.dataFimPeriodoInscicao = dataFimPeriodoInscicao;
	}
	public int getIdAtividadeSelecionada() {
		return idAtividadeSelecionada;
	}
	public void setIdAtividadeSelecionada(int idAtividadeSelecionada) {
		this.idAtividadeSelecionada = idAtividadeSelecionada;
	}

}