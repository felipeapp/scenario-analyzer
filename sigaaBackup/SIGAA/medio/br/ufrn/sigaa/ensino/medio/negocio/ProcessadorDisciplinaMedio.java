/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/06/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;

/**
 * Processador para gerenciar as disciplinas das turmas de série de ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorDisciplinaMedio extends ProcessadorCadastro{

	@Override
	public Object execute(Movimento movimento) throws NegocioException,	ArqException, RemoteException {
		validate(movimento);
		MovimentoCadastro mc = (MovimentoCadastro) movimento;
		
		/* Cadastrar / Alterar / Remover TurmaSerieAno */
		if (mc.getCodMovimento().equals(SigaaListaComando.CADASTAR_DISCIPLINA_MEDIO))
			criar(mc);
		else if (mc.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCIPLINA_MEDIO))
			alterar(mc);
		else if (mc.getCodMovimento().equals(SigaaListaComando.REMOVER_DISCIPLINA_MEDIO))
			remover(mc);

		return mc;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro mc = (MovimentoCadastro) mov;
		TurmaSerieAno tsa = (TurmaSerieAno) mc.getObjMovimentado();
		TurmaDao tDao = getDAO(TurmaDao.class, mov);
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class, mov);
		
		/* Validação de Remoção de disciplina de turmas de ensino médio. */
		if ( mc.getCodMovimento().equals(SigaaListaComando.REMOVER_DISCIPLINA_MEDIO) ) {
			
			if ( tDao.findQtdAlunosPorTurma(tsa.getTurma().getId(), SituacaoMatricula.getSituacoesAtivasArray()) > 0 )
				throw new NegocioException("Não é possível excluir esta disciplina, pois ela possui alunos.");
			
		}
		/* Validação de Remoção de turmas de ensino médio. */
		if ( mc.getCodMovimento().equals(SigaaListaComando.REMOVER_TURMA_SERIE) ) {
			
			if ( tsDao.findQtdeAlunosByTurma(tsa.getTurmaSerie()) > 0 )
				throw new NegocioException("Não é possível excluir esta turma, pois há alunos relacionados a ela.");
			
		}
		
		
		super.validate(mov);
	}
}
