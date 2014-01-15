package br.ufrn.sigaa.projetos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.validator.EmailValidator;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.jsf.AbstractControllerNotificacoes;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.AtividadeExtensaoMBean;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/****************************************************************************************************
 * MBean Responsável por enviar um comunicado a todos os coordenadores de Ações de Extensão e Integradas encontrados na Busca.
 * @author julio
 *
 ****************************************************************************************************/
@Scope("session")
@Component("comunicarCoordenadores")
public class ComunicarCoordenadoresMBean extends AbstractControllerNotificacoes{


	/**
	 * Serial default
	 */
	private static final long serialVersionUID = 1L;
	
	/** Atributo que guarda a lista dos coordenadores que receberão o e-mail de notificação */
	private Collection<MembroProjeto> coordenadoresNotificacao;
	/** Atributo utilizado para informar se o usuário remetente receberá uma cópia do e-mail enviado. */
	private boolean enviarCopia = true;
	
	/**
	 * Construtor padrão
	 */
	public ComunicarCoordenadoresMBean(){
		
	}
	/**
	 * Método que retorna o caminho do formulário para envio do comunicado 
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 */
	@Override
	public String getCaminhoFormulario() {
		return "/projetos/ComunicarCoordenadores/form.jsf";
	}

	/**
	 * Método que retorna a lista de papeis que tem permissão para enviar comunicados
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 */
	@Override
	public int[] getPapeis() {
		return new int[] {SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.MEMBRO_COMITE_INTEGRADO};
	}
	
	/**
	 * Realizar validações antes do envio da notificação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/extensao/ComunicarCoordenadores/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	@Override
	public String enviar() throws ArqException, NegocioException {
		if(ValidatorUtil.isEmpty(coordenadoresNotificacao)){
			addMensagemErro("Selecione ao menos uma Ação que tenha um coordenador associado a ela.");
			return preComunicarCoordenadores();
		}
		
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(obj.getTitulo(), "Assunto", lista);
		ValidatorUtil.validateRequired(obj.getMensagem(), "Corpo da mensagem", lista);
		
		// Apenas se o assunto e a mensagem estiverem preenchidos é que se verifica o e-mail de respostas
		if(ValidatorUtil.isEmpty(lista)){
			// Verifica se o usuário setou um e-mail de respostas
			if(ValidatorUtil.isEmpty(obj.getEmailRespostas())){
				obj.setEmailRespostas("naoresponder@ufrn.br");
			}else{
				// É necessário validar o e-mail de respostas digitado pelo usuário neste momento, 
				// 		porque na arquitetura não há o nome do campo correto na mensagem de erro.
				if(!EmailValidator.getInstance().isValid(obj.getEmailRespostas())){
					lista.addErro("Responder para: Formato de e-mail inválido.");
					addMensagens(lista);
					return null;
				}
			}
		}else{
			addMensagens(lista);
			return null;
		}
		obj.setEnviarEmail(true);
		obj.setEnviarMensagem(false);
		return super.enviar();
	}

	/**
	 * Método que prenche os destinatários e cria a mensagem para o envio do e-mail de notificação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 */
	@Override
	public void popularDestinatarios() throws ArqException {
		if(enviarCopia){
			Destinatario usuarioAtual = new Destinatario(getUsuarioLogado().getNome(), getUsuarioLogado().getEmail());
			obj.adicionarDestinatario(usuarioAtual);
		}
		
		for (MembroProjeto m : coordenadoresNotificacao){
			Destinatario d = new Destinatario(m.getPessoa().getNome(), m.getPessoa().getEmail());
			obj.adicionarDestinatario(d);
		}
		
		obj.setNomeRemetente(getUsuarioLogado().getNome());
		
		// Criando mensagem para envio caso o usuário tenha digitado alguma mensagem
		if(!ValidatorUtil.isEmpty(obj.getMensagem())){
			obj.setContentType(MailBody.HTML);
			StringBuilder mensagem = new StringBuilder("");
			
			mensagem.append("<html>");
			mensagem.append("<body>");
			
			mensagem.append("Prezado(a):\n<br />");
			mensagem.append(getUsuarioLogado().getNome()+ " lhe enviou a seguinte mensagem:<br />");
			
			mensagem.append("<table style=\"width: 90%; margin-left: auto; margin-right: auto; border: 1px solid #C8D5EC;\">");
			
			mensagem.append("<tbody>");
			mensagem.append("<tr>");
			mensagem.append("<td>");
			
			mensagem.append(StringUtils.removerComentarios(obj.getMensagem())+"<br />");
			
			mensagem.append("</td>");
			mensagem.append("</tr>");
			mensagem.append("</tbody>");
			mensagem.append("</table>");
			mensagem.append("<div style=\"font-weight: bold; padding-top: 30px; padding-bottom: 30px; text-align: center;\">\n ESTE E-MAIL FOI GERADO PELO SIGAA. </div>");
			mensagem.append("</body>");	
			mensagem.append("</html>");
			
			obj.setMensagem(mensagem.toString());
		}
	}
	
	/**
	 * Método que recupera os coordenadores a partir da lista de ações listadas na busca
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/extensao/ComunicarCoordenadores/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String comunicarCoordenadores() throws ArqException{
		coordenadoresNotificacao = new ArrayList<MembroProjeto>();
		BuscaAcaoAssociadaMBean beanBusca = getMBean("buscaAcaoAssociada");
		Collection<Projeto> projetosLocalizados = beanBusca.getResultadosBusca();
		return iniciarComunicarCoordenadores(projetosLocalizados);
	}
	
	/**
	 * Método que recupera os coordenadores a partir da lista de ações listadas na busca
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/extensao/ComunicarCoordenadores/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String comunicarCoordenadoresExtensao() throws ArqException{
		coordenadoresNotificacao = new ArrayList<MembroProjeto>();
		AtividadeExtensaoMBean beanBusca = getMBean("atividadeExtensao");
		Collection<AtividadeExtensao> atividadesLocalizadas = beanBusca.getAtividadesLocalizadas();
		Collection<Projeto> projetosLocalizados = new ArrayList<Projeto>();
		for (AtividadeExtensao atv : atividadesLocalizadas) {
			projetosLocalizados.add(atv.getProjeto());
		}
		return iniciarComunicarCoordenadores(projetosLocalizados);
	}
	
	/**
	 * Prepara o Bean com a lista de coordenadores selecionado e redireciona para o formulário de
	 * cadastro de mensagem.
	 * 
	 * @param projetos
	 * @return
	 * @throws ArqException
	 */
	private String iniciarComunicarCoordenadores(Collection<Projeto> projetos) throws ArqException {
		Set<Integer> coordNotRepeat = new HashSet<Integer>(); // Coleção auxiliar para verificar se um coordenador já foi inserido na lista.
		for (Projeto proj : projetos){
			if(proj.isSelecionado() && proj.getCoordenador() != null && proj.getCoordenador().getPessoa() != null 
					&& !ValidatorUtil.isEmpty(proj.getCoordenador().getPessoa().getNome())){
				if(coordNotRepeat.add(proj.getCoordenador().getPessoa().getId())){
					coordenadoresNotificacao.add(proj.getCoordenador());
				}
			}
		}
		if(ValidatorUtil.isEmpty(coordenadoresNotificacao)){
			addMensagemErro("Selecione ao menos uma Ação que tenha um coordenador associado a ela.");
			return null;
		}else{
			return iniciar();
		}
	}
	
	/**
	 * Método que remove um coordenador da lista de envio do e-mail de notificação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/extensao/ComunicarCoordenadores/form.jsp</li>
	 * </ul>
	 */
	public void removerCoordenadorNotificacao(){
		int idCoord = getParameterInt("idCoord", 0);
		if(idCoord > 0){
			Iterator<MembroProjeto> it = coordenadoresNotificacao.iterator();
			while(it.hasNext()){
				MembroProjeto m  = it.next();
				if(idCoord == m.getPessoa().getId()){
					it.remove();
					break;
				}
			}
		}
	}
	
	/**
	 * Método que redireciona para tela de busca das ações
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/projeto/menu.jsp</li>
	 * 		<li>/sigaa/projeto/ComunicarCoordenadores/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String preComunicarCoordenadores(){
		return redirect("/sigaa/projetos/ComunicarCoordenadores/lista.jsf");
	}
	
	/**
	 * Método que redireciona para tela de busca das ações
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/extensao/menu.jsp</li>
	 * 		<li>/sigaa/extensao/ComunicarCoordenadoresExtensao/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String preComunicarCoordenadoresExtensao(){
		return redirect("/sigaa/extensao/ComunicarCoordenadoresExtensao/lista.jsf");
	}

	
	public Collection<MembroProjeto> getCoordenadoresNotificacao() {
		return coordenadoresNotificacao;
	}
	public void setCoordenadoresNotificacao(
			Collection<MembroProjeto> coordenadoresNotificacao) {
		this.coordenadoresNotificacao = coordenadoresNotificacao;
	}
	public boolean isEnviarCopia() {
		return enviarCopia;
	}
	public void setEnviarCopia(boolean enviarCopia) {
		this.enviarCopia = enviarCopia;
	}

}
