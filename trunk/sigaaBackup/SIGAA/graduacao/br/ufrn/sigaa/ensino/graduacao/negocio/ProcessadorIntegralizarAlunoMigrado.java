/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/12/2007
 * 
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;

/**
 * Processador responsável pela validação e execução da integralização dos alunos migrados do antigo sistema acadêmico.
 * @author leonardo
 *
 */
public class ProcessadorIntegralizarAlunoMigrado extends AbstractProcessador {

	
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		DiscenteGraduacao discente = (DiscenteGraduacao) movc.getObjMovimentado();
		
		GenericDAO dao = getGenericDAO(mov); 
		
		try {
			discente = dao.findByPrimaryKey(discente.getId(), DiscenteGraduacao.class);
			
			// zerando os campos de créditos/carga horárias pendentes
			discente.setCrAulaPendente(new Short("0"));
			discente.setChAulaPendente(new Short("0"));
			
			discente.setCrLabPendente(new Short("0"));
			discente.setChLabPendente(new Short("0"));
			
			discente.setCrEstagioPendente(new Short("0"));
			discente.setChEstagioPendente(new Short("0"));
			
			discente.setCrNaoAtividadeObrigPendente(new Short("0"));
			discente.setChNaoAtividadeObrigPendente(new Short("0"));
			
			discente.setCrTotalPendentes(new Short("0"));
			discente.setChTotalPendente(new Short("0"));
			
			discente.setChAtividadeObrigPendente(new Short("0"));
			discente.setChOptativaPendente(new Short("0"));
			
			dao.update(discente);
		} finally {
			if ( dao != null ) dao.close();
		}
		return discente;
	}

	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		DiscenteAdapter discente = movc.getObjMovimentado();
		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		
		try {
			ListaMensagens erros = new ListaMensagens();
			if(!dao.isAlunoConcluidoCreditoPendente(discente)){
				erros.addErro("O aluno selecionado não possui créditos pendentes.");
			}
			List<MatriculaComponente> disciplinas = dao.findDisciplinasConcluidasMatriculadas(discente.getId(), true);
			Collection<ComponenteCurricular> lista = dao.findByDisciplinasCurricularesPendentes(discente.getId(), disciplinas, new ArrayList<TipoGenerico>());
			if(lista != null && !lista.isEmpty()){
				erros.addErro("O aluno selecionado ainda possui componentes obrigatórios pendentes.");
			}
			checkValidation(erros);
		} finally {
			if ( dao != null ) dao.close();
		}
	}

}
