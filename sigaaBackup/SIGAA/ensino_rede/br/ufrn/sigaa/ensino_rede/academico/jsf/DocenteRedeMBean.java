package br.ufrn.sigaa.ensino_rede.academico.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.DadosCursoRedeDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.TipoDocenteRede;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaCampus;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaCampusIesMBean;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

@SuppressWarnings("serial")
@Component("docenteRedeMBean") @Scope("session")
public class DocenteRedeMBean extends EnsinoRedeAbstractController<DocenteRede> implements SelecionaCampus, OperadorDadosPessoais {

	public DocenteRedeMBean() {
		obj = new DocenteRede();
		obj.setPessoa(new Pessoa());
		obj.setTipo(new TipoDocenteRede());
	}
	
	public String iniciarCadastrar() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRO_DOCENTE_REDE);
		
		
		SelecionaCampusIesMBean mBean = getMBean("selecionaCampusRedeMBean");
		mBean.setRequisitor(this);
		return mBean.iniciar();
	}

	@Override
	public String selecionaCampus() throws ArqException {
		return popularDadosPessoais();
	}

	private String popularDadosPessoais() throws SegurancaException, DAOException {
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.CADASTRO_DOCENTE_REDE );
		return dadosPessoaisMBean.popular();
	}

	public String telaDadosPessoais() {
		obj.getPessoa().prepararDados();
		return forward( "/geral/pessoa/dados_pessoais.jsp");
	}

	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CADASTRO_DOCENTE_REDE);
		mov.setObjMovimentado(obj);
		
		try {
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Docente");
		} catch (NegocioException ex) {
			addMensagemErroPadrao();
		}
		
		return cancelar();
	}
	
	@Override
	public void setCampus(CampusIes campus) throws ArqException {
		
		DadosCursoRedeDao dao = getDAO(DadosCursoRedeDao.class);
		DadosCursoRede dadosCurso = dao.findByCampusPrograma(campus, getProgramaRede());
		obj.setDadosCurso(dadosCurso);
	}

	@Override
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	@Override
	public String submeterDadosPessoais() {
		return forward("/ensino_rede/docente_rede/form.jsp");
	}
	
}
