/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/*******************************************************************************
 * <p>
 * Classe utilizada no contexto de Prestação Serviços. Representa um membro da
 * equipe de uma proposta de prestação de serviços. O modulo de prestação de
 * serviços ainda não está totalmente definido no SIGAA.
 * </p>
 * 
 * @author Gleydson
 * @author Victor Hugo
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "membro_prestacao_servico")
public class MembroPrestacaoServico implements Validatable {

	// constantes
	// tipo de membro
	public static final Short MEMBRO = 1;
	public static final Short COORDENADOR = 2;

	// categoria funcional
	public static final Short DOCENTE = 1;
	public static final Short TEC_NIVEL_MEDIO = 2;
	public static final Short TEC_NIVEL_SUPERIOR = 3;
	public static final Short ALUNO_VOLUNTARIO = 4;
	public static final Short ALUNO_BOLSISTA = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_membro_prestacao_servico", nullable = false)
	private int id;

	@Column(name = "tipo")
	private Short tipo;

	@Column(name = "ch_semanal")
	private Short chSemanal;

	@Column(name = "fone_fax")
	private String foneFax;

	@Column(name = "categoria_funcional")
	private Short categoriaFuncional;

	// base de pesquisa ou extensão a qual o coordenador pertence
	@Column(name = "id_unidade_coordenador")
	private Integer idUnidadeCoordenador;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa", unique = false, nullable = true, insertable = true, updatable = true)
	private Pessoa pessoa = new Pessoa();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	private Servidor servidor = new Servidor();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente", unique = false, nullable = true, insertable = true, updatable = true)
	private Discente discente = new Discente();

	@ManyToOne
	@JoinColumn(name = "id_prestacao_servico", referencedColumnName = "id_prestacao_servico")
	private br.ufrn.sigaa.extensao.dominio.PrestacaoServico prestacaoServico;

	public MembroPrestacaoServico() {
	}

	public MembroPrestacaoServico(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Short getTipo() {
		return this.tipo;
	}

	public void setTipo(Short tipo) {
		this.tipo = tipo;
	}

	public Short getChSemanal() {
		return this.chSemanal;
	}

	public void setChSemanal(Short chSemanal) {
		this.chSemanal = chSemanal;
	}

	public String getFoneFax() {
		return this.foneFax;
	}

	public void setFoneFax(String foneFax) {
		this.foneFax = foneFax;
	}

	public Short getCategoriaFuncional() {
		return this.categoriaFuncional;
	}

	public void setCategoriaFuncional(Short categoriaFuncional) {
		this.categoriaFuncional = categoriaFuncional;
	}

	public Integer getIdUnidadeCoordenador() {
		return this.idUnidadeCoordenador;
	}

	public void setIdUnidadeCoordenador(Integer idUnidadeCoordenador) {
		this.idUnidadeCoordenador = idUnidadeCoordenador;
	}

	/**
	 * @return Returns the discente.
	 */
	 public Discente getDiscente() {
		 return discente;
	 }

	 /**
	  * @param discente
	  *            The discente to set.
	  */
	 public void setDiscente(Discente discente) {
		 this.discente = discente;
	 }

	 /**
	  * @return Returns the pessoa.
	  */
	 public Pessoa getPessoa() {

		 if ((pessoa.getId() > 0) && (discente.getId() == 0)
				 && (servidor.getId() == 0)) {
			 return pessoa;
		 }
		 if ((pessoa.getId() == 0) && (discente.getId() > 0)
				 && (servidor.getId() == 0)) {
			 return discente.getPessoa();
		 }
		 if ((pessoa.getId() == 0) && (discente.getId() == 0)
				 && (servidor.getId() > 0)) {
			 return servidor.getPessoa();
		 }

		 return null;
	 }

	 /**
	  * @param pessoa
	  *            The pessoa to set.
	  */
	 public void setPessoa(Pessoa pessoa) {
		 this.pessoa = pessoa;
	 }

	 /**
	  * @return Returns the servidor.
	  */
	 public Servidor getServidor() {
		 return servidor;
	 }

	 /**
	  * @param servidor
	  *            The servidor to set.
	  */
	 public void setServidor(Servidor servidor) {
		 this.servidor = servidor;
	 }

	 public br.ufrn.sigaa.extensao.dominio.PrestacaoServico getPrestacaoServico() {
		 return this.prestacaoServico;
	 }

	 public void setPrestacaoServico(
			 br.ufrn.sigaa.extensao.dominio.PrestacaoServico prestacaoServico) {
		 this.prestacaoServico = prestacaoServico;
	 }

	 public String toString() {
		 return "br.ufrn.sigaa.extensao.dominio.MembroPrestacaoServico[id=" + id
		 + "]";
	 }

	 @Override
	 public boolean equals(Object obj) {
		 if (obj == null)
			 return false;
		 if (obj instanceof MembroPrestacaoServico) {
			 MembroPrestacaoServico newObj = (MembroPrestacaoServico) obj;

			 return newObj.getPessoa().getId() == this.getPessoa().getId();
		 }

		 return false;
	 }

	 public ListaMensagens validate() {
		 return null;
	 }

}
