/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 10/03/2010
 *
 */

package br.ufrn.sigaa.ensino.stricto.remoto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe que representa os atributos da Tese de Dissertação de discente stricto. 
 * Essa classe é utilizada na integração SIGAA/CAPES via webService.
 * 
 * @author Rafael Gomes
 *
 */
public class TeseDissertacaoCapesDTO implements Serializable{

	/** Código identificador de Discente Stricto*/
	private int idDiscente;
	
	/** {@link br.ufrn.sigaa.dominio.Curso#codProgramaCAPES}*/
	private String programa; 
	
	/** {@link br.ufrn.sigaa.dominio.InstituicoesEnsino#COD_CAPES_UFRN}*/
	private String ies;
	
	/** 
	 * {@link br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa#area}
	 * Esta é uma lista de AreaConhecimentoCNPQ, podendo usar este, pois é a mesma lista encontrada nos arquivos da CAPES.
	 * */
	private String[] areasConhecimento;

	/**
	 * Arquivo da tese de Dissertação do Discente, necessariamente em base64.  
	 */
	private byte[] arquivoTeseDissertacao;
	
	
	/** Campo verificador da autorização de divulgação da Tese de Dissertação do discente. */
	private boolean autorizaDivulgacao;
	
	
	private String biblioteca;
	
	/* Início - coOrientador */
	/** 
	 * Campo responsável pela manipulação do cpf do CoOrientador do Discente.
	 * */
	private String coOrientador_cpf;
	
	/** 
	 * Campo responsável pela manipulação do nome do CoOrientador do Discente.
	 * */
	private String coOrientador_nome;
	/* Fim - coOrientador */

	/** 
	 * CPF do Discente autor da tese de dissertação. Campo localizado em: br.ufrn.sigaa.pessoa.dominio.Discente >> br.ufrn.comum.dominio.PessoaGeral.cpf_cnpj
	 * */
	private String cpfAutor;
	
	/** Data de defesa da tese de dissertação. Campo localizado em: {@link br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto#getDataDefesa()} */
	private Date dataDefesa;
	
	/**
	 * Responsável por armazenar os dados dos outros membros (internos ou externos) da banca  de defesa do discente.
	 */
	private List<HashMap<String, String>> demaisMembros;
	
	/** Idioma que a tese de dissertação foi escrita. */
	private String idioma;
	
	/* - Início - motivoJustificativa - */
	private String justificativa;
	
	/**
	 * Motivo de não divulgação da tese.
	 * Valores no webService da CAPES:
     *  <ul>
	 * 		<li>"P" - Exigência de periódico de não divulgação até a publicação
     *     	<li>"C" - Não envio por exigência contratual
     *     	<li>"O" - Outro
     *  </ul>
	 * */
	private String motivo;
	/* - Fim - motivoJustificativa - */
	
   	/**
   	 * Nível de Ensino do Discente Stricto.
   	 * {@link br.ufrn.sigaa.pessoa.dominio.Discente#nivel}
 	 *  <xs:enumeration value="D"/> Doutorado [CAPES]
   	 *  <xs:enumeration value="M"/> Mestrado Acadêmico [CAPES]
   	 *  <xs:enumeration value="F"/>	Mestrado Profissional [CAPES]
   	 */
   	private String nivel;
   	
   	/** Nome do arquivo da tese de dissertação do discente de stricto. */
   	private String nomeArquivo;
   	
   	/** CPF do Orientador do Discente, informações localizada em: # br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto.orientacao.getPessoa.cpf_cnpj */
   	private String orientadorPrincipal_cpf;
   	
   	/** Nome do Orientador do Discente, informação localizada em: # br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto.orientacao.getNomeOrientador */
   	private String orientadorPrincipal_nome;
   	
   	/** Número de páginas da tese de dissertação, informação localizada em: {@link br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa#paginas} */
   	private int paginas;
   	
   	/** Palavras chaves inseridas da tese de dissertação, informação localizado em: {@link br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa#palavrasChave} */
   	private String palavrasChave;
   	
   	/** Passaporte do discente autor da dissertação, informação localizada em: {@link br.ufrn.comum.dominio.PessoaGeral#passaporte} */
   	private String passaporteAutor;
   	
   	/** Resumo da tese de dissertação, localizado em: {@link br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa#getResumo()} */
   	private String resumo;
   	
	/** No request, vários discentes podem ser enviados ao mesmo tempo, assim, 
   	 *  cada discente deve receber um número sequencial para tratamento de erros. 
   	 *  O primeiro discente do request deve possuir sequencial 1. 	 */
   	private String sequencial;
   	
   	/** Título da tese de dissertação, localizado em: {@link br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa#getTitulo()} */
   	private String titulo;
   	
   	/** Número de volumes da tese de dissertação. */
   	private int volumes;
   	
   	/** Código identificador da banca de defesa do discente de stricto sensu.*/
   	private int idBancaPos;
   	
	
	/** Map para armazenar o cpf e nome dos membros da banca*/
	private Map<String, String> membroBanca = new HashMap<String, String>();


	
	/**  GETTERS AND SETTERS **/
	
	/**
	 * @return the programa
	 */
	public String getPrograma() {
		return programa;
	}


	/**
	 * @param programa the programa to set
	 */
	public void setPrograma(String programa) {
		this.programa = programa;
	}


	/**
	 * @return the idDiscente
	 */
	public int getIdDiscente() {
		return idDiscente;
	}


	/**
	 * @param idDiscente the idDiscente to set
	 */
	public void setIdDiscente(int idDiscente) {
		this.idDiscente = idDiscente;
	}


	/**
	 * @return the ies
	 */
	public String getIes() {
		return ies;
	}


	/**
	 * @param ies the ies to set
	 */
	public void setIes(String ies) {
		this.ies = ies;
	}


	/**
	 * @return the areasConhecimento
	 */
	public String[] getAreasConhecimento() {
		return areasConhecimento;
	}


	/**
	 * @return the arquivoTeseDissertacao
	 */
	public byte[] getArquivoTeseDissertacao() {
		return arquivoTeseDissertacao;
	}


	/**
	 * @param arquivoTeseDissertacao the arquivoTeseDissertacao to set
	 */
	public void setArquivoTeseDissertacao(byte[] arquivoTeseDissertacao) {
		this.arquivoTeseDissertacao = arquivoTeseDissertacao;
	}


	/**
	 * @param areasConhecimento the areasConhecimento to set
	 */
	public void setAreasConhecimento(String[] areasConhecimento) {
		this.areasConhecimento = areasConhecimento;
	}


	/**
	 * @return the autorizaDivulgacao
	 */
	public boolean isAutorizaDivulgacao() {
		return autorizaDivulgacao;
	}


	/**
	 * @param autorizaDivulgacao the autorizaDivulgacao to set
	 */
	public void setAutorizaDivulgacao(boolean autorizaDivulgacao) {
		this.autorizaDivulgacao = autorizaDivulgacao;
	}


	/**
	 * @return the biblioteca
	 */
	public String getBiblioteca() {
		return biblioteca;
	}


	/**
	 * @param biblioteca the biblioteca to set
	 */
	public void setBiblioteca(String biblioteca) {
		this.biblioteca = biblioteca;
	}


	/**
	 * @return the coOrientador_cpf
	 */
	public String getCoOrientador_cpf() {
		return coOrientador_cpf;
	}


	/**
	 * @param coOrientadorCpf the coOrientador_cpf to set
	 */
	public void setCoOrientador_cpf(String coOrientadorCpf) {
		coOrientador_cpf = coOrientadorCpf;
	}


	/**
	 * @return the coOrientador_nome
	 */
	public String getCoOrientador_nome() {
		return coOrientador_nome;
	}


	/**
	 * @param coOrientadorNome the coOrientador_nome to set
	 */
	public void setCoOrientador_nome(String coOrientadorNome) {
		coOrientador_nome = coOrientadorNome;
	}


	/**
	 * @return the cpfAutor
	 */
	public String getCpfAutor() {
		return cpfAutor;
	}


	/**
	 * @param cpfAutor the cpfAutor to set
	 */
	public void setCpfAutor(String cpfAutor) {
		this.cpfAutor = cpfAutor;
	}


	/**
	 * @return the dataDefesa
	 */
	public Date getDataDefesa() {
		return dataDefesa;
	}


	/**
	 * @param dataDefesa the dataDefesa to set
	 */
	public void setDataDefesa(Date dataDefesa) {
		this.dataDefesa = dataDefesa;
	}


	/**
	 * @return the demaisMembros
	 */
	public List<HashMap<String, String>> getDemaisMembros() {
		return demaisMembros;
	}


	/**
	 * @param demaisMembros the demaisMembros to set
	 */
	public void setDemaisMembros(List<HashMap<String, String>> demaisMembros) {
		this.demaisMembros = demaisMembros;
	}


	/**
	 * @return the idioma
	 */
	public String getIdioma() {
		return idioma;
	}


	/**
	 * @param idioma the idioma to set
	 */
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}


	/**
	 * @return the justificativa
	 */
	public String getJustificativa() {
		return justificativa;
	}


	/**
	 * @param justificativa the justificativa to set
	 */
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}


	/**
	 * @return the motivo
	 */
	public String getMotivo() {
		return motivo;
	}


	/**
	 * @param motivo the motivo to set
	 */
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}


	/**
	 * @return the nivel
	 */
	public String getNivel() {
		return nivel;
	}


	/**
	 * @param nivel the nivel to set
	 */
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}


	/**
	 * @return the nomeArquivo
	 */
	public String getNomeArquivo() {
		return nomeArquivo;
	}


	/**
	 * @param nomeArquivo the nomeArquivo to set
	 */
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}


	/**
	 * @return the orientadorPrincipal_cpf
	 */
	public String getOrientadorPrincipal_cpf() {
		return orientadorPrincipal_cpf;
	}


	/**
	 * @param orientadorPrincipalCpf the orientadorPrincipal_cpf to set
	 */
	public void setOrientadorPrincipal_cpf(String orientadorPrincipalCpf) {
		orientadorPrincipal_cpf = orientadorPrincipalCpf;
	}


	/**
	 * @return the orientadorPrincipal_nome
	 */
	public String getOrientadorPrincipal_nome() {
		return orientadorPrincipal_nome;
	}


	/**
	 * @param orientadorPrincipalNome the orientadorPrincipal_nome to set
	 */
	public void setOrientadorPrincipal_nome(String orientadorPrincipalNome) {
		orientadorPrincipal_nome = orientadorPrincipalNome;
	}


	/**
	 * @return the paginas
	 */
	public int getPaginas() {
		return paginas;
	}


	/**
	 * @param paginas the paginas to set
	 */
	public void setPaginas(int paginas) {
		this.paginas = paginas;
	}


	/**
	 * @return the palavrasChave
	 */
	public String getPalavrasChave() {
		return palavrasChave;
	}


	/**
	 * @param palavrasChave the palavrasChave to set
	 */
	public void setPalavrasChave(String palavrasChave) {
		this.palavrasChave = palavrasChave;
	}


	/**
	 * @return the passaporteAutor
	 */
	public String getPassaporteAutor() {
		return passaporteAutor;
	}


	/**
	 * @param passaporteAutor the passaporteAutor to set
	 */
	public void setPassaporteAutor(String passaporteAutor) {
		this.passaporteAutor = passaporteAutor;
	}


	/**
	 * @return the resumo
	 */
	public String getResumo() {
		return resumo;
	}


	/**
	 * @param resumo the resumo to set
	 */
	public void setResumo(String resumo) {
		this.resumo = resumo;
	}


	/**
	 * @return the sequencial
	 */
	public String getSequencial() {
		return sequencial;
	}


	/**
	 * @param sequencial the sequencial to set
	 */
	public void setSequencial(String sequencial) {
		this.sequencial = sequencial;
	}


	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}


	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}


	/**
	 * @return the volumes
	 */
	public int getVolumes() {
		return volumes;
	}


	/**
	 * @param volumes the volumes to set
	 */
	public void setVolumes(int volumes) {
		this.volumes = volumes;
	}


	/**
	 * @return the idBancaPos
	 */
	public int getIdBancaPos() {
		return idBancaPos;
	}


	/**
	 * @param idBancaPos the idBancaPos to set
	 */
	public void setIdBancaPos(int idBancaPos) {
		this.idBancaPos = idBancaPos;
	}


	/**
	 * @return the membroBanca
	 */
	public Map<String, String> getMembroBanca() {
		return membroBanca;
	}


	/**
	 * @param membroBanca the membroBanca to set
	 */
	public void setMembroBanca(Map<String, String> membroBanca) {
		this.membroBanca = membroBanca;
	}
	
	

	
	
}
