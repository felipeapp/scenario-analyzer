<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<a4j:keepAlive beanName="buscaTurmaBean"/>
<f:view>
	<h2><ufrn:subSistema /> &gt; Alterar Status de Matrícula por Turma</h2>
	
	<h:form id="form" prependId="false">
	
	<c:set value="${alteracaoStatusMatricula.turma}" var="turma"/>
	<%@include file="/graduacao/alteracao_status_matricula/dados_turma.jsp"%>
	
	<br/>
	
	<table class="formulario" style="width: 90%">
		<caption> Informe a nova Situação do(s) discente(s) listado(s) abaixo, para alteração do status da matrícula</caption>
		<thead>
			<tr>
				<th width="1px;">
				</th>
				<th style="text-align: center;">Matrícula</th>
				<th>Discente</th>
				<th width="255px">Situação</th>
			</tr>
		</thead>		
		<tbody>
			<tr>
				<td width="1px" class="subFormulario">
					<h:selectBooleanCheckbox id="btnSelecionarTodos">
					</h:selectBooleanCheckbox> 
				</td>
				<td class="subFormulario">
				Todos
				</td>
				<td style="text-align:right !important" class="subFormulario">
				Selecionar Situação das Matrícula Selecionadas Para
				</td>
				<td align="left" class="subFormulario">
					<h:selectOneMenu id="btnAlterarStatusSelecao">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{ alteracaoStatusMatricula.situacoes }"/>
					</h:selectOneMenu>			
				</td>
			</tr>
			<c:forEach items="#{alteracaoStatusMatricula.matriculas}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td> 
					<c:set var="id" value="${status.index}"/>
						<h:selectBooleanCheckbox value="#{item.selected}" id="mat_${status.index}" styleClass="todosChecks"/> 					
					</td> 
					<td style="text-align: center;">${item.discente.matricula}</td>
					<td>${item.discente.nome}</td>
					<td>
						<h:inputHidden value="#{item.situacaoMatricula.id}" id="sitAtual_${status.index}"/>
						<h:selectOneMenu value="#{ item.novaSituacaoMatricula.id }"  styleClass="itemSelecionado"
							id="sit_${status.index}">
							<f:selectItems value="#{ alteracaoStatusMatricula.situacoes }"/>
						</h:selectOneMenu>					
					</td>
				</tr>			
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="Alterar Situação" action="#{alteracaoStatusMatricula.selecionarDiscentes}" id="selecionar"/>
					<h:commandButton value="<< Selecionar Outra Turma" action="#{buscaTurmaBean.telaSelecaoTurma}" id="outra"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alteracaoStatusMatricula.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<script type="text/javascript">
<!--
	//Seleciona checkbox correspondente ao combobox alterado
	jQuery(".itemSelecionado").change(function()	{
		var id = jQuery(this).attr('id').substring( jQuery(this).attr('id').indexOf('_')+1 );
		jQuery('#mat_'+id).attr('checked',true);
	});
	

	//Selecionar ou Deseleciona todos as matrículas
	jQuery("#btnSelecionarTodos").click(function()	{
		var checked_status = this.checked;
		jQuery(".todosChecks").each( function(){
			this.checked = checked_status;
			var id = jQuery(this).attr('id').substring( jQuery(this).attr('id').indexOf('_')+1 );
			//jQuery('#sit_'+id).attr('disabled',checked_status);
		} );
		
	});
	
	//Aplica o status a todos as matrícula selecionadas
	jQuery("#btnAlterarStatusSelecao").change(function()	{
		if( jQuery("#btnAlterarStatusSelecao").val()!= 0 && jQuery(".todosChecks:checked").size() > 0){
			jQuery(".todosChecks:checked").each(function(){
				var id = jQuery(this).attr('id').substring( jQuery(this).attr('id').indexOf('_')+1 );
				jQuery('#sit_'+id).val( jQuery("#btnAlterarStatusSelecao").val() );
			});
		}else{
			if(jQuery("#btnAlterarStatusSelecao").val() != 0)
				alert("Por favor selecione pelo menos um discente.");
			jQuery("#btnAlterarStatusSelecao").val(0);
		}	
	});	
-->
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>