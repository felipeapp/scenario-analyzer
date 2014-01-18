/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
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
 * avaliador sobre a submiss�o de um projeto.
 * 
 * @author Daniel Augusto
 * 
 */
public class EnvioMensagemHelper {

	/** Atributo utilizado para formar mensagens de envio */
    public static final String PROJETO_ASSOCIADO = "Associado";
    /** Atributo utilizado para formar mensagens de envio */
	public static final String PROJETO_EXTENSAO = "de Extens�o";
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
	 * Informa um consultor sobre projetos submetidos para avalia��o atrav�s do
	 * envio de e-mail.
	 * 
	 * @param projetos
	 *            - cole��o de Projetos
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
			mensagem.append(" foi submetido para sua avalia��o.");
		} else {
			mensagem.append("os seguintes projetos ");
			mensagem.append(tipoProjeto);
			mensagem.append(" foram submetidos para sua avalia��o.");
			for (Projeto proj : projetos) {
				mensagem.append("\n\nTitulo:  ");
				mensagem.append(proj.getTitulo());
			}
		}
		
		if (consultor.isInterno()) {
			mensagem
			.append("\n\nPara visualizar o conte�do deste projeto e proceder sua avalia��o, utilize o caminho Portal Docente > ");
			mensagem.append(tipoProjeto.substring(3) + " > Acessar Portal do Consultor.");
		}
		
		mail = new MailBody();
		mail.setContentType(MailBody.TEXT_PLAN);
		mail.setAssunto("Notifica��o de Avalia��o - MENSAGEM AUTOM�TICA");
		mail.setMensagem(mensagem.toString());
		mail.setEmail(consultor.getEmail());
		mail.setNome(consultor.getNome());

		Mail.send(mail);

	}

	/**
	 * Informa um avaliador sobre um projeto submetido para avalia��o atrav�s do
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
		mensagem.append(" foi submetido para sua avalia��o.\n\nPara visualizar o conte�do deste projeto e proceder sua avalia��o, utilize o link correspondente dispon�vel no Portal Docente.");

		mail = new MailBody();
		mail.setContentType(MailBody.TEXT_PLAN);
		mail.setAssunto("Notifica��o para an�lise de Projeto");
		mail.setMensagem(mensagem.toString());
		mail.setEmail(servidor.getPrimeiroUsuario().getEmail());
		mail.setNome(nomeUsuario);

		Mail.send(mail);

	}

	/**
	 * Informa aos membros de um projeto submetido para avalia��o atrav�s do
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
					.append("Informamos que seu nome foi inclu�do como participante do projeto de pesquisa ");
			mensagem.append(projeto.getCodigoTitulo());
			if (membro.getFuncaoMembro().getId() != FuncaoMembro.COORDENADOR) {
				mensagem.append(", coordenado por ");
				mensagem.append(projeto.getCoordenador().getNome());

			}
			mensagem.append(".\n\n Sua participa��o foi definida como ");
			mensagem.append(membro.getFuncaoMembro().getDescricao());
			mensagem.append(", com dedica��o semanal de ");
			mensagem.append(membro.getChDedicada());
			mensagem.append("h.\n\n");
			
			mail = new MailBody();
			mail.setContentType(MailBody.TEXT_PLAN);
			mail.setAssunto("Comunicar Participa��o - MENSAGEM AUTOM�TICA");
			mail.setMensagem(mensagem.toString());
			mail.setEmail(membro.getPessoa().getEmail());
			mail.setNome(nome);

			Mail.send(mail);

		}

	}
	
	
	/**
	 * Informa aos chefes de deptos de um projeto submetido que envolve servidores do seu depto. atrav�s do
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
		mensagem.append(",\n\nInformamos que o Projeto Acad�mico Associado");
		mensagem.append(" '");
		mensagem.append(projeto.getTitulo());		
		mensagem.append("' foi submetido para avalia��o dos membros do comit� de Ensino, Pesquisa e Extens�o.");		
		mensagem.append("' O motivo do recebimento desta mensagem � que o sistema detectou que algum membro da equipe deste projeto faz parte da unidade da qual o(a) Sr(a). � gestor(a).");
		mensagem.append("\n\nEsta mensagem tem car�ter apenas informativo, n�o sendo necess�rio que V.sa. execute qualquer a��o no SIGAA para sua valida��o.");
		
		mensagem.append(" \n\nO referido projeto envolve os seguintes docentes, t�cnicos administrativos, discentes e membros da comunidade externa em sua equipe: ");		
		for (MembroProjeto membro : projeto.getEquipe()) {
		    mensagem.append("\n");
		    mensagem.append("\nNome: " + membro.getPessoa().getNome());
		    mensagem.append("\nCategoria: " + membro.getCategoriaMembro().getDescricao());
		    mensagem.append("\nFun��o: " + membro.getFuncaoMembro().getDescricao());
		    mensagem.append("\nCarga Hor�ria: " + membro.getChDedicada() + " hora(s).");
		}
		mensagem.append(" \n\nLogo abaixo, segue o resumo da proposta:");
		mensagem.append("\nTitulo: " + projeto.getTitulo());
		mensagem.append("\nAno: " + projeto.getAno());
		mensagem.append("\nResumo:\n" + projeto.getResumo());
		
		mensagem.append("\n\nEsta mensagem foi enviada automaticamente atrav�s do Sistema de Gest�o de Atividades Acad�micas (SIGAA).");
		mensagem.append("\nPor favor, n�o respond�-la.");
		
		mail = new MailBody();
		mail.setContentType(MailBody.TEXT_PLAN);
		mail.setAssunto("SIGAA - Notifica��o de Envio de Projeto - MENSAGEM AUTOM�TICA");
		mail.setMensagem(mensagem.toString());
		mail.setEmail(responsavel.getServidor().getPessoa().getEmail());
		mail.setNome(nomeUsuario);
		Mail.send(mail);
	    }
	}
	
	/**
	 * Informa aos chefes de deptos de um projeto submetido que envolve servidores do seu depto. atrav�s do
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
		mensagem.append(",\n\nInformamos que h� um Relat�rio referente � A��o Associada");
		mensagem.append(" '");
		mensagem.append(projeto.getTitulo());		
		mensagem.append("' foi submetido para avalia��o dos membros do comit� de Ensino, Pesquisa e Extens�o.");		
		mensagem.append("' O motivo do recebimento desta mensagem � que o sistema detectou que algum membro da equipe deste projeto faz parte da unidade da qual o(a) Sr(a). � gestor(a).");
		mensagem.append("\n\nEsta mensagem tem car�ter apenas informativo, n�o sendo necess�rio que V.sa. execute qualquer a��o no SIGAA para sua valida��o.");
		
		mensagem.append(" \n\nO referido projeto envolve os seguintes docentes, t�cnicos administrativos, discentes e membros da comunidade externa em sua equipe: ");		
		for (MembroProjeto membro : projeto.getEquipe()) {
		    mensagem.append("\n");
		    mensagem.append("\nNome: " + membro.getPessoa().getNome());
		    mensagem.append("\nCategoria: " + membro.getCategoriaMembro().getDescricao());
		    mensagem.append("\nFun��o: " + membro.getFuncaoMembro().getDescricao());
		    mensagem.append("\nCarga Hor�ria: " + membro.getChDedicada() + " hora(s).");
		}
		
		mensagem.append("\n\nEsta mensagem foi enviada automaticamente atrav�s do Sistema de Gest�o de Atividades Acad�micas (SIGAA).");
		mensagem.append("\nPor favor, n�o respond�-la.");
		
		mail = new MailBody();
		mail.setContentType(MailBody.TEXT_PLAN);
		mail.setAssunto("SIGAA - Notifica��o de Envio de Relat�rio - MENSAGEM AUTOM�TICA");
		mail.setMensagem(mensagem.toString());
		mail.setEmail(responsavel.getServidor().getPessoa().getEmail());
		mail.setNome(nomeUsuario);
		Mail.send(mail);
	    }
	}
	
	/**
	 * M�todo utilizado para notifica��o de avaliadores de projetos.
	 * 
	 * @param projeto
	 * @param servidor
	 * @param texto Mensagem de notifica��o propriamente dita.
	 * @throws DAOException
	 */
	public static void notificarAvaliadorProjeto(Servidor servidor, String texto) throws DAOException {
	    mensagem = new StringBuffer(CAPACIDADE_INIT);
	    
	    String nomeUsuario = servidor.getPessoa().getNome();
	    mensagem.append(texto);
	    mensagem.append("\n\nEsta mensagem foi enviada automaticamente atrav�s do Sistema de Gest�o de Atividades Acad�micas (SIGAA).");
	    mensagem.append("\nPor favor, n�o respond�-la.");
	    
	    mail = new MailBody();
	    mail.setContentType(MailBody.TEXT_PLAN);
	    mail.setAssunto("[Mensagem Autom�tica] Notifica��o para Avalia��o de Projetos.");
	    mail.setMensagem(mensagem.toString());
	    mail.setEmail(servidor.getPrimeiroUsuario().getEmail());
	    mail.setNome(nomeUsuario);
	    Mail.send(mail);
	}
	 
	/**
	 * Notifica��o quanto aos email enviado quanto o edital � de a��es acad�micas Associadas.
	 */
	public static void notificaAvaliadoresAssociados( Projeto projeto, String tipoProjeto, Servidor servidor ) throws DAOException {
			MailBody mail = new MailBody();
		    mail.setContentType(MailBody.HTML);

		    // Definir remetente
		    mail.setFromName( servidor.getPessoa().getNome() );
		    mail.setEmail( servidor.getPrimeiroUsuario().getEmail() ) ;
		    mail.setAssunto("Notifica��o para an�lise de Projeto");
		    String mensagem = "Prezado(a) " + servidor.getPessoa().getNome() + ", informamos que o Projeto " + tipoProjeto + ", intitulado : <b>" + projeto.getTitulo() + "</b>" +  
		    				  " foi submetido para a sua avalia��o<br/><br/>";
		    mensagem +=	" Para visualizar o conte�do deste projeto e proceder sua avalia��o, utilize o link correspondente dispon�vel no Portal Docente ( " + RepositorioDadosInstitucionais.get("siglaSigaa") + " -> Portal Docente -> A��es Integradas -> Avaliar Proposta ) ou " +
		    		"<a href='"+ RepositorioDadosInstitucionais.getLinkSigaa() +"/sigaa/public/extensao/avaliacaoProjeto/"+servidor.getId()+".jsf'>Clique aqui</a> <br/><br/>";
		    
		    mail.setMensagem(mensagem);
		    Mail.send(mail);
	}
	
}