/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/03/2011
 */
package br.ufrn.sigaa.relatoriosgestao.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.PlanoCurso;
import br.ufrn.sigaa.relatoriosgestao.dao.RelatorioPlanosCursosDao;

/**
 * Este MBean tem como finalidade de auxiliar na geração do relatório de Planos de Cursos Cadastrados
 * 
 * @author arlindo
 *
 */
@Component("relatorioPlanosCursosCadastradosMBean") @Scope("request")
public class RelatorioPlanosCursosCadastradosMBean extends SigaaAbstractController<PlanoCurso>  {
	
	/** Lista com o resultado do relatório */
	private List<Map<String, Object>> listagem = new ArrayList<Map<String,Object>>();
	
	/** Lista com o detalhamento das informações */
	private List<Map<String, Object>> detalhamento = new ArrayList<Map<String,Object>>();
	
	/** Ano informado */
	private int ano;
	
	/** Periodo informado */
	private int periodo;
	
	/** Unidade selecionada */
	private Unidade unidade;
	
	/** Unidade selecionada */
	private Unidade gestora;	
	
	/**
	 * Inicia o relatório de Planos de Cursos Cadastrados
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/relatorios/abas/ensino.jsp</li/>
	 * </ul>
	 * @return
	 */
	public String iniciar(){
		ano = 0;
		periodo = 0;
		if (getCalendarioVigente() != null){
			ano = getCalendarioVigente().getAno();
			periodo = getCalendarioVigente().getPeriodo();			
		}		
		return forward("/portais/relatorios/planos_cursos_cadastrados/form.jsp");
	}
	
	/**
	 * Gera o relatório de Planos de Cursos Cadastrados
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/relatorios/planos_cursos_cadastrados/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws HibernateException, DAOException{
		
		if (ano <= 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		
		if (periodo <= 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");
		
		if (hasErrors())
			return null;
		
		RelatorioPlanosCursosDao dao = getDAO(RelatorioPlanosCursosDao.class);
		try {
			listagem = dao.findTotaisPlanoCursos(ano, periodo);
			
			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/portais/relatorios/planos_cursos_cadastrados/lista.jsp");
	}

	/**
	 * Detalha o relatório conforme a coluna que foi clicada
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/relatorios/planos_curso_cadastrados/lista.jsp</li/>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String detalhar() throws HibernateException, DAOException{
		
		int idUnidade = getParameterInt("idUnidade", 0);
		int idGestora = getParameterInt("idGestora", 0);		
		unidade = new Unidade(idUnidade);		
		gestora = new Unidade(idGestora);		
		
		boolean apenasCadastrados = getParameterBoolean("cadastrados");
		
		RelatorioPlanosCursosDao dao = getDAO(RelatorioPlanosCursosDao.class);
		try {
			detalhamento = dao.findTurmasByParametros(ano, periodo, unidade.getId(), gestora.getId(), apenasCadastrados);	
			
			if (!ValidatorUtil.isEmpty(detalhamento)){
				if (!ValidatorUtil.isEmpty(unidade)){
					String u = (String) detalhamento.get(0).get("unidade");
					unidade.setNome(u);
				}
				
				if (!ValidatorUtil.isEmpty(gestora)){
					String u = (String) detalhamento.get(0).get("gestora");
					gestora.setNome(u);
				}			
			}
			
			getCurrentSession().setAttribute("apenasCadastrados", apenasCadastrados);
		} finally {
			if (dao != null)
				dao.close();
		}		
		
		return forward("/portais/relatorios/planos_cursos_cadastrados/detalhe.jsp");
	}	

	public List<Map<String, Object>> getListagem() {
		return listagem;
	}

	public void setListagem(List<Map<String, Object>> listagem) {
		this.listagem = listagem;
	}

	public List<Map<String, Object>> getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(List<Map<String, Object>> detalhamento) {
		this.detalhamento = detalhamento;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Unidade getGestora() {
		return gestora;
	}

	public void setGestora(Unidade gestora) {
		this.gestora = gestora;
	}
}
