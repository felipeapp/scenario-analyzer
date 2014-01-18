package br.ufrn.sigaa.ensino_rede.academico.dao;

import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino_rede.academico.dominio.MovimentacaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.academico.dominio.TipoMovimentacao;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;

public class MovimentacaoDiscenteAssociadoDao extends GenericSigaaDAO {

	public MovimentacaoDiscenteAssociado findByDiscenteAnoPeriodo(DiscenteAssociado discente, int ano, int periodo) throws DAOException {
		
		String hql = " select ma from MovimentacaoDiscenteAssociado ma "
				+ "	join ma.discente discente "
				+ " where ma.ativo = trueValue() "
				+ " and discente.id = " + discente.getId()
				+ " and ma.anoReferencia = " + ano
				+ " and ma.periodoReferencia = " + periodo;
		
		return (MovimentacaoDiscenteAssociado) getSession().createQuery(hql).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<MovimentacaoDiscenteAssociado> findByDiscenteAndTipo(DiscenteAssociado discente,TipoMovimentacao tipo) throws DAOException {
		
		String hql = " select ma from MovimentacaoDiscenteAssociado ma "
				+ "	join ma.discente discente "
				+ "	join ma.tipo tipo "
				+ " where ma.ativo = trueValue() "
				+ " and discente.id = " + discente.getId()
				+ " and tipo.id = " + tipo.getId();
		
		return getSession().createQuery(hql).list();
	}
	
	
	/**
	 * Retorna os trancamento que não foram retornados e nem estornados.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<MovimentacaoDiscenteAssociado> findTrancamentosNaoRetornados(DiscenteAssociado discente) throws DAOException {
		
		String hql = " select ma from MovimentacaoDiscenteAssociado ma "
				+ "	join ma.discente discente "
				+ "	join ma.tipo tipo "
				+ " where ma.ativo = trueValue() AND ma.dataRetorno is null"
				+ " and discente.id = " + discente.getId()
				+ " and tipo.id = " + TipoMovimentacao.TRANCAMENTO.getId();
		
		return getSession().createQuery(hql).list();
	}
	
	/**
	 * Retornar todos os movimentos de um dicente.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<MovimentacaoDiscenteAssociado> findAllByDiscente(DiscenteAssociado discente) throws DAOException {
		
		String hql = " select ma from MovimentacaoDiscenteAssociado ma "
				+ "	join ma.discente discente "
				+ "	join ma.tipo tipo "
				+ " where ma.ativo = trueValue() "
				+ " and discente.id = " + discente.getId();
		
		return getSession().createQuery(hql).list();
	}
	
	
}
