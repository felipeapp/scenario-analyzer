/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/06/20
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Representa um grupo de alunos que estão fazendo
 * uma tarefa juntos na turma virtual. 
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(name="grupo_tarefa_turma", schema="ava")
public class GrupoTarefaTurma implements DominioTurmaVirtual {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	private String descricao;
	
	@ManyToOne
	@JoinColumn(name="id_tarefa")
	private TarefaTurma tarefa;
	
	@ManyToMany
	@JoinTable(name="ava.membro_grupo", joinColumns={@JoinColumn(name="id_grupo")}, inverseJoinColumns={@JoinColumn(name="id_discente")})
	private List<Discente> discentes;
	
	@Column(name="data_cadastro")
	private Date dataCadastro;	
	
	@ManyToOne
	@JoinColumn(name="id_usuario_cadastro")
	private Usuario usuarioCadastro;

	/**
	 * @return the dataCadastro
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**
	 * @param dataCadastro the dataCadastro to set
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * @return the discentes
	 */
	public List<Discente> getDiscentes() {
		return discentes;
	}

	/**
	 * @param discentes the discentes to set
	 */
	public void setDiscentes(List<Discente> discentes) {
		this.discentes = discentes;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the tarefa
	 */
	public TarefaTurma getTarefa() {
		return tarefa;
	}

	/**
	 * @param tarefa the tarefa to set
	 */
	public void setTarefa(TarefaTurma tarefa) {
		this.tarefa = tarefa;
	}

	/**
	 * @return the usuarioCadastro
	 */
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	/**
	 * @param usuarioCadastro the usuarioCadastro to set
	 */
	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (descricao == null || "".equals(descricao.trim()))
			lista.addErro("É necessário escrever uma descrição para o grupo");
		if (discentes == null || discentes.isEmpty())
			lista.addErro("Selecione os discentes");
		return lista;
	}

	/**
	 * Retorna a turma do grupo.
	 * @Deprecated Não usar. Foi criado por imposição da interface mas não está implementado.
	 */
	@Deprecated
	public Turma getTurma() {
		return null;
	}

	/**
	 * Seta a turma do grupo
	 * @Deprecated Não usar. Foi criado por imposição da interface mas não está implementado.
	 */
	@Deprecated
	public void setTurma(Turma turma) {
		
	}

	/**
	 * Retorna a MensagemAtividade
	 * @Deprecated Não usar. Foi criado por imposição da interface mas não está implementado.
	 */
	@Deprecated
	public String getMensagemAtividade() {
		return null;
	}
}
