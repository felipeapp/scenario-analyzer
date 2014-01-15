/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 18/05/2011
 *
 */
package br.ufrn.sigaa.ouvidoria.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe que armazena o histórico de movimentação de uma {@link Manifestacao}.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="historico_manifestacao")
public class HistoricoManifestacao implements PersistDB {
    
    /**
     * Chave primária do {@link HistoricoManifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_historico_manifestacao")
    private int id;
    
    /**
     * Indica o tipo do histórico.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_tipo_historico_manifestacao")
    private TipoHistoricoManifestacao tipoHistoricoManifestacao;
    
    /**
     * Armazena a solicitação de esclarecimento enviada.
     */
    private String solicitacao;
    
    /**
     * Armazena a resposta dada à {@link #solicitacao}.
     */
    private String resposta;
    
    /**
     * Resposta dada por um designado à unidade responsável.
     */
    @Column(name="resposta_unidade")
    private String respostaUnidade;
    
    /**
     * Armazena a data de cadastro do {@link HistoricoManifestacao}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    @CriadoEm
    private Date dataCadastro;
    
    /**
     * Registro de entrada do {@link HistoricoManifestacao}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_cadastro")
    @CriadoPor
    private RegistroEntrada registroCadastro;
    
    /**
     * Armazena a {@link Unidade} responsável por responder o {@link HistoricoManifestacao}.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_unidade")
    private Unidade unidadeResponsavel;
    
    /**
     * Armazena a {@link Pessoa} designada a responder o {@link HistoricoManifestacao}.
     */
    @OneToMany(mappedBy="historicoManifestacao")
    private List<DelegacaoUsuarioResposta> delegacoesUsuarioResposta;
    
    /**
     * Armazena a {@link Manifestacao} detentora do {@link HistoricoManifestacao}.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_manifestacao")
    private Manifestacao manifestacao;
    
    /**
     * Indica se o {@link HistoricoManifestacao} já foi lido pela {@link Unidade} ou {@link Pessoa} responsável.
     */
    private boolean lido;
    
    /**
     * Armazena o prazo estabelecido para resposta do {@link HistoricoManifestacao}.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "prazo_resposta")
    private Date prazoResposta;
    
    /**
     * Armazena a data em que foi respondido o {@link HistoricoManifestacao}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_resposta")
    private Date dataResposta;
    
    /**
     * Armazena a data em que foi respondido o {@link HistoricoManifestacao}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_resposta_unidade")
    private Date dataRespostaUnidade;
        	
    /**
     * Entidade transiente que armazena a pessoa responsável pela {@link #unidadeResponsavel}.
     */
    @Transient
    private Pessoa pessoaResponsavel;

    /**
     * Construtor padrão.
     */
    public HistoricoManifestacao() {
    }

    /**
     * Adiciona a delegação passada à listagem {@link #delegacoesUsuarioResposta}.
     * 
     * @param delegacaoUsuarioResposta
     */
    public void adicionarDelegacaoUsuarioResposta(DelegacaoUsuarioResposta delegacaoUsuarioResposta) {
		if(delegacoesUsuarioResposta == null)
		    delegacoesUsuarioResposta = new ArrayList<DelegacaoUsuarioResposta>();
		
		delegacoesUsuarioResposta.add(delegacaoUsuarioResposta);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoHistoricoManifestacao getTipoHistoricoManifestacao() {
        return tipoHistoricoManifestacao;
    }

    public void setTipoHistoricoManifestacao(
    	TipoHistoricoManifestacao tipoHistoricoManifestacao) {
        this.tipoHistoricoManifestacao = tipoHistoricoManifestacao;
    }

    public String getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(String solicitacao) {
        this.solicitacao = solicitacao;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

	public String getRespostaUnidade() {
		return respostaUnidade;
	}

	public void setRespostaUnidade(String respostaUnidade) {
		this.respostaUnidade = respostaUnidade;
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

    public Unidade getUnidadeResponsavel() {
        return unidadeResponsavel;
    }

    public void setUnidadeResponsavel(Unidade unidadeResponsavel) {
        this.unidadeResponsavel = unidadeResponsavel;
    }

    public List<DelegacaoUsuarioResposta> getDelegacoesUsuarioResposta() {
        return delegacoesUsuarioResposta;
    }

    public void setDelegacoesUsuarioResposta(List<DelegacaoUsuarioResposta> delegacoesUsuarioResposta) {
        this.delegacoesUsuarioResposta = delegacoesUsuarioResposta;
    }

    public Manifestacao getManifestacao() {
        return manifestacao;
    }

    public void setManifestacao(Manifestacao manifestacao) {
        this.manifestacao = manifestacao;
    }

    public boolean isLido() {
        return lido;
    }

    public void setLido(boolean lido) {
        this.lido = lido;
    }

    public Date getPrazoResposta() {
        return prazoResposta;
    }

    public void setPrazoResposta(Date prazoResposta) {
        this.prazoResposta = prazoResposta;
    }

    public Date getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(Date dataResposta) {
        this.dataResposta = dataResposta;
    }
    
    public Date getDataRespostaUnidade() {
		return dataRespostaUnidade;
	}

	public void setDataRespostaUnidade(Date dataRespostaUnidade) {
		this.dataRespostaUnidade = dataRespostaUnidade;
	}

	public Pessoa getPessoaResponsavel() {
		return pessoaResponsavel;
	}

	public void setPessoaResponsavel(Pessoa pessoa) {
		this.pessoaResponsavel = pessoa;
	}

	/**
	 * Retorna os dias de atraso para resposta da manifestação.
	 * 
	 * @return
	 */
	@Transient
    public int getDiasAtraso() {
		if(prazoResposta != null && prazoResposta.getTime() < new Date().getTime()) {
			if(dataResposta != null) {
				return CalendarUtils.calculoDias(prazoResposta, dataResposta);
			}
			else {
				return CalendarUtils.calculoDias(prazoResposta, new Date());
			}
		}
		
		return 0;
    }
    
	/**
	 * Indica se o histórico é do tipo OUVIDORIA_INTERESSADO.
	 * 
	 * @return
	 */
    @Transient
    public boolean isRespostaUsuario() {
    	return tipoHistoricoManifestacao != null && tipoHistoricoManifestacao.getId() == TipoHistoricoManifestacao.OUVIDORIA_INTERESSADO;
    }
    
	/**
	 * Indica se o histórico é do tipo ESCLARECIMENTO_OUVIDORIA_INTERESSADO.
	 * 
	 * @return
	 */
    public boolean isSolicitacaoEsclarecimento() {
    	return tipoHistoricoManifestacao != null && tipoHistoricoManifestacao.getId() == TipoHistoricoManifestacao.ESCLARECIMENTO_OUVIDORIA_INTERESSADO;
    }
    
	/**
	 * Indica se o histórico é do tipo INTERESSADO_OUVIDORIA.
	 * 
	 * @return
	 */
    public boolean isRespostaEsclarecimento() {
    	return tipoHistoricoManifestacao != null && tipoHistoricoManifestacao.getId() == TipoHistoricoManifestacao.INTERESSADO_OUVIDORIA || tipoHistoricoManifestacao.getId() == TipoHistoricoManifestacao.INTERESSADO_RESPONSAVEL;
    }
    
    /**
	 * Indica se o histórico é do tipo OUVIDORIA_RESPONSAVEL.
	 * 
	 * @return
	 */
    @Transient
    public boolean isOuvidoriaResponsavel() {
    	return tipoHistoricoManifestacao != null && tipoHistoricoManifestacao.getId() == TipoHistoricoManifestacao.OUVIDORIA_RESPONSAVEL;
    }

}
