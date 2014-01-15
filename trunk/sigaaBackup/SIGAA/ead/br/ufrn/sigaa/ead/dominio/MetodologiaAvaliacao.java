/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/10 - 19:09:08
 */
package br.ufrn.sigaa.ead.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isAllNotEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Dados das Fichas de Avaliação por curso 
 * @author David Pereira
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name="metodologia_avaliacao", schema="ead")
public class MetodologiaAvaliacao implements Validatable {

	/** Chave primária da metodologia. */
	@Id @GeneratedValue
	private int id;
	
	/** Curso associado Ã  metodologia. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_curso")
	/** Curso associado a metodologia */
	private Curso curso;
	
	/** Itens de avaliação definidos para a metodologia. */
	@OneToMany(mappedBy="metodologia", cascade=CascadeType.ALL)
	private List<ItemAvaliacaoEad> itens;
	
	/** Indica se a metodologia vai permitir o uso de tutores */
	@Column(name="permite_tutor")
	private boolean permiteTutor = true;
	
	/** Quantos porcento da nota do aluno é da avaliação do tutor */
	@Column(name="porcentagem_tutor")
	private Integer porcentagemTutor;
	
	/** Quantos porcento da nota do aluno é da avaliação do professor */
	@Column(name="porcentagem_professor")
	private Integer porcentagemProfessor = 100;
	
	/** Método de avaliação */
	@Column(name="metodo_avaliacao")
	private int metodoAvaliacao;
	
	/** Número total de semanas de aula */
	@Column(name="numero_aulas")
	private String numeroAulas;
	
	/** Semanas de aula que estão habilitadas para avaliação */
	@Column(name="aulas_ativas")
	private String aulasAtivas;
	
	/** Se a metodologia está ativa. */
	private boolean ativa;
	
	/** Data de cadastro da metodologia. */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/** Data que a metodologia foi inativada */
	@Column(name="data_inativacao")
	private Date dataInativacao;
	
	/** Usuário que realizou o cadastro da metodolofia. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_usuario_cadastro")
	private Usuario usuarioCadastro;

	/** Set contendo os ids das {@link #aulasAtivas}. */
	@Transient
	private TreeSet<Integer> aulasAtivasSet;
	
	/** Texto a ser exibido aos tutores no inÃ­cio da ficha de avaliação. */
	@Column(name="cabecalho_ficha")
	private String cabecalhoFicha;
	
	/** Lista de componentes ativos para avaliação */
	@Transient
	private Set<Integer> listAtivos;
	
	/** Ano de início da metodogolia */
	@Column(name = "ano_inicio")
	private Integer anoInicio;
	/** Ano final da metodogolia */
	@Column(name = "ano_fim")
	private Integer anoFim;
	
	/** Período inicial da metodogolia */
	@Column(name = "periodo_inicio")
	private Integer periodoInicio;
	/** Período final da metodogolia */
	@Column(name = "periodo_fim")
	private Integer periodoFim;
	
	/** Referência à metodologia de avaliação anterior */
	@Transient
	private MetodologiaAvaliacao metodologiaAnterior;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<ItemAvaliacaoEad> getItens() {
		return itens;
	}

	public void setItens(List<ItemAvaliacaoEad> itens) {
		this.itens = itens;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}
	
	/**
	 * Valida os dados gerais da metodologia de avaliação
	 * @return
	 */
	public ListaMensagens validarDadosGerais() {
		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(curso, "Curso", erros);
		ValidatorUtil.validateRequiredId(metodoAvaliacao, "Método Avaliação", erros);
		if (isPermiteTutor())
			ValidatorUtil.validateRequired(porcentagemTutor, "Peso do Tutor", erros);
		
		ValidatorUtil.validateRequired(porcentagemProfessor, "Peso do Professor", erros);
		
		if (porcentagemProfessor != null && porcentagemTutor != null &&	porcentagemProfessor + porcentagemTutor != 100) {
			erros.addErro("A soma dos pesos do tutor (se existir) e professor deverá totalizar 100%.");
		}
		
		ValidatorUtil.validateRequired(anoInicio, "Ano iní­cio", erros);
		ValidatorUtil.validateRequired(periodoInicio, "PerÃ­odo iní­cio", erros);
		
		if (metodologiaAnterior != null && anoInicio != null && periodoInicio != null) {
			int anoPeriodoAnterior = (metodologiaAnterior.getAnoFim() * 10 ) + metodologiaAnterior.getPeriodoFim();
			int anoPeriodoAtual = (getAnoInicio() * 10 ) + getPeriodoInicio();
			
			if (anoPeriodoAtual <= anoPeriodoAnterior)
				erros.addErro("O Ano e perí­odo inicial não pode ser menor ou igual a " + metodologiaAnterior.getAnoFim() + "." + metodologiaAnterior.getPeriodoFim());
			
		}
		
		return erros;
	}
	
	/**
	 * Valida os Itens da metodologia de avaliação
	 * @return
	 */
	public ListaMensagens validarItens() {
		ListaMensagens erros = new ListaMensagens();
		
		if (!permiteTutor)
			return new ListaMensagens();
		
		if (metodoAvaliacao == MetodoAvaliacao.DUAS_PROVAS_RECUPERACAO) {
			if (StringUtils.isEmpty(numeroAulas)) {
				ValidatorUtil.validateRequired(null, "Número de Aulas", erros);
			} else {
				String[] aulas = numeroAulas.split(",");
				if (aulas.length != 1 && metodoAvaliacao == MetodoAvaliacao.UMA_PROVA) {
					erros.addErro("O número de aulas está em um formato incorreto.");
				} else if (aulas.length != 2 && metodoAvaliacao == MetodoAvaliacao.DUAS_PROVAS_RECUPERACAO) {
					erros.addErro("O número de aulas está em um formato incorreto.");
				}
			}
		}		
		
		ValidatorUtil.validateRequired(itens, "Item", erros);
		
		if (!itens.isEmpty()) {
			for (int i = 0; i < itens.size(); i++) {
				ItemAvaliacaoEad item = itens.get(i);
				if (StringUtils.isEmpty(item.getNome()))
					erros.addErro("Digite uma descrição para o item " + (i+1));
			}
		}
		
		return erros;
	}
	
	/**
	 * Valida a metodologia de avaliação
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		erros.addAll(validarDadosGerais());
		erros.addAll(validarItens());
		
		return erros;
	}

	public Integer getPorcentagemTutor() {
		return porcentagemTutor;
	}

	public void setPorcentagemTutor(Integer porcentagemTutor) {
		this.porcentagemTutor = porcentagemTutor;
	}

	public Integer getPorcentagemProfessor() {
		return porcentagemProfessor;
	}

	public void setPorcentagemProfessor(Integer porcentagemProfessor) {
		this.porcentagemProfessor = porcentagemProfessor;
	}

	public int getMetodoAvaliacao() {
		return metodoAvaliacao;
	}

	public void setMetodoAvaliacao(int metodoAvaliacao) {
		this.metodoAvaliacao = metodoAvaliacao;
	}
	
	/**
	 * Retorna a descrição associada ao método de avaliação.
	 * 
	 * @return
	 */
	public String getDescricaoMetodoAvaliacao() {
		return isEmpty(metodoAvaliacao) ? "-" : MetodoAvaliacao.getDescricao(metodoAvaliacao);
	}

	/**
	 * Descrição da virgência da metodologia de avaliação.
	 * @return
	 */
	public String getDescricaoAnoPeriodo() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(curso.getNome() + " (");
		
		if (isNotEmpty(anoInicio) && isNotEmpty(periodoInicio)) 
			sb.append(anoInicio + "." + periodoInicio);
		else 
			sb.append("*");
		
		sb.append(" - ");
		
		if (isNotEmpty(anoFim) && isNotEmpty(periodoFim))
			sb.append(anoFim + "." + periodoFim);
		else 
			sb.append("*");
		
		sb.append(")");
		
		return sb.toString();
	}
	
	public String getNumeroAulas() {
		return numeroAulas;
	}

	public void setNumeroAulas(String numeroAulas) {
		this.numeroAulas = numeroAulas;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public String getAulasAtivas() {
		return aulasAtivas;
	}

	public void setAulasAtivas(String aulasAtivas) {
		this.aulasAtivas = aulasAtivas;
	}
	
	/**
	 * Retorna uma lista de {@link SemanaAvaliacao} referente as {@link #aulasAtivas}.
	 * 
	 * @return
	 * @throws NegocioException
	 */
	public List<SemanaAvaliacao> getSemanasAvaliacao() throws NegocioException {
		List<SemanaAvaliacao> semanas = new ArrayList<SemanaAvaliacao>();
		String[] aulas = null;
		
		if (isUmaProva()) {
			if (aulasAtivas != null && !"".equals(aulasAtivas.trim())) {
				aulas = aulasAtivas.split(",");
				
				for (String aula : aulas) {
					int componente = Integer.parseInt(aula);
										
					SemanaAvaliacao semana = new SemanaAvaliacao();
					semana.setMetodologia(this);
					semana.setSemana(componente);
					semana.setUnidade(1);
					semana.setHabilitada(aulaAtiva(componente));
					
					semanas.add(semana);
				}
			}
		} else {
			int contadorAula = 0;
			int contadorUnidade = 0;

			if (numeroAulas == null)
				throw new NegocioException("Não foi possí­vel carregar o número de aulas para avaliação. Por favor, reinicie a operação utilizando os links oferecidos pelo sistema.");
			
			aulas = numeroAulas.split(",");

			for (String aula : aulas) {
				contadorUnidade++;
					
				int numeroAulas = Integer.parseInt(aula.trim());
				for (int i = 0; i < numeroAulas; i++) {
					contadorAula++;
						
					SemanaAvaliacao semana = new SemanaAvaliacao();
					semana.setMetodologia(this);
					semana.setSemana(contadorAula);
					semana.setUnidade(contadorUnidade);
					semana.setHabilitada(aulaAtiva(contadorAula));
					semanas.add(semana);
				}			
			}
		}

		
		return semanas;
	}
	
	/**
	 * Retorna uma lista de {@link SemanaAvaliacao} que estejam habilitadas ou que sejam de 
	 * componentes ativos para avaliação.
	 * 
	 * @return
	 * @throws NegocioException
	 */
	public List<SemanaAvaliacao> getSemanasAvaliacaoAtivas() throws NegocioException {
		List<SemanaAvaliacao> semanas = new ArrayList<SemanaAvaliacao>();
			
		for (SemanaAvaliacao semana : getSemanasAvaliacao())
			if (semana.isHabilitada()) 
				semanas.add(semana);
		
		List<SemanaAvaliacao> listConsolidadas = new ArrayList<SemanaAvaliacao>();

		if (listAtivos != null) {
			for (SemanaAvaliacao semanaAvaliacao : getSemanasAvaliacao()) {
				Iterator<Integer> it = listAtivos.iterator();
				while (it.hasNext()) {
					if (it.next() == semanaAvaliacao.getSemana())
						listConsolidadas.add(semanaAvaliacao);	
				}
			}
			return listConsolidadas;
		}
		else {
			return semanas;
		}
	
	}
	
	/**
	 * Verifica se a aula com o id passado é uma aula ativa, verificando sua presenÃ§a na lista {@link #aulasAtivas}.
	 * 
	 * @param aulaAtual
	 * @return
	 */
	public boolean aulaAtiva(int aulaAtual) {
		if (aulasAtivas != null)
			return getAulasAtivasSet().contains(aulaAtual);
		
		return false;
	}

	/**
	 * Habilita a {@link SemanaAvaliacao} passada, colocando-a nas {@link #aulasAtivas}.
	 * 
	 * @param semana
	 */
	public void habilitar(SemanaAvaliacao semana) {
		getAulasAtivasSet().add(semana.getSemana());
		aulasAtivas = getAulasAtivasSetComoString();
	}

	/**
	 * Desabilita a {@link SemanaAvaliacao} passada, retirando-a das {@link #aulasAtivas}.
	 * 
	 * @param semana
	 */
	public void desabilitar(SemanaAvaliacao semana) {
		getAulasAtivasSet().remove(semana.getSemana());
		aulasAtivas = getAulasAtivasSetComoString();
	}

	/**
	 * Retorna uma string com os ids das {@link #aulasAtivas} separados por ví­rgula.
	 * 
	 * @return
	 */
	private String getAulasAtivasSetComoString() {
		StringBuilder sb = new StringBuilder();
		
		if (getAulasAtivasSet() != null) {
			for (Iterator<Integer> it = getAulasAtivasSet().iterator(); it.hasNext(); ) {
				Integer aula = it.next();
				sb.append(aula);
				if (it.hasNext())
					sb.append(",");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Retorna um set contendo os ids das {@link #aulasAtivas}.
	 * 
	 * @return
	 */
	public TreeSet<Integer> getAulasAtivasSet() {
		if (aulasAtivasSet == null) {
			aulasAtivasSet = new TreeSet<Integer>();
			if (!StringUtils.isEmpty(aulasAtivas)) {
				String[] aulas = aulasAtivas.split(",");
				for (String aulaStr : aulas) {
					Integer aula = Integer.valueOf(aulaStr.trim());
					aulasAtivasSet.add(aula);
				}
			}
		}
		
		return aulasAtivasSet;
	}

	public boolean isUmaProva() {
		return metodoAvaliacao == MetodoAvaliacao.UMA_PROVA;
	}

	public boolean isDuasProvas() {
		return metodoAvaliacao == MetodoAvaliacao.DUAS_PROVAS_RECUPERACAO;
	}
	
	public String getCabecalhoFicha() {
		return cabecalhoFicha;
	}

	public void setCabecalhoFicha(String cabecalhoFicha) {
		this.cabecalhoFicha = cabecalhoFicha;
	}

	/**
	 * Retorna um vetor de inteiros representando os dados do {@link #numeroAulas}.
	 * 
	 * @return
	 */
	public int[] getNumeroAulasInt() {
		if(isUmaProva())
			return null;
		
		String[] aulas = numeroAulas.split(",");
		int[] aulasInt = new int[aulas.length];
		
		for (int i = 0; i < aulasInt.length; i++) {
			aulasInt[i] = Integer.parseInt(aulas[i].trim());
		}
		
		return aulasInt;
	}

	public Set<Integer> getListAtivos() {
		return listAtivos;
	}

	public void setListAtivos(Set<Integer> listAtivos) {
		this.listAtivos = listAtivos;
	}
	
	/**
	 * Retorna o número de aulas de uma determinada unidade passada, ou 0 caso não esteja definido.
	 * 
	 * @param unidade
	 * @return
	 */
	public int getNumeroAulasByUnidade(int unidade) {
		int[] numeroAulasInt = getNumeroAulasInt();
		if(isEmpty(numeroAulasInt))
			return 0;
		return numeroAulasInt[unidade - 1];
	}
	
	/**
	 * Retorna o número de aulas definido para a primeira unidade, ou 0 caso não esteja definido.
	 * 
	 * @return
	 */
	public int getNumeroAulasPrimeiraUnidade() {
		return getNumeroAulasByUnidade(1);
	}

	/**
	 * Retorna a data do período inicial formatado
	 * @return
	 */
	public String getAnoPeriodoInicialFormatado() {
		if (isAllNotEmpty(anoInicio, periodoInicio))
			return anoInicio + "." + periodoInicio;
		else
			return " - ";
	}
	
	/**
	 * Retorna a data do período final formatado
	 * @return
	 */
	public String getAnoPeriodoFimFormatado() {
		if (isAllNotEmpty(anoFim, periodoFim))
			return anoFim + "." + periodoFim;
		else
			return " - ";
	}
	
	public Integer getAnoInicio() {
		return anoInicio;
	}

	public void setAnoInicio(Integer anoInicio) {
		this.anoInicio = anoInicio;
	}

	public Integer getAnoFim() {
		return anoFim;
	}

	public void setAnoFim(Integer anoFim) {
		this.anoFim = anoFim;
	}

	public Integer getPeriodoInicio() {
		return periodoInicio;
	}

	public void setPeriodoInicio(Integer periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	public Integer getPeriodoFim() {
		return periodoFim;
	}

	public void setPeriodoFim(Integer periodoFim) {
		this.periodoFim = periodoFim;
	}

	public boolean isPermiteTutor() {
		return permiteTutor;
	}

	public void setPermiteTutor(boolean permiteTutor) {
		this.permiteTutor = permiteTutor;
	}

	public Date getDataInativacao() {
		return dataInativacao;
	}

	public void setDataInativacao(Date dataInativacao) {
		this.dataInativacao = dataInativacao;
	}

	public MetodologiaAvaliacao getMetodologiaAnterior() {
		return metodologiaAnterior;
	}

	public void setMetodologiaAnterior(MetodologiaAvaliacao metodologiaAnterior) {
		this.metodologiaAnterior = metodologiaAnterior;
	}

}
