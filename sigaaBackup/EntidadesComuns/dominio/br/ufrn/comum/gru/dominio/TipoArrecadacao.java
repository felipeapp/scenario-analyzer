/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/05/2011
 *
 */
package br.ufrn.comum.gru.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Define os tipos de arrecadações internos aos sistemas da família SIG.
 * 
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(name = "tipo_arrecadacao", schema="gru")
public class TipoArrecadacao implements PersistDB {

	public static final int VESTIBULAR = 1;
	public static final int TRANSFERENCIA_VOLUNTARIA = 2;
	public static final int REINGRESSO = 3;
	public static final int REOPCAO = 4;
	public static final int PROCESSO_SELETIVO_LATO_SENSU = 5;
	public static final int PROCESSO_SELETIVO_STRICTO_SENSU = 6;
	public static final int PROCESSO_SELETIVO_TECNICO = 7;
	public static final int CURSO_E_EVENTOS_EXTENSAO = 8;
	public static final int REVALIDACAO_DIPLOMA = 9;
	public static final int PAGAMENTO_MULTAS_BIBLIOTECA = 10;
	public static final int PROCESSO_SELETIVO_ESCOLAS_ESPECIALIZADAS = 11;
	public static final int MENSALIDADE_CURSO_LATO = 12;
	public static final int CONCURSO_PUBLICO = 13;
	public static final int PROCESSO_SELETIVO_FORMACAO_COMPLEMENTAR = 14;
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="gru.gru_sequence") })
	@Column(name = "id_tipo_arrecadacao")
	private int id;
	
	/** Descrição do tipo de arrecadação da GRU. */
	private String descricao;
	
	/** Código do Recolhimento utilizado na GRU Simples. */
	@ManyToOne
	@JoinColumn(name = "id_codigo_recolhimento")
	private CodigoRecolhimentoGRU codigoRecolhimento;
	
	public TipoArrecadacao() {
	}
	
	public TipoArrecadacao(int id) {
		this();
		setId(id);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return id + " - " + descricao;
	}

	public CodigoRecolhimentoGRU getCodigoRecolhimento() {
		return codigoRecolhimento;
	}

	public void setCodigoRecolhimento(CodigoRecolhimentoGRU codigoRecolhimento) {
		this.codigoRecolhimento = codigoRecolhimento;
	}
}
