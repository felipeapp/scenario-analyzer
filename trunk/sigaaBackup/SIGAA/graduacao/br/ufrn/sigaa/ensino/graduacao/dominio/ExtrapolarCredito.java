/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Entidade que registra a permiss�o para extrapolar os limites de cr�ditos (m�ximos ou m�nimos) de turmas matriculadas
 * em um semestre por discente, estabelecido por sua estrutura curricular.
 * 
 * @author Henrique Andr�
 */
@Entity
@Table(name = "extrapolar_credito", schema = "graduacao")
public class ExtrapolarCredito implements PersistDB {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_extrapolar_credito")
	private int id;

	/** Ano em que a permiss�o para extrapolar a carga hor�ria � v�lida. */
	@Column(name = "ano")
	private Integer ano;

	/** Per�odo em que a permiss�o para extrapolar a carga hor�ria � v�lida. */
	@Column(name = "periodo")
	private Integer periodo;
	
	/** Valor m�ximo da carga hor�ria extrapolada. */
	@Column(name = "cr_maximo_extrapolado")
	private Integer crMaximoExtrapolado;
	
	/** Valor m�nimo da carga hor�ria extrapolada. */
	@Column(name = "cr_minimo_extrapolado")
	private Integer crMinimoExtrapolado;

	/** Indica se o discente pode extrapolar a carga hor�ria minima. */
	@Deprecated
	@Column(name = "extrapolar_minimo")
	private boolean extrapolarMinimo;

	/** Indica se o discente pode extrapolar a carga hor�ria m�xima. */
	@Deprecated
	@Column(name = "extrapolar_maximo")
	private boolean extrapolarMaximo;

	/** Discente ao qual se concede a permiss�o para extrapolar a carga hor�ria. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private DiscenteGraduacao discente;
	
	/** Indica se esta permiss�o � ativa. */
	@Column(name = "ativo")
	private boolean ativo;

	/** Data que esta permiss�o foi cadastrada. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro de Entrada do usu�rio respons�vel pelo cadastro desta permiss�o. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	public ExtrapolarCredito() {
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	@Deprecated
	public boolean isExtrapolarMaximo() {
		return extrapolarMaximo;
	}

	@Deprecated
	public void setExtrapolarMaximo(boolean extrapolarMaximo) {
		this.extrapolarMaximo = extrapolarMaximo;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	@Deprecated
	public boolean isExtrapolarMinimo() {
		return extrapolarMinimo;
	}

	@Deprecated
	public void setExtrapolarMinimo(boolean extrapolarMinimo) {
		this.extrapolarMinimo = extrapolarMinimo;
	}

	public Integer getCrMaximoExtrapolado() {
		return crMaximoExtrapolado;
	}

	public void setCrMaximoExtrapolado(Integer crMaximoExtrapolado) {
		this.crMaximoExtrapolado = crMaximoExtrapolado;
	}

	public Integer getCrMinimoExtrapolado() {
		return crMinimoExtrapolado;
	}

	public void setCrMinimoExtrapolado(Integer crMinimoExtrapolado) {
		this.crMinimoExtrapolado = crMinimoExtrapolado;
	}

}
