/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 26/12/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.timer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.dao.DisseminacaoDaInformacaoDao;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.ConfiguraPerfilInteresseUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 * <p>Timer que notifica os usuários que cadastraram interesse em receber notificações sobre materiais 
 * de um determinado assunto ou autor na biblioteca. Parte final do caso de uso de DSI.
 * </p>
 *
 * <p> <i> Essa classe é executada paralelamente sempre que um novo material é incluído no acervo, deve buscar os autores e assuntos a partir
 * dos Títulos dos materiais, achar as autoridades desses autores e assuntos e notificar os usuários que cadastraram interesse nessas autoridades.
 * </i> 
 * </p>
 * 
 * <p>
 * <strong>Observação: </strong> inserir um registro na tabela INFRA.REGISTRO_TIMER (Banco SISTEMAS_COMUM). Vai possuir 2 parâmetros: 
 *  horaExecucao: 12h 
 *  tipoReplicacao: S = Semanal 
 *  diaExecucao: DOM = 1
 *</p>
 * 
 * @author jadson
 * @see ConfiguraPerfilInteresseUsuarioBibliotecaMBean
 */
public class NotificaUsuariosInteressadosDSITimer extends TarefaTimer{
	
	/**
	 * Construtor
	 */
	public NotificaUsuariosInteressadosDSITimer(){
		
	}

	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
			MaterialInformacionalDao daoMaterial = null;
			
			DisseminacaoDaInformacaoDao daoDisseminacao = null;
			
			final int diasDeRetardoAConsiderar = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.DIAS_RETARDO_MATERIAL_DISPONIVEL_ACERVO);
			
			Date hoje =  new Date();
			Date inicioSemana = CalendarUtils.adicionaDias(hoje, -6);
			
			if(diasDeRetardoAConsiderar > 0){
				hoje = CalendarUtils.adicionaDias(hoje, -diasDeRetardoAConsiderar);
				inicioSemana = CalendarUtils.adicionaDias(inicioSemana, -diasDeRetardoAConsiderar);
			}
			
			long tempoInicial = System.currentTimeMillis();
			
			try{
			
				daoMaterial = DAOFactory.getInstance().getDAO(MaterialInformacionalDao.class);
				daoDisseminacao = DAOFactory.getInstance().getDAO(DisseminacaoDaInformacaoDao.class);
				
				/** A lista de informações montadas que serão enviadas por email para o usuário */
				List<InformacaoNotificacaoUsuarioInteressadosDSI> informacoesNotificacoes = new ArrayList<InformacaoNotificacaoUsuarioInteressadosDSI>();
				
				/** Guarda em um map as referências dos títulos para não precisar gerar novamente */
				Map<Integer, String> cacheReferencias = new HashMap<Integer, String>();
				
				
				
				// Retorno os Títulos cujos materiais foram incluídos no acervo na última semana. !!!!  // 
				List<Integer> idsNovosMateriais = daoDisseminacao.findIdsMateriaisIncluidosAcervoEntreDatas(inicioSemana, hoje);
				
				
				// Para cada material incluído no acervo no perído entre as execuções do timer //
				for (Integer idMaterial : idsNovosMateriais) {
					
					Integer idTitulo = daoMaterial.findIdTituloMaterial(idMaterial);
					Biblioteca bibliotecaDoMaterial = daoMaterial.findBibliotecaDoMaterial(idMaterial);
					
					String formatoReferencia = "";
					
					if(cacheReferencias.containsKey(idTitulo))
						formatoReferencia = cacheReferencias.get(idTitulo);
					else{
						formatoReferencia = new FormatosBibliograficosUtil().gerarFormatoReferencia(new TituloCatalografico(idTitulo), true);
						cacheReferencias.put(idTitulo, formatoReferencia);
					}
					
					/**
					 * Usuários interessados encontrados utilizando a referência que o Título guarda para as autoridades utilizadas no momento da catalogação. 
					 * 
					 *  Object[0] : o id do usuário biblioteca                   <br/>  
					 *  Object[1] : o nome do usuário                            <br/>  
					 *  Object[2] : o email do Usuário                           <br/>  
					 *  Object[3] : o email da pessoa                            <br/>  
			 		 *  Object[4] : se é informação de assunto                   <br/> 
					 *  Object[5] : o assunto ou autor de interesse                <br/>  
					 *  Object[6] : a biblioteca de interesse do perfil do usuário <br/>  
					 */
					List<Object[]> inforUsuarioReferencia = daoDisseminacao.findUsuariosInteresseTituloEBibliotecaByReferencia(idTitulo, bibliotecaDoMaterial.getId());
				
					// Para cada usuário retornado como tendo interesse
					for (Object[] info : inforUsuarioReferencia) {
						InformacaoNotificacaoUsuarioInteressadosDSI infoTemp = new InformacaoNotificacaoUsuarioInteressadosDSI( (Integer) info[0]);
					
						if(informacoesNotificacoes.contains(infoTemp)){
							infoTemp = informacoesNotificacoes.get(informacoesNotificacoes.indexOf(infoTemp));
						}else{
							infoTemp.nomeUsuario = (String) info[1];
							infoTemp.email = ( info[2] != null ?  (String) info[2] :  (String) info[3] );
							informacoesNotificacoes.add(infoTemp);
						}
						boolean assunto = (Boolean) info[4];
						infoTemp.adicionaDadosInterresseUsuario(new DadosInteresseUsuario(formatoReferencia),   (assunto ? "Assunto: ": "Autor: ")+(String) info[5],    bibliotecaDoMaterial.getDescricao()  );
					
					}
					
					
					/**
					 * Usuários interessados encontrados utilizando uma busca textual simples nos campos que deveriam ser completados com informações de autoridades.
					 * 
					 *  Object[0] : o id do usuário biblioteca                   <br/>  
					 *  Object[1] : o nome do usuário                            <br/>  
					 *  Object[2] : o email do Usuário                           <br/>  
					 *  Object[3] : o email da pessoa                            <br/>  
			 		 *  Object[4] : se é informação de assunto                   <br/> 
					 *  Object[5] : o assunto ou autor de interesse               <br/>  
					 *  Object[6] : a biblioteca de interesse do perfil do usuário <br/>  
					 */
					List<Object[]> inforUsuarioTexto = daoDisseminacao.findUsuariosInteresseTituloEBibliotecaByTexto(idTitulo, bibliotecaDoMaterial.getId()); 
					
					
					// Para cada usuário retornado como tendo interesse
					for (Object[] info : inforUsuarioTexto) {
						InformacaoNotificacaoUsuarioInteressadosDSI infoTemp = new InformacaoNotificacaoUsuarioInteressadosDSI( (Integer) info[0]);
					
						if(informacoesNotificacoes.contains(infoTemp)){
							infoTemp = informacoesNotificacoes.get(informacoesNotificacoes.indexOf(infoTemp));
						}else{
							infoTemp.nomeUsuario = (String) info[1];
							infoTemp.email = ( info[2] != null ?  (String) info[2] :  (String) info[3] );
							informacoesNotificacoes.add(infoTemp);
						}
						
						boolean assunto = (Boolean) info[4];
						infoTemp.adicionaDadosInterresseUsuario(new DadosInteresseUsuario(formatoReferencia),   (assunto ? "Assunto: ": "Autor: ")+(String) info[5],   bibliotecaDoMaterial.getDescricao()  );
					
					}
					
				}
				
				
				// Envia emails para todos usuários da lista //
				enviaEmailNotificacaoUsuarios( informacoesNotificacoes );
				
				enviaEmailNotificacaoAdministradorSistema(informacoesNotificacoes.size(), ( ( System.currentTimeMillis()-tempoInicial)/1000 )   );
				
			} catch (Exception ex) {
				enviaEmailErroAdministradorSistema(ex);
			}finally{
				if(daoMaterial != null) daoMaterial.close();
				if(daoDisseminacao != null) daoDisseminacao.close();
				
			}
		
	}
	
	
	



	/**
	 * Método que envia o email de notificação ao usuário e encerra esse caso de uso.
	 * 
	 * @param nomeUsuario
	 * @param email
	 */
	private void enviaEmailNotificacaoUsuarios(List<InformacaoNotificacaoUsuarioInteressadosDSI> informacoesNotificacoes ) {
		
		EnvioEmailBiblioteca sender = new EnvioEmailBiblioteca();
		
		final String assuntoEmail = " Notificação Novos Materiais Incluídos no Acervo da Biblioteca ";
		final String tituloEmail = " Novos Materiais Incluídos no Acervo da Biblioteca ";
		final String mensagemAlertaRodape = "Você optou por receber este e-mail. Se não desejar mais recebê-lo, atualize o seu Perfil de Interesse na Biblioteca ";
		
		for (InformacaoNotificacaoUsuarioInteressadosDSI informacao : informacoesNotificacoes) {
			
			StringBuilder mensagemNivel1 = new StringBuilder();
			
			
			mensagemNivel1.append(" <p style=\"font-weight: bold;\"> Novos materiais para os Títulos abaixo foram incluídos no acervo : </p> ");
			
			mensagemNivel1.append(" <p> ");
			
			int contador = 1;
			
			for (DadosInteresseUsuario dados : informacao.dadosInteresseUsuario) {
				mensagemNivel1.append("<hr>");
				mensagemNivel1.append(contador+" - "+dados.formatoReferenciaTitulo+" <br/><br/> ");
				
				mensagemNivel1.append(" de acordo com seu perfil de interesse cadastrado no sistema: <br/> ");
				
				for(String interesse : dados.informacoesInteresse){
					mensagemNivel1.append(" <br/> "+"<span style=\"font-style:italic;\">"+interesse+"</span>") ;
				}
				for(String bibliotec : dados.descricoesBibliotecas){
					mensagemNivel1.append(" <br/> "+"<span style=\"font-style:italic;\">"+"Biblioteca: "+bibliotec+"</span>") ;
				}
				
				mensagemNivel1.append(" <br/><br/>");
				mensagemNivel1.append("<hr>");
				mensagemNivel1.append(" <br/><br/>");
				
				contador++;
			}
			mensagemNivel1.append(" </p> ");
			
			sender.enviaEmail( 
					informacao.nomeUsuario, informacao.email
					, assuntoEmail, tituloEmail
					, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA
					, null
					, mensagemNivel1.toString()
					, null
					, null
					, null, null
					, null
					, null
					, mensagemAlertaRodape);
			
		} // para cada usuário de interesse
	
		
	}
	
	
	/**
	 * Envia um email com a informação do erro na execução da rotina para os administradores do sistema.
	 *
	 * @param siglaSigaa
	 * @param assuntoEmail
	 * @param e
	 */
	private void enviaEmailNotificacaoAdministradorSistema(int quantidadeEmailsEnviados, long tempoExecucao){
		
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "ENVIO DE NOTIFICAÇÕES PARA OS USUÁRIOS QUE REGISTRARAM INTERESSE - EXECUTADO EM " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
		
		String mensagem = " Classe: "+this.getClass().getName()+"<br/>"
		    +" Server: " + NetworkUtils.getLocalName()+"<br/>" 
			+ "<br>  Esse email é enviado semanalmente para os usuários que registraram interesse em assunto ou autores do acervo da biblioteca."
			+ "<br>  Quantidade de usuários que receberam notificação do sistema : " + quantidadeEmailsEnviados
			+ "<br>  Tempo de execução: " + tempoExecucao+" minutos."+"<br/>";
		
		MailBody mail = new MailBody();
		mail.setEmail(email);
		mail.setAssunto(assunto);
		mail.setMensagem(mensagem);
		Mail.send(mail);
	}
	
	
	
	/**
	 * Envia um email com a informação do erro na execução da rotina para os administradores do sistema.
	 *
	 * @param siglaSigaa
	 * @param assuntoEmail
	 * @param e
	 */
	private void enviaEmailErroAdministradorSistema(Exception ex){
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "["+RepositorioDadosInstitucionais.get("siglaSigaa")+"]"+" Erro ao executar o envio de notificação para os usuários que registraram interesse ";
		String mensagem = " Classe: "+this.getClass().getName()+"<br/>"
		+" Server: " + NetworkUtils.getLocalName() + "<br>" +
		" <br/>  Esse email é enviado semanalmente para os usuários que registraram interesse em assunto ou autores do acervo da biblioteca. <br/><br/> "+
		ex.getMessage() + "<br><br><br>" + Arrays.toString(ex.getStackTrace()).replace(",", "\n") +
		(ex.getCause() != null ? Arrays.toString(ex.getCause().getStackTrace()).replace(",", "\n") : "");
		
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem );
		Mail.send(mail);
	}
	
	
	
	/** Garda a lista de informações a serem enviadas para o email do usuário que cadastrou interesse em algum assunto ou autor no sistema*/
	private class InformacaoNotificacaoUsuarioInteressadosDSI{
		
		/** Identifica o usuário para não deixar repetir  */
		public int idUsuarioBiblioteca;
		
		/** O nome do usuário impresso no email */
		public String nomeUsuario;
		/** O email do usuário para onde será enviado o aviso */
		public String email;
	
		/** Os dados de interesse do usuário */
		public List<DadosInteresseUsuario> dadosInteresseUsuario;

		
		public InformacaoNotificacaoUsuarioInteressadosDSI(int idUsuarioBiblioteca) {
			this.idUsuarioBiblioteca = idUsuarioBiblioteca;
		}
		
		
		/** Adiciona um novo dados de interrese para o usuário já existente, todos esses dados serão enviados em um mesmo email */
		public void adicionaDadosInterresseUsuario(DadosInteresseUsuario dados, String informacaoInteresse, String descricaoBiblioteca){
			if(dadosInteresseUsuario == null)
				dadosInteresseUsuario = new ArrayList<DadosInteresseUsuario>();
			
			if(dadosInteresseUsuario.contains(dados)){
				DadosInteresseUsuario dadosTemp = dadosInteresseUsuario.get(dadosInteresseUsuario.indexOf(dados));
				dadosTemp.adicionaInformacaEBibliotecaInteresse(informacaoInteresse, descricaoBiblioteca);
			}else{
				dados.adicionaInformacaEBibliotecaInteresse(informacaoInteresse, descricaoBiblioteca);
				dadosInteresseUsuario.add(dados);
			}
			
		}

		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + idUsuarioBiblioteca;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			InformacaoNotificacaoUsuarioInteressadosDSI other = (InformacaoNotificacaoUsuarioInteressadosDSI) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (idUsuarioBiblioteca != other.idUsuarioBiblioteca)
				return false;
			return true;
		}

		private NotificaUsuariosInteressadosDSITimer getOuterType() {
			return NotificaUsuariosInteressadosDSITimer.this;
		}
		
		
		
	}
	
	/** Agrupa os dados de interesse do usuário para enviar no máximo 1 email para cada usuário por semana, 
	 * não importa a quantidade de materiais incluídos no acervo. 
	 */
	private class DadosInteresseUsuario{
		/** O título em formato de referência */
		public String formatoReferenciaTitulo;
		/** A lista de Assuntos ou Autorre de interesse do usuário 
		 * Pode existir mais de 1, por exemplo, para títulos de assuntos e autores de interesse do usuário */
		public Set<String> informacoesInteresse;
		/** A descrição das bibliotecas onde o usuário registrou intresse. 
		 * Pode existir mais de 1, por exemplo, para títulos de assuntos e autores de interesse do usuário */
		public Set<String> descricoesBibliotecas;
		
		/** Construtor para identificar os dados de Interesse */
		public DadosInteresseUsuario(String formatoReferenciaTitulo) {
			this.formatoReferenciaTitulo = formatoReferenciaTitulo;
		}

		public void adicionaInformacaEBibliotecaInteresse(String informacaoInteresse, String descricaoBiblioteca) {
			if(this.informacoesInteresse == null)
				this.informacoesInteresse = new HashSet<String>();
			
			this.informacoesInteresse.add(informacaoInteresse);
			
			if(this.descricoesBibliotecas == null)
				this.descricoesBibliotecas = new HashSet<String>();
			
			this.descricoesBibliotecas.add(descricaoBiblioteca);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime
					* result
					+ ((formatoReferenciaTitulo == null) ? 0
							: formatoReferenciaTitulo.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DadosInteresseUsuario other = (DadosInteresseUsuario) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (formatoReferenciaTitulo == null) {
				if (other.formatoReferenciaTitulo != null)
					return false;
			} else if (!formatoReferenciaTitulo
					.equals(other.formatoReferenciaTitulo))
				return false;
			return true;
		}

		private NotificaUsuariosInteressadosDSITimer getOuterType() {
			return NotificaUsuariosInteressadosDSITimer.this;
		}
		
		
	}
	
}
