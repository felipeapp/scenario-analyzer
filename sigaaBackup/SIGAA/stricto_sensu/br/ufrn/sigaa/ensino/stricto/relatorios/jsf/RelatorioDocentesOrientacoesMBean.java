/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
 * Controller responsável pela geração do relatório de orientações concluídas por docentes.
 * @author Victor Hugo
 */
@Component("relatorioDocentesOrientacoes") 
@Scope("request")
public class RelatorioDocentesOrientacoesMBean extends RelatoriosStrictoMBean {

	/** Ano de referência para geração do relatório. */
	private int ano;

	/** Dados do relatório. */
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
		titulo = "Docentes X Orientações Concluídas";
		return forward("/stricto/relatorios/docentes_orientacoes_concluidas.jsp");
	}

	/** 
	 * Gera relatório.
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

	/** Retorna o ano de referência para geração do relatório. 
	 * @return Ano de referência para geração do relatório. 
	 */
	public Integer getAno() {
		return ano;
	}

	/** Seta o ano de referência para geração do relatório.
	 * @param ano Ano de referência para geração do relatório. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna os dados do relatório.  
	 * @return Dados do relatório. 
	 */
	public List<Map<String, Object>> getDadosRelatorio() {
		return dadosRelatorio;
	}

	/** Seta os dados do relatório.
	 * @param dadosRelatorio Dados do relatório. 
	 */
	public void setDadosRelatorio(List<Map<String, Object>> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

}
