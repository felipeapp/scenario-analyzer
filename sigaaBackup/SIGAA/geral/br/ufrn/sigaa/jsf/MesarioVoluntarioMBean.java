/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 22/05/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.MesarioVoluntario;

/**
 * Managed bean responsável pelo controle dos discentes que desejam ser mesários voluntários.
 * 
 * @author Rafael Gomes
 *
 */
@Component @Scope("request")
public class MesarioVoluntarioMBean extends SigaaAbstractController<MesarioVoluntario>{

	public MesarioVoluntarioMBean() {
		obj = new MesarioVoluntario();
	}
	
	/**
	 * Método responsável por inicializar o cadastro de discentes que desejam ser mesários voluntários.
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		if ( !ParametroHelper.getInstance().getParametroBoolean(ParametrosGerais.MESARIO_VOLUNTARIO_ATIVO) ){
			addMensagemErro("Inscrições para o Mesário Voluntário não disponível.");
			return null;
		}
		
		obj.setDiscenteInscricao(getDiscenteUsuario().getDiscente());
		if (obj.getDiscenteInscricao().getPessoa().getTituloEleitor() != null) {
			if (obj.getDiscenteInscricao().getPessoa().getTituloEleitor().getNumero() != null)
				obj.setTituloEleitor(obj.getDiscenteInscricao().getPessoa().getTituloEleitor().getNumero());
			if (obj.getDiscenteInscricao().getPessoa().getTituloEleitor().getZona() != null)
				obj.setZona(obj.getDiscenteInscricao().getPessoa().getTituloEleitor().getZona());
			if (obj.getDiscenteInscricao().getPessoa().getTituloEleitor().getSecao() != null)
				obj.setSecao(obj.getDiscenteInscricao().getPessoa().getTituloEleitor().getSecao());
		}	
		obj.setAno(CalendarUtils.getAnoAtual());
		
		doValidate();
		if (hasErrors()) return null;
		
		setConfirmButton("Inscrever-se");
		return forward(getFormPage());
	}
	
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {
		obj.setDiscenteInscricao(getDiscenteUsuario().getDiscente());
		obj.setAno(CalendarUtils.getAnoAtual());
	}
	
	@Override
	protected void doValidate() throws ArqException {
		if ( getGenericDAO().findByExactField(MesarioVoluntario.class, 
							new String[]{"discenteInscricao.id","ano"}, 
							new Object[]{obj.getDiscenteInscricao().getId(), obj.getAno()}).size() > 0 ){
			addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Mesário Voluntário");
			return;
		}
	}
	
	@Override
	public String getDirBase() {
		return "/geral/mesario_voluntario";
	}
	
	@Override
	public String forwardCadastrar() {
		return getSubSistema().getLink();
	}
}
