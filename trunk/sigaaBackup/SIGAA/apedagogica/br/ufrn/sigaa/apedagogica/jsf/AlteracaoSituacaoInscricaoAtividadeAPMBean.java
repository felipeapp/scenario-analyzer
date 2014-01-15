package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.apedagogica.dao.ParticipanteAtividadeAPDAO;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.MensagensAP;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.StatusParticipantesAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.negocio.MovimentoInscricaoAtividadeAP;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;

/**
 * Controller que gerencia a operação de alteração da situação da inscrição.
 * @author Mário Rizzi
 *
 */

@Component("alteraSituacaoInscricaoAP") @Scope("request")
public class AlteracaoSituacaoInscricaoAtividadeAPMBean extends SigaaAbstractController<AtividadeAtualizacaoPedagogica> 
	implements OperadorAtividadeAP{

	/** Lista de participantes de uma atividade */
	private List<ParticipanteAtividadeAtualizacaoPedagogica> participantes;
	/** Lista de participantes com status alterado de uma atividade */
	private List<ParticipanteAtividadeAtualizacaoPedagogica> participantesSituacaoAlterada;
	
	
	public AlteracaoSituacaoInscricaoAtividadeAPMBean(){
		participantes = new ArrayList<ParticipanteAtividadeAtualizacaoPedagogica>();
		participantesSituacaoAlterada = new ArrayList<ParticipanteAtividadeAtualizacaoPedagogica>();
	}
	
	/**
	 * Define o diretório base para as operações de CRUD.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getDirBase() 
	 */
	@Override
	public String getDirBase() {
		return "/apedagogica/AlteracaoSituacaoInscricaoAP";
	}
	
	/**
	 * Define o formulário de confirmação da participação na atividade.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getFormPage() 
	 */
	@Override
	public String getFormPage() {
		// TODO Auto-generated method stub
		return getDirBase() + "/form_alteracao_situacao.jsf";
	}
	
	/**
	 * Verifica se o usuário possui o papel informado.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#checkChangeRole() 
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		isUserInRole(SigaaPapeis.GESTOR_PAP);
	}
	
		
	/**
	 * Efetua a consulta dos participantes.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/consultaAtividadeAPedagogica/busca_atividade.jsp</li>
 	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String consultar() throws SegurancaException{
		
		checkChangeRole();
		
		ConsultaAtividadeAPMBean consultaMBean = (ConsultaAtividadeAPMBean) getMBean("consultaAtividadeAP");
		consultaMBean.setCodigoOperacao(OperacaoAtividadeAP.ALTERA_SITUACAO_INSCRICAO_ATIVIDADE);
		consultaMBean.setAll(null);
		consultaMBean.setResultadosBusca(null);
		
		return forward( consultaMBean.getFormPage() );
	}
	
	/**
	 * Voltar para tela de consulta dos participantes.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/consultaAtividadeAPedagogica/busca_atividade.jsp</li>
 	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String voltar() throws SegurancaException{
		
		checkChangeRole();
		ConsultaAtividadeAPMBean consultaMBean = (ConsultaAtividadeAPMBean) getMBean("consultaAtividadeAP");
		consultaMBean.setCodigoOperacao(OperacaoAtividadeAP.ALTERA_SITUACAO_INSCRICAO_ATIVIDADE);
		
		return forward( consultaMBean.getFormPage() );
	}
	
	/**
	 * Redireciona para o subsistema após alterar o status.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#forwardCadastrar() 
	 */
	@Override
	public String forwardCadastrar() {
		try {
			return voltar();
		} catch (SegurancaException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Retorna os participantes da atividade selecionada.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
	 * @return
	 */
	public List<ParticipanteAtividadeAtualizacaoPedagogica> getParticipantes(){
		return participantes;
	}
	
	/**
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
 	 * */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		checkChangeRole();
		
		MovimentoInscricaoAtividadeAP movInscricao = new MovimentoInscricaoAtividadeAP();
		movInscricao.setCodMovimento(SigaaListaComando.ALTERAR_SITUACAO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA );
		movInscricao.setParticipantesSituacaoAlterada( participantesSituacaoAlterada );
		
		try {
			execute(movInscricao);
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
		} 
		
		if(hasErrors())
			return null;
		
		addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO,"Situação dos participantes");
		removeOperacaoAtiva();
		all = null;
		
		return forwardCadastrar();
		
	}
	
	
	
	/**
	 * Adiciona o participante com a situação alterada a listada 
	 * para depois ser passada no movimento. 
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
	 * @param e
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void addInscricaoStatusAlterado(ValueChangeEvent e) throws ArqException, NegocioException{

		addInscricaoStatusAlterado(getParameterInt("idParticipante"), (Integer)e.getNewValue());		
	}
	
	/**
	 * Adiciona o participante com a situação alterada a listada 
	 * para depois ser passada no movimento. 
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
	 * @param e
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void addInscricaoStatusConcluido(ActionEvent e) throws ArqException, NegocioException{
		Integer id = getParameterInt("idParticipante");
		atualizarSituacaoListaParticipantes(id,StatusParticipantesAtualizacaoPedagogica.CONCLUIDO.getId());
		addInscricaoStatusAlterado(id, StatusParticipantesAtualizacaoPedagogica.CONCLUIDO.getId());		
	}
	
	/**
	 * Adiciona o participante com a situação alterada a listada 
	 * para depois ser passada no movimento. 
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
	 * @param e
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void addInscricaoStatusAusente(ActionEvent e) throws ArqException, NegocioException{
		Integer id = getParameterInt("idParticipante");
		atualizarSituacaoListaParticipantes(id,StatusParticipantesAtualizacaoPedagogica.AUSENTE.getId());
		addInscricaoStatusAlterado(id, StatusParticipantesAtualizacaoPedagogica.AUSENTE.getId());		
	}
	
	/**
	 * Adiciona o participante com a situação alterada a listada 
	 * para depois ser passada no movimento. 
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
	 * @param e
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void addInscricaoStatusInscrito(ActionEvent e) throws ArqException, NegocioException{
		Integer id = getParameterInt("idParticipante");
		atualizarSituacaoListaParticipantes(id,StatusParticipantesAtualizacaoPedagogica.INSCRITO.getId());
		addInscricaoStatusAlterado(id, StatusParticipantesAtualizacaoPedagogica.INSCRITO.getId());		
	}
	
	private void atualizarSituacaoListaParticipantes(Integer id, Integer status) {
		for (ParticipanteAtividadeAtualizacaoPedagogica p : participantes)
			if (p.getId() == id){
				p.setSituacao(status);
				break;
			}	
	}

	/**
	 * Adiciona o participante com a situação alterada a listada 
	 * para depois ser passada no movimento. 
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
	 * @param e
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void addInscricaoStatusAlterado(Integer id, Integer status) throws ArqException, NegocioException{

		ParticipanteAtividadeAtualizacaoPedagogica participanteSelecionado = new ParticipanteAtividadeAtualizacaoPedagogica();
	    participanteSelecionado.setId( id );
		participanteSelecionado = getGenericDAO().refresh(participanteSelecionado);
		participanteSelecionado.setSituacao( status );
		participanteSelecionado.setSelecionado(true);
		
		if( !isEmpty(participanteSelecionado) ){
						
			/** Remove o particpante da lista se já existir */
			int index = participantesSituacaoAlterada.indexOf(participanteSelecionado);
			if( index >= 0 )
				participantesSituacaoAlterada.remove(index);
		
			participantesSituacaoAlterada.add(participanteSelecionado);
		}	
		
	}
	
	/**
	 * Muda todas as situações dos participantes para depois ser passada no movimento. 
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
	 * @param status
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void changeAll(Integer status) throws DAOException {
		
		for (ParticipanteAtividadeAtualizacaoPedagogica p : participantes){
			if (p.getSituacao() != status){
				
				p.setSituacao(status);
				p.setSelecionado(true);
				
				ParticipanteAtividadeAtualizacaoPedagogica participanteSelecionado = UFRNUtils.deepCopy(p);
				
				if( !isEmpty(participanteSelecionado) ){
					
					/** Remove o particpante da lista se já existir */
					int pos = participantesSituacaoAlterada.indexOf(p);
					if( pos >= 0 )
						participantesSituacaoAlterada.remove(pos);
					
					participantesSituacaoAlterada.add(participanteSelecionado);
				}
			}
		}	
	}
	
	/**
	 * Muda todas as situações dos participantes para depois ser passada no movimento. 
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
	 * @param e
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void changeAllPresente (ActionEvent e) throws DAOException {
		
		changeAll(StatusParticipantesAtualizacaoPedagogica.CONCLUIDO.getId());
	}
	
	/**
	 * Muda todas as situações dos participantes para depois ser passada no movimento. 
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
	 * @param e
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void changeAllAusente (ActionEvent e) throws DAOException {
		
		changeAll(StatusParticipantesAtualizacaoPedagogica.AUSENTE.getId());
	}
	
	/**
	 * Muda todas as situações dos participantes para depois ser passada no movimento. 
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/AlteracaoSituacaoInscricaoAP/form_alteracao_situacao.jsp</li>
 	 * </ul>
	 * @param e
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void changeAllInscrito (ActionEvent e) throws DAOException {
		
		changeAll(StatusParticipantesAtualizacaoPedagogica.INSCRITO.getId());
	}
	
	/**
	 * Prepara formulário para confirmar inscrição do participante.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/consultaAtividadeAPedagogica/busca_atividade.jsp</li>
 	 * </ul>
	 */
	@Override
	public String selecionaAtividade() {
		
		try {
			
			removeOperacaoAtiva();
			setOperacaoAtiva(SigaaListaComando.ALTERAR_SITUACAO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA.getId());
			prepareMovimento(SigaaListaComando.ALTERAR_SITUACAO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA );
			ParticipanteAtividadeAPDAO dao = getDAO(ParticipanteAtividadeAPDAO.class);
			participantes = (List<ParticipanteAtividadeAtualizacaoPedagogica>) dao.findByAtividade( obj.getId() );
			if( isEmpty(participantes) )
				addMensagem(MensagensAP.BUSCA_PARTICIPANTES_SEM_RESULTADO);
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}	
		
		if( hasErrors() )
			return null;
		
		return forward(getFormPage());
	}
	
	public void setParticipantes(
			List<ParticipanteAtividadeAtualizacaoPedagogica> participantes) {
		this.participantes = participantes;
	}

	@Override
	public void setAtividade(AtividadeAtualizacaoPedagogica atividade) {
		obj = atividade;
	}
	
	
	
}
