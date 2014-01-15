/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Managed Bean é responsável pela geração do relatório dos empréstimos realizados as comunidades interna
 * e externa a Instituição.
 * 
 * @Autor: Jean Guerethes
 */
@Component("relatorioEmprestimoComunidadeMBean")
@Scope("request")
public class RelatorioEmprestimoComunidadeMBean extends AbstractRelatorioBibliotecaMBean{

	/**
	 * A página do filtro do relatório
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
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório de empréstimos por comunidade");
		
		setDescricao(
				"<p>Neste relatório é possível consultar a quantidade de empréstimos realizados " +
				"pelos usuários internos e externos da "+RepositorioDadosInstitucionais.getSiglaInstituicao()+".</p>"+
				"<p>Os dados dos relatório são mostrados <strong>agrupados pela biblioteca</strong> do material que foi emprestado.</p>");
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -1); // Por padrão recupera o empréstimos do último mês
		
		setFiltradoPorPeriodo(true);
	}

	/**
	 * Esse método é responsável pela geração do relatório dos empréstimos realizados para as comunidades
	 * interna e externa a Instituição.
	 * Tendo como parâmetro de entrada o período Inicial e Final.
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
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
