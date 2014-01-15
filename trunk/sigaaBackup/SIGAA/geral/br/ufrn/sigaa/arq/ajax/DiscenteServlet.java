/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/03/2007
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Servlet que busca as informações de discentes por ajax
 * 
 * @author Rafael
 *
 */
public class DiscenteServlet extends SigaaAjaxServlet {

	/**
	 * Busca de Dicente por Ajax.
	 * 
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public String getXmlContent(HttpServletRequest request,	HttpServletResponse response) throws Exception {

		DiscenteDao dao = new DiscenteDao();
		
		try {
			// Inicializar critério de busca
			String param  = findParametroLike("discente", request);
			if ("".equals(param)) {
				param  = findParametroLike("pessoa", request);
			}

			// Inicializar nível de ensino do discente
			char[] nivel = getNivelEnsinoArray(request);

			// Inicializar status desejados para o discente
			String statusDiscente = request.getParameter("status");
			boolean somenteAtivos = true;
			if (statusDiscente != null && "todos".equals(statusDiscente)) {
				somenteAtivos = false;
			}
			boolean somenteConcluintes = ("sim".equals(request.getParameter("concluido"))) ? true : false;
				
			Curso curso = null;
			// Verificar se está no contexto de coordenação de Lato Sensu
			if ( (getAcessoMenu(request).isCoordenadorCursoLato() 
					|| getAcessoMenu(request).isSecretarioLato()) 
					&& SigaaSubsistemas.PORTAL_COORDENADOR_LATO.equals(getSubSistemaCorrente(request)) ) {
				curso = (Curso) request.getSession().getAttribute("cursoAtual");
			}
			
			Collection<Discente> lista = new HashSet<Discente>();
		
			// Tenta converter para Long e buscar por matrícula
			try {
				Long matricula = Long.parseLong(param.trim());
				// Buscar apenas se mais de 7 dígitos tiverem sido informados
				if (param.trim().length() > 7) {
					Discente discente;
					if (somenteConcluintes) 
						discente = dao.findByMatricula(matricula, nivel, StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO);
					else 
						discente = somenteAtivos ? dao.findAtivosByMatricula(matricula, nivel) : dao.findByMatricula(matricula, nivel);

					if (curso != null && discente.getCurso() != null && discente.getCurso().getId() != curso.getId()) {
						discente = null;
					}
					if (discente != null) {
						lista.add(discente);
					}
				}
			} catch (NumberFormatException nfe) {
				// Inicializar unidade gestora acadêmica
				boolean ignorarUnidade = ("sim".equals(request.getParameter("ignorarUnidade"))) ? true : false;
				int idUnidade = 0;
				if (!ignorarUnidade) 
					idUnidade =  identificarUnidadeUsuario(request, nivel);
				
				// Tentar buscar por nome
				lista = dao.findByNomeOtimizado(param, idUnidade, nivel, curso, somenteAtivos, somenteConcluintes);
			}
			String campo = "matriculaNomeNivelFormatado";
			if ( statusDiscente != null && statusDiscente.equals("todos") ) {
				campo = "matriculaNomeSituacaoFormatado";
			}
			// Create xml schema
			return new AjaxXmlBuilder().addItems(lista, campo, "id").toString();

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Erro ao buscar discente. Contacte o administrador do sistema.");
			response.flushBuffer();
			return null;
		} finally {
			if (dao != null)
				dao.close();			
		}
	}

	/**
	 * Retorna o id da unidade do usuário que deverá ser usada para as consultas 
	 * e -1 caso nenhuma deva ser selecionada. 
	 * 
	 * @param request
	 * @param nivel
	 * @return
	 */
	private int identificarUnidadeUsuario(HttpServletRequest request, char[] nivel) {
		
		Unidade unidade = getUnidadeUsuario(request);
		if ( ArrayUtils.contains(nivel, NivelEnsino.FORMACAO_COMPLEMENTAR) ) {
			unidade = ((Usuario)getUsuarioLogado(request)).getVinculoAtivo().getUnidade();
		} 
		
		int idUnidade = -1;
		if (unidade != null
				&& unidade.getGestoraAcademica() != null
				&& !ArrayUtils.contains(nivel, NivelEnsino.GRADUACAO)
				&& !ArrayUtils.contains(nivel, NivelEnsino.MESTRADO)
				&& !ArrayUtils.contains(nivel, NivelEnsino.DOUTORADO)
				&& !ArrayUtils.contains(nivel, NivelEnsino.STRICTO)
				&& !ArrayUtils.contains(nivel, NivelEnsino.LATO)) {
			if(ArrayUtils.contains(nivel, NivelEnsino.TECNICO) || ArrayUtils.contains(nivel, NivelEnsino.FORMACAO_COMPLEMENTAR)) {
				idUnidade = unidade.getId();
			} else {
				idUnidade = unidade.getGestoraAcademica().getId();
			}
		}
		return idUnidade;
	}
	
	/** Retorna um array de char, contendo os níveis passados como parâmetro na request.
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	public char[] getNivelEnsinoArray(HttpServletRequest req) throws ArqException {

		String nivel = req.getParameter("nivel");
		if (nivel == null || "".equals(nivel))
			nivel  = String.valueOf(super.getNivelEnsino(req));
		else if (nivel.equalsIgnoreCase("ufrn")) {
			nivel = String.valueOf('0');
		}
		return nivel.toCharArray();
	}

	/**
	 * Retorna a Unidade do Usuário.
	 * @see br.ufrn.sigaa.arq.ajax.SigaaAjaxServlet#getUnidadeUsuario(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Unidade getUnidadeUsuario(HttpServletRequest req) {
		try {
			char[] nivel = getNivelEnsinoArray(req);
			if (nivel[0] != '0')
				return super.getUnidadeUsuario(req);
			else
				return new Unidade(-1);
		} catch (ArqException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
