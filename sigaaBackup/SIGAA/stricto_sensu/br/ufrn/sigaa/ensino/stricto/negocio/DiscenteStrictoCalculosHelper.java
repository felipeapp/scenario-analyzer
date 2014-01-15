/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 19/03/2008
 * 
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.HomologacaoTrabalhoFinalDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;
import br.ufrn.sigaa.ensino.stricto.dominio.AlteracaoDiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;
import br.ufrn.sigaa.ensino.stricto.dominio.TipoCursoStricto;
import br.ufrn.sigaa.ensino.stricto.negocio.calculos.AtualizaStatusDiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.negocio.calculos.AtualizaTotaisIntegralizadosStricto;
import br.ufrn.sigaa.ensino.stricto.negocio.calculos.AtualizacoesGerais;
import br.ufrn.sigaa.ensino.stricto.negocio.calculos.AtualizarIndicesDiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.negocio.calculos.AtualizarMesAtualPrazoConclusaoStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe auxiliar que cont�m m�todos para calcular as integraliza��es e
 * identificar se o discente de stricto sensu pode concluir o curso de
 * p�s-gradua��o.
 * 
 * @author David Pereira
 * 
 */
public class DiscenteStrictoCalculosHelper {

	/**
	 * Calcula integraliza��es e verifica atividades para identificar se
	 * o discente pode concluir o curso de p�s-gradua��o.
	 * @throws NegocioException 
	 */
	public static boolean realizarCalculosDiscenteChain(DiscenteStricto d, Movimento mov) throws ArqException, NegocioException {
		CalculosDiscenteChainNode<DiscenteStricto> chain = new CalculosDiscenteChainNode<DiscenteStricto>();
		chain.setNext(new AtualizaTotaisIntegralizadosStricto())
			.setNext(new AtualizarIndicesDiscenteStricto())
			.setNext(new AtualizarMesAtualPrazoConclusaoStricto())
			.setNext(new AtualizaStatusDiscenteStricto())
			.setNext(new AtualizacoesGerais());
		
		chain.executar(d, mov, false);
		return false;
	}
	
	/**
	 * Calcula integraliza��es e verifica atividades para identificar se
	 * o discente pode concluir o curso de p�s-gradua��o.
	 */
	public static boolean realizarCalculosDiscente(DiscenteStricto d, Movimento mov) throws ArqException {
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class, mov.getUsuarioLogado());
		MatriculaComponenteDao mDao = getDAO(MatriculaComponenteDao.class, mov.getUsuarioLogado());
		try {

			d = dao.findByPrimaryKey(d.getId(), DiscenteStricto.class);
			
			int crTotalCurriculo = dao.calculaCrTotalCurriculo(d);

			atualizarTotaisIntegralizados(d, mov);
			atualizarCoeficienteRendimento(d, mov);
			atualizarMesAtualPrazoConclusao(d, mov);

			// Identifica se o discente cumpriu os cr�ditos exigidos
			boolean crCumpridos = crTotalCurriculo == d.getCrTotaisIntegralizados();

			Collection<MatriculaComponente> proficiencias = mDao.findAtividades(d, new TipoAtividade(TipoAtividade.PROFICIENCIA), SituacaoMatricula.APROVADO);
			Collection<MatriculaComponente> proficienciasAproveitadas = mDao.findAtividades(d, new TipoAtividade(TipoAtividade.PROFICIENCIA), SituacaoMatricula.APROVEITADO_CUMPRIU);
			Collection<MatriculaComponente> qualificacoes = mDao.findAtividades(d, new TipoAtividade(TipoAtividade.QUALIFICACAO), SituacaoMatricula.APROVADO);
			Collection<MatriculaComponente> defesas = mDao.findAtividades(d, new TipoAtividade(TipoAtividade.TESE), SituacaoMatricula.APROVADO);

			// Identifica se o discente possui defesas aprovadas 
			boolean cumpriuDefesas = !isEmpty(defesas);
			// Identifica se o discente possui qualifica��es aprovadas 
			boolean cumpriuQualificacoes = !isEmpty(qualificacoes);
			boolean cumpriuProficiencia = d.cumpriuTrabalhoProficiencia(proficiencias, proficienciasAproveitadas, DiscenteHelper.getQuantidadeMinimaTrabalhosProficienciaExigida(d));
			
			atualizarStatusDiscente(d, mov, cumpriuDefesas);
			
			dao.updateField(DiscenteStricto.class, d.getId(), "ultimaAtualizacaoTotais", new Date());
			
			return crCumpridos && cumpriuDefesas && cumpriuQualificacoes && cumpriuProficiencia;

		} finally {
			dao.close();
			mDao.close();
		}

	}

	/**
	 * Verifica se o status do discente deve ser alterado, se necess�rio j� atualiza.
	 * @param d 
	 * @param mov
	 * @throws DAOException 
	 */
	private static void atualizarStatusDiscente(DiscenteStricto d, Movimento mov, boolean cumpriuDefesas) throws DAOException {
		
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class, mov.getUsuarioLogado());
		try {
			
			if( d.isAtivo() && cumpriuDefesas ){
				// se o discente estiver com o status ATIVO e j� tiver cumprido a atividade de defesa ent�o ele deve ir para o status DEFENDIDO
				dao.updateField(Discente.class, d.getId(), "status", StatusDiscente.DEFENDIDO);
			} else if( d.isDefendido() && !cumpriuDefesas ){
				// se o discente estiver com o status DEFENDIDO e n�o tiver cumprido a atividade de defesa ent�o ele deve ir para o status ATIVO
				dao.updateField(Discente.class, d.getId(), "status", StatusDiscente.ATIVO);
			}
			
		} finally{
			dao.close();
		}
		
	}

	/** Calcula e atualiza o m�s atual e o prazo de conclus�o do discente.
	 * @param d
	 * @param mov
	 * @throws DAOException
	 */
	public static void atualizarMesAtualPrazoConclusao(DiscenteStricto d, Movimento mov) throws DAOException {
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
			Integer anoInicio = d.getAnoEntrada();
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

			dao.updateField(DiscenteStricto.class, d.getId(), "prazoMaximoConclusao", c.getTime());
			dao.updateField(DiscenteStricto.class, d.getId(), "mesAtual", d.getMesAtual());
			dao.updateField(DiscenteStricto.class, d.getId(), "variacaoPrazo", d.getVariacaoPrazo());
			
		} finally {
			dao.close();
			homologacaoDao.close();
			movDao.close();
		}
	}

	/** Calcula e atualiza o coeficiente de rendimento do discente.
	 * @param d
	 * @param mov
	 * @throws DAOException
	 */
	private static void atualizarCoeficienteRendimento(DiscenteStricto d, Movimento mov) throws DAOException {
		IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class, mov.getUsuarioLogado());
		try {
			d.setMediaGeral((float) dao.calculaIraDiscenteStricto(d.getId()));
			dao.updateField(DiscenteStricto.class, d.getId(), "mediaGeral", d.getMediaGeral());
		} finally {
			dao.close();
		}
	}

	/**
	 * Calcula e atualiza os campos (em DiscenteStricto) dos cr�ditos e CH
	 * integralizados pelo discente.
	 */
	private static void atualizarTotaisIntegralizados(DiscenteStricto d, Movimento mov) throws ArqException {
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class, mov.getUsuarioLogado());

		try {
			calcularIntegralizacoes(d, SituacaoMatricula.getSituacoesPagas());
			dao.atualizaTotaisIntegralizados(d);

			registraAlteracaoCalculos(d.getId(), dao, mov);
		} finally {
			dao.close();
		}

	}

	/**
	 * Calcula a carga hor�ria integralizada do discente.
	 */
	private static void calcularIntegralizacoes(DiscenteStricto d, Collection<SituacaoMatricula> situacoes) throws DAOException {
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class);

		try {
			int chTotal = dao.calculaCrTotaisIntegralizados(d);
			d.setCrTotaisIntegralizados((short) chTotal);
		} finally {
			dao.close();
		}

	}

	/** Retorna uma inst�ncia de um DAO especificado.
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return getDAO(dao, null);
	}
	
	/** Retorna uma inst�ncia de um DAO especificado.
	 * @param <T>
	 * @param dao
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao, UsuarioGeral usuario) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, usuario, null);
	}

	/** Registra a altera��o dos c�lculos realizados para o discente.
	 * @param idDiscenteStricto
	 * @param dao
	 * @param mov
	 * @throws DAOException
	 */
	private static void registraAlteracaoCalculos(int idDiscenteStricto, DiscenteStrictoDao dao, Movimento mov) throws DAOException {

		DiscenteStricto ds = dao.findByPrimaryKey(idDiscenteStricto, DiscenteStricto.class);

		AlteracaoDiscenteStricto alteracao = new AlteracaoDiscenteStricto();
		alteracao.setDiscente(ds);
		alteracao.setData(new Date());
		alteracao.setOperacao(mov.getCodMovimento().getId());
		alteracao.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());

		alteracao.setCrTotaisIntegralizados(ds.getCrTotaisIntegralizados());
		alteracao.setCrTotaisObrigatorios(ds.getCrTotaisObrigatoriosIntegralizado());

		dao.create(alteracao);
	}

}
