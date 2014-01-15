/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/09/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * <p>Dao para realizar buscas na tabela: biblioteca.tabela_cutter. Cuja a �nica finalidade � gerar 
 * o valor que identifica a autoria de uma cataloga��o e � colocado no campo 090$b MARC. </p>
 * 
 * <br/>
 * 
 * <p><i> Esta tabela n�o � mapeada por nenhuma classe de dom�nio, j� que teoricamente n�o existem 
 *       altera��es na tabela, s� consultas. </i> </p>
 * 
 * @author Victor Hugo
 */
public class TabelaCutterDao  extends GenericSigaaDAO{

	
	
	
	/**
	 * <p> Retorna o id da tabela cutter correspondente ao c�digo que o sistema acha que � o correto.</p>
	 * <p> A busca � realizada pelo sobre nome exato do Autor. </p>
	 * 
	 * @param sobreNomeAutor
	 * @return
	 * @throws DAOException
	 */
	public Object[] findCodigoCutterByNome(String sobreNomeAutor) throws DAOException{
		
		if(StringUtils.isEmpty(sobreNomeAutor))
			return null;
		
		Query q = getSession().createSQLQuery("SELECT t.id_tabela_cutter, t.codigo, t.nome FROM biblioteca.tabela_cutter t WHERE t.nome_upper = :nome ");
		q.setString("nome",  StringUtils.toAsciiAndUpperCase(sobreNomeAutor).trim());
		q.setMaxResults(1);
		
		return (Object[]) q.uniqueResult();
	}
	
	
	
	
	/**
	 * <p> Retorna os dados da tabela cutter que est�o entre no nome e o nome menos uma letra. </p>
	 * <p> Ordena de forma decrescente e limita a 20 resultados, porque geralmente os mais pr�ximos est�o no final</p>
	 * 
	 * @param sobreNomeAutor
	 * @return
	 * @throws DAOException
	 */
	
	public List<Object[]> findCodigoCutterBetweenNome(String sobreNomeAutor, String sobreNomeAutorCompleto, final int qtdResultadosDesejados) throws DAOException{
		
		if(StringUtils.isEmpty(sobreNomeAutor) || StringUtils.isEmpty(sobreNomeAutorCompleto) )
			return new ArrayList<Object[]>();
		
		Query q = getSession().createSQLQuery("SELECT t.id_tabela_cutter, t.codigo, t.nome FROM biblioteca.tabela_cutter t "
				+" WHERE nome_upper BETWEEN :nome AND :nomeCompleto ORDER BY id_tabela_cutter DESC ");
		q.setString("nome", "" + StringUtils.toAsciiAndUpperCase(sobreNomeAutor).trim() + "%");
		q.setString("nomeCompleto", "" + StringUtils.toAsciiAndUpperCase(sobreNomeAutorCompleto).trim() + "%");
		q.setMaxResults(qtdResultadosDesejados);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		return list;
	}
	
	/**
	 * <p> Retorna os dados da tabela cutter que com come�am com o nome passado</p>
	 *  <p> Ordena de forma crescente e limita a 20 resultados, porque geralmente os mais pr�ximos est�o no in�cio</p>
	 *
	 * 
	 * @param sobreNomeAutor
	 * @return
	 * @throws DAOException
	 */
	public List<Object[]> findCodigoCutterLikeNome(String sobreNomeAutorCompleto,  final int qtdResultadosDesejados) throws DAOException{
		
		if(StringUtils.isEmpty(sobreNomeAutorCompleto) )
			return new ArrayList<Object[]>();
		
		Query q = getSession().createSQLQuery("SELECT t.id_tabela_cutter, t.codigo, t.nome FROM biblioteca.tabela_cutter t "
				+" WHERE nome_upper LIKE :nomeCompleto ORDER BY id_tabela_cutter ASC ");
		q.setString("nomeCompleto", "" + StringUtils.toAsciiAndUpperCase(sobreNomeAutorCompleto).trim() + "%");
		q.setMaxResults(qtdResultadosDesejados);
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		return list;
	}
	
	
	/**
	 * <p> Esse m�todo completa os resultados obtidos para garantir que o sistema sempre retorne os 20 mais pr�ximo c�digos cutter </p>
	 * <p> <strong>Para este m�todo funcionar a tabela cutter deve ser mantida ordenada pelo <code>id_tabela_cutter</code>. </strong> </p> 
	 *  
	 *  
	 * @param idPrimeiroCodCutter  o id do primeiro c�digo cutter que foi retornardo pelo sistema
	 * @param idUltimoCodCutter  o id do �ltimo c�digo cutter que foi retornardo pelo sistema
	 * @param qtdResultadosObtidos necess�rio para calcular a quantidade que deve ser consultada
	 * @param qtdResultadosDesejados  necess�rio para calcular a quantidade que deve ser consultada
	 * @return Object[] contendo -> Object[0]: id_tabela_cutter, Object[1]: codigo cutter, Object[2]: Sobre nome do autor correspondente ao c�digo
	 * @throws DAOException
	 */
	public List<Object[]> findCodigosProximosTabelaCutter(int idPrimeiroCodCutter, int idUltimoCodCutter, int qtdResultadosObtidos, final int qtdResultadosDesejados) throws DAOException{
		
		int qtdResultadosFalta = qtdResultadosDesejados - qtdResultadosObtidos;
		
		int idInicioIntervalo = idPrimeiroCodCutter - (qtdResultadosFalta/2);
		int idFinalIntervalo = idUltimoCodCutter + (qtdResultadosFalta/2);
		
		Query q = getSession().createSQLQuery("SELECT id_tabela_cutter, codigo, nome FROM biblioteca.tabela_cutter "
				+" WHERE ( id_tabela_cutter between :inicio1 AND :fim1 ) "  // X c�digos anteriores 
				+" OR ( id_tabela_cutter between :inicio2 AND :fim2 ) " // X c�digos posteriores
				+" ORDER BY id_tabela_cutter ");
		
		q.setInteger("inicio1", idInicioIntervalo);
		q.setInteger("fim1", idPrimeiroCodCutter-1); // n�o inlcui o primerio j� recuperado na busca original
		q.setInteger("inicio2", idUltimoCodCutter+1);  // n�o inlcui o �ltimo j� recuperado na busca original
		q.setInteger("fim2", idFinalIntervalo);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		return list;
	}
	
	
}
