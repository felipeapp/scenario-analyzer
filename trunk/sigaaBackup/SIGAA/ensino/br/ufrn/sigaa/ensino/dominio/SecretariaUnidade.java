/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 10/10/2007
 *
 */	
package br.ufrn.sigaa.ensino.dominio;

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

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Esta entidade armazena as informações das secretarias de departamento e de
 * programas de pós-graduação.
 *
 * @author Gleydson Lima
 *
 */

@Entity
@Table(name = "secretaria_unidade", schema = "ensino")
public class SecretariaUnidade implements Validatable {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_secretaria_unidade", nullable = false)
	private int id;

	/** Usuario da secretária */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	/** Unidade da secretária */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade")
	private Unidade unidade;
	
	/** Curso da secretária */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso")
	private Curso curso;

	/** Data de inicio do secretariado */
	@Temporal(TemporalType.DATE)
	private Date inicio;

	/** Usuário que efetuou o login no Sistema */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atribuidor")
	private RegistroEntrada usuarioAtribuidor;
	
	/** Fim do período de secretaria */
	@Temporal(TemporalType.DATE)
	private Date fim;
	
	/** Usuário que efetuou o logout no Sistema */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_finalizacao")
	private RegistroEntrada usuarioFinalizador;

	/** Diz se uma secretaria foi cancelada, deve ser utilizado para o caso de ter sido cadastrado errado e foi preciso cancelar.
	 * NÃO INDICA O FIM DE UM MANDATO. E SIM O CANCELAMENTO DE UMA SECRETARIA CADASTRADA ERRADA */
	private boolean ativo = true;

	@Transient
	private Servidor servidor;
	
	/**
	 * Diz qual o tipo da unidade da secretaria.
	 * Observe as constantes abaixo
	 */
	@Transient
	private int tipo;

	/** Tipo de secretaria departamento */
	public static int DEPARTAMENTO = 1;
	/** Tipo de secretaria centro */
	public static int CENTRO = 2;
	/** Tipo de secretaria curso */
	public static int CURSO = 3;
	/** Tipo de secretaria programa */
	public static int PROGRAMA = 4;
	/** Tipo de secretaria unidade academica Especializada */
	public static int UNID_ACADEMICA_ESPECIALIZADA = 5;

	public SecretariaUnidade() {
		super();
	}
	
	public SecretariaUnidade(int id, int idUsuario, String login, String nomeUsuario, String celular, String telefone, String email, String ramal,  Integer idUnidade, String nomeUnidade, Integer tipoAcademica,
			Integer idCurso, String nomeCurso, String nomeMunicipio, Character nivelCurso, Date inicio, Date fim, boolean ativo, Integer idUnidadeCoordenacaoCurso, String nomeUnidadeCoordenacaoCurso ) {
		this.id = id;
		this.usuario = new Usuario(idUsuario, nomeUsuario, login);
		this.usuario.setRamal(ramal);
		this.usuario.setEmail(email);
		this.usuario.getPessoa().setTelefone(telefone);
		this.usuario.getPessoa().setCelular(celular);
		if( idUnidade != null ){
			this.unidade = new Unidade(idUnidade);
			this.unidade.setNome(nomeUnidade);
			this.unidade.setTipoAcademica(tipoAcademica);
		
		}
		if( idCurso != null ){
			this.curso = new Curso(idCurso);
			this.curso.setNome( nomeCurso );
			this.curso.setMunicipio(new Municipio(nomeMunicipio));
			this.curso.setNivel(nivelCurso);
		}
		this.inicio = inicio;
		this.fim = fim;
		this.ativo = ativo;
		if (idUnidadeCoordenacaoCurso != null) {
			this.curso.setUnidadeCoordenacao(new Unidade(idUnidadeCoordenacaoCurso));
			this.curso.getUnidadeCoordenacao().setNome(nomeUnidadeCoordenacaoCurso);
		}
	}

	public SecretariaUnidade(int id, int idUsuario, String login, String nomeUsuario, String celular, String telefone, String email, String ramal,  Integer idUnidade, String nomeUnidade, Integer tipoAcademica,
			Integer idCurso, String nomeCurso, String nomeMunicipio, Character nivelCurso, Date inicio, Date fim, boolean ativo ) {
		this(id, idUsuario, login, nomeUsuario, celular, telefone, email, ramal, idUnidade, nomeUnidade, tipoAcademica, idCurso, nomeCurso, nomeMunicipio, nivelCurso, inicio, fim, ativo, null, null);
	}
	
	public SecretariaUnidade(int id, int idUsuario, String login, String nomeUsuario, String ramal, Integer idUnidade, String nomeUnidade,
			Integer idCurso, String nomeCurso, String nomeMunicipio, Date inicio, Date fim, boolean ativo ) {
		this(id, idUsuario, login, nomeUsuario, null, null, null, ramal, idUnidade, nomeUnidade, null, idCurso, nomeCurso, nomeMunicipio, null, inicio, fim, ativo);
	}


	public SecretariaUnidade(int id) {
		this.id = id;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public RegistroEntrada getUsuarioAtribuidor() {
		return usuarioAtribuidor;
	}

	public void setUsuarioAtribuidor(RegistroEntrada usuarioAtribuidor) {
		this.usuarioAtribuidor = usuarioAtribuidor;
	}

	public RegistroEntrada getUsuarioFinalizador() {
		return usuarioFinalizador;
	}

	public void setUsuarioFinalizador(RegistroEntrada usuarioFinalizador) {
		this.usuarioFinalizador = usuarioFinalizador;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/** Realiza as validações referente a Secretaria */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(usuario, "Usuário", erros);
		return erros;
	}

	/**
	 * Returna o Curso
	 */
	public Curso getCurso() {
		return curso;
	}

	/**
	 * Seta o curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/**
	 * Retorna o nome do usuário 
	 */
	public String getNome(){
		return getUsuario().getPessoa().getNome();
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	/** Retorna um boleano informando se a secretaria e de centro */
	public boolean isCentro(){
		if (getUnidade() != null && getUnidade().getTipoAcademica() != null)
			return getUnidade().getTipoAcademica() == TipoUnidadeAcademica.CENTRO;
		else
			return tipo == CENTRO;
	}

	/** Retorna um boleano informando se a secretaria e de departamento */
	public boolean isDepartamento(){
		return tipo == DEPARTAMENTO;
	}
	
	/** Retorna um boleano informando se a secretaria e de departamento */
	public boolean isPrograma(){
		return tipo == PROGRAMA;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ativo ? 1231 : 1237);
		result = prime * result + ((curso == null) ? 0 : curso.hashCode());
		result = prime * result + ((unidade == null) ? 0 : unidade.hashCode());
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
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
		SecretariaUnidade other = (SecretariaUnidade) obj;
		if (ativo != other.ativo)
			return false;
		if (curso == null) {
			if (other.curso != null)
				return false;
		} else if (!curso.equals(other.curso))
			return false;
		if (unidade == null) {
			if (other.unidade != null)
				return false;
		} else if (!unidade.equals(other.unidade))
			return false;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		return true;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	
	
}