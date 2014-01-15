/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/03/2011
 *
 */
package br.ufrn.sigaa.relatoriosgestao.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pid.dominio.PlanoIndividualDocente;
import br.ufrn.sigaa.relatoriosgestao.dao.RelatorioPIDDao;
import br.ufrn.sigaa.relatoriosgestao.dominio.RelatorioPIDPorSemestre;

/**
 * MBean Responsável por Gerar o Relatório por Semestre do PID.
 * 
 * @author Arlindo
 *
 */
@Component("relatorioPIDPorSemestre") @Scope("request")
public class RelatorioPIDPorSemestreMBean  extends SigaaAbstractController<PlanoIndividualDocente> {
	
	/** Lista com os dados do relatório. */
	private Collection<RelatorioPIDPorSemestre> listagem = new ArrayList<RelatorioPIDPorSemestre>();
	
	/** Lista com os dados dos docentes selecionados */
	private Collection<PlanoIndividualDocente> listagemPIDs = new ArrayList<PlanoIndividualDocente>();
	
	/** Ano informado para geração do relatório */
	private Integer ano;
	
	/** Período informado para geração do relatório */
	private Integer periodo;
	
	/** Unidade selecionada no detalhamento */
	private Unidade unidade;
	
	/** Centro selecionado no detalhamento */
	private Unidade gestora;	
	
	/** Descrição do status */
	private String statusDescricao;
	
	/** Exibe somente os docentes ativos no detalhe */
	private boolean apenasAtivos;
	
	/**
	 * Inicia o relatório de PID por Semestre
	 * <br/><br/>
	 * Método Chamado pela Seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/relatorios/abas/ensino.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar(){
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		return forward("/pid/relatorios/form_relatorio_por_semestre.jsp");
	}
	
	/**
	 * Gera o Relatório por Semestre.
	 * <br/><br/>
	 * Método Chamado pela Seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/pid/relatorios/form_relatorio_por_semestre.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String gerarRelatorio() throws DAOException{
		
		ValidatorUtil.validateRequired(ano, "Ano", erros);		
		ValidatorUtil.validateRequired(periodo, "Período", erros);
		
		ValidatorUtil.validateMinValue(ano, 1900, "Ano", erros);
		ValidatorUtil.validateRange(periodo, 1, 2, "Período", erros);
		
		if (hasErrors())
			return null;
		
		RelatorioPIDDao dao = getDAO(RelatorioPIDDao.class);
		try {
			
			listagem = dao.findTotaisPIDByAnoPeriodo(ano, periodo);
			
			if (listagem.isEmpty()){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}		
		return forward("/pid/relatorios/relatorio_por_semestre.jsp");
	}
	
	/**
	 * Detalha o Relatório conforme a coluna clicada
	 * <br/><br/>
	 * Método Chamado pela Seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/pid/relatorios/relatorio_por_semestre.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String detalharRelatorio() throws HibernateException, DAOException{
		
		RelatorioPIDDao dao = getDAO(RelatorioPIDDao.class);
		try {
			int idUnidade = getParameterInt("id",0);
			String tipo = getParameter("tipo");
			int idCentro = getParameterInt("idCentro",0);
			
			unidade = null;
			Integer[] status = null;
			statusDescricao = null;
			gestora = null;
			apenasAtivos = false;
			
			if (idUnidade > 0)
				unidade = new Unidade(idUnidade);
			
			if (idCentro > 0)
				gestora = new Unidade(idCentro);
			
			if (tipo != null){
				if (tipo.equals("0"))
					apenasAtivos = true;
				else if (tipo.equals("1"))
					status = new Integer[]{ PlanoIndividualDocente.CADASTRADO, PlanoIndividualDocente.HOMOLOGADO };
				else if (tipo.equals("2"))
					status = new Integer[]{ PlanoIndividualDocente.HOMOLOGADO };				
			}
			
			if (apenasAtivos)
				listagemPIDs = dao.findDocentesAtivosByUnidade(unidade, gestora, ano, periodo);
			else
				listagemPIDs = dao.findPIDBySemestreAndDepartamento(unidade, gestora, ano, periodo, status);
			
			if (listagemPIDs.isEmpty()){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			} else {
				if (status != null)
					if (tipo.equals("1"))
						statusDescricao = "Cadastrados";
					else
						statusDescricao = listagemPIDs.iterator().next().getDescricaoStatus();
				
				if (!ValidatorUtil.isEmpty(unidade)){
					unidade = listagemPIDs.iterator().next().getServidor().getUnidade();
				}
				
				if (!ValidatorUtil.isEmpty(gestora)){
					gestora = listagemPIDs.iterator().next().getServidor().getUnidade().getGestora();
				}				
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}					
		return forward("/pid/relatorios/detalhe_relatorio_por_semestre.jsp");
	}

	public Collection<RelatorioPIDPorSemestre> getListagem() {
		return listagem;
	}

	public void setListagem(Collection<RelatorioPIDPorSemestre> listagem) {
		this.listagem = listagem;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Collection<PlanoIndividualDocente> getListagemPIDs() {
		return listagemPIDs;
	}

	public void setListagemPIDs(Collection<PlanoIndividualDocente> listagemPIDs) {
		this.listagemPIDs = listagemPIDs;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public String getStatusDescricao() {
		return statusDescricao;
	}

	public void setStatusDescricao(String statusDescricao) {
		this.statusDescricao = statusDescricao;
	}

	public Unidade getGestora() {
		return gestora;
	}

	public void setGestora(Unidade gestora) {
		this.gestora = gestora;
	}

	public boolean isApenasAtivos() {
		return apenasAtivos;
	}

	public void setApenasAtivos(boolean apenasAtivos) {
		this.apenasAtivos = apenasAtivos;
	}
}
