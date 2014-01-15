/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.arq.web.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.ufrn.arq.dao.ArvoreUnidadesDao;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.UnidadeGeral;

/**
 * Classe com métodos para busca de unidades e criação
 * da hierarquia de unidades no componente da árvore de unidades.
 * 
 * @author David Pereira
 *
 */
public class ArvoreUnidades {

	private static final ArvoreUnidades INSTANCE = new ArvoreUnidades();
	
	public static ArvoreUnidades getInstance() {
		return INSTANCE;
	}
	
	public static final int ORGANIZACIONAL = 1;
	public static final int ORCAMENTARIA = 2;
	public static final int ACADEMICA = 3;
	
	private List<UnidadeGeral> unidadesOrganizacionais;
	private List<UnidadeGeral> unidadesOrcamentarias;
	private List<UnidadeGeral> unidadesAcademicas;

	private UnidadeGeral arvoreOrganizacional;
	private UnidadeGeral arvoreOrcamentaria;
	private UnidadeGeral arvoreAcademica;
	
	private Date ultimaAtualizacaoOrganizacionais = CalendarUtils.createDate(1, 1, 1900);
	private Date ultimaAtualizacaoOrcamentarias = CalendarUtils.createDate(1, 1, 1900);
	private Date ultimaAtualizacaoAcademicas = CalendarUtils.createDate(1, 1, 1900);
	
	private ArvoreUnidades() {
		buscaUnidadesOrganizacionais();
		buscaUnidadesOrcamentarias();
		buscaUnidadesAcademicas();
	}

	/**
	 * Retorna uma String contendo informações da árvore de unidades
	 * de acordo com a hierarquia organizacional de unidades.
	 * @param raiz 
	 * @return
	 */
	public String getArvoreOrganizacional(Integer[] raizes, boolean ajax) {
		
		buscaUnidadesOrganizacionais();
		
		StringBuilder arvore = new StringBuilder("<ul>");
		
		if (raizes.length > 1) {
			arvore.append("<li><span></span><label></label><ul>");
		}
		
		for (Integer raiz : raizes) {
			UnidadeGeral raizOrganizacional = arvoreOrganizacional;

			if (raiz != raizOrganizacional.getId()) {
				raizOrganizacional = getSubRaiz(raizOrganizacional, raiz);
			}
			
			arvore.append(getSubArvore(raizOrganizacional, ajax));
		}
		
		if (raizes.length > 1) {
			arvore.append("</ul></li>");
		}
		
		return arvore.append("</ul>").toString();
	}
	
	/**
	 * Retorna uma String contendo informações da árvore de unidades
	 * de acordo com a hierarquia orçamentária de unidades.
	 * @param raiz 
	 * @return
	 */
	public String getArvoreOrcamentaria(Integer[] raizes, boolean ajax) {
		
		buscaUnidadesOrcamentarias();
		
		StringBuilder arvore = new StringBuilder("<ul>");
		
		if (raizes.length > 1) {
			arvore.append("<li><span></span><label></label><ul>");
		}
		
		for (Integer raiz : raizes) {
			UnidadeGeral raizOrcamentaria = arvoreOrcamentaria;
			
			if (raiz != raizOrcamentaria.getId()) {
				raizOrcamentaria = getSubRaiz(raizOrcamentaria, raiz);
			}
			
			arvore.append(getSubArvore(raizOrcamentaria, ajax));
		}
		
		if (raizes.length > 1) {
			arvore.append("</ul></li>");
		}
		
		return arvore.append("</ul>").toString();
	}
	
	/**
	 * Retorna uma String contendo informações da árvore de unidades
	 * de acordo com a hierarquia acadêmica de unidades.
	 * @param raiz 
	 * @return
	 */
	public String getArvoreAcademica(Integer[] raizes, boolean ajax) {
		
		buscaUnidadesAcademicas();
		
		StringBuilder arvore = new StringBuilder("<ul>");
		
		if (raizes.length > 1) {
			arvore.append("<li><span></span><label></label><ul>");
		}
		
		for (Integer raiz : raizes) {
			UnidadeGeral raizAcademica = arvoreAcademica;
			
			if (raiz != raizAcademica.getId()) {
				raizAcademica = getSubRaiz(raizAcademica, raiz);
			}

			arvore.append(getSubArvore(raizAcademica, ajax));
		}
		
		if (raizes.length > 1) {
			arvore.append("</ul></li>");
		}
		
		return arvore.append("</ul>").toString();
	}
	
	/*
	 * Busca a raiz de uma sub-árvore cuja raiz
	 * é a passada como parâmetro.
	 */
	private UnidadeGeral getSubRaiz(UnidadeGeral arvore, Integer raiz) {
		if (arvore.getId() == raiz) {
			return arvore;
		} else {
			UnidadeGeral result = null;
			
			if (!isEmpty(arvore.getUnidadesFilhas())) {
				for (UnidadeGeral un : arvore.getUnidadesFilhas()) {		
					result = getSubRaiz(un, raiz);
					if (result != null && result.getId() == raiz)
						break;
				}
			}
			
			return result;
		}
	}
	
	/*
	 * Retorna a String com a árvore de unidades associada a sub-árvore da
	 * raiz passada como parâmetro. 
	 */
	private StringBuilder getSubArvore(UnidadeGeral arvore, boolean ajax) {
		StringBuilder str = new StringBuilder(2000);
		
		str.append("<li id=\"" + arvore.getId() + "\">");
		str.append("<span>" + arvore.getCodigoFormatado() + "</span><label>" + getDescricaoUnidade(arvore) + "</label><ul>");

		if (!isEmpty(arvore.getUnidadesFilhas()) && !ajax) {
			for (UnidadeGeral unidade : arvore.getUnidadesFilhas()) {
				if (unidade.getUnidadeResponsavel().getId() == arvore.getId()) {
					str.append(getSubArvore(unidade, ajax));
				}
			}
		}
		
		str.append("</ul>");
		str.append("</li>\n");
		
		return str;
	}

	/*
	 * Retorna a descrição da unidade de acordo com o parâmetro ARVORE_UNIDADES_FORMA_EXIBICAO,
	 * podendo ela ser por sigla ou por nome.
	 */
	private String getDescricaoUnidade(UnidadeGeral unidade) {
		String forma = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.ARVORE_UNIDADES_FORMA_EXIBICAO);
		
		if ("S".equals(forma)) {
			return unidade.getCodigoSigla();
		} else {
			return unidade.getCodigoNome();
		}
	}
	
	/*
	 * Pega o conjunto de unidades e monta o grafo de unidades,
	 */
	private UnidadeGeral organizarArvore(List<UnidadeGeral> unidades) {
		UnidadeGeral raiz = null;
		
		for (UnidadeGeral unidade : unidades) {
			if (unidade.getId() == UnidadeGeral.UNIDADE_DIREITO_GLOBAL)
				raiz = unidade;
			
			unidade.setUnidadesFilhas(new LinkedList<UnidadeGeral>());
			for (UnidadeGeral filha : unidades) {
				if (filha.getUnidadeResponsavel().getId() == unidade.getId() && filha.getId() != unidade.getId()) {
					unidade.getUnidadesFilhas().add(filha);
				}
			}
		}
		
		return raiz;
	}
	
	/*
	 * Retorna a lista de unidades a ser usada em uma busca de acordo com o tipo
	 * de árvore passado como parâmetro.
	 */
	private List<UnidadeGeral> listaUnidadesPorTipo(int tipo) {
		switch(tipo) {
		case ORGANIZACIONAL:
			return unidadesOrganizacionais;
		case ORCAMENTARIA:
			return unidadesOrcamentarias;
		case ACADEMICA:
			return unidadesAcademicas;
		}
		return null;
	}
	
	/*
	 * Retorna a raiz das unidades a ser usada em uma árvore de acordo com o tipo
	 * de árvore passado como parâmetro.
	 */
	private UnidadeGeral buscaRaizPorTipo(int tipo) {
		switch(tipo) {
		case ORGANIZACIONAL:
			return arvoreOrganizacional;
		case ORCAMENTARIA:
			return arvoreOrcamentaria;
		case ACADEMICA:
			return arvoreAcademica;
		}
		return null;
	}

	/**
	 * Busca uma unidade em uma árvore de acordo com o tipo informado.
	 * @param id
	 * @param tipo
	 * @return
	 */
	public UnidadeGeral getUnidade(int id, int tipo) {
		List<UnidadeGeral> unidades = listaUnidadesPorTipo(tipo);

		if (unidades != null) {
			for (UnidadeGeral unidade : unidades) {
				if (unidade.getId() == id)
					return unidade;
			}
		}
		
		return null;
	}
	
	/**
	 * Busca uma unidade pelo código em uma árvore de acordo com o tipo informado.
	 * @param id
	 * @param tipo
	 * @return
	 */
	public UnidadeGeral getUnidadePorCodigo(long codigo, int tipo) {
		List<UnidadeGeral> unidades = listaUnidadesPorTipo(tipo);

		if (unidades != null) {
			for (UnidadeGeral unidade : unidades) {
				if (unidade.getCodigo() == codigo)
					return unidade;
			}
		}
		
		return null;
	}

	/**
	 * Retorna uma lista de unidades fazendo uma busca por parte do nome. A busca
	 * será efetuada na árvore cujo tipo foi passado como parâmetro.
	 * @param nome
	 * @param tipo
	 * @return
	 */
	public List<UnidadeGeral> getUnidades(String nome, int tipo) {
		List<UnidadeGeral> unidades = listaUnidadesPorTipo(tipo);
		
		List<UnidadeGeral> result = new LinkedList<UnidadeGeral>();
		for (UnidadeGeral unidade : unidades) {
			if (StringUtils.toAsciiAndUpperCase(unidade.getNome()).contains(nome)) {
				result.add(unidade);
			}
		}
		return result;
	}

	/**
	 * Retorna todos os filhos (diretos) da unidade cujo id foi passado como parâmetro. A busca
	 * será efetuada na árvore cujo tipo foi passado como parâmetro.
	 * @param id
	 * @param tipo
	 * @return
	 */
	public List<UnidadeGeral> buscaFilhos(int id, int tipo) {
		List<UnidadeGeral> unidades = listaUnidadesPorTipo(tipo);
		List<UnidadeGeral> result = new LinkedList<UnidadeGeral>();
		
		if (id > 0) {
			for (UnidadeGeral unidade : unidades) {
				if (unidade.getUnidadeResponsavel().getId() == id && unidade.getId() != UnidadeGeral.UNIDADE_DIREITO_GLOBAL)
					result.add(unidade);
			}
		} else {
			result.add(buscaRaizPorTipo(tipo));
		}
		
		return result;
	}

	/**
	 * Retorna o path da unidade cujo código foi passado como parâmetro.
	 * Usado para expandir nós em árvores carregadas via ajax.
	 * @param extractInteger
	 * @return
	 */
	public String getPath(long codigo, int tipo) {
		UnidadeGeral unidade = getUnidadePorCodigo(codigo, tipo);
		StringBuilder path = new StringBuilder(String.valueOf(unidade.getId()));
		
		while(unidade.getId() != UnidadeGeral.UNIDADE_DIREITO_GLOBAL) {
			unidade = getUnidade(unidade.getUnidadeResponsavel().getId(), tipo);
			path.insert(0, unidade.getId() + "/");
		}
		
		return "/" + path;
	}
	
	/**
	 * Preenche as unidades organizacionais ou usa as que estiverem no cache, se ele não tiver expirado.
	 */
	private void buscaUnidadesOrganizacionais() {
		synchronized (ultimaAtualizacaoOrganizacionais) {

			if ( CalendarUtils.calculaMinutos(ultimaAtualizacaoOrganizacionais, new Date()) <
					ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.ARVORE_UNIDADES_DURACAO_CACHE) )
				return;
			
			ArvoreUnidadesDao dao = new ArvoreUnidadesDao();
			try {
				unidadesOrganizacionais = dao.findUnidadesOrganizacionaisComponenteArvore();
				arvoreOrganizacional = organizarArvore(unidadesOrganizacionais);
				ultimaAtualizacaoOrganizacionais = new Date();
			} finally {
				dao.close();
			}
		}
	}

	/**
	 * Preenche as unidades orçamentárias ou usa as que estiverem no cache, se ele não tiver expirado.
	 */
	private void buscaUnidadesOrcamentarias() {
		synchronized (ultimaAtualizacaoOrcamentarias) {

			if ( CalendarUtils.calculaMinutos(ultimaAtualizacaoOrcamentarias, new Date()) <
					ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.ARVORE_UNIDADES_DURACAO_CACHE) )
				return;
			
			ArvoreUnidadesDao dao = new ArvoreUnidadesDao();
			try {
				unidadesOrcamentarias = dao.findUnidadesOrcamentariasComponenteArvore();
				arvoreOrcamentaria = organizarArvore(unidadesOrcamentarias);
				ultimaAtualizacaoOrcamentarias = new Date();
			} finally {
				dao.close();
			}
		}
	}

	/**
	 * Preenche as unidades acadêmicas ou usa as que estiverem no cache, se ele não tiver expirado.
	 */
	private void buscaUnidadesAcademicas() {
		synchronized (ultimaAtualizacaoAcademicas) {
			
			if ( CalendarUtils.calculaMinutos(ultimaAtualizacaoAcademicas, new Date()) <
					ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.ARVORE_UNIDADES_DURACAO_CACHE) )
				return;
			
			ArvoreUnidadesDao dao = new ArvoreUnidadesDao();
			try {
				unidadesAcademicas = dao.findUnidadesAcademicasComponenteArvore();
				arvoreAcademica = organizarArvore(unidadesAcademicas);
				ultimaAtualizacaoAcademicas = new Date();
			} finally {
				dao.close();
			}
		}
	}

}
