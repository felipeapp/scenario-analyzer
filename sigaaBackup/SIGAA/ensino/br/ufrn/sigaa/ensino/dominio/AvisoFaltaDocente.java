/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '30/04/2010'
 *
 */

package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Representa cada aviso de falta de que discente faz.
 * 
 * @author Henrique André
 */
@Entity
@Table(schema = "ensino", name = "aviso_falta_docente")
public class AvisoFaltaDocente implements PersistDB, Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_aviso_falta_docente")
	private int id;

	@Column(name = "observacao", columnDefinition = HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String observacao;

	/** Data de caadstro do {@link AvisoFaltaDocente}. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	@ManyToOne
	@JoinColumn(name = "id_falta_docente")
	private DadosAvisoFalta dadosAvisoFalta;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public DadosAvisoFalta getDadosAvisoFalta() {
		return dadosAvisoFalta;
	}

	public void setDadosAvisoFalta(DadosAvisoFalta dadosAvisoFalta) {
		this.dadosAvisoFalta = dadosAvisoFalta;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(observacao, "Observações", erros);
		erros.addAll( dadosAvisoFalta.validate() );
		return erros;
	}

}
