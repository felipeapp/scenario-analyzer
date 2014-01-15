/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.cv.dominio.IndicacaoReferenciaComunidade;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;

/**
 * Managed bean para cadastro de referências para a turma
 * virtual.
 * 
 * @author David Pereira
 *
 */
@Component @Scope("request")
public class IndicacaoReferenciaComunidadeMBean extends CadastroComunidadeVirtual<IndicacaoReferenciaComunidade> {

	/**
	 * Construtor
	 */
	public IndicacaoReferenciaComunidadeMBean() {
		object = new IndicacaoReferenciaComunidade();
	}
	
	/**
	 * Lista as Indicações de Referência.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/IndicacaoReferenciaComunidade/listar.jsp
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@Override
	public List<IndicacaoReferenciaComunidade> lista() throws HibernateException, DAOException {
		
		ComunidadeVirtualMBean cvBean = getMBean("comunidadeVirtualMBean");
		boolean moderador = cvBean.getMembro() != null && cvBean.getMembro().isPermitidoModerar();
		
		return getDAO(ComunidadeVirtualDao.class).findReferenciasTurma(comunidade(),moderador,getUsuarioLogado());
	}
	
	/**
	 * Remove uma Indicação Referência.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/IndicacaoReferenciaComunidade/listar.jsp
	 */
	@Override
	public String remover() {
		try {
			prepare(SigaaListaComando.REMOVER_CV);
			classe = ReflectionUtils.getParameterizedTypeClass(this);
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), classe);
			antesRemocao();
			
			if (object != null) {
				Notification notification = execute(SigaaListaComando.REMOVER_CV, object, getEspecificacaoRemocao());
				if (notification.hasMessages())
					return notifyView(notification);
			}
				
			listagem = null;		
			flash("Referência removida com sucesso.");

		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// força recarregar os tópicos de aulas
		clearCacheTopicosAula();

		return redirect("/cv/" + classe.getSimpleName() + "/listar.jsf");
	}
	
	/**
	 * Método para validar campos obrigatórios.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/IndicacaoReferenciaComunidade/novo.jsp
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				IndicacaoReferenciaComunidade ref = (IndicacaoReferenciaComunidade) objeto;
				if (ref.getTopico() == null || ref.getTopico().getId() == 0)
					notification.addError("É obrigatório informar o tópico da comunidade");
				if (isEmpty(ref.getNome()))
					notification.addError("É obrigatório informar o nome/título");
				if (isEmpty(ref.getTipo()))
					notification.addError("É obrigatório informar o tipo de referência");
				return !notification.hasMessages();
			}
		};
	}

	/**
	 * Abre página para inserir nova Indicação Referência.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/IndicacaoReferenciaComunidade/listar.jsp
	 * @param idTopicoSelecionado
	 */
	public void inserirReferencia(Integer idTopicoSelecionado) {
		object = new IndicacaoReferenciaComunidade();
		object.setTopico(new TopicoComunidade(idTopicoSelecionado));
		forward("/cv/IndicacaoReferenciaComunidade/novo.jsp");
	}

}
