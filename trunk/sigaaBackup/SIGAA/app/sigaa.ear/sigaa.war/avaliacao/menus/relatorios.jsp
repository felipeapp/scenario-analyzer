<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
<li> Relat�rios
	<ul>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarMediaNotas}"         value="Resultado Sint�tico dos Docentes por Departamento"      onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarResultadoAnalitico}" value="Resultado Anal�tico do Docente"            	            onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarPortalDocente}"      value="Portal do Resultado do Docente"            			    onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciar}"                   value="Relat�rios Quantitativos de Trancamentos e Reprova��es" onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarMediaBaixa}"         value="Relat�rio de Docentes com M�dia Baixa por Pergunta"     onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{relatorioAvaliacaoMBean.iniciarQuantitativoNaoComputado}" value="Relat�rio Quantitativo de Avalia��es Institucionais n�o Computadas" onclick="setAba('relatorios')" /></li>
	</ul>
</li>
<li> Exporta��o de Dados
	<ul>
		<li><h:commandLink action="#{exportaAvaliacaoInstitucional.legendaPerguntasDiscentes}"   value="Legenda das Perguntas da Avalia��o da Doc�ncia pelo Aluno"    onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{exportaAvaliacaoInstitucional.legendaPerguntasDocentes}"    value="Legenda das Perguntas da Avalia��o da Doc�ncia pelo Docente"  onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{resultadoAvaliacaoInstitucionalMBean.iniciar}"              value="Coment�rios Relativos ao Docente"                             onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{motivosTrancamentoMBean.iniciar}"                           value="Trancamentos da Avalia��o Institucional"                      onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{exportaAvaliacaoInstitucional.iniciarDiscente}"             value="Dados da Avalia��o Institucional dos Discentes"               onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{exportaAvaliacaoInstitucional.iniciarDocente}"              value="Dados da Avalia��o Institucional dos Docentes"                onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{exportaAvaliacaoInstitucional.iniciarTutorEad}"             value="Dados da Avalia��o Institucional dos Tutores de EAD"          onclick="setAba('relatorios')" /></li>
	</ul>
</li>
<li> Consultas
	<ul>
		<li><a href="${ctx}/geral/curso/busca_geral.jsf" onclick="setAba('relatorios')">Consulta Geral de Cursos</a></li>
		<li><a href="${ctx}/geral/componente_curricular/busca_geral.jsf" onclick="setAba('relatorios')">Consulta Geral de Componentes Curriculares</a></li>
		<li><h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Consulta Geral de Turmas" onclick="setAba('relatorios')" /></li>
		<li><h:commandLink action="#{ relatorioAvaliacaoMBean.atualizaEstatisticas }" value="Estat�sticas do Preenchimento das Avali��es Ativas" id="atualizarEstatisticas" onclick="setAba('relatorios')" /></li>
	</ul>
</li>
</ul>