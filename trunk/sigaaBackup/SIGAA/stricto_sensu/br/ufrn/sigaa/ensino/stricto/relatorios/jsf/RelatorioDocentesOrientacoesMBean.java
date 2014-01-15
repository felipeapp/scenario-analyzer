/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on May 27, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;

/**
 * Controller respons�vel pela gera��o do relat�rio de orienta��es conclu�das por docentes.
 * @author Victor Hugo
 */
@Component("relatorioDocentesOrientacoes") 
@Scope("request")
public class RelatorioDocentesOrientacoesMBean extends RelatoriosStrictoMBean {

	/** Ano de refer�ncia para gera��o do relat�rio. */
	private int ano;

	/** Dados do relat�rio. */
	private List<Map<String, Object>> dadosRelatorio;

	/**
	 * @see br.ufrn.sigaa.ensino.stricto.relatorios.jsf.RelatoriosStrictoMBean#clear()
	 */
	@Override
	protected void clear() {
		super.clear();
		ano = getAnoAtual();
	}

	/**
	 * Inicializa os atributos do controller.
	 * 
	 * Chamado por:
	 * /stricto/menus/relatorios.jsp
	 */
	public String iniciar() {
		clear();
		titulo = "Docentes X Orienta��es Conclu�das";
		return forward("/stricto/relatorios/docentes_orientacoes_concluidas.jsp");
	}

	/** 
	 * Gera relat�rio.
	 * 
	 * Chamado por:
	 * /stricto/relatorios/docentes_orientacoes_concluidas.jsp
	 * 
	 * @see br.ufrn.sigaa.ensino.stricto.relatorios.jsf.RelatoriosStrictoMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		if (ano <= 1900) {
			addMensagemErro("Informe um ano maior que 1900");
			return null;
		}
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		dadosRelatorio = dao.dadosRelatorioOrientacoesConcluidas(unidade.getId(), ano, NivelEnsino.getNiveisStricto());
		if (unidade.getId() > 0)
			unidade = dao.refresh(unidade);
		else 
			unidade.setNome("TODOS");
		return forward("/stricto/relatorios/relatorio_orientacoes_concluidas.jsp");
	}

	/** Retorna o ano de refer�ncia para gera��o do relat�rio. 
	 * @return Ano de refer�ncia para gera��o do relat�rio. 
	 */
	public Integer getAno() {
		return ano;
	}

	/** Seta o ano de refer�ncia para gera��o do relat�rio.
	 * @param ano Ano de refer�ncia para gera��o do relat�rio. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna os dados do relat�rio.  
	 * @return Dados do relat�rio. 
	 */
	public List<Map<String, Object>> getDadosRelatorio() {
		return dadosRelatorio;
	}

	/** Seta os dados do relat�rio.
	 * @param dadosRelatorio Dados do relat�rio. 
	 */
	public void setDadosRelatorio(List<Map<String, Object>> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

}
