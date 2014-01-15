/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * MBean responsável por gerar relatório com as turmas pendentes (Sem professor ou não consolidada)
 * 
 * @author Henrique André
 * 
 */
@Component("relatorioTurmasPendentes") @Scope("request")
public class RelatorioTurmasPendentesMBean extends SigaaAbstractController<Turma> {

	/**
	 * Combos permitidos
	 */
	private SelectItem[] comboNiveis = new SelectItem[] {
		new SelectItem(NivelEnsino.GRADUACAO + "", NivelEnsino.getDescricao(NivelEnsino.GRADUACAO)),
		new SelectItem(NivelEnsino.LATO + "", NivelEnsino.getDescricao(NivelEnsino.LATO)),
		new SelectItem(NivelEnsino.STRICTO + "", NivelEnsino.getDescricao(NivelEnsino.STRICTO)),
		new SelectItem(NivelEnsino.MESTRADO + "", NivelEnsino.getDescricao(NivelEnsino.MESTRADO)),
		new SelectItem(NivelEnsino.DOUTORADO + "", NivelEnsino.getDescricao(NivelEnsino.DOUTORADO)) };	
	
	private char nivel;
	
	private Integer anoInicio = 2003;
	private Integer anoFim;
	
	private List<Turma> turmasPendentes;
	
	/**
	 * Construtor Padrão
	 */
	public RelatorioTurmasPendentesMBean() {
	}
	
	/**
	 * Inicia o caso de uso para gerar relatório
	 * JSP: /sigaa.war/portais/rh_plan/abas/planejamento.jsp
	 * @return
	 */
	public String iniciar() {
		return forward("/graduacao/relatorios/turma/seleciona_turmas_pendetes.jsp");
	}

	/**
	 * Gera o relatório de turmas pendentes
	 * 
	 * JSP: /sigaa.war/graduacao/relatorios/turma/seleciona_turmas_pendetes.jsp
	 * @return
	 */
	public String gerarRelatorio() {
		
//		
//		String[] anoPeriodoAnterior = getCalendarioVigente().getAnoPeriodoAnterior().split(".");
//		
//		int anoAnterior = Integer.valueOf(anoPeriodoAnterior[0]);
//		int periodoAnterior = Integer.valueOf(anoPeriodoAnterior[1]);
		String anoPeriodoAtual = getCalendarioVigente().getAnoPeriodo().replace(".", "");
		TurmaDao dao = getDAO(TurmaDao.class);
		turmasPendentes = dao.findTurmasPendentes(nivel, anoInicio, anoFim, anoPeriodoAtual);
		return forward("/graduacao/relatorios/turma/lista_turmas_pendetes.jsp");
	}
	
	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public List<Turma> getTurmasPendentes() {
		return turmasPendentes;
	}

	public void setTurmasPendentes(List<Turma> turmasPendentes) {
		this.turmasPendentes = turmasPendentes;
	}

	
	public String getDescricao() {
		return NivelEnsino.getDescricao(nivel);
	}

	/**
	 * Monta combo com os níveis permitidos
	 * JSP: /sigaa.war/graduacao/relatorios/turma/seleciona_turmas_pendetes.jsp
	 * @return
	 */
	public List<SelectItem> getComboNiveis() {
		return Arrays.asList(comboNiveis);
	}

	public Integer getAnoInicio() {
		return anoInicio;
	}

	public void setAnoInicio(Integer anoInicio) {
		this.anoInicio = anoInicio;
	}

	public Integer getAnoFim() {
		return anoFim;
	}

	public void setAnoFim(Integer anoFim) {
		this.anoFim = anoFim;
	}
	
}
