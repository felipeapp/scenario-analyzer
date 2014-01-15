<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2>  <ufrn:subSistema /> > Cadastrar Orientação Acadêmica </h2>

	<table width="100%" class="visualizacao">
		<tr>
			<th width="30%">Orientador Acadêmico:</th>
			<td>
				<c:if test="${acesso.secretarioGraduacao || acesso.coordenadorCursoGrad || acesso.tecnico}">
				<h:outputText value="#{orientacaoAcademica.orientador}"/>
				</c:if>
				<c:if test="${acesso.secretariaPosGraduacao}">
				<h:outputText value="#{orientacaoAcademica.equipe.servidor}"/>
				</c:if>

			</td>
		</tr>
		<tr>
			<th width="30%">Total de Orientandos:</th>
			<td><h:outputText value="#{orientacaoAcademica.totalOrientandos}"/></td>
		</tr>
	</table>

	<br/>
	<center>
	<div class="infoAltRem">
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:Remover Orientando
		<br />
	</div>
	</center>


	<table class="formulario" width="90%">
		<caption> Discente Selecionados (${fn:length(orientacaoAcademica.orientacoes)})</caption>

		<c:if test="${not empty orientacaoAcademica.orientacoes}">
		<c:forEach var="orientacao" items="#{orientacaoAcademica.orientacoes}" varStatus="status">
			<tr class="${ status.index % 2 == 0 ? "linhaPar":"linhaImpar" }">
				<td>${orientacao.discente}</td>
				<td>
					<c:if test="${acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao}">
					${ orientacao.tipoOrientacaoString }
					</c:if>
				</td>
				<h:form>
					<td width=25>
						<input type="hidden" value="${orientacao.discente.id}" name="id" />
						<h:commandButton image="/img/delete.gif" styleClass="noborder" alt="Remover" action="#{orientacaoAcademica.removerDiscente}" />
					</td>
				</h:form>
			</tr>
		</c:forEach>
		</c:if>

		<c:if test="${empty orientacaoAcademica.orientacoes}">
		<tr>
			<td align="center"><font color="red">Nenhum Discente Selecionado</font></td>
		</tr>
		</c:if>

		<h:form id="formBotoesForm">
		<tfoot>
			<tr><td colspan="3">
				<h:commandButton value="Cadastrar Orientações Acadêmicas" action="#{ orientacaoAcademica.cadastrar }" id="cadastrarorientAcademica"/>
				<h:commandButton value="Cancelar" action="#{ orientacaoAcademica.cancelar }" onclick="#{confirm}" id="cancelarorientaccao"/><br><br>
				<h:commandButton title="<< Selecionar Outro Orientador" value="<< Selecionar Outro Orientador" action="#{orientacaoAcademica.telaOrientador}" id="btaoOutroOrientador"/>
				<h:commandButton title="<< Buscar Outros Discentes"  value="<< Buscar Outros Discentes" action="#{orientacaoAcademica.telaBusca}" id="buscarOutrosDiscnetes"/>
			</td></tr>
		</tfoot>
		</h:form>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>