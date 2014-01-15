/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/02/2013
 */
package br.ufrn.sigaa.ensino.stricto.negocio.calculos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
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
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Calcula e atualiza o m�s atual e o prazo de conclus�o dos discentes.
 * 
 * @author Diego J�come
 *
 */
public class AtualizarMesAtualPrazoConclusaoStrictoEmLote extends CalculosDiscenteChainNode<DiscenteStricto> {

	@SuppressWarnings("unchecked")
	@Override
	public void processar(DiscenteStricto d, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {
		DiscenteDao dao = getDAO(DiscenteDao.class, mov.getUsuarioLogado());
		HomologacaoTrabalhoFinalDao homologacaoDao = getDAO(HomologacaoTrabalhoFinalDao.class, mov.getUsuarioLogado());
		MovimentacaoAlunoDao movDao = getDAO(MovimentacaoAlunoDao.class, mov.getUsuarioLogado());

		try {

			Collection<DiscenteStricto> discentes = (Collection<DiscenteStricto>) ((MovimentoCadastro) mov).getColObjMovimentado();
			Collection<Discente> discentesBuscados = new ArrayList<Discente>();
			
			for (DiscenteStricto ds : discentes){
				ds.getDiscente().setId(ds.getId());
				discentesBuscados.add(ds.getDiscente());
			}
			
			HashMap<Discente,Collection<MovimentacaoAluno>> trancTotais = movDao.findTrancamentosByDiscentes(discentesBuscados);
			HashMap<Discente,Collection<MovimentacaoAluno>> progTotais = movDao.findProrrogacoesByDiscentes(discentesBuscados);
			HashMap<Discente,Collection<MovimentacaoAluno>> afastTotais = movDao.findAfastamentosByDiscentes(discentesBuscados);
			HashMap<Discente,Collection<HomologacaoTrabalhoFinal>> trabTotais = homologacaoDao.findByDiscentes(discentesBuscados);
			
			for (DiscenteStricto dAtual : discentes){

				// Contabiliza os trancamentos
				Collection<MovimentacaoAluno> trancamentos = trancTotais.get(dAtual);
				int countTrancamentos = 0;
				if (trancamentos!=null){
					for (MovimentacaoAluno t : trancamentos) {
						countTrancamentos += (t.getValorMovimentacao() == null ? 0 : t.getValorMovimentacao());
					}
				}
				// Contabiliza as prorroga��es
				Collection<MovimentacaoAluno> prorrogacoes = progTotais.get(dAtual);
				int countProrrogacoes = 0;
				if (prorrogacoes!=null){
					for (MovimentacaoAluno p : prorrogacoes) {
						countProrrogacoes += (p.getValorMovimentacao() == null ? 0 : p.getValorMovimentacao());
					}
				}
	
				int ano = CalendarUtils.getAnoAtual();
				int mes = CalendarUtils.getMesAtual() + 1;
	
				// Caso seja um discente inativo, calcula o m�s atual em fun��o da data de conclus�o.
				if (!dAtual.isAtivo()) {
					// se o discente tiver conclu�do, busca a data de homologa��o
					if (dAtual.isConcluido()) {
						Collection<HomologacaoTrabalhoFinal> trabalhos = trabTotais.get(dAtual);
						HomologacaoTrabalhoFinal homologacao = null;
						if ( trabalhos!=null)
							homologacao = trabalhos.iterator().next();
						
						if (homologacao != null) {
							Date dataHomologacao = homologacao.getCriadoEm();
							ano = CalendarUtils.getAno(dataHomologacao);
							mes = CalendarUtils.getMesByData(dataHomologacao);
						} 
					}
					
					// movimenta��o de sa�da
					Collection<MovimentacaoAluno> afastamentos = afastTotais.get(dAtual);
					MovimentacaoAluno saida = null;
					if ( afastamentos!=null)
						saida = afastamentos.iterator().next();
					
					dAtual.setMovimentacaoSaida(saida);
					if (dAtual.getMovimentacaoSaida() != null) {
						ano = dAtual.getMovimentacaoSaida().getAnoReferencia();
						Calendar c = Calendar.getInstance();
						c.setTime(dAtual.getMovimentacaoSaida().getDataOcorrencia());
						mes = c.get(Calendar.MONTH) + 1;
					}
				}
	
				// data de in�cio do discente
				Integer anoInicio = dAtual.getDiscente().getAnoEntrada();
				Integer mesInicio = dAtual.getMesEntrada();
				if (mesInicio == null) {
					if (dAtual.getPeriodoIngresso() == 1)
						mesInicio = 1;
					else
						mesInicio = 7;
				}
	
				// c�lculo do m�s atual do discente
				// segundo a RESOLU��O No 072/2004-CONSEPE, Art. 35, � 3�:
				// Durante o per�odo sob trancamento, estar� suspensa a contagem do prazo m�ximo de dura��o do curso.
				int mesAtual = ((ano - anoInicio) * 12 + (mes - mesInicio) - countTrancamentos) + 1;
				if (mesAtual < 0 ) mesAtual = 0;
				dAtual.setMesAtual(mesAtual);
	
				// Prazo m�ximo de conclus�o definido no curr�culo do discente.
				int maximo = 0;
				if (dAtual.getCurriculo() != null) {
					if (dAtual.getCurriculo().getMesesConclusaoIdeal() != null) {
						maximo += dAtual.getCurriculo().getMesesConclusaoIdeal();
					} else {
						// Caso n�o esteja definido, atribui os valores padr�o definidos na RESOLU��O No 072/2004-CONSEPE, Art. 28:
						// tr�s anos para mestrado e cinco para doutorado.
						TipoCursoStricto tipoCurso = dAtual.getCurriculo().getCurso().getTipoCursoStricto();
						if (tipoCurso.equals(TipoCursoStricto.DOUTORADO))
							maximo += 12 * ParametroHelper.getInstance().getParametroInt(ConstantesParametro.PRAZO_MAXIMO_CONCLUSAO_DOUTORADO );
						else if (tipoCurso.equals(TipoCursoStricto.MESTRADO_ACADEMICO))
							maximo += 12 * ParametroHelper.getInstance().getParametroInt(ConstantesParametro.PRAZO_MAXIMO_CONCLUSAO_MESTRADO_ACADEMICO );
						else if (tipoCurso.equals(TipoCursoStricto.MESTRADO_PROFISSIONAL))
							maximo += 12 * ParametroHelper.getInstance().getParametroInt(ConstantesParametro.PRAZO_MAXIMO_CONCLUSAO_MESTRADO_PROFISSIONAL);
					}
				}
	
				// O prazo m�ximo n�o contabiliza os trancamentos e desconta as prorroga��es.
				maximo += countProrrogacoes + countTrancamentos;
	
				dAtual.setVariacaoPrazo(countProrrogacoes + countTrancamentos);
				
				// Calcula o m�s/ano do prazo m�ximo para conclus�o.
				// O mes final � sempre 1 mes antes do mes de Inicio. Se o discente comeca mar�o, termina em fevereiro.
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, anoInicio);
				c.set(Calendar.MONTH, mesInicio - 2);            
				c.set(Calendar.DATE, 1);
				c.add(Calendar.MONTH, maximo);
				
				dao.setUsuario(mov.getUsuarioLogado());
				dAtual.setPrazoMaximoConclusao(c.getTime());
				dao.updateField(DiscenteStricto.class, dAtual.getId(), "prazoMaximoConclusao", c.getTime());
				dao.updateField(DiscenteStricto.class, dAtual.getId(), "mesAtual", dAtual.getMesAtual());
				dao.updateField(DiscenteStricto.class, dAtual.getId(), "variacaoPrazo", dAtual.getVariacaoPrazo());
			}
		} finally {
			dao.close();
			homologacaoDao.close();
			movDao.close();
		}
	}
	
}
