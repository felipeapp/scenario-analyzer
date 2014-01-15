/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 30/08/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Managed Bean para geração do Diário de Classe do Ensino Médio.
 *
 * @author Arlindo Rodrigues
 *
 */
@Component("diarioClasseMedio") @Scope("request")
public class DiarioClasseMedioMBean extends ControllerTurmaVirtual {
	
	
	/**
	 * Inicia a geração do diário de classe para a turma virtual que está sendo trabalhada atualmente.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	  <li>/sigaa.war/medio/turmaSerie/lista.jsp</li>
	 * </ul>
	 * @throws DAOException
	 * @throws JRException
	 * @throws IOException
	 */
	public String gerarDiarioClasse() throws DAOException, JRException, IOException {
		try {
			Turma turma = null;
			
			if (getParameterInt("id") == null) {
				turma = getDAO(GenericSigaaDAO.class).findByPrimaryKey(turma().getId(), Turma.class);
			} else {
				turma = getDAO(GenericSigaaDAO.class).findByPrimaryKey(getParameterInt("id"), Turma.class);
			}
			
			gerarDiario(turma);
			
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
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
	private void gerarDiario(Turma turma) throws IOException, Exception {

		if (turma == null || turma.getDisciplina() == null) return;

		CalendarioAcademico calendario = null;
		
		Curso c = turma.getDisciplina().getCurso();		
		if (c != null)
			calendario = CalendarioAcademicoHelper.getCalendario(c);
		
		if (calendario == null)
			calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalMedio();
		
		if (ValidatorUtil.isEmpty(calendario)){
			addMensagemErro("Calendário acadêmico não definido.");
			return;
		}

		HttpServletResponse res = getCurrentResponse();
		try {
			ParametrosGestoraAcademica p = ParametrosGestoraAcademicaHelper.getParametrosMedio();
			new GerarDiarioClasseMedio(turma, calendario, p).gerar(res.getOutputStream());
			res.setContentType("application/pdf");
			res.addHeader("Content-Disposition", "attachment; filename=diario_"+turma.getDisciplina().getCodigo()+"_"+turma.getAnoPeriodo()+"_"+turma.getCodigo()+"_.pdf");
			FacesContext.getCurrentInstance().responseComplete();
		} catch(NegocioException e) {
			throw e;
		}
	}	

}
