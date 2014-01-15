/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/04/2010
 *
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.negocio.MovimentoConclusaoCursoLato;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Controlador responsável por realizar todas as verificações
 * necessárias e finalizar os alunos de um curso de pós-graduação lato sensu.
 * 
 * @author Leonardo Campos
 *
 */
@Component("conclusaoCursoLatoMBean") @Scope("request")
public class ConclusaoCursoLatoMBean extends SigaaAbstractController<CursoLato> {

	private Collection<SelectItem> cursosCombo;

	private Collection<Discente> discentes;
	
	private Integer ano;
	
	private Integer periodo;
	
	public ConclusaoCursoLatoMBean() {
		obj = new CursoLato();
	}
	
	/**
	 * Popula as informações iniciais necessárias para a conclusão de um curso lato sensu.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\WEB-INF\jsp\ensino\latosensu\menu\discente.jsp</li>
	 *   </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		cursosCombo = toSelectItems(getDAO(CursoLatoDao.class).findAllByFilter(SituacaoProposta.ACEITA), "id", "nome");
		prepareMovimento(SigaaListaComando.CONCLUIR_CURSO_LATO);
		setOperacaoAtiva(SigaaListaComando.CONCLUIR_CURSO_LATO.getId());
		return forward(ConstantesNavegacao.FORM_CONCLUSAO_CURSO);
	}
	
	/**
	 * Carrega os dados dos discentes do curso selecionado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\conclusao_curso\form.jsp</li>
	 *   </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public String carregarAlunos() throws DAOException{
		if (!checkOperacaoAtiva(SigaaListaComando.CONCLUIR_CURSO_LATO.getId())) return null;
		Integer idCurso = obj.getId();
		if (idCurso ==  0) {
			addMensagemErro("Selecione um curso da lista de sugestões de curso.");
			return null;
		}
		obj = getGenericDAO().findByPrimaryKey(idCurso, CursoLato.class);
		if (isEmpty(obj)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			obj = new CursoLato(); 
			return null;
		}
		// Populando sugestão de ano-período de conclusão a partir da data de encerramento do curso
		Calendar c = Calendar.getInstance();
		c.setTime(obj.getDataFim());
		ano = c.get(Calendar.YEAR);
		periodo = c.get(Calendar.MONTH) <= Calendar.JUNE ? 1 : 2;
		
		discentes = getDAO(DiscenteDao.class).findDiscentesComVinculoByCurso(idCurso);
		if (isEmpty(discentes)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		return null;
	}
	
	/**
	 * Encerra o presente caso de uso invocando o processador para finalizar o curso e os seus alunos.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\conclusao_curso\form.jsp</li>
	 *   </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String concluirAlunos() throws ArqException, NegocioException{
		if (!checkOperacaoAtiva(SigaaListaComando.CONCLUIR_CURSO_LATO.getId())) return null;
		if(obj.getId() <= 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		
		if(ano == null || periodo == null || ano <= 0 || periodo <= 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano-período");
		
		if(discentes == null || discentes.isEmpty())
			addMensagemErro("Não há alunos para serem concluídos.");
		
		Collection<Discente> discentesSelecionados = new ArrayList<Discente>();
		if(discentes != null){
			for(Discente d: discentes){
				if(d.isSelecionado())
					discentesSelecionados.add(d);
			}
			
			if(discentesSelecionados.isEmpty())
				addMensagemErro("Selecione pelo menos um discente para concluir.");
		}
		
		
		Iterator<Discente> iterator = discentesSelecionados.iterator();
		
		while (iterator.hasNext()) {
			Discente discente = iterator.next();

			
			ListaMensagens erros = VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(discente.getDiscente());
			if(erros.size() > 0){
				addMensagemErro("Não é possível concluir o discente "+discente.getMatriculaNome()+" pois o ele possui empréstimos pendentes nas bibliotecas.");
				iterator.remove();
			}
			
		}
		
		confirmaSenha();
		
		if(hasErrors())
			return null;
		
		MovimentoConclusaoCursoLato mov = new MovimentoConclusaoCursoLato();
		mov.setCodMovimento(SigaaListaComando.CONCLUIR_CURSO_LATO);
		mov.setCursoLato(obj);
		mov.setDiscentes(discentesSelecionados);
		mov.setAno(ano);
		mov.setPeriodo(periodo);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		return cancelar();
	}
	
	// Getters and Setters
	
	public Collection<SelectItem> getCursosCombo() {
		return cursosCombo;
	}

	public void setCursosCombo(Collection<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}

	public Collection<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}
	
	
	
}
