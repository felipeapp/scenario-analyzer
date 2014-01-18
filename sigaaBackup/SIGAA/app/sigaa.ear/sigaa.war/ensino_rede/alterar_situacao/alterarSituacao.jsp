<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<f:view>
	<a4j:keepAlive beanName="alterarSituacaoMatriculaRede"/>
	<h2><ufrn:subSistema /> &gt; Consolidadar Turma</h2>
	
	<h:form id="form" prependId="false">
	
	<br/>
	
	<c:set var="turma" value="${alterarSituacaoMatriculaRede.turma}" />
	<%@include file="/ensino_rede/modulo/turma/info_turma.jsp"%>
	
	<table class="formulario" style="width: 90%">
		<caption> Informe a nova Situação do(s) discente(s) listado(s) abaixo, para alteração do status da matrícula</caption>
		<thead>
			<tr>
				<th width="1px;"></th>
				<th width="35%">Discente</th>
				<th></th>
				<th width="255px">Situação</th>
			</tr>
		</thead>		
		<tbody>
			<tr>
				<td width="1px" class="subFormulario">
				</td>
				<td class="subFormulario">
				</td>
				<td style="text-align:right !important" class="subFormulario">
				Selecionar Situação das Matrículas Para
				</td>
				<td align="left" class="subFormulario">
					<h:selectOneMenu id="btnAlterarStatusSelecao">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{ alterarSituacaoMatriculaRede.situacoesConsolidacao }"/>
					</h:selectOneMenu>			
				</td>
			</tr>
			<c:forEach items="#{alterarSituacaoMatriculaRede.matriculasRede}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td> 
					<c:set var="id" value="${status.index}"/>
					</td> 
					<td>${item.discente.nome}</td>
					<td></td>
					<td class="todos">
						<h:inputHidden value="#{item.situacao.id}" id="sitAtual_${status.index}"/>
						<h:selectOneMenu value="#{ item.novaSituacaoMatricula.id }"  styleClass="itemSelecionado" id="sit_${status.index}">
							<f:selectItems value="#{ alterarSituacaoMatriculaRede.situacoesConsolidacao }"/>
						</h:selectOneMenu>					
					</td>
				</tr>			
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="Alterar Situação" action="#{alterarSituacaoMatriculaRede.selecionarMatriculas}" id="selecionar"/>
					<h:commandButton value="<< Selecionar Outra Turma" action="#{alterarSituacaoMatriculaRede.voltarListarTurmas}" rendered="#{!alterarSituacaoMatriculaRede.acessoPortal}" id="outra"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alterarSituacaoMatriculaRede.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<script type="text/javascript">
<!--
	//Aplica o status a todos as matrícula selecionadas
	jQuery("#btnAlterarStatusSelecao").change(function()	{
		if( jQuery("#btnAlterarStatusSelecao").val()!= 0){
			jQuery(".todos").each(function(){
				var id = jQuery(this).children(":first").attr('id').substring( jQuery(this).children(":first").attr('id').indexOf('_')+1 );
				jQuery('#sit_'+id).val( jQuery("#btnAlterarStatusSelecao").val() );
			});
		}
	});	
-->
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>