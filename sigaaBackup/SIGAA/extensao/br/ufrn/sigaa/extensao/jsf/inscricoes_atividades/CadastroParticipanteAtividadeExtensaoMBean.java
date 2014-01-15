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
 * <p>MBean responsável por gerenciar <strong> *** APENAS !!! *** </strong> o cadastro de participantes de cursos e eventos de extensão. </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
@Component("cadastroParticipanteAtividadeExtensaoMBean")
@Scope("request")
public class CadastroParticipanteAtividadeExtensaoMBean  extends SigaaAbstractController<CadastroParticipanteAtividadeExtensao>{

	
	/** Página de cadastros para acessar os cursos e eventos extensão. Essa página é aberta, pode ser acessada sem o usuário está logado. */
	public final static String PAGINA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/cadastroParticipanteCursosEventosExtensao.jsp";
	
	/** Página de cadastros para acessar os cursos e eventos extensão. Essa página é fechada, o usuário está logado deve está logado. */
	public final static String PAGINA_ALTERA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/alteraCadastroParticipanteCursosEventosExtensao.jsp";
	
	/** Página de confirmação do cadastro do usuário. */
	public final static String PAGINA_CONFIRMA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/confirmaCadastroParticipanteCursosEventosExtensao.jsp";
	
	
	/** Página para o usuário poder recuperar sua senha caso perca */
	public final static String REENVIA_SENHA_ACESSO_CADASTROS_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/reenviaSenhaAcessoCadastroCursosEventosExtensao.jsp";
	
	
	/** Atributo utilizado para armazenar as unidades federativas */
	private List<UnidadeFederativa> unidadeFederativas;
	
	/** Atributo utilizado para armazenar os municípios */
	private List<Municipio> municipios= new ArrayList<Municipio>();
	
	/** Usado no cadastra para confirmar se o email foi digitado corretamente. */
	private String emailConfirmacao;
	
	/** Usado no cadastra para confirmar se a senha foi digitado corretamente. */
	private String senhaConfirmacao;
	
	
	
	/** O código de acesso utilizado para validar o email utilizado no cadastro dos participantes de cursos e eventos de extensao. 
	 *   Ou alteração da senha do participante.
	 * 
	 *  Esse dado é preenchidos pelo pretty-faces quando o usuário acessa o link /link/public/extensao/confirmarCadastro/"codigo"/"id"
	 *  Esse dado é preenchidos pelo pretty-faces quando o usuário acessa o link /link/public/extensao/confirmarAlteracaoSenha/"codigo"/"id"
	 *  @see pretty-config.xml
	 */
	private String codigoAcessoConfirmacaoInscricao;
	
	
	/** O Id do cadastra realizado mais ainda não confirmado ou que a senha foi alterada.
	 *  
	 *  Esse dado é preenchidos pelo pretty-faces quando o usuário acessa o link /link/public/extensao/confirmarCadastro/"codigo"/"id"
	 *  Esse dado é preenchidos pelo pretty-faces quando o usuário acessa o link /link/public/extensao/confirmarAlteracaoSenha/"codigo"/"id"
	 *  @see pretty-config.xml
	 */
	private Integer idCadastroParticipanteNaoConfirmado;
	
	
	
	///////////////// A parte de realizar um novo cadastro  ///////////////////////
	
	
	
	/**
	 * <p>Método chamado para o cadastro de uma novo participante para cursos e eventos de extensão.</p>
	 * 
	 * <p>Só deixa cadastrar caso o usuário nunca tenha realizado nenhum cadastro na parte de cursos 
	 * e eventos, se já realizou tem que usar a conta que tem.</p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/loginCurosEventosExtensao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String iniciarCadastroNovoParticipante() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO);
		
		obj = new CadastroParticipanteAtividadeExtensao();  // os dados no novo cadastrão vão ficar aqui.
		obj.setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		obj.setMunicipio(new Municipio(-1));
		carregarMunicipios(null);
		return telaCadastraParticipanteCursosEventosExtensao();
	}
	
	
	
	/**
	 * <p>Cadastra o nome pretendente a participante de curso de extensão no banco como não confirmado.</p> 
	 * <p>Ele tem que confirmar o email informado no próximo passo para o cadastro ser efetivado.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemWarning("Foi enviado um e-mail de confirmação para o endereço "+obj.getEmail()+". Será necessário realizar a confirmação para concluir o seu cadastro.");
			
			enviaEmailEnderecoConfirmacaoCadastro(cadastroRealizado);
			
			adcionaMensagemTelaConfirmacao("Confirme seu cadastro por meio do link enviado para o endereço:  "+obj.getEmail() +" ");
			
			return telaConfirmaCadastroParticipanteCursosEventosExtensao();
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	
	/** Como na tela de confirmação usa redirect, tem que colocar as mensagem em sessão. Não não pega. **/
	private void adcionaMensagemTelaConfirmacao(String mensagem){
		getCurrentSession().setAttribute("mensagemTelaConfirmacao", mensagem);
	}
	
	
	/**
	 *   Envia o email com o link para o usuário acessar e confirma o cadastro.
	 *   
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 */
	private void enviaEmailEnderecoConfirmacaoCadastro(CadastroParticipanteAtividadeExtensao cadastroRealizado ) {
		
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("Foi realizada uma solicitação de cadastramento na área de Cursos e Eventos de Extensão para o seu e-mail.<br/><br/><br/> ");
		mensagem.append("Para confirmar seu cadastro no sistema acesse o endereço abaixo:<br/><br/> ");
	    
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		
	    String link = ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO)+"/"+StringUtils.lowerCase(siglaSistema)+"/link/public/extensao/confirmarCadastro/"+cadastroRealizado.getCodigoAcessoConfirmacao()+ "/"+cadastroRealizado.getId();
	    
		mensagem.append("<a href="+link+">"+link+"</a> <br/><br/>");
	
		mensagem.append("<strong>Caso não tenha solicitado nenhum cadastro, por favor, ignore este e-mail para que ele não seja realizado.</strong> <br/><br/> ");
		
		
		EnviarEmailExtensaoHelper.enviarEmail("["+siglaSistema+"] "+"Confirmação de Cadastro", "Confirmação de Cadastro para Cursos e Eventos de Extensão", cadastroRealizado.getNome(), cadastroRealizado.getEmail(), mensagem.toString(), null, null);
		
	}


	/**
	 *  <p>Realiza o operação de confirmar um cadastro para participar de um curso ou evento de extensão.</p>
	 *
	 *  <p>Esse métdo é chamdo a partir do pretty-config.xml quando o usuário acessa o endereço: 
	 *      link /link/public/extensao/confirmarCadastro/"codigo"/"id"</p>
	 *
	 *  <p>Método não chamado por nenhuma página jsp.</p>
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
			
			// aqui não tem problema em preparar e executar pois a execução em duplicidade é garantida pela busca anterior
			prepareMovimento(SigaaListaComando.CONFIRMA_CADASTRO_PARTICIPANTE_EXTENSAO);
			
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setObjMovimentado(obj);
			movimento.setCodMovimento(SigaaListaComando.CONFIRMA_CADASTRO_PARTICIPANTE_EXTENSAO);
			
			try {
				execute(movimento);
				adcionaMensagemTelaConfirmacao("Cadastro do participante "+obj.getEmail()+" confirmado com sucesso!");
				addMensagemInformation("Cadastro do participante "+obj.getEmail()+" confirmado com sucesso!");
				
			} catch (NegocioException e) {
				adcionaMensagemTelaConfirmacao("Cadastro inválido ou já foi confirmado!");
				addMensagemErro("Cadastro inválido ou já foi confirmado!");
			} 
		}else{
			adcionaMensagemTelaConfirmacao("Cadastro inválido ou já foi confirmado!");
			addMensagemErro("Cadastro inválido ou já foi confirmado!");
		}
		
		return telaConfirmaCadastroParticipanteCursosEventosExtensao();
	}
	
	
	
	
	
	
	
	
	////////////////// A parte de renviar a senha de acesso  /////////////////////
	
	
	
	/**
	 * Inicia a caso de suo que permite o usuário receber uma nova senha caso ele esqueça, nesse 
	 * caso o sistema gerará outra senha que o usuário poderá trocar depois, pois não temos acesso a 
	 * senha original cadastrada pelo usuário. 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<ul>
	 * 			<li>/sigaa.war/public/extensao/loginCurosEventosExtensao.jsp</li>
	 * 		</ul>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarReenvioSenhaParticipante() throws ArqException{
		
		prepareMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO);
		
		obj = new CadastroParticipanteAtividadeExtensao();  // os dados que o usuário vai digitar. no caso: email, data nascimento, cpf ou passaporte.
		
		return telaReenviaSenhaAcessoCadstrosCursosEventosExtensao();
	}
	
	
	
	
	
	
	/**
	 * <p>Caso um cadastro com os dados informados exista, vai ser gerado uma nova senha automática 
	 * pelo sistema e enviada ao email do usuário. A senha só vai ser alterada de fato se ele confirmar acesso a conta do 
	 * e-mail cadastrado.</p>
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			
			// aqui não tem problema em preparar e executar pois a execução em duplicidade é garantida pela busca anterior
			prepareMovimento(SigaaListaComando.GERAR_NOVA_SENHA_CADASTRO_EXTENSAO);
			
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setObjMovimentado(cadastroBuscado);
			movimento.setCodMovimento(SigaaListaComando.GERAR_NOVA_SENHA_CADASTRO_EXTENSAO);
			
			try {
				
				CadastroParticipanteAtividadeExtensao cadastroSolicitado = execute(movimento);
				
				addMensagemInformation("Solicitação de alteração de senha do participante "+cadastroSolicitado.getEmail()+" foi realizada sucesso!");
				
				enviaEmailEnderecoAlteracaoSenha(cadastroSolicitado);
				
				adcionaMensagemTelaConfirmacao(" Uma nova senha de acesso foi enviado para o e-mail:  "
					+cadastroSolicitado.getEmail() +" "+" acesse o endereço enviado no e-mail para confirmar a alteração da senha.");
				
			} catch (NegocioException e) {
				addMensagemErro("Endereço para alteração da senha inválido!");
			} 
			
		}else{
			addMensagemErro("Não existe um cadastro com as informações fornecidas.");
			return null;
		}
		
		return telaConfirmaCadastroParticipanteCursosEventosExtensao();
	}



	/**
	 * Verifica se os dados do formulário foram preenchidos
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
	 *   Envia o email com o link para o usuário acessar e confirma o cadastro.
	 *   
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 */
	private void enviaEmailEnderecoAlteracaoSenha(CadastroParticipanteAtividadeExtensao cadastroSolicitado) {
		
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("  Uma solicição de alteração de senha foi realizada para o seu cadastro no sistema. <br/><br/> ");
		mensagem.append("  Acesse o endereço abaixo para confirmar a alteração da sua senha: <br/><br/> ");
	    
		String linkAcesso = ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO)
				+"/sigaa/link/public/extensao/confirmarAlteracaoSenha/"+cadastroSolicitado.getCodigoAcessoConfirmacao()+ "/"+cadastroSolicitado.getId();
		
		mensagem.append(" <a href="+linkAcesso+">"+linkAcesso+"</a> <br/><br/>");
		
		mensagem.append("A nova senha criada foi:  <strong>"+cadastroSolicitado.getSenhaGerada()+"</strong> <br/>");
		
		mensagem.append("Após realizar o login no sistema será possível alterar a senha para uma de sua preferência. <br/> ");
		
		mensagem.append(" <br/><br/> <strong>Caso não tenha solicitado nenhuma alteração de senha, por favor ignore esse e-mail para que a senha continue a atual. </strong>");
		
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		
		mensagem.append(" <br/><br/> O "+siglaSistema+" não envia por e-emails pedidos de informações cadastrais como CPF, endereços e senhas.");
		
		EnviarEmailExtensaoHelper.enviarEmail("["+siglaSistema+"] "+"Confirmação de Alteração de Senha"
				, "Confirmação de Alteração de Senha para Cursos e Eventos de Extensão"
				, cadastroSolicitado.getNome(), cadastroSolicitado.getEmail(), mensagem.toString(), null, null);
		
	}

	
	
	
	/**
	 *  <p>Caso o usuário confirme o link enviado para o email, vai ser gerado o hash da senha gerada 
	 * e salvo no banco no banco "senha".</p>
	 * 
	 *   <p>Assim apenas se ele for o dono do email de fato vai ser alterado a senha usada para se logar no sistema. 
	 *   Garantindo o mínimo de segurança.</p>
	 *
	 *  <p>Esse método é chamado a partir do pretty-config.xml quando o usuário acessa o endereço: 
	 *      link /link/public/extensao/confirmarAlteracaoSenha/"codigo"/"id"/"senhaGerada"</p>
	 *
	 *  <p>Método não chamado por nenhuma página jsp.</p>
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
			
			// aqui não tem problema em preparar e executar pois a execução em duplicidade é garantida pela busca anterior
			prepareMovimento(SigaaListaComando.CONFIRMA_ALTERACAO_SENHA_CADASTRO_EXTENSAO);
			
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setObjMovimentado(cadastro);
			movimento.setCodMovimento(SigaaListaComando.CONFIRMA_ALTERACAO_SENHA_CADASTRO_EXTENSAO);
			
			try {
				execute(movimento);
				adcionaMensagemTelaConfirmacao("Senha do participante "+cadastro.getEmail()+" alterada com sucesso!");
				addMensagemInformation("Senha do participante "+cadastro.getEmail()+" alterada com sucesso!");
				
			} catch (NegocioException e) {
				addMensagemErro("Endereço para alteração da senha inválido!");
			} 
		}else{
			addMensagemErro("Endereço para alteração da senha inválido!");
		}
		
		return telaConfirmaCadastroParticipanteCursosEventosExtensao();
	}
	
	
	
	
	
	
	
	
	
	
	//////////////////////// A parte de alteração  do cadastro do usuário já logado  ///////////////
	
	
	/**
	 * <p>Método chamado para o cadastro de uma novo participante para cursos e eventos de extensão.</p>
	 * 
	 * <p>Só deixa cadastrar caso o usuário nunca tenha realizado nenhum cadastro na parte de cursos 
	 * e eventos, se já realizou tem que usar a conta que tem.</p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
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
	 * <p>Cadastra o nome pretendente a participante de curso de extensão no banco como não confirmado.</p> 
	 * <p>Ele tem que confirmar o email informado no próximo passo para o cadastro ser efetivado.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
			getCurrentSession().setAttribute("participanteCursosEventosExtensaoLogado", obj); // substitui os dados do usuário logado na sessão
			
			GerenciaAreaInternaCursosEventoExtensaoMBean gerencia = getMBean("gerenciaAreaInternaCursosEventoExtensaoMBean");
			
			return gerencia.forwardPaginaInternaPadrao();
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	
	
	
	
	/////// Tela de navegação ///////////////
	
	
	/** <p>Método não chamado por nenhuma página jsp.</p> */
	public String telaCadastraParticipanteCursosEventosExtensao(){
		return forward(PAGINA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO);
	}
	
	
	/** <p>Método não chamado por nenhuma página jsp.</p> 
	 *  <p> Tem que usar redirect senão o pretty faces não acha o caminho.</p>
	 */
	public String telaConfirmaCadastroParticipanteCursosEventosExtensao(){
		return redirect(PAGINA_CONFIRMA_CADASTRO_PARTICIPANTES_CURSOS_EVENTOS_EXTENSAO.replace(".jsp", ".jsf"));
	}
	
	
	/** <p>Método não chamado por nenhuma página jsp.</p> */
	public String telaReenviaSenhaAcessoCadstrosCursosEventosExtensao(){
		return forward(REENVIA_SENHA_ACESSO_CADASTROS_CURSOS_EVENTOS_EXTENSAO);
	}
	
	
	
	///////////////////////////////////////////////
	
	
	
	/**
	 * Retorna as unidades federetivas cadastradas no sistema para o combo box da página.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna os municípois das unidade federativa selecionada para o combo box da página.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Carrega os municípios de uma unidade federativa selecioando pelo usuário na página de cadastro.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
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
