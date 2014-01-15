/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * Managed bean responsável pelas notificações dos inscritos envolvidos em um processo seletivo
 *  (STRICTO-SENSU, LATO, TECNICO e TRANSFERÊNCIA VOLUNTÀRIA).
 * 
 * @author Mário Rizzi
 * 
 */
@Component("notificarInscritos") @Scope("request")
public class NotificarInscritosMBean extends SigaaAbstractController<NotificacaoProcessoSeletivo>{

	/** Constante contém o texto de confirmação da mensagem enviada. **/
	private static final String MSG_SUCESSO = "Sua notificação foi enviada com sucesso!";
	/** Coleção de Inscrição Seleção  */
	private Collection<InscricaoSelecao> inscritos = new ArrayList<InscricaoSelecao>();
	/** Mapa contendo e-mail do inscrito como chave, e nome do inscrito como valor **/
	private Map<String, String> destinatarios =  new HashMap<String,String>();
	/** Atributos referentes aos dados da mensagem **/
	private List<String> statusInscricaoSel;
	/** Boleano que informa se há ou não destinatário para a mensagem. */
    public boolean haDestinatarios = false;
	/** Define a quantidade de notíficaçõe já enviadas */
    public int qtdNotificacoes = 0;
    
    
    @Override
    public String getDirBase() {
    	// TODO Auto-generated method stub
    	return "/administracao/cadastro/ProcessoSeletivo/notificar_inscrito";
    }
    
	/**
	 * Envia uma mensagem notificando todos os inscritos de um processo seletivo.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>//adnministracao/cadastro/ProcessoSeletivo/notificar_inscrito/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String enviar() throws ArqException, NegocioException {
				
		checkChangeRole();
		
		//Verifica se os campos obrigatórios foram preenchidos.
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
	 * Prepara o formulário para envio.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Prepara o formulário para envio.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna todas as notificações para o processos seletivo selecionado.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * para visualização no formulário de envio.
	 *  <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método responsável em popular um combo com os status de inscrição utilizado
	 * para filtrar os destinatários.
	 *  <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>//adnministracao/cadastro/ProcessoSeletivo/notificar_inscrito/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public  Collection<SelectItem> getComboStatusInscricao(){
		
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(StatusInscricaoSelecao.SUBMETIDA, "Inscrição Submetida"));
		itens.add(new SelectItem(StatusInscricaoSelecao.DEFERIDA, "Inscrição Deferida"));
		itens.add(new SelectItem(StatusInscricaoSelecao.INDEFERIDA, "Inscrição Indeferida"));
		itens.add(new SelectItem(StatusInscricaoSelecao.APROVADO_SELECAO, "Candidato Aprovado"));
		return itens;
	}
	
	/**
	 * Método responsável em popular os destinatários de acordo com o status do inscrito
	 * setado. A notificação será enviada somente para os destinatários que possuam o status 
	 * da inscrição selecionada.
	 * 
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Cancela a operação ativa.
	 * 
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método responsável em validar os campos para o envio da notificação.
	 * @return
	 * @throws SegurancaException 
	 */
	private boolean validaForm() throws SegurancaException{
		
		ListaMensagens erros = new ListaMensagens();	
		
		
		if(statusInscricaoSel==null || statusInscricaoSel.size()==0)
			erros.addErro("Status da Inscrição: Campo obrigatório não informado.");
		else if(destinatarios==null || destinatarios.size()==0)
			erros.addErro("Nenhum destinatário selecionado para o status" +
					" da inscrição selecionado. Por favor selecione outro status.");
		
		//Verifica se o usuário possui um dos papéis abaixo
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.COORDENADOR_LATO, 
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,
				SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.DAE, SigaaPapeis.PPG);
	
		erros.addAll(obj.validate());
		addMensagens(erros);
		
		//Caso exista erro ou operação não está ativa
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