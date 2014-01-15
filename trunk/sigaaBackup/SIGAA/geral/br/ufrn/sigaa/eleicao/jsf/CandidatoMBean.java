/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '03/04/2007'
 *
 */
package br.ufrn.sigaa.eleicao.jsf;

import java.util.Collection;
import java.util.Date;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.eleicao.CandidatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.eleicao.dominio.Candidato;
import br.ufrn.sigaa.eleicao.dominio.Eleicao;
import br.ufrn.sigaa.eleicao.negocio.CandidatoMov;

/**
 * Managed Bean responsável pelo controle dos canditados de uma Eleição
 *
 * @see Eleicao
 * @see Candidato
 * @author Victor Hugo
 */
@Component("candidato") @Scope("request")
public class CandidatoMBean extends SigaaAbstractController<Candidato> {

	private final String VIEW_FORM = "/administracao/cadastro/Candidato/form.jsf";

	/**
	 * Foto do candidato
	 */
	private UploadedFile foto;
	
	public CandidatoMBean() {
		obj = new Candidato();
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
	}

	@Override
	public String getFormPage() {
		return VIEW_FORM;
	}

	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException, SegurancaException, DAOException {
		if( getConfirmButton().equalsIgnoreCase("cadastrar") ){
			obj.setDataCadastro(new Date());
			obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());

			if( obj.getServidor().getId() == 0 )
				obj.setServidor(null);
		}
	}

	@Override
	public String cadastrar() throws ArqException{

		try {

			beforeCadastrarAndValidate();

			// Validar dados do perfil
			ListaMensagens erros = obj.validate();

			if (foto != null && foto.getSize() > 1024 * 100) {
				if (erros == null) {
					erros = new ListaMensagens();
				}
				erros.addErro("A foto deve ser menor do que 100kb");
			}

			if (erros != null && !erros.isEmpty()) {
				addMensagens(erros);
				return null;
			}

			prepareMovimento(SigaaListaComando.ELEICAO_CADASTRAR_CANDIDATO);


			// Popular movimento
			CandidatoMov candidatoMov = new CandidatoMov();
			candidatoMov.setCodMovimento(SigaaListaComando.ELEICAO_CADASTRAR_CANDIDATO);
			candidatoMov.setObjMovimentado(obj);
			candidatoMov.setFoto(foto);

			// Executar atualização do perfil
			execute(candidatoMov, getCurrentRequest());

			obj = new Candidato();
			//foto = new UploadedFile();

			addMessage("Candidato cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION);
			return super.cancelar();

		} catch (NegocioException e) {
			return tratamentoErroPadrao(e, e.getMessage());
		}

	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}
	
	public Collection<Candidato> getAllCandidato() throws DAOException {
		CandidatoDao dao = getDAO(CandidatoDao.class);
		all = dao.findAll();
		return all;
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, Candidato.class);
		if (obj == null) {
			addMensagemErro("Candidato inexistente, selecione um cadidato válido.");
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
}