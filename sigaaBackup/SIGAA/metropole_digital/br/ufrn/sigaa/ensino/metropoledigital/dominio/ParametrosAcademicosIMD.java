package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


import br.ufrn.arq.dominio.PersistDB;

/**
 * Parametros acadêmicos para os cursos do IMD.
 * 
 * @author Gleydson
 * @author Rafael Silva
 * 
 */
@Entity
@Table(name = "parametros_academicos", schema = "metropole_digital")
public class ParametrosAcademicosIMD implements PersistDB {
	/** ID da Entidade */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "metropole_digital.parametros_academicos_id_parametros_academicos_seq") })
	@Column(name = "id_parametros_academicos", unique = true, insertable = true, updatable = true, nullable = false)
	private int id;
	
	/** Identifica se o parâmetro está ativo ou não */
	@Column(name="vigente")
	private boolean vigente;
	
	/** Frequência mínima para Aprovação */
	@Column(name="frequencia_minima_aprovacao")	
	private double frequenciaMinimaAprovacao;
	
	/** Nota Mínima para a recuperação */
	@Column(name="nota_minima_recuperacao")
	private double notaMinimaRecuperacao; // 3
	
	/** Nota mínima para reprovação do componente */
	@Column(name="nota_reprovacao_componente")
	private double notaReprovacaoComponente; //3
	
	/** Média mínima para considerar o aluno aprovado */
	@Column(name="media_aprovacao")
	private double mediaAprovacao; //5
	
	/** Data de Cadastro do dos parâmetros*/
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/** Fim da vigência dos parâmetros*/
	@Column(name="data_inativacao")
	private Date dataInativacao;
		
	
	// GETTERS AND SETTERS
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isVigente() {
		return vigente;
	}

	public void setVigente(boolean vigente) {
		this.vigente = vigente;
	}

	public double getFrequenciaMinimaAprovacao() {
		return frequenciaMinimaAprovacao;
	}

	public void setFrequenciaMinimaAprovacao(double frequenciaMinimaAprovacao) {
		this.frequenciaMinimaAprovacao = frequenciaMinimaAprovacao;
	}

	public double getNotaMinimaRecuperacao() {
		return notaMinimaRecuperacao;
	}

	public void setNotaMinimaRecuperacao(double notaMinimaRecuperacao) {
		this.notaMinimaRecuperacao = notaMinimaRecuperacao;
	}

	public double getNotaReprovacaoComponente() {
		return notaReprovacaoComponente;
	}

	public void setNotaReprovacaoComponente(double notaReprovacaoComponente) {
		this.notaReprovacaoComponente = notaReprovacaoComponente;
	}

	public double getMediaAprovacao() {
		return mediaAprovacao;
	}

	public void setMediaAprovacao(double mediaAprovacao) {
		this.mediaAprovacao = mediaAprovacao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataInativacao() {
		return dataInativacao;
	}

	public void setDataInativacao(Date dataInativacao) {
		this.dataInativacao = dataInativacao;
	}
}
