/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/10/2008 
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.text.DecimalFormat;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;

/**
 * Managed bean para criação do relatório de integralização
 * do currículo, que mostra, semestre a semestre, o que
 * o aluno integralizou.
 * 
 * @author David Pereira
 *
 */
@Component @Scope("request")
public class RelatorioIntegralizacaoCurriculoMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente {
	
	/** Coleção de Componentes Curriculares usados para listar os que foram integralizados */
	private Collection<CurriculoComponente> componentes;
	/** Componentes curriculares concluídos */
	private Collection<ComponenteCurricular> concluidos;
	/** Indice de Integralização */
	private double indice;

	/**
	 * Direciona o usuário para a tela de busca geral de discentes.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/relatorios_cdp.jsp</li>
	 * </ul>
	 */
	public String iniciar() {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.RELATORIO_INTEGRALIZACAO_CURRICULO);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Busca as informações de integralização do discente e direciona o usuário para o relatório.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * </ul>
	 */
	public String selecionaDiscente() throws ArqException {
		
		int id = getParameterInt("idDiscente", 0);
		if (id > 0){
			DiscenteDao dao = getDAO(DiscenteDao.class);			
			obj = dao.findDiscenteAdapterById(id);
		}
		
		componentes = getDAO(EstruturaCurricularDao.class).findCurriculoComponentesObrigatoriosByCurriculo(obj.getCurriculo().getId());
		concluidos = getDAO(MatriculaComponenteDao.class).findComponentesSituacaoByDiscente(obj, SituacaoMatricula.getSituacoesPagas());
		
		for (CurriculoComponente cc : componentes) {
			if (cc.getSemestreOferta() <= obj.getPeriodoAtual())
				indice++;
		}
		int total = calcularComponentesMatriculados();
		indice = total / indice;
		
		return forward("/graduacao/relatorios/integralizacao_curriculo/relatorio.jsp");
	}

	/**
	 * Identifica se um componente foi pago ou se uma equivalente foi paga. 
	 */
	private int calcularComponentesMatriculados() throws ArqException {
		int total = 0;
		for (CurriculoComponente cc : componentes) {
			
			boolean pagouComponente = concluidos.contains(cc.getComponente());
			boolean pagouEquivalente = false;
			if (cc.getComponente().getEquivalencia() != null)
				pagouEquivalente = ExpressaoUtil.eval(cc.getComponente().getEquivalencia(), concluidos);
			
			if (pagouComponente || pagouEquivalente) {
				cc.setSelecionado(true);
				total++;
			}
		}
		
		return total;
	}
	
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.obj = discente;
	}

	public Collection<CurriculoComponente> getComponentes() {
		return componentes;
	}

	public void setComponentes(Collection<CurriculoComponente> componentes) {
		this.componentes = componentes;
	}

	public Collection<ComponenteCurricular> getConcluidos() {
		return concluidos;
	}

	public void setConcluidos(Collection<ComponenteCurricular> concluidos) {
		this.concluidos = concluidos;
	}

	public double getIndice() {
		return indice;
	}

	/** Retorna o Índice formatado */
	public String getIndiceStr() {
		return new DecimalFormat("#,##0.00").format(indice);
	}
	
}
