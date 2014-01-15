/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * 07/12/2007
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.graduacao.MudancaCurricularColetivaDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MudancaCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoMudancaCurricular;

/**
 * Processador para efetuar a mudan�a de curr�culo de todos os alunos ativos de
 * um curso. Opcionalmente pode-se filtrar apenas os alunos de um determinado
 * ano-per�odo de ingresso.
 * 
 * @author leonardo
 * 
 */
public class ProcessadorMudancaCurricularColetiva extends AbstractProcessador {

	/** Executa a mudan�a curricular coletiva.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		MudancaCurricularColetivaDao dao = getDAO(MudancaCurricularColetivaDao.class, mov);
		MovimentoMudancaCurricularColetiva movm = (MovimentoMudancaCurricularColetiva) mov;
		Curriculo currOrigem = movm.getCurriculoOrigem();
		Curriculo currDestino = movm.getCurriculoDestino();

		Collection<DiscenteGraduacao> discentes = dao.findByCurriculoAnoPeriodo(currOrigem.getId(), movm.getAnoIngresso(), movm.getPeriodoIngresso());

		if (discentes == null || discentes.isEmpty())
			throw new NegocioException("N�o h� alunos associados ao curr�culo de origem.");

		for (DiscenteGraduacao d : discentes) {
			// registra a mudan�a de curr�culo
			MudancaCurricular mudanca = new MudancaCurricular();
			mudanca.setTipoMudanca(TipoMudancaCurricular.MUDANCA_CURRICULO);
			mudanca.setCurriculoAntigo(currOrigem);
			mudanca.setCurriculoNovo(currDestino);
			mudanca.setDiscente(d);
			mudanca.setData(new Date());
			mudanca.setEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.create(mudanca);
		}

		JdbcTemplate template = dao.getJdbcTemplate();
		
		// muda os alunos para o novo curr�culo
		template.update("update discente set id_curriculo = "+ currDestino.getId() +" where id_discente in "+ UFRNUtils.gerarStringIn(discentes));
		
		// zera os tipos de integraliza��es das matriculas
		template.update("update ensino.matricula_componente set tipo_integralizacao = null where id_discente in "+ UFRNUtils.gerarStringIn(discentes));

		movm.setQtdAlunos(discentes.size());
		
		return movm;
	}

	/** Valida os dados da mudan�a curricular coletiva
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoMudancaCurricularColetiva movm = (MovimentoMudancaCurricularColetiva) mov;
		ListaMensagens erros = new ListaMensagens();
		if (movm.getCurriculoOrigem().equals(movm.getCurriculoDestino()))
			erros.addErro("O curr�culo de origem n�o pode ser o mesmo que o de destino.");

		checkValidation(erros.getMensagens());
	}

}
