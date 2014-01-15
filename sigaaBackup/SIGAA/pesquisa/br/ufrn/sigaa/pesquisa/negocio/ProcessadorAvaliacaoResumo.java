/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2008
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoResumo;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;

/**
 * Processador responsável por distribuir e gravar as avaliações dos resumos do CIC.
 * 
 * @author leonardo
 *
 */
public class ProcessadorAvaliacaoResumo extends AbstractProcessador {

	
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoAvaliacaoResumo movd = (MovimentoAvaliacaoResumo) mov;
		GenericDAO dao = getGenericDAO(mov);
		
		if(mov.getCodMovimento() == SigaaListaComando.DISTRIBUIR_AVALIACAO_RESUMO_CIC){
			for(AvaliacaoResumo ava: movd.getAvaliacoes()){
				
				// Se possui Id e não está marcado, remove do banco
				// Se não possui Id e está marcado, cria novo registro
				if(ava.getId() > 0 && !ava.isSelecionado())
					dao.remove(ava);
				else if(ava.getId() <= 0 && ava.isSelecionado())
					dao.create(ava);
			}
		}else if(mov.getCodMovimento() == SigaaListaComando.AVALIAR_RESUMO_CIC){
			
			// A avaliação de resumos é feita individualmente para cada resumo.
			AvaliacaoResumo ava = movd.getAvaliacoes().iterator().next();
			if(ava.getId() > 0){
				dao.update(ava);
			}else{
				dao.create(ava);
			}
			
			// Atualizando o status do resumo de acordo com a avaliação.
			if(ava.isErroConteudo() || ava.isErroPortugues()){
				dao.updateField(ResumoCongresso.class, ava.getResumo().getId(), "status", ResumoCongresso.NECESSITA_CORRECOES);
				
				// Notificar o aluno
				ResumoCongresso resumo = dao.findByPrimaryKey(ava.getResumo().getId(), ResumoCongresso.class);
				MailBody mail = new MailBody();
				mail.setEmail(resumo.getAutor().getEmail());
				mail.setAssunto("Notificação - Resumo do CIC");
				mail.setFromName("SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas");
				mail.setNome(resumo.getAutor().getNome());
				mail.setMensagem("Caro(a) " + resumo.getAutor().getNome() +", <br><br>" +
						"Seu resumo necessita de correções. Acesse o SIGAA na opção 'Pesquisa -> Congresso de Iniciação Científica -> Meus Resumos' " +
						"para visualizar o parecer do avaliador e efetuar as correções necessárias.<br><br>");
				mail.setContentType(MailBody.HTML);
				Mail.send(mail);
				
			} else {
				dao.updateField(ResumoCongresso.class, ava.getResumo().getId(), "status", ResumoCongresso.APROVADO);
			}
		}
		
		return null;
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
