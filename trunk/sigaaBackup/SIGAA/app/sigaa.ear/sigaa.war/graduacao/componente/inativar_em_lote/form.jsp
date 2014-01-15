<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<script type="text/javascript">
function marcarTodos(chkTodos) {
	var re= new RegExp('selecionado', 'g')
	var elements = document.getElementsByTagName('input');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].id.match(re)) {
			elements[i].checked = chkTodos.checked;
		}
	}
}
</script>
<h2><ufrn:subSistema /> > Inativar Componentes de Departamento</h2>
<div class="descricaoOperacao">
	<p>Caro Usuário,</p>
	<p>Selecione um departamento e selecione na lista um ou mais componentes curriculares serão inativados.</p>
</div>
<f:view>
<h:form id="form">
	<a4j:keepAlive beanName="inativarComponentesDepartamentoMBean" />
	<table class="formulario" width="95%">
		<caption>Selecione um Departamento</caption>
		<tr>
			<th width="15%" class="required">Departamento:</th>
			<td width="70%" style="text-align: left;">
				<h:selectOneMenu id="curso" value="#{inativarComponentesDepartamentoMBean.obj.unidade.id}" rendered="#{listaAssinaturasGraduandos.inicial}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{unidade.allDetentorasComponentesGraduacaoCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton action="#{inativarComponentesDepartamentoMBean.buscarComponentes}" value="Buscar Componentes Curriculares" id="buscar"/>
					<h:commandButton action="#{inativarComponentesDepartamentoMBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	<c:if test="${ not empty inativarComponentesDepartamentoMBean.resultadosBusca }">
		<table class="formulario" width="95%">
			<caption>Componentes Curriculares do Departamento (${ fn:length(inativarComponentesDepartamentoMBean.resultadosBusca) })</caption>
			<thead>
				<tr>
					<th width="2%"><input type="checkbox" id="checkAll" title="Marcar Todos"onclick="marcarTodos(this);"/></th>
					<th style="text-align: center; width: 8%">Código</th>
					<th>Nome</th>
					<th width="6%" style="text-align: right;">Carga Horária<br/>Total</th>
					<th>Tipo</th>
					<th style="text-align: center;" width="5%">Ativo</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{inativarComponentesDepartamentoMBean.componentes}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td><h:selectBooleanCheckbox value="#{item.selecionado}" id="selecionado" rendered="#{ item.objeto.ativo }"/> </td>
						<td style="text-align: center;">${item.objeto.codigo}</td>
						<td>${item.objeto.detalhes.nome}</td>
						<td style="text-align: right;">${item.objeto.detalhes.chTotal} h</td>
						<td>
							<c:if test="${!item.objeto.atividade}">${item.objeto.tipoComponente.descricao}</c:if>
							<c:if test="${item.objeto.atividade}">
								${item.objeto.tipoAtividade.descricao}<br/>
								(${item.objeto.formaParticipacao.descricao})
							</c:if>
						</td>
						<td style="${ item.objeto.ativo ? '' : 'color:red;'}" align="center"><ufrn:format type="simnao" valor="${item.objeto.ativo}"/></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="6" align="center">
						<h:commandButton action="#{inativarComponentesDepartamentoMBean.confirmarInativacao}" value="Inativar Componentes Curriculares" id="inativar"/>
						<h:commandButton action="#{inativarComponentesDepartamentoMBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar2"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</c:if>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

