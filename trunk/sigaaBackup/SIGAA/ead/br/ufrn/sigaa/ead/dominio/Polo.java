package br.ufrn.sigaa.ead.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Cidade Polo de ensino a dist�ncia. Um polo possui um coordenador.
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(name = "polo", schema = "ead")
public class Polo implements Validatable {

	/** id do p�lo */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_polo")
	private int id;
	
	/** Cidade do p�lo */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_cidade")
	private Municipio cidade;
	
	/** Endere�o do polo */
	private String endereco;

	/** Telefone do polo */
	private String telefone;

	/** CEP do polo */
	private String cep;

	/** Hor�rio de funcionamento do p�lo */
	private String horarioFuncionamento;
	
	/** Cursos que esse polo possui */
	@OneToMany(mappedBy = "polo", fetch=FetchType.LAZY)
	private Collection<PoloCurso> polosCursos;
	
	/** Coordena��es do polo */
	@OneToMany(mappedBy = "polo", fetch=FetchType.LAZY)
	private Collection<CoordenacaoPolo> coordenacoes;

	/** C�digo das turmas que ser�o criadas para este p�lo */
	private String codigo;
	
	public Polo(int id) {
		this.id = id;
	}
	
	public String observacao;
	
	public Polo() {
		
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(endereco, "Endere�o do polo", lista);
		ValidatorUtil.validateRequired(telefone, "Telefone do polo", lista);
		if (ValidatorUtil.validateCEP(cep, "CEP do polo", lista) == 0)
			ValidatorUtil.validateRequired(cep, "CEP do polo", lista);
		ValidatorUtil.validateRequired(horarioFuncionamento, "Hor�rio de funcionamento do p�lo", lista);
		ValidatorUtil.validateRequiredId(cidade.getId(), "Cidade do p�lo", lista);		
		ValidatorUtil.validateMaxLength(telefone, 20, "Telefone do polo", lista);
		ValidatorUtil.validateMaxLength(cep, 10, "CEP do polo", lista);
		ValidatorUtil.validateMaxLength(horarioFuncionamento, 160, "Hor�rio de funcionamento do p�lo", lista);
		ValidatorUtil.validateRequired(codigo, "C�digo das Turmas", lista);
		return lista;
	}

	@Transient
	public String getDescricao() {
		
		StringBuilder sb = new StringBuilder();
		
		if (cidade != null)
			sb.append(cidade.getNomeUF());
		
		if (isNotEmpty(observacao))
			sb.append(" (" + observacao + ") ");
		
		return sb.toString();
	}

	
	public int getId() {
		return id;
	}

	
	public void setId(int id) {
		this.id = id;
	}

	
	public Municipio getCidade() {
		return cidade;
	}

	
	public void setCidade(Municipio cidade) {
		this.cidade = cidade;
	}

	
	public String getEndereco() {
		return endereco;
	}

	
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	
	public String getTelefone() {
		return telefone;
	}

	
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	
	public String getCep() {
		return cep;
	}

	
	public void setCep(String cep) {
		this.cep = cep;
	}

	
	public String getHorarioFuncionamento() {
		return horarioFuncionamento;
	}

	
	public void setHorarioFuncionamento(String horarioFuncionamento) {
		this.horarioFuncionamento = horarioFuncionamento;
	}

	
	public Collection<CoordenacaoPolo> getCoordenacoes() {
		return coordenacoes;
	}

	
	public void setCoordenacoes(Collection<CoordenacaoPolo> coordenacoes) {
		this.coordenacoes = coordenacoes;
	}

	
	public Collection<PoloCurso> getPolosCursos() {
		return polosCursos;
	}

	
	public void setPolosCursos(Collection<PoloCurso> polosCursos) {
		this.polosCursos = polosCursos;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
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
		final Polo other = (Polo) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		if (cidade == null)
			return null;
		return cidade.toString();
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
