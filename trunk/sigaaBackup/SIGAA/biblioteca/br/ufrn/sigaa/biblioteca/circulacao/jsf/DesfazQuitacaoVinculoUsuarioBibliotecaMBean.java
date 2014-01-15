/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 25/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoDesfazQuitacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>MBean que possibilita a um bibliotec�rio desfazer a quita��o de um v�nculo</p>
 *
 * <p> <i> Usado nos casos em que o usu�rio realizou a quita��o do v�nculo, mas por algum motivo n�o 
 * conseguiu se formar e deseja continuar fazendo novos empr�stimos na biblioteca </i> </p>
 * 
 * @author jadson
 *
 */
@Component("desfazQuitacaoVinculoUsuarioBibliotecaMBean")
@Scope("request")
public class DesfazQuitacaoVinculoUsuarioBibliotecaMBean  extends SigaaAbstractController<UsuarioBiblioteca> implements PesquisarUsuarioBiblioteca{

	/**
	 * A p�gina do caso de uso 
	 */
	public static final String PAGINA_DESFAZ_QUITACAO_USUARIO_BIBLIOTECA = "/biblioteca/circulacao/paginaDesfazQuitacaoUsuarioBiblioteca.jsp";
	
	/**
	 * A pessoa cuja conta vai ser reativada
	 */
	private Pessoa pessoa;
	
	/**
	 * A biblioteca cuja conta vai ser reativada
	 */
	private Biblioteca biblioteca;
	
	
	/**
	 * As contas que o usu�rio teve durante a sua vida acad�mica, para ativar alguma � preciso que todas estejam quitadas.
	 */
	private List<UsuarioBiblioteca> contasUsuarioBiblioteca;
	
	/**
	 * Para mostrar na p�gina as informa��es do usu�rio
	 */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	/**
	 * Indica se todos os v�nculos do usu�rio est�o quitados, caso algum n�o esteja, n�o pode habilitar a op��o de retirar a quita��o de um v�nculo.
	 */
	private boolean todosVinculosQuitados;
	
	
	/**
	 *  Inicia o caso de uso de bloquear usu�rios da biblioteca carregando as suas informa��es e 
	 *  redirecionando para a p�gina na qual o operador vai confirmar o bloquea o desbloquear. 
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
	public String iniciar() throws ArqException{
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, true, true, "Desfazer Quita��o dos Usu�rios", null);
	}
	
	
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		this.biblioteca = null;
		this.pessoa = p;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		this.pessoa = null;
		this.biblioteca = biblioteca;
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
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		carregaVinculosQuitadosUsuario();
		return telaDesfazQuitacaoUsuarioBiblioteca();
	}

	
	/**
	 *  Carrega os v�nculos do usu�rio, para desfazer a quita��o de algum.
	 * @throws ArqException 
	 */
	private void carregaVinculosQuitadosUsuario() throws ArqException{
		
		prepareMovimento(SigaaListaComando.DESFAZ_QUITACAO_BIBLIOTECA);
		
		UsuarioBibliotecaDao dao = null;
		
		try {
			dao = getDAO(UsuarioBibliotecaDao.class);
		
			if(pessoa != null){
				contasUsuarioBiblioteca = dao.findUsuarioBibliotecaAtivoByPessoa(pessoa.getId() );
				infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, pessoa.getId(), null);
			}
			
			if(biblioteca != null){
				contasUsuarioBiblioteca = dao.findUsuarioBibliotecaAtivoByBiblioteca(biblioteca.getId() );
				infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, null, biblioteca.getId());
			}
			
			todosVinculosQuitados = true;
			
			for (UsuarioBiblioteca usuario : contasUsuarioBiblioteca) {
				
				if(usuario.isQuitado() == false){
					todosVinculosQuitados = false;
					break;
				}	
			}
			
			if(! todosVinculosQuitados){
				addMensagemWarning("O usu�rio j� possui um v�nculo ativo, por isso n�o � poss�vel ativar outro.");
			}
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		}finally{
			
			if(dao != null ) dao.close();
		}
		
	}
	
	
	/**
	 * <p>Realiza a a��o de retirar a quita��o do v�nculo selecionado pelo usu�rio.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war//biblioteca/circulacao/paginaDesfazQuitacaoUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String retirarQuitacao() throws ArqException{
		
		int idContaSelecionada = getParameterInt("idContaUsuarioBibliotecaSelecionado");
		
		UsuarioBiblioteca contaSelecionado = null;
		
		try {
		
			for (UsuarioBiblioteca conta : contasUsuarioBiblioteca) {
				
				if(conta.getId() == idContaSelecionada){
					
					contaSelecionado = conta;
					
					if(! contaSelecionado.getVinculo().isVinculoBiblioteca()){
						ObtemVinculoUsuarioBibliotecaStrategy estrategia = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo();
						
						if(! estrategia.isVinculoAtivo(conta.getPessoa().getId(), conta.getVinculo(), conta.getIdentificacaoVinculo())){
							addMensagemErro("A quita��o do v�nculo selecionado n�o pode ser desfeita porque ele n�o est� mais ativo." );
							return null;
						}
					}
				}
			}
		
			if(contaSelecionado == null){
				addMensagemErro("Seleciona um v�nculo do usu�rio" );
				return null;
			}
				
		
		
			MovimentoDesfazQuitacaoUsuarioBiblioteca mov 
				= new MovimentoDesfazQuitacaoUsuarioBiblioteca(contasUsuarioBiblioteca, contaSelecionado);
		
		
			execute(mov);
			addMensagemInformation("V�nculo de "+contaSelecionado.getVinculo().getDescricao()+" para o usu�rio "+infoUsuario.getNomeUsuario()+" ativado com sucesso.");
			
			BuscaUsuarioBibliotecaMBean mbean = getMBean("buscaUsuarioBibliotecaMBean");
			return mbean.telaBuscaUsuarioBiblioteca();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	
	
	
	/**
	 *   Redireciona para a tela do caso de uso
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaDesfazQuitacaoUsuarioBiblioteca (){
		return forward(PAGINA_DESFAZ_QUITACAO_USUARIO_BIBLIOTECA);
	}


	//////////////////////// Sets e Gets ////////////////////////

	public List<UsuarioBiblioteca> getContasUsuarioBiblioteca() {
		return contasUsuarioBiblioteca;
	}

	public void setContasUsuarioBiblioteca(List<UsuarioBiblioteca> contasUsuarioBiblioteca) {
		this.contasUsuarioBiblioteca = contasUsuarioBiblioteca;
	}

	public InformacoesUsuarioBiblioteca getInfoUsuario() {
		return infoUsuario;
	}

	public void setInfoUsuario(InformacoesUsuarioBiblioteca infoUsuario) {
		this.infoUsuario = infoUsuario;
	}

	public boolean isTodosVinculosQuitados() {
		return todosVinculosQuitados;
	}

	public void setTodosVinculosQuitados(boolean todosVinculosQuitados) {
		this.todosVinculosQuitados = todosVinculosQuitados;
	}
	
	
	
}
