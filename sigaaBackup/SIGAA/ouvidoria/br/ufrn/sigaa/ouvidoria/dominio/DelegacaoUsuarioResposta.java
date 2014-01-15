/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 18/05/2011
 *
 */
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
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe que representa uma delegação de um usuário que pode responder à manifestação associada.
 * A delegação de um usuário é feita quando não só o responsável pela unidade pode responder essa manifestação.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="delegacao_usuario_resposta")
public class DelegacaoUsuarioResposta implements PersistDB {
    
    /**
     * Chave primária da {@link DelegacaoUsuarioResposta}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_delegacao_usuario_resposta")
    private int id;
    
    /**
     * Pessoa delegada à responder a {@link Manifestacao}.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_pessoa")
    private Pessoa pessoa;
    
    /**
     * Define se a delegação está ativa ou não.
     */
    private boolean ativo;
    
    /**
     * Armazena o histórico associado à delegação.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_historico_manifestacao")
    private HistoricoManifestacao historicoManifestacao;
    
    /**
     * Registro de entrada do {@link AssuntoManifestacao}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_cadastro")
    @CriadoPor
    private RegistroEntrada registroCadastro;

    /**
     * Data de criação do {@link AssuntoManifestacao}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    @CriadoEm
    private Date dataCadastro;
    
    /**
     * Registro de atualização do {@link AssuntoManifestacao}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_atualizacao")
    @AtualizadoPor
    private RegistroEntrada registroAlteracao;
    
    /**
     * Data de atualização do {@link AssuntoManifestacao}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_atualizacao")
    @AtualizadoEm
    private Date dataAtualizacao;

    /**
     * Construtor padrão.
     */
    public DelegacaoUsuarioResposta() {
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

    public HistoricoManifestacao getHistoricoManifestacao() {
        return historicoManifestacao;
    }

    public void setHistoricoManifestacao(HistoricoManifestacao historicoManifestacao) {
        this.historicoManifestacao = historicoManifestacao;
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

    public RegistroEntrada getRegistroAlteracao() {
        return registroAlteracao;
    }

    public void setRegistroAlteracao(RegistroEntrada registroAlteracao) {
        this.registroAlteracao = registroAlteracao;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    @Override
    public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		DelegacaoUsuarioResposta other = (DelegacaoUsuarioResposta) obj;
		if (id != other.id)
		    return false;
		return true;
    }

}
