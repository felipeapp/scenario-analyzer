/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 09/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.timer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.administracao.dao.CamposAtualizacaoCacheArtigos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *
 * <p>Classe utilizada para atualizar o cache das informa��es MARC no acervo da biblioteca para artigos de peri�dicos .</p>
 * 
 * @author jadson
 *
 */
public class AtualizaCacheMARCArtigosThread implements Runnable {

	/**
	 * O mail para o qual a resposta da atualiza��o vai ser enviada
	 */
	private String email;
	
	/**
	 * A data em que a atualiza��o do cache foi executada
	 */
	private Calendar dataExecucao;
	
	/**
	 * Os campos escolhidos pelo usu�rio para ser atualizados
	 */
	private CamposAtualizacaoCacheArtigos camposAtualizacao;
	
	
	
	/**
	 * Construtor
	 */
	public AtualizaCacheMARCArtigosThread(String email, Date horaExecucao, CamposAtualizacaoCacheArtigos camposAtualizacao){
		this.email = email;
		this.dataExecucao = Calendar.getInstance(); // a data de hoje
		
		this.camposAtualizacao = camposAtualizacao;
		
		dataExecucao= Calendar.getInstance();
		
		if(horaExecucao != null)
			dataExecucao.setTime(CalendarUtils.definirHorario(new Date(), horaExecucao ));
	}
	
	
	/**
	 *  <p>M�todo que roda em segundo plano verificando se � hora de realizar a atualiza��o.</p>
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		Calendar calendarHoraAtual  = Calendar.getInstance();
		calendarHoraAtual.setTime(new Date());
		
		try {
			
			// Testa a cada hora, se � tempo de executar
			while(dataExecucao.get(Calendar.HOUR_OF_DAY) > calendarHoraAtual.get(Calendar.HOUR_OF_DAY) ){
				Thread.sleep(3600000); // dorme 1h
				calendarHoraAtual.setTime(new Date());
			}
			
			// Chegou na hora da execu��o, testa a cada 10 mim se � tempo de executar
			while(dataExecucao.get(Calendar.MINUTE) > calendarHoraAtual.get(Calendar.MINUTE) ){
				Thread.sleep(600000); // dorme   10 min
				calendarHoraAtual.setTime(new Date());
			}
			
			iniciarAtualizacaoCacheEntidadesMARC();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * M�todo que realiza as a��es para atualizar o cache MARC
	 */
	private void iniciarAtualizacaoCacheEntidadesMARC(){
		
		long tempo = System.currentTimeMillis();
		
		Connection con = null;
		
		try {
			
			con = Database.getInstance().getSigaaConnection();
			con.setAutoCommit(false);
			
		
		
			int[]  quantidades = montaDadosCacheTitulosEAtualizaCache(con, tempo);
			
			//executaManutencaoCacheTitulos(con);
			
			con.commit();
			
			enviaEmailAtualizacaoSucesso(tempo, quantidades[0], quantidades[1]);
			
		} catch (Exception ex) {
			
			if(con != null){
				try {
					con.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			enviaEmailAtualizacaoComErro(ex, tempo);
			
		}finally{
			
			System.gc();
			
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	
	
	/**
	 * Monta as informa��es do cache
	 *
	 * @param con
	 * @throws SQLException
	 */
	private int[] montaDadosCacheTitulosEAtualizaCache(Connection con, long tempo) throws SQLException {
		
		System.out.println("  ==========  ATUALIZANDO CAMPO:  "+camposAtualizacao.getDescricao()+"   ==========  ");
		
		List<CacheEntidadesMarc> caches = new ArrayList<CacheEntidadesMarc>();
		List<ArtigoDePeriodico> artigos = new ArrayList<ArtigoDePeriodico>();
		
		ResultSet resultado = con.createStatement().executeQuery(camposAtualizacao.getSqlBuscaInformacoesMontarCampo());
			
		int quantidadeAtualizacoes = 0;
		
		while(resultado.next()){
				
			int idArtigo = resultado.getInt(1);
			
			String tagEtiqueta = resultado.getString(2);
			Short tipoEtiqueta  = resultado.getShort(3);
			
			ArtigoDePeriodico artigo = new ArtigoDePeriodico(idArtigo);    
			
			artigo = recuperaArtigo(artigos, artigo);
			
			if(artigo == null){ // n�o existe na lista ainda
				artigo = new ArtigoDePeriodico(idArtigo);
				artigos.add(artigo);
			}
				
			Integer idCampoDados = resultado.getInt(4);
				
			String dadoSubCampo = resultado.getString(5);
			Character codigoSubCampo = resultado.getString(6).charAt(0);
			
		    CampoDados c = new CampoDados();
		    c.setId(idCampoDados);
		    c.setEtiqueta(new Etiqueta(tagEtiqueta, tipoEtiqueta));
		    
		    c = recuperaCampoDados(artigo.getCamposDados(), c);
		    
		    if( c == null){ // ainda n�o tem o campo de dados
		    	c = new CampoDados();
			    c.setId(idCampoDados);
			    c.setEtiqueta(new Etiqueta(tagEtiqueta, tipoEtiqueta));
		    	new SubCampo( codigoSubCampo, dadoSubCampo, c, 0);
		    	artigo.addCampoDados(c);
			}else{
				new SubCampo( codigoSubCampo, dadoSubCampo, c, 0); // adiciona s� o subcampo
				
			}		    
			    
		} // while 
			
			
		int quantidadeArtigosEncontrados = artigos.size();
			
		int contador = 1;
			
		for (ArtigoDePeriodico artigo : artigos) {
			
			CacheEntidadesMarc cache = new CacheEntidadesMarc(artigo.getId()); // usa o id do artigo como id do cache s� para comprar
		    caches.add( CatalogacaoUtil.sincronizaArtigoDePeriodicoCache(artigo, cache)  );
		    
		    
		    contador++;
		    
		    if(contador % 5000 == 0 && contador != 0){
		    	/*
				 * Atualiza cada campo separado e limpa a cole��o para n�o ficar muitos objetos na mem�rio e dar problemas na mem�rio do servidor
				 * 
				 */
		    	quantidadeAtualizacoes += executaAtualizacoesNoBanco(con, caches, camposAtualizacao,  tempo);
				caches = new ArrayList<CacheEntidadesMarc>();
		    }
		}
			
			/*
			 * Atualiza cada campo separado e limpa a cole��o para n�o ficar muitos objetos na mem�rio e dar problemas na mem�rio do servidor
			 * 
			 */
		executaAtualizacoesNoBanco(con, caches, camposAtualizacao,  tempo);
			
			
			caches = new ArrayList<CacheEntidadesMarc>();
			artigos = new ArrayList<ArtigoDePeriodico>();
			
	
		
		return new int[]{quantidadeArtigosEncontrados, quantidadeAtualizacoes};
	}
	
	
	/**
	 * ZERA TODOS os dados do cache de t�tulos
	 */
	private int executaAtualizacoesNoBanco(Connection con, List<CacheEntidadesMarc> caches, CamposAtualizacaoCacheArtigos campo, long tempo) throws SQLException{
		
		PreparedStatement pBusca =  con.prepareStatement(campo.getSqlBuscaCampo());
		
		PreparedStatement pUpdate =  con.prepareStatement(campo.getSqlAtualizaCampo());
		
		int quantidadeAtualizacoes = 0; // guarda a quantidade de atualiza��es feitas no banco
		
		int contadorCaches = 1;
		
		if( StringUtils.notEmpty( campo.getColuna() ) ){ // Se tem coluna para atualizar 

			for (CacheEntidadesMarc cacheTemp : caches) {
			
				
				pBusca.setInt(1, cacheTemp.getIdArtigoDePeriodico());
				ResultSet result =  pBusca.executeQuery();
				
				if(result.next()){
					String dadosAtuais = result.getString(1);
					
					if(StringUtils.isEmpty( dadosAtuais) || ! dadosAtuais.equals( campo.getInformacaoAtualizacaoCampo(cacheTemp) )){ // se mudou
						
						pUpdate.setString(1, campo.getInformacaoAtualizacaoCampo(cacheTemp) );
						pUpdate.setInt(2, cacheTemp.getIdArtigoDePeriodico());
						pUpdate.addBatch();
						
						quantidadeAtualizacoes++;
						
						if(contadorCaches % 1000 == 0){
							pUpdate.executeBatch();
						}
						
					}
					
				}
				
				contadorCaches++;
				
			}
			
			if(pUpdate != null){
				pUpdate.executeBatch();
			}
			
			
			
		}
		
		
		pBusca =  con.prepareStatement(campo.getSqlBuscaCampoAscii());
		pUpdate =  con.prepareStatement(campo.getSqlAtualizaCampoAscii());
		
		if( StringUtils.notEmpty( campo.getColunaAscii() ) ){ // Se tem coluna para atualizar 

			for (CacheEntidadesMarc cacheTemp : caches) {
			
				
				pBusca.setInt(1, cacheTemp.getIdArtigoDePeriodico());
				ResultSet result =  pBusca.executeQuery();
				
				if(result.next()){
					String dadosAtuais = result.getString(1);
					
					if(StringUtils.isEmpty( dadosAtuais) || ! dadosAtuais.equals( campo.getInformacaoAtualizacaoCampoAscii(cacheTemp) )){ // se mudou
						
						pUpdate.setString(1, campo.getInformacaoAtualizacaoCampoAscii(cacheTemp) );
						pUpdate.setInt(2, cacheTemp.getIdArtigoDePeriodico());
						pUpdate.addBatch();
						
						quantidadeAtualizacoes++;
						
						if(contadorCaches % 1000 == 0){
							pUpdate.executeBatch();
						}
						
					}
					
				}
				
				contadorCaches++;
				
			}
			
			if(pUpdate != null){
				pUpdate.executeBatch();
			}		
			
		}
		
		return quantidadeAtualizacoes;
	}
	
	
	/**
	 * Recupera um campo de dados j� existente.
	 *  
	 * @param camposDados
	 * @param tituloCat
	 * @return
	 */
	private CampoDados recuperaCampoDados(List<CampoDados> camposDadosLocais, CampoDados cd) {
		if(camposDadosLocais != null)
		for (CampoDados campoDados : camposDadosLocais) {
			if(cd.getId() == campoDados.getId())
				return campoDados;
		}
		return null;
	}

	

	/**
	 * Retorna um t�tulo j� adicionado � lista
	 *
	 * @param titulos2
	 * @param tituloCat
	 * @return
	 */
	private ArtigoDePeriodico recuperaArtigo(List<ArtigoDePeriodico> artigosLocal, ArtigoDePeriodico artigo) {
		
		if(artigosLocal != null)
		for (ArtigoDePeriodico artigoDePeriodico : artigosLocal) {
			if(artigoDePeriodico.getId() == artigo.getId())
				return artigoDePeriodico;
		}
		return null;
	}

	
	
	
	
	/**
	 * Envia um email de confirma��o que a atualiza��o do cache foi executada com sucesso !
	 */
	private void enviaEmailAtualizacaoSucesso(long tempo, int quantidadeArtigos, int quantidadeAtualizacoes){
		String siglaSistema = RepositorioDadosInstitucionais.get("siglaSigaa");
		
		/**
		 * Envia um email com as informa��es das atualiza��es feitas no acervo
		 */
		String mensagem ="<br/><br/> ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA DE BIBLIOTECAS DO "+siglaSistema+". POR FAVOR, N�O RESPOND�-LO. <br/><br/><br/>"
		    +"***************************************************************************************************<br/>"
			+"  Atualiza��o do Cache terminada com sucesso! <br/>"
			+"  Quantidade de Artigos com o Campo do Acervo: "+quantidadeArtigos+" <br/> "
			+"  Quantidade de Atualiza��es Realizadas: "+quantidadeAtualizacoes+" <br/> "
			+"  Tempo total atualiza��o : "+((System.currentTimeMillis()-tempo)/60000)+" minutos <br/> "
			+ "***************************************************************************************************<br/>"
			+"<br/>Campo Atualizado no Cache:  "+camposAtualizacao.getDescricao()+" <br/>";
			
		MailBody mail = new MailBody();
		mail.setReplyTo("noReply@ufrn.br");
		mail.setEmail( email );
		mail.setAssunto("["+siglaSistema+"] - NOTIFICACAO ATUALIZA��O CACHE MARC ");
		mail.setMensagem( mensagem );
		Mail.send(mail);
		
	}
	
	/**
	 * Envia um email caso der algum erro na atualiza��o do cache
	 */
	private void enviaEmailAtualizacaoComErro(Exception ex, long tempo){
		
		String siglaSistema = RepositorioDadosInstitucionais.get("siglaSigaa");
		
		/**
		 * Envia um email com o erro caso tenha ocorrido
		 */
		String mensagem = "\n\n\n ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA DE BIBLIOTECAS DO "+siglaSistema+". POR FAVOR, N�O RESPOND�-LO. \n\n\n"
			+"<p>Ocorreu um erro ao tentar atualizar o cache MARC depois de "+((System.currentTimeMillis()-tempo)/60000)+" minutos <br/> </p> <br/><br/>";
		
		mensagem += ex.getMessage()+" <br/>";
		
		for (StackTraceElement stack : ex.getStackTrace()) {
			mensagem += stack.toString()+" <br/>";
		}
		
		MailBody mail = new MailBody();
		mail.setReplyTo("noReply@ufrn.br");
		mail.setEmail( email );
		mail.setAssunto("["+RepositorioDadosInstitucionais.get("siglaSigaa")+"] - ERRO NOTIFICACAO ATUALIZA��O CACHE MARC ");
		mail.setMensagem( mensagem );
		Mail.send(mail);
		
	}
	
}
