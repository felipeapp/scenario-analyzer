/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/11/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.form;

import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.ComponenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteDisciplinaLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaAvaliacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaSelecaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.PublicoAlvoCurso;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;

/**
 * Form Bean para as operações que envolvem a proposta/curso lato sensu.
 * 
 * @author Leonardo
 */
public class CursoLatoForm extends SigaaForm<CursoLato> {

	private AreaConhecimentoCnpq grandeArea;

	private AreaConhecimentoCnpq area;

	private AreaConhecimentoCnpq subarea;

	private AreaConhecimentoCnpq especialidade;

	private PropostaCursoLato proposta;

	private CorpoDocenteCursoLato cursoServidor;

	private TurmaEntradaLato turmaEntrada;

	private ComponenteCurricular disciplina;
	
	private ComponenteCurricular disciplinaAjax;

	private CorpoDocenteDisciplinaLato equipeLato;

	private Collection<CorpoDocenteDisciplinaLato> equipesLato;

	private CoordenacaoCurso coordenador;

	private CoordenacaoCurso viceCoordenador;

	private CoordenacaoCurso secretario;

	private String valor;

	private String dataInicio;

	private String dataFim;

	private String inicioInscSelecao;

	private String fimInscSelecao;

	private String inicioSelecao;

	private String fimSelecao;

	private String dataInicioTurmaEntrada;

	private String dataFimTurmaEntrada;

	private String dataInicioMandatoCoordenador;

	private String dataFimMandatoCoordenador;

	private String dataInicioMandatoViceCoordenador;

	private String dataFimMandatoViceCoordenador;

	private String[] tiposPublicoAlvo;

	private String[] formasAvaliacaoProposta;

	private String[] formasSelecaoProposta;

	private boolean interno = true;
	
	private boolean andamento = false;
	
	private float mediaNota;
	
	private float mediaConceito;
	

	/** Coleções Auxiliares para remoção */
	private Collection<PublicoAlvoCurso> removePublicosAlvo;

	private Collection<FormaSelecaoProposta> removeFormasSelecao;

	private Collection<FormaAvaliacaoProposta> removeFormasAvaliacao;

	private Collection<CorpoDocenteCursoLato> removeCursosServidores;
	
	private Collection<ComponenteCursoLato> removeComponentesCursoLato;

	private DocenteExterno docenteExterno;

	private boolean estrangeiro;
	
	private boolean tecnico;
	
	private Collection<CorpoDocenteDisciplinaLato> docentesDisciplina;

	/** CPF do docente externo	 */
	private String cpf;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	/** Construtor	 */
	public CursoLatoForm(){
		obj = new CursoLato();
		obj.setModalidadeEducacao(new ModalidadeEducacao());

		grandeArea = new AreaConhecimentoCnpq();
		area = new AreaConhecimentoCnpq();
		subarea = new AreaConhecimentoCnpq();
		especialidade = new AreaConhecimentoCnpq();

		proposta = new PropostaCursoLato();
		cursoServidor = new CorpoDocenteCursoLato();
		turmaEntrada = new TurmaEntradaLato();
		disciplina = new ComponenteCurricular();
		disciplinaAjax = new ComponenteCurricular();
		equipeLato = new CorpoDocenteDisciplinaLato();

		equipesLato = new HashSet<CorpoDocenteDisciplinaLato>();

		coordenador = new CoordenacaoCurso();
		viceCoordenador = new CoordenacaoCurso();
		secretario = new CoordenacaoCurso();
		
		removePublicosAlvo = new HashSet<PublicoAlvoCurso>();
		removeFormasSelecao = new HashSet<FormaSelecaoProposta>();
		removeFormasAvaliacao = new HashSet<FormaAvaliacaoProposta>();
		removeCursosServidores = new HashSet<CorpoDocenteCursoLato>();
		removeComponentesCursoLato = new HashSet<ComponenteCursoLato>();
		
		docenteExterno = new DocenteExterno();
		registerSearchData("tipoBusca", "obj.nome");
	}

	/** Getters e Setters	 */

	public String getDataFim() {
		CursoLato curso = obj;
		if( curso.getDataFim() != null ){
			dataFim = Formatador.getInstance().formatarData( curso.getDataFim() );
		}
		return dataFim;
	}

	public void setDataFim(String dataFim) {
		CursoLato curso = obj;
		curso.setDataFim( parseDate(dataFim)  );
		this.dataFim = dataFim;
	}

	public String getDataInicio() {
		CursoLato curso = obj;
		if( curso.getDataInicio() != null ){
			dataInicio = Formatador.getInstance().formatarData( curso.getDataInicio() );
		}
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		CursoLato curso = obj;
		curso.setDataInicio( parseDate(dataInicio)  );
		this.dataInicio = dataInicio;
	}

	public PropostaCursoLato getProposta() {
		return proposta;
	}

	public void setProposta(PropostaCursoLato proposta) {
		this.proposta = proposta;
	}

	public String getFimInscSelecao() {
		PropostaCursoLato proposta = this.proposta;
		if( proposta.getFimInscSelecao() != null ){
			fimInscSelecao = Formatador.getInstance().formatarData( proposta.getFimInscSelecao() );
		}
		return fimInscSelecao;
	}

	public void setFimInscSelecao(String fimInscSelecao) {
		PropostaCursoLato proposta = this.proposta;
		proposta.setFimInscSelecao( parseDate(fimInscSelecao)  );
		this.fimInscSelecao = fimInscSelecao;
	}

	public String getFimSelecao() {
		PropostaCursoLato proposta = this.proposta;
		if( proposta.getFimSelecao() != null ){
			fimSelecao = Formatador.getInstance().formatarData( proposta.getFimSelecao() );
		}
		return fimSelecao;
	}

	public void setFimSelecao(String fimSelecao) {
		PropostaCursoLato proposta = this.proposta;
		proposta.setFimSelecao( parseDate(fimSelecao)  );
		this.fimSelecao = fimSelecao;
	}

	public String getInicioInscSelecao() {
		PropostaCursoLato proposta = this.proposta;
		if( proposta.getInicioInscSelecao() != null ){
			inicioInscSelecao = Formatador.getInstance().formatarData( proposta.getInicioInscSelecao() );
		}
		return inicioInscSelecao;
	}

	public void setInicioInscSelecao(String inicioInscSelecao) {
		PropostaCursoLato proposta = this.proposta;
		proposta.setInicioInscSelecao( parseDate(inicioInscSelecao)  );
		this.inicioInscSelecao = inicioInscSelecao;
	}

	public String getInicioSelecao() {
		PropostaCursoLato proposta = this.proposta;
		if( proposta.getInicioSelecao() != null ){
			inicioSelecao = Formatador.getInstance().formatarData( proposta.getInicioSelecao() );
		}
		return inicioSelecao;
	}

	public void setInicioSelecao(String inicioSelecao) {
		PropostaCursoLato proposta = this.proposta;
		proposta.setInicioSelecao( parseDate(inicioSelecao)  );
		this.inicioSelecao = inicioSelecao;
	}

	public CorpoDocenteCursoLato getCursoServidor() {
		return cursoServidor;
	}

	public void setCursoServidor(CorpoDocenteCursoLato cursoServidor) {
		this.cursoServidor = cursoServidor;
	}

	public TurmaEntradaLato getTurmaEntrada() {
		return turmaEntrada;
	}

	public void setTurmaEntrada(TurmaEntradaLato turmaEntrada) {
		this.turmaEntrada = turmaEntrada;
	}

	public String getDataFimTurmaEntrada() {
		TurmaEntradaLato turmaEntrada = this.turmaEntrada;
		if( turmaEntrada.getDataFim() != null ){
			dataFimTurmaEntrada = Formatador.getInstance().formatarData( turmaEntrada.getDataFim() );
		}
		return dataFimTurmaEntrada;
	}

	public void setDataFimTurmaEntrada(String dataFimTurmaEntrada) {
		TurmaEntradaLato turmaEntrada = this.turmaEntrada;
		turmaEntrada.setDataFim( parseDate(dataFimTurmaEntrada)  );
		this.dataFimTurmaEntrada = dataFimTurmaEntrada;
	}

	public String getDataInicioTurmaEntrada() {
		TurmaEntradaLato turmaEntrada = this.turmaEntrada;
		if( turmaEntrada.getDataInicio() != null ){
			dataInicioTurmaEntrada = Formatador.getInstance().formatarData( turmaEntrada.getDataInicio() );
		}
		return dataInicioTurmaEntrada;
	}

	public void setDataInicioTurmaEntrada(String dataInicioTurmaEntrada) {
		TurmaEntradaLato turmaEntrada = this.turmaEntrada;
		turmaEntrada.setDataInicio( parseDate(dataInicioTurmaEntrada)  );
		this.dataInicioTurmaEntrada = dataInicioTurmaEntrada;
	}

	public String getDataFimMandatoCoordenador() {
		CoordenacaoCurso c = this.coordenador;
		if ( c.getDataFimMandato() != null ){
			dataFimMandatoCoordenador = Formatador.getInstance().formatarData( c.getDataFimMandato() );
		}
		return dataFimMandatoCoordenador;
	}

	public void setDataFimMandatoCoordenador(String dataFimMandato) {
		CoordenacaoCurso c = this.coordenador;
		c.setDataFimMandato( parseDate( dataFimMandato ) );
		this.dataFimMandatoCoordenador = dataFimMandato;
	}

	public String getDataInicioMandatoCoordenador() {
		CoordenacaoCurso c = this.coordenador;
		if ( c.getDataInicioMandato() != null ){
			dataInicioMandatoCoordenador = Formatador.getInstance().formatarData( c.getDataInicioMandato() );
		}
		return dataInicioMandatoCoordenador;
	}

	public void setDataInicioMandatoCoordenador(String dataInicioMandato) {
		CoordenacaoCurso c = this.coordenador;
		c.setDataInicioMandato( parseDate( dataInicioMandato ) );
		this.dataInicioMandatoCoordenador = dataInicioMandato;
	}

	public String getDataFimMandatoViceCoordenador() {
		CoordenacaoCurso c = this.viceCoordenador;
		if ( c.getDataFimMandato() != null ){
			dataFimMandatoViceCoordenador = Formatador.getInstance().formatarData( c.getDataFimMandato() );
		}
		return dataFimMandatoViceCoordenador;
	}

	public void setDataFimMandatoViceCoordenador(String dataFimMandato) {
		CoordenacaoCurso c = this.viceCoordenador;
		c.setDataFimMandato( parseDate( dataFimMandato ) );
		this.dataFimMandatoViceCoordenador = dataFimMandato;
	}

	public String getDataInicioMandatoViceCoordenador() {
		CoordenacaoCurso c = this.viceCoordenador;
		if ( c.getDataInicioMandato() != null ){
			dataInicioMandatoViceCoordenador = Formatador.getInstance().formatarData( c.getDataInicioMandato() );
		}
		return dataInicioMandatoViceCoordenador;
	}

	public void setDataInicioMandatoViceCoordenador(String dataInicioMandato) {
		CoordenacaoCurso c = this.viceCoordenador;
		c.setDataInicioMandato( parseDate( dataInicioMandato ) );
		this.dataInicioMandatoViceCoordenador = dataInicioMandato;
	}

	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	@Override
	public Collection<? extends Curso> customSearch(HttpServletRequest req) throws ArqException {

		final String CURSO = "1";
		
		CursoLatoDao dao = getDAO(CursoLatoDao.class, req);
		Collection<? extends Curso> lista = null;

		try {
			String tipoBusca = getSearchItem(req, "tipoBusca");

			if (CURSO.equals(tipoBusca)) {
				String nome = getSearchItem(req, "obj.nome");
				if(nome != null && !nome.equals("") && nome.length() >= 3){
					lista = dao.findByNome(nome, 0, CursoLato.class, NivelEnsino.LATO, null);
				} else
					addMensagemErro("Informe pelo menos 3 caracteres para a busca.", req);
			} else {
				lista = dao.findAllOtimizado();
			}
		} finally {
			dao.close();
		}

		return lista;
	}

	/**
	 * @return the tiposPublicoAlvo
	 */
	public String[] getTiposPublicoAlvo() {
		return tiposPublicoAlvo;
	}

	/**
	 * @param tiposPublicoAlvo the tiposPublicoAlvo to set
	 */
	public void setTiposPublicoAlvo(String[] tiposPublicoAlvo) {
		this.tiposPublicoAlvo = tiposPublicoAlvo;
	}

	/**
	 * @return the coordenador
	 */
	public CoordenacaoCurso getCoordenador() {
		return coordenador;
	}

	/**
	 * @param coordenador the coordenador to set
	 */
	public void setCoordenador(CoordenacaoCurso coordenador) {
		this.coordenador = coordenador;
	}

	/**
	 * @return the secretario
	 */
	public CoordenacaoCurso getSecretario() {
		return secretario;
	}

	/**
	 * @param secretario the secretario to set
	 */
	public void setSecretario(CoordenacaoCurso secretario) {
		this.secretario = secretario;
	}

	/**
	 * @return the viceCoordenador
	 */
	public CoordenacaoCurso getViceCoordenador() {
		return viceCoordenador;
	}

	/**
	 * @param viceCoordenador the viceCoordenador to set
	 */
	public void setViceCoordenador(CoordenacaoCurso viceCoordenador) {
		this.viceCoordenador = viceCoordenador;
	}

	/**
	 * @return the removePublicosAlvo
	 */
	public Collection<PublicoAlvoCurso> getRemovePublicosAlvo() {
		return removePublicosAlvo;
	}

	/**
	 * @param removePublicosAlvo the removePublicosAlvo to set
	 */
	public void setRemovePublicosAlvo(
			Collection<PublicoAlvoCurso> removePublicosAlvo) {
		this.removePublicosAlvo = removePublicosAlvo;
	}

	/**
	 * @return the formasAvaliacao
	 */
	public String[] getFormasAvaliacaoProposta() {
		return formasAvaliacaoProposta;
	}

	/**
	 * @param formasAvaliacao the formasAvaliacao to set
	 */
	public void setFormasAvaliacaoProposta(String[] formasAvaliacaoProposta) {
		this.formasAvaliacaoProposta = formasAvaliacaoProposta;
	}

	/**
	 * @return the formasSelecao
	 */
	public String[] getFormasSelecaoProposta() {
		return formasSelecaoProposta;
	}

	/**
	 * @param formasSelecao the formasSelecao to set
	 */
	public void setFormasSelecaoProposta(String[] formasSelecaoProposta) {
		this.formasSelecaoProposta = formasSelecaoProposta;
	}

	/**
	 * @return the removeFormasAvaliacao
	 */
	public Collection<FormaAvaliacaoProposta> getRemoveFormasAvaliacao() {
		return removeFormasAvaliacao;
	}

	/**
	 * @param removeFormasAvaliacao the removeFormasAvaliacao to set
	 */
	public void setRemoveFormasAvaliacao(
			Collection<FormaAvaliacaoProposta> removeFormasAvaliacao) {
		this.removeFormasAvaliacao = removeFormasAvaliacao;
	}

	/**
	 * @return the removeFormasSelecao
	 */
	public Collection<FormaSelecaoProposta> getRemoveFormasSelecao() {
		return removeFormasSelecao;
	}

	/**
	 * @param removeFormasSelecao the removeFormasSelecao to set
	 */
	public void setRemoveFormasSelecao(
			Collection<FormaSelecaoProposta> removeFormasSelecao) {
		this.removeFormasSelecao = removeFormasSelecao;
	}

	/**
	 * @return the docenteExterno
	 */
	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	/**
	 * @param docenteExterno the docenteExterno to set
	 */
	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	public Collection<CorpoDocenteCursoLato> getRemoveCursosServidores() {
		return removeCursosServidores;
	}

	public void setRemoveCursosServidores(
			Collection<CorpoDocenteCursoLato> removeCursosServidores) {
		this.removeCursosServidores = removeCursosServidores;
	}

	public String getValor() {
		CursoLato curso = obj;
		if( curso.getValor() != null ){
			valor = Formatador.getInstance().formatarMoeda( curso.getValor() );
		}
		return valor;
	}

	public void setValor(String valor) {
		CursoLato curso = obj;
		curso.setValor( parseValor(valor)  );
		this.valor = valor;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public AreaConhecimentoCnpq getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AreaConhecimentoCnpq especialidade) {
		this.especialidade = especialidade;
	}

	public AreaConhecimentoCnpq getGrandeArea() {
		return grandeArea;
	}

	public void setGrandeArea(AreaConhecimentoCnpq grandeArea) {
		this.grandeArea = grandeArea;
	}

	public AreaConhecimentoCnpq getSubarea() {
		return subarea;
	}

	public void setSubarea(AreaConhecimentoCnpq subarea) {
		this.subarea = subarea;
	}

	public CorpoDocenteDisciplinaLato getEquipeLato() {
		return equipeLato;
	}

	public void setEquipeLato(CorpoDocenteDisciplinaLato equipeLato) {
		this.equipeLato = equipeLato;
	}

	public boolean isEstrangeiro() {
		return estrangeiro;
	}

	public void setEstrangeiro(boolean estrangeiro) {
		this.estrangeiro = estrangeiro;
	}

	public Collection<CorpoDocenteDisciplinaLato> getEquipesLato() {
		return equipesLato;
	}

	public void setEquipesLato(Collection<CorpoDocenteDisciplinaLato> equipesLato) {
		this.equipesLato = equipesLato;
	}

	public boolean isInterno() {
		return interno;
	}

	public void setInterno(boolean interno) {
		this.interno = interno;
	}

	public ComponenteCurricular getDisciplinaAjax() {
		return disciplinaAjax;
	}

	public void setDisciplinaAjax(ComponenteCurricular disciplinaAjax) {
		this.disciplinaAjax = disciplinaAjax;
	}

	public Collection<ComponenteCursoLato> getRemoveComponentesCursoLato() {
		return removeComponentesCursoLato;
	}

	public void setRemoveComponentesCursoLato(
			Collection<ComponenteCursoLato> removeComponentesCursoLato) {
		this.removeComponentesCursoLato = removeComponentesCursoLato;
	}

	public boolean isTecnico() {
		return tecnico;
	}

	public void setTecnico(boolean tecnico) {
		this.tecnico = tecnico;
	}

	public boolean isAndamento() {
		return andamento;
	}

	public void setAndamento(boolean andamento) {
		this.andamento = andamento;
	}

	public float getMediaNota() {
		if(proposta.getMetodoAvaliacao() == MetodoAvaliacao.NOTA)
			mediaNota = proposta.getMediaMinimaAprovacao();
		return mediaNota;
	}

	public void setMediaNota(float mediaNota) {
		if(proposta.getMetodoAvaliacao() == MetodoAvaliacao.NOTA)
			proposta.setMediaMinimaAprovacao(mediaNota);
		this.mediaNota = mediaNota;
	}

	public float getMediaConceito() {
		if(proposta.getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO)
			mediaConceito = proposta.getMediaMinimaAprovacao();
		return mediaConceito;
	}

	public void setMediaConceito(float mediaConceito) {
		if(proposta.getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO)
			proposta.setMediaMinimaAprovacao(mediaConceito);
		this.mediaConceito = mediaConceito;
	}

	public void setDocentesDisciplina(Collection<CorpoDocenteDisciplinaLato> docentesDisciplina) {
		this.docentesDisciplina = docentesDisciplina;
	}

	public Collection<CorpoDocenteDisciplinaLato> getDocentesDisciplina() {
		return docentesDisciplina;
	}

}
