/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/06/21 - 20:25:31
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ava.dominio.GrupoTarefaTurma;

/**
 * Glasse que gerencia os grupos de alunos que estão realizando uma tarefa juntos.
 * 
 * @author David Pereira
 *
 */
@Component("grupoTarefaTurma") @Scope("request")
public class GrupoTarefaTurmaMBean extends CadastroTurmaVirtual<GrupoTarefaTurma> {

	private String[] discentes;

	public GrupoTarefaTurmaMBean() {
		/*this.obj = new GrupoTarefaTurma();
		obj.setTarefa(new TarefaTurma());
		obj.setDiscentes(new ArrayList<Discente>());*/
	}

	public String cadastroGrupos() throws DAOException {
		setarTarefa();
		/*if (!obj.getTarefa().isEmGrupo()) {
			addMensagemErro("A tarefa selecionada não é em grupo!");
			return forward("/portais/turma/TarefaTurma/form.jsp");
		}*/

		return forward("/portais/turma/GrupoTarefaTurma/lista.jsp");
	}

	private void setarTarefa() throws DAOException {
		//int id = getParameterInt("id");

		//GenericDAO dao = getGenericDAO();
		//obj.setTarefa(dao.findByPrimaryKey(id, TarefaTurma.class));
	}

	public String novoGrupo() throws DAOException {
		setarTarefa();
		return forward("/portais/turma/GrupoTarefaTurma/form.jsp");
	}

	/**
	 * @return the discentes
	 */
	public String[] getDiscentes() {
		return discentes;
	}

	/**
	 * @param discentes the discentes to set
	 */
	public void setDiscentes(String[] discentes) {
		this.discentes = discentes;
	}

	@Override
	public List<GrupoTarefaTurma> lista() {
		return null;
	}

}

