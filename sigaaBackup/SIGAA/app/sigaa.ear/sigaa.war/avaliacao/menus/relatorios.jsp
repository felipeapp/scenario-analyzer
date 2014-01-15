<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
<li> Relatórios
	<ul>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarMediaNotas}"         value="Resultado Sintético dos Docentes por Departamento"      onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarResultadoAnalitico}" value="Resultado Analítico do Docente"            	            onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarPortalDocente}"      value="Portal do Resultado do Docente"            			    onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciar}"                   value="Relatórios Quantitativos de Trancamentos e Reprovações" onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarMediaBaixa}"         value="Relatório de Docentes com Média Baixa por Pergunta"     onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarQuantitativoNaoComputado}" value="Relatório Quantitativo de Avaliações Institucionais não Computadas" onclick="setAba('relatorios')" /></li>
	</ul>
</li>
<li> Exportação de Dados
	<ul>
		<li><h:commandLink action="#{exportaAvaliacaoInstitucional.legendaPerguntasDiscentes}"   value="Legenda das Perguntas da Avaliação da Docência pelo Aluno"    onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{exportaAvaliacaoInstitucional.legendaPerguntasDocentes}"    value="Legenda das Perguntas da Avaliação da Docência pelo Docente"  onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{resultadoAvaliacaoInstitucionalMBean.iniciar}"              value="Comentários Relativos ao Docente"                             onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{motivosTrancamentoMBean.iniciar}"                           value="Trancamentos da Avaliação Institucional"                      onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{exportaAvaliacaoInstitucional.iniciarDiscente}"             value="Dados da Avaliação Institucional dos Discentes"               onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{exportaAvaliacaoInstitucional.iniciarDocente}"              value="Dados da Avaliação Institucional dos Docentes"                onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{exportaAvaliacaoInstitucional.iniciarTutorEad}"             value="Dados da Avaliação Institucional dos Tutores de EAD"          onclick="setAba('relatorios')" /></li>
	</ul>
</li>
<li> Consultas
	<ul>
		<li><a href="${ctx}/geral/curso/busca_geral.jsf" onclick="setAba('relatorios')">Consulta Geral de Cursos</a></li>
		<li><a href="${ctx}/geral/componente_curricular/busca_geral.jsf" onclick="setAba('relatorios')">Consulta Geral de Componentes Curriculares</a></li>
		<li><h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Consulta Geral de Turmas" onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{ relatorioAvaliacaoMBean.atualizaEstatisticas }" value="Estatísticas do Preenchimento das Avalições Ativas" id="atualizarEstatisticas" onclick="setAba('relatorios')" /></li>
	</ul>
</li>
</ul>