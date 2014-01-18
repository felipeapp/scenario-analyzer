<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2><ufrn:subSistema /> &gt; Consultar Turma</h2>
	
	<h:form id="form" prependId="false">
	
	<br/>
	<div class="descricaoOperacao">
		<b>Caro usuário,</b> 
		<br/><br/>
		Nesta tela é possível selecionar as turmas para visualizar ou alterar.
	</div>	
	
	<table class="formulario" >
		<caption>Busca de Turmas</caption>
		<tbody>
			<tr>
				<td width="1px;"><h:selectBooleanCheckbox value="true" readonly="true" disabled="true" styleClass="check" /></td>
				<td><label>Sigla IES: </label></td>
				<td>			
					<h:selectOneMenu value="#{turmaRedeMBean.idIes}" id="status" style="width: 90%;">
						<f:selectItems value="#{selecionaCampusRedeMBean.ifesCombo}" id="statusDiscenteCombo"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="1px;"><h:selectBooleanCheckbox value="#{turmaRedeMBean.checkAnoPeriodo}" id="checkAnoPeriodo" styleClass="noborder" /></td>
				<td><label for="busca:chkAnoPeriodo">Ano-Período: </label></td>
				<td>			
					<h:inputText value="#{ turmaRedeMBean.anoTurmaFiltro }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="ano" onfocus="$('checkAnoPeriodo').checked = true;"/>-
					<h:inputText value="#{ turmaRedeMBean.periodoTurmaFiltro }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodo" onfocus="$('checkAnoPeriodo').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td width="1px;"><h:selectBooleanCheckbox value="#{turmaRedeMBean.checkCodComponente}" id="checkCodComponente" styleClass="noborder" /></td>
				<td><label for="busca:chkCodigo">Código do Componente: </label></td>
				<td>			
					<h:inputText value="#{ turmaRedeMBean.codigoComponente }" size="10" maxlength="9" id="txtcodigo" onfocus="$('checkCodComponente').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td width="1px;"><h:selectBooleanCheckbox value="#{turmaRedeMBean.checkNomeComponente}" id="checkNomeComponente" styleClass="noborder" /></td>
				<td><label for="busca:chkNome">Nome do Componente: </label></td>
				<td>			
					<h:inputText value="#{ turmaRedeMBean.nomeComponente }"  size="60" maxlength="100" id="txtnome" onfocus="$('checkNomeComponente').checked = true;"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>	
				<td colspan="3">
					<h:commandButton value="Buscar" action="#{turmaRedeMBean.buscarTurmas}" />
					<h:commandButton value="Cancelar" action="#{turmaRedeMBean.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br/>

	<c:if test="${ not empty turmaRedeMBean.turmas}">
	
		<div class="infoAltRem" style="width: 90%">
			<img src="${ctx}/ava/img/zoom.png"/>: Visualizar Turma
			<img src="/sigaa/ava/img/page_edit.png">: Alterar Turma
		</div>

		<table class="listagem" style="width: 90%">
			<caption>Turmas Encontradas (${fn:length(turmaRedeMBean.turmas)})</caption>
			<thead>
				<tr>
					<th width="10%">Ano-Período</th>
					<th width="40%">Componente Curricular</th>
					<th>Código da Turma</th>
					<th>Situação</th>
					<th width="2%" style="text-align:center"></th>
					<th width="2%" style="text-align:center"></th>
				</tr>
			</thead>		
			<tbody>
					<c:set var="campus" value="0"/>
					<c:forEach items="#{turmaRedeMBean.turmas}" var="t" varStatus="status">
						<c:if test="${campus != t.dadosCurso.campus.id}">
							<tr>
								<td colspan="6" style="background-color: #C8D5EC">
									<c:set var="campus" value="#{t.dadosCurso.campus.id}"/>
									<b>${t.dadosCurso.campus.nome}</b>
								</td>
							</tr>		
						</c:if>
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${t.anoPeriodo}</td>
							<td>${t.componente.descricaoResumida}</td>
							<td>Turma ${t.codigo}</td>
							<td>${t.situacaoTurma.descricao}</td>
							<td style="text-align:center">
								<h:commandLink title="Visualizar Turma" action="#{turmaRedeMBean.viewTurma}">
								<h:graphicImage value="/ava/img/zoom.png" width="16" />
									<f:param name="idTurma" value="#{t.id}"></f:param>
								</h:commandLink>
							</td>
							<td style="text-align:center">
								<h:commandLink title="Alterar Turma" action="#{turmaRedeMBean.alterarTurma}">
									<h:graphicImage value="/ava/img/page_edit.png"/>
									<f:param name="idTurma" value="#{t.id}"></f:param>
								</h:commandLink>
							</td>
						</tr>			
					</c:forEach>
			</tbody>
		</table>
	</c:if>
	<br/>
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>