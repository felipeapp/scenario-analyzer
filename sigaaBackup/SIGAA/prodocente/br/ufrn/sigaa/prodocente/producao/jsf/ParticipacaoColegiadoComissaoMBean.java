/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '29/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoColegiadoComissao;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoComissaoColegiado;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.jsf.MembroComissaoMBean;

/**
 * MBean respons�vel pelo controle geral do cadastro de Participa��o em Colegiados e Comiss�es 
 * na Produ��o Intelectual.
 * 
 * Gerado pelo CrudBuilder
*/

@Scope("session")
@Component("participacaoColegiadoComissao")
public class ParticipacaoColegiadoComissaoMBean extends 
	AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoColegiadoComissao> {

	private boolean cadastroVindoTelaMembroComissao;
	private boolean required = false;
	private Servidor servidorSelecionado;
	private MembroComissao membroSelecionado;

	/**
	 * Construtor padr�o
	 */
	public ParticipacaoColegiadoComissaoMBean() {
		obj = new ParticipacaoColegiadoComissao();
	}

	/**
	 * Gera um cole��o com todos as Participa��es Colegiado Comiss�o.
	 * 
	 * M�todo n�o invocado por JSP�s
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(ParticipacaoColegiadoComissao.class, "id", "descricao");
	}
	
	/**
	 * S�o realizadas alguns valida��es antes do cadastro. 
	 * Se o mesmo vem da tela de cadastro.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /portais/docente/docente.jsf
	 * 
	 */
	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
		obj.setTipoProducao(TipoProducao.PARTICIPACAO_COLEGIADOS_COMISSOES);
		obj.setArea(AreaConhecimentoCnpq.INDEFINIDO);
		obj.setSubArea(AreaConhecimentoCnpq.INDEFINIDO);
		obj.setTitulo("");

		if (cadastroVindoTelaMembroComissao == true)
			obj.setServidor(servidorSelecionado);
		else 
			obj.setServidor(getServidorUsuario());
		
		//Retirar o combo de departamento
		//obj.setDepartamento(new Unidade());
	}
	
	/**
	 * M�todo invocado ap�s se cadastrar um MembroComissao para que o Docente possa 
	 * trabalhar com o form de cadastro do dom�nio ParticipacaoColegiadoComissao.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /portais/docente/docente.jsf
	 * 
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String entradaTelaMembroComisssao(Servidor s, MembroComissao membro)
			throws SegurancaException, ArqException, NegocioException {
		cadastroVindoTelaMembroComissao = true;
		servidorSelecionado = s;
		membroSelecionado = membro; 
		return redirectJSF("prodocente/producao/ParticipacaoColegiadoComissao/form.jsf");
	}
	
	/**
	 * M�todo invocado quando se clica no bot�o Cancelar que �
	 * exibido no form quando se vem do tela de cadastro MembroComissao.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * prodocente/producao/ParticipacaoColegiadoComissao/form.jsf
	 * 
	 * @return
	 */
	public String cancelarVindoTelaMembroComissao() {
		if(membroSelecionado.getPapel() == MembroComissao.MEMBRO_COMISSAO_MONITORIA){
			return redirect("/monitoria/index.jsf");
		} else if (membroSelecionado.getPapel() == MembroComissao.MEMBRO_COMISSAO_INTEGRADA){
			return redirect("/projetos/menu.jsf");
		}else{
			return redirect("/extensao/menu.jsf");
		}
		
	}
	
	/**
	 * M�todo invocado quando se clica no bot�o Cadastrar que �
	 * exibido no form quando se vem do tela de cadastro MembroComissao.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * prodocente/producao/ParticipacaoColegiadoComissao/form.jsf
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrarVindoTelaMembroComissao() throws ArqException, NegocioException {
		if (validarDados()) {
			MembroComissaoMBean bean = getMBean("membroComissao");
			bean.setObj(membroSelecionado);
			bean.cadastrar();
			bean.clearMensagens();
			
			cadastrar();
			obj = new ParticipacaoColegiadoComissao();
			int [] papeis = {SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO, SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO};
			if (getUsuarioLogado().isUserInRole(papeis)){
				return redirect("/extensao/menu.jsf");
			}else{
				return redirect("/monitoria/index.jsf");
			}
		}
		return null;
	}

	/**
	 * Cadastro da Participa��o em Colegiados e Comiss�es. 
	 * O Departamento � atribu�do pelo usu�rio logado.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * prodocente/producao/ParticipacaoColegiadoComissao/form.jsf 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		obj.setDepartamento(getUsuarioLogado().getVinculoAtivo().getUnidade());
		return super.cadastrar();
	}

	/**
	 * Valida os dados atrav�s do m�todo Validate, que se encontra na classe dom�nio. 
	 * 
	 * @return
	 */
	private boolean validarDados() {
		if ( !obj.validate().isEmpty() ) {
			addMensagens(obj.validate().getMensagens());
			return false;
		}
		else 
			return true;
	}

	/**
	 * M�todo n�o invocado por JSP�s
	 */
	public boolean isCadastroVindoTelaMembroComissao() {
		return cadastroVindoTelaMembroComissao;
	}

	/**
	 *  Verifica se o cadastro foi oriundo da tela de cadastro.
	 * 
	 * @param cadastroVindoTelaMembroComissao
	 */
	public void setCadastroVindoTelaMembroComissao(boolean cadastroVindoTelaMembroComissao) {
		this.cadastroVindoTelaMembroComissao = cadastroVindoTelaMembroComissao;
	}

	/**
	 * Depois de cadastrar ele inst�ncia um novo obj. 
	 */
	@Override
	protected void afterCadastrar() {
		obj = new ParticipacaoColegiadoComissao();
		obj.getInstituicao().setId(InstituicoesEnsino.UFRN); 
	}

	/**
	 * O Objeto recebe o p.
	 * 
	 * M�todo n�o invocado por JSP�s
	 * 
	 * @param p
	 */
	public void popularObjeto(Producao p) {
		obj =(ParticipacaoColegiadoComissao) p;
	}
	
	/**
	 * Lista todos os Tipos de Produ��o. 
	 * 
	 * M�todo n�o invocado por JSP�s
	 * 
	 * @param tipoProducao
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getTipoParticipacao(TipoProducao tipoProducao) throws DAOException {
		return getTipoParticipacao(TipoProducao.PARTICIPACAO_COLEGIADOS_COMISSOES);
	}
	
	/**
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/nova_producao.jsp
	 * 
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		
		obj = new ParticipacaoColegiadoComissao();
		obj.getInstituicao().setId(InstituicoesEnsino.UFRN); 
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		setDirBase("/prodocente/producao/");
		if (verificaBloqueio()) {
			return forward("/prodocente/aviso_bloqueio.jsp");
		} else {
			return forward(getFormPage());
		}
	}

	/**
	 * Deixar o campo data fim sem obrigatoriedade apenas para Comiss�o Permanente.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * prodocente/producao/ParticipacaoColegiadoComissao/form.jsf 
	 * 
	 * @param evt
	 */
	public void setRequired(ActionEvent event) {
		required = (obj.getTipoComissaoColegiado().getId() != TipoComissaoColegiado.COMISSAO_PERMANENTE) 
				? true : false;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}
}
