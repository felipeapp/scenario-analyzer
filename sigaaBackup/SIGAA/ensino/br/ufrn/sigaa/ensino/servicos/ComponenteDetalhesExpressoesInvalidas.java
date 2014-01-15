/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 09/07/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.servicos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;

/**
 * Classe respons�vel por analisar e retornar os {@link ComponenteDetalhes}, que possuem a express�es de 
 * Equival�ncia, pr�-requisito, e co-requisito de forma invalida, realizando a corre��o destas express�es.
 * 
 * @author SIGAA
 *
 */
public class ComponenteDetalhesExpressoesInvalidas {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		
		Connection con = null;
		Collection<ComponenteDetalhes> componenteDetalhesInvalidos = new ArrayList<ComponenteDetalhes>();
		ComponenteDetalhes cDetalhe = new ComponenteDetalhes();
		try {
			con = DriverManager.getConnection("jdbc:postgresql://bddesenv.info.ufrn.br:5432/sigaa_desenv", "sigaa", "sigaa");
			con.setAutoCommit(false);
			
			String sql = " select ccd.id_componente_detalhes, ccd.id_componente, ccd.codigo, ccd.equivalencia, ccd.pre_requisito, ccd.co_requisito" +
					" from ensino.componente_curricular_detalhes ccd " +
					" where ccd.equivalencia ~ '(A|B|C|D|F|G|H|I|J|K|L|M|N|P|Q|R|S|T|V)'" +
					" or ccd.pre_requisito ~ '(A|B|C|D|F|G|H|I|J|K|L|M|N|P|Q|R|S|T|V)'" +
					" or ccd.co_requisito ~ '(A|B|C|D|F|G|H|I|J|K|L|M|N|P|Q|R|S|T|V)'" +
					" order by ccd.codigo";
			
			Statement st = con.createStatement();
			
			ResultSet resul = st.executeQuery(sql);
			
			while(resul.next()){
				cDetalhe.setId(resul.getInt("id_componente_detalhes"));
				cDetalhe.setCodigo(resul.getString("codigo"));
				cDetalhe.setEquivalencia(resul.getString("equivalencia") != null ? resul.getString("equivalencia") : "");
				cDetalhe.setPreRequisito(resul.getString("pre_requisito") !=null ? resul.getString("pre_requisito") : "");
				cDetalhe.setCoRequisito(resul.getString("co_requisito") != null ? resul.getString("co_requisito") : "");
				
				componenteDetalhesInvalidos.add(cDetalhe);
				cDetalhe = new ComponenteDetalhes();
			}
			
		
		
		
			ComponenteCurricular cc = new ComponenteCurricular();
			cc.setNivel(NivelEnsino.GRADUACAO);
			
			boolean somenteAtivos = true;
		
			for (ComponenteDetalhes detalhe : componenteDetalhesInvalidos) {
				
				if (detalhe.getPreRequisito() != null && !detalhe.getPreRequisito().trim().equals("")) {
					try {
						String expP = BuildExpressaoToDB(detalhe.getPreRequisito(), somenteAtivos, con);
						detalhe.setPreRequisito(expP != null ? expP : "" );
					} catch (Exception e) {
						System.out.println("# Express�o Inv�lida. N�o Foi Poss�vel converter a Express�o de Pr�-Requisito para o componente "+detalhe.getCodigo()+".");
					}
				}
				
				if (detalhe.getCoRequisito() != null && !detalhe.getCoRequisito().trim().equals("")) {
					try {
						String expC = BuildExpressaoToDB(detalhe.getCoRequisito(),somenteAtivos, con);
						detalhe.setCoRequisito(expC != null ? expC : "" );
					} catch (Exception e) {
						System.out.println("# Express�o Inv�lida. N�o Foi Poss�vel converter a Express�o de C�-Requisito para o componente "+detalhe.getCodigo()+".");
					}
				}
	
				if (detalhe.getEquivalencia() != null && !detalhe.getEquivalencia().trim().equals("")) {
					try {
						String expE = BuildExpressaoToDB(detalhe.getEquivalencia(), somenteAtivos, con);
						detalhe.setEquivalencia(expE != null ? expE : "");
					} catch (Exception e) {
						System.out.println("# Express�o Inv�lida. N�o Foi Poss�vel converter a Express�o de Equival�ncia para o componente "+detalhe.getCodigo()+".");
					}
				}
				
				System.out.println( "C�digo: " + detalhe.getCodigo());
				System.out.println( "Equivalencia: " + detalhe.getEquivalencia());
				System.out.println( "Pre-requisitos: " + detalhe.getPreRequisito());
				System.out.println( "Co-requisitos: " + detalhe.getCoRequisito()+"\n\n");
				
				try {
					String updateComponenteDetalhe = "update ensino.componente_curricular_detalhes  set " +
					   " equivalencia = '"+detalhe.getEquivalencia()+"', " +
					   " pre_requisito = '"+detalhe.getPreRequisito()+"', " +
					   " co_requisito = '"+detalhe.getCoRequisito()+"' " +
					   " where id_componente_detalhes = "+detalhe.getId();
					Statement stComponenteDetalhe  = con.createStatement();
					
					stComponenteDetalhe.executeUpdate(updateComponenteDetalhe);
					con.commit();

				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} 
		}catch (SQLException e1) {
			e1.printStackTrace();
		} finally{
			if(con != null)
				con.close();
		}

	}
	
	/**
	 * M�todo respons�vel pela convers�o dos valores das express�es armazenando os Id's do componente ao inv�s do c�digo.
	 * @param expressao
	 * @param somenteAtivos
	 * @return
	 * @throws ArqException
	 * @throws SQLException
	 */
	public static String BuildExpressaoToDB(String expressao, boolean somenteAtivos, Connection con) throws ArqException, SQLException {
		Map<String, Integer> mapa = ExpressaoUtil.expressaoToMapa(expressao);
		
		for (String k : mapa.keySet()) {
			Integer id = FindIdByCodigo(k, con);
			if (id == null)
				System.out.println("N�o existe componente com esse c�digo: " + k);
			else
				mapa.put(k, id);
		}
		return ExpressaoUtil.compileExpressao(expressao, mapa);
	}
	
	/**
	 * M�todo respons�vel por retornar o Id do {@link ComponenteDetalhes} com o c�digo passado por par�metro.
	 * @param codigo
	 * @return
	 * @throws SQLException
	 */
	public static Integer FindIdByCodigo(String codigo, Connection con) throws SQLException{
		
		String sql = " select cc.id_disciplina " +
				" from ensino.componente_curricular cc " +
				" where cc.codigo =  '" +codigo + "'" +
				" and cc.ativo = trueValue()";
		
		Statement st = con.createStatement();
		
		ResultSet resul = st.executeQuery(sql);
		
		Integer id_componente = null;
		
		while(resul.next()){
			id_componente = (resul.getInt("id_disciplina"));
		}
		
		return id_componente;
	}

}
