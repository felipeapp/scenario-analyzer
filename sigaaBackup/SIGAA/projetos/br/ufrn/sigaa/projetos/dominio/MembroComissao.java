/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Representa um membro da comissão de avaliação dos projetos, 
 * seus relatórios e resumos. O membro pode participar de duas
 * comissões, são elas: 
 * - A Comissão Científica: que analisa, basicamente, os
 * resumos do Seminário de Iniciação a Docência (SID) e Seminário de Iniciação Científica (SIC). 
 * - A Comissão de Monitoria: que analisa os projetos de ensino e resumos do SID.
 * - A Comissão de Pesquisa: que analisa os projetos de pesquisa e resumos do SIC.
 * - A Comissão de Extensão: que analisa os ações e relatórios de extensão.
 * </p>
 * <p>
 * O tipo de comissão da qual o membro faz parte é determinada pelo atributo
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
	 * Membro de comissão de monitoria.  
	 */
	public static final int MEMBRO_COMISSAO_MONITORIA = 1;
	
	/**
	 * Membro de comissão científica.
	 */
	public static final int MEMBRO_COMISSAO_CIENTIFICA = 2;
	
	/**
	 * Membro de comissão de pesquisa.
	 */
	public static final int MEMBRO_COMISSAO_PESQUISA = 3;
	
	/**
	 * Membro de comissão de extensão.
	 */
	public static final int MEMBRO_COMISSAO_EXTENSAO = 4;

	/**
	 * Membro da comissão integrada de ensino, pesquisa e extensão (CIEPE). 
	 */
	public static final int MEMBRO_COMISSAO_INTEGRADA = 5;
	
	/**
	 * Membro da comissão de pós-graduação. 
	 */
	public static final int MEMBRO_COMISSAO_POS_GRADUACAO = 6;
	
	/**Identificador do membro da comissão. Membro de comissão pode ser de pesquisa, monitoria ou extensão.'*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_membro_comissao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/**Dados do servidor membro da comissão.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor = new Servidor();

	/**papel do membro da comissao. não confundir com os papeis do controle de acesso.*/
	@Column(name = "id_papel")
	private Integer papel;

	/**Data que o servidor foi cadastrado como membro.*/
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/**Data que o servidor foi desligado da comissão.*/
	@Column(name = "data_desligamento")
	private Date dataDesligamento;

	/**Informa se o membro da comissão está ativo. Membros removidos possuem ativo=false'*/
	@Column(name = "ativo")
	private boolean ativo = true;

	/**data de inicio do mandato que vale por 2 anos e precisa ser renovado para que o membro da comissao volte a ser ativo.*/
	@Column(name = "data_inicio_mandato")
	private Date dataInicioMandato;

	/**data de finalizacao do mandato, se data vencida, o membro da comissao nao pode receber projetos ou relatorios para avaliar.*/
	@Column(name = "data_fim_mandato")
	private Date dataFimMandato;

	// Usuário que cadastrou o projeto
	/**registro de entrada do usuario que cadastrou o membro da comissao*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;

	/** Quantidade de avaliações já distribuidas */
	@Transient
	private int qntAvaliacoes=0;
	
	/**
	 * Campo Transient Retorna as avaliações pendentes do membro da comissão
	 */
	@Transient
	private Set<AvaliacaoMonitoria> avaliacoesPendentes = new HashSet<AvaliacaoMonitoria>();

	/**
	 * Melhora a manipulação da classe na view (chekbox)
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
		ValidatorUtil.validateRequired(dataInicioMandato, "Início do Mandato", lista);
		ValidatorUtil.validateRequired(dataFimMandato, "Final do Mandato", lista);
		ValidatorUtil.validateRequired(papel, "Comissão", lista);

		if ((dataFimMandato != null) && (dataInicioMandato != null)) {
			ValidatorUtil.validaOrdemTemporalDatas(dataInicioMandato,
							dataFimMandato,
							true,
							"Final do mandato deve ser posterior ao início.",
							lista);
		}
		return lista;
	}

	/**
	 * Melhora a manipulação da classe na view (chekbox)
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
	 *  ordena os membros da comissão pela data de cadastro
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
	 * Campo Transient Retorna as avaliações pendentes do membro da comissão
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
	 * Retorna a descrição do papel passado por parametro.
	 * 
	 * @return
	 * @param papel
	 */
	public static String getPapelString(int papel) {
		switch (papel) {
			case MEMBRO_COMISSAO_CIENTIFICA:
				return "COMISSÃO CIENTÍFICA";
			case MEMBRO_COMISSAO_MONITORIA:
				return "COMISSÃO DE MONITORIA";
			case MEMBRO_COMISSAO_PESQUISA:
				return "COMISSÃO DE PESQUISA";
			case MEMBRO_COMISSAO_EXTENSAO:
				return "COMITÊ DE EXTENSÃO";
			case MEMBRO_COMISSAO_INTEGRADA:
				return "COMISSÃO INTEGRADA DE ENSINO, PESQUISA E EXTENSÃO(CIEPE)";
			case MEMBRO_COMISSAO_POS_GRADUACAO:
				return "COMISSÃO DE PÓS-GRADUAÇÃO";
			default:
				return "NÂO INFORMADO";
			}
	}
	
	/**
	 * Usado na tela de visualização de avaliação de relatório parcial.
	 * 
	 * @return
	 */
	public String getUnidadeServidor() {
		return ( this.getServidor().getNome() + " - " + this.servidor.getUnidade().getNome()  );
	}
	
	/**
	 * Verifica se o membro da comissão está dentro do período de mandato válido.
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
