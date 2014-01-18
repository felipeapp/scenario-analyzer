package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.Unidade;

@Entity
@Table(schema = "ensino", name = "bolsa")
public class Bolsas implements Validatable {	
	
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="ensino.bolsa_sequence") })
	@Column(name = "id_bolsa", unique = true, nullable = false)	
	private int id;
	
	
	/** Bolsista */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_bolsista")
	private Bolsistas bolsista;
    
    /** Tipo Bolsa. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_bolsa")
	private TipoBolsas tipoBolsa;
    
    
    /** Unidade. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade")
    private Unidade unidade;
	
	
	/** Unidade de Trabalho do bolsista. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade_trabalho")
	private Unidade unidadeTrabalho;	
	
	/** Início */
    @Temporal(TemporalType.DATE)
    @Column(name = "inicio")
    private Date inicio;
    
    /** Fim */
    @Temporal(TemporalType.DATE)
    @Column(name = "fim")
    private Date fim;
    
    /** Fim */
    @Temporal(TemporalType.DATE)
    @Column(name = "data_finalizacao")
    private Date dataFinalizacao;
    
    /** Usuário que finalizou a bolsa. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario_finalizacao")
	private Unidade usuarioFinalizacao;    
    

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Bolsistas getBolsista() {
		return bolsista;
	}

	public void setBolsista(Bolsistas bolsista) {
		this.bolsista = bolsista;
	}

	public TipoBolsas getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsas tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Unidade getUnidadeTrabalho() {
		return unidadeTrabalho;
	}

	public void setUnidadeTrabalho(Unidade unidadeTrabalho) {
		this.unidadeTrabalho = unidadeTrabalho;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}	

	public Date getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(Date dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}

	public Unidade getUsuarioFinalizacao() {
		return usuarioFinalizacao;
	}

	public void setUsuarioFinalizacao(Unidade usuarioFinalizacao) {
		this.usuarioFinalizacao = usuarioFinalizacao;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();				
		validateRequired(inicio, "Início",mensagens);
		validateRequired(unidade, "Unidade",mensagens);
		validateRequired(tipoBolsa, "Tipo de Bolsa",mensagens);
		return mensagens;
	}
    
    
    

}
