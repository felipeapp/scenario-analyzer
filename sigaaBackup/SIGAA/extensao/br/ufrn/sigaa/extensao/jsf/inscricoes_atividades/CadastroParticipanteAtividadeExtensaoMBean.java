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

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.CadastroParticipanteAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.extensao.helper.EnviarEmailExtensaoHelper;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoCadastroParticipanteExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 *
 * <p>MBean respons�vel por gerenciar <strong> *** APENAS !!! *** </strong> o cadastro de participantes de cursos e eventos de extens�o. </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
@Component("cadastroParticipanteAtividadeExtensaoMBean")
@Scope("request")
public class CadastroParticipanteAtividadeExtensaoMBean  extends SigaaAbstractController<CadastroParticipanteAtividadeExtensao>{

	
	/** P�gina de cadastros para acessar os cursos e eventos extens�o. Essa p�gina � aberta, pode ser acessada sem o usu�rio est� logado. */
	public final static String PAGINA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/cadastroParticipanteCursosEventosExtensao.jsp";
	
	/** P�gina de cadastros para acessar os cursos e eventos extens�o. Essa p�gina � fechada, o usu�rio est� logado deve est� logado. */
	public final static String PAGINA_ALTERA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/alteraCadastroParticipanteCursosEventosExtensao.jsp";
	
	/** P�gina de confirma��o do cadastro do usu�rio. */
	public final static String PAGINA_CONFIRMA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/confirmaCadastroParticipanteCursosEventosExtensao.jsp";
	
	
	/** P�gina para o usu�rio poder recuperar sua senha caso perca */
	public final static String REENVIA_SENHA_ACESSO_CADASTROS_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/reenviaSenhaAcessoCadastroCursosEventosExtensao.jsp";
	
	
	/** Atributo utilizado para armazenar as unidades federativas */
	private List<UnidadeFederativa> unidadeFederativas;
	
	/** Atributo utilizado para armazenar os munic�pios */
	private List<Municipio> municipios= new ArrayList<Municipio>();
	
	/** Usado no cadastra para confirmar se o email foi digitado corretamente. */
	private String emailConfirmacao;
	
	/** Usado no cadastra para confirmar se a senha foi digitado corretamente. */
	private String senhaConfirmacao;
	
	
	
	/** O c�digo de acesso utilizado para validar o email utilizado no cadastro dos participantes de cursos e eventos de extensao. 
	 *   Ou altera��o da senha do participante.
	 * 
	 *  Esse dado � preenchidos pelo pretty-faces quando o usu�rio acessa o link /link/public/extensao/confirmarCadastro/"codigo"/"id"
	 *  Esse dado � preenchidos pelo pretty-faces quando o usu�rio acessa o link /link/public/extensao/confirmarAlteracaoSenha/"codigo"/"id"
	 *  @see pretty-config.xml
	 */
	private String codigoAcessoConfirmacaoInscricao;
	
	
	/** O Id do cadastra realizado mais ainda n�o confirmado ou que a senha foi alterada.
	 *  
	 *  Esse dado � preenchidos pelo pretty-faces quando o usu�rio acessa o link /link/public/extensao/confirmarCadastro/"codigo"/"id"
	 *  Esse dado � preenchidos pelo pretty-faces quando o usu�rio acessa o link /link/public/extensao/confirmarAlteracaoSenha/"codigo"/"id"
	 *  @see pretty-config.xml
	 */
	private Integer idCadastroParticipanteNaoConfirmado;
	
	
	
	///////////////// A parte de realizar um novo cadastro  ///////////////////////
	
	
	
	/**
	 * <p>M�todo chamado para o cadastro de uma novo participante para cursos e eventos de extens�o.</p>
	 * 
	 * <p>S� deixa cadastrar caso o usu�rio nunca tenha realizado nenhum cadastro na parte de cursos 
	 * e eventos, se j� realizou tem que usar a conta que tem.</p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/loginCurosEventosExtensao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String iniciarCadastroNovoParticipante() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO);
		
		obj = new CadastroParticipanteAtividadeExtensao();  // os dados no novo cadastr�o v�o ficar aqui.
		obj.setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		obj.setMunicipio(new Municipio(-1));
		carregarMunicipios(null);
		return telaCadastraParticipanteCursosEventosExtensao();
	}
	
	
	
	/**
	 * <p>Cadastra o nome pretendente a participante de curso de extens�o no banco como n�o confirmado.</p> 
	 * <p>Ele tem que confirmar o email informado no pr�ximo passo para o cadastro ser efetivado.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/cadatroParticipantesCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String cadastrarNovoParticipante() throws ArqException{
		
		
		MovimentoCadastroParticipanteExtensao mov = new MovimentoCadastroParticipanteExtensao(obj, emailConfirmacao, senhaConfirmacao);
		mov.setCodMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO);
		
		try{
			CadastroParticipanteAtividadeExtensao cadastroRealizado  = execute(mov); // retorna o objeito persitido
			
			addMensagemInformation("Cadastro Realizado com Sucesso!");
			addMensagemWarning("Foi enviado um e-mail de confirma��o para o endere�o "+obj.getEmail()+". Ser� necess�rio realizar a confirma��o para concluir o seu cadastro.");
			
			enviaEmailEnderecoConfirmacaoCadastro(cadastroRealizado);
			
			adcionaMensagemTelaConfirmacao("Confirme seu cadastro por meio do link enviado para o endere�o:  "+obj.getEmail() +" ");
			
			return telaConfirmaCadastroParticipanteCursosEventosExtensao();
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	
	/** Como na tela de confirma��o usa redirect, tem que colocar as mensagem em sess�o. N�o n�o pega. **/
	private void adcionaMensagemTelaConfirmacao(String mensagem){
		getCurrentSession().setAttribute("mensagemTelaConfirmacao", mensagem);
	}
	
	
	/**
	 *   Envia o email com o link para o usu�rio acessar e confirma o cadastro.
	 *   
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 */
	private void enviaEmailEnderecoConfirmacaoCadastro(CadastroParticipanteAtividadeExtensao cadastroRealizado ) {
		
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("Foi realizada uma solicita��o de cadastramento na �rea de Cursos e Eventos de Extens�o para o seu e-mail.<br/><br/><br/> ");
		mensagem.append("Para confirmar seu cadastro no sistema acesse o endere�o abaixo:<br/><br/> ");
	    
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		
	    String link = ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO)+"/"+StringUtils.lowerCase(siglaSistema)+"/link/public/extensao/confirmarCadastro/"+cadastroRealizado.getCodigoAcessoConfirmacao()+ "/"+cadastroRealizado.getId();
	    
		mensagem.append("<a href="+link+">"+link+"</a> <br/><br/>");
	
		mensagem.append("<strong>Caso n�o tenha solicitado nenhum cadastro, por favor, ignore este e-mail para que ele n�o seja realizado.</strong> <br/><br/> ");
		
		
		EnviarEmailExtensaoHelper.enviarEmail("["+siglaSistema+"] "+"Confirma��o de Cadastro", "Confirma��o de Cadastro para Cursos e Eventos de Extens�o", cadastroRealizado.getNome(), cadastroRealizado.getEmail(), mensagem.toString(), null, null);
		
	}


	/**
	 *  <p>Realiza o opera��o de confirmar um cadastro para participar de um curso ou evento de extens�o.</p>
	 *
	 *  <p>Esse m�tdo � chamdo a partir do pretty-config.xml quando o usu�rio acessa o endere�o: 
	 *      link /link/public/extensao/confirmarCadastro/"codigo"/"id"</p>
	 *
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String confirmarCadastroParticipante() throws ArqException{
		
		CadastroParticipanteAtividadeExtensaoDao dao = null;
		
		try{
			dao = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
		
			obj = dao.findCadastroByCodigoAcesso(codigoAcessoConfirmacaoInscricao, idCadastroParticipanteNaoConfirmado, false);
		
		}finally{
			if(dao != null) dao.close();
		}
		
		if (obj != null) {
			
			// aqui n�o tem problema em preparar e executar pois a execu��o em duplicidade � garantida pela busca anterior
			prepareMovimento(SigaaListaComando.CONFIRMA_CADASTRO_PARTICIPANTE_EXTENSAO);
			
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setObjMovimentado(obj);
			movimento.setCodMovimento(SigaaListaComando.CONFIRMA_CADASTRO_PARTICIPANTE_EXTENSAO);
			
			try {
				execute(movimento);
				adcionaMensagemTelaConfirmacao("Cadastro do participante "+obj.getEmail()+" confirmado com sucesso!");
				addMensagemInformation("Cadastro do participante "+obj.getEmail()+" confirmado com sucesso!");
				
			} catch (NegocioException e) {
				adcionaMensagemTelaConfirmacao("Cadastro inv�lido ou j� foi confirmado!");
				addMensagemErro("Cadastro inv�lido ou j� foi confirmado!");
			} 
		}else{
			adcionaMensagemTelaConfirmacao("Cadastro inv�lido ou j� foi confirmado!");
			addMensagemErro("Cadastro inv�lido ou j� foi confirmado!");
		}
		
		return telaConfirmaCadastroParticipanteCursosEventosExtensao();
	}
	
	
	
	
	
	
	
	
	////////////////// A parte de renviar a senha de acesso  /////////////////////
	
	
	
	/**
	 * Inicia a caso de suo que permite o usu�rio receber uma nova senha caso ele esque�a, nesse 
	 * caso o sistema gerar� outra senha que o usu�rio poder� trocar depois, pois n�o temos acesso a 
	 * senha original cadastrada pelo usu�rio. 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<ul>
	 * 			<li>/sigaa.war/public/extensao/loginCurosEventosExtensao.jsp</li>
	 * 		</ul>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarReenvioSenhaParticipante() throws ArqException{
		
		prepareMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO);
		
		obj = new CadastroParticipanteAtividadeExtensao();  // os dados que o usu�rio vai digitar. no caso: email, data nascimento, cpf ou passaporte.
		
		return telaReenviaSenhaAcessoCadstrosCursosEventosExtensao();
	}
	
	
	
	
	
	
	/**
	 * <p>Caso um cadastro com os dados informados exista, vai ser gerado uma nova senha autom�tica 
	 * pelo sistema e enviada ao email do usu�rio. A senha s� vai ser alterada de fato se ele confirmar acesso a conta do 
	 * e-mail cadastrado.</p>
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<ul>
	 * 			<li>/sigaa.war/public/extensao/reenviaSenhaAcessoCadastroCursosEventosExtensao.jsp</li>
	 * 		</ul>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String reenviarSenhaParticipante() throws ArqException{
		
		CadastroParticipanteAtividadeExtensaoDao dao = null;
		
		CadastroParticipanteAtividadeExtensao cadastroBuscado = null;
		
		try{
			dao = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
		
			boolean contemErro = verificaDadosFormularioReenviaSenha();
			if( contemErro )
				return null;
			
			cadastroBuscado = dao.findCadastroByInformacoesAlterarSenha(obj.getEmail(), obj.getDataNascimento(), obj.isEstrangeiro(), obj.getCpf(), obj.getPassaporte());
		
		}finally{
			if(dao != null) dao.close();
		}
		
		if(cadastroBuscado != null){
			
			// aqui n�o tem problema em preparar e executar pois a execu��o em duplicidade � garantida pela busca anterior
			prepareMovimento(SigaaListaComando.GERAR_NOVA_SENHA_CADASTRO_EXTENSAO);
			
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setObjMovimentado(cadastroBuscado);
			movimento.setCodMovimento(SigaaListaComando.GERAR_NOVA_SENHA_CADASTRO_EXTENSAO);
			
			try {
				
				CadastroParticipanteAtividadeExtensao cadastroSolicitado = execute(movimento);
				
				addMensagemInformation("Solicita��o de altera��o de senha do participante "+cadastroSolicitado.getEmail()+" foi realizada sucesso!");
				
				enviaEmailEnderecoAlteracaoSenha(cadastroSolicitado);
				
				adcionaMensagemTelaConfirmacao(" Uma nova senha de acesso foi enviado para o e-mail:  "
					+cadastroSolicitado.getEmail() +" "+" acesse o endere�o enviado no e-mail para confirmar a altera��o da senha.");
				
			} catch (NegocioException e) {
				addMensagemErro("Endere�o para altera��o da senha inv�lido!");
			} 
			
		}else{
			addMensagemErro("N�o existe um cadastro com as informa��es fornecidas.");
			return null;
		}
		
		return telaConfirmaCadastroParticipanteCursosEventosExtensao();
	}



	/**
	 * Verifica se os dados do formul�rio foram preenchidos
	 */
	private boolean verificaDadosFormularioReenviaSenha() {
		
		boolean contemErro = false;
		
		if( StringUtils.isEmpty(obj.getEmail()) ){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "E-mail");
			contemErro = true;
		}
		
		if( obj.getDataNascimento() == null ){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Nascimento");
			contemErro = true;
		}
		
		if( (obj.isEstrangeiro() &&  StringUtils.isEmpty(obj.getPassaporte()) ) ){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Passaporte");
			contemErro = true;
		}
		
		if( (! obj.isEstrangeiro() &&  ( obj.getCpf() == null || obj.getCpf() <= 0 ) ) ){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
			contemErro = true;
		}
		
		return contemErro;
	}
	
	
	
	/**
	 *   Envia o email com o link para o usu�rio acessar e confirma o cadastro.
	 *   
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 */
	private void enviaEmailEnderecoAlteracaoSenha(CadastroParticipanteAtividadeExtensao cadastroSolicitado) {
		
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("  Uma solici��o de altera��o de senha foi realizada para o seu cadastro no sistema. <br/><br/> ");
		mensagem.append("  Acesse o endere�o abaixo para confirmar a altera��o da sua senha: <br/><br/> ");
	    
		String linkAcesso = ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO)
				+"/sigaa/link/public/extensao/confirmarAlteracaoSenha/"+cadastroSolicitado.getCodigoAcessoConfirmacao()+ "/"+cadastroSolicitado.getId();
		
		mensagem.append(" <a href="+linkAcesso+">"+linkAcesso+"</a> <br/><br/>");
		
		mensagem.append("A nova senha criada foi:  <strong>"+cadastroSolicitado.getSenhaGerada()+"</strong> <br/>");
		
		mensagem.append("Ap�s realizar o login no sistema ser� poss�vel alterar a senha para uma de sua prefer�ncia. <br/> ");
		
		mensagem.append(" <br/><br/> <strong>Caso n�o tenha solicitado nenhuma altera��o de senha, por favor ignore esse e-mail para que a senha continue a atual. </strong>");
		
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		
		mensagem.append(" <br/><br/> O "+siglaSistema+" n�o envia por e-emails pedidos de informa��es cadastrais como CPF, endere�os e senhas.");
		
		EnviarEmailExtensaoHelper.enviarEmail("["+siglaSistema+"] "+"Confirma��o de Altera��o de Senha"
				, "Confirma��o de Altera��o de Senha para Cursos e Eventos de Extens�o"
				, cadastroSolicitado.getNome(), cadastroSolicitado.getEmail(), mensagem.toString(), null, null);
		
	}

	
	
	
	/**
	 *  <p>Caso o usu�rio confirme o link enviado para o email, vai ser gerado o hash da senha gerada 
	 * e salvo no banco no banco "senha".</p>
	 * 
	 *   <p>Assim apenas se ele for o dono do email de fato vai ser alterado a senha usada para se logar no sistema. 
	 *   Garantindo o m�nimo de seguran�a.</p>
	 *
	 *  <p>Esse m�todo � chamado a partir do pretty-config.xml quando o usu�rio acessa o endere�o: 
	 *      link /link/public/extensao/confirmarAlteracaoSenha/"codigo"/"id"/"senhaGerada"</p>
	 *
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String confirmarAlteracaoSenhaAcesso() throws ArqException{
		
		CadastroParticipanteAtividadeExtensaoDao dao = null;
		
		CadastroParticipanteAtividadeExtensao cadastro = null;
		
		try{
			dao = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
		
			cadastro = dao.findCadastroByCodigoAcesso(codigoAcessoConfirmacaoInscricao, idCadastroParticipanteNaoConfirmado, true);
		
		}finally{
			if(dao != null) dao.close();
		}
		
		if (cadastro != null) {
			
			// aqui n�o tem problema em preparar e executar pois a execu��o em duplicidade � garantida pela busca anterior
			prepareMovimento(SigaaListaComando.CONFIRMA_ALTERACAO_SENHA_CADASTRO_EXTENSAO);
			
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setObjMovimentado(cadastro);
			movimento.setCodMovimento(SigaaListaComando.CONFIRMA_ALTERACAO_SENHA_CADASTRO_EXTENSAO);
			
			try {
				execute(movimento);
				adcionaMensagemTelaConfirmacao("Senha do participante "+cadastro.getEmail()+" alterada com sucesso!");
				addMensagemInformation("Senha do participante "+cadastro.getEmail()+" alterada com sucesso!");
				
			} catch (NegocioException e) {
				addMensagemErro("Endere�o para altera��o da senha inv�lido!");
			} 
		}else{
			addMensagemErro("Endere�o para altera��o da senha inv�lido!");
		}
		
		return telaConfirmaCadastroParticipanteCursosEventosExtensao();
	}
	
	
	
	
	
	
	
	
	
	
	//////////////////////// A parte de altera��o  do cadastro do usu�rio j� logado  ///////////////
	
	
	/**
	 * <p>M�todo chamado para o cadastro de uma novo participante para cursos e eventos de extens�o.</p>
	 * 
	 * <p>S� deixa cadastrar caso o usu�rio nunca tenha realizado nenhum cadastro na parte de cursos 
	 * e eventos, se j� realizou tem que usar a conta que tem.</p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/loginCurosEventosExtensao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String iniciarAlteracaoDadosParticipante() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO);
		
		GerenciaAreaInternaCursosEventoExtensaoMBean gerencia = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
		
		obj = gerencia.getParticipanteLogadoAreaInterna();
		GenericDAO dao = null;
		try{
			dao = getGenericDAO();
			// Buscas todos os dados cadastrados para alterar
			obj = dao.findByPrimaryKey(obj.getId(), CadastroParticipanteAtividadeExtensao.class);
		}finally{
			if(dao != null) dao.close();
		}
		
		carregarMunicipios(null);
		return gerencia.forwardPaginaInterna(PAGINA_ALTERA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO);
	}
	
	
	
	
	
	/**
	 * <p>Cadastra o nome pretendente a participante de curso de extens�o no banco como n�o confirmado.</p> 
	 * <p>Ele tem que confirmar o email informado no pr�ximo passo para o cadastro ser efetivado.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/cadatroParticipantesCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String alteraDadosParticipante() throws ArqException{
		
		
		MovimentoCadastroParticipanteExtensao mov = new MovimentoCadastroParticipanteExtensao(obj, emailConfirmacao, senhaConfirmacao);
		mov.setCodMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO);
		
		try{
			execute(mov);
			
			addMensagemInformation("Cadastro alterado com sucesso!");
		
			getCurrentSession().setAttribute("participanteCursosEventosExtensaoLogado", obj); // substitui os dados do usu�rio logado na sess�o
			
			GerenciaAreaInternaCursosEventoExtensaoMBean gerencia = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
			
			return gerencia.forwardPaginaInternaPadrao();
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	
	
	
	
	/////// Tela de navega��o ///////////////
	
	
	/** <p>M�todo n�o chamado por nenhuma p�gina jsp.</p> */
	public String telaCadastraParticipanteCursosEventosExtensao(){
		return forward(PAGINA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO);
	}
	
	
	/** <p>M�todo n�o chamado por nenhuma p�gina jsp.</p> 
	 *  <p> Tem que usar redirect sen�o o pretty faces n�o acha o caminho.</p>
	 */
	public String telaConfirmaCadastroParticipanteCursosEventosExtensao(){
		return redirect(PAGINA_CONFIRMA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO.replace(".jsp", ".jsf"));
	}
	
	
	/** <p>M�todo n�o chamado por nenhuma p�gina jsp.</p> */
	public String telaReenviaSenhaAcessoCadstrosCursosEventosExtensao(){
		return forward(REENVIA_SENHA_ACESSO_CADASTROS_CURSOS_EVENTOS_EXTENSAO);
	}
	
	
	
	///////////////////////////////////////////////
	
	
	
	/**
	 * Retorna as unidades federetivas cadastradas no sistema para o combo box da p�gina.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/cadastroParticipanteCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getUnidadesFederativasCombo() throws DAOException{
		
		if(unidadeFederativas == null || unidadeFederativas.size() == 0){
			GenericDAO dao = null;
			try{
				dao = getGenericDAO();
				unidadeFederativas = new ArrayList<UnidadeFederativa>(dao.findAllProjection(UnidadeFederativa.class, new String[]{"id", "descricao"} ));
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return toSelectItems(unidadeFederativas, "id", "descricao");
	}
	

	/**
	 * Retorna os munic�pois das unidade federativa selecionada para o combo box da p�gina.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/cadastroParticipanteCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getMunicipiosCombo() throws DAOException{
		List<SelectItem> temp = new ArrayList<SelectItem>();
		
		for (Municipio municipio : municipios) {
			temp.add(new SelectItem(municipio.getId(), municipio.getNome()));
		}
		
		return temp;
	}
	
	
	/**
	 * Carrega os munic�pios de uma unidade federativa selecioando pelo usu�rio na p�gina de cadastro.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/cadastroParticipanteCursosEventosExtensao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void carregarMunicipios(ActionEvent evet) throws DAOException {		
		MunicipioDao dao = null;
		
		try{
			dao = getDAO(MunicipioDao.class);
			
			if(obj.getUnidadeFederativa() == null){
				obj.setUnidadeFederativa( new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
			}
			
			UnidadeFederativa ufSelecionada = dao.findByPrimaryKey(obj.getUnidadeFederativa().getId(), UnidadeFederativa.class, new String[]{"id"});
			
			municipios = new ArrayList<Municipio>();
			
			if(ufSelecionada != null){
				
				UnidadeFederativa ufComCapital = dao.findByPrimaryKey(obj.getUnidadeFederativa().getId(), UnidadeFederativa.class, new String[]{"capital.id"});
				if(ufComCapital != null && ufComCapital.getCapital() != null)
					obj.getMunicipio().setId(ufComCapital.getCapital().getId());
				
				municipios.addAll( dao.findByUF(ufSelecionada.getId(), new String[]{"id", "nome"}) );
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
	}

	public String getEmailConfirmacao() {	return emailConfirmacao;}
	public void setEmailConfirmacao(String emailConfirmacao) {	this.emailConfirmacao = emailConfirmacao;}
	public String getSenhaConfirmacao() {	return senhaConfirmacao;}
	public void setSenhaConfirmacao(String senhaConfirmacao) {this.senhaConfirmacao = senhaConfirmacao;}
	public String getCodigoAcessoConfirmacaoInscricao() {return codigoAcessoConfirmacaoInscricao;}
	public void setCodigoAcessoConfirmacaoInscricao(String codigoAcessoConfirmacaoInscricao) {this.codigoAcessoConfirmacaoInscricao = codigoAcessoConfirmacaoInscricao;}
	public Integer getIdCadastroParticipanteNaoConfirmado() {	return idCadastroParticipanteNaoConfirmado;}
	public void setIdCadastroParticipanteNaoConfirmado(Integer idCadastroParticipanteNaoConfirmado) {	this.idCadastroParticipanteNaoConfirmado = idCadastroParticipanteNaoConfirmado;}
	
	
}
