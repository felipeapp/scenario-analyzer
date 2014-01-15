/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 29/09/2010
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

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.administracao.dao.CamposAtualizacaoCacheTitulos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *
 * <p>Classe utilizada para atualizar o cache das informações MARC no acervo da biblioteca.</p>
 * 
 * <p>As regras das pesquisa no acervo estão descritas no link abaixo:
 * 
 * {@linkplain  http://info.ufrn.br/wikisistemas/doku.php?id=desenvolvimento:especificacoes:sigaa:biblioteca:catalogacao:pesquisas_acervo}
 * </p>
 * 
 * 
 * 
 * @author jadson
 *
 */
public class AtualizaCacheMARCTitulosThread implements Runnable {
	
	/**
	 * O mail para o qual a resposta da atualização vai ser enviada
	 */
	private String email;
	
	/**
	 * A data em que a atualização do cache foi executada
	 */
	private Calendar dataExecucao;
	
	/**
	 * Os campos escolhidos pelo usuário para ser atualizados
	 */
	private CamposAtualizacaoCacheTitulos camposAtualizacao;
	
	
	/** Guarda em cache a lista de classificações bibliográfica utilizadas no sistema para não precisar busca sempre no banco*/
	private List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>(); 
	
	
	/**
	 * Construtor
	 */
	public AtualizaCacheMARCTitulosThread(String email, Date horaExecucao, CamposAtualizacaoCacheTitulos camposAtualizacao){
		this.email = email;
		this.dataExecucao = Calendar.getInstance(); // a data de hoje
		
		this.camposAtualizacao = camposAtualizacao;
		
		dataExecucao= Calendar.getInstance();
		
		if(horaExecucao != null)
			dataExecucao.setTime(CalendarUtils.definirHorario(new Date(), horaExecucao ));
		
		ClassificacaoBibliograficaDao dao = null;
		try{
			 dao = DAOFactory.getInstance().getDAO(ClassificacaoBibliograficaDao.class);
			 classificacoesUtilizadas = dao.findAllClassificacoesParaValidacao();
		} catch (DAOException e) {
			e.printStackTrace();
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	
	
	/**
	 * <p>Método que roda em segundo plano verificando se é hora de realizar a atualização.</p>
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		Calendar calendarHoraAtual  = Calendar.getInstance();
		calendarHoraAtual.setTime(new Date());
		
		try {
			
			// Testa a cada hora, se é tempo de executar
			while(dataExecucao.get(Calendar.HOUR_OF_DAY) > calendarHoraAtual.get(Calendar.HOUR_OF_DAY) ){
				Thread.sleep(3600000); // dorme 1h
				calendarHoraAtual.setTime(new Date());
			}
			
			// Chegou na hora da execução, testa a cada 10 mim se é tempo de executar
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
	 * Método que realiza as ações para atualizar o cache MARC
	 */
	private void iniciarAtualizacaoCacheEntidadesMARC(){
		
		long tempo = System.currentTimeMillis();
		
		Connection con = null;
		
		try {
			
			con = Database.getInstance().getSigaaConnection();
			con.setAutoCommit(false);
			
			int[]  quantidades = montaDadosCacheTitulosEAtualizaCache(con, tempo);
			
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
	 * Monta as informações do cache
	 *
	 * @param con
	 * @throws SQLException
	 */
	private int[] montaDadosCacheTitulosEAtualizaCache(Connection con, long tempo) throws SQLException {
			
		System.out.println("  ==========  ATUALIZANDO CAMPO:  "+camposAtualizacao.getDescricao()+"   ==========  ");
		
		List<CacheEntidadesMarc> caches = new ArrayList<CacheEntidadesMarc>();
		List<TituloCatalografico> titulos = new ArrayList<TituloCatalografico>();
		
		// Busca apenas as informações que serão utilizadas no cache para o campo que está sendo atualizado //
		ResultSet resultado = con.createStatement().executeQuery(camposAtualizacao.getSqlBuscaInformacoesMontarCampo());
		
		int quantidadeAtualizacoes = 0;
		
		// Gera uma lista de títulos com seus campos de dados e controle a patir das informações recuperados //
		while(resultado.next()){
			
			Character tipoCampo = resultado.getString(1).charAt(0);
			
			int idTitulo = resultado.getInt(2);
			
			String tagEtiqueta = resultado.getString(3);
			Short tipoEtiqueta  = resultado.getShort(4);
			   
			TituloCatalografico tituloCat = recuperaTitulo(titulos, new TituloCatalografico(idTitulo));
			
			if(tituloCat == null){ // não existe na lista ainda
				tituloCat = new TituloCatalografico(idTitulo);
				titulos.add(tituloCat);
			}
			
			if(tipoCampo.equals( CampoDados.IDENTIFICADOR_CAMPO)){
				
				Integer idCampoDados = resultado.getInt(5);
				
				String dadoSubCampo = resultado.getString(6);
				Character codigoSubCampo = resultado.getString(7).charAt(0);
				
			    CampoDados c = new CampoDados();
			    c.setId(idCampoDados);
			    c.setEtiqueta(new Etiqueta(tagEtiqueta, tipoEtiqueta));
			    c = recuperaCampoDados(tituloCat.getCamposDados(), c);
			    
			    if( c == null){ // ainda não tem o campo de dados
			    	c = new CampoDados();
				    c.setId(idCampoDados);
				    c.setEtiqueta(new Etiqueta(tagEtiqueta, tipoEtiqueta));
			    	new SubCampo( codigoSubCampo, dadoSubCampo, c, 0);
					tituloCat.addCampoDados(c);
				}else{
					new SubCampo( codigoSubCampo, dadoSubCampo, c, 0); // adiciona só o subcampo
					
				}
			    
			}else{
				Integer idCampoControle = resultado.getInt(5);
				
			    CampoControle c = new CampoControle();
			    c.setId(idCampoControle);
			    c.setEtiqueta(new Etiqueta(tagEtiqueta, tipoEtiqueta));
			    c.setDado(resultado.getString(6));
			    tituloCat.addCampoControle(c);
			}
		    
		    
		}
		
		
		int quantidadeTitulosEncontrados = titulos.size();
		
		int contador = 1;
		
		// Para cada título, monta o cache utilizando o método padrão do sistema //
		for (TituloCatalografico tituloCat : titulos) {
			
			CacheEntidadesMarc cache = new CacheEntidadesMarc(tituloCat.getId()); // usa o id do título como id do cache só para comprar
		    caches.add( CatalogacaoUtil.sincronizaTituloCatalograficoCache(tituloCat, cache, classificacoesUtilizadas)  );
		    
		    contador++;
		    
		    if(contador % 5000 == 0 && contador != 0){
		    	/*
				 * Atualiza cada campo separado e limpa a coleção para não ficar muitos objetos na memório e dar problemas na memória do servidor
				 */
		    	quantidadeAtualizacoes += executaAtualizacoesNoBanco(con, caches, camposAtualizacao,  tempo);
				caches = new ArrayList<CacheEntidadesMarc>();
		    }
		}
		
		
		/*  Atualiza o que sobrou  */
		quantidadeAtualizacoes +=  executaAtualizacoesNoBanco(con, caches, camposAtualizacao,  tempo);
		
		
		caches = new ArrayList<CacheEntidadesMarc>();
		titulos = new ArrayList<TituloCatalografico>();
		
		return new int[]{quantidadeTitulosEncontrados, quantidadeAtualizacoes};
	}
	
	

	/**
	 * Recupera um campo de dados já existente.
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
	 * Retorna um título já adicionado à lista
	 *
	 * @param titulos2
	 * @param tituloCat
	 * @return
	 */
	private TituloCatalografico recuperaTitulo(List<TituloCatalografico> titulosLocais, TituloCatalografico tituloCat) {
		
		if(titulosLocais != null)
		for (TituloCatalografico tituloCatalografico : titulosLocais) {
			if(tituloCatalografico.getId() == tituloCat.getId())
				return tituloCatalografico;
		}
		return null;
	}

	
	
	/**
	 * Executa a atualização na tabela cache, caso a informação tenha mudado
	 */
	private int executaAtualizacoesNoBanco(Connection con, List<CacheEntidadesMarc> caches, CamposAtualizacaoCacheTitulos campo, long tempo) throws SQLException{
		
		PreparedStatement pBusca =  con.prepareStatement(campo.getSqlBuscaCampo());
		
		PreparedStatement pUpdate =  con.prepareStatement(campo.getSqlAtualizaCampo());
		
		int quantidadeAtualizacoes = 0; // guarda a quantidade de atualizações feitas no banco
		
		int contadorCaches = 1;
		
		if( StringUtils.notEmpty( campo.getColuna() ) ){ // Se tem coluna para atualizar 

			for (CacheEntidadesMarc cacheTemp : caches) {
			
				
				pBusca.setInt(1, cacheTemp.getIdTituloCatalografico());
				ResultSet result =  pBusca.executeQuery();
				
				if(result.next()){
					String dadosAtuais = result.getString(1);
					
					if(StringUtils.isEmpty( dadosAtuais) || ! dadosAtuais.equals( campo.getInformacaoAtualizacaoCampo(cacheTemp) )){ // se mudou
						
						pUpdate.setString(1, campo.getInformacaoAtualizacaoCampo(cacheTemp) );
						pUpdate.setInt(2, cacheTemp.getIdTituloCatalografico());
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
			
				
				pBusca.setInt(1, cacheTemp.getIdTituloCatalografico());
				ResultSet result =  pBusca.executeQuery();
				
				if(result.next()){
					String dadosAtuais = result.getString(1);
					
					if(StringUtils.isEmpty( dadosAtuais) || ! dadosAtuais.equals( campo.getInformacaoAtualizacaoCampoAscii(cacheTemp) )){ // se mudou
						
						pUpdate.setString(1, campo.getInformacaoAtualizacaoCampoAscii(cacheTemp) );
						pUpdate.setInt(2, cacheTemp.getIdTituloCatalografico());
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
	 * Envia um email de confirmação que a atualização do cache foi executada com sucesso !
	 */
	private void enviaEmailAtualizacaoSucesso(long tempo, int quantidadeTitulo, int quantidadeAtualizacoes){
		String siglaSistema = RepositorioDadosInstitucionais.get("siglaSigaa");
		
		/**
		 * Envia um email com as informações das atualizações feitas no acervo
		 */
		String mensagem ="<br/><br/> ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA DE BIBLIOTECAS DO "+siglaSistema+". POR FAVOR, NÃO RESPONDÊ-LO. <br/><br/><br/>"
		    +"***************************************************************************************************<br/>"
			+"  Atualização do Cache terminada com sucesso! <br/>"
			+"  Quantidade de Título com o Campo do Acervo: "+quantidadeTitulo+" <br/> "
			+"  Quantidade de Atualizações Realizadas: "+quantidadeAtualizacoes+" <br/> "
			+"  Tempo total atualização : "+((System.currentTimeMillis()-tempo)/60000)+" minutos <br/> "
			+ "***************************************************************************************************<br/>"
			+"<br/>Campo Atualizado no Cache:  "+camposAtualizacao.getDescricao()+" <br/>";
			
		MailBody mail = new MailBody();
		mail.setReplyTo("noReply@ufrn.br");
		mail.setEmail( email );
		mail.setAssunto("["+siglaSistema+"] - NOTIFICACAO ATUALIZAÇÃO CACHE MARC ");
		mail.setMensagem( mensagem );
		Mail.send(mail);
		
	}
	
	/**
	 * Envia um email caso der algum erro na atualização do cache
	 */
	private void enviaEmailAtualizacaoComErro(Exception ex, long tempo){
		
		String siglaSistema = RepositorioDadosInstitucionais.get("siglaSigaa");
		
		/**
		 * Envia um email com o erro caso tenha ocorrido
		 */
		String mensagem = "\n\n\n ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA DE BIBLIOTECAS DO "+siglaSistema+". POR FAVOR, NÃO RESPONDÊ-LO. \n\n\n"
			+"<p>Ocorreu um erro ao tentar atualizar o cache MARC depois de "+((System.currentTimeMillis()-tempo)/60000)+" minutos <br/> </p> <br/><br/>";
		
		mensagem += ex.getMessage()+" <br/>";
		
		for (StackTraceElement stack : ex.getStackTrace()) {
			mensagem += stack.toString()+" <br/>";
		}
		
		MailBody mail = new MailBody();
		mail.setReplyTo("noReply@ufrn.br");
		mail.setEmail( email );
		mail.setAssunto("["+RepositorioDadosInstitucionais.get("siglaSigaa")+"] - ERRO NOTIFICACAO ATUALIZAÇÃO CACHE MARC ");
		mail.setMensagem( mensagem );
		Mail.send(mail);
		
	}
	
	
}

