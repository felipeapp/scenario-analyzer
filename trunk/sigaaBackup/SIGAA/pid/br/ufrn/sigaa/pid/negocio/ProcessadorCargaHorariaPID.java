/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 29/10/2009
 *
 */
package br.ufrn.sigaa.pid.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pid.dao.PlanoIndividualDocenteDao;
import br.ufrn.sigaa.pid.dominio.AtividadesEspecificasDocente;
import br.ufrn.sigaa.pid.dominio.CargaHorariaAdministracao;
import br.ufrn.sigaa.pid.dominio.CargaHorariaAtividadesComplementares;
import br.ufrn.sigaa.pid.dominio.CargaHorariaOrientacao;
import br.ufrn.sigaa.pid.dominio.ChEnsinoPIDocenteTurma;
import br.ufrn.sigaa.pid.dominio.ChProjetoPIDMembroProjeto;
import br.ufrn.sigaa.pid.dominio.ChResidenciaMedicaPID;
import br.ufrn.sigaa.pid.dominio.PlanoIndividualDocente;

/**
 * Processador para as operações do Plano Individual do Docente - PID
 * 
 * @author agostinho campos
 *
 */
public class ProcessadorCargaHorariaPID extends ProcessadorCadastro {
 
	@Override
	public Object execute(Movimento movimento) throws NegocioException,
			ArqException, RemoteException {
		
		MovimentoCadastro movCad = (MovimentoCadastro) movimento;
		validate(movCad);
		
		if ( movCad.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PID))
			cadastrar(movCad);
		if ( movCad.getCodMovimento().equals(SigaaListaComando.ALTERAR_PID))
			atualizar(movCad);
		if ( movCad.getCodMovimento().equals(SigaaListaComando.REMOVER_ATIV_ESPECIFICAS_DOCENTE))
			removerOutrasAtividades(movCad);
		if ( movCad.getCodMovimento().equals(SigaaListaComando.REMOVER_TURMAS_DOCENTE_PID))
			removerTurmasDocentePID(movCad);
		if ( movCad.getCodMovimento().equals(SigaaListaComando.REMOVER_DESIGNACOES_DOCENTE_PID))
			removerDesignacoesDocentePID(movCad);
		if ( movCad.getCodMovimento().equals(SigaaListaComando.REMOVER_ORIENTACOES_DOCENTE_PID))
			removerCHOrientacoes(movCad);
		if ( movCad.getCodMovimento().equals(SigaaListaComando.REMOVER_TURMAS_RESIDENCIA_MEDICA))
			removerCHTurmaResidenciaMedica(movCad);
		if ( movCad.getCodMovimento().equals(SigaaListaComando.REMOVER_PROJETOS_PID))
			removerProjetos(movCad);
		
		return movCad;
	}

	/**
	 * Remove os elementos de ChProjetoPIDMembroProjeto
	 * @param movCad
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	private void removerProjetos(MovimentoCadastro movCad) throws DAOException {
		PlanoIndividualDocenteDao dao = getDAO(PlanoIndividualDocenteDao.class, movCad);
		try {
			List<ChProjetoPIDMembroProjeto> listaAtividades = (List<ChProjetoPIDMembroProjeto>) movCad.getColObjMovimentado();
	
			for (ChProjetoPIDMembroProjeto it : listaAtividades)
				dao.remove(it); 
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Remove os elementos de ChResidenciaMedicaPID
	 * @param movCad
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	private void removerCHTurmaResidenciaMedica(MovimentoCadastro movCad) throws DAOException {
		PlanoIndividualDocenteDao dao = getDAO(PlanoIndividualDocenteDao.class, movCad);
		try {
			List<ChResidenciaMedicaPID> listaAtividades = (List<ChResidenciaMedicaPID>) movCad.getColObjMovimentado();
	
			for (ChResidenciaMedicaPID it : listaAtividades)
				dao.remove(it); 
		} finally {
			dao.close();
		}
	}

	/**
	 * Remove os elementos de CargaHorariaOrientacao
	 * @param movCad
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void removerCHOrientacoes(MovimentoCadastro movCad) throws DAOException {
		PlanoIndividualDocenteDao dao = getDAO(PlanoIndividualDocenteDao.class, movCad);
		try {
			List<CargaHorariaOrientacao> listaAtividades = (List<CargaHorariaOrientacao>) movCad.getColObjMovimentado();
	
			for (CargaHorariaOrientacao it : listaAtividades)
				dao.remove(it); 
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Remove os elementos de ChEnsinoPIDocenteTurma (turmas excluídas ou transferidas) 
	 * @param movCad
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void removerDesignacoesDocentePID(MovimentoCadastro movCad) throws DAOException {
		PlanoIndividualDocenteDao dao = getDAO(PlanoIndividualDocenteDao.class, movCad);
		try {
			List<CargaHorariaAdministracao> listaAtividades = (List<CargaHorariaAdministracao>) movCad.getColObjMovimentado();
	
			for (CargaHorariaAdministracao it : listaAtividades)
				dao.remove(it);
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Remove os elementos de ChEnsinoPIDocenteTurma (turmas excluídas ou transferidas) 
	 * @param movCad
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void removerTurmasDocentePID(MovimentoCadastro movCad) throws DAOException {
		PlanoIndividualDocenteDao dao = getDAO(PlanoIndividualDocenteDao.class, movCad);
		try {
			List<ChEnsinoPIDocenteTurma> listaAtividades = (List<ChEnsinoPIDocenteTurma>) movCad.getColObjMovimentado();
	
			for (ChEnsinoPIDocenteTurma it : listaAtividades)
				dao.remove(it);
		} finally {
			dao.close();
		}
	}

	/**
	 * Remove as atividades {@link AtividadesEspecificasDocente} "complementares" que 
	 * o docente adicionou manualmente de acordo com suas necessidades ao PID. 
	 * 
	 * @param movCad
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	private void removerOutrasAtividades(MovimentoCadastro movCad) throws DAOException {
		PlanoIndividualDocenteDao dao = getDAO(PlanoIndividualDocenteDao.class, movCad);
		try {
			List<AtividadesEspecificasDocente> listaAtividades = (List<AtividadesEspecificasDocente>) movCad.getColObjMovimentado();
	
			for (AtividadesEspecificasDocente it : listaAtividades)
				dao.remove(it);
		} finally {
			dao.close();
		}
	}

	/**
	 * Atualiza um novo PID do docente no banco
	 * 
	 * @param movCad
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void atualizar(MovimentoCadastro movCad) throws DAOException {
		PlanoIndividualDocenteDao dao = getDAO(PlanoIndividualDocenteDao.class, movCad);
		ServidorDao sDao = getDAO(ServidorDao.class, movCad);
		PlanoIndividualDocente pid = (PlanoIndividualDocente) movCad.getObjMovimentado();
		List<CargaHorariaAtividadesComplementares> elementosNaoSel = (List<CargaHorariaAtividadesComplementares>) movCad.getColObjMovimentado();
		try {
			for (CargaHorariaAtividadesComplementares ele : elementosNaoSel) {
				dao.remove(ele);
			}
			dao.update(pid);
			// se o status anterior era homologado, e o status atual não é, envia e-mail para o chefe avisando da alteraçaõ.
			if (((Integer) movCad.getObjAuxiliar()) == PlanoIndividualDocente.HOMOLOGADO && (pid.isCadastrado() || pid.isEnviadoHomologacao())) {
				Servidor chefe = sDao.findChefeByDepartamento(pid.getServidor().getUnidade().getId());
				if(chefe == null)
					chefe = sDao.findDiretorByUnidade(pid.getServidor().getUnidade().getId());
				MailBody mail = new MailBody();
				mail.setContentType(MailBody.TEXT_PLAN);
				mail.setAssunto("Plano Individual do Docente Atualizado.");
				mail.setMensagem("Prezado(a) " + chefe.getNome() + ", \n\n" + 
						"O docente "+pid.getServidor().getNome()
						+" alterou o Plano Individual do Docente - PID - de "+
						pid.getAno()+"."+pid.getPeriodo()
						+" que estava homologado.\n\n"
						+"Assim que o docente reenviar o PID, deverá ser revalidado novamente.");
				mail.setEmail(chefe.getPessoa().getEmail());
				mail.setNome(chefe.getNome());
				Mail.send(mail);
			}
		} finally {
			dao.close();
			sDao.close();
		}
	}

	/**
	 * Cadastra um novo PID do docente no banco
	 * 
	 * @param movCad
	 * @throws DAOException
	 */
	private void cadastrar(MovimentoCadastro movCad) throws DAOException {
		PlanoIndividualDocenteDao dao = getDAO(PlanoIndividualDocenteDao.class, movCad);
		try {
			PlanoIndividualDocente pid = (PlanoIndividualDocente) movCad.getObjMovimentado();
			dao.create(pid);
		} finally {
			dao.close();
		}
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}
}
