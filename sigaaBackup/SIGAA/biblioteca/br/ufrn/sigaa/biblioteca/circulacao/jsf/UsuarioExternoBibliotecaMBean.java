/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * <p> MBean que gerencia as altera��es nos v�nculos de um usu�rio externo da biblioteca.</p>
 * 
 * <p> Para ser cadastrado como usu�rio externo, o usu�rio precisa possuir pelo menos uma Pessoa 
 * casdastrada no sistema, por isso esse MBean chama o MBean <code>DadosPessoaisMBean</code>. <br/> 
 * E para que o fluxo volte a esse caso de uso depois que a pessoa for cadastrada, � necess�rio 
 * implementar a interface <code>OperadorDadosPessoais</code> que possui o m�todo <code>submeterDadosPessoais()</code> 
 * e  <code>setDadosPessoais()</code> para onde o fluxo ser� retornado.</p>
 * 
 * <p>� preciso tamb�m cadastra este Mbean na classe  <code>OperacaoDadosPessoais</code></p>
 * 
 * @author Fred_Castro
 * @since 20/05/2009
 *
 */

@Component ("usuarioExternoBibliotecaMBean")
@Scope ("session")
public class UsuarioExternoBibliotecaMBean extends SigaaAbstractController <UsuarioExternoBiblioteca> implements OperadorDadosPessoais, PesquisarUsuarioBiblioteca{
	
	/** Cadastra um novo usu�rio da biblioteca */
	public static final String PAGINA_FORM = "/biblioteca/UsuarioExternoBiblioteca/form.jsp"; 
	
	/** Cadastra um novo usu�rio da biblioteca */
	public static final String PAGINA_FORM_VERIFICA_PESSOA_EXTRAGEIRA = "/biblioteca/UsuarioExternoBiblioteca/formVerificaPessoaExtrageira.jsp"; 
	
	/** Cancelar o v�nculo do usu�rio externo, o v�nculo tamb�m deve ser automaticamente cancelado se o prazo expirar*/
	public static final String PAGINA_FORM_CANCELAR_VINCULO = "/biblioteca/UsuarioExternoBiblioteca/formCancelar.jsp";
	
	
	/** A pessoa para a qual vai ser criado o v�nculo de usu�iro externo, as informa��es da pessoa 
	 * s�o configuradas pelo MBean <code>DadosPessoaisMBean</code>. 
	 * <br/>
	 * <br/>
	 *  Na parte de cancelamento, essa vari�vel � usada para guardar a pessoa para a qual o usu�rio 
	 *  externo via ser cancelado. 
	 */
	private Pessoa pessoa;
	
	
	/**
	 * Utilizado no cadastro de pessoas extrangeiras caso exista mais de uma com o mesmo passaporte.
	 * 
	 * Usu�rio vai escolher se utiliza uma pessoa j� existente ou cadastra uma nova.
	 */
	private List<Pessoa> pessoasComPassaporteDuplicados;
	
	
	/** Usado para o usu�rio digitar o senha e a confirma��o da senha.*/
	private String senha;
	/** Usado para o usu�rio digitar o senha e a confirma��o da senha.*/
	private String confirmarSenha;
	
	/** As informa��es do usu�rio mostrada na p�gina */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	/** Usado para contar para qual p�gina deve voltar quando o usu�rio clicar no bot�o voltar.*/
	private boolean voltaPaginaSelecionaPessoaExtrageiraExistente = false;
	
	/**
	 * Indica que se vai utilizar a busca do usu�rio para selecionar um usu�rio e cancelar
	 */
	private boolean buscandoUsusarioParaCancelar = false;
	
	/**
	 * Construtor padr�o.
	 */
	public UsuarioExternoBibliotecaMBean (){
		
	}
	
	
	/**
	 * <p>Inicia o cadastro de um v�nculo de usu�rio externo para uma nova pessoa.</p>
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul> 
	 */
	public String iniciarCadastroUsuarioExterno() throws ArqException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		buscandoUsusarioParaCancelar = false;
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, false, true, false, false, "Cadastro de Usu�rios Externos", "/biblioteca/circulacao/incluirBotaoCadastraNovoUsuarioExterno.jsp");
	}
	
	/**
	 * <p>Inicia o cancelamento de um v�nculo de usu�rio externo da biblioteca.</p>
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul> 
	 */
	public String  iniciarCancelamentoVinculo() throws ArqException {
		
		buscandoUsusarioParaCancelar = true;
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, false, true, false, false, "Cancelar V�nculo de Usu�rios Externos", null);
		
		
	}
	
	
	/**
	 * <p>Inicia o cadastro de um v�nculo de usu�rio externo para uma nova pessoa.</p>
	 *  
	 *  
	 * M�todo chamado pela seguinte JSP: /biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp
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
	
	
	/////////////////////////// M�todos da interface padr�o de busca ///////////////////////////
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
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
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		
	}



	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa,
			String... parametros) {
		
	}



	/**
	 * Ver coment�rios da classe pai.<br/>
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
	 * <p>Inicia o cadastro de um v�nculo de usu�rio externo para uma pessoa j� existente no sistema.</p>
	 * <p>Utilizado quando na busca de usu�rios externos, o operador escolher uma pessoa que j� est� cadastrada no sistema.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
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
		dadosPessoaisMBean.setOrdemBotoes(false); // Significa que est� alterando
		return forward(DadosPessoaisMBean.JSP_FORM);
	}
	
	
	
	
	
	/**
	 * <p>O MBean dados pessoais vai retorna a pessoa por meio desse m�todo.</p>
	 * 
	 * @see br.ufrn.sigaa.jsf.OperadorDadosPessoais#setDadosPessoais(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	public void setDadosPessoais(Pessoa pessoa){
		this.pessoa = pessoa;
	}
	
	
	
	/**
	 * <p>M�todo que � chamado pelo DadosPessoaisMBean depois que a pessoa � cadastrada para retornar 
	 * o caso de uso os fluxo normal do cadastro de um usu�rio externo </p> .
	 * 
	 * 
	 * @see br.ufrn.sigaa.jsf.OperadorDadosPessoais#submeterDadosPessoais()
	 * <br><br>
	 * M�todo n�o � chamado por nenhuma JSP.<br>
	 * Chamado por {@link DadosPessoaisMBean#submeterDadosPessoais()}
	 */
	public String submeterDadosPessoais() {
		
		
		UsuarioExternoBibliotecaDao daoUsuarioExterno = null;
		try {
			
			
			daoUsuarioExterno = getDAO(UsuarioExternoBibliotecaDao.class);
			
			/*
			 *  Atualizou os dados da pessoa, em outras palavras pessoa j� existe no sistema. 
			 */
			if(pessoa.getId() > 0){ 
				return continuaCadastroUsuarioExterno();
			}else{
			
				 /*
                  *  Pessoa n�o existe no sistema
				  *  Verifica se j� existe outra pessoa com o mesmo n�mero de passaporte.  <br/>
				  *  
				  *  Se sim, redireciona para o usu�rio escolher se se trata do mesmo usu�rio ou n�o,
				  *  j� que n�o podemos verificar a unicidade do passaporte  
				  */
				if(pessoa.isInternacional()){ 
					pessoasComPassaporteDuplicados  = (List<Pessoa>) daoUsuarioExterno.findByExactField(Pessoa.class, "passaporte", pessoa.getPassaporte());
					
					if(pessoasComPassaporteDuplicados.size() <= 0){     // N�o Existem pessoas cadastradas com o passaporte informado
						voltaPaginaSelecionaPessoaExtrageiraExistente = false;
						return continuaCadastroUsuarioExterno();
					}else{
						voltaPaginaSelecionaPessoaExtrageiraExistente = true;
						return verificaCadastroPessoasExtrageiras();
					}
					
				}else{  // Para pessoas nacionais, n�o tem problema, pois h� verifica��o de duplicidade do CPF
					
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
	 *   Caso o usu�rio digite um passaporte que j� existe no sitema, mostra os outros usu�rio com o mesmo passaporte para confirma��o
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @return
	 */
	private String verificaCadastroPessoasExtrageiras(){
		
		return forward(PAGINA_FORM_VERIFICA_PESSOA_EXTRAGEIRA);
	}
	
	
	/**
	 *   <p>M�todo respons�vel por verifica se o usu�rio j� possui <strong>usu�rio externo biblioteca</strong> ou <strong>usu�rio biblioteca</strong>. </p>
	 *   
	 *   <p>Caso possua qualquer um dos dois, esse m�todo deve garantir que as informa��es deles sejam apenas <strong>atualizadas</strong>, para
	 *   n�o gerar duplicidade no cadastro na biblioteca.</p>
	 *  
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
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
				addMensagemErro("Usu�rio possui cadastro duplicado no sistema, por favor entre em contado com o suporte para desativar um cadastro.");
				return null;
			}
				
			if(usuarios.size() == 0){  // N�o possui UsuarioBiblioteca cadastrado ainda, ou todos est�o quitados
				
				obj.setUsuarioBiblioteca(new UsuarioBiblioteca(pessoa, VinculoUsuarioBiblioteca.USUARIO_EXTERNO, 0));
				setConfirmButton("Cadastrar novo Usu�rio Externo");    // Pode realizar o cadastro no novo usu�rio
				
			}else{ // J� possui usu�rio biblioteca cadastrado
				
				UsuarioBiblioteca usuarioNaoQuitado = usuarios.get(0);
				
				if(usuarioNaoQuitado.getVinculo() == VinculoUsuarioBiblioteca.USUARIO_EXTERNO){ // O n�o quitado � um v�nculo de usu�rio externo, ent�o atualiza esse cadastro
					
					List<UsuarioExternoBiblioteca> usuariosExt =  daoUsuarioExterno.findAtivoNaoQuitadoByUsuarioBiblioteca(usuarioNaoQuitado.getId());
					
					// S� uma verifica��o, o sistema n�o deveria deixar ocorrer isso
					if(usuariosExt.size() > 1){
						addMensagemErro("Usu�rio possui cadastro de usu�rios externos duplicados no sistema, por favor entre em contado com o suporte para desativar um cadastro.");
						return null;
					}
					
					if(usuariosExt.size() == 0){ // Outra situa��o que n�o pode ocorrer, o usu�ri tem v�nculo de usu�rio externo, mas n�o tem as informa��es do usu�rio externo 
						
						addMensagemErro("N�o foi poss�vel recuperar as informa��es do v�nculo do usu�rio externo, quite o v�nculo atual para poder cadastra um novo v�nculo e resolver o problema.");
						return null;
						
					}else{  // O normal de ocorrer, o usu�rio biblioteca com o  v�nculo de usu�rio externo deve possuir um usu�rio externo
						
						obj = usuariosExt.get(0);
						
						if(obj.getUsuarioBiblioteca().getPessoa().getId() == pessoa.getId()) // Atualiza as informa��es da pessoa
							obj.getUsuarioBiblioteca().setPessoa(pessoa);
						
						setConfirmButton("Alterar Dados do Usu�rio Externo");
					}
					
				}else{ // J� tem uma conta na biblioteca n�o quitada, como n�o pode ter 2, tem que quitar a anterior para fazer o cadastro como usu�rio externo 
					addMensagemErro("Usu�rio est� cadastrado na biblioteca com o v�nculo de "+usuarioNaoQuitado.getVinculo().getDescricao()+" � preciso primeiro quitar esse v�nculo para realizar o cadastro dele como usu�rio externo. ");
					return null;
				}
				
			}
				
			if(obj.isCancelado()){
				addMensagemWarning("O v�nculo de usu�rio externo est� cancelado, atualizando as suas informa��es ele ser� ativado novamente.");
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
	 * Executa a a��o do bot�o foltar no formul�rio de cadastro das informa��es de um usu�rio externo
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Se na p�gina que o usu�rio escolhe entre as pessoas castradas com mesmo passaporte ele revolve voltar para a p�gina de cadastro.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/UsuarioExternoBiblioteca/formVerificaPessoaExtrageira.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String voltarFormularioPessoaExtrageira() throws SegurancaException, DAOException{
		// retorna para a p�gina do formul�rio de pessoas
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
	 * Se na p�gina que o usu�rio escolhe entre as pessoas castradas com mesmo passaporte ele revolve incluir os dados de uma nova pessoa, com o mesmo passaporte.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Se na p�gina que o usu�rio escolhe entre as pessoas castradas com mesmo passaporte ele revolve criar o v�nculo para uma pessoa que j� existia no sistema com o mesmo passaporte
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Exibe o formul�rio para se cancelar o v�nculo de um usu�rio externo. <br/>
	 * M�todo chamado pela seguinte JSP: /biblioteca/resultadoBuscaUsuarioBiblioteca.jsp <br/>
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
				addMensagemErro(" Existe mais de um v�nculo de usu�rio externo para o mesmo usu�rio, por favor entre em contado com o suporta para os outros v�nculo serem cancelados. ");
				return null;
			}
			
			if(listaTemp.size() == 0){
				addMensagemErro(" V�nculo do usu�rio externo n�o encontado.");
				return null;
			}
			
			obj = listaTemp.get(0);
			
			if(obj.isCanceladoPorPrazo())
				addMensagemErro("O v�nculo de usu�rio externo expirou.");
			
			if(obj.isCanceladoManualmenente())
				addMensagemErro("O v�nculo de usu�rio externo foi cancelado.");
			
			prepareMovimento(ArqListaComando.ALTERAR);
			
			return forward (PAGINA_FORM_CANCELAR_VINCULO);
			
		}finally {
			if (dao != null)
				dao.close();
		}
	}
	
	
	
	/**
	 * Cancela o v�nculo do usu�rio externo, fazendo com que ele n�o possa mais realizar empr�stimos.
	 * M�todo chamado pela seguinte JSP: /biblioteca/UsuarioExternoBiblioteca/formCancelar.jsp
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
	 * Cadastra o v�nculo do usu�rio externo.
	 * 
	 * M�todo chamado pela seguinte JSP: /biblioteca/UsuarioExterno/form.jsp
	 * 
	 */
	@Override
	public String cadastrar () throws ArqException {
		
		if (!senha.equals(confirmarSenha)){
			addMensagemErro("A confirma��o da senha n�o confere com a senha digitada.");
			return null;
		}
		
		String mensagemUsuario = "";
		
		if(obj.getId() == 0){
			
			if(obj.getUsuarioBiblioteca().getId() == 0)
				mensagemUsuario = "Cadastro para utiliza��o das Bibliotecas como Usu�rio Externo realizado com sucesso.";
			else	
				mensagemUsuario = "V�nculo de Usu�rio Externo cadastrado com sucesso.";
		}else
			mensagemUsuario = "Informa��es do Usu�rio Externo atualizadas com sucesso.";
		
		try {
			
			// Importante: Se o usu�rio digitou alguma coisa no campo senha, tem que atualizar a senha
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