/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean que gerencia a busca padrão dos cadastros de participantes de extensão no sistema.</p>
 *
 * <p>Caso o participante não esteja cadastrado, é permitido realizar novos cadastros, geralmente usado pelo coordenador ou gestores de extensão.
 * Nesse caso alguns dados são opcionais.</p>
 *
 * <p> <i> Essa busca é usada porque daqui para frente, sempre que um novo participante for cadatrado 
 * ou inscrito o coordenador deve buscar primeiro os cadastros existentes no sistema, já que eles 
 * não serão mais repetidos.</i> </p>
 * 
 * @author jadson
 * @see PesquisarParticipanteExtensao
 */
@Component("buscaPadraoParticipanteExtensaoMBean")
@Scope("request")
public class BuscaPadraoParticipanteExtensaoMBean  extends SigaaAbstractController<CadastroParticipanteAtividadeExtensao>{

	/** Págia padrão de busca padrão dos eventos existentes.  */
	public static final String PAGINA_BUSCA_PADRAO_PARTICIPANTES_EXTENSAO = "/extensao/GerenciarInscricoes/buscaPadraoParticipantesExtensao.jsp";
	
	
	/** <p>Págia que para realizar um novo cadastra para os participantes de extensão, geralmente usada pelo coordenador da ação. 
	 *  Igual a página que o usuário realizar o seu cadastro na área pública do sistema, sendo que aqui é o coordenador que faz.</p>
	 *  
	 *  <p>Para auxiliar o cooordenador, caso o participante seja discente ou docente da instituição, já puxa os dado pessoais da pessoa associada.</p>
	 */
	public static final String PAGINA_CADASTRO_PARTICIPANTE_EXTENSAO_BY_GESTOR = "/extensao/GerenciarInscricoes/cadastroParticipanteCursosEventosExtensaoByGestor.jsp";
	
	
	/** Altera dados de um cadastro de extensão, normalmente para corrigir erros de cadastros migrados, porque normalmente o 
	 * próprio participante pode alterar seus dados, mas se por exemplo o email estiver errado ele não consegue entrar no sistema para alterar.
	 */
	//public static final String PAGINA_ALTERA_CADASTRO_EXTENSAO = "/extensao/GerenciarInscricoes/paginaAlteraCadastroExtensao.jsp";
	
	
	//////////////// Campos da busca padrão  //////////////////////
	
	/** Se o usuário vai busca por CPF  */
	private boolean buscarCPF;
	
	/** Se o usuário vai busca por passaporte  */
	private boolean buscarPassaporte;
	
	/** Se o usuário vai busca por nome  */
	private boolean buscarNome;
	
	/** Se o usuário vai busca por email  */
	private boolean buscarEmail;
	
	/** O cpf digitado pelo usuário no formulário de busca  */
	private String cpf;
	
	/** O passaporte digitado pelo usuário no formulário de busca  */
	private String passaporte;
	
	/** O nome digitado pelo usuário no formulário de busca  */
	private String nome;
	
	/** O email digitado pelo usuário no formulário de busca  */
	private String email;
	
	////////////////////////////////////////////////////////////
	
	
	/** Atributo utilizado para armazenar as unidades federativas */
	private List<UnidadeFederativa> unidadeFederativas;
	
	/** Atributo utilizado para armazenar os municípios */
	private List<Municipio> municipios= new ArrayList<Municipio>();
	
	
	/** Usado no cadastra para confirmar se o email foi digitado corretamente. */
	private String emailConfirmacao;
	
	
	/** O Mbean que chamou a busca padrão de participantes.  
	 * Tem que implementar essa interface para poder usar esse caso de uso.
	 */
	private PesquisarParticipanteExtensao mBeanChamador;
	
	
	/** O título da operação que está chamando a busca de participantes */
	private String operacao;
	
	/** A descrição da operação que está chamando a busca de participantes */
	private String descricaoOperacao;
	
	/** Cadastros encontrados pela busca */
	private List<CadastroParticipanteAtividadeExtensao> cadastros;
	
	/** O id da pessoa buscada pelo coordenador. Usado para preencher os dados do particiapante 
	 * quando o participante é um usuário interno do sistema. E o coordenador não ter que preencher tudo do zero.*/
	private int idpessoaBusca = -1;
	
	/** O nome da pessoa no sistema para o coordenador buscar e preencher os dados do participante que está sendo  cadatrado.*/
	private String nomePessoaBusca;
	
	
	
	/** Se a página de busca padrão deve exibir uma opção que permite alterar o cadastro dos participantes encontrados. */
	private boolean permitirAlterarCadastro = false;
	
	
	/** Usado na alteração de um cadastro de um participante, se o sistema vai permitir o usuário alterar a senha do participante.
	 *  Normalmente apenas os gestores de extensão podem redenir a senha, para casos extremos onde o usuário não consegue acessar o sistema.
	 */
	private boolean permiteRedefinirSenha = false;
	
	
	/** 
	 * Indica se o caso de uso está sendo usando para cadastro ou alteração de cadastro. Aparecer os campos corretos na tela.
	 */
	private boolean isAlterando = false;
	
	/**
	 *  Inicia o caso de uso de busca eventos, chamado de outros Mbeans de outros casos de uso que utilizam a busca padrão.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String iniciaBuscaSelecaoParticipanteExtensao(PesquisarParticipanteExtensao mBeanChamador, String operacao, String descricaoOperacao, boolean permitirAlterarCadastro, boolean permiteRedefinirSenha){
		this.mBeanChamador = mBeanChamador;
		this.operacao = operacao;
		this.descricaoOperacao = descricaoOperacao;
		this.permitirAlterarCadastro = permitirAlterarCadastro;
		
		if(permitirAlterarCadastro) // só faz sentido se o usuário puder alterar o cadastro
			this.permiteRedefinirSenha = permiteRedefinirSenha;
		
		return telaBuscaPadraoParticipantesExtensao();
	}

	
	/**
	 *  Realiza a busca nos cadastros de participante existentes no sistema.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
				addMensagemErro("Nenhum cadastro foi encontrado com essas informações.");
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
	 * <p>Chamado quando o usuário seleciona um participante da busca padrão.</p>
	 * 
	 * <p>Continua o fluxo para o caso de uso que chamou a busca, passando só o participante com o id, o caso 
	 * de uso chamador deve popular os dados necessários.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Chama o MBean do caso de uso para realizar o operação de quando o usuário seleciona o cadastro do  participante.
	 *
	 * @return
	 */
	public String selecionouParticipante(int idParticipanteSelecionado) throws ArqException{
		mBeanChamador.setParticipanteExtensao(new CadastroParticipanteAtividadeExtensao(idParticipanteSelecionado)); 
		return mBeanChamador.selecionouParticipanteExtensao();
	}
	
	/**
	 * Chama o MBean do caso de uso para realizar o operação de cancelar da tela de busca.
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
	 * Vai para a tela para o coordenador cadastrar uma novo participante para poder incluí-lo em uma atividade de extensão.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		obj.setSenha("123456"); // só para não ficar nula e não gerar erros de validação, vai ser gerada uma pelo sistema antes de salvar....
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
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *  Envia um email com a senha para o usuário poder acesar o sisteam quando o coordenador realizar o cadastro.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param cadastroRealizado
	 */
	private void enviaEmailEnderecoAvisoCadastro( CadastroParticipanteAtividadeExtensao cadastroRealizado) {
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		   
		String enderecoTelaLoginAreaInternaExtensao = RepositorioDadosInstitucionais.get("linkSigaa")+"/link/public/extensao/acessarAreaInscrito";
		   
		   
		String mensagem = " <p>Você foi cadastrado no sistema para participar de atividades de extensão. </p>"+
				             " <br/>"+
				             " <p> Dados de Acesso: "+
				             " <p> E-mail : "+cadastroRealizado.getEmail()+"</p> "+
				             " <p> Senha : "+cadastroRealizado.getSenhaGerada()+"</p> "+
				             " <br/>"+
				             " <p>Para gerenciar suas informações acesse o sistema pelo endereço abaixo informando seu e-mail e senha de acesso: "+
				             " <br/>"+ 
				             "<a href=\""+enderecoTelaLoginAreaInternaExtensao+"\">"+enderecoTelaLoginAreaInternaExtensao+"</a>" +
				              "</p>"+
				              " <p> <strong> O "+siglaSistema+" não envia e-mails solicitando senhas ou dados pessoais.</strong> </p>";
		   
	   // Envia para todos os participantes, com cópia para o coordenador
	   EnviarEmailExtensaoHelper.enviarEmail(
			   "["+siglaSistema+"]"+" Notificação de Cadastro para Participação nas Ações de Extensão ", 
			   " Notificação de Cadastro para Participação nas Ações de Extensão "
			   , cadastroRealizado.getNome(), cadastroRealizado.getEmail(), mensagem, null, null);
	   
	   addMensagemWarning("Foi enviado um e-mail de aviso para o participante no endereço "+cadastroRealizado.getEmail()+"");
		
	}


	/////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	

	///////////////////////// A parte de Alteração de Cadastros Existentes ///////////////////////////////////////////
	
	
	/**
	 * Vai para a tela para o usuário alterar os dados de cadastro de um participante.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Realizar a alteração do participante pelo coordenador
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			
			addMensagemInformation("Alteração dos dados do participante realizado com sucesso!");
			
			enviaEmailEnderecoAvisoAlteracaoDados(cadastroAlterado, senha);
			
			buscarParticipante(null); // atualiza a listagem
			
			return telaBuscaPadraoParticipantesExtensao();
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	
	
	/**
	 *  Envia um email com a senha para o usuário poder acesar o sisteam quando o coordenador realizar o cadastro.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param cadastroRealizado
	 */
	private void enviaEmailEnderecoAvisoAlteracaoDados( CadastroParticipanteAtividadeExtensao cadastroAlterado, String novaSenha) {
		
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		
		String enderecoTelaLoginAreaInternaExtensao = RepositorioDadosInstitucionais.get("linkSigaa")+"/link/public/extensao/acessarAreaInscrito";
		
		String mensagem = " <p>Os seus dados de cadastro no sistema para participar de atividades de extensão foram alterados por: "+getUsuarioLogado().getNome()+"</p>"+
				             " <br/>"+
				             " <p>Para gerenciar suas informações acesse o sistema pelo endereço abaixo informando seu e-mail e senha de acesso: "+
				             " <br/>"+ 
				             "<a href=\""+enderecoTelaLoginAreaInternaExtensao+"\">"+enderecoTelaLoginAreaInternaExtensao+"</a>" +
				              "</p>"+
				              " <p> E-mail de acesso : "+cadastroAlterado.getEmail()+"</p> "+
				              ( StringUtils.notEmpty(novaSenha) ? " <p> Nova senha de acesso : "+novaSenha+"</p> " : " ")+ 
				              " <p> <strong> O "+siglaSistema+" não envia e-mails solicitando senhas ou dados pessoais.</strong> </p>";
		   
	   // Envia para todos os participantes, com cópia para o coordenador
	   EnviarEmailExtensaoHelper.enviarEmail(
			   "["+siglaSistema+"]"+" Notificação de Alteração dos Dados de Cadastro ", 
			   " Notificação de Alteração dos Dados de Cadastro para Participação nas Ações de Extensão "
			   , cadastroAlterado.getNome(), cadastroAlterado.getEmail(), mensagem, null, null);
	   
	   addMensagemWarning("Foi enviado um e-mail de aviso de alteração dos dados cadastrais para o participante no endereço: "+cadastroAlterado.getEmail()+"");
		
	}


	/////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de pessoas.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s): 
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
	 * Preenche os dados do participante com as informações da pessoa selecionada pelo coordenador. 
	 * Isso para ajudar no cadastro daquelas usuários que já tem dados pessoais na nossa base interna.
	 *  
	 *   Método chamado pela(s) seguinte(s) JSP(s): 
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
	 *  Tela de navegação
	 *  
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaBuscaPadraoParticipantesExtensao() {
		return forward(PAGINA_BUSCA_PADRAO_PARTICIPANTES_EXTENSAO);
	}

	/**
	 *  Tela de navegação
	 *  
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaCadastroParticipanteExtensaoByGestor() {
		return forward(PAGINA_CADASTRO_PARTICIPANTE_EXTENSAO_BY_GESTOR);
	}
	
	
	
//	/**
//	 *  Tela de navegação
//	 *  
//	 *
//	 *   <p>Método não chamado por nenhuma página jsp.</p>
//	 *
//	 * @return
//	 */
//	public String telaAlteraCadastroExtensao() {
//		return forward(PAGINA_ALTERA_CADASTRO_EXTENSAO);
//	}
	
	
	/**
	 * Retorna a quantidade de cadastro encontrados na busca.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
