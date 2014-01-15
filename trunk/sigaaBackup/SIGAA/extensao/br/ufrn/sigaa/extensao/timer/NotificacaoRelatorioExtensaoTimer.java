/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/12/2008
 *
 */
package br.ufrn.sigaa.extensao.timer;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * Thread que roda periodicamente enviando e-mail para os coordenadores de
 * ações de extensão que estão a 30 dias ou menos da data de finalização.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class NotificacaoRelatorioExtensaoTimer extends TarefaTimer {

	public void run() {
		try {
			notificarCoordenadores();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Lista todas as ações de extensão que serão finalizadas nos próximos 30 dias
	 * e envia e-mail para os coordenadores avisando do cadastramento do relatório.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void notificarCoordenadores() {

		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		String nomeSistema =  RepositorioDadosInstitucionais.get("nomeSigaa");
		
		JdbcTemplate template = new JdbcTemplate(Database.getInstance().getSigaaDs());
		
		// Todos os alunos ativos devem ser recalculados
		// TODO Melhorar código: Torná-lo independente do SGBD.
		String sqlBuscasAtividadesPendentes = 
				" SELECT a.id_atividade, a.sequencia, a.id_tipo_atividade_extensao, projeto.titulo, projeto.ano, usuario.email as emailCoordenador, pessoa.nome as nomeCoordenador " +
				" FROM extensao.atividade a "+
				" INNER JOIN projetos.projeto projeto on a.id_atividade = projeto.id_projeto "+
				" INNER JOIN projetos.membro_projeto coordenador on coordenador.id_membro_projeto = projeto.id_coordenador "+
				" INNER JOIN comum.pessoa pessoa ON pessoa.id_pessoa = coordenador.id_pessoa "+
				" INNER JOIN comum.usuario usuario ON usuario.id_pessoa = pessoa.id_pessoa "+
				" WHERE projeto.data_fim > now() and (   ( projeto.data_fim - now() ) <= cast('30 day' as interval) ) " +
				" AND usuario.inativo = falseValue() AND projeto.id_tipo_situacao_projeto in ";
				UFRNUtils.gerarStringIn(new int[] { TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS, 
						TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS, TipoSituacaoProjeto.EM_ANDAMENTO, TipoSituacaoProjeto.EXTENSAO_SUBMETIDO, 
						TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS, TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO});	
			
		 
		
		
		// The returned result will be a list of maps. Each map stores a row of the result   //
		// set with the column names as the keys.                                            //
		List<Map<String,Object>> acoesFinalizando = template.queryForList(sqlBuscasAtividadesPendentes);

		int contadorAcoesExtensao = acoesFinalizando.size();
		int contadorEmailsEviados = 0;
		
		GenericDAO dao = new GenericSigaaDAO();
		try {

			for (Map<String,Object> idAcao : acoesFinalizando) {
				
				String nomeCoordenador = (String)  idAcao.get("nomeCoordenador");
				String emailCoordenador = (String)  idAcao.get("emailCoordenador");
				
				if(   StringUtils.notEmpty( nomeCoordenador  )  &&  StringUtils.notEmpty( emailCoordenador )    ){
					
					// para gerar o código da atividade de acordo com as regras de negócio //
					AtividadeExtensao atividadeTemp = new AtividadeExtensao();
					atividadeTemp.setSequencia(  (Integer) idAcao.get("sequencia") );
					atividadeTemp.setTipoAtividadeExtensao( new TipoAtividadeExtensao((Integer) idAcao.get("id_tipo_atividade_extensao")));
					atividadeTemp.setProjeto( new Projeto());
					atividadeTemp.setTitulo((String) idAcao.get("titulo"));
					atividadeTemp.setAno((Integer) idAcao.get("ano"));
										
					enviarEmail(atividadeTemp.getCodigoTitulo(), nomeCoordenador,  emailCoordenador, siglaSistema, nomeSistema);
					
					contadorEmailsEviados++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
		System.out.println(" ********************************************************** ");
		System.out.println(" "+contadorAcoesExtensao+" ações de extensao perto de vencer. ");
		System.out.println(" "+contadorEmailsEviados+" emails enviados. ");
		System.out.println(" ********************************************************** ");
	}

	
	public void enviarEmail(String codigoTituloAtividade, String nomeCoordenador, String emailCoordenador, String siglaSistema, String nomeSistema) {
		
		// enviando e-mail para o candidato		
		MailBody email = new MailBody();
		email.setAssunto("["+siglaSistema+"] Envio de Relatório de Ação de Extensão");
		email.setContentType(MailBody.HTML);
		email.setNome(nomeCoordenador);
		email.setEmail(emailCoordenador);

		
		StringBuffer msg = new StringBuffer();
		msg.append("<div style=\"width: 100%; text-aling: center;\"> Mensagem Gerada pelo "+siglaSistema+" - "+nomeSistema+" </div>");
		
		msg.append("<br/><br/>" + "Caro(a) Coordenador(a) "+nomeCoordenador+", "
				+"<br/><br/>"
				+"A ação de Extensão: <span style=\"font-style: italic; font-weight: bold;\"> '"+ codigoTituloAtividade+ "' </span>. "
				+"<br/>"
				+"será finalizada em menos de 30 dias. Não esqueça de cadastrar os relatórios parciais e/ou finais desta ação.");
		
		email.setMensagem(msg.toString());	
		Mail.send(email);

	}

	public static void main(String[] args) throws RemoteException, NamingException, CreateException {

		System.out.println("\n\n\n INVOCANDO TIMER \n\n\n ");

		NotificacaoRelatorioExtensaoTimer timer = new NotificacaoRelatorioExtensaoTimer();
		timer.notificarCoordenadores();

		System.out.println("\n\n\n TIMER REALIZADO COM SUCESSO \n\n\n ");

	}

	
}
