/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '08/12/2009'
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.jsf.NotificacoesMBean;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean respons�vel por listar os interessados de uma bolsa de pesquisa e integrar com o formul�rio de envio de mensagem
 * 
 * @author Henrique Andr�
 *
 */
@Component("enviarMsgInteressados") @Scope("request")
public class EnviarMensagemInteressadosMBean extends NotificacoesMBean {
	
	/**
	 * Direciona o usuario para a pagina onde contem a listagem
	 * JSP: N�o invocado por JSP
	 */
	public String iniciar() throws ArqException {
		return redirect(getFormPage());
	}

	/**
	 * Metodo invocado durante a renderiza��o da view
	 * JSP: /geral/mensagem/enviar_mensagem_interessados.jsp
	 */
	@Override
	public String getIniciarFormulario() throws ArqException {
		clear();
		return super.getIniciarFormulario();
	}
	
	/**
	 * Abre o hist�rico do discente selecionado
	 * JSP: /geral/mensagem/enviar_mensagem_interessados.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String detalhesDiscente() throws ArqException, NegocioException {
		Integer id = getParameterInt("id");
		if (id == null) throw new NegocioException("Nenhum discente foi selecionado");
		
		Discente discente = getGenericDAO().findByPrimaryKey(id, Discente.class);
		
		HistoricoMBean historico = new HistoricoMBean();
		historico.setDiscente(discente);
		return historico.selecionaDiscente();
	}	
	
	/**
	 * Define a pagina que contem o formul�rio
	 */
	@Override
	public String getFormPage() {
		return "/geral/mensagem/enviar_mensagem_interessados.jsf";
	}	
	
	/**
	 * Retorna os alunos interessados
	 * 
	 * JSP: /geral/mensagem/enviar_mensagem_interessados.jsp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getInteressados() {
		return (List<Map<String, Object>>) getCurrentSession().getAttribute("alunosInteressados");
	}
	
}
