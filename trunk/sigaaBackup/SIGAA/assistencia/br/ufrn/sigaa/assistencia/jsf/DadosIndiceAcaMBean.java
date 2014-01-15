package br.ufrn.sigaa.assistencia.jsf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dao.DadosIndiceAcademicoDao;
import br.ufrn.sigaa.assistencia.dominio.DadosIndiceAcademico;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

@Component @Scope("request")
public class DadosIndiceAcaMBean extends SigaaAbstractController<DadosIndiceAcademico>{
	
	private List<Map<String,Object>> sumarioIndices;
	private Collection<DadosIndiceAcademico> all;
	
	public DadosIndiceAcaMBean() {
		clear();
	}

	private void clear() {
		obj = new DadosIndiceAcademico();
		obj.setMatriz(new MatrizCurricular());
	}
	
	public String iniciarImportacao() throws ArqException {
		clear();
		obj.setAnoReferencia( CalendarUtils.getAnoAtual() );
		prepareMovimento(SigaaListaComando.CADASTRAR_INDICES_ACADEMICOS);
		return forward("/sae/ImportarDadosAcademicos/form.jsp");
	}
	
	public String importarDados() throws HibernateException, ArqException {
		ValidatorUtil.validateRequired(obj.getAnoReferencia(), "Ano", erros);
		if ( hasOnlyErrors() ) return null;
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setRegistroEntrada( getRegistroEntrada() );
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_INDICES_ACADEMICOS);
			execute(mov);
			addMensagemInformation("Índices consolidados com sucesso!");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		return iniciarImportacao();
	}

	public String remover() throws HibernateException, ArqException {
		Integer ano = getParameterInt("ano", 0);
		try {
			clear();
			obj.setAnoReferencia(ano);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setRegistroEntrada( getRegistroEntrada() );
			prepareMovimento(SigaaListaComando.REMOVER_INDICES_ACADEMICOS);
			mov.setCodMovimento(SigaaListaComando.REMOVER_INDICES_ACADEMICOS);
			execute(mov);
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Índices Acadêmicos");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		return listar();
	}
	
	@Override
	public String listar() throws ArqException {
		return forward("/sae/ImportarDadosAcademicos/lista.jsp");
	}
	
	public String visualizar() throws DAOException {
		Integer ano = getParameterInt("ano", 0);
		DadosIndiceAcademicoDao dao = getDAO(DadosIndiceAcademicoDao.class);
		try {
			all = dao.findAllIndicesAno(ano);
		} finally {
			dao.close();
		}
		return forward("/sae/ImportarDadosAcademicos/view.jsp");
	}
	
	@Override
	public Collection<DadosIndiceAcademico> getAllAtivos() throws ArqException {
		DadosIndiceAcademicoDao dao = getDAO(DadosIndiceAcademicoDao.class);
		try {
			return dao.findAllIndicesCadastrados();
		} finally {
			dao.close();
		}
	}
	
	public List<Map<String, Object>> getSumarioIndices() {
		return sumarioIndices;
	}
	
	public void setSumarioIndices(List<Map<String, Object>> sumarioIndices) {
		this.sumarioIndices = sumarioIndices;
	}

	public Collection<DadosIndiceAcademico> getAll() {
		return all;
	}

	public void setAll(Collection<DadosIndiceAcademico> all) {
		this.all = all;
	}
	
}