/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 21/09/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.timer;


/**
 *
 * <p>Thread que roda atualizando o cache do relacionamento entre �reas CNPq e classifica��es bibliotegr�ficas (CDU, black, CDD)</p>
 * <p>No final da atualiza��o ser� enviado um email com o resumo das altera��es feitas</p>
 * 
 * @author jadson
 * @deprecated  n�o pode mais sair atualiza��o de acordo com o sistema porque o bibliotec�rio pode mudar o que o sistema gerou, 
 * ent�o apagaria o que os bibliotec�rios alteraram.
 */
public class AtualizaRelacaoClassificacaoAreasCNPqThread /* implements Runnable */ {

//	private Collection<AreaConhecimentoCnpq> grandesAreas;
//	private String email;
//	private Date horaExecucao;
//	
//	public AtualizaRelacaoClassificacaoAreasCNPqThread(Collection<AreaConhecimentoCnpq> grandesAreas, String email, Date horaExecucao){
//		this.grandesAreas = grandesAreas;
//		this.email = email;
//		this.horaExecucao = horaExecucao;
//	}
//	
//	
//	@Override
//	public void run() {
//		try {
//			
//			// se n�o informou a hora da execu��o, ou a hora de execu��o j� possou 
//			if(horaExecucao != null ){
//				
//				while(horaExecucao.after(new Date())){
//					Thread.sleep(600000); // 10 mim 				
//				}
//				
//				atualizarRelacionamentosAreasCNPq();
//				
//			}else{	
//				atualizarRelacionamentosAreasCNPq();
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	
//	
//	/**
//	 * <p> M�todo que realiza a atualiza��o do cache gerado para o relacionamento entre �reas CNPq e classfi��es bibliogr�ficas ( CDU, black, CDD). </p>
//	 *
//	 * <p> Por uma quest�o de otimiza��o dos relat�rios, as �reas CNPq dos t�tulo s�o calculadas no momento da cria��o ou atualiza��o dos T�tulo
//	 * e s�o guardas neles pr�prios, assim n�o � preciso ter esse trabalho no momento da consulta dos relat�rios.
//	 * </p>
//	 *
//	 */
//	public void atualizarRelacionamentosAreasCNPq(){
//
//		
//		if(grandesAreas == null)
//			return;
//		
//		long tempo = System.currentTimeMillis();
//		
//		JdbcTemplate template = null;
//	
//		try {
//			
//		
//			try {
//			
//				template = new JdbcTemplate( Database.getInstance().getSigaaDs());
//				
//				
//				 ///// Busca TODOS t�tulos ativo no sistema /////////
//				@SuppressWarnings("unchecked")
//				Collection titulos = template.queryForList(
//				 " SELECT titulo.id_titulo_catalografico as id_titulo, titulo.classe_cdu as classe_cdu, titulo.classe_black as classe_black "
//						+" ,titulo.id_area_conhecimento_cnpq as area_cnpq_cdu, titulo.id_area_conhecimento_cnpq_black as area_cnpq_cdu_black "
//				+" FROM biblioteca.titulo_catalografico titulo "
//				+" WHERE titulo.catalogado = trueValue() AND titulo.ativo = trueValue() ");
//				
//				
//				int qtdTitulosAtualizados = 0;
//				
//				/**
//				 * Para casa t�tulo existente no acervo, calcula novamente a �rea CNPq com base nas novas regras de relacionamento e atualiza isso no t�tulo
//				 */
//				@SuppressWarnings("unchecked")
//				Iterator it = titulos.iterator();
//				while(it.hasNext()){
//					
//					@SuppressWarnings("unchecked")
//					Map<String, Object> mapa = (Map<String, Object>) it.next();
//					Integer idTitulo = (Integer) mapa.get("id_titulo");
//					String classeCDU = (String) mapa.get("classe_cdu");
//					String classeBlack = (String) mapa.get("classe_black");
//					Integer idAreaCNPqCDUAntigo = (Integer) mapa.get("area_cnpq_cdu");
//					Integer idAreaCNPqBlackAntigo = (Integer) mapa.get("area_cnpq_cdu_black");
//					
//					AreaConhecimentoCnpq areaDaCDU =  CatalogacaoUtil.encontraAreaConhecimentoCNPQAPartirDaClasseCDU(grandesAreas, classeCDU);
//					
//					AreaConhecimentoCnpq areaDaBlack =  CatalogacaoUtil.encontraAreaConhecimentoCNPQAPartirDaClasseBlack(grandesAreas, classeBlack);
//					
//					
//					boolean atualizarAreaCDU = false;
//					boolean atualizarAreaBlack = false;
//					
//					Integer idAreaCNPqCDUNovo = null;
//					Integer idAreaCNPqBlackNovo = null;
//					
//					if(areaDaCDU != null)
//						idAreaCNPqCDUNovo = areaDaCDU.getId();
//					
//					if(areaDaBlack != null)
//						idAreaCNPqBlackNovo = areaDaBlack.getId();
//					
//					// S� atualiza se mudou a �rea cnpq //
//					
//					if(idAreaCNPqCDUNovo == null){
//						if(idAreaCNPqCDUAntigo != null){
//							atualizarAreaCDU = true;
//						}
//					}else{
//						if( ! idAreaCNPqCDUNovo.equals(idAreaCNPqCDUAntigo) ) {
//							atualizarAreaCDU = true;
//						}
//					}
//					
//					if(idAreaCNPqBlackNovo == null){
//						if(idAreaCNPqBlackAntigo != null){
//							atualizarAreaBlack = true;
//						}
//					}else{
//						if( ! idAreaCNPqBlackNovo.equals(idAreaCNPqBlackAntigo) ) {
//							atualizarAreaBlack = true;
//						}
//					}
//				
//					if(atualizarAreaCDU && ! atualizarAreaBlack){
//						qtdTitulosAtualizados++;
//						template.update("update biblioteca.titulo_catalografico set id_area_conhecimento_cnpq = ? where id_titulo_catalografico = ? ", new Object[]{idAreaCNPqCDUNovo, idTitulo});
//					}
//					
//					if(! atualizarAreaCDU && atualizarAreaBlack){
//						qtdTitulosAtualizados++;
//						template.update("update biblioteca.titulo_catalografico set id_area_conhecimento_cnpq_black = ? where id_titulo_catalografico = ? ", new Object[]{idAreaCNPqBlackNovo, idTitulo});
//					}
//					
//					if(atualizarAreaCDU && atualizarAreaBlack){
//						qtdTitulosAtualizados++;
//						template.update("update biblioteca.titulo_catalografico set id_area_conhecimento_cnpq = ?, id_area_conhecimento_cnpq_black = ? where id_titulo_catalografico = ? ", new Object[]{idAreaCNPqCDUNovo, idAreaCNPqBlackNovo, idTitulo});
//					}
//					
//				}	
//				
//				String siglaSistema = RepositorioDadosInstitucionais.get("siglaSigaa");
//				
//				/**
//				 * Envia um email com as informa��es das atualiza��es feitas no acervo
//				 */
//				String mensagem ="***************************************************************************************************<br/>"
//					+"  Atualiza��o do relacionalmento entre �reas CNPq e classfica��es bibliogr�ficas terminada com sucesso! <br/>"
//					+"  Quantidade de T�tulos Ativos no acervo : "+titulos.size()+" <br/>"
//					+"  Quantidade de T�tulos que foram atualizados: "+qtdTitulosAtualizados+" <br/>"
//					+"  Tempo total atualiza��o : "+((System.currentTimeMillis()-tempo)/1000)+" segundos <br/> "
//					+"***************************************************************************************************<br/>"
//					+"\n\n\n ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA DE BIBLIOTECAS DO "+siglaSistema+". POR FAVOR, N�O RESPOND�-LO. \n\n\n";
//				MailBody mail = new MailBody();
//				mail.setReplyTo("noReply@ufrn.br");
//				mail.setEmail( email );
//				mail.setAssunto("["+siglaSistema+"] - NOTIFICACAO ATUALIZA��O �REAS CNPq ");
//				mail.setMensagem( mensagem );
//				Mail.send(mail);
//				
//			} finally {
//				if(template != null) template.getDataSource().getConnection().close();
//			}
//			
//		} catch (Exception ex) {
//			
//			String siglaSistema = RepositorioDadosInstitucionais.get("siglaSigaa");
//			
//			/**
//			 * Envia um email com o erro caso tenha ocorrido
//			 */
//			String mensagem = "\n\n\n ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA DE BIBLIOTECAS DO "+siglaSistema+". POR FAVOR, N�O RESPOND�-LO. \n\n\n"
//				+"<p>Ocorreu um erro ao tentar atualizar os relacionamentos entre �reas CNPq e classifica��es bibliogr�ficas </p> <br/><br/>";
//			
//			mensagem += ex.getMessage()+" <br/>";
//			
//			for (StackTraceElement stack : ex.getStackTrace()) {
//				mensagem += stack.toString()+" <br/>";
//			}
//			
//			MailBody mail = new MailBody();
//			mail.setReplyTo("noReply@ufrn.br");
//			mail.setEmail( email );
//			mail.setAssunto("["+RepositorioDadosInstitucionais.get("siglaSigaa")+"] - ERRO NOTIFICACAO ATUALIZA��O �REAS CNPq ");
//			mail.setMensagem( mensagem );
//			Mail.send(mail);
//		}	
//			
//	}
	
}
