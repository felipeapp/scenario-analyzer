/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 31/05/2007
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;

/**
 * MBean para gerar os relatórios de docentes ministrantes de componentes
 * @author jacome
 *
 */
@Component("relatorioMinistrantes") @Scope("request")
public class RelatorioMinistrantesComponentesMBean extends SigaaAbstractController<DocenteTurma> {

	// Constantes das views
	/** Link para o formulário de parâmetros. */
	public static final String JSP_PARAM_MINISTRANTES = "/graduacao/relatorios/docente/seleciona_lista_ministrantes_anosemestre.jsp";
	/** Link para o relatório. */
	public static final String JSP_RELATORIO_MINISTRANTES = "/graduacao/relatorios/docente/relatorio_ministrantes_anosemestre.jsp";

	/** Ano inicial utilizado para filtrar os docentes */
	private Integer anoInicio;
	/** Período inicial utilizado para filtrar os docentes */
	private Integer periodoInicio;
	/** Ano final utilizado para filtrar os docentes */
	private Integer anoFim;
	/** Período final utilizado para filtrar os docentes */
	private Integer periodoFim;
	/** Componente Curricular filtrado */
	private ComponenteCurricular disciplina;
	/** Lista de turmas contendo seus ministrantes*/
	private List<DocenteTurma> ministrantes;

	public String iniciar () {
		anoInicio = null;
		periodoInicio = null;
		anoFim = null;
		periodoFim = null;
		disciplina = new ComponenteCurricular();
		disciplina.setDetalhes(new ComponenteDetalhes());
		ministrantes = new ArrayList<DocenteTurma>();
		return forward(JSP_PARAM_MINISTRANTES);
	}
	
	public String gerarRelatorio () throws HibernateException, DAOException {
		
		DocenteTurmaDao dtDao = null;
		
		try {
			
			if (anoInicio == null || periodoInicio == null)
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período Letivo Inicial");
			if (anoFim == null || periodoFim == null)
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período Letivo Final");
			if (anoInicio != null && periodoInicio != null && anoFim != null && periodoFim != null){
				if ( anoInicio.intValue() > anoFim.intValue() || (anoInicio.intValue() == anoFim.intValue() && periodoInicio.intValue() > periodoFim.intValue()) )
					addMensagemErro("Período Inválido: Período Letivo Final deve ser maior que Período Letivo Inicial.");
			}
			
			if (hasErrors())
				return null;
			
			dtDao = getDAO(DocenteTurmaDao.class);
			ministrantes = dtDao.findDocentesByPeriodoComponente(anoInicio, periodoInicio, anoFim, periodoFim,getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), disciplina);
						
			disciplina = new ComponenteCurricular();
			disciplina.setDetalhes(new ComponenteDetalhes());
			
			if (isEmpty(ministrantes)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}			
			
			return forward(JSP_RELATORIO_MINISTRANTES);
		} finally {
			if (dtDao!=null)
				dtDao.close();
		}		
	}
	
	public void setAnoInicio(Integer anoInicio) {
		this.anoInicio = anoInicio;
	}

	public Integer getAnoInicio() {
		return anoInicio;
	}

	public void setPeriodoInicio(Integer periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	public Integer getPeriodoInicio() {
		return periodoInicio;
	}

	public void setAnoFim(Integer anoFim) {
		this.anoFim = anoFim;
	}

	public Integer getAnoFim() {
		return anoFim;
	}

	public void setPeriodoFim(Integer periodoFim) {
		this.periodoFim = periodoFim;
	}

	public Integer getPeriodoFim() {
		return periodoFim;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setMinistrantes(List<DocenteTurma> ministrantes) {
		this.ministrantes = ministrantes;
	}

	public List<DocenteTurma> getMinistrantes() {
		return ministrantes;
	}
	
}
