/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.pesquisa.ConsultorDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Processador responsável por notificar os consultores
 * com o código e senha de acesso para avaliação dos projetos pendentes
 * de avaliação
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorNotificacaoConsultores extends AbstractProcessador {

	private static final Pattern TOKEN_CONSULTOR = Pattern.compile("\\{consultor\\}");
	private static final Pattern TOKEN_ACESSO =  Pattern.compile("\\{acesso\\}");
	private static final Pattern TOKEN_SENHA =  Pattern.compile("\\{senha\\}");
	private static final Pattern TOKEN_PROJETOS =  Pattern.compile("\\{projetos\\}");


	/**
	 * Método responsável pela execução do processador de notificação de Consultores 
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);

		MovimentoNotificacaoConsultores notificacaoMov = (MovimentoNotificacaoConsultores) mov;

		ConsultorDao consultorDao = getDAO(ConsultorDao.class, mov);
		Map<Consultor, Collection<ProjetoPesquisa>> mapa = new HashMap<Consultor, Collection<ProjetoPesquisa>>();

		// Template a ser utilizado no envio do e-mail
		String template = null;
		StringBuilder listaProjetos = new StringBuilder();

		try {

			if (notificacaoMov.getCodMovimento() == SigaaListaComando.NOTIFICAR_CONSULTORES) {
				validate(mov);

				// Persistir template
				template = notificacaoMov.getTemplate();
				ParametroHelper.getInstance().atualizaParametro(mov.getUsuarioLogado(), Sistema.SIGAA, ConstantesParametro.TEMPLATE_NOTIFICACAO_CONSULTOR, template);

				// Buscar consultores com avaliações pendentes
				mapa = consultorDao.findPendentesNotificacao();

			} else if (notificacaoMov.getCodMovimento() == SigaaListaComando.NOTIFICAR_CONSULTORES_ESPECIAIS) {
				// Buscar template
				template = ParametroHelper.getInstance().getParametro(ConstantesParametro.TEMPLATE_NOTIFICACAO_CONSULTOR);
				mapa = consultorDao.findEspeciaisPendentesNotificacao();
			}

			// Para cada consultor, preparar email de notificação
			for (Consultor consultor : mapa.keySet() ) {

				// Criar corpo do email
				String corpo = new String( template );

				// Gerar senha
				String senha = consultor.getSenha();
				if ( senha == null ) {
					senha =  StringUtils.generatePassword( Consultor.TAMANHO_SENHA_AUTOMATICA );
					consultorDao.updateField(Consultor.class, consultor.getId(),
							"senha", senha);
				}

				if (notificacaoMov.getCodMovimento() == SigaaListaComando.NOTIFICAR_CONSULTORES) {
					// Listar projetos deste consultor
					listaProjetos = new StringBuilder();
					for (ProjetoPesquisa projeto : mapa.get(consultor) ) {
						listaProjetos.append(projeto.getCodigo() + " - " + projeto.getTitulo() + "\n");
					}
				} else if (notificacaoMov.getCodMovimento() == SigaaListaComando.NOTIFICAR_CONSULTORES_ESPECIAIS) {
					listaProjetos.append("(projetos pertencentes à Grande Área de Conhecimento " +
							consultor.getAreaConhecimentoCnpq().getNome() + ") " );
				}

					// Substituir campos
					corpo = TOKEN_CONSULTOR.matcher(corpo)
						.replaceFirst(consultor.getNome());
					
					if ( !consultor.isInterno() ) {
						
						corpo = TOKEN_ACESSO.matcher(corpo)
							.replaceFirst(notificacaoMov.getHost() + Consultor.ENDERECO_ACESSO + "?" +
									Consultor.CODIGO_CONSULTOR + "=" + Consultor.PREFIXO_USUARIO + consultor.getCodigo());

						corpo = TOKEN_SENHA.matcher(corpo)
						.replaceFirst( senha );
					
					} else {
						
						corpo = TOKEN_ACESSO.matcher(corpo).replaceFirst("Acesse o Portal do Consultor seguindo o caminho: SIGAA -> Portal do Docente -> Pesquisa -> Acessar Portal do Consultor.");
						corpo = TOKEN_SENHA.matcher(corpo).replaceFirst("");
						
					}
					
					corpo = TOKEN_PROJETOS.matcher(corpo)
						.replaceFirst( listaProjetos.toString() );

				// Enviar email
				MailBody mensagem = new MailBody();
				mensagem.setContentType(MailBody.TEXT_PLAN);

				mensagem.setNome(consultor.getNome());
				mensagem.setEmail(consultor.getEmail());
				mensagem.setAssunto("UFRN - Avaliação de Projetos de Pesquisa");
				mensagem.setMensagem( corpo );
				mensagem.setReplyTo(ParametroHelper.getInstance().getParametro(ParametrosPesquisa.EMAIL_REPLY_TO_NOTIFICACAO_CONSULTOR));

				Mail.send(mensagem);
			}
		} finally {
			consultorDao.close();
		}
		return mapa.keySet().size();
	}

	/**
	 * Responsável pela validação necessário para o processamento da Notificação Consultores
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		MovimentoNotificacaoConsultores notificacaoMov = (MovimentoNotificacaoConsultores) mov;

		// Validar template
		if (!StringUtils.notEmpty(notificacaoMov.getTemplate()) ) {
			erros.addErro("É necessário informar o modelo do e-mail de notificação");
		}

		// Verificar se existem consultores a serem notificados
		ConsultorDao consultorDao = getDAO(ConsultorDao.class, mov);
		if ( consultorDao.findTotalPendentesNotificacao() <= 0) {
			erros.addErro("Não há consultores com avaliações pendentes que necessitem de notificação");
		}

		checkValidation(erros);
	}

}
