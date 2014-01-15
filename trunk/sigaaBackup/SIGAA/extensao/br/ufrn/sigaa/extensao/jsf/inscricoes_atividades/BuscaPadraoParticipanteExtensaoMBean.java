/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 29/11/2012
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
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
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
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>MBean que gerencia a busca padr�o dos cadastros de participantes de extens�o no sistema.</p>
 *
 * <p>Caso o participante n�o esteja cadastrado, � permitido realizar novos cadastros, geralmente usado pelo coordenador ou gestores de extens�o.
 * Nesse caso alguns dados s�o opcionais.</p>
 *
 * <p> <i> Essa busca � usada porque daqui para frente, sempre que um novo participante for cadatrado 
 * ou inscrito o coordenador deve buscar primeiro os cadastros existentes no sistema, j� que eles 
 * n�o ser�o mais repetidos.</i> </p>
 * 
 * @author jadson
 * @see PesquisarParticipanteExtensao
 */
@Component("buscaPadraoParticipanteExtensaoMBean")
@Scope("request")
public class BuscaPadraoParticipanteExtensaoMBean  extends SigaaAbstractController<CadastroParticipanteAtividadeExtensao>{

	/** P�gia padr�o de busca padr�o dos eventos existentes.  */
	public static final String PAGINA_BUSCA_PADRAO_PARTICIPANTES_EXTENSAO = "/extensao/GerenciarInscricoes/buscaPadraoParticipantesExtensao.jsp";
	
	
	/** <p>P�gia que para realizar um novo cadastra para os participantes de extens�o, geralmente usada pelo coordenador da a��o. 
	 *  Igual a p�gina que o usu�rio realizar o seu cadastro na �rea p�blica do sistema, sendo que aqui � o coordenador que faz.</p>
	 *  
	 *  <p>Para auxiliar o cooordenador, caso o participante seja discente ou docente da institui��o, j� puxa os dado pessoais da pessoa associada.</p>
	 */
	public static final String PAGINA_CADASTRO_PARTICIPANTE_EXTENSAO_BY_GESTOR = "/extensao/GerenciarInscricoes/cadastroParticipanteCursosEventosExtensaoByGestor.jsp";
	
	
	/** Altera dados de um cadastro de extens�o, normalmente para corrigir erros de cadastros migrados, porque normalmente o 
	 * pr�prio participante pode alterar seus dados, mas se por exemplo o email estiver errado ele n�o consegue entrar no sistema para alterar.
	 */
	//public static final String PAGINA_ALTERA_CADASTRO_EXTENSAO = "/extensao/GerenciarInscricoes/paginaAlteraCadastroExtensao.jsp";
	
	
	//////////////// Campos da busca padr�o  //////////////////////
	
	/** Se o usu�rio vai busca por CPF  */
	private boolean buscarCPF;
	
	/** Se o usu�rio vai busca por passaporte  */
	private boolean buscarPassaporte;
	
	/** Se o usu�rio vai busca por nome  */
	private boolean buscarNome;
	
	/** Se o usu�rio vai busca por email  */
	private boolean buscarEmail;
	
	/** O cpf digitado pelo usu�rio no formul�rio de busca  */
	private String cpf;
	
	/** O passaporte digitado pelo usu�rio no formul�rio de busca  */
	private String passaporte;
	
	/** O nome digitado pelo usu�rio no formul�rio de busca  */
	private String nome;
	
	/** O email digitado pelo usu�rio no formul�rio de busca  */
	private String email;
	
	////////////////////////////////////////////////////////////
	
	
	/** Atributo utilizado para armazenar as unidades federativas */
	private List<UnidadeFederativa> unidadeFederativas;
	
	/** Atributo utilizado para armazenar os munic�pios */
	private List<Municipio> municipios= new ArrayList<Municipio>();
	
	
	/** Usado no cadastra para confirmar se o email foi digitado corretamente. */
	private String emailConfirmacao;
	
	
	/** O Mbean que chamou a busca padr�o de participantes.  
	 * Tem que implementar essa interface para poder usar esse caso de uso.
	 */
	private PesquisarParticipanteExtensao mBeanChamador;
	
	
	/** O t�tulo da opera��o que est� chamando a busca de participantes */
	private String operacao;
	
	/** A descri��o da opera��o que est� chamando a busca de participantes */
	private String descricaoOperacao;
	
	/** Cadastros encontrados pela busca */
	private List<CadastroParticipanteAtividadeExtensao> cadastros;
	
	/** O id da pessoa buscada pelo coordenador. Usado para preencher os dados do particiapante 
	 * quando o participante � um usu�rio interno do sistema. E o coordenador n�o ter que preencher tudo do zero.*/
	private int idpessoaBusca = -1;
	
	/** O nome da pessoa no sistema para o coordenador buscar e preencher os dados do participante que est� sendo  cadatrado.*/
	private String nomePessoaBusca;
	
	
	
	/** Se a p�gina de busca padr�o deve exibir uma op��o que permite alterar o cadastro dos participantes encontrados. */
	private boolean permitirAlterarCadastro = false;
	
	
	/** Usado na altera��o de um cadastro de um participante, se o sistema vai permitir o usu�rio alterar a senha do participante.
	 *  Normalmente apenas os gestores de extens�o podem redenir a senha, para casos extremos onde o usu�rio n�o consegue acessar o sistema.
	 */
	private boolean permiteRedefinirSenha = false;
	
	
	/** 
	 * Indica se o caso de uso est� sendo usando para cadastro ou altera��o de cadastro. Aparecer os campos corretos na tela.
	 */
	private boolean isAlterando = false;
	
	/**
	 *  Inicia o caso de uso de busca eventos, chamado de outros Mbeans de outros casos de uso que utilizam a busca padr�o.
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String iniciaBuscaSelecaoParticipanteExtensao(PesquisarParticipanteExtensao mBeanChamador, String operacao, String descricaoOperacao, boolean permitirAlterarCadastro, boolean permiteRedefinirSenha){
		this.mBeanChamador = mBeanChamador;
		this.operacao = operacao;
		this.descricaoOperacao = descricaoOperacao;
		this.permitirAlterarCadastro = permitirAlterarCadastro;
		
		if(permitirAlterarCadastro) // s� faz sentido se o usu�rio puder alterar o cadastro
			this.permiteRedefinirSenha = permiteRedefinirSenha;
		
		return telaBuscaPadraoParticipantesExtensao();
	}

	
	/**
	 *  Realiza a busca nos cadastros de participante existentes no sistema.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @param evt
	 * @throws DAOException 
	 */
	public void buscarParticipante(ActionEvent evt) throws DAOException{
		
		CadastroParticipanteAtividadeExtensaoDao dao = null;
		
		try{
			verificaCamposBusca();
			dao = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
			cadastros = dao.buscaPadraoParticipante(cpf, passaporte, nome, email);
			
			if(cadastros.size() == 0 ){
				addMensagemErro("Nenhum cadastro foi encontrado com essas informa��es.");
			}
			
			if(cadastros.size() > 300 ){
				addMensagem(MensagensArquitetura.BUSCA_MAXIMO_RESULTADOS, 300);
			}
			
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return;
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	
	
	/**
	 * <p>Chamado quando o usu�rio seleciona um participante da busca padr�o.</p>
	 * 
	 * <p>Continua o fluxo para o caso de uso que chamou a busca, passando s� o participante com o id, o caso 
	 * de uso chamador deve popular os dados necess�rios.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/buscaPadraoParticipantesExtensao.jsp</li>
	 *   </ul>
	 *
	 * @param evt
	 * @throws ArqException 
	 */
	public String selecionouParticipante() throws ArqException{
		return selecionouParticipante(getParameterInt("idParticipanteSelecionado"));
	}
	
	/**
	 * Chama o MBean do caso de uso para realizar o opera��o de quando o usu�rio seleciona o cadastro do  participante.
	 *
	 * @return
	 */
	public String selecionouParticipante(int idParticipanteSelecionado) throws ArqException{
		mBeanChamador.setParticipanteExtensao(new CadastroParticipanteAtividadeExtensao(idParticipanteSelecionado)); 
		return mBeanChamador.selecionouParticipanteExtensao();
	}
	
	/**
	 * Chama o MBean do caso de uso para realizar o opera��o de cancelar da tela de busca.
	 *
	 * @return
	 */
	public String cancelarPesquiasParticipanteExtensao(){
		return mBeanChamador.cancelarPesquiasParticipanteExtensao();
	}
	
	
	/**
	 * Verifica se os campos de busca foram preenchidos
	 * @throws NegocioException 
	 *
	 */
	private void verificaCamposBusca() throws NegocioException {
		if(! buscarCPF ) cpf = null; 
		if(! buscarPassaporte ) passaporte = null;
		if(! buscarNome ) nome = null;
		if(! buscarEmail ) email = null;
		
		if( StringUtils.isEmpty(cpf)  
				&& StringUtils.isEmpty(passaporte) 
				&& StringUtils.isEmpty(nome) 
				&& StringUtils.isEmpty(email)     ){
			throw new NegocioException("Selecione pelo menos um dos filtros para a busca: cpf, passaporte, nome ou email.");
		}
	}
	
	
	
	
	
	///////////////////////// A parte de Cadastro de Novos Participantes ///////////////////////////////////////////
	
	
	/**
	 * Vai para a tela para o coordenador cadastrar uma novo participante para poder inclu�-lo em uma atividade de extens�o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>/sigaa.war/extensao/GerenciarInscricoes/buscaPadraoParticipantesExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String preCadastrarParticipante() throws ArqException{
		
		prepareMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO_PELOS_GESTORES);
		
		idpessoaBusca = -1;
		nomePessoaBusca = "";
		emailConfirmacao = null;
		
		obj = new CadastroParticipanteAtividadeExtensao();
		obj.setSenha("123456"); // s� para n�o ficar nula e n�o gerar erros de valida��o, vai ser gerada uma pelo sistema antes de salvar....
		obj.setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		obj.setMunicipio(new Municipio(-1));
		carregarMunicipios(null);
		
		isAlterando = false;
		
		return telaCadastroParticipanteExtensaoByGestor();
	}
	
	
	
	
	/**
	 * Realizar o cadatro do participante pelo coordenador
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>/sigaa.war/extensao/GerenciarInscricoes/paginaRealizaNovoCadastroExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarNovoParticipante() throws ArqException{
		
		
		MovimentoCadastroParticipanteExtensao mov = new MovimentoCadastroParticipanteExtensao(obj, emailConfirmacao, null);
		mov.setCodMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO_PELOS_GESTORES);
		
		try{
			CadastroParticipanteAtividadeExtensao cadastroRealizado  = execute(mov); // retorna o objeito persitido
			
			addMensagemInformation("Cadastro Realizado com sucesso!");
			
			enviaEmailEnderecoAvisoCadastro(cadastroRealizado);
			
			return selecionouParticipante(obj.getId());
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	
	
	/**
	 *  Envia um email com a senha para o usu�rio poder acesar o sisteam quando o coordenador realizar o cadastro.
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @param cadastroRealizado
	 */
	private void enviaEmailEnderecoAvisoCadastro( CadastroParticipanteAtividadeExtensao cadastroRealizado) {
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		   
		String enderecoTelaLoginAreaInternaExtensao = RepositorioDadosInstitucionais.get("linkSigaa")+"/link/public/extensao/acessarAreaInscrito";
		   
		   
		String mensagem = " <p>Voc� foi cadastrado no sistema para participar de atividades de extens�o. </p>"+
				             " <br/>"+
				             " <p> Dados de Acesso: "+
				             " <p> E-mail : "+cadastroRealizado.getEmail()+"</p> "+
				             " <p> Senha : "+cadastroRealizado.getSenhaGerada()+"</p> "+
				             " <br/>"+
				             " <p>Para gerenciar suas informa��es acesse o sistema pelo endere�o abaixo informando seu e-mail e senha de acesso: "+
				             " <br/>"+ 
				             "<a href=\""+enderecoTelaLoginAreaInternaExtensao+"\">"+enderecoTelaLoginAreaInternaExtensao+"</a>" +
				              "</p>"+
				              " <p> <strong> O "+siglaSistema+" n�o envia e-mails solicitando senhas ou dados pessoais.</strong> </p>";
		   
	   // Envia para todos os participantes, com c�pia para o coordenador
	   EnviarEmailExtensaoHelper.enviarEmail(
			   "["+siglaSistema+"]"+" Notifica��o de Cadastro para Participa��o nas A��es de Extens�o ", 
			   " Notifica��o de Cadastro para Participa��o nas A��es de Extens�o "
			   , cadastroRealizado.getNome(), cadastroRealizado.getEmail(), mensagem, null, null);
	   
	   addMensagemWarning("Foi enviado um e-mail de aviso para o participante no endere�o "+cadastroRealizado.getEmail()+"");
		
	}


	/////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	

	///////////////////////// A parte de Altera��o de Cadastros Existentes ///////////////////////////////////////////
	
	
	/**
	 * Vai para a tela para o usu�rio alterar os dados de cadastro de um participante.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>/sigaa.war/extensao/GerenciarInscricoes/buscaPadraoParticipantesExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String preAlterarCadastroParticipante() throws ArqException{
		
		prepareMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO_PELOS_GESTORES);
		
		idpessoaBusca = -1;
		nomePessoaBusca = "";
		emailConfirmacao = null;
		
		CadastroParticipanteAtividadeExtensaoDao dao = null;
		
		try{
			dao = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
			// Buscas todos os dados cadastrados para alterar
			obj = dao.findByPrimaryKey(getParameterInt("idCadastroParticipanteSelecionado"), CadastroParticipanteAtividadeExtensao.class);
			
			carregarMunicipios(null);
		
		}finally{
			if(dao != null) dao.close();
		}
		
		isAlterando = true;
		
		return telaCadastroParticipanteExtensaoByGestor();
	}
	
	
	
	
	/**
	 * Realizar a altera��o do participante pelo coordenador
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>/sigaa.war/extensao/GerenciarInscricoes/paginaAlteraCadastroExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String alterarCadastroParticipante() throws ArqException{
		
		
		MovimentoCadastroParticipanteExtensao mov = new MovimentoCadastroParticipanteExtensao(obj, permiteRedefinirSenha);
		mov.setCodMovimento(SigaaListaComando.CADASTRO_PARTICIPANTE_EXTENSAO_PELOS_GESTORES);
		
		String senha = obj.getSenha(); // guarda a senha pura digitada antes que seja gerado o hash
		
		try{
			CadastroParticipanteAtividadeExtensao cadastroAlterado  = execute(mov); // retorna o objeito persitido
			
			addMensagemInformation("Altera��o dos dados do participante realizado com sucesso!");
			
			enviaEmailEnderecoAvisoAlteracaoDados(cadastroAlterado, senha);
			
			buscarParticipante(null); // atualiza a listagem
			
			return telaBuscaPadraoParticipantesExtensao();
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	
	
	/**
	 *  Envia um email com a senha para o usu�rio poder acesar o sisteam quando o coordenador realizar o cadastro.
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @param cadastroRealizado
	 */
	private void enviaEmailEnderecoAvisoAlteracaoDados( CadastroParticipanteAtividadeExtensao cadastroAlterado, String novaSenha) {
		
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		
		String enderecoTelaLoginAreaInternaExtensao = RepositorioDadosInstitucionais.get("linkSigaa")+"/link/public/extensao/acessarAreaInscrito";
		
		String mensagem = " <p>Os seus dados de cadastro no sistema para participar de atividades de extens�o foram alterados por: "+getUsuarioLogado().getNome()+"</p>"+
				             " <br/>"+
				             " <p>Para gerenciar suas informa��es acesse o sistema pelo endere�o abaixo informando seu e-mail e senha de acesso: "+
				             " <br/>"+ 
				             "<a href=\""+enderecoTelaLoginAreaInternaExtensao+"\">"+enderecoTelaLoginAreaInternaExtensao+"</a>" +
				              "</p>"+
				              " <p> E-mail de acesso : "+cadastroAlterado.getEmail()+"</p> "+
				              ( StringUtils.notEmpty(novaSenha) ? " <p> Nova senha de acesso : "+novaSenha+"</p> " : " ")+ 
				              " <p> <strong> O "+siglaSistema+" n�o envia e-mails solicitando senhas ou dados pessoais.</strong> </p>";
		   
	   // Envia para todos os participantes, com c�pia para o coordenador
	   EnviarEmailExtensaoHelper.enviarEmail(
			   "["+siglaSistema+"]"+" Notifica��o de Altera��o dos Dados de Cadastro ", 
			   " Notifica��o de Altera��o dos Dados de Cadastro para Participa��o nas A��es de Extens�o "
			   , cadastroAlterado.getNome(), cadastroAlterado.getEmail(), mensagem, null, null);
	   
	   addMensagemWarning("Foi enviado um e-mail de aviso de altera��o dos dados cadastrais para o participante no endere�o: "+cadastroAlterado.getEmail()+"");
		
	}


	/////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
				if(ufComCapital != null && ufComCapital.getCapital() != null){
					obj.setMunicipio( new Municipio() );
					obj.getMunicipio().setId(ufComCapital.getCapital().getId());
				}
					
				municipios.addAll( dao.findByUF(ufSelecionada.getId(), new String[]{"id", "nome"}) );
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	
	/**
	 * M�todo que responde �s requisi��es de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de pessoas.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/cadastroParticipanteCursosEventosExtensao.jsp</li>
	 * </ul>
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<Pessoa> autocompleteNomePessoa(Object event) throws DAOException {
		String nome = event.toString(); //Nome do item digitado no autocomplete

		List<Pessoa> lista = new ArrayList<Pessoa>();
	
		CadastroParticipanteAtividadeExtensaoDao genDAO = null;
		
		try{
			genDAO = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
			lista = genDAO.findPessoaInternaByNome(nome);
		}finally{
			if(genDAO != null) genDAO.close();
		}

		return lista;
	}
	
	/**
	 * Preenche os dados do participante com as informa��es da pessoa selecionada pelo coordenador. 
	 * Isso para ajudar no cadastro daquelas usu�rios que j� tem dados pessoais na nossa base interna.
	 *  
	 *   M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/public/extensao/cadastroParticipanteCursosEventosExtensao.jsp</li>
	 * </ul>
	 *
	 *
	 * @param event
	 * @throws DAOException
	 */
	public void preencheCadastroInformacoesPessoaSelecionada(ActionEvent event) throws DAOException{
	
		CadastroParticipanteAtividadeExtensaoDao genDAO = null;
		
		
		if(idpessoaBusca > 0){
			try{
				genDAO = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
				Pessoa pessoa = genDAO.findInformacoesPessoaInternaById(idpessoaBusca);
				
				if(pessoa != null){
					obj.setNome(pessoa.getNome());
					obj.setCpf(pessoa.getCpf_cnpj());
					obj.setPassaporte(pessoa.getPassaporte());
					obj.setEstrangeiro( pessoa.isInternacional() );
					obj.setDataNascimento(pessoa.getDataNascimento());
					obj.setEmail(pessoa.getEmail());
					emailConfirmacao = pessoa.getEmail();
					
					if(pessoa.getEnderecoContato() != null){
						obj.setLogradouro(pessoa.getEnderecoContato().getLogradouro());
						obj.setNumero(pessoa.getEnderecoContato().getNumero());
						obj.setComplemento(pessoa.getEnderecoContato().getComplemento());
						obj.setBairro (pessoa.getEnderecoContato().getBairro());
						obj.setCep(pessoa.getEnderecoContato().getCep());
	
						if(pessoa.getEnderecoContato().getMunicipio() != null){
							obj.setMunicipio( pessoa.getEnderecoContato().getMunicipio());
						}
						
						if(pessoa.getEnderecoContato().getUnidadeFederativa() != null){
							obj.setUnidadeFederativa( pessoa.getEnderecoContato().getUnidadeFederativa());
						}
					}
				}
			
				idpessoaBusca = -1;
			}finally{
				if(genDAO != null) genDAO.close();
			}
		}
		
	}
	
	
	/**
	 *  Tela de navega��o
	 *  
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaBuscaPadraoParticipantesExtensao() {
		return forward(PAGINA_BUSCA_PADRAO_PARTICIPANTES_EXTENSAO);
	}

	/**
	 *  Tela de navega��o
	 *  
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaCadastroParticipanteExtensaoByGestor() {
		return forward(PAGINA_CADASTRO_PARTICIPANTE_EXTENSAO_BY_GESTOR);
	}
	
	
	
//	/**
//	 *  Tela de navega��o
//	 *  
//	 *
//	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
//	 *
//	 * @return
//	 */
//	public String telaAlteraCadastroExtensao() {
//		return forward(PAGINA_ALTERA_CADASTRO_EXTENSAO);
//	}
	
	
	/**
	 * Retorna a quantidade de cadastro encontrados na busca.
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public int getQtdCadastros() {
		if(cadastros == null) 
			return 0;
		else 
			return cadastros.size();
	}

	

	/////   sets e gets /////

	public PesquisarParticipanteExtensao getmBeanChamador() {
		return mBeanChamador;
	}
	public void setmBeanChamador(PesquisarParticipanteExtensao mBeanChamador) {
		this.mBeanChamador = mBeanChamador;
	}
	public String getOperacao() {
		return operacao;
	}
	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}
	public String getDescricaoOperacao() {
		return descricaoOperacao;
	}
	public void setDescricaoOperacao(String descricaoOperacao) {
		this.descricaoOperacao = descricaoOperacao;
	}
	public boolean isBuscarCPF() {
		return buscarCPF;
	}
	public void setBuscarCPF(boolean buscarCPF) {
		this.buscarCPF = buscarCPF;
	}
	public boolean isBuscarPassaporte() {
		return buscarPassaporte;
	}
	public void setBuscarPassaporte(boolean buscarPassaporte) {
		this.buscarPassaporte = buscarPassaporte;
	}
	public boolean isBuscarNome() {
		return buscarNome;
	}
	public void setBuscarNome(boolean buscarNome) {
		this.buscarNome = buscarNome;
	}
	public boolean isBuscarEmail() {
		return buscarEmail;
	}
	public void setBuscarEmail(boolean buscarEmail) {
		this.buscarEmail = buscarEmail;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getPassaporte() {
		return passaporte;
	}
	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<CadastroParticipanteAtividadeExtensao> getCadastros() {
		return cadastros;
	}
	public void setCadastros(List<CadastroParticipanteAtividadeExtensao> cadastros) {
		this.cadastros = cadastros;
	}
	public String getEmailConfirmacao() {
		return emailConfirmacao;
	}
	public void setEmailConfirmacao(String emailConfirmacao) {
		this.emailConfirmacao = emailConfirmacao;
	}
	public int getIdpessoaBusca() {
		return idpessoaBusca;
	}
	public void setIdpessoaBusca(int idpessoaBusca) {
		this.idpessoaBusca = idpessoaBusca;
	}
	public String getNomePessoaBusca() {
		return nomePessoaBusca;
	}
	public void setNomePessoaBusca(String nomePessoaBusca) {
		this.nomePessoaBusca = nomePessoaBusca;
	}

	public boolean isPermitirAlterarCadastro() {
		return permitirAlterarCadastro;
	}

	public void setPermitirAlterarCadastro(boolean permitirAlterarCadastro) {
		this.permitirAlterarCadastro = permitirAlterarCadastro;
	}

	public boolean isPermiteRedefinirSenha() {
		return permiteRedefinirSenha;
	}

	public void setPermiteRedefinirSenha(boolean permiteRedefinirSenha) {
		this.permiteRedefinirSenha = permiteRedefinirSenha;
	}

	public boolean isAlterando() {
		return isAlterando;
	}

	public void setAlterando(boolean isAlterando) {
		this.isAlterando = isAlterando;
	}
}
