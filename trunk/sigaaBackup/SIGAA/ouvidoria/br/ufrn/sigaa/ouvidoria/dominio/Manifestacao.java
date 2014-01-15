/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe que representa uma manifesta��o cadastrada para a ouvidoria da institui��o.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="manifestacao")
public class Manifestacao implements PersistDB {
    
    /**
     * Chave prim�ria da {@link Manifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_manifestacao")
    private int id;
    
    /**
     * Armazena o n�mero de protocolo gerado no cadastro da {@link Manifestacao}.
     */
    private String numero;
    
    /**
     * Armazena o assunto definido para a manifesta��o. Esse dado � selecionado pelo {@link InteressadoManifestacao}, 
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
     * Armazena o tipo de manifesta��o definido para a {@link Manifestacao}.
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
    
    /** Refer�ncia para o identificador do arquivo anexado a manifesta��o*/
	@Column(name="id_anexo")
	private Integer idAnexo;
	

	/**
     * Armazena o t�tulo indicado para a {@link Manifestacao}.
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
     * Data de cria��o da {@link Manifestacao}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    @CriadoEm
    private Date dataCadastro;
    
    /**
     * Indica se os dados do usu�rio devem ser mantidos em sigilo.
     */
    private boolean anonima;
    
    /**
     * Indica se a manifesta��o j� foi lida.
     */
    @Transient
    private boolean lida = true;
    
    /**
     * Indica se o interessado solicitou anonimato no momento do cadastro.
     */
    @Transient
    private boolean anonimatoSolicitado;
    
    /**
     * Entidade transiente que guarda, caso exista, a unidade respons�vel pela resposta da manifesta��o.
     */
    @Transient
    private Unidade unidadeResponsavel;

    /**
     * Construtor padr�o.
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
     * Retorna uma String contendo o n�mero e o ano da manifesta��o, separados por uma <code>/</code>
     * 
     * @return
     */
    @Transient
    public String getNumeroAno() {
    	return String.format("%s/%d", getNumero(), getAno());
    }
    
    /**
     * Indica se a manifesta��o est� designada para o respons�vel.
     * @return
     */
    @Transient
    public boolean isDesignada() {
    	return statusManifestacao != null && StatusManifestacao.DESIGNADA_RESPONSAVEL == statusManifestacao.getId();
    }
    
    /**
     * Indica se a manifesta��o est� aguardando parecer da chefia de unidade.
     * @return
     */
    @Transient
    public boolean isAguardandoParecer() {
    	return statusManifestacao != null && StatusManifestacao.AGUARDANDO_PARECER == statusManifestacao.getId();
    }
    
    /**
     * Indica se a manifesta��o pode ser designada.
     * @return
     */
    @Transient
    public boolean isPassivelDesignacao() {
    	return statusManifestacao != null && !isDesignada() && !isAguardandoParecer();
    }
    
    /**
     * Indica se a manifesta��o j� foi respondida.
     * @return
     */
    @Transient
    public boolean isRespondida() {
    	return statusManifestacao != null && StatusManifestacao.RESPONDIDA == statusManifestacao.getId();
    }
    
    /**
     * Indica se a manifesta��o j� foi finalizada.
     * @return
     */
    @Transient
    public boolean isFinalizada() {
    	return statusManifestacao != null && StatusManifestacao.FINALIZADA == statusManifestacao.getId();
    }
    
    /**
     * Indica se a manifesta��o est� esperando esclarecimento pelo solicitante.
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
     * Realiza a valida��o completa da manifesta��o.
     * Valida os dados do interessado de acordo com o par�metro passado.
     * 
     * @param validarDadosInteressado
     * @return
     */
    public ListaMensagens validateCompleto(boolean validarDadosInteressado) {
		ListaMensagens erros = new ListaMensagens();
		
		if(validarDadosInteressado) {
		    validateDadosInteressadoNaoAutenticado(erros);
		}
		
		//Dados comuns aos formul�rios
		validateDadosManifestacao(erros);
		
		//Dados do formul�rio da ouvidoria
		validateFormularioOuvidoria(erros);
		
		return erros;
    }

    /**
     * Valida os dados da manifesta��o cadastrados atrav�s do formul�rio da ouvidoria.
     * 
     * @param erros
     */
    public void validateFormularioOuvidoria(ListaMensagens erros) {
		if(interessadoManifestacao != null)
		    ValidatorUtil.validateRequired(interessadoManifestacao.getCategoriaSolicitante().getId(), "Categoria do Solicitante", erros);
		ValidatorUtil.validateRequired(origemManifestacao, "Origem da Manifesta��o", erros);
		ValidatorUtil.validateRequired(tipoManifestacao, "Tipo da Manifesta��o", erros);
    }

    /**
     * Valida os dados gerais de uma manifesta��o.
     * 
     * @param erros
     */
    public void validateDadosManifestacao(ListaMensagens erros) {
		if(assuntoManifestacao != null)
		    ValidatorUtil.validateRequired(assuntoManifestacao.getCategoriaAssuntoManifestacao(), "Categoria do Assunto", erros);
		ValidatorUtil.validateRequired(assuntoManifestacao, "Assunto", erros);
		ValidatorUtil.validateRequired(tipoManifestacao, "Tipo da Manifesta��o", erros);
		ValidatorUtil.validateRequired(titulo, "T�tulo", erros);
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
