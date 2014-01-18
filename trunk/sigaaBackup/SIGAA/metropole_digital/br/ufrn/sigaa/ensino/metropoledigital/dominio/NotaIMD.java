package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;

/**
 * Entidade que representa as notas do IMD
 * 
 * @author Rafael Silva
 *
 */
public class NotaIMD {	
	/**Participação Presencial do Aluno*/
	private Double participacaoPresencial;
	/**Participação Virtual do Aluno*/
	private Double participacaoVirtual;
	/**Participação Total do Aluno*/
	private NotaUnidade participacaoTotal = new NotaUnidade();
	/**Nota das atividades realizadas via moodle*/
	private NotaUnidade atividadeOnline = new NotaUnidade();
	/**Nota da prova escrita*/
	private NotaUnidade provaEscrita = new NotaUnidade();
	/**Nota da prova de recuperação*/
	private Double provaRecuperacao;
	/**Carga horária não frequentada*/
	private Integer chnf;
	/**Média parcial do aluno no módulo (Após a consolidação parcial)*/
	private Double mediaParcial;
	/**Média final do aluno no módulo (Após a consolidação final)*/
	private Double mediaFinal;
	/**Matricula do aluno na turma*/
	private MatriculaComponente matriculaComponente = new MatriculaComponente();
	/**Discente*/
	private DiscenteTecnico discente = new DiscenteTecnico();
	
	/**
	 * Calcula a média da disciplina.
	 * 
	 * @return
	 */
	public Double getMediaCalculada(){
		if (participacaoPresencial!= null && participacaoVirtual!= null && atividadeOnline.getNota()!=null && provaEscrita.getNota()!= null) {
			if (provaRecuperacao!=null) {
				if (provaRecuperacao > provaEscrita.getNota()) {					
					return (((participacaoPresencial*3 + participacaoVirtual*7)/10)+atividadeOnline.getNota()+(provaRecuperacao*2))/4;
				}				
			}
			return (((participacaoPresencial*3 + participacaoVirtual*7)/10)+atividadeOnline.getNota()+(provaEscrita.getNota()*2))/4;															
		} 
		return null;		
	}
	
	/**
	 * Retorna a carga horaria total da disciplina (Turma).
	 * 
	 * @return
	 */
	public Integer getCargaHoriaDisciplina(){		
		return matriculaComponente.getTurma().getChTotalTurma();
	}
	
	
	//GETTERS AND SETTERS
	public Double getParticipacaoPresencial() {
		return participacaoPresencial;
	}
	public void setParticipacaoPresencial(Double participacaoPresencial) {
		this.participacaoPresencial = participacaoPresencial;
	}
	public Double getParticipacaoVirtual() {
		return participacaoVirtual;
	}
	public void setParticipacaoVirtual(Double participacaoVirtual) {
		this.participacaoVirtual = participacaoVirtual;
	}
	public NotaUnidade getParticipacaoTotal() {
		return participacaoTotal;
	}
	public void setParticipacaoTotal(NotaUnidade participacaoTotal) {
		this.participacaoTotal = participacaoTotal;
	}
	public NotaUnidade getAtividadeOnline() {
		return atividadeOnline;
	}
	public void setAtividadeOnline(NotaUnidade atividadeOnline) {
		this.atividadeOnline = atividadeOnline;
	}
	public NotaUnidade getProvaEscrita() {
		return provaEscrita;
	}
	public void setProvaEscrita(NotaUnidade provaEscrita) {
		this.provaEscrita = provaEscrita;
	}	
	public Double getProvaRecuperacao() {
		return provaRecuperacao;
	}
	public void setProvaRecuperacao(Double provaRecuperacao) {
		this.provaRecuperacao = provaRecuperacao;
	}
	public Integer getChnf() {
		return chnf;
	}
	public void setChnf(Integer chnf) {
		this.chnf = chnf;
	}
	public Double getMediaParcial() {
		return mediaParcial;
	}
	public void setMediaParcial(Double mediaParcial) {
		this.mediaParcial = mediaParcial;
	}
	public Double getMediaFinal() {
		return mediaFinal;
	}
	public void setMediaFinal(Double mediaFinal) {
		this.mediaFinal = mediaFinal;
	}
	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}
	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}
	public DiscenteTecnico getDiscente() {
		return discente;
	}
	public void setDiscente(DiscenteTecnico discente) {
		this.discente = discente;
	}
}
