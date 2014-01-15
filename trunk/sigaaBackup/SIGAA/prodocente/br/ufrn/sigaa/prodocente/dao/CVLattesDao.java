/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 05/03/2013 
 */
package br.ufrn.sigaa.prodocente.dao;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.prodocente.lattes.dominio.PessoaLattes;

/**
 * Dao com consultas utilizadas pelo cliente Web Service do CV Lattes.
 * 
 * @author Leonardo
 * 
 */
public class CVLattesDao extends GenericSigaaDAO {

	/**
	 * Retorna a pessoa associada ao currículo lattes a partir do seu CPF.
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public PessoaLattes findByCPF(long cpf) throws DAOException {
		return (PessoaLattes) getSession()
				.createQuery(
						"select p from PessoaLattes p where p.pessoa.cpf_cnpj = ?")
				.setLong(1, cpf).setMaxResults(1).uniqueResult();
	}
	
	/**
	 * Retorna a pessoa associada ao currículo lattes a partir do seu identificador.
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public PessoaLattes findByIdPessoa(int idPessoa) throws DAOException {
		return (PessoaLattes) getSession()
				.createQuery(
						"select p from PessoaLattes p where p.pessoa.id = :idPessoa")
						.setInteger("idPessoa", idPessoa).setMaxResults(1).uniqueResult();
	}
	
	/**
	 * Retorna as pessoas que ainda não possuem o identificador do CNPq registrado.
	 * @return
	 * @throws DAOException
	 */
	public List<PessoaLattes> findPessoasSemIdCNPq() throws DAOException {
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createQuery("SELECT pl, p.cpf_cnpj, p.nome FROM PessoaLattes pl join pl.pessoa WHERE pl.idCnpq is null and pl.autorizaAcesso").list();
		
		List<PessoaLattes> result = new ArrayList<PessoaLattes>();
		for(Object[] o: lista){
			Pessoa p = new Pessoa();
			p.setCpf_cnpj((Long) o[1]);
			p.setNome((String) o[2]);
			
			PessoaLattes pl = new PessoaLattes();
			pl = (PessoaLattes) o[0];
			p.setId(pl.getPessoa().getId());
			pl.setPessoa(p);
			pl.setAtualizar(Boolean.TRUE);
			
			result.add(pl);
		}
		
		return result;
	}
	
	/**
	 * Retorna as pessoas que autorizaram o acesso do sistema aos seus currículos lattes.
	 * @return
	 * @throws DAOException
	 */
	public List<PessoaLattes> findPessoasAutorizadas() throws DAOException {
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createQuery("SELECT pl, p.cpf_cnpj, p.nome FROM PessoaLattes pl join pl.pessoa p WHERE pl.autorizaAcesso = trueValue()").list();
		
		List<PessoaLattes> result = new ArrayList<PessoaLattes>();
		for(Object[] o: lista){
			Pessoa p = new Pessoa();
			p.setCpf_cnpj((Long) o[1]);
			p.setNome((String) o[2]);
			
			PessoaLattes pl = new PessoaLattes();
			pl = (PessoaLattes) o[0];
			p.setId(pl.getPessoa().getId());
			pl.setPessoa(p);
			
			result.add(pl);
		}
		
		return result;
	}
}
