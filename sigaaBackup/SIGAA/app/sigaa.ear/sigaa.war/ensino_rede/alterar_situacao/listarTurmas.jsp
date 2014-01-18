<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
	<a4j:keepAlive beanName="alterarSituacaoMatriculaRede" />
	
	<h2><ufrn:subSistema/> > Consolidar Turma</h2>
	<h:form>
		<table class="formulario" width="70%">
			<caption>Informe os critérios de consulta</caption>
			<tbody>
				<tr>
					<th width="23%" class="obrigatorio"><label>Ano-Período:</label></th>
					<td>
						<h:inputText value="#{ alterarSituacaoMatriculaRede.ano }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="ano" />-
						<h:inputText value="#{ alterarSituacaoMatriculaRede.periodo }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodo"/>
					</td>
				</tr>
				<tr>
					<th width="23%" class="obrigatorio"><label>Instituição:</label></th>
					<td>
						<a4j:region>
						<h:selectOneMenu id="instituicoesBusca" value="#{alterarSituacaoMatriculaRede.ies.id}" 
						valueChangeListener="#{alterarSituacaoMatriculaRede.carregarCampus }">
							<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
							<f:selectItems value="#{alterarSituacaoMatriculaRede.instituicoesCombo}" />
							<a4j:support event="onchange" reRender="campus" />
						</h:selectOneMenu>
						</a4j:region>						
					</td>
				</tr>
				<tr>
					<th width="23%" class="obrigatorio"><label>Campus:</label></th>
					<td>
						<h:selectOneMenu id="campus" value="#{alterarSituacaoMatriculaRede.campus.id}">
							<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
							<f:selectItems value="#{alterarSituacaoMatriculaRede.campusCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{alterarSituacaoMatriculaRede.buscar}" />
						<h:commandButton value="Cancelar" action="#{alterarSituacaoMatriculaRede.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		
		<c:if test="${not empty alterarSituacaoMatriculaRede.turmas}">

			<br />
			<center>
				<div class="infoAltRem" style="width:70%;"> 
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Turma
				</div>
			</center>

			<table class="listagem" style="width:70%">
				<caption>${fn:length(alterarSituacaoMatriculaRede.turmas)} turmas encontradas</caption>
				<thead>
					<tr>
						<th style="text-align: center;width:15%;">Ano Período</th>
						<th style="text-align: center;">Turma</th>
						<th>Docente(s)</th>
						<th style="width:2%"></th>
					</tr>
				</thead>
				<tbody>
				<c:set var="disciplinaAtual" value="0" />
				<c:forEach items="#{alterarSituacaoMatriculaRede.turmas}" var="t" varStatus="loop">
					<c:if test="${ disciplinaAtual != t.componente.id}">
						<c:set var="disciplinaAtual" value="${t.componente.id}" />
						<tr style="background-color:#C8D5EC;font-weight:bold;">
							<td colspan="4" style="font-variant: small-caps;" style="text-align: left;">
								${t.componente.descricaoResumida}
							</td>
						</tr>
					</c:if>
					<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align: center;width:15%;">${t.ano}.${t.periodo}</td>
						<td style="text-align: center;">T${t.codigo}</td>
						<td id="colDocente">
							${empty t.docentesNomes ? "A DEFINIR" : t.docentesNomes}
						</td>
						<td>
							<h:commandLink action="#{alterarSituacaoMatriculaRede.selecionarTurma}" title="Selecionar Turma" id="selecionarTurma">
								<h:graphicImage value="/img/seta.gif"></h:graphicImage>
								<f:param name="idTurma" value="#{t.id}" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:if>
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
