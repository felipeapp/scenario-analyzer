/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 11/03/2011
 * 
 */
package br.ufrn.sigaa.arq.dao.biblioteca.relatorios;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.ResultadoRelatorioMultas;

/**
 * <p> Dao <strong>EXCLUSIVO</strong> para as consultas utilizadas na parte de multas da biblioteca </p>
 * 
 * @author jadson
 *
 */
public class RelatoriosMultasBibliotecaDao  extends GenericSigaaDAO {
	
	/**
	 * left join utilizados para recuperar as informações do usuário que recebeu a multa.
	 * precisa ser left join porque a multa pode ser gerada por um empréstimo, ou caso seja manual, ela vai
	 * ser atribuída diretamente ao usuário biblioteca.
	 */
	private static final String LEFT_JOINS_PADRAO_MULTAS_PESSOAS = 
		" left join biblioteca.usuario_biblioteca ubManual on m.id_usuario_biblioteca = ubManual.id_usuario_biblioteca "
		+" left join comum.pessoa pManual on pManual.id_pessoa = ubManual.id_pessoa "
		+" left join biblioteca.emprestimo e on m.id_emprestimo = e.id_emprestimo "
		+" left join biblioteca.usuario_biblioteca ubAutomatica on e.id_usuario_biblioteca = ubAutomatica.id_usuario_biblioteca "
		+" left join comum.pessoa pAutomatica on pAutomatica.id_pessoa = ubAutomatica.id_pessoa ";
	
	
	/**
	 * left join utilizados para recuperar as informações do usuário que recebeu a multa.
	 * precisa ser left join porque a multa pode ser gerada por um empréstimo, ou caso seja manual, ela vai
	 * ser atribuída diretamente ao usuário biblioteca.
	 */
	private static final String LEFT_JOINS_PADRAO_MULTAS_BIBLIOTECAS = 
		" left join biblioteca.usuario_biblioteca ubManual on m.id_usuario_biblioteca = ubManual.id_usuario_biblioteca "
		+" left join biblioteca.biblioteca bManual on bManual.id_biblioteca = ubManual.id_biblioteca "
		+" left join biblioteca.emprestimo e on m.id_emprestimo = e.id_emprestimo "
		+" left join biblioteca.usuario_biblioteca ubAutomatica on e.id_usuario_biblioteca = ubAutomatica.id_usuario_biblioteca "
		+" left join biblioteca.biblioteca bAutomatica on bAutomatica.id_biblioteca = ubAutomatica.id_biblioteca ";
	
	
	
	
	
	/**
	 * <p>Retorna todos as multa que foram pagas no período informado.</p>
	 *
	 * <p>Esse método pode retornar multas estornadas, caso o estorno tenha ocorrido depois que o usuário emitiu a GRU 
	 * para o pagamento e o pagamento tenha sido efetivado no banco.  Não é possível pagar multas estornadas manualmente. </p>
	 *
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public List<ResultadoRelatorioMultas> findMultasPagasPorPeriodo(Collection<Integer> idsBibliotecas, Date dataInicio, Date dataFim)throws DAOException {
		
		final String PROJECAO_MULTAS_PAGAS = " m.valor as valormulta, pQuitou.nome as nomeQuitador, m.data_quitacao, m.status, m.observacao_pagamento, m.numero_referencia "; 
		
		if(dataInicio == null && dataFim == null)
			throw new IllegalArgumentException("O período é obrigatório"); // erro do programador
		
		dataInicio = CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 000);
		dataFim = CalendarUtils.configuraTempoDaData(dataFim, 23, 59, 59, 999);
		
		String sqlPessoas = " SELECT CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.manual as multaManual, "
							
							// O usuário da multa, seja ela automatica ou manual
							+" pManual.nome as nomepManual, pManual.cpf_cnpj as cpfPManual, pManual.internacional as internacionalPManual, pManual.passaporte as passaportePManal, "
							+" pAutomatica.nome as  nomePAutomatica, pAutomatica.cpf_cnpj as cpfPAutomatica, pAutomatica.internacional as internacionalpAutomatica, pAutomatica.passaporte as passaportepAutomatica, "
			
							+PROJECAO_MULTAS_PAGAS // Só varia aqui
							
							+" FROM biblioteca.multa_usuario_biblioteca m "
							+ LEFT_JOINS_PADRAO_MULTAS_PESSOAS
							+" LEFT JOIN comum.usuario uQuitou on uQuitou.id_usuario =  m.id_usuario_quitacao "
							+" LEFT JOIN comum.pessoa pQuitou on pQuitou.id_pessoa = uQuitou.id_pessoa "
							+" WHERE m.status in "+UFRNUtils.gerarStringIn(MultaUsuarioBiblioteca.getStatusPagos())
							+" AND ( pManual.nome IS NOT NULL or pAutomatica.nome IS NOT NULL ) "
							+" AND ( m.data_quitacao between :dataInicio and :dataFim ) ";
		
		if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
			sqlPessoas += (" AND m.id_biblioteca_recolhimento IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
		}
			
		sqlPessoas += " ORDER BY  CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.data_quitacao "; 
		
		
		String sqlBibliotecas = " SELECT CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.manual as multaManual, "
			
			+" bManual.identificador as identificadorManual, bManual.descricao as descricaoManual, bManual.id_unidade as unidadeManual, "
			+" bAutomatica.identificador as identificadorAutomatica, bAutomatica.descricao as descricaoAutomatica, bAutomatica.id_unidade as UniadeAutomatica, "
			
			+PROJECAO_MULTAS_PAGAS // Só varia aqui
			
			+" FROM biblioteca.multa_usuario_biblioteca m "
			+ LEFT_JOINS_PADRAO_MULTAS_BIBLIOTECAS
			+" LEFT JOIN comum.usuario uQuitou on uQuitou.id_usuario =  m.id_usuario_quitacao "
			+" LEFT JOIN comum.pessoa pQuitou on pQuitou.id_pessoa = uQuitou.id_pessoa "
			+" WHERE m.status in "+UFRNUtils.gerarStringIn(MultaUsuarioBiblioteca.getStatusPagos()) 
			+" AND ( bManual.identificador IS NOT NULL or bAutomatica.identificador IS NOT NULL )"
			+" AND ( m.data_quitacao between :dataInicio and :dataFim ) ";
			
			if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
				sqlBibliotecas += (" AND m.id_biblioteca_recolhimento IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
			}
				
			
			sqlBibliotecas += " ORDER BY  CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.data_quitacao ";
		
		
		
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
		
		return montaDadosRelatorioMultas(resultadosPessoa, resultadosBiblioteca, false, true, false);
	}
	
	
	
	/**
	 * Retorna todos as multa que foram estornadas no período passado
	 *
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public List<ResultadoRelatorioMultas> findMultasEstornadasPorPeriodo(Collection<Integer> idsBibliotecas, Date dataInicio, Date dataFim)throws DAOException {
		
		final String PROJECAO_MULTAS_ESTORNADAS = " m.valor as valormulta, pEstornou.nome as nomeEstornador, m.data_estorno, m.motivo_estorno ";
		
		if(dataInicio == null && dataFim == null)
			throw new IllegalArgumentException("O período é obrigatório"); // erro do programador
		
		dataInicio = CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 000);
		dataFim = CalendarUtils.configuraTempoDaData(dataFim, 23, 59, 59, 999);
		
		String sqlPessoas = " SELECT CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.manual as multaManual, "
							
							+" pManual.nome as nomepManual, pManual.cpf_cnpj as cpfPManual, pManual.internacional as internacionalPManual, pManual.passaporte as passaportePManal, "
							+" pAutomatica.nome as  nomePAutomatica, pAutomatica.cpf_cnpj as cpfPAutomatica, pAutomatica.internacional as internacionalpAutomatica, pAutomatica.passaporte as passaportepAutomatica, "
			
							+PROJECAO_MULTAS_ESTORNADAS
							
							+" FROM biblioteca.multa_usuario_biblioteca m "
							+ LEFT_JOINS_PADRAO_MULTAS_PESSOAS
							+" inner join comum.usuario uEstornou on uEstornou.id_usuario =  m.id_usuario_estorno "
							+" inner join comum.pessoa pEstornou on pEstornou.id_pessoa = uEstornou.id_pessoa "
							+" where m.ativo = falseValue()  AND ( pManual.nome IS NOT NULL or pAutomatica.nome IS NOT NULL ) "
							+" AND ( m.data_estorno between :dataInicio and :dataFim ) ";
		
							if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
								sqlPessoas += (" AND m.id_biblioteca_recolhimento IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
							}
							
							sqlPessoas +=  " ORDER BY  CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.data_estorno "; 
		
		
		String sqlBibliotecas = " SELECT CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END,  m.manual as multaManual, "
		
			+" bManual.identificador as identificadorManual, bManual.descricao as descricaoManual, bManual.id_unidade as unidadeManual, "
			+" bAutomatica.identificador as identificadorAutomatica, bAutomatica.descricao as descricaoAutomatica, bAutomatica.id_unidade as UniadeAutomatica, "
			
			+PROJECAO_MULTAS_ESTORNADAS
		
			+" FROM biblioteca.multa_usuario_biblioteca m "
			+ LEFT_JOINS_PADRAO_MULTAS_BIBLIOTECAS
			+" inner join comum.usuario uEstornou on uEstornou.id_usuario =  m.id_usuario_estorno "
			+" inner join comum.pessoa pEstornou on pEstornou.id_pessoa = uEstornou.id_pessoa "
			+" where m.ativo = falseValue() and ( bManual.identificador IS NOT NULL or bAutomatica.identificador IS NOT NULL )"
			+" AND ( m.data_estorno between :dataInicio and :dataFim ) ";
			
			if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
				sqlBibliotecas += (" AND m.id_biblioteca_recolhimento IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
			}
			
			sqlBibliotecas += " ORDER BY  CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.data_estorno ";
		
		
		
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
		
		return montaDadosRelatorioMultas(resultadosPessoa, resultadosBiblioteca, true, false, false);
	}

	
	
	
	
	
	
	
	/**
	 * Retorna todos as multa que estão no status EM ABERTO e que foram cadastradas dentro do período especificado.
	 *
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public List<ResultadoRelatorioMultas> findMultasNaoPagasPorPeriodo(Collection<Integer> idsBibliotecas, Date dataInicio, Date dataFim)throws DAOException {
		
		final String PROJECAO_NAO_MULTAS_PAGAS = " m.valor as valormulta  "; 
		
		if(dataInicio == null && dataFim == null)
			throw new IllegalArgumentException("O período é obrigatório"); // erro do programador
		
		dataInicio = CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 000);
		dataFim = CalendarUtils.configuraTempoDaData(dataFim, 23, 59, 59, 999);
		
		String sqlPessoas = " SELECT CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.manual as multaManual,  "
							
							+" pManual.nome as nomepManual, pManual.cpf_cnpj as cpfPManual, pManual.internacional as internacionalPManual, pManual.passaporte as passaportePManal, "
							+" pAutomatica.nome as  nomePAutomatica, pAutomatica.cpf_cnpj as cpfPAutomatica, pAutomatica.internacional as internacionalpAutomatica, pAutomatica.passaporte as passaportepAutomatica, "
			
							+PROJECAO_NAO_MULTAS_PAGAS
							
							+" FROM biblioteca.multa_usuario_biblioteca m "
							+ LEFT_JOINS_PADRAO_MULTAS_PESSOAS
							+" where m.ativo = trueValue() "
							+" AND m.status in "+UFRNUtils.gerarStringIn(MultaUsuarioBiblioteca.getStatusNaoPagos())
							+" AND ( pManual.nome IS NOT NULL or pAutomatica.nome IS NOT NULL ) "
							
							+" AND ( m.data_cadastro between :dataInicio and :dataFim ) ";
							
							if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
								sqlPessoas += (" AND m.id_biblioteca_recolhimento IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
							}
							
							sqlPessoas += " ORDER BY  CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.data_cadastro "; 
		
		
		String sqlBibliotecas = " SELECT CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.manual as multaManual, "
			
			+" bManual.identificador as identificadorManual, bManual.descricao as descricaoManual, bManual.id_unidade as unidadeManual, "
			+" bAutomatica.identificador as identificadorAutomatica, bAutomatica.descricao as descricaoAutomatica, bAutomatica.id_unidade as uniadeAutomatica, "
			
			+PROJECAO_NAO_MULTAS_PAGAS
			
			+" FROM biblioteca.multa_usuario_biblioteca m "
			+ LEFT_JOINS_PADRAO_MULTAS_BIBLIOTECAS
			+" where m.ativo = trueValue() and ( bManual.identificador IS NOT NULL or bAutomatica.identificador IS NOT NULL )"
			+" AND m.status in "+UFRNUtils.gerarStringIn(MultaUsuarioBiblioteca.getStatusNaoPagos())
			+" AND ( m.data_cadastro between :dataInicio and :dataFim ) ";
			
			if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
				sqlBibliotecas += (" AND m.id_biblioteca_recolhimento IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
			}
			
			sqlBibliotecas +=  " ORDER BY  CASE WHEN m.manual THEN m.id_usuario_biblioteca ELSE e.id_usuario_biblioteca END, m.data_cadastro ";
		
		
		
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
		
		return montaDadosRelatorioMultas(resultadosPessoa, resultadosBiblioteca, false, false, true);
	}
	
	
	
	///////////////////////////// métodos auxiliares ///////////////////////////
	
	

	/**
	 * Monta a lista de resultados do relatório de multas estorndas
	 *
	 * @param resultadosPessoa
	 * @param resultadosBiblioteca
	 * @return
	 */
	private List<ResultadoRelatorioMultas> montaDadosRelatorioMultas(List<Object> resultadosPessoa, List<Object> resultadosBiblioteca, boolean estornadas, boolean quitadas, boolean naoQuitadas) {
		
		List<ResultadoRelatorioMultas> lista = new ArrayList<ResultadoRelatorioMultas>();
		
		
		for (Object infoMultas : resultadosPessoa) {
			
			Object[] arrayinfoMultas  = (Object[]) infoMultas;
			
			int idUsuarioBiblioteca  = (Integer) arrayinfoMultas[0];
			
			ResultadoRelatorioMultas resultado =  new ResultadoRelatorioMultas(idUsuarioBiblioteca);
			
			if(lista.contains(resultado)){ // já tem o usuário, só adiciona as informações da multa
				
				resultado = lista.get(lista.indexOf(resultado));
				
				adicionaInforacoesMultaPessoa(resultado, arrayinfoMultas, estornadas, quitadas, naoQuitadas);
				
			}else{  // não tem o usuário, então aciciona
				
				adicionaInforacoesPessoa(resultado, arrayinfoMultas);
				
				adicionaInforacoesMultaPessoa(resultado, arrayinfoMultas, estornadas, quitadas, naoQuitadas);
				
				lista.add(resultado);
				
			}
			
		}
		
		
		
		for (Object infoMultas : resultadosBiblioteca) {
			
			Object[] arrayinfoMultas  = (Object[]) infoMultas;
			
			int idUsuarioBiblioteca  = (Integer) arrayinfoMultas[0];
			
			ResultadoRelatorioMultas resultado =  new ResultadoRelatorioMultas(idUsuarioBiblioteca);
			
			if(lista.contains(resultado)){ // já tem o usuário, só adiciona as informações da multa
				
				resultado = lista.get(lista.indexOf(resultado));
				
				adicionaInforacoesMultaBiblioteca(resultado, arrayinfoMultas, estornadas, quitadas, naoQuitadas);
				
			}else{  // não tem o usuário, então aciciona
				
				adicionaInforacoesBiblioteca(resultado, arrayinfoMultas);
				
				adicionaInforacoesMultaBiblioteca(resultado, arrayinfoMultas, estornadas, quitadas, naoQuitadas);
				
				lista.add(resultado);
				
			}
			
		}
		
		return lista;
	}
	
	/**
	 * Adiciona as informações sobre a pessoa da multa
	 * @param resultado
	 * @param arrayinfoMultas
	 */
	private void adicionaInforacoesPessoa(ResultadoRelatorioMultas resultado, Object[] arrayinfoMultas){
		
		if((Boolean) arrayinfoMultas[1] ){ // se é multa manual
			
			String nome = (String) arrayinfoMultas[2];
			String cpf =  ( (BigInteger) arrayinfoMultas[3]).toString() ;
			Boolean internacional = (Boolean) arrayinfoMultas[4];
			String passaporte = (String) arrayinfoMultas[5];
			
			if(StringUtils.notEmpty(cpf)){
				resultado.setInfoUsuario("(CPF) "+cpf+" - "+nome);
			}else{
				if(internacional){
					resultado.setInfoUsuario("(passaporte) "+passaporte+" - "+nome);
				}
			}
			
		}else{
			String nome = (String) arrayinfoMultas[6];
			String cpf = ((BigInteger) arrayinfoMultas[7]).toString();
			Boolean internacional = (Boolean) arrayinfoMultas[8];
			String passaporte = (String) arrayinfoMultas[9];
			
			if(StringUtils.notEmpty(cpf)){
				resultado.setInfoUsuario("(CPF) "+cpf+" - "+nome);
			}else{
				if(internacional){
					resultado.setInfoUsuario("(passaporte) "+passaporte+" - "+nome);
				}
			}
				
		}
	}
	
	/**
	 * Adiciona as informações sobre a biblioteca da multa
	 * @param resultado
	 * @param arrayinfoMultas
	 */
	private void adicionaInforacoesBiblioteca(ResultadoRelatorioMultas resultado, Object[] arrayinfoMultas){
		
		if((Boolean) arrayinfoMultas[1] ){ // se é multa manual
			
			String indentificador = (String) arrayinfoMultas[2];
			String descricao =  (String)arrayinfoMultas[3]  ;
			Integer idUnidade = (Integer) arrayinfoMultas[4];
			
			
			resultado.setInfoUsuario(indentificador+" - "+descricao + (idUnidade == null ? "(Biblioteca Externa)" : "(Biblioteca Interna)") );
			
		}else{
			String indentificador = (String) arrayinfoMultas[5];
			String descricao = (String) arrayinfoMultas[6];
			Integer idUnidade = (Integer) arrayinfoMultas[7];
			
			resultado.setInfoUsuario(indentificador+" - "+descricao + (idUnidade == null ? "(Biblioteca Externa)" : "(Biblioteca Interna)") );
		}
	}
	
	
	/**
	 * Adiciona as informações sobre a multa da pessoa
	 * @param resultado
	 * @param arrayinfoMultas
	 */
	private void adicionaInforacoesMultaPessoa(ResultadoRelatorioMultas resultado, Object[] arrayinfoMultas, boolean estornadas, boolean quitadas, boolean naoQuitadas){
		
		
		if(estornadas)
			resultado.setInfoMultaEstornada((BigDecimal)arrayinfoMultas[10], (String)arrayinfoMultas[11], (Date) arrayinfoMultas[12], (String)arrayinfoMultas[13]);
		
		if(quitadas)
			resultado.setInfoMultaQuitadas((BigDecimal)arrayinfoMultas[10], (String)arrayinfoMultas[11], (Date) arrayinfoMultas[12], (Integer) arrayinfoMultas[13], (String)arrayinfoMultas[14], (BigInteger)arrayinfoMultas[15] );
		
		if(naoQuitadas)
			resultado.setInfoMultaNaoQuitadas((BigDecimal)arrayinfoMultas[10]);
		
	}
	
	/**
	 * Adiciona as informações sobre a multa da biblioteca
	 * @param resultado
	 * @param arrayinfoMultas
	 */
	private void adicionaInforacoesMultaBiblioteca(ResultadoRelatorioMultas resultado, Object[] arrayinfoMultas, boolean estornadas, boolean quitadas, boolean naoQuitadas){
		
		if(estornadas)
			resultado.setInfoMultaEstornada((BigDecimal)arrayinfoMultas[8], (String)arrayinfoMultas[9], (Date) arrayinfoMultas[10], (String)arrayinfoMultas[11]);
		
		if(quitadas)
			resultado.setInfoMultaQuitadas((BigDecimal)arrayinfoMultas[8], (String)arrayinfoMultas[9], (Date) arrayinfoMultas[10], (Integer) arrayinfoMultas[11], (String)arrayinfoMultas[12], (BigInteger)arrayinfoMultas[13] );
		
		if(naoQuitadas)
			resultado.setInfoMultaNaoQuitadas((BigDecimal)arrayinfoMultas[8]);
	}
	
}


