package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe responsável pela transferência de dados de pessoas para a aplicação de
 * auto atendimento
 * 
 * @author Rafael Moreira
 * 
 */
public class PessoaDto implements Serializable {

	private static final long serialVersionUID = -1L;

	/** Identificador */
	private int id;

	/** campo cpf */
	private long cpf;
	
	/** Tipo de pessoa. F - Física; J - Jurídica. */
	private char tipo;
	
	/** Sexo da pessoa. M - Masculino; F - Feminino. */
	private char sexo;

	/** campo nome */
	private String nome;

	private Date dataNascimento;
	
	private String tipoDedo; // polegar Direito ou Esquerdo
	/**
	 * Armazena a digtal da pessoa caso a mesma exista no Servidor. Serve para
	 * exibir mensagem informando que a digital já existe.
	 */
	private byte[] digital;
	
	private String cartaoAcessoRu;

	/** discente **/
	private Long matricula;
	private String curso;
	private String centro;

	/** servidor **/
	private Long siape;
	private String lotacao;

	/**
	 * usado nas buscar para trazer pessoas que sejam só alunos ou só
	 * funcionários, por exemplo.
	 **/
	private String vinculo;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (cpf ^ (cpf >>> 32));
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		PessoaDto other = (PessoaDto) obj;
		if (cpf != other.cpf)
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	public int compareTo(Object obj) {
		PessoaDto pessoa = (PessoaDto) obj;
		int ultimaComparacao = nome.compareTo(pessoa.nome);
		return (ultimaComparacao != 0 ? ultimaComparacao : nome
				.compareTo(pessoa.nome));
	}

	/* GETTERS AND SETTERS */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getCpf() {
		return cpf;
	}

	public void setCpf(long cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipoDedo() {
		return tipoDedo;
	}

	public void setTipoDedo(String tipoDedo) {
		this.tipoDedo = tipoDedo;
	}

	public byte[] getDigital() {
		return digital;
	}

	public void setDigital(byte[] digital) {
		this.digital = digital;
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public String getCentro() {
		return centro;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	public Long getSiape() {
		return siape;
	}

	public void setSiape(Long siape) {
		this.siape = siape;
	}

	public String getLotacao() {
		return lotacao;
	}

	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}

	public String getVinculo() {
		return vinculo;
	}

	public void setVinculo(String vinculo) {
		this.vinculo = vinculo;
	}

	public String getNomeLimitado(int limite) {
		if (nome.length() > limite) {
			return nome.substring(0, limite);
		} else {
			return nome;
		}

	}
	
	public char getTipo() {
		return tipo;
	}
	
	public void setTipo(char tipo) {
		this.tipo = tipo;
	}
	
	public char getSexo() {
		return sexo;
	}
	
	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public void setCartaoAcessoRu(String cartaoAcessoRu) {
		this.cartaoAcessoRu = cartaoAcessoRu;
	}

	public String getCartaoAcessoRu() {
		return cartaoAcessoRu;
	}

}