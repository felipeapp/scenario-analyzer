package br.ufrn.sigaa.vestibular.dominio;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 20/01/2010
 *
 */

/**
 * Classe que modela a restrição de inscrição para um determinado curso no
 * Vestibular. É usada basicamente para o curso de música/bacharelado, o qual é
 * restrito aos candidatos aprovados, em fase anterior, em um teste de aptidão.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "restricao_inscricao", schema = "vestibular", uniqueConstraints = {})
public class RestricaoInscricaoVestibular implements Validatable, TipoRestricaoInscricaoVestibular {
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_restricao_inscricao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Processo seletivo em que o candidato foi isento. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoVestibular processoSeletivoVestibular;
	
	/** Coleção de CPFs que tem esta restrição. */
	@CollectionOfElements
	@JoinTable(name="restricao_inscricao_cpf", schema="vestibular",
			joinColumns=@JoinColumn(name="id_restricao_inscricao"))
	@Column(name="cpf", nullable=false)
	private Set<Long> cpfs;
	
	/** Matriz curricular para o qual a restrição é definida. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matriz_curricular", nullable = false)
	private MatrizCurricular matrizCurricular;
	
	/** Tipo de restrição: exclusivo à, exceto à. */
	@Column(name = "tipo_restricao")
	private int tipoRestricao;
	
	/** Descricao da restrição. */
	private String descricao;

	/** Observações acerca da isenção. */
	private String observacao;
	
	/** Data de criação do registro, para fins de log. */
	@CriadoEm
	@Column(name = "criado_em")
	private Date criadoEm;
	
	/** Data de modificação do registro, para fins de log. */
	@AtualizadoEm
	@Column(name = "atualizado_em")
	private Date atualizadoEm;
	
	/** Registro de entrada, para fins de log. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Retorna a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária
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

	/** Retorna as observações acerca da isenção. 
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	/** Seta as observações acerca da isenção.
	 * @param observacao
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/** Retorna a data de criação do registro, para fins de log. 
	 * @return
	 */
	public Date getCriadoEm() {
		return criadoEm;
	}

	/** Seta a data de criação do registro, para fins de log.
	 * @param criadoEm
	 */
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	/** Retorna a data de modificação do registro, para fins de log. 
	 * @return
	 */
	public Date getAtualizadoEm() {
		return atualizadoEm;
	}

	/** Seta a data de modificação do registro, para fins de log.
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
	
	/**
	 * Compara este objeto com o passado por parâmetro, comparando se ambos
	 * tem o mesmo valor para a matriz curricular, o processo seletivo e tipo de
	 * restrição.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj);
	}
	
	
	/** Retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(matrizCurricular, processoSeletivoVestibular, tipoRestricao);
	}

	/**
	 * Retorna uma representação textual da restrição informando o processo
	 * seletivo, a matriz curricular, o tipo de restrição, e a lista de CPFs.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s - %s, %s: %s", processoSeletivoVestibular.getNome(),
				matrizCurricular.getDescricao(),
				getDescricaoTipoRestricao(),
				StringUtils.transformaEmLista(UFRNUtils.toList(cpfs)));
	}

	/** Retorna uma descrição textual do tipo de restrição.
	 * @see TipoRestricaoInscricaoVestibular
	 * @return
	 */
	public String getDescricaoTipoRestricao(){
		switch (tipoRestricao) {
			case TipoRestricaoInscricaoVestibular.EXCETO_A: return "sem permissão para os candidatos";
			case TipoRestricaoInscricaoVestibular.EXCLUSIVO_A: return "exclusivo aos candidatos";
			default: return "não definido";
		}
	}
	/** Valida os dados obrigatórios do isento: cpf, processo seletivo, tipo de isenção, taxa de inscrição.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();
		ValidatorUtil.validateRequired(getProcessoSeletivoVestibular(), "Processo Seletivo", mensagens);
		ValidatorUtil.validateRequired(cpfs, "Lista de CPFs", mensagens);
		ValidatorUtil.validateRequired(getTipoRestricao(), "Tipo de Restrição", mensagens);
		ValidatorUtil.validateRequired(getDescricao(), "Descrição", mensagens);
		return mensagens;
	}

	/** Retorna a coleção de CPFs que tem esta restrição.
	 * @return
	 */
	public Set<Long> getCpfs() {
		return cpfs;
	}

	/** Seta a coleção de CPFs que tem esta restrição. 
	 * @param cpfs
	 */
	public void setCpfs(Set<Long> cpfs) {
		this.cpfs = cpfs;
	}

	/** Retorna a matriz curricular para o qual a restrição é definida. 
	 * @return
	 */
	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	/** Seta a matriz curricular para o qual a restrição é definida.
	 * @param matrizCurricular
	 */
	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}

	/** Retorna o tipo de restrição: exclusivo à, exceto à. 
	 * @return
	 */
	public int getTipoRestricao() {
		return tipoRestricao;
	}

	/** Seta o tipo de restrição: exclusivo à, exceto à.
	 * @param tipoRestricao
	 */
	public void setTipoRestricao(int tipoRestricao) {
		this.tipoRestricao = tipoRestricao;
	}

	/** Retorna a descricao da restrição.
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descricao da restrição.
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	/** Indica se a restrição é to tipo EXCLUSIVA À 
	 * @return
	 */
	public boolean isExclusivoA() {
		return tipoRestricao == EXCLUSIVO_A;
	}
	
	/** Indica se a restrição é to tipo EXCETO A 
	 * @return
	 */
	public boolean isExcetoA() {
		return tipoRestricao == EXCETO_A;
	}
	
}
