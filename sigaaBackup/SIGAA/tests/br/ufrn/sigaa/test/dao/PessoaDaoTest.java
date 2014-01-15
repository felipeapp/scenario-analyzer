package br.ufrn.sigaa.test.dao;

import java.util.Collection;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
/**
 * 
 * @author Rafael Buriti /  Dalton
 *
 */
public class PessoaDaoTest extends TestCase{
	
	PessoaDao p = new PessoaDao();
	
	/**
	 * Retorna uma coleção de pessoas que possuem o nome e o tipo que são passados como 
	 * paramentros.
	 * 
	 * Método: findByNomeTipo
	 * 
	 * @param nome
	 * @param tipo
	 * @param paging
	 * 
	 * @return Collection<Pessoa>
	 * @throws DAOException
	 */
	public void testFindByNomeTipo() throws DAOException{
		Collection pessoas = p.findByNomeTipo("GLEYDSON", 'F', null);
		
		assertNotNull(pessoas);
		assertTrue(pessoas.size() > 0);
		
		pessoas = p.findByNomeTipo("GLEYDSON LIMA", 'K', null);
		
		assertTrue(pessoas.size() == 0);
	
	}

	/**
	 * Retorna uma coleção de pessoas que possuem o cpf ou cnpj e o tipo que são passados como 
	 * paramentros.
	 * 
	 * Método: findByCpfCnpjTipo
	 * 
	 * @param num
	 * @param tipo
	 * @param paging
	 * 
	 * @return Collection<Pessoa>
	 * @throws DAOException
	 */
	public void testFindByCpfCnpjTipo() throws DAOException{
		Collection pessoas = p.findByCpfCnpjTipo(Long.valueOf("1313937401"), 'F', null);
	
		assertNotNull(pessoas);
		assertTrue(pessoas.size() == 1);
		
		pessoas = p.findByCpfCnpjTipo(Long.valueOf("1313937401"), 'J', null);
	
		assertTrue(pessoas.size() == 0);
		
		// passando CPF = 0
		assertTrue(p.findByCpfCnpjTipo(Long.valueOf("0"), 'F', null).size() == 0);
	}
	
	/**
	 * Retorna uma coleção de pessoas que possuem o cpf ou cnpj passado como 
	 * paramentro.
	 * 
	 * Método: findByCpfCnpj
	 * 
	 * @param cpfCnpj
	 * 
	 * @return Collection<Pessoa>
	 * @throws DAOException
	 */
	public void testFindByCpfCnpj() throws DAOException{
		Collection pessoas = p.findByCpfCnpj(Long.valueOf("1313937401"));
	
		assertNotNull(pessoas);
		assertTrue(pessoas.size() == 1);
		
		pessoas = p.findByCpfCnpj(Long.valueOf("1313937400"));
	
		assertTrue(pessoas.size() == 0);
		
		// passando o cfp = "0"
		assertTrue(p.findByCpfCnpj(Long.valueOf("0")).size() == 0);
	}
	
	/**
	 * Retorna uma coleção de pessoas que possuem o cpf ou cnpj iguais.
	 * 
	 * Método: existePessoa
	 * 
	 * @param pessoa
	 * 
	 * @return boolean
	 * @throws DAOException
	 */
	public void testExistePessoa() throws DAOException{
		//teste só poderá ser realizado com pessoas diferentes que tenham o memso CPF ou CNPJ
		Pessoa pessoa = p.findByPrimaryKey(1093724, Pessoa.class);

		//assertTrue(p.existePessoa(pessoa));

		assertFalse(p.existePessoa(pessoa));
		
		// passando valor nulo
		assertFalse(p.existePessoa(null));
	}
	
	
	/**
	 * Retorna o id de uma pessoa que possue o cpf ou cnpj passado como 
	 * paramentro.
	 * 
	 * Método: findIdByCpf
	 * 
	 * @param cpf
	 * 
	 * @return Integer
	 * @throws DAOException
	 */
	public void testFindIdByCpf() throws DAOException{
		int id_pessoa = p.findIdByCpf(Long.valueOf("1313937401"));

		assertNotNull(id_pessoa);
		assertTrue(id_pessoa > 0);

		// passando valor nulo
		assertTrue(p.findIdByCpf(0) == 0);
		
	}
	
	/**
	 * Retorna uma pessoa que possue o cpf ou cnpj passado como paramentro.
	 * Retorna o registro mais recente.
	 * 
	 * Método: findMaisRecenteByCPF
	 * 
	 * @param nome
	 * @param tipo
	 * @param paging
	 * 
	 * @return Pessoa
	 * @throws DAOException
	 */
	public void testFindMaisRecenteByCPF() throws DAOException{
		Pessoa pessoa = p.findMaisRecenteByCPF(Long.valueOf("1313937401"));

		assertNotNull(pessoa);
		assertTrue(pessoa.getId() > 0);
		
		// passando cpf = 0
		assertNull(p.findMaisRecenteByCPF(0));

	}
	
	/**
	 * Retorna uma pessoa que possue o cpf ou cnpj passado como paramentro.
	 * 
	 * Método: findByCpf
	 * 
	 * @param cpf
	 * 
	 * @return Pessoa
	 * @throws DAOException
	 */
	public void testFindByCpf() throws DAOException{
		Pessoa pessoa = p.findByCpf(Long.valueOf("1313937401"));

		assertNotNull(pessoa);
		assertTrue(pessoa.getId() > 0);
		
		//passando cpf = 0
		assertNull(p.findByCpf(0));

	}
	
	/**
	 * Retorna uma pessoa que possue o cpf ou cnpj passado como paramentro.
	 * 
	 * Método: findByCpf
	 * 
	 * @param cpf
	 * @param leve
	 *  
	 * @return Pessoa
	 * @throws DAOException
	 */
	public void testFindByCpf2() throws DAOException{
		//falha no Teste se colocar true
		Pessoa pessoa = p.findByCpf(1313937401, false);
		
		assertNotNull(pessoa);
		assertTrue(pessoa.getId() > 0);
		
		//passando cpf = 0
		assertNull(p.findByCpf(0, false));

	}
	
	/**
	 * Retorna uma pessoa que possue o passaporte passado como paramentro.
	 * 
	 * Método: findByPassaporte
	 * 
	 * @param passaporte
	 * 
	 * @return Pessoa
	 * @throws DAOException
	 */
	public void testFindByPassaporte() throws DAOException{
		Pessoa pessoa = p.findByPassaporte("CA0140480");
		
		assertNotNull(pessoa);
		assertTrue(pessoa.getId() > 0);
		
		// passando um passaporte = 0
		assertNull(p.findByPassaporte("0"));
	
	}
	
	/**
	 * Retorna uma pessoa que possue o passado como paramentro.
	 * 
	 * Método: findCompleto
	 * 
	 * @param id
	 * 
	 * @return Pessoa
	 * @throws DAOException
	 */
	public void testFindCompleto() throws DAOException{
		Pessoa pessoa = p.findCompleto(1);
		
		assertNotNull(pessoa);
		assertTrue(pessoa.getId() == 1);
		
		pessoa = p.findCompleto(2);
		
		assertNull(pessoa);
	
	}
	
	
}
