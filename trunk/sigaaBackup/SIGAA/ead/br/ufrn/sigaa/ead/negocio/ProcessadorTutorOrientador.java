/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2012/09/09
 */
package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Processador para cadastro de Tutor 
 * 
 * @author Diego Jácome
 *
 */
public class ProcessadorTutorOrientador extends AbstractProcessador {

	/**
	 * Método responsável pela execução do processamento do cadastro do Tutor
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	
		if (mov.getCodMovimento().getId() == SigaaListaComando.CADASTRAR_TUTOR_DISTANCIA.getId()) 
			cadastrarTutor(mov);

		return null;
	}

	@SuppressWarnings("unchecked")
	private void cadastrarTutor(Movimento mov) throws DAOException {
		MovimentoTutorOrientador tMov = (MovimentoTutorOrientador) mov;
		Usuario user = (Usuario) tMov.getUsuarioLogado();
		TutorOrientador tutor = tMov.getObjMovimentado();
		TurmaVirtualDao tDao = null;
		
		ArrayList<Turma> turmas = null;
		if (tMov.getColObjMovimentado() != null)
			turmas = (ArrayList<Turma>) tMov.getColObjMovimentado();
		
		try {
			tDao = getDAO(TurmaVirtualDao.class, mov);
			tDao.create(tutor);
			ArrayList<Turma> turmasComPermissao = (ArrayList<Turma>) tDao.findTurmasPermitidasByPessoa(tutor.getPessoa(), PermissaoAva.DOCENTE);
			
			if (turmas!=null)
				for (Turma t : turmas){		
					if (!turmasComPermissao.contains(t)){
						PermissaoAva p = new PermissaoAva();
						p.setPessoa(tutor.getPessoa());
						p.setTurma(t);
						p.setCorrigirTarefa(false);
						p.setEnquete(false);
						p.setForum(false);
						p.setTarefa(false);
						p.setInserirArquivo(false);
						p.setDocente(true);
						p.setDataCadastro(new Date());
						p.setUsuarioCadastro(user);
						tDao.create(p);
					}
				}
			
			ArrayList<PermissaoAva> permissoesARemover = null;
			if (tMov.getTurmasARemover() != null && !tMov.getTurmasARemover().isEmpty() ){
				ArrayList<Integer> idsTurma = new ArrayList<Integer>();
				for (Turma t : tMov.getTurmasARemover())
					idsTurma.add(t.getId());
				permissoesARemover = tDao.findPermissaoByPessoaTurmas(tutor.getPessoa(), idsTurma );
			}
			if (permissoesARemover!=null)
				for (PermissaoAva p : permissoesARemover)		
					tDao.remove(p);
				
			
		} finally {
			if (tDao!=null)
				tDao.close();
		}
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}
	
}
