<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/jquery/jquery-1.4.4.min.js"></script>
<script type="text/javascript" charset="ISO-8859">
	var J = jQuery.noConflict();
</script>
<a4j:keepAlive beanName="alteracaoStatusMatriculaMedioMBean"/>	
<f:view>
	<h2><ufrn:subSistema /> &gt; Alterar Status de Matrícula por Série</h2>
	
	<h:form id="form" prependId="false">
	
	<table class="visualizacao" style="width: 60%">
		<tr>
			<th>Ano:</th>
			<td>${alteracaoStatusMatriculaMedioMBean.turmaSerie.ano}</td>
		</tr>
		<tr>
			<th>Série:</th>
			<td>${alteracaoStatusMatriculaMedioMBean.turmaSerie.serie.descricaoCompleta}</td>
		</tr>
		<tr>
			<th>Turma:</th>
			<td>${alteracaoStatusMatriculaMedioMBean.turmaSerie.nome}</td>
		</tr>
	</table>
	<br/>
	
	<table class="formulario" style="width: 90%">
		<caption> Informe a nova Situação do(s) discente(s) listado(s) abaixo, para alteração do status da matrícula</caption>
		<thead>
			<tr>
				<th width="1px;">
				</th>
				<th style="text-align: center;">Matrícula</th>
				<th>Discente</th>
				<th>Situação Atual</th>
				<th width="255px">Nova Situação</th>
			</tr>
		</thead>		
		<tbody>
			<tr>
				<td width="1px" class="subFormulario">
					<h:selectBooleanCheckbox id="btnSelecionarTodos">
					</h:selectBooleanCheckbox> 
				</td>
				<td class="subFormulario">
					<label for="btnSelecionarTodos">Todos</label>
				</td>
				<td style="text-align:right !important" class="subFormulario" colspan="2">
				Alterar Situação das Matrículas Selecionadas Para
				</td>
				<td align="left" class="subFormulario">
					<h:selectOneMenu id="btnAlterarStatusSelecao">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{ alteracaoStatusMatriculaMedioMBean.situacoes }"/>
					</h:selectOneMenu>			
				</td>
			</tr>
			<c:forEach items="#{alteracaoStatusMatriculaMedioMBean.matriculas}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td> 
					<c:set var="id" value="${status.index}"/>
						<h:selectBooleanCheckbox value="#{item.selected}" id="mat_${status.index}" styleClass="todosChecks"/> 					
					</td> 
					<td style="text-align: center;">${item.discenteMedio.matricula}</td>
					<td>${item.discenteMedio.nome}</td>
					<td>${item.situacaoMatriculaSerie.descricao}</td>
					<td>
						<h:inputHidden value="#{item.situacaoMatriculaSerie.id}" id="sitAtual_${status.index}"/>
						<h:selectOneMenu value="#{ item.novaSituacaoMatricula.id }" 
							onchange="selecionar(this);" onblur="selecionar(this);" id="sit_${status.index}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{ alteracaoStatusMatriculaMedioMBean.situacoes }"/>
						</h:selectOneMenu>					
					</td>
				</tr>			
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan=5">
					<h:commandButton value="Alterar Situação" action="#{alteracaoStatusMatriculaMedioMBean.selecionarDiscentes}" id="selecionar"/>
					<h:commandButton value="<< Selecionar Outra Turma" action="#{alteracaoStatusMatriculaMedioMBean.iniciar}" id="outra"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alteracaoStatusMatriculaMedioMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<script type="text/javascript">
<!--
	//Seleciona checkbox correspondente ao combobox alterado
	function selecionar(obj){
		var id = obj.id;			
		id =  id.substring(id.indexOf('_')+1);
		alert(J('#sitAtual_'+id).val());
		
	}

	//Selecionar ou Deseleciona todos as matrículas
	J("#btnSelecionarTodos").click(function()	{
		var checked_status = this.checked;
		J(".todosChecks").each( function(){
			this.checked = checked_status;
			var id = J(this).attr('id').substring( J(this).attr('id').indexOf('_')+1 );
		} );
		
	});
	
	//Aplica o status a todos as matrícula selecionadas
	J("#btnAlterarStatusSelecao").change(function()	{
		if( J("#btnAlterarStatusSelecao").val()!= 0 && J(".todosChecks:checked").size() > 0){
			J(".todosChecks:checked").each(function(){
				var id = J(this).attr('id').substring( J(this).attr('id').indexOf('_')+1 );
				J('#sit_'+id).val( J("#btnAlterarStatusSelecao").val() );
			});
		}else{
			if(J("#btnAlterarStatusSelecao").val() != 0)
				alert("Por favor, selecione pelo menos um discente.");
			J("#btnAlterarStatusSelecao").val(0);
		}	
	});	
-->
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>