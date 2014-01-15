<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
<li> Formulários de Avaliação Institucional
	<ul>
		<li><h:commandLink action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.listar}" value="Listar" onclick="setAba('bolsista')" /></li>
	</ul>
</li>
<li> Calendário de Avaliação Institucional
	<ul>
		<li><h:commandLink action="#{calendarioAvaliacaoInstitucionalBean.listar}" value="Listar" onclick="setAba('bolsista')" /></li>
	</ul>
</li>
<li> Moderação de Observações
	<ul>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoDocenteTurma}" value="Moderar Observações de Discentes sobre Docentes de Turmas" onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoTrancamentos}" value="Moderar Observações de Discentes sobre Trancamento de Turmas" onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoTurma}" value="Moderar Observações de Docentes sobre Turmas" onclick="setAba('bolsista')" /></li>
	</ul>
</li>

<li> Relatórios
	<ul>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarMediaNotas}"         value="Resultado Sintético dos Docentes por Departamento"      onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarResultadoAnalitico}" value="Resultado Analítico do Docente"            	            onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarPortalDocente}"      value="Portal do Resultado do Docente"            			    onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciar}"                   value="Relatórios Quantitativos de Trancamentos e Reprovações" onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarMediaBaixa}"         value="Relatório de Docentes com Média Baixa por Pergunta"     onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarQuantitativoNaoComputado}" value="Relatório Quantitativo de Avaliações Institucionais não Computadas" onclick="setAba('bolsista')" /></li>
	</ul>
</li>

<li> Consultas
	<ul>
		<li><a href="${ctx}/geral/curso/busca_geral.jsf" onclick="setAba('bolsista')">Consulta Geral de Cursos</a></li>
		<li><a href="${ctx}/geral/componente_curricular/busca_geral.jsf" onclick="setAba('bolsista')">Consulta Geral de Componentes Curriculares</a></li>
		<li><h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Consulta Geral de Turmas" onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{ relatorioAvaliacaoMBean.atualizaEstatisticas }" value="Estatísticas do Preenchimento das Avalições Ativas" id="atualizarEstatisticas" onclick="setAba('bolsista')" /></li>
	</ul>
</li>
</ul>