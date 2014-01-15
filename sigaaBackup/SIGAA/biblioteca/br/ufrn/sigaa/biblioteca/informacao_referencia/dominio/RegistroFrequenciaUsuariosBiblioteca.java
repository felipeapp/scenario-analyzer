/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Criado em 10/11/2008
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 * Entidade que representa o acesso dos usuários a uma biblioteca. O registro é realizado pelo funcionário
 * da biblioteca usando os dados coletados por um sensor ou catraca.
 * 
 * @version 08/2010 O turno agora é representado por constantes, em vez de ser ligado à classe Turno.
 * 
 * @author Agostinho
 * @author Bráulio
 */

@Entity
@Table(schema="biblioteca", name="registro_frequencia_usuarios_bib")
public class RegistroFrequenciaUsuariosBiblioteca implements PersistDB {
	
	/** Indica que o registro da frequência foi feita para o turno matutino */
	public static final int TURNO_MATUTINO = 1;
	/** Indica que o registro da frequência foi feita para o turno vespertino */
	public static final int TURNO_VESPERTINO = 2;
	/** Indica que o registro da frequência foi feita para o turno noturno */
	public static final int TURNO_NOTURNO = 3;
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column(name="id_registro_frequencia_usuarios_bib")
	private int id;
	
	/** Data de cadastro do registro */
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/** Ano do acesso a biblioteca */
	@Column(name="ano")
	private int ano;
	
	/** Mês que acessou a biblioteca */
	@Column(name="mes")
	private int mes;
	
	/** Quantidade de acessos */
	@Column(name="quant_acesso")
	private Integer quantAcesso;
	
	/** Biblioteca de referência de acessos */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_biblioteca")
	private Biblioteca biblioteca = new Biblioteca();
	
	/** Turno no qual foi acessada. */
	@Column(name="turno")
	private int turno;

	@Transient
	private int totalAcessoJaneiro, totalAcessoFevereiro, totalAcessoMarco, totalAcessoAbril, totalAcessoMaio, totalAcessoJunho, totalAcessoJulho, 
				totalAcessoAgosto, totalAcessoSetembro, totalAcessoOutubro, totalAcessoNovembro, totalAcessoDezembro;

	@Transient
	private int somaJaneiro, somaFevereiro, somaMarco, somaAbril, somaMaio, somaJunho, 
				somaJulho, somaAgosto, somaSetembro, somaOutubro, somaNovembro, somaDezembro;
	
	private boolean ativo = true;
	
	
	/**
	 * Construtor padrão
	 */
	public RegistroFrequenciaUsuariosBiblioteca() {
		super();
	}
	
	/**
	 * Construtor registro persistido
	 * @param id
	 */
	public RegistroFrequenciaUsuariosBiblioteca(int id) {
		this.id = id;
	}


	
	////////////////////////////// auditoria  //////////////////////////////
	
	/**
	 * Registro entrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * Data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cricao")
	private Date dataCriacao;

	/**
	 * Registro entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	/////////////////////////////////////////////////////////////////
	

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
		RegistroFrequenciaUsuariosBiblioteca other = (RegistroFrequenciaUsuariosBiblioteca) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/** Retorna uma string com o turno no formato humano. */
	public static final String getDescricaoDoTurno(int i) {
		String[] DESCRICAO_TURNO = {"", "Matutino" , "Vespertino", "Noturno"};
		return DESCRICAO_TURNO[i];
	}
	
	/** Retorna o turno num formato humano. */
	public String getDescricaoDoTurno() {
		return getDescricaoDoTurno(this.turno);
	}
	
	/// sets e gets ///
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public Integer getQuantAcesso() {
		return quantAcesso;
	}

	public void setQuantAcesso(Integer quantAcesso) {
		this.quantAcesso = quantAcesso;
	}

	public int getTurno() {
		return turno;
	}

	public void setTurno(int turno) {
		this.turno = turno;
	}

	public int getTotalAcessoJaneiro() {
		return totalAcessoJaneiro;
	}

	public void setTotalAcessoJaneiro(int totalAcessoJaneiro) {
		this.totalAcessoJaneiro = totalAcessoJaneiro;
	}

	public int getTotalAcessoFevereiro() {
		return totalAcessoFevereiro;
	}

	public void setTotalAcessoFevereiro(int totalAcessoFevereiro) {
		this.totalAcessoFevereiro = totalAcessoFevereiro;
	}

	public int getTotalAcessoMarco() {
		return totalAcessoMarco;
	}

	public void setTotalAcessoMarco(int totalAcessoMarco) {
		this.totalAcessoMarco = totalAcessoMarco;
	}

	public int getTotalAcessoAbril() {
		return totalAcessoAbril;
	}

	public void setTotalAcessoAbril(int totalAcessoAbril) {
		this.totalAcessoAbril = totalAcessoAbril;
	}

	public int getTotalAcessoMaio() {
		return totalAcessoMaio;
	}

	public void setTotalAcessoMaio(int totalAcessoMaio) {
		this.totalAcessoMaio = totalAcessoMaio;
	}

	public int getTotalAcessoJunho() {
		return totalAcessoJunho;
	}

	public void setTotalAcessoJunho(int totalAcessoJunho) {
		this.totalAcessoJunho = totalAcessoJunho;
	}

	public int getTotalAcessoJulho() {
		return totalAcessoJulho;
	}

	public void setTotalAcessoJulho(int totalAcessoJulho) {
		this.totalAcessoJulho = totalAcessoJulho;
	}

	public int getTotalAcessoAgosto() {
		return totalAcessoAgosto;
	}

	public void setTotalAcessoAgosto(int totalAcessoAgosto) {
		this.totalAcessoAgosto = totalAcessoAgosto;
	}

	public int getTotalAcessoSetembro() {
		return totalAcessoSetembro;
	}

	public void setTotalAcessoSetembro(int totalAcessoSetembro) {
		this.totalAcessoSetembro = totalAcessoSetembro;
	}

	public int getTotalAcessoOutubro() {
		return totalAcessoOutubro;
	}

	public void setTotalAcessoOutubro(int totalAcessoOutubro) {
		this.totalAcessoOutubro = totalAcessoOutubro;
	}

	public int getTotalAcessoNovembro() {
		return totalAcessoNovembro;
	}

	public void setTotalAcessoNovembro(int totalAcessoNovembro) {
		this.totalAcessoNovembro = totalAcessoNovembro;
	}

	public int getTotalAcessoDezembro() {
		return totalAcessoDezembro;
	}

	public void setTotalAcessoDezembro(int totalAcessoDezembro) {
		this.totalAcessoDezembro = totalAcessoDezembro;
	}

	public int getSomaJaneiro() {
		return somaJaneiro;
	}

	public void setSomaJaneiro(int somaJaneiro) {
		this.somaJaneiro = somaJaneiro;
	}

	public int getSomaFevereiro() {
		return somaFevereiro;
	}

	public void setSomaFevereiro(int somaFevereiro) {
		this.somaFevereiro = somaFevereiro;
	}

	public int getSomaMarco() {
		return somaMarco;
	}

	public void setSomaMarco(int somaMarco) {
		this.somaMarco = somaMarco;
	}

	public int getSomaAbril() {
		return somaAbril;
	}

	public void setSomaAbril(int somaAbril) {
		this.somaAbril = somaAbril;
	}

	public int getSomaMaio() {
		return somaMaio;
	}

	public void setSomaMaio(int somaMaio) {
		this.somaMaio = somaMaio;
	}

	public int getSomaJunho() {
		return somaJunho;
	}

	public void setSomaJunho(int somaJunho) {
		this.somaJunho = somaJunho;
	}

	public int getSomaJulho() {
		return somaJulho;
	}

	public void setSomaJulho(int somaJulho) {
		this.somaJulho = somaJulho;
	}

	public int getSomaAgosto() {
		return somaAgosto;
	}

	public void setSomaAgosto(int somaAgosto) {
		this.somaAgosto = somaAgosto;
	}

	public int getSomaSetembro() {
		return somaSetembro;
	}

	public void setSomaSetembro(int somaSetembro) {
		this.somaSetembro = somaSetembro;
	}

	public int getSomaOutubro() {
		return somaOutubro;
	}

	public void setSomaOutubro(int somaOutubro) {
		this.somaOutubro = somaOutubro;
	}

	public int getSomaNovembro() {
		return somaNovembro;
	}

	public void setSomaNovembro(int somaNovembro) {
		this.somaNovembro = somaNovembro;
	}

	public int getSomaDezembro() {
		return somaDezembro;
	}

	public void setSomaDezembro(int somaDezembro) {
		this.somaDezembro = somaDezembro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
	
	

}