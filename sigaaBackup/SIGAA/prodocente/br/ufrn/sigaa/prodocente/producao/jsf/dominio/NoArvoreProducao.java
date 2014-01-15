/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '14/11/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf.dominio;

import java.util.ArrayList;

import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.ApresentacaoEmEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.Artigo;
import br.ufrn.sigaa.prodocente.producao.dominio.AudioVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.Banca;
import br.ufrn.sigaa.prodocente.producao.dominio.Capitulo;
import br.ufrn.sigaa.prodocente.producao.dominio.Livro;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoColegiadoComissao;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoComissaoOrgEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoSociedade;
import br.ufrn.sigaa.prodocente.producao.dominio.Patente;
import br.ufrn.sigaa.prodocente.producao.dominio.PremioRecebido;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.PublicacaoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TextoDidatico;
import br.ufrn.sigaa.prodocente.producao.dominio.TextoDiscussao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Classe que encapsula todas as produções de um determinado servidor e retorna
 * em
 *
 * @author Gleydson
 *
 */
public class NoArvoreProducao implements Comparable {

	private Servidor servidor;

	private int totalProducoes;

	private ArrayList<Producao> artigos = new ArrayList<Producao>();

	private ArrayList<Producao> capitulos = new ArrayList<Producao>();

	private ArrayList<Producao> livros = new ArrayList<Producao>();

	private ArrayList<Producao> publicacoesEventos = new ArrayList<Producao>();

	private ArrayList<Producao> textosDidatico = new ArrayList<Producao>();

	private ArrayList<Producao> textosDiscussao = new ArrayList<Producao>();

	private ArrayList<Producao> audioVisuais = new ArrayList<Producao>();

	private ArrayList<Producao> exposicao = new ArrayList<Producao>();

	private ArrayList<Producao> montagens = new ArrayList<Producao>();

	private ArrayList<Producao> programacoesVisual = new ArrayList<Producao>();

	private ArrayList<Producao> bancasCursos = new ArrayList<Producao>();

	private ArrayList<Producao> bancasConcursos = new ArrayList<Producao>();

	private ArrayList<Producao> maquetes = new ArrayList<Producao>();

	private ArrayList<Producao> patentes = new ArrayList<Producao>();

	private ArrayList<Producao> apresentacaoEmEventos = new ArrayList<Producao>();

	private ArrayList<Producao> premiosRecebidos = new ArrayList<Producao>();

	private ArrayList<Producao> bolsasObtidas = new ArrayList<Producao>();

	private ArrayList<Producao> visitasCientificas = new ArrayList<Producao>();

	private ArrayList<Producao> participacaoComissao = new ArrayList<Producao>();

	private ArrayList<Producao> participacaoSociedade = new ArrayList<Producao>();

	private ArrayList<Producao> participacaoColegiado = new ArrayList<Producao>();

	public void addProducao(Producao producao) {

		totalProducoes++;

		if (producao instanceof Artigo)
			artigos.add(producao);
		else if (producao instanceof Capitulo)
			capitulos.add(producao);
		else if (producao instanceof Livro)
			livros.add(producao);
		else if (producao instanceof PublicacaoEvento)
			publicacoesEventos.add(producao);
		else if (producao instanceof TextoDidatico)
			textosDidatico.add(producao);
		else if (producao instanceof TextoDiscussao)
			textosDiscussao.add(producao);
		else if (producao instanceof AudioVisual)
			audioVisuais.add(producao);
		else if (producao.getTipoProducao().equals(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS))
			exposicao.add(producao);
		else if (producao.getTipoProducao().equals(TipoProducao.MAQUETES_PROTOTIPOS_OUTROS))
			maquetes.add(producao);
		else if (producao.getTipoProducao().equals(TipoProducao.MONTAGENS))
			montagens.add(producao);
		else if (producao.getTipoProducao()
				.equals(TipoProducao.PROGRAMACAO_VISUAL))
			programacoesVisual.add(producao);
		else if (producao instanceof Banca) {
			Banca b = (Banca) producao;
			if (b.getTipoBanca().getId() == TipoBanca.CURSO)
				bancasCursos.add(producao);
			else if (b.getTipoBanca().getId() == TipoBanca.CONCURSO)
				bancasConcursos.add(producao);
		}
		/*else if (producao instanceof MaquetePrototipoOutro)
			maquetes.add(producao);
		*/
		else if (producao instanceof Patente)
			patentes.add(producao);
		else if (producao instanceof ApresentacaoEmEvento)
			apresentacaoEmEventos.add(producao);
		else if (producao instanceof PremioRecebido)
			premiosRecebidos.add(producao);
		else if (producao.getTipoProducao().equals(TipoProducao.BOLSA_OBTIDA))
			bolsasObtidas.add(producao);
		else if (producao.getTipoProducao().equals(TipoProducao.VISITA_CIENTIFICA))
			visitasCientificas.add(producao);
		else if (producao instanceof ParticipacaoComissaoOrgEventos)
			participacaoComissao.add(producao);
		else if (producao instanceof ParticipacaoSociedade)
			participacaoSociedade.add(producao);
		else if (producao instanceof ParticipacaoColegiadoComissao)
			participacaoColegiado.add(producao);

	}

	private static final String NO_SERVIDOR = "servidor";

	private static final String PRODUCAO_VALIDADA = "validado";

	private static final String PRODUCAO_PENDENTE = "producaoPendente";

	private static final String PRODUCAO_INVALIDADA = "invalidado";

	private static final String CATEGORIA_PRODUCAO = "foo-folder";

	public static String getValidacaoProducao(Producao p) {

		if(p.getValidado() == null)
			return PRODUCAO_PENDENTE;
		else if(p.getValidado())
			return PRODUCAO_VALIDADA;
		else
			return PRODUCAO_INVALIDADA;
	}

	public TreeNode criaNoProducao(Producao p) {
		return new TreeNodeBase(getValidacaoProducao(p), p.getTitulo(), String
				.valueOf(p.getId()), true);
	}

	@SuppressWarnings("unchecked")
	public TreeNode getArvore() {

		TreeNodeBase servNode = new TreeNodeBase(NO_SERVIDOR, servidor
				.getPessoa().getNome(), false);

		TreeNodeBase pubNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
				"Publicações", false);
		servNode.getChildren().add(pubNode);

		boolean publicacoes = false;

		if (artigos.size() > 0) {
			TreeNodeBase artigosNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
					"Artigos, Periódicos, Jornais e Similares", false);
			for (Producao p : artigos) {
				artigosNode.getChildren().add(criaNoProducao(p));
			}
			pubNode.getChildren().add(artigosNode);
			publicacoes = true;
		}

		if (capitulos.size() > 0) {
			TreeNodeBase capitulosNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
					"Capítulos de Livros", false);
			for (Producao p : capitulos) {
				capitulosNode.getChildren().add(criaNoProducao(p));
			}
			pubNode.getChildren().add(capitulosNode);
			publicacoes = true;
		}

		if (livros.size() > 0) {
			TreeNodeBase livrosNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
					"Livros", false);
			for (Producao p : livros) {
				livrosNode.getChildren().add(criaNoProducao(p));
			}
			pubNode.getChildren().add(livrosNode);
			publicacoes = true;
		}

		if (publicacoesEventos.size() > 0) {
			TreeNodeBase publicacaoNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
					"Publicação em Eventos", false);
			for (Producao p : publicacoesEventos) {
				publicacaoNode.getChildren().add(criaNoProducao(p));
			}
			pubNode.getChildren().add(publicacaoNode);
			publicacoes = true;
		}

		if (textosDidatico.size() > 0) {
			TreeNodeBase textosDidaticosNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO, "Textos Didáticos", false);
			for (Producao p : textosDidatico) {
				textosDidaticosNode.getChildren().add(criaNoProducao(p));
			}
			pubNode.getChildren().add(textosDidaticosNode);
			publicacoes = true;
		}

		if (textosDiscussao.size() > 0) {
			TreeNodeBase textosDiscussaoNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO, "Textos pra Discussão", false);
			for (Producao p : textosDiscussao) {
				textosDiscussaoNode.getChildren().add(criaNoProducao(p));
			}
			pubNode.getChildren().add(textosDiscussaoNode);
			publicacoes = true;
		}

		if (!publicacoes)
			servNode.getChildren().remove(pubNode);

		// Artísticas, Literárias ou Visuais

		TreeNodeBase artisticaNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
				"Artísticas, Literárias ou Visuais", false);
		servNode.getChildren().add(artisticaNode);

		boolean artistica = false;

		if (audioVisuais.size() > 0) {
			TreeNodeBase audioVisuaisNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO, "Audiovisuais", false);
			for (Producao p : audioVisuais) {
				audioVisuaisNode.getChildren().add(criaNoProducao(p));
			}
			artisticaNode.getChildren().add(audioVisuaisNode);
			artistica = true;
		}

		if (exposicao.size() > 0) {
			TreeNodeBase exposicoesNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
					"Exposições ou Apresentações Artísticas", false);
			for (Producao p : exposicao) {
				exposicoesNode.getChildren().add(criaNoProducao(p));
			}
			artisticaNode.getChildren().add(exposicoesNode);
			artistica = true;
		}

		if (montagens.size() > 0) {

			TreeNodeBase montagensNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
					"Montagens", false);
			for (Producao p : montagens) {
				montagensNode.getChildren().add(criaNoProducao(p));
			}
			artisticaNode.getChildren().add(montagensNode);
			artistica = true;

		}
		if (programacoesVisual.size() > 0) {

			TreeNodeBase programacaoVisualNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO, "Programação Visual", false);
			for (Producao p : programacoesVisual) {
				programacaoVisualNode.getChildren().add(criaNoProducao(p));
			}
			artisticaNode.getChildren().add(programacaoVisualNode);
			artistica = true;
		}

		if (!artistica)
			servNode.getChildren().remove(artisticaNode);

		// Bancas Examinadoras
		TreeNodeBase bancasNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
				"Bancas Examinadoras/Seleção para Curso", false);
		servNode.getChildren().add(bancasNode);

		boolean bancas = false;

		if (bancasCursos.size() > 0) {
			TreeNodeBase bancaCursoNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
					"Bancas de Cursos / Seleções", false);
			for (Producao p : bancasCursos) {
				bancaCursoNode.getChildren().add(criaNoProducao(p));
			}
			bancasNode.getChildren().add(bancaCursoNode);
			bancas = true;
		}
		if (bancasConcursos.size() > 0) {
			TreeNodeBase bancaConcursoNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO, "Concursos", false);
			for (Producao p : bancasConcursos) {
				bancaConcursoNode.getChildren().add(criaNoProducao(p));
			}
			bancasNode.getChildren().add(bancaConcursoNode);
			bancas = true;
		}
		if (!bancas)
			servNode.getChildren().remove(bancasNode);

		// Tecnológicas

		TreeNodeBase tecnologicasNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
				"Tecnológicas", false);
		servNode.getChildren().add(tecnologicasNode);

		boolean tecnologicas = false;
		if (maquetes.size() > 0) {
			TreeNodeBase maquetesNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
					"Maquetes, Protótipos e Outros (Produção Tecnológica)",
					false);
			for (Producao p : maquetes) {
				maquetesNode.getChildren().add(criaNoProducao(p));
			}
			tecnologicasNode.getChildren().add(maquetesNode);
			tecnologicas = true;
		}

		if (patentes.size() > 0) {

			TreeNodeBase patentesNode = new TreeNodeBase(CATEGORIA_PRODUCAO,
					"Patentes", false);
			for (Producao p : patentes) {
				patentesNode.getChildren().add(criaNoProducao(p));
			}
			tecnologicasNode.getChildren().add(patentesNode);
			tecnologicas = true;
		}

		if (!tecnologicas)
			servNode.getChildren().remove(tecnologicasNode);

		// Outras Atividades
		TreeNodeBase outrasAtividades = new TreeNodeBase(CATEGORIA_PRODUCAO,
				"Outras Atividades", false);
		servNode.getChildren().add(outrasAtividades);

		boolean outrasAtiv = false;

		if (apresentacaoEmEventos.size() > 0) {
			TreeNodeBase apresentacaoEmEventosNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO,
					"Apresentação em Eventos(sem Publicação)", false);
			for (Producao p : apresentacaoEmEventos) {
				apresentacaoEmEventosNode.getChildren().add(criaNoProducao(p));
			}
			outrasAtividades.getChildren().add(apresentacaoEmEventosNode);
			outrasAtiv = true;
		}

		if (premiosRecebidos.size() > 0) {
			TreeNodeBase premiosRecebidosNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO, "Prêmios Recebidos", false);
			for (Producao p : premiosRecebidos) {
				premiosRecebidosNode.getChildren().add(criaNoProducao(p));
			}
			outrasAtividades.getChildren().add(premiosRecebidosNode);
			outrasAtiv = true;
		}

		if (bolsasObtidas.size() > 0) {
			TreeNodeBase bolsaObtidasNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO, "Bolsas Obtidas", false);
			for (Producao p : bolsasObtidas) {
				bolsaObtidasNode.getChildren().add(criaNoProducao(p));
			}
			outrasAtividades.getChildren().add(bolsaObtidasNode);
			outrasAtiv = true;
		}

		if (visitasCientificas.size() > 0) {
			TreeNodeBase visitasCientificasNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO, "Visitas Científicas", false);
			for (Producao p : visitasCientificas) {
				visitasCientificasNode.getChildren().add(criaNoProducao(p));
			}
			outrasAtividades.getChildren().add(visitasCientificasNode);
			outrasAtiv = true;
		}

		if (participacaoComissao.size() > 0) {
			TreeNodeBase participacaoComissaoNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO,
					"Participação em Comissão de Organização de Evento", false);
			for (Producao p : participacaoComissao) {
				participacaoComissaoNode.getChildren().add(criaNoProducao(p));
			}
			outrasAtividades.getChildren().add(participacaoComissaoNode);
			outrasAtiv = true;
		}

		if (participacaoSociedade.size() > 0) {
			TreeNodeBase participacaoSociedadeNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO,
					"Participação em Sociedades Científicas ou Culturais",
					false);
			for (Producao p : participacaoSociedade) {
				participacaoSociedadeNode.getChildren().add(criaNoProducao(p));
			}
			outrasAtividades.getChildren().add(participacaoSociedadeNode);
			outrasAtiv = true;
		}
		if (participacaoColegiado.size() > 0) {
			TreeNodeBase participacaoColegiadoNode = new TreeNodeBase(
					CATEGORIA_PRODUCAO,
					"Participação em Colegiado e Comissões", false);
			for (Producao p : participacaoColegiado) {
				participacaoColegiadoNode.getChildren().add(criaNoProducao(p));
			}
			outrasAtividades.getChildren().add(participacaoColegiadoNode);
			outrasAtiv = true;
		}
		if (!outrasAtiv)
			servNode.getChildren().remove(outrasAtividades);

		return servNode;
	}

	public ArrayList<Producao> getArtigos() {
		return artigos;
	}

	public void setArtigos(ArrayList<Producao> artigos) {
		this.artigos = artigos;
	}

	public ArrayList<Producao> getCapitulos() {
		return capitulos;
	}

	public void setCapitulos(ArrayList<Producao> capitulos) {
		this.capitulos = capitulos;
	}

	public ArrayList<Producao> getLivros() {
		return livros;
	}

	public void setLivros(ArrayList<Producao> livros) {
		this.livros = livros;
	}

	public ArrayList<Producao> getPublicacoesEventos() {
		return publicacoesEventos;
	}

	public void setPublicacoesEventos(ArrayList<Producao> publicacoesEventos) {
		this.publicacoesEventos = publicacoesEventos;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public ArrayList<Producao> getTextosDidatico() {
		return textosDidatico;
	}

	public void setTextosDidatico(ArrayList<Producao> textosDidatico) {
		this.textosDidatico = textosDidatico;
	}

	public ArrayList<Producao> getTextosDiscussao() {
		return textosDiscussao;
	}

	public void setTextosDiscussao(ArrayList<Producao> textosDiscussao) {
		this.textosDiscussao = textosDiscussao;
	}

	public ArrayList<Producao> getApresentacaoEmEventos() {
		return apresentacaoEmEventos;
	}

	public void setApresentacaoEmEventos(
			ArrayList<Producao> apresentacaoEmEventos) {
		this.apresentacaoEmEventos = apresentacaoEmEventos;
	}

	public ArrayList<Producao> getAudioVisuais() {
		return audioVisuais;
	}

	public void setAudioVisuais(ArrayList<Producao> audioVisuais) {
		this.audioVisuais = audioVisuais;
	}

	public ArrayList<Producao> getBancasConcursos() {
		return bancasConcursos;
	}

	public void setBancasConcursos(ArrayList<Producao> bancasConcursos) {
		this.bancasConcursos = bancasConcursos;
	}

	public ArrayList<Producao> getBancasCursos() {
		return bancasCursos;
	}

	public void setBancasCursos(ArrayList<Producao> bancasCursos) {
		this.bancasCursos = bancasCursos;
	}

	public ArrayList<Producao> getBolsasObtidas() {
		return bolsasObtidas;
	}

	public void setBolsasObtidas(ArrayList<Producao> bolsasObtidas) {
		this.bolsasObtidas = bolsasObtidas;
	}

	public ArrayList<Producao> getExposicao() {
		return exposicao;
	}

	public void setExposicao(ArrayList<Producao> exposicao) {
		this.exposicao = exposicao;
	}

	public ArrayList<Producao> getMaquetes() {
		return maquetes;
	}

	public void setMaquetes(ArrayList<Producao> maquetes) {
		this.maquetes = maquetes;
	}

	public ArrayList<Producao> getMontagens() {
		return montagens;
	}

	public void setMontagens(ArrayList<Producao> montagens) {
		this.montagens = montagens;
	}

	public ArrayList<Producao> getParticipacaoColegiado() {
		return participacaoColegiado;
	}

	public void setParticipacaoColegiado(
			ArrayList<Producao> participacaoColegiado) {
		this.participacaoColegiado = participacaoColegiado;
	}

	public ArrayList<Producao> getParticipacaoComissao() {
		return participacaoComissao;
	}

	public void setParticipacaoComissao(ArrayList<Producao> participacaoComissao) {
		this.participacaoComissao = participacaoComissao;
	}

	public ArrayList<Producao> getParticipacaoSociedade() {
		return participacaoSociedade;
	}

	public void setParticipacaoSociedade(
			ArrayList<Producao> participacaoSociedade) {
		this.participacaoSociedade = participacaoSociedade;
	}

	public ArrayList<Producao> getPatentes() {
		return patentes;
	}

	public void setPatentes(ArrayList<Producao> patentes) {
		this.patentes = patentes;
	}

	public ArrayList<Producao> getPremiosRecebidos() {
		return premiosRecebidos;
	}

	public void setPremiosRecebidos(ArrayList<Producao> premiosRecebidos) {
		this.premiosRecebidos = premiosRecebidos;
	}

	public ArrayList<Producao> getProgramacoesVisual() {
		return programacoesVisual;
	}

	public void setProgramacoesVisual(ArrayList<Producao> programacoesVisual) {
		this.programacoesVisual = programacoesVisual;
	}

	public ArrayList<Producao> getVisitasCientificas() {
		return visitasCientificas;
	}

	public void setVisitasCientificas(ArrayList<Producao> visitasCientificas) {
		this.visitasCientificas = visitasCientificas;
	}

	@Override
	public boolean equals(Object obj) {

		NoArvoreProducao outro = (NoArvoreProducao) obj;
		if (outro.getServidor().getId() == getServidor().getId()) {
			return true;
		} else {
			return false;
		}
	}

	public int getTotalProducoes() {
		return totalProducoes;
	}

	public void setTotalProducoes(int totalProducoes) {
		this.totalProducoes = totalProducoes;
	}

	public int compareTo(Object o) {
		NoArvoreProducao outro = (NoArvoreProducao) o;
		return getServidor().getPessoa().getNome().compareTo(
				outro.getServidor().getPessoa().getNome());
	}

}
