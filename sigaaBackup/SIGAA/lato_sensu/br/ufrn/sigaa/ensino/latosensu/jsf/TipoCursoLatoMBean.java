package br.ufrn.sigaa.ensino.latosensu.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.latosensu.dominio.TipoCursoLato;

/**
 * Managed Bean de Tipo Curso de Lato-Sensu, responsável pela realização das sequintes
 * operações cadastro, atualização, remoção de um discente.
 * 
 * @author guerethes
 */
@Component @Scope("request")
public class TipoCursoLatoMBean extends SigaaAbstractController<TipoCursoLato>{

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		Collection<TipoCursoLato> tiposCursoLato = 
			getGenericDAO().findByExactField(TipoCursoLato.class, "descricao", obj.getDescricao());
		
		for (TipoCursoLato tipoCursoLato : tiposCursoLato) {
			if (tipoCursoLato.isAtivo() && tipoCursoLato.getId() != obj.getId()) {
				addMensagemErro("Já existe um tipo de curso cadastrado com essa descrição. Por favor, escolha outra descrição.");
				return null;
			}
		}
		return super.cadastrar();
	}
	
	/**
	 * Direcionar para a tela da listagem depois do cadastro.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	@Override
	public String inativar() throws ArqException, NegocioException {
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), TipoCursoLato.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		}
		prepareMovimento(ArqListaComando.DESATIVAR);
		return super.inativar();
	}
	
	@Override
	public String getDirBase() {
		return "/lato/tipo_curso";
	}
	
	/** Construtor padrão. */
	public TipoCursoLatoMBean() {
		obj = new TipoCursoLato();
	}
	
	/** Retornar combo com todos os tipos de Curso Lato */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return getAllAtivo(TipoCursoLato.class, "id", "descricao");
	}
	
}