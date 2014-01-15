/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 28/09/2006
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Servlet para busca de turmas por ajax
 *
 * @author David Ricardo
 *
 */
public class TurmaServlet extends SigaaAjaxServlet {

	@Override
	public String getXmlContent(HttpServletRequest req, HttpServletResponse res) throws Exception {

		String metodo = req.getParameter("dispatch");
		if (metodo != null && !metodo.equals(""))
			return callOutPeriodos(req, res);

		TurmaDao dao = new TurmaDao();
		CalendarioAcademicoDao cdao = new CalendarioAcademicoDao();
		List<Turma> lista = new ArrayList<Turma>();

		try {
			String nome = req.getParameter("obj.turma.disciplina.nome");

			if (nome == null)
				nome = findParametroLike("nomeTurma", req);

			char nivelEnsino = getNivelEnsino(req);
			Unidade unidadeUsuario = identificarUnidadeUsuario(req);
			
			CalendarioAcademico cal = cdao.findByUnidadeNivel(unidadeUsuario.getId(), nivelEnsino);

			if (getSubSistemaCorrente(req).equals(SigaaSubsistemas.TECNICO) || getSubSistemaCorrente(req).equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR)) {
				lista.addAll(dao.findByNomeDisciplina(nome, unidadeUsuario.getId(), nivelEnsino, SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE));
			} else if( !getSubSistemaCorrente(req).equals(SigaaSubsistemas.LATO_SENSU) && !getSubSistemaCorrente(req).equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) ) {
				lista.addAll(dao.findByNomeDisciplinaAnoPeriodo(nome, unidadeUsuario.getId(), cal.getAno(), cal.getPeriodo(), nivelEnsino, SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE));
			} else {
				lista.addAll(dao.findByNomeDisciplina(nome, 0, nivelEnsino, SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE));
			}

			for (Turma turma : lista) {
				turma.getDescricaoComCh();
			}
		}
		finally {
			dao.close();
		}
		return new AjaxXmlBuilder().addItems(lista, "descricaoComCh", "id").toString();
	}

	private Unidade identificarUnidadeUsuario(HttpServletRequest req) throws ArqException {
		Unidade unidadeUsuario = getUnidadeUsuario(req);
		if (getNivelEnsino(req) == NivelEnsino.FORMACAO_COMPLEMENTAR ) {
			unidadeUsuario = new Unidade( 
					getUsuarioLogado(req).getPermissao(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR)
							.iterator().next().getUnidadePapel() );
		}
		return unidadeUsuario;
	}

	public String callOutPeriodos(HttpServletRequest req, HttpServletResponse res) throws Exception {
		StringBuffer html = new StringBuffer();
		html.append("<input type='radio' class='noborder' name='periodo' value='atual' id='atualId' onfocus='updateForm(this)'> "+
		"<label onclick=\"marcaCheckBox('atualId');$('atualId').focus();\" >Ano-Período Atual</label><br>");
		if (req.getSession().getAttribute("calProx") != null)
			html.append("<input type='radio' class='noborder' name='periodo' value='proximo' id='proxId' onfocus='updateForm(this)'> "+
		"<label onclick=\"marcaCheckBox('proxId');$('proxId').focus();\" >Próximo Ano-Período</label>");
		return new AjaxXmlBuilder().addItemAsCData(html.toString(), "").toString();
	}

}