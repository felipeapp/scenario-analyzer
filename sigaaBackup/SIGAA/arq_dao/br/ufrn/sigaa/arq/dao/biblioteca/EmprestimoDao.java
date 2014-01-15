/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/09/2008
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.OperacoesDesfeitasDesktop;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * DAO que busca por empr�stimos dos usu�rio do sistema da biblioteca.
 *
 * OBS.: Empr�stimos ATIVOS    = n�o estornados
 *       Empr�stimos EM ABERTO = n�o estornados e ainda n�o devolvidos pelo usu�rio.
 *
 * @author jadson
 * @since 24/09/2008
 * @version 1.0 cria��o da classe
 *
 */
public class EmprestimoDao  extends GenericSigaaDAO{
	
	/**
	 * A quantidade m�xima de empr�stimos institucionais recuperados por vez.
	 */
	public static final int LIMITE_BUSCA_EMPRESTIMOS_INSTITUCIONAIS = 30;
	
	
	
	/**
	 *    <p>M�todo que retorna apenas os ids dos materiais principais dos materiais emprestados aos usu�rios (desconsidera os anexos). </p>
	 *    
	 *    <p> Se o material n�o for anexo a lista vai contar ele pr�prio, tem l�gica. Se o material for um anexo a lista vai conter o id do principal dele.
	 *    Caso o usu�rio possua apenas 1 anexo, vai cont�m 1 id do principal   </p> 
	 *    
	 *    
	 * @param idMateriaisAtualmenteEmprestados   os ids dos materiais que est�o empr�stados ao usu�rio (inclui o que est� sendo emprestado atualmente).
	 * @param politicaEmprestimo   a pol�tica que o usu�rio est� querendo utilizar para realizar o empr�stimo neste momento.
	 * 
	 * @returnos ids dos materiais que est�o empr�stados ao usu�rio E N�O S�O ANEXOS.
	 * @throws DAOException
	 */
	public Set<Integer> getIdsMateriaisDescontandoAnexosQuandoPrincipalEstaEmprestado(List<Integer> idMateriaisAtualmenteEmprestadosParaMesmaPolitica) throws DAOException{
		
		Set<Integer> idMateriaisEmprestadosNaoDuplicados = new HashSet<Integer>();
		
		// busca no banco a informa��es de quais dos materiais s�o anexos para as informa��es da pol�tica usada //
		String sql =" SELECT distinct exemplar.id_exemplar, exemplar.id_exemplar_de_quem_sou_anexo "
			+" FROM biblioteca.exemplar exemplar "
			+" WHERE exemplar.id_exemplar in ( :idMateriais ) ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setParameterList("idMateriais", idMateriaisAtualmenteEmprestadosParaMesmaPolitica);  // dos materiais que est�o emprestados 
		
	
		@SuppressWarnings("unchecked")
		List<Object[]> idExemplaresEmprestados = q.list();
		
		
		
		for (Object[] objects : idExemplaresEmprestados) {
			if(objects[1] == null){  // O material � um material principal
				
				idMateriaisEmprestadosNaoDuplicados.add( (Integer) objects[0]); //  adiciona o id dele na lista
				
			}else{ // Se o material � anexo
				
				if(idMateriaisAtualmenteEmprestadosParaMesmaPolitica.contains((Integer) objects[1] )){ // se o principal t� emprestado.
					idMateriaisEmprestadosNaoDuplicados.add( (Integer) objects[1]);  // coloca o principal dele na lista, desconsidera o anexo !!!!
				}else{
					idMateriaisEmprestadosNaoDuplicados.add( (Integer) objects[0]); // coloca todos os anexos para contar como materiais normais.
				}				
				
				
			}
		}
		
	
		
		sql =" SELECT distinct fasciculo.id_fasciculo, fasciculo.id_fasciculo_de_quem_sou_suplemento "
			+" FROM biblioteca.fasciculo fasciculo "
			+" WHERE fasciculo.id_fasciculo in ( :idMateriais ) ";
	
		
		q = getSession().createSQLQuery(sql);
		
		q.setParameterList("idMateriais", idMateriaisAtualmenteEmprestadosParaMesmaPolitica);  // dos materiais que est�o emprestados 
		
		@SuppressWarnings("unchecked")
		List<Object[]> idFasciculosEmprestados = q.list();
		
		// S� executa esse c�digo se os materiais foram fasc�culos 
		
		for (Object[] objects : idFasciculosEmprestados) {
			if(objects[1] == null){  // O material � um material principal
					             
				idMateriaisEmprestadosNaoDuplicados.add( (Integer) objects[0]); // e adiciona ele na lista
				
				
			}else{ // Se o material � suplemento
				
				if(idMateriaisAtualmenteEmprestadosParaMesmaPolitica.contains((Integer) objects[1] )){ // se o principal t� emprestado.
					
					idMateriaisEmprestadosNaoDuplicados.add( (Integer) objects[1]);  // coloca o principal dele na lista, desconsidera o suplemento !!!!
				}else{
					
					idMateriaisEmprestadosNaoDuplicados.add( (Integer) objects[0]); // coloca todos os suplementos para contar como materiais normais.
				}			
			}
		}
		
		return idMateriaisEmprestadosNaoDuplicados;
	}
	
	
	
	
	
	/**
	 *    <p>M�todo que conta todos os empr�timos do usu�rio que est�o associados com o v�nculo passado. </p>
	 *    <p>O tipo de usu�rio na pol�tica de empr�stimos == tipoUsuarioBiblioteca e identificacaoUsuario == idVinculo </p>
	 *    
	 * @param usuarioBiblioteca
	 * @param idVinculo
	 * @param tipoUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public int countEmprestimosTotaisPorVinculoUsuario(UsuarioBiblioteca usuarioBiblioteca) throws DAOException{
		
		String hql = " SELECT count(e.id) FROM Emprestimo e WHERE e.usuarioBiblioteca.id = :idUsuarioBiblioteca ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuarioBiblioteca", usuarioBiblioteca.getId());

		return ((Long) q.uniqueResult()).intValue();
	}
	
	
	/**
	 *    <p>M�todo que conta todos os empr�timos do usu�rio que est�o associados com o v�nculo passado. </p>
	 *    <p>O tipo de usu�rio na pol�tica de empr�stimos == tipoUsuarioBiblioteca e identificacaoUsuario == idVinculo </p>
	 *    
	 * @param usuarioBiblioteca
	 * @param idVinculo
	 * @param tipoUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public int countEmprestimosDoMaterial(int idMaterial) throws DAOException{
		
		String hql = " SELECT count(e.id) FROM Emprestimo e "
			+" WHERE e.material.id = :idMaterial " 
			+" AND e.situacao in ("+Emprestimo.EMPRESTADO+", "+Emprestimo.DEVOLVIDO+")";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idMaterial", idMaterial);

		return ((Long) q.uniqueResult()).intValue();
	}
	
	
	
	/**
	 *    <p>M�todo que conta a quantidade de empr�stimos ativos de um usu�rio cujas pol�ticas de 
	 * empr�stimos s�o do v�nculo do usu�rio passado. </p>
	 *  
	 *    <p><i> Geralmente este m�todo � usado para saber se o usu�rio pode ter um v�nculo espec�fico finalizado. </i></p>
	 *    
	 *
	 * @param usuarioBiblioteca
	 * @param idVinculo
	 * @param tipoUsuarioBiblioteca
	 * @return
	 * @throws DAOException 
	 * 
	 * @see {@link EmprestimoDao#findEmprestimosAtivosPorVinculoUsuario(UsuarioBiblioteca, int, int)} 
	 */
	public Long countEmprestimosAtivosPorVinculoUsuario(int idPessoa, VinculoUsuarioBiblioteca vinculo, int identificacaoVinculo) throws DAOException{
		
		String hql = " SELECT count( distinct e.id )" 
		+" FROM Emprestimo e "
		+" WHERE e.usuarioBiblioteca.pessoa.id = :idPessoa "
		+" AND e.usuarioBiblioteca.vinculo = "+vinculo
		+" AND e.usuarioBiblioteca.identificacaoVinculo = :identificacaoVinculo "
		+" AND e.situacao = "+Emprestimo.EMPRESTADO +"  AND e.ativo = trueValue() ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idPessoa", idPessoa);
		q.setInteger("identificacaoVinculo", identificacaoVinculo);
		
		return (Long) q.uniqueResult();
		
	}
	
	
	
	
	/**
	 *    <p>M�todo que retorna todos os empr�timos ativos do usu�rioBiblioteca. </p>
	 *    <p>Cada objeto UsuarioBiblioteca representa um v�nculo que o usu�rio tem ou teve no sistema. Apenas um vai est� ativo um v�nculo por vez</p>
	 *    
	 *    
	 *    <p>Utilizado geralmente para visualizar os empr�stimos de um usu�rio por v�nculo.</p>
	 *    
	 * @param usuarioBiblioteca
	 * @param idVinculo
	 * @param tipoUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 * 
	 */
	public List<Emprestimo> findEmprestimosAtivosPorVinculoUsuario(UsuarioBiblioteca usuarioBiblioteca) throws DAOException{
		
		String projecao = " e.id, e.dataEmprestimo, e.prazo, e.dataDevolucao, po.quantidadeRenovacoes, po.tipoPrazo, e.material.codigoBarras, e.material.biblioteca.descricao ";
		
		String hql = "SELECT "+projecao+" FROM Emprestimo e "
		+" INNER JOIN e.politicaEmprestimo po "
		+" WHERE e.usuarioBiblioteca.id = :idUsuarioBiblioteca AND e.situacao = "+Emprestimo.EMPRESTADO;
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuarioBiblioteca", usuarioBiblioteca.getId());

		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		                            
		// HibernateUtils.parseTo nao funciona por causa do instanciacao de material
		
		List <Emprestimo> emprestimosTemp = new ArrayList<Emprestimo>();
		
		for (Object[] objects : linhas) {
			Emprestimo e = new Emprestimo();
			e.setId( ( Integer ) objects[0]);
			e.setDataEmprestimo( ( Date ) objects[1]);
			e.setPrazo( ( Date ) objects[2]);
			e.setDataDevolucao( ( Date ) objects[3]);
			
			PoliticaEmprestimo po = new PoliticaEmprestimo();
			po.setQuantidadeRenovacoes((Integer )objects[4]);
			po.setTipoPrazo( (Short )objects[5]) ;
			e.setPoliticaEmprestimo(po);
			Exemplar ex = new Exemplar();   // so para pegar a informacao do codigo de barras
			
			ex.setCodigoBarras((String) objects[6]);
			ex.setBiblioteca( new Biblioteca((String) objects[7]));
			e.setMaterial( ex );
			
			emprestimosTemp.add(e);
		}
		
		
		List <Emprestimo> emprestimos = new ArrayList<Emprestimo>();
		
		for (Emprestimo e : emprestimosTemp){
				e.setDataRenovacao(BibliotecaUtil.getDataUltimaRenovacao(e));
				e.setQuantidadeRenovacoes(BibliotecaUtil.getQuantidadeRenovacoesEmprestimo(e));
				emprestimos.add(e);
		}
		
		return emprestimos;
	}
	
	
	
	
	
	
	
	
	
	/**
	 *   M�todo que encontra a matr�cula do discente usado no empr�stimo
	 *
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public Long findMatriculaDiscenteRealizouEmprestimo(int idEmprestimo) throws DAOException {
		
		// Pega a identifica��o do usu�rio da �ltima prorroga��o por renova��o, se existir //
		
		String hql = " SELECT e.usuarioBiblioteca.identificacaoVinculo FROM Emprestimo e  " 
		 +" WHERE e.id = :idEmprestimo AND e.usuarioBiblioteca.vinculo in "+UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosAluno());
		
		Query q = getSession().createQuery(hql);
		q.setMaxResults(1);
		q.setInteger("idEmprestimo", idEmprestimo);                      
		
		Integer idDiscente = (Integer) q.uniqueResult();
		
		if(idDiscente != null){
			hql = " SELECT matricula  FROM Discente WHERE id = :idDiscente "; 
			q = getSession().createQuery(hql.toString());
			q.setInteger("idDiscente", idDiscente);                      
			return (Long) q.uniqueResult();
		}
		
		return null;
	}
	
	/**
	 * Descobre o siape do usu�rio que realizou o empr�stimo, se este for servidor.
	 * 
	 * @param idEmprestimo
	 * @return
	 * @throws DAOException
	 */
	public Integer findSiapeServidorRealizouEmprestimo(int idEmprestimo) throws DAOException {

		// Pega a identifica��o do usu�rio da �ltima prorroga��o por renova��o, se existir //
		
		String hql = " SELECT e.usuarioBiblioteca.identificacaoVinculo FROM Emprestimo e  " 
			 +" WHERE e.id = :idEmprestimo AND e.usuarioBiblioteca.vinculo in "+UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosServidor());
		
		
		Query q = getSession().createQuery(hql.toString());
		q.setMaxResults(1);
		q.setInteger("idEmprestimo", idEmprestimo);                      
		
		Integer idServidor = (Integer) q.uniqueResult();
		
		if(idServidor != null){
			
			hql = " SELECT siape FROM Servidor WHERE id = :idServidor ";
			q = getSession().createQuery(hql.toString());
			q.setInteger("idServidor", idServidor); 
			return (Integer) q.uniqueResult();
			
		}
		
		return null;
		
	}
	
	
	
	/**
	 *      M�todo que registra as opera��es desfeitas no desktop para alguma poss�vel auditoria, j� 
	 *  que � uma opera��o cr�tica do sistema, que pode gerar conflitos se o usu�rio esquecer que a 
	 *  devolu��o foi desfeita e querer alegar que devolveu o material.
	 *
	 * @param e
	 * @param idOperador
	 * @param idAutorizador
	 * @param operacao
	 * @throws DAOException
	 */
	public void registraOperacaoDesfeita(Emprestimo e,  int idOperador, int idAutorizador, String operacao) throws DAOException{
		
		OperacoesDesfeitasDesktop operacaoDesfeita = new OperacoesDesfeitasDesktop();
	
		operacaoDesfeita.setEmprestimo(e);
		operacaoDesfeita.setOperador(new Usuario(idOperador));
		operacaoDesfeita.setAutorizador(new Usuario(idAutorizador));
		operacaoDesfeita.setOperacao(operacao);
		
		this.create(operacaoDesfeita);
	}
	
	
	/**
	 *   <p>Conta a quantidade de empr�stimos ativos do usu�rio biblioteca passado.</p>
	 *
	 *   <p>N�o interessa o tipo de v�nculo que ele tenha nessa conta da biblioteca.</p> 
	 *   
	 * @param usuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public Integer countEmprestimosAtivosByUsuario(UsuarioBiblioteca usuarioBiblioteca) throws DAOException{
		Criteria c = getSession().createCriteria(Emprestimo.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		
		if (usuarioBiblioteca != null)
			c.add(Restrictions.eq("usuarioBiblioteca.id", usuarioBiblioteca.getId()));
			
		c.add(Restrictions.eq("situacao", Emprestimo.EMPRESTADO));
		c.add(Restrictions.eq("ativo", true));
		
		return (Integer) c.list().get(0);
	}
	
	
	
	/**
	 * Retorna todos os empr�stimos de um usu�rio da biblioteca, dada a situa��o (devolvidos, n�o devolvidos, ambos) e o per�odo.<br><br>
	 * 
	 * Os atributos nulos ou zerados n�o ser�o utilizados na filtragem do resultado.<br><br>
	 * 
	 * @param usuarioBiblioteca
	 * @param materialInformacional
	 * @param biblioteca
	 * @param devolvidos
	 * @param podeRenovar
	 * @param atrasados
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 * 
	 */
	public List <Emprestimo> findEmprestimosAtivosByUsuarioMaterialBiblioteca (UsuarioBiblioteca usuarioBiblioteca,
			MaterialInformacional materialInformacional, Biblioteca biblioteca, Boolean devolvidos, Boolean podeRenovar,
			Boolean atrasados, Date dataInicio, Date dataFim, Integer limit) throws DAOException{

		Criteria c = getSession().createCriteria(Emprestimo.class);
		
		if (usuarioBiblioteca != null)
			if (usuarioBiblioteca.getId() > 0)
				c.add(Restrictions.eq("usuarioBiblioteca.id", usuarioBiblioteca.getId()));
			else if (usuarioBiblioteca.getBiblioteca() != null) {
				if (usuarioBiblioteca.getBiblioteca().getUnidade() != null)
					c.createCriteria("usuarioBiblioteca").add(Restrictions.isNotNull("biblioteca")).createCriteria("biblioteca").add(Restrictions.isNotNull("unidade") );
				else
					c.createCriteria("usuarioBiblioteca").add(Restrictions.isNotNull("biblioteca")).createCriteria("biblioteca").add(Restrictions.isNull("unidade") );
			}
		
		if (materialInformacional != null && materialInformacional.getId() > 0)
			c.add(Restrictions.eq("material.id", materialInformacional.getId()));
		
		if (biblioteca != null && biblioteca.getId() > 0)
			c.createCriteria("material").add(Restrictions.eq("biblioteca.id", biblioteca.getId()));
		
		if (atrasados != null){
			// Se o empr�stimo deve estar atrasado.
			if (atrasados){
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_MONTH, 1);
				
				c.add(Restrictions.le("prazo", CalendarUtils.configuraTempoDaData(cal.getTime(),0,0,0,0)));
				c.add(Restrictions.isNull("dataDevolucao")); // N�o pode ter sido devolvido.
			} else { // Se n�o pode estar atrasado.
				c.add(Restrictions.ge("prazo", CalendarUtils.configuraTempoDaData(new Date(),0,0,0,0)));
				c.add(Restrictions.isNull("dataDevolucao")); // N�o pode ter sido devolvido.
			}
		}
		
		if (devolvidos != null){
			if (devolvidos) // Se foi devolvido.
				c.add(Restrictions.isNotNull("dataDevolucao"));
			else // Se n�o foi devolvido.
				c.add(Restrictions.isNull("dataDevolucao"));
		}
			
		if (dataInicio != null)
			c.add(Restrictions.ge("dataEmprestimo", dataInicio));
		
		if (dataFim != null){
			// A data fim vem com a hora igual a 00:00:00 ...
			// Se um empr�stimo for no mesmo dia, mas com a hora maior que 00:00:00, n�o ser� listado
			// Para corrigir isso, adiciona-se um dia � dataFim.
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFim);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			
			c.add(Restrictions.le("dataEmprestimo", cal.getTime()));
		}
		
		if (limit != null)
			c.setMaxResults(limit);

		c.add(Restrictions.eq("ativo", true));
		c.addOrder(Order.desc("dataEmprestimo"));
		

		@SuppressWarnings("unchecked")
		List <Emprestimo> emprestimosTemp =  c.list();
		
		List <Emprestimo> emprestimos = new ArrayList<Emprestimo>();
		
		for (Emprestimo e : emprestimosTemp){
			
			if (podeRenovar != null && podeRenovar){ // retorna somente os empr�stimos que podem ser renovados
				
				e.setDataRenovacao(BibliotecaUtil.getDataUltimaRenovacao(e));
				e.setQuantidadeRenovacoes(BibliotecaUtil.getQuantidadeRenovacoesEmprestimo(e));
				if(e.podeRenovar())
					emprestimos.add(e);
				
			}else{
				e.setDataRenovacao(BibliotecaUtil.getDataUltimaRenovacao(e));
				e.setQuantidadeRenovacoes(BibliotecaUtil.getQuantidadeRenovacoesEmprestimo(e));
				emprestimos.add(e);
			}
		}
		
		return emprestimos;
	}
	
	
	/**
	 * <p>Retorna todos os empr�stimos ativos de um usu�rio da biblioteca com as informa��es extritamentes necess�rias 
	 * para saber se o empr�timos est� atrazado ou n�o.</p>
	 * 
	 * <p> M�todo utilizado para saber se o usu�rio possui apenas empr�stimos ativos ou se eles est�o atrasados</p>
	 * 
	 * @param usuarioBiblioteca
	 * @param materialInformacional
	 * @param biblioteca
	 * @param devolvidos
	 * @param podeRenovar
	 * @param atrasados
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public List <Emprestimo> findEmprestimosAtivosByUsuarioComInfoExtritamenteNecessarias(UsuarioBiblioteca usuarioBiblioteca) throws DAOException{

		String projections = " e.dataDevolucao, e.dataEstorno, e.prazo, e.politicaEmprestimo.tipoPrazo ";
		
		String hql = " SELECT"+projections
		+ " FROM Emprestimo e "
		+ " WHERE e.usuarioBiblioteca.id = :idUsuarioBiblioteca "
		+ " AND e.situacao = "+Emprestimo.EMPRESTADO;
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuarioBiblioteca", usuarioBiblioteca.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		return new ArrayList<Emprestimo>( HibernateUtils.parseTo(linhas, projections, Emprestimo.class, "e"));
		
	
	}
	
	

	/**
	 * Encontra o empr�stimo ativo do material cujo idMaterial � o passado.
	 * @param IdMaterial
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	public Emprestimo findEmAbertoByIdMaterial(int idMaterial) throws DAOException {
		
		Criteria c = getSession().createCriteria(Emprestimo.class);
		
		c.add(Restrictions.eq("material.id", idMaterial));

		c.add(Restrictions.isNull("dataDevolucao"));
		c.add(Restrictions.isNull("dataEstorno"));
		c.add(Restrictions.eq("ativo", true));
		c.setMaxResults(1);
		
		return (Emprestimo) c.uniqueResult();
	}
	
	/**
	 *    Encontra o prazo de devolu��o de um material geralmente usado para exibir 
	 * para o usu�rio.
	 * 
	 * @param IdMaterial
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	public Date findPrazoDevolucaoMaterial(int idMaterial) throws DAOException {
		
		Criteria c = getSession().createCriteria(Emprestimo.class);
		c.setProjection(Projections.property("prazo"));
		
		c.add(Restrictions.eq("material.id", idMaterial));
		c.add(Restrictions.isNull("dataDevolucao"));
		c.add(Restrictions.isNull("dataEstorno"));
		c.add(Restrictions.eq("ativo", true));
		
		c.setMaxResults(1);
		
		return (Date) c.uniqueResult();
	}
	
	/**
	 *
	 * Encontra o empr�stimo ativo do material cujo c�digo de barras � o passado.
	 * 
	 * @param codigoBarras
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	public Emprestimo findEmAbertoByCodigoBarras (String codigoBarras) throws DAOException {
		
		Criteria c = getSession().createCriteria(Emprestimo.class);
		
		Criteria c2 = c.createCriteria("material");
		c2.add(Restrictions.eq("codigoBarras", codigoBarras));

		c.add(Restrictions.isNull("dataDevolucao"));
		c.add(Restrictions.eq("ativo", true));
		c.setMaxResults(1);
		
		return (Emprestimo) c.uniqueResult();
	}	


	/**
	 * <p>Retorna  a data do �ltimo empr�stimo ativo do material passado feito pelo usu�rio passado.</p>
	 * 
	 * <p> Utilizado para verificar a regra do tempo m�nimo entre empr�stimos, caso ela esteja ativa.</p>
	 * 
	 * @param usuarioBiblioteca
	 * @param material
	 * @return
	 * @throws DAOException 
	 */
	public Date findDataDevolucaoUltimoEmprestimoAtivoByUsuarioMaterial(UsuarioBiblioteca usuarioBiblioteca, MaterialInformacional material) throws DAOException {

		Criteria c = getSession().createCriteria(Emprestimo.class);
		
		c.setProjection(Projections.property("dataDevolucao"));
		
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("usuarioBiblioteca.id", usuarioBiblioteca.getId()));
		c.add(Restrictions.eq("material.id", material.getId()));
		c.addOrder(Order.desc("dataEmprestimo"));
		c.setMaxResults(1);
		
		return (Date) c.uniqueResult();
	}

	
	
	/**
	 * <p> M�TODO USADO PARA BUSCAR OS EMPR�STIMOS INSTITUCIONAIS </p>
	 * <p>Retorna os empr�stimos realizados para uma biblioteca espec�fica ou os empr�stimos para todas as bibliotecas.</p>
	 * 
	 * @param usuarioBiblioteca O UsuarioBiblioteca da biblioteca
	 * @param situacao
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException 
	 */
	public Collection<Emprestimo> findEmprestimosParaBiblioteca(UsuarioBiblioteca usuarioBiblioteca, Boolean situacao, Date dataInicio, Date dataFim, boolean emprestimosBibliotecasInternas) throws DAOException {
		
		Criteria c = getSession().createCriteria(Emprestimo.class);
		
		if (usuarioBiblioteca != null)
			if (usuarioBiblioteca.getId() > 0)
				c.add(Restrictions.eq("usuarioBiblioteca.id", usuarioBiblioteca.getId()));
			else if (usuarioBiblioteca.getBiblioteca() != null) {
				if (usuarioBiblioteca.getBiblioteca().getUnidade() != null)
					c.createCriteria("usuarioBiblioteca").add(Restrictions.isNotNull("biblioteca")).createCriteria("biblioteca").add(Restrictions.isNotNull("unidade") );
				else
					c.createCriteria("usuarioBiblioteca").add(Restrictions.isNotNull("biblioteca")).createCriteria("biblioteca").add(Restrictions.isNull("unidade") );
			}
		
		if (situacao != null){
			if (situacao) // Se foi devolvido.
				c.add(Restrictions.isNotNull("dataDevolucao"));
			else // Se n�o foi devolvido.
				c.add(Restrictions.isNull("dataDevolucao"));
		}
			
		if (dataInicio != null)
			c.add(Restrictions.ge("dataEmprestimo", dataInicio));
		
		if (dataFim != null){
			// A data fim vem com a hora igual a 00:00:00 ...
			// Se um empr�stimo for no mesmo dia, mas com a hora maior que 00:00:00, n�o ser� listado
			// Para corrigir isso, adiciona-se um dia � dataFim.
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFim);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			
			c.add(Restrictions.le("dataEmprestimo", cal.getTime()));
		}

		c.add(Restrictions.eq("ativo", true));
		c.addOrder(Order.desc("dataEmprestimo"));

		// Se for buscar empr�stimos n�o ativos, limit a resultado porque pode ficar pesado a consulta
		if (situacao == null || situacao == false){
			c.setMaxResults(LIMITE_BUSCA_EMPRESTIMOS_INSTITUCIONAIS);
		}
		
		@SuppressWarnings("unchecked")
		List <Emprestimo> emprestimosTemp =  c.list();
		
		List <Emprestimo> emprestimos = new ArrayList<Emprestimo>();
		
		// Verifica as renova��es do usu�rio
		for (Emprestimo e : emprestimosTemp){
			e.setDataRenovacao(BibliotecaUtil.getDataUltimaRenovacao(e));
			e.setQuantidadeRenovacoes(BibliotecaUtil.getQuantidadeRenovacoesEmprestimo(e));
			emprestimos.add(e);
		}
		
		return emprestimos;
	}

	
	
	/**
	 * Retorna os empr�stimos para um usu�rio ou os empr�stimos para todos os usu�rios.
	 * 
	 * @param usuarioBiblioteca
	 * @param devolvidos
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public List<Emprestimo> findEmprestimosByUsuarioSituacaoPeriodo(UsuarioBiblioteca usuarioBiblioteca, Boolean devolvidos, Date dataInicio, Date dataFim) throws DAOException {

		if (usuarioBiblioteca == null){
			usuarioBiblioteca = new UsuarioBiblioteca();
			
			// COMENTADO PORQUE USUARIO BIBLIOTECA N�O TEM MAIS USU�RIO  //
			//.setUsuario(new Usuario());
		}
		
		return findEmprestimosAtivosByUsuarioMaterialBiblioteca(usuarioBiblioteca, null, null, devolvidos, null, null, dataInicio, dataFim, null);
	}

	/**
	 * 
	 *    Encontra empr�stimos de um material em um per�odo de tempo passado. 
	 *
	 * @param materialCatalografico
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public Collection<Emprestimo> findEmprestimosByMaterial(MaterialInformacional materialCatalografico, Date dataInicio, Date dataFim, Integer limit) throws DAOException {
		return findEmprestimosAtivosByUsuarioMaterialBiblioteca(null, materialCatalografico, null, null, null, null, dataInicio, dataFim, limit);
	}


	
	
	/**
	 * Encontra o empr�stimo do material, ap�s a busca pelo c�digo de barras.
	 * 
	 * @param id
	 * @param buscarMaterial informa se � necess�rio buscar as informa��es do material do empr�stimo
	 * @return
	 * @throws DAOException
	 */
	public Emprestimo findEmprestimoCirculacao(int idMaterial, boolean buscarMaterial, boolean buscarUsuario) throws DAOException {

		int auxBM = buscarUsuario ? 16 : 10;
		
		//  de 1 ao 8
		String sql = "select e.id_emprestimo, pe.id_politica_emprestimo, pe.tipo_prazo, te.id_tipo_emprestimo, te.descricao, e.data_emprestimo, e.prazo, e.data_devolucao ";
		
		
		// de 9 ao 15
		if( buscarUsuario ){
			sql += ", ub.id_usuario_biblioteca, p.id_pessoa, p.nome, p.cpf_cnpj, b2.id_biblioteca, b2.descricao, b2.identificador ";
		}
		
		// se buscar usu�rio:  de 16 ao 23
		// se n�o busca usu�rio: de 10 ao 17
		if( buscarMaterial ){
			sql += ", ex.id_exemplar, m.id_material_informacional, b.id_biblioteca, u.id_unidade, m.codigo_barras, c.id_titulo_catalografico, c.autor, c.titulo ";
		}	
		
		sql += " from biblioteca.emprestimo e " +
			   " join biblioteca.politica_emprestimo pe on pe.id_politica_emprestimo = e.id_politica_emprestimo " +
			   " join biblioteca.tipo_emprestimo te on te.id_tipo_emprestimo = pe.id_tipo_emprestimo ";
		
		if( buscarMaterial){
			sql +=	"join biblioteca.material_informacional m on m.id_material_informacional = e.id_material " +
					"left join biblioteca.exemplar ex on ex.id_exemplar = m.id_material_informacional " +
					"left join (biblioteca.fasciculo f join biblioteca.assinatura a on a.id_assinatura = f.id_assinatura) on f.id_fasciculo = m.id_material_informacional " +
					"join biblioteca.biblioteca b on b.id_biblioteca = m.id_biblioteca " +
					"join comum.unidade u on u.id_unidade = b.id_unidade " +
					"join biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = coalesce (a.id_titulo_catalografico, ex.id_titulo_catalografico) ";
		}
		
		if(buscarUsuario ){
			sql +=		"join biblioteca.usuario_biblioteca ub on ub.id_usuario_biblioteca = e.id_usuario_biblioteca " +
						"left join comum.pessoa p on ub.id_pessoa = p.id_pessoa " +
						"left join biblioteca.biblioteca b2 on (b2.id_biblioteca = ub.id_biblioteca) ";
		}
		
		sql += "where e.id_material = ? and e.ativo = true and e.data_devolucao is null "+BDUtils.limit(1);
		
		Emprestimo e = null;
		Connection con = null;
		
		try{
			
			con = Database.getInstance().getSigaaConnection();
			PreparedStatement prepared = con.prepareStatement(sql);
			
			prepared.setInt(1, idMaterial);
			
			ResultSet resultSet = prepared.executeQuery();
			
			
			int cont = 1;
			
			while (resultSet.next()) { 
				
				e = new Emprestimo();
				
				////// Informa��es b�sicas do empr�stimo  ///////
				
				e.setId(resultSet.getInt(cont++));
				
				e.setPoliticaEmprestimo(new PoliticaEmprestimo(resultSet.getInt(cont++)));
				e.getPoliticaEmprestimo().setTipoPrazo( resultSet.getShort(cont++));
				
				e.getPoliticaEmprestimo().setTipoEmprestimo(new TipoEmprestimo ());
				e.getPoliticaEmprestimo().getTipoEmprestimo().setId( resultSet.getInt(cont++) );
				e.getPoliticaEmprestimo().getTipoEmprestimo().setDescricao( resultSet.getString(cont++) );
				
				e.setDataEmprestimo(resultSet.getTimestamp(cont++));
				e.setPrazo(resultSet.getTimestamp(cont++));
				e.setDataDevolucao(resultSet.getTimestamp(cont++));
				
				
				if (buscarUsuario){
					UsuarioBiblioteca u = new UsuarioBiblioteca();
					u.setId( resultSet.getInt(cont++));
					Integer idPessoa = resultSet.getInt(cont++);
					
					if(idPessoa != null && idPessoa > 0){
						Pessoa p = new Pessoa(idPessoa);
						p.setNome(resultSet.getString(cont++));
						
						Long cpf = resultSet.getLong(cont++);
						
						if(cpf != null) // ALGUMAS PESSOAS N�O TEM CPF, � IMPORTANTE TESTAR ISSO
							p.setCpf_cnpjString( cpf.toString());
						
						u.setPessoa(p);
					}else{
						cont+=2;  // pula nome e CPF da pessoa
						Biblioteca b = new Biblioteca();
						b.setId(resultSet.getInt(cont++)); 
						b.setDescricao(resultSet.getString(cont++));
						b.setIdentificador(resultSet.getString(cont++));
						
						u.setBiblioteca(b); 
					}
					
					e.setUsuarioBiblioteca(u);
				}
				
				if (buscarMaterial){
					MaterialInformacional m = null;
					
					Integer idExemplar = resultSet.getInt(auxBM);
					
					if (idExemplar != null && idExemplar > 0){ // � um exemplar
						Exemplar ex = new Exemplar();
						ex.setTituloCatalografico(new TituloCatalografico(resultSet.getInt(auxBM+5)));
						m = ex;
					} else {
						Fasciculo f = new Fasciculo ();
						f.setAssinatura(new Assinatura());
						f.setTituloCatalografico(new TituloCatalografico(resultSet.getInt(auxBM+5)));
						m = f;
					}
					
					m.setId(resultSet.getInt(auxBM+1));
	
					// Configura os dados catalogr�ficos do t�tulo do material devolvido. (Usados na impress�o do comprovante)
					CacheEntidadesMarc cache = new CacheEntidadesMarc();
					cache.setIdTituloCatalografico(resultSet.getInt(auxBM+5));
					cache.setAutor(resultSet.getString(auxBM+6));
					cache.setTitulo(resultSet.getString(auxBM+7));
					m.getTituloCatalografico().setCache(cache);
					
					m.setBiblioteca(new Biblioteca(resultSet.getInt(auxBM+2)));
					
					m.getBiblioteca().setUnidade(new Unidade(resultSet.getInt(auxBM+3)));
					m.setCodigoBarras(resultSet.getString(auxBM+4));
					
					e.setMaterial(m);
				}
				
				
			}
		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			if(con != null){
				try {
					con.close();
				} catch (SQLException sqlEx) {
					throw new DAOException(sqlEx.getMessage());
				}
			}
		}
		
		return e;
	}


	/**
	 * <p>M�todo que conta a quantidade de empr�stimos atrasados de uma determinada pessoa h� um certo 
	 * per�odo. </p>
	 *   
	 * <p>Utilizando para identifica as pessoas que est�o com um atraso maior que o tolerado e aplicar as san��es cab�veis.</p>
	 *   
	 * @param qtdMaximoDiasAtraso
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public int countEmprestimosAtrasadosVencidoPorDeterminadoPeriodo(int idPessoa, int qtdMaximoDiasAtraso) throws DAOException {
		String hql = " SELECT COUNT(DISTINCT e.id) FROM Emprestimo e "
			+" WHERE e.situacao = "+Emprestimo.EMPRESTADO+" AND e.usuarioBiblioteca.pessoa.id = :idPessoa "
			+" AND e.prazo < :dataLimite ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idPessoa", idPessoa);
		q.setDate("dataLimite", CalendarUtils.subtraiDias(new Date(), qtdMaximoDiasAtraso));
		return ((Long) q.uniqueResult()).intValue();
	}
	

	
	/**
	 * <p>M�todo que conta a quantidade de empr�stimos atrasados de uma determinada pessoa h� um certo 
	 * per�odo. </p>
	 *   
	 * <p>Utilizando para identifica as pessoas que est�o com um atraso maior que o tolerado e aplicar as san��es cab�veis.</p>
	 *   
	 * @param qtdMaximoDiasAtraso
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public int countEmprestimosAtrasadosVencidoPorDeterminadoPeriodo(long cpf_cnpj, int qtdMaximoDiasAtraso) throws DAOException {
		String hql = " SELECT COUNT(DISTINCT e.id) FROM Emprestimo e "
			+" WHERE e.situacao = "+Emprestimo.EMPRESTADO+" AND e.usuarioBiblioteca.pessoa.cpf_cnpj = :cpf_cnpj"
			+" AND e.prazo < :dataLimite ";
		
		Query q = getSession().createQuery(hql);
		q.setLong("cpf_cnpj", cpf_cnpj);
		q.setDate("dataLimite", CalendarUtils.subtraiDias(new Date(), qtdMaximoDiasAtraso));
		return ((Long) q.uniqueResult()).intValue();
	}

	/**
	 * Retorna a lista de empr�stimos renovados e n�o devolvidos do usu�rio.
	 * 
	 * @param usuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public List<Emprestimo> findRenovacoesAtivasByUsuario(UsuarioBiblioteca usuarioBiblioteca) throws DAOException {
		StringBuilder hql = new StringBuilder(); 
		
		hql.append(" SELECT \n");
		hql.append("\t e \n");
		hql.append(" FROM \n");
		hql.append("\t Emprestimo e \n");
		hql.append(" WHERE \n");
		hql.append("\t e.usuarioBiblioteca.id = :idUsuarioBiblioteca \n");
		hql.append("\t AND e.situacao = :situacaoEmprestimo \n");
		hql.append("\t AND ( \n");
		hql.append("\t\t SELECT \n");
		hql.append("\t\t\t COUNT(pe) \n");
		hql.append("\t\t FROM \n");
		hql.append("\t\t\t ProrrogacaoEmprestimo pe \n");
		hql.append("\t\t WHERE \n");
		hql.append("\t\t\t pe.tipo = :tipoProrrogacaoEmprestimo \n");
		hql.append("\t\t\t AND pe.emprestimo.id = e.id \n");
		hql.append("\t ) > 0 ");
		hql.append(" ORDER BY e.dataEmprestimo DESC ");
		
		Query q = getSession().createQuery(hql.toString());

		q.setInteger("idUsuarioBiblioteca", usuarioBiblioteca.getId());
		q.setInteger("situacaoEmprestimo", Emprestimo.EMPRESTADO);
		q.setInteger("tipoProrrogacaoEmprestimo", TipoProrrogacaoEmprestimo.RENOVACAO);
		
		@SuppressWarnings("unchecked")
		List<Emprestimo> lista = q.list();

		for (Iterator<Emprestimo> it = lista.iterator(); it.hasNext(); ) {
			Emprestimo e = it.next();
			
			e.setDataRenovacao(findUltimaDataRenovacao(e.getId()));
		}
		
		return lista;
	}

	/**
	 * Retorna a lista de empr�stimos devolvidos em um per�odo recente (2 dias).
	 * 
	 * @param usuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public List<Emprestimo> findDevolucoesRecentesByUsuario(UsuarioBiblioteca usuarioBiblioteca) throws DAOException {
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT \n");
		hql.append("\t e \n");
		hql.append(" FROM \n");
		hql.append("\t Emprestimo e \n");
		hql.append(" WHERE \n");
		hql.append("\t e.usuarioBiblioteca.id = :idUsuarioBiblioteca \n");
		hql.append("\t AND e.situacao = :situacaoEmprestimo \n");
		hql.append("\t AND e.dataDevolucao >= :dataLimite \n");
		hql.append(" ORDER BY e.dataEmprestimo DESC ");
		
		Date dataLimite = CalendarUtils.descartarHoras(CalendarUtils.adicionaDias(new Date(), -2));
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idUsuarioBiblioteca", usuarioBiblioteca.getId());
		q.setInteger("situacaoEmprestimo", Emprestimo.DEVOLVIDO);
		q.setDate("dataLimite", dataLimite);
		
		@SuppressWarnings("unchecked")
		List<Emprestimo> lista = q.list();
		
		for (Iterator<Emprestimo> it = lista.iterator(); it.hasNext(); ) {
			Emprestimo e = it.next();
			
			e.setDataRenovacao(findUltimaDataRenovacao(e.getId()));
		}
		
		return lista;
	}
	
	
	
	/**
	 * 
	 *    Retorna os id das prorroga��es por renova��o do empr�stimo passado.
	 *
	 * Chamado a partir da p�gina: /biblioteca/
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	
	public Date findUltimaDataRenovacao(int idEmprestimo) throws DAOException {	
		String hql = " SELECT DISTINCT p.dataCadastro  FROM ProrrogacaoEmprestimo p  " +
					 " WHERE p.emprestimo.id = :idEmprestimo AND p.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO
					 + " ORDER BY p.dataCadastro DESC ";
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idEmprestimo", idEmprestimo);                      
		q.setMaxResults(1);
		
		return (Date) q.uniqueResult();		
	}
	
	
	
	
	/**
	 * <p> Verifica se o material est� com um comunica��o de perda. Nesse caso n�o pode realizar uma devolu��o normal desse material. </p>
	 * 
	 * @param idMaterial
	 * @return se o material posssui comunica��o de perda ou n�o
	 * @throws DAOException 
	 */
	public boolean isMaterialComComunicacaoPerdaAtiva(int idMaterial) throws DAOException {

		BigInteger quantidade = BigInteger.ZERO;
		
		StringBuilder sqlCountExemplar = new StringBuilder("SELECT COUNT( DISTINCT emp.id_emprestimo ) "
			+" FROM biblioteca.emprestimo emp "
			+" LEFT JOIN biblioteca.prorrogacao_emprestimo pro ON emp.id_emprestimo = pro.id_emprestimo "
			+" WHERE emp.id_material = :idMaterial " 
			+" AND emp.situacao = "+Emprestimo.EMPRESTADO                       // Se o empr�stimo est� ativo (n�o cancelado nem devolvido)
			+" AND pro.tipo = "+TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL    // e esse empr�stimo tem uma prorroga��o por perda
			);
		
		Query q = getSession().createSQLQuery(sqlCountExemplar.toString());
		q.setInteger("idMaterial", idMaterial);
		quantidade = (BigInteger) q.uniqueResult();
		if(quantidade.longValue() > 0)
			return true;
		else
			return false;
	}
	
	
	
	/**
	 * <p> Retorna a unidade do empr�stimo passado </p>
	 * 
	 * @param idMaterial
	 * @return se o material posssui comunica��o de perda ou n�o
	 * @throws DAOException 
	 */
	public Unidade findUnidadeMaterialDoEmprestimo(int idEmprestimo) throws DAOException {
		
		String hql =
			 " SELECT emp.material.biblioteca.unidade.id "
			+" FROM Emprestimo emp "
			+" WHERE emp.id= :idEmprestimo ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idEmprestimo", idEmprestimo);
		int idUnidade = (Integer) q.uniqueResult();
		
		return new Unidade(idUnidade);
	}
	
	
	
}