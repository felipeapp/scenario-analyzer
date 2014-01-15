/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 10/01/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Where;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;

/**
 * Reflete as notas que o discente obteve nas unidades de um componente
 * curricular
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "nota_unidade", schema = "ensino")
public class NotaUnidade implements Validatable {

	/** Chave Primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_nota_unidade", nullable = false)
	private int id;

	/** Número de faltas do aluno na unidade. */
	private short faltas;

	/** Nota do Aluno na unidade */
	@Column(name = "nota", unique = false, nullable = true, insertable = true, updatable = true, precision = 4)
	private Double nota;

	/** Unidade que a nota se refere. */
	private byte unidade;

	/** Se a unidade é de recuperação. */
	private boolean recuperacao;
	
	@Column(name = "ativo")
	@Where(clause = "ativo = trueValue()") 
	private boolean ativo = true;
	
	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	@Column(name = "data_inativacao")
	private Date dataInativacao;
	
	/** Peso da nota da unidade.  */
	@Transient
	private String peso;

	/** Avaliações no qual a unidade foi dividida. */
	@OneToMany(mappedBy = "unidade", fetch=FetchType.LAZY) @OrderBy("id")
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();

	/** Matricula em componente que a nota se refere. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matricula_componente")
	private MatriculaComponente matricula;

	/** Número de faltas calculadas na turma virtual. Utilizado no ensino médio. */
	@Transient
	private short faltasCalc;
	
	public NotaUnidade() {

	}

	public NotaUnidade(int id, short faltas, Double nota, byte unidade, int idMatricula ) {
		this.id = id;
		this.faltas = faltas;
		this.nota = nota;
		this.unidade = unidade;
		matricula = new MatriculaComponente(idMatricula);
	}

	public List<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public short getFaltas() {
		return faltas;
	}

	public void setFaltas(short faltas) {
		this.faltas = faltas;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getNota() {
		return nota;
	}

	public void setNota(Double nota) {
		this.nota = nota;
	}

	public byte getUnidade() {
		return unidade;
	}

	public void setUnidade(byte unidade) {
		this.unidade = unidade;
	}

	public ListaMensagens validate() {
		return null;
	}

	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public int getNumeroAvaliacoes() {
		if (avaliacoes != null && !avaliacoes.isEmpty()) {
			return avaliacoes.size() + 1;
		}
		return 1;
	}

	public boolean isRecuperacao() {
		return recuperacao;
	}

	public void setRecuperacao(boolean recuperacao) {
		this.recuperacao = recuperacao;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataInativacao() {
		return dataInativacao;
	}

	public void setDataInativacao(Date dataInativacao) {
		this.dataInativacao = dataInativacao;
	}

	/** 
	 * Retorna a nota da unidade apenas quando todas suas avaliações estiverem com as notas preenchidas.
	 */
	public Double getNotaPreenchida ()
	{
		if(matricula !=  null && matricula.getSituacaoMatricula() != null && matricula.isConsolidada()) {
			return getNota();
		}
		
		if ( avaliacoes != null ) {
			for ( Avaliacao avaliacao : avaliacoes ) {
				if ( avaliacao.getNota() == null )
					return null;
			}
		}	
		return getNota();
	}

	public void setFaltasCalc(short faltasCalc) {
		this.faltasCalc = faltasCalc;
	}

	public short getFaltasCalc() {
		return faltasCalc;
	}
	
}
