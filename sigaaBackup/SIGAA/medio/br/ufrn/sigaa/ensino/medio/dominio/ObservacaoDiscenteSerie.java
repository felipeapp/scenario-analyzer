/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 19/07/2011
 */
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;

/**
 * Observa��es cadastradas para um aluno em cada s�rie, a serem exibidas em seu boletim escolar
 * 
 * @author Arlindo
 *
 */
@Entity
@Table(name = "observacao_discente_serie", schema = "medio")
public class ObservacaoDiscenteSerie implements PersistDB {
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_observacao_discente_serie", nullable = false)
	private int id;

	/** Matr�cula em s�rie do discente que foi cadastrada a observa��o */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matricula_discente_serie")
	private MatriculaDiscenteSerie matricula;

	/** Texto da observa��o cadastrada */
	private String observacao;

	/**
	 * Dados do usu�rio que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro")
	private RegistroEntrada registro;

	/** Data de cadastro */
	private Date data;
	
	/** Indica se est� ativo ou n�o */
	private boolean ativo;
	
	/** Observa��o anterior */
	@Transient
	private ObservacaoDiscenteSerie observacaoAnterior;	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MatriculaDiscenteSerie getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaDiscenteSerie matricula) {
		this.matricula = matricula;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public RegistroEntrada getRegistro() {
		return registro;
	}

	public void setRegistro(RegistroEntrada registro) {
		this.registro = registro;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public ObservacaoDiscenteSerie getObservacaoAnterior() {
		return observacaoAnterior;
	}

	public void setObservacaoAnterior(ObservacaoDiscenteSerie observacaoAnterior) {
		this.observacaoAnterior = observacaoAnterior;
	}
}
