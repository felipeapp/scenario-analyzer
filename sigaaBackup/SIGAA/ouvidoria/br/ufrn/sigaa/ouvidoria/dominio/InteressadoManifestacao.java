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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe que representa um interessado na manifestação.
 * O interessado é aquele que realizou ou cadastrou a manifestação.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="interessado_manifestacao")
public class InteressadoManifestacao implements PersistDB {
    
    /**
     * Chave primária do {@link InteressadoManifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_interessado_manifestacao")
    private int id;
    
    /**
     * Armazena a {@link CategoriaSolicitante} do interessado.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_categoria_solicitante")
    private CategoriaSolicitante categoriaSolicitante;
    
    /**
     * Armazena os dados informados pelo {@link InteressadoManifestacao}.
     * Os dados serão armazenados em um {@link InteressadoNaoAutenticado} 
     * ou usarão os dados de {@link Pessoa} do usuário logado.
     */
    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="id_dados_interessado_manifestacao")
    private DadosInteressadoManifestacao dadosInteressadoManifestacao;
    
    /**
     * Registro de entrada do {@link InteressadoManifestacao}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_cadastro")
    @CriadoPor
    private RegistroEntrada registroCadastro;

    /**
     * Data de criação do {@link InteressadoManifestacao}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    @CriadoEm
    private Date dataCadastro;

    /**
     * Construtor padrão.
     */
    public InteressadoManifestacao() {
    }
    
    public InteressadoManifestacao(int categoriaSolicitante, Pessoa pessoa) {
		this.categoriaSolicitante = CategoriaSolicitante.getCategoriaSolicitante(categoriaSolicitante);
		if(pessoa == null)
		    this.dadosInteressadoManifestacao = new DadosInteressadoManifestacao(new InteressadoNaoAutenticado());
		else
		    this.dadosInteressadoManifestacao = new DadosInteressadoManifestacao(pessoa);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CategoriaSolicitante getCategoriaSolicitante() {
        return categoriaSolicitante;
    }

    public void setCategoriaSolicitante(CategoriaSolicitante categoriaSolicitante) {
        this.categoriaSolicitante = categoriaSolicitante;
    }

    public DadosInteressadoManifestacao getDadosInteressadoManifestacao() {
        return dadosInteressadoManifestacao;
    }

    public void setDadosInteressadoManifestacao(DadosInteressadoManifestacao dadosInteressadoManifestacao) {
        this.dadosInteressadoManifestacao = dadosInteressadoManifestacao;
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
		InteressadoManifestacao other = (InteressadoManifestacao) obj;
		if (id != other.id)
		    return false;
		return true;
    }

}
