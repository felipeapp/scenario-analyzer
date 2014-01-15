package br.ufrn.sigaa.ensino.latosensu.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Tipos de Financiamentos dos Curso Lato Sensu
 *  
 * @author guerethes
 */
@Entity
@Table(name = "financiamento_curso_lato", schema = "lato_sensu")
public class FinanciamentoCursoLato implements Validatable {
	
	/** Chave primária do finaciamento do Curso Lato Sensu */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_financiamento_curso_lato", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Descrição do Financiamento do Curso Lato Sensu */
	private String descricao;
	
	/** Indica se o financiamento está ativo ou não */
	@CampoAtivo
	private boolean ativo;

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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}
	
}