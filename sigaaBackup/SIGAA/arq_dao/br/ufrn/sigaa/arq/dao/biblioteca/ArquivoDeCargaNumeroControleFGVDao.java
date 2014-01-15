/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/09/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArquivoDeCargaNumeroControleFGV;

/**
 *      DAO para buscar os arquivos de carga dos n�mero de controle da FGV.<br/>
 *      <strong>OBS.: Os arquivos s�o lidos e apenas os seus conte�dos s�o salvos no banco.</strong>
 *      <p><i> ( Arquivos de carga s�o os arquivos que s�o previamente carregados no sistema, com os n�meros 
 * de controle do campo 001 para serem usados na exporta��o para a FGV, j� que esses n�meros de controle
 * s�o gerados pela FGV ) </i></p>
 *
 * @author jadson
 * @since 15/09/2009
 * @version 1.0 criacao da classe
 *
 */
public class ArquivoDeCargaNumeroControleFGVDao extends GenericSigaaDAO{
	
	/**
	 *   M�todo que encontra todos os arquivos de carga da FGV de t�tulos que ainda possuem n�meros 
	 *   que podem ser usados.  
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<ArquivoDeCargaNumeroControleFGV> findAllArquivosTituloAtivosOrderByDataCarga() throws DAOException{
		
		String hql = new String( " SELECT new br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArquivoDeCargaNumeroControleFGV "
				+" ( a.id, a.numeroInicialSequencia, a.numeroFinalSequencia, a.numeroAtualSequencia, a.dataCarga, a.tipo )"
				+" FROM ArquivoDeCargaNumeroControleFGV a "
				+" WHERE a.ativo  = trueValue() AND a.tipo = "+ArquivoDeCargaNumeroControleFGV.ARQUIVO_BIBLIOGRAFICO
				+" ORDER BY a.dataCarga ");
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<ArquivoDeCargaNumeroControleFGV> lista = q.list();
		return lista;
		
	}
	
	
	/**
	 *   M�todo que encontra todos os arquivos salvos no sistema. Usado para n�o deixa o usu�rio 
	 *   carregar dois arquivos com a mesma faixa de n�meros.
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<ArquivoDeCargaNumeroControleFGV> findAllArquivosTituloOrderByDataCarga() throws DAOException{
		
		String hql = new String( " SELECT new br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArquivoDeCargaNumeroControleFGV "
				+" ( a.id, a.numeroInicialSequencia, a.numeroFinalSequencia, a.numeroAtualSequencia, a.dataCarga, a.tipo )"
				+" FROM ArquivoDeCargaNumeroControleFGV a "
				+" WHERE a.tipo = "+ArquivoDeCargaNumeroControleFGV.ARQUIVO_BIBLIOGRAFICO
				+" ORDER BY a.dataCarga ");
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<ArquivoDeCargaNumeroControleFGV> lista = q.list();
		return lista;
		
	}
	
	
	/**
	 *   M�todo que encontra dos os arquivos de carga da FGV de autoridades que ainda possuem n�meros 
	 *   que podem ser usados para colocar no campo 001. 
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public  List<ArquivoDeCargaNumeroControleFGV> findAllArquivosAutoridadeAtivosOrderByDataCarga() throws DAOException{
		
		String hql = new String( " SELECT new br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArquivoDeCargaNumeroControleFGV "
				+" ( a.id, a.numeroInicialSequencia, a.numeroFinalSequencia, a.numeroAtualSequencia, a.dataCarga, a.tipo )"
				+" FROM ArquivoDeCargaNumeroControleFGV a "
				+" WHERE a.ativo  = trueValue() AND a.tipo = "+ArquivoDeCargaNumeroControleFGV.ARQUIVO_AUTORIDADES
				+" ORDER BY a.dataCarga  ");
		
		Query q = getSession().createQuery(hql);

		@SuppressWarnings("unchecked")
		List<ArquivoDeCargaNumeroControleFGV> lista = q.list();
		return lista;
	}
	
	
	/**
	 *  M�todo que encontra todos os arquivos de autoridades salvos no sistema. Usado para n�o deixa o usu�rio 
	 *   carregar dois arquivos com a mesma faixa de n�meros.
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public  List<ArquivoDeCargaNumeroControleFGV> findAllArquivosAutoridadeOrderByDataCarga() throws DAOException{
		
		String hql = new String( " SELECT new br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArquivoDeCargaNumeroControleFGV "
				+" ( a.id, a.numeroInicialSequencia, a.numeroFinalSequencia, a.numeroAtualSequencia, a.dataCarga, a.tipo )"
				+" FROM ArquivoDeCargaNumeroControleFGV a "
				+" WHERE a.tipo = "+ArquivoDeCargaNumeroControleFGV.ARQUIVO_AUTORIDADES
				+" ORDER BY a.dataCarga  ");
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<ArquivoDeCargaNumeroControleFGV> lista = q.list();
		return lista;
	}
	
	
}
