/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 18/04/2011
 *
 */
package br.ufrn.sigaa.complexohospitalar.relatorios.jsf;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.complexohospitalar.relatorios.dao.RelatorioAlunosCadastradosResidenciaDao;
import br.ufrn.sigaa.complexohospitalar.relatorios.dominio.RelatorioAlunosCadastradosResidencia;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean responsável por gerar o relatório de alunos cadastrados nos programas de residência médica 
 * @author arlindo
 *
 */
@Component("relatorioAlunosCadastradosResidenciaMBean") @Scope("request")
public class RelatorioAlunosCadastradosResidenciaMBean extends SigaaAbstractController<RelatorioAlunosCadastradosResidencia> {
	
	/** Listagem contendo o resultado da consulta*/
	private List<RelatorioAlunosCadastradosResidencia> listagem = new ArrayList<RelatorioAlunosCadastradosResidencia>();
	
	/** Lista de discentes do programa selecionado */
	private List<Discente> discentes = new ArrayList<Discente>();

	/** Construtor padrão */
	public RelatorioAlunosCadastradosResidenciaMBean() {
		obj = new RelatorioAlunosCadastradosResidencia();
	}
	
	/**
	 * Inicia o relatório de alunos cadastrados
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/complexo_hospitalar/index.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciar() throws HibernateException, DAOException{
		
		RelatorioAlunosCadastradosResidenciaDao dao = getDAO(RelatorioAlunosCadastradosResidenciaDao.class);
		try {
			listagem = dao.findAlunosCadastrados();
			
			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/complexo_hospitalar/relatorios/alunos_cadastrados/lista.jsp");
	}
	
	/**
	 * Detalha os alunos do programa selecionado
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/complexo_hospitalar/relatorios/alunos_cadastrados/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String detalhar() throws HibernateException, DAOException{
		
		int id = getParameterInt("idUnidade", 0);
		obj.setUnidade(new Unidade(id));
			
		RelatorioAlunosCadastradosResidenciaDao dao = getDAO(RelatorioAlunosCadastradosResidenciaDao.class);
		try {
			discentes = dao.findDiscentesByPrograma(id);

			if (id > 0){
				Unidade u = dao.findByPrimaryKey(id, Unidade.class, "id", "nome");
				obj.setUnidade(u);				
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}		
		
		return forward("/complexo_hospitalar/relatorios/alunos_cadastrados/detalhe.jsp");
	}

	public List<RelatorioAlunosCadastradosResidencia> getListagem() {
		return listagem;
	}

	public void setListagem(List<RelatorioAlunosCadastradosResidencia> listagem) {
		this.listagem = listagem;
	}

	public List<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(List<Discente> discentes) {
		this.discentes = discentes;
	}
	
}
