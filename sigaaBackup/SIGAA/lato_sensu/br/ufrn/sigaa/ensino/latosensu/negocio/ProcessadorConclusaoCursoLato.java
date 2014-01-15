/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/04/2010
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ProcessadorMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador que efetua a conclusão de alunos ativos de um curso de pós-graduação lato sensu.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorConclusaoCursoLato extends ProcessadorMovimentacaoAluno {

	
	/** Persiste as movimentações referentes à conclusão de curso.
	 * @see br.ufrn.sigaa.ensino.negocio.ProcessadorMovimentacaoAluno#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		MovimentoConclusaoCursoLato movimentoConclusaoCursoLato = (MovimentoConclusaoCursoLato) mov;
		CursoLato cursoLato = movimentoConclusaoCursoLato.getCursoLato();
		Collection<Discente> discentes = movimentoConclusaoCursoLato.getDiscentes();
		TipoMovimentacaoAluno tipoConclusao = getGenericDAO(mov).findByPrimaryKey(TipoMovimentacaoAluno.CONCLUSAO, TipoMovimentacaoAluno.class);
		for (Discente d : discentes) {
			MovimentacaoAluno conclusao = new MovimentacaoAluno();
			conclusao.setDiscente(d);
			conclusao.setAnoReferencia(movimentoConclusaoCursoLato.getAno());
			conclusao.setPeriodoReferencia(movimentoConclusaoCursoLato.getPeriodo());
			conclusao.setAnoOcorrencia(cal.getAno());
			conclusao.setPeriodoOcorrencia(cal.getPeriodo());
			conclusao.setTipoMovimentacaoAluno(tipoConclusao);
			conclusao.setDataColacaoGrau(cursoLato.getDataFim());
			MovimentoCadastro movCad = new MovimentoCadastro();
			movCad.setSistema(movimentoConclusaoCursoLato.getSistema());
			movCad.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
			movCad.setUsuarioLogado(movimentoConclusaoCursoLato.getUsuarioLogado());
			movCad.setObjMovimentado(conclusao);
			super.execute(movCad);
		}
		
		return null;
	}

	/** Valida as movimentações referentes à conclusão de curso.
	 * @see br.ufrn.sigaa.ensino.negocio.ProcessadorMovimentacaoAluno#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		if (movimento instanceof MovimentoConclusaoCursoLato) {
			
			MovimentoConclusaoCursoLato mov = (MovimentoConclusaoCursoLato) movimento;
			
			ListaMensagens erros = new ListaMensagens();
			
			for(Discente d : mov.getDiscentes()) {
				erros.addAll(VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(d));
			}
			
			if (erros.size() > 0) throw new NegocioException(erros);
			
		} else super.validate(movimento);
	}

}
