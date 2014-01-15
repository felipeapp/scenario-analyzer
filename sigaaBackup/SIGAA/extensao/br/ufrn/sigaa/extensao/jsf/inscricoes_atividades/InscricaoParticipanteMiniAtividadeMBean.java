/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 14/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoAtividadeParticipanteDao;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.SubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoParticipanteSubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoSubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoInscreveParticipanteMiniCursoEventoExtensao;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;

/**
 *
 * <p>MBean usado na �rea p�blica para gerenciar a inscri��o do participante um mini curso 
 * ou evento de extens�o. </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
@Component("inscricaoParticipanteMiniAtividadeMBean")
@Scope("request")
public class InscricaoParticipanteMiniAtividadeMBean extends AbstractInscricaoParticipanteMBean{

	
	/** Lista os per�odos de inscri��o abertos para  mini atividades. (Quando o usu�rio est� logado na �rea interna.) */
	public final static String PAGINA_LISTA_PERIODOS_INSCRICOES_ABERTOS_MINI_ATIVIDADES = "/public/extensao/paginaListaPeriodosInscricoesAbertosSubAtividades.jsp";
	
	/** Formul�rio de inscria��o em uma mini atividade de extens�o. */
	public final static String PAGINA_REALIZA_INSCRICOES_SUB_ATIVIDADES_EXTENSAO = "/public/extensao/paginaRealizaInscricaoSubAtividadeExtensao.jsp";
	
	/** Formul�rio de inscria��o em uma mini atividade de extens�o. */
	public final static String PAGINA_VISUALIZA_DADOS_SUB_ATIVIDADES_EXTENSAO = "/public/extensao/viewDadosMiniAtividade.jsp";
	
	
	/** Guarda o id da atividade de extens�o selecionada, a partir desse ai vai recuperar as mini atividade com inscri��es abertas */
	private int idAtividadePaiSelecionada;
	
	/** Informa se a inscri��o em mini atividades est� habilitada, essa vari�vel � setada para "true" se o usu�rio j� se inscreveu na atividade pai. */
	private boolean inscricaoMiniAtividadeEstaHabilitada;
	
	/** Sub Atividade selecinada para visualiza��o */
	private SubAtividadeExtensao subAtividadeSelecionada;
	
	
	/**
	 * <p>M�todo que listas os per�odos de inscri��es abertos para as mini atividades da atividade passado.</p>
	 *  
	 * <p><strong>This method is called just if the user have enroll on the "father activity" </strong> </p> 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaListaPeriodosInscricoesAbertosAtividades.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String listarPeriodosInscricaoAbertosMiniAtividades() throws DAOException{
		
		idAtividadePaiSelecionada = getParameterInt("idAtividadePaiSelecionada");
		
		inscricaoMiniAtividadeEstaHabilitada = getParameterBoolean("inscricaoMiniAtividadeEstaHabilitada");
		
		if(! inscricaoMiniAtividadeEstaHabilitada){
			addMensagemWarning("Para se inscrever nas mini atividades � preciso primeiro realizar a inscri��o na atividade principal.");
		}
		
		atualizaPeriodoInscricoesAbertos(idAtividadePaiSelecionada);	
		
		return telaListaPeriodosInscricoesAbertosMiniAtividades();
		
	}


	/**
	 * Atualiza os per�odos de inscri��o de sub atividades mostrados para o usu�rio
	 *
	 * @param idAtividadePaiSelecionada
	 * @return
	 * @throws DAOException
	 */
	private void atualizaPeriodoInscricoesAbertos( int idAtividadePaiSelecionada) throws DAOException {
		
		InscricaoParticipanteSubAtividadeExtensaoDao dao = null;
		
		try {
			
			dao = getDAO(InscricaoParticipanteSubAtividadeExtensaoDao.class);
			
			GerenciaAreaInternaCursosEventoExtensaoMBean mBean = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
			periodosInscricoesAbertos = dao.findSubAtividadesComPeriodosDeInscricoesAbertos(idAtividadePaiSelecionada, mBean.getParticipanteLogadoAreaInterna().getId());
			
		} finally {
			if(dao != null) dao.close();
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

		prepareMovimento(SigaaListaComando.INSCREVE_PARTICIPANTE_MINI_ATIVIDADE_EXTENSAO);
		
		idMolidadeParticipanteSelecionada = -1;
		
		int idPeriodoAbertoSelecionado = getParameterInt("idPeriodoAbertoSelecionado");
		
		InscricaoAtividade periodoInscricaoSelecionado = null;
		
		for (InscricaoAtividade periodo : periodosInscricoesAbertos) {
			if(periodo.getId() == idPeriodoAbertoSelecionado){
				periodoInscricaoSelecionado = periodo;
				break;
			}
		}
		
		if(periodoInscricaoSelecionado == null){
			addMensagemErro("Selecione um per�odo de inscri��o.");
			return null;
		}
		
		
		obj = new InscricaoAtividadeParticipante();
		GerenciaAreaInternaCursosEventoExtensaoMBean mBean = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		obj.setCadastroParticipante(mBean.getParticipanteLogadoAreaInterna());
		
		
		InscricaoSubAtividadeExtensaoDao dao = null;
		InscricaoAtividade periodoInscricao = null;
		QuestionarioDao daoQuestionario = null;
		InscricaoAtividadeParticipanteDao daoParticipante = null;
		
		try{
			
			dao = getDAO(InscricaoSubAtividadeExtensaoDao.class);
			daoQuestionario = getDAO(QuestionarioDao.class);
			daoParticipante = getDAO(InscricaoAtividadeParticipanteDao.class);
			
			periodoInscricao = dao.findInformacoesInscricoesSubAtividadeParaAlteracao(periodoInscricaoSelecionado.getId());
			
			// As informa��es transientes que n�o vem na consulta //
			periodoInscricao.setQuantidadeInscritosAprovados( periodoInscricaoSelecionado.getQuantidadeInscritosAprovados() );
			periodoInscricao.setQuantidadeInscritos( periodoInscricaoSelecionado.getQuantidadeInscritos() );
			
			
			if( periodoInscricao.getQuestionario() !=  null){
			    // Buscas as informa��es do question�rio //
				periodoInscricao.setQuestionario(daoQuestionario.findInformacaoQuestionario(periodoInscricao.getQuestionario().getId()));
			} 
			
			periodoInscricao.setSubAtividade(periodoInscricaoSelecionado.getSubAtividade());
			
			obj.setInscricaoAtividade(periodoInscricao);
			
			// Verificas se o participante j� se inscreveu nesse atividade
			inscricoesAnteriores = daoParticipante
					.findInscricoesAtivasParticipanteSubAtividade(obj.getInscricaoAtividade().getSubAtividade().getId(), obj.getCadastroParticipante().getId());
			
			if(inscricoesAnteriores.size() > 0){
				addMensagemWarning("Voc� j� realizou uma inscri��o para essa mini atividade, caso se inscreva de novo as anteriores ser�o canceladas.");
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
//				addMensagemWarning("Essa mini atividade possui o n�mero de inscri��o realizadas maior que o n�mero de vagas dispon�veis por�m, ainda est� aberta para inscri��es. " +
//					 " Sua aceita��o como participante estar� dependente do aumento no n�mero de vagas pelo coordenador ou a desist�ncia de algum participante.");
//			}
			
			// Se � autom�tico o preencimento da vagas, j� bloqueia a inscri��o do usu�rio
//			if(periodoInscricaoSelecionado.getMetodoPreenchimento() == InscricaoAtividade.PREENCHIMENTO_AUTOMATICO ){
				addMensagemWarning("Essa mini atividade encontra-se com o n�mero de vagas esgotado. ");
				return null;
//			}
			
		}
		
		return telaRealizaInscricoesSUbAtividade();
		
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
		
		MovimentoInscreveParticipanteMiniCursoEventoExtensao movimento = new MovimentoInscreveParticipanteMiniCursoEventoExtensao(obj, inscricoesAnteriores);
		
		try {
			
			if(obj.getInscricaoAtividade().isCobrancaTaxaMatricula() && ! usuarioConcordaCondicoesPagamento ){
				addMensagemErro("Concorde com os termos da inscri��o.");
				return null;
			}
			
			if(obj.getInscricaoAtividade().getQuestionario() != null )
				obj.setQuestionarioRespostas(((QuestionarioRespostasMBean) getMBean("questionarioRespostasBean")).getObj());
			
			execute(movimento);
			
			if( obj.getInscricaoAtividade().getMetodoPreenchimento() == InscricaoAtividade.COM_CONFIRMACAO 
					||  obj.getInscricaoAtividade().isCobrancaTaxaMatricula() ){
				addMensagemInformation("Sua inscri��o est� pendente de aprova��o pelo coordenador da atividade!");
				addMensagemInformation("Inscri��o submetida com sucesso!");
			}else{
				addMensagemInformation("Inscri��o realizada com sucesso!");
			}
			
			atualizaPeriodoInscricoesAbertos(idAtividadePaiSelecionada);	
			
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
	 * Permite ao participante visualizar os dados da mini atividade antes de se inscrever.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/public/extensao/paginaListaPeriodosInscricoesAbertosSubAtividades.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String visualizarDadosMiniAtividade() throws DAOException{
		
		Integer idSubAtividadeExtensaoSelecionada = getParameterInt("idSubAtividadeExtensaoSelecionada", 0);

			
		SubAtividadeExtensaoDao dao = null;
		
		try {
			dao = getDAO(SubAtividadeExtensaoDao.class);
			subAtividadeSelecionada = dao.findInformacoesAlteracaoSubAtividadeExtensao(idSubAtividadeExtensaoSelecionada);

			return telaVisualizaDadosSubAtividade();
		} finally{
			if(dao != null) dao.close();
		}
		
	}
	
	
	
	// sets e gets ///
	
	
	
	
	
	
	public boolean isInscricaoMiniAtividadeEstaHabilitada() {
		return inscricaoMiniAtividadeEstaHabilitada;
	}

	public void setInscricaoMiniAtividadeEstaHabilitada(	boolean inscricaoMiniAtividadeEstaHabilitada) {
		this.inscricaoMiniAtividadeEstaHabilitada = inscricaoMiniAtividadeEstaHabilitada;
	}

	public SubAtividadeExtensao getSubAtividadeSelecionada() {
		return subAtividadeSelecionada;
	}

	public void setSubAtividadeSelecionada(SubAtividadeExtensao subAtividadeSelecionada) {
		this.subAtividadeSelecionada = subAtividadeSelecionada;
	}


	/**
	 * Retorna  a quantidade de per�odos de inscri��o abertos.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaListaPeriodosInscricoesAtividadesAbertos.jsp/</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdPeriodosInscricaoAbertos(){
		if(periodosInscricoesAbertos == null)
			return 0;
		else
			return periodosInscricoesAbertos.size();
	}
	
	
	
	
	////////////////////////////////   Telas de Navega��o    ////////////////////////////////
	
	/**
	 *  Tela de navega��o
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaListaPeriodosInscricoesAbertosMiniAtividades(){
		GerenciaAreaInternaCursosEventoExtensaoMBean gerenciaAreaInterna = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		return gerenciaAreaInterna.forwardPaginaInterna(PAGINA_LISTA_PERIODOS_INSCRICOES_ABERTOS_MINI_ATIVIDADES);
	}
	
	/**
	 *  Tela de navega��o
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaRealizaInscricoesSUbAtividade(){
		GerenciaAreaInternaCursosEventoExtensaoMBean gerenciaAreaInterna = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		return gerenciaAreaInterna.forwardPaginaInterna(PAGINA_REALIZA_INSCRICOES_SUB_ATIVIDADES_EXTENSAO);
	}

	/**
	 *  Tela de navega��o
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaVisualizaDadosSubAtividade(){
		return forward(PAGINA_VISUALIZA_DADOS_SUB_ATIVIDADES_EXTENSAO);
	}
	
	////////////////////////////////////////////////////////////////
	
	
	
}
