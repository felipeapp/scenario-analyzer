/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/02/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * <p>Dao Exclusivo para o relatório de suspesões extornadas.</p>
 * 
 * @author jadson
 *
 */
public class RelatoriosSuspensoesEstornadasBibliotecaDao extends GenericSigaaDAO {

	/**
	 * left join utilizados para recuperar as informações do usuário que recebeu a multa.
	 * precisa ser left join porque a multa pode ser gerada por um empréstimo, ou caso seja manual, ela vai
	 * ser atribuída diretamente ao usuário biblioteca.
	 */
	private static final String LEFT_JOINS_PADRAO_SUSPENSOES_PESSOAS = 
		 " left join biblioteca.usuario_biblioteca ubManual on s.id_usuario_biblioteca = ubManual.id_usuario_biblioteca "
		+" left join comum.pessoa pManual on pManual.id_pessoa = ubManual.id_pessoa "
		+" left join biblioteca.emprestimo e on s.id_emprestimo = e.id_emprestimo "
		+" left join biblioteca.material_informacional m on m.id_material_informacional = e.id_material "
		+" left join biblioteca.usuario_biblioteca ubAutomatica on e.id_usuario_biblioteca = ubAutomatica.id_usuario_biblioteca "
		+" left join comum.pessoa pAutomatica on pAutomatica.id_pessoa = ubAutomatica.id_pessoa ";
	
	
	/**
	 * left join utilizados para recuperar as informações do usuário que recebeu a multa.
	 * precisa ser left join porque a multa pode ser gerada por um empréstimo, ou caso seja manual, ela vai
	 * ser atribuída diretamente ao usuário biblioteca.
	 */
	private static final String LEFT_JOINS_PADRAO_SUSPENSOES_BIBLIOTECAS = 
		 " left join biblioteca.usuario_biblioteca ubManual on s.id_usuario_biblioteca = ubManual.id_usuario_biblioteca "
		+" left join biblioteca.biblioteca bManual on bManual.id_biblioteca = ubManual.id_biblioteca "
		+" left join biblioteca.emprestimo e on s.id_emprestimo = e.id_emprestimo "
		+" left join biblioteca.material_informacional m on m.id_material_informacional = e.id_material "
		+" left join biblioteca.usuario_biblioteca ubAutomatica on e.id_usuario_biblioteca = ubAutomatica.id_usuario_biblioteca "
		+" left join biblioteca.biblioteca bAutomatica on bAutomatica.id_biblioteca = ubAutomatica.id_biblioteca ";
	
	
	
	/**
	 * Retorna todos as suspensões que foram estornadas no período passado
	 *
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public List<Object> findSuspensoesEstornadasPorPeriodo(Collection<Integer> idsBibliotecas, Date dataInicio, Date dataFim )throws DAOException {
		
		final String PROJECAO_SUSPENSOES_ESTORNADAS = " s.data_inicio as dataInicio, s.data_fim as dataFim, pEstornou.nome as nomeEstornador, s.data_estorno, s.motivo_estorno, ";
		
		if(dataInicio == null && dataFim == null)
			throw new IllegalArgumentException("O período é obrigatório"); // caso a validação no MBean falhe.
		
		dataInicio = CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 000);
		dataFim = CalendarUtils.configuraTempoDaData(dataFim, 23, 59, 59, 999);
		
		String sqlPessoas = " SELECT  " +
							" true as supensaoPessoa, s.manual as suspensaoManual, COALESCE (s.id_usuario_biblioteca , e.id_usuario_biblioteca) as id_usuario_biblioteca, "+ // usuário biblioteca suspenso
							PROJECAO_SUSPENSOES_ESTORNADAS+
							"  COALESCE (pAutomatica.internacional, pManual.internacional) internacional, COALESCE (pAutomatica.cpf_cnpj, pManual.cpf_cnpj) cpf, COALESCE (pAutomatica.passaporte, pManual.passaporte) passaporte,  COALESCE (pAutomatica.nome, pManual.nome) nome    " // pessoa suspensao
							
							+" FROM biblioteca.suspensao_usuario_biblioteca s "
							+ LEFT_JOINS_PADRAO_SUSPENSOES_PESSOAS
							+" inner join comum.usuario uEstornou on uEstornou.id_usuario =  s.id_usuario_estorno "
							+" inner join comum.pessoa pEstornou on pEstornou.id_pessoa = uEstornou.id_pessoa "
							+" where s.ativo = falseValue() "
							+" AND ( s.data_estorno between :dataInicio and :dataFim ) "
							+"AND ( pManual.id_pessoa IS NOT NULL OR pAutomatica.id_pessoa  IS NOT NULL )";
		
							if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) { // suspensões manual não tem biblioteca 
								sqlPessoas += (" AND ( s.manual OR m.id_biblioteca IN ("+ StringUtils.join(idsBibliotecas, ',') +") ) ");
							}
							
							sqlPessoas +=  " ORDER BY  id_usuario_biblioteca, s.data_estorno DESC "; 
		
		
		String sqlBibliotecas = " SELECT " +
				" false as supensaoPessoa,  s.manual as suspensaoManual, COALESCE (s.id_usuario_biblioteca , e.id_usuario_biblioteca) as id_usuario_biblioteca, "+
				PROJECAO_SUSPENSOES_ESTORNADAS+
				" COALESCE (bManual.identificador, bAutomatica.identificador) identificador, COALESCE (bManual.descricao, bAutomatica.descricao) descricao, COALESCE (bManual.id_unidade, bAutomatica.id_unidade) unidade "
			
				+" FROM biblioteca.suspensao_usuario_biblioteca s "
			+ LEFT_JOINS_PADRAO_SUSPENSOES_BIBLIOTECAS
			+" inner join comum.usuario uEstornou on uEstornou.id_usuario =  s.id_usuario_estorno "
			+" inner join comum.pessoa pEstornou on pEstornou.id_pessoa = uEstornou.id_pessoa "
			+" where s.ativo = falseValue()"
			+" AND ( s.data_estorno between :dataInicio and :dataFim ) "
		    +" AND ( bAutomatica.id_biblioteca IS NOT NULL OR bManual.id_biblioteca  IS NOT NULL )";
		
			if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) { // suspensões manual não tem biblioteca 
				sqlBibliotecas += (" AND ( s.manual OR m.id_biblioteca IN ("+ StringUtils.join(idsBibliotecas, ',') +") ) ");
			}
			
			sqlBibliotecas += " ORDER BY  id_usuario_biblioteca, s.data_estorno DESC ";
		
		
		
		Query qPessoas = getSession().createSQLQuery(sqlPessoas);
		qPessoas.setParameter("dataInicio", dataInicio);
		qPessoas.setParameter("dataFim", dataFim);
		
		@SuppressWarnings("unchecked")
		List<Object> resultadosPessoa = qPessoas.list();
		
		Query qBibliotecas = getSession().createSQLQuery(sqlBibliotecas);
		qBibliotecas.setParameter("dataInicio", dataInicio);
		qBibliotecas.setParameter("dataFim", dataFim);
		
		@SuppressWarnings("unchecked")
		List<Object> resultadosBiblioteca = qBibliotecas.list();
		
		resultadosPessoa.addAll(resultadosBiblioteca); // junta tudo numa lista
		
		return resultadosPessoa;
	}

	
}
