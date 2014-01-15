/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 16/12/2009
*
*/

package br.ufrn.integracao.siged.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.integracao.dto.UsuarioDTO;

/**
 * Data Transfer Object que contém as principais informações
 * associadas a um documento do SIGED.
 * 
 * @author David Pereira
 * @author Raphael Medeiros
 *
 */
public class DocumentoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7347677795201220121L;

	/** Identificador do documento no SIGED. */
	private int id;
	
	/** Nome do arquivo associado ao documento. */
	private String nome;
	
	/** Nome do documento, um identificador amigável para servidor como título nas buscas */
	private String label;

	/** Identificador do tipo do documento. */
	private int tipoDocumento;
	
	/** Id do arquivo (na base de arquivos) associado ao documento. */
	private int idArquivo;

	/** Indica se o documento é indexável ou não. */
	private boolean indexavel;
	
	/** Palavras-chave do documento. */
	private String palavrasChaves;
	
	/** Localização física do documento. */
	private String localizacaoFisica;

	/** Uma lista de objetos do tipo DescritorDTO, que contém os descritores do documento e seus valores. */
	private List<DescritorDTO> descritores;
	
	/** Identificador da pasta em que o documento será armazenado. Pode ser null, se for armazenar na raiz. */
	private Integer localizacao;
	
	/**
	 * Utilizado para um objeto do tipo UsuaioDTO que armazena o registro do usuário logado.
	 */
	private UsuarioDTO usuario;
	
	/**
	 * Label da pasta, ou seja, o seu identificador para suportar a variação de
	 * nomes
	 */
	private String pastaId;

	/** Nome da pasta onde será armazenado o documento */
	private String pastaLabel;

	/** Arquivo do documento postado */
	private ArquivoDocumento arquivo;

	private String securityToken;
	
	/** Indica se o documento irá utilizar versionamento ou não */
	private boolean utilizaVersionamento;
	
	/**
	 * Quando o documento utiliza o versionamento, o valor deste atributo 
	 * indica a última versão do documento
	 */
	private Long versaoAtual;
	
	/**
	 * Lista de versões do documento
	 */
	private List<DocumentoVersaoDTO> versoes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(int tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public boolean isIndexavel() {
		return indexavel;
	}

	public void setIndexavel(boolean indexavel) {
		this.indexavel = indexavel;
	}

	public String getPalavrasChaves() {
		return palavrasChaves;
	}

	public void setPalavrasChaves(String palavrasChaves) {
		this.palavrasChaves = palavrasChaves;
	}

	public String getLocalizacaoFisica() {
		return localizacaoFisica;
	}

	public void setLocalizacaoFisica(String localizacaoFisica) {
		this.localizacaoFisica = localizacaoFisica;
	}

	public List<DescritorDTO> getDescritores() {
		return descritores;
	}

	public void setDescritores(List<DescritorDTO> descritores) {
		this.descritores = descritores;
	}

	public Integer getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Integer localizacao) {
		this.localizacao = localizacao;
	}

	public int getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(int idArquivo) {
		this.idArquivo = idArquivo;
	}
	
	public String getPastaId() {
		return pastaId;
	}

	public void setPastaId(String pastaId) {
		this.pastaId = pastaId;
	}

	public String getPastaLabel() {
		return pastaLabel;
	}

	public void setPastaLabel(String pastaLabel) {
		this.pastaLabel = pastaLabel;
	}

	public ArquivoDocumento getArquivo() {
		return arquivo;
	}

	public void setArquivo(ArquivoDocumento arquivo) {
		this.arquivo = arquivo;
	}
	
	public UsuarioDTO getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioDTO usuario) {
		this.usuario = usuario;
	}

	/**
	 * Adiciona um descritor a lista de descritores associados ao documento.
	 * 
	 * @param id
	 * @param valor
	 * @param adicionarPalavraChave
	 */
	public void adicionarDescritor(int id, String valor, boolean adicionarPalavraChave) {
		DescritorDTO descritor = new DescritorDTO();
		descritor.setId(id);
		descritor.setValor(valor);

		if (descritores == null)
			descritores = new ArrayList<DescritorDTO>();

		if (adicionarPalavraChave) {
			if (palavrasChaves!=null && palavrasChaves.isEmpty())
				palavrasChaves = valor;
			else
				palavrasChaves += ";" + valor;
		}

		descritores.add(descritor);
	}
	
	/**
	 * Adiciona uma versão do documento a lista de versões associadas.
	 * 
	 * @param id
	 * @param versao
	 * @param diff
	 */
	public void adicionarVersao(int id, long versao, String diff) {
		DocumentoVersaoDTO documentoVersao = new DocumentoVersaoDTO();
		
		documentoVersao.setId(id);
		documentoVersao.setVersao(versao);
		documentoVersao.setDiff(diff);
		
		if (versoes == null)
			versoes = new ArrayList<DocumentoVersaoDTO>();
		
		versoes.add(documentoVersao);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	public boolean isUtilizaVersionamento() {
		return utilizaVersionamento;
	}

	public void setUtilizaVersionamento(boolean utilizaVersionamento) {
		this.utilizaVersionamento = utilizaVersionamento;
	}

	public Long getVersaoAtual() {
		return versaoAtual;
	}

	public void setVersaoAtual(Long versaoAtual) {
		this.versaoAtual = versaoAtual;
	}

	public List<DocumentoVersaoDTO> getVersoes() {
		return versoes;
	}

	public void setVersoes(List<DocumentoVersaoDTO> versoes) {
		this.versoes = versoes;
	}
}