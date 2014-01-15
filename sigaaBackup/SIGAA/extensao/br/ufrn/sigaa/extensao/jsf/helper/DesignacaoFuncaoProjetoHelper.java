package br.ufrn.sigaa.extensao.jsf.helper;

import java.util.Collection;

import org.hibernate.HibernateException;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.extensao.dao.DesignacaoFuncaoProjetoDao;
import br.ufrn.sigaa.extensao.dominio.TipoDesignacaoFuncaoProjeto;

public class DesignacaoFuncaoProjetoHelper {

	/**
	 * Serve para verificar se uma determinada pessoa é Coordenador ou 
	 * se tem designação de Coordenador em um determinado projeto.
	 * @param idProjeto
	 * @param idPessoa
	 * @return
	 */
	public static boolean isCoordenadorProjeto( int idProjeto, int idPessoa ){
		return DAOFactory.getInstance().getDAO(DesignacaoFuncaoProjetoDao.class).
				findDesignacaoMembroProjeto(idProjeto, idPessoa, 
						TipoDesignacaoFuncaoProjeto.FUNCAO_COORDENACAO_EXTENSAO, true);
	}
	
	/**
	 * Serve para verificar se uma determinada pessoa é Coordenador ou 
	 * se tem designação de Coordenador em um determinado projeto.
	 * @param idProjeto
	 * @param idPessoa
	 * @return
	 */
	public static boolean isCoordenadorOrDesignacaoCoordenadorProjeto( int idProjeto, int idPessoa ){
		return DAOFactory.getInstance().getDAO(DesignacaoFuncaoProjetoDao.class).
				findDesignacaoMembroProjeto(idProjeto, idPessoa, 
						TipoDesignacaoFuncaoProjeto.FUNCAO_COORDENACAO_EXTENSAO, false);
	}
	
	/**
	 * Serve para verificar se uma determinada pessoa é Coordenador ou 
	 * se tem designação de Gerencia de participantes em um determinado projeto.
	 * 
	 * @param idProjeto
	 * @param idPessoa
	 * @return
	 */
	public static boolean isCoordenadorOrDesignacaoGerenciaParticipanteProjeto( int idProjeto, int idPessoa ){
		return DAOFactory.getInstance().getDAO(DesignacaoFuncaoProjetoDao.class).
				findDesignacaoMembroProjeto(idProjeto, idPessoa, 
						TipoDesignacaoFuncaoProjeto.GERENCIAR_PARTICIPANTES_EXTENSAO, false);
	}

	public static boolean isCoordenadorOrDesignacaoCoordenador( int idPessoa ) {
		return DAOFactory.getInstance().getDAO(DesignacaoFuncaoProjetoDao.class).
				findDesignacaoMembroProjeto(0, idPessoa, 
						TipoDesignacaoFuncaoProjeto.FUNCAO_COORDENACAO_EXTENSAO, false);
	}

	public static Collection<Integer> projetosByCoordenadoresOrDesignacaoCoordenador( int idServidor ) throws HibernateException, DAOException {
		return DAOFactory.getInstance().getDAO(DesignacaoFuncaoProjetoDao.class).
				findDesignacoesByServidor(idServidor, true, 
						TipoDesignacaoFuncaoProjeto.FUNCAO_COORDENACAO_EXTENSAO);
	}

	public static Collection<Integer> atividadesByCoordenadoresOrDesignacaoCoordenador( int idServidor ) throws HibernateException, DAOException {
		return DAOFactory.getInstance().getDAO(DesignacaoFuncaoProjetoDao.class).
				findDesignacoesByServidor(idServidor, false, 
						TipoDesignacaoFuncaoProjeto.FUNCAO_COORDENACAO_EXTENSAO);
	}
	
	public static Collection<Integer> projetosByCoordenadoresOrDesignacaoCoordenadorOrDesignacaoGerenciaParticipante( int idServidor ) throws HibernateException, DAOException {
		return DAOFactory.getInstance().getDAO(DesignacaoFuncaoProjetoDao.class).
				findDesignacoesByServidor(idServidor, true, 
						TipoDesignacaoFuncaoProjeto.FUNCAO_COORDENACAO_EXTENSAO, TipoDesignacaoFuncaoProjeto.GERENCIAR_PARTICIPANTES_EXTENSAO);
	}

}