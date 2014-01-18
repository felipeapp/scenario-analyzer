/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 13/05/2009
 *
 */
package br.ufrn.sigaa.arq.util;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Classe utilizada para criar e despachar um e-mail notificando um ou mais
 * avaliador sobre a submissão de um projeto.
 * 
 * @author Daniel Augusto
 * 
 */
public class EnvioMensagemHelper {

	/** Atributo utilizado para formar mensagens de envio */
    public static final String PROJETO_ASSOCIADO = "Associado";
    /** Atributo utilizado para formar mensagens de envio */
	public static final String PROJETO_EXTENSAO = "de Extensão";
	/** Atributo utilizado para formar mensagens de envio */
	public static final String PROJETO_PESQUISA = "de Pesquisa";
	/** Atributo utilizado para formar mensagens de envio */
	public static final String PROJETO_MONITORIA = "de Monitoria";
	/** Atributo utilizado para formar mensagens de envio */
	private static final int CAPACIDADE_INIT = 200;

	/** Atributo utilizado para criar a mensagem a ser enviada */
	private static StringBuffer mensagem;
	/** Atributo utilizado para criar o e-mail a ser enviado */
	private static MailBody mail;

	/**
	 * Informa um consultor sobre projetos submetidos para avaliação através do
	 * envio de e-mail.
	 * 
	 * @param projetos
	 *            - coleção de Projetos
	 * @param tipoProjeto
	 *            - define qual o tipo do projeto
	 * @param consultor
	 *            - define o consultor a ser informado
	 * @throws DAOException
	 */
	public static void notificarSubmissaoProjeto(Collection<Projeto> projetos, String tipoProjeto,
			Consultor consultor) throws DAOException {

		mensagem = new StringBuffer(CAPACIDADE_INIT);

		mensagem.append("Prezado(a) Sr(a) ");
		mensagem.append(consultor.getNome());
		mensagem.append(",\n\nInformamos que ");

		if (projetos.size() == 1) {
			mensagem.append("o Projeto ");
			mensagem.append(tipoProjeto);
			mensagem.append(" ");
			mensagem.append(((Projeto) projetos.toArray()[0]).getTitulo());
			mensagem.append(" foi submetido para sua avaliação.");
		} else {
			mensagem.append("os seguintes projetos ");
			mensagem.append(tipoProjeto);
			mensagem.append(" foram submetidos para sua avaliação.");
			for (Projeto proj : projetos) {
				mensagem.append("\n\nTitulo:  ");
				mensagem.append(proj.getTitulo());
			}
		}
		
		if (consultor.isInterno()) {
			mensagem
			.append("\n\nPara visualizar o conteúdo deste projeto e proceder sua avaliação, utilize o caminho Portal Docente > ");
			mensagem.append(tipoProjeto.substring(3) + " > Acessar Portal do Consultor.");
		}
		
		mail = new MailBody();
		mail.setContentType(MailBody.TEXT_PLAN);
		mail.setAssunto("Notificação de Avaliação - MENSAGEM AUTOMÁTICA");
		mail.setMensagem(mensagem.toString());
		mail.setEmail(consultor.getEmail());
		mail.setNome(consultor.getNome());

		Mail.send(mail);

	}

	/**
	 * Informa um avaliador sobre um projeto submetido para avaliação através do
	 * envio de e-mail.
	 * 
	 * @param projeto
	 *            - Projeto
	 * @param tipoProjeto
	 *            - define qual o tipo do projeto
	 * @param servidor
	 *            - define o avalidador a ser informado
	 * @throws DAOException
	 */
	public static void notificarSubmissaoProjeto(Projeto projeto,
			String tipoProjeto, Servidor servidor) throws DAOException {

		mensagem = new StringBuffer(CAPACIDADE_INIT);

		String nomeUsuario = servidor.getPessoa().getNome();
		mensagem.append("Prezado(a) Sr(a): ");
		mensagem.append(nomeUsuario);
		mensagem.append(",\n\nInformamos que o Projeto ");
		mensagem.append(tipoProjeto);
		mensagem.append(" ");
		mensagem.append(projeto.getTitulo());
		mensagem.append(" foi submetido para sua avaliação.\n\nPara visualizar o conteúdo deste projeto e proceder sua avaliação, utilize o link correspondente disponível no Portal Docente.");

		mail = new MailBody();
		mail.setContentType(MailBody.TEXT_PLAN);
		mail.setAssunto("Notificação para análise de Projeto");
		mail.setMensagem(mensagem.toString());
		mail.setEmail(servidor.getPrimeiroUsuario().getEmail());
		mail.setNome(nomeUsuario);

		Mail.send(mail);

	}

	/**
	 * Informa aos membros de um projeto submetido para avaliação através do
	 * envio de e-mail.
	 * 
	 * @param projeto
	 *            - ProjetoPesquisa
	 *
	 * @throws DAOException
	 */
	public static void comunicarMembrosProjeto(ProjetoPesquisa projeto)
			throws DAOException {

		String nome = null;
		for (MembroProjeto membro : projeto.getMembrosProjeto()) {
			nome = membro.getPessoa().getNome();
			mensagem = new StringBuffer(CAPACIDADE_INIT);
			mensagem.append("Caro(a) ");
			mensagem.append(nome);
			mensagem.append(",\n\n");
			mensagem
					.append("Informamos que seu nome foi incluído como participante do projeto de pesquisa ");
			mensagem.append(projeto.getCodigoTitulo());
			if (membro.getFuncaoMembro().getId() != FuncaoMembro.COORDENADOR) {
				mensagem.append(", coordenado por ");
				mensagem.append(projeto.getCoordenador().getNome());

			}
			mensagem.append(".\n\n Sua participação foi definida como ");
			mensagem.append(membro.getFuncaoMembro().getDescricao());
			mensagem.append(", com dedicação semanal de ");
			mensagem.append(membro.getChDedicada());
			mensagem.append("h.\n\n");
			
			mail = new MailBody();
			mail.setContentType(MailBody.TEXT_PLAN);
			mail.setAssunto("Comunicar Participação - MENSAGEM AUTOMÁTICA");
			mail.setMensagem(mensagem.toString());
			mail.setEmail(membro.getPessoa().getEmail());
			mail.setNome(nome);

			Mail.send(mail);

		}

	}
	
	
	/**
	 * Informa aos chefes de deptos de um projeto submetido que envolve servidores do seu depto. através do
	 * envio de e-mail.
	 * 
	 * @param projeto - Projeto
	 *
	 * @throws DAOException
	 */
	public static void comunicarResponsaveisUnidade(Projeto projeto, List<Responsavel> responsaveis) throws DAOException {	    
	    for (Responsavel responsavel : responsaveis) {
		mensagem = new StringBuffer(CAPACIDADE_INIT);

		String nomeUsuario = responsavel.getServidor().getPessoa().getNome();
		mensagem.append("Prezado(a) Sr(a): ");
		mensagem.append(nomeUsuario);
		mensagem.append(",\n\nInformamos que o Projeto Acadêmico Associado");
		mensagem.append(" '");
		mensagem.append(projeto.getTitulo());		
		mensagem.append("' foi submetido para avaliação dos membros do comitê de Ensino, Pesquisa e Extensão.");		
		mensagem.append("' O motivo do recebimento desta mensagem é que o sistema detectou que algum membro da equipe deste projeto faz parte da unidade da qual o(a) Sr(a). é gestor(a).");
		mensagem.append("\n\nEsta mensagem tem caráter apenas informativo, não sendo necessário que V.sa. execute qualquer ação no SIGAA para sua validação.");
		
		mensagem.append(" \n\nO referido projeto envolve os seguintes docentes, técnicos administrativos, discentes e membros da comunidade externa em sua equipe: ");		
		for (MembroProjeto membro : projeto.getEquipe()) {
		    mensagem.append("\n");
		    mensagem.append("\nNome: " + membro.getPessoa().getNome());
		    mensagem.append("\nCategoria: " + membro.getCategoriaMembro().getDescricao());
		    mensagem.append("\nFunção: " + membro.getFuncaoMembro().getDescricao());
		    mensagem.append("\nCarga Horária: " + membro.getChDedicada() + " hora(s).");
		}
		mensagem.append(" \n\nLogo abaixo, segue o resumo da proposta:");
		mensagem.append("\nTitulo: " + projeto.getTitulo());
		mensagem.append("\nAno: " + projeto.getAno());
		mensagem.append("\nResumo:\n" + projeto.getResumo());
		
		mensagem.append("\n\nEsta mensagem foi enviada automaticamente através do Sistema de Gestão de Atividades Acadêmicas (SIGAA).");
		mensagem.append("\nPor favor, não respondê-la.");
		
		mail = new MailBody();
		mail.setContentType(MailBody.TEXT_PLAN);
		mail.setAssunto("SIGAA - Notificação de Envio de Projeto - MENSAGEM AUTOMÁTICA");
		mail.setMensagem(mensagem.toString());
		mail.setEmail(responsavel.getServidor().getPessoa().getEmail());
		mail.setNome(nomeUsuario);
		Mail.send(mail);
	    }
	}
	
	/**
	 * Informa aos chefes de deptos de um projeto submetido que envolve servidores do seu depto. através do
	 * envio de e-mail.
	 * 
	 * @param projeto - Projeto
	 *
	 * @throws DAOException
	 */
	public static void comunicarRelatorioResponsaveisUnidade(Projeto projeto, List<Responsavel> responsaveis) throws DAOException {	    
	    for (Responsavel responsavel : responsaveis) {
		mensagem = new StringBuffer(CAPACIDADE_INIT);

		String nomeUsuario = responsavel.getServidor().getPessoa().getNome();
		mensagem.append("Prezado(a) Sr(a): ");
		mensagem.append(nomeUsuario);
		mensagem.append(",\n\nInformamos que há um Relatório referente à Ação Associada");
		mensagem.append(" '");
		mensagem.append(projeto.getTitulo());		
		mensagem.append("' foi submetido para avaliação dos membros do comitê de Ensino, Pesquisa e Extensão.");		
		mensagem.append("' O motivo do recebimento desta mensagem é que o sistema detectou que algum membro da equipe deste projeto faz parte da unidade da qual o(a) Sr(a). é gestor(a).");
		mensagem.append("\n\nEsta mensagem tem caráter apenas informativo, não sendo necessário que V.sa. execute qualquer ação no SIGAA para sua validação.");
		
		mensagem.append(" \n\nO referido projeto envolve os seguintes docentes, técnicos administrativos, discentes e membros da comunidade externa em sua equipe: ");		
		for (MembroProjeto membro : projeto.getEquipe()) {
		    mensagem.append("\n");
		    mensagem.append("\nNome: " + membro.getPessoa().getNome());
		    mensagem.append("\nCategoria: " + membro.getCategoriaMembro().getDescricao());
		    mensagem.append("\nFunção: " + membro.getFuncaoMembro().getDescricao());
		    mensagem.append("\nCarga Horária: " + membro.getChDedicada() + " hora(s).");
		}
		
		mensagem.append("\n\nEsta mensagem foi enviada automaticamente através do Sistema de Gestão de Atividades Acadêmicas (SIGAA).");
		mensagem.append("\nPor favor, não respondê-la.");
		
		mail = new MailBody();
		mail.setContentType(MailBody.TEXT_PLAN);
		mail.setAssunto("SIGAA - Notificação de Envio de Relatório - MENSAGEM AUTOMÁTICA");
		mail.setMensagem(mensagem.toString());
		mail.setEmail(responsavel.getServidor().getPessoa().getEmail());
		mail.setNome(nomeUsuario);
		Mail.send(mail);
	    }
	}
	
	/**
	 * Método utilizado para notificação de avaliadores de projetos.
	 * 
	 * @param projeto
	 * @param servidor
	 * @param texto Mensagem de notificação propriamente dita.
	 * @throws DAOException
	 */
	public static void notificarAvaliadorProjeto(Servidor servidor, String texto) throws DAOException {
	    mensagem = new StringBuffer(CAPACIDADE_INIT);
	    
	    String nomeUsuario = servidor.getPessoa().getNome();
	    mensagem.append(texto);
	    mensagem.append("\n\nEsta mensagem foi enviada automaticamente através do Sistema de Gestão de Atividades Acadêmicas (SIGAA).");
	    mensagem.append("\nPor favor, não respondê-la.");
	    
	    mail = new MailBody();
	    mail.setContentType(MailBody.TEXT_PLAN);
	    mail.setAssunto("[Mensagem Automática] Notificação para Avaliação de Projetos.");
	    mail.setMensagem(mensagem.toString());
	    mail.setEmail(servidor.getPrimeiroUsuario().getEmail());
	    mail.setNome(nomeUsuario);
	    Mail.send(mail);
	}
	 
	/**
	 * Notificação quanto aos email enviado quanto o edital é de ações acadêmicas Associadas.
	 */
	public static void notificaAvaliadoresAssociados( Projeto projeto, String tipoProjeto, Servidor servidor ) throws DAOException {
			MailBody mail = new MailBody();
		    mail.setContentType(MailBody.HTML);

		    // Definir remetente
		    mail.setFromName( servidor.getPessoa().getNome() );
		    mail.setEmail( servidor.getPrimeiroUsuario().getEmail() ) ;
		    mail.setAssunto("Notificação para análise de Projeto");
		    String mensagem = "Prezado(a) " + servidor.getPessoa().getNome() + ", informamos que o Projeto " + tipoProjeto + ", intitulado : <b>" + projeto.getTitulo() + "</b>" +  
		    				  " foi submetido para a sua avaliação<br/><br/>";
		    mensagem +=	" Para visualizar o conteúdo deste projeto e proceder sua avaliação, utilize o link correspondente disponível no Portal Docente ( " + RepositorioDadosInstitucionais.get("siglaSigaa") + " -> Portal Docente -> Ações Integradas -> Avaliar Proposta ) ou " +
		    		"<a href='"+ RepositorioDadosInstitucionais.getLinkSigaa() +"/sigaa/public/extensao/avaliacaoProjeto/"+servidor.getId()+".jsf'>Clique aqui</a> <br/><br/>";
		    
		    mail.setMensagem(mensagem);
		    Mail.send(mail);
	}
	
}