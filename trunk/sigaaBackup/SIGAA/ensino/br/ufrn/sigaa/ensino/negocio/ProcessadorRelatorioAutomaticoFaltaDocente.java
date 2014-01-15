/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.DtoUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.integracao.dto.DestinatarioDTO;
import br.ufrn.integracao.dto.NotificacaoDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.EnvioNotificacoesRemoteService;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.AvisoFaltaDocenteDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.relatorios.dominio.RelatorioAutomaticoFaltaDocente;

/**
 * Processador invocado para gerar e enviar o relatório mensal de avisos de falta de docentes
 * para os chefes de departamento
 * 
 * @author Henrique André
 *
 */
public class ProcessadorRelatorioAutomaticoFaltaDocente extends AbstractProcessador {

	/** Nome do parâmetro que armazena a lista de e-mails das pessoas que devem ser notificadas com o resumo das faltas de docentes informadas pelos alunos. */
	public static final String EMAIL_RELATORIO_FALTA_DOCENTE = ParametroHelper.getInstance().getParametro(ConstantesParametro.EMAIL_RELATORIO_FALTA_DOCENTE); 
	
	/**
	 * Invocado pela arquitetura
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		
		CalendarioAcademico calAcademico = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		
		if (mov.getCodMovimento().equals(SigaaListaComando.ENVIO_AUTOMATICO_RELATORIO_FALTA_DOCENTE_SEMANAL)) {
			enviarDepartamentos(mov, calAcademico);
			enviarCentros(mov, calAcademico);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ENVIO_AUTOMATICO_RELATORIO_FALTA_DOCENTE_MENSAL)) {
			enviarLista(mov, calAcademico);
		}
		
		return null;
	}

	/**
	 * Envia o relatório para a lista parametrizada de emails
	 * 
	 * @param mov
	 * @param calAcademico
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private void enviarLista(Movimento mov, CalendarioAcademico calAcademico) throws NegocioException, ArqException, RemoteException {
		Collection<Unidade> centros = getDAO(UnidadeDao.class, mov).findByTipoUnidadeAcademica(TipoUnidadeAcademica.CENTRO);
		
		List<RelatorioAutomaticoFaltaDocente> faltasDept = new ArrayList<RelatorioAutomaticoFaltaDocente>();
		AvisoFaltaDocenteDao dao = getDAO(AvisoFaltaDocenteDao.class, mov);

		try {
			for (Unidade unidade : centros)
				faltasDept.addAll(dao.findByCentro(unidade.getId(), calAcademico.getAno(), calAcademico.getPeriodo()));
		} finally {
			dao.close();
		}
		
		if (isEmpty(faltasDept))
			return;
		
		StringBuilder pagina = contruirPagina(faltasDept);
		
		substituirParametros(pagina, "unidadeLabel", "Todos os Centros");
		substituirParametros(pagina, "unidadeValor", "");
		
		enviar(pagina, null, mov);
	}

	/**
	 * Envia Relatório aos Chefes de Centros
	 * 
	 * @param mov
	 * @param calAcademico 
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private void enviarCentros(Movimento mov, CalendarioAcademico calAcademico) throws NegocioException, ArqException, RemoteException {
		Collection<Unidade> centros = getDAO(UnidadeDao.class, mov).findByTipoUnidadeAcademica(TipoUnidadeAcademica.CENTRO);
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		AvisoFaltaDocenteDao dao = getDAO(AvisoFaltaDocenteDao.class, mov);
		
		try {
			for (Unidade unidade : centros) {
				
				List<UsuarioGeral> chefe = null;
				
				chefe = uDao.findUsuarioResponsavelAtualByUnidade(unidade.getId(), NivelResponsabilidade.CHEFE);
				if (isEmpty(chefe)) 
					continue;
	
				List<RelatorioAutomaticoFaltaDocente> faltasDept = dao.findByCentro(unidade.getId(), calAcademico.getAno(), calAcademico.getPeriodo());
				if (isEmpty(faltasDept))
					continue;
	
				StringBuilder pagina = contruirPagina(faltasDept);
				
				substituirParametros(pagina, "unidadeLabel", "Centro:");
				substituirParametros(pagina, "unidadeValor", faltasDept.iterator().next().getDepartamento().getGestora().getNome());
				
				enviar(pagina, chefe, mov);			
				
			}
		} finally {
			uDao.close();
			dao.close();
		}
		
	}

	/**
	 * Envia Relatório aos Chefes de Departamentos
	 * 
	 * @param mov
	 * @param calAcademico 
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void enviarDepartamentos(Movimento mov, CalendarioAcademico calAcademico) throws NegocioException, ArqException, RemoteException {
		Collection<Unidade> departamentos = getDAO(UnidadeDao.class, mov).findByTipoUnidadeAcademica(TipoUnidadeAcademica.DEPARTAMENTO);
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		AvisoFaltaDocenteDao dao = getDAO(AvisoFaltaDocenteDao.class, mov);
		
		try {
			for (Unidade unidade : departamentos) {
				List<UsuarioGeral> chefe = uDao.findUsuarioChefeDepartamento(unidade.getId());
				if (isEmpty(chefe)) 
					continue;
				
				List<RelatorioAutomaticoFaltaDocente> faltasDept = dao.findByDepartamento(unidade.getId(), calAcademico.getAno(), calAcademico.getPeriodo());
				if (isEmpty(faltasDept))
					continue;
				
				StringBuilder pagina = contruirPagina(faltasDept);
				
				substituirParametros(pagina, "unidadeLabel", "Departamento:");
				substituirParametros(pagina, "unidadeValor", unidade.getNome());
	
				
				enviar(pagina, chefe, mov);
			}
		} finally {
			uDao.close();
			dao.close();
		}
	}
	
	
	/**
	 * Substitui parâmetros do modelo de relatório
	 * 
	 * @param pagina
	 * @param parametro
	 * @param valor
	 */
	private void substituirParametros(StringBuilder pagina, String parametro, String valor) {
		String novaPagina = pagina.toString().replace("${" + parametro + "}", valor);
		pagina.setLength(0);
		pagina.append(novaPagina);
	}

	/**
	 * Envia o relatório para o chefe
	 * 
	 * @param paginaConstuida
	 * @param chefe
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void enviar(StringBuilder paginaConstuida, List<UsuarioGeral> chefe, Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		List<DestinatarioDTO> destinatarios = null;
		
		if (!isEmpty(chefe))
			destinatarios = criarDestinatarios(chefe);
		else
			destinatarios = criarDestinatarios(EMAIL_RELATORIO_FALTA_DOCENTE);
		
		if (isEmpty(destinatarios))
			return;
		
		NotificacaoDTO not = criarNotificacao(paginaConstuida, destinatarios);
		
		try {
			EnvioNotificacoesRemoteService enviador = getBean("envioNotificacoesInvoker", mov);
			enviador.enviar(DtoUtils.deUsuarioParaDTO(mov.getUsuarioLogado()), not);
		} catch (NegocioRemotoException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	/**
	 * Cria os destinatários com base na lista parametrizada
	 * 
	 * @param emailRelatorioFaltaDocente
	 * @return
	 */
	private List<DestinatarioDTO> criarDestinatarios(String emailRelatorioFaltaDocente) {

		String[] emails = emailRelatorioFaltaDocente.split(";");
		List<DestinatarioDTO> resultado = new ArrayList<DestinatarioDTO>();
		
		for (String email : emails) {
			
			DestinatarioDTO dest = new DestinatarioDTO();
			dest.setEmail(email);
			resultado.add(dest);
		}
			
		return resultado;
	}

	/**
	 * Cria uma notificação para ser enviada
	 * 
	 * @param paginaConstuida
	 * @param destinatarios
	 * @return
	 */
	private NotificacaoDTO criarNotificacao(StringBuilder paginaConstuida, List<DestinatarioDTO> destinatarios) {
		NotificacaoDTO not = new NotificacaoDTO();
		not.setEnviarEmail(true);
		not.setEnviarMensagem(false);
		not.setDestinatarios(destinatarios);
		not.setMensagem(paginaConstuida.toString());
		not.setTitulo("Relatório de Aviso de Faltas de Docentes");
		not.setAutorizado(true);
		not.setContentType(MailBody.HTML);
		not.setNomeRemetente("Sistemas/UFRN");
		return not;
	}

	/**
	 * Cria os destinatários da mensagem
	 * 
	 * @param chefe
	 * @return
	 * @throws DAOException
	 */
	private List<DestinatarioDTO> criarDestinatarios(List<UsuarioGeral> chefe) throws DAOException {
		
		List<DestinatarioDTO> resultado = new ArrayList<DestinatarioDTO>();
		
		for (UsuarioGeral usuarioGeral : chefe) {
			if (usuarioGeral == null || isEmpty(usuarioGeral.getEmail()))
				return null;
			
			DestinatarioDTO dest = new DestinatarioDTO();
			dest.setEmail(usuarioGeral.getEmail());
			resultado.add(dest);
		}
		
		
		return resultado;
	}

	/**
	 * Monta a página que será enviada como relatório
	 * 
	 * @param faltasDept
	 * @return
	 */
	private StringBuilder contruirPagina(List<RelatorioAutomaticoFaltaDocente> faltasDept) {
		StringBuilder pagina = new StringBuilder();
		pagina.append(CABECALHO);
		pagina.append(CORPO_INICIO);
		pagina.append(contruirCorpo(faltasDept));
		pagina.append(CORPO_FIM);
		pagina.append(RODAPE);
		return pagina;
	}

	/**
	 * Constrói o corpo, a parte dinâmica da mensagem
	 * 
	 * @param faltasDept
	 * @return
	 */
	private String contruirCorpo(List<RelatorioAutomaticoFaltaDocente> faltasDept) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		StringBuilder corpo = new StringBuilder();
		
		Unidade departamentoAtual = new Unidade();
		Unidade centroAtual = new Unidade();
		for (RelatorioAutomaticoFaltaDocente rel : faltasDept) {
			
			// Quando gestora for diferente de null, é porque a busca é pelo centro
			// na qual imprime o nome de cada departamento
			if (rel.getDepartamento().getGestora() != null && !rel.getDepartamento().equals(departamentoAtual)) {
				
				if (!rel.getDepartamento().getGestora().equals(centroAtual)) {
					corpo.append("\n<tr>");
					corpo.append("\n<td colspan=\"3\">&nbsp</td>");
					corpo.append("\n</tr>");
					corpo.append("\n<tr>");
					corpo.append("\n<td colspan=\"3\"><h3>" + rel.getDepartamento().getGestora().getNome()  + "</h3></td>");
					corpo.append("\n</tr>");
					centroAtual = rel.getDepartamento().getGestora();
				}
				corpo.append("\n<tr>");
				corpo.append("\n<td colspan=\"3\">&nbsp</td>");
				corpo.append("\n</tr>");
				corpo.append("\n<tr>");
				corpo.append("\n<td colspan=\"3\"><h4>" + rel.getDepartamento().getNome()  + "</h4></td>");
				corpo.append("\n</tr>");
				departamentoAtual = rel.getDepartamento();
			}
			
			corpo.append("\n<tr>");
			corpo.append("\n<td>" + rel.getPessoa().getNome()  + "</td>");
			corpo.append("\n<td align=\"center\">" + df.format(rel.getDataAula())  + "</td>");
			corpo.append("\n<td align=\"center\">" + rel.getNumeroFaltas()  + "</td>");
			corpo.append("\n</tr>");
		}
		return corpo.toString();
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

	//Constantes da página a ser gerada
	/** Cabeçalho da página */
	private static final String CABECALHO = 
		"\n<html>" +
			"\n<head>" +
				"\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
				"\n<title>Avisos de Falta</title>" +
			"\n</head>" +
			"\n<body>";
	
	/** Início do corpo da página. */
	private static final String CORPO_INICIO = "" +
			"\n<h2>${unidadeLabel} ${unidadeValor}</h2>" +
			"\n<table>" +
				"\n<caption>Avisos de Falta</caption>" +
				"\n<thead>" +
					"\n<tr>" +
						"\n<th>Docente</th>" +
						"\n<th>Data da Aula</th>" +
						"\n<th>Número de Avisos</th>" +
					"\n</tr>" +
				"\n</thead>" +
				"\n<tbody>"; 
	
	/** Fim do corpo da página. */
	private static final String CORPO_FIM = "\n</tbody>";
	
	/** Rodapé da página. */
	private static final String RODAPE = "\n</body>\n</html>";
	
}
