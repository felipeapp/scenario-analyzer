/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.VisitaCientifica;

/**
 * Managed Bean para o caso de uso de Visita Científica da Produção Intelectual
 * 
 * Gerado pelo CrudBuilder
 */
@Component("visitaCientifica")
@Scope("session")
public class VisitaCientificaMBean extends AbstractControllerProdocente<VisitaCientifica> {

	private String dataInicio;
	private String dataFim;
	private SimpleDateFormat df = new SimpleDateFormat("MM/yyyy");

	public VisitaCientificaMBean() {
		super();
		clear();
	}

	private void clear() {
		obj = new VisitaCientifica();
		obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
		obj.getIes().setId(InstituicoesEnsino.UFRN); 
		obj.setDepartamento(getServidorUsuario().getUnidade());
		dataInicio = "";
		dataFim = "";
	}

	/** 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/atividades/FinanciamentoVisitaCientifica/form.jsp
	 * /prodocente/atividades/FinanciamentoVisitaCientifica/view.jsp
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(VisitaCientifica.class, "id", "titulo");
	}

	/**
	 * Método não invocado por JSP´s
	 */
	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.VISITA_CIENTIFICA);
		obj.setServidor(getServidorUsuario());
	}

	@Override
	protected void afterCadastrar() {
		clear();
	}

	/**
	 * Método não invocado por JSP´s
	 */
	@Override
	public void popularObjeto(Producao p) {
		obj = (VisitaCientifica) p;
	}

	/**
	 * Método não invocado por JSP´s
	 */
	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.VISITA_CIENTIFICA);
	}
		
	/**
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/VisitaCientifica/lista.jsp
	 * /prodocente/nova_producao.jsp
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		clear();
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		setDirBase("/prodocente/producao/");
		if (verificaBloqueio()) {
			return forward("/prodocente/aviso_bloqueio.jsp");
		} else {
			return forward(getFormPage());
		}		
	}
	
	/**
	 * Alterar a opção da Instituição para outros.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/VisitaCientifica/form.jsp
	 * 
	 * @param event
	 * 
	 */
	public void alterarStatusInst(ActionEvent event){
		if (obj.getIes().getId() == 0)
			obj.setOutraInstituicao(true);
		else
			obj.setOutraInstituicao(false);
	}
	
	/**
	 * Realizar o cadastro de uma visita científica a uma Instituição.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/VisitaCientifica/form.jsp
	 * 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		Calendar calendar = Calendar.getInstance();
		int mes, ano;
		if (dataInicio == null || "".equals(dataInicio) || !dataInicio.contains("/")) 
			obj.setPeriodoInicio(null);
		else {
			mes = Integer.parseInt(dataInicio.split("/")[0]);
			ano = Integer.parseInt(dataInicio.split("/")[1]);
			if (mes > 12)
				obj.setPeriodoInicio(null);
			else {
				calendar.set(ano, --mes, 1);
				obj.setPeriodoInicio(calendar.getTime());
			}
		}
		if (dataFim == null || "".equals(dataFim) || !dataFim.contains("/")) 
			obj.setPeriodoFim(null);
		else {
			mes = Integer.parseInt(dataFim.split("/")[0]);
			ano = Integer.parseInt(dataFim.split("/")[1]);
			if (mes > 12)
				obj.setPeriodoFim(null);
			else {
				calendar.set(ano, --mes, 1);
				obj.setPeriodoFim(calendar.getTime());
			}
		}
		String result = super.cadastrar();
		if (obj.getIes() == null) {
			obj.setIes(new InstituicoesEnsino()) ;
			obj.getIes().setId(0);
		}
		return result;
	}

	/**
	 * @param dataInicio the dataInicio to set
	 */
	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	/**
	 * @return the dataInicio
	 */
	public String getDataInicio() {
		return obj != null && obj.getPeriodoInicio() != null ? df.format(obj.getPeriodoInicio()) : dataInicio;
	}

	/**
	 * @param dataFim the dataFim to set
	 */
	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}

	/**
	 * @return the dataFim
	 */
	public String getDataFim() {
		return obj != null && obj.getPeriodoFim() != null ? df.format(obj.getPeriodoFim()) : dataFim;
	}

}
