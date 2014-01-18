package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

@SuppressWarnings("serial")
@Entity
@Table(schema = "ensino_rede", name = "situacao_docente_rede")
public class SituacaoDocenteRede implements PersistDB {

	/** Constante que define o docente está pendente. */
	public static final int PENDENTE = 1;
	/** Constante que define o docente está desligado. */
	public static final int DESLIGADO = 2;
	/** Constante que define o docente está ativo. */
	public static final int ATIVO = 3;
	
	public SituacaoDocenteRede() {
	}
	
	public SituacaoDocenteRede(int id) {
		this.id = id;
	}
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_situacao", nullable = false)
	private int id;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	@Column(name = "significado", nullable = false)
	private String significado;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSignificado() {
		return significado;
	}

	public void setSignificado(String significado) {
		this.significado = significado;
	}

	/** Retorna uma coleção de situações válidas. 
	 * @return
	 */
	public static Collection<SituacaoDocenteRede> getSituaoesValidas() {
		Collection<SituacaoDocenteRede> situacoes = new ArrayList<SituacaoDocenteRede>();
		situacoes.add(new SituacaoDocenteRede(ATIVO));
		return situacoes;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	/** Retorna uma descrição textual do Status informado.
	 * @param status
	 * @return
	 */
	public static String getDescricao(int status){
		switch (status) {
		case ATIVO:          return "ATIVO";
		case PENDENTE:          return "PENDENTE";
		case DESLIGADO:          return "DESLIGADO";
		default:             return "DESCONHECIDO";
		}
	}
	
}
