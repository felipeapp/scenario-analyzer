/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean responsável por gerenciar <strong> *** APENAS *** </strong> as inscrições em cursos e eventos de extensão. </p>
 *
 * <p> <i> Esse MBean gerencia as inscrições que o usuário vai fazer em cursos e eventos de extensão na área interna 
 *     da parte pública do SIGAA</i>     
 * </p>
 * 
 * <p> <i> Para fazer a inscrição o usuário precisa ter feito o seu cadastro e está "logado" na área interna da parte pública.</i>     
 * </p>
 * 
 * @author jadson
 *
 */
@Component("inscricaoParticipanteAtividadeMBean")
@Scope("session")
public class InscricaoParticipanteAtividadeMBean extends AbstractInscricaoParticipanteMBean{

	/** Lista os períodos de inscrição abertos para  cursos e eventos de  extensão na área pública. Para todo mundo ver, mesmo não estando logado.  */
	public final static String PAGINA_LISTA_PERIODOS_INSCRICOES_ATIVIDADES_PUBLICO = "/public/extensao/paginaListaPeriodosInscricoesAtividadesPublico.jsp";
	
	/** Lista os períodos de inscrição abertos para  cursos e eventos de  extensão. (Quando o usuário está logado na área interna.) */
	public final static String PAGINA_LISTA_PERIODOS_INSCRICOES_ATIVIDADES_ABERTOS = "/public/extensao/paginaListaPeriodosInscricoesAbertosAtividades.jsp";
	
	/** Formulário de inscriação em um curso ou evento de extensão */
	public final static String PAGINA_REALIZA_INSCRICOES_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/paginaRealizaInscricaoCursosEventosExtensao.jsp";
	
	/** Visualiza os dados cujo curso ou evento está aberto */
	public final static String PAGINA_VISUALIZA_DADOS_CURSO_EVENTO_EXTENSAO	 =  "/public/extensao/viewDadosCursoEvento.jsp";
	
	/** Atividade selecionada para visualizar os seus dados */
	private AtividadeExtensao atividadeSelecionada;
	
	/** Atividade selecionada para visualizar os seus dados */
	private int idAtividadeSelecionada;
	
	
	/////////////////////////// Dados par filtrar os períodos de inscrição  /////////////////////////////
	
	/** Dados par filtrar os períodos de inscrição */
	private boolean checkBuscaTitulo;
	/** Dados par filtrar os períodos de inscrição */
	private String tituloAtividade;
	/** Dados par filtrar os períodos de inscrição */
	private boolean checkBuscaTipoAtividade;
	/** Dados par filtrar os períodos de inscrição */
	private int idTipoAtividade;
	/** Dados par filtrar os períodos de inscrição */
	private boolean checkBuscaAreaTematica;
	/** Dados par filtrar os períodos de inscrição */
	private int idAreaTematica;
	/** Dados par filtrar os períodos de inscrição */
	private boolean checkBuscaCoordenador;
	/** Dados par filtrar os períodos de inscrição */
	private Servidor coordenador = new Servidor();
	/** Dados par filtrar os períodos de inscrição */
	private boolean checkBuscaPeriodo;
	/** Dados par filtrar os períodos de inscrição */
	private Date dataInicioPeriodoInscricao;
	/** Dados par filtrar os períodos de inscrição */
	private Date dataFimPeriodoInscicao;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * <p>Carrega os períodos de inscrição de extensão para os usuários não logados.</p>
	 * 
	 * <p>Essa operação permite ao usuário apenas visualzar os cursos e eventos abertos.</p>
	 * 
	 * <p>Consulta usada por todos que acessem o SIGAA. Para realizar a inscrição o usuário vai precisar se logar </p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Inicia o caso de uso para se inscreve em um curso ou evento de inscrição que esteja com o período 
	 * de inscrições aberto no momento.</p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método que realizar a busca quando o usuário filtra a busca pelo formulário de busca na página
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/public/extensao/pagianListaPeriodosInscricoesAtividadesPublico.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
	
	
	
	/** Método que realizar de fato a busca de períodos de inscrições abertos */
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
	 * <p>Método que popula os dados de uma atividade ineficientemente para visualização do usuário.</p>
	 * 
	 * <p>Essas consultas precisam ser otimizadas</p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Ação de extensao não selecionada");
			return;
		}
	}
	
	
	/**
	 * <p>Redireciona para a página na qual o usuário vai confirma a inscrição.</p>
	 * 
	 * <p>Mostras as informações do período de inscrição, como valor da inscrição. Se a inscrição 
	 * possuir algum questionário relacionado o usuário deve responde aqui também.</p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Selecione um período de inscrição.");
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
			
			// As informações transientes que não vem na consulta //
			periodoInscricao.setQuantidadeInscritos( periodoInscricaoSelecionado.getQuantidadeInscritos() );
			periodoInscricao.setQuantidadeInscritosAprovados( periodoInscricaoSelecionado.getQuantidadeInscritosAprovados() );
			
			if( periodoInscricao.getQuestionario() !=  null){
			    // Buscas as informações do questionário //
				periodoInscricao.setQuestionario(daoQuestionario.findInformacaoQuestionario(periodoInscricao.getQuestionario().getId()));
			} 
			
			periodoInscricao.setAtividade(periodoInscricaoSelecionado.getAtividade());
			
			obj.setInscricaoAtividade(periodoInscricao);
			
			// Verificas se o participante já se inscreveu nesse atividade
			inscricoesAnteriores = daoParticipante
					.findInscricoesAtivasParticipanteAtividade(obj.getInscricaoAtividade().getAtividade().getId(), obj.getCadastroParticipante().getId());
			inscricoesAnterioresSubAtividades = daoParticipante.findInscricoesParticipanteAtivasSubAtividadeDaAtividade(obj.getInscricaoAtividade().getAtividade().getId(), obj.getCadastroParticipante().getId());
			
			if(inscricoesAnteriores.size() > 0){
				addMensagemWarning("Você já realizou uma inscrição para essa atividade, caso se inscreva de novo as anteriores serão canceladas.");
			}
			
		}finally{
			if(dao != null) dao.close();
			if(daoQuestionario != null) daoQuestionario.close();
			if(daoParticipante != null) daoParticipante.close();
		}
		
		
		
		
		
		
		// Carregas as informações do questionário a ser respondido pelo usuário
		if ( periodoInscricao.getQuestionario() !=  null ) {
			((QuestionarioRespostasMBean) getMBean("questionarioRespostasBean")).inicializarQuestionarioAtividade(obj);
		}
		
		
		/*
		 *  Esse número são transientes e são recuperados na busca desse caso de uso, se não 
		 *  recuperar na busca esse calculo não vai funcionar
		 */
		if (  periodoInscricaoSelecionado.getQuantidadeVagasRestantes() <= 0   ){
			
//			if(periodoInscricaoSelecionado.getMetodoPreenchimento() == InscricaoAtividade.COM_CONFIRMACAO){
//			
//				addMensagemWarning("Esse curso ou evento possui o número de inscrição realizadas maior que o número de vagas disponíveis porém, ainda está aberta para inscrições. " +
//					 " Sua aceitação como participante estará dependente do aumento no número de vagas pelo coordenador ou a desistência de algum participante.");
//			}
			
			// Se é automático o preencimento da vagas, já bloqueia a inscrição do usuário
//			if(periodoInscricaoSelecionado.getMetodoPreenchimento() == InscricaoAtividade.PREENCHIMENTO_AUTOMATICO ){
				addMensagemWarning("Esse curso ou evento encontra-se com o número de vagas esgotado. ");
				return null;
//			}
			
		}
		
		return telaRealizaInscricaoCursosEventosExtensao();
		
	}
	
	/**
	 *  <p>Cria a inscrição do usuário validando todas as regras de negócio.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemErro("Concorde com os termos da inscrição.");
				return null;
			}
			
			if(obj.getInscricaoAtividade().getQuestionario() != null )
				obj.setQuestionarioRespostas(((QuestionarioRespostasMBean) getMBean("questionarioRespostasBean")).getObj());
			
			execute(movimento);
			
			
			if(obj.getInscricaoAtividade().getQuantidadePeriodosInscricoesMiniAtividade() > 0 ){
				addMensagemInformation("Essa atividade possue mini atividades com períodos de inscrição abertos.");
			}
			
			
			if( obj.getInscricaoAtividade().getMetodoPreenchimento() == InscricaoAtividade.COM_CONFIRMACAO 
					||  obj.getInscricaoAtividade().isCobrancaTaxaMatricula() ){
				addMensagemInformation("Sua inscrição está pendente de aprovação pelo coordenador da atividade!");
				addMensagemInformation("Inscrição submetida com sucesso!");
			}else{
				addMensagemInformation("Inscrição realizada com sucesso!");
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
	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
	 *   <p>Método não chamado por nenhuma página jsp.</p>
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