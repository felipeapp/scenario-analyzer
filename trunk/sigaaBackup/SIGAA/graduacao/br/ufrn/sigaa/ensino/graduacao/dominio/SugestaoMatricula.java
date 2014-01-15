/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import org.apache.commons.lang.builder.CompareToBuilder;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe de domínio auxiliar que agrega as informações necessárias
 * para a matrícula em uma turma
 *
 * @author ricardo
 *
 */
public class SugestaoMatricula {

	/** atributo que agrupa as turmas na JSP.
	 * para graduação ele indica o semestre de oferta, para técnico é a descrição do módulo */
	private String nivel;

	/** {@link Turma} referente a sugestão. */
	private Turma turma;

	/** {@link ComponenteCurricular} referente a sugestão. */
	private ComponenteCurricular atividade;

	/** Indica se a sugestão foi selecionada. */
	private boolean selected;

	/** Indica se a sugestão se refere a uma disciplina obrigatória ou optativa. */
	private Boolean obrigatoria;

	/** informa o tipo do motivo por que o aluno não pode escolher essa turma*/
	private Integer tipoInvalido;

	// Definição do tipoInvalido
	/** A turma não pode ser selecionada pois o discente não pagou os pré-requisitos da disciplina desejada. */
	public final static int PRE_REQUISITO = 1;

	/** A turma não pode ser selecionada pois o discente já pagou uma disciplina equivalente a desejada. */
	public final static int EQUIVALENTE_PAGO = 2;

	/** A turma não pode ser selecionada pois o discente já se encontra matriculado na disciplina desejada ou em uma equivalente. */
	public final static int JA_MATRICULADO = 3;

	/** A turma não pode ser selecionada pois o discente já selecionou uma turma da disciplina desejada ou de um componente equivalente. */
	public final static int JA_SELECIONADO = 4;

	/** A turma não pode ser selecionada pois a matrícula só pode ser realizada pela coordenação. */
	public final static int NAO_MATRICULAVEL = 5;


	public ComponenteCurricular getAtividade() {
		return this.atividade;
	}

	public void setAtividade(ComponenteCurricular atividade) {
		this.atividade = atividade;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Boolean isObrigatoria() {
		return obrigatoria;
	}

	public void setObrigatoria(Boolean obrigatoria) {
		this.obrigatoria = obrigatoria;
	}

	public String getDocentesNomes() {
		return turma.getDocentesNomes();
	}

	/**
	 * Cria o {@link DocenteTurma} com os dados passados e o adiciona na turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @param idDocenteTurma
	 * @param nome
	 */
	public void addDocentesNomes(int idDocenteTurma, String nome) {
		DocenteTurma dt = new DocenteTurma( idDocenteTurma );
		dt.setDocente(new Servidor(idDocenteTurma, nome)); //ATENÇÃO! este idDocenteTurma NÃO é o id do servidor!  este objeto é utilizado apenas na sugestão de matricula! NÃO PODE SER PERSISTIDO!
		dt.setDocenteExterno(null);
		turma.addDocenteTurma(dt);
	}

	public SugestaoMatricula() {
		turma = new Turma();
	}


	public SugestaoMatricula(Integer id) {
		turma = new Turma(id);
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public String getDescricao() {
		return "Turma " + turma.getCodigo() + " / " + getDocentesNomes() + " / " + turma.getDescricaoHorario() + " / " + turma.getLocal();
	}

	/**
	 * Retorna a descrição do motivo pelo qual a turma escolhida não pode ser selecionada.
	 * 
	 * @return
	 */
	public String getMotivoInvalido() {
		if (tipoInvalido == null)
			return "";
		switch (tipoInvalido) {
			case PRE_REQUISITO: return "O discente não possui o(s) pré-requisito(s) necessário(s)";
			case EQUIVALENTE_PAGO: return "O discente já pagou componenete(s) equivalente(s)";
			case JA_MATRICULADO: return "O discente já se encontra matriculado nesse componente (ou equivalentes)";
			case JA_SELECIONADO: return "O discente já selecionou uma turma desse componente (ou equivalentes)";
			case NAO_MATRICULAVEL: return "Só é possível fazer matrículas nessa disciplina diretamente na coordenação do seu curso";
		}
		return "";
	}

	public boolean isNaoTemPreRequisito() {
		return tipoInvalido != null && tipoInvalido == PRE_REQUISITO;
	}

	public boolean isEquivalentePago() {
		return tipoInvalido != null && tipoInvalido == EQUIVALENTE_PAGO;
	}

	public boolean isJaMatriculado() {
		return tipoInvalido != null && tipoInvalido == JA_MATRICULADO;
	}

	public boolean isJaSelecionado() {
		return tipoInvalido != null && tipoInvalido == JA_SELECIONADO;
	}

	public boolean isPodeMatricular() {
		return tipoInvalido == null || tipoInvalido == 0;
	}

	public boolean isBuscarEquivalentes() {
		return isPodeMatricular() && !isEmpty( turma.getDisciplina().getEquivalencia());
	}

	public boolean isBuscarPreRequisitos() {
		return !isEmpty( turma.getDisciplina().getPreRequisito()) && isNaoTemPreRequisito();
	}

	public boolean isBuscarCoRequisitos() {
		return !isEmpty( turma.getDisciplina().getCoRequisito()) && isPodeMatricular() ;
	}

	public Integer getTipoInvalido() {
		return tipoInvalido;
	}

	public void setTipoInvalido(Integer tipoInvalido) {
		this.tipoInvalido = tipoInvalido;
	}

	public boolean isTemReservaPraMatriz() {
		return !isEmpty(turma.getReservas());
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	/**
	 * Retorna <code>Obrig. Currículo</code> caso a disciplina associada seja obrigatória,
	 * ou <code>Optativa</code> caso contrário.
	 * 
	 * @return
	 */
	public String getObrigatoriaDescricao() {
		if (obrigatoria != null)
			return obrigatoria ? "Obrig. Currículo":"Optativa";
		else
			return "";
	}
	
	/**
	 * Compara as sugestões de acordo com suas disciplinas.
	 * 
	 * @param o
	 * @return
	 */
	public int compareByDisciplinaTo(Object o) {
		SugestaoMatricula sugestao = (SugestaoMatricula) o;
		return new CompareToBuilder()
			.append(atividade.getId(), sugestao.getAtividade().getId())
			.toComparison();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((turma == null) ? 0 : turma.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(obj, this, "turma.id");
	}

}
