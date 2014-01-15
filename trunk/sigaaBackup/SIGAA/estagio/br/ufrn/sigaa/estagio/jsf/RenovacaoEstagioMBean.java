/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 16/11/2010
 */
package br.ufrn.sigaa.estagio.jsf;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.estagio.RenovacaoEstagioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.ParametrosEstagio;
import br.ufrn.sigaa.estagio.dominio.RenovacaoEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusRenovacaoEstagio;

/**
 * Este MBean tem como finalidade de auxiliar nas operações relacionadas a Renovação do Estágio
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("renovacaoEstagioMBean") @Scope("request")
public class RenovacaoEstagioMBean extends SigaaAbstractController<RenovacaoEstagio>  {
	
	/** Estágio Selecionado */
	private Estagiario estagio;
	
	/** MBean de BuscaEstagio que auxilia nas operações da Renovação */
	@Autowired
	private BuscaEstagioMBean buscaEstagioMBean;	
	
	/** Construtor Padrão */
	public RenovacaoEstagioMBean() { 
		initObj();
	}
	
	/** Inicializa os Objetos */
	private void initObj(){
		obj = new RenovacaoEstagio();
		obj.setEstagio(new Estagiario());
		obj.setStatus(new StatusRenovacaoEstagio());
	}
	
	/**
	 * Inicia a Renovação do Estágio
     * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{

		if (ValidatorUtil.isEmpty(estagio)){
			addMensagemErro("Estágio não selecionado!");
			return null;
		}
		
		RenovacaoEstagioDao dao = getDAO(RenovacaoEstagioDao.class);
		try {
			if (dao.findRenovacaoAbertaByEstagio(estagio) != null){				
				addMensagemErro("Já existe uma Renovação Pendente aguadando o preenchimento dos Relatórios.");
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
				
		validaVigenciaEstagio(estagio.getDataInicio(), estagio.getDataFim());
		
		if (hasErrors())
			return null;
		
		prepareMovimento(SigaaListaComando.RENOVAR_ESTAGIO);
		
		obj.setEstagio(getGenericDAO().refresh(estagio));
		/* Atribui a data fim do estágio */
		obj.setDataFimAnterior(obj.getEstagio().getDataFim());
		
		return forward(getFormPage());
	}
	
	/**
	 * Cadastra a Renovação de Estágio
     * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/renovacao_estagio/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		erros = new ListaMensagens();

		erros.addAll(obj.validate().getMensagens());		
		if (obj.getDataRenovacao() != null)
			validaVigenciaEstagio(obj.getEstagio().getDataInicio(), obj.getDataRenovacao());
		
		if (hasErrors()) 
			return null;
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(getUltimoComando());
		try {
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, TipoMensagemUFRN.INFORMATION);
			return buscaEstagioMBean.filtrar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
	
	/**
	 * Valida os renovação do estágio
	 * @return
	 */
	private void validaVigenciaEstagio(Date dataInicio, Date dataFim){
		erros = new ListaMensagens();
		
		Integer periodoMax = ParametroHelper.getInstance().getParametroInt(ParametrosEstagio.PERIODO_MAX_ESTAGIO);
		
		int difAnos = CalendarUtils.calculaQuantidadeAnosEntreDatasIntervaloFechado(dataInicio, dataFim)-1;
		
		if (periodoMax < difAnos){
			addMensagemErro("Não é possível renovar o Estágio, pois ultrapassou o Prazo máximo de Vigência, " +
					"que é de "+periodoMax+" anos.");
		}
	}	
	
	@Override
	public String getFormPage() {
		return "/estagio/renovacao_estagio/form.jsp";
	}

	public Estagiario getEstagio() {
		return estagio;
	}

	public void setEstagio(Estagiario estagio) {
		this.estagio = estagio;
	}
}
