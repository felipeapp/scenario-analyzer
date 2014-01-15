<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Impress�o dos Relat�rios e Documentos para Cadastramento de Discentes</h2>

	<div class="descricaoOperacao">
		<p><b>Caro Usu�rio,</b></p>
		<p>Atrav�s deste formul�rio, voc� poder� gerar um dos seguintes conjuntos de documentos:
		<ul> 
			<li><b>Etiquetas:</b> Etiquetas dos Candidatos Aprovados, utilizadas para a identifica��o da pasta de documentos do aluno cadastrado.</li> 
			<li><b>Documentos para cadastro de Discentes:</b> Nessa op��o s�o gerados os documentos necess�rios para o cadastramento do discente na institui��o.</li>
			<li><b>Lista de Convoca��o de Aprovados: </b>Ser� gerada uma lista de candidatos a serem convocados para o preenchimento das vagas, na referida chamada, informando nome e inscri��o do candidato, agrupados por curso. </li>
			<li><b>Lista de Convoca��o para Mudan�a de Semestre e Reconvoca��o para 1� op��o: </b>Similar ao item anterior, por�m, apenas candidatos convocados para o remanejamento de semestre e reconvocados para a 1� op��o ser�o listados. </li>
			<li><b>Documentos dos Discentes com Mudan�a de Semestre: </b> Gera apenas os documentos necess�rios para o remanejamento de semestre. </li>
		</ul>
	</div>

	<h:form id="form">
		<table align="center" class="formulario" width="100%">
			<caption class="listagem">Dados dos Documentos</caption>
			<tr>
				<th width="20%" class="obrigatorio">Documento: </th>
				<td style="padding-left: 5px;">
					<h:selectOneMenu value="#{documentosDiscentesConvocadosMBean.idRelatorio}" id="idRelario" onchange="submit()"
						valueChangeListener="#{documentosDiscentesConvocadosMBean.relatorioListener}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{documentosDiscentesConvocadosMBean.allRelatorio}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Processo Seletivo: </th>
				<td width="70%" style="padding-left: 5px;">
					<h:selectOneMenu id="processoSeletivo" onchange="submit()"
						value="#{documentosDiscentesConvocadosMBean.idProcessoSeletivo}"
						valueChangeListener="#{documentosDiscentesConvocadosMBean.carregarCursosFase}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${ documentosDiscentesConvocadosMBean.escolheCurso }">
				<tr>
					<th class="obrigatorio">Curso(s): </th>
					<td width="70%;">
							<h:selectManyMenu id="curso" value="#{documentosDiscentesConvocadosMBean.idsMatrizSelecionadas}" 
								rendered="#{not empty documentosDiscentesConvocadosMBean.cursos}" style="width: 850px !important;">
								<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
								<f:selectItems value="#{documentosDiscentesConvocadosMBean.cursos}"/>
							</h:selectManyMenu>
							<h:outputText value="N�o h� cursos com convoca��o de candidatos para o Processo Seletivo selecionado" 
								rendered="#{empty documentosDiscentesConvocadosMBean.cursos && documentosDiscentesConvocadosMBean.idProcessoSeletivo > 0}"/>
							<h:outputText value="Selecione um Processo Seletivo"
								rendered="#{documentosDiscentesConvocadosMBean.idProcessoSeletivo == 0}"/>
					</td>
				</tr>
			</c:if>
			<c:if test="${ documentosDiscentesConvocadosMBean.escolheChamada }">
				<tr>
					<th class="obrigatorio">Chamada: </th>
					<td width="70%">
						<h:panelGrid id="fasePanel">
							<h:selectOneMenu id="fase" value="#{documentosDiscentesConvocadosMBean.idFase}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{documentosDiscentesConvocadosMBean.fases}" />
							</h:selectOneMenu>
						</h:panelGrid>
					</td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio" action="#{documentosDiscentesConvocadosMBean.gerarRelatorio}" /> 
						<h:commandButton value="Cancelar" action="#{documentosDiscentesConvocadosMBean.cancelar}" id="cancelar" onclick="#{confirm}"/></td>
				</tr>
			</tfoot>
		</table>
		<br />
			<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	<script>
	// Script necess�rio para que no IE o tamanho da caixa de sele��o de cursos seja adequado (bug do IE).
	$('form:curso').size = 8;
	</script>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>