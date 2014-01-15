/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioDocenteSqlDao;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * MBean responsável pelo controle das páginas de relatórios destinados ao docente
 * @author Eric Moura
 *
 */
public class RelatorioDocenteMBean extends AbstractRelatorioGraduacaoMBean {

	/** Lista de docentes referentes ao relatório. */
	ArrayList<Map<String,Object>> listaDocentes = new ArrayList<Map<String,Object>>();

	//Constantes de navegação
	/** Link da lista de docentes. */
	private static final String JSP_RELATORIO_LISTA = "lista_docente.jsp";
	/** Link do relatório quantitativo. */
	private static final String JSP_RELATORIO_QUANTITATIVO= "lista_quantitativo.jsp";
	/** Link da lista de disciplinas ministradas por um docente em um determinado ano e semestre. */
	private static final String JSP_RELATORIO_DISCIPLINA_ANOSEMESTRE = "lista_disciplina_anosemestre.jsp";
	/** Contexto das páginas. */
	private static final String CONTEXTO ="/graduacao/relatorios/docente/";

	public RelatorioDocenteMBean(){
		initObj();
	}

	@Override
	public void carregarTituloRelatorio() throws DAOException{
		RelatorioDocenteSqlDao dao =getDAO(RelatorioDocenteSqlDao.class);
		
		try{
			if(centro!=null && centro.getId()!=0 )
				centro = dao.findByPrimaryKey(centro.getId(), Unidade.class);
			else if(departamento!=null && departamento.getId() != 0 )
				departamento = dao.findByPrimaryKey(departamento.getId(), Unidade.class);
			else{
				centro.setNome("TODOS");
				departamento.setNome("TODOS");
				departamento.setGestora(new Unidade());
				departamento.getGestora().setNome("TODOS");
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Método que repassa pra view o resultado da consulta da lista de docentes.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/docente/seleciona_lista_docente.jsp</li>
	 * <li>/sigaa.war/graduacao/departamento.jsp</li>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioLista() throws DAOException{
		carregarTituloRelatorio();
		RelatorioDocenteSqlDao dao =getDAO(RelatorioDocenteSqlDao.class);
		
		try {
			if( isUserInRole( SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.CHEFE_DEPARTAMENTO ) ){
				listaDocentes = (ArrayList<Map<String, Object>>) dao.findListaDocenteDepartamento(null, getUsuarioLogado().getVinculoAtivo().getUnidade());
			}else{
				listaDocentes = (ArrayList<Map<String, Object>>) dao.findListaDocenteDepartamento(centro, departamento);
			}
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+JSP_RELATORIO_LISTA);

	}

	/**
	 * Método que repassa pra view o resultado da consulta da quantitativos de docentes por departamento.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/docente/seleciona_lista_quantitativo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativo() throws DAOException{
		carregarTituloRelatorio();
		RelatorioDocenteSqlDao dao =getDAO(RelatorioDocenteSqlDao.class);
		
		try {
			listaDocentes = (ArrayList<Map<String, Object>>) dao.findQuantitativoDocenteDepartamento(departamento);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+JSP_RELATORIO_QUANTITATIVO);
	}

	/**
	 * Método que repassa pra view o resultado da consulta da lista disciplinas de docentes por departamento/ano semestre.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/docente/seleciona_lista_disciplina_anosemestre.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioDisciplinaAnoSemestre() throws DAOException{
		carregarTituloRelatorio();
		RelatorioDocenteSqlDao dao =getDAO(RelatorioDocenteSqlDao.class);
		
		try {
			listaDocentes =  (ArrayList<Map<String, Object>>) dao.findDisciplinaDocenteAnoSemestre(departamento, ano, periodo);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+JSP_RELATORIO_DISCIPLINA_ANOSEMESTRE);
	}

	@Override
	public Unidade getCentro() {
		return centro;
	}

	@Override
	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	@Override
	public Unidade getDepartamento() {
		return departamento;
	}

	@Override
	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	public List<Map<String, Object>> getListaDocentes() {
		return listaDocentes;
	}

	/**
	 * Retorna o tamanho da Lista.
	 * 
	 * @return 
	 */
	public int getNumeroListaDocentes() {
		return listaDocentes.size();
	}

	public void setListaDocentes(List<Map<String, Object>> listaDocentes) {
		this.listaDocentes = (ArrayList<Map<String, Object>>) listaDocentes;
	}
}
