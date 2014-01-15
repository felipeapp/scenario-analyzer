/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;

/**
 * Processador para cadastrar a avaliação de um professor para uma tarefa.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorAvaliacaoTarefa extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		AvaliacaoTarefaMov aMov = (AvaliacaoTarefaMov) mov;
		List<RespostaTarefaTurma> respostas = aMov.getRespostas();
		GenericDAO dao = getGenericDAO(mov);
		
		try {
			//TarefaTurma tarefa = null;
			for (RespostaTarefaTurma resposta : respostas) {
				RespostaTarefaTurma respBD = dao.findByPrimaryKey(resposta.getId(), RespostaTarefaTurma.class);
				respBD.setComentarios(resposta.getComentarios());
				respBD.setNotaRecebida(resposta.getNotaRecebida());
				
				dao.update(respBD);
				
				//tarefa = respBD.getTarefa();
			}

//			RegistroAtividadeTurma reg = new RegistroAtividadeTurma();
//			reg.setData(new Date());
//			reg.setTurma(tarefa.getTurma());
//			reg.setUsuario((Usuario) mov.getUsuarioLogado());
//			reg.setDescricao("Cadastrada a avaliação da tarefa" + tarefa.getTitulo());
//			
//			dao.create(reg);
			
		} finally {
			dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		
	}

}
