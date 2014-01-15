/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 17/02/2011
 *
 */
package br.ufrn.sigaa.ensino.tecnico.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Action que controla a entrada no menu do módulo de Formação Complementar
 * O nível de ensino no qual tais escolas estão inseridas é o de Formação Complementar.
 * 
 * @author Leonardo Campos
 * @author Ricardo Wendell
 * 
 */
public class EntrarFormacaoComplementarAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		Usuario usuario = (Usuario) getUsuarioLogado(request);

		// Definir os dados de acesso do usuário ao modulo
		usuario.setNivelEnsino(NivelEnsino.FORMACAO_COMPLEMENTAR);
		Unidade unidade = getUnidadePermissaoUsuario(request, usuario);
		
		setSubSistemaAtual(request, SigaaSubsistemas.FORMACAO_COMPLEMENTAR);
		setParametrosGestoraAcademica(request, ParametrosGestoraAcademicaHelper.getParametros(unidade, usuario.getNivelEnsino(), null, null, null));

		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(null, null, unidade, usuario.getNivelEnsino(), null, null, null);
		session.setAttribute(CALENDARIO_SESSAO, calendario.getNivel() == NivelEnsino.FORMACAO_COMPLEMENTAR && calendario.getUnidade().getId() == unidade.getId() ? calendario : null);
		session.setAttribute("nivel", NivelEnsino.FORMACAO_COMPLEMENTAR);
		
		request.setAttribute("hideSubsistema", Boolean.TRUE);
		
		
		if ( getParametrosAcademicos(request).getNivel() != NivelEnsino.FORMACAO_COMPLEMENTAR || !( usuario.getVinculoAtivo().getUnidade().isUnidadeAcademica() || usuario.getVinculoAtivo().getUnidade().isUnidadeAcademicaEspecializada() ) ) {
			throw new ConfiguracaoAmbienteException("Os parâmetros da sua unidade para este nível de ensino não foram localizados ou a sua unidade de lotação não é acadêmica. " +
					"Por favor, entre em contato com o suporte do sistema.");
		}		
		
		return mapping.findForward("sucesso");
	}

	private Unidade getUnidadePermissaoUsuario(HttpServletRequest request, Usuario usuario) throws DAOException, ArqException {
		UnidadeGeral unidadePermissao = usuario.getPermissao(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR).get(0).getUnidadePapel();
		Unidade unidade = getGenericDAO(request).findByPrimaryKey(unidadePermissao.getId() , Unidade.class);
		return unidade;
	}
}
