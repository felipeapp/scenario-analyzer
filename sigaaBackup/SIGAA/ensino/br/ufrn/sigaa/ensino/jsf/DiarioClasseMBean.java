/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 05/12/2006
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Managed Bean para geração do Diário de Classe
 * no portal da turma.
 *
 * @author David Ricardo
 *
 */
@Component("diarioClasse")
@Scope("request")
public class DiarioClasseMBean extends ControllerTurmaVirtual {

	/**
	 * Inicia a geração do diário de classe para a turma virtual que está sendo trabalhada atualmente.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/turma/menu_turma_old.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 * @throws JRException
	 * @throws IOException
	 */
	public void gerarDiarioClasse(ActionEvent evt) throws DAOException, JRException, IOException {
		try {
			Turma turma = getDAO(GenericSigaaDAO.class).findByPrimaryKey(turma().getId(), Turma.class);
			responderDiario(turma);
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
		}
	}

	/**
	 * Inicia a geração do diário de classe de acordo com a turma escolhida.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/turma/menu_turma.jsp</li>
	 * <li>/sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * <li>/sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * <li>/sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws JRException
	 * @throws IOException
	 */
	public String gerarDiarioClasse() throws DAOException, JRException, IOException{
		try {
			Turma turma = null;
			
			if (getParameterInt("id") == null) {
				turma = getDAO(GenericSigaaDAO.class).findByPrimaryKey(turma().getId(), Turma.class);
			} else {
				turma = getDAO(GenericSigaaDAO.class).findByPrimaryKey(getParameterInt("id"), Turma.class);
			}
			
			responderDiario(turma);
		} catch(NegocioException e) {
			addMensagens(e.getListaMensagens());
		} catch (ConfiguracaoAmbienteException e){
			addMensagemErro(e.getMessage());
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}

	/**
	 * Efetua a geração do diário de classe de acordo com a turma passada como parâmetro.
	 * 
	 * @param turma
	 * @throws Exception
	 * @throws IOException
	 */
	private void responderDiario(Turma turma) throws IOException, Exception {
		TurmaDao dao = getDAO(TurmaDao.class);
		FrequenciaAlunoDao freqDao = getDAO(FrequenciaAlunoDao.class);
		AvaliacaoDao avDao = getDAO(AvaliacaoDao.class);

		int unidade = 0;
		if (turma == null || turma.getDisciplina() == null) return;
		
		if (turma.getDisciplina().getNivel() == NivelEnsino.GRADUACAO)
			unidade = UnidadeGeral.UNIDADE_DIREITO_GLOBAL;
		else
			unidade = turma.getDisciplina().getUnidade().getId();

		boolean gerarTabelaFrequencia = true;
		
		ParametrosGestoraAcademica p = null;
        if (NivelEnsino.isAlgumNivelStricto(turma.getDisciplina().getNivel()))
            p = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalStricto();
        else 
            p = ParametrosGestoraAcademicaHelper.getParametros(turma, new br.ufrn.sigaa.dominio.Unidade(unidade));
		
		CalendarioAcademico calendario = null;
		
		try {
			calendario = CalendarioAcademicoHelper.getCalendario(turma, new br.ufrn.sigaa.dominio.Unidade(unidade));
		} catch (ConfiguracaoAmbienteException e){
			gerarTabelaFrequencia = false;
		}

		HttpServletResponse res = getCurrentResponse();
		try {
			new GerarDiarioClasse (turma, calendario, p, dao, freqDao, avDao, getDAO(UsuarioDao.class), gerarTabelaFrequencia).gerar(res.getOutputStream());
			res.setContentType("application/pdf");
			res.addHeader("Content-Disposition", "attachment; filename=diario_"+turma.getDisciplina().getCodigo()+"_"+turma.getAnoPeriodo()+"_"+turma.getCodigo()+"_.pdf");
			FacesContext.getCurrentInstance().responseComplete();
		} catch(NegocioException e) {
			throw e;
		}
	}

}
