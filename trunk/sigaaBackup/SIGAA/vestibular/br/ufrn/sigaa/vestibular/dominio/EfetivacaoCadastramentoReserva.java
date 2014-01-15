/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/07/2013
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Agrupa as convocações de discentes PRÉ-CADASTRO (cadastro reserva),
 * permitindo consultas posteriores e relatórios.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "efetivacao_cadastramento_reserva", schema = "vestibular")
public class EfetivacaoCadastramentoReserva implements PersistDB, Validatable{

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_efetivacao_cadastramento_reserva", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Registro de Entrada do usuário que cadastrou a convocação. */
	@CriadoPor
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada efetuadoPor;
	
	/** Data de cadastro da convocação. */
	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Convocações que foram efetivadas neste processamento. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="convocacao_efetivada_cadastramento_reserva", schema="vestibular",
			joinColumns=@JoinColumn(name="id_efetivacao_cadastramento_reserva"),  
			inverseJoinColumns=@JoinColumn(name="id_convocacao_processo_seletivo_discente"))
	private Collection<ConvocacaoProcessoSeletivoDiscente> convocacoes;

	/**
	 * Construtor padrão.
	 */
	public EfetivacaoCadastramentoReserva() {
		convocacoes = new LinkedList<ConvocacaoProcessoSeletivoDiscente>();
	}
	
	/** Construtor parametrizado.
	 * @param id
	 */
	public EfetivacaoCadastramentoReserva(int id) {
		this();
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getEfetuadoPor() {
		return efetuadoPor;
	}

	public void setEfetuadoPor(RegistroEntrada convocadoPor) {
		this.efetuadoPor = convocadoPor;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Collection<ConvocacaoProcessoSeletivoDiscente> getConvocacoes() {
		return convocacoes;
	}

	public void setConvocacoes(
			Collection<ConvocacaoProcessoSeletivoDiscente> convocacoes) {
		this.convocacoes = convocacoes;
	}
	
	/** Valida os dados desta convocação
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(convocacoes, "Convocações", lista);
		return lista;
	}
	
	/** Retorna uma representação textual desta convocação
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Processamento realizado em " + Formatador.getInstance().formatarData(dataCadastro);
	}
	
	/** Indica se este objeto é igual a outro passado por parâmetro
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EfetivacaoCadastramentoReserva) {
			EfetivacaoCadastramentoReserva outro = (EfetivacaoCadastramentoReserva) obj;
			return this.id != 0 && outro.id == this.id;
		}
		return false;
	}
	
	/** Retorna um código hash para este objeto
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, dataCadastro);
	}

	/** Adiciona uma convocação à lista de convocações efetivadas
	 * @param convocacao
	 */
	public void addConvocacao(ConvocacaoProcessoSeletivoDiscente convocacao) {
		if (convocacoes == null) convocacoes = new LinkedList<ConvocacaoProcessoSeletivoDiscente>();
		convocacoes.add(convocacao);
		
	}

}
