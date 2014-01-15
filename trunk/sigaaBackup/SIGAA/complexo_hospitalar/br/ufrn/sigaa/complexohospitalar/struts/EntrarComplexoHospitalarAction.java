package br.ufrn.sigaa.complexohospitalar.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PROGRAMA_ATUAL;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Action que controla a entrada no menu da Residência em Saúde.
 * 
 * @author Ricardo Wendell
 * 
 */
public class EntrarComplexoHospitalarAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {

		setSubSistemaAtual(req, SigaaSubsistemas.COMPLEXO_HOSPITALAR);
		checkRole(new int[] {SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
				SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA}, req);
		
		DadosAcesso dados = getAcessoMenu(req);
		
		Usuario usr = (Usuario)getUsuarioLogado(req);
		usr.setNivelEnsino(NivelEnsino.RESIDENCIA);
		req.getSession().setAttribute("nivel", NivelEnsino.RESIDENCIA);		
		
		req.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
		req.getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad());
		
		
		if( !isEmpty(usr.getServidorAtivo()) ){ 
			
			CoordenacaoCursoDao coordCursoDao = getDAO(CoordenacaoCursoDao.class, req);
			Collection<CoordenacaoCurso> coordenacoesResidencia = coordCursoDao.findCoordResidenciaByServidor(usr.getServidorAtivo().getId(), true, null);
			
			if(!ValidatorUtil.isEmpty(coordenacoesResidencia)){
				
				dados.setResidencias(new ArrayList<Unidade>(0));
				for (CoordenacaoCurso cc : coordenacoesResidencia) {
					dados.getResidencias().add(cc.getUnidade());
				}
				
				req.getSession().setAttribute(PROGRAMA_ATUAL, dados.getResidencias().iterator().next());
				usr.setNivelEnsino(NivelEnsino.RESIDENCIA);
				req.getSession().setAttribute("nivel", NivelEnsino.RESIDENCIA);
				dados.incrementaTotalSistemas();
				
			}
			
		}
		
		return mapping.findForward("sucesso");
	}

}
