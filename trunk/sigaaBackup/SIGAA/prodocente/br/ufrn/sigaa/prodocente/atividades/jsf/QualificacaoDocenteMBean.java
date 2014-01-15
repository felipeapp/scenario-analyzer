/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.prodocente.QualificacaoDocenteDao;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.DisciplinaQualificacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.QualificacaoDocente;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoParecer;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoQualificacao;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
@Component("qualificacao")
@Scope("request")
public class QualificacaoDocenteMBean extends 
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.QualificacaoDocente> {

	private String disciplina;
	private String conceito;
	private Collection<DisciplinaQualificacao> listaDisciplinaQualificacao;

	/** Construtor padrão */
	public QualificacaoDocenteMBean() {
		clear();
	}

	/** Serve para limpar os dados para o cadastro de uma nova qualificação de um docente. */
	private void clear(){
		obj = new QualificacaoDocente();
		obj.setPais(new Pais());
		obj.setServidor(new Servidor());
		obj.setTipoParecer(new TipoParecer());
		listaDisciplinaQualificacao = new ArrayList<DisciplinaQualificacao>();
	}
	
	@Override
	public String getDirBase() {
		return "/prodocente/atividades/QualificacaoDocente";
	}
	
	/** Serve para limpar os dados é direcionar o docente para a tela de cadastro. */
	@Override
	public String preCadastrar() {
		clear();
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(QualificacaoDocente.class, "id", "descricao");
	}

	public Collection<SelectItem> getConceitos() {
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem("A", "A");
		itens.add(item);
		item = new SelectItem("B", "B");
		itens.add(item);
		item = new SelectItem("C", "C");
		itens.add(item);
		item = new SelectItem("D", "D");
		itens.add(item);
		item = new SelectItem("E", "E");
		itens.add(item);

		return itens;
	}

	public Collection<SelectItem> getQualificacoes() {
		ArrayList<SelectItem> qualificacoes = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem("G","Graduação");
		qualificacoes.add(item);
		item = new SelectItem("C","Curso de Atualização Pedagógica");
		qualificacoes.add(item);
		item = new SelectItem("E","Especialização");
		qualificacoes.add(item);
		item = new SelectItem("M","Mestrado");
		qualificacoes.add(item);
		item = new SelectItem("D","Doutorado");
		qualificacoes.add(item);
		item = new SelectItem("P","Pós-Doutorado");
		qualificacoes.add(item);
		item = new SelectItem("O","Outra");
		qualificacoes.add(item);

		return qualificacoes;
	}

	@Override
	protected void afterCadastrar() {
		obj = new QualificacaoDocente();
		conceito = "";
		disciplina = "";
		listaDisciplinaQualificacao = null;

		obj = new QualificacaoDocente();
		obj.setPais(new Pais());
		obj.setServidor(new Servidor());
		obj.setTipoParecer(new TipoParecer());

		resetBean();
	}

	@Override
	public void afterAtualizar() throws ArqException {
		super.afterAtualizar();
		
		if (obj.getDisciplinaQualificacao() != null && !obj.getDisciplinaQualificacao().isEmpty()) {
			setListaDisciplinaQualificacao( obj.getDisciplinaQualificacao() );
		}
		
		obj.setTipoParecer( obj.getTipoParecer() == null ? new TipoParecer() : obj.getTipoParecer() );
	}
	
	@Override
	public String atualizar() throws ArqException {
		setId();
		setObj( getGenericDAO().findByPrimaryKey(obj.getId(), QualificacaoDocente.class) );		
		if ( ValidatorUtil.isNotEmpty( obj.getAtivo() ) && !obj.getAtivo()  ) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		} else
			return super.atualizar();
	}
	
	public void adicionarDisciplina(ActionEvent e) {
		if (listaDisciplinaQualificacao == null)
			listaDisciplinaQualificacao = new ArrayList<DisciplinaQualificacao>();
		DisciplinaQualificacao dis = new DisciplinaQualificacao();

		validateDisciplina();
		if (!hasErrors()) {
			dis.setConceito(conceito);
			dis.setDisciplina(disciplina);
			dis.setQualificacaoDocente(obj);
			
			//Verifica se não já foi inserida uma disciplina com o mesmo nome
			Boolean naoTemIgual = true;
			for (Iterator<DisciplinaQualificacao> iter = listaDisciplinaQualificacao.iterator(); iter.hasNext();) {
				DisciplinaQualificacao disciplina = iter.next();
				if (disciplina.getDisciplina().equalsIgnoreCase(dis.getDisciplina())) {
					erros = new ListaMensagens();
					addMensagemErro("Disciplina já presente na Qualificação do Docente.");
					naoTemIgual = false;
				}
			}
			if (naoTemIgual)
				listaDisciplinaQualificacao.add(dis);
			//---//

			conceito = "";
			disciplina = "";
			dis = null;
		}
	}

	@Override
	public String cancelar() {
		if (getConfirmButton().equals("Cadastrar")) {
			validar = null;
			setConfirmButton(null);
		}
		return super.cancelar();
	}
	
	/**
	 * Serve para validar o cadastro de uma disciplina 
	 */
	private void validateDisciplina() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(disciplina, "Disciplina", erros);
		if (!erros.isEmpty())
			addMensagens(erros);
	}

	public void removerDisciplina(ActionEvent e) {
		Iterator<DisciplinaQualificacao> iter = listaDisciplinaQualificacao.iterator();
		while (iter.hasNext()) {
			DisciplinaQualificacao disciplinaI = iter.next();
			if (disciplinaI.getSelecionado())
				iter.remove();
		}
	}

	@Override
	public void beforeCadastrarAndValidate() {
		obj.setDisciplinaQualificacao(listaDisciplinaQualificacao);

		//existe uma redundância nessa tabela: qualificacao & tipoQualificacao
		TipoQualificacao tipoQualificacao = null;
		if (obj.getQualificacao().equalsIgnoreCase("E"))
			tipoQualificacao = new TipoQualificacao(TipoQualificacao.ESPECIALIZACAO);
		else if (obj.getQualificacao().equalsIgnoreCase("M"))
			tipoQualificacao = new TipoQualificacao(TipoQualificacao.MESTRADO);
		else if (obj.getQualificacao().equalsIgnoreCase("D"))
			tipoQualificacao = new TipoQualificacao(TipoQualificacao.DOUTORADO);
		else if (obj.getQualificacao().equalsIgnoreCase("P"))
			tipoQualificacao = new TipoQualificacao(TipoQualificacao.POS_DOUTORADO);

		obj.setTipoQualificacao(tipoQualificacao);
		
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		super.cadastrar();
		if ( !hasErrors() )
			clear();
		return forward( getFormPage() );
	}
	
	public String getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(String disciplina) {
		this.disciplina = disciplina;
	}

	public String getConceito() {
		return conceito;
	}

	public void setConceito(String conceito) {
		this.conceito = conceito;
	}

	public Collection<DisciplinaQualificacao> getListaDisciplinaQualificacao() {
		return listaDisciplinaQualificacao;
	}

	public void setListaDisciplinaQualificacao(Collection<DisciplinaQualificacao> listaDisciplinaQualificacao) {
		this.listaDisciplinaQualificacao = listaDisciplinaQualificacao;
	}

	/**
	 * Lista os estágios
	 */
	public String listar() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO);
		return forward("/prodocente/atividades/QualificacaoDocente/lista.jsp");
	}

	@Override
	public String getUrlRedirecRemover() {
		return "/sigaa/prodocente/atividades/QualificacaoDocente/lista.jsf";
	}

	public Collection<QualificacaoDocente> getAllDepartamento() throws DAOException {
		QualificacaoDocenteDao dao = getDAO(QualificacaoDocenteDao.class);
		return dao.findByDepartamento( getUsuarioLogado().getVinculoAtivo().getUnidade() );
	}

}
