/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 24/10/2008 
 */
package br.ufrn.sigaa.cv.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Membro de uma comunidade virtual.
 * Um membro pode ser: DONO, ADMINISTRADOR_CONTEUDO, CONVIDADO, APENAS_VISUALIZACAO
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(name="membro_comunidade", schema="cv")
public class MembroComunidade implements DominioComunidadeVirtual {

	public static final int ADMINISTRADOR = 1;
	public static final int MODERADOR = 2;
	public static final int MEMBRO = 3;
	public static final int VISITANTE = 4;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	
	@ManyToOne 
	@JoinColumn(name="id_pessoa")
	private Pessoa pessoa = new Pessoa();
	
	@ManyToOne 
	@JoinColumn(name="id_comunidade")
	private ComunidadeVirtual comunidade;
	
	private int permissao;
	
	private boolean ativo = true;
	
	//////////////////////////// informações auditoria //////////////////////////////
	
	/**
	 * Registro de entrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * Registro entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	@Transient
	private Usuario usuario;
	
	public MembroComunidade() {
	}
	
	public MembroComunidade(ComunidadeVirtual cv) {
		this();
		this.comunidade = cv;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pessoa == null) ? 0 : pessoa.hashCode());
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
		MembroComunidade other = (MembroComunidade) obj;
		if (pessoa == null) {
			if (other.pessoa != null)
				return false;
		} else if (!pessoa.equals(other.pessoa))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getMensagemAtividade() {
		return null;
	}

	public int getPermissao() {
		return permissao;
	}

	public void setPermissao(int permissao) {
		this.permissao = permissao;
	}

	public static String getDescricaoPermissaoByID(int id) {
		switch(id) {
				case ADMINISTRADOR:  return "ADMINISTRADOR";
				case MODERADOR:  return "MODERADOR";
				case MEMBRO:  return "MEMBRO";
				case VISITANTE:  return "VISITANTE";
			default: return "PERMISSÃO DESCONHECIDA";
		}
	}
	
	public String getDescricaoPermissao() {
		switch(this.permissao) {
				case ADMINISTRADOR:  return "ADMINISTRADOR";
				case MODERADOR:  return "MODERADOR";
				case MEMBRO:  return "MEMBRO";
				case VISITANTE:  return "VISITANTE";
			default: return "PERMISSÃO DESCONHECIDA";
		}
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * Verifica se o usuário é administrador da comunidade
	 */
	public boolean isAdministrador() {
		return permissao == ADMINISTRADOR;
	}
	
	/**
	 * Verifica se o usuário é moderador da comunidade
	 * @return
	 */
	public boolean isModerador() {
		return permissao == MODERADOR;
	}
	
	public boolean isMembro() {
		return permissao == MEMBRO;
	}

	/**
	 * Verifica se o usuário pode apenas visualizar o conteúdo da comunidade
	 * @return
	 */
	public boolean isVisitante() {
		return permissao == VISITANTE;
	}
	
	/**
	 * Verifica se o usuário tem permissão para alterar o conteúdo da comunidade
	 * 
	 * @return
	 */
	public boolean isPermitidoModerar() {
		return isAdministrador() || isModerador();
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}	
}
