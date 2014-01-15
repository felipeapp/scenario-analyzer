<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipos de Cursos e Eventos de Extensão</h2>

	<h:messages showDetail="true" showSummary="true" />

	<h:form id="form">
		<table class=formulario>
			<caption class="listagem">Cadastro de Tipo de Curso e
			Evento de Extensão</caption>
			<h:inputHidden value="#{tipoCursoEventoExtensao.confirmButton}" />
			<input type="hidden" value="${tipoCursoEventoExtensao.obj.id}" id="id" name="id"/>

			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText
					value="#{tipoCursoEventoExtensao.obj.descricao}" size="60"
					maxlength="255" readonly="#{tipoCursoEventoExtensao.readOnly}"
					id="descricao" /></td>
			</tr>

			<tr>
				<th class="required">Escopo:</th>
				<td><h:selectOneMenu id="escopo"
					value="#{tipoCursoEventoExtensao.obj.escopo}">
					<f:selectItem itemValue="" itemLabel=" -- SELECIONE UM ESCOPO --" />
					<f:selectItem itemValue="C" itemLabel="CURSO" />
					<f:selectItem itemValue="E" itemLabel="EVENTO" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Carga Horária Mínima:</th>
				<td><h:inputText
					value="#{tipoCursoEventoExtensao.obj.chMinima}" size="5"
					maxlength="4" readonly="#{tipoCursoEventoExtensao.readOnly}"
					id="ch_minima" /> horas
					<ufrn:help img="/img/ajuda.gif">Carga horária mínima que uma ação deve ter para ser deste tipo.</ufrn:help>
					</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton
						value="#{tipoCursoEventoExtensao.confirmButton}"
						action="#{tipoCursoEventoExtensao.cadastrar}"
						id="bt_cadastrar_alterar"
						rendered="#{tipoCursoEventoExtensao.confirmButton != 'Remover'}" />
						
					<h:commandButton value="Remover"
						action="#{tipoCursoEventoExtensao.inativar}" id="bt_inativar"
						rendered="#{tipoCursoEventoExtensao.confirmButton == 'Remover'}" />
						
					<h:commandButton value="Cancelar"
						action="#{tipoCursoEventoExtensao.cancelar}" id="bt_cancelar" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>