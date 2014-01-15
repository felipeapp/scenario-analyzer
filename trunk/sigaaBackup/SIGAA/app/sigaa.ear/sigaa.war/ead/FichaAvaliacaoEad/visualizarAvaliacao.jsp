<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<f:subview id="menu">
<%@include file="/portais/tutor/menu_tutor.jsp" %>
</f:subview>

<h2><ufrn:subSistema /> > Avaliação Periódica</h2>
<br/>

	<h:form id="form">
		<table align="center" class="formulario" width="95%">
			<caption>Avaliação Periódica</caption>
			<tr>
				<th class="required" width="18%">Discente:</th>
				<td>
					<h:selectOneMenu id="discentes" value="#{fichaAvaliacaoEad.discenteEscolhido }" valueChangeListener="#{fichaAvaliacaoEad.carregaDiscente}" style="width: 95%" onchange="submit()" immediate="true">
						<f:selectItem itemValue="0" itemLabel="-- Selecione um Discente --" />
						<f:selectItems value="#{fichaAvaliacaoEad.discentesTutoriaCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${fichaAvaliacaoEad.metodologia.umaProva && not empty fichaAvaliacaoEad.discenteEscolhido && fichaAvaliacaoEad.discenteEscolhido > 0 }">
				<tr>
					<th class="required">Componente Curricular:</th>
					<td>
						<h:selectOneMenu id="componentes" valueChangeListener="#{fichaAvaliacaoEad.carregaSemana}" style="width: 95%" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="-- Selecione um Componente --" />
							<f:selectItems value="#{fichaAvaliacaoEad.semanasCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Visualizar Notas" id="visualizarNotas"
						action="#{fichaAvaliacaoEad.mostrarFicha }" />
					<h:commandButton value="Cancelar" id="cancelar" onclick="#{confirm}"
						action="#{fichaAvaliacaoEad.cancelar }" />
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>