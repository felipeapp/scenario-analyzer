/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em: 16/05/2007
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade que cont�m as situa��es poss�vel de matr�cula. 
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "situacao_matricula", schema = "ensino", uniqueConstraints = {})
@Cache ( usage = CacheConcurrencyStrategy.READ_ONLY )
public class SituacaoMatricula implements Validatable {

	// Fields

	/** Id referente a {@link SituacaoMatricula}. */
	private int id;

	/** Descri��o da {@link SituacaoMatricula}. */
	private String descricao;

	/** Indica se este status est� ativo ou n�o no sistema.
	 * caso n�o esteja ativo n�o deve ser utilizado em lugar nenhum!*/
	private boolean ativo;
	
	/** Indica se a matr�cula � v�lida no semestre. */
	private boolean matriculaValidaNoSemestre;

	/**
	 * Aguardando processamento da matr�cula
	 */
	public static final SituacaoMatricula EM_ESPERA = new SituacaoMatricula(1, "EM ESPERA");

	/**
	 * Matr�cula confirmada no componente
	 */
	public static final SituacaoMatricula MATRICULADO = new SituacaoMatricula(2, "MATRICULADO");

	/**
	 * Matr�cula cancelada pelo DAE, coordena��o de p�s, etc...
	 */
	public static final SituacaoMatricula CANCELADO = new SituacaoMatricula(3, "CANCELADO");

	/**
	 * Aprovado no componente
	 */
	public static final SituacaoMatricula APROVADO	 = new SituacaoMatricula(4, "APROVADO");

	/**
	 * Trancou o componente
	 */
	public static final SituacaoMatricula TRANCADO = new SituacaoMatricula(5, "TRANCADO");

	/**
	 * Reprovado por Nota
	 */
	public static final SituacaoMatricula REPROVADO = new SituacaoMatricula(6, "REPROVADO");

	/**
	 * Reprovado por Falta
	 */
	public static final SituacaoMatricula REPROVADO_FALTA = new SituacaoMatricula(7, "REPROVADO POR FALTA");
	
	/**
	 * Reprovado por m�dia e por faltas
	 */
	public static final SituacaoMatricula REPROVADO_MEDIA_FALTA = new SituacaoMatricula(9, "REPROVADO POR M�DIA E POR FALTAS");

	/**
	 * A matr�cula exclu�da n�o deve ser vista em lugar nenhuma. 
	 * � exclu�da por erro.
	 */
	public static final SituacaoMatricula EXCLUIDA = new SituacaoMatricula(10, "EXCLU�DA");

	/**
	 * Matr�cula indeferida depois do processamento de matr�cula
	 */
	public static final SituacaoMatricula INDEFERIDA = new SituacaoMatricula(11, "INDEFERIDA");


	/**
	 * Matr�cula do aluno em que ele desistiu no per�odo de re-matricula
	 */
	public static final SituacaoMatricula DESISTENCIA = new SituacaoMatricula(12, "DESISTENCIA");

	/**
	 * Matr�cula do aluno que n�o foi conclu�da no per�odo esperada.
	 */
	public static final SituacaoMatricula NAO_CONCLUIDO = new SituacaoMatricula(15, "N�O CONCLU�DO");
	
	/**
	 * Aproveitou o componente e foi dispensando
	 */
	public static final SituacaoMatricula APROVEITADO_DISPENSADO = new SituacaoMatricula(21, "DISPENSADO");

	/**
	 * Cumpriu o componente em outro curso dentro da Institui��o
	 */
	public static final SituacaoMatricula APROVEITADO_CUMPRIU = new SituacaoMatricula(22, "CUMPRIU");

	/**
	 * Fez o componente em outra institui��o e aproveitou na Institui��o
	 */
	public static final SituacaoMatricula APROVEITADO_TRANSFERIDO = new SituacaoMatricula(23, "TRANSFERIDO");


	// Constructors

	/** default constructor */
	public SituacaoMatricula() {
	}

	/** default minimal constructor */
	public SituacaoMatricula(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public SituacaoMatricula(int idSituacaoMatricula, String descricao) {
		this.id = idSituacaoMatricula;
		this.descricao = descricao;
	}

	/** full constructor */
	public SituacaoMatricula(int idSituacaoMatricula, String descricao,
			Set<MatriculaComponente> matriculaDisciplinas) {
		this.id = idSituacaoMatricula;
		this.descricao = descricao;
	}

	public SituacaoMatricula(String d) {
		descricao = d;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") }) 
	@Column(name = "id_situacao_matricula", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idSituacaoMatricula) {
		this.id = idSituacaoMatricula;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Column(name = "matricula_valida_no_semestre", unique = true, nullable = false, insertable = true, updatable = true)
	public boolean isMatriculaValidaNoSemestre() {
		return matriculaValidaNoSemestre;
	}

	public void setMatriculaValidaNoSemestre(boolean matriculaValidaNoSemestre) {
		this.matriculaValidaNoSemestre = matriculaValidaNoSemestre;
	}
	
	/**
	 * Retorna uma lista com as {@link SituacaoMatricula} nas quais o discente n�o foi aprovado.
	 * @return
	 */
	@Transient
	public static SituacaoMatricula[] getNaoAprovados() {
		SituacaoMatricula[] situacoes = {CANCELADO, EM_ESPERA, MATRICULADO,
				REPROVADO, REPROVADO_FALTA, REPROVADO_MEDIA_FALTA, TRANCADO, EXCLUIDA, NAO_CONCLUIDO};
		return situacoes;
	}

	/**
	 * Implementa��o do m�todo equals comparando-se os ids das {@link SituacaoMatricula}.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * Defini��o do hashcode da classe.
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/**
	 * Efetua a valida��o dos dados da classe.
	 */
	public ListaMensagens validate() {
		return null;
	}
	
	/** Situa��es que somam nas integraliza��es do aluno: pagas, matriculas, em espera */
	public static Collection<SituacaoMatricula> getSituacoesPositivas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.addAll(getSituacoesPagas());
		situacoes.add(MATRICULADO);
		situacoes.add(EM_ESPERA);
		return situacoes;
	}

	/**
	 * Situa��es que indicam que o aluno teve sua matr�cula cancelada, exclu�da, indeferida ou trancada.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesNegativas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.addAll(SituacaoMatricula.getSituacoesReprovadas());
		situacoes.add(CANCELADO);
		situacoes.add(EXCLUIDA);
		situacoes.add(INDEFERIDA);
		situacoes.add(TRANCADO);
		situacoes.add(NAO_CONCLUIDO);
		return situacoes;
	}

	/**
	 * Situa��es que indicam que o aluno teve sua matr�cula cancelada, exclu�da, indeferida ou trancada.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesInativas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(CANCELADO);
		situacoes.add(EXCLUIDA);
		situacoes.add(INDEFERIDA);
		return situacoes;
	}
	
	/**
	 * Situa��es aprovadas e aproveitadas
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesPagas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(APROVADO);
		situacoes.addAll(getSituacoesAproveitadas());
		return situacoes;
	}

	/**
	 * Situa��es pagas e matriculadas.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesPagasEMatriculadas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.addAll(getSituacoesMatriculadas());
		situacoes.addAll(getSituacoesPagas());
		return situacoes;
	}

	/**
	 * Array de situa��es pagas.
	 * @return
	 */
	public static SituacaoMatricula[] getSituacoesPagasArray() {
		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesPagas();
		return situacoes.toArray(new SituacaoMatricula[situacoes.size()]);
	}

	/**
	 * Array de situa��es padas e matriculadas.
	 * @return
	 */
	public static SituacaoMatricula[] getSituacoesPagasEMatriculadasArray() {
		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesPagasEMatriculadas();
		return situacoes.toArray(new SituacaoMatricula[situacoes.size()]);
	}
	
	/**
	 * Array de situa��es da implantacao de historico.
	 * @return
	 */
	public static SituacaoMatricula[] getSituacoesImplantadasMedio() {
		Collection<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.addAll(getSituacoesPagasEMatriculadas());
		situacoes.addAll(getSituacoesAbandono());
		return situacoes.toArray(new SituacaoMatricula[situacoes.size()]);
	}

	/**
	 * Situa��es Aproveitadas, seja em outro curso da institui��o, outra institui��o ou sendo dispensado da mesma.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesAproveitadas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(APROVEITADO_CUMPRIU);
		situacoes.add(APROVEITADO_DISPENSADO);
		situacoes.add(APROVEITADO_TRANSFERIDO);
		return situacoes;
	}

	/**
	 * Array de situa��es aproveitadas.
	 * @return
	 */
	public static SituacaoMatricula[] getSituacoesAproveitadasArray() {
		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAproveitadas();
		return situacoes.toArray(new SituacaoMatricula[situacoes.size()]);
	}

	/**
	 * Situa��es de reprova��o por nota, falta ou m�dia e falta.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesReprovadas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(REPROVADO);
		situacoes.add(REPROVADO_FALTA);
		situacoes.add(REPROVADO_MEDIA_FALTA);
		return situacoes;
	}

	/**
	 * Situa��es matriculadas ou conclu�das.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesMatriculadoOuConcluido() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(APROVADO);
		situacoes.add(REPROVADO);
		situacoes.add(REPROVADO_FALTA);
		situacoes.add(REPROVADO_MEDIA_FALTA);
		situacoes.add(MATRICULADO);
		situacoes.add(APROVEITADO_TRANSFERIDO);		
		return situacoes;
	}
	
	/**
	 * Situa��es ativas.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesAtivas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(APROVADO);
		situacoes.add(REPROVADO);
		situacoes.add(REPROVADO_FALTA);
		situacoes.add(REPROVADO_MEDIA_FALTA);
		situacoes.add(EM_ESPERA);
		situacoes.add(MATRICULADO);
		situacoes.add(TRANCADO);
		return situacoes;
	}
	
	/**
	 * Situa��es de matr�culas consideradas como ocupantes de vaga numa turma.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesOcupamVaga() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(EM_ESPERA);
		situacoes.add(MATRICULADO);
		situacoes.add(APROVADO);
		situacoes.add(REPROVADO);
		situacoes.add(REPROVADO_FALTA);
		situacoes.add(REPROVADO_MEDIA_FALTA);
		return situacoes;
	}
	
	/**
	 * Array de situa��es ativas.
	 * @return
	 */
	public static SituacaoMatricula[] getSituacoesAtivasArray() {
		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
		return situacoes.toArray(new SituacaoMatricula[situacoes.size()]);
	}

	/**
	 * Situa��es matriculadas e em espera.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesMatriculadas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(EM_ESPERA);
		situacoes.add(MATRICULADO);
		return situacoes;
	}

	/**
	 * Situa��es pagas ou reprovadas.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesConcluidas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.addAll(getSituacoesPagas());
		situacoes.addAll(getSituacoesReprovadas());
		return situacoes;
	}

	/**
	 * retorna todas as situa��es de matr�cula
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesTodas() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.addAll(getSituacoesConcluidas());
		situacoes.add(EM_ESPERA);
		situacoes.add(MATRICULADO);
		situacoes.add(CANCELADO);
		situacoes.add(TRANCADO);
		situacoes.add(EXCLUIDA);
		situacoes.add(INDEFERIDA);
		situacoes.add(DESISTENCIA);
		situacoes.add(NAO_CONCLUIDO);
		return situacoes;
	}
	
	/**
	 * Situa��es de trancamento ou reprova��o.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesAbandono() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(REPROVADO);
		situacoes.add(REPROVADO_FALTA);
		situacoes.add(REPROVADO_MEDIA_FALTA);
		situacoes.add(TRANCADO);
		return situacoes;
	}
	
	/**
	 * Situa��es para o lan�amento de frequ�ncia.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesFrequencia() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(SituacaoMatricula.APROVADO);
		situacoes.add(SituacaoMatricula.REPROVADO);
		situacoes.add(SituacaoMatricula.REPROVADO_FALTA);
		situacoes.add(SituacaoMatricula.REPROVADO_MEDIA_FALTA);
		situacoes.add(SituacaoMatricula.MATRICULADO);
		situacoes.add(SituacaoMatricula.TRANCADO);
		return situacoes;
	}

	/**
	 * Situa��es que possuem v�nculo com uma turma.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesComVinculoTurma() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(APROVADO);
		situacoes.add(CANCELADO);
		situacoes.add(MATRICULADO);
		situacoes.add(REPROVADO);
		situacoes.add(REPROVADO_FALTA);
		situacoes.add(REPROVADO_MEDIA_FALTA);
		situacoes.add(TRANCADO);
		situacoes.add(NAO_CONCLUIDO);
		return situacoes;
	}
	
	/**
	 * Situa��es poss�veis no ensino m�dio.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesMedio() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(EM_ESPERA);
		situacoes.add(MATRICULADO);
		situacoes.add(CANCELADO);
		situacoes.add(APROVADO);
		situacoes.add(TRANCADO);
		situacoes.add(REPROVADO);
		situacoes.add(REPROVADO_FALTA);
		situacoes.add(REPROVADO_MEDIA_FALTA);
		situacoes.add(EXCLUIDA);
		return situacoes;
	}
	
	/**
	 * Array de situa��es que possuem v�nculo com uma turma.
	 * @return
	 */
	public static SituacaoMatricula[] getSituacoesComVinculoTurmaArray() {
		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesComVinculoTurma();
		return situacoes.toArray(new SituacaoMatricula[situacoes.size()]);
	}
	
	/**
	 * Situa��es nas quais o discente pode acessar a turma virtual.
	 * 
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesAcessoTurmaVirtual() {
		Collection<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>();
		
		situacoes.add(SituacaoMatricula.MATRICULADO);
		situacoes.add(SituacaoMatricula.APROVADO);
		situacoes.addAll(SituacaoMatricula.getSituacoesReprovadas());
		
		return situacoes;
	}
	
	/**
	 * Retorna a situa��o referente ao id passado.
	 * @param id
	 * @return
	 */
	public static SituacaoMatricula getSituacao(int id) {
		List<SituacaoMatricula> situacoes = (List<SituacaoMatricula>) getSituacoesTodas();
		return situacoes.get(situacoes.indexOf(new SituacaoMatricula(id)));
	}

	/**
	 * Override do m�todo toString. Mostra a descri��o da situa��o.
	 */
	@Override
	public String toString() {
		return descricao;
	}

	/**
	 * Situa��es poss�veis no ensino m�dio.
	 * @return
	 */
	public static Collection<SituacaoMatricula> getSituacoesConcluidasNormal() {
		ArrayList<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
		situacoes.add(APROVADO);
		situacoes.add(REPROVADO);
		situacoes.add(REPROVADO_FALTA);
		situacoes.add(REPROVADO_MEDIA_FALTA);
		return situacoes;
	}
}