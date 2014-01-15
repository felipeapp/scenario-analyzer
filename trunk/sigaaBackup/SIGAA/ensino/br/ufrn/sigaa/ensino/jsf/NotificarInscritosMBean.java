/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.jsf;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.NotificacaoProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.StatusInscricaoSelecao;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoNotificarProcessoSeletivo;

/**
 * Managed bean respons�vel pelas notifica��es dos inscritos envolvidos em um processo seletivo
 *  (STRICTO-SENSU, LATO, TECNICO e TRANSFER�NCIA VOLUNT�RIA).
 * 
 * @author M�rio Rizzi
 * 
 */
@Component("notificarInscritos") @Scope("request")
public class NotificarInscritosMBean extends SigaaAbstractController<NotificacaoProcessoSeletivo>{

	/** Constante cont�m o texto de confirma��o da mensagem enviada. **/
	private static final String MSG_SUCESSO = "Sua notifica��o foi enviada com sucesso!";
	/** Cole��o de Inscri��o Sele��o  */
	private Collection<InscricaoSelecao> inscritos = new ArrayList<InscricaoSelecao>();
	/** Mapa contendo e-mail do inscrito como chave, e nome do inscrito como valor **/
	private Map<String, String> destinatarios =  new HashMap<String,String>();
	/** Atributos referentes aos dados da mensagem **/
	private List<String> statusInscricaoSel;
	/** Boleano que informa se h� ou n�o destinat�rio para a mensagem. */
    public boolean haDestinatarios = false;
	/** Define a quantidade de not�fica��e j� enviadas */
    public int qtdNotificacoes = 0;
    
    
    @Override
    public String getDirBase() {
    	// TODO Auto-generated method stub
    	return "/administracao/cadastro/ProcessoSeletivo/notificar_inscrito";
    }
    
	/**
	 * Envia uma mensagem notificando todos os inscritos de um processo seletivo.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>//adnministracao/cadastro/ProcessoSeletivo/notificar_inscrito/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String enviar() throws ArqException, NegocioException {
				
		checkChangeRole();
		
		//Verifica se os campos obrigat�rios foram preenchidos.
		if(validaForm()){
			
			// Persistir processo seletivo
			MovimentoNotificarProcessoSeletivo mov = new MovimentoNotificarProcessoSeletivo();
			mov.setCodMovimento(SigaaListaComando.NOTIFICAR_INSCRITOS);
			obj.carregarStatusInscricao(statusInscricaoSel);
			mov.setNotificacao(obj);
			mov.setDestinatarios(destinatarios);
			
			if( hasErrors() )
				return null;
			
			try {
				execute(mov);
			} catch (NegocioException e) {
				addMensagens( e.getListaMensagens() );
				return null;
			}
			
			addMensagemInformation(MSG_SUCESSO);
			return forward("/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsf");
		}
		return  forward(getFormPage());
	}
		
	/**
	 * Prepara o formul�rio para envio.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		
		prepareMovimento(SigaaListaComando.NOTIFICAR_INSCRITOS);
		obj = new NotificacaoProcessoSeletivo();
		if( !isEmpty(inscritos) )
			obj.setProcessoSeletivo(inscritos.iterator().next().getProcessoSeletivo());
		else
			obj.setProcessoSeletivo(new ProcessoSeletivo());
					
		statusInscricaoSel = new ArrayList<String>();
		haDestinatarios = false;
		qtdNotificacoes = 0;

		return forward(getFormPage());
		
	}
	
	/**
	 * Prepara o formul�rio para envio.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>//adnministracao/cadastro/ProcessoSeletivo/notificar_inscrito/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String view(ActionEvent e) throws ArqException,DAOException {
		if( obj == null )
			obj = new NotificacaoProcessoSeletivo();
		
		populateObj(true);
		
		return forward(getViewPage());
		 
	}
	
	/**
	 * Retorna todas as notifica��es para o processos seletivo selecionado.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	@Override
	public Collection<NotificacaoProcessoSeletivo> getAll() throws ArqException {
		
		if(all == null){
			all = getGenericDAO().
				findByExactField(NotificacaoProcessoSeletivo.class, "processoSeletivo.id", 
									obj.getProcessoSeletivo().getId(),"DESC","data");
			qtdNotificacoes = all.size();
		}	
		return all;
	}
	
	/**
	 * Retorna uma string contendo todos os nomes e-mails dos inscritos
	 * para visualiza��o no formul�rio de envio.
	 *  <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>//adnministracao/cadastro/ProcessoSeletivo/notificar_inscrito/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getDestinatariosDesc(){
		
		StringBuilder strDestinarios = new StringBuilder();
		carregaDestinatarios();
		
		for (Map.Entry<String, String> d : destinatarios.entrySet()){ 
			strDestinarios.append( d.getValue().toLowerCase() );
			strDestinarios.append( "<" );
			strDestinarios.append( d.getKey() );
			strDestinarios.append( ">, " );
		}
		
		return strDestinarios.toString().substring(0, strDestinarios.length()-2);
		
	}
	
	/**
	 * M�todo respons�vel em popular um combo com os status de inscri��o utilizado
	 * para filtrar os destinat�rios.
	 *  <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>//adnministracao/cadastro/ProcessoSeletivo/notificar_inscrito/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public  Collection<SelectItem> getComboStatusInscricao(){
		
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(StatusInscricaoSelecao.SUBMETIDA, "Inscri��o Submetida"));
		itens.add(new SelectItem(StatusInscricaoSelecao.DEFERIDA, "Inscri��o Deferida"));
		itens.add(new SelectItem(StatusInscricaoSelecao.INDEFERIDA, "Inscri��o Indeferida"));
		itens.add(new SelectItem(StatusInscricaoSelecao.APROVADO_SELECAO, "Candidato Aprovado"));
		return itens;
	}
	
	/**
	 * M�todo respons�vel em popular os destinat�rios de acordo com o status do inscrito
	 * setado. A notifica��o ser� enviada somente para os destinat�rios que possuam o status 
	 * da inscri��o selecionada.
	 * 
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/notificar_inscrito/form.jsp</li>
	 * </ul>
	 */
	public void carregaDestinatarios(){
		destinatarios.clear();

		for (InscricaoSelecao i : inscritos) {
			if(statusInscricaoSel.contains(String.valueOf(i.getStatus())))
				destinatarios.put(i.getPessoaInscricao().getEmail() , i.getPessoaInscricao().getNome());
		}
		if (destinatarios.size() > 0) 
			haDestinatarios = true;
		else
			haDestinatarios = false;
	}
	
	/**
	 * Cancela a opera��o ativa.
	 * 
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/notificar_inscrito/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		return super.cancelar();
	}
	
	public void setInscritos(Collection<InscricaoSelecao> inscritos) {
		this.inscritos = inscritos;
	}

	public List<String> getStatusInscricaoSel() {
		return statusInscricaoSel;
	}

	public void setStatusInscricaoSel(List<String> statusInscricaoSel) {
		this.statusInscricaoSel = statusInscricaoSel;
	}
	
	/**
	 * M�todo respons�vel em validar os campos para o envio da notifica��o.
	 * @return
	 * @throws SegurancaException 
	 */
	private boolean validaForm() throws SegurancaException{
		
		ListaMensagens erros = new ListaMensagens();	
		
		
		if(statusInscricaoSel==null || statusInscricaoSel.size()==0)
			erros.addErro("Status da Inscri��o: Campo obrigat�rio n�o informado.");
		else if(destinatarios==null || destinatarios.size()==0)
			erros.addErro("Nenhum destinat�rio selecionado para o status" +
					" da inscri��o selecionado. Por favor selecione outro status.");
		
		//Verifica se o usu�rio possui um dos pap�is abaixo
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.COORDENADOR_LATO, 
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,
				SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.DAE, SigaaPapeis.PPG);
	
		erros.addAll(obj.validate());
		addMensagens(erros);
		
		//Caso exista erro ou opera��o n�o est� ativa
		if (erros.size()>0)
			return false;
	
		return true;
	}

	public boolean isHaDestinatarios() {
		return haDestinatarios;
	}

	public void setHaDestinatarios(boolean haDestinatarios) {
		this.haDestinatarios = haDestinatarios;
	}

	public int getQtdNotificacoes() {
		return qtdNotificacoes;
	}
	
}