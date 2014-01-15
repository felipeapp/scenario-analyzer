<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@page import="br.ufrn.sigaa.pesquisa.form.MembroProjetoDiscenteForm"%>

    <ul>
    	<li> Projetos de Pesquisa
    		<ul>
	        <li> <html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=consulta&popular=true&aba=relatorios">Consultar</html:link> </li>
			<li> <html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=financiamentos&popular=true&aba=relatorios"> Projetos Financiados </html:link> </li>
			<li>
				<html:link action="/pesquisa/relatoriosPesquisa?dispatch=popularFinanciamentosSintetico&aba=relatorios">
					Relat�rio Sint�tico de Financiamentos
				</html:link>
			</li>
			<li>
				<html:link action="/pesquisa/editalSintetico?dispatch=popularEditais&aba=relatorios">
					Relat�rio de Submiss�o de Projetos
				</html:link>
			</li>

			<li>
				<html:link action="/pesquisa/distribuirProjetoPesquisa?dispatch=consultaResultadoDistribuicao&aba=relatorios">
					Projetos com avalia��es pendentes
				</html:link>
			</li>
			<li> <html:link action="/pesquisa/avaliarRelatorioProjeto?dispatch=listRelatoriosFinais&popular=true&aba=relatorios"> Relat�rios Finais </html:link></li>
			<li> <a href="${ctx}/pesquisa/relatorios/form_estatisticas_projetos.jsf"> Estat�sticas de Cadastros de Projetos </a></li>
			<li> <a href="${ctx}/pesquisa/relatorios/form_quant_projetos.jsf"> Relat�rio Quantitativo de Projetos </a></li>
			</ul>
		</li>

		<li> Inicia��o Cient�fica
			<ul>
			<li>
				<html:link action="/pesquisa/editalSintetico?dispatch=popularCotas&aba=relatorios">
					Relat�rio de Cotas Solicitadas
				</html:link>
			</li>
			<li>
				<html:link action="/pesquisa/relatorios/quantitativoSolicitacoesCotas?dispatch=iniciar&aba=relatorios">
					Relat�rio Quantitativo de Solicita��es de Bolsas
				</html:link>
			</li>
			<li>
				<html:link action="/pesquisa/relatorios/acompanhamentoCotas?dispatch=iniciar&aba=relatorios">
					Acompanhamento de Distribui��o de Cotas de Bolsas
				</html:link>
			</li>
			<li> <h:commandLink action="#{relatorioPendenciasIndicacao.iniciarRelatorioPendenciasIndicacao}" value="Relat�rio de Pend�ncias de Indica��o" onclick="setAba('relatorios');"/> </li>
			<li> <h:commandLink action="#{relatorioResumosCic.iniciarRelatorioResumosCic}" value="Relat�rio Quantitativo de Resumos CIC"/> </li>
			<li> <h:commandLink action="#{avaliacaoApresentacaoResumoBean.popularRelatorioAvaliadores}" value="Relat�rio de Avaliadores de Trabalhos do CIC" onclick="setAba('relatorios');"/> </li>
			<li> <h:commandLink action="#{avaliacaoApresentacaoResumoBean.popularRelatorioPontuacao}" value="Relat�rio de Premia��o de Trabalhos do CIC" onclick="setAba('relatorios');"/> </li>
			<li> <h:commandLink action="#{relatorioRenovacaoBolsaMBean.selecionaQuantitativoBolsaPibic}" value="Relat�rio Quantitativo de Renova��o de Bolsas" onclick="setAba('relatorios');"/> </li>
			<li> <a href="${ctx}/pesquisa/relatorios/form_bolsas_pesquisa.jsf?aba=relatorios"> Relat�rio Quantitativo de Bolsas de Pesquisa Ativas </a> </li>
			<li> <a href="${ctx}/pesquisa/relatorios/form_resumo_cotas.jsf"> Relat�rio Quantitativo de Cotas de Bolsas</a> </li>
			<li> <h:commandLink action="#{relatoriosPesquisaMBean.gerarRelatorioQuantBolsasCentroDepartamento}" value="Relat�rio Quantitativo de Bolsas por Centro/Departamento" onclick="setAba('relatorios');"/> </li>
			</ul>
		</li>
		
		<li> Docentes
			<ul>
			<li> <a href="${ctx}/administracao/cadastro/Servidor/lista.jsf?aba=relatorios">Consulta de Docentes</a></li>
			<li> <html:link action="/pesquisa/relatorioParticipacaoDocentes?dispatch=iniciar&aba=relatorios"> Participa��o em Projetos de Pesquisa </html:link></li>
			<li> <h:commandLink action="#{relatorioCargaHorariaPesquisaBean.gerar}" value="Carga Hor�ria Dedicada a Projetos de Pesquisa"/> </li>
			
			</ul>
		</li>
		
		<li> Censo
			<ul>
			<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosDocente}" value="Relat�rio de Tempo de Dedica��o � Pesquisa por Grau de Forma��o"/> </li>
			<li> Relat�rio de Tempo de Dedica��o � Pesquisa por �rea de Conhecimento
				<ul>
					<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosArea}" value="Relat�rio Sint�tico"/> </li>
					<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosAreaAnalitico}" value="Relat�rio Anal�tico"/> </li>
				</ul>
			</li>
			<li> <h:commandLink action="#{relatoriosCNPQMBean.gerarIndicadoresGerais}" value="Indicadores Gerais" onclick="setAba('relatorios');"/> </li>
			</ul>
		</li>

		<li> Relat�rios CNPq
			<ul>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarCorpoDiscente}" value="Corpo Discente" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.gerarCorpoDocente}" value="Corpo Docente" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarEnvolvidosComPesquisa}" value="Pesquisa Cient�fica desenvolvida na Institui��o" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarBolsasInstituicao}" value="N�mero de Bolsas de Inicia��o ao Densevolvimento" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarPesquisadoresCNPQ}" value="Relat�rio Pesquisadores Produtividade CNPQ" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarRelatorioGrandeArea}" value="Relat�rio Bolsistas Grande �rea" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.gerarComiteExterno}" value="Relat�rio Comit� Externo" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.gerarComiteInstitucional}" value="Relat�rio Comit� Institucional" onclick="setAba('relatorios');"/> </li>
			</ul>
		</li>

    </ul>