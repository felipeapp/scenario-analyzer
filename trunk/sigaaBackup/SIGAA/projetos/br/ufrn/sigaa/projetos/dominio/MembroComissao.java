/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/*******************************************************************************
 * <p>
 * Representa um membro da comiss�o de avalia��o dos projetos, 
 * seus relat�rios e resumos. O membro pode participar de duas
 * comiss�es, s�o elas: 
 * - A Comiss�o Cient�fica: que analisa, basicamente, os
 * resumos do Semin�rio de Inicia��o a Doc�ncia (SID) e Semin�rio de Inicia��o Cient�fica (SIC). 
 * - A Comiss�o de Monitoria: que analisa os projetos de ensino e resumos do SID.
 * - A Comiss�o de Pesquisa: que analisa os projetos de pesquisa e resumos do SIC.
 * - A Comiss�o de Extens�o: que analisa os a��es e relat�rios de extens�o.
 * </p>
 * <p>
 * O tipo de comiss�o da qual o membro faz parte � determinada pelo atributo
 * papel nesta classe.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "monitoria", name = "membro_comissao")
public class MembroComissao implements PersistDB, Validatable,
		Comparable<MembroComissao> {

	/**
	 * Membro de comiss�o de monitoria.  
	 */
	public static final int MEMBRO_COMISSAO_MONITORIA = 1;
	
	/**
	 * Membro de comiss�o cient�fica.
	 */
	public static final int MEMBRO_COMISSAO_CIENTIFICA = 2;
	
	/**
	 * Membro de comiss�o de pesquisa.
	 */
	public static final int MEMBRO_COMISSAO_PESQUISA = 3;
	
	/**
	 * Membro de comiss�o de extens�o.
	 */
	public static final int MEMBRO_COMISSAO_EXTENSAO = 4;

	/**
	 * Membro da comiss�o integrada de ensino, pesquisa e extens�o (CIEPE). 
	 */
	public static final int MEMBRO_COMISSAO_INTEGRADA = 5;
	
	/**
	 * Membro da comiss�o de p�s-gradua��o. 
	 */
	public static final int MEMBRO_COMISSAO_POS_GRADUACAO = 6;
	
	/**Identificador do membro da comiss�o. Membro de comiss�o pode ser de pesquisa, monitoria ou extens�o.'*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_membro_comissao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/**Dados do servidor membro da comiss�o.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor = new Servidor();

	/**papel do membro da comissao. n�o confundir com os papeis do controle de acesso.*/
	@Column(name = "id_papel")
	private Integer papel;

	/**Data que o servidor foi cadastrado como membro.*/
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/**Data que o servidor foi desligado da comiss�o.*/
	@Column(name = "data_desligamento")
	private Date dataDesligamento;

	/**Informa se o membro da comiss�o est� ativo. Membros removidos possuem ativo=false'*/
	@Column(name = "ativo")
	private boolean ativo = true;

	/**data de inicio do mandato que vale por 2 anos e precisa ser renovado para que o membro da comissao volte a ser ativo.*/
	@Column(name = "data_inicio_mandato")
	private Date dataInicioMandato;

	/**data de finalizacao do mandato, se data vencida, o membro da comissao nao pode receber projetos ou relatorios para avaliar.*/
	@Column(name = "data_fim_mandato")
	private Date dataFimMandato;

	// Usu�rio que cadastrou o projeto
	/**registro de entrada do usuario que cadastrou o membro da comissao*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;

	/** Quantidade de avalia��es j� distribuidas */
	@Transient
	private int qntAvaliacoes=0;
	
	/**
	 * Campo Transient Retorna as avalia��es pendentes do membro da comiss�o
	 */
	@Transient
	private Set<AvaliacaoMonitoria> avaliacoesPendentes = new HashSet<AvaliacaoMonitoria>();

	/**
	 * Melhora a manipula��o da classe na view (chekbox)
	 */
	@Transient
	private boolean selecionado;

	public MembroComissao() {	    
	}
	
	public MembroComissao(int id) {
	    this.setId(id);
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataDesligamento() {
		return dataDesligamento;
	}

	public void setDataDesligamento(Date dataDesligamento) {
		this.dataDesligamento = dataDesligamento;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getPapel() {
		return papel;
	}

	public void setPapel(Integer papel) {
		this.papel = papel;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(servidor, "Servidor", lista);
		ValidatorUtil.validateRequired(dataInicioMandato, "In�cio do Mandato", lista);
		ValidatorUtil.validateRequired(dataFimMandato, "Final do Mandato", lista);
		ValidatorUtil.validateRequired(papel, "Comiss�o", lista);

		if ((dataFimMandato != null) && (dataInicioMandato != null)) {
			ValidatorUtil.validaOrdemTemporalDatas(dataInicioMandato,
							dataFimMandato,
							true,
							"Final do mandato deve ser posterior ao in�cio.",
							lista);
		}
		return lista;
	}

	/**
	 * Melhora a manipula��o da classe na view (chekbox)
	 * 
	 * @return true se selecionado
	 */
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	/**
	 *  ordena os membros da comiss�o pela data de cadastro
	 */
	public int compareTo(MembroComissao o) {
		return o.getDataCadastro().compareTo(this.getDataCadastro());
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
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
		final MembroComissao other = (MembroComissao) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/**
	 * Campo Transient Retorna as avalia��es pendentes do membro da comiss�o
	 * 
	 * @return
	 */
	public Set<AvaliacaoMonitoria> getAvaliacoesPendentes() {
		return avaliacoesPendentes;
	}

	public void setAvaliacoesPendentes(
			Set<AvaliacaoMonitoria> avaliacoesPendentes) {
		this.avaliacoesPendentes = avaliacoesPendentes;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataFimMandato() {
		return dataFimMandato;
	}

	public void setDataFimMandato(Date dataFimMandato) {
		this.dataFimMandato = dataFimMandato;
	}

	public Date getDataInicioMandato() {
		return dataInicioMandato;
	}

	public void setDataInicioMandato(Date dataInicioMandato) { 
		this.dataInicioMandato = dataInicioMandato;
	}

	/**
	 * Retorna o papel do membro como string
	 * 
	 * @return
	 */
	public String getPapelString() {
		return getPapelString(papel);
	}
	
	/**
	 * Retorna a descri��o do papel passado por parametro.
	 * 
	 * @return
	 * @param papel
	 */
	public static String getPapelString(int papel) {
		switch (papel) {
			case MEMBRO_COMISSAO_CIENTIFICA:
				return "COMISS�O CIENT�FICA";
			case MEMBRO_COMISSAO_MONITORIA:
				return "COMISS�O DE MONITORIA";
			case MEMBRO_COMISSAO_PESQUISA:
				return "COMISS�O DE PESQUISA";
			case MEMBRO_COMISSAO_EXTENSAO:
				return "COMIT� DE EXTENS�O";
			case MEMBRO_COMISSAO_INTEGRADA:
				return "COMISS�O INTEGRADA DE ENSINO, PESQUISA E EXTENS�O(CIEPE)";
			case MEMBRO_COMISSAO_POS_GRADUACAO:
				return "COMISS�O DE P�S-GRADUA��O";
			default:
				return "N�O INFORMADO";
			}
	}
	
	/**
	 * Usado na tela de visualiza��o de avalia��o de relat�rio parcial.
	 * 
	 * @return
	 */
	public String getUnidadeServidor() {
		return ( this.getServidor().getNome() + " - " + this.servidor.getUnidade().getNome()  );
	}
	
	/**
	 * Verifica se o membro da comiss�o est� dentro do per�odo de mandato v�lido.
	 * @return
	 */
	public boolean isMandatoVigente() {
	    return CalendarUtils.isDentroPeriodo(getDataInicioMandato(), getDataFimMandato());
	}

	public int getQntAvaliacoes() {
		return qntAvaliacoes;
	}

	public void setQntAvaliacoes(int qntAvaliacoes) {
		this.qntAvaliacoes = qntAvaliacoes;
	}
	
}
