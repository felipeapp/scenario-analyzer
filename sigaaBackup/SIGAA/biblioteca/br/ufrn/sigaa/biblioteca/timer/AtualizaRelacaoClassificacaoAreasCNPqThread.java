/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/09/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.timer;


/**
 *
 * <p>Thread que roda atualizando o cache do relacionamento entre áreas CNPq e classificações bibliotegráficas (CDU, black, CDD)</p>
 * <p>No final da atualização será enviado um email com o resumo das alterações feitas</p>
 * 
 * @author jadson
 * @deprecated  não pode mais sair atualização de acordo com o sistema porque o bibliotecário pode mudar o que o sistema gerou, 
 * então apagaria o que os bibliotecários alteraram.
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
//			// se não informou a hora da execução, ou a hora de execução já possou 
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
//	 * <p> Método que realiza a atualização do cache gerado para o relacionamento entre áreas CNPq e classfições bibliográficas ( CDU, black, CDD). </p>
//	 *
//	 * <p> Por uma questão de otimização dos relatórios, as áreas CNPq dos título são calculadas no momento da criação ou atualização dos Título
//	 * e são guardas neles próprios, assim não é preciso ter esse trabalho no momento da consulta dos relatórios.
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
//				 ///// Busca TODOS títulos ativo no sistema /////////
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
//				 * Para casa título existente no acervo, calcula novamente a área CNPq com base nas novas regras de relacionamento e atualiza isso no título
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
//					// Só atualiza se mudou a área cnpq //
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
//				 * Envia um email com as informações das atualizações feitas no acervo
//				 */
//				String mensagem ="***************************************************************************************************<br/>"
//					+"  Atualização do relacionalmento entre áreas CNPq e classficações bibliográficas terminada com sucesso! <br/>"
//					+"  Quantidade de Títulos Ativos no acervo : "+titulos.size()+" <br/>"
//					+"  Quantidade de Títulos que foram atualizados: "+qtdTitulosAtualizados+" <br/>"
//					+"  Tempo total atualização : "+((System.currentTimeMillis()-tempo)/1000)+" segundos <br/> "
//					+"***************************************************************************************************<br/>"
//					+"\n\n\n ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA DE BIBLIOTECAS DO "+siglaSistema+". POR FAVOR, NÃO RESPONDÊ-LO. \n\n\n";
//				MailBody mail = new MailBody();
//				mail.setReplyTo("noReply@ufrn.br");
//				mail.setEmail( email );
//				mail.setAssunto("["+siglaSistema+"] - NOTIFICACAO ATUALIZAÇÃO ÁREAS CNPq ");
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
//			String mensagem = "\n\n\n ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA DE BIBLIOTECAS DO "+siglaSistema+". POR FAVOR, NÃO RESPONDÊ-LO. \n\n\n"
//				+"<p>Ocorreu um erro ao tentar atualizar os relacionamentos entre áreas CNPq e classificações bibliográficas </p> <br/><br/>";
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
//			mail.setAssunto("["+RepositorioDadosInstitucionais.get("siglaSigaa")+"] - ERRO NOTIFICACAO ATUALIZAÇÃO ÁREAS CNPq ");
//			mail.setMensagem( mensagem );
//			Mail.send(mail);
//		}	
//			
//	}
	
}
