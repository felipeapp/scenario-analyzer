/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
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
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.GrupoAtividadesAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.dao.InstituicoesEnsinoDao;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.prodocente.producao.dominio.Patente;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducaoTecnologica;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * Managed Bean para o caso de uso de Patentes (Tecnológicas) da Produção Intelectual
 * 
 * Gerado pelo CrudBuilder
 */
@Component("patente")
@Scope("session")
public class PatenteMBean extends AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.Patente> {

	/** Guarda as instituições Patrocinadoras escolhidas pelo usuário (foi a única forma que encontrei para fazer isso, pois setar essa propriedade em obj não estava dando certo) @author Edson Anibal (ambar@info.ufrn.br) */
	private List<InstituicoesEnsino> patrocinadoras;
	
	private int idInstituicao = InstituicoesEnsino.UFRN;
	/** Guarda as instituições Patrocinadoras escolhidas pelo usuário (foi a única forma que encontrei para fazer isso, pois setar essa propriedade em obj não estava dando certo) @author Edson Anibal (ambar@info.ufrn.br) */
	public List<InstituicoesEnsino> getPatrocinadoras() { return patrocinadoras; }
	/** Guarda as instituições Patrocinadoras escolhidas pelo usuário (foi a única forma que encontrei para fazer isso, pois setar essa propriedade em obj não estava dando certo) @author Edson Anibal (ambar@info.ufrn.br) */
	public void setPatrocinadoras(List<InstituicoesEnsino> patrocinadoras) { this.patrocinadoras = patrocinadoras; }
    //------//

	/** Guarda a Instituição Patrocinadora atualmente selecionada, para que quando o usuário clicar no botão 'add' ela seja adicionada (foi a única forma de adicionar, pois o JSF executava o actionListener do combobox sempre antes do commandButton) @author Edson Anibal (ambar@info.ufrn.br) */
	private InstituicoesEnsino patrocinadoraSelecionadaAdd;
	/** Guarda a Instituição Patrocinadora atualmente selecionada, para que quando o usuário clicar no botão 'add' ela seja adicionada (foi a única forma de adicionar, pois o JSF executava o actionListener do combobox sempre antes do commandButton) @author Edson Anibal (ambar@info.ufrn.br) */
	public InstituicoesEnsino getPatrocinadoraSelecionadaAdd() { return patrocinadoraSelecionadaAdd;	}
	/** Guarda a Instituição Patrocinadora atualmente selecionada, para que quando o usuário clicar no botão 'add' ela seja adicionada (foi a única forma de adicionar, pois o JSF executava o actionListener do combobox sempre antes do commandButton) @author Edson Anibal (ambar@info.ufrn.br) */
	public void setPatrocinadoraSelecionadaAdd(InstituicoesEnsino patrocinadoraSelecionadaAdd) { this.patrocinadoraSelecionadaAdd = patrocinadoraSelecionadaAdd; }
    //----//

	/** Guarda a Instituição Patrocinadora atualmente selecionada, para que quando o usuário clicar no botão 'Deletar' ela seja Deletar (foi a única forma de adicionar, pois o JSF executava o actionListener do combobox sempre antes do commandButton) @author Edson Anibal (ambar@info.ufrn.br) */
	private InstituicoesEnsino patrocinadoraSelecionadaDeletar;
	/** Guarda a Instituição Patrocinadora atualmente selecionada, para que quando o usuário clicar no botão 'add' ela seja adicionada (foi a única forma de adicionar, pois o JSF executava o actionListener do combobox sempre antes do commandButton) @author Edson Anibal (ambar@info.ufrn.br) */
	public InstituicoesEnsino getPatrocinadoraSelecionadaDeletar() { return patrocinadoraSelecionadaDeletar;	}
	/** Guarda a Instituição Patrocinadora atualmente selecionada, para que quando o usuário clicar no botão 'add' ela seja adicionada (foi a única forma de adicionar, pois o JSF executava o actionListener do combobox sempre antes do commandButton) @author Edson Anibal (ambar@info.ufrn.br) */
	public void setPatrocinadoraSelecionadaDeletar(InstituicoesEnsino patrocinadoraSelecionadaDeletar) { this.patrocinadoraSelecionadaDeletar = patrocinadoraSelecionadaDeletar; }
    //----//

	private List<String> idsPatrocinadoraSelecionadaDeletar = new ArrayList<String>();

	/** Atenção: Este MBean está em sessão. */
	public PatenteMBean() {
		 obj = new Patente();
		 getInitMBean();
	}

	/** 
	 * Inicia os valores do MBean (faz o papel do construtor, pois o mesmo está em sessão) 
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/Patente/form.jsp
	 * /prodocente/producao/Patente/lista.jsp
	 * /prodocente/producao/Patente/view.jsp
	 */
	public String getInitMBean() {
		setPatrocinadoras(obj.getPatrocinadora());
		return null;
	}

	/**
	 * Reinicia o MBean para apagar os valores que estão em sessão
	 * 
	 * Método não invocado por JSP´s
	 * 
	 */
	public void resetMBean() {
		obj = new Patente();
		setPatrocinadoras(new ArrayList<InstituicoesEnsino>());
		setPatrocinadoraSelecionadaAdd(new InstituicoesEnsino());
		setPatrocinadoraSelecionadaDeletar(new InstituicoesEnsino());
	}

	/**
	 * Método não invocado por JSP´s
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(Patente.class, "id", "descricao");
	}

	/**
	 * Método não invocado por JSP´s
	 */
	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.PATENTE);
		obj.setServidor(getServidorUsuario());
		obj.setTipoProducaoTecnologica(TipoProducaoTecnologica.NENHUMA);
		obj.setTipoRegiao(new TipoRegiao(TipoRegiao.NAO_INFORMADO));
		obj.setPatrocinadora(getPatrocinadoras());

		super.beforeCadastrarAndValidate();
	}

	/**
	 * Método não invocado por JSP´s
	 */
	@Override
	protected void afterCadastrar() {
		resetMBean();
    }

	/**
	 * Método não invocado por JSP´s
	 */
	@Override
	public void popularObjeto(Producao p) {
		obj = (Patente) p;
		if (obj.getPatrocinadora() != null) {
			for (InstituicoesEnsino instituicao : obj.getPatrocinadora() ) {
				instituicao.getNome();
			}
		}
	}

	/**
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/Patente/form.jsp
	 * 
	 */
	public List<SelectItem> getTipoParticipacao() throws DAOException {
        //tem o tipo participacao igual a publicacoes em eventos
		return getTipoParticipacao(TipoProducao.PUBLICACOES_EVENTOS);
	}


	/**
	 * Quando o formulário é submetido o método que add uma instituição patrocinadora irá adicionar o que estiver
	 * selecionado no combobox. Este método é necessário para informar ao método cancheInstituicaoPatrocinadora que ele
	 * só deve add se tiver sido clicado no botão 'add' e não somente porque o form foi submetido.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/Patente/form.jsp
	 * 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException 
	 */
	public void addPatrocinadora() throws DAOException {
	
		InstituicoesEnsino instituicao = getDAO(InstituicoesEnsinoDao.class).findByPrimaryKey(
				getIdInstituicaoDefault(), InstituicoesEnsino.class);
		if (!getPatrocinadoras().contains(instituicao))
			   getPatrocinadoras().add(instituicao);
		
	}

	/**
	 * Irá deletar da lista de Instituições escolhidas a Instituição que estiver selecionada no combobox.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/Patente/form.jsp
	 * 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void deletarPatrocinadora() {
		List<InstituicoesEnsino> listaTemporaria = new ArrayList<InstituicoesEnsino>();
		//if (getPatrocinadoraSelecionadaDeletar() != null && getPatrocinadoraSelecionadaDeletar().getId() > 0)
		if (idsPatrocinadoraSelecionadaDeletar.size() > 0) {
			for (InstituicoesEnsino patrocinadora : getPatrocinadoras()) {
				for (String idSelecionado : getIdsPatrocinadoraSelecionadaDeletar())
					if (patrocinadora.getId() == Integer.parseInt(idSelecionado))
						listaTemporaria.add(patrocinadora);
			}
			getPatrocinadoras().removeAll(listaTemporaria);
			setPatrocinadoraSelecionadaDeletar(new InstituicoesEnsino()); //após retira a seleção
		}

	}

	/**
	 * Exibe todas Instituições Patrocinadoras escolhidas pelo Usuário
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/Patente/form.jsp
	 * 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Collection<SelectItem> getPatrocinadorasEscolhidas()	{
		return toSelectItems(getPatrocinadoras(), "id", "nome");
	}
	
	/**
	 * @param idInstituicaoDefault the idInstituicaoDefault to set
	 */
	public void setIdInstituicaoDefault(int idInstituicaoDefault) {
		this.idInstituicao = idInstituicaoDefault;
	}
	
	/**
	 * @return the idInstituicaoDefault
	 */
	public int getIdInstituicaoDefault() {
		return idInstituicao;
	}

	/**
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/nova_producao.jsp
	 * /prodocente/producao/Patente/lista.jsp
	 * 
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		resetBean();
		checkChangeRole();
		//setIdInstituicaoDefault(InstituicoesEnsino.UFRN);
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
	 * @param idsPatrocinadoraSelecionadaDeletar the idsPatrocinadoraSelecionadaDeletar to set
	 */
	public void setIdsPatrocinadoraSelecionadaDeletar(List<String> idsPatrocinadoraSelecionadaDeletar) {
		this.idsPatrocinadoraSelecionadaDeletar = idsPatrocinadoraSelecionadaDeletar;
	}
	
	/**
	 * @return the idsPatrocinadoraSelecionadaDeletar
	 */
	public List<String> getIdsPatrocinadoraSelecionadaDeletar() {
		return idsPatrocinadoraSelecionadaDeletar;
	}
	
}
