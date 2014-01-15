/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 17/05/2011
 *
 */
package br.ufrn.sigaa.ouvidoria.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Calendar;
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
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe que representa uma manifestação cadastrada para a ouvidoria da instituição.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="manifestacao")
public class Manifestacao implements PersistDB {
    
    /**
     * Chave primária da {@link Manifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_manifestacao")
    private int id;
    
    /**
     * Armazena o número de protocolo gerado no cadastro da {@link Manifestacao}.
     */
    private String numero;
    
    /**
     * Armazena o assunto definido para a manifestação. Esse dado é selecionado pelo {@link InteressadoManifestacao}, 
     * mas pode ser modificado pelo ouvidor, no ato de encaminhamento da {@link Manifestacao}.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_assunto_manifestacao")
    private AssuntoManifestacao assuntoManifestacao;
    
    /**
     * Armazena o estado atual da {@link Manifestacao}.
     * 
     * @see {@link StatusManifestacao}
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_status_manifestacao")
    private StatusManifestacao statusManifestacao;
    
    /**
     * Armazena o modo de cadastro da {@link Manifestacao}.
     * 
     * @see {@link OrigemManifestacao}
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_origem_manifestacao")
    private OrigemManifestacao origemManifestacao;
    
    /**
     * Armazena o tipo de manifestação definido para a {@link Manifestacao}.
     * 
     * @see {@link TipoManifestacao}
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_tipo_manifestacao")
    private TipoManifestacao tipoManifestacao;
    
    /**
     * Armazena os dados do solicitante da {@link Manifestacao}.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_interessado_manifestacao")
    private InteressadoManifestacao interessadoManifestacao;
    
    /** Referência para o identificador do arquivo anexado a manifestação*/
	@Column(name="id_anexo")
	private Integer idAnexo;
	

	/**
     * Armazena o título indicado para a {@link Manifestacao}.
     */
    private String titulo;
    
    /**
     * Armazena a mensagem enviada como corpo da {@link Manifestacao}.
     */
    private String mensagem;
    
    /**
     * Registro de entrada da {@link Manifestacao}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_cadastro")
    @CriadoPor
    private RegistroEntrada registroCadastro;

    /**
     * Data de criação da {@link Manifestacao}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    @CriadoEm
    private Date dataCadastro;
    
    /**
     * Indica se os dados do usuário devem ser mantidos em sigilo.
     */
    private boolean anonima;
    
    /**
     * Indica se a manifestação já foi lida.
     */
    @Transient
    private boolean lida = true;
    
    /**
     * Indica se o interessado solicitou anonimato no momento do cadastro.
     */
    @Transient
    private boolean anonimatoSolicitado;
    
    /**
     * Entidade transiente que guarda, caso exista, a unidade responsável pela resposta da manifestação.
     */
    @Transient
    private Unidade unidadeResponsavel;

    /**
     * Construtor padrão.
     */
    public Manifestacao() {
    }
    
    public Manifestacao(int categoriaSolicitante, int origemManifestacao, int statusManifestacao, Pessoa pessoa) {
		this.interessadoManifestacao = new InteressadoManifestacao(categoriaSolicitante, pessoa);
		this.origemManifestacao = OrigemManifestacao.getOrigemManifestacao(origemManifestacao);
		this.statusManifestacao = StatusManifestacao.getStatusManifestacao(statusManifestacao);
		this.assuntoManifestacao = new AssuntoManifestacao();
		this.assuntoManifestacao.setCategoriaAssuntoManifestacao(new CategoriaAssuntoManifestacao());
		this.tipoManifestacao = new TipoManifestacao();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIdAnexo() {
    	return idAnexo;
    }
    
    public void setIdAnexo(Integer idAnexo) {
    	this.idAnexo = idAnexo;
    }
    
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public AssuntoManifestacao getAssuntoManifestacao() {
        return assuntoManifestacao;
    }

    public void setAssuntoManifestacao(AssuntoManifestacao assuntoManifestacao) {
        this.assuntoManifestacao = assuntoManifestacao;
    }

    public StatusManifestacao getStatusManifestacao() {
        return statusManifestacao;
    }

    public void setStatusManifestacao(StatusManifestacao statusManifestacao) {
        this.statusManifestacao = statusManifestacao;
    }

    public OrigemManifestacao getOrigemManifestacao() {
        return origemManifestacao;
    }

    public void setOrigemManifestacao(OrigemManifestacao origemManifestacao) {
        this.origemManifestacao = origemManifestacao;
    }

    public TipoManifestacao getTipoManifestacao() {
        return tipoManifestacao;
    }

    public void setTipoManifestacao(TipoManifestacao tipoManifestacao) {
        this.tipoManifestacao = tipoManifestacao;
    }

    public InteressadoManifestacao getInteressadoManifestacao() {
        return interessadoManifestacao;
    }

    public void setInteressadoManifestacao(
    	InteressadoManifestacao interessadoManifestacao) {
        this.interessadoManifestacao = interessadoManifestacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public RegistroEntrada getRegistroCadastro() {
        return registroCadastro;
    }

    public void setRegistroCadastro(RegistroEntrada registroCadastro) {
        this.registroCadastro = registroCadastro;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

	public boolean isAnonima() {
		return anonima;
	}

	public void setAnonima(boolean anonima) {
		this.anonima = anonima;
	}

	public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }

    public boolean isAnonimatoSolicitado() {
		return anonimatoSolicitado;
	}

	public void setAnonimatoSolicitado(boolean anonimatoSolicitado) {
		this.anonimatoSolicitado = anonimatoSolicitado;
	}

	public Unidade getUnidadeResponsavel() {
		return unidadeResponsavel;
	}

	public void setUnidadeResponsavel(Unidade unidadeResponsavel) {
		this.unidadeResponsavel = unidadeResponsavel;
	}

	/**
     * Retorna o ano de cadastro da {@link Manifestacao}.
     * 
     * @return
     */
    @Transient
    public int getAno() {
		if(dataCadastro != null) {
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(dataCadastro);
		    
		    return cal.get(Calendar.YEAR);
		}
		
		return 0;
    }
    
    /**
     * Retorna uma String contendo o número e o ano da manifestação, separados por uma <code>/</code>
     * 
     * @return
     */
    @Transient
    public String getNumeroAno() {
    	return String.format("%s/%d", getNumero(), getAno());
    }
    
    /**
     * Indica se a manifestação está designada para o responsável.
     * @return
     */
    @Transient
    public boolean isDesignada() {
    	return statusManifestacao != null && StatusManifestacao.DESIGNADA_RESPONSAVEL == statusManifestacao.getId();
    }
    
    /**
     * Indica se a manifestação está aguardando parecer da chefia de unidade.
     * @return
     */
    @Transient
    public boolean isAguardandoParecer() {
    	return statusManifestacao != null && StatusManifestacao.AGUARDANDO_PARECER == statusManifestacao.getId();
    }
    
    /**
     * Indica se a manifestação pode ser designada.
     * @return
     */
    @Transient
    public boolean isPassivelDesignacao() {
    	return statusManifestacao != null && !isDesignada() && !isAguardandoParecer();
    }
    
    /**
     * Indica se a manifestação já foi respondida.
     * @return
     */
    @Transient
    public boolean isRespondida() {
    	return statusManifestacao != null && StatusManifestacao.RESPONDIDA == statusManifestacao.getId();
    }
    
    /**
     * Indica se a manifestação já foi finalizada.
     * @return
     */
    @Transient
    public boolean isFinalizada() {
    	return statusManifestacao != null && StatusManifestacao.FINALIZADA == statusManifestacao.getId();
    }
    
    /**
     * Indica se a manifestação está esperando esclarecimento pelo solicitante.
     * @return
     */
    @Transient
    public boolean isEsperandoEsclarecimento() {
    	return statusManifestacao != null && StatusManifestacao.ESPERANDO_ESCLARECIMENTO == statusManifestacao.getId();
    }
    
    @Override
    public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
    }

    @Override
    public boolean equals(Object obj) {
		if (this == obj)
		    return true;
		if (obj == null)
		    return false;
		if (getClass() != obj.getClass())
		    return false;
		Manifestacao other = (Manifestacao) obj;
		if (id != other.id)
		    return false;
		if (numero == null) {
		    if (other.numero != null)
			return false;
		} else if (!numero.equals(other.numero))
		    return false;
		return true;
    }

    /**
     * Realiza a validação completa da manifestação.
     * Valida os dados do interessado de acordo com o parâmetro passado.
     * 
     * @param validarDadosInteressado
     * @return
     */
    public ListaMensagens validateCompleto(boolean validarDadosInteressado) {
		ListaMensagens erros = new ListaMensagens();
		
		if(validarDadosInteressado) {
		    validateDadosInteressadoNaoAutenticado(erros);
		}
		
		//Dados comuns aos formulários
		validateDadosManifestacao(erros);
		
		//Dados do formulário da ouvidoria
		validateFormularioOuvidoria(erros);
		
		return erros;
    }

    /**
     * Valida os dados da manifestação cadastrados através do formulário da ouvidoria.
     * 
     * @param erros
     */
    public void validateFormularioOuvidoria(ListaMensagens erros) {
		if(interessadoManifestacao != null)
		    ValidatorUtil.validateRequired(interessadoManifestacao.getCategoriaSolicitante().getId(), "Categoria do Solicitante", erros);
		ValidatorUtil.validateRequired(origemManifestacao, "Origem da Manifestação", erros);
		ValidatorUtil.validateRequired(tipoManifestacao, "Tipo da Manifestação", erros);
    }

    /**
     * Valida os dados gerais de uma manifestação.
     * 
     * @param erros
     */
    public void validateDadosManifestacao(ListaMensagens erros) {
		if(assuntoManifestacao != null)
		    ValidatorUtil.validateRequired(assuntoManifestacao.getCategoriaAssuntoManifestacao(), "Categoria do Assunto", erros);
		ValidatorUtil.validateRequired(assuntoManifestacao, "Assunto", erros);
		ValidatorUtil.validateRequired(tipoManifestacao, "Tipo da Manifestação", erros);
		ValidatorUtil.validateRequired(titulo, "Título", erros);
		ValidatorUtil.validateRequired(mensagem, "Mensagem", erros);
    }

    /**
     * Valida os dados de um manifestante da comunidade externa.
     * 
     * @param erros
     */
    public void validateDadosInteressadoNaoAutenticado(ListaMensagens erros) {
		InteressadoNaoAutenticado interessadoNaoAutenticado = interessadoManifestacao.getDadosInteressadoManifestacao().getInteressadoNaoAutenticado();
		ValidatorUtil.validateRequired(interessadoNaoAutenticado.getNome(), "Nome", erros);
		if(isNotEmpty(interessadoNaoAutenticado.getNome()))
			ValidatorUtil.validateMinLength(interessadoNaoAutenticado.getNome(), 3, "Nome", erros);
		ValidatorUtil.validateRequired(interessadoNaoAutenticado.getEmail(), "E-Mail", erros);
		
		ValidatorUtil.validateEmail(interessadoNaoAutenticado.getEmail(), "E-Mail", erros);
		ValidatorUtil.validateTelefone(interessadoNaoAutenticado.getTelefone(), "Telefone", erros);
		if(interessadoNaoAutenticado.getEndereco() != null)
			ValidatorUtil.validateCEP(interessadoNaoAutenticado.getEndereco().getCep(), "CEP", erros);
    }

}
