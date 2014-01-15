/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 28/06/2011
* 
*/
package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.medio.dao.CurriculoMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.DiscenteMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.SerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.negocio.MovimentoImplantarHistoricoMedio;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;

/**
 * MBeam responsável por realizar a implantação de históricos de alunos do ensino médio que ainda não o possuem.
 * 
 * @author Arlindo
 */
@Component("implantarHistoricoMedioMBean") @Scope("request")
public class ImplantarHistoricoMedioMBean extends SigaaAbstractController<MatriculaComponente> implements OperadorDiscente  {
	
	/** Discente que terá os componentes implantados no histórico. */
	private DiscenteAdapter discente;
	
	/** Lista das matrículas em componente curricular que será implantada no histórico. */
	private List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();	
	
	/** Lista de disciplinas da série selecionada */
	private List<SelectItem> disciplinas = new ArrayList<SelectItem>();
	
	/** Série selecionada */
	private Serie serie = new Serie();
	
	/** Construtor padrão */
	public ImplantarHistoricoMedioMBean() {
		init();
	}
	
	/** Inicializa os atributos do controller. */
	private void init(){
		obj = new MatriculaComponente();
		obj.setComponente( new ComponenteCurricular() );
		obj.setSituacaoMatricula( new SituacaoMatricula() );
		serie = new Serie();
	}	
	
	/**
	 * Inicia o caso de uso de implantar histórico
	 * <br>
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/medio/menus/discente.jsp
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole( SigaaPapeis.SECRETARIA_MEDIO, SigaaPapeis.GESTOR_MEDIO);
		
		prepareMovimento(SigaaListaComando.IMPLANTAR_HISTORICO_MEDIO);

		init();

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao( OperacaoDiscente.IMPLANTAR_HISTORICO_DISCENTE_MEDIO );
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Adiciona uma matrícula na lista de matrículas
	 * <br>
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/medio/implantar_historico/form.jsp
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String adicionarMatricula() throws ArqException{
		
		obj.setMetodoAvaliacao( getMetodoAvaliacao() );
		
		if (serie == null || serie.getId() == 0)
			addMensagemErro("Selecione uma Série.");
		
		if( obj == null || obj.getComponente() == null || obj.getComponente().getId() == 0 )
			addMensagemErro("Selecione uma Disciplina.");

		if( getMetodoAvaliacao().equals(MetodoAvaliacao.NOTA) && (obj.getMediaFinal() == null || obj.getMediaFinal() < 0  || obj.getMediaFinal() > 10 ))
			addMensagemErro("Média final inválida.");
		
		if( getMetodoAvaliacao().equals(MetodoAvaliacao.CONCEITO) && (obj.getConceito() == null || obj.getConceito() < 1 || obj.getConceito() > 5 ) )
			addMensagemErro("Conceito inválido. Selecione um conceito válido.");
		
		if( getMetodoAvaliacao().equals(MetodoAvaliacao.COMPETENCIA) && obj.getApto() == null)
			addMensagemErro("Competência inválida. Selecione uma competência válida.");
		
		if( obj.getAno() == null || obj.getAno() <= 1950 )
			addMensagemErro("Ano inválido.");
		
		if( obj.getAno() == null || obj.getAno() > getCalendarioVigente().getAno() )
			addMensagemErro("O ano não pode ser superior ao ano do calendário vigente.");

		if( obj.getSituacaoMatricula() == null || obj.getSituacaoMatricula().getId() == 0)
			addMensagemErro("Selecione uma situação.");
		
		if (!ValidatorUtil.isEmpty(obj.getComponente()) && !ValidatorUtil.isEmpty(serie)){
			for (MatriculaComponente mat : matriculas){
				if (mat.getComponente().getId() == obj.getComponente().getId() && mat.getSerie().getId() == serie.getId()){
					addMensagemErro("A disciplina selecionada já foi adicionada anteriormente para mesma série.");
					break;
				}
			}					
		}
		
		if( hasErrors() )
			return null;
		
		DiscenteMedioDao dao = getDAO(DiscenteMedioDao.class);
		try {
			obj.setComponente(dao.findByPrimaryKey(obj.getComponente().getId(), ComponenteCurricular.class));
			obj.setDetalhesComponente(obj.getComponente().getDetalhes());
			
			if( hasErrors() )
				return null;
			
			obj.setSerie(dao.refresh(serie));
			obj.setDiscente(discente.getDiscente());
			obj.setSituacaoMatricula( dao.findByPrimaryKey(obj.getSituacaoMatricula().getId(), SituacaoMatricula.class) );
			matriculas.add(obj);
			
			init();
		} finally {
			if (dao != null)
				dao.close();
		}


		return null;
	}

	/**
	 * Remove uma matrícula da lista de matrículas em componentes curriculares a
	 * serem implementados no histórico do discente. 
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/medio/implantar_historico/form.jsp
	 * </ul>
	 * 
	 * @return
	 */
	public String removerMatricula(){

		Integer idComponente = getParameterInt("idComponente");
		if( idComponente == null ){
			addMensagemErro("Nenhuma matrícula selecionada para remoção.");
			return null;
		}
		Iterator<MatriculaComponente> it = matriculas.iterator();
		while (it.hasNext()) {
			MatriculaComponente matricula = it.next();
			if (matricula.getComponente().getId() == idComponente)
				it.remove();
		}

		return null;
	}	
	
	/**
	 * Chama a camada de negócio para persistir o histórico cadastrado.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/medio/implantar_historico/resumo.jsp
	 * </ul>
	 * @throws ArqException 
	 */
	@Override
	public String cadastrar() throws ArqException {

		if( !confirmaSenha() )
			return null;

		try {
			MovimentoImplantarHistoricoMedio mov = new MovimentoImplantarHistoricoMedio();
			mov.setCodMovimento(SigaaListaComando.IMPLANTAR_HISTORICO_MEDIO);
			mov.setMatriculas(matriculas);
			mov.setDiscente(discente.getDiscente());
			execute( mov );
			addMessage("Implantação de histórico realizada com sucesso", TipoMensagemUFRN.INFORMATION );
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			e.printStackTrace();
			return null;
		} 

		return cancelar();

	}	
	
	/**
	 * Retorna as situações de matrícula que o usuário pode selecionar
	 * na operação de implantar histórico.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/medio/implantar_historico/form.jsp
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getSituacoesMatriculas(){
		ArrayList<SelectItem> situacoes = new ArrayList<SelectItem>();
		situacoes.add( new SelectItem( SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.APROVADO.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), SituacaoMatricula.APROVEITADO_CUMPRIU.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.APROVEITADO_DISPENSADO.getId(), SituacaoMatricula.APROVEITADO_DISPENSADO.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(), SituacaoMatricula.APROVEITADO_TRANSFERIDO.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.TRANCADO.getId(), SituacaoMatricula.TRANCADO.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_FALTA.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.REPROVADO.getId(), SituacaoMatricula.REPROVADO.getDescricao()  )  );
		return situacoes;
	}	
	
	/**
     * Redireciona para o formulário de dados da implantação de histórico.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/medio/implantar_historico/resumo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String voltar() {
		return forward(getFormPage());
	}

	/**
	 * Redireciona para a página de resumo.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/medio/implantar_historico/form.jsp
	 * </ul>
	 * @return
	 */
	public String submeterDados(){

		if( matriculas == null || matriculas.isEmpty() ){
			addMensagemErro("É necessário adicionar pelo menos uma matrícula para implantar o histórico.");
			return null;
		}

		return forward( getViewPage() );
	}	
	
	/** 
	 * Método invocado pelo MBean de busca de discente, após setar o discente.<br /><br />
	 * Não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		init();
		matriculas = new ArrayList<MatriculaComponente>();
		return forward( getFormPage() );
	}	
	
	/**
	 * Retorna o discente que terá os componentes implantados no histórico.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	/** 
	 * Seta o discente que terá os componentes implantados no histórico. Invocado pelo MBean de busca de discente.
	 *  
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}
	
	/** 
	 * Retorna a lista das matrículas em componente curricular que será implantada no histórico.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public List<MatriculaComponente> getMatriculas() {
		return matriculas;
	}
	
	/** 
	 * Retorna o dataModel da lista das matrículas em componente curricular que será implantada no histórico.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public ListDataModel getMatriculasDataModel() {
		return new ListDataModel(matriculas);
	}

	/** 
	 * Seta a lista das matrículas em componente curricular que será implantada no histórico.
	 * @param matriculas
	 */
	public void setMatriculas(List<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}	
	
	/**
	 * Método responsável por retornar a descrição em forma de texto da metodologia de avaliação 
	 * utilizada nos componentes curriculares.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/medio/implantar_historico/form.jsp
	 * 	<li>/sigaa.war/medio/implantar_historico/resumo.jsp
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String getDescricaoMetodoAvaliacao() throws DAOException{
		if(isNota())
			return "Nota";
		else if(isConceito())
			return "Conceito";
		else if(isCompetencia())
			return "Competência";
		return "";
	}	
	
	/** 
	 * Método responsável por verificar se o método de avaliação do discente é por nota
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/implantar_historico/form.jsp
	 * </ul>
	 * @throws DAOException 
	 * 
	 */
	public boolean isNota() throws DAOException{
		return getMetodoAvaliacao() == MetodoAvaliacao.NOTA;
	}
	
	/** Método responsável por verificar se o método de avaliação do discente é por conceito
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/implantar_historico/form.jsp
	 * </ul>
	 * @throws DAOException 
	 */
	public boolean isConceito() throws DAOException{
		return getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO;
	}
	
	/** Método responsável por verificar se o método de avaliação do discente é por competência
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/implantar_historico/form.jsp
	 * </ul>
	 * @throws DAOException 
	 */
	public boolean isCompetencia() throws DAOException{
		return getMetodoAvaliacao() == MetodoAvaliacao.COMPETENCIA;
	}	
	
	/**
	 * Retorna o método padrão de avaliação
	 * @return
	 * @throws DAOException
	 */
	private Integer getMetodoAvaliacao() throws DAOException{
		Integer metodo = getParametrosAcademicos().getMetodoAvaliacao();
		if (metodo != null)
			return metodo;
		else
			return MetodoAvaliacao.NOTA;
	}

	@Override
	public String getFormPage() {
		return "/medio/implantar_historico/form.jsp";
	}
	
	@Override
	public String getViewPage() {
		return "/medio/implantar_historico/resumo.jsp";
	}
	
	/**
	 * Retornas as séries do curso do discente  
	 * @return the seriesByCurso 
	 * @throws DAOException */
	public List<SelectItem> getSeriesByCurso() throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		try {
			return toSelectItems(dao.findByCurso((CursoMedio) discente.getDiscente().getCurso()), "id", "descricaoCompleta");
		} finally {
			if (dao != null)
				dao.close();
		}
	}	
	
	/** 
	 * Carrega as disciplinas da série selecionada.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/implantar_historico/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarDisciplinasBySerie(ValueChangeEvent e) throws DAOException {
		CurriculoMedioDao dao = getDAO( CurriculoMedioDao.class );
		try {
			disciplinas = new ArrayList<SelectItem>(0);
			if( e != null && (Integer)e.getNewValue() > 0 ){
				Serie serie = new Serie((Integer) e.getNewValue());
				Collection<ComponenteCurricular> componentes = dao.findDisciplinasByCurriculoSerie(null, serie);
				if (!ValidatorUtil.isEmpty( componentes ))
					disciplinas = toSelectItems(componentes, "id", "descricao");
			} 
		} finally {
			if (dao != null)
				dao.close();
		}
		
	}

	public List<SelectItem> getDisciplinas() {
		return disciplinas;
	}

	public void setDisciplinas(List<SelectItem> disciplinas) {
		this.disciplinas = disciplinas;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}	
	
}
