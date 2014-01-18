/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 22/03/2013
 *
 */
package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.UnidadeTempo;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CronogramaExecucaoAulasDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CargaHorariaSemanalDisciplina;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CronogramaExecucaoAulas;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * 
 * Entidade responsável pela criação do cronograma de execução de aulas do IMD.
 * 
 * @author Rafael Silva
 * @author Rafael Barros
 *
 */

@Component("cronogramaExecucao")
@Scope("session")
@SuppressWarnings("serial")
public class CronogramaExecucaoMBean extends SigaaAbstractController<Curso> {
	
	/**Cronograma de aulas do Instituto Metrópole Digital*/
	private CronogramaExecucaoAulas cronograma = new CronogramaExecucaoAulas();
	
	/** Coleção de selectItem de cursos do Instituto Metrópole Digital. */
	private List<SelectItem> cursosCombo = new ArrayList<SelectItem>(0);
	
	/** Coleção de selectItem de módulos */
	private List<SelectItem> modulosCombo = new ArrayList<SelectItem>(0);
	
	/**Coleção de SelectItem de possíveis períodos de avaliação*/
	private List<SelectItem> periodicidadesCombo = new ArrayList<SelectItem>();
	
	/**ID do cronograma de execuçao*/
	private int idCronograma;
	/**ID do Módulo Vinculado ao Cronograma*/
	private int idModulo;
	/**Filtro do ano*/
	private Integer filtroAno;
	
	/**Filtro do Período*/
	private Integer filtroPeriodo;
	
	/**Lista das carga horária semanal calculada das disciplinas*/
	private List<CargaHorariaSemanalDisciplina> chsdList  = new ArrayList<CargaHorariaSemanalDisciplina>();
	
	/**Matriz com as cargas horárias semanais das disciplinas*/
	private List<List<CargaHorariaSemanalDisciplina>> tabelaCargaHoraria = new ArrayList<List<CargaHorariaSemanalDisciplina>>();
	
	/**Lista de carga horária das disciplinas*/
	private List<Integer> chDisciplina = new ArrayList<Integer>();

	/**Lista de carga horária semanal*/
	private List<Integer> chSemana = new ArrayList<Integer>();

	/**Carga horária tótal do módulo*/
	private int chTotal;
	
	/**Lista de cronogramas de execução*/
	private List<CronogramaExecucaoAulas> listaCronogramas = new ArrayList<CronogramaExecucaoAulas>();
	
	/**Identifica se o cronograma já foi cadastrado*/
	private Boolean isCadastroNovo = new Boolean(true);
	
	/**Identifica se a operação é de alteração*/
	private Boolean isAlterar = new Boolean(false);
		

	/**Construtor da Classe*/
	public CronogramaExecucaoMBean() throws DAOException, ArqException{
		this.limparDadosCronograma();
		cronograma = new CronogramaExecucaoAulas();
		cronograma.setCurso(new Curso());
		cronograma.setModulo(new Modulo());
		cronograma.setUnidadeTempo(new UnidadeTempo());
		
		CursoDao cursoDao = getDAO(CursoDao.class);
		periodicidadesCombo = toSelectItems(cursoDao.findAll(UnidadeTempo.class), "id", "descricao");
		
		Unidade imd = getGenericDAO().findByPrimaryKey(ParametroHelper.getInstance().getParametroInt(
				ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL), Unidade.class);
		
		//cursosCombo = toSelectItems(cursoDao.findByNivel(getNivelEnsino(), true, null, new Unidade(getUnidadeGestora())), "id", "descricao");
		cursosCombo = toSelectItems(cursoDao.findByNivel(getNivelEnsino(), true, null, imd), "id", "descricao");
	}
	
	//INÍCIO NAVEGAÇÃO:
	/**
	 * Carrega as informações do cronograma quando o usuário seleciona o botão avançar. 
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws DAOException
	 */
	public String avancar() throws DAOException{
		chSemana = new ArrayList<Integer>();
		chDisciplina = new ArrayList<Integer>();
		chTotal = 0;
		chsdList = new ArrayList<CargaHorariaSemanalDisciplina>();
		tabelaCargaHoraria = null;
		cronograma.setPeriodosAvaliacao(new ArrayList<PeriodoAvaliacao>());
		
		erros.addAll(cronograma.validate());
		
		
		//GERA OS ERROS DE VALICAÇÃO DO FORMULÁRIO
		
		
		if (cronograma.getModulo() == null || cronograma.getModulo().getId()==0)
			erros.addErro("Módulo:  Campo obrigatório não informado.");
		
		if (cronograma.getUnidadeTempo().getId()==0) 
			erros.addErro("Periodicidade:  Campo obrigatório não informado.");
		
		if (cronograma.getPeriodo() != null ) {
			if (cronograma.getPeriodo()!=2 && cronograma.getPeriodo()!=1) {
				erros.addErro("Período: O valor deve ser maior ou igual a 1 e menor ou igual a 2.");
			}
		}
		
		if (cronograma.getAno()!=null) {
			if (cronograma.getAno()<1900 ) {
				erros.addErro("Ano: O valor deve ser maior ou igual a 1900.");
			}
		}
			
		
		if (!hasErrors()) {
			cronograma.setUnidadeTempo(getDAO(CronogramaExecucaoAulasDao.class).findByPrimaryKey(cronograma.getUnidadeTempo().getId(), UnidadeTempo.class));
			cronograma.setCurso(getDAO(CronogramaExecucaoAulasDao.class).findByPrimaryKey(cronograma.getCurso().getId(), Curso.class));
			
			//Carrega Todas as informações do módulo selecionado
			if (cronograma.getModulo() != null && cronograma.getModulo().getId()!= 0)
				cronograma.setModulo(getDAO(CronogramaExecucaoAulasDao.class).findByPrimaryKey(cronograma.getModulo().getId(), Modulo.class));
			//Gera os períodos de avaliação e insere no cronograma.
			
			carregaPeriodosAvalicao();
		
			for (int i = 0; i < cronograma.getModulo().getDisciplinas().size(); i++){
				chDisciplina.add(0);
			}
			
			for (int i = 0; i < getQuantidadePeriodos(); i++) {
				chSemana.add(0);
			}
			
			//Gera a tabela de carga horária conforme irá ser exibida posteriormente;
			CargaHorariaSemanalDisciplina chsd = null;
			tabelaCargaHoraria = new ArrayList<List<CargaHorariaSemanalDisciplina>>();
			
			for (ComponenteCurricular cc: cronograma.getModulo().getDisciplinas()){
				chsdList = new ArrayList<CargaHorariaSemanalDisciplina>();
				for (PeriodoAvaliacao p:cronograma.getPeriodosAvaliacao()) {
					chsd = new CargaHorariaSemanalDisciplina();
					chsd.setDisciplina(cc);
					chsd.setPeriodoAvaliacao(p);
					chsd.setCargaHoraria(0);
					chsdList.add(chsd);
				}
				tabelaCargaHoraria.add(chsdList);
			}
			
			if (cronograma.getDataFim()!= null && cronograma.getDataInicio()!= null) {
				if (cronograma.getDataFim().before(cronograma.getDataInicio())) {
					erros.addErro("A data final deve ser maior que a data inicial!");
				}else{
					if (getQuantidadePeriodos()==0){
						erros.addErro("O período letivo digitado é menor que a periodicidade informada!");
					}
				}
			}
			
			if (!hasErrors()) {
				redirect("/metropole_digital/cronograma_execucao/form_carga_horaria.jsf");
			}
		}
		return null;
	}
	
	/**
	 * Redireciona para o "PASSO 1" do cadastro do cronograma de execução. 
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 */
	public String voltarForm(){
		tabelaCargaHoraria = null;
		cronograma.setPeriodosAvaliacao(new ArrayList<PeriodoAvaliacao>());
		if (cronograma.getId()==0) {
			redirect("/metropole_digital/cronograma_execucao/form.jsf");
		}
		return null;
	}
		
	/**
	 * Carrega as informações necessárias para a alteração do cronograma.
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws DAOException
	 */
	public String preAlterar() throws DAOException{
		isAlterar = true;
		CronogramaExecucaoAulasDao cronoDao = getDAO(CronogramaExecucaoAulasDao.class);
		cronograma = cronoDao.findByPrimaryKey(getParameterInt("id"));
		Collections.sort(cronograma.getPeriodosAvaliacao());
		int somaCargaHoraria = 0;

		try {
			chDisciplina = new ArrayList<Integer>();
			tabelaCargaHoraria = new ArrayList<List<CargaHorariaSemanalDisciplina>>();
			
			for (int i = 0; i < cronograma.getModulo().getDisciplinas().size(); i++) {
				chsdList = new ArrayList<CargaHorariaSemanalDisciplina>();
				somaCargaHoraria=0;
				
				for (int j = 0; j < cronograma.getPeriodosAvaliacao().size(); j++) {
					Collections.sort(cronograma.getPeriodosAvaliacao().get(j).getChsdList());
					chsdList.add(cronograma.getPeriodosAvaliacao().get(j).getChsdList().get(i));
					somaCargaHoraria = somaCargaHoraria + cronograma.getPeriodosAvaliacao().get(j).getChsdList().get(i).getCargaHoraria();
				}
				chDisciplina.add(somaCargaHoraria);
				
				tabelaCargaHoraria.add(chsdList);	
			}
			
			chSemana = new ArrayList<Integer>();
			for (List<CargaHorariaSemanalDisciplina> chsdList : tabelaCargaHoraria) {
				for (CargaHorariaSemanalDisciplina chsd : chsdList) {
					chSemana.add(chsd.getPeriodoAvaliacao().getChTotalPeriodo());
				}
			}
			
			chTotal = 0;
			for (Integer ch : chDisciplina) {
				chTotal = chTotal+ch;
			}
		} finally{
			cronoDao.close();
		}
		
		return redirect("/metropole_digital/cronograma_execucao/alterar.jsf");
	}
	
	/**
	 * Carrega as informações de um cronograma cadastrado e em seguida redireciona para a tela de visualização do mesmo.
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param  
	 * @return
	 * @throws DAOException
	 */
	public String visualizar() throws DAOException{
		cronograma = getDAO(CronogramaExecucaoAulasDao.class).findByPrimaryKey(getParameterInt("id"));
		Collections.sort(cronograma.getPeriodosAvaliacao());
		int somaCargaHoraria;
		tabelaCargaHoraria = new ArrayList<List<CargaHorariaSemanalDisciplina>>();
		for (int i = 0; i < cronograma.getModulo().getDisciplinas().size(); i++) {
			chsdList = new ArrayList<CargaHorariaSemanalDisciplina>();
			somaCargaHoraria=0;
			for (int j = 0; j < cronograma.getPeriodosAvaliacao().size(); j++) {
				chsdList.add(cronograma.getPeriodosAvaliacao().get(j).getChsdList().get(i));
				somaCargaHoraria = somaCargaHoraria + cronograma.getPeriodosAvaliacao().get(j).getChsdList().get(i).getCargaHoraria();
			}
			chDisciplina.add(somaCargaHoraria);
			tabelaCargaHoraria.add(chsdList);
			
		}
		return redirect("/metropole_digital/cronograma_execucao/view.jsf");
	}
	
	
	/**
	 * Carrega as informações necessárias para exibição da página de detalhamento dos períodos do cronograma.
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws DAOException
	 */
	public String visualizarPeriodos() throws DAOException{
		cronograma = getDAO(CronogramaExecucaoAulasDao.class).findByPrimaryKey(getParameterInt("id"));
		Collections.sort(cronograma.getPeriodosAvaliacao());
		int somaCargaHoraria;
		tabelaCargaHoraria = new ArrayList<List<CargaHorariaSemanalDisciplina>>();
		for (int i = 0; i < cronograma.getModulo().getDisciplinas().size(); i++) {
			chsdList = new ArrayList<CargaHorariaSemanalDisciplina>();
			somaCargaHoraria=0;
			for (int j = 0; j < cronograma.getPeriodosAvaliacao().size(); j++) {
				chsdList.add(cronograma.getPeriodosAvaliacao().get(j).getChsdList().get(i));
				somaCargaHoraria = somaCargaHoraria + cronograma.getPeriodosAvaliacao().get(j).getChsdList().get(i).getCargaHoraria();
			}
			chDisciplina.add(somaCargaHoraria);
			tabelaCargaHoraria.add(chsdList);
			
		}
		return redirect("/metropole_digital/cronograma_execucao/view_periodos.jsf");
	}
	
	/**
	 * Redireciona o sistema para a página de pesquisa de cronogramas 
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 */
	public String preBuscar(){
		limparDadosCronograma();
		return redirect("/metropole_digital/cronograma_execucao/buscar.jsf");
	}
	
	/**
	 * Redireciona o sistema para a página de cadastro do cronograma 
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		this.limparDadosCronograma();
		isAlterar = false;
		return redirect("/metropole_digital/cronograma_execucao/form.jsf");
	}
	
	/**
	 * Redireciona para a página de busca, mantendo as informções que já estavam em sessão.
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 */
	public String voltarFormBusca(){
		return redirect("/metropole_digital/cronograma_execucao/buscar.jsf");
	}
	
	
	/**
	 * Realiza pesquisa dos cronogramas de execução cadastrados;
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 * 
	 */
	public String buscar() throws DAOException{
		CronogramaExecucaoAulasDao dao = getDAO(CronogramaExecucaoAulasDao.class);
		listaCronogramas = Collections.emptyList();
		if (cronograma.getCurso().getId()==0) {
			addMensagemErro("Curso: Campo Obrigatório não Informado!");
		}else{
			if (!hasErrors()) {
				listaCronogramas = (List<CronogramaExecucaoAulas>) dao.findCronograma(idModulo, filtroAno, filtroPeriodo);
				if (listaCronogramas.isEmpty()) {
					addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
				}
			}
		}
		//limparDadosCronograma();
		return null;
	}
	
	/**
	 * Persiste o cronograma de Execução
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws DAOException
	 */
	public String salvar() throws DAOException{
		CronogramaExecucaoAulasDao dao = getDAO(CronogramaExecucaoAulasDao.class);
		this.validarCargaHoraria();
		if (isAlterar) {
			if (cronograma.getPeriodo() != null ) {
				if (cronograma.getPeriodo()!=2 && cronograma.getPeriodo()!=1) {
					addMessage("Período: O valor deve ser maior ou igual a 1 e menor ou igual a 2.", TipoMensagemUFRN.ERROR);
					//erros.addErro("Período: O valor deve ser maior ou igual a 1 e menor ou igual a 2.");
				}
			}else{
				addMessage("Período: Campo Obrigatório não Informado.", TipoMensagemUFRN.ERROR);
			}
			
			if (cronograma.getAno()!=null) {
				if (cronograma.getAno()<1900 ) {
					addMessage("Ano: O valor deve ser maior ou igual a 1900.", TipoMensagemUFRN.ERROR);
//					erros.addErro("Ano: O valor deve ser maior ou igual a 1900.");
				}
			}else{
				addMessage("Ano: Campo Obrigatório não Informado.", TipoMensagemUFRN.ERROR);
			}
		}
		
		if (!hasErrors()) {
				salvarCronograma();
				if (isAlterar) {
					addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO,"Cronograma");
					buscar();
					return redirect("/metropole_digital/cronograma_execucao/buscar.jsf");
				}else{
					addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO,"Cronograma");
					
				}
			listaCronogramas = (List<CronogramaExecucaoAulas>) dao.findCronograma(cronograma.getModulo().getId(), cronograma.getAno(), cronograma.getPeriodo());
			redirect("/metropole_digital/menus/menu_imd.jsf");
		}else{
			return null;
		}
		return null;
	}
	
	/**
	 * Cancela a operação realizada e redireciona o sistema para a página inicial do módulo
	 * 
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 */
	@Override
	public String cancelar() {
		this.limparDadosCronograma();
		return super.cancelar();
	}
	
	/**
	 * Retorna para a busca de cronogramas caso o usuário selecione o botão cancelar
	 * 
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws DAOException
	 */
	public String cancelarAlteracao() throws DAOException{
		buscar();
		return redirect("/metropole_digital/cronograma_execucao/buscar.jsf");
	}
	
	/**
	 * Remove um cronograma
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws DAOException 
	 */
	public String remover() throws DAOException{
		CronogramaExecucaoAulasDao dao =  getDAO(CronogramaExecucaoAulasDao.class);
		CronogramaExecucaoAulas c = dao.findByPrimaryKey(getParameterInt("id"));
		dao.remove(c);
		addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Cronograma");
		buscar();
		return redirect("/metropole_digital/cronograma_execucao/buscar.jsf");
	}	
	//FIM NAVEGAÇÃO

	//AJAX:
	/**
	 * Carrega os módulos cadastrados do curso escolhido
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public void carregarModulos(ValueChangeEvent e) throws DAOException{
		if ((Integer)e.getNewValue()==0) {
			modulosCombo = Collections.emptyList();
		}else{
			cronograma.getCurso().setId((Integer)e.getNewValue());
			CronogramaExecucaoAulasDao cronogramaDao = getDAO(CronogramaExecucaoAulasDao.class);
			modulosCombo = toSelectItems(cronogramaDao.findByCursoTecnico((Integer)e.getNewValue()),"id", "descricao");
		}
	}
//	
	/**
	 * Calcula a carga horária das disciplinas do cronograma
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param e
	 * @return
	 * @throws Exception
	 */
	public void calcularCargaHoraria(ValueChangeEvent e) throws Exception{
			chDisciplina = new ArrayList<Integer>();
			chSemana = new ArrayList<Integer>();
				 	
			//Atualiza chDisciplina
			int somaLinha = 0;
			Integer contadorLinha=0, contadorColuna;
			int[] somaSemana = new int[tabelaCargaHoraria.get(0).size()];
			
			Integer linha = (Integer)e.getComponent().getAttributes().get("linha");
			Integer coluna = (Integer)e.getComponent().getAttributes().get("coluna");
			for (List<CargaHorariaSemanalDisciplina> l: tabelaCargaHoraria) {
				contadorColuna=0;
				somaLinha = 0;
				for (CargaHorariaSemanalDisciplina chsd : l) {
					if (e.getNewValue()==null && contadorLinha.equals(linha) && contadorColuna.equals(coluna)) {
						chsd.setCargaHoraria(0);
					}
					if (chsd.getCargaHoraria()==null) {
						chsd.setCargaHoraria(0);
					}
					
					if (contadorLinha.equals(linha) && contadorColuna.equals(coluna) && e.getNewValue() != null) {
							somaLinha = somaLinha + (Integer) e.getNewValue();
							somaSemana[contadorColuna] = somaSemana[contadorColuna] + (Integer) e.getNewValue();
					}else{
						somaLinha = somaLinha + chsd.getCargaHoraria();
						somaSemana[contadorColuna] = somaSemana[contadorColuna] + chsd.getCargaHoraria();
					}
					contadorColuna++;
				}
				chDisciplina.add(somaLinha);
				contadorLinha++;
			}
			
			chSemana = new ArrayList<Integer>();
			chTotal = 0;
			int i = 0;
			for (PeriodoAvaliacao p : cronograma.getPeriodosAvaliacao()) {
				chSemana.add(somaSemana[i]);
				chTotal = chTotal + somaSemana[i];
				p.setChTotalPeriodo(somaSemana[i]);
				i++;
			}
		}
	
	//MÉTODOS AUXILIARES
	/**
	 * Verifica se a carga horária digitada no cronograma é igual a carga horária da disciplina;
	 * 
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 * 
	 */
	private void validarCargaHoraria(){
		int i=0;
		for (ComponenteCurricular cc: cronograma.getModulo().getDisciplinas()){
			if (cc.getChTotal()!=chDisciplina.get(i)) {
				erros.addErro("A carga Horária da disciplina "+cc.getNome()+" ("+cc.getChTotal()+ ") está diferente da carga horária informada ("+chDisciplina.get(i)+")");
			}
			i++;
		}
		Boolean isChNaoInformado = false;
		if (!hasErrors()) {
			for (List<CargaHorariaSemanalDisciplina> linha: tabelaCargaHoraria) {
				for (CargaHorariaSemanalDisciplina chsd : linha) {
					if(chsd.getCargaHoraria()==null){
						isChNaoInformado = true;
						break;
					}
				}
			}
			if (isChNaoInformado) {
				erros.addErro("É necessário preencher toda Carga Horária dos períodos!");
			}
			
		}
		
	}
	
	/**
	 * Limpa os dados do cronograma
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 * 
	 */
	private void limparDadosCronograma(){
		chDisciplina = new ArrayList<Integer>();
		modulosCombo = new ArrayList<SelectItem>(0);
		cronograma = new CronogramaExecucaoAulas();
		cronograma.setCurso(new Curso());
		cronograma.setModulo(new Modulo());
		cronograma.setUnidadeTempo(new UnidadeTempo());
		isCadastroNovo = true;
		idCronograma = 0;
		listaCronogramas = Collections.emptyList();
		filtroAno = null;
		filtroPeriodo = null;
		
	}
	
	/**
	 * Cadastra ou Atualiza um Cronograma de Execução
	 * 
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws DAOException
	 */
	private void salvarCronograma() throws DAOException{
		CronogramaExecucaoAulasDao cronoDao = new CronogramaExecucaoAulasDao();
		try {
			if (cronograma!=null && cronograma.getDataInicio()!= null && cronograma.getDataFim()!= null) {
				cronoDao.createOrUpdate(cronograma);
				cronoDao.findCHTotalPeriodoByCronograma(cronograma.getId());
				for (List<CargaHorariaSemanalDisciplina> linha: tabelaCargaHoraria) {
					for (CargaHorariaSemanalDisciplina chsd : linha) {
						if(chsd.getCargaHoraria()==null){
							chsd.setCargaHoraria(0);
						}
						cronoDao.createOrUpdate(chsd);
					}
				}
			}
			
		} finally {
			cronoDao.close();
		}
	}
	
	/**
	 * Carrega dos períodos de avaliação do Cronograma
	 * 
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws DAOException
	 */
	private void carregaPeriodosAvalicao() throws DAOException{
		//Calcula a quantidade de dias entre os períodos
		int qtdDias =  (int) ((cronograma.getDataFim().getTime()-cronograma.getDataInicio().getTime())/86400000);
		
		//unidade tempo Ex: dia, semana, mês.... 
		UnidadeTempo unidadeTempo= getDAO(CronogramaExecucaoAulasDao.class).findByPrimaryKey(cronograma.getUnidadeTempo().getId(), UnidadeTempo.class);
		
		//Quantidade de Períodos
		Integer qtdPeriodos = (qtdDias/unidadeTempo.getValorEmDias())+1;
			
		//Adiciona a lista de períodos
		PeriodoAvaliacao periodoAvaliacao = null;
		Calendar dataTemp = Calendar.getInstance();
		for (int i = 0; i < qtdPeriodos; i++) {
			periodoAvaliacao = new PeriodoAvaliacao();
			periodoAvaliacao.setNumeroPeriodo(i+1);
			if (i==0) {
				dataTemp.setTimeInMillis(cronograma.getDataInicio().getTime());
			}else{
				dataTemp.add(Calendar.DAY_OF_MONTH, 1);
			}
			periodoAvaliacao.setDataInicio(dataTemp.getTime());
			dataTemp.add(Calendar.DAY_OF_MONTH,unidadeTempo.getValorEmDias()-1);
			
			periodoAvaliacao.setDatafim(dataTemp.getTime());
			periodoAvaliacao.setCronogramaExecucaoAulas(cronograma);
			
			cronograma.getPeriodosAvaliacao().add(periodoAvaliacao);
		}
	}
	
	/**
	 * Retorna a quantidade de períodos do cronograma
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 */
	public Integer getQuantidadePeriodos(){
		return cronograma.getPeriodosAvaliacao().size();
	}
	
	/**
	 * Retorna a quantidade de disciplinas do cronograma
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 */
	public Integer getQuantidadeDisciplinas(){
		return cronograma.getModulo().getDisciplinas().size();
	}
	
	/**
	 * Retorna a quantidade das cargas horárias semanais das disciplinas cadastradas no cronograma. 
	 * 
     * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/metropole_digital/</li>
	 *	</ul>
	 *
	 * @param 
	 * @return
	 * @throws
	 */
	public Integer getSizeChsdList(){ 
		return chsdList.size();
	}
	
	//GETTERES AND SETTERS
	public CronogramaExecucaoAulas getCronograma() {
		return cronograma;
	}
	public void setCronograma(CronogramaExecucaoAulas cronograma) {
		this.cronograma = cronograma;
	}	
	public List<SelectItem> getCursosCombo() {
		return cursosCombo;
	}
	public void setCursosCombo(List<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}
	public List<SelectItem> getModulosCombo() {
		return modulosCombo;
	}
	public void setModulosCombo(List<SelectItem> modulosCombo) {
		this.modulosCombo = modulosCombo;
	}
	public List<SelectItem> getPeriodicidadesCombo() {
		return periodicidadesCombo;
	}
	public void setPeriodicidadesCombo(List<SelectItem> periodicidadesCombo) {
		this.periodicidadesCombo = periodicidadesCombo;
	}
	public List<CargaHorariaSemanalDisciplina> getChsdList() {
		return chsdList;
	}
	public void setChsdList(List<CargaHorariaSemanalDisciplina> chsdList) {
		this.chsdList = chsdList;
	}
	public List<List<CargaHorariaSemanalDisciplina>> getTabelaCargaHoraria() {
		return tabelaCargaHoraria;
	}
	public void setTabelaCargaHoraria(
			List<List<CargaHorariaSemanalDisciplina>> tabelaCargaHoraria) {
		this.tabelaCargaHoraria = tabelaCargaHoraria;
	}
	public List<Integer> getChDisciplina() {
		return chDisciplina;
	}
	public void setChDisciplina(List<Integer> chDisciplina) {
		this.chDisciplina = chDisciplina;
	}
	public List<CronogramaExecucaoAulas> getListaCronogramas() {
		return listaCronogramas;
	}
	public void setListaCronogramas(List<CronogramaExecucaoAulas> listaCronogramas) {
		this.listaCronogramas = listaCronogramas;
	}

	public List<Integer> getChSemana() {
		return chSemana;
	}

	public void setChSemana(List<Integer> chSemana) {
		this.chSemana = chSemana;
	}

	public int getChTotal() {
		return chTotal;
	}

	public void setChTotal(int chTotal) {
		this.chTotal = chTotal;
	}

	public int getIdCronograma() {
		return idCronograma;
	}

	public void setIdCronograma(int idCronograma) {
		this.idCronograma = idCronograma;
	}

	public Boolean getIsCadastroNovo() {
		return isCadastroNovo;
	}

	public void setIsCadastroNovo(Boolean isCadastroNovo) {
		this.isCadastroNovo = isCadastroNovo;
	}

	public Boolean getIsAlterar() {
		return isAlterar;
	}

	public void setIsAlterar(Boolean isAlterar) {
		this.isAlterar = isAlterar;
	}

	public int getIdModulo() {
		return idModulo;
	}

	public void setIdModulo(int idModulo) {
		this.idModulo = idModulo;
	}

	public Integer getFiltroAno() {
//		if (filtroAno==0) {
//			return null;
//		}
		return filtroAno;
	}

	public void setFiltroAno(Integer filtroAno) {
//		if (filtroAno==null) {
//			this.filtroAno= 0;
//		}else{
//			this.filtroAno= filtroAno;
//		}
		
		this.filtroAno = filtroAno;
	}

	public Integer getFiltroPeriodo() {
//		if (filtroPeriodo==0) {
//			return null;
//		}
		return filtroPeriodo;
	}

	public void setFiltroPeriodo(Integer filtroPeriodo) {
		
//		if (filtroPeriodo==null) {
//			this.filtroPeriodo = 0;
//		}else{
//			this.filtroPeriodo = filtroPeriodo;
//		}
		this.filtroPeriodo = filtroPeriodo;
	}
	
	
}


