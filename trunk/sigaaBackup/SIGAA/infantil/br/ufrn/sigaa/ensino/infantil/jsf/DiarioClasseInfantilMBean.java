/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 01/10/2012
 *
 */
package br.ufrn.sigaa.ensino.infantil.jsf;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Managed Bean para gera��o do Di�rio de Classe do Ensino Infantil.
 *
 * @author Diego J�come
 *
 */
@Component("diarioClasseInfantil") @Scope("request")
public class DiarioClasseInfantilMBean extends ControllerTurmaVirtual {

	/**
	 * Inicia a gera��o do di�rio de classe para a turma virtual que est� sendo trabalhada atualmente.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Efetua a gera��o do di�rio de classe de acordo com a turma passada como par�metro.
	 * 
	 * @param turma
	 * @throws Exception
	 * @throws IOException
	 */
	private void gerarDiario(Turma turma) throws IOException, Exception {

		if (turma == null || turma.getDisciplina() == null) return;
		turma = getDAO(GenericSigaaDAO.class).findByPrimaryKey(turma.getId(), Turma.class);

		HttpServletResponse res = getCurrentResponse();
		try {
			new GerarDiarioClasseInfantil(turma).gerar(res.getOutputStream());
			res.setContentType("application/pdf");
			res.addHeader("Content-Disposition", "attachment; filename=diario_"+turma.getDisciplina().getCodigo()+"_"+turma.getAnoPeriodo()+"_"+turma.getCodigo()+"_.pdf");
			FacesContext.getCurrentInstance().responseComplete();
		} catch(NegocioException e) {
			throw e;
		}
	}
	
	/**
	 * Efetua a gera��o do mapa de frequ�ncia de acordo com a turma passada como par�metro.
	 * 
	 * @param turma
	 * @throws Exception
	 * @throws IOException
	 */
	public String gerarMapaFrequencia(Turma turma) throws IOException, Exception {

		if (turma == null || turma.getDisciplina() == null) return null;
		turma = getDAO(GenericSigaaDAO.class).findByPrimaryKey(turma.getId(), Turma.class);
		
		HttpServletResponse res = getCurrentResponse();
		try {
			new GerarDiarioClasseInfantil(turma).gerarMapa(res.getOutputStream());
			res.setContentType("application/pdf");
			res.addHeader("Content-Disposition", "attachment; filename=diario_"+turma.getDisciplina().getCodigo()+"_"+turma.getAnoPeriodo()+"_"+turma.getCodigo()+"_.pdf");
			FacesContext.getCurrentInstance().responseComplete();
			return null;
		} catch(NegocioException e) {
			throw e;
		}
	}	
	
}
