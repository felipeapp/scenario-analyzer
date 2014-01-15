package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.mobile.commons.SigaaTouchAbstractController;

@Component("buscaCursoTouch") @Scope("request")
public class BuscaCursoTouchMBean extends SigaaTouchAbstractController<Curso> {
	
	private Collection<Curso> cursos;
	
	private String nomeBusca;
	
	private ModalidadeEducacao modalidadeEducacaoBusca;
	
	private char nivelBusca;
	
	public BuscaCursoTouchMBean(){
		init();
	}
	
	private void init() {
		obj = new Curso();
		modalidadeEducacaoBusca = new ModalidadeEducacao();
	}

	public String iniciarBusca() {
		return forward("/mobile/touch/public/nivel_curso.jsf");
	}
	
	public String selecionaNivelBusca() throws DAOException {
		char nivel = getParameterChar("nivelCurso");
		
		obj.setNivel(nivel);
		
		return forwardBuscaCursos();
	}

	public String forwardBuscaCursos() {
		return forward("/mobile/touch/public/busca_curso.jsf");
	}

	public String forwardListaCursos() {
		return forward("/mobile/touch/public/lista_cursos.jsf");
	}
	
	public String buscarCursos() throws DAOException {
		CursoDao cursoDao = getDAO(CursoDao.class);
		
		try {
			char nivel = (isEmpty(nivelBusca) || nivelBusca == '\u0000') ? '\u0000' : nivelBusca;
			String nome = isEmpty(nomeBusca) ? null : nomeBusca;
			ModalidadeEducacao modalidadeEducacao = isEmpty(modalidadeEducacaoBusca) ? null : modalidadeEducacaoBusca;
			
			if(nivel == '\u0000' && isEmpty(nome) && isEmpty(modalidadeEducacao)) {
				addMensagemErro("Especifique um parâmetro para realizar a busca.");
				return null;
			}
			
			cursos = cursoDao.findConsultaPublica(null ,nivel, nome, modalidadeEducacao);
			
			if(isEmpty(cursos)) {
				addMensagemErro("Nenhum Curso encontrado com os parâmetros informados.");
				return null;
			}
		} finally {
			cursoDao.close();
		}
		
		return forwardListaCursos();
	}
	
	public String view() throws ArqException {
		setId();
		
		if (obj.getId() > 0) {
			obj = getGenericDAO().findByPrimaryKey(obj.getId(),Curso.class);
		} else {
			addMensagemErro("Curso não selecionado");
			return null;
		}
		
		return forward("/mobile/touch/public/view_curso.jsf");
	}

	public Collection<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(Collection<Curso> cursos) {
		this.cursos = cursos;
	}

	public String getNomeBusca() {
		return nomeBusca;
	}

	public void setNomeBusca(String nomeBusca) {
		this.nomeBusca = nomeBusca;
	}

	public ModalidadeEducacao getModalidadeEducacaoBusca() {
		return modalidadeEducacaoBusca;
	}

	public void setModalidadeEducacaoBusca(ModalidadeEducacao modalidadeEducacaoBusca) {
		this.modalidadeEducacaoBusca = modalidadeEducacaoBusca;
	}

	public char getNivelBusca() {
		return nivelBusca;
	}

	public void setNivelBusca(char nivelBusca) {
		this.nivelBusca = nivelBusca;
	}

}
