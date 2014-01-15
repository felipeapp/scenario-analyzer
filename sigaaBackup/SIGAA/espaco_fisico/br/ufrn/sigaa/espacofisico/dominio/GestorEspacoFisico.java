/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Entidade que representa a permissão que um usuário possui para realizar a
 * gestão ou controle de reservas para a utilização dos espaços físicos da
 * instituição
 * 
 * @author Henrique André
 * @author Ricardo Wendell
 * 
 */

@Entity
@Table(name = "gestor_espaco_fisico", schema = "espaco_fisico")
public class GestorEspacoFisico implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_gestor_espaco_fisico")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "id_tipo_gestor_espaco_fisico")
	private TipoGestorEspacoFisico tipo;

	/**
	 * O gestor de espaços físicos pode ser responsável por todos os espaços
	 * vinculados a uma unidade ou a um espaço físico específico.
	 */
	@ManyToOne
	@JoinColumn(name = "id_espaco_fisico")
	private EspacoFisico espacoFisico;
	
	@ManyToOne
	@JoinColumn(name = "id_unidade")
	private Unidade unidade;

	private boolean ativo = true;
	
	@CriadoEm
	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;
	
	@CriadoPor
	@ManyToOne
	@JoinColumn(name = "id_registro_cadastro")
	private RegistroEntrada registroCadastro;
	
	@AtualizadoEm
	@Column(name = "data_atualizacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAtualizacao;
	
	@AtualizadoPor
	@ManyToOne
	@JoinColumn(name = "id_registro_atualizacao")
	private RegistroEntrada registroAtualizacao;

	public GestorEspacoFisico() {
	}

	public GestorEspacoFisico(int id) {
		this();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public TipoGestorEspacoFisico getTipo() {
		return tipo;
	}

	public void setTipo(TipoGestorEspacoFisico tipo) {
		this.tipo = tipo;
	}

	public EspacoFisico getEspacoFisico() {
		return espacoFisico;
	}

	public void setEspacoFisico(EspacoFisico espacoFisico) {
		this.espacoFisico = espacoFisico;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public ListaMensagens validate() {
		return null;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(usuario, unidade);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "usuario", "unidade");
	}

	@Transient
	public boolean isGestorUnidade() {
		return unidade != null;
	}
	
	@Transient
	public boolean isGestorEspacoFisico() {
		return espacoFisico != null;
	}
	
	@Override
	public String toString() {

		String str = usuario.toString() + " - " + unidade;

		return str;

	}
}
