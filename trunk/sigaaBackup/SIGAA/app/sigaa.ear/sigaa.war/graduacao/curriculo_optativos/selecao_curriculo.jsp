<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Grupo de Optativas &gt; Seleção de Currículo </h2>

	<h:form id="buscaCC">
		<table class="formulario" width="100%">
			<caption>Selecione um currículo</caption>
			<tbody>
				<tr>
					<th>Curso: <span class="required">&nbsp;</span></th>
					<td>
						<a4j:region>
						<h:selectOneMenu id="cursos" value="#{grupoOptativasMBean.curso.id}" style="width: 550px;">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE UM CURSO <--" />
							<f:selectItems value="#{cursoGrad.allCombo}" />
							<a4j:support event="onchange" reRender="matrizes"/>
						</h:selectOneMenu> &nbsp;
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/ajax-loader.gif"/>
							</f:facet>
						</a4j:status>
						</a4j:region> 
					</td>
				</tr>
				<tr>
					<th nowrap="nowrap">Matrizes Curriculares: <span class="required">&nbsp;</span></th>
					<td>
						<a4j:region>
						<h:selectOneMenu id="matrizes" value="#{grupoOptativasMBean.matriz.id }" >
							<f:selectItems value="#{grupoOptativasMBean.matrizesCurriculares}" />
							<a4j:support event="onchange" reRender="curriculo"/>
						</h:selectOneMenu>&nbsp;
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/ajax-loader.gif"/>
							</f:facet>
						</a4j:status>
						</a4j:region>
					</td>
				</tr>
				<tr>
					<th>Currículos: <span class="required">&nbsp;</span></th>
					<td>
						<h:selectOneMenu id="curriculo" value="#{grupoOptativasMBean.curriculo.id }" >
							<f:selectItems value="#{grupoOptativasMBean.curriculos}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<Tr> <td colspan="2">
						<h:commandButton value="Carregar Grupos" action="#{grupoOptativasMBean.selecionarCurriculo}"  id="selecionar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}"  action="#{grupoOptativasMBean.cancelar}" id="cancelar" />
				</td> </Tr>
			</tfoot>
		</table>
		
		<br />
		<center><img src="${ctx}/img/required.gif" style="vertical-align: middle;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span></center>
		<br />
	
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>