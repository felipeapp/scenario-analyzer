/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.OrdenacaoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p>Dao para a consulta específica do relatório.</p>
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
 * @since 16/04/2013
 *
 */
public class RelatorioUsuariosFinalizadosComPedenciasBibliotecaDao extends GenericSigaaDAO{

	
	
	/**
	 * Retorna todos empréstimos ativos e/ou atrasados das bibliotecas e categorias de usuários passados para
	 * a composição do relatório de usuários com empréstimos em atraso ou de empréstimos ativos.
	 */
	public List<Object []> findDiscenteFinalizadosComPendenciaNaBiblioteca(Collection<Integer> idBibliotecas, Collection<Integer> categoriasDeUsuario, OrdenacaoRelatoriosBiblioteca ordenacao) throws DAOException {
				
		
		StringBuilder sql = new StringBuilder(
			" SELECT \n"+
			" bMaterial.descricao AS nome_biblioteca_emprestimo, \n"+
			" ub.vinculo, \n"+
			" m.codigo_barras, \n" +
			" c.autor, \n" +
			" c.titulo, \n" +
			" discente.matricula, \n" +
			" p.nome as nome, \n" +
			" emprestimo.data_emprestimo, \n" +
			" emprestimo.prazo \n");			
			
		sql.append(
			" FROM biblioteca.emprestimo emprestimo \n"+
			" INNER JOIN biblioteca.usuario_biblioteca  AS ub        ON ub.id_usuario_biblioteca = emprestimo.id_usuario_biblioteca \n" +
			" INNER JOIN discente AS discente                        ON discente.id_discente = ub.identificacao_vinculo "+
			" INNER JOIN comum.pessoa AS p                           ON p.id_pessoa = ub.id_pessoa \n" +
			" INNER JOIN biblioteca.material_informacional AS m      ON m.id_material_informacional = emprestimo.id_material \n"+
			" INNER JOIN biblioteca.biblioteca AS bMaterial          ON bMaterial.id_biblioteca = m.id_biblioteca \n"+
			" LEFT JOIN biblioteca.exemplar   AS e                   ON e.id_exemplar = m.id_material_informacional \n"+
			" LEFT JOIN biblioteca.fasciculo  AS f                   ON f.id_fasciculo = m.id_material_informacional \n"+
			" LEFT JOIN biblioteca.assinatura AS a                   ON a.id_assinatura = f.id_assinatura \n"+
			" LEFT JOIN biblioteca.cache_entidades_marc AS c         ON ( c.id_titulo_catalografico = e.id_titulo_catalografico OR c.id_titulo_catalografico = a.id_titulo_catalografico ) \n");
		
		sql.append(" WHERE emprestimo.situacao =  " +Emprestimo.EMPRESTADO+" \n");  // emprestitmos abertos
		
		if( idBibliotecas != null && ! idBibliotecas.isEmpty() )
			sql.append(" AND bMaterial.id_biblioteca IN ("+ StringUtils.join(idBibliotecas, ',') + ") \n");
		
		if( ! categoriasDeUsuario.isEmpty() ) {                             // usuários da categoria escolhida
			

			// Filtra apenas os vínculo de discente selecionados pelo usuário //
			List<VinculoUsuarioBiblioteca> vinculosDiscenteSelecionados = new ArrayList<VinculoUsuarioBiblioteca>();
			
			VinculoUsuarioBiblioteca[] vinculos = VinculoUsuarioBiblioteca.getVinculosAluno();
			
			for (int i = 0; i < vinculos.length; i++) {
				if(categoriasDeUsuario.contains(vinculos[i].getValor()))
					vinculosDiscenteSelecionados.add(vinculos[i]);
			}
			
			if(vinculosDiscenteSelecionados.size() > 0)
				sql.append(" AND ub.vinculo IN " + UFRNUtils.gerarStringIn( vinculosDiscenteSelecionados ));
			else
				sql.append(" AND ub.vinculo = -1 ");  // se não selecionou vínculo discente, senão recupera nada
		}
		
		/* 
		 * Que NÃO estão mais ativos
		 */
		sql.append("AND discente.status NOT IN "+ UFRNUtils.gerarStringIn( new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getStatusDiscenteUtilizarBiblioteca() ) );
		
		sql.append("ORDER BY bMaterial.id_biblioteca, ub.vinculo"); // ordenação fixa 
		
		if(ordenacao == OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME)
			sql.append(",  p."+ordenacao.getCampoOrdenacao()+", p.id_pessoa, prazo");
		if(ordenacao == OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_PRAZO)
			sql.append(", "+ordenacao.getCampoOrdenacao()+",  p.nome, p.id_pessoa ");

		
		Query q = getSession().createSQLQuery(sql.toString());
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = q.list();
		
		
		return rs;
	}
	
	
	
	/**
	 * Retorna todos empréstimos ativos e/ou atrasados das bibliotecas e categorias de usuários passados para
	 * a composição do relatório de usuários com empréstimos em atraso ou de empréstimos ativos.
	 */
	public List<Object []> findServidoresFinalizadosComPendenciaNaBiblioteca(Collection<Integer> idBibliotecas, Collection<Integer> categoriasDeUsuario, OrdenacaoRelatoriosBiblioteca ordenacao) throws DAOException {
				
		
		StringBuilder sql = new StringBuilder(
			" SELECT \n"+
			" bMaterial.descricao AS nome_biblioteca_emprestimo, \n"+
			" ub.vinculo, \n"+
			" m.codigo_barras, \n" +
			" c.autor, \n" +
			" c.titulo, \n" +
			" servidor.siape, \n" +
			" p.nome as nome, \n" +
			" emprestimo.data_emprestimo, \n" +
			" emprestimo.prazo \n");			
			
		sql.append(
			" FROM biblioteca.emprestimo emprestimo \n"+
			" INNER JOIN biblioteca.usuario_biblioteca  AS ub        ON ub.id_usuario_biblioteca = emprestimo.id_usuario_biblioteca \n" +
			" INNER JOIN rh.servidor AS servidor                     ON servidor.id_servidor = ub.identificacao_vinculo  "+
			" INNER JOIN rh.ativo AS ativo                      	 ON servidor.id_ativo = ativo.id_ativo "+
			" INNER JOIN comum.pessoa AS p                           ON p.id_pessoa = ub.id_pessoa \n" +
			" INNER JOIN biblioteca.material_informacional AS m      ON m.id_material_informacional = emprestimo.id_material \n"+
			" INNER JOIN biblioteca.biblioteca AS bMaterial          ON bMaterial.id_biblioteca = m.id_biblioteca \n"+
			" LEFT JOIN biblioteca.exemplar   AS e                   ON e.id_exemplar = m.id_material_informacional \n"+
			" LEFT JOIN biblioteca.fasciculo  AS f                   ON f.id_fasciculo = m.id_material_informacional \n"+
			" LEFT JOIN biblioteca.assinatura AS a                   ON a.id_assinatura = f.id_assinatura \n"+
			" LEFT JOIN biblioteca.cache_entidades_marc AS c         ON ( c.id_titulo_catalografico = e.id_titulo_catalografico OR c.id_titulo_catalografico = a.id_titulo_catalografico ) \n");
		
		sql.append(" WHERE emprestimo.situacao =  " +Emprestimo.EMPRESTADO+" \n");  // emprestitmos abertos
		
		if( idBibliotecas != null && ! idBibliotecas.isEmpty() )
			sql.append(" AND bMaterial.id_biblioteca IN ("+ StringUtils.join(idBibliotecas, ',') + ") \n");
		
		if( ! categoriasDeUsuario.isEmpty() ){                               // usuários da categoria escolhida
			
			// Filtra apenas os vínculo de servidores selecionados pelo usuário //
			List<VinculoUsuarioBiblioteca> vinculosServidoresSelecionados = new ArrayList<VinculoUsuarioBiblioteca>();
			
			VinculoUsuarioBiblioteca[] vinculos = VinculoUsuarioBiblioteca.getVinculosServidor();
			
			for (int i = 0; i < vinculos.length; i++) {
				if(categoriasDeUsuario.contains(vinculos[i].getValor()))
					vinculosServidoresSelecionados.add(vinculos[i]);
			}
			if(vinculosServidoresSelecionados.size() > 0)
				sql.append(" AND ub.vinculo IN " + UFRNUtils.gerarStringIn( vinculosServidoresSelecionados ));
			else
				sql.append(" AND ub.vinculo = -1 ");  // se não selecionou vínculo servidor, senão recupera nada
		}
		
		/* 
		 * Que NÃO estão mais ativos 
		 */
		sql.append("AND ativo.id_ativo NOT IN "+ UFRNUtils.gerarStringIn( new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getStatusServidorUtilizarBiblioteca() ) );
		
		sql.append("ORDER BY bMaterial.id_biblioteca, ub.vinculo"); // ordenação fixa 
		
		if(ordenacao == OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME)
			sql.append(",  p."+ordenacao.getCampoOrdenacao()+", p.id_pessoa, prazo");
		if(ordenacao == OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_PRAZO)
			sql.append(", "+ordenacao.getCampoOrdenacao()+",  p.nome, p.id_pessoa ");
		

		
		Query q = getSession().createSQLQuery(sql.toString());
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = q.list();
		
		
		return rs;
	}
	
	
	/**
	 * Retorna todos empréstimos ativos e/ou atrasados das bibliotecas e categorias de usuários passados para
	 * a composição do relatório de usuários com empréstimos em atraso ou de empréstimos ativos.
	 */
	public List<Object []> findBibliotecasFinalizadosComPendenciaNaBiblioteca(Collection<Integer> idBibliotecas, Collection<Integer> categoriasDeUsuario, OrdenacaoRelatoriosBiblioteca ordenacao) throws DAOException {
				
		
		StringBuilder sql = new StringBuilder(
			" SELECT \n"+
			" bMaterial.descricao AS nome_biblioteca_emprestimo, \n"+
			" ub.vinculo, \n"+
			" m.codigo_barras, \n" +
			" c.autor, \n" +
			" c.titulo, \n" +
			" biblioteca.identificador, \n" +
			" biblioteca.descricao as nome, \n" +
			" emprestimo.data_emprestimo, \n" +
			" emprestimo.prazo \n");			
			
		sql.append(
			" FROM biblioteca.emprestimo emprestimo \n"+
			" INNER JOIN biblioteca.usuario_biblioteca  AS ub        ON ub.id_usuario_biblioteca = emprestimo.id_usuario_biblioteca \n" +
			" INNER JOIN biblioteca.biblioteca AS biblioteca         ON biblioteca.id_biblioteca = ub.identificacao_vinculo  "+
			" INNER JOIN biblioteca.material_informacional AS m      ON m.id_material_informacional = emprestimo.id_material \n"+
			" INNER JOIN biblioteca.biblioteca AS bMaterial          ON bMaterial.id_biblioteca = m.id_biblioteca \n"+
			" LEFT JOIN biblioteca.exemplar   AS e                   ON e.id_exemplar = m.id_material_informacional \n"+
			" LEFT JOIN biblioteca.fasciculo  AS f                   ON f.id_fasciculo = m.id_material_informacional \n"+
			" LEFT JOIN biblioteca.assinatura AS a                   ON a.id_assinatura = f.id_assinatura \n"+
			" LEFT JOIN biblioteca.cache_entidades_marc AS c         ON ( c.id_titulo_catalografico = e.id_titulo_catalografico OR c.id_titulo_catalografico = a.id_titulo_catalografico ) \n");
		
		sql.append(" WHERE emprestimo.situacao =  " +Emprestimo.EMPRESTADO+" \n");  // emprestitmos abertos
		
		if( idBibliotecas != null && ! idBibliotecas.isEmpty() )
			sql.append(" AND bMaterial.id_biblioteca IN ("+ StringUtils.join(idBibliotecas, ',') + ") \n");
		
		if( ! categoriasDeUsuario.isEmpty() ){                               // usuários da categoria escolhida
			
			// Filtra apenas os vínculo de servidores selecionados pelo usuário //
			List<VinculoUsuarioBiblioteca> vinculosBibliotecasSelecionados = new ArrayList<VinculoUsuarioBiblioteca>();
			
			VinculoUsuarioBiblioteca[] vinculos = VinculoUsuarioBiblioteca.getVinculosBibliotecas();
			
			for (int i = 0; i < vinculos.length; i++) {
				if(categoriasDeUsuario.contains(vinculos[i].getValor()))
					vinculosBibliotecasSelecionados.add(vinculos[i]);
			}
			if(vinculosBibliotecasSelecionados.size() > 0)
				sql.append(" AND ub.vinculo IN " + UFRNUtils.gerarStringIn( vinculosBibliotecasSelecionados ));
			else
				sql.append(" AND ub.vinculo = -1 ");  // se não selecionou vínculo biblioteca, senão recupera nada
		}
		
		/* 
		 * Que NÃO estão mais ativos 
		 */
		sql.append("AND biblioteca.ativo = falseValue() " );
		
		sql.append("ORDER BY bMaterial.id_biblioteca, ub.vinculo"); // ordenação fixa 
		
		if(ordenacao == OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME)
			sql.append(", biblioteca.descricao, biblioteca.id_biblioteca, prazo ");
		if(ordenacao == OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_PRAZO)
			sql.append(", prazo, biblioteca.descricao, biblioteca.id_biblioteca");
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = q.list();
		
		
		return rs;
	}
	
	
	
}
