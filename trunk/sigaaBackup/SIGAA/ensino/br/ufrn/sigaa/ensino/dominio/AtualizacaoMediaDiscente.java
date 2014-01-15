/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 14/07/2008
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que registra as alterações em um IRA de alunos de graduação
 * O nome da tabela é atualização pois ela grava todas as atualizações e não só as 
 * alterações de média.
 * 
 * @author Gleydson
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "atualizacao_media_discente", schema = "ensino")
public class AtualizacaoMediaDiscente implements PersistDB {

	@Id
	@Column(name="id_atualizacao_media_discente")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	private float media;

	private Date data;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private Discente discente;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getMedia() {
		return media;
	}

	public void setMedia(float media) {
		this.media = media;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

}
