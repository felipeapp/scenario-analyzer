package br.ufrn.sigaa.assistencia.dominio;

import java.util.Collection;
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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

@Entity
@Table(name = "restricao_solicitacao", schema = "sae")
public class RestricaoSolicitacaoBolsaAuxilio implements Validatable {
	
	/** Chave primária da bolsa de auxilio */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_restricao_solicitacao_bolsa_auxilio")
	private int id;

	/** Representa os tipos de bolsa auxílio disponíveis */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_bolsa_auxilio")
	private TipoBolsaAuxilio tipoBolsaAuxilio;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_bolsa_auxilio_restricao")
	private TipoBolsaAuxilio bolsasAuxilioRestricao;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao")
	private SituacaoBolsaAuxilio situacao;
	
	/** Indica se o componente é ativo ou não */
	@CampoAtivo(true)
	private boolean ativo = true;

	/** Data de cadastro do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada do usuário que cadastrou. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data da última atualização do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Registro entrada do usuário que realizou a última atualização. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	@Transient
	private Collection<RestricaoSolicitacaoBolsaAuxilio> restricao;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TipoBolsaAuxilio getBolsasAuxilioRestricao() {
		return bolsasAuxilioRestricao;
	}

	public void setBolsasAuxilioRestricao(TipoBolsaAuxilio bolsasAuxilioRestricao) {
		this.bolsasAuxilioRestricao = bolsasAuxilioRestricao;
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

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public TipoBolsaAuxilio getTipoBolsaAuxilio() {
		return tipoBolsaAuxilio;
	}

	public void setTipoBolsaAuxilio(TipoBolsaAuxilio tipoBolsaAuxilio) {
		this.tipoBolsaAuxilio = tipoBolsaAuxilio;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}
	
	public SituacaoBolsaAuxilio getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoBolsaAuxilio situacao) {
		this.situacao = situacao;
	}

	
	public Collection<RestricaoSolicitacaoBolsaAuxilio> getRestricao() {
		return restricao;
	}

	public void setRestricao(Collection<RestricaoSolicitacaoBolsaAuxilio> restricao) {
		this.restricao = restricao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	public ListaMensagens validate(RestricaoSolicitacaoBolsaAuxilio restricao) {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredId(tipoBolsaAuxilio.getId(), "Tipo da bolsa", lista);
		ValidatorUtil.validateRequiredId(restricao.getTipoBolsaAuxilio().getId(), "Tipo de Bolsa Restrição", lista);
		ValidatorUtil.validateRequiredId(restricao.getSituacao().getId(), "Situação da Bolsa", lista);
		
		if ( lista.isEmpty() && restricao != null && !this.restricao.isEmpty() ) {
			for (RestricaoSolicitacaoBolsaAuxilio rest : this.restricao) {
				if ( rest.getBolsasAuxilioRestricao().getId() == restricao.getBolsasAuxilioRestricao().getId() && 
						rest.getSituacao().getId() == restricao.getSituacao().getId() ) {
					lista.addErro("Restrição já cadastrada.");
				}
			}
		}
		
		return lista;
	}
	
	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}