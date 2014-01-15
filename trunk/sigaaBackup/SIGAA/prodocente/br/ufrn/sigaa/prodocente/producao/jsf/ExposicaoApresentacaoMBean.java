/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/02/2007'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.TipoArtisticoDao;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoArtisticaLiterariaVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
@Component("exposicao")
@Scope("session")
public class ExposicaoApresentacaoMBean
		extends
		AbstractControllerProdocente<ProducaoArtisticaLiterariaVisual> {

	public ExposicaoApresentacaoMBean() {
		obj = new ProducaoArtisticaLiterariaVisual();
		obj.setTipoProducao(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS);
		obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(ProducaoArtisticaLiterariaVisual.class, "id", "descricao");
	}

	/**
	 * Seleciona o tipo artístico a partir da produção
	 * @return
	 */
	public Collection<SelectItem> getTipoArtistico()
	{
		TipoArtisticoDao dao = getDAO(TipoArtisticoDao.class);
		try
		{
			return toSelectItems(dao.findTipoArtisticoByProducao(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS), "id", "descricao");
		} catch(Exception e)
		  {
			e.printStackTrace();
			  return new ArrayList<SelectItem>();
		  }

	}

	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS);
		obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
		obj.setServidor(getServidorUsuario());
		
		super.beforeCadastrarAndValidate();
	}

	@Override
	protected void afterCadastrar() {
		obj = new ProducaoArtisticaLiterariaVisual();
		obj.setTipoProducao(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS);
	}

	@Override
	public void popularObjeto(Producao p) {
		obj =(ProducaoArtisticaLiterariaVisual) p;
	}
	
	@Override
	public String getDirBase(){
		return "/prodocente/producao/ExposicaoApresentacao";
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/listar_producao.jsp</li>
	 *	</ul>
	 */
	public String listar() {

		resetBean("paginacao");
		setDirBase("/prodocente/producao/");
		getCurrentSession().setAttribute("backPage", "ExposicaoApresentacao/lista.jsp");
		return forward("/prodocente/producao/ExposicaoApresentacao/lista.jsp");

	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/ExposicaoApresentacao/lista.jsp</li>
	 *		<li>sigaa.war/prodocente/nova_producao.jsp</li>
	 *	</ul>
	 */
	public String preCadastrar() throws ArqException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		setDirBase("/prodocente/producao/");
		return forward("/prodocente/producao/ExposicaoApresentacao/form.jsp");
	}
	
	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/ExposicaoApresentacao/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		 
		 setId();
		 PersistDB obj = this.obj;
		 ProducaoArtisticaLiterariaVisual producao = (ProducaoArtisticaLiterariaVisual) getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
		 
		if (producao == null || producao.getId() == 0) {
			addMensagemErro("O registro informado não encontra-se disponível para atualização");
			return null;
		}
		 
		return super.atualizar();
	}
	
	public Collection<Object> getAll() throws SegurancaException {

		setTamanhoPagina(20);
		ProducaoDao dao = getDAO(ProducaoDao.class);

		checkDocenteRole();

		try {
			return dao.findByTipoProducaoServidor(ProducaoArtisticaLiterariaVisual.class,getServidorUsuario() ,TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS, getPaginacao());
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<Object>();
		}

	}
	
	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS);
	}

}
