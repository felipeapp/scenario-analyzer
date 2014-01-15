<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="registroAcaoAva" />

	<%@include file="/ava/menu.jsp" %>
	
	<fieldset><legend>Relatório de ações registradas na turma</legend></fieldset>
	
	<h:form id="formAva">
	
		<table class="formulario">
			<caption>Filtros</caption>
			<tr>
				<td>
					&nbsp;<span style="font-weight:bold;">Ação:</span>&nbsp;
					<h:selectOneMenu value="#{ registroAcaoAva.acao }">
						<f:selectItem itemValue = "0" itemLabel = " -- Selecione uma ação -- " />
						<f:selectItems value="#{ registroAcaoAva.acoesCombo }" />
					</h:selectOneMenu>
					
					&nbsp;<span style="font-weight:bold;">Entidade:</span>&nbsp; 
					<h:selectOneMenu value="#{ registroAcaoAva.entidade }">
						<f:selectItem itemValue = "0" itemLabel = " -- Selecione uma entidade -- " />
						<f:selectItems value="#{ registroAcaoAva.entidadesCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td>
					&nbsp;<span style="font-weight:bold;">Usuário:</span>&nbsp;
					<h:selectOneMenu value="#{ registroAcaoAva.usuario.id }">
						<f:selectItem itemValue = "0" itemLabel = " -- Selecione um usuário -- " />
						<f:selectItems value="#{ registroAcaoAva.usuariosCombo }" />
					</h:selectOneMenu>
					
					&nbsp;<span style="font-weight:bold;">Período:</span>&nbsp;
					<t:inputCalendar id="Inicio" value="#{ registroAcaoAva.inicio }" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return (formataData(this,event));"  maxlength="10" title="Início">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</t:inputCalendar>
					&nbsp;a&nbsp;
					<t:inputCalendar id="Fim" value="#{ registroAcaoAva.fim }" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return (formataData(this,event));"  maxlength="10" title="Fim">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr><td>&nbsp;<span style="font-weight:bold;">Agrupar estatísticas por usuário:</span>&nbsp;
					<h:selectBooleanCheckbox value="#{ registroAcaoAva.contar }" /></td></tr>
			<tfoot><tr><td style="text-align:center;">
				<h:commandButton action="#{ registroAcaoAva.exibirRelatorio }" value=" Filtrar os registros " />
			</td></tr></tfoot>
		</table><br/>

		<style>
			.listagem td {
				border-right:1px solid #CCC;
			}
		</style>
		
		<c:if test="${fn:length( registroAcaoAva.relatorio) > 0 }">
		
			<c:set var="maxAcessos" value="0" />
			<table class="listagem" style="width:90%;">
				<thead>
					<tr>
						<th>Nome</th><th style="text-align:center;">Ação</th><th>${registroAcaoAva.contar ? "Acessos" : "Dados"}</th>${ registroAcaoAva.contar ? "<th></th>" : "<th style='text-align:center;'>Data</th>" }
					</tr>
				</thead>
				<c:forEach items="#{ registroAcaoAva.relatorio }" var="r" varStatus="indice">
					<tr class="${ indice.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="vertical-align:top;">
							<h:commandLink action="#{ registroAcaoAva.exibirRelatorio }" value="#{ r[2] }" title="Filtrar também pelo(a) usuário(a) '#{ r[2] }'">
								<f:param name="idUsuario" value="#{ r[9] }" />
							</h:commandLink>
						</td>
						<td style="text-align:center;" nowrap="nowrap">
							<c:if test="${ registroAcaoAva.contar }">
								<c:forEach begin="0" end="${fn:length(r[12]) - 1}" var="i">
									${ r[12][i] }<br/>
								</c:forEach>
							</c:if>
							<h:commandLink action="#{ registroAcaoAva.exibirRelatorio }" value="#{ r[3] } #{ r[4] }" title="Filtrar também pela ação '#{ r[3]}' na entidade '#{ r[4] }'" rendered="#{ !registroAcaoAva.contar }">
								<f:param name="idAcao" value="#{ r[10] }" />
								<f:param name="idTipoEntidade" value="#{ r[11] }" />
							</h:commandLink>
						</td>
						<c:if test="${ registroAcaoAva.contar }">
							<td style="text-align:right;">
								<c:forEach begin="0" end="${fn:length(r[12]) - 1}" var="i">
									<c:if test="${r[13][i] != null && maxAcessos < r[13][i]}">
										<c:set var="maxAcessos" value="${r[13][i]}" />
									</c:if>
									${ r[13][i] }<br/>
								</c:forEach>
							</td>
						</c:if>
						<h:outputText value="<td>#{ r[5] }</td>" escape="false" rendered="#{ !registroAcaoAva.contar }" />
						
						<c:if test="${ registroAcaoAva.contar }">
							<td style="width:202px;">
								<c:forEach begin="0" end="${fn:length(r[12]) - 1}" var="i">
									<div class="barraRelatorio" style="background:#33AAFF;margin:0px;padding:0px;" title="${ r[13][i] }">${ r[13][i] }</div>
								</c:forEach>
							</td>
						</c:if>
						
						<c:if test="${ not registroAcaoAva.contar }">
							<td nowrap="nowrap" style="text-align:center;border:none;"><ufrn:format type="dataHora" valor="${ r[8] }" /></td>
						</c:if>
					</tr>
				</c:forEach>
			</table>
			
			<script>

				var maxAcessos = ${ maxAcessos };
			
				J(".barraRelatorio").each(function () {
					J(this).css("width", (200 / maxAcessos ) * J(this).html());
					J(this).html("&nbsp;");
				});
			</script>
		</c:if>
		
		<c:if test="${fn:length( registroAcaoAva.relatorio) == 0 }">
			<div style="text-align:center;color:#CC0000;font-weight:bold;margin:10px;padding:10px;">Nenhum registro encontrado.</div>
		</c:if>
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>