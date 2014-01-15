<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<f:subview id="menu">
		<%@include file="/portais/tutor/menu_tutor.jsp" %>
	</f:subview>
 	<h2><ufrn:subSistema /> > Avaliação Periódica</h2>

	<h:form id="form">
		<table align="center" class="formulario" width="95%">
			<caption>Avaliação Periódica</caption>
			<c:if test="${fichaAvaliacaoEad.discenteEscolhido == null || fichaAvaliacaoEad.cursoEscolhido != null  }">
				<tr>
					<th class="required" width="18%">Curso:</th>
					<td>
						<h:selectOneMenu id="curso" value="#{fichaAvaliacaoEad.cursoEscolhido }" style="width: 95%" valueChangeListener="#{fichaAvaliacaoEad.carregarDiscentes }" onchange="submit()" immediate="true">
							<f:selectItem itemValue="0" itemLabel="-- Selecione um Curso --" />
							<f:selectItems value="#{fichaAvaliacaoEad.cursosCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<c:if test="${fichaAvaliacaoEad.cursoEscolhido > 0 || fichaAvaliacaoEad.discenteEscolhido > 0 }">
				<tr>
					<th class="required" width="18%">Discente:</th>
					<td>
						<h:selectOneMenu id="discentes" value="#{fichaAvaliacaoEad.discenteEscolhido }" style="width: 95%" onchange="submit()" immediate="true">
							<f:selectItem itemValue="0" itemLabel="-- Selecione um Discente --" />
							<f:selectItems value="#{fichaAvaliacaoEad.discentesTutoriaCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<c:if test="${fichaAvaliacaoEad.metodologia.umaProva && not empty fichaAvaliacaoEad.discenteEscolhido && fichaAvaliacaoEad.discenteEscolhido > 0 }">
				<tr>
					<th class="required">Componente Curricular:</th>
					<td>
						<h:selectOneMenu id="componentes" value="#{fichaAvaliacaoEad.componenteEscolhido }" style="width: 95%" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="-- Selecione um Componente --" />
							<f:selectItems value="#{fichaAvaliacaoEad.disciplinasCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<c:if test="${(fichaAvaliacaoEad.discenteEscolhido != null && fichaAvaliacaoEad.discenteEscolhido > 0 && fichaAvaliacaoEad.componenteEscolhido != null && fichaAvaliacaoEad.componenteEscolhido > 0) 
							|| (!fichaAvaliacaoEad.metodologia.umaProva && fichaAvaliacaoEad.discenteEscolhido != null && fichaAvaliacaoEad.discenteEscolhido > 0) }">
				<tr>
					<th class="required">Semana de Avaliação:</th>
					<td>
						<h:selectOneMenu id="semanas" value="#{fichaAvaliacaoEad.semanaEscolhida }" style="width: 95%" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="-- Selecione uma Semana --" />
							<f:selectItems value="#{fichaAvaliacaoEad.semanasComponenteCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Preencher Avaliação" id="preencherAvaliacao"
						action="#{fichaAvaliacaoEad.novaAvaliacaoSemanal }" />
					<h:commandButton value="Cancelar" id="cancelar" onclick="#{confirm}"
						action="#{fichaAvaliacaoEad.cancelar }" />
				</td>
			</tr>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
