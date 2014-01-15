/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jul 10, 2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.prodocente.atividades.dominio.TeseOrientada;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacao;

/**
 * Processador respons�vel por executar as opera��es sobre orienta��o acad�mica
 * @author Victor Hugo
 *
 */
public class ProcessadorOrientacaoAcademica extends AbstractProcessador {

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException {

		validate(movimento);
		MovimentoOrientacaoAcademica mov = (MovimentoOrientacaoAcademica) movimento;

		if( mov.getCodMovimento() == SigaaListaComando.CADASTRAR_ORIENTACAO_ACADEMICA )
			return cadastrar(mov);
		else if( mov.getCodMovimento() == SigaaListaComando.DESATIVAR_ORIENTACAO_ACADEMICA )
			return desativar(mov);
		else if( mov.getCodMovimento() == SigaaListaComando.CANCELAR_ORIENTACAO_ACADEMICA )
			return cancelarOrientacaoStricto(mov);
		else if( mov.getCodMovimento() == SigaaListaComando.ALTERAR_ORIENTACAO_ACADEMICA )
			return alterarOrientacaoStricto(mov);
		else if( mov.getCodMovimento() == SigaaListaComando.FINALIZAR_ORIENTACOES_DISCENTE )
			desativarOrientacoes(mov);
		else if( mov.getCodMovimento() == SigaaListaComando.CANCELAR_ORIENTACOES_DISCENTE )
			desativarOrientacoes(mov);

		return null;
	}

	/**
	 * Alterar orienta��o de aluno stricto. S� altera a data de inicio.
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Object alterarOrientacaoStricto(MovimentoOrientacaoAcademica mov) throws DAOException {
		OrientacaoAcademica orientacao = mov.getOrientacao();
		getGenericDAO(mov).updateField(OrientacaoAcademica.class, orientacao.getId(), "inicio", orientacao.getInicio());
		return null;
	}

	/**
	 * Desativa a orienta��o acad�mica.
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private OrientacaoAcademica desativar(MovimentoOrientacaoAcademica mov) throws DAOException, NegocioException {

		GenericDAO dao = getGenericDAO(mov);
		try {
			OrientacaoAcademica orientacao = mov.getOrientacao();
			
			Date dataFim = orientacao.getFim();
			if( dataFim == null && orientacao.getDiscente().isStricto() ) 
				// se for discente stricto � necess�rio informar a data de fim da orienta��o
				throw new NegocioException("� necess�rio informar a data de finaliza��o da orienta��o");
			else if( dataFim == null && (orientacao.getDiscente().isGraduacao() || orientacao.getDiscente().isTecnico()) )
				// se for discente de gradua��o/t�cnico a data de fim por padr�o eh HOJE, caso esteja nulo
				dataFim = new Date();  
			
			orientacao = dao.findByPrimaryKey(orientacao.getId(), OrientacaoAcademica.class);
			orientacao.setFim( dataFim );
			orientacao.setDataFinalizacao( new Date() );
			orientacao.setRegistroFinalizacao( mov.getUsuarioLogado().getRegistroEntrada() );
			
			dao.update(orientacao);
			
			if( orientacao.getDiscente().isStricto() )
				finalizarTeseOrientadas(mov, dataFim);
			
			return orientacao;
		} finally {
			dao.close();
		}
	}

	/**
	 * <b>DESATIVA</b> todas as orienta��es abertas do aluno. Deve ser invocado
	 * sempre que o v�nculo com a institui��o for interrompido, seja tempor�ria
	 * ou permanentemente.
	 * 
	 * @param mov
	 *            deve ter uma OrientacaoAcademica setada com um discente e a
	 *            dataFim da orienta��o.
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void desativarOrientacoes(MovimentoOrientacaoAcademica mov) throws DAOException, NegocioException{

		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class, mov);
		try {
			OrientacaoAcademica orientacao = mov.getOrientacao();
			Discente discente = orientacao.getDiscente();
			Collection<OrientacaoAcademica> orientacoes = dao.findAtivosByDiscente(discente.getId(), null);
			
			if (isEmpty(orientacoes)) 
				return;
			
			Date dataFim = orientacao.getFim();
			if( dataFim == null && orientacao.getDiscente().isStricto() ) 
				// se for discente stricto � necess�rio informar a data de fim da orienta��o
				throw new NegocioException("� necess�rio informar a data de finaliza��o da orienta��o");
			else if( dataFim == null && orientacao.getDiscente().isGraduacao() )
				// se for discente de gradua��o a data de fim por padr�o eh HOJE, caso esteja nulo
				dataFim = new Date(); 
			
			for( OrientacaoAcademica o : orientacoes ){
				o.setFim( dataFim );
				o.setCancelado(true);
				MovimentoOrientacaoAcademica movOrientacoes = new MovimentoOrientacaoAcademica();
				movOrientacoes.setCodMovimento(SigaaListaComando.DESATIVAR_ORIENTACAO_ACADEMICA);
				movOrientacoes.setSistema(mov.getSistema());
				movOrientacoes.setOrientacao(o);
				movOrientacoes.setSubsistema(mov.getSubsistema());
				movOrientacoes.setUsuarioLogado(mov.getUsuarioLogado());
				desativar(movOrientacoes);
			}
			
		} finally {
			dao.close();
		}

	}

	/** Finaliza as testes orientadas.
	 * @param mov
	 * @param dataFim
	 * @throws DAOException
	 */
	private void finalizarTeseOrientadas(MovimentoOrientacaoAcademica mov, Date dataFim) throws DAOException {
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class, mov);
		OrientacaoAcademica oa = mov.getOrientacao();
		// S� CADASTRA TESEORIENTADA PARA DOCENTE DA UFRN
		if( oa.isExterno() )
			return;  

		TeseOrientada tese = dao.findTeseOrientadaByDiscenteOrientador(oa.getDiscente(), oa.getServidor());
		if (tese != null) {
			tese.setPeriodoFim(dataFim);
			dao.update(tese);
		}
	}

	/**
	 * Cadastra novas orienta��es acad�micas.
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private Object cadastrar(MovimentoOrientacaoAcademica mov) throws DAOException, NegocioException {

		if( (mov.getOrientacoes() == null || mov.getOrientacoes().size() == 0 )&& mov.getOrientacao() == null )
			throw new NegocioException("Opera��o n�o suportada. N�o h� nenhuma orienta��o informada para cadastro.");
		else if( mov.getOrientacao() != null )
			return cadastrarOrientacaoStricto(mov);
		else if( mov.getOrientacoes() != null )
			return cadastrarOrientacaoAcademica(mov);

		return null;
	}

	/**
	 * Cadastra orienta��es acad�micas para discentes de gradua��o.
	 * @param mov
	 * @throws DAOException
	 */
	private Collection<OrientacaoAcademica> cadastrarOrientacaoAcademica(MovimentoOrientacaoAcademica mov) throws DAOException {

		OrientacaoAcademicaDao dao = getDAO( OrientacaoAcademicaDao.class, mov);

		for (OrientacaoAcademica orientacao : mov.getOrientacoes()) {
			orientacao.setDocenteExterno(null);
			if( orientacao.getInicio() == null )
				orientacao.setInicio(new Date());
			dao.createNoFlush(orientacao);
		}
		dao.close();

		return mov.getOrientacoes();
	}

	/**
	 * Cadastra orienta��es para discentes de stricto.
	 * @param mov
	 * @throws DAOException
	 */
	private OrientacaoAcademica cadastrarOrientacaoStricto(MovimentoOrientacaoAcademica mov) throws DAOException  {

		OrientacaoAcademicaDao dao = getDAO( OrientacaoAcademicaDao.class, mov);
		OrientacaoAcademica orientacao = mov.getOrientacao();
		orientacao.ajustar();
		dao.create(orientacao);
		// se for ORIENTADOR deve cadastrar tese orientada 
		if (orientacao.isOrientador())
			cadastrarTeseOrientadas(mov);
		dao.close();

		return orientacao;

	}

	/**
	 * Cadastrar uma tese orientada referencia a esta orienta��o no prodocente.
	 * @param mov
	 * @param orientacao
	 * @throws DAOException
	 */
	private void cadastrarTeseOrientadas(MovimentoOrientacaoAcademica mov) throws DAOException {
		// APENAS ORIENTACAO PONTUA NO PRODOCENTE
		OrientacaoAcademica orientacao = mov.getOrientacao();
		// SO CADASTRA TESEORIENTADA PARA DOCENTE DA UFRN 
		if( orientacao.isExterno() )
			return; 

		if( orientacao.isOrientador() ){
			GenericDAO dao = getGenericDAO(mov);
			DiscenteStricto d = dao.findByPrimaryKey(orientacao.getDiscente().getId(), DiscenteStricto.class);
			TeseOrientada tese = new TeseOrientada();
			tese.setOrientacao( orientacao.getTipoOrientacao() );
			tese.setAtivo(true);
			tese.setServidor(orientacao.getServidor());
			tese.setOrientandoDiscente(d.getDiscente());
			tese.setDiscenteExterno(false);
			tese.setPeriodoInicio(orientacao.getInicio());
			tese.setProgramaPos(d.getGestoraAcademica());
			tese.setInstituicaoEnsino(new InstituicoesEnsino(InstituicoesEnsino.UFRN));
			if (d.getNivel() == NivelEnsino.MESTRADO)
				tese.setTipoOrientacao(new TipoOrientacao(TipoOrientacao.MESTRADO)); // de acordo com n�vel do discente
			else if (d.getNivel() == NivelEnsino.DOUTORADO)
				tese.setTipoOrientacao(new TipoOrientacao(TipoOrientacao.DOUTORADO)); // de acordo com n�vel do discente
			dao.create(tese);
		}
	}

	/**
	 * Cancela uma orienta��o, utilizado apenas para discente de stricto.
	 * O cancelamento deve ser utilizado APENAS QUANDO A ORIENTACAO FOI CADASTRADA ERRADA.
	 * SER� CANCELADA A ORIENTACAO DO ALUNO E AS TESES ORIENTADAS CRIADAS DEVIDO � ESTA ORIENTACAO.
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private OrientacaoAcademica cancelarOrientacaoStricto(MovimentoOrientacaoAcademica mov) throws DAOException  {

		OrientacaoAcademicaDao dao = getDAO( OrientacaoAcademicaDao.class, mov);
		OrientacaoAcademica orientacao = mov.getOrientacao();
		dao.updateField(OrientacaoAcademica.class, orientacao.getId(), "cancelado", true);
		// se for ORIENTADOR deve cadastrar tese orientada 
		if (orientacao.isOrientador())
			cancelarTeseOrientadas(mov);

		dao.close();
		return orientacao;
	}

	/**
	 * Cancela a tese orientada relacionada a orienta��o do movimento.
	 * @param mov
	 * @throws DAOException
	 */
	private void cancelarTeseOrientadas(MovimentoOrientacaoAcademica mov) throws DAOException {
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class, mov);
		OrientacaoAcademica oa = mov.getOrientacao();
		if( oa.isExterno() )
			return; /** SO CADASTRA TESEORIENTADA PARA DOCENTE DA UFRN */

		TeseOrientada tese = dao.findTeseOrientadaByDiscenteOrientador(oa.getDiscente(), oa.getServidor());
		if (tese != null) {
			tese.setAtivo(false);
			dao.update(tese);
		}
	}

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento movimento) throws NegocioException, SegurancaException, ArqException {

		MovimentoOrientacaoAcademica mov = (MovimentoOrientacaoAcademica) movimento;
		ListaMensagens erros = new ListaMensagens();

		if( mov.getCodMovimento() == SigaaListaComando.CADASTRAR_ORIENTACAO_ACADEMICA ){
			checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
					SigaaPapeis.SECRETARIA_POS , SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
					SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.DAE, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO}, mov);

			if( (mov.getOrientacoes() == null || mov.getOrientacoes().size() == 0 )&& mov.getOrientacao() == null )
				throw new NegocioException("Opera��o n�o suportada. � necess�rio definir a orienta��o que deseja cadastrar.");

			if( !isEmpty( mov.getOrientacoes() ) )
				OrientacaoAcademicaValidator.validaOrientacoes(mov.getOrientacoes(), erros);

			if( !isEmpty( mov.getOrientacao() ) ){
				OrientacaoAcademicaValidator.validaOrientacao(mov.getOrientacao(), erros);
				if( erros != null && !erros.isEmpty() ){
					NegocioException e = new NegocioException();
					e.setListaMensagens(erros);
					throw e;
				}
			}

		}else if( mov.getCodMovimento() == SigaaListaComando.CANCELAR_ORIENTACAO_ACADEMICA ){
			checkRole( new int[] {SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO}, mov);
		}else if( mov.getCodMovimento() == SigaaListaComando.DESATIVAR_ORIENTACAO_ACADEMICA ){
			checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
					SigaaPapeis.SECRETARIA_POS , SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
					SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO,
					SigaaPapeis.CDP, SigaaPapeis.DAE}, mov);
			OrientacaoAcademica orientacao = mov.getOrientacao();

			if ( orientacao.getDiscente().isStricto() ) {
				if ( orientacao.getFim() == null ) {
					throw new NegocioException("Data de finaliza��o: campo obrigat�rio n�o informado");
				}
				
				if( orientacao.getDiscente().isStricto() &&  !orientacao.getFim().after( orientacao.getInicio() ) )
					throw new NegocioException("A data de finaliza��o da orienta��o deve ser posterior a data de in�cio.");
			}
			

		}

	}

}
