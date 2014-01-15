/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 25/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dao.BloqueioUsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.BloqueiosUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p> MBean que gerencia o caso de uso de bloquear e desbloquear um usu�rio da biblioteca. </p>
 *
 * <p> <i> ( O bloqueio de um usu�rio � um caso raro e ocorre o bloqueio da pessoa no sistema da biblioteca, j� 
 * que o sistema vai passar a ter o conceito de usu�rio "quitado". Que era mais ou menos o que o bloqueio tentava fazer anteriormente ).
 * <br/><br/>
 * � para ser utilizado quando, por exemplo, um usu�rio foi pego robando um material e como puni��o n�o vai poder realizar mais empr�stimos 
 * por resto da vida, mesmo que adquira um novo v�nculo com a institui��o futuramente.</i> </p>
 * 
 * @author jadson
 * @version 2.0. 19/04/2011 Bloqueio vai bloquear a pessoa ou biblioteca. J� que a pessoa vai passar a ter v�rios contas de usu�rios biblioteca 
 * no sistema, mas apenas 1 n�o quitado por vez.
 *
 */
@Component("bloquearUsuarioBibliotecaMBean")
@Scope("request")
public class BloquearUsuarioBibliotecaMBean extends SigaaAbstractController<UsuarioBiblioteca> implements PesquisarUsuarioBiblioteca{

	/**
	 * P�gina que bloqueia ou desbloqueia o usu�rio
	 */
	public static final String PAGINA_BLOQUEAR_USUARIO_BIBLIOTECA = "/biblioteca/circulacao/paginaBloquearUsuario.jsp";
	
	/** Apenas para mostrar as informa��es completas do usu�rio selecionado */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	
	/** Guarda se o usu�rio est� bloqueado ou n�o */
	private boolean usuarioEstaBloqueado = false; 
	
	/** Usado porque o usu�rio s� pode utilizar o bot�o voltar se vier da busca do usu�rio.*/
	private boolean desabilitaBotaoVoltar = false;
	
	/** Guarda o motivo do bloqueio informado pelo usu�rio que vai para o hist�rico de bloqueios do usu�rio */
	private String motivoBloqueio;
	
	
	/** Guarda o hist�rico de bloqueios do usu�rio para mostrar ao operador na tela */
	private List<BloqueiosUsuarioBiblioteca> bloqueiosUsuario;
	
	
	/**
	 * Pessoa retornada pela busca padr�o do sistema, para ser bloqueada
	 */
	private Pessoa pessoaRetornadaBusca;
	
	
	/**
	 * Biblioteca retornada pela busca padr�o do sistema, para ser bloqueada
	 */
	private Biblioteca bibliotecaRetornadaBusca;
	
	
	
	
	/**
	 *  <p>Inicia o caso de uso de bloquear usu�rios da biblioteca carregando as suas informa��es e 
	 *  redirecionando para a p�gina na qual o operador vai confirmar o bloquea ou desbloquear.</p> 
	 *  
	 *  <p> Essa opera��o deve poder ser realizada mesmo que o usu�rio n�o tenha conta da biblioteca, pois o 
	 *  bloqueio � por pessoa e caso ele quita todos os seus vinculos n�o teria como desbloquea-lo.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarBloqueio() throws ArqException{
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, false, true, "Bloquear Usu�rios", null);
	}
	
	
	/**
	 *  <p>Inicia o caso de uso de bloquear usu�rios da biblioteca carregando as suas informa��es e 
	 *  redirecionando para a p�gina na qual o operador vai confirmar o bloquea ou desbloquear.</p> 
	 *  
	 *  <p> Essa opera��o deve poder ser realizada mesmo que o usu�rio n�o tenha conta da biblioteca, pois o 
	 *  bloqueio � por pessoa e caso ele quita todos os seus vinculos n�o teria como desbloquea-lo.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/informacao_referencia.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarBloqueioBibliotecas() throws ArqException{
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, false, false, true, true, "Bloquear Usu�rios", null);
	}
	
	
	
	///////////////////////// M�todos da interface  de busca //////////////////////


	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		
		
		BloqueioUsuarioBibliotecaDao dao = null;
		
		try {
			
			dao = getDAO(BloqueioUsuarioBibliotecaDao.class);
			
			Integer idPessoaBusca = pessoaRetornadaBusca != null ? pessoaRetornadaBusca.getId() : null;
			Integer idBibliotecaBusca =  bibliotecaRetornadaBusca != null ? bibliotecaRetornadaBusca.getId() : null;
			
			if(idPessoaBusca == null && idBibliotecaBusca == null){
				addMensagemErro("Selecione o usu�rio corretamente");
				return null;
			}
			
			if(idPessoaBusca != null && idBibliotecaBusca != null){
				addMensagemErro("S� � poss�vel bloquear um tipo de usu�rio por vez.");
				return null;
			}
			
			infoUsuario= new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario( null, idPessoaBusca, idBibliotecaBusca);		
			
			try {
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioBloqueado(idPessoaBusca, idBibliotecaBusca);
				usuarioEstaBloqueado = false;
			} catch (NegocioException ne) {
				usuarioEstaBloqueado = true;
			}
			
			if(idPessoaBusca != null)
				bloqueiosUsuario =  dao.findBloqueiosBibliotecaByPessoa(pessoaRetornadaBusca.getId());
			else
				bloqueiosUsuario =  dao.findBloqueiosBibliotecaByBiblioteca(bibliotecaRetornadaBusca.getId());
			
			prepareMovimento(ArqListaComando.ALTERAR); 
			
			return telaBloquearUsuario();
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}finally{
			if(dao != null) dao.close();
		}
		
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		bibliotecaRetornadaBusca = biblioteca;
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametos) {
		
	}


	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		pessoaRetornadaBusca = p;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Volta a a tela de busca dos usu�rios da biblitoteca
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/paginaBloquearUsuario.jsp</li>
	 *   </ul>
	 * 
	 */
	public String voltarParaBuscaUsuarioBiblioteca() {
		return ((BuscaUsuarioBibliotecaMBean) getMBean("buscaUsuarioBibliotecaMBean")).telaBuscaUsuarioBiblioteca();
	}
	
	
	/**
	 *  M�todo que realiza a a��o de bloquear e desbloquear um usu�rio da biblioteca para a realiza��o de empr�timos, 
	 *  alterando o valor da vari�vel <code>bloqueado</code> da classe <code>UsuarioBiblioteca</code>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/paginaBloquearUsuario.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String bloqueiar() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO);
		
		try {
			
			BloqueiosUsuarioBiblioteca bloqueio = null;
			
			if( bibliotecaRetornadaBusca != null && bibliotecaRetornadaBusca.getId() > 0 ) {
				bloqueio = new BloqueiosUsuarioBiblioteca( (usuarioEstaBloqueado ? BloqueiosUsuarioBiblioteca.DESBLOQUEIO : BloqueiosUsuarioBiblioteca.BLOQUEIO),
						motivoBloqueio, new Date(), getUsuarioLogado(), bibliotecaRetornadaBusca );
			}else{
				bloqueio = new BloqueiosUsuarioBiblioteca( (usuarioEstaBloqueado ? BloqueiosUsuarioBiblioteca.DESBLOQUEIO : BloqueiosUsuarioBiblioteca.BLOQUEIO),
						motivoBloqueio, new Date(), getUsuarioLogado(),  pessoaRetornadaBusca);
			}
			
			if(bloqueio.validate().isErrorPresent()){
				for (MensagemAviso erro : bloqueio.validate().getErrorMessages()) {
					addMensagemErro(erro.getMensagem());
				}
				return null;
			} 
			
			
			////////  cria um bloqueio ou desbloqueio para o usu�rio /////////
			prepareMovimento(ArqListaComando.CADASTRAR); 
			
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setObjMovimentado(bloqueio);
			movimento.setCodMovimento(ArqListaComando.CADASTRAR);
			
			execute(movimento);
			///////////////////////////////////////////////////
			
			
			if(! usuarioEstaBloqueado)
				addMensagemInformation("Usu�rio bloqueado com sucesso ! ");
			else
				addMensagemInformation("Usu�rio desbloqueado com sucesso ! ");
			
			return cancelar();
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			return null;
		} 
		
	}
	
	
	
	
	
	
	/**
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @return
	 */
	public String telaBloquearUsuario(){
		return forward(PAGINA_BLOQUEAR_USUARIO_BIBLIOTECA);
	}
	
	
	
	///// sets e gets ////
	
	public InformacoesUsuarioBiblioteca getInfoUsuario() {
		return infoUsuario;
	}

	public void setInfoUsuario(InformacoesUsuarioBiblioteca infoUsuario) {
		this.infoUsuario = infoUsuario;
	}


	public boolean isDesabilitaBotaoVoltar() {
		return desabilitaBotaoVoltar;
	}


	public void setDesabilitaBotaoVoltar(boolean desabilitaBotaoVoltar) {
		this.desabilitaBotaoVoltar = desabilitaBotaoVoltar;
	}

	public String getMotivoBloqueio() {
		return motivoBloqueio;
	}

	public void setMotivoBloqueio(String motivoBloqueio) {
		this.motivoBloqueio = motivoBloqueio;
	}


	public boolean isUsuarioEstaBloqueado() {
		return usuarioEstaBloqueado;
	}

	public void setUsuarioEstaBloqueado(boolean usuarioEstaBloqueado) {
		this.usuarioEstaBloqueado = usuarioEstaBloqueado;
	}


	public List<BloqueiosUsuarioBiblioteca> getBloqueiosUsuario() {
		return bloqueiosUsuario;
	}

	public void setBloqueiosUsuario(List<BloqueiosUsuarioBiblioteca> bloqueiosUsuario) {
		this.bloqueiosUsuario = bloqueiosUsuario;
	}
	
}


