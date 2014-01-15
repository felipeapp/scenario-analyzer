package br.ufrn.sigaa.ensino.graduacao.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Interface utilizada para que outro controller utilize o controller
 * ComponenteCurricularMBean para selecionar um componente curricular.<br/>
 * Forma de utilização:<br/>
 * Para chamar o controller e buscar um componente, implemente a interface e
 * chame-o da seguinte forma no seu controller:<br/>
 * <code>
 * ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
 * return mBean.buscarComponente(this, "Cadastro de Turmas", null, null);
 * </code> Implemente o método
 * selecionaComponenteCurricular(ComponenteCurricular) no seu controller, que
 * irá receber o componente curricular selecionado pelo usuário, e redirecione
 * para o formulário desejado.<br/>
 * Exemplo:<br/>
 * <code>
 * obj.setDisciplina(componente);
 * return forward(formDadosGerais());
 * </code>
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public interface SeletorComponenteCurricular {
	
	/** Método para seleção de um componente curricular. */
	public String selecionaComponenteCurricular(ComponenteCurricular componente) throws ArqException;
	
	/** Método para efetuar a validação dos dados da seleção de um componente curricular. */
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente) throws ArqException;

	/** Método que retorna da seleção de componente, sem selecionar um componente. (Operação voltar). */
	public String retornarSelecaoComponente();
	
}
