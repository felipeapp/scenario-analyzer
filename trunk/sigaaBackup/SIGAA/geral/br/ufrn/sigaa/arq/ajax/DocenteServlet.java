/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '25/09/2006'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CorpoDocenteCursoLatoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.jsf.RegistroAtividadeMBean;
import br.ufrn.sigaa.jsf.DiscenteMBean;
import br.ufrn.sigaa.parametros.dominio.ParametrosInfantil;
import br.ufrn.sigaa.vestibular.jsf.ValidacaoFotoCandidatoMBean;

/**
 * Servlet que busca as informações dos docentes por AJAX
 *
 * @author Gleydson
 *
 */
public class DocenteServlet extends SigaaAjaxServlet {

	/**
	 * Busca por docentes por AJAX
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ServidorDao dao = null;
		try {

			/*
			 * Tipos: todos, externo, unidade (padrão)
			 */
			String tipo = findParametroLike("tipo", request);
			String nome = StringUtils.toAscii(findParametroLike("nome", request));
			String situacao = findParametroLike("situacao", request);
			String inativos = findParametroLike("inativos", request);
			String ativoEm = findParametroLike("ativoEm", request);
			String cedidos = findParametroLike("cedidos", request);

			// Popular ano em que o docente deveria estar ativo
			Integer anoAtivo = null;
			try {
				anoAtivo = Integer.valueOf(ativoEm);
			} catch (Exception e) {}

			dao = new ServidorDao();
			Collection lista = null;
			boolean apenasAtivos = inativos == null || !inativos.equals("true");
			boolean trazerCedidos = cedidos != null && "true".equals(cedidos);
			
			String campoId = "id";

			String label = apenasAtivos ? "siapeNomeFormatado" : "descricaoCompletaFormatado";

			if (tipo == null || "".equals(tipo)) {
				lista = dao.findByDocente(nome, getUnidadeUsuario(request).getId());
			} 
			if (tipo.equals("internoLato")) {
				CorpoDocenteCursoLatoDao corpoDao = new CorpoDocenteCursoLatoDao();
				try {
					label =  "descricaoCompleta";
					if ( (Curso) request.getSession().getAttribute("cursoAtual") != null ) {
						lista = corpoDao.findServidorCurso(nome, (Curso) request.getSession().getAttribute("cursoAtual"));
					} else {
						org.springframework.web.context.WebApplicationContext webApp = WebApplicationContextUtils.getWebApplicationContext( request.getSession().getServletContext() );
						RegistroAtividadeMBean mBean = (RegistroAtividadeMBean) webApp.getBean("registroAtividade");
						lista = corpoDao.findServidorCurso(nome, mBean.getObj().getDiscente().getCurso() );
					}
				} finally {
					corpoDao.close();
				}
				
			}
			else if (tipo.equalsIgnoreCase("ufrn")) {
				if (situacao != null && !situacao.equals("")) {
					if (situacao.equals("ativo")) {
						lista = dao.findByDocente(nome, 0, 0, apenasAtivos, anoAtivo, trazerCedidos, null);
						lista.addAll( dao.findByColaboradorVoluntario(nome, 0) );
					} else if(situacao.equals("inativo")){
						label =  "descricaoCompleta";
						lista = dao.findByDocenteInativo(nome, 0);
					}
				} else {
					lista = dao.findByDocente(nome, 0, 0, apenasAtivos, null, trazerCedidos, null);
					lista.addAll( dao.findByColaboradorVoluntario(nome, 0) );
				}
			} else if (tipo.equalsIgnoreCase("externo")) {
				label = "cpfNomeFormatado";
				lista = dao.findByDocenteExterno(nome);
			} else if (tipo.equalsIgnoreCase("externoInstituicao")) {
				label = "cpfNomeFormatado";
				lista = dao.findPessoaByNome(nome);
			//	campoId = "cpf_cnpj";
			}			
			else if (tipo.equalsIgnoreCase("externoInfantil")) {
				label = "cpfNome";
				lista = dao.findByDocenteExterno(nome, new Unidade(ParametroHelper.getInstance().getParametroInt(ParametrosInfantil.ID_NEI)));
			}			
			else if (tipo.equalsIgnoreCase("externoLato") ) {
				label = "cpfNome";
				if ( (Curso) request.getSession().getAttribute("cursoAtual") != null )
					lista = dao.findByDocenteExternoLato(nome, (Curso) request.getSession().getAttribute("cursoAtual") );
				else {
					org.springframework.web.context.WebApplicationContext webApp = WebApplicationContextUtils.getWebApplicationContext( request.getSession().getServletContext() );
					RegistroAtividadeMBean mBean = (RegistroAtividadeMBean) webApp.getBean("registroAtividade");
					lista = dao.findByDocenteExternoLato(nome, mBean.getObj().getDiscente().getCurso() );
				}
			} 
			else if (tipo.equalsIgnoreCase("unidade")) {

				if (situacao != null && !situacao.equals("")) {
					if (situacao.equals("ativo")) {
						lista = dao.findByDocente(nome, getUnidadeUsuario(request).getId(), 0, apenasAtivos, trazerCedidos);
						lista.addAll( dao.findByColaboradorVoluntario(nome, 0) );
					}
				} else {
					lista = dao.findByDocente(nome, getUnidadeUsuario(request)
							.getId(), apenasAtivos, trazerCedidos);
					lista.addAll( dao.findByColaboradorVoluntario(nome, 0) );
				}
			}

			// Create xml schema
			return new AjaxXmlBuilder().addItems(lista, label, campoId).toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

}
