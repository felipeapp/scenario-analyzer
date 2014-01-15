/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ProcessadorMovimentacaoAluno;

/**
 * Colação de grau é o ato formal necessário que concede o grau ao aluno que
 * integralizou o currículo do seu curso. O aluno só recebe o diploma após a
 * colação de grau. Pode ocorrer de forma coletiva (todo um curso, ou um centro,
 * ou alguns cursos associados) ou individual. <br>
 * Esta classe processa um conjunto de movimentação de aluno do tipo conclusão,
 * de vários discentes de um curso que colaram grau de forma coletiva.
 * 
 * @author Andre Dantas
 * 
 */
public class ProcessadorColacaoGrauColetiva  extends ProcessadorMovimentacaoAluno {

	/** Persiste as movimentações referentes à conclusão de curso.
	 * @see br.ufrn.sigaa.ensino.negocio.ProcessadorMovimentacaoAluno#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		validate(movimento);
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		MovimentoColacaoGrauColetiva mov = (MovimentoColacaoGrauColetiva) movimento;
		TipoMovimentacaoAluno tipoConclusao = getGenericDAO(mov).findByPrimaryKey(TipoMovimentacaoAluno.CONCLUSAO, TipoMovimentacaoAluno.class);
		for(DiscenteGraduacao dg : mov.getDiscentes()) {
			MovimentacaoAluno conclusao = new MovimentacaoAluno();
			conclusao.setDiscente(dg.getDiscente());
			conclusao.setAnoReferencia(mov.getAno());
			conclusao.setPeriodoReferencia(mov.getPeriodo());
			conclusao.setAnoOcorrencia(cal.getAno());
			conclusao.setPeriodoOcorrencia(cal.getPeriodo());
			conclusao.setTipoMovimentacaoAluno(tipoConclusao);
			conclusao.setDataColacaoGrau(mov.getDataColacao());
			MovimentoCadastro movCad = new MovimentoCadastro();
			movCad.setSistema(mov.getSistema());
			movCad.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
			movCad.setUsuarioLogado(mov.getUsuarioLogado());
			movCad.setObjMovimentado(conclusao);
			movCad.setObjAuxiliar(new Boolean(false));
			super.execute(movCad);
		}
		return null;
	}

	/** Valida as movimentações referentes à conclusão de curso.
	 * @see br.ufrn.sigaa.ensino.negocio.ProcessadorMovimentacaoAluno#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento movimento) throws NegocioException,	ArqException {
		if (movimento instanceof MovimentoColacaoGrauColetiva) {
			/* Essa validação passou para a impressão do diploma.
			 * 
			MovimentoColacaoGrauColetiva mov = (MovimentoColacaoGrauColetiva) movimento;
			EmprestimoDao emprestimoDao = getDAO(EmprestimoDao.class, mov);
			StringBuffer listaErro = new StringBuffer();
			for(DiscenteGraduacao dg : mov.getDiscentes()) {
				Collection<Emprestimo> pendencias = emprestimoDao.findPendenciasByDiscente(dg.getId());
				if (!ValidatorUtil.isEmpty(pendencias)) {
					List<String> bibliotecas = new ArrayList<String>();
					StringBuffer listaBiblioteca = new StringBuffer();
					for (Emprestimo emprestimo : pendencias) {
						if (!bibliotecas.contains(emprestimo.getMaterial().getBiblioteca().getDescricao())) {
							bibliotecas.add(emprestimo.getMaterial().getBiblioteca().getDescricao());
							listaBiblioteca.append(
									(listaBiblioteca.length() > 0 ? ", " : "") + 
									emprestimo.getMaterial().getBiblioteca().getDescricao());
						}
					}
					listaErro.append(listaBiblioteca + "\n");
				}
			}
			if (listaErro.length() > 0) throw new NegocioException(listaErro.toString());
			*/
		} else super.validate(movimento);
	}

}
