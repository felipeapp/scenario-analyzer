/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/03/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * 
 * Tipos de bolsas de interesse do discente.
 * @author 
 *
 */
@Entity
@Table(schema = "graduacao", name = "tipo_interessado_bolsa")
public class TipoInteressadoBolsa implements PersistDB {
	
	public static final TipoInteressadoBolsa PESQUISA		= new TipoInteressadoBolsa(3);
	public static final TipoInteressadoBolsa APOIO_TECNICO	= new TipoInteressadoBolsa(4);
	public static final TipoInteressadoBolsa EXTENSAO	= new TipoInteressadoBolsa(5);
	public static final TipoInteressadoBolsa MONITORIA	= new TipoInteressadoBolsa(6);
	public static final TipoInteressadoBolsa ACAO_ASSOCIADA	= new TipoInteressadoBolsa(7);
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })

	@Column(name = "id_tipo")
	private int id;

	@Column(name = "descricao")
	private String descricao;

	public TipoInteressadoBolsa() {
	}
	
	public TipoInteressadoBolsa(int id) {
		this.id = id;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoInteressadoBolsa other = (TipoInteressadoBolsa) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
}
