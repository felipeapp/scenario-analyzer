/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '29/12/2006'
 *
 */
package br.ufrn.sigaa.negocio;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.EventoExtraSistema;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;

/**
 * Executa atualiza��o do calend�rio acad�mico.
 * Motivo: tratamento da lista de eventos extras
 * @author amdantas
 * @author Victor Hugo
 */
public class ProcessadorCalendarioAcademico extends ProcessadorCadastro {


	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		// verificar se o calend�rio for de gradua��o se o usu�rio que est� autorizando � administrador DAE

		CalendarioMov cMov = (CalendarioMov) mov;
		String mensagem = "Voc� n�o tem permiss�o para alterar este calend�rio";

		CalendarioAcademico calendario = ( CalendarioAcademico) cMov.getObjMovimentado();
		if ( calendario.getNivel() == NivelEnsino.GRADUACAO && !calendario.isADistancia() ) {
			if ( !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE)) {
				throw new SegurancaException(mensagem);
			}
		}
		if ( calendario.getNivel() == NivelEnsino.GRADUACAO && calendario.isADistancia() ) {
			if ( !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.SEDIS)) {
				throw new SegurancaException(mensagem);
			}
		}
		
		// altera��o do calend�rio geral da p�s
		if ( calendario.getNivel() == NivelEnsino.STRICTO && calendario.getUnidade().getId() == Unidade.UNIDADE_DIREITO_GLOBAL ) {
			if ( !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.PPG) ) {
				throw new SegurancaException(mensagem);
			}
		}
		// altera��o do calend�rio do programa
		if ( calendario.getNivel() == NivelEnsino.STRICTO && calendario.getUnidade().getId() != Unidade.UNIDADE_DIREITO_GLOBAL ) {
			
			if ( !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG ) ) {
				throw new SegurancaException(mensagem);
			} else if( mov.getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO ) ){
				CoordenacaoCursoDao coordCursoDao = getDAO( CoordenacaoCursoDao.class , mov);
				try{
					//CoordenacaoCurso coordenacaoPos = coordCursoDao.findCoordPosByServidor(((Usuario) mov.getUsuarioLogado() ).getServidorAtivo().getId(), true, null);
					ArrayList<CoordenacaoCurso> coordenacoesPos = (ArrayList<CoordenacaoCurso>) coordCursoDao.findCoordPosByServidor(((Usuario) mov.getUsuarioLogado() ).getServidorAtivo().getId(), true, null);
					boolean ehCoordenador = false;
					for( CoordenacaoCurso cc : coordenacoesPos ){
						if ( cc.getUnidade().getId() == calendario.getUnidade().getId() ) {
							ehCoordenador = true;
							break;
						}
					}
					if ( !ehCoordenador ) {
						throw new SegurancaException(mensagem);
					}
				} finally {
					if (coordCursoDao != null)
						coordCursoDao.close();
				}				
				
			} else if( mov.getUsuarioLogado().isUserInRole(SigaaPapeis.SECRETARIA_POS ) && !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.PPG ) ){
				SecretariaUnidadeDao secDao = getDAO(SecretariaUnidadeDao.class, mov);
				try {
					Collection<SecretariaUnidade> secretarias = secDao.findByUsuarioTipoAcademico(mov.getUsuarioLogado().getId(), TipoUnidadeAcademica.PROGRAMA_POS);
					boolean ehSecretaria = false;
					for( SecretariaUnidade su : secretarias ){
						if( calendario.getUnidade().getId() == su.getUnidade().getId() )
							ehSecretaria = true;
					}
					if( !ehSecretaria )
						throw new SegurancaException(mensagem);
				} finally {
					if (secDao != null)
						secDao.close();
				}
			}

		}

		/*
		 * verificando duplicidade de calend�rio
		 */
		if( cMov.getCodMovimento().equals( SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO ) ){
			CalendarioAcademico calendarioExistente = CalendarioAcademicoHelper.getCalendarioExato( calendario.getAno(), calendario.getPeriodo(), calendario.getUnidade(), calendario.getNivel() ,calendario.getModalidade(), calendario.getConvenio(), calendario.getCurso(), null );
			Unidade unidade = calendario.getUnidade();

			if( calendarioExistente != null && calendarioExistente.getUnidade().getId() == unidade.getId() )
				throw new NegocioException("J� existe um calend�rio cadastrado para esta unidade no ano/per�odo selecionado.");
		}

	}


	/**
	 * Atualiza calend�rio , removendo separadamente os eventos extras
	 */	
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		CalendarioMov cMov = (CalendarioMov) movimento;
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class, cMov);
		try {
			validate(movimento);
	
			CalendarioAcademico cal = (CalendarioAcademico) cMov.getObjMovimentado();
			if (cal.isVigente()) {
				// tratando vig�ncia dos calend�rios
				CalendarioAcademico atualVigente =  dao.findByParametros(null, null, cal.getUnidade(),
						cal.getNivel(), cal.getModalidade(), cal.getConvenio(), cal.getCurso(), null);
				if ( atualVigente != null &&  atualVigente.getId() != cal.getId())
					dao.updateField(CalendarioAcademico.class, atualVigente.getId(), "vigente", false);
				else
					dao.detach(atualVigente);
			}
	
			// tratar eventos removidos
			if (cMov.getExtrasRemovidos() != null) {
				for (EventoExtraSistema extra : cMov.getExtrasRemovidos()) {
					extra.setCalendario(null);
					dao.remove(extra);
				}
			}
	
			if (cal.getEventosExtra() != null && cal.getEventosExtra().size() == 0)
				cal.setEventosExtra(null);
	
			// update
			if( cMov.getCodMovimento().equals( SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO ) )
				dao.create(cal);
			else if( cMov.getCodMovimento().equals( SigaaListaComando.ALTERAR_CALENDARIO_ACADEMICO ) )
				dao.update(cal);
			else
				throw new NegocioException("Opera��o n�o suportada.");
			
			// Notifica apenas a Pr�-Reitoria de P�s Gradua��o
			notificar(cal);
			
			return cal;
			
		} finally {
			if (dao != null)
				dao.close();
		}			
	}
	
	/**
	 * Conte�do da Notifica��o.
	 * @param cal
	 * @return
	 */
	private String getMensagemNotificacao(CalendarioAcademico cal){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		StringBuilder sb = new StringBuilder();
		sb.append("O Calend�rio do Programa de "+cal.getUnidade().getNome()+" foi alterado.<br/>");
		sb.append("<table>");
		sb.append(" <tr>");
		sb.append("  <td colspan='2' style='text-align:center;'>");
		sb.append("   <b>DADOS DO CALEND�RIO ALTERADO</b>");
		sb.append("  </td>");
		sb.append(" </tr>");
		
		sb.append(" <tr>");
		sb.append("  <td>");
		sb.append("   <b>Ano - Per�odo:</b>");
		sb.append("  </td>");
		sb.append("  <td>");
		sb.append(cal.getAno() + "." + cal.getPeriodo() + (cal.isVigente() ? " (Vigente)" : ""));
		sb.append("  </td>");		
		sb.append(" </tr>");
		
		if (cal.getInicioPeriodoLetivo() != null && cal.getFimPeriodoLetivo() != null){
			sb.append(" <tr>");
			sb.append("  <td>");
			sb.append("   <b>Per�odo Letivo:</b>");
			sb.append("  </td>");
			sb.append("  <td>");
			sb.append(df.format(cal.getInicioPeriodoLetivo()) +" at� "+ df.format(cal.getFimPeriodoLetivo()));
			sb.append("  </td>");		
			sb.append(" </tr>");			
		}
		
		if (cal.getInicioConsolidacaoTurma() != null && cal.getFimConsolidacaoTurma() != null){
			sb.append(" <tr>");
			sb.append("  <td>");
			sb.append("   <b>Consolida��o de Turmas:</b>");
			sb.append("  </td>");
			sb.append("  <td>");
			sb.append(df.format(cal.getInicioConsolidacaoTurma()) +" at� "+ df.format(cal.getFimConsolidacaoTurma()));
			sb.append("  </td>");		
			sb.append(" </tr>");
		}
		
		if (cal.getInicioConsolidacaoParcialTurma() != null && cal.getFimConsolidacaoParcialTurma() != null){
			sb.append(" <tr>");
			sb.append("  <td>");
			sb.append("   <b>Consolida��o Parcial de Turmas:</b>");
			sb.append("  </td>");
			sb.append("  <td>");
			sb.append(df.format(cal.getInicioConsolidacaoParcialTurma()) +" at� "+ df.format(cal.getFimConsolidacaoParcialTurma()));
			sb.append("  </td>");		
			sb.append(" </tr>");	
		}
		
		if (cal.getInicioTrancamentoTurma() != null && cal.getFimTrancamentoTurma() != null){
			sb.append(" <tr>");
			sb.append("  <td>");
			sb.append("   <b>Trancamento de Turmas:</b>");
			sb.append("  </td>");
			sb.append("  <td>");
			sb.append(df.format(cal.getInicioTrancamentoTurma()) +" at� "+ df.format(cal.getFimTrancamentoTurma()));
			sb.append("  </td>");		
			sb.append(" </tr>");	
		}
		
		if (cal.getInicioMatriculaOnline() != null && cal.getFimMatriculaOnline() != null){
			sb.append(" <tr>");
			sb.append("  <td>");
			sb.append("   <b>Matr�cula OnLine:</b>");
			sb.append("  </td>");
			sb.append("  <td>");
			sb.append(df.format(cal.getInicioMatriculaOnline()) +" at� "+ df.format(cal.getFimMatriculaOnline()));
			sb.append("  </td>");		
			sb.append(" </tr>");
		}
		
		if (cal.getInicioCoordenacaoAnaliseMatricula() != null && cal.getFimCoordenacaoAnaliseMatricula() != null){
			sb.append(" <tr>");
			sb.append("  <td>");
			sb.append("   <b>An�lise dos Coordenadores/Orientadores da Matr�cula:</b>");
			sb.append("  </td>");
			sb.append("  <td>");
			sb.append(df.format(cal.getInicioCoordenacaoAnaliseMatricula()) +" at� "+ df.format(cal.getFimCoordenacaoAnaliseMatricula()));
			sb.append("  </td>");		
			sb.append(" </tr>");
		}
		
		if (cal.getInicioReMatricula() != null && cal.getFimReMatricula() != null){
			sb.append(" <tr>");
			sb.append("  <td>");
			sb.append("   <b>Re-Matr�cula:</b>");
			sb.append("  </td>");
			sb.append("  <td>");
			sb.append(df.format(cal.getInicioReMatricula()) +" at� "+ df.format(cal.getFimReMatricula()));
			sb.append("  </td>");		
			sb.append(" </tr>");	
		}
		
		if (cal.getInicioCoordenacaoAnaliseReMatricula() != null && cal.getFimCoordenacaoAnaliseReMatricula() != null){
			sb.append(" <tr>");
			sb.append("  <td>");
			sb.append("   <b>An�lise dos Coordenadores/Orientadores para Re-Matr�cula:</b>");
			sb.append("  </td>");
			sb.append("  <td>");
			sb.append(df.format(cal.getInicioCoordenacaoAnaliseReMatricula()) +" at� "+ df.format(cal.getFimCoordenacaoAnaliseReMatricula()));
			sb.append("  </td>");		
			sb.append(" </tr>");	
		}
		sb.append("</table>");
		return sb.toString();
	}

	/**
	 * Notifica os Usu�rios que foi alterado o Calend�rio Acad�mico.
	 * @param texto
	 * @throws RemoteException  
	 * @throws NegocioException 
	 */
	private void notificar(CalendarioAcademico cal) throws ArqException, NegocioException, RemoteException {
		if (cal.getNivel() == NivelEnsino.STRICTO){
			
			String emails = ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.EMAILS_RESPONSAVEIS_PRO_REITORIA_POS);
			String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");
			
			if (!ValidatorUtil.isEmpty(emails)){
				
				String[] lista = emails.split(";");
				
				for (int i = 0; i < lista.length; i++){
					// enviando e-mail.
					MailBody email = new MailBody();
					email.setAssunto("["+siglaSigaa+"] - Altera��o no Calend�rio do Programa de "+cal.getUnidade().getNome());
					email.setContentType(MailBody.HTML);
					email.setEmail(lista[i]);
					email.setMensagem(getMensagemNotificacao(cal));
					Mail.send(email);												
				}
			}			
		}
	}		
}
