/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/03/2013
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.tecnico.dao.DadosDiscentesIMDDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;

/**
 * Controlador responsável por permitir aos gestores do IMD visualizar os dados dos discentes.
 * 
 * @author Fred_Castro
 * 
 */
@Component("consultaDadosDiscentesIMD") @Scope("request")
public class ConsultaDadosDiscentesIMDMBean extends SigaaAbstractController<Object> {
	
	private String nomeBusca;
	private Long matriculaBusca;
	private String cpfBusca;
	private Integer opcaoPoloGrupoBusca;
	private Integer statusBusca;
	
	private boolean filtroNome;
	private boolean filtroMatricula;
	private boolean filtroCpf;
	private boolean filtroOpcaoPoloGrupo;
	private boolean filtroStatus;
	
	private List <DiscenteTecnico> discentes;
	private ConvocacaoProcessoSeletivoDiscenteTecnico discente;
	private List <SelectItem> opcoesPoloGrupo;
	
	public String iniciarBusca () {
		return forward ("/tecnico/cadastramento_discente/form_dados_discentes.jsf");
	}
	
	public String buscar () throws DAOException, NumberFormatException {
		
		DadosDiscentesIMDDao dao = null;
		
		if (filtroNome && StringUtils.isEmpty(nomeBusca))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
		if (filtroMatricula && matriculaBusca == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Matrícula");
		if (filtroCpf && StringUtils.isEmpty(cpfBusca))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Cpf");
		
		if (!filtroNome && !filtroMatricula && !filtroCpf && !filtroOpcaoPoloGrupo && !filtroStatus)
			addMensagemErro("Você deve preencher pelo menos um campo.");
		
		if (!hasErrors()){
			try {
				dao = getDAO(DadosDiscentesIMDDao.class);
				discentes = dao.findDiscentesByNomeMatriculaCPF(filtroNome ? nomeBusca : null, filtroMatricula ? matriculaBusca : null, filtroCpf ? StringUtils.isEmpty(cpfBusca) ? null : Long.parseLong(("0" + cpfBusca).replaceAll("\\-", "").replaceAll("\\.", "")) : null, filtroOpcaoPoloGrupo ? opcaoPoloGrupoBusca : 0, filtroStatus ? statusBusca : 0);
			} finally {
				if (dao != null)
					dao.close();
			}
		} else
			discentes = new ArrayList <DiscenteTecnico> ();
		
		return forward ("/tecnico/cadastramento_discente/form_dados_discentes.jsf");
	}
	
	public String visualizarDiscente () throws DAOException {
		Integer idDiscente = getParameterInt("idDiscente");
		DadosDiscentesIMDDao dao = null;
		
		try {
			dao = getDAO(DadosDiscentesIMDDao.class);
			discente = dao.findByExactField(ConvocacaoProcessoSeletivoDiscenteTecnico.class, "discente.id", idDiscente, true);
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward ("/tecnico/cadastramento_discente/dados_discente.jsf");
	}
	
	public String getNomeBusca() {
		return nomeBusca;
	}

	public void setNomeBusca(String nomeBusca) {
		this.nomeBusca = nomeBusca;
	}

	public Long getMatriculaBusca() {
		return matriculaBusca;
	}

	public void setMatriculaBusca(Long matriculaBusca) {
		this.matriculaBusca = matriculaBusca;
	}

	public String getCpfBusca() {
		return cpfBusca;
	}

	public void setCpfBusca(String cpfBusca) {
		this.cpfBusca = cpfBusca;
	}

	public boolean isFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome (boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	public boolean isFiltroMatricula() {
		return filtroMatricula;
	}

	public void setFiltroMatricula(boolean filtroMatricula) {
		this.filtroMatricula = filtroMatricula;
	}

	public boolean isFiltroCpf() {
		return filtroCpf;
	}

	public void setFiltroCpf(boolean filtroCpf) {
		this.filtroCpf = filtroCpf;
	}

	public List<DiscenteTecnico> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(List<DiscenteTecnico> discentes) {
		this.discentes = discentes;
	}

	public ConvocacaoProcessoSeletivoDiscenteTecnico getDiscente() {
		return discente;
	}

	public void setDiscente(ConvocacaoProcessoSeletivoDiscenteTecnico discente) {
		this.discente = discente;
	}

	public Integer getStatusBusca() {
		return statusBusca;
	}

	public void setStatusBusca(Integer statusBusca) {
		this.statusBusca = statusBusca;
	}

	public boolean isFiltroStatus() {
		return filtroStatus;
	}

	public void setFiltroStatus(boolean filtroStatus) {
		this.filtroStatus = filtroStatus;
	}

	public Integer getOpcaoPoloGrupoBusca() {
		return opcaoPoloGrupoBusca;
	}

	public void setOpcaoPoloGrupoBusca(Integer opcaoPoloGrupoBusca) {
		this.opcaoPoloGrupoBusca = opcaoPoloGrupoBusca;
	}

	public boolean isFiltroOpcaoPoloGrupo() {
		return filtroOpcaoPoloGrupo;
	}

	public void setFiltroOpcaoPoloGrupo(boolean filtroOpcaoPoloGrupo) {
		this.filtroOpcaoPoloGrupo = filtroOpcaoPoloGrupo;
	}

	public List<SelectItem> getOpcoesPoloGrupo() throws DAOException {
		if (opcoesPoloGrupo == null || opcoesPoloGrupo.isEmpty()){
			GenericDAO dao = null;
			try {
				dao = getGenericDAO();
				opcoesPoloGrupo = toSelectItems(dao.findAll(OpcaoPoloGrupo.class, "descricao", "asc"), "id", "descricao");
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return opcoesPoloGrupo;
	}

	public void setOpcoesPoloGrupo(List<SelectItem> opcoesPoloGrupo) {
		this.opcoesPoloGrupo = opcoesPoloGrupo;
	}
}