package fundacao.integracao.academico;

import java.io.Serializable;
import java.util.Date;


/**
 * DTO para recepção dos dados de discente via o serviço do SIGAA.
 * 
 * @author Gleydson
 *
 */
public class DiscenteDTO implements Serializable{

	/** Identificador do registro do discente. */
	private int idDiscente;

	/** Cpf do discente Nacional. */
	private Long cpf;

	/** Número de matrícula do discente. */
	private String matricula;

	/** Passaporte do discente estrangeiro. */
	private String passaporte;

	/** Nome do discente. */
	private String nome;

	/** Data de nascimento do discente. */
	private Date dataNascimento;

	/** Curso do discente. */
	private CursoDTO curso;

	/** Sexo do discente. */
	private Character sexo;
	
	/** Status do discente atual. */
	private Integer status;

	/** Nível de ensino do discente. */
	private String nivel;
	
	/** Data do cadastro do discente.*/
	private Date dataCadastro;  

	/** Data da alteração do status do discente.*/
	private Date dataAlteracaoStatus;

	/** Data da última atualização da pessoa. */
	private Date ultimaAtualizacao;

	public int getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(int idDiscente) {
		this.idDiscente = idDiscente;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public CursoDTO getCurso() {
		return curso;
	}

	public void setCurso(CursoDTO curso) {
		this.curso = curso;
	}

	public Character getSexo() {
		return sexo;
	}

	public void setSexo(Character sexo) {
		this.sexo = sexo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataAlteracaoStatus() {
		return dataAlteracaoStatus;
	}

	public void setDataAlteracaoStatus(Date dataAlteracaoStatus) {
		this.dataAlteracaoStatus = dataAlteracaoStatus;
	}

	public Date getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

}