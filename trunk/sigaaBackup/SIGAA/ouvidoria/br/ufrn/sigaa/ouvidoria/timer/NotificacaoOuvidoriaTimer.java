package br.ufrn.sigaa.ouvidoria.timer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.sigaa.ouvidoria.dao.ManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.DelegacaoUsuarioResposta;
import br.ufrn.sigaa.ouvidoria.dominio.NotificacaoManifestacaoPendente;
import br.ufrn.sigaa.parametros.dominio.ParametrosOuvidoria;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;


/**
 * Timer Responsável pelas notificações aos usuários da ouvidoria com manifestacões pendentes.
 * @author suelton 
 */
public class NotificacaoOuvidoriaTimer extends TarefaTimer {
	
	/** Executa a thread.
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			notificarManifestacoesPendentes();
		} catch (Exception e) {
			notificarErro(e);
		}
	}
	
	/**
	 * Notifica os designados  para responder uma manifestação e os responsáveis pela respectiva unidade
	 * A respeito dos prazos das manifestações.  
	 * @throws DAOException
	 */
	public void notificarManifestacoesPendentes() throws DAOException {
		
		int diasRestantes = ParametroHelper.getInstance().getParametroInt(ParametrosOuvidoria.DIAS_RESTANTES_NOTIFICACAO_MANIFESTACAO);
		ManifestacaoDao dao = new ManifestacaoDao();
		List<NotificacaoManifestacaoPendente> pendentes = null;		
		
		try {
			pendentes = dao.findAllManifestacoesPendentes(diasRestantes);
		} finally {
			if (dao != null)
				dao.close();
		}
		
		if (pendentes != null){
			
			List<NotificacaoManifestacaoPendente> designados = new ArrayList<NotificacaoManifestacaoPendente>(); 
			
			for (NotificacaoManifestacaoPendente n : pendentes){
				
				Pessoa responsavel = n.getResponsavel();
				ArrayList<DelegacaoUsuarioResposta> delegacoes = (ArrayList<DelegacaoUsuarioResposta>) n.getManifestacoes();
				
				String textoResp = "Caro(a) "+responsavel.getNome()+
									",<br/><br/> Este e-mail tem como objetivo notificá-lo das manifestações da ouvidoria pendentes de respostas.<br/><br/>"+
								    "<b>Acesse o Módulo da Ouvidoria no Sigaa para visualizar essa manifestação.</b>"+
									"<br/><br/>"+
									"As manifestações abaixo estão pendentes de análise.<br/><br/><br/>";
				
				textoResp += "<table border='1'><thead><tr>"+
	  			  "<th><b>Número de Manifestação</b></th>"+
	  			  "<th><b>Prazo de Resposta</b></th>"+
	  			  "<th><b>Expirou</b></th>"+
	  			  "<th style='text-align:left;'><b>Assunto</b></th>"+
	  			  "<th><b>Título</b></th>"+
	  			  "<th><b>Designado</b></th>"+
	  			  "</tr></thead><tbody>";
	
				int count = 0;
							
				for (DelegacaoUsuarioResposta d : delegacoes){
					
					count++;
					Calendar cal = Calendar.getInstance();
				    cal.setTime( d.getHistoricoManifestacao().getManifestacao().getDataCadastro() );

					if (d.getPessoa()==null){
						
						textoResp += "<tr>"+
						  			  "<td style='text-align:center;'>"+d.getHistoricoManifestacao().getManifestacao().getNumero()+"/"+cal.get(Calendar.YEAR)+"</td>"+
						  			  "<td style='text-align:center;'>"+new SimpleDateFormat("dd/MM/yy").format(d.getHistoricoManifestacao().getPrazoResposta())+"</td>"+
						  			  "<td>"+(new Date().before( d.getHistoricoManifestacao().getManifestacao().getDataCadastro()) ?"Não":"Sim")+"</td>"+
						  			  "<td>"+d.getHistoricoManifestacao().getManifestacao().getAssuntoManifestacao().getDescricao()+"</td>"+
						  			  "<td>"+d.getHistoricoManifestacao().getManifestacao().getTitulo()+"</td>"+
						  			  "<td style='text-align:center;'>-</td>"+
						  			  "</tr>";	
						
					} else {
						
						textoResp += "<tr>"+
						  			  "<td style='text-align:center;'>"+d.getHistoricoManifestacao().getManifestacao().getNumero()+"/"+cal.get(Calendar.YEAR)+"</td>"+
						  			  "<td style='text-align:center;'>"+new SimpleDateFormat("dd/MM/yy").format(d.getHistoricoManifestacao().getPrazoResposta())+"</td>"+
						  			  "<td>"+(new Date().before( d.getHistoricoManifestacao().getManifestacao().getDataCadastro()) ?"Não":"Sim")+"</td>"+
						  			  "<td>"+d.getHistoricoManifestacao().getManifestacao().getAssuntoManifestacao().getDescricao()+"</td>"+
						  			  "<td>"+d.getHistoricoManifestacao().getManifestacao().getTitulo()+"</td>"+
						  			  "<td>"+d.getPessoa().getNome()+"</td>"+
						  			  "</tr>";	
						
						NotificacaoManifestacaoPendente nDesig = null;
						
						if (!d.getHistoricoManifestacao().getManifestacao().isAguardandoParecer()) {
					        
						    if (!designados.isEmpty()){
								for ( NotificacaoManifestacaoPendente nD : designados ){
									if (nD.getDesignado().getId()==d.getPessoa().getId())
										nDesig = nD;
								}
							}
							
							if (nDesig == null){
								nDesig = new NotificacaoManifestacaoPendente();
								nDesig.setResponsavel(responsavel);
								nDesig.setDesignado(d.getPessoa());
								nDesig.setManifestacoes(new ArrayList<DelegacaoUsuarioResposta>());
								nDesig.getManifestacoes().add(d);
								designados.add(nDesig);
							} else {
								nDesig.getManifestacoes().add(d);
							}
						}	
					}	
				}
				
				textoResp += "</tbody></table><br/><br/>";
				textoResp += "<center><b>"+count+" Manifestações Pendentes de Análise</b></center><br/><br/>";
				textoResp += "ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA " +
				RepositorioDadosInstitucionais.get("siglaSigaa")+".<br />POR FAVOR, NÃO RESPONDÊ-LO. <br/><br/><br/>";
				
				MailBody bodyResp = new MailBody();
				bodyResp.setAssunto("SIGAA - Ouvidoria: Manifestação Pendente de Análise.");
				bodyResp.setMensagem(textoResp);
				bodyResp.setFromName("SIGAA - Ouvidoria");
				bodyResp.setContentType(MailBody.HTML);
				bodyResp.setEmail(responsavel.getEmail());
				Mail.send(bodyResp);
				
			}
			
			for ( NotificacaoManifestacaoPendente nD : designados ){
				
				Pessoa designado = nD.getDesignado();
				ArrayList<DelegacaoUsuarioResposta> delegacoesDesig = (ArrayList<DelegacaoUsuarioResposta>) nD.getManifestacoes();

				Collections.sort(nD.getManifestacoes(), new Comparator<DelegacaoUsuarioResposta>(){
					public int compare(DelegacaoUsuarioResposta d1, DelegacaoUsuarioResposta d2) {
						 return d1.getHistoricoManifestacao().getManifestacao().getDataCadastro().compareTo(d2.getHistoricoManifestacao().getManifestacao().getDataCadastro());
					}
				});
				
				String textoDesig = "Caro(a) "+designado.getNome()+
				",<br/><br/> Este e-mail tem como objetivo notificá-lo das manifestações da ouvidoria pendentes de respostas.<br/><br/>"+
			    "<b>Acesse o Módulo da Ouvidoria no Sigaa para visualizar essa manifestação.</b>"+
				"<br/><br/>"+
				"As manifestações abaixo estão pendentes de análise.<br/><br/><br/>";
				
				textoDesig += "<table border='1'><thead><tr>"+
				  			  "<th><b>Número de Manifestação</b></th>"+
				  			  "<th><b>Prazo de Resposta</b></th>"+
				  			  "<th><b>Expirou</b></th>"+
				  			  "<th style='text-align:left;'><b>Assunto</b></th>"+
				  			  "<th><b>Título</b></th>"+
				  			  "</tr></thead><tbody>";
				
				int count = 0;
				
				for (DelegacaoUsuarioResposta d : delegacoesDesig){
					
					count++;
					Calendar cal = Calendar.getInstance();
				    cal.setTime( d.getHistoricoManifestacao().getManifestacao().getDataCadastro() );
					
					textoDesig += "<tr>"+
					  			  "<td style='text-align:center;'>"+d.getHistoricoManifestacao().getManifestacao().getNumero()+"/"+cal.get(Calendar.YEAR)+"</td>"+
					  			  "<td style='text-align:center;'>"+new SimpleDateFormat("dd/MM/yy").format(d.getHistoricoManifestacao().getPrazoResposta())+"</td>"+
					  			  "<td>"+(new Date().before( d.getHistoricoManifestacao().getManifestacao().getDataCadastro()) ?"Não":"Sim")+"</td>"+
					  			  "<td>"+d.getHistoricoManifestacao().getManifestacao().getAssuntoManifestacao().getDescricao()+"</td>"+
					  			  "<td>"+d.getHistoricoManifestacao().getManifestacao().getTitulo()+"</td>"+
					  			  "</tr>";					
				}
								
				textoDesig += "</tbody></table><br/><br/>";
				textoDesig += "<center><b>"+count+" Manifestações Pendentes de Análise</b></center><br/><br/>";
				textoDesig += "ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA " +
				RepositorioDadosInstitucionais.get("siglaSigaa")+".<br />POR FAVOR, NÃO RESPONDÊ-LO. <br/><br/><br/>";
			
				MailBody bodyDesig = new MailBody();
				bodyDesig.setAssunto("SIGAA - Ouvidoria: Manifestação Pendente de Análise.");
				bodyDesig.setMensagem(textoDesig);
				bodyDesig.setFromName("SIGAA - Ouvidoria");
				bodyDesig.setContentType(MailBody.HTML);
				bodyDesig.setEmail(designado.getEmail());
				Mail.send(bodyDesig);
				
			}
			
		}	
		
	}
	
	/**
	 * Método que manda email pra administração em caso de erro na rotina dessa classe.
	 * 
	 * @param e
	 */
	private void notificarErro(Exception e) {
		e.printStackTrace();
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "Erro SIGAA - NOTIFICAR USUARIOS COM MANIFESTAÇÕES PENDENTES: " + e.getMessage();
		String mensagem =  "Server: " +  NetworkUtils.getLocalName() + "<br>" +
			e.getMessage() + "<br><br><br>" + Arrays.toString(e.getStackTrace()).replace(",", "\n") +
			(e.getCause() != null ? Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n") : "");

		// Enviando email para administração do sistema para notificar do erro.
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem );
		Mail.send(mail);
	}
	

}
