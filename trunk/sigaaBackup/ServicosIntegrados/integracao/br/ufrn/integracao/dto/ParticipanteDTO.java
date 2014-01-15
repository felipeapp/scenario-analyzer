/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data Transfer Object para informações dos participantes do projeto.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ParticipanteDTO implements Serializable {
	
	
	private Integer idEscolaridade;
	private String escolaridade;
	private Boolean ativoEscolaridade;

	private Integer idTipoParticipante;
	private String tipoParticipante;
	
	private Integer idCategoria;
	private String categoria;

	private Integer idBancoConta;
	private String bancoConta;
	private String agenciaConta;
	private Integer tipoConta;
	private String numeroConta;
	
	private Date dataCadastro;

	private Boolean coordenador;
	private Boolean viceCoordenador;
	
	private Integer origem;
	private String descricaoOrigem;
	
	private Integer quantidade;
	
	private Integer cargaHoraria;

	private Integer idFormacao;
	private String formacao;

	/** Função do participante.*/
	private Integer funcao;

	/** Descrição da Função do participante.*/
	private String descricaoFuncao;

	/** Nome do participante.*/
	private String nomeParticipante;

	/** CPF/CNPJ do participante.*/
	private Long cpfCnpjParticipante;

	/** Lotação do servidor participante.*/
	private String lotacaoServidor;
	
	/** Curso do aluno participante.*/
	private Integer idCursoAluno;
	private String cursoAluno;
	
	private PessoaProjetoDTO pessoaDTO;
	
	/** Instituição de origem do participante externo. */
	private String instituicaoOrigemParticipanteExterno;

	/** Telefone da instituição de origem do participante externo. */
	private String telefoneInstituicaoParticipanteExterno;

	public void setOrigem(Integer origem) {
		this.origem = origem;
	}

	public Integer getOrigem() {
		return origem;
	}

	public void setFuncao(Integer funcao) {
		this.funcao = funcao;
	}

	public Integer getFuncao() {
		return funcao;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCpfCnpjParticipante(Long cpfCnpjParticipante) {
		this.cpfCnpjParticipante = cpfCnpjParticipante;
	}

	public Long getCpfCnpjParticipante() {
		return cpfCnpjParticipante;
	}

	public void setNomeParticipante(String nomeParticipante) {
		this.nomeParticipante = nomeParticipante;
	}

	public String getNomeParticipante() {
		return nomeParticipante;
	}

	public void setLotacaoServidor(String lotacaoServidor) {
		this.lotacaoServidor = lotacaoServidor;
	}

	public String getLotacaoServidor() {
		return lotacaoServidor;
	}

	public void setCursoAluno(String cursoAluno) {
		this.cursoAluno = cursoAluno;
	}

	public String getCursoAluno() {
		return cursoAluno;
	}

	public void setInstituicaoOrigemParticipanteExterno(
			String instituicaoOrigemParticipanteExterno) {
		this.instituicaoOrigemParticipanteExterno = instituicaoOrigemParticipanteExterno;
	}

	public String getInstituicaoOrigemParticipanteExterno() {
		return instituicaoOrigemParticipanteExterno;
	}

	public void setTelefoneInstituicaoParticipanteExterno(
			String telefoneInstituicaoParticipanteExterno) {
		this.telefoneInstituicaoParticipanteExterno = telefoneInstituicaoParticipanteExterno;
	}

	public String getTelefoneInstituicaoParticipanteExterno() {
		return telefoneInstituicaoParticipanteExterno;
	}

	public void setBancoConta(String bancoConta) {
		this.bancoConta = bancoConta;
	}

	public String getBancoConta() {
		return bancoConta;
	}

	public void setAgenciaConta(String agenciaConta) {
		this.agenciaConta = agenciaConta;
	}

	public String getAgenciaConta() {
		return agenciaConta;
	}

	public void setNumeroConta(String numeroConta) {
		this.numeroConta = numeroConta;
	}

	public String getNumeroConta() {
		return numeroConta;
	}

	public void setTipoConta(Integer tipoConta) {
		this.tipoConta = tipoConta;
	}

	public Integer getTipoConta() {
		return tipoConta;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setDescricaoFuncao(String descricaoFuncao) {
		this.descricaoFuncao = descricaoFuncao;
	}

	public String getDescricaoFuncao() {
		return descricaoFuncao;
	}

	public void setDescricaoOrigem(String descricaoOrigem) {
		this.descricaoOrigem = descricaoOrigem;
	}

	public String getDescricaoOrigem() {
		return descricaoOrigem;
	}

	public void setIdEscolaridade(Integer idEscolaridade) {
		this.idEscolaridade = idEscolaridade;
	}

	public Integer getIdEscolaridade() {
		return idEscolaridade;
	}

	public void setEscolaridade(String escolaridade) {
		this.escolaridade = escolaridade;
	}

	public String getEscolaridade() {
		return escolaridade;
	}

	public void setAtivoEscolaridade(Boolean ativoEscolaridade) {
		this.ativoEscolaridade = ativoEscolaridade;
	}

	public Boolean getAtivoEscolaridade() {
		return ativoEscolaridade;
	}

	public void setIdTipoParticipante(Integer idTipoParticipante) {
		this.idTipoParticipante = idTipoParticipante;
	}

	public Integer getIdTipoParticipante() {
		return idTipoParticipante;
	}

	public void setTipoParticipante(String tipoParticipante) {
		this.tipoParticipante = tipoParticipante;
	}

	public String getTipoParticipante() {
		return tipoParticipante;
	}

	public void setIdBancoConta(Integer idBancoConta) {
		this.idBancoConta = idBancoConta;
	}

	public Integer getIdBancoConta() {
		return idBancoConta;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setCoordenador(Boolean coordenador) {
		this.coordenador = coordenador;
	}

	public Boolean getCoordenador() {
		return coordenador;
	}

	public void setViceCoordenador(Boolean viceCoordenador) {
		this.viceCoordenador = viceCoordenador;
	}

	public Boolean getViceCoordenador() {
		return viceCoordenador;
	}

	public void setIdFormacao(Integer idFormacao) {
		this.idFormacao = idFormacao;
	}

	public Integer getIdFormacao() {
		return idFormacao;
	}

	public void setFormacao(String formacao) {
		this.formacao = formacao;
	}

	public String getFormacao() {
		return formacao;
	}

	public void setIdCursoAluno(Integer idCursoAluno) {
		this.idCursoAluno = idCursoAluno;
	}

	public Integer getIdCursoAluno() {
		return idCursoAluno;
	}

	public void setPessoaDTO(PessoaProjetoDTO pessoaDTO) {
		this.pessoaDTO = pessoaDTO;
	}

	public PessoaProjetoDTO getPessoaDTO() {
		return pessoaDTO;
	}
}
