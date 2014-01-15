/*
 * ListaDevolucoesRecentesUsuarioMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software � confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * N�o se deve utilizar este produto em desacordo com as normas
 * da referida institui��o.
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
 *    MBean que gerencia a p�gina onde as devolu��es recentes de um determinado usu�rio
 * s�o mostradas. 
 * 
 * 		H� tamb�m a possibilidade de estornar qualquer uma dessas devolu��es, se o usu�rio for chefe da 
 * sess�o de circula��o.
 *
 * @author felipe
 * @since 30/01/2012
 * @version 1.0 Cria��o da classe
 *
 */
@Component("listaDevolucoesRecentesUsuarioMBean")
@Scope("request")
public class ListaDevolucoesRecentesUsuarioMBean extends SigaaAbstractController <Emprestimo> implements PesquisarUsuarioBiblioteca{

	/**
	 * P�gina que lista os empr�stimos ativo do usu�rio
	 */
	public static final String PAGINA_LISTA_DEVOLUCOES_RECENTES_USUARIO = "/biblioteca/circulacao/listaDevolucoesRecentes.jsp";
	
	/**
	 * Informa��es sobre o usu�rio selecionado mostrado na p�gina
	 */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	/**
	 * A lista de empr�stimos em aberto do usu�rio.
	 */
	private List<Emprestimo> devolucoesRecentes;
	
	/** O id do usu�rio biblioteca retornado pela busca padr�o do sistema */
	private int idUsuarioBibliotecaRetornadoBuscaPadrao;
	
	/**
	 *  Inica o caso de uso de lista empr�stimos de um usu�rio.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
		return pBean.iniciar(this, true, true, true, false, "Listar as Devolu��es recentes do Usu�rio", null);
	}
	
	
	///////////////////////// M�todos da interface  de busca //////////////////////
	


	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 *   <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
		
		
		/* ***** Caso usu�rio esteja listando os empr�timos para estornar algum **** */
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
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		
	}


	/**
	 *  Ver coment�rios da classe pai.<br/>
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
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////////
	

	
	
	
	
	/**
	 * Limpa os dados da sess�o e retorna para a p�gina de busca.
	 * <br/>M�todo chamado pela seguinte JSP: /biblioteca/circulacao/listaDevolucoesRecentes.jsp
	 * 
	 * @return
	 */
	public String  voltar (){
		return ((BuscaUsuarioBibliotecaMBean) getMBean("buscaUsuarioBibliotecaMBean")).telaBuscaUsuarioBiblioteca();
	}
	
	/**
	 * Exibe a listagem dos empr�stimos ativos do usu�rio.
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
