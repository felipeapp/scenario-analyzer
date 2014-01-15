package br.ufrn.sigaa.ensino.graduacao.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Interface utilizada para que outro controller utilize o controller
 * ComponenteCurricularMBean para selecionar um componente curricular.<br/>
 * Forma de utiliza��o:<br/>
 * Para chamar o controller e buscar um componente, implemente a interface e
 * chame-o da seguinte forma no seu controller:<br/>
 * <code>
 * ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
 * return mBean.buscarComponente(this, "Cadastro de Turmas", null, null);
 * </code> Implemente o m�todo
 * selecionaComponenteCurricular(ComponenteCurricular) no seu controller, que
 * ir� receber o componente curricular selecionado pelo usu�rio, e redirecione
 * para o formul�rio desejado.<br/>
 * Exemplo:<br/>
 * <code>
 * obj.setDisciplina(componente);
 * return forward(formDadosGerais());
 * </code>
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public interface SeletorComponenteCurricular {
	
	/** M�todo para sele��o de um componente curricular. */
	public String selecionaComponenteCurricular(ComponenteCurricular componente) throws ArqException;
	
	/** M�todo para efetuar a valida��o dos dados da sele��o de um componente curricular. */
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente) throws ArqException;

	/** M�todo que retorna da sele��o de componente, sem selecionar um componente. (Opera��o voltar). */
	public String retornarSelecaoComponente();
	
}
