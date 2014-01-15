/**
 *
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Escolaridade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * Representa um reponsável de um discente do ensino infantil.
 * 
 * @author Andre M Dantas
 * @author Leonardo Campos
 */
@Entity
@Table(name = "responsavel_discente", schema = "infantil", uniqueConstraints = {})
public class ResponsavelDiscenteInfantil implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "id_responsavel_discente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

    @Column(name = "grau_parentesco", unique = false, nullable = true, insertable = true, updatable = true)
	private String grauParentesco;
    
    private String profissao;
    
    @Column(name = "codigo_area_nacional_telefone_trabalho", unique = false, nullable = true, insertable = true, updatable = true)
    private Short codigoAreaNacionalTelefoneTrabalho;
    
    @Column(name = "telefone_trabalho", unique = false, nullable = true, insertable = true, updatable = true)
    private String telefoneTrabalho;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pessoa", unique = false, nullable = true)
	private Pessoa pessoa = new Pessoa();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_escolaridade", unique = false, nullable = true)
	private Escolaridade escolaridade = new Escolaridade();


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Escolaridade getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(Escolaridade escolaridade) {
		this.escolaridade = escolaridade;
	}

	public String getGrauParentesco() {
		return grauParentesco;
	}

	public void setGrauParentesco(String grauParentesco) {
		this.grauParentesco = grauParentesco;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Transient
	public String getNome() {
		if (pessoa != null)
			return pessoa.getNome();
		return "";
	}

	@Transient
	public Pessoa getDadosPessoais() {
		return pessoa;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getTelefoneTrabalho() {
        return telefoneTrabalho;
    }

    public void setTelefoneTrabalho(String telefoneTrabalho) {
        this.telefoneTrabalho = telefoneTrabalho;
    }

    public ListaMensagens validate() {
        ListaMensagens lista = new ListaMensagens();
        
        ValidatorUtil.validateCPF_CNPJ(getPessoa().getCpf_cnpj(), "CPF", lista);
        ValidatorUtil.validateRequired(getPessoa().getNome(), "Nome", lista);
        ValidatorUtil.validateRequired(grauParentesco, "Grau de Parentesco", lista);
        ValidatorUtil.validateRequired(profissao, "Profissão", lista);
        ValidatorUtil.validateRequired(getPessoa().getCelular(), "Celular", lista);
        ValidatorUtil.validateRequired(getPessoa().getEmail(), "Email", lista);
        ValidatorUtil.validateRequiredId(getEscolaridade().getId(), "Escolaridade", lista);
        
        return lista;
    }

    public Short getCodigoAreaNacionalTelefoneTrabalho() {
        return codigoAreaNacionalTelefoneTrabalho;
    }

    public void setCodigoAreaNacionalTelefoneTrabalho(
            Short codigoAreaNacionalTelefoneTrabalho) {
        this.codigoAreaNacionalTelefoneTrabalho = codigoAreaNacionalTelefoneTrabalho;
    }

}
