/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean usado na área pública para gerenciar a inscrição do participante um mini curso 
 * ou evento de extensão. </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
@Component("inscricaoParticipanteMiniAtividadeMBean")
@Scope("request")
public class InscricaoParticipanteMiniAtividadeMBean extends AbstractInscricaoParticipanteMBean{

	
	/** Lista os períodos de inscrição abertos para  mini atividades. (Quando o usuário está logado na área interna.) */
	public final static String PAGINA_LISTA_PERIODOS_INSCRICOES_ABERTOS_MINI_ATIVIDADES = "/public/extensao/paginaListaPeriodosInscricoesAbertosSubAtividades.jsp";
	
	/** Formulário de inscriação em uma mini atividade de extensão. */
	public final static String PAGINA_REALIZA_INSCRICOES_SUB_ATIVIDADES_EXTENSAO = "/public/extensao/paginaRealizaInscricaoSubAtividadeExtensao.jsp";
	
	/** Formulário de inscriação em uma mini atividade de extensão. */
	public final static String PAGINA_VISUALIZA_DADOS_SUB_ATIVIDADES_EXTENSAO = "/public/extensao/viewDadosMiniAtividade.jsp";
	
	
	/** Guarda o id da atividade de extensão selecionada, a partir desse ai vai recuperar as mini atividade com inscrições abertas */
	private int idAtividadePaiSelecionada;
	
	/** Informa se a inscrição em mini atividades está habilitada, essa variável é setada para "true" se o usuário já se inscreveu na atividade pai. */
	private boolean inscricaoMiniAtividadeEstaHabilitada;
	
	/** Sub Atividade selecinada para visualização */
	private SubAtividadeExtensao subAtividadeSelecionada;
	
	
	/**
	 * <p>Método que listas os períodos de inscrições abertos para as mini atividades da atividade passado.</p>
	 *  
	 * <p><strong>This method is called just if the user have enroll on the "father activity" </strong> </p> 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemWarning("Para se inscrever nas mini atividades é preciso primeiro realizar a inscrição na atividade principal.");
		}
		
		atualizaPeriodoInscricoesAbertos(idAtividadePaiSelecionada);	
		
		return telaListaPeriodosInscricoesAbertosMiniAtividades();
		
	}


	/**
	 * Atualiza os períodos de inscrição de sub atividades mostrados para o usuário
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
			addMensagemErro("Selecione um período de inscrição.");
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
			
			// As informações transientes que não vem na consulta //
			periodoInscricao.setQuantidadeInscritosAprovados( periodoInscricaoSelecionado.getQuantidadeInscritosAprovados() );
			periodoInscricao.setQuantidadeInscritos( periodoInscricaoSelecionado.getQuantidadeInscritos() );
			
			
			if( periodoInscricao.getQuestionario() !=  null){
			    // Buscas as informações do questionário //
				periodoInscricao.setQuestionario(daoQuestionario.findInformacaoQuestionario(periodoInscricao.getQuestionario().getId()));
			} 
			
			periodoInscricao.setSubAtividade(periodoInscricaoSelecionado.getSubAtividade());
			
			obj.setInscricaoAtividade(periodoInscricao);
			
			// Verificas se o participante já se inscreveu nesse atividade
			inscricoesAnteriores = daoParticipante
					.findInscricoesAtivasParticipanteSubAtividade(obj.getInscricaoAtividade().getSubAtividade().getId(), obj.getCadastroParticipante().getId());
			
			if(inscricoesAnteriores.size() > 0){
				addMensagemWarning("Você já realizou uma inscrição para essa mini atividade, caso se inscreva de novo as anteriores serão canceladas.");
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
//				addMensagemWarning("Essa mini atividade possui o número de inscrição realizadas maior que o número de vagas disponíveis porém, ainda está aberta para inscrições. " +
//					 " Sua aceitação como participante estará dependente do aumento no número de vagas pelo coordenador ou a desistência de algum participante.");
//			}
			
			// Se é automático o preencimento da vagas, já bloqueia a inscrição do usuário
//			if(periodoInscricaoSelecionado.getMetodoPreenchimento() == InscricaoAtividade.PREENCHIMENTO_AUTOMATICO ){
				addMensagemWarning("Essa mini atividade encontra-se com o número de vagas esgotado. ");
				return null;
//			}
			
		}
		
		return telaRealizaInscricoesSUbAtividade();
		
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
		
		MovimentoInscreveParticipanteMiniCursoEventoExtensao movimento = new MovimentoInscreveParticipanteMiniCursoEventoExtensao(obj, inscricoesAnteriores);
		
		try {
			
			if(obj.getInscricaoAtividade().isCobrancaTaxaMatricula() && ! usuarioConcordaCondicoesPagamento ){
				addMensagemErro("Concorde com os termos da inscrição.");
				return null;
			}
			
			if(obj.getInscricaoAtividade().getQuestionario() != null )
				obj.setQuestionarioRespostas(((QuestionarioRespostasMBean) getMBean("questionarioRespostasBean")).getObj());
			
			execute(movimento);
			
			if( obj.getInscricaoAtividade().getMetodoPreenchimento() == InscricaoAtividade.COM_CONFIRMACAO 
					||  obj.getInscricaoAtividade().isCobrancaTaxaMatricula() ){
				addMensagemInformation("Sua inscrição está pendente de aprovação pelo coordenador da atividade!");
				addMensagemInformation("Inscrição submetida com sucesso!");
			}else{
				addMensagemInformation("Inscrição realizada com sucesso!");
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
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna  a quantidade de períodos de inscrição abertos.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	
	
	
	
	////////////////////////////////   Telas de Navegação    ////////////////////////////////
	
	/**
	 *  Tela de navegação
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaListaPeriodosInscricoesAbertosMiniAtividades(){
		GerenciaAreaInternaCursosEventoExtensaoMBean gerenciaAreaInterna = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		return gerenciaAreaInterna.forwardPaginaInterna(PAGINA_LISTA_PERIODOS_INSCRICOES_ABERTOS_MINI_ATIVIDADES);
	}
	
	/**
	 *  Tela de navegação
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaRealizaInscricoesSUbAtividade(){
		GerenciaAreaInternaCursosEventoExtensaoMBean gerenciaAreaInterna = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		return gerenciaAreaInterna.forwardPaginaInterna(PAGINA_REALIZA_INSCRICOES_SUB_ATIVIDADES_EXTENSAO);
	}

	/**
	 *  Tela de navegação
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaVisualizaDadosSubAtividade(){
		return forward(PAGINA_VISUALIZA_DADOS_SUB_ATIVIDADES_EXTENSAO);
	}
	
	////////////////////////////////////////////////////////////////
	
	
	
}
