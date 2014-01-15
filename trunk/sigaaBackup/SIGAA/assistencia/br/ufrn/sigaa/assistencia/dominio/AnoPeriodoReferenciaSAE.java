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
 * Representa ano/período que vai estar ativo para ser utilizado como referência
 * pelo SAE quando ocorrer mudança de semestre. Mesmo o semestre mudando, os
 * alunos vão continuar com sua bolsa válida, pois por default o semestre
 * anterior irá continuar tendo validade até que o SAE resolva alterar o
 * semestre de referência.
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

	/** Período correspondente. */
	private Integer periodo;

	/** Ano alimentação correspondente. */
	@Column(name = "ano_alimentacao")
	private int anoAlimentacao;

	/** Período alimentação correspondente. */
	@Column(name = "periodo_alimentacao")
	private int periodoAlimentacao;

	/**
	 * Exibe um texto informativo com datas relativas ao processo seletivo para
	 * os discentes quando os mesmos tentam solicitar bolsa auxilio. Essas
	 * informações são atualizadas pelo SAE a cada semestre.
	 */
	@Column(name = "texto_tela_aviso_discentes")
	private String textoTelaAvisoDiscentes;

	/**
	 * Identificador do arquivo anexado (opcional) com informações adicionais
	 * sobre solicitações de bolsa-auxílio.
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
	
	/** Retorna o ano período. */
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