/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/03/2013
 *
 */
package br.ufrn.sigaa.estagio.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.estagio.dominio.ConvenioEstagio;

/**
 * Interface entre dois controlers (MBeans) para a seleção de um convoênio de
 * estágio.<br/>
 * O controller solicitante deverá implementar esta interface. Para selecionar
 * um convênio, o controller cliente deverá chamar a busca por convênio da
 * seguinte forma: 
 * <pre>
		StatusConvenioEstagio[] statusConvenioEstagio = {StatusConvenioEstagio.APROVADO};
		ConvenioEstagioMBean convenioEstagioMBean = getMBean("convenioEstagioMBean");
		return convenioEstagioMBean.iniciarSeletorEstagio(this, statusConvenioEstagio);
 * </pre> 
 * Quando o usuário selecionar o estágio, o controller
 * irá chamar o cliente para validar o convênio selecionado (método
 * {@link #validaConvenioSelecionado(ConvenioEstagio)}). Se não houver mensagem
 * de erro, setará o convênio selecionado no controller solicitante e passará o
 * fluxo para este.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public interface SeletorConvenioEstagio {

	/**
	 * Método utilizado para definir os horários da turma.
	 * 
	 * @param convenioEstagio
	 * @return
	 */
	public ListaMensagens validaConvenioSelecionado(ConvenioEstagio convenioEstagio);
	
	/**
	 * Seta no controller cliente o convenio de estágio selecionado pelo
	 * usuário, redirecionando-o para o próximo passo.
	 * 
	 * @param convenioEstagio
	 * @return
	 * @throws ArqException 
	 */
	public String selecionaConvenioEstagio(ConvenioEstagio convenioEstagio) throws ArqException;
	
	/**
	 * Retorna o usuário para o passo anterior à da seleção de um convênio de estágio.
	 * 
	 * @return
	 */
	public String seletorConvenioEstagioVoltar();
		
}
