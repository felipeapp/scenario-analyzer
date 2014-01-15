package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoApoioNovosPesquisadoresDao;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioNovosPesquisadores;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.negocio.CronogramaProjetoHelper;

public class ProjetoApoioNovosPesquisadoresValidator {

	public static void validaDadosGerais(ProjetoApoioNovosPesquisadores projeto, ProjetoApoioNovosPesquisadoresDao dao, ListaMensagens lista) throws HibernateException, DAOException {
		
		validateRequiredId(projeto.getEditalPesquisa().getId(), "Edital de Pesquisa", lista);
		validateRequired(projeto.getProjeto().getTitulo(), "Título", lista);
		validateRequired(projeto.getProjeto().getObjetivos(), "Objetivos", lista);
		validateRequired(projeto.getProjeto().getMetodologia(), "Metodologia", lista);
		validateRequired(projeto.getProjeto().getResultados(), "Resultados", lista);
		validateRequired(projeto.getProjeto().getReferencias(), "Referência", lista);
		validateRequired(projeto.getIntegracao(), "Integração", lista);
		
		if (projeto.getEditalPesquisa() != null && projeto.getEditalPesquisa().getId() != 0) {
			Collection<Integer> haProjetoCadastrado = dao.haProjetoCadastrado(projeto.getEditalPesquisa().getId(), projeto.getCoordenador().getId() );
			
			if ( !haProjetoCadastrado.isEmpty() &&  !haProjetoCadastrado.contains(projeto.getId()) )
				lista.addErro("Já foi cadastrado um projeto de Apoio a Novos Pesquisadores para esse edital de pesquisa.");
		}
	}
	
	/**
	 * Faz a validação do orçamento de um Projeto de Novos Pesquisadores. 
	 * 
	 * @param atividade
	 * @param orcamento
	 * @param lista
	 */
	public static void validaAdicionaOrcamento(OrcamentoDetalhado orcamento, ListaMensagens lista) {

		validateRequired(orcamento.getDiscriminacao(), "Discriminação", lista);
		validateRequired(orcamento.getElementoDespesa(), "Selecione um elemento de despesa.", lista);
		validateRequired(orcamento.getQuantidade(), "Quantidade", lista);
		validateRequired(orcamento.getValorUnitario(), "Valor Unitário", lista);

		if (orcamento.getElementoDespesa() != null) {
			validateRequiredId(orcamento.getElementoDespesa().getId(), "Selecione um elemento de despesa.", lista);
		}
		if ((orcamento.getQuantidade() != null)	&& (orcamento.getQuantidade() == 0)) {
			lista.addErro("Quantidade deve ser maior que 0 (zero)");
		}
		if (orcamento.getValorUnitario() == 0) {
			lista.addErro("Valor Unitário deve ser maior que 0 (zero)");
		}
	}
	
	public static void validaCronograma(HttpServletRequest request, TelaCronograma telaCronograma, 
			Projeto projeto, ListaMensagens lista) throws NumberFormatException, Exception {
		
		// Obter objetos cronogramas a partir dos dados do formulário
		String[] calendario = request.getParameterValues("telaCronograma.calendario");
		telaCronograma.setCalendario(calendario);

		String[] atividadesDesenvolvidas = request.getParameterValues("telaCronograma.atividade");
		telaCronograma.setAtividade(atividadesDesenvolvidas);

		// Obtendo atividades desenvolvidas do cronograma a partir da view.
		telaCronograma.definirCronograma(request);
		List<CronogramaProjeto> cronogramas = telaCronograma.getCronogramas();
		for (CronogramaProjeto cronograma : cronogramas) {
			cronograma.setProjeto(projeto);
		}

		projeto.setCronograma(cronogramas);
		lista.addAll( CronogramaProjetoHelper.validate( telaCronograma ) );
		
		// Validar dados gerais do plano de trabalho
		if (ValidatorUtil.isEmpty(calendario)) {
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Cronograma");
		}
		
		if (ValidatorUtil.isEmpty(atividadesDesenvolvidas)) {
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Atividade");
		}
		
	}
	
}