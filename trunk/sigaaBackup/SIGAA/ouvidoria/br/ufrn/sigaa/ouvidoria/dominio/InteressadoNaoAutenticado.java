/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 18/05/2011
 *
 */
package br.ufrn.sigaa.ouvidoria.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import javax.persistence.CascadeType;
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
import br.ufrn.sigaa.pessoa.dominio.Endereco;

/**
 * Classe que representa um {@link InteressadoManifestacao} n�o autenticado no sistema.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(schema="ouvidoria", name="interessado_nao_autenticado")
public class InteressadoNaoAutenticado implements PersistDB {
    
    /**
     * Chave prim�ria do {@link InteressadoNaoAutenticado}.
     */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
    @Column(name = "id_interessado_nao_autenticado")
    private int id;
    
    /**
     * Nome informado pelo {@link InteressadoNaoAutenticado}.
     */
    private String nome;
    
    /**
     * Nome do {@link InteressadoNaoAutenticado} armazenado em ASCII.
     */
    @Column(name="nome_ascii")
    private String nomeAscii;
    
    /**
     * Endere�o informado pelo {@link InteressadoNaoAutenticado}.
     */
    @ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
    @JoinColumn(name="id_endereco")
    private Endereco endereco;
    
    /**
     * C�digo de �rea do telefone informado pelo {@link InteressadoNaoAutenticado}.
     */
    @Column(name="codigo_area_telefone")
    private short codigoAreaTelefone;
    
    /**
     * Telefone informado pelo {@link InteressadoNaoAutenticado}.
     */
    private String telefone;
    
    /**
     * E-mail informado pelo {@link InteressadoNaoAutenticado}.
     */
    private String email;
    
    /**
     * C�digo de acesso que permite o interessado ter acesso a visualiza��o das manifesta��es atrav�s do Portal P�blico
     */
    @Column(name="codigo_acesso")
    private String codigoAcesso;
    
    /**
     * C�digo de acesso criptografado com MD5
     */
    private String senha;
    
    
    /**
     * Construtor padr�o.
     */
    public InteressadoNaoAutenticado() {
    }
    
    public InteressadoNaoAutenticado(int id) {
    	this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    } 

    public String getNomeAscii() {
		return nomeAscii;
	}

	public void setNomeAscii(String nomeAscii) {
		this.nomeAscii = nomeAscii;
	}

	public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public short getCodigoAreaTelefone() {
        return codigoAreaTelefone;
    }

    public void setCodigoAreaTelefone(short codigoAreaTelefone) {
        this.codigoAreaTelefone = codigoAreaTelefone;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Utilizado na visualiza��o de uma {@link Manifestacao} para verificar se o endere�o foi cadastrado e, portanto, deve ser mostrado.
     * 
     * @return
     */
    public boolean isEnderecoCadastrado() {
    	validarEndereco();
    	
    	return endereco != null;
    }
    
    /**
     * Valida os dados de endere�o para garantir que o ele s� ser� cadastrado
     * se o usu�rio informar o logradouro, o CEP e o n�mero
     * 
     */
    public void validarEndereco() {
		//Somente cadastrar o endere�o se o usu�rio informar o logradouro, o CEP e o n�mero
		if(endereco != null && (isEmpty(endereco.getLogradouro()) || isEmpty(endereco.getCep()) || isEmpty(endereco.getNumero())))
			setEndereco(null);
		//Evitar erro caso n�o seja selecionado nenhum munic�pio
		if(endereco != null && isEmpty(endereco.getMunicipio()))
			endereco.setMunicipio(null);
		//Evitar erro caso n�o seja selecionado nenhuma uf
		if(endereco != null && isEmpty(endereco.getUnidadeFederativa()))
			endereco.setUnidadeFederativa(null);
	}
    
    /**
     * Retorna a representa��o textual do telefone armazenado.
     * 
     * @return
     */
    public String getTelefoneString() {
		if(isNotEmpty(telefone)) {
		    String tel = "(";
		    tel += isEmpty(codigoAreaTelefone) ? "--" : codigoAreaTelefone;
		    tel += ") ";
		    tel += telefone;
		    
		    return tel;
		}
		
		return "-";
    }
    
    /**
     * Retorna o logradouro do endere�o associado ao interessado.
     * 
     * @return
     */
    public String getLogradouroString() {
    	if(endereco != null) {
    		String log = "";
		    log += isEmpty(endereco.getTipoLogradouro()) ? "" : endereco.getTipoLogradouro().getDescricao();
		    log += " ";
		    log += endereco.getLogradouro();
		    
		    return log.toUpperCase();
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
		InteressadoNaoAutenticado other = (InteressadoNaoAutenticado) obj;
		if (id != other.id)
		    return false;
		return true;
    }

	public String getCodigoAcesso() {
		return codigoAcesso;
	}

	public void setCodigoAcesso(String codigoAcesso) {
		this.codigoAcesso = codigoAcesso;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}


}
