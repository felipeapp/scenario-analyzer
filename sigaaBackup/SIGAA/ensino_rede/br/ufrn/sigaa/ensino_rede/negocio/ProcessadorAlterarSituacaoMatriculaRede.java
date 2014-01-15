/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino_rede.dao.TurmaRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.MatriculaComponenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.TurmaRede;

/**
 * Processador responsável pela alteração dos status de matrículas de discentes de ensino em rede
 *
 * @author Diego Jácome
 */
public class ProcessadorAlterarSituacaoMatriculaRede  extends AbstractProcessador {

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {

		MovimentoCadastro cmov = (MovimentoCadastro) mov;
		TurmaRedeDao dao = null;
		
		try {
						
			dao = getDAO(TurmaRedeDao.class, cmov);
			ArrayList<MatriculaComponenteRede> matriculas = (ArrayList<MatriculaComponenteRede>) cmov.getColObjMovimentado();
			TurmaRede turma = cmov.getObjMovimentado();
			Boolean consolidacao = (Boolean) cmov.getObjAuxiliar();
			
			validate(cmov);
			
			if (matriculas != null){
				for (MatriculaComponenteRede m : matriculas)
					dao.updateField(MatriculaComponenteRede.class, m.getId(), "situacao.id", m.getNovaSituacaoMatricula().getId());
			}
			
			if (consolidacao)
				dao.updateField(TurmaRede.class, turma.getId(), "situacaoTurma.id", SituacaoTurma.CONSOLIDADA);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		MovimentoCadastro cmov = (MovimentoCadastro) mov;
		TurmaRede turma = cmov.getObjMovimentado();

		checkRole(new int[] { SigaaPapeis.COORDENADOR_GERAL_REDE , SigaaPapeis.COORDENADOR_UNIDADE_REDE },cmov); 
		
		if (!turma.isAberta())
			throw new NegocioException("Está turma não está aberta.");
		
	}

}
