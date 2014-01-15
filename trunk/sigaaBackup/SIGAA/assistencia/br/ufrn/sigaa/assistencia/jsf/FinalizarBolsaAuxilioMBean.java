package br.ufrn.sigaa.assistencia.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dao.FinalizarBolsaAuxilioDao;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioPeriodo;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;

@Component
@Scope("request")
public class FinalizarBolsaAuxilioMBean extends
		SigaaAbstractController<BolsaAuxilioPeriodo> {

	private Integer ano;
	private Integer periodo;
	private Collection<BolsaAuxilioPeriodo> bolsasAuxilio;

	public FinalizarBolsaAuxilioMBean() {
		clear();
	}

	private void clear() {
		obj = new BolsaAuxilioPeriodo();
		obj.setBolsaAuxilio(new BolsaAuxilio());
		obj.getBolsaAuxilio().setTipoBolsaAuxilio(new TipoBolsaAuxilio());
	}

	@Override
	public String getDirBase() {
		return "/sae/FinalizacaoBolsista";
	}

	public String iniciarFinalizacaoBolsistas() throws ArqException {
		clear();
		prepareMovimento(SigaaListaComando.FINALIZAR_BOLSISTAS);
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		return forward(getFormPage());
	}

	@Override
	public String buscar() throws Exception {
		if ( ano == null || periodo == null )
			ValidatorUtil.validateRequired(null, "Ano-Período", erros);
		if ( hasOnlyErrors() ) return null;
		
		FinalizarBolsaAuxilioDao dao = getDAO(FinalizarBolsaAuxilioDao.class);
		try {
			bolsasAuxilio = dao.findBolsistasAtivos(ano, periodo, obj
					.getBolsaAuxilio().getTipoBolsaAuxilio().getId());
			setOperacaoAtiva(SigaaListaComando.FINALIZAR_BOLSISTAS.getId());
		} finally {
			dao.close();
		}

		if (bolsasAuxilio.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}

		return forward(getDirBase() + "/finalizar.jsp");
	}

	public String finalizarBolsistas() throws ArqException {
		try {
			if ( !checkOperacaoAtiva(SigaaListaComando.FINALIZAR_BOLSISTAS.getId()) )
				return cancelar();
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(null);
			mov.setObjAuxiliar(bolsasAuxilio);
			mov.setRegistroEntrada(getRegistroEntrada());
			mov.setCodMovimento(SigaaListaComando.FINALIZAR_BOLSISTAS);
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO,"Finalização");
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		return iniciarFinalizacaoBolsistas();
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Collection<BolsaAuxilioPeriodo> getBolsasAuxilio() {
		return bolsasAuxilio;
	}

	public void setBolsasAuxilio(Collection<BolsaAuxilioPeriodo> bolsasAuxilio) {
		this.bolsasAuxilio = bolsasAuxilio;
	}

}