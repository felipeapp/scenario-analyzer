/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/11/2011
 */
package br.ufrn.sigaa.ensino.infantil.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.infantil.dao.FormularioEvolucaoCriancaDao;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioEvolucaoCrianca;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioTurma;
import br.ufrn.sigaa.ensino.infantil.dominio.ItemInfantilFormulario;
import br.ufrn.sigaa.ensino.infantil.dominio.TipoFormaAvaliacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosInfantil;

/**
 * Controlador responsável pelas operações de registro dos Formulários de Evolução da Criança. 
 * 
 * @author Leonardo Campos
 *
 */
@Component("formularioEvolucaoCriancaMBean") @Scope("session")
public class FormularioEvolucaoCriancaMBean extends SigaaAbstractController<FormularioEvolucaoCrianca> {

	private final int CIMA = -1;
	private final int BAIXO = 1;

	private Set<Integer> keys = null;
	
	private Turma turma = new Turma(); 
	private FormularioTurma formularioTurma = new FormularioTurma();
	private Integer profundidade = 0;
	
	private DataModel itens;
	private List<FormularioTurma> formularios = new ArrayList<FormularioTurma>();
	
	private int quantPeriodo = ParametroHelper.getInstance().getParametroInt(ParametrosInfantil.QNT_BIMESTRES);

	private Collection<TipoFormaAvaliacao> formasAvaliacao;
	
	private ItemInfantilFormulario item;
	
	private boolean acessoTurmaVirtual = false;
	
	/** 
	 * Construtor padrão.
	 */
	public FormularioEvolucaoCriancaMBean() {
		clear();
	}

	private void clear() {
		obj = new FormularioEvolucaoCrianca();
		obj.setPeriodo(1);
		obj.setItens( new LinkedList<ItemInfantilFormulario>() );
		acessoTurmaVirtual = false;
		formasAvaliacao = new ArrayList<TipoFormaAvaliacao>(); 
	}
	
	public String iniciarFormularioTurma() throws HibernateException, ArqException {
		int idTurma = getParameterInt("idTurma", 0);
		int idComponente = getParameterInt("idComponente", 0);
		return iniciarFormularioTurma(idTurma, idComponente,false);
	}

	public String iniciarFormularioTurma(int idTurma, int idComponente, boolean turmaVirtual)
			throws ArqException, DAOException {
		clear();
		acessoTurmaVirtual = turmaVirtual;
		FormularioEvolucaoCriancaDao dao = getDAO(FormularioEvolucaoCriancaDao.class);
		prepareMovimento(SigaaListaComando.CADASTRAR_FORMULARIO_EVOLUCAO);
		
		// Verifica se já possui formulário para a turma
		carregarFormulario(idTurma); 
		reordenar();
		turma = dao.findByPrimaryKey(idTurma, Turma.class);
		
		if(isNotEmpty(obj)) {
			getObj().setComponente(turma.getDisciplina());
			itens = new ListDataModel(obj.getItensPeriodo(obj, false));
			itemInicialBimestre();
			return telaFormularioEvolucaoCrianca();
		}
		
		// Busca formulários de outras turmas do mesmo componente curricular
		List<FormularioTurma> lista = dao.findByComponenteTurma(idComponente);
		if(isNotEmpty(lista) && isNotEmpty(obj) && !obj.getItensPeriodo(obj, false).isEmpty()) {
			getObj().setComponente(turma.getDisciplina());
			setFormasAvaliacao( dao.findAllAtivos(TipoFormaAvaliacao.class, "opcoes") );
			setFormularios(lista);
			itens = new ListDataModel(obj.getItensPeriodo(obj, false));
			return telaListaFormularios();
		}
		
		clear();
		getObj().setComponente(turma.getDisciplina());
		setFormasAvaliacao( dao.findAllAtivos(TipoFormaAvaliacao.class, "opcoes") );
		setTurma( dao.findByPrimaryKey(idTurma, Turma.class) );
		getTurma().getDocentesTurmas().iterator();
		return novoFormulario();
	}

	private FormularioEvolucaoCrianca carregarFormulario(int idTurma) throws DAOException {
		FormularioEvolucaoCriancaDao dao = getDAO(FormularioEvolucaoCriancaDao.class);
		try {
			setObj( dao.findByTurma(idTurma, obj.getId()) );
			if ( obj != null ) {
				itens = new ListDataModel(obj.getItensPeriodo(obj, false));
				obj.setComponente(turma.getDisciplina());
			}
			
			
			
			return obj;
		} finally {
			dao.close();
		}
	}

	/**
	 * Inicia o preenchimento de um novo formulário.
	 * @return
	 */
	public String novoFormulario() {
		setObj(new FormularioEvolucaoCrianca());
		setFormularioTurma(new FormularioTurma());
		getFormularioTurma().setTurma(getTurma());
		getFormularioTurma().setFormulario(getObj());
		obj.setPeriodo(1);
		getObj().addItem(new ItemInfantilFormulario(), 0, obj.getPeriodo());
		itemInicialBimestre();
		itens = new ListDataModel(obj.getItensPeriodo(obj, false));
		return telaFormularioEvolucaoCrianca();
	}

	private void rowData() {
		try {
			Integer row = getParameterInt("ordem");
			itens.setRowIndex(row);
			if ( itens.getRowCount() == 0 ) {
				addMensagemErro("A operação já havia sido concluída. Por favor, reinicie os procedimentos.");
				redirectJSF(getSubSistema().getLink());
			} else {
				item = (ItemInfantilFormulario) itens.getRowData();
			}
		} catch (Exception e) {
			addMensagemErro("A operação já havia sido concluída. Por favor, reinicie os procedimentos.");
			redirectJSF(getSubSistema().getLink());
		}
	}

	private ItemInfantilFormulario rowAddData( int ordem, int profundidade ) {
		itens.setRowIndex(ordem);
		ItemInfantilFormulario item = (ItemInfantilFormulario) itens.getRowData();
		return item;
	}
	
	/**
	 * Adiciona um item ao formulário no mesmo nível do item atual.
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void adicionarItem() throws ArqException, NegocioException {
		Integer ordem = getParameterInt("ordem");
		profundidade = getParameterInt("profundidade");
		if ( getParameterInt("periodo") != null ) {
			obj.setPeriodo( getParameterInt("periodo") );
			ordem = ( obj.getItensPeriodo(obj, false).size() - 1);
		} 
		if ( itens == null || itens.getRowCount() == 0 ) {
			getObj().addItem(new ItemInfantilFormulario(0), 0, obj.getPeriodo());
		} else {
			gravar();
			if ( !hasErrors() ) {
				ItemInfantilFormulario item = rowAddData(ordem, profundidade);
				item.setEditavel(false);
				// TODO inserir na posição do próximo com a mesma profundidade.
				getGenericDAO().initialize(item.getItem().getFormaAvaliacao());
				getObj().addItem(new ItemInfantilFormulario(profundidade), item.getOrdem() + 1, item.getPeriodo());
				reordenar();
				itemInicialBimestre();
			}
		}
		
		itens = new ListDataModel(obj.getItensPeriodo(obj, false));
	}
	
	/**
	 * Altera a posição do item no formulário, permutando com o item anterior.
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void subirItem() throws ArqException, NegocioException {
		rowData();
		if(item.getOrdem() > 0)
			swapItens(item, CIMA);
		if ( hasErrors() ) {
			carregarFormulario(turma.getId());
		} else {
			itemInicialBimestre();
			itens = new ListDataModel(obj.getItensPeriodo(obj, false));
		}
	}
	
	/**
	 * Altera a posição do item no formulário, permutando com o item posterior.
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void descerItem() throws ArqException, NegocioException {
		rowData();
		if(item.getOrdem() + 1 < getObj().getItens().size())
			swapItens(item, BAIXO);
		if ( hasErrors() )
			carregarFormulario(turma.getId());
		else {
			itemInicialBimestre();
			itens = new ListDataModel(obj.getItensPeriodo(obj, false));
		}
	}
	
	/**
	 * Salva as informações do item desabilitando a edição.
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void salvarItem() throws ArqException, NegocioException {
		rowData();
		reordenar();
		gravar();
	}

	private void selecionarTodos() {
		for (ItemInfantilFormulario it : getObj().getItens()){
			it.setEditavel(false);
			it.setSelecionado(true);
		}
	}
	
	/**
	 * Habilita o item para edição das suas informações.
	 * @return
	 */
	public void editarItem() {
		rowData();
		if ( !hasErrors() )
			item.setEditavel(true);
	}
	
	/**
	 * Remove o item do formulário.
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void removerItem() throws ArqException, NegocioException {
		rowData();
		if ( hasErrors() )
			carregarFormulario(turma.getId());
		else {
			getObj().getItens().remove(item.getOrdem());
			reordenar();
			gravar();
			itens = new ListDataModel(obj.getItensPeriodo(obj, false));
		}
	}
	
	/**
	 * Permuta a posição no formulário do item atual com seu vizinho anterior/posterior.
	 * @param item
	 * @param vizinho
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private void swapItens(ItemInfantilFormulario item, int vizinho) throws ArqException, NegocioException {
		int indiceVizinho = item.getOrdem() + vizinho;
		ItemInfantilFormulario itemVizinho = getObj().getItens().get(indiceVizinho);
		//Troca apenas o layout
		String aux = item.getItem().getDescricao();
		item.getItem().setDescricao(itemVizinho.getItem().getDescricao());
		itemVizinho.getItem().setDescricao(aux);
		for(ItemInfantilFormulario sel: getObj().getItens())
			sel.setSelecionado(true);
		reordenar();
		gravar();
	}
	
	/**
	 * Reordena os itens do formulário conforme sua posição na lista.
	 */
	private void reordenar() {
		int i = 0;
		for(ItemInfantilFormulario item: getObj().getItens())
			item.setOrdem(i++);
	}
	
	public void gravar() throws ArqException, NegocioException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(getObj());
		mov.setObjAuxiliar(getFormularioTurma());
		mov.setCodMovimento(getUltimoComando());
		try {
			if ( obj.getId() > 0 ) {
				prepareMovimento(SigaaListaComando.ALTERAR_FORMULARIO_EVOLUCAO);
				mov.setCodMovimento(SigaaListaComando.ALTERAR_FORMULARIO_EVOLUCAO);
			} else {
				prepareMovimento(SigaaListaComando.CADASTRAR_FORMULARIO_EVOLUCAO);
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_FORMULARIO_EVOLUCAO);
			}
			reordenar();
			selecionarTodos();
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
	}

	public void copiarBimestre() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(getObj());
		mov.setObjAuxiliar(getFormularioTurma());
		mov.setCodMovimento(getUltimoComando());
		try {
			prepareMovimento(SigaaListaComando.DUPLICAR_FORMULARIO_EVOLUCAO);
			mov.setCodMovimento(SigaaListaComando.DUPLICAR_FORMULARIO_EVOLUCAO);
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		gravar();
		addMensagemInformation("Formulário Cadastrado com sucesso.");
		if (!isAcessoTurmaVirtual())
			return listar();
		else 
			return cancelar();
	}
	
	public void hasObs() {
		if ( !isEmpty( obj.getItens() ) ) {
			boolean hasObs = false;
			for (int i = 0; i < obj.getItens().size(); i++) {
				ItemInfantilFormulario item = getObj().getItens().get(i);
				if ( !item.isConteudo() ) {
					hasObs = item.getItem().isTemObservacao();
				} else if ( !item.isConteudo() ) {
					obj.getItens().get(i-1).setExibirTextArea(hasObs);
					hasObs = item.getItem().isTemObservacao();
				} else if ( ( obj.getItens().size() - 1 ) == i ) {
					obj.getItens().get(i).setExibirTextArea(hasObs);
				} else {
					obj.getItens().get(i).setExibirTextArea(false);
				}
			}
		}
	}
	
	public String copiarBimestrerAnterior() throws ArqException {
		getFormularioTurma().setTurma(turma);
		copiarBimestre();
		int periodo = obj.getPeriodo();
		clear();
		setObj( carregarFormulario(turma.getId()) );
		if (obj != null)
			obj.setComponente(turma.getDisciplina());
		itemInicialBimestre();
		obj.setPeriodo(periodo);
		itens = new ListDataModel(obj.getItensPeriodo(obj, false));
		reordenar();
		return telaFormularioEvolucaoCrianca();
	}
	
	public String proximoBimestre() throws ArqException, NegocioException {
		if ( !haItemBimestre() ) {
			addMensagemErro("É necessário informar pelo menos um item.");
			return null;
		}
		if ( obj.getPeriodo() != quantPeriodo ) {
			gravar();
			if ( hasErrors() ) return null;
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Ficha de Avaliação - " + obj.getPeriodo() + "° Bimestre");
			obj.setPeriodo(obj.getPeriodo()+1);
			getObj().desabilitarEdicao();
			if ( !hasErrors() ) {
				itemInicialBimestre();
				itens = new ListDataModel(obj.getItensPeriodo(obj, false));
				reordenar();
			}
			return telaFormularioEvolucaoCrianca();
		} else {
			gravar();
			if ( hasErrors() ) return null;
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Ficha de Avaliação - " + obj.getPeriodo() + "° Bimestre");
			obj.desabilitarEdicao();
			setObj( carregarFormulario(turma.getId()) );
			if ( isEmpty(obj) ) clear();
			hasObs();
			itens = new ListDataModel(obj.getItens());
			obj.setPeriodo( quantPeriodo + 1 );
			itemInicialBimestre();
			return telaViewFormularios();
		}
	}
	
	private boolean haItemBimestre() {
		return obj.getItensPeriodo(obj, false).size() > 0;
	}

	public String anteriorBimestre(){
		obj.setPeriodo( obj.getPeriodo()-1 );
		itens = new ListDataModel(obj.getItensPeriodo(obj, false));
		itemInicialBimestre();
		return telaFormularioEvolucaoCrianca();
	}
	
	public boolean getPrimeiroBimestre() {
		return obj.getPeriodo() == 1;
	}

	public void itemInicialBimestre() {
		if ( obj.getPeriodo() > quantPeriodo ) {
		int bimetreAtual = 0;
			for (int i = 0; i < obj.getItens().size(); i++) {
				ItemInfantilFormulario item = obj.getItens().get(i);
				if ( i == 0 || bimetreAtual != item.getPeriodo() ) {
					item.setExibirBimestre(true);
					bimetreAtual = item.getPeriodo();
				}
			}
		}
	}
	
	public boolean isVazia(){
		return itens.getRowCount() == 0;
	}
	
	public String listar() throws ArqException {
		TurmaInfantilMBean mBean = getMBean("turmaInfantilMBean");
		mBean.listar();
		mBean.setBuscaFichaEvolucao(false);
		return forward(ConstantesNavegacaoInfantil.TURMA_INFANTIL_LISTA);
	}
	
	public String listarTurmasDocente() throws ArqException {
		TurmaInfantilMBean mBean = getMBean("turmaInfantilMBean");
		mBean.carregarTurmasDocente();
		mBean.setBuscaFichaEvolucao(false);
		if ( hasErrors() )
			return null;
		return forward(ConstantesNavegacaoInfantil.VIEW_TURMA_DOCENTE);
	}
	
	/**
	 * Encaminha para a tela de criação do formulário de evolução da criança.
	 * @return
	 */
	public String telaFormularioEvolucaoCrianca() {
		return forward(ConstantesNavegacaoInfantil.FORMULARIO_EVOLUCAO_CRIANCA_FORM);
	}
	
	/**
	 * Encaminha para a listagem de formulários.
	 * @return
	 */
	public String telaListaFormularios() {
		return forward(ConstantesNavegacaoInfantil.FORMULARIO_EVOLUCAO_CRIANCA_LISTA);
	}

	/**
	 * Encaminha para a listagem de formulários.
	 * @return
	 */
	public String telaViewFormularios() {
		return forward(ConstantesNavegacaoInfantil.VIEW_EVOLUCAO_CRIANCA_LISTA);
	}

	public List<FormularioTurma> getFormularios() {
		return formularios;
	}

	public void setFormularios(List<FormularioTurma> formularios) {
		this.formularios = formularios;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Set<Integer> getKeys() {
		return keys;
	}

	public void setKeys(Set<Integer> keys) {
		this.keys = keys;
	}

	public FormularioTurma getFormularioTurma() {
		return formularioTurma;
	}

	public void setFormularioTurma(FormularioTurma formularioTurma) {
		this.formularioTurma = formularioTurma;
	}

	public DataModel getItens() {
		return itens;
	}

	public void setItens(DataModel itens) {
		this.itens = itens;
	}

	public int getProfundidade() {
		return profundidade;
	}

	public void setProfundidade(int profundidade) {
		this.profundidade = profundidade;
	}

	public Collection<TipoFormaAvaliacao> getFormasAvaliacao() throws DAOException {
		if( formasAvaliacao.isEmpty() ){
			FormularioEvolucaoCriancaDao dao = getDAO(FormularioEvolucaoCriancaDao.class);
			try{
				setFormasAvaliacao( dao.findAllAtivos(TipoFormaAvaliacao.class, "opcoes") );
			} finally{
				dao.close();
			}
		}
		return formasAvaliacao;
	}

	public Collection<TipoFormaAvaliacao> getFormasAvaliacaoValidas() throws DAOException {
		if( formasAvaliacao.isEmpty() ){
			FormularioEvolucaoCriancaDao dao = getDAO(FormularioEvolucaoCriancaDao.class);
			try{
				Collection<TipoFormaAvaliacao> formaAva = dao.findAllAtivos(TipoFormaAvaliacao.class, "opcoes");
				Iterator<TipoFormaAvaliacao> it = formaAva.iterator();
				while (it.hasNext()){
					if (it.next().isIndefinida()){
						it.remove();
						break;
					}	
				}
				setFormasAvaliacao( formaAva );
			} finally{
				dao.close();
			}
		}
		return formasAvaliacao;
	}
	
	public void setFormasAvaliacao(Collection<TipoFormaAvaliacao> formasAvaliacao) {
		this.formasAvaliacao = formasAvaliacao;
	}

	public ItemInfantilFormulario getItem() {
		return item;
	}

	public void setItem(ItemInfantilFormulario item) {
		this.item = item;
	}

	public void setAcessoTurmaVirtual(boolean acessoTurmaVirtual) {
		this.acessoTurmaVirtual = acessoTurmaVirtual;
	}

	public boolean isAcessoTurmaVirtual() {
		return acessoTurmaVirtual;
	}
	
}