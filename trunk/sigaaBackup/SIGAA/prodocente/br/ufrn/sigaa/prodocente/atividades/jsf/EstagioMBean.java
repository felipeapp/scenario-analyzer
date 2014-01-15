/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.prodocente.EstagioDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.RegistroAtividadeMBean;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.Estagio;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * MBean respons�vel por carregar as informa��es das p�ginas que mostram as informa��es 
 * sobre afastamento.
 * 
 * @author Mario Melo
 */
@Scope("session")
@Component("estagio")
public class EstagioMBean extends AbstractControllerAtividades<Estagio> implements OperadorDiscente{

	public EstagioMBean() {
		clear();
	}

	/**
	 * Inicializa todos os objetos e atributos gerenciados pelo Managed Bean.
	 */
	private void clear() {
		obj = new Estagio();
		obj.setEntidadeFinanciadora(new EntidadeFinanciadora());
		obj.setArea(new AreaConhecimentoCnpq());
		obj.setDepartamento(new Unidade());
		obj.setServidor(new Servidor());
		obj.setSubArea(new AreaConhecimentoCnpq());
		obj.setAluno(new Discente());
		setOrder(false);

		if (getServidorUsuario() != null) {
			obj.setServidor(getServidorUsuario());
			obj.setDepartamento( obj.getServidor().getUnidade() );
		}
	}
	@Override
	public String getDirBase() {
		// TODO Auto-generated method stub
		return "/prodocente/atividades/Estagio";
	}
	
	/**
	 * Iniciar fluxo da altera��o de est�gio
	 * por alguns gestores de ensino como, por exemplo, 
	 * coordenadores de curso ou DAE
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/WEB-INF/jsp/graduacao/menu/programa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarAlteracao() throws SegurancaException {
		checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.DAE, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO});
		
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_ESTAGIO);
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		return buscaDiscenteMBean.popular();
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(Estagio.class, "id", "nomeProjeto");
	}
	
	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		super.checkDocenteRole();
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {

		obj.anularAtributosTransient();
		
		if (obj.getServidor() == null || obj.getServidor().getId() == 0 ) {
			obj.setServidor(getServidorUsuario());
			obj.setDepartamento( obj.getServidor().getUnidade() );
		}

		/**
		 * Quando o acesso � a partir do m�dulo do Portal do Coord. de Gradua��o. 
		 * Verifica a situa��o da matr�cula do discente para o componente 
		 * com {@link TipoAtividade#ESTAGIO} 
		 * */
		if( getUsuarioLogado().isUserInRole( SigaaPapeis.COORDENADOR_CURSO ) &&
				obj.getId() != 0 && !isEmpty( obj.getMatricula() ) 
				&& obj.getMatricula().getDiscente().isGraduacao() 
				&& !obj.getMatricula().isMatriculado() )
			addMensagem( MensagensGraduacao.PERMITIDO_ALTERAR_ATIVIDADE_SITUACAO_MATRICULADO );
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public void afterAtualizar() {
		obj.iniciarAtributosTransient();
		telaListaAtividadesOrientandos();
	}

	/**
	 * Carrega as sub-areas da �rea cadastrada
	 * 
	 * M�todo n�o � invocado por jsp
	 *
	 * @throws DAOException
	 */
	@Override
	public void carregaSubAreas() throws DAOException {

		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (obj != null && obj.getArea() != null && obj.getArea().getId() != 0 ) {
			subArea = toSelectItems(dao.findAreas(obj.getArea()), "id", "nome");
			setSubArea(subArea);
		}
	}

	/**
	 * Para tratar a mudan�a de �rea
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/atividades/Estagio/form.jsp</li>
	 * </ul>
	 */
	@Override
	public void changeArea() throws DAOException {
		carregaSubAreas();
	}

	/**
	 * Cancela a opera��o atual.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/atividades/Estagio/form.jsp</li>
	 *  <li>sigaa.war/prodocente/atividades/Estagio/view.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		resetBean();
		if (preCadastroParaGraduacao || atualizacaoParaGraduacao) {
			String mbean = "registroAtividade";
			resetBean(mbean);
			return ((RegistroAtividadeMBean)getMBean(mbean)).cancelar();
		}
		return super.cancelar();
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public String getUrlRedirecRemover() {
		return getListPage();
	}
	
	/**
	 * Redireciona para a tela de lista de atividades dos orientandos do usu�rio logado. 
	 * M�todo n�o � invocado por jsp.
	 * @return
	 */
	public String telaListaAtividadesOrientandos(){
		return forward(RegistroAtividadeMBean.VIEW_ATIVIDADES_ORIENTANDOS);
	}

	/**
	 * Popula o discente ap�s o resultado ad busca.
	 * M�todo n�o invocado por JSP's.
	 */
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj.setAluno( discente.getDiscente() );
	}

	/**
	 * Seleciona o discente e exibe uma lista das atividades de est�gio para altera��o.
	 * M�todo n�o invocado por JSP's.
	 */
	@Override
	public String selecionaDiscente() throws ArqException {
		
		atividades = getDAO( EstagioDao.class ).findByDiscenteServidor( obj.getAluno().getId(), idServidor );
		
		if( isEmpty(atividades) ){
			addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS, "est�gios" );
			return null;
		}
		
		return forward( getListPage() );
		
	}
	
}