/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/** Classe que modela uma inscri��o
 * 
 * @author �dipo Elder F. de Melo
 * @author Fred_Castro
 * 
 */
@Entity
@Table(name = "inscricao_processo_seletivo_tecnico", schema = "tecnico")
public class InscricaoProcessoSeletivoTecnico implements PersistDB, Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_inscricao_processo_seletivo_tecnico", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** N�mero de inscri��o. */
	@Column(name="numero_inscricao", nullable = false)
	private int numeroInscricao;
	
	/** Dados pessoais do candidato. */
	@ManyToOne
	@JoinColumn(name = "id_pessoa_tecnico")
	private PessoaTecnico pessoa;

	/** Processo Seletivo para o qual o candidato est� se inscrevendo. */
	@ManyToOne
	@JoinColumn(name = "id_processo_seletivo_tecnico")
	private ProcessoSeletivoTecnico processoSeletivo;

	/** Indica se o discente participa de reserva de vagas. */
	@Column(name="reserva_vagas")
	private boolean reservaVagas;
	
	/** Especifica o P�lo e o grupo que o candidato escolheu para se inscrever. */
	@ManyToOne
	@JoinColumn(name = "id_opcao", nullable = true)
	private OpcaoPoloGrupo opcao;
	
	/** Construtor m�nimo. */
	public InscricaoProcessoSeletivoTecnico(int id) {
		this();
		this.id = id;
	}
	
	/** Construtor padr�o. */
	public InscricaoProcessoSeletivoTecnico() {
		this.processoSeletivo = new ProcessoSeletivoTecnico();
		this.pessoa = new PessoaTecnico();
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Retorna o n�mero de inscri��o. 
	 * @return N�mero de inscri��o. 
	 */
	public int getNumeroInscricao() {
		return numeroInscricao;
	}

	/** Retorna os dados pessoais do candidato. 
	 * @return Dados pessoais do candidato. 
	 */
	public PessoaTecnico getPessoa() {
		return pessoa;
	}

	/** Retorna o processo Seletivo para o qual o candidato est� se inscrevendo. 
	 * @return Processo Seletivo para o qual o candidato est� se inscrevendo. 
	 */
	public ProcessoSeletivoTecnico getProcessoSeletivo() {
		return processoSeletivo;
	}

	/** Inicializa os atributos nulos. */
	public void prepararDados() {
		pessoa = new PessoaTecnico();
		pessoa.prepararDados();
		pessoa.getIdentidade().setDataExpedicao(null);
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Seta o n�mero de inscri��o.  
	 * @param inscricao N�mero de inscri��o. 
	 */
	public void setNumeroInscricao(int numeroInscricao) {
		this.numeroInscricao = numeroInscricao;
	}

	/** Seta os dados pessoais do candidato. 
	 * @param pessoa Dados pessoais do candidato. 
	 */
	public void setPessoa(PessoaTecnico pessoa) {
		this.pessoa = pessoa;
	}

	/** Seta o processo Seletivo para o qual o candidato est� se inscrevendo. 
	 * @param processoSeletivo Processo Seletivo para o qual o candidato est� se inscrevendo. 
	 */
	public void setProcessoSeletivo(ProcessoSeletivoTecnico processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/** Retorna uma representa��o textual de uma inscri��o no formato:
	 * n�mero de inscri��o, seguido de v�rgula, seguido do nome do candidato,
	 * seguido de v�rgula, seguido da matriz curricular da primeira op��o. 
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(getNumeroInscricao());
		if (pessoa.getNome() != null)
			str.append(", ").append(getPessoa().getNome());
		return str.toString();
	}

	public OpcaoPoloGrupo getOpcao() {
		return opcao;
	}

	public void setOpcao(OpcaoPoloGrupo opcao) {
		this.opcao = opcao;
	}

	public boolean isReservaVagas() {
		return reservaVagas;
	}

	public void setReservaVagas(boolean reservaVagas) {
		this.reservaVagas = reservaVagas;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
}