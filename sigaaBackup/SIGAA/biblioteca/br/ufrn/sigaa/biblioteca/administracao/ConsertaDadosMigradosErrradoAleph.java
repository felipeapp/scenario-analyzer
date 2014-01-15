/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 11/03/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.administracao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Classe que conserta vários erros de migração do aleph que estão gerando chamados.
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
 * @since 11/03/2013
 *
 */
public class ConsertaDadosMigradosErrradoAleph {

	/**
	 * @param args
	 *  
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/</li>
	 * </ul>
	 *
	 */
	public static void main(String[] args)  throws SQLException {
		
		Connection connection = null;
		
		boolean executar = true; // Indica se vai só testar as consultas ou executar as mudanças no banco.
		
		long tempo = System.currentTimeMillis();
		
		try{
			// TODO colocar o endereço e senha do banco de produção aqui //
			connection = getConnection("jdbc:postgresql://bdgeral.info.ufrn.br:5432/sigaa_3_9_20130218", "sigaa", "sigaa");
			connection.setAutoCommit(false);
			
			System.out.println("**************   Inicio  ************** ");
			consertaExemplaresMigradosComoFasciculos(connection, executar);
			consertaFasciculosMigradosComoExemplares(connection, executar);
			
			connection.commit();
			
		}catch(Exception ex){
			ex.printStackTrace();
			if (connection != null) connection.rollback();
		}finally{
			if (connection != null) connection.close();
		}
		
		System.out.println(" ************** FIM "+(System.currentTimeMillis()-tempo)/1000+" segundos ************** ");
		System.exit(0);

	}
	

	public static void consertaExemplaresMigradosComoFasciculos(Connection connection, boolean executar) throws SQLException {
		
		// titulo não periodicos com fascíciulo
		String sql = "select distinct t.id_titulo_catalografico, t.numero_do_sistema, c.id_cache_entidades_marc, c.quantidade_materiais_ativos_titulo, t.id_formato_material, m.codigo_barras" +
				" , f.id_fasciculo, m.codmerg, f.suplemento, f.id_assinatura "+
		" from biblioteca.titulo_catalografico t "+ 
		" inner join biblioteca.cache_entidades_marc c on t.id_titulo_catalografico = c.id_titulo_catalografico "+
		" inner join biblioteca.assinatura a on a.id_titulo_catalografico = t.id_titulo_catalografico "+
		" inner join biblioteca.fasciculo f on f.id_assinatura = a.id_assinatura "+
		" inner join biblioteca.material_informacional m on m.id_material_informacional = f.id_fasciculo "+
		" WHERE t.id_formato_material <> 5 and m.ativo = true " +
		" ORDER BY t.id_titulo_catalografico; ";
		
		// Ler os dados da entidade CursoEvento //
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		List<DadosMigracao> lista = new ArrayList<DadosMigracao>();
		
		
		while( rs.next() ){
		
			int idTitulo  = rs.getInt(1);
			int numeroSistema = rs.getInt(2);
			int idCache = rs.getInt(3);
			int qtdMateriaisCache = rs.getInt(4);
			int idFormatoMaterial = rs.getInt(5);
			
			String codigoBarras = rs.getString(6);
			int idMaterial = rs.getInt(7);
			String codigoMerge = rs.getString(8);
			boolean suplemento = rs.getBoolean(9);
			int idAssinatura = rs.getInt(10);
			
			DadosMigracao dados = new DadosMigracao();			
			dados.setIdTitulo(idTitulo);
			dados.setNumeroSistema(numeroSistema);
			dados.setIdCache(idCache);
			dados.setQtdMateriaisCache(qtdMateriaisCache);
			dados.setIdFormatoMaterial(idFormatoMaterial);
			
			if( lista.contains(dados))
				dados = lista.get(lista.indexOf(dados));
			else
				lista.add(dados);
			
			dados.adicionaMaterial(idMaterial, new InfoMaterial(codigoBarras, codigoMerge, suplemento, idAssinatura, 0));
			
		}

	
		// A lógia na migração //
		for (DadosMigracao dadosMigracao : lista) {
			if(dadosMigracao.realmenteContemSoExemplares()){
				
				List<Integer> idsFasciculosASeremRemovidos = new ArrayList<Integer>();
				List<Integer> idsAssinaturas = new ArrayList<Integer>();
				
				criaExemplar(dadosMigracao, connection,  executar, idsFasciculosASeremRemovidos, idsAssinaturas);
				removeFasciculos(connection, executar, idsFasciculosASeremRemovidos);
				removeAssinaturaDosFasciculos(connection, executar, idsAssinaturas);
				
			}else{
				if( dadosMigracao.realmenteContemSoFasciculos() ){
					mundaFormatoMaterialTitulo(dadosMigracao, connection,  executar, 5);
				}else{
					System.out.println(" !!!!!!!!!!!!! "+" [Erro] contém exemplares e fascículos "+dadosMigracao.getIdTitulo()+" !!!!!!!!!!!!! ");
				}
			}
		}
		
	}


	/**
	 * Aqui é o caso mais complicado, o título contém realmente exemplares (o código de barras sem o traço "-").
	 * 
	 * Então para cada fascículo existente, cria o exemplar, apaga o fascículo e no final apaga a assinatura.
	 * @throws SQLException 
	 *
	 */
	private static void criaExemplar(DadosMigracao dadosMigracao, Connection connection, boolean executar
			, List<Integer> idsFasciculosASeremRemovidos,  List<Integer> idsAssinaturas) throws SQLException {
		
		String sqlInsertExemplar = "INSERT INTO biblioteca.exemplar( id_exemplar, id_titulo_catalografico, numero_patrimonio, codmerg, anexo)"+
				" VALUES (?, ?, ?, ?, ?)";
		
		
		PreparedStatement psInsertExemplar = connection.prepareStatement(sqlInsertExemplar);
			
			
		for (Integer idMaterial : dadosMigracao.getMateriais().keySet()) {
		
			InfoMaterial infoMateral = dadosMigracao.getMateriais().get(idMaterial);
			
			psInsertExemplar.setInt( 1, idMaterial );
			psInsertExemplar.setInt( 2,  dadosMigracao.getIdTitulo());
			
			String codigoBarras = infoMateral.getCodigoBarras();
			if(StringUtils.notEmpty(codigoBarras) && codigoBarras.startsWith("20") && ! codigoBarras.matches("\\D"))
				psInsertExemplar.setInt( 3,  Integer.parseInt(codigoBarras) );
			else
				psInsertExemplar.setNull( 3, java.sql.Types.INTEGER );
			
			psInsertExemplar.setString( 4,  infoMateral.getCodMerg());	
			psInsertExemplar.setBoolean( 5,  infoMateral.isAnexo());  // faz função de suplemento
		
			
			System.out.println(" Criando Exemplar: ");
			System.out.println(" INSERT INTO biblioteca.exemplar( id_exemplar, id_titulo_catalografico, numero_patrimonio, codmerg, anexo)"+
				" VALUES ("+idMaterial+", "+ dadosMigracao.getIdTitulo()+", "+infoMateral.getCodigoBarras()+", "+infoMateral.getCodMerg()+", "+infoMateral.isAnexo()+")" );			
			System.out.println(" ");
			
			if(executar)
				psInsertExemplar.execute();
			
			idsFasciculosASeremRemovidos.add(idMaterial);
			idsAssinaturas.add(infoMateral.getIdAssinatura());
		}
		
		
	}

	/**
	 * Aqui é o caso mais complicado, o título contém realmente exemplares (o código de barras sem o traço "-").
	 * 
	 * Então para cada fascículo existente, cria o exemplar, apaga o fascículo e no final apaga a assinatura.
	 * @throws SQLException 
	 *
	 */
	private static void removeFasciculos(Connection connection, boolean executar, List<Integer> idsFasciculosASeremRemovidos) throws SQLException {
		
		String sqlDeleteFasciculos = " DELETE FROM biblioteca.fasciculo where id_fasciculo in "+UFRNUtils.gerarStringIn(idsFasciculosASeremRemovidos);
		
		System.out.println(" Apagando fascículos: ");
		System.out.println(" DELETE FROM biblioteca.fasciculo where id_fasciculo in "+UFRNUtils.gerarStringIn(idsFasciculosASeremRemovidos) );
		System.out.println(" ");
		
		if(executar){
			System.out.println(" Apagando fascículos !!!! ");
			PreparedStatement psUpdate = connection.prepareStatement(sqlDeleteFasciculos);
			psUpdate.execute();
		}
		
	}
	
	
	/**
	 * Aqui é o caso mais complicado, o título contém realmente exemplares (o código de barras sem o traço "-").
	 * 
	 * Então para cada fascículo existente, cria o exemplar, apaga o fascículo e no final apaga a assinatura.
	 * @throws SQLException 
	 *
	 */
	private static void removeAssinaturaDosFasciculos(Connection connection, boolean executar, List<Integer> idsAssinaturas) throws SQLException {
		
		String sqlDeleteAssinatura = " DELETE from biblioteca.assinatura where id_assinatura IN "+UFRNUtils.gerarStringIn(idsAssinaturas);
		
		System.out.println(" Apagando assinaturas: ");
		System.out.println(" DELETE from biblioteca.assinatura where id_assinatura IN "+UFRNUtils.gerarStringIn(idsAssinaturas) );
		System.out.println(" ");
		
		if(executar){
			System.out.println(" Apagando assinatura dos fascículos !!!! ");
			PreparedStatement psUpdate = connection.prepareStatement(sqlDeleteAssinatura);
			psUpdate.execute();
		}
		
	}
	

	
	
	
	
	/**
	 * Aqui é o caso mais fácil, o título contém realmente fascículos, xxx-xxx.  mas o formato dela tá errado muda o formato.
	 *
	 */
	private static void mundaFormatoMaterialTitulo(DadosMigracao dadosMigracao, Connection connection, boolean executar, int idFormatoMaterialTitulo) throws SQLException {
		
		String sqlUpdate = " update biblioteca.titulo_catalografico set id_formato_material = ?  WHERE id_titulo_catalografico = ? ";
		
		System.out.println(" >>>>>>>>> Alterado formato Material do Título:  update biblioteca.titulo_catalografico set id_formato_material = "+idFormatoMaterialTitulo+"  WHERE id_titulo_catalografico = "+dadosMigracao.getIdTitulo()+" ");
		System.out.println(" ");
		System.out.println(" ");
		
		PreparedStatement psUpdateInscricaoParticipante = connection.prepareStatement(sqlUpdate);
		psUpdateInscricaoParticipante.setInt( 1,  idFormatoMaterialTitulo);
		psUpdateInscricaoParticipante.setInt( 2, dadosMigracao.getIdTitulo() );
		
		if(executar){
			System.out.println(" Executando migração dos valores cobrados dos participante  InscricaoAtividadeParticipante !!!! ");
			psUpdateInscricaoParticipante.execute();
		}
		
	}


	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	

	public static void consertaFasciculosMigradosComoExemplares(Connection connection, boolean executar) throws SQLException {
		
		
		String sql = " select t.id_titulo_catalografico, t.numero_do_sistema, c.id_cache_entidades_marc, c.quantidade_materiais_ativos_titulo, t.id_formato_material, c.titulo" +
				" , m.codigo_barras, e.id_exemplar, m.codmerg, e.anexo, m.id_biblioteca "+
				" from biblioteca.titulo_catalografico t "+ 
				" inner join biblioteca.cache_entidades_marc c on t.id_titulo_catalografico = c.id_titulo_catalografico  "+
				" inner join biblioteca.exemplar e on t.id_titulo_catalografico = e.id_titulo_catalografico  "+
				" inner join biblioteca.material_informacional m on m.id_material_informacional = e.id_exemplar "+
				" WHERE t.id_formato_material = 5 and m.ativo = true" +
				" ORDER BY t.id_titulo_catalografico; ";
		
		// Ler os dados da entidade CursoEvento //
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		List<DadosMigracao> lista = new ArrayList<DadosMigracao>();
		
		
		while( rs.next() ){
		
			int idTitulo  = rs.getInt(1);
			int numeroSistema = rs.getInt(2);
			int idCache = rs.getInt(3);
			int qtdMateriaisCache = rs.getInt(4);
			int idFormatoMaterial = rs.getInt(5);
			String titulo = rs.getString(6);
			
			String codigoBarras = rs.getString(7);
			int idMaterial = rs.getInt(8);
			String codigoMerge = rs.getString(9);
			boolean anexo = rs.getBoolean(10);
			int idBiblioteca = rs.getInt(11);
			
			DadosMigracao dados = new DadosMigracao();			
			dados.setIdTitulo(idTitulo);
			dados.setTitulo(titulo);
			dados.setNumeroSistema(numeroSistema);
			dados.setIdCache(idCache);
			dados.setQtdMateriaisCache(qtdMateriaisCache);
			dados.setIdFormatoMaterial(idFormatoMaterial);
			
			if( lista.contains(dados))
				dados = lista.get(lista.indexOf(dados));
			else
				lista.add(dados);
			
			dados.adicionaMaterial(idMaterial, new InfoMaterial(codigoBarras, codigoMerge, anexo, 0, idBiblioteca));
			
		}

	
		// A lógia na migração //
		for (DadosMigracao dadosMigracao : lista) {
			if(dadosMigracao.realmenteContemSoExemplares()){
				
				mundaFormatoMaterialTitulo(dadosMigracao, connection,  executar, 1); // se o título como livro.
				
			}else{
				if( dadosMigracao.realmenteContemSoFasciculos() ){
					
					List<Integer> idsExemplaresASeremRemovidos = new ArrayList<Integer>();
					
					criarFasciculos(dadosMigracao, connection,  executar, idsExemplaresASeremRemovidos);
					removeExemplares(connection, executar, idsExemplaresASeremRemovidos);
					
				}else{
					System.out.println(" \n!!!!!!!!!!!!! "+" [Erro] contém exemplares e fascículos "+dadosMigracao.getIdTitulo()+" !!!!!!!!!!!!!\n ");
				}
			}
		}
		
	}


	
	
	
	
	/**
	 * Aqui é o caso mais complicado, o título contém realmente fascículos (o código de barras com o traço "-").
	 * 
	 * Então para cada exemplar existente, cria o fascículo a sua assinatura, apaga o exemplar que não existia.
	 * @throws SQLException 
	 *
	 */
	private static void criarFasciculos(DadosMigracao dadosMigracao, Connection connection, boolean executar, List<Integer> idsExemplaresASeremRemovidos) throws SQLException {
		
		
		String sql = " select id_assinatura, id_biblioteca from biblioteca.assinatura where id_titulo_catalografico = ? ";
		
		String sqlInsertFasciculo = "INSERT INTO biblioteca.fasciculo( id_fasciculo, id_assinatura, incluido_acervo, codmerg, suplemento)"+
				" VALUES (?, ?, true, ?, ?)";
		
		PreparedStatement psInsertFasciculo = connection.prepareStatement(sqlInsertFasciculo);
		
		
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.setInt(1, dadosMigracao.getIdTitulo());
		
		ps.execute();
		
		ResultSet rs = ps.getResultSet();
		
		int idAssinaturaFasciculo = 0;
		int idBibliotecaFasciculo = 0;
		
		while( rs.next() ){
			
			int idAssinatura = rs.getInt(1);
			int idBiblioteca = rs.getInt(2);
			
			if(idAssinatura > 0 ){
				
				boolean bibliotecaFasciculosCorreta = true;
				
				for(Integer idMaterial : dadosMigracao.getMateriais().keySet()){
					if( dadosMigracao.getMateriais().get(idMaterial).getIdBiblioteca() != idBiblioteca){
						bibliotecaFasciculosCorreta = false;
					}
				}
				
				// Tem uma assinatura para a biblioteca dos fascículos // 
				if(bibliotecaFasciculosCorreta){
					idAssinaturaFasciculo = idAssinatura;
					idBibliotecaFasciculo = idBiblioteca;
				}
			}
		}
		
		
		
		if(idAssinaturaFasciculo == 0){
			
			int maiorNumeroFasciculo = 0;

			// Pega o maior código de barras para gerar o número da assinatura 
			for (Integer idMaterial : dadosMigracao.getMateriais().keySet()) {	
				InfoMaterial infoMaterial = dadosMigracao.getMateriais().get(idMaterial);
				
				int numeroFasciculo = 0;
				try{
					numeroFasciculo = Integer.parseInt( infoMaterial.getCodigoBarras().split("-")[1] );
				}catch(NumberFormatException nfe){
					// se não for numero ignore.
				}
						
				if(numeroFasciculo > maiorNumeroFasciculo)
					maiorNumeroFasciculo = numeroFasciculo;
				
				idBibliotecaFasciculo = infoMaterial.getIdBiblioteca();
			}
			
			idAssinaturaFasciculo = criarAssinaturaFasciculos(connection, executar, idBibliotecaFasciculo, dadosMigracao.getIdTitulo(), dadosMigracao.getTitulo(), maiorNumeroFasciculo);
			
		}else{
			System.out.println(" Assinatura já existe; "+idAssinaturaFasciculo);
		}
		
		for (Integer idMaterial : dadosMigracao.getMateriais().keySet()) {
		
			InfoMaterial infoMateral = dadosMigracao.getMateriais().get(idMaterial);
			
			psInsertFasciculo.setDouble( 1, idMaterial );
			psInsertFasciculo.setInt( 2,  idAssinaturaFasciculo);	
			psInsertFasciculo.setString( 3,  infoMateral.getCodMerg());	
			psInsertFasciculo.setBoolean( 4,  infoMateral.isAnexo());  // faz função de suplemento
		
			
			System.out.println(" Criando Fascículo: ");
			System.out.println(" INSERT INTO biblioteca.fasciculo( id_fasciculo, id_assinatura, incluido_acervo, codmerg, suplemento)"+
				" VALUES ("+idMaterial+", "+idAssinaturaFasciculo +" true, "+infoMateral.getCodMerg()+", "+infoMateral.isAnexo()+")" );			
			System.out.println(" ");
			
			if(executar)
				psInsertFasciculo.execute();
			
			idsExemplaresASeremRemovidos.add(idMaterial);
		}
		
		
		
	}

	private static int criarAssinaturaFasciculos(Connection connection, boolean executar, int idBibliotecaFasciculo, int idTitulo, String titulo, int numeroGeradorFasciculo) throws SQLException {
		
		String sqlInsertAssinatura = " INSERT INTO biblioteca.assinatura (id_assinatura, id_titulo_catalografico, internacional, titulo, codigo, data_criacao, data_inicio_assinatura, numero_primeiro_fasciculo, id_biblioteca, codmerg, titulo_ascii, numero_gerador_fasciculo, ativa) "+
		" VALUES (?, ?, false, ?, ?, ?, ?, 1, ?, 'CONSERTANDO_BASE', ?, ?, true) ";
		
		String sqlSequencia = " select nextval('biblioteca.material_informacional_sequence') ";
		
		String sqlBiblioteca = " select codigo_identificador_biblioteca, numero_gerador_codigo_assinatura from biblioteca.biblioteca where id_biblioteca = ? ";
		
		String sqlUpdateBiblioteca = " UPDATE biblioteca.biblioteca SET numero_gerador_codigo_assinatura = ? where id_biblioteca = ? ";
		
		int idAssinatura = 0;
		
		if(executar){
			
			PreparedStatement ps = connection.prepareStatement( sqlSequencia );
			ps.execute();
			
			ResultSet rs = ps.getResultSet();
			while( rs.next() ){
				idAssinatura = rs.getInt(1);
			}
			
		}
		
			
		
	
	
		String codigoIdentificadorBiblioteca = "";
		int numeroGeradorCodigoAssinatura = 0;
		
		PreparedStatement ps = connection.prepareStatement( sqlBiblioteca );
		ps.setInt(1, idBibliotecaFasciculo);
		ps.execute();
		
		ResultSet rs = ps.getResultSet();
		while( rs.next() ){
			codigoIdentificadorBiblioteca = rs.getString(1);
			numeroGeradorCodigoAssinatura = rs.getInt(2);
		}
		
	
		String codigoAssinatura =  Calendar.getInstance().get(Calendar.YEAR)+codigoIdentificadorBiblioteca+numeroGeradorCodigoAssinatura;
		
		
		
		PreparedStatement psInsertaAssinatura = connection.prepareStatement(sqlInsertAssinatura);
		psInsertaAssinatura.setDouble( 1, idAssinatura );
		psInsertaAssinatura.setInt( 2,  idTitulo);
		psInsertaAssinatura.setString( 3, titulo );
		psInsertaAssinatura.setString( 4, codigoAssinatura );
		psInsertaAssinatura.setDate( 5, new java.sql.Date( new Date().getTime() ) );
		psInsertaAssinatura.setDate( 6, new java.sql.Date( new Date().getTime() ) );
		psInsertaAssinatura.setInt( 7, idBibliotecaFasciculo );
		psInsertaAssinatura.setString( 8, StringUtils.toAsciiAndUpperCase(" "+titulo+" ") );
		psInsertaAssinatura.setInt( 9, numeroGeradorFasciculo); 
		
		
		System.out.println(" Criando assinaturas: ");
		System.out.println(" INSERT INTO biblioteca.assinatura (id_assinatura, id_titulo_catalografico, internacional, titulo, codigo, data_criacao, data_inicio_assinatura, numero_primeiro_fasciculo, id_biblioteca, codmerg, titulo_ascii, numero_gerador_fasciculo, ativa) "+
		" VALUES ("+idAssinatura+", "+idTitulo+", false, "+titulo+","+codigoAssinatura+", hoje, hoje, 1, "+idBibliotecaFasciculo+", 'CONSERTANDO_BASE', "+StringUtils.toAsciiAndUpperCase(" "+titulo+" ")+", "+numeroGeradorFasciculo+", true) ");
		
		if(executar)
			psInsertaAssinatura.execute();
		
		numeroGeradorCodigoAssinatura++;
		
		
		System.out.println(" Atualizando numero gerador biblioteca:  UPDATE biblioteca.biblioteca SET numero_gerador_codigo_assinatura = "+numeroGeradorCodigoAssinatura+" where id_biblioteca = "+idBibliotecaFasciculo+" ");
		
		if(executar){
			
			PreparedStatement psUpdate = connection.prepareStatement( sqlUpdateBiblioteca );
			psUpdate.setInt(1, numeroGeradorCodigoAssinatura);
			psUpdate.setInt(2, idBibliotecaFasciculo);
			psUpdate.execute();
		}
			
		
		
		return idAssinatura;
	}


	/**
	 * Aqui é o caso mais complicado, o título contém realmente exemplares (o código de barras sem o traço "-").
	 * 
	 * Então para cada fascículo existente, cria o exemplar, apaga o fascículo e no final apaga a assinatura.
	 * @throws SQLException 
	 *
	 */
	private static void removeExemplares(Connection connection, boolean executar, List<Integer> idsExemplaresASeremRemovidos) throws SQLException {
		
		String sqlDeleteExemplares = " DELETE FROM biblioteca.exemplar where id_exemplar in "+UFRNUtils.gerarStringIn(idsExemplaresASeremRemovidos);
		
		System.out.println(" Apagando exemplares: ");
		System.out.println(" DELETE FROM biblioteca.exemplar where id_exemplar in "+UFRNUtils.gerarStringIn(idsExemplaresASeremRemovidos) );
		System.out.println(" ");
		
		if(executar){
			System.out.println(" Apagando fascículos !!!! ");
			PreparedStatement psUpdate = connection.prepareStatement(sqlDeleteExemplares);
			psUpdate.execute();
		}
		
	}
	
	
	
	
	

	/**
	 *   Cria uma conexão com o banco do Sigaa
	 *
	 * @param urlFonte
	 * @param usuarioFonte
	 * @param senhaFonte
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(String urlDestino, String usuarioDestino, String senhaDestino) throws SQLException{
		try{
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {}
		return DriverManager.getConnection(urlDestino, usuarioDestino, senhaDestino);
	}
	

}

class DadosMigracao{
	
	int idTitulo;
	String titulo;
	int numeroSistema;
	int idCache;
	int qtdMateriaisCache;
	int idFormatoMaterial;
	
	Map<Integer, InfoMaterial> materiais = new HashMap<Integer, InfoMaterial>();

	public void adicionaMaterial(int idMaterial, InfoMaterial infoMaterial){
		if(materiais == null)
			materiais = new HashMap<Integer, InfoMaterial>();
		materiais.put(idMaterial, infoMaterial);
	}
	
	public boolean realmenteContemSoExemplares() {
		for (Integer  idMaterial : materiais.keySet()) {
			if( materiais.get(idMaterial).getCodigoBarras().contains("-"))
				return false; // sem tem -  no código de barra são fascículos
		}
		return true; // se não tem fascículos, são mesmo exemplares que foram migrados como fascículos
	}
	
	public boolean realmenteContemSoFasciculos() {
		for (Integer  idMaterial : materiais.keySet()) {
			if( ! materiais.get(idMaterial).getCodigoBarras().contains("-"))
				return false; // sem tem -  no código de barra são fascículos
		}
		return true; // se não tem fascículos, são mesmo exemplares que foram migrados como fascículos
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idTitulo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DadosMigracao other = (DadosMigracao) obj;
		if (idTitulo != other.idTitulo)
			return false;
		return true;
	}
	
	
	

	public int getIdTitulo() {
		return idTitulo;
	}

	public void setIdTitulo(int idTitulo) {
		this.idTitulo = idTitulo;
	}

	public int getIdCache() {
		return idCache;
	}

	public void setIdCache(int idCache) {
		this.idCache = idCache;
	}

	public int getQtdMateriaisCache() {
		return qtdMateriaisCache;
	}

	public void setQtdMateriaisCache(int qtdMateriaisCache) {
		this.qtdMateriaisCache = qtdMateriaisCache;
	}

	public int getIdFormatoMaterial() {
		return idFormatoMaterial;
	}

	public void setIdFormatoMaterial(int idFormatoMaterial) {
		this.idFormatoMaterial = idFormatoMaterial;
	}

	public Map<Integer, InfoMaterial> getMateriais() {
		return materiais;
	}

	public void setMateriais(Map<Integer, InfoMaterial> materiais) {
		this.materiais = materiais;
	}

	public int getNumeroSistema() {
		return numeroSistema;
	}

	public void setNumeroSistema(int numeroSistema) {
		this.numeroSistema = numeroSistema;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
}

class InfoMaterial{
	
	String codigoBarras;
	String codMerg;
	boolean anexo;
	int idAssinatura;
	int idBiblioteca;
	
	public InfoMaterial(String codigoBarras, String codMerg, boolean anexo, int idAssinatura, int idBiblioteca) {
		super();
		this.codigoBarras = codigoBarras;
		this.codMerg = codMerg;
		this.anexo = anexo;
		this.idAssinatura = idAssinatura;
		this.idBiblioteca = idBiblioteca;
	}
	
	
	
	@Override
	public String toString() {
		return "InfoMaterial [codigoBarras=" + codigoBarras + ", codMerg="
				+ codMerg + ", anexo=" + anexo + "]";
	}


	public String getCodigoBarras() {
		return codigoBarras;
	}
	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}
	public String getCodMerg() {
		return codMerg;
	}
	public void setCodMerg(String codMerg) {
		this.codMerg = codMerg;
	}
	public boolean isAnexo() {
		return anexo;
	}
	public void setAnexo(boolean anexo) {
		this.anexo = anexo;
	}
	public int getIdAssinatura() {
		return idAssinatura;
	}
	public void setIdAssinatura(int idAssinatura) {
		this.idAssinatura = idAssinatura;
	}
	public int getIdBiblioteca() {
		return idBiblioteca;
	}
	public void setIdBiblioteca(int idBiblioteca) {
		this.idBiblioteca = idBiblioteca;
	}
	
	
}

