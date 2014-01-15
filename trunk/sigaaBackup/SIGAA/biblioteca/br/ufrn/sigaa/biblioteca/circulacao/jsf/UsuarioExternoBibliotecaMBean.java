/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/05/2009
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioExternoBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioExternoBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * <p> MBean que gerencia as alterações nos vínculos de um usuário externo da biblioteca.</p>
 * 
 * <p> Para ser cadastrado como usuário externo, o usuário precisa possuir pelo menos uma Pessoa 
 * casdastrada no sistema, por isso esse MBean chama o MBean <code>DadosPessoaisMBean</code>. <br/> 
 * E para que o fluxo volte a esse caso de uso depois que a pessoa for cadastrada, é necessário 
 * implementar a interface <code>OperadorDadosPessoais</code> que possui o método <code>submeterDadosPessoais()</code> 
 * e  <code>setDadosPessoais()</code> para onde o fluxo será retornado.</p>
 * 
 * <p>É preciso também cadastra este Mbean na classe  <code>OperacaoDadosPessoais</code></p>
 * 
 * @author Fred_Castro
 * @since 20/05/2009
 *
 */

@Component ("usuarioExternoBibliotecaMBean")
@Scope ("session")
public class UsuarioExternoBibliotecaMBean extends SigaaAbstractController <UsuarioExternoBiblioteca> implements OperadorDadosPessoais, PesquisarUsuarioBiblioteca{
	
	/** Cadastra um novo usuário da biblioteca */
	public static final String PAGINA_FORM = "/biblioteca/UsuarioExternoBiblioteca/form.jsp"; 
	
	/** Cadastra um novo usuário da biblioteca */
	public static final String PAGINA_FORM_VERIFICA_PESSOA_EXTRAGEIRA = "/biblioteca/UsuarioExternoBiblioteca/formVerificaPessoaExtrageira.jsp"; 
	
	/** Cancelar o vínculo do usuário externo, o vínculo também deve ser automaticamente cancelado se o prazo expirar*/
	public static final String PAGINA_FORM_CANCELAR_VINCULO = "/biblioteca/UsuarioExternoBiblioteca/formCancelar.jsp";
	
	
	/** A pessoa para a qual vai ser criado o vínculo de usuáiro externo, as informações da pessoa 
	 * são configuradas pelo MBean <code>DadosPessoaisMBean</code>. 
	 * <br/>
	 * <br/>
	 *  Na parte de cancelamento, essa variável é usada para guardar a pessoa para a qual o usuário 
	 *  externo via ser cancelado. 
	 */
	private Pessoa pessoa;
	
	
	/**
	 * Utilizado no cadastro de pessoas extrangeiras caso exista mais de uma com o mesmo passaporte.
	 * 
	 * Usuário vai escolher se utiliza uma pessoa já existente ou cadastra uma nova.
	 */
	private List<Pessoa> pessoasComPassaporteDuplicados;
	
	
	/** Usado para o usuário digitar o senha e a confirmação da senha.*/
	private String senha;
	/** Usado para o usuário digitar o senha e a confirmação da senha.*/
	private String confirmarSenha;
	
	/** As informações do usuário mostrada na página */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	/** Usado para contar para qual página deve voltar quando o usuário clicar no botão voltar.*/
	private boolean voltaPaginaSelecionaPessoaExtrageiraExistente = false;
	
	/**
	 * Indica que se vai utilizar a busca do usuário para selecionar um usuário e cancelar
	 */
	private boolean buscandoUsusarioParaCancelar = false;
	
	/**
	 * Construtor padrão.
	 */
	public UsuarioExternoBibliotecaMBean (){
		
	}
	
	
	/**
	 * <p>Inicia o cadastro de um vínculo de usuário externo para uma nova pessoa.</p>
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul> 
	 */
	public String iniciarCadastroUsuarioExterno() throws ArqException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		buscandoUsusarioParaCancelar = false;
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, false, true, false, false, "Cadastro de Usuários Externos", "/biblioteca/circulacao/incluirBotaoCadastraNovoUsuarioExterno.jsp");
	}
	
	/**
	 * <p>Inicia o cancelamento de um vínculo de usuário externo da biblioteca.</p>
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul> 
	 */
	public String  iniciarCancelamentoVinculo() throws ArqException {
		
		buscandoUsusarioParaCancelar = true;
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, false, true, false, false, "Cancelar Vínculo de Usuários Externos", null);
		
		
	}
	
	
	/**
	 * <p>Inicia o cadastro de um vínculo de usuário externo para uma nova pessoa.</p>
	 *  
	 *  
	 * Método chamado pela seguinte JSP: /biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDadosPessoa() throws ArqException {
		
		pessoa = null;
		pessoasComPassaporteDuplicados = new ArrayList<Pessoa>();
		obj = new UsuarioExternoBiblioteca();
	

		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.carregarMunicipios();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.USUARIO_EXTERNO_BIBLIOTECA );
		return dadosPessoaisMBean.popular();
	}
	
	
	/////////////////////////// Métodos da interface padrão de busca ///////////////////////////
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		if(! buscandoUsusarioParaCancelar)
		return iniciarAlteracaoDadosPessoa();
		else
			return preCancelarVinculo();
	}



	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		
	}



	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa,
			String... parametros) {
		
	}



	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		pessoa = p;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 
	 * <p>Inicia o cadastro de um vínculo de usuário externo para uma pessoa já existente no sistema.</p>
	 * <p>Utilizado quando na busca de usuários externos, o operador escolher uma pessoa que já está cadastrada no sistema.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 * @throws ArqException
	 */
	public String  iniciarAlteracaoDadosPessoa()throws ArqException {
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
		
		pessoa = getDAO(PessoaDao.class).findCompleto(pessoa.getId()); 
		pessoa.prepararDados();
		
		obj = new UsuarioExternoBiblioteca();
		
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.USUARIO_EXTERNO_BIBLIOTECA );
		dadosPessoaisMBean.setExibirPainel(false);
		dadosPessoaisMBean.setBackButton(null);
		dadosPessoaisMBean.setObj(pessoa);
		dadosPessoaisMBean.carregarMunicipios();
		dadosPessoaisMBean.setOrdemBotoes(false); // Significa que está alterando
		return forward(DadosPessoaisMBean.JSP_FORM);
	}
	
	
	
	
	
	/**
	 * <p>O MBean dados pessoais vai retorna a pessoa por meio desse método.</p>
	 * 
	 * @see br.ufrn.sigaa.jsf.OperadorDadosPessoais#setDadosPessoais(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	public void setDadosPessoais(Pessoa pessoa){
		this.pessoa = pessoa;
	}
	
	
	
	/**
	 * <p>Método que é chamado pelo DadosPessoaisMBean depois que a pessoa é cadastrada para retornar 
	 * o caso de uso os fluxo normal do cadastro de um usuário externo </p> .
	 * 
	 * 
	 * @see br.ufrn.sigaa.jsf.OperadorDadosPessoais#submeterDadosPessoais()
	 * <br><br>
	 * Método não é chamado por nenhuma JSP.<br>
	 * Chamado por {@link DadosPessoaisMBean#submeterDadosPessoais()}
	 */
	public String submeterDadosPessoais() {
		
		
		UsuarioExternoBibliotecaDao daoUsuarioExterno = null;
		try {
			
			
			daoUsuarioExterno = getDAO(UsuarioExternoBibliotecaDao.class);
			
			/*
			 *  Atualizou os dados da pessoa, em outras palavras pessoa já existe no sistema. 
			 */
			if(pessoa.getId() > 0){ 
				return continuaCadastroUsuarioExterno();
			}else{
			
				 /*
                  *  Pessoa não existe no sistema
				  *  Verifica se já existe outra pessoa com o mesmo número de passaporte.  <br/>
				  *  
				  *  Se sim, redireciona para o usuário escolher se se trata do mesmo usuário ou não,
				  *  já que não podemos verificar a unicidade do passaporte  
				  */
				if(pessoa.isInternacional()){ 
					pessoasComPassaporteDuplicados  = (List<Pessoa>) daoUsuarioExterno.findByExactField(Pessoa.class, "passaporte", pessoa.getPassaporte());
					
					if(pessoasComPassaporteDuplicados.size() <= 0){     // Não Existem pessoas cadastradas com o passaporte informado
						voltaPaginaSelecionaPessoaExtrageiraExistente = false;
						return continuaCadastroUsuarioExterno();
					}else{
						voltaPaginaSelecionaPessoaExtrageiraExistente = true;
						return verificaCadastroPessoasExtrageiras();
					}
					
				}else{  // Para pessoas nacionais, não tem problema, pois há verificação de duplicidade do CPF
					
					voltaPaginaSelecionaPessoaExtrageiraExistente = false;
					return continuaCadastroUsuarioExterno();
				}
			}
			
			
		}catch (DAOException de){
			return tratamentoErroPadrao(de);
		} finally {
			if (daoUsuarioExterno != null) daoUsuarioExterno.close();
		}
		
	}
	
	/**
	 *   Caso o usuário digite um passaporte que já existe no sitema, mostra os outros usuário com o mesmo passaporte para confirmação
	 *
	 *   Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 */
	private String verificaCadastroPessoasExtrageiras(){
		
		return forward(PAGINA_FORM_VERIFICA_PESSOA_EXTRAGEIRA);
	}
	
	
	/**
	 *   <p>Método responsável por verifica se o usuário já possui <strong>usuário externo biblioteca</strong> ou <strong>usuário biblioteca</strong>. </p>
	 *   
	 *   <p>Caso possua qualquer um dos dois, esse método deve garantir que as informações deles sejam apenas <strong>atualizadas</strong>, para
	 *   não gerar duplicidade no cadastro na biblioteca.</p>
	 *  
	 *   Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 */
	private String continuaCadastroUsuarioExterno(){
		
		UsuarioBibliotecaDao dao = null;
		UsuarioExternoBibliotecaDao daoUsuarioExterno = null;
		
		try {
		
			dao = getDAO(UsuarioBibliotecaDao.class);
			daoUsuarioExterno = getDAO(UsuarioExternoBibliotecaDao.class);
			
			
			List<UsuarioBiblioteca> usuarios = dao.findUsuarioBibliotecaAtivoNaoQuitadoByPessoa( pessoa.getId() );
			
			if(usuarios.size() > 1){
				addMensagemErro("Usuário possui cadastro duplicado no sistema, por favor entre em contado com o suporte para desativar um cadastro.");
				return null;
			}
				
			if(usuarios.size() == 0){  // Não possui UsuarioBiblioteca cadastrado ainda, ou todos estão quitados
				
				obj.setUsuarioBiblioteca(new UsuarioBiblioteca(pessoa, VinculoUsuarioBiblioteca.USUARIO_EXTERNO, 0));
				setConfirmButton("Cadastrar novo Usuário Externo");    // Pode realizar o cadastro no novo usuário
				
			}else{ // Já possui usuário biblioteca cadastrado
				
				UsuarioBiblioteca usuarioNaoQuitado = usuarios.get(0);
				
				if(usuarioNaoQuitado.getVinculo() == VinculoUsuarioBiblioteca.USUARIO_EXTERNO){ // O não quitado é um vínculo de usuário externo, então atualiza esse cadastro
					
					List<UsuarioExternoBiblioteca> usuariosExt =  daoUsuarioExterno.findAtivoNaoQuitadoByUsuarioBiblioteca(usuarioNaoQuitado.getId());
					
					// Só uma verificação, o sistema não deveria deixar ocorrer isso
					if(usuariosExt.size() > 1){
						addMensagemErro("Usuário possui cadastro de usuários externos duplicados no sistema, por favor entre em contado com o suporte para desativar um cadastro.");
						return null;
					}
					
					if(usuariosExt.size() == 0){ // Outra situação que não pode ocorrer, o usuári tem vínculo de usuário externo, mas não tem as informações do usuário externo 
						
						addMensagemErro("Não foi possível recuperar as informações do vínculo do usuário externo, quite o vínculo atual para poder cadastra um novo vínculo e resolver o problema.");
						return null;
						
					}else{  // O normal de ocorrer, o usuário biblioteca com o  vínculo de usuário externo deve possuir um usuário externo
						
						obj = usuariosExt.get(0);
						
						if(obj.getUsuarioBiblioteca().getPessoa().getId() == pessoa.getId()) // Atualiza as informações da pessoa
							obj.getUsuarioBiblioteca().setPessoa(pessoa);
						
						setConfirmButton("Alterar Dados do Usuário Externo");
					}
					
				}else{ // Já tem uma conta na biblioteca não quitada, como não pode ter 2, tem que quitar a anterior para fazer o cadastro como usuário externo 
					addMensagemErro("Usuário está cadastrado na biblioteca com o vínculo de "+usuarioNaoQuitado.getVinculo().getDescricao()+" é preciso primeiro quitar esse vínculo para realizar o cadastro dele como usuário externo. ");
					return null;
				}
				
			}
				
			if(obj.isCancelado()){
				addMensagemWarning("O vínculo de usuário externo está cancelado, atualizando as suas informações ele será ativado novamente.");
			}
			
			prepareMovimento(SigaaListaComando.CADASTRAR_USUARIO_EXTERNO_BIBLIOTECA);
				
			return forward(PAGINA_FORM);
		
		}catch (DAOException de){
			return tratamentoErroPadrao(de);
		} catch (ArqException ae){
			return tratamentoErroPadrao(ae);
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/**
	 * 
	 * Executa a ação do botão foltar no formulário de cadastro das informações de um usuário externo
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/UsuarioExternoBiblioteca/form.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String voltarPagina() throws SegurancaException, DAOException{
		if( voltaPaginaSelecionaPessoaExtrageiraExistente)
			return forward(PAGINA_FORM_VERIFICA_PESSOA_EXTRAGEIRA);
		else
			return voltarFormularioPessoaExtrageira();
	}
	
	
	
	/**
	 * 
	 * Se na página que o usuário escolhe entre as pessoas castradas com mesmo passaporte ele revolve voltar para a página de cadastro.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/UsuarioExternoBiblioteca/formVerificaPessoaExtrageira.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String voltarFormularioPessoaExtrageira() throws SegurancaException, DAOException{
		// retorna para a página do formulário de pessoas
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.USUARIO_EXTERNO_BIBLIOTECA );
		dadosPessoaisMBean.setExibirPainel(false);
		dadosPessoaisMBean.setBackButton(null);
		dadosPessoaisMBean.setObj(pessoa);
		dadosPessoaisMBean.carregarMunicipios();
		return forward(DadosPessoaisMBean.JSP_FORM);
		
	}
	
	
	/**
	 * 
	 * Se na página que o usuário escolhe entre as pessoas castradas com mesmo passaporte ele revolve incluir os dados de uma nova pessoa, com o mesmo passaporte.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/UsuarioExternoBiblioteca/formVerificaPessoaExtrageira.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String prosseguirCadastroPessoaExtrageira(){
		return continuaCadastroUsuarioExterno();       // vai incluir uma nova pessoa com os dados digitados
	}
	
	
	/**
	 * 
	 * Se na página que o usuário escolhe entre as pessoas castradas com mesmo passaporte ele revolve criar o vínculo para uma pessoa que já existia no sistema com o mesmo passaporte
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/UsuarioExternoBiblioteca/formVerificaPessoaExtrageira.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void selecionouPessoaExtrageiraCadastrada(ActionEvent  evt) throws DAOException{
		this.pessoa = getDAO(PessoaDao.class).findCompleto(getParameterInt("idPessoaSelecionada")); // Pega os dados da pessoa selecionada
		continuaCadastroUsuarioExterno();   
	}
	
	
	/**
	 * Exibe o formulário para se cancelar o vínculo de um usuário externo. <br/>
	 * Método chamado pela seguinte JSP: /biblioteca/resultadoBuscaUsuarioBiblioteca.jsp <br/>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String preCancelarVinculo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		UsuarioExternoBibliotecaDao dao = null;
		
		try {
			dao = getDAO(UsuarioExternoBibliotecaDao.class);
			
			pessoa = dao.refresh(pessoa);
			
			List<UsuarioExternoBiblioteca> listaTemp = dao.findUsuarioExternoBibliotecaNaoQuitadoByPessoa(pessoa.getId());
			
			if(listaTemp.size() > 1){
				addMensagemErro(" Existe mais de um vínculo de usuário externo para o mesmo usuário, por favor entre em contado com o suporta para os outros vínculo serem cancelados. ");
				return null;
			}
			
			if(listaTemp.size() == 0){
				addMensagemErro(" Vínculo do usuário externo não encontado.");
				return null;
			}
			
			obj = listaTemp.get(0);
			
			if(obj.isCanceladoPorPrazo())
				addMensagemErro("O vínculo de usuário externo expirou.");
			
			if(obj.isCanceladoManualmenente())
				addMensagemErro("O vínculo de usuário externo foi cancelado.");
			
			prepareMovimento(ArqListaComando.ALTERAR);
			
			return forward (PAGINA_FORM_CANCELAR_VINCULO);
			
		}finally {
			if (dao != null)
				dao.close();
		}
	}
	
	
	
	/**
	 * Cancela o vínculo do usuário externo, fazendo com que ele não possa mais realizar empréstimos.
	 * Método chamado pela seguinte JSP: /biblioteca/UsuarioExternoBiblioteca/formCancelar.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cancelarVinculo () throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		confirmaSenha();
		if (StringUtils.isEmpty(obj.getMotivoCancelamento()))
				addMensagemErro("O motivo do cancelamento deve ser informado");
		
		if (!hasErrors()){
			obj.setDataCancelamento(new Date());
			obj.setRegistroEntradaCancelamento(getUsuarioLogado().getRegistroEntrada());
			obj.setCancelado(true);
			
			MovimentoCadastro mov = new MovimentoCadastro(obj);
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			
			try {
				execute(mov);
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			}
			
			return forward("/biblioteca/index.jsp");
		}
		
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Cadastra o vínculo do usuário externo.
	 * 
	 * Método chamado pela seguinte JSP: /biblioteca/UsuarioExterno/form.jsp
	 * 
	 */
	@Override
	public String cadastrar () throws ArqException {
		
		if (!senha.equals(confirmarSenha)){
			addMensagemErro("A confirmação da senha não confere com a senha digitada.");
			return null;
		}
		
		String mensagemUsuario = "";
		
		if(obj.getId() == 0){
			
			if(obj.getUsuarioBiblioteca().getId() == 0)
				mensagemUsuario = "Cadastro para utilização das Bibliotecas como Usuário Externo realizado com sucesso.";
			else	
				mensagemUsuario = "Vínculo de Usuário Externo cadastrado com sucesso.";
		}else
			mensagemUsuario = "Informações do Usuário Externo atualizadas com sucesso.";
		
		try {
			
			// Importante: Se o usuário digitou alguma coisa no campo senha, tem que atualizar a senha
			//  e confirmar a senha do operador.
			if( StringUtils.notEmpty(senha)){
				obj.getUsuarioBiblioteca().atualizaSenha(senha);
				confirmaSenha();
			}	
			
			if (!hasErrors()){
				
				MovimentoCadastro mov = new MovimentoCadastro(obj);	
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_USUARIO_EXTERNO_BIBLIOTECA);
				
				execute(mov);
				
				addMensagemInformation(mensagemUsuario);
				
				return forward("/biblioteca/index.jsp");
				
			}
				
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
		}
		return null;
	}

	
	
	
	
	///////////  Sets e Gets ////////
	
	

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getConfirmarSenha() {
		return confirmarSenha;
	}

	public void setConfirmarSenha(String confirmarSenha) {
		this.confirmarSenha = confirmarSenha;
	}

	public InformacoesUsuarioBiblioteca getInfoUsuario() {
		return infoUsuario;
	}

	public void setInfoUsuario(InformacoesUsuarioBiblioteca infoUsuario) {
		this.infoUsuario = infoUsuario;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public List<Pessoa> getPessoasComPassaporteDuplicados() {
		return pessoasComPassaporteDuplicados;
	}

	public void setPessoasComPassaporteDuplicados(List<Pessoa> pessoasComPassaporteDuplicados) {
		this.pessoasComPassaporteDuplicados = pessoasComPassaporteDuplicados;
	}


	
	
}