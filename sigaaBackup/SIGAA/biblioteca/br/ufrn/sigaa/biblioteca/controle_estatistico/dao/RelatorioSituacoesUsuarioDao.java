/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 27/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca.StatusMulta;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.RelatorioSituacaoDosUsuarios;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p>Classe que contém as consultas utilizadas nos relatórios que busca usuários em uma determinada situação</p>
 * 
 * @author jadson
 *
 */
public class RelatorioSituacoesUsuarioDao extends GenericSigaaDAO{

	/**
	 * Retorna todos empréstimos ativos e/ou atrasados das bibliotecas e categorias de usuários passados para
	 * a composição do relatório de usuários com empréstimos em atraso ou de empréstimos ativos.
	 */
	public Map<String, Map<String, Map<String, List<RelatorioSituacaoDosUsuarios>>>> findUsuariosServidoresComEmprestimosAtivos (
			Collection<Integer> bibliotecas, Collection<Integer> categoriasDeUsuario, Collection<Integer> situacoesDeServidor, 
			Collection<Integer> unidades, Date dataInicial, Date dataFinal, boolean incluirAtrasados, boolean incluirAtivosNaoAtrasados, 
			int tipoDeMaterial, int tipoDeEmprestimo )
			throws DAOException {
		
		String periodo =
				"'" + CalendarUtils.format(dataInicial, "yyyy-MM-dd") + " 00:00:00' AND " +
				"'" + CalendarUtils.format(dataFinal, "yyyy-MM-dd") + " 23:59:59' ";
		
		Map<String,Map<String,Map<String,List<RelatorioSituacaoDosUsuarios>>>> resultados =
				new TreeMap<String, Map<String, Map<String, List<RelatorioSituacaoDosUsuarios>>>>();
		
		String categoriasFrom = "";		
		String categoriasWhere = "";
		
		if (! categoriasDeUsuario.isEmpty() ) {
			categoriasFrom += "\t LEFT JOIN rh.ativo AS ativo ON ativo.id_ativo = serv.id_ativo \n";
			
			categoriasWhere += "\t AND ub.vinculo IN (" + StringUtils.join( categoriasDeUsuario, ',' ) + ") \n";
			
			if (situacoesDeServidor != null && situacoesDeServidor.size() > 0) {
				categoriasWhere += "\t AND ativo.id_ativo IN (" + StringUtils.join( situacoesDeServidor, ',' ) + ") \n";
			}

			if (unidades != null && unidades.size() > 0) {
				categoriasWhere += "\t AND unid.id_unidade IN (" + StringUtils.join( unidades, ',' ) + ") \n";
			}
		}
		
		String sql =
			"SELECT \n"+
			"\t em.id_emprestimo, \n"+
			"\t ub.id_usuario_biblioteca, \n" +
			"\t p.nome, \n" +
			"\t b.descricao AS nome_biblioteca, \n" +
			"\t coalesce(unid.nome, 'Sem Unidade') AS nome_unidade, \n" +
			"\t p.cpf_cnpj, \n" +
			"\t em.data_emprestimo, \n" +
			"\t em.prazo, \n" +
			"\t m.codigo_barras, \n" +
			"\t c.titulo, \n" +
			"\t c.autor, \n" +
			"\t ueb.id_usuario_externo_biblioteca, \n" +
			"\t disc.matricula, \n" +
			"\t serv.siape, \n" +
			"\t ub.vinculo, \n" +
			"\t b2.descricao AS nome_biblioteca_emprestimo \n" +
			"FROM \n" +
			"\t biblioteca.emprestimo em \n";
			
			if(tipoDeEmprestimo != 0){
				sql += "\t INNER JOIN biblioteca.politica_emprestimo AS pol USING (id_politica_emprestimo) \n";
			}
			
			sql += "\t INNER JOIN biblioteca.usuario_biblioteca  AS ub ON ub.id_usuario_biblioteca = em.id_usuario_biblioteca \n" +
			"\t LEFT JOIN biblioteca.usuario_externo_biblioteca AS ueb ON ueb.id_usuario_biblioteca = ub.id_usuario_biblioteca \n"+
			"\t LEFT JOIN comum.pessoa AS p ON p.id_pessoa = ub.id_pessoa \n" +
			"\t LEFT JOIN biblioteca.biblioteca AS b ON b.id_biblioteca = ub.id_biblioteca \n"+
			"\t LEFT JOIN biblioteca.material_informacional AS m ON m.id_material_informacional = em.id_material \n"+
			"\t LEFT JOIN biblioteca.biblioteca AS b2 ON b2.id_biblioteca = m.id_biblioteca \n"+
			"\t LEFT JOIN biblioteca.exemplar   AS e  ON e.id_exemplar = m.id_material_informacional \n"+
			"\t LEFT JOIN biblioteca.fasciculo  AS f  ON f.id_fasciculo = m.id_material_informacional \n"+
			"\t LEFT JOIN biblioteca.assinatura AS a  ON a.id_assinatura = f.id_assinatura \n"+
			"\t LEFT JOIN biblioteca.cache_entidades_marc AS c \n" +
			"\t\t ON c.id_titulo_catalografico = e.id_titulo_catalografico \n" +
			"\t\t OR c.id_titulo_catalografico = a.id_titulo_catalografico \n"+
			"\t LEFT JOIN public.discente AS disc ON disc.id_discente = ub.identificacao_vinculo \n" +
			"\t LEFT JOIN rh.servidor AS serv ON serv.id_servidor = ub.identificacao_vinculo \n" +
			"\t LEFT JOIN comum.unidade unid ON unid.id_unidade = serv.id_unidade \n" +
			categoriasFrom +
			"WHERE \n" +
			"\t ( falseValue() " +
			( incluirAtrasados ?
			" OR em.prazo < :hoje " : "" ) +
			( incluirAtivosNaoAtrasados ?
			" OR em.prazo >= :hoje " : "" ) +
			" ) \n" +
			"\t AND em.situacao =  " +Emprestimo.EMPRESTADO+" \n"+
			"\t AND ( em.data_emprestimo BETWEEN " + periodo + " ) \n" +
			( ! bibliotecas.isEmpty() ?
					"\t AND m.id_biblioteca IN ( "+ StringUtils.join(bibliotecas, ',') + " ) \n" : "" ) +
			categoriasWhere +
			( tipoDeEmprestimo != 0 ?
					"\t AND pol.id_tipo_emprestimo = " + tipoDeEmprestimo + " \n" : "" ) +
			( tipoDeMaterial != 0 ?
					"\t AND m.id_tipo_material = " + tipoDeMaterial + " \n" : "" ) +
			"ORDER BY nome, prazo";

		
		Query q = getSession().createSQLQuery(sql);
		q.setTimestamp("hoje", CalendarUtils.configuraTempoDaData(new Date(), 23, 59, 59, 999) );
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = q.list();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		for (Object [] r : rs) {
			
			RelatorioSituacaoDosUsuarios relatorio = new RelatorioSituacaoDosUsuarios();
			relatorio.setIdEmprestimo(Integer.parseInt(""+r[0]));
			relatorio.setId(Integer.parseInt(""+r[1]));
			relatorio.setNome(""+(r[2] != null ? r[2] : r[3] != null ? r[3] : ""));
			if (r[5] != null ) relatorio.setCpfCnpj(""+r[5]);
			
			try {
				relatorio.setDataEmprestimo(sdf.parse((""+r[6]).substring(0,19)));
				relatorio.setPrazo(sdf.parse((""+r[7]).substring(0,19)));
			} catch (ParseException e) { /*nunca ocorre*/ }
			
			relatorio.setCodigoBarras(""+r[8]);
			relatorio.setTitulo(""+r[9]);
			relatorio.setAutor(""+r[10]);
			
			relatorio.setUsuarioExterno(""+r[11] == null);
			
			int valorVinculo = Integer.parseInt(""+r[14]);
			
			VinculoUsuarioBiblioteca vinculo = VinculoUsuarioBiblioteca.getVinculo( valorVinculo );
			
			if ( vinculo.isVinculoAluno() )
				if ( r[12] != null ) relatorio.setMatricula("" + r[12]);
			
			if ( vinculo.isVinculoServidor() )
				if ( r[13] != null ) relatorio.setSiape("" + r[13]);
			
			String biblioteca = r[15].toString();
			String tipoUsuario = vinculo.getDescricao();
			String unidade = r[4].toString();

			Map<String, Map<String, List<RelatorioSituacaoDosUsuarios>>> res;
			Map<String, List<RelatorioSituacaoDosUsuarios>> res2;
			List<RelatorioSituacaoDosUsuarios> lista;
			
			if ( resultados.get(unidade) == null ) {
				res = new TreeMap<String, Map<String, List<RelatorioSituacaoDosUsuarios>>>();
				resultados.put(unidade, res);
			} else
				res = resultados.get(unidade);
			
			if ( res.get(tipoUsuario) == null ) {
				res2 = new TreeMap<String, List<RelatorioSituacaoDosUsuarios>>();
				res.put(tipoUsuario, res2);
			} else
				res2 = res.get(tipoUsuario);
			
			if ( res2.get(biblioteca) == null ) {
				lista = new ArrayList<RelatorioSituacaoDosUsuarios>();
				res2.put(biblioteca, lista);
			} else
				lista = res2.get(biblioteca);
			
			lista.add(relatorio);
		}
		
		return resultados;
	}

	/**
	 * Retorna todos empréstimos ativos e/ou atrasados das bibliotecas e categorias de usuários passados para
	 * a composição do relatório de usuários com empréstimos em atraso ou de empréstimos ativos.
	 */
	public Map<String, Map<String, List<RelatorioSituacaoDosUsuarios>>> findUsuariosComEmprestimosAtivos (
			Collection<Integer> bibliotecas, Collection<Integer> categoriasDeUsuario, 
			Date dataInicial, Date dataFinal, boolean incluirAtrasados, boolean incluirAtivosNaoAtrasados, 
			int tipoDeMaterial, int tipoDeEmprestimo )
			throws DAOException {
				
		String periodo =
				"'" + CalendarUtils.format(dataInicial, "yyyy-MM-dd") + " 00:00:00' AND " +
				"'" + CalendarUtils.format(dataFinal, "yyyy-MM-dd") + " 23:59:59' ";
		
		Map<String,Map<String,List<RelatorioSituacaoDosUsuarios>>> resultados =
				new TreeMap<String, Map<String, List<RelatorioSituacaoDosUsuarios>>>();
		
		String sql =
			"SELECT \n"+
			"\t em.id_emprestimo, \n"+
			"\t ub.id_usuario_biblioteca, \n" +
			"\t p.nome, \n" +
			"\t b.descricao AS nome_biblioteca, \n" +
			"\t p.cpf_cnpj, \n" +
			"\t em.data_emprestimo, \n" +
			"\t em.prazo, \n" +
			"\t m.codigo_barras, \n" +
			"\t c.titulo, \n" +
			"\t c.autor, \n" +
			"\t ueb.id_usuario_externo_biblioteca, \n" +
			"\t disc.matricula, \n" +
			"\t serv.siape, \n" +
			"\t ub.vinculo, \n" +
			"\t b2.descricao AS nome_biblioteca_emprestimo \n" +
			"FROM \n" +
			"\t biblioteca.emprestimo em \n";
			
			if(tipoDeEmprestimo != 0){
				sql += "\t INNER JOIN biblioteca.politica_emprestimo AS pol USING (id_politica_emprestimo) \n";
			}
			
			sql += "\t INNER JOIN biblioteca.usuario_biblioteca  AS ub ON ub.id_usuario_biblioteca = em.id_usuario_biblioteca \n" +
			"\t LEFT JOIN biblioteca.usuario_externo_biblioteca AS ueb ON ueb.id_usuario_biblioteca = ub.id_usuario_biblioteca \n"+
			"\t LEFT JOIN comum.pessoa AS p ON p.id_pessoa = ub.id_pessoa \n" +
			"\t LEFT JOIN biblioteca.biblioteca AS b ON b.id_biblioteca = ub.id_biblioteca \n"+
			"\t LEFT JOIN biblioteca.material_informacional AS m ON m.id_material_informacional = em.id_material \n"+
			"\t LEFT JOIN biblioteca.biblioteca AS b2 ON b2.id_biblioteca = m.id_biblioteca \n"+
			"\t LEFT JOIN biblioteca.exemplar   AS e  ON e.id_exemplar = m.id_material_informacional \n"+
			"\t LEFT JOIN biblioteca.fasciculo  AS f  ON f.id_fasciculo = m.id_material_informacional \n"+
			"\t LEFT JOIN biblioteca.assinatura AS a  ON a.id_assinatura = f.id_assinatura \n"+
			"\t LEFT JOIN biblioteca.cache_entidades_marc AS c \n" +
			"\t\t ON c.id_titulo_catalografico = e.id_titulo_catalografico \n" +
			"\t\t OR c.id_titulo_catalografico = a.id_titulo_catalografico \n"+
			"\t LEFT JOIN public.discente AS disc ON disc.id_discente = ub.identificacao_vinculo \n" +
			"\t LEFT JOIN rh.servidor AS serv ON serv.id_servidor = ub.identificacao_vinculo \n" +
			"WHERE \n" +
			"\t ( falseValue() " +
			( incluirAtrasados ?
			" OR em.prazo < :hoje " : "" ) +
			( incluirAtivosNaoAtrasados ?
			" OR em.prazo >= :hoje " : "" ) +
			" ) \n" +
			"\t AND em.situacao =  " +Emprestimo.EMPRESTADO+" \n"+
			"\t AND ( em.data_emprestimo BETWEEN " + periodo + " ) \n" +
			( ! bibliotecas.isEmpty() ?
					"\t AND m.id_biblioteca IN ( "+ StringUtils.join(bibliotecas, ',') + " ) \n" : "" ) +
			( ! categoriasDeUsuario.isEmpty() ?
					"\t AND ub.vinculo IN (" + StringUtils.join( categoriasDeUsuario, ',' ) + ") \n": "") + 
			( tipoDeEmprestimo != 0 ?
					"\t AND pol.id_tipo_emprestimo = " + tipoDeEmprestimo + " \n" : "" ) +
			( tipoDeMaterial != 0 ?
					"\t AND m.id_tipo_material = " + tipoDeMaterial + " \n" : "" ) +
			"ORDER BY nome, prazo";

		
		Query q = getSession().createSQLQuery(sql);
		q.setTimestamp("hoje", CalendarUtils.configuraTempoDaData(new Date(), 23, 59, 59, 999) );
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = q.list();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		for (Object [] r : rs) {
			
			RelatorioSituacaoDosUsuarios relatorio = new RelatorioSituacaoDosUsuarios();
			relatorio.setIdEmprestimo(Integer.parseInt(""+r[0]));
			relatorio.setId(Integer.parseInt(""+r[1]));
			relatorio.setNome(""+(r[2] != null ? r[2] : r[3] != null ? r[3] : ""));
			if (r[4] != null ) relatorio.setCpfCnpj(""+r[4]);
			
			try {
				relatorio.setDataEmprestimo(sdf.parse((""+r[5]).substring(0,19)));
				relatorio.setPrazo(sdf.parse((""+r[6]).substring(0,19)));
			} catch (ParseException e) { /*nunca ocorre*/ }
			
			relatorio.setCodigoBarras(""+r[7]);
			relatorio.setTitulo(""+r[8]);
			relatorio.setAutor(""+r[9]);
			
			relatorio.setUsuarioExterno(""+r[10] == null);
			
			int valorVinculo = Integer.parseInt(""+r[13]);
			
			VinculoUsuarioBiblioteca vinculo = VinculoUsuarioBiblioteca.getVinculo( valorVinculo );
			
			if ( vinculo.isVinculoAluno() )
				if ( r[11] != null ) relatorio.setMatricula("" + r[11]);
			
			if ( vinculo.isVinculoServidor() )
				if ( r[12] != null ) relatorio.setSiape("" + r[12]);
			
			String biblioteca = r[14].toString();
			String tipoUsuario = vinculo.getDescricao();

			Map<String, List<RelatorioSituacaoDosUsuarios>> res;
			List<RelatorioSituacaoDosUsuarios> lista;
						
			if ( resultados.get(tipoUsuario) == null ) {
				res = new TreeMap<String, List<RelatorioSituacaoDosUsuarios>>();
				resultados.put(tipoUsuario, res);
			} else
				res = resultados.get(tipoUsuario);
			
			if ( res.get(biblioteca) == null ) {
				lista = new ArrayList<RelatorioSituacaoDosUsuarios>();
				res.put(biblioteca, lista);
			} else
				lista = res.get(biblioteca);
			
			lista.add(relatorio);
		}
		
		return resultados;
	}
	
	
	/**
	 * <p> Retorna os usuários que estão suspensos da biblioteca. Além disso, para cada usuário, retorna
	 * os empréstimos entregues com atraso que causaram a suspensão. No caso de suspensão manual,
	 * as informações dela também são retornadas.
	 * 
	 * <p> <em>Observação:</em> para suspensões manuais  a suspensão não está ligada a uma biblioteca em particular.
	 * Então esses são retornados numa categoria geral.
	 * 
	 * @return um Map agrupado primeiro por categoria de usuário e depois por biblioteca
	 */
	public Map<String, Map<String, List<RelatorioSituacaoDosUsuarios> > > findUsuariosSuspensos (
			Collection<Integer> bibliotecas, Collection<Integer> categoriasDeUsuario, Date dataInicial, Date dataFinal,
			boolean incluirSuspensoesManuais )
			throws DAOException {
		
		Map<String, Map<String, List<RelatorioSituacaoDosUsuarios>>>
				r = new TreeMap<String, Map<String, List<RelatorioSituacaoDosUsuarios>>>();

		String sql =
				"SELECT " +
				"	usub.vinculo, " +
				"	bibl.descricao, " +
				"	empr.data_emprestimo, " +
				"	empr.prazo, " +
				"	empr.data_devolucao, " +
				"	susp.data_inicio, " +
				"	susp.data_fim, " +
				"	COALESCE(pessoa.nome, biblU.descricao) AS nome, " +
				"	usub.id_usuario_biblioteca, " +
				//	-- se foi causada por um empréstimo atrasado, isso aqui aparece
				"	empr.id_emprestimo, " +
				//	-- e se for manual:
				"	pesresp.nome AS nome_responsavel,  " +
				"	susp.motivo_cadastro," +
				"	matr.codigo_barras, " +
				"	cache.titulo, " +
				"	cache.autor, " +
				"	pessoa.cpf_cnpj," +
				"	susp.id_usuario_cadastro, " +
				"	susp.id_suspensao_usuario_biblioteca " +
				"FROM " +
				"	biblioteca.suspensao_usuario_biblioteca AS susp " +
				"	LEFT JOIN biblioteca.emprestimo AS empr ON susp.id_emprestimo = empr.id_emprestimo " +
				"	INNER JOIN biblioteca.usuario_biblioteca AS usub " +
				"		ON usub.id_usuario_biblioteca = COALESCE(susp.id_usuario_biblioteca, empr.id_usuario_biblioteca) " +
				"	LEFT JOIN comum.pessoa AS pessoa ON usub.id_pessoa = pessoa.id_pessoa " +
				"	LEFT JOIN biblioteca.biblioteca AS biblU ON biblU.id_biblioteca = usub.id_usuario_biblioteca " +
				//"	LEFT JOIN biblioteca.politica_emprestimo AS polempr " +
				//"		ON polempr.id_politica_emprestimo = empr.id_politica_emprestimo " +
				"	LEFT JOIN biblioteca.material_informacional AS matr " +
				"		ON matr.id_material_informacional = empr.id_material " +
				"	LEFT JOIN biblioteca.exemplar AS exem  ON exem.id_exemplar = matr.id_material_informacional "+
				"	LEFT JOIN biblioteca.fasciculo AS fasc  ON fasc.id_fasciculo = matr.id_material_informacional "+
				"	LEFT JOIN biblioteca.assinatura AS ass  ON ass.id_assinatura = fasc.id_assinatura "+
				"	LEFT JOIN biblioteca.cache_entidades_marc AS cache " +
				"		ON cache.id_titulo_catalografico = exem.id_titulo_catalografico " +
				"		OR cache.id_titulo_catalografico = ass.id_titulo_catalografico "+
				"	LEFT JOIN biblioteca.biblioteca AS bibl ON bibl.id_biblioteca = matr.id_biblioteca " +
				"	LEFT JOIN comum.usuario AS usuresp ON usuresp.id_usuario = susp.id_usuario_cadastro " +
				"	LEFT JOIN comum.pessoa AS pesresp ON pesresp.id_pessoa = usuresp.id_pessoa " +
				"WHERE " +
				//	-- suspensão ativa
				"	susp.data_fim >= :hoje " +
				"	AND susp.ativo = trueValue() " +
				//	-- data da suspensão: se a suspensão está dentro do intervalo de datas passado " +
				( dataInicial != null && dataFinal != null ?
				"	AND " +
				"		((susp.data_fim BETWEEN :dataInicial AND :dataFinal ) OR " +
				"		 ( :dataFinal BETWEEN susp.data_inicio AND susp.data_fim)) " : "" );
				//	-- biblioteca do empréstimo
				if( bibliotecas != null && bibliotecas.size() > 0 ){
					sql += " AND ( matr.id_biblioteca IN "+UFRNUtils.gerarStringIn(bibliotecas) ;
					if( incluirSuspensoesManuais){
						sql +=" OR matr.id_biblioteca IS NULL "; // suspensão manual não tem biblioteca. 
					}
					
					sql += " ) ";
				}
				
				//	-- categoria de usuário
				sql += ( categoriasDeUsuario != null && categoriasDeUsuario.size() > 0 ?
				"	AND ( usub.vinculo IS NULL OR usub.vinculo IN ("+ StringUtils.join(categoriasDeUsuario, ',') +") ) " : "" ) +
				( ! incluirSuspensoesManuais ?
				"	AND susp.id_emprestimo IS NOT NULL " : "" ) +
				"ORDER BY usub.vinculo, bibl.descricao, pessoa.nome, susp.data_inicio ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setDate("hoje", CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0));
		q.setDate("dataInicial", CalendarUtils.configuraTempoDaData(dataInicial, 0, 0, 0, 0) );
		q.setDate("dataFinal", CalendarUtils.configuraTempoDaData(dataFinal, 23, 59, 59, 999) );
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		
		for ( Object[] linha : lista ) {
			String catUsuario = linha[0] == null ? null : VinculoUsuarioBiblioteca.getVinculo((Integer) linha[0]).getDescricao();
			String biblioteca = linha[1] == null ? null : (String) linha[1];
			
			if ( catUsuario == null )
				catUsuario = "Nenhuma (suspensão manual)";
			if ( biblioteca == null )
				biblioteca = "Todas";
			
			Map<String, List<RelatorioSituacaoDosUsuarios>> s = r.get(catUsuario);
			if ( s == null ) {
				s = new TreeMap<String, List<RelatorioSituacaoDosUsuarios>>();
				r.put( catUsuario, s );
			}
			
			List<RelatorioSituacaoDosUsuarios> t = s.get(biblioteca);
			if ( t == null ) {
				t = new ArrayList<RelatorioSituacaoDosUsuarios>();
				s.put( biblioteca, t );
			}
			
			RelatorioSituacaoDosUsuarios dados = new RelatorioSituacaoDosUsuarios();
			dados.setPunicaoPorSuspensao(true);
			
			dados.setCategoriaDoUsuario(catUsuario);
			dados.setBiblioteca(biblioteca);
			try {
				dados.setDataEmprestimo( linha[2] == null ? null : sdf.parse( "" + linha[2] ) );
				dados.setPrazo( linha[3] == null ? null : sdf.parse( "" + linha[3] ) );
				dados.setDataDevolucao( linha[4] == null ? null : sdf.parse("" + linha[4] ) );
				dados.setInicioSuspensao( linha[5] == null ? null : sdf2.parse("" + linha[5] ) );
				dados.setPrazoSuspensao( linha[6] == null ? null : sdf2.parse("" + linha[6] ) );
			} catch ( ParseException e ) { new DAOException("Erro no passer das datas no relatório de usuário suspensos "); }
			
			dados.setNome( "" +linha[7] );
			dados.setIdEmprestimo( linha[9] == null ? 0 : Integer.parseInt( "" + linha[9] ) );
			if ( dados.getIdEmprestimo() == 0 ) {
				dados.setSuspensaoManualCadastradaPor( linha[10] == null ? null : ""+linha[10] );
				dados.setMotivoPunicao( linha[11] == null ? null : "" + linha[11] );
			} else {
				dados.setSuspensaoManualCadastradaPor( null );
				dados.setCodigoBarras("" + linha[12]);
				dados.setTitulo("" + linha[13]);
				dados.setAutor("" + linha[14]);
			}
			dados.setCpfCnpj( linha[15] == null ? null : "" + linha[15]);
			dados.setId( Integer.parseInt( "" + linha[8] ) );
			
			t.add(dados);
		}
		
		return r;
	}
	
	
	
	/**
	 * <p> Retorna os usuários que estão multados da biblioteca. Além disso, para cada usuário, retorna
	 * os empréstimos entregues com atraso que causaram a multa. No caso de multa manual,
	 * as informações dela também são retornadas.
	 * 
	 * <p> <em>Observação:</em> para multas manuais não está ligada a uma biblioteca em particular.
	 * 
	 * @return um Map agrupado primeiro por categoria de usuário e depois por biblioteca
	 */
	public Map<String, Map<String, List<RelatorioSituacaoDosUsuarios> > > findUsuariosMultados(
			Collection<Integer> bibliotecas, Collection<Integer> categoriasDeUsuario, boolean incluirMultasManuais )
			throws DAOException {
		
		Map<String, Map<String, List<RelatorioSituacaoDosUsuarios>>>
				r = new TreeMap<String, Map<String, List<RelatorioSituacaoDosUsuarios>>>();

		String sql =
				"SELECT " +
				"	usub.vinculo, " +
				"	bibl.descricao, " +
				"	empr.data_emprestimo, " +
				"	empr.prazo, " +
				"	empr.data_devolucao, " +
				"	multa.valor, " +
				"	COALESCE(pessoa.nome, biblU.descricao) AS nome, " +
				"	usub.id_usuario_biblioteca, " +
				//	-- se foi causada por um empréstimo atrasado, isso aqui aparece
				"	empr.id_emprestimo, " +
				//	-- e se for manual:
				"	pesresp.nome AS nome_responsavel,  " +
				"	multa.motivo_cadastro," +
				"	matr.codigo_barras, " +
				"	cache.titulo, " +
				"	cache.autor, " +
				"	pessoa.cpf_cnpj," +
				"	multa.id_usuario_cadastro, " +
				"	multa.id_multa_usuario_biblioteca " +
				"FROM " +
				"	biblioteca.multa_usuario_biblioteca AS multa " +
				"	LEFT JOIN biblioteca.emprestimo AS empr ON multa.id_emprestimo = empr.id_emprestimo " +
				"	INNER JOIN biblioteca.usuario_biblioteca AS usub " +
				"		ON usub.id_usuario_biblioteca = COALESCE(multa.id_usuario_biblioteca, empr.id_usuario_biblioteca) " +
				"	LEFT JOIN comum.pessoa AS pessoa ON usub.id_pessoa = pessoa.id_pessoa " +
				"	LEFT JOIN biblioteca.biblioteca AS biblU ON biblU.id_biblioteca = usub.id_usuario_biblioteca " +
				//"	LEFT JOIN biblioteca.politica_emprestimo AS polempr " +
				//"		ON polempr.id_politica_emprestimo = empr.id_politica_emprestimo " +
				"	LEFT JOIN biblioteca.material_informacional AS matr " +
				"		ON matr.id_material_informacional = empr.id_material " +
				"	LEFT JOIN biblioteca.exemplar AS exem  ON exem.id_exemplar = matr.id_material_informacional "+
				"	LEFT JOIN biblioteca.fasciculo AS fasc  ON fasc.id_fasciculo = matr.id_material_informacional "+
				"	LEFT JOIN biblioteca.assinatura AS ass  ON ass.id_assinatura = fasc.id_assinatura "+
				"	LEFT JOIN biblioteca.cache_entidades_marc AS cache " +
				"		ON cache.id_titulo_catalografico = exem.id_titulo_catalografico " +
				"		OR cache.id_titulo_catalografico = ass.id_titulo_catalografico "+
				"	LEFT JOIN biblioteca.biblioteca AS bibl ON bibl.id_biblioteca = matr.id_biblioteca " +
				"	LEFT JOIN comum.usuario AS usuresp ON usuresp.id_usuario = multa.id_usuario_cadastro " +
				"	LEFT JOIN comum.pessoa AS pesresp ON pesresp.id_pessoa = usuresp.id_pessoa " +
				"WHERE " +
				//	-- multa ativa
				"   multa.status = "+StatusMulta.EM_ABERTO+" "+
				"	AND multa.ativo = trueValue() ";
				//	-- data em que a  multa foi gerada " +
			//	if(  dataInicial != null && dataFinal != null )
			//			sql += "AND ( multa.data_cadastro BETWEEN '"+ CalendarUtils.format(dataInicial, "yyyy-MM-dd") +"' AND '"+ CalendarUtils.format(dataFinal, "yyyy-MM-dd") +"') ";
				
				//	-- biblioteca do empréstimo				
				if( bibliotecas != null && bibliotecas.size() > 0 ){
					sql += " AND ( matr.id_biblioteca IN "+UFRNUtils.gerarStringIn(bibliotecas) ;
					if( incluirMultasManuais){
						sql +=" OR matr.id_biblioteca IS NULL "; // multa manual não tem biblioteca, mas que o usuário selecione uma biblioteca deve mostrar as manuais
					}
					
					sql += " ) ";
				}
				
				//	-- categoria de usuário
				sql += ( categoriasDeUsuario != null && categoriasDeUsuario.size() > 0 ?
				"	AND ( usub.vinculo IS NULL OR usub.vinculo IN ("+ StringUtils.join(categoriasDeUsuario, ',') +") ) " : "" ) +
				( ! incluirMultasManuais ?
				"	AND multa.id_emprestimo IS NOT NULL " : "" ) +
				" ORDER BY usub.vinculo, bibl.descricao, pessoa.nome, multa.data_cadastro ";
		
		Query q = getSession().createSQLQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		for ( Object[] linha : lista ) {
			String catUsuario = linha[0] == null ? null : VinculoUsuarioBiblioteca.getVinculo((Integer) linha[0]).getDescricao();
			String biblioteca = linha[1] == null ? null : (String) linha[1];
			
			if ( catUsuario == null )
				catUsuario = "Nenhuma (suspensão manual)";
			if ( biblioteca == null )
				biblioteca = "Todas";
			
			Map<String, List<RelatorioSituacaoDosUsuarios>> s = r.get(catUsuario);
			if ( s == null ) {
				s = new TreeMap<String, List<RelatorioSituacaoDosUsuarios>>();
				r.put( catUsuario, s );
			}
			
			List<RelatorioSituacaoDosUsuarios> t = s.get(biblioteca);
			if ( t == null ) {
				t = new ArrayList<RelatorioSituacaoDosUsuarios>();
				s.put( biblioteca, t );
			}
			
			RelatorioSituacaoDosUsuarios dados = new RelatorioSituacaoDosUsuarios();
			
			dados.setPunicaoPorSuspensao(false);
			
			dados.setCategoriaDoUsuario(catUsuario);
			dados.setBiblioteca(biblioteca);
			try {
				dados.setDataEmprestimo( linha[2] == null ? null : sdf.parse( "" + linha[2] ) );
				dados.setPrazo( linha[3] == null ? null : sdf.parse( "" + linha[3] ) );
				dados.setDataDevolucao( linha[4] == null ? null : sdf.parse("" + linha[4] ) );
				dados.setValorMulta( (BigDecimal)   linha[5] );
			} catch ( ParseException e ) { /*nunca ocorre*/ }
			
			dados.setNome( "" +linha[6] );
			dados.setIdEmprestimo( linha[8] == null ? 0 : Integer.parseInt( "" + linha[8] ) );
			if ( dados.getIdEmprestimo() == 0 ) {
				dados.setSuspensaoManualCadastradaPor( linha[9] == null ? null : ""+linha[9] );
				dados.setMotivoPunicao( linha[10] == null ? null : "" + linha[10] );
			} else {
				dados.setSuspensaoManualCadastradaPor( null );
				dados.setCodigoBarras("" + linha[11]);
				dados.setTitulo("" + linha[12]);
				dados.setAutor("" + linha[13]);
			}
			dados.setCpfCnpj( linha[14] == null ? null : "" + linha[14]);
			dados.setId( Integer.parseInt( "" + linha[7] ) );
			
			t.add(dados);
		}
		
		return r;
	}
	
}
