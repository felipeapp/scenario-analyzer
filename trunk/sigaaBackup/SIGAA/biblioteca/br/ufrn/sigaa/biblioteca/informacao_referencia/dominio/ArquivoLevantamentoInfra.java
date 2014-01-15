/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/11/2010
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Guardas os anexos da resposta a um pedido de levantamento de infra-estrutura.
 *
 * @author Bráulio
 * 
 * @see LevantamentoInfraEstrutura
 */
@Entity
@Table( name="arquivo_levantamento_infra", schema="biblioteca" )
public class ArquivoLevantamentoInfra implements Validatable {
	
	/** Os tipos de arquivos que podem ser anexados a um levantamento de infra-estrutura. */
	public static final Set<String> ARQUIVOS_PERMITIDOS = new HashSet<String>( Arrays.asList(
			new String[]{
			"text/plain", // .txt
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
			"application/vnd.oasis.opendocument.spreadsheet", // .ods
			"application/vnd.oasis.opendocument.text", // .odt
			"application/vnd.ms-excel", // .xls
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
			"application/pdf", // .pdf
			"application/msword", // .doc
			"application/octet-stream" // o Internet Explorer não manda o tipo do arquivo
			}) );
	
	/** As extensões permitidas. */
	public static final Set<String> EXTENSOES_PERMITIDAS = new HashSet<String>( Arrays.asList(
			new String[]{".txt", ".docx", ".ods", ".odt", "xls", "xlsx", ".pdf", ".doc" }) );
	
	/** Tamanho máximo de cada arquivo, em MB. */
	public static final int TAMANHO_MAXIMO_ARQUIVO = 10;
	
	/////// Campos da entidade //////

	/** O id do arquivo de levantamento (não é o id do arquivo do sistema propriamente dito). */
	@Id
	@Column(name = "id_arquivo_levantamento_infra")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="biblioteca.solicitacoes_usuario_sequence") })
	private int id;
	
	/** O levantamento de infra-estrutura ao qual o arquivo pertence. */
	@ManyToOne
	@JoinColumn( name="id_levantamento_infra" )
	private LevantamentoInfraEstrutura levantamentoInfra;
	
	/** O id do arquivo no sistema. */
	@Column(name="id_arquivo")
	private int idArquivo;
	
	/////// Campos auxiliares ///////
	
	/** Campo transiente que guarda o conteúdo do arquivo. */
	@Transient
	private byte[] conteudo;
	
	/** Campo transiente que guarda o nome do arquivo. */
	@Transient
	private String nome;
	
	/** Campo transiente que guarda o tipo MIME do arquivo. */
	@Transient
	private String tipo;
	
	//////////////////////////////////
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens msgs = new ListaMensagens();
		
		if ( levantamentoInfra == null )
			msgs.addErro("Um arquivo de levantamento de infra-estrutura deve estar vinculado a um levantamento.");
		
		if ( idArquivo <= 0 )
			msgs.addErro("Um arquivo de levantamento de infra-estrutura deve estar vinculado a um arquivo do sistema.");
		
		return msgs;
	}

	///////// GETs e SETs /////////
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public LevantamentoInfraEstrutura getLevantamentoInfra() {
		return levantamentoInfra;
	}

	public void setLevantamentoInfra(LevantamentoInfraEstrutura levantamentoInfra) {
		this.levantamentoInfra = levantamentoInfra;
	}

	public int getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(int idArquivo) {
		this.idArquivo = idArquivo;
	}

	/** <b>Atenção:</b> campo transiente. */
	public byte[] getConteudo() {
		return conteudo;
	}

	/** <b>Atenção:</b> campo transiente. */
	public void setConteudo(byte[] conteudo) {
		this.conteudo = conteudo;
	}

	/** <b>Atenção:</b> campo transiente. */
	public String getNome() {
		return nome;
	}

	/** <b>Atenção:</b> campo transiente. */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** <b>Atenção:</b> campo transiente. */
	public String getTipo() {
		return tipo;
	}

	/** <b>Atenção:</b> campo transiente. */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
}
