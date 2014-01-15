/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 06/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.ConstantesRelatorioBiblioteca;

/**
 *
 * <p>Dao utilizado exclusivamente para o relatório de empréstimos por tipo de empréstimo. </p>
 * 
 * @author jadson
 *
 */
public class RelatorioEmprestimosPorTipoEmprestimoDao  extends GenericSigaaDAO {
	

	/**
	 * <p>Consulta os empréstimos e renovações por tipo de empréstimos no formato analítico.</p>
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @param idTipoEmprestimo
	 * @param idsBibliotecas
	 * @param agruparPorTurno
	 * @param pagina
	 * @param limite
	 * @return
	 * @throws DAOException
	 */
	public List<Object> findEmprestimosPorTipoDeEmprestimoAnalitico( Date dataInicio, Date dataFim, int idTipoEmprestimo, 
			Collection<Integer> idsBibliotecas, boolean agruparPorTurno, int pagina, int limite) throws DAOException {

		if ( limite <= 0 )
			throw new IllegalArgumentException("Limite deve ser maior que zero.");
		if ( pagina < 0 )
			throw new IllegalArgumentException("Página deve ser maior que zero.");
		
		@SuppressWarnings("unchecked")
		List<Object> retorno = (List<Object>) consultaEmprestimosPorTipoDeEmprestimoAnalitico(dataInicio, dataFim, idTipoEmprestimo, 
				idsBibliotecas, agruparPorTurno, pagina, limite, false);
		return retorno;
		
	}
	
	
	/**
	 * <p>Conta os empréstimos e renovações por tipo de empréstimos no formato analítico.</p>
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @param idTipoEmprestimo
	 * @param idsBibliotecas
	 * @param agruparPorTurno
	 * @return
	 * @throws DAOException
	 */
	public int countEmprestimosPorTipoDeEmprestimoAnalitico( Date dataInicio, Date dataFim, int idTipoEmprestimo, 
			Collection<Integer> idsBibliotecas, boolean agruparPorTurno) throws DAOException {

		Object totalObj = consultaEmprestimosPorTipoDeEmprestimoAnalitico(dataInicio, dataFim, idTipoEmprestimo, idsBibliotecas, 
				agruparPorTurno, 0, 0, true);
		
		int total =  (totalObj instanceof BigDecimal ? (BigDecimal) totalObj : (BigInteger) totalObj).intValue();
		
		return total;	
		
	}
	

	/**
	 * Consulta padrão do formato analítico.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @param idTipoEmprestimo
	 * @param idsBibliotecas
	 * @param agruparPorTurno
	 * @param pagina
	 * @param limite
	 * @param isCount
	 * @return
	 * @throws DAOException
	 */
	private Object consultaEmprestimosPorTipoDeEmprestimoAnalitico( Date dataInicio, Date dataFim, int idTipoEmprestimo, 
			Collection<Integer> idsBibliotecas, boolean agruparPorTurno, int pagina, int limite , boolean isCount) throws DAOException {
		
		
		//List<Object> resultados = new ArrayList<Object>();
		
		
		final String projecaoTurno  = " , \t\t ( CASE \n"+
        "\t\t\t WHEN EXTRACT(HOUR from emprestimo.data_emprestimo)  BETWEEN 0 AND 11 THEN "+ConstantesRelatorioBiblioteca.Turnos.MANHA.getValor()+" \n"+
        "\t\t\t WHEN EXTRACT(HOUR from emprestimo.data_emprestimo)  BETWEEN 12 AND 17 THEN "+ConstantesRelatorioBiblioteca.Turnos.TARDE.getValor()+" \n"+
        "\t\t\t WHEN EXTRACT(HOUR from emprestimo.data_emprestimo)  > 17 THEN "+ConstantesRelatorioBiblioteca.Turnos.NOITE.getValor()+" \n"+
        "\t\t\t ELSE -1   "+
   		"\t\t END )  as turno \n"; 
		
		final String innerJoins = "\t\t INNER JOIN biblioteca.material_informacional material on emprestimo.id_material = material.id_material_informacional \n"+
		        "\t\t INNER JOIN biblioteca.biblioteca biblioteca on material.id_biblioteca  =biblioteca.id_biblioteca \n"+
		        "\t\t INNER JOIN biblioteca.politica_emprestimo politicaEmprestimo on emprestimo.id_politica_emprestimo=politicaEmprestimo.id_politica_emprestimo \n"+
		        "\t\t INNER JOIN biblioteca.tipo_emprestimo tipoEmprestimo on politicaEmprestimo.id_tipo_emprestimo=tipoEmprestimo.id_tipo_emprestimo \n"+
		        "\t\t INNER JOIN comum.usuario usuario on emprestimo.id_usuario_realizou_emprestimo = usuario.id_usuario \n"+
		        "\t\t INNER JOIN comum.pessoa pessoaOperador on usuario.id_pessoa = pessoaOperador.id_pessoa \n"+
		        "\t\t INNER JOIN biblioteca.usuario_biblioteca usuarioBiblioteca on emprestimo.id_usuario_biblioteca = usuarioBiblioteca.id_usuario_biblioteca \n"+
		        "\t\t LEFT JOIN comum.pessoa pessoa on usuarioBiblioteca.id_pessoa = pessoa.id_pessoa \n"+
		        "\t\t LEFT JOIN biblioteca.biblioteca bib on usuarioBiblioteca.id_biblioteca = bib.id_biblioteca \n";
		
		
		StringBuilder sql = new StringBuilder("SELECT \n" +
				"\t interna.data_emprestimo, \n" +
				"\t interna.idTipo, \n" +
				"\t interna.descricaoTipo, \n" +
				"\t interna.idBiblioteca, \n" +
				"\t interna.desBiblioteca, \n"+
				"\t interna.codigoMaterial, \n" +
				"\t interna.usuarioEmprestou, \n" +
				"\t interna.usuarioPegou \n"+
				( agruparPorTurno ? ", \t interna.turno \n": "")+
				"FROM ( \n");
		sql.append("\t SELECT \n" +
				"\t\t emprestimo.data_emprestimo, \n"+
				"\t\t tipoEmprestimo.id_tipo_emprestimo as idTipo, \n"+ 
				"\t\t tipoEmprestimo.descricao as descricaoTipo, \n"+
				"\t\t biblioteca.id_biblioteca as idBiblioteca, \n"+ 
				"\t\t biblioteca.descricao as desBiblioteca, \n"+ 
				"\t\t material.codigo_barras as codigoMaterial, \n"+
				"\t\t pessoaOperador.nome as usuarioEmprestou, \n"+ 
				"\t\t COALESCE(pessoa.nome, bib.descricao) as usuarioPegou \n");
		
		if(agruparPorTurno){
			sql.append(projecaoTurno);
		}       
		
		sql.append("\t FROM \n" +
				"\t\t biblioteca.emprestimo emprestimo \n"+
				innerJoins+
		        "\t WHERE \n" +
		        "\t\t emprestimo.ativo = trueValue() \n");
		
		sql.append("\t\t AND (   emprestimo.data_emprestimo between :dataInicio and :dataFim  ) \n");
		
		if(idsBibliotecas != null && idsBibliotecas.size() > 0) 
			sql.append("\t\t AND biblioteca.id_biblioteca in ( :idBibliotecas ) \n");
		
		if(idTipoEmprestimo > 0)
			sql.append("\t\t AND tipoEmprestimo.id_tipo_emprestimo = :idTipoEmprestimo \n");
		
		sql.append("\t GROUP BY emprestimo.data_emprestimo, tipoEmprestimo.id_tipo_emprestimo , tipoEmprestimo.descricao , biblioteca.id_biblioteca , biblioteca.descricao, material.codigo_barras, pessoaOperador.nome, usuarioPegou "+( agruparPorTurno ? ", turno \n": " \n") );
		
		
		//////////////////// Conta as renovações //////////////////////////
		
		sql.append( "\t UNION ALL ( \n" );
		
				sql.append("\t SELECT \n" +
						"\t\t porrogacaoEmprestimo.data_cadastro, \n"+
						"\t\t tipoEmprestimo.id_tipo_emprestimo as idTipo, \n"+ 
						"\t\t tipoEmprestimo.descricao as descricaoTipo, \n"+
						"\t\t biblioteca.id_biblioteca as idBiblioteca, \n"+ 
						"\t\t biblioteca.descricao as desBiblioteca, \n"+ 
						"\t\t material.codigo_barras as codigoMaterial, \n"+
						"\t\t pessoaOperador.nome as usuarioEmprestou, \n"+ 
						"\t\t COALESCE(pessoa.nome, bib.descricao) as usuarioPegou \n");
				
				if(agruparPorTurno){
					sql.append(projecaoTurno);
				}       
		
				sql.append("\t FROM \n" +
						"\t\t biblioteca.prorrogacao_emprestimo porrogacaoEmprestimo \n" );
		
				sql.append("\t\t INNER JOIN biblioteca.emprestimo emprestimo on emprestimo.id_emprestimo = porrogacaoEmprestimo.id_emprestimo \n"+
						innerJoins);
				
				if(agruparPorTurno)
						sql.append( "\t\t INNER JOIN comum.registro_entrada re ON (re.id_entrada = porrogacaoEmprestimo.id_registro_cadastro) \n" );
				
				sql.append("\t WHERE \n" +
						"\t\t porrogacaoEmprestimo.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO + " \n" +
						"\t\t AND emprestimo.ativo = trueValue() \n"+
						"\t\t AND ( porrogacaoEmprestimo.data_cadastro between :dataInicio and :dataFim  ) \n");
				
				if(agruparPorTurno)
					sql.append(	"\t\t AND re.canal = '"+RegistroEntrada.CANAL_DESKTOP +"' \n"); // Apenas renovações presenciais, porque nos relatórios por turno eles querem saber a demanda de usuários na biblioteca. 
						 
				if(idsBibliotecas != null && idsBibliotecas.size() > 0) 
					sql.append("\t\t AND biblioteca.id_biblioteca in ( :idBibliotecas ) \n");
				
				if(idTipoEmprestimo > 0)
					sql.append("\t\t AND tipoEmprestimo.id_tipo_emprestimo = :idTipoEmprestimo \n");
				
				sql.append("\t GROUP BY porrogacaoEmprestimo.data_cadastro, tipoEmprestimo.id_tipo_emprestimo , tipoEmprestimo.descricao , biblioteca.id_biblioteca , biblioteca.descricao, material.codigo_barras, pessoaOperador.nome, usuarioPegou "+( agruparPorTurno ? ", turno \n": " \n") );		
				
		sql.append( "\t ) \n ) as interna \n" );

		if (isCount) {
			sql = new StringBuilder("" +
					"SELECT count(*) FROM (" +
					sql.toString() + 
					") time");
		} else {
			sql.append("GROUP BY interna.data_emprestimo, interna.idTipo, interna.descricaoTipo, interna.idBiblioteca , interna.desBiblioteca, interna.codigoMaterial, interna.usuarioEmprestou, interna.usuarioPegou " +( agruparPorTurno ? ", interna.turno \n": " \n")+
					   "ORDER BY idbiblioteca "+( agruparPorTurno ? ", turno ": " ")+", data_emprestimo desc, idTipo, codigoMaterial DESC \n");
		}

		
		Query qEmprestimos = getSession().createSQLQuery(sql.toString());
		qEmprestimos.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 0) );
		qEmprestimos.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(dataFim, 23, 59, 59, 999));
		
		//qEmprestimos.setMaxResults(1000);
		
		if(idTipoEmprestimo > 0)
			qEmprestimos.setParameter("idTipoEmprestimo", idTipoEmprestimo);
		
		if(idsBibliotecas != null && idsBibliotecas.size() > 0) 
			qEmprestimos.setParameterList("idBibliotecas", idsBibliotecas);
		
		//@SuppressWarnings("unchecked")
		//List<Object> resultadosEmprestimos = qEmprestimos.list();
		//resultados.addAll( resultadosEmprestimos  );
		
		//return resultados;
				
		if(!isCount){
			qEmprestimos.setFirstResult((pagina - 1) * limite);
			qEmprestimos.setMaxResults(limite);
		}
		
		if(isCount) {
			return qEmprestimos.uniqueResult();
		} else {
			return qEmprestimos.list();
		}
		
	}
	
	
	/**
	 * <p>Conta os empréstimos e renovações por tipo de empréstimos no formato sintético.</p>
	 * 
	 * <p>Utilizando o relatório quantitativo por tipo de empréstimo</p>
	 *
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */	
	public List<Object> countEmprestimosPorTipoDeEmprestimoSintetico( Date dataInicio, Date dataFim, int idTipoEmprestimo, Collection<Integer> idsBibliotecas, boolean agruparPorTurno) throws DAOException {
		
		
		List<Object> resultados = new ArrayList<Object>();
		
		
		final String projecaoTurno  = " , \t\t ( CASE \n"+
        "\t\t\t WHEN EXTRACT(HOUR from emprestimo.data_emprestimo)  BETWEEN 0 AND 11 THEN "+ConstantesRelatorioBiblioteca.Turnos.MANHA.getValor()+" \n"+
        "\t\t\t WHEN EXTRACT(HOUR from emprestimo.data_emprestimo)  BETWEEN 12 AND 17 THEN "+ConstantesRelatorioBiblioteca.Turnos.TARDE.getValor()+" \n"+
        "\t\t\t WHEN EXTRACT(HOUR from emprestimo.data_emprestimo)  > 17 THEN "+ConstantesRelatorioBiblioteca.Turnos.NOITE.getValor()+" \n"+
        "\t\t\t ELSE -1   "+
   		"\t\t END )  as turno \n"; 
		
		final String innerJoins = "\t\t INNER JOIN biblioteca.material_informacional material on emprestimo.id_material = material.id_material_informacional \n"+
		        "\t\t INNER JOIN biblioteca.biblioteca biblioteca on material.id_biblioteca  =biblioteca.id_biblioteca \n"+
		        "\t\t INNER JOIN biblioteca.politica_emprestimo politicaEmprestimo on emprestimo.id_politica_emprestimo=politicaEmprestimo.id_politica_emprestimo \n"+
		        "\t\t INNER JOIN biblioteca.tipo_emprestimo tipoEmprestimo on politicaEmprestimo.id_tipo_emprestimo=tipoEmprestimo.id_tipo_emprestimo \n";
		
		
		StringBuilder sql = new StringBuilder("SELECT \n" +
				"\t sum(interna.quantidade) as quantidade, \n" +
				"\t interna.idTipo, \n" +
				"\t interna.descricaoTipo, \n" +
				"\t interna.idBiblioteca, \n" +
				"\t interna.desBiblioteca \n"+
				( agruparPorTurno ? ", \t interna.turno \n": "")+
				"FROM ( \n");
		sql.append("\t SELECT \n" +
				"\t\t count( DISTINCT emprestimo.id_emprestimo) as quantidade, \n"+
				"\t\t tipoEmprestimo.id_tipo_emprestimo as idTipo, \n"+ 
				"\t\t tipoEmprestimo.descricao as descricaoTipo, \n"+
				"\t\t biblioteca.id_biblioteca as idBiblioteca, \n"+ 
				"\t\t biblioteca.descricao as desBiblioteca \n");
		
		if(agruparPorTurno){
			sql.append(projecaoTurno);
		}       
		
		sql.append("\t FROM \n" +
				"\t\t biblioteca.emprestimo emprestimo \n"+
				innerJoins+
		        "\t WHERE \n" +
		        "\t\t emprestimo.ativo = trueValue() \n");
		
		sql.append("\t\t AND (   emprestimo.data_emprestimo between :dataInicio and :dataFim  ) \n");
		
		if(idsBibliotecas != null && idsBibliotecas.size() > 0) 
			sql.append("\t\t AND biblioteca.id_biblioteca in ( :idBibliotecas ) \n");
		
		if(idTipoEmprestimo > 0)
			sql.append("\t\t AND tipoEmprestimo.id_tipo_emprestimo = :idTipoEmprestimo \n");
		
		sql.append("\t GROUP BY tipoEmprestimo.id_tipo_emprestimo , tipoEmprestimo.descricao , biblioteca.id_biblioteca , biblioteca.descricao "+( agruparPorTurno ? ", turno \n": " \n") );
		
		
		//////////////////// Conta as renovações //////////////////////////
		
		sql.append( "\t UNION ALL ( \n" );
		
				sql.append("\t SELECT \n" +
						"\t\t count( DISTINCT porrogacaoEmprestimo.id_prorrogacao_emprestimo) as quantidade, \n"+
						"\t\t tipoEmprestimo.id_tipo_emprestimo as idTipo, \n"+ 
						"\t\t tipoEmprestimo.descricao as descricaoTipo, \n"+
						"\t\t biblioteca.id_biblioteca as idBiblioteca, \n"+ 
						"\t\t biblioteca.descricao as desBiblioteca \n");
				
				if(agruparPorTurno){
					sql.append(projecaoTurno);
				}       
		
				sql.append("\t FROM \n" +
						"\t\t biblioteca.prorrogacao_emprestimo porrogacaoEmprestimo \n" );
		
				sql.append("\t\t INNER JOIN biblioteca.emprestimo emprestimo on emprestimo.id_emprestimo = porrogacaoEmprestimo.id_emprestimo \n"+
						innerJoins);
				
				if(agruparPorTurno)
						sql.append( "\t\t INNER JOIN comum.registro_entrada re ON (re.id_entrada = porrogacaoEmprestimo.id_registro_cadastro) \n" );
				
				sql.append("\t WHERE \n" +
						"\t\t porrogacaoEmprestimo.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO + " \n" +
						"\t\t AND emprestimo.ativo = trueValue() \n"+
						"\t\t AND ( porrogacaoEmprestimo.data_cadastro between :dataInicio and :dataFim  ) \n");
				
				if(agruparPorTurno)
					sql.append(	"\t\t AND re.canal = '"+RegistroEntrada.CANAL_DESKTOP +"' \n"); // Apenas renovações presenciais, porque nos relatórios por turno eles querem saber a demanda de usuários na biblioteca. 
						 
				if(idsBibliotecas != null && idsBibliotecas.size() > 0) 
					sql.append("\t\t AND biblioteca.id_biblioteca in ( :idBibliotecas ) \n");
				
				if(idTipoEmprestimo > 0)
					sql.append("\t\t AND tipoEmprestimo.id_tipo_emprestimo = :idTipoEmprestimo \n");
				
				sql.append("\t GROUP BY tipoEmprestimo.id_tipo_emprestimo , tipoEmprestimo.descricao , biblioteca.id_biblioteca , biblioteca.descricao   "+( agruparPorTurno ? ", turno \n": " \n") );		
				
		sql.append( "\t ) \n ) as interna \n" );
		
		sql.append("GROUP BY interna.idTipo, interna.descricaoTipo, interna.idBiblioteca , interna.desBiblioteca" +( agruparPorTurno ? ", interna.turno \n": " \n")+
				   "ORDER BY idbiblioteca "+( agruparPorTurno ? ", turno ": " ")+" , quantidade DESC \n");

		
		Query qEmprestimos = getSession().createSQLQuery(sql.toString());
		qEmprestimos.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 0) );
		qEmprestimos.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(dataFim, 23, 59, 59, 999));
		
		if(idTipoEmprestimo > 0)
			qEmprestimos.setParameter("idTipoEmprestimo", idTipoEmprestimo);
		
		if(idsBibliotecas != null && idsBibliotecas.size() > 0) 
			qEmprestimos.setParameterList("idBibliotecas", idsBibliotecas);
		
		@SuppressWarnings("unchecked")
		List<Object> resultadosEmprestimos = qEmprestimos.list();
		resultados.addAll( resultadosEmprestimos  );
		
		return resultados;
		
	}
}
