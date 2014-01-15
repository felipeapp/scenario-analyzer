/*
 * ListaDevolucoesRecentesUsuarioMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software é confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Não se deve utilizar este produto em desacordo com as normas
 * da referida instituição.
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.ConsultasEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *    MBean que gerencia a página onde as devoluções recentes de um determinado usuário
 * são mostradas. 
 * 
 * 		Há também a possibilidade de estornar qualquer uma dessas devoluções, se o usuário for chefe da 
 * sessão de circulação.
 *
 * @author felipe
 * @since 30/01/2012
 * @version 1.0 Criação da classe
 *
 */
@Component("listaDevolucoesRecentesUsuarioMBean")
@Scope("request")
public class ListaDevolucoesRecentesUsuarioMBean extends SigaaAbstractController <Emprestimo> implements PesquisarUsuarioBiblioteca{

	/**
	 * Página que lista os empréstimos ativo do usuário
	 */
	public static final String PAGINA_LISTA_DEVOLUCOES_RECENTES_USUARIO = "/biblioteca/circulacao/listaDevolucoesRecentes.jsp";
	
	/**
	 * Informações sobre o usuário selecionado mostrado na página
	 */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	/**
	 * A lista de empréstimos em aberto do usuário.
	 */
	private List<Emprestimo> devolucoesRecentes;
	
	/** O id do usuário biblioteca retornado pela busca padrão do sistema */
	private int idUsuarioBibliotecaRetornadoBuscaPadrao;
	
	/**
	 *  Inica o caso de uso de lista empréstimos de um usuário.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul>
	 * 
	 * @param infoUsuario
	 * @return
	 * @throws ArqException 
	 */
	public String iniciaListaDevolucoes() throws ArqException{
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, true, false, "Listar as Devoluções recentes do Usuário", null);
	}
	
	
	///////////////////////// Métodos da interface  de busca //////////////////////
	


	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 *   <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
		
		
		/* ***** Caso usuário esteja listando os emprétimos para estornar algum **** */
		prepareMovimento(SigaaListaComando.DESFAZ_OPERACAO); 
		
		EmprestimoDao emprestimoDAO = null;
		ConsultasEmprestimoDao daoConsultas = null;
		
		try {
			emprestimoDAO = getDAO(EmprestimoDao.class);
			daoConsultas = getDAO(ConsultasEmprestimoDao.class);
			
			UsuarioBiblioteca usuarioBiblioteca = daoConsultas.findInformacoesUsuarioBibliotecaNaoQuitado(idUsuarioBibliotecaRetornadoBuscaPadrao);
			infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo()
				.getInformacoesUsuario(usuarioBiblioteca, ( usuarioBiblioteca.getPessoa() != null ? usuarioBiblioteca.getPessoa().getId() : null)
						, (usuarioBiblioteca.getBiblioteca()!=  null ? usuarioBiblioteca.getBiblioteca().getId() : null)  );
			
			devolucoesRecentes = emprestimoDAO.findDevolucoesRecentesByUsuario(new UsuarioBiblioteca(idUsuarioBibliotecaRetornadoBuscaPadrao));
		
			return telaListaDevolucoesRecentesUsuario();
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		} finally {
			if (emprestimoDAO != null)emprestimoDAO.close();
			if (daoConsultas != null) daoConsultas.close();
		}
		
	}


	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		
	}


	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros) {
		
		// VER BuscaUsuarioBibliotecaMBean#selecionouUsuario()
		
		if(parametroDePessoa)
			idUsuarioBibliotecaRetornadoBuscaPadrao = Integer.parseInt(parametros[3]);
		else 
			idUsuarioBibliotecaRetornadoBuscaPadrao = Integer.parseInt(parametros[2]);
	}


	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////////
	

	
	
	
	
	/**
	 * Limpa os dados da sessão e retorna para a página de busca.
	 * <br/>Método chamado pela seguinte JSP: /biblioteca/circulacao/listaDevolucoesRecentes.jsp
	 * 
	 * @return
	 */
	public String  voltar (){
		return ((BuscaUsuarioBibliotecaMBean) getMBean("buscaUsuarioBibliotecaMBean")).telaBuscaUsuarioBiblioteca();
	}
	
	/**
	 * Exibe a listagem dos empréstimos ativos do usuário.
	 * @return
	 */
	private String telaListaDevolucoesRecentesUsuario(){
		return forward(PAGINA_LISTA_DEVOLUCOES_RECENTES_USUARIO);
	}
	
	// sets e gets
	
	public InformacoesUsuarioBiblioteca getInfoUsuario() {
		return infoUsuario;
	}


	public List<Emprestimo> getDevolucoesRecentes() {
		return devolucoesRecentes;
	}

	public void setDevolucoesRecentes(List<Emprestimo> devolucoesRecentes) {
		this.devolucoesRecentes = devolucoesRecentes;
	}

	
	
	
}
