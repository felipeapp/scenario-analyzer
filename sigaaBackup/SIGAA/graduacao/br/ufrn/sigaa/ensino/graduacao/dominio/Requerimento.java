/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Representa o requerimento padrão
 * 
 * @author Henrique Andre
 */

@Entity
@Table(name = "requerimento", schema = "graduacao", uniqueConstraints = {})
public class Requerimento implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_requerimento")
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_discente")
	private Discente discente;

	@ManyToOne
	@JoinColumn(name = "id_tipo_requerimento")
	private TipoRequerimento tipo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizado;

	@Column(name = "solicitacao")
	private String solicitacao;

	@Column(name = "visto_coordenador")
	private boolean vistoCoordenador;

	@ManyToOne()
	@JoinColumn(name = "id_status_requerimento")
	private StatusRequerimento status;

	@Column(name = "codigo_processo")
	private Integer codigoProcesso;

	@Column(name = "trancar_qtd_semestres")
	private Integer trancarQtdSemestres;

	@Column(name = "trancar_ano_base")
	private Integer anoBase;

	@Column(name = "trancar_periodo_base")
	private Integer periodoBase;

	/*
	 * Usado para indicar se o processo
	 */
	@Transient
	private boolean gerarCodigo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public TipoRequerimento getTipo() {
		return tipo;
	}

	public void setTipo(TipoRequerimento tipo) {
		this.tipo = tipo;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}

	public boolean isVistoCoordenador() {
		return vistoCoordenador;
	}

	public void setVistoCoordenador(boolean vistoCoordenador) {
		this.vistoCoordenador = vistoCoordenador;
	}

	public StatusRequerimento getStatus() {
		return status;
	}

	public void setStatus(StatusRequerimento status) {
		this.status = status;
	}

	public Date getDataAtualizado() {
		return dataAtualizado;
	}

	public void setDataAtualizado(Date dataAtualizado) {
		this.dataAtualizado = dataAtualizado;
	}

	public boolean isAlunoEnviou() {
		if (status.getId() != StatusRequerimento.ABERTO_PELO_ALUNO)
			return true;
		return false;
	}
	
	public boolean isAtendida() {
		if (status.getId() == StatusRequerimento.ATENDIDO)
			return true;
		return false;
	}

	public Integer getCodigoProcesso() {
		return codigoProcesso;
	}

	public void setCodigoProcesso(Integer codigoProcesso) {
		this.codigoProcesso = codigoProcesso;
	}

	public Integer getTrancarQtdSemestres() {
		return trancarQtdSemestres;
	}

	public void setTrancarQtdSemestres(Integer trancarQtdSemestres) {
		if (tipo.getId() == TipoRequerimento.TRANCAMENTO_PROGRAMA)
			this.trancarQtdSemestres = trancarQtdSemestres;
	}

	public boolean isGerarCodigo() {
		return gerarCodigo;
	}

	public void setGerarCodigo(boolean gerarCodigo) {
		this.gerarCodigo = gerarCodigo;
	}

	public Integer getAnoBase() {
		return anoBase;
	}

	public void setAnoBase(Integer anoBase) {
		this.anoBase = anoBase;
	}

	public Integer getPeriodoBase() {
		return periodoBase;
	}

	public void setPeriodoBase(Integer periodoBase) {
		this.periodoBase = periodoBase;
	}

}
