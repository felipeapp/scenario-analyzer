/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
 * Created on 09/08/2013
*/
package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * Representa uma matr�cula do ensino em rede.
 * @author Henrique
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "ensino_rede", name = "matricula_componente_rede")
public class MatriculaComponenteRede implements PersistDB {
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.matricula_componente_seq") })
	@Column(name = "id_matricula_componente", nullable = false)
	private int id;
	
	/** Discente matriculado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private DiscenteAssociado discente;

	/** Turma no qual o discente foi matriculado */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_turma")
	private TurmaRede turma;
	
	/** Situa��o da matr�cula */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao")
	private SituacaoMatricula situacao;

	/** Ano da matr�cula */
	@Column(name = "ano")
	private Integer ano;
	
	/** Per�odo da matr�cula */
	@Column(name = "periodo")
	private Integer periodo;
	
	/** M�s de in�cio da matr�cula */
	@Column(name = "mes_inicio")
	private Integer mesInicio;
	
	/** M�s final da matr�cula */
	@Column(name = "mes_fim")
	private Integer mesFim;
	
	/** Data em que a matr�cula foi cadastrada */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Nova situa��o de matr�cula para altera��o da situa��o atual do discente. */
	@Transient
	private SituacaoMatricula novaSituacaoMatricula;
	
	/** Atributo transiente usado em chekboxes de data tables */
	@Transient
	private boolean selected;
	
	@Transient
	private Double total;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteAssociado getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAssociado discente) {
		this.discente = discente;
	}

	public TurmaRede getTurma() {
		return turma;
	}

	public void setTurma(TurmaRede turma) {
		this.turma = turma;
	}

	public SituacaoMatricula getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoMatricula situacao) {
		this.situacao = situacao;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Integer getMesInicio() {
		return mesInicio;
	}

	public void setMesInicio(Integer mesInicio) {
		this.mesInicio = mesInicio;
	}

	public Integer getMesFim() {
		return mesFim;
	}

	public void setMesFim(Integer mesFim) {
		this.mesFim = mesFim;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setNovaSituacaoMatricula(SituacaoMatricula novaSituacaoMatricula) {
		this.novaSituacaoMatricula = novaSituacaoMatricula;
	}

	public SituacaoMatricula getNovaSituacaoMatricula() {
		return novaSituacaoMatricula;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

}