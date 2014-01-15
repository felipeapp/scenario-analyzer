/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean que possibilita a um bibliotecário desfazer a quitação de um vínculo</p>
 *
 * <p> <i> Usado nos casos em que o usuário realizou a quitação do vínculo, mas por algum motivo não 
 * conseguiu se formar e deseja continuar fazendo novos empréstimos na biblioteca </i> </p>
 * 
 * @author jadson
 *
 */
@Component("desfazQuitacaoVinculoUsuarioBibliotecaMBean")
@Scope("request")
public class DesfazQuitacaoVinculoUsuarioBibliotecaMBean  extends SigaaAbstractController<UsuarioBiblioteca> implements PesquisarUsuarioBiblioteca{

	/**
	 * A página do caso de uso 
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
	 * As contas que o usuário teve durante a sua vida acadêmica, para ativar alguma é preciso que todas estejam quitadas.
	 */
	private List<UsuarioBiblioteca> contasUsuarioBiblioteca;
	
	/**
	 * Para mostrar na página as informações do usuário
	 */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	/**
	 * Indica se todos os vínculos do usuário estão quitados, caso algum não esteja, não pode habilitar a opção de retirar a quitação de um vínculo.
	 */
	private boolean todosVinculosQuitados;
	
	
	/**
	 *  Inicia o caso de uso de bloquear usuários da biblioteca carregando as suas informações e 
	 *  redirecionando para a página na qual o operador vai confirmar o bloquea o desbloquear. 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, true, true, "Desfazer Quitação dos Usuários", null);
	}
	
	
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		this.biblioteca = null;
		this.pessoa = p;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		this.pessoa = null;
		this.biblioteca = biblioteca;
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
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		carregaVinculosQuitadosUsuario();
		return telaDesfazQuitacaoUsuarioBiblioteca();
	}

	
	/**
	 *  Carrega os vínculos do usuário, para desfazer a quitação de algum.
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
				addMensagemWarning("O usuário já possui um vínculo ativo, por isso não é possível ativar outro.");
			}
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		}finally{
			
			if(dao != null ) dao.close();
		}
		
	}
	
	
	/**
	 * <p>Realiza a ação de retirar a quitação do vínculo selecionado pelo usuário.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
							addMensagemErro("A quitação do vínculo selecionado não pode ser desfeita porque ele não está mais ativo." );
							return null;
						}
					}
				}
			}
		
			if(contaSelecionado == null){
				addMensagemErro("Seleciona um vínculo do usuário" );
				return null;
			}
				
		
		
			MovimentoDesfazQuitacaoUsuarioBiblioteca mov 
				= new MovimentoDesfazQuitacaoUsuarioBiblioteca(contasUsuarioBiblioteca, contaSelecionado);
		
		
			execute(mov);
			addMensagemInformation("Vínculo de "+contaSelecionado.getVinculo().getDescricao()+" para o usuário "+infoUsuario.getNomeUsuario()+" ativado com sucesso.");
			
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
	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
