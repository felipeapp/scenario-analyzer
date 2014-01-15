package br.ufrn.sigaa.ensino.latosensu.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.WordUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.SituacaoPropostaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;

/**
 * Managed Bean das Situa��es da Proposta de Curso Lato Sensu, respons�vel pela realiza��o das sequintes
 * opera��es cadastro, atualiza��o, remo��o de um discente.
 * 
 * @author Jean Guerethes
 */
@Component("situacaoPropostaMBean") @Scope("request")
public class SituacaoPropostaMBean extends SigaaAbstractController<SituacaoProposta> {

	/** Construtor padr�o. */
	public SituacaoPropostaMBean(){
		obj = new SituacaoProposta();
	}

	/** 
	 * M�todo respons�vel pelo cadastro de uma nova situa��o Proposta. 
	 *  
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/situacao_proposta/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<SituacaoProposta> mesmoNome = dao.findByExactField(SituacaoProposta.class, "descricao", obj.getDescricao());

		for (SituacaoProposta sp : mesmoNome) {
			if (sp.getDescricao().equals(obj.getDescricao()) && sp.getId() != obj.getId()) {
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Situa��o de Proposta");
				return null;
			}
		}
		obj.setDescricao(WordUtils.capitalize(obj.getDescricao()));		
		super.cadastrar();
		return forward(getListPage());
	}
	
	/**
	 * M�todo faz uma verifica��o 
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/alterar_situacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String atualizar() throws ArqException {
		carregarObj();
		if (obj == null || obj.getId() == 0) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return redirectJSF(getListPage());
		}
		prepareMovimento(ArqListaComando.ALTERAR);
		return super.atualizar();
	}

	/**
	 * M�todo respons�vel pela inativa��o de uma situa��o proposta
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/situacao_proposta/lista.jsp
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		carregarObj();
		if (obj == null || obj.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return redirectJSF(getListPage());
		}
		
		SituacaoPropostaDao dao = getDAO(SituacaoPropostaDao.class);
		try {
			boolean passivelDeInativacao = dao.haPropostaSituacao(obj);
			if (passivelDeInativacao) {
				addMensagem(MensagensArquitetura.REMOCAO_OBJETO_ASSOCIADO);
				return redirectJSF(getListPage());
			}
		} finally {
			dao.close();
		}
		
		prepareMovimento(ArqListaComando.DESATIVAR);
		return super.inativar();
	}
	
	/**
	 * M�todo tem como finalidade carregar todas as informa��es da Situa��o Proposta.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/situacao_proposta/lista.jsp
	 */
	public SituacaoProposta carregarObj() throws DAOException{
		int id = getParameterInt("id",0);
		return obj = getGenericDAO().findByPrimaryKey(id, SituacaoProposta.class);
	}

	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems(getAllAtivos(), "id", "descricao");
	}
	
	public Collection<SelectItem> getAllSituacoesValidas() throws ArqException {
		return toSelectItems(getDAO(SituacaoPropostaDao.class).situacoesValidas(), "id", "descricao");
	}
	
	@Override
	protected void beforeAtualizar() throws ArqException {
		carregarObj();
	}

	/**
	 * M�todo respons�vel por carregar a Situa��o Proposta antes de remover.
	 * 
	 * JSP: N�o invocado por jsp.	 
	 */
	@Override
	public void beforeRemover() throws DAOException {
		carregarObj();
	}
	
	@Override
	public String getDirBase() {
		return "/lato/situacao_proposta";
	}

}