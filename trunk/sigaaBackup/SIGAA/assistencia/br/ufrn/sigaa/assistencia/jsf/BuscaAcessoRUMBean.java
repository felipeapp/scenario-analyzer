/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 27/02/2009
 *
 */	
package br.ufrn.sigaa.assistencia.jsf;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.sae.RegistroAcessoRUDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.restaurante.dominio.RegistroAcessoRU;
import br.ufrn.sigaa.assistencia.restaurante.dominio.TipoLiberacaoAcessoRU;
import br.ufrn.sigaa.mensagens.MensagensAssistencia;

/**
 * MBean respons�vel por gerar o relat�rio de acesso ao RU, ou seja,
 * todas as pessoas que passaram no RU atrav�s da catraca instalada.
 * 
 * @author agostinho
 *
 */
@Component("buscaAcessoRUMBean") 
@Scope("request")
public class BuscaAcessoRUMBean extends SigaaAbstractController<BolsaAuxilio> {

	/**
	 * Tipos de libera��o que podem ser feitas pela catraca.
	 */
	private Collection<SelectItem> allTiposLiberacaoCatraca;
	/**
	 * Tipos de bolsa existentes 
	 */
	private Collection<SelectItem> allTiposBolsas;
	
	/**
	 * Listagem usada para exibir relat�rio 
	 */
	private List<RegistroAcessoRU> listagem;
	
	private Date dataInicio = new Date();
	private Date dataFinal = new Date();
	
	private TipoBolsaAuxilio tipoBolsaAuxilio = new TipoBolsaAuxilio();
	private TipoLiberacaoAcessoRU tipoLiberacaoAcessoRU = new TipoLiberacaoAcessoRU();
	
	/**
	 * Construtor
	 */
	public BuscaAcessoRUMBean() {
		tipoBolsaAuxilio = new TipoBolsaAuxilio();
		tipoLiberacaoAcessoRU = new TipoLiberacaoAcessoRU();
	}
	
	/**
	 *  Abre o form de consulta do relat�rio
	 *  <br/><br/>
	 * 	M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li>/sigaa.war/sae/menu.jsp </ul>
	 */
	public String iniciarRelatorioAcessoRU() throws SQLException {
		return forward("/sae/busca_acessos_ru.jsp");
	}
	
	/**
	 * Realiza a busca de acordo com o tipo da bolsa e o tipo de libera��o que foi concedido.
	 *  <br/><br/>
	 * 	M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul> <li>/sigaa.war/sae/busca_acessos_ru.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String consultarAcessoRUCatraca() throws DAOException {
		
		RegistroAcessoRUDao dao = getDAO(RegistroAcessoRUDao.class);

		if (validarData()) {
			listagem = dao.relatorioAcessoRUCatraca(tipoBolsaAuxilio, tipoLiberacaoAcessoRU, dataInicio, dataFinal);
				if (listagem != null && listagem.size() != 0) {
					tipoLiberacaoAcessoRU = listagem.get(0).getTipoLiberacao(); // mesmo existindo muito registros, o tipo de libera��o � o mesmo para todos.
					tipoBolsaAuxilio = getGenericDAO().findByPrimaryKey(tipoBolsaAuxilio.getId(), TipoBolsaAuxilio.class);
						if (tipoBolsaAuxilio == null) {
							tipoBolsaAuxilio = new TipoBolsaAuxilio();
							tipoBolsaAuxilio.setDenominacao("TODOS");
						}
				}
		} else {
			addMensagem(MensagensAssistencia.DATA_INICIAL_MAIOR_DATA_FINAL);
			return forward("/sae/busca_acessos_ru.jsp");
		}
		
		return forward("/sae/relatorios/relatorio_acesso_ru.jsp");
	}
	
	private boolean validarData() {
		
		if (ValidatorUtil.isEmpty(dataInicio) || ValidatorUtil.isEmpty(dataFinal))
			return false;
		
		if ( !CalendarUtils.isDentroPeriodo(dataInicio, dataFinal) )
			return false;
		
		return true;
	}

	/**
	 * Gera uma Collection<SelectItem> para ser usada no Combobox de Tipos de Bolsa
	 *  <br/><br/>
	 * 	M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> /sigaa.war/sae/busca_acessos_ru.jsp </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTiposBolsasCombo() throws DAOException {
		return getAll(TipoBolsaAuxilio.class, "id", "denominacao");
	}
	
	/**
	 * Gera uma Collection<SelectItem> para ser usada no Combobox de Tipos de Libera��o
	 *  <br/><br/>
	 * 	M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li>/sigaa.war/sae/busca_acessos_ru.jsp </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTiposLiberacaoCatracaCombo() throws DAOException {
		RegistroAcessoRUDao dao = getDAO(RegistroAcessoRUDao.class);
		return toSelectItems(dao.getAllTipoLiberacaoCatraca(), "id", "descricao");
	}

	public Collection<SelectItem> getAllTiposLiberacaoCatraca() {
		return allTiposLiberacaoCatraca;
	}
	
	public TipoBolsaAuxilio getTipoBolsaAuxilio() {
		return tipoBolsaAuxilio;
	}

	public void setTipoBolsaAuxilio(TipoBolsaAuxilio tipoBolsaAuxilio) {
		this.tipoBolsaAuxilio = tipoBolsaAuxilio;
	}

	public TipoLiberacaoAcessoRU getTipoLiberacaoAcessoRU() {
		return tipoLiberacaoAcessoRU;
	}

	public void setTipoLiberacaoAcessoRU(TipoLiberacaoAcessoRU tipoLiberacaoAcessoRU) {
		this.tipoLiberacaoAcessoRU = tipoLiberacaoAcessoRU;
	}

	public void setAllTiposLiberacaoCatraca(
			Collection<SelectItem> allTiposLiberacaoCatraca) {
		this.allTiposLiberacaoCatraca = allTiposLiberacaoCatraca;
	}

	public Collection<SelectItem> getAllTiposBolsas() {
		return allTiposBolsas;
	}

	public void setAllTiposBolsas(Collection<SelectItem> allTiposBolsas) {
		this.allTiposBolsas = allTiposBolsas;
	}

	public List<RegistroAcessoRU> getListagem() {
		return listagem;
	}

	public void setListagem(List<RegistroAcessoRU> listagem) {
		this.listagem = listagem;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
}
