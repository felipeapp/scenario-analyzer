/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>MBean respons�vel por gerenciar <strong> *** APENAS !!! *** </strong> o login de recupera��o de 
 * senha da �rea restrita dos participantes de cursos  e eventos de extens�o.  </p>
 * 
 * @author jadson
 *
 */
@Component("logonCursosEventosExtensaoMBean")
@Scope("request")
public class LogonCursosEventosExtensaoMBean extends SigaaAbstractController<CadastroParticipanteAtividadeExtensao>{

	/** P�gina de login na �rea p�blica do sigaa para cursos e eventos de extens�o. Essa p�gina � aberta, pode ser acessada sem o usu�rio est� logado.  */
	public final static String PAGINA_LOGIN_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/loginCursosEventosExtensao.jsp";
	
	/** O email digitado no processo de login */
	private String email;
	
	/** A senha digitada no processo de login */
	private String senha;
	
	
	/**
	 * <p>Valida o login do inscrito ou participante pelo email e senha informados. Caso o usu�rio esteja cadastrado, libera 
	 * o acesso � �rea privada do usu�rio que fica na parte p�blica do sigaa.</p>
	 * 
	 * <p>Esse login na �rea p�blica � usado porque muitas vezes o usu�rio que participa dos cursos eventos de extens�o 
	 * n�o s�o usu�rios internos do sigaa. Ent�o � criado um novo cadastros para eles e acesso pela �rea p�blica.<p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
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
				
				/* AQUI EST� A LIBERA��O DO ACESSO.
				 * TODAS P�GINAS PRIVADAS DE CURSOS E EVENTOS DE EXTENS�O DEVEM VERIFICAR SE TEM PARTICIPANTE LOGADO.
				 * J� QUE O VIEW FILTER N�O VAI VERIFICAR.
				 */
				getCurrentSession().setAttribute("participanteCursosEventosExtensaoLogado", obj); 
				return forward(GerenciaAreaInternaCursosEventoExtensaoMBean.AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO);
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	
	
	/**
	 * <p>Invalida a se��o do usu�rio para n�o permitir mais o acesso � �rea restrita</p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
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
	 * Redireciona para a tela de login da �rea restrita dos curso e eventos.  
	 * TEM QUE SER REDIRECT SEN�O D� ERRO DE ViewExpiredExceptio pretty faces. 
	 *
	 *   <p>M�todo chamado a partir do arquivo pretty-confi.xml do sigaa.</p>
	 *
	 * @return
	 */
	public String redirectTelaLoginCursosEventosExtensao() {
		return redirect(PAGINA_LOGIN_CURSOS_EVENTOS_EXTENSAO.replace(".jsp", ".jsf"));
	}
	
	
	/** 
	 * Tela de navega��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
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
