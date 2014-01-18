package br.ufrn.sigaa.ensino.metropoledigital.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CURSO_ATUAL;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * 
 * @author Rafael Barros, Rafael Silva, Gleydson Lima
 * 
 * Classe responsável pela verificação do acesso ao modulo metropole digital IMD
 *
 */
public class EntrarMetropoleDigitalAction extends SigaaAbstractAction{
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Usuario usr = (Usuario) getUsuarioLogado(request);
		usr.setNivelEnsino(NivelEnsino.TECNICO);
		request.getSession().setAttribute("nivel", NivelEnsino.TECNICO);
		request.setAttribute("hideSubsistema", Boolean.TRUE);
		setSubSistemaAtual(request, SigaaSubsistemas.METROPOLE_DIGITAL);

		DadosAcesso dados = getAcessoMenu(request);
		
		if(isUserInRole(request, SigaaPapeis.GESTOR_METROPOLE_DIGITAL)){
			CoordenacaoCursoDao coordCursoDao = getDAO(CoordenacaoCursoDao.class, request);
			if (usr.getServidor()!=null) {
				Collection<CoordenacaoCurso> coordenacoesTecnicas = coordCursoDao.findByServidor(usr.getServidor().getId(), new Integer(0), NivelEnsino.TECNICO, null);
				if (coordenacoesTecnicas != null && !coordenacoesTecnicas.isEmpty()) {
					Curso cursoAtual = (Curso) request.getSession().getAttribute("cursoAtual");
					dados.setCursos ( new ArrayList<Curso>() );	
					for (CoordenacaoCurso c : coordenacoesTecnicas) {
						dados.getCursos().add(c.getCurso());
						c.getCurso().getDescricaoCompleta();
						c.getCurso().getDescricao();
						if (c.getCurso().isADistancia()) {
							dados.setCursoEad(true);
						}
						if( isEmpty(cursoAtual) || !cursoAtual.isTecnico() )
							request.getSession().setAttribute(CURSO_ATUAL, c.getCurso());
					}
					dados.incrementaTotalSistemas();
				}				
			}
		}
			
			
					
		Curso cur = (Curso) request.getSession().getAttribute(CURSO_ATUAL);
		
		request.getSession().setAttribute(PARAMETROS_SESSAO, 
						ParametrosGestoraAcademicaHelper.getParametros(cur != null ? cur.getUnidade() : usr.getVinculoAtivo().getUnidade(), usr.getNivelEnsino(), null, null, null));
		
		ParametrosGestoraAcademica param = (ParametrosGestoraAcademica) request.getSession().getAttribute(PARAMETROS_SESSAO);
		
		if(!(isUserInRole(request, SigaaPapeis.ASSISTENTE_SOCIAL_IMD) || isUserInRole(request, SigaaPapeis.COORDENADOR_TUTOR_IMD) || isUserInRole(request, SigaaPapeis.COORDENADOR_POLO_IMD ) || isUserInRole(request, SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_IMD)  || isUserInRole(request, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_IMD))){
			if ( param.getNivel() != NivelEnsino.TECNICO || !( usr.getVinculoAtivo().getUnidade().isUnidadeAcademica() || usr.getVinculoAtivo().getUnidade().isUnidadeAcademicaEspecializada() ) ) {
				throw new ConfiguracaoAmbienteException("Os parâmetros da sua unidade para este nível de ensino não foram localizados ou a sua unidade de lotação não é acadêmica. " +
						"Por favor, entre em contato com o suporte do sistema.");
			}
		}
		
		request.getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(null, null, usr.getVinculoAtivo().getUnidade(), usr.getNivelEnsino(), null, null, null));

		return mapping.findForward("sucesso");

	}


}
