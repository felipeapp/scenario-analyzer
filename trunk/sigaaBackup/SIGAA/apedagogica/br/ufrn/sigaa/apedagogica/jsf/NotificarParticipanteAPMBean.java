package br.ufrn.sigaa.apedagogica.jsf;
/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.MensagensAP;
import br.ufrn.sigaa.apedagogica.dominio.NotificacaoParticipanteAtividade;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.negocio.MovimentoNotificarParticipanteAP;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;

/**
 * Managed bean responsável pelas notificações dos participantes em atividades de atualização pedagógica
 * 
 * @author Mário Rizzi
 * 
 */
@Component("notificarParticipanteAP") @Scope("request")
public class NotificarParticipanteAPMBean extends SigaaAbstractController<NotificacaoParticipanteAtividade>
	implements OperadorAtividadeAP{

	/** Lista de participantes de uma atividade */
	private Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantes;
	/** Indica se todos aprticpants de uma ativiadde serão notificados. */
	private boolean todosParticipantes = false;
	/** Indica a situação selecioanda dos participantes para notificação. */
	private Integer situacaoFiltro;
	
	public NotificarParticipanteAPMBean(){
		obj = new NotificacaoParticipanteAtividade();
		participantes = new ArrayList<ParticipanteAtividadeAtualizacaoPedagogica>();
		situacaoFiltro = 0;
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		isUserInRole(SigaaPapeis.GESTOR_PAP);
	}
	
	@Override
    public String getDirBase() {
    	// TODO Auto-generated method stub
    	return "/apedagogica/NotificarParticipanteAP";
    }
    

	/**
	 * Cancela a operação ativa.
	 * 
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/apedagogica/notificarParticpanteAP/form_participantes.jsp</li>
	 *    <li>/sigaa.war/apedagogica/notificarParticpanteAP/form_envio.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		return super.cancelar();
	}
	
	/**
	 * Retorna para a tela de consulta das atividades.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/apedagogica/notificarParticpanteAP/form_participantes.jsp</li>
	 *  </ul>  
	 * @return
	 */
	public String voltar(){
		ConsultaAtividadeAPMBean consultaMBean = (ConsultaAtividadeAPMBean) getMBean("consultaAtividadeAP");
		consultaMBean.setCodigoOperacao(OperacaoAtividadeAP.NOTIFICAR_PARTICIPANTES);
		return forward( consultaMBean.getFormPage() );
	}
	
	/**
	 * Prepara o formulário para envio.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/apedagogica/geral.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String consultar() throws ArqException{
		checkChangeRole();
		ConsultaAtividadeAPMBean consultaMBean = (ConsultaAtividadeAPMBean) getMBean("consultaAtividadeAP");
		consultaMBean.setCodigoOperacao(OperacaoAtividadeAP.NOTIFICAR_PARTICIPANTES);
		consultaMBean.setAll(null);
		consultaMBean.setResultadosBusca(null);
		return forward( consultaMBean.getFormPage() );
	}
	
	/**
	 * Seleciona a atividade para qual deseja selecionar os participantes
	 * que receberão a notificação. 
	 *  <br /> 
	 * Método não invocado por JSP's.
	 */
	@Override
	public String selecionaAtividade() {
		try {
			removeOperacaoAtiva();
			setOperacaoAtiva( SigaaListaComando.NOTIFICAR_PARTICIPANTES.getId() );
			prepareMovimento( SigaaListaComando.NOTIFICAR_PARTICIPANTES );
			participantes = getGenericDAO().findByExactField(ParticipanteAtividadeAtualizacaoPedagogica.class, "atividade.id", obj.getAtividade().getId());
			if( isEmpty(participantes) )
				addMensagem(MensagensAP.BUSCA_PARTICIPANTES_SEM_RESULTADO);
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}	
		
		if( hasErrors() )
			return null;
		
		return telaParticipantes();
	}
	
	/**
	 * Redireciona para tela contendo a listagem dos participantes da atividade selecionada.
	 *  <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/apedagogica/notificarParticpanteAP/form_envio.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaParticipantes(){
		return forward(getDirBase() + "/form_participantes.jsf");
	}
	
	/**
	 * Retorna uma string contendo todos os nomes e-mails dos inscritos
	 * para visualização no formulário de envio.
	 *  <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/apedagogica/notificarParticpanteAP/form_envio.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getDestinatariosDesc(){
		
		StringBuilder strDestinarios = new StringBuilder();
				
		for (ParticipanteAtividadeAtualizacaoPedagogica p : participantes) {
			if( p.isSelecionado() ){
				strDestinarios.append( p.getDocente().getNome() );
				strDestinarios.append( "<" );
				strDestinarios.append( p.getDocente().getPessoa().getEmail()  );
				strDestinarios.append( ">, " );
			}	
		}
		
		return strDestinarios.toString().substring(0, strDestinarios.length()-2);
		
	}
	
	/**
	 * Submete os participantes selecionados para o formulário
	 * da mensagem de notificação.
	 *  <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/apedagogica/notificarParticpanteAP/form_participante.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String submeterParticipantes() throws ArqException{
		if( !hasParticipanteSelecionado () ){
			addMensagemErro("Para prosseguir com a operação o usuário deve selecionar ao menos um participante.");
			return null;
		}
		return forward( getDirBase() + "/form_envio.jsf");
	}
	
	/**
	 * Verifica se existe ao menos um participante selecionado
	 * @return
	 */
	private boolean hasParticipanteSelecionado(){
		
		for (ParticipanteAtividadeAtualizacaoPedagogica p : participantes) {
			if( p.isSelecionado() )
				return true;
		}
		return false;
	}
	
	/**
	 * Retorna todos as notificações da atividade.
	 */
	@Override
	public Collection<NotificacaoParticipanteAtividade> getAll()
			throws ArqException {
		
		if( isEmpty(all) && !isEmpty(obj.getAtividade()) ){
			all = getGenericDAO().findByExactField(
					NotificacaoParticipanteAtividade.class, "atividade.id", obj.getAtividade().getId(), "DESC", "data" );
		}
		return all;
	}
	
	/**
	 * Popula o formulário de notificação com o título e mensagem 
	 * de uma notificação listada no histórico
	 * para a atividade.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/apedagogica/notificarParticpanteAP/form_participante.jsp</li>
	 * </ul>
	 * @param e
	 */
	public void popularNovaNotificacao(ActionEvent e){
		NotificacaoParticipanteAtividade 
			notificacaoAnterior =	(NotificacaoParticipanteAtividade) 
			e.getComponent().getAttributes().get("notificacaoAnterior");
		obj.setTitulo( notificacaoAnterior.getTitulo() );
		obj.setMensagem( notificacaoAnterior.getMensagem() );
	}
	
	/**
	 * Envia uma mensagem notificando todos os inscritos de um processo seletivo.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/apedagogica/notificarParticpanteAP/form_envio.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String enviar() throws ArqException, NegocioException {
				
		checkChangeRole();
		MovimentoNotificarParticipanteAP mov = new MovimentoNotificarParticipanteAP();
		mov.setCodMovimento(SigaaListaComando.NOTIFICAR_PARTICIPANTES);
		mov.setParticipantes( participantes );
		mov.setNotificacao( obj );
		erros.addAll( obj.validate() );
		
		if( hasErrors() )
			return null;
		
		try {
 			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, "Notificação");
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			return null;
		}
		
		return cancelar();
	
	}
	
	@Override
	public void setAtividade(AtividadeAtualizacaoPedagogica atividade) {
		this.obj.setAtividade( atividade );
	}

	/**
	 * Retorna todos os particpantes da atividade selecionados caso 
	 * {@link this#todosParticipantes} seja selecionado.
	 * @return
	 */
	public Collection<ParticipanteAtividadeAtualizacaoPedagogica> getParticipantes() {
		return participantes;
	}
	
	/**
	 * Marca ou desmarca todos os participantes que serão notificados.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/apedagogica/notificarParticpanteAP/form_participante.jsp</li>
	 * </ul>
	 * @param e
	 */
	public void marcarDesmarcarParticipante(ActionEvent e){
		for (ParticipanteAtividadeAtualizacaoPedagogica p : participantes) {
			if( todosParticipantes )
				p.setSelecionado(true);
			else
				p.setSelecionado(false);
		}
	}
	
	/**
	 * Marca todos os participantes de acordo com a situação selecionada.
	 * {@link this#todosParticipantes} seja selecionado.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/apedagogica/notificarParticpanteAP/form_participante.jsp</li>
	 * </ul>
	 * @return
	 */
	public void marcarParticipanteStatus(ValueChangeEvent e){
		situacaoFiltro = (Integer) e.getNewValue();
		for (ParticipanteAtividadeAtualizacaoPedagogica p : participantes) {
			p.setSelecionado(false);
		}
		for (ParticipanteAtividadeAtualizacaoPedagogica p : participantes) {
			if( p.getSituacao().equals(situacaoFiltro) || situacaoFiltro.equals(0) )
				p.setSelecionado(true);
		}
	}

	public void setParticipantes(
			Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantes) {
		this.participantes = participantes;
	}

	public boolean isTodosParticipantes() {
		return todosParticipantes;
	}

	public void setTodosParticipantes(boolean todosParticipantes) {
		this.todosParticipantes = todosParticipantes;
	}

	public Integer getSituacaoFiltro() {
		return situacaoFiltro;
	}

	public void setSituacaoFiltro(Integer situacaoFiltro) {
		this.situacaoFiltro = situacaoFiltro;
	}
	
}