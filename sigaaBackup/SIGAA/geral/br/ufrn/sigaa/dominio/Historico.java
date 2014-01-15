/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 03/02/2007
 * 
 */
package br.ufrn.sigaa.dominio;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * Classe que representa o histórico de um discente. (Não persistida)
 *
 * @author David Pereira
 * @author Eric Moura
 */
public class Historico {

	/** Discente a qual o histórico se refere */
	private DiscenteAdapter discente;
	
	/** Matricula do discente a qual o histórico se refere */
	private Collection<MatriculaComponente> matriculasDiscente;
	
	/** Disciplinas pendentes do discente a qual o histórico se refere */
	private Collection<ComponenteCurricular> disciplinasPendentesDiscente;

	/** Lista de disciplinas, pagas por equivalências, do discente a qual o histórico se refere */
	private List<TipoGenerico> equivalenciasDiscente;
	
	/** Lista de observações sobre o discente */
	private List<ObservacaoDiscente> observacoesDiscente;
	
	/** Lista de Mobilidade Estudantil que o discente realizou */
	private List<TipoGenerico> mobilidadeEstudantil;

	/** Data do histórico */
	private Date dataHistorico;

	/** Diz se o histórico é reconhecido ou não */
	private boolean reconhecimento;
	
	/** Portaria em que o histórico é reconhecido */
	private String reconhecimentoPortaria;
	
	/** Data do decreto */
	private Date dataDecreto;
	
	/** Dou do decreto */
	private Date dou;
	
	/** Média do discente a qual o histórico se refere */
	private float mediaDiscente;

	/** Total de disciplinas integradas ao discente a qual o histórico se refere */
	private int totalDisciplinasIntegralizadas;

	/** Total da carga horária integrada ao discente a qual o histórico se refere */
	private int totalCargaHorariaIntegralizada;

	/** Aproveitamentos do discente a qual o histórico se refere */
	private List<Object> aproveitamentosDiscente;

	/** Total de disciplinas pendentes do discente a qual o histórico se refere */
	private int totalDisciplinasPendentes;

	/** Total de carga horária pendentes do discente a qual o histórico se refere */
	private int totalCargaHorariaPendente;
	
	/** Atributo responsável por armazenar o status do discente com internacionalização. */
	private String statusDiscenteI18n;
	
	public boolean isGraduacao() {
		return verificaNivel(NivelEnsino.GRADUACAO);
	}

	public boolean isTecnico() {
		return verificaNivel(NivelEnsino.TECNICO);
	}
	
	public boolean isFormacaoComplementar() {
		return verificaNivel(NivelEnsino.FORMACAO_COMPLEMENTAR);
	}

	public boolean isPosGraduacao() {
		return isLato() || isStricto();
	}

	public boolean isLato() {
		return verificaNivel(NivelEnsino.LATO);
	}

	public boolean isStricto() {
		return verificaNivel(NivelEnsino.STRICTO) || verificaNivel(NivelEnsino.DOUTORADO) || verificaNivel(NivelEnsino.MESTRADO);
	}
	
	public boolean isResidencia() {
		return verificaNivel(NivelEnsino.RESIDENCIA);
	}

	private boolean verificaNivel(char nivel) {
		return discente.getNivel() == nivel;
	}

	public String getNivel() {
		if (isGraduacao()) return "Graduacao";
		else if (isTecnico() || isFormacaoComplementar()) return "Tecnico";
		else if (isLato()) return "Lato";
		else if (isStricto()) return "Stricto";
		else if (isResidencia()) return "Residencia";
		else return "";
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public Collection<MatriculaComponente> getMatriculasDiscente() {
		return matriculasDiscente;
	}

	public void setMatriculasDiscente(
			Collection<MatriculaComponente> matriculasDiscente) {
		this.matriculasDiscente = matriculasDiscente;
	}

	public Collection<ComponenteCurricular> getDisciplinasPendentesDiscente() {
		return disciplinasPendentesDiscente;
	}

	public void setDisciplinasPendentesDiscente(
			Collection<ComponenteCurricular> disciplinasPendentesDiscente) {
		this.disciplinasPendentesDiscente = disciplinasPendentesDiscente;
	}

	public List<TipoGenerico> getEquivalenciasDiscente() {
		return equivalenciasDiscente;
	}

	public void setEquivalenciasDiscente(List<TipoGenerico> equivalenciasDiscente) {
		this.equivalenciasDiscente = equivalenciasDiscente;
	}

	public List<ObservacaoDiscente> getObservacoesDiscente() {
		return observacoesDiscente;
	}

	public void setObservacoesDiscente(List<ObservacaoDiscente> observacoesDiscente) {
		this.observacoesDiscente = observacoesDiscente;
	}

	/**
	 * Popula as informações dos trancamentos para serem exibidas no
	 * histórico de acordo com o nível de ensino do discente ao qual
	 * o histórico pertence.
	 * @param trancamentos
	 */
	public void setTrancamentos(Collection<MovimentacaoAluno> trancamentos) {
		if (discente.isStricto() || discente.isLato()) {
			int total = 0;
			if (trancamentos != null && !trancamentos.isEmpty()) {
				//total = trancamentos.size() * 6;
				for (MovimentacaoAluno mov : trancamentos) {
					total += (mov.getValorMovimentacao() == null ? 0 : mov.getValorMovimentacao());
				}
			}
			discente.setTrancamentos(String.valueOf(total));
		} else {
			StringBuilder sb = new StringBuilder();
			if (trancamentos != null && !trancamentos.isEmpty()) {
				for (Iterator<MovimentacaoAluno> it = trancamentos.iterator(); it.hasNext(); ) {
					MovimentacaoAluno mov = it.next();
					sb.append(mov.getAnoPeriodoReferencia());
					if (it.hasNext())
						sb.append(", ");
				}
			} else {
				sb.append("Nenhum");
			}
			discente.setTrancamentos(sb.toString());
		}
	}

	public Date getDataHistorico() {
		return dataHistorico;
	}

	public void setDataHistorico(Date dataHistorico) {
		this.dataHistorico = dataHistorico;
	}

	public boolean isReconhecimento() {
		return reconhecimento;
	}

	public void setReconhecimento(boolean reconhecimento) {
		this.reconhecimento = reconhecimento;
	}

	public String getReconhecimentoPortaria() {
		return reconhecimentoPortaria;
	}

	public void setReconhecimentoPortaria(String reconhecimentoPortaria) {
		this.reconhecimentoPortaria = reconhecimentoPortaria;
	}

	public Date getDataDecreto() {
		return dataDecreto;
	}

	public void setDataDecreto(Date dataDecreto) {
		this.dataDecreto = dataDecreto;
	}

	
	public Date getDou() {
		return dou;
	}

	public void setDou(Date dou) {
		this.dou = dou;
	}

	public float getMediaDiscente() {
		return mediaDiscente;
	}

	public void setMediaDiscente(float mediaDiscente) {
		this.mediaDiscente = mediaDiscente;
	}

	public void setTotalDisciplinasIntegralizadas(int totalDisciplinasIntegralizadas) {
		this.totalDisciplinasIntegralizadas = totalDisciplinasIntegralizadas;
	}

	public void setTotalCargaHorariaIntegralizada(int cargaHorariaIntegralizada) {
		this.totalCargaHorariaIntegralizada = cargaHorariaIntegralizada;
	}

	public void setAproveitamentosDiscente(List<Object> lista1) {
		this.aproveitamentosDiscente = lista1;
	}

	public void setTotalDisciplinasPendentes(int size) {
		this.totalDisciplinasPendentes = size;
	}

	public void setTotalCargaHorariaPendente(int cargaHorariaPendente) {
		this.totalCargaHorariaPendente = cargaHorariaPendente;
	}

	public int getTotalDisciplinasIntegralizadas() {
		return totalDisciplinasIntegralizadas;
	}

	public int getTotalCargaHorariaIntegralizada() {
		return totalCargaHorariaIntegralizada;
	}

	public List<Object> getAproveitamentosDiscente() {
		return aproveitamentosDiscente;
	}

	public int getTotalDisciplinasPendentes() {
		return totalDisciplinasPendentes;
	}

	public int getTotalCargaHorariaPendente() {
		return totalCargaHorariaPendente;
	}

	public List<TipoGenerico> getMobilidadeEstudantil() {
		return mobilidadeEstudantil;
	}

	public void setMobilidadeEstudantil(
			List<TipoGenerico> mobilidadeEstudantil) {
		this.mobilidadeEstudantil = mobilidadeEstudantil;
	}

	public String getStatusDiscenteI18n() {
		return statusDiscenteI18n;
	}

	public void setStatusDiscenteI18n(String statusDiscenteI18n) {
		this.statusDiscenteI18n = statusDiscenteI18n;
	}
}