/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 19/10/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * Classe que controla a sequencia numérica utilizada na geração do número de
 * matrícula dos discentes de todos os níveis de ensino.
 * 
 * @author Ricardo Wendell
 * 
 */
@Entity
@Table(name = "geracao_matricula", schema = "ensino")
public class SequenciaGeracaoMatricula implements PersistDB {

	/** Constante que determina a quantidade de dígitos na sequência numérica da matrícula. */
	public static final int NUMERO_DIGITOS_SEQUENCIA = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.QUANTIDADE_DIGITOS_SEQUENCIA_MATRICULA);
	
	/** Constante que determina o limite da sequência numérica da matrícula. */
	public static final int LIMITE_SEQUENCIA_MATRICULA = (int) (Math.pow(10, NUMERO_DIGITOS_SEQUENCIA) - 1);

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_geracao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Sequencia numérica da matrícula. */
	@Column(name = "matricula")
	private int sequencia;

	/** Nível de ensino da sequência. */
	private Character nivel;

	/** Ano da sequência. */
	private int ano;
	
	/** Período da sequência. */
	private Integer periodo;

	public SequenciaGeracaoMatricula() {
		setSequencia(1);
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getSequencia() {
		return sequencia;
	}

	public void setSequencia(int sequencia) {
		this.sequencia = sequencia;
	}

	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = getNivelGeracaoMatricula(nivel);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/** 
	 * Incrementa a sequência atual em uma unidade.
	 */
	public void incrementarSequencia() {
		sequencia += 1;
	}

	/**
	 * Retorna o nível da geração de matrícula.
	 *
	 * Infantil, Médio e Técnico correspondem ao nível básico.
	 * Mestrado e Doutorado correspondem ao nível Stricto
	 *
	 * @param nivel
	 * @return
	 */
	public static char getNivelGeracaoMatricula(char nivel) {
		switch (nivel) {
			case NivelEnsino.INFANTIL:
			case NivelEnsino.MEDIO:
			case NivelEnsino.TECNICO: return  NivelEnsino.BASICO;
			case NivelEnsino.MESTRADO:
			case NivelEnsino.DOUTORADO: return NivelEnsino.STRICTO;
			case NivelEnsino.RESIDENCIA: return NivelEnsino.RESIDENCIA;
			case NivelEnsino.FORMACAO_COMPLEMENTAR: return NivelEnsino.FORMACAO_COMPLEMENTAR; 
			default: return nivel;
		}
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

}
