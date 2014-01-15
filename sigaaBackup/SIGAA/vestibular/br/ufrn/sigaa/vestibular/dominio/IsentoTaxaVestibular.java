/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em 18/01/2010
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import java.util.Date;

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
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Durante a inscri��o para o Vestibular, verifica-se se o CPF do candidato
 * consta da lista de isentos. Esta classe que modela um candidato que � isento
 * da taxa de inscri��o do Vestibular. A isen��o � determinada em Edital
 * espec�fico e pode variar conforme o Vestibular.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "isento_taxa_vestibular", schema = "vestibular", uniqueConstraints = {})
public class IsentoTaxaVestibular implements PersistDB, Validatable {
	// constantes
	/** Constante que define o tipo de isento ESTUDANTE. */
	public static final int ESTUDANTE = 1;
	/** Constante que define o tipo de isento FUNCION�RIO. */
	public static final int FUNCIONARIO = 2;

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_isento_taxa_vestibular", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Processo seletivo em que o candidato foi isento. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoVestibular processoSeletivoVestibular;
	
	/** Indica se o candidato � isento total da taxa de inscri��o. */
	@Column(name = "isento_total")
	private boolean isentoTotal;
	
	/** Valor a pagar, caso seja isento parcial da taxa de inscri��o. */
	private Double valor;
	
	/** CPF do candidato que ser� isento. */
	private Long cpf;

	/** Dados pessoais, caso cadastrado, do candidato que ter� isen��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private PessoaVestibular pessoa;
	
	/** Observa��es acerca da isen��o. */
	private String observacao;
	
	/** Data de cria��o do registro, para fins de log. */
	@CriadoEm
	@Column(name = "criado_em")
	private Date criadoEm;
	
	/** Data de modifica��o do registro, para fins de log. */
	@AtualizadoEm
	@Column(name = "atualizado_em")
	private Date atualizadoEm;
	
	/** Registro de entrada, para fins de log. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;
	
	/** Indica o tipo de isento: se � estudante ou funcion�rio.  */
	private int tipo = 0;
	
	/** Construtor padr�o. */
	public IsentoTaxaVestibular() {
		tipo = ESTUDANTE;
	}

	/** Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o Processo seletivo em que o candidato foi isento. 
	 * @return
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivoVestibular() {
		return processoSeletivoVestibular;
	}

	/** Seta o Processo seletivo em que o candidato foi isento.
	 * @param processoSeletivoVestibular
	 */
	public void setProcessoSeletivoVestibular(
			ProcessoSeletivoVestibular processoSeletivoVestibular) {
		this.processoSeletivoVestibular = processoSeletivoVestibular;
	}

	/** Indica se o candidato � isento total da taxa de inscri��o. 
	 * @return
	 */
	public boolean isIsentoTotal() {
		return isentoTotal;
	}

	/** Seta se o candidato � isento total da taxa de inscri��o. 
	 * @param isentoTotal
	 */
	public void setIsentoTotal(boolean isentoTotal) {
		this.isentoTotal = isentoTotal;
	}

	/** Retorna o valor a pagar, caso seja isento parcial da taxa de inscri��o. 
	 * @return
	 */
	public Double getValor() {
		return valor;
	}

	/** Seta o valor a pagar, caso seja isento parcial da taxa de inscri��o.
	 * @param valor
	 */
	public void setValor(Double valor) {
		this.valor = valor;
	}

	/** Retorna o CPF do candidato que ser� isento. 
	 * @return
	 */
	public Long getCpf() {
		return cpf;
	}

	/** Seta o CPF do candidato que ser� isento.
	 * @param cpf
	 */
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	/** Retorna os dados pessoais, caso cadastrada, do candidato que ter� isen��o. 
	 * @return
	 */
	public PessoaVestibular getPessoa() {
		return pessoa;
	}

	/** Seta os dados pessoais, caso cadastrada, do candidato que ter� isen��o.
	 * @param pessoa
	 */
	public void setPessoa(PessoaVestibular pessoa) {
		this.pessoa = pessoa;
	}

	/** Retorna as observa��es acerca da isen��o. 
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	/** Seta as observa��es acerca da isen��o.
	 * @param observacao
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/** Retorna a data de cria��o do registro, para fins de log. 
	 * @return
	 */
	public Date getCriadoEm() {
		return criadoEm;
	}

	/** Seta a data de cria��o do registro, para fins de log.
	 * @param criadoEm
	 */
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	/** Retorna a data de modifica��o do registro, para fins de log. 
	 * @return
	 */
	public Date getAtualizadoEm() {
		return atualizadoEm;
	}

	/** Seta a data de modifica��o do registro, para fins de log.
	 * @param modificadoEm
	 */
	public void setAtualizadoEm(Date atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

	/** Retorna o registro de entrada, para fins de log. 
	 * @return
	 */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o registro de entrada, para fins de log. 
	 * @param registroEntrada
	 */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	/** Compara este objeto com o passado por par�metro, comparando se os CPFs s�o iguais.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		IsentoTaxaVestibular outro;
		if (this.cpf == null) {
			return false;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof IsentoTaxaVestibular) {
			outro = (IsentoTaxaVestibular) obj;
			return this.cpf.equals(outro.getCpf());
		} if (obj instanceof Long){
			Long outroCPF = (Long) obj;
			return this.cpf.equals(outroCPF);
		} else if (obj instanceof String) {
			Long outroCPF = UFRNUtils.parseCpfCnpj((String) obj);
			return this.cpf.equals(outroCPF);
		} else {
			return false;
		}
	}
	
	/** Retorna uma string com o n�mero do CPF separado por pontos.
	 * @return
	 */
	public String getCpfFormatado() {
		if (cpf == null){
			cpf = new Long(0);
		}
		return Formatador.getInstance().formatarCPF_CNPJ(cpf);
	}
	
	/** Retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(cpf);
	}

	/**
	 * Retorna uma representa��o textual do registro de isen��o no formato: CPF,
	 * seguido de '-', seguido do nome da pessoa (caso cadastrado), seguido de
	 * ':', seguido do tipo de isen��o (total/parcial), seguido do valor, entre
	 * par�nteses (caso isento parcial), seguido do nome do processo seletivo.
	 */
	@Override
	public String toString() {
		return getCpfFormatado() + " - " +
		(pessoa != null && pessoa.getNome() != null ? pessoa.getNome():"") + ": " +
		(isentoTotal ? "isento total" : "isento parcial ("+Formatador.getInstance().formatarMoeda(valor)+")")+
		" no " + processoSeletivoVestibular.getNome();
	}

	/** Valida os dados obrigat�rios do isento: cpf, processo seletivo, tipo de isen��o, taxa de inscri��o.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();
		ValidatorUtil.validateRequired(getProcessoSeletivoVestibular(), "Processo Seletivo", mensagens);
		ValidatorUtil.validateRequired(cpf, "CPF", mensagens);
		if (isIsentoTotal()) {
			setValor(null);
		} else {
			ValidatorUtil.validateRequired(getValor(), "Valor da Inscri��o", mensagens);
		}
		return mensagens;
	}

	/** Indica o tipo de isento: se � estudante ou funcion�rio.  
	 * @return
	 */
	public int getTipo() {
		return tipo;
	}

	/** Seta o tipo de isento: se � estudante ou funcion�rio. 
	 * @param tipo
	 */
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	/** Indica que o tipo de isento � estudante. 
	 * @return
	 */
	public boolean isEstudante(){
		return tipo == ESTUDANTE;
	}
	
	/** Indica que o tipo de isento � funcion�rio.
	 * @return
	 */
	public boolean isFuncionario(){
		return tipo == FUNCIONARIO;
	}
}
