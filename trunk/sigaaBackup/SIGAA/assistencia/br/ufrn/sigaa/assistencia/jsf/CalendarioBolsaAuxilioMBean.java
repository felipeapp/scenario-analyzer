package br.ufrn.sigaa.assistencia.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dao.CalendarioBolsaAuxilioDao;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.CalendarioBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

@Component
@Scope("session")
public class CalendarioBolsaAuxilioMBean extends
		SigaaAbstractController<AnoPeriodoReferenciaSAE> {

	private CalendarioBolsaAuxilio calendario;
	public DataModel calendarios;

	/** Atributo utilizado para upload de arquivo */
	private UploadedFile file;
	
	public CalendarioBolsaAuxilioMBean() {
		obj = new AnoPeriodoReferenciaSAE();
		obj.setCalendario(new ArrayList<CalendarioBolsaAuxilio>());
		clear();
	}

	private void clear() {
		calendario = new CalendarioBolsaAuxilio();
		calendario.setTipoBolsaAuxilio(new TipoBolsaAuxilio());
		calendario.setMunicipio(new Municipio());
		
		setConfirmButton("Cadastrar");
	}

	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		obj = new AnoPeriodoReferenciaSAE();
		obj.setCalendario(new ArrayList<CalendarioBolsaAuxilio>());
		clear();
		return forward("/sae/CadastrarCalendario/form_resultados.jsp");
	}

	public void carregarCalendario(ValueChangeEvent evento) throws DAOException {
		GenericDAO dao = getGenericDAO();
		int idEscolhido = (Integer) evento.getNewValue();
		try {
			setObj(dao.findByPrimaryKey(idEscolhido,
					AnoPeriodoReferenciaSAE.class));
			setConfirmButton("Alterar");
			if (obj == null) {
				obj = new AnoPeriodoReferenciaSAE();
				obj.setCalendario(new ArrayList<CalendarioBolsaAuxilio>());
				clear();
			} else {
				calendarios = new ListDataModel(
						(List<CalendarioBolsaAuxilio>) obj.getCalendario());
			}
		} finally {
			dao.close();
		}

		redirectMesmaPagina();
	}

	public String adicionarPeriodoSolicitacaoBolsa() throws DAOException {
		
		if (!calendario.validate().isEmpty()) {
			addMensagens(calendario.validate());
			return null;
		}

		CalendarioBolsaAuxilioDao dao = getDAO(CalendarioBolsaAuxilioDao.class);
		try {
			if (obj.getCalendario().isEmpty()) {
				addPeriodoSolicitacao(dao);
			} else {
				if (!verificarExistenciaMunicipio()) {
					addPeriodoSolicitacao(dao);
				} else {
					addMensagemWarning("Esse Município já possui esse tipo de Bolsa Auxílio cadastrado. Por favor selecione outro Município.");
				}
			}
		} finally {
			dao.close();
			calendarios = new ListDataModel(
					(List<CalendarioBolsaAuxilio>) obj.getCalendario());
		}

		return null;
	}

	private void addPeriodoSolicitacao(CalendarioBolsaAuxilioDao dao)
			throws DAOException {
		calendario.setTipoBolsaAuxilio(dao.findByPrimaryKey(calendario
				.getTipoBolsaAuxilio().getId(), TipoBolsaAuxilio.class));
		calendario.setMunicipio(dao.findByPrimaryKey(calendario.getMunicipio()
				.getId(), Municipio.class));
		calendario.setAnoReferencia(obj);
		calendario.setAtivo(Boolean.TRUE);
		obj.getCalendario().add(calendario);
		clear();
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(obj.getAno(), "Ano", lista);
		ValidatorUtil.validateRequired(obj.getPeriodo(), "Período", lista);
		// Verifica se existe pelo menos uma bolsa auxílio cadastrada
		if (calendarios == null || calendarios.getRowCount() <= 0) {
			lista.addMensagem(MensagemAviso
					.valueOf("Nenhuma Bolsa Auxílio cadastrada.",
							TipoMensagemUFRN.ERROR));
		}

		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}

		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setObjAuxiliar(file);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_CALENDARIO_SAE);
			prepareMovimento(SigaaListaComando.CADASTRAR_CALENDARIO_SAE);
			execute(mov);
			if (isReprepare()) {
				prepareMovimento(SigaaListaComando.CADASTRAR_CALENDARIO_SAE);
			}
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO,
					"Calendário");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		return preCadastrar();
	}

	public void removerPeriodoSolicitacaoBolsa() {
		CalendarioBolsaAuxilio calendar = (CalendarioBolsaAuxilio) calendarios
				.getRowData();
		for (CalendarioBolsaAuxilio calen : obj.getCalendario()) {
			if (calen.equals(calendar))
				calen.setAtivo(Boolean.FALSE);
		}
		obj.getCalendario().remove(calendar);
	}

	private boolean verificarExistenciaMunicipio() throws DAOException {
		boolean municipioRepetido = false;
		for (CalendarioBolsaAuxilio it : obj.getCalendario()) {
			if (it.getMunicipio().getId() == calendario.getMunicipio().getId()
					&& it.getTipoBolsaAuxilio().getId() == calendario
							.getTipoBolsaAuxilio().getId())
				municipioRepetido = true;
		}
		return municipioRepetido;
	}

	/** Retorna os tipos de bolsa Auxilio ordenando por nome */
	public Collection<SelectItem> getTiposBolsaAuxilio() {
		return getAll(TipoBolsaAuxilio.class, "id", "denominacao");
	}

	public Collection<SelectItem> getAllMunicipios() throws DAOException {
		return toSelectItems(getDAO(MunicipioDao.class).findByUF(24), "id",
				"nome");
	}

	public Collection<SelectItem> getAllAnoCadastrados() throws DAOException {
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
		Collection<AnoPeriodoReferenciaSAE> list = getDAO(
				CalendarioBolsaAuxilioDao.class).findAllPeriodosCadatrados();
		for (AnoPeriodoReferenciaSAE anoPeriodoReferenciaSAE : list)
			itens.add(new SelectItem(anoPeriodoReferenciaSAE.getId(),
					anoPeriodoReferenciaSAE.getAnoPeriodo()));
		return itens;
	}

	public CalendarioBolsaAuxilio getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioBolsaAuxilio calendario) {
		this.calendario = calendario;
	}

	public DataModel getCalendarios() {
		return calendarios;
	}

	public void setCalendarios(DataModel calendarios) {
		this.calendarios = calendarios;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public void removeArquivo() throws ArqException {
		obj.setIdArquivo(null);
	}
	
	public String getNomeArquivo() {
		return EnvioArquivoHelper.recuperaNomeArquivo(obj.getIdArquivo());
	}

}
