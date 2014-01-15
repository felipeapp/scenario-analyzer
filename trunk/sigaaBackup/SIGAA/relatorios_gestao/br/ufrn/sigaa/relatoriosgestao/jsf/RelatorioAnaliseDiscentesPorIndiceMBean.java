/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/04/2011
 */
package br.ufrn.sigaa.relatoriosgestao.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.relatoriosgestao.dao.RelatorioAnaliseDiscentesPorIndiceDao;
import br.ufrn.sigaa.relatoriosgestao.dominio.RelatorioIndicesTotais;

/**
 * MBean responsável por gerar o relatório de analise de discentes por Índice acadêmico
 * 
 * @author arlindo
 *
 */
@Component("relatorioAnaliseDiscentesPorIndiceMBean") @Scope("request")
public class RelatorioAnaliseDiscentesPorIndiceMBean extends SigaaAbstractController<IndiceAcademicoDiscente> {
	
	/** Faixa de valores dos índices */
	private List<RelatorioIndicesTotais> valoresFaixa = new ArrayList<RelatorioIndicesTotais>();
	
	/** Lista de discente da faixa selecionada */
	private List<IndiceAcademicoDiscente> listaDiscente = new ArrayList<IndiceAcademicoDiscente>();
	
	/** Construtor padrão */
	public RelatorioAnaliseDiscentesPorIndiceMBean() {
		ano = getCalendarioVigente().getAno()-4;
		periodo = getCalendarioVigente().getPeriodo();
		curso = new Curso();
		indice = new IndiceAcademico();
	}
	
	/** Ano selecionado para filtrar o relatório */
	private Integer ano;
	/** Período selecionado para filtrar o relatório */
	private Integer periodo;
	/** Curso selecionado para filtrar o relatório */
	private Curso curso;
	/** Nível selecionado para filtrar o relatório */
	private Integer nivel;
	/** Índice Acadêmico selecionado para filtrar o relatório */
	private IndiceAcademico indice;		
	/** Faixa selecionada */
	private String faixa;
	/** Tipo do gráfico selecionado */
	private int tipoGrafico;
	
	/**
	 * Inicia o form para informar os filtros do relatório
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/portais/relatorios/abas/ensino.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar(){		
		return forward("/portais/relatorios/analise_discentes_indices/form.jsp");
	}
	
	/**
	 * Gera o relatório
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/portais/relatorios/analise_discentes_indices/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws HibernateException, DAOException{		
		
		erros = new ListaMensagens();
		
		ValidatorUtil.validaInt(ano, "Ano/Periodo de Entrada", erros);
		ValidatorUtil.validaInt(periodo, "Ano/Periodo de Entrada", erros);
		ValidatorUtil.validateRequired(curso, "Curso", erros);
		ValidatorUtil.validateRequired(indice, "Índice Acadêmico", erros);		
		
		Integer periodosRegulares = getParametrosAcademicos().getQuantidadePeriodosRegulares();
		if (periodosRegulares != null && periodosRegulares > 0 && periodo > periodosRegulares){
			addMensagemErro("Período Inválido. Número de períodos regulares ("+periodosRegulares+")");
			return null;
		}

		if (hasErrors())
			return null;
			
		RelatorioAnaliseDiscentesPorIndiceDao dao = getDAO(RelatorioAnaliseDiscentesPorIndiceDao.class);
		try {
			valoresFaixa = dao.findTotaisIndicesDiscente(ano, periodo, curso.getId(), nivel, indice.getId());
			
			if (ValidatorUtil.isEmpty(valoresFaixa)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			indice = dao.findByPrimaryKey(indice.getId(), IndiceAcademico.class, "id","nome");
			curso = dao.findByPrimaryKey(curso.getId(), Curso.class, "id","nome", "municipio.nome", "modalidadeEducacao.id");
			
		} finally {
			if (dao != null)
				dao.close();
		}		
		return forward("/portais/relatorios/analise_discentes_indices/lista.jsp");
	}
	
	/**
	 * Detalha os discente conforme a faixa de índice selecionada
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/portais/relatorios/analise_discentes_indices/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String detalhar() throws HibernateException, DAOException {			
		int ord = getParameterInt("index", -1);
		
		RelatorioAnaliseDiscentesPorIndiceDao dao = getDAO(RelatorioAnaliseDiscentesPorIndiceDao.class);
		try {
			indice = dao.findByPrimaryKey(indice.getId(), IndiceAcademico.class, "id", "nome", 
					"sigla", "frequenciaDivisaoHistograma");
			
			listaDiscente = dao.findDiscenteByIndice(ano, periodo, curso.getId(), nivel, indice, ord);
			
			faixa = null;			
			// Pega a faixa selecionada
			if (ord >= 0 && !ValidatorUtil.isEmpty(indice.getFrequenciaDivisaoHistograma())){				
				String[] faixas = indice.getFrequenciaDivisaoHistograma().split(",");
				
				if (faixas.length-1 == ord){
					if (faixas[ord].equals("*"))
						faixa = " > "+ faixas[ord-1];
					else
						faixa = faixas[ord];
				} else {					
					faixa = faixas[ord] + " - "+ faixas[ord + 1]; 
				}				
			}
			
			
		} finally {
			if (dao != null)
				dao.close();
		}			
		return forward("/portais/relatorios/analise_discentes_indices/detalhe.jsp");
	}	
	
    /**
     * Gera Histórico ao clicar sobre um discente.<br/>
     * Método chamado pela seguinte JSP:
     * <ul>
     *    <li>/sigaa.war/portais/relatorios/analise_discentes_indices/detalhe.jsp</li>
     * </ul>
     * @return
     * @throws ArqException
     */
    public String gerarHistorico() throws ArqException{
		HistoricoMBean bean = getMBean("historico");				
		return bean.selecionaDiscenteForm();
    }	
    
    /**
     * Lista todos os indices acadêmicos da graduação
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/portais/relatorios/analise_discentes_indices/form.jsp</li>
	 * </ul>
     * @return
     * @throws DAOException
     */
    public List<SelectItem> getAllIndicesGraduacao() throws DAOException{
    	IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class);
    	try {
    		return toSelectItems(dao.findIndicesAtivos(NivelEnsino.GRADUACAO), "id", "nome");    		
    	} finally {
    		dao.close();
    	}
    }  
	
	public List<IndiceAcademicoDiscente> getListaDiscente() {
		return listaDiscente;
	}

	public void setListaDiscente(List<IndiceAcademicoDiscente> listaDiscente) {
		this.listaDiscente = listaDiscente;
	}

	public List<RelatorioIndicesTotais> getValoresFaixa() {
		return valoresFaixa;
	}

	public void setValoresFaixa(List<RelatorioIndicesTotais> valoresFaixa) {
		this.valoresFaixa = valoresFaixa;
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

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Integer getNivel() {
		return nivel;
	}

	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}

	public IndiceAcademico getIndice() {
		return indice;
	}

	public void setIndice(IndiceAcademico indice) {
		this.indice = indice;
	}

	public String getFaixa() {
		return faixa;
	}

	public void setFaixa(String faixa) {
		this.faixa = faixa;
	}

	public int getTipoGrafico() {
		return tipoGrafico;
	}

	public void setTipoGrafico(int tipoGrafico) {
		this.tipoGrafico = tipoGrafico;
	}
}
