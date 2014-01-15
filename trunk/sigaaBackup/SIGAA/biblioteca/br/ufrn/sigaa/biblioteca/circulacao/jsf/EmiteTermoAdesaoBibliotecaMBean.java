/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/05/2013
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dao.TermoAdesaoBibliotecaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TermoAdesaoSistemaBibliotecas;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Mbean que a listagem e gera��o da vers�o impressa do termo de ades�o ao sistema de Bibliotecas.
 *
 * @author Deyvyd
 * @version 2.0 - refactory para melhor o Mbean que estava fora dos padr�es.
 */

@Component("emiteTermoAdesaoBibliotecaMBean")
@Scope("request")
public class EmiteTermoAdesaoBibliotecaMBean extends SigaaAbstractController <Object> implements PesquisarUsuarioBiblioteca{

	/** JSP do relat�rio (Lista de usu�rios com cadastro de acordo com os filtros utilizados). */
	private static final String PAGINA_LISTA = "/biblioteca/circulacao/listaTermoAdesao.jsp";

	/** JSP do relat�rio (Termo de um usu�rio espec�fico, selecionado da lista de resultados). */
	private static final String PAGINA_TERMO = "/biblioteca/circulacao/visualizaTermoAdesao.jsp";
	
	/** A pessoa seleciona da busca da biblioteca. */
	protected Pessoa pessoa;
	
	/** A lista de termos assinados pelo usu�rio. */
	private List<TermoAdesaoSistemaBibliotecas> termosDeAdesaoAssinados;
	
	/** O termo que ades�o selecionado para imprimi-lo como um todo. */
	private TermoAdesaoSistemaBibliotecas termoDeAdesaoSelecionado;
	
	
	/**
	 * <p>1� M�todo que � chamado por onde se inicia todos os casos de usos de relat�rios no sistema.</p>
	 * 
	 * <p>Faz a configura��o inicial do relat�rio e redireciona para o formul�rio de filtros padr�o.</p>
	 * 
	 * <p>Este m�todo � chamado, indiretamente, por praticamente todos os relat�rios na p�gina.
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	public final String iniciar() throws DAOException, SegurancaException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
	
		
		// Sem empr�stimos para bibliotecas aqui.
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, false, true, "Emitir Termo de Ades�o ao Sistema de Bibliotecas", null);
	}
	

	/// telas de navega��o ///
	
	public String paginaLista() {
		return forward(PAGINA_LISTA);
	}

	public String paginaTermo() {
		return forward(PAGINA_TERMO);
	}

	////////////////////////////
	
	/**
	 * Imprime o termo selecionado pelo usu�ri por completo
	 * @return
	 *  
	 * <p> Criado em:  27/06/2013  </p>
	 *
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/</li>
	 * 		<li>/</li>
	 * </ul>
	 * 
	 * <strong>M�todo n�o chamado de nenhuma p�gina JSP</strong>.
	 *
	 *
	 */
	public String imprimirTermoImpressao(){
		int idTermoSelecionado = getParameterInt("idTermoAdesao");
		for (TermoAdesaoSistemaBibliotecas termo : termosDeAdesaoAssinados) {
			if(termo.getId() == idTermoSelecionado){
				this.termoDeAdesaoSelecionado = termo;
				break;
			}
		}
		
		return paginaTermo();
	}
	
	//// m�todos da pesquisa padr�o de usu�rios na biblioteca //
	
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		this.pessoa = p;
	}
	

	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		// n�o usado
	}


	@Override
	public void setParametrosExtra(boolean parametroDePessoa,String... parametros) {
		// n�o usado
	}

	

	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {

		TermoAdesaoBibliotecaDAO dao = null;
		
		try{
			dao = getDAO(TermoAdesaoBibliotecaDAO.class);
		
			termosDeAdesaoAssinados = dao.findTermoAdesaoAssinadoPelaPessoa(pessoa.getId());
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return paginaLista();
	}

	////////////////////////////////////////////////
	
	
	
	// gets e sets //
	
	public int getQtdTermosDeAdesaoAssinados() {
		return termosDeAdesaoAssinados == null ? 0 : termosDeAdesaoAssinados.size();
	}
	public List<TermoAdesaoSistemaBibliotecas> getTermosDeAdesaoAssinados() {
		return termosDeAdesaoAssinados;
	}
	public void setTermosDeAdesaoAssinados(List<TermoAdesaoSistemaBibliotecas> termosDeAdesaoAssinados) {
		this.termosDeAdesaoAssinados = termosDeAdesaoAssinados;
	}

	public TermoAdesaoSistemaBibliotecas getTermoDeAdesaoSelecionado() {
		return termoDeAdesaoSelecionado;
	}

	public void setTermoDeAdesaoSelecionado(TermoAdesaoSistemaBibliotecas termoDeAdesaoSelecionado) {
		this.termoDeAdesaoSelecionado = termoDeAdesaoSelecionado;
	}
	
}


