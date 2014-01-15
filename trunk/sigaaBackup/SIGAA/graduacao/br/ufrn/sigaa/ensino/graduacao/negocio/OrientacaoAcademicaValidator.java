/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 *
 * Valida��es espec�ficas para atribui��es de orienta��es acad�micas
 *
 * @author Andre Dantas
 *
 */
public class OrientacaoAcademicaValidator {

	/** Retorna uma inst�ncia de um DAO.
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	/**
	 * Valida se as orienta��es podem ser cadastradas.
	 * <ul>
	 * <li>Cada discente de gradua��o pode ter apenas um orientador acad�mico.</li>
	 * <li>Cada discente de p�s-gradua��o pode ter um orientador e v�rios co-orientadores.</li>
	 * <li>Nenhum docente pode ser orientador e co-orientador de um mesmo aluno.</li>
	 * </ul>
	 * @param docente
	 * @param orientacoes
	 * @param erros
	 * @throws DAOException
	 */
	public static void validaOrientacoes(Collection<OrientacaoAcademica> orientacoes, ListaMensagens erros) throws DAOException {

		if( !isEmpty( orientacoes ) ){
			OrientacaoAcademica next = orientacoes.iterator().next();
			if( next.getDiscente().isGraduacao() ) // se for discente de gradua��o 
				validaMaximoOrientacoesAcademicas( next.getServidor() , orientacoes, erros);
		}

		for( OrientacaoAcademica o : orientacoes ){
			validaOrientacao(o, erros);
		}

	}

	/** Valida a orienta��o acad�mica especificada.
	 * @param orientacao
	 * @param erros
	 * @throws DAOException
	 */
	public static void validaOrientacao(OrientacaoAcademica orientacao, ListaMensagens erros) throws DAOException {

		Collection<Integer> status = StatusDiscente.getAtivos();
		status.add( StatusDiscente.DEFENDIDO );
		if (orientacao.getDiscente().isStricto())
			status.add( StatusDiscente.CONCLUIDO );
		if( !status.contains( orientacao.getDiscente().getStatus() ) )
			erros.addErro("N�o � poss�vel cadastrar orienta��o para " + orientacao.getDiscente().toString() + " pois ele est� " + orientacao.getDiscente().getStatusString());

		if( orientacao.getId() == 0 ){
			validateRequired(orientacao.getDiscente(), "Discente", erros);
			validateRequired(orientacao.getTipoOrientacao(), "Tipo Orienta��o", erros);
			if( orientacao.isExterno() )
				validateRequired(orientacao.getDocenteExterno(), "Orientador", erros);
			else
				validateRequired(orientacao.getServidor(), "Orientador", erros);
		}

		if( !erros.isEmpty() )
			return;

		if( orientacao.getDiscente().isGraduacao() )
			validaOrientacaoGraduacao(orientacao, erros);
		if( orientacao.getDiscente().isStricto() )
			validaOrientacaoStricto(orientacao, erros);

	}

	/**
	 * Valida a orienta��o se, para o caso de aluno de gradua��o, tem apenas um orientador acad�mico.
	 * @param orientacao
	 * @throws DAOException
	 */
	public static void validaOrientacaoGraduacao(OrientacaoAcademica orientacao, ListaMensagens erros) throws DAOException {

		OrientacaoAcademicaDao dao = getDAO( OrientacaoAcademicaDao.class );
		try{
			Collection<OrientacaoAcademica> orientacoesDiscente = dao.findAtivosByDiscente(orientacao.getDiscente().getId(), null);

			if( !isEmpty( orientacoesDiscente ) )
				erros.addErro("O aluno " + orientacao.getDiscente().getNome() + " j� possui orientador acad�mico.");
		}finally{
			dao.close();
		}

	}

	/**
	 * Se for discente de stricto ele pode ter um orientador e v�rios co-orientadores, nenhum deles pode coincidir.
	 * @param orientacao
	 * @param erros
	 * @throws DAOException
	 */
	public static void validaOrientacaoStricto(OrientacaoAcademica orientacao, ListaMensagens erros) throws DAOException {

		validateRequired(orientacao.getInicio(), "Data de In�cio", erros);

		// Se for alterar orienta��o n�o deve validar se j� possui o orientador. 
		// Quanto se altera orienta��o, n�o � poss�vel alterar o tipo da orienta��o e nem o orientador.
		if( orientacao.getId() > 0 )
			return;

		OrientacaoAcademicaDao dao = getDAO( OrientacaoAcademicaDao.class );
		try{
			Collection<OrientacaoAcademica> orientacoesDiscente = dao.findAtivosByDiscente(orientacao.getDiscente().getId(), null);

			if( orientacao.getTipoOrientacao().equals( OrientacaoAcademica.ORIENTADOR) ){ // SE FOR ORIENTADOR
				OrientacaoAcademica orientador = dao.findOrientadorAtivoByDiscente( orientacao.getDiscente().getId() );
				if( orientador != null )
					erros.addErro("O aluno " + orientacao.getDiscente().getNome() + " j� possui orientador.");
			}

			for( OrientacaoAcademica oa : orientacoesDiscente ){
				if( oa.getIdDocente() == orientacao.getIdDocente() && ( ( oa.isExterno() && orientacao.isExterno() ) || ( !oa.isExterno() && !orientacao.isExterno() )  ) )
					erros.addErro("O aluno " + oa.getDiscente().getNome() + " j� possui " + oa.getServidor().getNome() + " como " + oa.getTipoOrientacaoString());
			}
		}finally{
			dao.close();
		}

	}

	/**
	 * Valida se ao adicionar um conjunto de discentes o limite m�ximo de orienta��es sera excedido.
	 * Se a cole��o de discentes for nula verifica se o docente j� possui o m�ximo de orienta��es ACADEMICAS.
	 * S� DEVE SER UTILIZADO NA GRADUACAO.
	 * @param docente
	 * @param orientandos
	 * @throws DAOException
	 */
	public static void validaMaximoOrientacoesAcademicas(Servidor docente,
			Collection<OrientacaoAcademica> orientacoes, ListaMensagens erros) throws DAOException {

		int totalOrientandos = 1;
		if( orientacoes != null )
			totalOrientandos = orientacoes.size();

		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		try {
			int totalOrientados = dao.findTotalOrientandosAtivosNivel(docente.getId(), NivelEnsino.GRADUACAO);
			if (totalOrientados + totalOrientandos > OrientacaoAcademica.MAXIMO_ORIENTACOES)
				erros.addErro("N�o � poss�vel cadastrar mais orienta��es para este docente." + "<br>O limite m�ximo de "
						+ OrientacaoAcademica.MAXIMO_ORIENTACOES + " orienta��es foi atingido.");
		} finally {
			dao.close();
		}

	}
}
