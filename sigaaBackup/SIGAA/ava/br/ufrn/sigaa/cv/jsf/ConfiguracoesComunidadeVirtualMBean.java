package br.ufrn.sigaa.cv.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.cv.dominio.ConfiguracoesComunidadeVirtual;
import br.ufrn.sigaa.cv.negocio.MovimentoCadastroCv;

/**
 * Managed bean para gerenciar as configurações de uma comunidade virtual
 * 
 * @author Diego Jácome
 */
@Component("configuracoesComunidadeVirtual") @Scope("request")
public class ConfiguracoesComunidadeVirtualMBean extends CadastroComunidadeVirtual<ConfiguracoesComunidadeVirtual>{
	
	public ConfiguracoesComunidadeVirtualMBean (){
		object = new ConfiguracoesComunidadeVirtual();
	}
	
	private void init() throws DAOException {	
		object =  getGenericDAO().findByExactField(ConfiguracoesComunidadeVirtual.class, "comunidade.id", comunidade().getId(),true);
		
		if ( object == null ) {
			object  = new ConfiguracoesComunidadeVirtual();
			object.setComunidade(comunidade());
		}	
	}

	
	public String gerenciarTopicos() throws DAOException{
		init();
		return forward("/cv/TopicoComunidade/gerenciarTopico.jsf");
	}
	
	/**
	 * Retorna as possíveis formas de lançar notas para um questionário que foi respondido mais de uma vez.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> sigaa.war/ava/QuestionarioTurma/formDadosQuestionario.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getComboOrdemTopicos() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(Character.valueOf('D'), "Cronológica Decrescente"));
		result.add(new SelectItem(Character.valueOf('C'), "Cronológica Crescente"));
		result.add(new SelectItem(Character.valueOf('L'), "Livre"));
		return result;
	}
	
	public void salvar(ValueChangeEvent e) throws ArqException, NegocioException {
		
		TopicoComunidadeMBean tcMBean = getMBean("topicoComunidadeMBean");
		Character c = (Character) e.getNewValue();
		
		if ( c.equals(ConfiguracoesComunidadeVirtual.ORDEM_TOPICO_DECRESCENTE) )
			object.setOrdemTopico(ConfiguracoesComunidadeVirtual.ORDEM_TOPICO_DECRESCENTE);
		if ( c.equals(ConfiguracoesComunidadeVirtual.ORDEM_TOPICO_CRESCENTE) )
			object.setOrdemTopico(ConfiguracoesComunidadeVirtual.ORDEM_TOPICO_CRESCENTE);
		if ( c.equals(ConfiguracoesComunidadeVirtual.ORDEM_TOPICO_LIVRE) )
			object.setOrdemTopico(ConfiguracoesComunidadeVirtual.ORDEM_TOPICO_LIVRE);
		
		prepare(SigaaListaComando.SALVAR_CONFIGURACOES_CV);
		MovimentoCadastroCv mov = new MovimentoCadastroCv();
		mov.setCodMovimento(SigaaListaComando.SALVAR_CONFIGURACOES_CV);
		mov.setObjMovimentado(object);
		mov.setComunidade(comunidade());
		
		execute(mov);
		
		tcMBean.setListagem(null);
	}
	
	/**
	 * Encaminha o usuário para a página padrão após um cadastro.
	 * @return
	 */
	@Override
	public String forwardCadastrar() {
		return forward("/cv/TopicoComunidade/gerenciarTopico.jsf");
	}
	
	/**
	 * Encaminha o usuário para a página padrão após a atualização.
	 * @return
	 */
	@Override
	public String forwardAtualizar() {
		return forward("/cv/TopicoComunidade/gerenciarTopico.jsf");
	}
	
	@Override
	public List<ConfiguracoesComunidadeVirtual> lista() throws DAOException {	
		return null;
	}
}
