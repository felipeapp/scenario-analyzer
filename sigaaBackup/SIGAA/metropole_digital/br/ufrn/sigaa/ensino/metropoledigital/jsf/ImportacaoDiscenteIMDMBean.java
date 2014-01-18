package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeAluno;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ImportacaoDiscenteIMDDao;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.ensino.tecnico.dominio.InscricaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

@Component 
@Scope("session")
public class ImportacaoDiscenteIMDMBean extends SigaaAbstractController<DiscenteLato> {

	// 0 - Opção polo grupo;
	// 1 - Numero de inscrição no PS
	// 2 - CPF
	// 3 - Nome 
	// 4 - Nome da mãe
	// 5 - Nome do pai

	private List<String[]> dados;

	private UploadedFile arquivo;
	
	int idCursoIMD = 96054058;
	
	Collection<InscricaoProcessoSeletivoTecnico> listaSucessos = new ArrayList<InscricaoProcessoSeletivoTecnico>();
	
	Collection<InscricaoProcessoSeletivoTecnico> listaErros = new ArrayList<InscricaoProcessoSeletivoTecnico>();
	
	InscricaoProcessoSeletivoTecnico inscricao = new InscricaoProcessoSeletivoTecnico();

	public ImportacaoDiscenteIMDMBean() {
		obj = new DiscenteLato();
		obj.setDiscente(new Discente());
	}

	public String preImportar() {
		return forward("/metropole_digital/importacao/form.jsp");
	}

	public String importar() throws Exception {

		ImportacaoDiscenteIMDDao iDao = new ImportacaoDiscenteIMDDao();

		try {
			InscricaoProcessoSeletivoTecnico inscricao = new InscricaoProcessoSeletivoTecnico();
			
			carregarDados();

		    listaSucessos = new ArrayList<InscricaoProcessoSeletivoTecnico>();
		    listaErros = new ArrayList<InscricaoProcessoSeletivoTecnico>();
			
			for (int i = 1; i < dados.size(); i++) {
				String[] linha = dados.get(i);
				

				if (linha[0] != null && linha[1] != null) {
					
					
					inscricao = iDao.findInscricaoByNumero(Integer.parseInt(linha[1]));
					
					
					DiscenteTecnico discente = new DiscenteTecnico();
					discente.setDiscente(new Discente());

					PessoaDao dao = getDAO(PessoaDao.class);
					Pessoa p = (Pessoa) dao.findMaisRecenteByCPF(Long.parseLong(Formatador.getInstance().parseStringCPFCNPJ(linha[2])));

					
					if (p == null) {
						Pessoa pessoa = new Pessoa();
						pessoa.setId(SincronizadorPessoas.getNextIdPessoa());
						
						pessoa.setNome(inscricao.getPessoa().getNome());
						pessoa.setEmail(inscricao.getPessoa().getEmail());
						pessoa.setTelefone(inscricao.getPessoa().getTelefone());
						pessoa.setCelular(inscricao.getPessoa().getCelular());
						pessoa.setIdentidade(inscricao.getPessoa().getIdentidade());
						pessoa.getIdentidade().setNumero(inscricao.getPessoa().getIdentidade().getNumero());
						pessoa.setCpf_cnpj(inscricao.getPessoa().getCpf_cnpj());
						pessoa.setSexo(inscricao.getPessoa().getSexo());
						pessoa.setDataNascimento(inscricao.getPessoa().getDataNascimento());
						
						pessoa.setNomeMae(inscricao.getPessoa().getNomeMae());
						pessoa.setNomePai(inscricao.getPessoa().getNomePai());
						
						
						
						
						
						pessoa.setTipo(inscricao.getPessoa().getTipo());
						pessoa.getIdentidade().setOrgaoExpedicao(inscricao.getPessoa().getIdentidade().getOrgaoExpedicao());
						pessoa.setPaisOrigem(inscricao.getPessoa().getPaisOrigem());
						pessoa.setEndereco(inscricao.getPessoa().getEndereco());
						pessoa.setTipoRaca(inscricao.getPessoa().getTipoRaca());
						pessoa.setEstadoCivil(inscricao.getPessoa().getEstadoCivil());
						
						if(pessoa.getSexo() != 'M' && pessoa.getSexo() != 'F'){
							pessoa.setSexo('M');
						}
						
						discente.setPessoa(pessoa);
						discente.getDiscente().setObservacao("Importado via COMPERVE");
					} else {
						
						if (p.getNomeMae() == null)
							p.setNomeMae("Não informado");
						if (p.getNomePai() == null)
							p.setNomePai("Não informado");
						
						if(p.getSexo() != 'M' && p.getSexo() != 'F'){
							p.setSexo('M');
						}
						
						discente.getDiscente().setPessoa(p);
					}

					
					
					
					System.out.println("Nome Mãe: " + inscricao.getPessoa().getNomeMae());
					
					
					
					discente.setAnoIngresso( inscricao.getProcessoSeletivo().getAnoEntrada() );
					discente.setPeriodoIngresso(1);
					discente.setCurso( new CursoTecnico (ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL_PARA_CONVOCACAO)) );
					discente.setGestoraAcademica(new Unidade(ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL)));
					discente.setTurmaEntradaTecnico(null);
					discente.setEstruturaCurricularTecnica(null);
					// Discente normal
					discente.setTipoRegimeAluno(new TipoRegimeAluno(2));
					
					discente.setNivel( NivelEnsino.TECNICO );
					discente.setStatus(StatusDiscente.PENDENTE_CADASTRO);
					discente.setTipo(Discente.REGULAR);
					discente.setFormaIngresso(inscricao.getProcessoSeletivo().getFormaIngresso());
					
					ProcessadorDiscente processadorDiscente = new ProcessadorDiscente();
					DiscenteMov discmov = new DiscenteMov();
					discmov.setDiscenteAntigo( false );
					discmov.setCodMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
					discmov.setObjMovimentado(discente);
					discmov.setUsuarioLogado(getUsuarioLogado());
					discmov.setSistema( getSistema() );
					discente = (DiscenteTecnico) processadorDiscente.execute(discmov);
						
					
					ConvocacaoProcessoSeletivoDiscenteTecnico convocacaoDiscente = new ConvocacaoProcessoSeletivoDiscenteTecnico();
					convocacaoDiscente.setConvocacaoProcessoSeletivo(iDao.findPrimeiraConvocacaoByPSOpcao(inscricao.getProcessoSeletivo().getId(), inscricao.getOpcao().getId()));
					convocacaoDiscente.setInscricaoProcessoSeletivo(inscricao);
					convocacaoDiscente.setDiscente(discente);
					convocacaoDiscente.setAno(discente.getAnoIngresso());
					convocacaoDiscente.setPeriodo(discente.getPeriodoIngresso());
					
					getGenericDAO().create(convocacaoDiscente);
					
					listaSucessos.add(inscricao);
					

					processadorDiscente.execute(discmov);

				}
			}
		}  catch (NegocioException e) {
			listaErros.add(inscricao);
			throw e;
			
			
		}
		finally {
			iDao.close();
		}
		
		return forward("/metropole_digital/importacao/lista_resultado.jsp");
	}

	private void carregarDados() throws IOException {
		dados = new ArrayList<String[]>();
		String[] linha;

		if(arquivo != null) {
			BufferedReader StrR = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));
	
			String Str;
			String[] TableLine;
			while ((Str = StrR.readLine()) != null) {
				TableLine = Str.split(";");
				int col = 0;
				linha = new String[3];
				lido: for (String cell : TableLine) {
					linha[col++] = new String(cell.trim().getBytes(), "UTF-8");
					System.out.println(cell.trim() + " | " + linha[col - 1]);
					if (col == 3)
						break lido;
				}
				dados.add(linha);
			}
	
			StrR.close();
		} else {
			addMessage("O arquivo deve ser informado.", TipoMensagemUFRN.ERROR);
		}
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public Collection<InscricaoProcessoSeletivoTecnico> getListaSucessos() {
		return listaSucessos;
	}

	public void setListaSucessos(Collection<InscricaoProcessoSeletivoTecnico> listaSucessos) {
		this.listaSucessos = listaSucessos;
	}

	public Collection<InscricaoProcessoSeletivoTecnico> getListaErros() {
		return listaErros;
	}

	public void setListaErros(Collection<InscricaoProcessoSeletivoTecnico> listaErros) {
		this.listaErros = listaErros;
	}

	
}
