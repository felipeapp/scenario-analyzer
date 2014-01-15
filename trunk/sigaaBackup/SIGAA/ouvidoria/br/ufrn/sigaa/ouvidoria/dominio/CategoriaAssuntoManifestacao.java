/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 17/05/2011
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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;

/**
 * Classe que representa uma categoria de {@link AssuntoManifestacao}.
 * As categorias e seus assuntos são cadastrados pelo ouvidor através do módulo de Ouvidoria.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="categoria_assunto_manifestacao")
public class CategoriaAssuntoManifestacao implements PersistDB {
    
    /**
     * Chave primária da {@link CategoriaAssuntoManifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_categoria_assunto_manifestacao")
    private int id;
    
    /**
     * Descrição textual da {@link CategoriaAssuntoManifestacao}.
     */
    private String descricao;
    
    /**
     * Define se a {@link CategoriaAssuntoManifestacao} está ativa e deve aparecer para os usuários.
     */
    private boolean ativo;
    
    /**
     * Registro de entrada da criação da {@link CategoriaAssuntoManifestacao}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_cadastro")
    @CriadoPor
    private RegistroEntrada registroCadastro;

    /**
     * Data de criação da {@link CategoriaAssuntoManifestacao}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    @CriadoEm
    private Date dataCadastro;

    /**
     * Construtor padrão.
     */
    public CategoriaAssuntoManifestacao() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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
		CategoriaAssuntoManifestacao other = (CategoriaAssuntoManifestacao) obj;
		//testa se as descricoes sao iguais ignorando os acentos e se sao maiusculas ou minusculas. 
		if (StringUtils.toAscii(this.descricao).equalsIgnoreCase(StringUtils.toAscii(other.descricao)))
		return true;
		if (id != other.id)
		    return false;
		return true;
    }

}
