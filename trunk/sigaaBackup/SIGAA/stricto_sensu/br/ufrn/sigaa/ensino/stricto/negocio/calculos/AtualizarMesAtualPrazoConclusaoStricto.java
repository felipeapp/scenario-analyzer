/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/05/2010
 */
package br.ufrn.sigaa.ensino.stricto.negocio.calculos;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.HomologacaoTrabalhoFinalDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;
import br.ufrn.sigaa.ensino.stricto.dominio.TipoCursoStricto;

/**
 * Calcula e atualiza o m�s atual e o prazo de conclus�o do discente.
 * 
 * @author David Pereira
 *
 */
public class AtualizarMesAtualPrazoConclusaoStricto extends CalculosDiscenteChainNode<DiscenteStricto> {

	@Override
	public void processar(DiscenteStricto d, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {
		DiscenteDao dao = getDAO(DiscenteDao.class, mov.getUsuarioLogado());
		HomologacaoTrabalhoFinalDao homologacaoDao = getDAO(HomologacaoTrabalhoFinalDao.class, mov.getUsuarioLogado());
		MovimentacaoAlunoDao movDao = getDAO(MovimentacaoAlunoDao.class, mov.getUsuarioLogado());

		try {

			// Contabiliza os trancamentos
			Collection<MovimentacaoAluno> trancamentos = dao.findTrancamentosByDiscente(d);
			int countTrancamentos = 0;
			for (MovimentacaoAluno t : trancamentos) {
				countTrancamentos += (t.getValorMovimentacao() == null ? 0 : t.getValorMovimentacao());
			}
			
			// Contabiliza as prorroga��es
			int prorrogacoes = movDao.countProrrogacoesByDiscente(d);

			int ano = CalendarUtils.getAnoAtual();
			int mes = CalendarUtils.getMesAtual() + 1;

			// Caso seja um discente inativo, calcula o m�s atual em fun��o da data de conclus�o.
			if (!d.isAtivo()) {
				// se o discente tiver conclu�do, busca a data de homologa��o
				if (d.isConcluido()) {
					HomologacaoTrabalhoFinal homologacao = homologacaoDao.findUltimoByDiscente(d.getId());
					if (homologacao != null) {
						Date dataHomologacao = homologacao.getCriadoEm();
						ano = CalendarUtils.getAno(dataHomologacao);
						mes = CalendarUtils.getMesByData(dataHomologacao);
					} 
				}
				// movimenta��o de sa�da
				d.setMovimentacaoSaida(movDao.findUltimoAfastamentoByDiscente(d.getId(), true, false));
				if (d.getMovimentacaoSaida() != null) {
					ano = d.getMovimentacaoSaida().getAnoReferencia();
					Calendar c = Calendar.getInstance();
					c.setTime(d.getMovimentacaoSaida().getDataOcorrencia());
					mes = c.get(Calendar.MONTH) + 1;
				}
			}

			// data de in�cio do discente
			Integer anoInicio = d.getDiscente().getAnoEntrada();
			Integer mesInicio = d.getMesEntrada();
			if (mesInicio == null) {
				if (d.getPeriodoIngresso() == 1)
					mesInicio = 1;
				else
					mesInicio = 7;
			}

			// c�lculo do m�s atual do discente
			// segundo a RESOLU��O No 072/2004-CONSEPE, Art. 35, � 3�:
			// Durante o per�odo sob trancamento, estar� suspensa a contagem do prazo m�ximo de dura��o do curso.
			int mesAtual = ((ano - anoInicio) * 12 + (mes - mesInicio) - countTrancamentos) + 1;
			if (mesAtual < 0 ) mesAtual = 0;
			d.setMesAtual(mesAtual);

			// Prazo m�ximo de conclus�o definido no curr�culo do discente.
			int maximo = 0;
			if (d.getCurriculo() != null) {
				if (d.getCurriculo().getMesesConclusaoIdeal() != null) {
					maximo += d.getCurriculo().getMesesConclusaoIdeal();
				} else {
					// Caso n�o esteja definido, atribui os valores padr�o definidos na RESOLU��O No 072/2004-CONSEPE, Art. 28:
					// tr�s anos para mestrado e cinco para doutorado.
					TipoCursoStricto tipoCurso = d.getCurriculo().getCurso().getTipoCursoStricto();
					if (tipoCurso.equals(TipoCursoStricto.DOUTORADO))
						maximo += 12 * ParametroHelper.getInstance().getParametroInt(ConstantesParametro.PRAZO_MAXIMO_CONCLUSAO_DOUTORADO );
					else if (tipoCurso.equals(TipoCursoStricto.MESTRADO_ACADEMICO))
						maximo += 12 * ParametroHelper.getInstance().getParametroInt(ConstantesParametro.PRAZO_MAXIMO_CONCLUSAO_MESTRADO_ACADEMICO );
					else if (tipoCurso.equals(TipoCursoStricto.MESTRADO_PROFISSIONAL))
						maximo += 12 * ParametroHelper.getInstance().getParametroInt(ConstantesParametro.PRAZO_MAXIMO_CONCLUSAO_MESTRADO_PROFISSIONAL);
				}
			}

			// O prazo m�ximo n�o contabiliza os trancamentos e desconta as prorroga��es.
			maximo += prorrogacoes + countTrancamentos;

			d.setVariacaoPrazo(prorrogacoes + countTrancamentos);
			
			// Calcula o m�s/ano do prazo m�ximo para conclus�o.
			// O mes final � sempre 1 mes antes do mes de Inicio. Se o discente comeca mar�o, termina em fevereiro.
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, anoInicio);
			c.set(Calendar.MONTH, mesInicio - 2);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.MONTH, maximo);
			
			dao.setUsuario(mov.getUsuarioLogado());
			d.setPrazoMaximoConclusao(c.getTime());
			dao.updateField(DiscenteStricto.class, d.getId(), "prazoMaximoConclusao", c.getTime());
			dao.updateField(DiscenteStricto.class, d.getId(), "mesAtual", d.getMesAtual());
			dao.updateField(DiscenteStricto.class, d.getId(), "variacaoPrazo", d.getVariacaoPrazo());
			
		} finally {
			dao.close();
			homologacaoDao.close();
			movDao.close();
		}
	}
	
}
