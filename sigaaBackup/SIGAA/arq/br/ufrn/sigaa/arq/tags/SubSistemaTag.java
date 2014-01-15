package br.ufrn.sigaa.arq.tags;

import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.SubSistema;

public class SubSistemaTag extends ConditionalTagSupport {

	private static final String	TECNICO		= "tecnico";
	private static final String	LATO		= "lato";
	private static final String	PESQUISA	= "pesquisa";
	private static final String MEDIO 		= "medio";
	private static final String INFANTIL	= "infantil";
	private static final String GRADUACAO 	= "graduacao";
	private static final String MONITORIA	= "monitoria";
	private static final String STRICTO		= "stricto";
	private static final String ADMINISTRACAO		= "admin";
	private static final String PORTAL_DISCENTE		= "portalDiscente";
	private static final String PORTAL_DOCENTE		= "portalDocente";
	private static final String PORTAL_COORDENADOR		= "portalCoordenadorGrad";
	private static final String PORTAL_COORDENADOR_STRICTO	= "portalCoordenadorStricto";
	private static final String PORTAL_COORDENADOR_LATO	= "portalCoordenadorLato";
	private static final String CONSULTA		= "consulta";
	private static final String BIBLIOTECA		= "biblioteca";
	private static final String PORTAL_PLANEJAMENTO = "portalPlanejamento";
	private static final String RESIDENCIA	= "residencia";
	private static final String FORMACAO_COMPLEMENTAR	= "formacaoComplementar";
	private static final String PORTAL_TUTOR	= "tutor";

	private String				teste;

	private String semLink;

	@Override
	public int doStartTag() throws JspException {

		try {
			if (teste == null) {
				SubSistema sub = (SubSistema) pageContext.getSession()
				.getAttribute("subsistema");
				if (sub == null) {
					// verifica se não está na área pública
					if (pageContext.getSession().getAttribute("REGISTRO_ACESSO_PUBLICO") == null)
						pageContext.getOut().print("Não Definido");
				} else {
					if (!"true".equalsIgnoreCase(semLink)) {
						pageContext.getOut().print(
								"<a href='" + sub.getLink() + "'>" + sub.getNome()
								+ "</a>");
					}else {
						pageContext.getOut().print(sub.getNome());
					}

				}
				return EVAL_PAGE;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.doStartTag();
	}

	/**
	 * Nesse atributo deve conter os subsistemas (separados por vírgula) a ser
	 * testado para exibição do corpo da tag
	 */
	@Override
	protected boolean condition() throws JspTagException {

		SubSistema subSistemaCorrente = (SubSistema) pageContext.getSession()
				.getAttribute("subsistema");
		boolean exibir = false;
		boolean negacao = false;
		if (subSistemaCorrente != null) {
			if (teste.startsWith("not ")) {
				negacao = true;
				teste = teste.substring(3);
			}
			StringTokenizer st = new StringTokenizer(teste, ",");
			while (st.hasMoreTokens()) {
				String sub = st.nextToken().trim();
				if (TECNICO.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.TECNICO)) {
					exibir = true;
				} else if (LATO.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.LATO_SENSU)) {
					exibir = true;
				} else if (PESQUISA.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.PESQUISA)) {
					exibir = true;
				} else if (MEDIO.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.MEDIO)) {
					exibir = true;
				} else if (INFANTIL.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.INFANTIL)) {
					exibir = true;
				} else if (GRADUACAO.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.GRADUACAO)) {
					exibir = true;
				} else if (MONITORIA.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.MONITORIA)) {
					exibir = true;
				} else if (STRICTO.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.STRICTO_SENSU)){
					exibir = true;
				} else if (ADMINISTRACAO.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.ADMINISTRACAO)){
					exibir = true;
				} else if (PORTAL_DISCENTE.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.PORTAL_DISCENTE)){
					exibir = true;
				} else if (PORTAL_DOCENTE.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.PORTAL_DOCENTE)){
					exibir = true;
				} else if (PORTAL_COORDENADOR.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.PORTAL_COORDENADOR)){
					exibir = true;
				} else if (PORTAL_COORDENADOR_LATO.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)){
					exibir = true;
				} else if (PORTAL_COORDENADOR_STRICTO.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO)){
					exibir = true;
				} else if (CONSULTA.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.CONSULTA)){
					exibir = true;
				}else if (BIBLIOTECA.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.BIBLIOTECA)){
					exibir = true;
				}else if (PORTAL_PLANEJAMENTO.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.PORTAL_PLANEJAMENTO)){
					exibir = true;
				}else if (RESIDENCIA.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.COMPLEXO_HOSPITALAR)){
					exibir = true;
				}else if (FORMACAO_COMPLEMENTAR.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR)){
					exibir = true;
				}else if (PORTAL_TUTOR.equals(sub)
						& subSistemaCorrente.equals(SigaaSubsistemas.PORTAL_TUTOR)){
					exibir = true;
				}
			}
		}
		if (negacao) return !exibir;
		return exibir;
	}

	public void setTeste(String teste) {
		this.teste = teste;
	}

	public String getSemLink() {
		return semLink;
	}

	public void setSemLink(String semLink) {
		this.semLink = semLink;
	}


}
