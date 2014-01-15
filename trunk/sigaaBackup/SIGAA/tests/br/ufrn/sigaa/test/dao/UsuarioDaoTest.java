package br.ufrn.sigaa.test.dao;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.test.SigaaTestCase;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Teste Unitário da classe UsuarioDAO
 * 
 * @author Dalton
 *
 * Possíveis métodos com erros:
 * . testFindByPapel()
 * . testFindByMatricula()
 * . testFindUsuariosByPapel()
 *
 */
public class UsuarioDaoTest extends SigaaTestCase {

	UsuarioDao dao = new UsuarioDao();
	
	UnidadeDao unidade = new UnidadeDao();
	
	
	

//	/*
//	/**
//	 * Teste - findByLogin()
//	 * 
//	 * @param login
//	 * @return UsuarioGeral
//	 * @throws DAOException
//	 * 
//	 */
//	public void testFindByLogin() throws DAOException {
//		System.out.println("Teste 1");
//		UsuarioGeral u = dao.findByLogin("camilo");			//testa com um usuário que existe
//		System.out.println("Usuario: " + u.getId());
//		assertNotNull(u);
//		
//		UsuarioGeral u1 = dao.findByLogin("gggg");			//testa com um usuário que não existe
//		System.out.println("Usuario: " + u1);
//		assertNull(u1);
//		
//		// outros valores inválidos
//		assertNull(dao.findByLogin(""));
//	}
//	
//	/**
//	 * Teste - findUsuarioLeve()
//	 * 
//	 * @param id do usuario
//	 * @return UsuarioGeral
//	 * @throws DAOException
//	 * 
//	 */
//	public void testFindUsuarioLeve() throws DAOException {
//		System.out.println("Teste 2");
//		UsuarioGeral u = dao.findUsuarioLeve(3);			//testa passando um id válido
//		System.out.println("Usuario: " + u.getLogin());
//		assertNotNull(u);
//		
//		UsuarioGeral u1 = dao.findUsuarioLeve(2);			//testa passando um id que não existe
//		System.out.println("Usuario: " + u1);
//		assertNull(u1);
//		
//		// outros valores inválidos
//		assertNull(dao.findUsuarioLeve(0));
//		assertNull(dao.findUsuarioLeve(-1));
//		
//	}
//	
	/**
	 * Teste - findByPrimaryKey()
	 * Método mais otimizado do que o genérico
	 * 
	 * @param idUsuario
	 * 
	 * @return UsuarioGeral
	 * @throws DAOException
	 * 
	 */
	public void testFindByPrimaryKey() throws DAOException {
		System.out.println("Teste 3");
		Usuario u = dao.findByPrimaryKey(5);				//testa com um id do usuário válido
		System.out.println("Usuario: " + u.getLogin());
		assertNotNull(u);
		
		Usuario u1 = dao.findByPrimaryKey(2);			//testa passando um id que não existe
		System.out.println("Usuario: " + u1);
		assertNull(u1);
		
		// outros valores inválidos
		assertNull(dao.findUsuarioLeve(0));
		assertNull(dao.findUsuarioLeve(-1));
	}
//	
//	 /**
//	  * Teste - findByLogin()
//	  * 
//	  * @param login do usuário
//	  * @param apenasAtivos - se "true" retorna apenas usuários ativos  
//	  * 					- se "false" retorna todos os usuários (ativos ou não)
//	  * @param apenasAutorizados - se "true" retorna apenas usuários autorizados 
//	  * 						 - se "false" retorna todos os usuários (autorizados ou não)
//	  * 
//	  * @return UsuarioGeral
//	  * @throws DAOException
//	  * 
//	  */ 
//	public void testFindByLoginGeral() throws DAOException {
//		System.out.println("Teste 4");
//		
//		//apenasAtivos=true e apenasAutorizados=true
//		UsuarioGeral u = dao.findByLogin("severino", true, true);
//		System.out.println("Usuario: " + u.getId());
//		assertNotNull(u);
//		
//		//apenasAtivos=true e apenasAutorizados=false
//		u = dao.findByLogin("ritaa", true, false);		//esse login está inativo
//		assertNull(u);
//		
//		u = dao.findByLogin("carlos", true, false);		//esse login está ativo
//		System.out.println("Usuario: " + u.getId() + " - " + u.getLogin());
//		assertNotNull(u);
//		
//		//apenasAtivos=false e apenasAutorizados=false
//		UsuarioGeral u1 = dao.findByLogin("climerio1", false, false);
//		System.out.println("Usuario: " + u1.getId() + " - " + u1.getLogin());
//		assertNotNull(u1);
//		
//		UsuarioGeral u2 = dao.findByLogin("ggggg", false, false); //login inexistente
//		System.out.println("Usuario: " + u2);
//		assertNull(u2);
//		
//		// outros valores inválidos
//		assertNull(dao.findByLogin("", false, false));
//		
//		//apenasAtivos=false e apenasAutorizados=true - realizado no teste 1
//	}
//	
//	
//	 /**
//	  * Teste - findPendenteAutorizacao()
//	  * 
//	  * @return Collection<UsuarioGeral>
//	  * @throws DAOException
//	  * 
//	  */
//	public void testFindPendenteAutorizacao() throws DAOException {
//		Collection result = dao.findPendenteAutorizacao();
//		System.out.println("teste 5: " + result.size());
//		assertNotNull(result);
//	}
//	
//	
//	/**
//	 * Teste - findByPapel()
//	 *
//	 * @param papel
//	 * @param unidadeGeral
//	 * @param hierarquia
//	 *  
//	 * @return Collection<UsuarioGeral>
//	 * @throws DAOException
//	 *  
//	 * Não consegui testar: 
//	 * br.ufrn.arq.erros.DAOException: org.hibernate.hql.ast.QuerySyntaxException: Permissao is not mapped [select count(*) from Permissao p where p.papel = 12001 AND p.usuario.unidade = 125]
//	 * at br.ufrn.sigaa.arq.dao.UsuarioDao.findByPapel(UsuarioDao.java:245)
//	 * 
//	 */
//	public void testFindByPapel() throws DAOException {
//		System.out.println("Teste 6");
//		
//		//pega um objeto papel
//		Papel papel = new Papel(SigaaPapeis.CHEFE_DEPARTAMENTO);					
//		System.out.println("Papel: " + papel.getDescricao());
//		
//		//pega um objeto UnidadeGeral
//		UnidadeGeral unidadeGeral = unidade.findByPrimaryKey(125, Unidade.class);	
//		System.out.println("Unidade: " + unidadeGeral.getCodigoNome());
//		
//		Collection result = dao.findByPapel(papel, unidadeGeral, 125);
//		System.out.println("Administrador: :"  + result.size() );
//		assertNotNull(result);
//		
//		result = dao.findByPapel(papel, unidadeGeral, 0);
//		assertEquals(0, result.size());
//		
//		//passando valores nulos
//		result = dao.findByPapel(null, null, 0);
//		assertEquals(0, result.size());
//	}
//	
//	/**
//	 * Teste - findByNome()
//	 * 
//	 * @param nome do usuário
//	 * @param unidadeGeral
//	 *  
//	 * @return Collection
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindByNome1() throws DAOException {
//		System.out.println("Teste 7");
//		
//		//pega uma unidadeGeral
//		UnidadeGeral unidadeGeral = unidade.findByPrimaryKey(125, Unidade.class);
//		
//		Collection result = dao.findByNome("BENJAMIN RENE CALLEJAS BEDREGAL", unidadeGeral);
//		assertEquals(result.size(), 1);
//		
//		Collection result1 = dao.findByNome("DALTON DANTAS DE OLIVEIRA", unidadeGeral);
//		System.out.println("RESULTADO: " + result1.size());
//		assertEquals(result1.size(), 0);
//		
//		//result = dao.findByNome("", null);
//		//assertEquals(0, result.size());
//		
//	}
//	
//	/**
//	 * Teste - findByNome()
//	 * 
//	 * @param nome do usuário
//	 * @param somenteServidores - se "true" traz somente usuários servidores 
//	 * 							- se "false" traz todos os usuários
//	 *  
//	 * @return Collection
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindByNome2() throws DAOException {
//		System.out.println("Teste 8");
//		Collection result = dao.findByNome("DALTON DANTAS DE OLIVEIRA", true);
//		System.out.println("Resultado: " + result.size());
//		assertNotNull(result);
//		
//		result = dao.findByNome("TESTE", true);
//		assertEquals(0, result.size());
//	}
//	
//	/**
//	 * Teste - findByNome()
//	 * retorna todos os usuários de acordo com o nome passado
//	 * 
//	 * @param nome do usuário
//	 *  
//	 * @return Collection
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindByNome3() throws DAOException {
//		System.out.println("Teste 9");
//		Collection result = dao.findByNome("DALTON DANTAS DE OLIVEIRA");
//		assertNotNull(result);
//		assertEquals(result.size(), 1);
//		
//		//result = dao.findByNome("");
//		//assertEquals(0, result.size());
//	}
//	
//	
//	/**
//	 * Teste - findByMatricula()
//	 * 
//	 * @param matrícula do usuário
//	 *  
//	 * @return Collection<UsuarioGeral>
//	 * @throws DAOException
//	 *  
//	 *  Não consegui testar: 
//	 *  br.ufrn.arq.erros.DAOException: org.hibernate.QueryException: could not resolve property: 
//	 *  idServidor of: br.ufrn.sigaa.dominio.Usuario [select u from br.ufrn.sigaa.dominio.Usuario u 
//	 *  where u.idServidor in (select s.id from br.ufrn.sigaa.pessoa.dominio.Servidor s where s.siape = :SIAPE)]
//	 */
//	public void testFindByMatricula() throws DAOException {
//		System.out.println("Teste 10");
//		
//		Collection result = dao.findByMatricula("1160763");
//
//		UsuarioGeral u = (UsuarioGeral)result.iterator().next();
//		System.out.println("Resultado: " + result.size());
//		System.out.println("usuario:" + u.getNome() + " - " + u.getLogin());
//		assertNotNull(result);
//		assertEquals(result.size(), 1);
//		
//		result = dao.findByMatricula("");
//		assertEquals(0, result.size());
//	}
//
//	
//	/**
//	 * Teste - findByCpf()
//	 * 
//	 * @param cpf do usuário
//	 *  
//	 * @return Collection<UsuarioGeral>
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindByCpf() throws DAOException {
//		System.out.println("Teste 11");
//
//		Collection result = dao.findByCpf(60281570400l); // CPF de DALTON DANTAS DE OLIVEIRA
//		
//		UsuarioGeral u = (UsuarioGeral)result.iterator().next();
//		System.out.println("Resultado: " + result.size());
//		System.out.println("usuario:" + u.getNome() + " - " + u.getLogin());
//		assertNotNull(result);
//		assertEquals(result.size(), 1);
//		
//		result = dao.findByCpf(0);
//		assertEquals(0, result.size());
//	}
//	

	/**
	 * Teste - findByUnidade()
	 * 
	 * @param apenasAtivos 	- se "true" retorna apenas usuários ativos  
	 * 					   	- se "false" retorna todos os usuários (ativos ou não)
	 * @param apenasAutorizados - se "true" retorna apenas usuários autorizados 
	 * 							- se "false" retorna todos os usuários (autorizados ou não)
	 * @param nome do usuário
	 * @param unidadeGeral
	 *  
	 * @return Collection
	 * @throws DAOException
	 *  
	 */
	public void testFindByUnidadeAtivos() throws DAOException {
		System.out.println("Teste 13");
		UnidadeGeral unidadeGeral = unidade.findByPrimaryKey(125, Unidade.class);
		
		Collection<Usuario> result = dao.findByUnidade(unidadeGeral, true, false);
		Usuario u = result.iterator().next();
		System.out.println("Resultado: " + result.size());
		System.out.println("usuario:" + u.getNome() + " - " + u.getLogin());
		assertNotNull(result);
		//assertEquals(result.size(), 35);
		
		Collection<Usuario> result1 = dao.findByUnidade(unidadeGeral, true, true);
		Usuario u1 = result1.iterator().next();
		System.out.println("Resultado1: " + result1.size());
		System.out.println("usuario:" + u1.getNome() + " - " + u1.getLogin());
		assertNotNull(result1);
		
		//Collection result2 = dao.findByUnidade(unidadeGeral, false, true); // teste 12
		
		result = dao.findByUnidade(unidadeGeral, true, true);
		assertEquals(0, result.size());
		
	}

	/**
	 * Teste - findAll()
	 * 
	 * @param classe
	 *  
	 * @return Collection
	 * @throws DAOException
	 *  
	 */
	public void testFindAll() throws DAOException {
		System.out.println("Teste 14");
		
		Collection<Unidade> result = dao.findAll(Unidade.class);
		System.out.println("Resultado: " + result.size());
		assertNotNull(result);
		
//		result = dao.findAll(null);
//		assertEquals(0, result.size());
		
	}
	
//	/**
//	 * Teste - findAllByHierarquia()
//	 * 
//	 * @param unidadeGeral
//	 *  
//	 * @return Collection
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindAllByHierarquia() throws DAOException {
//		System.out.println("Teste 15");
//		
//		//pega uma unidade
//		UnidadeGeral unidadeGeral = unidade.findByPrimaryKey(125, Unidade.class);
//		Collection<?> result = dao.findAllByHierarquia(unidadeGeral); // apenas ativos = false apenas autorizados = true
//		System.out.println("Resultado: " + result.size());
//		assertNotNull(result);
//		
//		result = dao.findAllByHierarquia(null);
//		assertEquals("Está retornando todas as hierarquias da unidade", 0, result.size());
//	}

	/**
	 * Teste - findAllByHierarquia()
	 * 
	 * @param unidadeGeral
	 * @param apenasAtivos 	- se "true" retorna apenas usuários ativos  
	 * 					   	- se "false" retorna todos os usuários (ativos ou não)
	 * @param apenasAutorizados - se "true" retorna apenas usuários autorizados 
	 * 							- se "false" retorna todos os usuários (autorizados ou não)
	 *  
	 * @return Collection
	 * @throws DAOException
	 *  
	 */
	public void testFindAllByHierarquiaAtivos() throws DAOException {
		System.out.println("Teste 16");
		
		//pega uma unidade
		UnidadeGeral unidadeGeral = unidade.findByPrimaryKey(125, Unidade.class);
		
		//apenasAtivos=true e apenasAutorizados=true
		Collection<?> result = dao.findAllByHierarquia(unidadeGeral, true, true);
		System.out.println("Resultado: " + result.size());
		assertNotNull(result);
		
		//apenasAtivos=true e apenasAutorizados=false
		Collection<?> result1 = dao.findAllByHierarquia(unidadeGeral, true, false);
		System.out.println("Resultado1: " + result1.size());
		assertNotNull(result1);
		
		//teste3 - apenasAtivos=false e apenasAutorizados=false
		Collection<?> result2 = dao.findAllByHierarquia(unidadeGeral, false, false);
		System.out.println("Resultado2: " + result2.size());
		assertNotNull(result2);

		//teste3 - apenasAtivos=false e apenasAutorizados=false
		Collection<?> result3 = dao.findAllByHierarquia(unidadeGeral, false, true);
		System.out.println("Resultado3: " + result3.size());
		assertNotNull(result2);

		//apenasAtivos=false e apenasAutorizados=true ==> teste 15
		
//		result = dao.findAllByHierarquia(null, true, true);
//		assertEquals("Está retornando todas as hierarquias da unidade", 0, result.size());
		
	}

//	/**
//	 * Teste - findProponenteByMatricula()
//	 * 
//	 * @param matricula
//	 *  
//	 * @return Servidor
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindProponenteByMatricula() throws DAOException {
//		System.out.println("Teste 17");
//		Servidor result = dao.findProponenteByMatricula(1160763);
//		System.out.println("Resultado: " + result.getNome());
//		assertNotNull(result);
//		
//		assertNull(dao.findProponenteByMatricula(0));
//	}
//	
//	/**
//	 * Teste - findByNome()
//	 * 
//	 * @param nome
//	 * @param unidadeGeral
//	 * @param hierarquia
//	 *  
//	 * @return Collection<UsuarioGeral>
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindByNomeHierarquia() throws DAOException {
//		System.out.println("Teste 18");
//		
//		//pega uma unidade
//		UnidadeGeral unidadeGeral = unidade.findByPrimaryKey(2, Unidade.class);
//
//		Collection result = dao.findByNome("DALTON DANTAS DE OLIVEIRA", unidadeGeral, 2); //apenasAtivos=false, apenasAutorizados=true
//		
//		System.out.println("Resultado: " + result.size());
//		UsuarioGeral u = (UsuarioGeral)result.iterator().next();
//		System.out.println("usuario: " + u.getLogin());
//		assertNotNull(result);
//		assertEquals(result.size(), 1);
//		
//		result = dao.findByNome("", null, 2);
//		assertEquals("Consulta retornando todas as hierarquias", 0, result.size());
//		
//	}
//	/**
//	 * Teste - findByNome()
//	 * 
//	 * @param nome
//	 * @param unidadeGeral
//	 * @param hierarquia
//	 * @param apenasAtivos 	- se "true" retorna apenas usuários ativos  
//	 * 						- se "false" retorna todos os usuários (ativos ou não)
//	 * @param apenasAutorizados - se "true" retorna apenas usuários autorizados 
//	 * 						 	- se "false" retorna todos os usuários (autorizados ou não)
//	 * 
//	 * @return Collection<UsuarioGeral>
//	 * @throws DAOException
//	 * 
//	 */
//	public void testFindByNomeHierarquiaAtivos() throws DAOException {
//		
//		//O true/false serve apenas para adicionar ou não o teste.
//		
//		System.out.println("Teste 19");
//		UnidadeGeral unidadeGeral = unidade.findByPrimaryKey(2, Unidade.class);
//
//		Collection result1 = dao.findByNome("DALTON DANTAS DE OLIVEIRA", unidadeGeral, 2, true, true);
//		System.out.println("Resultado: " + result1.size());
//		UsuarioGeral u1 = (UsuarioGeral)result1.iterator().next();
//		System.out.println("usuario: " + u1.getLogin());
//		assertNotNull(result1);
//		assertEquals(result1.size(), 1);
//		
//		Collection result2 = dao.findByNome("DALTON DANTAS DE OLIVEIRA", unidadeGeral, 2, true, false);
//		System.out.println("Resultado: " + result2.size());
//		UsuarioGeral u2 = (UsuarioGeral)result2.iterator().next();
//		System.out.println("usuario: " + u2.getLogin());
//		assertNotNull(result2);
//		assertEquals(result2.size(), 1);
//		
//		//apenasAtivos=false e apenasAutorizados=true ==> teste 18
//		
//		//Collection result = dao.findByNome("", null, 0, true, true);
//		//assertEquals(0, result.size());
//		
//	}
//	
//	/**
//	 * Teste - findAllAtivos()
//	 * 
//	 * @param classe
//	 *  
//	 * @return Collection<UsuarioGeral>
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindAllAtivos() throws DAOException {
//		System.out.println("Teste 20");
//		Collection result = dao.findAll(Unidade.class);
//		System.out.println("Resultado: " + result.size()); // result = 2480
//		assertNotNull(result);
//		
//		assertNull(null);
//	}
//	
//	/**
//	 * Teste - expiraSenha()
//	 * 
//	 * @param unidadeGeral
//	 *  
//	 * @return
//	 * @throws DAOException
//	 * 
//	 *  Não consegui testar:
//	 *  br.ufrn.arq.erros.DAOException: org.postgresql.util.PSQLException: ERROR: invalid input syntax for type date: "Fri Jan 18 13:21:20 GMT-03:00 2008"
//	 *  at br.ufrn.sigaa.arq.dao.UsuarioDao.expiraSenha(UsuarioDao.java:771)
//	 *  at br.ufrn.sigaa.test.dao.UsuarioDaoTest.testExpiraSenha(UsuarioDaoTest.java:284)
//	 */
//	public void testExpiraSenha() throws DAOException {
//		System.out.println("Teste 21");
//		Collection result = dao.findByCpf(7486829452l);
//		UsuarioGeral u = (UsuarioGeral)result.iterator().next();
//		
//		dao.expiraSenha(u);
//	}
//	
//	/**
//	 * Teste - findDiscentesNaoAutorizados()
//	 * 
//	 * @return List<Usuario>
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindDiscentesNaoAutorizados() throws DAOException {
//		System.out.println("Teste 22");
//		List<Usuario> usuarios = dao.findDiscentesNaoAutorizados();
//		System.out.println("usuarios: " + usuarios.size());
//		assertTrue(usuarios.size() > 0);
//	}
//	
//	/**
//	 * Teste - findByDiscente()
//	 * 
//	 * @param discente
//	 *  
//	 * @return Usuario
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindByDiscente() throws DAOException {
//		System.out.println("Teste 23");
//		
//		//pega um discente
//		DiscenteDao discenteDao = new DiscenteDao();
//		Discente discente = discenteDao.findAtivosByMatricula(200506294, 'G');
//		
//		Usuario result = dao.findByDiscente(discente);
//		System.out.println(result.getLogin());
//		assertNotNull(result);
//		
//		// Dá erro de exceção - OK
//		Discente discente1 = discenteDao.findAtivosByMatricula(200127574, 'G');
//		Usuario result1 = dao.findByDiscente(discente1);
//		
//		assertNull(dao.findByDiscente(null));
//		
//	}
//	
//	/**
//	 * Teste - findByServidor()
//	 * 
//	 * @param servidor
//	 *  
//	 * @return Usuario
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindByServidor() throws DAOException {
//		System.out.println("Teste 24");
//		
//		//pega um servidor
//		ServidorDao servidorDao = new ServidorDao();
//		Servidor servidor = servidorDao.findByCpf(60281570400l);
//		
//		Usuario result = dao.findByServidor(servidor);
//		System.out.println(result.getLogin());
//		assertNotNull(result);
//
//		// Dá erro de exceção - OK
//		Servidor servidor1 = servidorDao.findByCpf(6858891498l);
//		Usuario result1 = dao.findByServidor(servidor1);
//		
//		assertNull(dao.findByServidor(null));
//	}
//	
//	/**
//	 * Teste - findByConsultor()
//	 * 
//	 * @param consultor
//	 *  
//	 * @return Usuario
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindByConsultor() throws DAOException {
//		System.out.println("Teste 25");
//		ConsultorDao daoConsultor = new ConsultorDao();
//		Consultor consultor = daoConsultor.findByPrimaryKey(64143, Consultor.class);
//		
//		Usuario result = dao.findByConsultor(consultor);
//		System.out.println(result.getLogin());
//		assertNotNull(result);
//		
//		assertNull(dao.findByConsultor(null));
//	}
//	
//	/**
//	 * Teste - findUsuariosBypapel()
//	 * 
//	 * @param papel
//	 *  
//	 * @return List<Usuario>
//	 * @throws DAOException
//	 *  
//	 * Erro: 
//	 * br.ufrn.arq.erros.DAOException: org.hibernate.hql.ast.QuerySyntaxException: Permissao is not mapped [select usr from br.ufrn.sigaa.dominio.Usuario usr, Permissao per where usr.id = per.usuario.id and per.papel.id = :papel order by usr.pessoa.nome]
//	 * at br.ufrn.sigaa.arq.dao.UsuarioDao.findUsuariosByPapel(UsuarioDao.java:829)
//	 * 
//	 */
//	public void testFindUsuariosByPapel() throws DAOException {
//		System.out.println("Teste 26");
//		Papel papel = new Papel(SigaaPapeis.CHEFE_DEPARTAMENTO);
//		
//		List usuarios = dao.findUsuariosByPapel(papel);
//		System.out.println("lista: " + usuarios.size());
//		Usuario usuario = (Usuario)usuarios.iterator().next();
//		System.out.println("usuario: " + usuario.getLogin());
//		assertNotNull(usuario);
//		
//		usuarios = dao.findUsuariosByPapel(null);
//		assertEquals(0, usuarios.size());
//	}
//
//	/**
//	 * Teste - findByDocenteExterno()
//	 * 
//	 * @param id_docenteExterno
//	 *  
//	 * @return Usuario
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindByDocenteExterno() throws DAOException {
//		System.out.println("Teste 27");
//		Usuario usuario = dao.findByDocenteExterno(243514);
//		System.out.println("Usuario: " + usuario.getLogin());
//		assertNotNull(usuario);
//		
//		assertNull(dao.findByDocenteExterno(0));
//	}
//
//	/**
//	 * Teste - findByTutor()
//	 * 
//	 * @param tutor
//	 *  
//	 * @return Usuario
//	 * @throws DAOException
//	 *  
//	 */
//	public void testFindByTuTor() throws DAOException {
//		System.out.println("Teste 28");
//		
//		//pega um tutor
//		TutorOrientadorDao tutorDao = new TutorOrientadorDao();
//		TutorOrientador tutor = tutorDao.findByPrimaryKey(2678170, TutorOrientador.class);
//		
//		Usuario usuario = dao.findByTuTor(tutor);
//		System.out.println("Usuario: " + usuario.getLogin());
//		assertNotNull(usuario);
//		
//		assertNull(dao.findByTuTor(null));
//	}
//	*/
}
