package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaProcessoSeletivo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.InscricaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.PessoaTecnico;

/***
 * 
 * @author Rafael Barros
 * 
 * Dao responsável por todas as consultas adequadas para a importação dos discentes aprovados do IMD
 *
 */

public class ImportacaoDiscenteIMDDao extends GenericSigaaDAO {

	/**
	 * Lista a pessoa tecnico vinculada a um determinado CPF
	 * 
	 * @param cpf
	 * @return PessoaTecnico
	 * @throws DAOException
	 */
	public PessoaTecnico findPessoaTecnicoByCPF(Long cpf) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(PessoaTecnico.class);
			c.add(Expression.eq("cpf_cnpj", cpf));

			return (PessoaTecnico) c.uniqueResult();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Lista a inscrição no processo seletivo de acordo com o PS e id_pessoa_tecnico
	 * 
	 * @param idPessoaTecnico, idPS
	 * @return InscricaoProcessoSeletivoTecnico
	 * @throws DAOException
	 */
	public InscricaoProcessoSeletivoTecnico findInscricaoByPSAndPessoa(int idPessoa, int idPS) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(InscricaoProcessoSeletivoTecnico.class);
			Criteria cPessoa = c.createCriteria("pessoa");	
			Criteria cPS = c.createCriteria("processoSeletivo");
			
			cPessoa.add(Expression.eq("id", idPessoa));
			cPS.add(Expression.eq("id", idPS));

			return (InscricaoProcessoSeletivoTecnico) c.uniqueResult();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	/**
	 * Lista a inscrição no processo seletivo de acordo com o numero da inscrição
	 * 
	 * @param numeroInscricao
	 * @return InscricaoProcessoSeletivoTecnico
	 * @throws DAOException
	 */
	public InscricaoProcessoSeletivoTecnico findInscricaoByNumero(int numeroInscricao) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(InscricaoProcessoSeletivoTecnico.class);
			
			c.add(Expression.eq("numeroInscricao", numeroInscricao));

			return (InscricaoProcessoSeletivoTecnico) c.uniqueResult();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	/**
	 * Lista a convocação relacionada a um determinado PS e opcao polo grupo
	 * 
	 * @param idPS, idOpcao
	 * @return ConvocacaoProcessoSeletivoTecnico
	 * @throws DAOException
	 */
	public ConvocacaoProcessoSeletivoTecnico findPrimeiraConvocacaoByPSOpcao(int idPS, int idOpcao) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(ConvocacaoProcessoSeletivoTecnico.class);
			Criteria cOp = c.createCriteria("opcao");
			Criteria cPs = c.createCriteria("processoSeletivo");
			
			cOp.add(Expression.eq("id", idOpcao));
			cPs.add(Expression.eq("id", idPS));

			return (ConvocacaoProcessoSeletivoTecnico) c.uniqueResult();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	
}
