package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.AreaConhecimentoTecn;

/**
 * Controlador para as operações sobre as Áreas de conhecimento Tecnologicas.
 * @author Jean Guerethes
 */
@Component @Scope("request")
public class AreaConhecimentoTecnMBean extends SigaaAbstractController<AreaConhecimentoTecn> {

	/** Construtor Padrão */
	public AreaConhecimentoTecnMBean(){
		obj = new AreaConhecimentoTecn();
		obj.setArea( new AreaConhecimentoCnpq() );
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		Collection<AreaConhecimentoTecn> areasCadastradas = getGenericDAO().findAll(AreaConhecimentoTecn.class);
		for (AreaConhecimentoTecn cursosTecnologicos : areasCadastradas) {
			if (cursosTecnologicos.getArea().equals(obj.getArea())) {
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Área de Conhecimento");
				break;
			}
		}
		
		if (hasErrors())
			return null;
		else
			return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		setId();
		setObj( getGenericDAO().findByPrimaryKey(obj.getId(), AreaConhecimentoTecn.class) );
		if ( isEmpty( obj ) ) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return cancelar();
		} else {
			prepareMovimento(ArqListaComando.REMOVER);
			return super.remover();
		}
	}
	
	@Override
	public String atualizar() throws ArqException {
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		setId();
		
		obj = getGenericDAO().findAndFetch(obj.getId(), AreaConhecimentoTecn.class, "curso");

		setConfirmButton("Alterar");
		afterAtualizar();

		return forward(getFormPage());
	}
	
	@Override
	public String getDirBase() {
		return "/pesquisa/areaTecnologica";
	}

}