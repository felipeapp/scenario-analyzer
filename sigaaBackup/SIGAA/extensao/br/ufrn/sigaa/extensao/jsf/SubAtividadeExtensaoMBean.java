/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/08/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.SubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.negocio.helper.SubAtividadeExtensaoHelper;

/**
 * <p>MBean resposável por gerenciar as operações exclusivas sobre as sub atividades de extensão</p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
@Component("subAtividadeExtensaoMBean")
@Scope("request")
public class SubAtividadeExtensaoMBean extends SigaaAbstractController<SubAtividadeExtensao>{

	/**
	 * Página que altera os dados de uma sub atividade de extensção.
	 */
	public static final String PAGINA_ALTERA_DADOS_SUB_ATIVIDADE_EXTENSAO = "/extensao/SubAtividade/alteraDadosSubAtividadeExtensao.jsp";
	
	
	/** Página pela qual o usuário pode remover uma sub atividade de extensão depois que a atividade pai já está em execução.
	 *  Nesse caso é mais complicado porque tem que verificar se tem alguém inscrito e desativar as incrições.
	 */
	public static final String PAGINA_REMOVE_SUB_ATIVIDADE = "/extensao/SubAtividade/removerSubAtividadeExtensao.jsp";
	
	
	
	/** 
	 * Página de retorno do caso de uso que é definida por quem chamar.
	 * @see extensao/Atividade/lista_minhas_atividades.jsp
	 */
	public String paginaRetorno;
	
	
	/**
	 * Os participantes da mini atividade que está sendo excluída.
	 */
	private List<InscricaoAtividadeParticipante> participantes;
	
	/**
     * Inicia o caso de alteração de uma sub atividade, depois que ação já está em execução.
     * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
     * 	<li>sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp </li>
     *
     * <ul>
	 * @throws ArqException 
     * 
     */
	public String preAtualizar() throws ArqException {
		
		int idSubmissao = getParameterInt("idSubAtividade", 0);
		
		if(idSubmissao <= 0){
			addMensagemErro("Seleciona uma Mini Atividade");
			return null;
		}
		
		if( ! getAcessoMenu().isCoordenadorExtensao() ){
			addMensagemErro("Apenas Coordenadores de Extensão podem alterar uma Mini Atividade");
			return null;
		}
		
		prepareMovimento(ArqListaComando.ALTERAR);
		
		SubAtividadeExtensaoDao dao = null;
		
		try{
			dao = getDAO(SubAtividadeExtensaoDao.class);
			obj = dao.findInformacoesAlteracaoSubAtividadeExtensao(idSubmissao);
			
			if ( ! SubAtividadeExtensaoHelper.isCoordenadorMiniAtividade(obj, getUsuarioLogado()) ){
				addMensagens(SubAtividadeExtensaoHelper.monstaMensagensErroAlteracaoMiniAtividade(obj, getUsuarioLogado()));
				return null;
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return telaAlteraDadosSubAtividadeExtensao();
	}
	
	
	/**
	 * Salva a atualização da informação da subAtividade na base.<br/>
     * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
	 *  <li>sigaa.war/extensao/SubAtividade/alteraDadosSubAtividadeExtensao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 *
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	public String atualizar() throws ArqException{
		
		if ( ! SubAtividadeExtensaoHelper.isCoordenadorMiniAtividade(obj, getUsuarioLogado()) ){
			addMensagens(SubAtividadeExtensaoHelper.monstaMensagensErroAlteracaoMiniAtividade(obj, getUsuarioLogado()));
			return null;
		}
		
		
		try{
			
			ListaMensagens erros =  obj.validate();
			
			if(erros.isErrorPresent()){
				throw new NegocioException(erros);
			}
			
			
			MovimentoCadastro movimento = new MovimentoCadastro(obj);
			movimento.setCodMovimento(ArqListaComando.ALTERAR);
			execute(movimento);
			
			addMensagemInformation("Atualização das informações da Mini Atividade realizada com sucesso! ");
			
			/** 
			 * @see AtividadeExtensaoMBean#getAtualizaMinhasAtividades()
			 * @see /extensao/Atividade/lista_minhas_atividades.jsp
			 */
			getCurrentRequest().setAttribute("_forcaAtualizacaoAtividades", true);
			
			return cancelar();
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;	
		}
	}
	
	
	
	
	/**
     * Inicia o caso de alteração remoção de uma sub atividade.
     * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
     * 	<li>sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp </li>
     * <ul>
	 * @throws ArqException 
     * 
     */
	public String preRemoverSubAtividade() throws ArqException {
		
		int idSubAtividade = getParameterInt("idSubAtividade", 0);
		
		if(idSubAtividade <= 0){
			addMensagemErro("Seleciona uma Mini Atividade");
			return null;
		}
		
		if( ! getAcessoMenu().isCoordenadorExtensao() ){
			addMensagemErro("Apenas Coordenadores de Extensão podem alterar uma Mini Atividade");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.RENOVAR_SUB_ATIVIDADE_EXTENSAO_EM_EXECUCAO);
		
		SubAtividadeExtensaoDao dao = null;
		
		try{
			dao = getDAO(SubAtividadeExtensaoDao.class);
			obj = dao.findInformacoesAlteracaoSubAtividadeExtensao(idSubAtividade);
			participantes =  dao.findInscritosSubAtividadeExtensao(idSubAtividade);
			
			if ( ! SubAtividadeExtensaoHelper.isCoordenadorMiniAtividade(obj, getUsuarioLogado()) ){
				addMensagens(SubAtividadeExtensaoHelper.monstaMensagensErroAlteracaoMiniAtividade(obj, getUsuarioLogado()));
				return null;
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return telaRemoveSubAtividadeExtensao();
	}
	
	
	/**
	 * 
	 * Salva a atualização da informação da subAtividade na base.<br/>
	 * @throws ArqException 
	 *
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
     * 	<li>sigaa.war/extensao/SubAtividade/removerSubAtividadeExtensao.jsp </li>
     * <ul>
	 * 
	 *
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	public String removerSubAtividade() throws ArqException{
		
		try{
			
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setObjMovimentado(obj);
			movimento.setCodMovimento(SigaaListaComando.RENOVAR_SUB_ATIVIDADE_EXTENSAO_EM_EXECUCAO);
			movimento.setColObjMovimentado(participantes);
			execute(movimento);
			
			enviaEmailInformativoParticipantes(participantes, obj);
			
			// as mesagens são exibidas na ordem inversa //
			addMensagemInformation("Um email infomativo foi enviado aos participantes, informando que a sua inscrição não é mais válida. ");
			addMensagemInformation("Todas as inscrições foram desatividas. ");
			addMensagemInformation("Mini Atividade removida com sucesso ! ");
		
			/** 
			 * @see AtividadeExtensaoMBean#getAtualizaMinhasAtividades()
			 * @see /extensao/Atividade/lista_minhas_atividades.jsp
			 */
			getCurrentRequest().setAttribute("_forcaAtualizacaoAtividades", true);
			
			AtividadeExtensaoMBean bean = getMBean("atividadeExtensao");
			return bean.listarMinhasAtividades();
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;	
		}
	}
	
	
	/**
	 * Envia um email para os participantes informanto do cancelamento da mini atividade.
	 *
	 * @param participantes2
	 */
	private void enviaEmailInformativoParticipantes(List<InscricaoAtividadeParticipante> participantesInscritos, SubAtividadeExtensao subAtividade) {
		
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		String textoRodape = RepositorioDadosInstitucionais.get("rodapeSistema");
		
		String assuntoEmail = "Mini Atividade Cancelada";
		
		List<StatusInscricaoParticipante> statusAtivos = Arrays.asList(StatusInscricaoParticipante.getStatusAtivos());
		
		for (InscricaoAtividadeParticipante participante : participantesInscritos) {
			
			if( statusAtivos.contains( participante.getStatusInscricao())){ // Só precisa enviar para aqueles que estavam inscritos //
				
				MailBody email = new MailBody();
				email.setAssunto("["+siglaSistema+"] "+assuntoEmail+" ");
				email.setContentType(MailBody.HTML);
				email.setNome(participante.getCadastroParticipante().getNome());
				email.setEmail(participante.getCadastroParticipante().getEmail());
				email.setReplyTo("noReply@ufrn.br");
				
				StringBuilder mensagem  = new StringBuilder("");
				
				mensagem.append("<html>");
				mensagem.append("<body>");
				
				mensagem.append("<div style=\"font-weight: bold; padding-top: 30px; padding-bottom: 30px; text-align: center;\"> ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO "+siglaSistema+". POR FAVOR, NÃO RESPONDÊ-LO. </div>");
				
				mensagem.append("<table style=\"width: 90%; margin-left: auto; margin-right: auto; border: 1px solid #C8D5EC;\">");
				mensagem.append("<caption style=\"font-weight: bold; color: #333366; background-color:#C8D5EC\" > Informativo de Cancelamento de Mini Atividade </caption>");
							
				mensagem.append("<tbody>");
				
					mensagem.append("<tr>");
					mensagem.append("<td>");
						
						mensagem.append("<p> Caro(a) "+ participante.getCadastroParticipante().getNome()+", </p>");
						mensagem.append("<p> Informamos que a Mini Atividade <i>\""+subAtividade.getTitulo()+"\"</i> para a qual o(a) senhor(a) estava inscrito(a) foi cancelada pelo coordenador. Ou seja, ela não ocorrerá mais.</p>");
						mensagem.append("<p> Por consequência, a sua e as demais inscrições para essa atividade foram invalidadas no sistema. </p>");
					
					mensagem.append("</td>");
					mensagem.append("</tr>");	
				
				mensagem.append("</tbody>");

				mensagem.append("<tfoot>");
				
					mensagem.append("<tr>");
					mensagem.append("<td style=\"font-style: italic; font-weight: bold; padding-top: 30px; text-align: center; font-size: small;\">");
						mensagem.append("<p> Não nos responsabilizamos pelo não recebimento deste email por qualquer motivo técnico.</p>");
					mensagem.append("</td>");
					mensagem.append("</tr>");
						
				mensagem.append("</tfoot>");
				
				mensagem.append("</table>");
				
				mensagem.append("<div style=\"padding-top: 30px; padding-bottom: 30px;  text-align: center; width: 90%; font-size: x-small;\"> "+siglaSistema +" | "+ textoRodape +" </div>");
					
				mensagem.append("</body>");	
				mensagem.append("</html>");
				
				
				email.setMensagem(mensagem.toString());
				Mail.send(email);	
			}
			
		
		}
		
		
		
	}


	/**
	 *  Redireciona para a página de alteração de sub atividades.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaAlteraDadosSubAtividadeExtensao(){
		return forward(PAGINA_ALTERA_DADOS_SUB_ATIVIDADE_EXTENSAO);
	}

	
	/**
	 *  Redireciona para a página para remover uma sub atividade.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaRemoveSubAtividadeExtensao(){
		return forward(PAGINA_REMOVE_SUB_ATIVIDADE);
	}
	
	
	
	/**
	 * Sobre escreve o método cancelar para retorna paga a página que chamou o caso de uso,
	 *  em vez de retorna para o menu principal do módulo..<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		if(StringUtils.isNotEmpty(paginaRetorno))
			return forward(paginaRetorno);
		else{
			return super.cancelar();
		}
	}

	public String getPaginaRetorno() {
		return paginaRetorno;
	}

	public void setPaginaRetorno(String paginaRetorno) {
		this.paginaRetorno = paginaRetorno;
	}


	public List<InscricaoAtividadeParticipante> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(List<InscricaoAtividadeParticipante> participantes) {
		this.participantes = participantes;
	}
	
	
	
}
