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

/**
 * Classe que representa os atributos de discente stricto. 
 * Essa classe é utilizada na integração SIGAA/CAPES via webService.
 * 
 * @author Rafael Gomes
 *
 */
public class DiscenteStrictoCapesDTO implements Serializable {
	
	/** 
	 *	No cliente o programa é a concatenação de {@link br.ufrn.sigaa.dominio.Curso#codProgramaCAPES} + 
	 * 	{@link br.ufrn.sigaa.dominio.InstituicoesEnsino#COD_CAPES_UFRN}
	 * */
	private String programa; 
	
	/** Armazena o identificador do curso do Discente. */
	private int idCurso;
	
	/** {@link br.ufrn.sigaa.dominio.InstituicoesEnsino#COD_CAPES_UFRN}*/
	private String ies;
	
	/** Armazena o identificador do Discente. */
	private int idDiscente;
	
	/** {@link br.ufrn.comum.dominio.PessoaGeral#cpf_cnpj} */
	private String cpf;
	
	/** {@link br.ufrn.sigaa.pessoa.dominio.Discente#dataCadastro} */
	private Date dataMatricula;
	
	/** {@link br.ufrn.comum.dominio.PessoaGeral#dataNascimento} */
	private Date dataNascimento;
	
	/** {@link br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto#prazoMaximoConclusao} */
	private Date dataPrevisaoConclusao;
	
	/** {@link br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto.dataAtualizacao} */
	private Date dataSituacaoDiscente;
	
	/** {@link br.ufrn.comum.dominio.PessoaGeral#email}*/
	private String email;
	
	/**	
	 * Indica o Estágio do discente no curso, contendo as seguintes variáveis da CAPES:
	 *  <ul>	
	 *  	<li>"I" -  Inicial (menos de 1/3 dos créditos de disciplinas concluídos)
     *      <li>"N" -  Intermediário (entre 1/3 e 2/3 dos créditos de disciplinas concluídos)
     *      <li>"F" -  Final (mais de 2/3 dos créditos de disciplinas concluídos) >> br.ufrn.academico.dominio.StatusDiscente.FORMANDO
     *      <li>"E" -  Elaboração de tese/dissertação (parte experimental)
     *      <li>"C" -  Créditos de disciplinas concluídos, aguardando a defesa da tese/dissertação
     *      <li>"T" -  Créditos de disciplinas concluídos, tese/dissertação defendida  >> br.ufrn.academico.dominio.StatusDiscente.DEFENDIDO
     *  </ul>    
     * */
	private String estagio;
	
	/**
	 * 	Representa o sexo do Discente.
	 * 	{@link br.ufrn.comum.dominio.PessoaGeral#sexo}
	 *  Valores na CAPES:
	 *  <ul>
	 *  	<li>"M" - Masculino
	 *  	<li>"F" - Feminino
	 *  </ul>
	 */
   	private String genero;
   	
   	/**
   	 * Nível de Ensino do Discente Stricto.
   	 * {@link br.ufrn.sigaa.pessoa.dominio.Discente#nivel}
   	 *  Valores na CAPES:
   	 *  <ul>
   	 *  	<li>"D" - Doutorado [CAPES]
   	 *  	<li>"M" - Mestrado Acadêmico [CAPES]
   	 *  	<li>"F" - Mestrado Profissional [CAPES]
   	 *  </ul>
   	 */
   	private String nivel;
   	
   	/**
   	 * Nome do Discente OK {@link br.ufrn.comum.dominio.PessoaGeral#getNome()}
   	 */
   	private String nome;
   	
   	/** Pais Emissão do RG do discente stricto*/
   	private String paisEmissao;
   	
   	/** {@link  br.ufrn.comum.dominio.PessoaGeral#paisOrigem}*/
   	private String paisNascimento;
   	
   	/** {@link br.ufrn.comum.dominio.PessoaGeral#passaporte}*/
   	private String passaporte;
   	
   	/** No request, vários discentes podem ser enviados ao mesmo tempo, assim, 
   	 *  cada discente deve receber um número sequencial para tratamento de erros. 
   	 *  O primeiro discente do request deve possuir sequencial 1. 	 */
   	private String sequencial;
   	
   	/** 
   	 * Informação sobre o status do discente no curso de stricto
   	 * {@link br.ufrn.sigaa.pessoa.dominio.Discente#status} 
   	 * Valores no webService da CAPES: 
   	 *  <ul>
   	 *  	<li>"A" - Ativo 
     *      <li>"S" - Trancado
     *      <li>"C" - Desligado 
     *      <li>"T" - Titulado 
     *  </ul>
   	 */
   	private String situacaoDiscente;
   	
   	/** Tipo do curso com relação a sua concepção, ex: regular ou Associado.
   	 *  Valores no webService da CAPES:  
   	 *   <ul>
   	 *   	<li>"R" - Regular
     *      <li>"I" - Minter/Dinter > [Dinter - Os Projetos de Doutorado Interinstitucional; Minter - Mestrado Interinstitucional]  
     *      <li>"A" - Associado
     *   </ul>   
     * */
   	private String tipoCurso;
   	
   	/** Indica se o discente possui vínculo empregatício (true) ou não (false). 
   	 * Se informado true, o elemento vinculoEmpregaticio deve ser preenchido. */
   	private boolean vinculo;
   	
   	/* ## Início dos atributos de vinculo Empregatício ## */
   	/** Data de Admissão do discente do seu vinculo empregatício.*/
   	private Date dataAdmissao;
   	/** Data de Desligamento do discente do seu vinculo empregatício.*/
   	private Date dataDesligamento;
   	/** Empresa a qual o discente mantém o seu vinculo empregatício.*/
   	private String empresa;
   	/** Campo para verificar se o discente possui a função de professor substituto.*/
   	private boolean professorSubstituto;
   	/** Valor do rendimento do discente no seu vinculo empregatício.*/
   	private float rendimento;
   	
    /* ## Fim dos atributos de Vinculo Empregatício ## */
   	
   	
   	/* ##  Início dos Campos referentes a Bolsa  ## */
   	/** 
   	 * Agência mantenedora da bolsa do Discente
   	 *    Valores no webService da CAPES:
     *      <ul>
     *          <li>"CAPES"</li>	<li>"CNPQ"</li>	   <li>"FACEP"</li>
     *          <li>"FADESP"</li>	<li>"FAP_DF"</li>  <li>"FAP_SE"</li>
     *          <li>"FAPEAL"</li>	<li>"FAPEMAT"</li> <li>"FAPEMIG"</li>
     *          <li>"FAPEPI"</li>   <li>"FAPERGS"</li> <li>"FAPERJ"</li>
     *          <li>"FAPESB"</li>   <li>"FAPESP"</li>  <li>"FAPESQ"</li>
     *          <li>"FCC"</li>      <li>"FINEP"</li>   <li>"FUNAPE"</li>
     *          <li>"FUNCAP"</li>   <li>"FUNCITEC"</li><li>"FUNDECT"</li>
     *          <li>"MCT"</li> <li>"NAO_INFORMADA"</li> <li>"OUTRA"</li>
     *      </ul>
   	 * */
   	private String agencia;
   	/** Data de início da bolsa do discente */
   	private Date inicioBolsa; 
   	/** Data de término da bolsa do discente */
   	private Date fimBolsa;
   	/** Campo para preenchimento de outra Agência, caso esta não esteja cadastrada. */
   	private String outraAgencia;

   	/** 
   	 *  Campo da situação da bolsa do discente stricto.
   	 *   <ul>  
   	 *     <li>"A" - Ativa</li> 	
     *     <li>"X" - Cancelada</li>		
     *     <li>"S" - Suspensa</li>	
     *   </ul>  
   	 * */
   	private String situacaoBolsa;
   	
   	/* ##  Fim dos Campos referentes a Bolsa  ## */
   	
	
   	
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
	 * @return the idCurso
	 */
	public int getIdCurso() {
		return idCurso;
	}

	/**
	 * @param idCurso the idCurso to set
	 */
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
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
	 * @return the cpf
	 */
	public String getCpf() {
		return cpf;
	}

	/**
	 * @param cpf the cpf to set
	 */
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	/**
	 * @return the dataMatricula
	 */
	public Date getDataMatricula() {
		return dataMatricula;
	}

	/**
	 * @param dataMatricula the dataMatricula to set
	 */
	public void setDataMatricula(Date dataMatricula) {
		this.dataMatricula = dataMatricula;
	}

	/**
	 * @return the dataNascimento
	 */
	public Date getDataNascimento() {
		return dataNascimento;
	}

	/**
	 * @param dataNascimento the dataNascimento to set
	 */
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	/**
	 * @return the dataPrevisaoConclusao
	 */
	public Date getDataPrevisaoConclusao() {
		return dataPrevisaoConclusao;
	}

	/**
	 * @param dataPrevisaoConclusao the dataPrevisaoConclusao to set
	 */
	public void setDataPrevisaoConclusao(Date dataPrevisaoConclusao) {
		this.dataPrevisaoConclusao = dataPrevisaoConclusao;
	}

	/**
	 * @return the dataSituacaoDiscente
	 */
	public Date getDataSituacaoDiscente() {
		return dataSituacaoDiscente;
	}

	/**
	 * @param dataSituacaoDiscente the dataSituacaoDiscente to set
	 */
	public void setDataSituacaoDiscente(Date dataSituacaoDiscente) {
		this.dataSituacaoDiscente = dataSituacaoDiscente;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the estagio
	 */
	public String getEstagio() {
		return estagio;
	}

	/**
	 * @param estagio the estagio to set
	 */
	public void setEstagio(String estagio) {
		this.estagio = estagio;
	}

	/**
	 * @return the genero
	 */
	public String getGenero() {
		return genero;
	}

	/**
	 * @param genero the genero to set
	 */
	public void setGenero(String genero) {
		this.genero = genero;
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
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the paisEmissao
	 */
	public String getPaisEmissao() {
		return paisEmissao;
	}

	/**
	 * @param paisEmissao the paisEmissao to set
	 */
	public void setPaisEmissao(String paisEmissao) {
		this.paisEmissao = paisEmissao;
	}

	/**
	 * @return the paisNascimento
	 */
	public String getPaisNascimento() {
		return paisNascimento;
	}

	/**
	 * @param paisNascimento the paisNascimento to set
	 */
	public void setPaisNascimento(String paisNascimento) {
		this.paisNascimento = paisNascimento;
	}

	/**
	 * @return the passaporte
	 */
	public String getPassaporte() {
		return passaporte;
	}

	/**
	 * @param passaporte the passaporte to set
	 */
	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
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
	 * @return the situacaoDiscente
	 */
	public String getSituacaoDiscente() {
		return situacaoDiscente;
	}

	/**
	 * @param situacaoDiscente the situacaoDiscente to set
	 */
	public void setSituacaoDiscente(String situacaoDiscente) {
		this.situacaoDiscente = situacaoDiscente;
	}

	/**
	 * @return the tipoCurso
	 */
	public String getTipoCurso() {
		return tipoCurso;
	}

	/**
	 * @param tipoCurso the tipoCurso to set
	 */
	public void setTipoCurso(String tipoCurso) {
		this.tipoCurso = tipoCurso;
	}

	/**
	 * @return the vinculo
	 */
	public boolean isVinculo() {
		return vinculo;
	}

	/**
	 * @param vinculo the vinculo to set
	 */
	public void setVinculo(boolean vinculo) {
		this.vinculo = vinculo;
	}

	/**
	 * @return the agencia
	 */
	public String getAgencia() {
		return agencia;
	}

	/**
	 * @param agencia the agencia to set
	 */
	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	/**
	 * @return the inicioBolsa
	 */
	public Date getInicioBolsa() {
		return inicioBolsa;
	}

	/**
	 * @param inicioBolsa the inicioBolsa to set
	 */
	public void setInicioBolsa(Date inicioBolsa) {
		this.inicioBolsa = inicioBolsa;
	}

	/**
	 * @return the fimBolsa
	 */
	public Date getFimBolsa() {
		return fimBolsa;
	}

	/**
	 * @param fimBolsa the fimBolsa to set
	 */
	public void setFimBolsa(Date fimBolsa) {
		this.fimBolsa = fimBolsa;
	}

	/**
	 * @return the outraAgencia
	 */
	public String getOutraAgencia() {
		return outraAgencia;
	}

	/**
	 * @param outraAgencia the outraAgencia to set
	 */
	public void setOutraAgencia(String outraAgencia) {
		this.outraAgencia = outraAgencia;
	}

	/**
	 * @return the situacaoBolsa
	 */
	public String getSituacaoBolsa() {
		return situacaoBolsa;
	}

	/**
	 * @param situacaoBolsa the situacaoBolsa to set
	 */
	public void setSituacaoBolsa(String situacaoBolsa) {
		this.situacaoBolsa = situacaoBolsa;
	}

	/**
	 * @return the dataAdmissao
	 */
	public Date getDataAdmissao() {
		return dataAdmissao;
	}

	/**
	 * @param dataAdmissao the dataAdmissao to set
	 */
	public void setDataAdmissao(Date dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}

	/**
	 * @return the dataDesligamento
	 */
	public Date getDataDesligamento() {
		return dataDesligamento;
	}

	/**
	 * @param dataDesligamento the dataDesligamento to set
	 */
	public void setDataDesligamento(Date dataDesligamento) {
		this.dataDesligamento = dataDesligamento;
	}

	/**
	 * @return the empresa
	 */
	public String getEmpresa() {
		return empresa;
	}

	/**
	 * @param empresa the empresa to set
	 */
	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	/**
	 * @return the professorSubstituto
	 */
	public boolean isProfessorSubstituto() {
		return professorSubstituto;
	}

	/**
	 * @param professorSubstituto the professorSubstituto to set
	 */
	public void setProfessorSubstituto(boolean professorSubstituto) {
		this.professorSubstituto = professorSubstituto;
	}

	/**
	 * @return the rendimento
	 */
	public float getRendimento() {
		return rendimento;
	}

	/**
	 * @param rendimento the rendimento to set
	 */
	public void setRendimento(float rendimento) {
		this.rendimento = rendimento;
	}
   	

   	
   	
}
