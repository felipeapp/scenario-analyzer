/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Created on 29/10/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Mbean responsável por realizar a exclusão de alunos de programas em graduação e stricto
 *
 * @author Victor Hugo
 *
 */
@Component("excluirDiscente")
@Scope("session")
public class ExcluirDiscenteMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente {

	public static final int EXCLUIR_DISCENTE = 1;

	private String observacao;

	/**
	 * Construtor padrão
	 */
	public ExcluirDiscenteMBean() {
		obj = new DiscenteGraduacao();
	}

	/**
	 * Inicia o caso de uso para exclusão do discente.<br><br>
	 * JSP: /sigaa.war/graduacao/menus/aluno.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.DAE, SigaaPapeis.PPG, SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, 
				SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA);
		prepareMovimento(SigaaListaComando.EXCLUIR_DISCENTE);
		setOperacaoAtiva(EXCLUIR_DISCENTE);
		return buscarDiscente();
	}

	/**
	 * Prepara e redireciona para a busca do discente.<br><br>
	 * JSP: Não invocado por JSP
	 * @return
	 */
	public String buscarDiscente() {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		
		if (NivelEnsino.isAlgumNivelStricto(getNivelEnsino()))
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.EXCLUIR_DISCENTE_STRICTO);
		else if ( isPortalComplexoHospitalar() ) 
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.EXCLUIR_DISCENTE_RESIDENCIA);
		else
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.EXCLUIR_DISCENTE_GRADUACAO);
		
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Valida se o discente selecionado pode ser excluído e redireciona para o formulário de exclusão.<br><br>
	 * JSP: Não invocado por JSP
	 */
	public String selecionaDiscente() throws ArqException {
		
		if (obj.isGraduacao())
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), DiscenteGraduacao.class);
		else
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), Discente.class);
		
		/*
		 * Não é permitido excluir discente de graduação com matrículas.	
		 */
		MatriculaComponenteDao mcDao = getDAO(MatriculaComponenteDao.class);
		int numeroMatriculas = mcDao.countMatriculasByDiscente(obj, new SituacaoMatricula[0]);
		if (isUserInRole(SigaaPapeis.DAE) && numeroMatriculas > 0) {
			addMensagemErro("Não é possível excluir este discente, pois este possui registros de matrículas.");
			return null;
		}
		
		/*
		 * DAE só pode excluir alunos com status cadastrado
		 */
		if( isUserInRole(SigaaPapeis.DAE) && obj.getStatus() != StatusDiscente.CADASTRADO){
			addMensagemErro("Só é possível excluir discentes com o status CADASTRADO.");
			return null;
		}

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		int totalMatriculas = dao.countMatriculasByDiscente( obj, SituacaoMatricula.getSituacoesPagasEMatriculadasArray() );
		if( totalMatriculas > 0 ){
			addMensagemErro("Não é possível excluir este discente pois este possui matrículas em componentes ativas ou aproveitadas.");
			return null;
		}

		return forward( getFormPage() );
	}

	public void setDiscente(DiscenteAdapter discente) {
		obj = discente;
	}

	/**
	 * Chama o processador para exclusão do aluno.<br><br>
	 * JSP: /sigaa.war/ensino/excluir_discente/form.jsp
	 * @throws ArqException 
	 */
	@Override
	public String cadastrar() throws ArqException {

		if (!checkOperacaoAtiva(EXCLUIR_DISCENTE)) return cancelar();

		if( getObservacao().trim().length() == 0 ){
			addMensagemErro("Entre com a justificativa da exclusão.");
			return null;
		}

		if( !confirmaSenha() )
			return null;

		obj.setObservacao( observacao );
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento( SigaaListaComando.EXCLUIR_DISCENTE );

		try {
			execute(mov, getCurrentRequest());
			addMessage("O aluno " + obj.getNome() + " foi excluído com sucesso!", TipoMensagemUFRN.INFORMATION);
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}

		return cancelar();
	}

	@Override
	public String getDirBase() {
		return "/ensino/excluir_discente";
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
