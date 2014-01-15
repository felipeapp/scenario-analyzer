/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Interface entre dois controlers (MBeans) para a sele��o de um convo�nio de
 * est�gio.<br/>
 * O controller solicitante dever� implementar esta interface. Para selecionar
 * um conv�nio, o controller cliente dever� chamar a busca por conv�nio da
 * seguinte forma: 
 * <pre>
		StatusConvenioEstagio[] statusConvenioEstagio = {StatusConvenioEstagio.APROVADO};
		ConvenioEstagioMBean convenioEstagioMBean = getMBean("convenioEstagioMBean");
		return convenioEstagioMBean.iniciarSeletorEstagio(this, statusConvenioEstagio);
 * </pre> 
 * Quando o usu�rio selecionar o est�gio, o controller
 * ir� chamar o cliente para validar o conv�nio selecionado (m�todo
 * {@link #validaConvenioSelecionado(ConvenioEstagio)}). Se n�o houver mensagem
 * de erro, setar� o conv�nio selecionado no controller solicitante e passar� o
 * fluxo para este.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
public interface SeletorConvenioEstagio {

	/**
	 * M�todo utilizado para definir os hor�rios da turma.
	 * 
	 * @param convenioEstagio
	 * @return
	 */
	public ListaMensagens validaConvenioSelecionado(ConvenioEstagio convenioEstagio);
	
	/**
	 * Seta no controller cliente o convenio de est�gio selecionado pelo
	 * usu�rio, redirecionando-o para o pr�ximo passo.
	 * 
	 * @param convenioEstagio
	 * @return
	 * @throws ArqException 
	 */
	public String selecionaConvenioEstagio(ConvenioEstagio convenioEstagio) throws ArqException;
	
	/**
	 * Retorna o usu�rio para o passo anterior � da sele��o de um conv�nio de est�gio.
	 * 
	 * @return
	 */
	public String seletorConvenioEstagioVoltar();
		
}
