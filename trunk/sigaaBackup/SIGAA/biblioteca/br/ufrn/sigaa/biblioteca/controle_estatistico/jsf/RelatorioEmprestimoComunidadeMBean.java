/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 30/11/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioPorComunidadeDao;

/**
 * Managed Bean � respons�vel pela gera��o do relat�rio dos empr�stimos realizados as comunidades interna
 * e externa a Institui��o.
 * 
 * @Autor: Jean Guerethes
 */
@Component("relatorioEmprestimoComunidadeMBean")
@Scope("request")
public class RelatorioEmprestimoComunidadeMBean extends AbstractRelatorioBibliotecaMBean{

	/**
	 * A p�gina do filtro do relat�rio
	 */
	private static final String RELATORIO_EMPRESTIMO_COMUNIDADE = "/biblioteca/controle_estatistico/relatorioEmprestimoComunidade.jsp";	
	
	/**
	 * Guarda os resulatdos da consulta.
	 * 
	 * Ma forma:  [Nome da Biblioteca], [quantidade Interna], [quantidade Externa] >
	 */
	private List<Object[]> lista = new ArrayList<Object[]>();
	
	public RelatorioEmprestimoComunidadeMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relat�rio de empr�stimos por comunidade");
		
		setDescricao(
				"<p>Neste relat�rio � poss�vel consultar a quantidade de empr�stimos realizados " +
				"pelos usu�rios internos e externos da "+RepositorioDadosInstitucionais.getSiglaInstituicao()+".</p>"+
				"<p>Os dados dos relat�rio s�o mostrados <strong>agrupados pela biblioteca</strong> do material que foi emprestado.</p>");
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -1); // Por padr�o recupera o empr�stimos do �ltimo m�s
		
		setFiltradoPorPeriodo(true);
	}

	/**
	 * Esse m�todo � respons�vel pela gera��o do relat�rio dos empr�stimos realizados para as comunidades
	 * interna e externa a Institui��o.
	 * Tendo como par�metro de entrada o per�odo Inicial e Final.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/biblioteca/controle_estatistico/formGeral.jsp
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioPorComunidadeDao dao = getDAO(RelatorioPorComunidadeDao.class);
		configuraDaoRelatorio(dao);
	
		dao = getDAO(RelatorioPorComunidadeDao.class);
		
		lista = dao.findEmprestimoComunidade(inicioPeriodo, fimPeriodo);
		
		if (lista.size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward( RELATORIO_EMPRESTIMO_COMUNIDADE );
	}

	

	public List<Object[]> getLista() {
		return lista;
	}

	public void setLista(List<Object[]> lista) {
		this.lista = lista;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
