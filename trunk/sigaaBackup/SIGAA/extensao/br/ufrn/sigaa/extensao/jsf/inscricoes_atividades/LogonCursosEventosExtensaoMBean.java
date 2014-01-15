/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/10/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.CadastroParticipanteAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.mensagens.MensagensExtensao;

/**
 *
 * <p>MBean responsável por gerenciar <strong> *** APENAS !!! *** </strong> o login de recuperação de 
 * senha da área restrita dos participantes de cursos  e eventos de extensão.  </p>
 * 
 * @author jadson
 *
 */
@Component("logonCursosEventosExtensaoMBean")
@Scope("request")
public class LogonCursosEventosExtensaoMBean extends SigaaAbstractController<CadastroParticipanteAtividadeExtensao>{

	/** Página de login na área pública do sigaa para cursos e eventos de extensão. Essa página é aberta, pode ser acessada sem o usuário está logado.  */
	public final static String PAGINA_LOGIN_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/loginCursosEventosExtensao.jsp";
	
	/** O email digitado no processo de login */
	private String email;
	
	/** A senha digitada no processo de login */
	private String senha;
	
	
	/**
	 * <p>Valida o login do inscrito ou participante pelo email e senha informados. Caso o usuário esteja cadastrado, libera 
	 * o acesso à área privada do usuário que fica na parte pública do sigaa.</p>
	 * 
	 * <p>Esse login na área pública é usado porque muitas vezes o usuário que participa dos cursos eventos de extensão 
	 * não são usuários internos do sigaa. Então é criado um novo cadastros para eles e acesso pela área pública.<p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/loginCurosEventosExtensao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String entrarAreaRetritaParticipante() throws DAOException {

		if( StringUtils.isEmpty(email) ){
			addMensagemErro("Digite o email cadastrado");
			return null;
		}
		
		if( StringUtils.isEmpty(senha) ){
			addMensagemErro("Digite a senha cadastrada");
			return null;
		}
		
		
		CadastroParticipanteAtividadeExtensaoDao dao = null;
		
		try{
			dao = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
			
			// Para gerar o hash da senha //
			CadastroParticipanteAtividadeExtensao cadatroTemp = new CadastroParticipanteAtividadeExtensao();
			cadatroTemp.setSenha(senha);
			cadatroTemp.geraHashSenha();
			////////////////////////////////
			
			obj = dao.findCadastroParticipanteByEmailSenha(email, cadatroTemp.getSenha());
			
			
			
			if(obj == null){
				addMensagem(MensagensExtensao.EMAIL_SENHA_INVALIDOS);
				return null;
			}else{
				
				/* AQUI ESTÁ A LIBERAÇÃO DO ACESSO.
				 * TODAS PÁGINAS PRIVADAS DE CURSOS E EVENTOS DE EXTENSÃO DEVEM VERIFICAR SE TEM PARTICIPANTE LOGADO.
				 * JÁ QUE O VIEW FILTER NÃO VAI VERIFICAR.
				 */
				getCurrentSession().setAttribute("participanteCursosEventosExtensaoLogado", obj); 
				return forward(GerenciaAreaInternaCursosEventoExtensaoMBean.AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO);
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	
	
	/**
	 * <p>Invalida a seção do usuário para não permitir mais o acesso à área restrita</p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/loginCurosEventosExtensao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String sairAreaRetritaParticipante() throws DAOException {

		getCurrentSession().invalidate();
		return telaLoginCursosEventosExtensao();
	}
	
	
	/***
	 * Redireciona para a tela de login da área restrita dos curso e eventos.  
	 * TEM QUE SER REDIRECT SENÃO DÁ ERRO DE ViewExpiredExceptio pretty faces. 
	 *
	 *   <p>Método chamado a partir do arquivo pretty-confi.xml do sigaa.</p>
	 *
	 * @return
	 */
	public String redirectTelaLoginCursosEventosExtensao() {
		return redirect(PAGINA_LOGIN_CURSOS_EVENTOS_EXTENSAO.replace(".jsp", ".jsf"));
	}
	
	
	/** 
	 * Tela de navegação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/cadastroParticipantesCursosEventosExtensao.jsp</li>
	 * </ul> 
	 */
	public String telaLoginCursosEventosExtensao(){
		return forward(PAGINA_LOGIN_CURSOS_EVENTOS_EXTENSAO);
	}


	
	public String getEmail() {return email;}
	public void setEmail(String email) {this.email = email;}
	public String getSenha() {return senha;}
	public void setSenha(String senha) {this.senha = senha;}
	
}
