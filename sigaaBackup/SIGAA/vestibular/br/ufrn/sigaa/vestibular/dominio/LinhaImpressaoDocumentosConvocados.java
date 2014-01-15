package br.ufrn.sigaa.vestibular.dominio;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import br.ufrn.arq.util.Formatador;

/**
 * Linha que será utilizada para a preenchimento dos documentos necessários para os discentes aprovados no vestibular.
 */
public class LinhaImpressaoDocumentosConvocados {

	/** Armazena o nome do curso */
	private String curso;
	/** Armazena a matricula do discente */
	private BigInteger matricula;
	/** Armazena o nome do discente */
	private String nome;
	/** Armazena o endereço do discente */
	private String endereco;
	/** Armazena o número do endereço do discente */
	private String enderecoNumero;
	/** Armazena o complemento do endereço do discente */
	private String enderecoComplemento;
	/** Armazena o bairro do discente */
	private String enderecoBairro;
	/** Armazena o cep do discente */
	private String cep;
	/** Armazena a cidade do discente */
	private String cidade;
	/** Armazena o estado do discente */
	private String estado;
	/** Armazena o telefone do discente */
	private String tel;
	/** Armazena o email do discente */
	private String email;
	/** Armazena o celular do discente */
	private String celular;
	/** Armazena a data de nascimento do discente */
	private Date dataNascimento;
	/** Armazena a cidade de nascimento do discente */
	private String cidadeNascimento;
	/** Armazena o estado de nascimento do discente */
	private String estadoNascimento;
	/** Armazena o pais de nascimento do discente */
	private String paisNascimento;
	/** Armazena o sexo do discente */
	private Character sexo;
	/** Armazena o estado cívil do discente */
	private String estadoCivil;
	/** Armazena o nome do pai do discente */
	private String nomePai;
	/** Armazena o nome da mãe do discente */
	private String nomeMae;
	/** Armazena o cpf do discente */
	private BigInteger cpf;
	/** Armazena o RG do discente */
	private String rg;
	/** Armazena o órgão do RG do discente */
	private String orgaoRG;
	/** Armazena o estado do RG do discente */
	private String estadoRG;
	/** Armazena o número do título de eleitor do discente */
	private String tituloEleitorNumero;
	/** Armazena o seção do título de eleitor do discente */
	private String tituloEleitorSecao;
	/** Armazena a zona do título de eleitor do discente */
	private String tituloEleitoZona;
	/** Armazena o estado do título de eleitor do discente */
	private String tituloEleitoEstado;
	/** Armazena o nome da escola que o discente concluiu o 2 grau*/
	private String nomeEscola;
	/** Armazena o ano de conclusão do 2 grau do discente */
	private String anoConclusao;
	/** Armazena o número de inscrição do vestibular do discente */
	private int numeroInscricao;
	/** Armazena o semestre de aprovação do discente */
	private int semestreAprocacao;
	/** Armazena o Município do curso do discente */
	private String municipioCurso;
	/** Armazena o nome do Concursos do discente */
	private String concurso;
	/** Armazena a chave primaria do candidato */
	private int idCandidato;
	/** Armazena o  argumento final do discente  */
	private Double argumentoFinal;
	/** Armazena qual a listagem ou chamada do discente */
	private String listagem;
	/** Armazena o período máximo de conclusão do curso pelo discente  */
	private int semestreMaximoConclusao; 
	/** Armazena a classificação do candidato */
	private int classificacaoCandidato;
	/** Armazena a habilitação da matriz curricular */
	private String habilitacao;
	/** Descrição do Grau Acadêmico */
	private String descricao;
	/** Sigla do Turno do curso */
	private String sigla;
	/** Matriz Curricular do Candidato*/
	private int matrizCurricular;
	/** Tipo de Convocação do Candidato*/
	private int tipoConvocacao;
	/** Grupo de cotas o qual o discente foi convocado. */
	private String grupoCota;
	/** Indica que o discente está aprovado dentro do número de vagas. */
	private Boolean dentroNumeroVagas;
	/** Grupo de cotas o qual o discente se inscreveu originalmente. */
	private String grupoCotaInscricao;
	
	/** Retorna o argumento da 1 Fase */
	public JRBeanArrayDataSource getprimeiraFase() throws JRException { 
		return new JRBeanArrayDataSource(resultado1Fase.toArray()); 
	} 
	/** Armazenando o argumento na 1 Fase */
	private Collection<ResultadoArgumento> resultado1Fase = new ArrayList<ResultadoArgumento>();
	/** Armazenando o argumento na 2 Fase */
	private Collection<ResultadoArgumento> resultado2Fase = new ArrayList<ResultadoArgumento>();
	
	/** Formata da descrição do curso para Curso (Grau Acadêmico) (Turno) */
	public String getCursoCompleto() {
		return curso + (habilitacao != null ? " (" + habilitacao + ")":"") + " (" + descricao.charAt(0) + ") " + "(" +sigla + ")";
	}
	
	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public BigInteger getMatricula() {
		return matricula;
	}

	public void setMatricula(BigInteger matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getEnderecoNumero() {
		return enderecoNumero;
	}

	public void setEnderecoNumero(String enderecoNumero) {
		this.enderecoNumero = enderecoNumero;
	}

	public String getEnderecoComplemento() {
		return enderecoComplemento;
	}

	public void setEnderecoComplemento(String enderecoComplemento) {
		this.enderecoComplemento = enderecoComplemento;
	}

	public String getEnderecoBairro() {
		return enderecoBairro;
	}

	public void setEnderecoBairro(String enderecoBairro) {
		this.enderecoBairro = enderecoBairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getCidadeNascimento() {
		return cidadeNascimento;
	}

	public void setCidadeNascimento(String cidadeNascimento) {
		this.cidadeNascimento = cidadeNascimento;
	}

	public String getEstadoNascimento() {
		return estadoNascimento;
	}

	public void setEstadoNascimento(String estadoNascimento) {
		this.estadoNascimento = estadoNascimento;
	}

	public String getPaisNascimento() {
		return paisNascimento;
	}

	public void setPaisNascimento(String paisNascimento) {
		this.paisNascimento = paisNascimento;
	}

	/**
	 * Retorna o sexo do candidato aprovado.
	 * @return
	 */
	public String getSexoDescricao() {
		if (sexo.equals('M')) 
			return "Masculino";
		else
			return "Feminino";
	}
	
	/** Método para recuperar o cpf formatado */
	public String getCpfFormatado() {
		return Formatador.getInstance().formatarCPF_CNPJ(cpf.longValue());
	}

	public void setSexo(Character sexo) {
		this.sexo = sexo;
	}
	
	public Character getSexo() {
		return sexo;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public String getNomePai() {
		return nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public BigInteger getCpf() {
		return cpf;
	}

	public void setCpf(BigInteger cpf) {
		this.cpf = cpf;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getOrgaoRG() {
		return orgaoRG;
	}

	public void setOrgaoRG(String orgaoRG) {
		this.orgaoRG = orgaoRG;
	}

	public String getEstadoRG() {
		return estadoRG;
	}

	public void setEstadoRG(String estadoRG) {
		this.estadoRG = estadoRG;
	}

	public String getTituloEleitorNumero() {
		return tituloEleitorNumero;
	}

	public void setTituloEleitorNumero(String tituloEleitorNumero) {
		this.tituloEleitorNumero = tituloEleitorNumero;
	}

	public String getTituloEleitorSecao() {
		return tituloEleitorSecao;
	}

	public void setTituloEleitorSecao(String tituloEleitorSecao) {
		this.tituloEleitorSecao = tituloEleitorSecao;
	}

	public String getTituloEleitoZona() {
		return tituloEleitoZona;
	}

	public void setTituloEleitoZona(String tituloEleitoZona) {
		this.tituloEleitoZona = tituloEleitoZona;
	}

	public String getTituloEleitoEstado() {
		return tituloEleitoEstado;
	}

	public void setTituloEleitoEstado(String tituloEleitoEstado) {
		this.tituloEleitoEstado = tituloEleitoEstado;
	}

	public String getNomeEscola() {
		return nomeEscola;
	}

	public void setNomeEscola(String nomeEscola) {
		this.nomeEscola = nomeEscola;
	}

	public String getAnoConclusao() {
		return anoConclusao;
	}

	public void setAnoConclusao(String anoConclusao) {
		this.anoConclusao = anoConclusao;
	}

	public int getNumeroInscricao() {
		return numeroInscricao;
	}

	public void setNumeroInscricao(int numeroInscricao) {
		this.numeroInscricao = numeroInscricao;
	}

	public int getSemestreAprocacao() {
		return semestreAprocacao;
	}

	public void setSemestreAprocacao(int semestreAprocacao) {
		this.semestreAprocacao = semestreAprocacao;
	}

	public String getMunicipioCurso() {
		return municipioCurso;
	}

	public void setMunicipioCurso(String municipioCurso) {
		this.municipioCurso = municipioCurso;
	}

	public String getConcurso() {
		return concurso;
	}

	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}

	public int getIdCandidato() {
		return idCandidato;
	}

	public void setIdCandidato(int idCandidato) {
		this.idCandidato = idCandidato;
	}

	public Double getArgumentoFinal() {
		return argumentoFinal;
	}

	public void setArgumentoFinal(Double argumentoFinal) {
		this.argumentoFinal = argumentoFinal;
	}

	public Collection<ResultadoArgumento> getResultado1Fase() {
		return resultado1Fase;
	}

	public void setResultado1Fase(Collection<ResultadoArgumento> resultado1Fase) {
		this.resultado1Fase = resultado1Fase;
	}

	public Collection<ResultadoArgumento> getResultado2Fase() {
		return resultado2Fase;
	}

	public void setResultado2Fase(Collection<ResultadoArgumento> resultado2Fase) {
		this.resultado2Fase = resultado2Fase;
	}

	public String getListagem() {
		return listagem;
	}

	public void setListagem(String listagem) {
		this.listagem = listagem;
	}

	public int getSemestreMaximoConclusao() {
		return semestreMaximoConclusao;
	}

	public void setSemestreMaximoConclusao(int semestreMaximoConclusao) {
		this.semestreMaximoConclusao = semestreMaximoConclusao;
	}

	public int getClassificacaoCandidato() {
		return classificacaoCandidato;
	}

	public void setClassificacaoCandidato(int classificacaoCandidato) {
		this.classificacaoCandidato = classificacaoCandidato;
	}

	public String getHabilitacao() {
		return habilitacao;
	}

	public void setHabilitacao(String habilitacao) {
		this.habilitacao = habilitacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public int getMatrizCurricular() {
		return matrizCurricular;
	}

	public void setMatrizCurricular(int matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}

	public int getTipoConvocacao() {
		return tipoConvocacao;
	}

	public void setTipoConvocacao(int tipoConvocacao) {
		this.tipoConvocacao = tipoConvocacao;
	}

	public String getGrupoCota() {
		return grupoCota;
	}

	public void setGrupoCota(String grupoCota) {
		this.grupoCota = grupoCota;
	}

	public Boolean getDentroNumeroVagas() {
		return dentroNumeroVagas;
	}

	public void setDentroNumeroVagas(Boolean dentroNumeroVagas) {
		this.dentroNumeroVagas = dentroNumeroVagas;
	}

	public String getGrupoCotaInscricao() {
		return grupoCotaInscricao;
	}

	public void setGrupoCotaInscricao(String grupoCotaInscricao) {
		this.grupoCotaInscricao = grupoCotaInscricao;
	}
	
}