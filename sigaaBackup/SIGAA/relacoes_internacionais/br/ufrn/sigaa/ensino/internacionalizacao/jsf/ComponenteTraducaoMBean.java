/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 09/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.jsf;

import org.hibernate.criterion.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.graduacao.jsf.ComponenteCurricularMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.SeletorComponenteCurricular;

/**
 * MBean para a manutenção das traduções do {@link ComponenteCurricular componente curricular}.
 * 
 * @author Rafael Gomes
 *
 */
@Component
@Scope("session")
public class ComponenteTraducaoMBean extends AbstractTraducaoElementoMBean<ComponenteCurricular> implements SeletorComponenteCurricular{

	/** Define o link para a busca geral de componente curricular. */
	private static final String JSP_BUSCA_GERAL = "/graduacao/componente/lista.jsf";
	

	/** 
	 * Construtor padrão. 
	 */
	public ComponenteTraducaoMBean() {
		initObj();
	}
	
	/**
	 * Inicializa os campos do objeto que representa um componente para ser
	 * manipulado durante as operações.
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	private void initObj() {
		obj = new ComponenteCurricular();
		
		obj.setPrograma(null);
		obj.setComponentesCursoLato(null);
		obj.setCurso(new Curso());
		obj.setDetalhes(new ComponenteDetalhes());
		obj.setConteudoVariavel(false);
	
	}
	
	@Override
	public String getDirBase() {
		return "/relacoes_internacionais/elemento";
	}
	
	@Override
	public String getListPage() {
		return JSP_BUSCA_GERAL;
	}
	
	/**
	 * Inicia o caso de uso de atualização.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkChangeRole();
		ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
		setConfirmButton("Cadastrar");
		return mBean.buscarComponente(this, "Tradução de Componente Curricular", null,false, true, null);
	}

	@Override
	public String retornarSelecaoComponente() {
		return cancelar();
	}

	@Override
	public String selecionaComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		this.obj = componente;
		prepareMovimento(SigaaListaComando.TRADUZIR_ELEMENTO);
		if (obj.getSubUnidades() != null) obj.getSubUnidades().iterator();
		
		carregarElementosTraducao(componente.getClass(), componente.getId(), Order.desc("nome"));
		
		return forward(getDirBase() + "/componente.jsp");
	}
	
	

	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		ListaMensagens lista = new ListaMensagens();
		return lista;
	}
	
}