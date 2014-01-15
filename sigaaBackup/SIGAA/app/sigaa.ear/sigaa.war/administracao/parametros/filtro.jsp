<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true" />
	<h2 class="title"><ufrn:subSistema /> > Par�metros do Sistema</h2>
	<h:form id="form">
		<table class="formulario" width="95%">
			<tr>
				<th>Unidade Respons�vel:</th>
				<td><h:selectOneMenu id="unidades"  value="#{parametros.obj.unidade.id}">
					<f:selectItems value="#{unidade.allGestorasAcademicasCombo}" />
				</h:selectOneMenu></td>
			</tr>
				<tr>
					<th>N�vel de Ensino:</th>
					<td><h:selectOneMenu onchange="submit()" valueChangeListener="#{parametros.selecionarNivel}" id="niveis">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{parametros.comboNiveis}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Modalidade de Ensino:</th>
					<td><h:selectOneMenu id="modalidade" valueChangeListener="#{parametros.carregarCursosPorModalidade}"
						  onchange="submit()" value="#{parametros.obj.modalidade.id}" >
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{modalidadeEducacao.allCombo}" />
					</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Conv�nio Acad�mico:</th>
					<td><h:selectOneMenu id="convenio" valueChangeListener="#{parametros.carregarCursosPorConvenio}"
						 onchange="submit()" value="#{parametros.obj.convenio.id}">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{convenioAcademico.allCombo}" />
					</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Curso:</th>
					<td><h:selectOneMenu value="#{parametros.obj.curso.id}"
						id="curso">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{parametros.comboCursos}" />
					</h:selectOneMenu></td>
				</tr>
			<tr><td colspan="2">&nbsp;</td></tr>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="Cancelar" onclick="#{confirm}"
						action="#{parametros.cancelar}" id="cancelar" />
					 <h:commandButton
						value="Pr�ximo >> " id="submParams"
						action="#{parametros.verValores}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>