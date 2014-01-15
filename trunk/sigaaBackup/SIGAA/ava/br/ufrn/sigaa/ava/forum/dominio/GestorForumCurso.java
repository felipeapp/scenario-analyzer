package br.ufrn.sigaa.ava.forum.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Esta entidade representa um gestor de fórum de cursos.
 * O papel de gestor de fórum é dado pelos coordenadores de curso a um servidor qualquer. 
 * Este servidor terá permissão de enviar e-mails para os alunos a partir do fórum dos cursos
 * e também anexar arquivos nas mensagens do fórum dos cursos.
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "gestor_forum_curso", schema = "ava")
public class GestorForumCurso implements PersistDB{

	/** identificador */
	@Id @GeneratedValue
	@Column(name="id_gestor_forum_curso")
	private int id;
	
	/** Curso que o gestor é responsavel */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso")
	private Curso curso;
	
	/** servidor que é gestor do fórum */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;
	
	/** docente externo que é gestor do fórum */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_docente_externo")
	private DocenteExterno docenteExterno;
	
	/** registro de entrada do usuário que cadastrou */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	private RegistroEntrada registroCadastro;
	
	/** data que este registro foi criado */
	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Determina se o a participação está ativa. Utilizado para exclusão lógica.*/
	private boolean ativo = true;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}
	
	/**
	 * Retorna a pessoa de acordo com o tipo do gestor
	 * ser for servidor ou docente externo.
	 * @return
	 */
	@Transient
	public Pessoa getPessoa(){
		if ( !isEmpty(servidor) )
				return servidor.getPessoa();
		else if ( !isEmpty(docenteExterno) )
				return docenteExterno.getPessoa();
		return null;
	}
	
	public Integer getIdServidorDocenteExterno(){
		if ( !isEmpty(servidor) )
			return servidor.getId();
		else if ( !isEmpty(docenteExterno) )
				return docenteExterno.getId();
		return null;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}