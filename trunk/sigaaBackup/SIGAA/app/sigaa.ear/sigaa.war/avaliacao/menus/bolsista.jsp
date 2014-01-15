<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
<li> Formul�rios de Avalia��o Institucional
	<ul>
		<li><h:commandLink action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.listar}" value="Listar" onclick="setAba('bolsista')" /></li>
	</ul>
</li>
<li> Calend�rio de Avalia��o Institucional
	<ul>
		<li><h:commandLink action="#{calendarioAvaliacaoInstitucionalBean.listar}" value="Listar" onclick="setAba('bolsista')" /></li>
	</ul>
</li>
<li> Modera��o de Observa��es
	<ul>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoDocenteTurma}" value="Moderar Observa��es de Discentes sobre Docentes de Turmas" onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoTrancamentos}" value="Moderar Observa��es de Discentes sobre Trancamento de Turmas" onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoTurma}" value="Moderar Observa��es de Docentes sobre Turmas" onclick="setAba('bolsista')" /></li>
	</ul>
</li>

<li> Relat�rios
	<ul>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarMediaNotas}"         value="Resultado Sint�tico dos Docentes por Departamento"      onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarResultadoAnalitico}" value="Resultado Anal�tico do Docente"            	            onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarPortalDocente}"      value="Portal do Resultado do Docente"            			    onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciar}"                   value="Relat�rios Quantitativos de Trancamentos e Reprova��es" onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarMediaBaixa}"         value="Relat�rio de Docentes com M�dia Baixa por Pergunta"     onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarQuantitativoNaoComputado}" value="Relat�rio Quantitativo de Avalia��es Institucionais n�o Computadas" onclick="setAba('bolsista')" /></li>
	</ul>
</li>

<li> Consultas
	<ul>
		<li><a href="${ctx}/geral/curso/busca_geral.jsf" onclick="setAba('bolsista')">Consulta Geral de Cursos</a></li>
		<li><a href="${ctx}/geral/componente_curricular/busca_geral.jsf" onclick="setAba('bolsista')">Consulta Geral de Componentes Curriculares</a></li>
		<li><h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Consulta Geral de Turmas" onclick="setAba('bolsista')" /></li>
		<li><h:commandLink action="#{ relatorioAvaliacaoMBean.atualizaEstatisticas }" value="Estat�sticas do Preenchimento das Avali��es Ativas" id="atualizarEstatisticas" onclick="setAba('bolsista')" /></li>
	</ul>
</li>
</ul>