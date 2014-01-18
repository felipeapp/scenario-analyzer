package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe que modela o conceito de justificativa de ausência do docente ou discente
 * no congresso de iniciação científica
 * 
 * @author Andreza Pollyana
 * 
 */

@Entity
@Table(schema="pesquisa", name="justificativa_ausencia_cic")
public class JustificativaAusenciaCIC implements Validatable {
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_justificativa_ausencia_cic", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@OneToOne
	@JoinColumn(name="id_pessoa")
	private Pessoa pessoa;
	
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	private String justificativa;
	
	private int status;
	
	private Date data_cadastro;
	
	private int tipo;
	
	private boolean visto;
	
	
	
	public boolean isVisto() {
		return visto;
	}



	public void setVisto(boolean visto) {
		this.visto = visto;
	}

	@OneToOne
	@JoinColumn(name="id_usuario")
	private Usuario cadastrado_por;
	
	@OneToOne
	@JoinColumn(name="id_cic")
	private CongressoIniciacaoCientifica CIC;
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

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public Usuario getUsuario() {
		return cadastrado_por;
	}

	public void setUsuario(Usuario cadastrado_por) {
		this.cadastrado_por = cadastrado_por;
	}

	public Usuario getCadastrado_por() {
		return cadastrado_por;
	}

	public void setCadastrado_por(Usuario cadastrado_por) {
		this.cadastrado_por = cadastrado_por;
	}

	public CongressoIniciacaoCientifica getCIC() {
		return CIC;
	}

	public void setCIC(CongressoIniciacaoCientifica cic) {
		CIC = cic;
	}


	public JustificativaAusenciaCIC() {
		setStatus(1);
		setVisto(false);
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(justificativa, "Justificativa", lista);
		return lista;
	}

}
