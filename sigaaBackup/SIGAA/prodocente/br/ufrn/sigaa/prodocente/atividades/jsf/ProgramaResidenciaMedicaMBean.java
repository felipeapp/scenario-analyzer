/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.prodocente.ProgramaResidenciaMedicaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.prodocente.atividades.dominio.ProgramaResidenciaMedica;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Managed Bean de Programa Residencia Medica, responsável pela realização 
 * das seguintes operações cadastro, atualização, remoção de um programa de residência Médica.  
 * 
 * Gerado pelo mario
 */
public class ProgramaResidenciaMedicaMBean extends AbstractControllerAtividades<ProgramaResidenciaMedica> {

	/** Combo de programas  */
	public Collection<SelectItem> comboProgramas;
	
	public ProgramaResidenciaMedicaMBean() {
		clear();
	}
	
	/**
	 * Retorna todos os programas de residência Médica que estiverem ativos.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/complexo_hospitalar/ResidenciaMedica/form.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllCombo() throws DAOException {
		return toSelectItems(getGenericDAO().findAllAtivos(ProgramaResidenciaMedica.class, "nome"), "id", "descricao");
	}
	
	/**
	 * Retorna todos os programas de residência em saúde da unidade associada à permissão do usuário logado.
	 */
	public Collection<SelectItem> getAllUnidadeCombo() throws DAOException {
		if(isUserInRole(SigaaPapeis.SECRETARIA_RESIDENCIA))
			return toSelectItems(getGenericDAO().findByExactField(ProgramaResidenciaMedica.class, 
					new String [] {"hospital.id", "ativo"}, 
					new Object [] {getUsuarioLogado().getPermissao(SigaaPapeis.SECRETARIA_RESIDENCIA).iterator().next().getUnidadePapel().getId(), true}), 
					"id", "descricao");
		if(isUserInRole(SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA))
			return toSelectItems(getDAO(ProgramaResidenciaMedicaDao.class).findAllProgramaCoordenador((getUsuarioLogado().getServidor().getId())), "id", "nome");
		return getAllCombo();
	}
	
	@Override
	protected void afterCadastrar() {
		clear();
	}

	/**
	 * Método responsável pelo cadastrar de um novo programa de Residência Médica.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/prodocente/atividades/ProgramaResidenciaMedica/form.jsp 
	 * 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,NegocioException {
		
		erros.addAll(obj.validate());
		
		if (hasErrors())
			return null;
		
		if (obj.getId() > 0) {
			obj.setAtivo(true);
		}
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		if (obj.getId() == 0) {
			prepareMovimento(SigaaListaComando.CADASTRAR_RESIDENCIA_MEDICA);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_RESIDENCIA_MEDICA);
			try {
				execute(mov);
				addMessage("Operação realizada com sucesso!",	TipoMensagemUFRN.INFORMATION);
			}catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				e.printStackTrace();
				return null;
			}catch (Exception e) {
				return tratamentoErroPadrao(e);
			}

			afterCadastrar();

			return cancelar();

		} else {
			prepareMovimento(SigaaListaComando.ALTERAR_RESIDENCIA_MEDICA);
			mov.setCodMovimento(SigaaListaComando.ALTERAR_RESIDENCIA_MEDICA);
			try {
				execute(mov);
				addMessage("Operação realizada com sucesso!",TipoMensagemUFRN.INFORMATION);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				e.printStackTrace();
				return forward(getFormPage());
			}

			afterCadastrar();

			String forward = forwardCadastrar();
			if (forward == null)
				return forward(getListPage());
			else
				return forward(forward);
		}
	}

	@Override
	public String inativar() throws ArqException, NegocioException {
		ProgramaResidenciaMedicaDao dao = getDAO(ProgramaResidenciaMedicaDao.class);
		MovimentoCadastro mov = new MovimentoCadastro();
		prepareMovimento(SigaaListaComando.REMOVER_RESIDENCIA_MEDICA);
		setObj(dao.findById(getParameterInt("id")));
		mov.setCodMovimento(SigaaListaComando.REMOVER_RESIDENCIA_MEDICA);
		mov.setObjMovimentado(obj);
		try {
			execute(mov);
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Programa de Residência Médica");
		} catch (ArqException e) {
			e.printStackTrace();
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
		}
		removeOperacaoAtiva();
		return forward(getListPage());
	}
	
	
	/** Diretório onde se encontram as view´s */
	@Override
	public String getDirBase() {
		return "/prodocente/atividades/ProgramaResidenciaMedica";
	}
	
	/**
	 * Método responsável pela atualização de um programa de residência Médica.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/prodocente/atividades/ProgramaResidenciaMedica/lista.jsp
	 * 
	 */
	@Override
	public String atualizar() throws ArqException {
		UnidadeDao dao = getDAO(UnidadeDao.class);
		ProgramaResidenciaMedicaDao programaDao = getDAO(ProgramaResidenciaMedicaDao.class);
		try {
			int id = getParameterInt("id");
			super.atualizar();
			setObj( programaDao.findById(id) );
		} finally {
			dao.close();
			programaDao.close();
		}
		
		return forward("/prodocente/atividades/ProgramaResidenciaMedica/form.jsf");
	}

	/**
	 * Retorna todos os programa do hospital.
	 */
	public Collection<SelectItem> getAllProgramas() throws DAOException {
		return toSelectItems(getDAO(UnidadeDao.class).findUnidadeProgramaResidenciaMedica(
				TipoUnidadeAcademica.PROGRAMA_RESIDENCIA, obj.getHospital().getId()), "id", "nome");
	}
	
	/**	Limpa os atributos deste controller. */
	private void clear() {
		obj = new ProgramaResidenciaMedica();
		obj.setHospital( new Unidade() );
		obj.setUnidadePrograma(new Unidade());
		comboProgramas = new ArrayList<SelectItem>();
	}

	/** 
	 * Tem como finalidade verificar se o usuário tem o permissão para acessar tal funcionalidade
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}
	
	/**
	 * Utilizado para que o usuário seja direcionado para a tela da listagem.
	 * 
	 * JSP: form.jsp
	 */
	@Override
	public String getListPage() {
		return "/prodocente/atividades/ProgramaResidenciaMedica/lista.jsf";
	}
	
	/**
	 * Para tratar mudança de Hospital
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/atividades/ProgramaResidenciaMedica/form.jsp</li>
	 * </ul>
	 *
	 * @param evt
	 * @throws DAOException
	 */
	public void changePrograma(ValueChangeEvent evt) throws DAOException {
		if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
			obj.getUnidadePrograma().setId((Integer) evt.getNewValue());
			getAllProgramas();
		}
	}

	public Collection<SelectItem> getComboProgramas() {
		return comboProgramas;
	}

	public void setComboProgramas(Collection<SelectItem> comboProgramas) {
		this.comboProgramas = comboProgramas;
	}
	
}