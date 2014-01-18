/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
 * Created on 21/08/2013
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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.dominio.Usuario;


/**
 * Representa a solicitação de alteração do docente.
 * @author Diego Jácome
 *
 */
@Entity
@Table(name = "solicitacao_docente_rede", schema = "ensino_rede")
public class SolicitacaoDocenteRede implements PersistDB  {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") }) 	
	@Column(name = "id_solicitacao_docente_rede", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente_rede")
	private DocenteRede docente;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_requerido")
	private TipoDocenteRede tipoRequerido;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_requerida")
	private SituacaoDocenteRede situacaoRequerida;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_atendimento")
	private Usuario usuarioAtendimento;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registro;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_atendimento")
	private RegistroEntrada registroAtendimento;
	
	@Column(name="data_solicitacao")
	private Date dataSolicitacao;
	
	@Column(name="data_atendimento")
	private Date dataAtendimento;
	
	@Column(name="observacao_solicitacao")
	private String observacao;

	@Column(name="observacao_atendimento")
	private String atendimento;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "status")
	private StatusSolicitacaoDocenteRede status;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DocenteRede getDocente() {
		return docente;
	}

	public void setDocente(DocenteRede docente) {
		this.docente = docente;
	}

	public SituacaoDocenteRede getSituacaoRequerida() {
		return situacaoRequerida;
	}

	public void setSituacaoRequerida(SituacaoDocenteRede situacaoRequerida) {
		this.situacaoRequerida = situacaoRequerida;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuarioAtendimento() {
		return usuarioAtendimento;
	}

	public void setUsuarioAtendimento(Usuario usuarioAtendimento) {
		this.usuarioAtendimento = usuarioAtendimento;
	}

	public RegistroEntrada getRegistro() {
		return registro;
	}

	public void setRegistro(RegistroEntrada registro) {
		this.registro = registro;
	}

	public RegistroEntrada getRegistroAtendimento() {
		return registroAtendimento;
	}

	public void setRegistroAtendimento(RegistroEntrada registroAtendimento) {
		this.registroAtendimento = registroAtendimento;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public void setStatus(StatusSolicitacaoDocenteRede status) {
		this.status = status;
	}

	public StatusSolicitacaoDocenteRede getStatus() {
		return status;
	}

	public void setAtendimento(String atendimento) {
		this.atendimento = atendimento;
	}

	public String getAtendimento() {
		return atendimento;
	}

	public void setTipoRequerido(TipoDocenteRede tipoRequerido) {
		this.tipoRequerido = tipoRequerido;
	}

	public TipoDocenteRede getTipoRequerido() {
		return tipoRequerido;
	}	
	
}
