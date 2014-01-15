package br.ufrn.sigaa.ensino.infantil.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosInfantil;

/**
 * Action que controla a entrada no menu do ensino infantil
 * @author Leonardo Campos
 *
 */
public class EntrarInfantilAction extends AbstractAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        checkRole(SigaaPapeis.GESTOR_INFANTIL, request);
        request.setAttribute("hideSubsistema", Boolean.TRUE);
        setSubSistemaAtual(request, SigaaSubsistemas.INFANTIL);
        request.getSession().setAttribute(
                SigaaAbstractController.CURSO_ATUAL,
                getGenericDAO(request).findByPrimaryKey(
                        new Integer(ParametroHelper.getInstance().getParametro(
                                ParametrosInfantil.ID_CURSO_INFANTIL))
                                .intValue(), Curso.class));
        request.getSession().setAttribute(
                SigaaAbstractController.PARAMETROS_SESSAO,
                ParametrosGestoraAcademicaHelper.getParametrosInfantil());
        return mapping.findForward("sucesso");

    }

}
