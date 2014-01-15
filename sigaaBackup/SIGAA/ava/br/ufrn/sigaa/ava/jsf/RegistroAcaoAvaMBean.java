/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/08/2010
 *
 */

package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.RegistroAcaoAvaDao;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.RegistroAcaoAva;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * MBean que gerencia os registros de ações sobre entidades da Turma Virtual.
 * @author Fred_Castro
 *
 */

@Component("registroAcaoAva")
@Scope("request")
public class RegistroAcaoAvaMBean extends ControllerTurmaVirtual {
	
	/** O discente para filtrar as consultas. */
	private Usuario usuario;
	
	/** A turma para filtrar as consultas. */
	private Turma turma;
	
	/** O conteúdo para filtrar as consultas. Pode ser uma tarefa, arquivo, indicação de referência, etc. */
	private AbstractMaterialTurma material;
	
	/** O identificador da entidade que está sendo filtrada. */
	private int entidade;
	
	/** Lista de registros de ações realizadas. */
	private List <EntidadeRegistroAcaoAva> registros;
	
	/** Data início. */
	private Date inicio;
	
	/** Data fim. */
	private Date fim;
	
	/** Ação realizada na entidade. */
	private Integer acao;
	
	/** Identificador único da entidade. */
	private Integer idEntidade;
	
	/** Determina se o agrupamento das estatísticas por usuário será realizada. */
	private boolean contar;
	
	/** Relatório em um formato flexível para facilitar a exibição na view. */
	private List <Object []> relatorio;
	
	/** Lista de usuários que realizaram as ações. */
	private List <SelectItem> usuariosCombo;
	
	/** Lista de entidades. */
	private List <SelectItem> entidadesCombo;
	
	/** Lista de ações possíveis. */
	private List <SelectItem> acoesCombo;
	
	/** Lista de turma sque a ação será executada. */
	private List <Turma> turmasSucesso;
	
	/**
	 * Método que deve ser chamado nos MBeans da Turma Virtual para se registrarem as ações sobre as entidades da Turma Virtual.<br/><br/>
 	 * 
	 * Método não invocado por JSPs.
	 * 
	 * @param entidade o número referente a entidade utilizada @see br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva
	 * @param acao o número referente a ação realizada sobre a entidade @see br.ufrn.sigaa.ava.dominio.AcaoAva
	 * @param id o id das entidades utilizadas
	 * @throws ArqException
	 */
	public void registrarAcao (String descricao, EntidadeRegistroAva entidade, AcaoAva acao, int ... ids) {
		
		Comando comandoAtivo = null;
		
		try {
			if (isEmpty(turmasSucesso))
				comandoAtivo = executarRegistroAcao(descricao, entidade, acao, turma().getId(), ids);
			else {
				for (Turma t : turmasSucesso)
					comandoAtivo = executarRegistroAcao(descricao, entidade, acao, t.getId(), ids);
				turmasSucesso = null;
			}
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} catch (ArqException e) {
			notifyError(e);
		} finally {
			try {
				if (comandoAtivo != null)
					prepareMovimento(comandoAtivo);
			} catch (ArqException e){
				notifyError (e);
			}
		}
	}

	/**
	 * Registra a operação na turma virtual e retorna o comando que estava ativo antes do registro, para que este possa ser preparado novamente e não atrapalhar a operação que está sendo executada.
	 * 
	 * @param descricao
	 * @param entidade
	 * @param acao
	 * @param idTurma
	 * @param ids
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Comando executarRegistroAcao(String descricao, EntidadeRegistroAva entidade, AcaoAva acao, int idTurma, int... ids) throws ArqException, NegocioException {
		Comando comandoAtivo = getUltimoComando();
		RegistroAcaoAva object = new RegistroAcaoAva(getUsuarioLogado(), descricao, entidade, acao, idTurma, ids);
		MovimentoCadastro mov = new MovimentoCadastro(object);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_REGISTRO_ACAO_AVA);
		prepareMovimento(SigaaListaComando.CADASTRAR_REGISTRO_ACAO_AVA);
		executeWithoutClosingSession(mov);
		return comandoAtivo != null && isOperacaoAtiva(comandoAtivo.getId()) ? comandoAtivo : null;
	}
	
	/**
	 * Exibe o relatório de acessos da turma virtual.
	 * 
  	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/ava/Relatorios/relatorio_acesso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String relatorioAcessos () throws DAOException {
		
		RegistroAcaoAvaDao dao = null;
		
		turma = turma();
		
		try {
			dao = getDAO(RegistroAcaoAvaDao.class);
			registros = preparaRegistrosParaRelatorio(dao.findRegistrosByGeral(turma, usuario, material, inicio, fim));
			registros = new ArrayList <EntidadeRegistroAcaoAva> ();
			
			return forward("/ava/Relatorios/relatorio_acesso.jsp");
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Prepara os registros para serem exibidos no relatório
	 * 
	 * @param objetos
	 * @return
	 */
	private List <EntidadeRegistroAcaoAva> preparaRegistrosParaRelatorio (List <RegistroAcaoAva> objetos){
		
		List <EntidadeRegistroAcaoAva> rs = new ArrayList <EntidadeRegistroAcaoAva> ();
		
		EntidadeRegistroAcaoAva registro = null;
		
		for (RegistroAcaoAva r : objetos){
			if (registro == null){
				registro = new EntidadeRegistroAcaoAva();
				//registro.setItems(new HashMap <EntidadeRegistroAcaoAva, ItemRegistroAcaoAva>());
				registro.setUltimaOperacao(r.getDataCadastro());
				//registro.getItems().get
			}
			
		}
		
		return rs;
	}
	
	/**
	 * Exibe o re relatório de acesso.
	 * 
  	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/ava/Relatorios/relatorio_acesso.jsp</li>
	 * </ul>
	 */
	public String exibirRelatorio () throws DAOException {
		
		RegistroAcaoAvaDao dao = null;
		if (usuario == null)
			usuario = new Usuario();
		
		if (inicio != null && fim != null && inicio.getTime() > fim.getTime()){
			addMensagemErro("Período Inválido: A data inicial deve ser anterior à data final.");
			return null;
		}	
		try {
			dao = getDAO (RegistroAcaoAvaDao.class);
			
			// Monta o combo com as ações
			if (acoesCombo == null){
				acoesCombo = new ArrayList <SelectItem> ();
				
				for (AcaoAva a : AcaoAva.values())
					if (a.isExibirNosFiltros())
						acoesCombo.add(new SelectItem (a.getValor(), a.getDescricao()));
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				
				inicio = cal.getTime();
				fim = cal.getTime();
			}
			
			// Monta o combo com as entidades
			if (entidadesCombo == null){
				entidadesCombo = new ArrayList <SelectItem> ();
				
				for (EntidadeRegistroAva e : EntidadeRegistroAva.values())
					entidadesCombo.add(new SelectItem (e.getValor(), e.getDescricao()));
			}
			
			// Monta uma lista de selectItems com todos os participantes da turma.
			if (usuariosCombo == null)
				usuariosCombo = dao.findUsuariosQueAcessaramATurma(turma().getId());
			
			
			Integer idUsuario = getParameterInt("idUsuario");
			if (idUsuario != null) usuario.setId(idUsuario);
			
			Integer idAcao = getParameterInt("idAcao");
			if (idAcao != null) acao = idAcao;
			
			Integer idTipoEntidade = getParameterInt("idTipoEntidade");
			if (idTipoEntidade != null) entidade = idTipoEntidade;
			
			// Efetua a busca
			relatorio = dao.findRegistrosByGeral(entidade, acao, idEntidade, usuario.getId(), turma().getId(), inicio, fim, contar);
			
			Object [] atual = null;
			List <Object []> itensContados = new ArrayList <Object []> ();
			
			
			for (Object [] r : relatorio ){
				
				AcaoAva acaoAva = AcaoAva.getAcaoAva(((Number)r[3]).intValue());
				if (acaoAva != null)
					r[3] = acaoAva.getDescricao();
				
				EntidadeRegistroAva entidade = EntidadeRegistroAva.getEntidade(((Number)r[4]).intValue());
				if (entidade != null)
					r[4] = entidade.getDescricao();
				
				if (contar){
					
					if (atual != null){
						if  (!atual[2].equals(r[2]))
							atual = null;
					}
					
					if (atual == null){
						atual = new Object [14];
						for (int i = 0; i < r.length; i++)
							atual[i] = r[i];
						atual[12] = new ArrayList <String> ();
						atual[13] = new ArrayList <Integer> ();
						itensContados.add(atual);
						
						if (atual[2] == null)
							atual[2] = "Sem nome";
					}
					
					boolean encontrado = false;
					for (int i = 0; i < ((List)atual[12]).size(); i++) {
						if (((List)atual[12]).get(i).equals(r[3] + " " + r[4])){
							Integer contador = (Integer) ((List)atual[13]).remove(i);
							contador ++;
							((List)atual[13]).add(i, contador);
							
							encontrado = true;
						}
					}
					
					if (!encontrado){
						((List)atual[12]).add(r[3] + " " + r[4]);
						((List)atual[13]).add(1);
					}
				}
			}
			
			if (contar)
				relatorio = itensContados;			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/ava/Relatorios/form_visualizar_registros.jsp");
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public AbstractMaterialTurma getMaterial() {
		return material;
	}

	public void setMaterial(AbstractMaterialTurma material) {
		this.material = material;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public List<EntidadeRegistroAcaoAva> getRegistros() {
		return registros;
	}

	public void setRegistros(List<EntidadeRegistroAcaoAva> registros) {
		this.registros = registros;
	}

	public int getEntidade() {
		return entidade;
	}

	public void setEntidade(int entidade) {
		this.entidade = entidade;
	}

	public Integer getAcao() {
		return acao;
	}

	public void setAcao(Integer acao) {
		this.acao = acao;
	}

	public Integer getIdEntidade() {
		return idEntidade;
	}

	public void setIdEntidade(Integer idEntidade) {
		this.idEntidade = idEntidade;
	}

	public List<Object[]> getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(List<Object[]> relatorio) {
		this.relatorio = relatorio;
	}

	public List<SelectItem> getUsuariosCombo() {
		return usuariosCombo;
	}

	public void setUsuariosCombo(List<SelectItem> usuariosCombo) {
		this.usuariosCombo = usuariosCombo;
	}

	public List<SelectItem> getEntidadesCombo() {
		return entidadesCombo;
	}

	public void setEntidadesCombo(List<SelectItem> entidadesCombo) {
		this.entidadesCombo = entidadesCombo;
	}

	public List<SelectItem> getAcoesCombo() {
		return acoesCombo;
	}

	public void setAcoesCombo(List<SelectItem> acoesCombo) {
		this.acoesCombo = acoesCombo;
	}

	public boolean isContar() {
		return contar;
	}

	public void setContar(boolean contar) {
		this.contar = contar;
	}

	public void setTurmasSucesso(List <Turma> turmasSucesso) {
		this.turmasSucesso = turmasSucesso;
	}

	public List <Turma> getTurmasSucesso() {
		return turmasSucesso;
	}
}


/** Entidade utilizada no registro da operação realizada.*/
class EntidadeRegistroAcaoAva {
	
	/** Nome da entidade. */
	private String nome;
	
	/** Mapa de com entidades. */
	private Map <EntidadeRegistroAva, ItemRegistroAcaoAva> items;
	
	/** Data da última operação realizada. */
	private Date ultimaOperacao;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getUltimaOperacao() {
		return ultimaOperacao;
	}

	public void setUltimaOperacao(Date ultimaOperacao) {
		this.ultimaOperacao = ultimaOperacao;
	}

	public Map<EntidadeRegistroAva, ItemRegistroAcaoAva> getItems() {
		return items;
	}

	public void setItems(Map<EntidadeRegistroAva, ItemRegistroAcaoAva> items) {
		this.items = items;
	}
}
/** Item de Registro da ação. */
class ItemRegistroAcaoAva {
	
	/** Identificador únido do ítem. */
	private int id;
	
	/** Quantidade de ítens. */
	private int quantidade;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
}