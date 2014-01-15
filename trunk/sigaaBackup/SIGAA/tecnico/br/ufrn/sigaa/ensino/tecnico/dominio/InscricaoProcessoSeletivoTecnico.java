/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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

/** Classe que modela uma inscrição
 * 
 * @author Édipo Elder F. de Melo
 * @author Fred_Castro
 * 
 */
@Entity
@Table(name = "inscricao_processo_seletivo_tecnico", schema = "tecnico")
public class InscricaoProcessoSeletivoTecnico implements PersistDB, Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_inscricao_processo_seletivo_tecnico", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Número de inscrição. */
	@Column(name="numero_inscricao", nullable = false)
	private int numeroInscricao;
	
	/** Dados pessoais do candidato. */
	@ManyToOne
	@JoinColumn(name = "id_pessoa_tecnico")
	private PessoaTecnico pessoa;

	/** Processo Seletivo para o qual o candidato está se inscrevendo. */
	@ManyToOne
	@JoinColumn(name = "id_processo_seletivo_tecnico")
	private ProcessoSeletivoTecnico processoSeletivo;

	/** Indica se o discente participa de reserva de vagas. */
	@Column(name="reserva_vagas")
	private boolean reservaVagas;
	
	/** Especifica o Pólo e o grupo que o candidato escolheu para se inscrever. */
	@ManyToOne
	@JoinColumn(name = "id_opcao", nullable = true)
	private OpcaoPoloGrupo opcao;
	
	/** Construtor mínimo. */
	public InscricaoProcessoSeletivoTecnico(int id) {
		this();
		this.id = id;
	}
	
	/** Construtor padrão. */
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

	/** Retorna o número de inscrição. 
	 * @return Número de inscrição. 
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

	/** Retorna o processo Seletivo para o qual o candidato está se inscrevendo. 
	 * @return Processo Seletivo para o qual o candidato está se inscrevendo. 
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

	/** Seta o número de inscrição.  
	 * @param inscricao Número de inscrição. 
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

	/** Seta o processo Seletivo para o qual o candidato está se inscrevendo. 
	 * @param processoSeletivo Processo Seletivo para o qual o candidato está se inscrevendo. 
	 */
	public void setProcessoSeletivo(ProcessoSeletivoTecnico processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/** Retorna uma representação textual de uma inscrição no formato:
	 * número de inscrição, seguido de vírgula, seguido do nome do candidato,
	 * seguido de vírgula, seguido da matriz curricular da primeira opção. 
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