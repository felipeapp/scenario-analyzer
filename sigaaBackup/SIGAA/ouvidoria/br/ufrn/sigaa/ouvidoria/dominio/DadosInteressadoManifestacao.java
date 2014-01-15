/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 18/05/2011
 *
 */
package br.ufrn.sigaa.ouvidoria.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

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
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Armazena os dados de um {@link InteressadoManifestacao}.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="dados_interessado_manifestacao")
public class DadosInteressadoManifestacao implements PersistDB {
    
    /**
     * Chave primária dos {@link DadosInteressadoManifestacao}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_dados_interessado_manifestacao")
    private int id;
    
    /**
     * {@link Pessoa} associada aos {@link DadosInteressadoManifestacao}.
     */
    @ManyToOne(fetch=FetchType.EAGER, optional=true)
    @JoinColumn(name="id_pessoa")
    private Pessoa pessoa;
    
    /**
     * {@link InteressadoNaoAutenticado} associado aos {@link DadosInteressadoManifestacao}.
     */
    @ManyToOne(fetch=FetchType.EAGER, optional=true)
    @JoinColumn(name="id_interessado_nao_autenticado")
    private InteressadoNaoAutenticado interessadoNaoAutenticado;
    
    /**
     * Construtor padrão.
     */
    public DadosInteressadoManifestacao() {
    }

    /**
     * Construtor para os {@link DadosInteressadoManifestacao} de um usuário autenticado no sistema.
     * 
     * @param pessoa
     */
    public DadosInteressadoManifestacao(Pessoa pessoa) {
		super();
		this.pessoa = pessoa;
    }

    /**
     * Construtor para os {@link DadosInteressadoManifestacao} de usuários da área pública do sistema.
     * 
     * @param interessadoNaoAutenticado
     */
    public DadosInteressadoManifestacao(InteressadoNaoAutenticado interessadoNaoAutenticado) {
		super();
		this.interessadoNaoAutenticado = interessadoNaoAutenticado;
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

    public InteressadoNaoAutenticado getInteressadoNaoAutenticado() {
        return interessadoNaoAutenticado;
    }

    public void setInteressadoNaoAutenticado(InteressadoNaoAutenticado interessadoNaoAutenticado) {
        this.interessadoNaoAutenticado = interessadoNaoAutenticado;
    }
    
    /**
     * Retorna uma entidade Pessoa com os dados do interessado, seja ele da comunidadde interna ou externa.
     * Popula os dados do nome e email e é usado no envio de notificações.
     * 
     * @return
     */
    public Pessoa getPessoaEmail() {
    	Pessoa p = new Pessoa();
    	
    	p.setNome(getNome());
    	p.setEmail(getEmail());
    	
    	return p;
    }

    /**
     * Retorna o nome da {@link Pessoa} ou {@link InteressadoNaoAutenticado}.
     * 
     * @return
     */
    public String getNome() {
		if(isNotEmpty(pessoa)) {
		    return pessoa.getNome();
		} else if (isNotEmpty(interessadoNaoAutenticado)) {
		    return interessadoNaoAutenticado.getNome();
		}
		return "-";
    }
    
    /**
     * Retorna o email da {@link Pessoa} ou {@link InteressadoNaoAutenticado}.
     * 
     * @return
     */
    public String getEmail() {
		if(isNotEmpty(pessoa)) {
		    return pessoa.getEmail();
		} else if (isNotEmpty(interessadoNaoAutenticado)) {
		    return interessadoNaoAutenticado.getEmail();
		}
		return "-";
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
		DadosInteressadoManifestacao other = (DadosInteressadoManifestacao) obj;
		if (id != other.id)
		    return false;
		return isNotEmpty(pessoa) ? pessoa.equals(other.getPessoa()) : 
		    interessadoNaoAutenticado.equals(other.getInteressadoNaoAutenticado());
    }

}
