/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/08/2009
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.pesquisa.TipoBolsaPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.CategoriaBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;

/**
 * Managed Bean para operações de tipo de bolsa de pesquisa
 * 
 * @author Leonardo Campos
 *
 */
@Component("tipoBolsaPesquisa") @Scope("request")
public class TipoBolsaPesquisaMBean extends SigaaAbstractController<TipoBolsaPesquisa> {

	/** coleção de tipos níveis permitidos*/
	private Collection<String> tipoNiveis = new ArrayList<String>();
	
	/** Construtor Padrão */
	public TipoBolsaPesquisaMBean(){
		obj = new TipoBolsaPesquisa();
	}

	/**
	 * Gera um selectItems de CategoriaBolsaPesquisa
	 * <br/>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/TipoBolsaPesquisa/form.jsp 
	 */
	public Collection<SelectItem> getCategoriasCombo(){
		return toSelectItems(CategoriaBolsaPesquisa.getCategorias());
	}
	
	/**
	 * Gera um selectItems de pagasSipac 
	 * <br/>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/homologacao_bolsas_sipac/lista_finalizacao.jsp
	 */
	public Collection<SelectItem> getPagasSipacCombo() throws DAOException {
		return toSelectItems(getDAO(TipoBolsaPesquisaDao.class).findPagasSipac(), "id", "descricaoResumida");
	}
	
	/**
	 * Serve pra direcionar o usuário para a tela do formulário
	 * <br/>
	 * JSP: Não invocado por jsp.
	 */
	@Override
	public String getFormPage() {
		return "/pesquisa/TipoBolsaPesquisa/form.jsf";
	}
	
	/**
	 * Serve pra direcionar o usuário pra a listagem.
	 * <br/>
	 * JSP: Não invocado por jsp.
	 */
	@Override
	public String getListPage() {
		return "/pesquisa/TipoBolsaPesquisa/lista.jsf";
	}

	/**
	 * Retora a coleção de registros armazenados no banco por ordem decrescente de data fim do período de validade da cota.
	 * Invocado na JSP:
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/TipoBolsaPesquisa/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public Collection<TipoBolsaPesquisa> getAllPaginado() throws ArqException {
		TipoBolsaPesquisaDao dao = getDAO(TipoBolsaPesquisaDao.class);
		return dao.findByExactField(TipoBolsaPesquisa.class, "ativo", Boolean.TRUE, getPaginacao(), "asc", "descricao");
	}
	
	/** Atualizar tipo de bolsa de pesquisa 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/pesquisa/TipoBolsaPesquisa/lista.jsp</li>
	 * </ul>
	 * */
	@Override
	public String atualizar() throws ArqException {

		GenericDAO dao = new GenericDAOImpl(getSistema(),
				getSessionRequest());
		try {

			beforeAtualizar();
			
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			prepareMovimento(ArqListaComando.ALTERAR);

			setId();
			setReadOnly(false);

			this.obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
			
			String [] niveis = obj.getNiveisPermitidos().trim().split(",");
			
			for (int i = 0; i < niveis.length; i++){
				tipoNiveis.add(niveis[i]);
			}

			setConfirmButton("Alterar");
			afterAtualizar();

		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return forward(getFormPage());
	}
	
	/** Cadastrar tipo de bolsa de pesquisa 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/pesquisa/TipoBolsaPesquisa/form.jsp</li>
	 * </ul>
	 * */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		//verifica duplicidade
		if(obj.getDescricao() != null && getDAO(TipoBolsaPesquisaDao.class).existsTipoBolsa(obj))
			addMensagemErro("Já existe um tipo de bolsa "+ obj.getDescricaoCompleta() +" cadastrado.");
		//verifica campo obrigatório: níveis permitidos
		if(tipoNiveis.isEmpty()){
			addMensagemErro("Níveis Permitidos: Campo obrigatório não informado.");
		}
		if(!tipoNiveis.isEmpty()){
			this.obj.setNiveisPermitidos(gerarNiveisString(tipoNiveis));
		}
		return super.cadastrar();
	}
	
	@Override
	protected void beforeInativar() {
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}
	}
	
	/**
	 * Remove o bean da sessão e redireciona o usuário para
	 * a página inicial do subsistema atual.
	 * Invocado na JSP:
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/TipoBolsaPesquisa/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		// removendo da sessao
		resetBean();
		
		try {
			if(!getUltimoComando().equals(ArqListaComando.CADASTRAR))
				return listar();
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		
		return null;
	}
	
	/** gerar expressão para níveis de permissão.
	 * Não invocado por JSP'S
	 *  */
	public String gerarNiveisString(Collection<?> objs) {
		ArrayList<String> vals = new ArrayList<String>(0);
		for (Object o : objs) {
			if (o instanceof PersistDB) {
				PersistDB p = (PersistDB) o;
				vals.add(p.getId() + "");
			} else if (o instanceof String || o instanceof Character || o instanceof Date) {
				vals.add((String) o);
			} else {
				vals.add(o + "");
			}
		}
		StringBuilder in = new StringBuilder();
		for (int i = 0; i < vals.size(); i++) {
			in.append(vals.get(i));
			if (i < (vals.size() - 1)) {
				in.append(",");
			}
		}
		return in.toString();
	}
	
	public Collection<SelectItem> getAllBolsasRelatorioCombo() throws ArqException {
		return toSelectItems(TipoBolsaPesquisa.BOLSAS_RELATORIO_CNPQ);
	}

	public Collection<String> getTipoNiveis() {
		return tipoNiveis;
	}

	public void setTipoNiveis(Collection<String> tipoNiveis) {
		this.tipoNiveis = tipoNiveis;
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		setLabelCombo("descricao");
		return super.getAllCombo();
	}
	
	public Collection<SelectItem> getAllAtivosCombo() throws ArqException {
		setLabelCombo("descricao");
		return toSelectItems(getAllAtivos(),"id","descricaoResumida");
	}
	
}