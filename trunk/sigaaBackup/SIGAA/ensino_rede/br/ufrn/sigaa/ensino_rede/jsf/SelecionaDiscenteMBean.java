package br.ufrn.sigaa.ensino_rede.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateCPF_CNPJ;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dao.DiscenteAssociadoDao;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;

@SuppressWarnings("serial")
@Component("selecionaDiscenteMBean") @Scope("session")
public class SelecionaDiscenteMBean extends EnsinoRedeAbstractController<DiscenteAssociado> {

	private static final String LISTA_DISCENTES = "/ensino_rede/discente/lista.jsp";

	private ParametrosSelecionaDiscente parametros;
	private ValoresSelecionaDiscente valores;
	private SelecionaDiscente requisitor;
	private CampusIes campusRestrito;
	
	public SelecionaDiscenteMBean() {
		clear();
	}

	private void clear() {
		obj = new DiscenteAssociado();
		parametros = new ParametrosSelecionaDiscente();
		valores = new ValoresSelecionaDiscente();
		setResultadosBusca(new ArrayList<DiscenteAssociado>());
		// fixo para todas consultas: programa em rede
	}
	
	public String executar() throws DAOException, NegocioException {
		return executar(null);
	}
	
	public String executar(CampusIes campusRestrito) throws DAOException, NegocioException {
		this.campusRestrito = campusRestrito;
		if (!isEmpty(campusRestrito)) {
			parametros.setCheckCampus(true);
			valores.setValorIdCampus(campusRestrito.getId());
		}else{
			valores.setValorIdCampus(0);
			parametros.setCheckCampus(false);
		}
		cleanFiltros();
		return forward(LISTA_DISCENTES);
	}
	
	/**
	 * Método utilizado pra limpar os dados do filtro
	 * Não invocado por JSP.
	 */
	public void cleanFiltros(){
		setResultadosBusca(new ArrayList<DiscenteAssociado>());
		parametros.setCheckCpf(false);
		parametros.setCheckNome(false);
		valores.setValorCpf( null );
		valores.setValorNome("");
		
	}

	public String buscarDiscentes() throws DAOException {
		validaParametrosBusca();
		if (hasErrors()) return null;
		
		DiscenteAssociadoDao dao = getDAO(DiscenteAssociadoDao.class);
		List<DiscenteAssociado> docentes = dao.findByParametros(getProgramaRede(), parametros, valores);

		setResultadosBusca(docentes);

		if (isEmpty(getResultadosBusca())) {
			addMensagemErro("Nenhum discente foi encontrado.");
			return null;
		}

		return forward(LISTA_DISCENTES);
	}	
	
	public String voltar() {
		return forward(LISTA_DISCENTES);
	}
	
	private void validaParametrosBusca() {
		
		if( !parametros.isCheckCpf() && !parametros.isCheckNome() &&  !parametros.isCheckCampus()  ){
			erros.addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}
		if (parametros.isCheckCpf())
			if(ValidatorUtil.isNotEmpty( valores.getValorCpf() ) ){
				validateCPF_CNPJ(valores.getValorCpf(), "CPF", erros);
			}else{
				erros.addErro("Valor de CPF inválido");
			}
		
		if (parametros.isCheckNome())
			validateRequired(valores.getValorNome(), "Nome", erros);
		if (parametros.isCheckCampus())
			validateRequiredId(valores.getValorIdCampus(), "Campus", erros);
		if (parametros.isCheckProgramaRede())
			validateRequiredId(valores.getValorIdProgramaRede(), "Programa em Rede", erros);
	}

	public void escolheDiscente(ActionEvent evt) throws ArqException {

		Integer id = (Integer) evt.getComponent().getAttributes().get("idDiscente");
		escolheDiscente(id);

	}	
	
	private void escolheDiscente(Integer id) throws ArqException {
		DiscenteAssociadoDao dao = getDAO(DiscenteAssociadoDao.class);
		obj = dao.findByPrimaryKey(id, DiscenteAssociado.class);

		requisitor.setDiscente(obj);
		requisitor.selecionaDiscente();
	}
	
	public ParametrosSelecionaDiscente getParametros() {
		return parametros;
	}

	public void setParametros(ParametrosSelecionaDiscente parametros) {
		this.parametros = parametros;
	}

	public ValoresSelecionaDiscente getValores() {
		return valores;
	}

	public void setValores(ValoresSelecionaDiscente valores) {
		this.valores = valores;
	}

	public SelecionaDiscente getRequisitor() {
		return requisitor;
	}

	public void setRequisitor(SelecionaDiscente requisitor) {
		this.requisitor = requisitor;
	}

	public CampusIes getCampusRestrito() {
		return campusRestrito;
	}

	public boolean isRestringeCampus() {
		return campusRestrito != null;
	}
	
	public void filterByStatus(List<StatusDiscenteAssociado> status) {
		parametros.setCheckStatusDiscente(true);
		valores.setStatus(status);
	}
}
