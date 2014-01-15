package br.ufrn.sigaa.ouvidoria.dominio;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Um acompanhamento representa uma cópia da manifestação que foi enviada para
 * uma pessoa ou unidade da instituição.
 * Ao receber uma manifestação para acompanhamento, essa pessoa (pessoa que recebeu
 * o acompanhamento diretamente ou chefe da unidade que recebeu a cópia) passa a ter
 * acesso aos dados da manifestação e pode acompanhá-la.
 * 
 * @author Bernardo
 *
 */
@Entity
@Table(schema="ouvidoria",name="acompanhamento_manifestacao")
public class AcompanhamentoManifestacao implements PersistDB {

	/**
     * Chave primária do {@link AcompanhamentoManifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_acompanhamento_manifestacao")
    private int id;
    
    /**
     * Pessoa com autorização para acompanhar a manifestação.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_pessoa")
    private Pessoa pessoa;
    
    /**
     * Unidade de responsabilidade que recebeu o acompanhamento.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_unidade")
    private Unidade unidadeResponsabilidade;
    
    /**
     * Define se o acompanhamento está ativo ou não.
     */
    private boolean ativo;
    
    /**
     * Manifestação que a Pessoa pode ter acesso.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_manifestacao")
    private Manifestacao manifestacao;
    
    /**
     * Registro de entrada do {@link AcompanhamentoManifestacao}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_cadastro")
    @CriadoPor
    private RegistroEntrada registroCadastro;

    /**
     * Data de criação do {@link AcompanhamentoManifestacao}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    @CriadoEm
    private Date dataCadastro;

    public AcompanhamentoManifestacao() {
	}
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Manifestacao getManifestacao() {
		return manifestacao;
	}

	public void setManifestacao(Manifestacao manifestacao) {
		this.manifestacao = manifestacao;
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

	public Unidade getUnidadeResponsabilidade() {
		return unidadeResponsabilidade;
	}

	public void setUnidadeResponsabilidade(Unidade unidadeResponsabilidade) {
		this.unidadeResponsabilidade = unidadeResponsabilidade;
	}

}
