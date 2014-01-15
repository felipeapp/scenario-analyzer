/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/09/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 *    Entidade que guarda os dados dos arquivos que geram os números de controle (dados do campo 001) 
 *    na exportação para a FGV. É a partir desse número que vai no campo 001 que a FGV identifica um 
 *    título ou autoridade.
 *
 * @author jadson
 * @since 14/09/2009
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "arquivo_de_carga_numero_controle_fgv", schema = "biblioteca")
public class ArquivoDeCargaNumeroControleFGV implements Validatable{

	
	public static final short ARQUIVO_BIBLIOGRAFICO = 1;
	public static final short ARQUIVO_AUTORIDADES = 2;
	
	public static final int TAMANHO_NUMERO_CONTROLE_FGV = 9; // o tamanho do número de controle que tem que ir no arquivo
	
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name="id_arquivo_de_carga_numero_controle_fgv", nullable=false)
	private int id;
	
	
	/**
	 * Onde começa, vem no arquivo. ex.: RN0003445  (sem o RN)
	 */
	@Column(name="numero_inicial_sequencia", nullable=false)
	private int numeroInicialSequencia;
	
	/**
	 * Onde termina, vem no arquivo. ex.: RN0003500 (sem o RN)
	 */
	@Column(name="numero_final_sequencia", nullable=false)
	private int numeroFinalSequencia;
	
	/**
	 * O número atual para usar, vai de numeroInicialSequencia até numeroFinalSequencia.
	 */
	@Column(name="numero_atual_sequencia", nullable=false)
	private int numeroAtualSequencia;
	
	/**
	 * Se é para geração de número de títulos ou autoridades
	 */
	@Column(name="tipo", nullable=false)
	private short tipo = ARQUIVO_BIBLIOGRAFICO;
	
	
	/**
	 * Indica quando a sequência de números se esgotou.
	 * Se numeroAtualSequencia > numeroFinalSequencia, ativo = false
	 */
	@Column(name="ativo", nullable=false)
	private boolean ativo = true;
	
	
	/**
	 * Construtor padrão.
	 */
	public ArquivoDeCargaNumeroControleFGV(){
		
	}
	
	
	////////////////////////////INFORMACOES AUDITORIA  ///////////////////////////////////////	


	/**
	 * O usuário que fez a carga do arquivo.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * A data da carga do arquivo no sistema. 
	 * Caso seja feita a carga de vários arquivo pega o mais antigo ainda ativo.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_carga")
	private Date dataCarga;

	/**
	 * Registro entrada  do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	@AtualizadoEm
	private Date dataUltimaAtualizacao;

	
	//////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Construtor passando o tipo, se é um arquivo com números de controle de  títulos ou autoridades.
	 */
	public ArquivoDeCargaNumeroControleFGV(short tipo) {
		this.tipo = tipo;
	}
	
	
	
	/**
	 * Construtor usado pelo hibernate para montar os resultados das pesquisas
	 */
	public ArquivoDeCargaNumeroControleFGV(int id, int numeroInicialSequencia, int  numeroFinalSequencia, int numeroAtualSequencia, Date dataCarga, short tipo){
		this.id = id;
		this.numeroInicialSequencia = numeroInicialSequencia;
		this.numeroFinalSequencia = numeroFinalSequencia;
		this.numeroAtualSequencia = numeroAtualSequencia;
		this.dataCarga = dataCarga;
		this.tipo = tipo;
	}
	
	
	
	/**
	 *     <p>Retorna a quantidade total de números de controle que o arquivo carregado possuia, 
	 *  mesmo aqueles que foram usados.</p>
	 *     <p>O intervalo é fechado, o número inicial e final entra na conta.</p>
	 *
	 * @return
	 */
	public int getQuantidadeNumeroControle(){
		return (numeroFinalSequencia - numeroInicialSequencia)+1;
	}
	
	
	
	/**
	 *   Retorna a quantidade de números de controle que faltam serem usados para essa arquivo.
	 *
	 * @return
	 */
	public int getQuantidadeNumeroControleNaoUsados(){
		return (numeroFinalSequencia - numeroAtualSequencia) + 1;
	}
	
	
	
	/**
	 *   Verifica se ainda existe números livre para exportação dentre os dados do arquivo 
	 *   carregado no sistema.
	 * @return
	 */
	public boolean possuiNumeroLivre(){
		if(getQuantidadeNumeroControleNaoUsados() > 0 && ativo)
			return true;
		else
			return false;
	}
	
	
	
	
	/**
	 *   <p>Retorna o próximo número livre do artigo e avança para o próximo registro.</p>
	 *   <p>Obs.: Se não existirem mais números livres retorna null</p>
	 *
	 * @return
	 */
	public Integer nextNumeroLivre(){
		
		if(! ativo)
			return null; // não tem mais
		
		if(numeroAtualSequencia+1 >  numeroFinalSequencia) // se o próximo passou do número final, desativa o arquivo 
			this.ativo = false;
		
		return numeroAtualSequencia++;
	}
	
	
	/**
	 * Realiza as validações nos dados lidos do arquivo ou digitado pelo usuário.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		
		ListaMensagens erros = new ListaMensagens();
		
		if( numeroInicialSequencia < 0 )
			erros.addErro("O número inicial da sequência não pode ser menor que zero.");
			
		if( numeroFinalSequencia  < 0 )	
			erros.addErro("O número final da sequência não pode ser menor que zero.");
			
		if( numeroFinalSequencia  < numeroInicialSequencia ){
			erros.addErro("O número final da sequência não pode ser menor que o número final.");
		}
		
		return erros;
	}
	
	
	
	
	
	/// sets e gets
	
	
	public int getNumeroInicialSequencia() {
		return numeroInicialSequencia;
	}

	/**
	 *   Retorna o número inicial formato do tamanho certo
	 *
	 * @return
	 */
	public String getNumeroInicialSequenciaFormatado() {
		
		String numeroInicialFormatado = new Integer(numeroInicialSequencia).toString();
		for (int i = numeroInicialFormatado.toString().length();  i < TAMANHO_NUMERO_CONTROLE_FGV ; i++) {
			numeroInicialFormatado = "0"+numeroInicialFormatado;
		}
		return numeroInicialFormatado;
	}
	
	
	public void setNumeroInicialSequencia(int numeroInicialSequencia) {
		this.numeroInicialSequencia = numeroInicialSequencia;
	}

	public int getNumeroFinalSequencia() {
		return numeroFinalSequencia;
	}
	
	/**
	 *   Retorna o número final formato do tamanho certo
	 *
	 * @return
	 */
	public String getNumeroFinalSequenciaFormatado() {
		
		String numeroFinalFormatado = new Integer(numeroFinalSequencia).toString();
		for (int i = numeroFinalFormatado.toString().length();  i < TAMANHO_NUMERO_CONTROLE_FGV ; i++) {
			numeroFinalFormatado = "0"+numeroFinalFormatado;
		}
		return numeroFinalFormatado;
	}
	

	public void setNumeroFinalSequencia(int numeroFinalSequencia) {
		this.numeroFinalSequencia = numeroFinalSequencia;
	}

	public int getNumeroAtualSequencia() {
		return numeroAtualSequencia;
	}
	
	
	/**
	 *   Retorna o número atual formato do tamanho certo
	 *
	 * @return
	 */
	public String getNumeroAtualSequenciaFormatado() {
		
		String numeroAtualFormatado = new Integer(numeroAtualSequencia).toString();
		for (int i = numeroAtualFormatado.toString().length();  i < TAMANHO_NUMERO_CONTROLE_FGV ; i++) {
			numeroAtualFormatado = "0"+numeroAtualFormatado;
		}
		return numeroAtualFormatado;
	}
	

	public void setNumeroAtualSequencia(int numeroAtualSequencia) {
		this.numeroAtualSequencia = numeroAtualSequencia;
	}

	public short getTipo() {
		return tipo;
	}

	public void setTipo(short tipo) {
		this.tipo = tipo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntrada getRegistroCriacao() {
		return registroCriacao;
	}

	public void setRegistroCriacao(RegistroEntrada registroCriacao) {
		this.registroCriacao = registroCriacao;
	}

	public Date getDataCarga() {
		return dataCarga;
	}

	public void setDataCarga(Date dataCarga) {
		this.dataCarga = dataCarga;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public void setRegistroUltimaAtualizacao(
			RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		
	}

	
}
