/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '14/06/2010'
 *
 */

package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoTurmaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Processador respons�vel por notificar as solicita��es de abertura de turma
 *
 * @author Nilber Joaquim
 *
 */
public class ProcessadorNotificarSolicitacaoTurma extends AbstractProcessador {

	/** 
	 * Manda um email para todos os Chefes e Secret�rios de todos os departamentos
	 * notificando quais as solicita��es de turma que n�o foram atendidas ainda. 
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		// verifica se est� no per�odo de cria��o de turmas
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		
		int ano = 0;
		int periodo = 0;
		Collection<SolicitacaoTurma> solicitacoes = new ArrayList<SolicitacaoTurma>();
		
		SolicitacaoTurmaDao stDao = getDAO(SolicitacaoTurmaDao.class, mov);
		if (calendario.isPeriodoCadastroTurmas()) {
			ano = calendario.getAnoNovasTurmas();
			periodo = calendario.getPeriodoNovasTurmas();
			Collection<SolicitacaoTurma> lista = stDao.findPendenteByAnoPeriodo(ano,periodo);
			if (!isEmpty(lista))
				solicitacoes.addAll(lista);
		} 
		if (calendario.isPeriodoCadastroTurmaEnsinoIndiv() || calendario.isPeriodoCadastroTurmasFerias()) {
			ano = calendario.getAnoFeriasVigente();
			periodo = calendario.getPeriodoFeriasVigente();
			Collection<SolicitacaoTurma> lista = stDao.findPendenteByAnoPeriodo(ano,periodo);
			if (!isEmpty(lista))
				solicitacoes.addAll(lista);
		}
		
		ServidorDao servidorDao = getDAO(ServidorDao.class, mov);
		SecretariaUnidadeDao suDao = getDAO(SecretariaUnidadeDao.class, mov);
		
		if (!isEmpty(solicitacoes)) {
			// agrupa as solicita��es por unidade
			Map<Unidade, List<SolicitacaoTurma>> mapa = new HashMap<Unidade, List<SolicitacaoTurma>>();
			for(SolicitacaoTurma st : solicitacoes){
				Unidade unidade = st.getComponenteCurricular().getUnidade();
				if (mapa.get(unidade) == null)
					mapa.put(unidade, new ArrayList<SolicitacaoTurma>());
				mapa.get(unidade).add(st);
			}
			
			// para cada departamento
			for (Unidade departamento : mapa.keySet()) {
				// lista de componentes curriculares
				Set<ComponenteCurricular> componentes = new TreeSet<ComponenteCurricular>();
				for (SolicitacaoTurma solicitacao : mapa.get(departamento))
					componentes.add(solicitacao.getComponenteCurricular());
				
				// mensagem
				StringBuilder mensagem = new StringBuilder();
				mensagem.append("<p>Caro Chefe de Departamento,</p>\n" +
						"<p>H� solicita��es de turmas pendentes para os seguintes componentes curriculares:</p>\n" +
						"<ul>\n");
				for (ComponenteCurricular componente : componentes)
					mensagem.append("<li>" + componente.getCodigoNome() + "</li>\n");
				mensagem.append("</ul>\n");
				mensagem.append("<p>Para visualiz�-las acesse, no SIGAA / Portal do Docente, o menu Chefia -> Turmas -> Gerenciar Solicita��es de Turmas.</p>\n");
				mensagem.append("<br/>" +
						"<p>Para maiores informa��es, por favor entre em contato com o DAE.</p>");
				
				// email
				MailBody mail = new MailBody();
				mail.setFromName(RepositorioDadosInstitucionais.get("siglaSigaa"));
				mail.setContentType(MailBody.HTML);
				mail.setAssunto("Solicita��o de Turma pendente.");
				mail.setMensagem(mensagem.toString());
				//usu�rio que esta enviando e email para resposta.
				String dominio = RepositorioDadosInstitucionais.getLinkSigaa();
				if (isEmpty(dominio))
					dominio = RepositorioDadosInstitucionais.getSiglaInstituicao();
				else
					dominio = dominio.replaceFirst("www.", "");
				mail.setReplyTo("noReply@" + dominio);
				
				// destinat�rios
				StringBuilder bcc = new StringBuilder();
				Collection<Servidor> chefes = servidorDao.findChefesByDepartamento(departamento.getId());
				if (chefes != null) {
					for (Servidor chefe : chefes)
						if (!isEmpty(chefe.getPessoa().getEmail()))
								bcc.append(chefe.getPessoa().getEmail() + "; ");
				}
				Collection<SecretariaUnidade> secretarias = suDao.findByUnidade(departamento.getId(),null);
				if (secretarias != null) {
					for (SecretariaUnidade secretaria : secretarias)
						if (!isEmpty(secretaria.getUsuario().getPessoa().getEmail()))
							bcc.append(secretaria.getUsuario().getPessoa().getEmail() + "; ");
				}
				// remove a �ltima v�rgula da lista de e-mails
				if (bcc.length() > 0)
					bcc.delete(bcc.lastIndexOf("; "), bcc.length());
				
				mail.setBcc(bcc.toString());
				if (bcc.length() > 0)
					Mail.send(mail);	
			}
		}
		servidorDao.close();
		suDao.close();
		stDao.close();
		return mov;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
}