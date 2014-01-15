package br.ufrn.sigaa.assistencia.dominio;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;

/**
 * Representa ano/per�odo que vai estar ativo para ser utilizado como refer�ncia
 * pelo SAE quando ocorrer mudan�a de semestre. Mesmo o semestre mudando, os
 * alunos v�o continuar com sua bolsa v�lida, pois por default o semestre
 * anterior ir� continuar tendo validade at� que o SAE resolva alterar o
 * semestre de refer�ncia.
 * 
 * @author Agostinho
 */

@Entity
@Table(schema = "sae", name = "ano_periodo_referencia")
public class AnoPeriodoReferenciaSAE implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	/** Ano correspondente. */
	private Integer ano;

	/** Per�odo correspondente. */
	private Integer periodo;

	/** Ano alimenta��o correspondente. */
	@Column(name = "ano_alimentacao")
	private int anoAlimentacao;

	/** Per�odo alimenta��o correspondente. */
	@Column(name = "periodo_alimentacao")
	private int periodoAlimentacao;

	/**
	 * Exibe um texto informativo com datas relativas ao processo seletivo para
	 * os discentes quando os mesmos tentam solicitar bolsa auxilio. Essas
	 * informa��es s�o atualizadas pelo SAE a cada semestre.
	 */
	@Column(name = "texto_tela_aviso_discentes")
	private String textoTelaAvisoDiscentes;

	/**
	 * Identificador do arquivo anexado (opcional) com informa��es adicionais
	 * sobre solicita��es de bolsa-aux�lio.
	 */
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** Aguarda os eventos da agenda */
	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, mappedBy = "anoReferencia")
	@Where(clause = "ativo = 't'")
	@OrderBy("tipoBolsaAuxilio asc, municipio asc")
	private Collection<CalendarioBolsaAuxilio> calendario;

	private boolean vigente;

	@CampoAtivo(true)
	private boolean ativo;

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTextoTelaAvisoDiscentes() {
		return textoTelaAvisoDiscentes;
	}

	public void setTextoTelaAvisoDiscentes(String textoTelaAvisoDiscentes) {
		this.textoTelaAvisoDiscentes = textoTelaAvisoDiscentes;
	}

	public int getAnoAlimentacao() {
		return anoAlimentacao;
	}

	public void setAnoAlimentacao(int anoAlimentacao) {
		this.anoAlimentacao = anoAlimentacao;
	}

	public int getPeriodoAlimentacao() {
		return periodoAlimentacao;
	}

	public void setPeriodoAlimentacao(int periodoAlimentacao) {
		this.periodoAlimentacao = periodoAlimentacao;
	}

	public Collection<CalendarioBolsaAuxilio> getCalendario() {
		return calendario;
	}

	public void setCalendario(Collection<CalendarioBolsaAuxilio> calendario) {
		this.calendario = calendario;
	}

	public boolean isVigente() {
		return vigente;
	}

	public void setVigente(boolean vigente) {
		this.vigente = vigente;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Transient
	public boolean isPossuiArquivo(){
		return idArquivo != null;
	}
	
	/** Retorna o ano per�odo. */
	@Transient
	public String getAnoPeriodo() {
		return ano + "." + periodo;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

}