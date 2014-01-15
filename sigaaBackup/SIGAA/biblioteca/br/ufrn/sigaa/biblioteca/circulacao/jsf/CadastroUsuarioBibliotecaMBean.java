/*
 * CadastroUsuarioBibliotecaMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InfoVinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TermoAdesaoSistemaBibliotecas;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoSenhaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>MBean que gerencia a p�gina de cadastro de usu�rios das bibliotecas.</p>
 * <p>Cadastrar um usu�rio para utilizar a biblioteca consiste em apenas criar uma senha para ele.</p>
 *
 *  <p>Este MBean tamb�m gerencia a parte onde um usu�rio com permiss�o de circula��o pode alterar 
 *  a senha dos "usu�rios biblioteca" do sistema. Qualquer outro usu�rio s� pode alterar a sua pr�pria senha.</p>
 *
 * @author Jadson
 * @since 06/10/2008
 * @version 1.0 cria��o da classe
 * @version 2.0 12/04/2011 altera��o no cadastro para guardar as informa��es do v�nculo do usu�rio no pr�prio usu�rio biblioteca.
 * @version 2.1 17/05/2012 Alterando o caso de uso para o usu�rio poder alterar entre os v�nculos ativo n�o usados. Se ele tiver mais 
 * de 1 vai poder alterar entre eles e escolher qual usar. Para n�o ter que quitar o v�nculo usado, cadastrar uma conta para o outro 
 * e n�o poder usar o v�nculo que ainda est� ativo.
 */
@Component("cadastroUsuarioBibliotecaMBean") 
@Scope("request")
public class CadastroUsuarioBibliotecaMBean  extends SigaaAbstractController<UsuarioBiblioteca> implements PesquisarUsuarioBiblioteca{

	
	/** Formul�rio de cadastro;altera��o de senha de empr�stimo */
	public static final String PAGINA_FORM_SENHA_USUARIO = "/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp";
	
	/** Formul�rio de cadastro;altera��o de senha de empr�stimo */
	public static final String PAGINA_TERMO_RESPONSABILIDADE = "/biblioteca/circulacao/termoResponsabilidadeAdesaoBibliotecas.jsp";
	
	/** Indica se � um operador criando a senha de um usu�rio. */
	private boolean operador;
	
	/** Indica se o usu�rio concorda com o termo de ades�o. */
	private boolean concordoCheck = false;
	
	/** Texto do termo de ades�o a ser carregado na p�gina. */
	private String textTermoAdesao = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.TEXTO_DE_ADESAO_AO_SISTEMA_DE_BIBLIOTECAS); 
	
	
	/** O termo de ades�o que ser� assinatado pelo usu�rio em caso de novo cadastro e caso essa funcionalidade esteja ativa no sistema.*/
	private TermoAdesaoSistemaBibliotecas termoAdesaoBibliotecas = new TermoAdesaoSistemaBibliotecas();


	/** 
	 * Guarda a pessoa que se deseja altera o cadastro na biblioteca.
	 */
	private Pessoa pessoaRetornadaBusca;
	/** apenas guarda os dados da pessoa para ser mostrado na tela ao operador que est� alterando. */
	private String cpfPessoa;  
	/** apenas guarda os dados da pessoa para ser mostrado na tela ao operador que est� alterando. */
	private String passaportePessoa;
	/** apenas  guarda os dados da pessoa para ser mostrado na tela  ao operador que est� alterando.*/
	private String nomePessoa;
	/**  apenas  guarda os dados da pessoa para ser mostrado na tela  ao operador que est� alterando. */
	private String dataNascimento;
	
	
	
	/** Guarda o UsuarioBibiloteca atualmente utilizado por quem est� cadastrando ou alterando a conta.*/
	private UsuarioBiblioteca usuarioBibliotecaAtual = new UsuarioBiblioteca();
	
	/** Todas as contas que o usu�rio j� tive na biblioteca at� as quitadas. */
	private List<UsuarioBiblioteca> contasUsuarioBiblioteca;
	
	/**
	 * <p>O v�nculo preferencial a ser usado � escolhido dentre os ativos ainda n�o utilizado.</p>
	 * 
	 * <p>Caso o usu�rio j� tenha conta, a conta n�o quitada vai ser atualizada para esse v�nculo.</p>
	 * 
	 * <p>Sempre vai conter um v�nculo ativo, se todos expiraram o usu�rio n�o vai conseguir realizar o cadastro. 
	 * Sempre que � realizado um novo empr�stimo, o sistema verifica se o v�nculo atual expirou, se sim o usu�rio vai ter que se cadastrar para obter um novo.</p>
	 */
	private InfoVinculoUsuarioBiblioteca vinculoEscolhido;

	
	/**
	 * <p>O v�nculo que est� sendo atualmente usado pelo usu�rio. O usu�rio vai poder alterar entre seus v�nculos ativos n�o usados.</p>
	 */
	private InfoVinculoUsuarioBiblioteca vinculoAtual;
	
	
	/** Guarda a informa��o do v�nculo selecionado pelo usu�rio dentre os vinculos ativos ainda n�o usado do usu�rio */
	private String identificacaoVinculoSelecionado;
	
	
	/** Guarda os vinculos ativos n�o usados para o usu�rio escolher qual desses vai quer utilizar na biblioteca.*/
	private List<InfoVinculoUsuarioBiblioteca> vinculosAtivosNaoUsados;
	
	
	/**
	 * Usado APENAS nos casos em que o usu�rio biblioteca j� existia, mas sem a informa��o do v�nculo, neste caso vai ter que atualizar al�m da senha o v�nculo
	 */
	private boolean atualizaVinculo = false;
	
	/**
	 * Indica que o v�nculo para o qual o usu�rio tinha realizado o cadastro expirou, neste caso n�o ser� poss�vel fazer mais nada com esse v�nculo.
	 * 
	 * O usu�rio deve quita-lo. E se tiver outro v�nculo ativo, voltar a essa p�gina e fazer o recadastro para criar uma nova conta com o outro v�nculo.
	 */
	private boolean vinculoAtualExpirado = false;
	
	/**
	 * 
	 * <p>Inicia o caso de uso para o operador cadastrar a senha de qualquer usu�rio da biblioteca</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarCadastroUsuarioPeloOperador () throws SegurancaException, DAOException{
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, false, false, true, "Cadastrar/Alterar Senha de Usu�rios", null);
	}
	
	
	
	/**
	 * 
	 * <p>Inicia o caso de uso para o pr�prio usu�rio reailzar o cadastro na biblioteca.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 *   	<li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * 		<li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarAutoCadastro () throws ArqException{
		
		prepareMovimento(SigaaListaComando.ALTERAR_SENHA_BIBLIOTECA);
		prepareMovimento(SigaaListaComando.CADASTRAR_SENHA_BIBLIOTECA);
		
		
		pessoaRetornadaBusca = getUsuarioLogado().getPessoa();
		
		
		try {
			carregaUsuarioBiblioteca();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		return getPaginaUsuarioBiblioteca();
	}
	
	
	
	
	
	
	
	///////////////////////// M�todo da interface de busca de usu�rios ///////////////////////
	

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		
		operador = true;
		
		prepareMovimento(SigaaListaComando.ALTERAR_SENHA_BIBLIOTECA);
		prepareMovimento(SigaaListaComando.CADASTRAR_SENHA_BIBLIOTECA);
		
		try {
			carregaUsuarioBiblioteca();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		return getPaginaUsuarioBiblioteca();
	}

	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca){
		
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		pessoaRetornadaBusca = p;
	}
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(String... parametos)
	 */
	@Override
	public void setParametrosExtra(boolean dadosDePessoa, String... parametos){
		
		// VER BuscaUsuarioBibliotecaMBean#selecionouUsuario()
		if(dadosDePessoa){
			cpfPessoa = parametos[0];
			passaportePessoa = parametos[1];
			nomePessoa = parametos[2];
			dataNascimento = parametos[4];
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  Carrega os dados do usu�rio biblioteca, com a pessoa da busca ou com o usu�rio logado
	 * 
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private void carregaUsuarioBiblioteca() throws NegocioException, DAOException{
		
		usuarioBibliotecaAtual = null;
		
		atualizaVinculo = false;
		vinculoAtualExpirado = false;
		vinculoAtual = null;
		
		UsuarioBibliotecaDao dao = null;
		
		try{
			
			dao = getDAO(UsuarioBibliotecaDao.class);
						
			contasUsuarioBiblioteca = dao.findUsuarioBibliotecaAtivoByPessoa( pessoaRetornadaBusca.getId() );
			
			// retorna ordenador na ordem de maior preced�ncia //
			List<InfoVinculoUsuarioBiblioteca> vinculosAtivos = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getVinculosAtivos(pessoaRetornadaBusca.getId());
			
			
			usuarioBibliotecaAtual = UsuarioBibliotecaUtil.recuperaUsuarioNaoQuitadosAtivos(contasUsuarioBiblioteca);
		
			vinculosAtivosNaoUsados = recuperaVinculosAtivosNaoUtilizado(vinculosAtivos, contasUsuarioBiblioteca);
			atualizaVinculo = true;
			vinculoAtual = recuperaVinculosAtivosUtilizadoAtualmente(vinculosAtivos, usuarioBibliotecaAtual);

			if(vinculoAtual != null){
				vinculosAtivosNaoUsados.add(0, vinculoAtual);
				identificacaoVinculoSelecionado = vinculoAtual.getIdVinculo();
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	
	
	
	/**
	 * Cadastra ou atualiza a senha do usu�rio biblioteca caso ele j� esteja selecionado.
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		if (!operador){
			if (!BibliotecaUtil.senhaSigaaCorreta(getCurrentRequest(), getParameter("senhaSigaa"), getUsuarioLogado()))
				addMensagemErro("A senha do sistema est� incorreta!");
		} else 
			confirmaSenha();
			
		String senha1 = getParameter("senha1");
		String senha2 = getParameter("senha2");
		
		if ( !  digitouSenhaIguais(senha1, senha2) )
			addMensagemErro("As senhas digitadas n�o conferem!");
			
		if ( !hasErrors() ){
				
			UsuarioBibliotecaDao dao = getDAO(UsuarioBibliotecaDao.class);
			
			MovimentoSenhaUsuarioBiblioteca mov = new MovimentoSenhaUsuarioBiblioteca();
			
			try {
				
				
				
				/** Atribui o vinculo que ele vai usar o v�nculo escolhido */
				if(vinculosAtivosNaoUsados != null)
				for (InfoVinculoUsuarioBiblioteca infoVinculo : vinculosAtivosNaoUsados) {
					if(infoVinculo.getIdVinculo().equalsIgnoreCase(identificacaoVinculoSelecionado)){
						vinculoEscolhido = infoVinculo;
						break;
					}
				}
			
				
				if( (usuarioBibliotecaAtual == null || atualizaVinculo) && vinculoEscolhido == null){ 

					if(vinculosAtivosNaoUsados != null && vinculosAtivosNaoUsados.size() > 0){ // O usu�rio n�o escolheu nenhum dos v�nculos
						addMensagemErro("Selecione um V�nculo para utilizar o sistema da Biblioteca.");
					}else{                                                                     // o usu�rio n�o possu� v�nculo
						addMensagemErro("O usu�rio n�o possu� v�nculos ativos para utilizar o sistema da Biblioteca");
					}
					
					return null;
				}	
				
				
				
				
				if(usuarioBibliotecaAtual == null){ // Precisa criar um novo usu�rio biblioteca (primeiro cadastro, ou o v�nculo anterior foi quitado)
					
					usuarioBibliotecaAtual = new UsuarioBiblioteca( new Pessoa(pessoaRetornadaBusca.getId()), senha1, vinculoEscolhido.getVinculo(), vinculoEscolhido.getIdentificacaoVinculo());
					usuarioBibliotecaAtual.setVinculo(vinculoEscolhido.getVinculo());
					usuarioBibliotecaAtual.setIdentificacaoVinculo(vinculoEscolhido.getIdentificacaoVinculo());
					
					if( termoAdesaoBibliotecas != null)
						termoAdesaoBibliotecas.setUsuarioBibioteca(usuarioBibliotecaAtual);
					
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_SENHA_BIBLIOTECA);
					mov.setUsuarioBiblioteca(usuarioBibliotecaAtual);
					mov.setTermoAdesao(termoAdesaoBibliotecas); // passa nulo para n�o criar.
					mov.setContasUsuarioBiblioteca(contasUsuarioBiblioteca);
					
					execute(mov);
					
					addMensagemInformation("Usu�rio cadastrado para utilizar os servi�os das bibliotecas do sistema.");
					
				}else{   // Usu�rio j� existe, apenas atualiza sua senha, ou a senha + o vinculo, para o usu�rio atual n�o tenha a informa��o do v�nculo ainda (usu�rios antigos)
					
					usuarioBibliotecaAtual.atualizaSenha(senha1);
					
					if(vinculoAtual  != null  && ! vinculoAtual.getIdVinculo().equalsIgnoreCase(vinculoEscolhido.getIdVinculo() ) )
						atualizaVinculo = true;
					else
						atualizaVinculo = false;
					
					if(atualizaVinculo){
						usuarioBibliotecaAtual.setVinculo(vinculoEscolhido.getVinculo());
						usuarioBibliotecaAtual.setIdentificacaoVinculo(vinculoEscolhido.getIdentificacaoVinculo());
					}
						
					mov.setCodMovimento(SigaaListaComando.ALTERAR_SENHA_BIBLIOTECA);
					mov.setUsuarioBiblioteca(usuarioBibliotecaAtual);
					mov.setContasUsuarioBiblioteca(contasUsuarioBiblioteca);
					mov.setAtualizaVinculo(atualizaVinculo); // S� vai atulizar o v�nculo caso j� possua uma conta e essa conta n�o possuia a informa��o do v�nculo (usu�rios antigos)
					
					execute(mov);
					
					if(atualizaVinculo)
						addMensagemInformation("V�nculo do Usu�rio atualizada com sucesso.");
					else	
						addMensagemInformation("Senha do usu�rio atualizada com sucesso.");
					
				}
				
			} catch (NegocioException ne){
				addMensagens(ne.getListaMensagens());
				
				if(mov != null && mov.getCodMovimento() != null && mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_SENHA_BIBLIOTECA))
					usuarioBibliotecaAtual = null;
				
				return null;
			}finally{
				if(dao != null) dao.close();
			}
			
			if (!operador)
				return getPaginaRetorno();
			else
				return cancelar();
		}
		
		return null; // Caso hava algum erro, fica na mesma p�gina mostrando o erro
		
	}
	
	
	
	
	
	
	/**
	 * 
	 * <p>Retorna os vinculos ativos ainda n�o utilizados pelo usu�riona biblioteca, para ele escolher qual quer utilizar.</p>
	 * 
	 *
	 * @return
	 */
	private List<InfoVinculoUsuarioBiblioteca> recuperaVinculosAtivosNaoUtilizado(List<InfoVinculoUsuarioBiblioteca> vinculosAtivos, List<UsuarioBiblioteca> contasUsuarioBiblioteca ){
	
		List<InfoVinculoUsuarioBiblioteca> vinculosAtivosNaoUsados = new ArrayList<InfoVinculoUsuarioBiblioteca>();
		
		if(contasUsuarioBiblioteca.size() == 0 && vinculosAtivos.size() > 0){ // Se nunca teve um conta, retorna todos ativos
			return vinculosAtivos;
		}
		
		for (InfoVinculoUsuarioBiblioteca vinculoAtivo : vinculosAtivos) {
			
			boolean jaUtilizouVinculo = false;
			
			for (UsuarioBiblioteca usuarioBiblioteca : contasUsuarioBiblioteca) {
				if(usuarioBiblioteca.getVinculo() != null &&  usuarioBiblioteca.getIdentificacaoVinculo() != null
						&& usuarioBiblioteca.getVinculo() == vinculoAtivo.getVinculo() 
						&& usuarioBiblioteca.getIdentificacaoVinculo().equals(vinculoAtivo.getIdentificacaoVinculo())){
					jaUtilizouVinculo = true;
				}
			} 
			
			if(! jaUtilizouVinculo) vinculosAtivosNaoUsados.add(vinculoAtivo);
			
		}
		
		return vinculosAtivosNaoUsados;
		
	}
	
	
	/**
	 * 
	 * <p>Retorna os vinculos ativos ainda n�o utilizados pelo usu�riona biblioteca, para ele escolher qual quer utilizar.</p>
	 * 
	 *
	 * @return
	 */
	private InfoVinculoUsuarioBiblioteca recuperaVinculosAtivosUtilizadoAtualmente(List<InfoVinculoUsuarioBiblioteca> vinculosAtivos, UsuarioBiblioteca usuarioBibliotecaAtual ){
	
		
		
		if(usuarioBibliotecaAtual == null){ // Se nunca teve um conta, retorna todos ativos
			return null;
			
		}
		
		for (InfoVinculoUsuarioBiblioteca vinculoAtivo : vinculosAtivos) {
			if(usuarioBibliotecaAtual.getVinculo() != null &&  usuarioBibliotecaAtual.getIdentificacaoVinculo() != null
					&& usuarioBibliotecaAtual.getVinculo() == vinculoAtivo.getVinculo() 
					&& usuarioBibliotecaAtual.getIdentificacaoVinculo().equals(vinculoAtivo.getIdentificacaoVinculo())){
				return vinculoAtivo;
			}
		}
		
		return null;
	}
	
	
	
	
	
	/**
	 * 
	 * <p> Retorna os componente curriculares para a atividade selecionada.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @throws DAOException 
	 *
	 *
	 */
	public Collection <SelectItem> getVinculosAtivosNaoUsadosComboBox() throws DAOException{
		
		List<SelectItem> itens = new ArrayList<SelectItem>();
		
		if(vinculosAtivosNaoUsados != null){
			int contador = 1;
			for (InfoVinculoUsuarioBiblioteca infoVinculo : vinculosAtivosNaoUsados) {
				
				String infoInicio = "";
				String infoFinal = "";
				
				if(vinculoAtual != null && infoVinculo.getIdVinculo().equals(vinculoAtual.getIdVinculo())){
					infoInicio = "";
					infoFinal = " ( V�nculo Atual ) ";
				}else{	
					infoInicio = contador+"� - ";
					infoFinal = "";
					contador++;
				}
				
				SelectItem item = new SelectItem(infoVinculo.getIdVinculo(), infoInicio+infoVinculo.getDescricaoVinculo()+infoFinal);
				itens.add(item);
				
			}
		}
		
		return itens;
	}
	
	
	
	/**
	 * 
	 * Verifica se a senha digitada esta igual a sua confirmacao
	 *
	 * @param senha1
	 * @param senha2
	 * @return
	 */
	private boolean digitouSenhaIguais(String senha1, String senha2) {
		if(senha1 == null || senha2 == null)
			return false;
		
		if (!senha1.equals(senha2))
			return false;

		return true;
	}
	
	/***
	 * @return Se o usu�rio vai fazer cadastro no sistema.
	 *  
	 * <p> Criado em:  28/06/2013  </p>
	 *
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp/</li>
	 * </ul>
	 */
	public boolean isCadastrarNovoUsuario(){
		return (((usuarioBibliotecaAtual == null && getQtdVinculosAtivosNaoUsados() > 0) && !vinculoAtualExpirado)  && pessoaRetornadaBusca != null );
	}
	
	/***
	 * @return Verifica se termo de ades�o est� ativo
	 *  
	 * <p> Criado em:  28/06/2013  </p>
	 *
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp/</li>
	 * </ul>
	 */
	public boolean isTermoDeResponsabilidadeAtivo(){
		if (StringUtils.isEmpty(textTermoAdesao))
			return false;
		else
			return true;
	}
	
	
	/***
	 * N�o deixar o usu�rio passa para o p�gina de cadastro se ele n�o concordar com o termo.
	 *  
	 * <p> Criado em:  28/06/2013  </p>
	 *
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp/</li>
	 * </ul>
	 */
	public String paginaCadastro(){
		if (concordoCheck)
			return forward(PAGINA_FORM_SENHA_USUARIO);
		else{
			addMensagemErro("Para realizar o cadastro deve-se concordar com o presente Termo de Responsabilidade.");
			return null;
		}
	}
	
	private String getPaginaUsuarioBiblioteca(){
		
		/*
		 * Assina o termo se e somente se:
		 * 
		 * � um novo cadastro
		 * Se o termpo de responsabilidade est� ativo
		 * Se � o pr�prio usu�rio que est� cadastrando  (S� funciona quando � o pr�prio usu�rio, pois pega as informa��es do usu�rio logado !!!!!!!!!!!!! )
		 */
		if (isCadastrarNovoUsuario() && isTermoDeResponsabilidadeAtivo() && ! isOperador()){
			
			// cria o nome termo a ser assinado
			termoAdesaoBibliotecas = new TermoAdesaoSistemaBibliotecas(getUsuarioLogado().getRegistroEntrada()
					, textTermoAdesao, new Date(), getUsuarioLogado().getPessoa().getNome(),
					getUsuarioLogado().getPessoa().getCpf_cnpj() != null ? getUsuarioLogado().getPessoa().getCpf_cnpj().toString() : getUsuarioLogado().getPessoa().getPassaporte());
			
			return forward(PAGINA_TERMO_RESPONSABILIDADE);
		}
		else{
			termoAdesaoBibliotecas = null; // n�o vai ser criado
			return forward(PAGINA_FORM_SENHA_USUARIO);
		}
	}
	
	/**
	 * Verifica para qual p�gina o fluxo da navega��o deve ir, dependendo de qual v�nculo
	 * o usu�rio esteja utilizando no momento. Isso porque a p�gina que esse MBean controla pode ser
	 * chamada de lugares diferentes e tem que retornar para lugares diferentes.
	 *
	 * @return
	 */
	private String getPaginaRetorno(){
		return cancelar();
	}

	

	/// sets e gets
	
	public boolean isOperador() {
		return operador;
	}

	public void setOperador(boolean operador) {
		this.operador = operador;
	}
	
	
	public UsuarioBiblioteca getUsuarioBibliotecaAtual() {
		return usuarioBibliotecaAtual;
	}

	public void setUsuarioBibliotecaAtual(UsuarioBiblioteca usuarioBibliotecaAtual) {
		this.usuarioBibliotecaAtual = usuarioBibliotecaAtual;
	}

	public String getCpfPessoa() {
		return cpfPessoa;
	}

	public void setCpfPessoa(String cpfPessoa) {
		this.cpfPessoa = cpfPessoa;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	public String getPassaportePessoa() {
		return passaportePessoa;
	}

	public void setPassaportePessoa(String passaportePessoa) {
		this.passaportePessoa = passaportePessoa;
	}

	public InfoVinculoUsuarioBiblioteca getVinculoEscolhido() {
		return vinculoEscolhido;
	}

	public void setVinculoEscolhido(InfoVinculoUsuarioBiblioteca vinculoEscolhido) {
		this.vinculoEscolhido = vinculoEscolhido;
	}

	/**
	 * Retorna a string formatada que representa a data de nascimento.
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>/sigaa.war/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 */
	public String getDataNascimento() {
		
		if(StringUtils.isEmpty(dataNascimento))
			return "";
		
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
	    Date date;
		try {
			date = formatter.parse(dataNascimento);
			return new SimpleDateFormat("dd/MM/yyyy").format(date);
		} catch (ParseException e) {
			return "";
		}  
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}


	public boolean isVinculoAtualExpirado() {
		return vinculoAtualExpirado;
	}

	public void setVinculoAtualExpirado(boolean vinculoAtualExpirado) {
		this.vinculoAtualExpirado = vinculoAtualExpirado;
	}

	public List<InfoVinculoUsuarioBiblioteca> getVinculosAtivosNaoUsados() {
		return vinculosAtivosNaoUsados;
	}

	public void setVinculosAtivosNaoUsados(List<InfoVinculoUsuarioBiblioteca> vinculosAtivosNaoUsados) {
		this.vinculosAtivosNaoUsados = vinculosAtivosNaoUsados;
	}

	/**
	 * Retorna a quantidade de v�nculos ativos que o usu�rio n�o utilizou ainda. Se ele n�o possui 
	 * nenhuma conta na biblioteca atualmente e possui pelo menos 1 v�nculo ativo ainda n�o usado, ele vai poder criar 
	 * uma nova conta na biblioteca. 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>/sigaa.war/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdVinculosAtivosNaoUsados(){
		if (vinculosAtivosNaoUsados == null)
			return 0;
		else
			return vinculosAtivosNaoUsados.size();
	}


	public Pessoa getPessoaRetornadaBusca() {
		return pessoaRetornadaBusca;
	}

	public void setPessoaRetornadaBusca(Pessoa pessoaRetornadaBusca) {
		this.pessoaRetornadaBusca = pessoaRetornadaBusca;
	}

	public String getIdentificacaoVinculoSelecionado() {
		return identificacaoVinculoSelecionado;
	}

	public void setIdentificacaoVinculoSelecionado(String identificacaoVinculoSelecionado) {
		this.identificacaoVinculoSelecionado = identificacaoVinculoSelecionado;
	}

	public boolean isConcordoCheck() {
		return concordoCheck;
	}

	public void setConcordoCheck(boolean concordoCheck) {
		this.concordoCheck = concordoCheck;
	}

	public String getTermoAdesao() {
		return textTermoAdesao;
	}

	public void setTermoAdesao(String termoAdesao) {
		this.textTermoAdesao = termoAdesao;
	}
	
	public TermoAdesaoSistemaBibliotecas getTermoAdesaoBibliotecas() {
		return termoAdesaoBibliotecas;
	}

	public void setTermoAdesaoBibliotecas(
			TermoAdesaoSistemaBibliotecas termoAdesaoBibliotecas) {
		this.termoAdesaoBibliotecas = termoAdesaoBibliotecas;
	}
	
}
